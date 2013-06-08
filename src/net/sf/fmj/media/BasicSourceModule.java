package net.sf.fmj.media;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.rtp.util.*;
import net.sf.fmj.media.util.*;

/**
 * MediaSource is a module which have OutputConnectors and no InputConnectors.
 * It receives data from PullSourceStream and stream the data to the downstream
 * modules (in case of PushSourceStream an adapter should be written Push2Pull
 * such as the RTP "bucket" adapter).<br>
 *
 * MediaSource are typically not threaded and use Pull protocol (URL connection
 * is really threaded but everything goes "under the hood" so we refer to it as
 * unthreaded one).<br>
 * This class support data caching in either memory or disk. <br>
 * When an attemped read would block, the Player would Restart (in order to
 * fetch data). We need the level 3 design for:
 * <ul>
 * <li>container file format (such as WAV or AVI) header parsers</li>
 * <li>container file format Tracks separator. For each Track OutputConnector is
 * generated</li>
 * <li>fixed frame size codecs (e.g. GSM or G.723) need to expose: time ->
 * offset and offset -> time conversions. How detection of the codec mode is
 * handled (G.723 Lo and Hi) for seek?<br>
 * <i><b>A good candidate for putting those methods is the Format class or the
 * Codec class</b></i></li>
 * <li>Variable frame size file format (such as MPEG system layer) should
 * provide a method to perform the seek.</li>
 * </ul>
 *
 */

public class BasicSourceModule extends BasicModule implements Duration,
        Positionable
{
    /**
     * Create a plugin parser based on the input DataSource.
     */
    static protected Demultiplexer createDemultiplexer(DataSource ds)
            throws IOException, IncompatibleSourceException
    {
        // Create the parser based on the DataSource's mime type.
        ContentDescriptor cd = new ContentDescriptor(ds.getContentType());
        Vector cnames = PlugInManager.getPlugInList(cd, null,
                PlugInManager.DEMULTIPLEXER);
        Class<?> cls;
        Demultiplexer parser = null;
        IOException ioe = null;
        IncompatibleSourceException ise = null;
        for (int i = 0; i < cnames.size(); i++)
        {
            try
            {
                // cls = Class.forName((String)cnames.elementAt(i));
                cls = BasicPlugIn.getClassForName((String) cnames.elementAt(i));
                Object p = cls.newInstance();

                if (p instanceof Demultiplexer)
                {
                    parser = (Demultiplexer) p;
                    try
                    {
                        parser.setSource(ds);
                    } catch (IOException e)
                    {
                        parser = null;
                        ioe = e;
                        continue;
                    } catch (IncompatibleSourceException e)
                    {
                        parser = null;
                        ise = e;
                        continue;
                    }
                    break;
                }
            } catch (ClassNotFoundException e)
            {
            } catch (InstantiationException e)
            {
            } catch (IllegalAccessException e)
            {
            }
        }
        if (parser == null)
        {
            if (ioe != null)
                throw ioe;
            if (ise != null)
                throw ise;
        }
        return parser;
    }

    static public BasicSourceModule createModule(DataSource ds)
            throws IOException, IncompatibleSourceException
    {
        Demultiplexer parser = createDemultiplexer(ds);
        if (parser == null)
            return null;
        return new BasicSourceModule(ds, parser);
    }

    PlaybackEngine engine;
    protected DataSource source;
    protected Demultiplexer parser;
    protected Track tracks[] = new Track[0];
    protected SourceThread loops[];

    protected String connectorNames[];

    protected long bitsRead = 0;
    /* private */Object resetSync = new Object();
    protected boolean started = false;
    protected SystemTimeBase systemTimeBase = new SystemTimeBase();
    protected long lastSystemTime = 0;

    protected long originSystemTime = 0;

    protected long currentSystemTime = 0;
    protected Time lastPositionSet = new Time(0L);
    // For remapping RTP Time.
    RTPTimeBase rtpMapperUpdatable = null;
    RTPTimeBase rtpMapper = null;
    long currentRTPTime = 0L;
    long oldOffset = 0L;

    boolean rtpOffsetInvalid = true;

    String cname = null;

    public String errMsg = null;

    // The index of the track selected for doing the latency computation.
    int latencyTrack = -1;

    protected BasicSourceModule(DataSource ds, Demultiplexer demux)
    {
        source = ds;
        parser = demux;

        SourceStream stream = null;
        if (source instanceof PullDataSource)
        {
            stream = ((PullDataSource) source).getStreams()[0];
        } else if (source instanceof PushDataSource)
        {
            stream = ((PushDataSource) source).getStreams()[0];
        }

    }

    @Override
    public void abortPrefetch()
    {
        doStop();
    }

    @Override
    public void abortRealize()
    {
        parser.stop();
        parser.close();
    }

    protected boolean checkAllPaused()
    {
        for (int i = 0; i < loops.length; i++)
        {
            if (tracks[i].isEnabled() && loops[i] != null
                    && !loops[i].isPaused())
                return false;
        }
        return true;
    }

    public void checkLatency()
    {
        // If a track is already assigned for the latency computation,
        // use it.
        if (latencyTrack > -1)
        {
            if (tracks[latencyTrack].isEnabled() && loops[latencyTrack] != null)
            {
                loops[latencyTrack].checkLatency = true;
                return;
            } else
                latencyTrack = -1;
        }

        // Select a track to compute the latency.
        for (int i = 0; i < tracks.length; i++)
        {
            if (!tracks[i].isEnabled())
                continue;

            latencyTrack = i;
            if (tracks[i].getFormat() instanceof VideoFormat)
            {
                // If there's a video track, use that.
                break;
            }
        }

        if (latencyTrack > -1 && loops[latencyTrack] != null)
            loops[latencyTrack].checkLatency = true;
    }

    /**
     * Create the source loop thread.
     */
    SourceThread createSourceThread(int idx)
    {
        SourceThread thread = null;
        MyOutputConnector oc = (MyOutputConnector) getOutputConnector(connectorNames[idx]);

        if (oc == null || oc.getInputConnector() == null)
        {
            tracks[idx].setEnabled(false);
            return null;
        }

        thread = new SourceThread(this, oc, idx);

        // Use this rough priority scheme for now.
        if (tracks[idx].getFormat() instanceof AudioFormat)
            thread.useAudioPriority();
        else
            thread.useVideoPriority();

        return thread;
    }

    @Override
    public void doClose()
    {
        parser.close();
        if (tracks == null)
            return;
        // Kill the threads.
        for (int i = 0; i < tracks.length; i++)
        {
            if (loops[i] != null)
                loops[i].kill();
        }

        if (rtpMapperUpdatable != null)
        {
            RTPTimeBase.returnMapperUpdatable(rtpMapperUpdatable);
            rtpMapperUpdatable = null;
        }
    }

    @Override
    public void doDealloc()
    {
    }

    @Override
    public void doFailedPrefetch()
    {
    }

    @Override
    public void doFailedRealize()
    {
        parser.stop();
        parser.close();
    }

    @Override
    public boolean doPrefetch()
    {
        super.doPrefetch();
        return true;
    }

    /**
     * Parsed in the input to get the track info. This should be called in
     * Player.realize() or Processor.connect().
     */
    @Override
    public boolean doRealize()
    {
        try
        {
            parser.open();
        } catch (ResourceUnavailableException e)
        {
            errMsg = "Resource unavailable: " + e.getMessage();
            return false;
        }

        try
        {
            parser.start();
            tracks = parser.getTracks();
        } catch (BadHeaderException e)
        {
            errMsg = "Bad header in the media: " + e.getMessage();
            parser.close();
            return false;
        } catch (IOException e)
        {
            errMsg = "IO exception: " + e.getMessage();
            parser.close();
            return false;
        }

        // Guard against some menace parser.
        if (tracks == null || tracks.length == 0)
        {
            errMsg = "The media has 0 track";
            parser.close();
            return false;
        }

        MyOutputConnector oc;

        loops = new SourceThread[tracks.length];
        connectorNames = new String[tracks.length];
        for (int i = 0; i < tracks.length; i++)
        {
            oc = new MyOutputConnector(tracks[i]);
            oc.setProtocol(Connector.ProtocolPush);
            oc.setSize(1);
            connectorNames[i] = tracks[i].toString();
            registerOutputConnector(tracks[i].toString(), oc);
            loops[i] = null;
        }

        engine = (PlaybackEngine) getController();

        // For RTP, we don't stop the parser. This prevents
        // the RTP buffer Q from being flushed. Flushing the
        // buffer Q will flush the initial chunks of data. That's
        // bad for H.261 which requires the initial key frame.
        if (engine == null || !engine.isRTP())
            parser.stop();

        return true;
    }

    @Override
    public void doStart()
    {
        lastSystemTime = systemTimeBase.getNanoseconds();
        originSystemTime = currentSystemTime;

        rtpOffsetInvalid = true;

        super.doStart();
        try
        {
            parser.start();
        } catch (IOException e)
        {
        }

        for (int i = 0; i < loops.length; i++)
        {
            // Start the track only if the track is enabled and the
            // output connector is connected to an input.
            if (tracks[i].isEnabled())
            {
                if (loops[i] == null
                        && (loops[i] = createSourceThread(i)) == null)
                {
                    continue;
                }
                loops[i].start();
            }
        }

        started = true;
    }

    /**
     * This is a blocking pause.
     */
    @Override
    public void doStop()
    {
        // We don't stop the source until prefetch is done.
        started = false;
    }

    public long getBitsRead()
    {
        return bitsRead;
    }

    @Override
    public Object getControl(String s)
    {
        return parser.getControl(s);
    }

    @Override
    public Object[] getControls()
    {
        return parser.getControls();
    }

    public Demultiplexer getDemultiplexer()
    {
        return parser;
    }

    public Time getDuration()
    {
        return parser.getDuration();
    }

    /**
     * Return an array of strings containing this media module's output port
     * names.
     */
    @Override
    public String[] getOutputConnectorNames()
    {
        return connectorNames;
    }

    public boolean isPositionable()
    {
        return parser.isPositionable();
    }

    public boolean isRandomAccess()
    {
        return parser.isRandomAccess();
    }

    /**
     * This is essentially a non-blocking version of doStop.
     */
    public void pause()
    {
        synchronized (resetSync)
        {
            for (int i = 0; i < loops.length; i++)
            {
                if (tracks[i].isEnabled() && loops[i] != null
                        && !loops[i].resetted)
                    loops[i].pause();
            }
            parser.stop();
        }
    }

    // Not applicable.
    @Override
    public void process()
    {
    }

    boolean readHasBlocked()
    {
        if (loops == null)
            return false;
        for (int i = 0; i < loops.length; i++)
        {
            if (loops[i] != null && loops[i].readBlocked)
                return true;
        }
        return false;
    }

    @Override
    public void reset()
    {
        synchronized (resetSync)
        {
            super.reset();
            for (int i = 0; i < loops.length; i++)
            {
                if (tracks[i].isEnabled())
                {
                    if (loops[i] == null
                            && (loops[i] = createSourceThread(i)) == null)
                    {
                        continue;
                    }
                    loops[i].resetted = true;
                    loops[i].start();
                }
            }
        }
    }

    public void resetBitsRead()
    {
        bitsRead = 0;
    }

    // Not applicable.
    @Override
    public void setFormat(Connector connector, Format format)
    {
    }

    public Time setPosition(Time when, int rounding)
    {
        Time t = parser.setPosition(when, rounding);

        // This is a hack for MPEG/RTP right now. The MPEG
        // packetizers uses the header attribute in the Buffer object
        // to store the last position (media time) set. It used to
        // do that in the MPEG parser. But it needs to be done
        // for all cases since transcoding can occur from any parser
        // to the MPEG packetizers.
        if (lastPositionSet.getNanoseconds() == t.getNanoseconds())
            lastPositionSet = new Time(t.getNanoseconds() + 1);
        else
            lastPositionSet = t;
        return t;
    }

}

// //////////////////////////
//
// Inner classes ! not $$$
// //////////////////////////

class MyOutputConnector extends BasicOutputConnector
{
    protected Track track;

    public MyOutputConnector(Track track)
    {
        this.track = track;
        format = track.getFormat();
    }

    @Override
    public String toString()
    {
        return super.toString() + ": " + getFormat();
    }
}

class SourceThread extends LoopThread implements TrackListener
{
    BasicSourceModule bsm;
    int index = 0;
    protected MyOutputConnector oc;
    protected boolean readBlocked = false;
    protected boolean checkLatency = false;
    protected boolean resetted = false;
    long sequenceNum = 0;

    static int remapTimeFlag = Buffer.FLAG_SYSTEM_TIME
            | Buffer.FLAG_RELATIVE_TIME | Buffer.FLAG_RTP_TIME;

    protected long lastRelativeTime = -1;

    long currentTime = 0;

    long counter = 0;

    public SourceThread(BasicSourceModule bsm, MyOutputConnector oc, int i)
    {
        this.bsm = bsm;
        this.oc = oc;
        this.index = i;
        setName(getName() + ": " + oc.track);
        oc.track.setTrackListener(this);
    }

    // Given a buffer with a relative time stamp, this method will
    // remap that into a timestamp in the player's time base.
    @Override
    protected boolean process()
    {
        readBlocked = false;

        Buffer buffer = oc.getEmptyBuffer();

        buffer.setOffset(0);
        buffer.setLength(0);
        buffer.setFlags(0);
        buffer.setSequenceNumber(sequenceNum++);

        if (resetted)
        {
            // Synchronized block may be expensive.
            // That's why I'm checking for resetted state first then check
            // again inside the block.
            synchronized (bsm.resetSync)
            {
                if (resetted)
                {
                    // This is in the resetted state, we'll generate a
                    // zero-length flush buffer.
                    buffer.setFlags(Buffer.FLAG_FLUSH);
                    resetted = false;
                    this.pause(); // non-blocking pause.
                    if (bsm.checkAllPaused())
                    {
                        bsm.parser.stop();
                        bsm.parser.reset();
                    }

                    oc.writeReport();

                    return true;
                }
            } // synchronized.
        }

        try
        {
            oc.track.readFrame(buffer);

        } catch (Throwable e)
        {
            Log.dumpStack(e);
            if (bsm.moduleListener != null)
                bsm.moduleListener.internalErrorOccurred(bsm);
        }

        if (PlaybackEngine.TRACE_ON && !bsm.verifyBuffer(buffer))
        {
            System.err.println("verify buffer failed: " + oc.track);
            Thread.dumpStack();
            if (bsm.moduleListener != null)
                bsm.moduleListener.internalErrorOccurred(bsm);
        }

        if (buffer.getTimeStamp() != Buffer.TIME_UNKNOWN
                && (buffer.getFlags() & remapTimeFlag) != 0)
        {
            boolean success = true;
            if ((buffer.getFlags() & Buffer.FLAG_SYSTEM_TIME) != 0)
                success = remapSystemTime(buffer);
            else if ((buffer.getFlags() & Buffer.FLAG_RELATIVE_TIME) != 0)
                success = remapRelativeTime(buffer);
            else if ((buffer.getFlags() & Buffer.FLAG_RTP_TIME) != 0)
                success = remapRTPTime(buffer);

            if (!success)
            {
                buffer.setDiscard(true);
                oc.writeReport();
                return true;
            }
        }

        if (checkLatency)
        {
            buffer.setFlags(buffer.getFlags() | Buffer.FLAG_SYSTEM_MARKER);
            if (bsm.moduleListener != null)
                bsm.moduleListener.markedDataArrived(bsm, buffer);
            checkLatency = false;
        } else
            buffer.setFlags(buffer.getFlags() & ~Buffer.FLAG_SYSTEM_MARKER);

        /*
         * For debugging.
         *
         * if (buffer.getFormat() instanceof javax.media.format.VideoFormat) {
         * System.err.println("BSM: readFrame: " + buffer.getSequenceNumber());
         * }
         */

        if (readBlocked && bsm.moduleListener != null)
            bsm.moduleListener.dataBlocked(bsm, false);

        if (buffer.isEOM())
        {
            synchronized (bsm.resetSync)
            {
                if (!resetted)
                {
                    this.pause();
                    if (bsm.checkAllPaused())
                        bsm.parser.stop();
                }
            }
        } else
            bsm.bitsRead += buffer.getLength();

        oc.writeReport();

        return true;
    }

    public void readHasBlocked(Track t)
    {
        readBlocked = true;
        if (bsm.moduleListener != null)
            bsm.moduleListener.dataBlocked(bsm, true);
    }

    private boolean remapRelativeTime(Buffer buffer)
    {
        /*
         * Commented out. Requires some more thinking. - ivg.
         *
         * if( lastRelativeTime < 0) { //System.out.println(
         * "relative time reset!"); // Shift the time by .01 sec to avoid
         * generating the // same time stamp after retart. currentTime +=
         * 10000000; } else { //System.out.println( "else..."); currentTime +=
         * buffer.getTimeStamp() - lastRelativeTime; }
         *
         * lastRelativeTime= buffer.getTimeStamp();
         *
         * buffer.setTimeStamp( currentTime);
         */
        buffer.setFlags((buffer.getFlags() & ~Buffer.FLAG_RELATIVE_TIME)
                | Buffer.FLAG_NO_SYNC);

        // System.out.println( "currentTime[" + (counter++) + "]: " +
        // currentTime);
        return true;
    }

    // Given a buffer in RTP time stamps, remap it to media time.
    private boolean remapRTPTime(Buffer buffer)
    {
        if (buffer.getTimeStamp() <= 0)
        {
            buffer.setTimeStamp(Buffer.TIME_UNKNOWN);
            return true;
        }

        if (bsm.cname == null)
        {
            bsm.cname = bsm.engine.getCNAME();
            if (bsm.cname == null)
            {
                buffer.setTimeStamp(Buffer.TIME_UNKNOWN);
                return true;
            }
        }

        if (bsm.rtpOffsetInvalid)
        {
            if (bsm.rtpMapperUpdatable == null)
            {
                bsm.rtpMapperUpdatable = RTPTimeBase
                        .getMapperUpdatable(bsm.cname);
                // Someone else has claim the updatable mapper.
                // We won't need to then.
                if (bsm.rtpMapperUpdatable == null)
                    bsm.rtpOffsetInvalid = false;
            }

            if (bsm.rtpMapperUpdatable != null)
            {
                bsm.rtpMapperUpdatable.setOrigin(bsm.currentRTPTime);
                bsm.rtpMapperUpdatable.setOffset(buffer.getTimeStamp());
                bsm.rtpOffsetInvalid = false;
            }
        }

        if (bsm.rtpMapper == null)
            bsm.rtpMapper = RTPTimeBase.getMapper(bsm.cname);

        if (bsm.rtpMapper.getOffset() != bsm.oldOffset)
        {
            bsm.oldOffset = bsm.rtpMapper.getOffset();
        }

        long dur = buffer.getTimeStamp() - bsm.rtpMapper.getOffset();
        if (dur < 0)
        {
            if (bsm.rtpMapperUpdatable != null)
            {
                // The timestamps have rolled back and we have the
                // updatable mapper; we'll reset the remapper.
                bsm.rtpOffsetInvalid = true;
            } else
                dur = 0;
        }

        bsm.currentRTPTime = bsm.rtpMapper.getOrigin() + dur;
        buffer.setTimeStamp(bsm.currentRTPTime);

        /*
         * System.err.println("remap: " + buffer.getTimeStamp() + " off: " +
         * bsm.rtpMapper.getOffset() + " dur: " + dur + " orig: " +
         * bsm.rtpMapper.getOrigin() + " TS: " + bsm.currentRTPTime);
         */

        return true;
    }

    // Remap the given system time stamp to the player's media time.
    private boolean remapSystemTime(Buffer buffer)
    {
        if (!bsm.started)
            return false;

        long ts = buffer.getTimeStamp() - bsm.lastSystemTime;

        // If ts is negative, then we are dealing with a frame
        // that's from the past (probably some problem with flushing).
        if (ts < 0)
            return false;

        bsm.currentSystemTime = bsm.originSystemTime + ts;

        buffer.setTimeStamp(bsm.currentSystemTime);
        buffer.setFlags((buffer.getFlags() & ~Buffer.FLAG_SYSTEM_TIME)
                | Buffer.FLAG_NO_SYNC);

        return true;
    }

    @Override
    public synchronized void start()
    {
        super.start();
        lastRelativeTime = -1; // Reset the relative time count.
    }

}
