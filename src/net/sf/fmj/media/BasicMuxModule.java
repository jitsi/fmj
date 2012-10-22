package net.sf.fmj.media;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.util.*;

/**
 * BasicMuxModule is a module which have InputConnectors and no
 * OutputConnectors. It receives data from its inputs, feed that to a plugin
 * Multiplexer which then outputs the data via an output DataSource.
 */
public class BasicMuxModule extends BasicSinkModule
{
    class MyInputConnector extends BasicInputConnector
    {
        public MyInputConnector()
        {
        }

        @Override
        public String toString()
        {
            return super.toString() + ": " + getFormat();
        }
    }

    protected Multiplexer multiplexer;
    protected Format inputs[];
    protected InputConnector ics[];
    protected boolean prefetchMarkers[];
    protected boolean endMarkers[];
    protected boolean resettedMarkers[];
    protected boolean stopAtTimeMarkers[];
    protected boolean paused[];
    protected boolean prerollTrack[];
    private Object pauseSync[];
    protected ElapseTime elapseTime[];
    protected boolean prefetching = false;
    protected boolean started = false; // state of the controller.
    private boolean closed = false;
    private boolean failed = false;
    private Object prefetchSync = new Object();
    private float frameRate = 30f;
    private float lastFramesBehind = -1f;
    private int framesPlayed = 0;
    private VideoFormat rtpVideoFormat = null;
    private VideoFormat firstVideoFormat = null;

    public static String ConnectorNamePrefix = "input";

    private long bitsWritten = 0;

    // For comparing formats.
    static AudioFormat mpegAudio = new AudioFormat(AudioFormat.MPEG_RTP);

    protected BasicMuxModule(Multiplexer m, Format inputs[])
    {
        multiplexer = m;
        if (inputs != null)
        {
            InputConnector ic;
            ics = new InputConnector[inputs.length];
            for (int i = 0; i < inputs.length; i++)
            {
                ic = new MyInputConnector();
                ic.setSize(1);
                ic.setModule(this);
                registerInputConnector(ConnectorNamePrefix + i, ic);
                ics[i] = ic;
                if (inputs[i] instanceof VideoFormat
                        && firstVideoFormat == null)
                {
                    firstVideoFormat = (VideoFormat) inputs[i];
                    String encoding = inputs[i].getEncoding().toUpperCase();
                    if (encoding.endsWith("RTP"))
                        rtpVideoFormat = firstVideoFormat;
                }
            }
            this.inputs = inputs;
        }
        if (multiplexer != null && multiplexer instanceof Clock)
            setClock((Clock) multiplexer);
        setProtocol(Connector.ProtocolPush);
    }

    @Override
    public void abortPrefetch()
    {
        // multiplexer.close();
        prefetching = false;
    }

    boolean checkEnd(int idx)
    {
        synchronized (endMarkers)
        {
            endMarkers[idx] = true;
            for (int i = 0; i < endMarkers.length; i++)
            {
                if (!endMarkers[i])
                    return false;
            }
            return true;
        }
    }

    boolean checkPrefetch(int idx)
    {
        synchronized (prefetchMarkers)
        {
            prefetchMarkers[idx] = true;
            for (int i = 0; i < prefetchMarkers.length; i++)
            {
                if (!prefetchMarkers[i])
                    return false;
            }
            return true;
        }
    }

    boolean checkResetted(int idx)
    {
        synchronized (resettedMarkers)
        {
            resettedMarkers[idx] = true;
            for (int i = 0; i < resettedMarkers.length; i++)
            {
                if (!resettedMarkers[i])
                    return false;
            }
            return true;
        }
    }

    boolean checkStopAtTime(int idx)
    {
        synchronized (stopAtTimeMarkers)
        {
            stopAtTimeMarkers[idx] = true;
            for (int i = 0; i < stopAtTimeMarkers.length; i++)
            {
                if (!stopAtTimeMarkers[i])
                    return false;
            }
            return true;
        }
    }

    /**
     * This is the main processing function. It is called when one of the the
     * upstream modules pushes a buffer to this module.
     */
    @Override
    public void connectorPushed(InputConnector ic)
    {
        int idx = -1;

        // Determine track index.
        // Do some loop unrolling to find the track index since there's
        // probably just 2 tracks.
        if (ics[0] == ic)
            idx = 0;
        else if (ics[1] == ic)
            idx = 1;
        else
        {
            for (int i = 2; i < ics.length; i++)
            {
                if (ics[i] == ic)
                {
                    idx = i;
                    break;
                }
            }
            if (idx == -1)
            {
                // Something is terribly wrong.
                throw new RuntimeException(
                        "BasicMuxModule: unmatched input connector!");
            }
        }

        // This weird looking while loop here is necessary.
        // What we are trying to achieve here is to never return from
        // connectorPush until we actually finish processing the buffer.
        // If a preset stop time is reached, we'll keep looping here.
        // Without this loop, we'll miss one valid buffer from the
        // upstream module. This is indeed very tricky.
        while (true)
        {
            if (paused[idx])
            {
                // Not sure how efficient the synchronized block is.
                // So I'm doing the check before entering into it.
                // Another check is performed inside the block.
                synchronized (pauseSync[idx])
                {
                    try
                    {
                        while (paused[idx] && !closed)
                            pauseSync[idx].wait();
                    } catch (Exception e)
                    {
                    }
                }
            }

            // Check to see if we have reached the preset stop time.
            // If so, we'll notify the player and then loop back to
            // the pause above. That way, we won't go on to process
            // the data.
            if (stopTime > -1 && elapseTime[idx].value >= stopTime)
            {
                paused[idx] = true;

                if (checkStopAtTime(idx))
                {
                    if (multiplexer instanceof Drainable)
                        ((Drainable) multiplexer).drain();
                    doStop();
                    if (moduleListener != null)
                        moduleListener.stopAtTime(this);
                }
            } else
                break; // We've checked the stop time and we can just
            // move on.
        }

        Buffer buffer = ic.getValidBuffer();
        int flags = buffer.getFlags();

        int rc = 0;

        // Check if we are in the resetted state.
        if (resetted)
        {
            // Check if the input buffer contains the zero-length
            // flush flag.
            if ((flags & Buffer.FLAG_FLUSH) != 0)
            {
                // This causes a deadlock interacting with the sync mux.
                // Pause the particular track.
                // paused[idx] = true;

                // If all tracks are resetted, then we are done.
                if (checkResetted(idx))
                {
                    resetted = false;
                    doStop();
                    if (moduleListener != null)
                        moduleListener.resetted(this);
                }
            }

            // In the resetted state, we'll not pass any data to the
            // multiplexer.
            ic.readReport();

            return;
        }

        if (failed || closed || buffer.isDiscard())
        {
            ic.readReport();
            return;
        }

        // Signal the engine if the marker bit is set.
        if ((flags & Buffer.FLAG_SYSTEM_MARKER) != 0 && moduleListener != null)
        {
            moduleListener.markedDataArrived(this, buffer);
            flags = flags & ~Buffer.FLAG_SYSTEM_MARKER;
            buffer.setFlags(flags);
        }

        // Flag to indicate if data is prerolled, then we don't
        // need to process it any further.
        boolean dataPrerolled = false;

        Format format = buffer.getFormat();

        if (format == null)
        {
            // Something's weird, we'll just assume it's the previous
            // format.
            format = ic.getFormat();
            buffer.setFormat(format);
        }

        // Update the elapse time for prerolling and checking the
        // preset stop time.
        if (elapseTime[idx].update(buffer.getLength(), buffer.getTimeStamp(),
                format))
        {
            // If an elapse time can be computed.

            // Check prerolling.
            if (prerollTrack[idx])
            {
                long target = getMediaNanoseconds();
                if (elapseTime[idx].value > target)
                {
                    // Done with prerolling.
                    if (format instanceof AudioFormat
                            && AudioFormat.LINEAR.equals(format.getEncoding()))
                    {
                        int remain = (int) ElapseTime.audioTimeToLen(
                                elapseTime[idx].value - target,
                                (AudioFormat) format);

                        int offset = buffer.getOffset() + buffer.getLength()
                                - remain;
                        if (offset >= 0)
                        {
                            buffer.setOffset(offset);
                            buffer.setLength(remain);
                        }
                    }
                    prerollTrack[idx] = false;
                    elapseTime[idx].setValue(target);
                } else
                {
                    dataPrerolled = true;
                }
            }

            // Check the preset stop time.
            if (stopTime > -1 && elapseTime[idx].value > stopTime
                    && format instanceof AudioFormat)
            {
                // Processing the full chunk will have exceeded the
                // preset stop time. We'll cut the audio data.
                long exceeded = elapseTime[idx].value - stopTime;
                int exceededLen = (int) ElapseTime.audioTimeToLen(exceeded,
                        (AudioFormat) format);
                if (buffer.getLength() > exceededLen)
                    buffer.setLength(buffer.getLength() - exceededLen);
            }
        }

        // Report the frame behind time for the engine.
        if (moduleListener != null && format instanceof VideoFormat)
        {
            // Check to see if the frame is delayed.
            long mt = getMediaNanoseconds();

            // Let's bring the #'s back to milli seconds range.
            long lateBy = mt / 1000000L - buffer.getTimeStamp() / 1000000L
                    - getLatency() / 1000000L;

            // System.err.println("lateBy = " + lateBy);

            float fb = lateBy * frameRate / 1000f;
            if (fb < 0)
                fb = 0;

            if (lastFramesBehind != fb && (flags & Buffer.FLAG_NO_DROP) == 0)
            {
                moduleListener.framesBehind(this, fb, ic);
                lastFramesBehind = fb;
            }

            // System.err.println("frames behind = " + fb);
        }

        do
        {
            if (!dataPrerolled)
            {
                try
                {
                    rc = multiplexer.process(buffer, idx);

                } catch (Throwable e)
                {
                    Log.dumpStack(e);
                    if (moduleListener != null)
                        moduleListener.internalErrorOccurred(this);
                }

                // Update the frame rate
                if (rc == PlugIn.BUFFER_PROCESSED_OK
                        && format == firstVideoFormat)
                {
                    if (format == rtpVideoFormat)
                    {
                        if ((flags & Buffer.FLAG_RTP_MARKER) > 0)
                            framesPlayed++;
                    } else
                    {
                        framesPlayed++;
                    }
                }
            } else
            {
                rc = PlugIn.BUFFER_PROCESSED_OK;
            }

            if ((rc & PlugIn.PLUGIN_TERMINATED) != 0)
            {
                failed = true;
                if (moduleListener != null)
                    moduleListener.pluginTerminated(this);
                ic.readReport();
                return;
            }

            // If the module is prefetching, we'll need to check to see
            // if the device has been prefetched.
            if (prefetching
                    && (!(multiplexer instanceof Prefetchable) || ((Prefetchable) multiplexer)
                            .isPrefetched()))
            {
                synchronized (prefetchSync)
                {
                    if (!started && prefetching && !resetted)
                        paused[idx] = true;
                    if (checkPrefetch(idx))
                        prefetching = false;
                }

                // Notify the engine prefetching is done.
                if (!prefetching && moduleListener != null)
                    moduleListener.bufferPrefetched(this);
            }

        } while (!resetted && rc == PlugIn.INPUT_BUFFER_NOT_CONSUMED);

        bitsWritten += buffer.getLength();

        if (buffer.isEOM())
        {
            if (!resetted)
                paused[idx] = true;

            if (checkEnd(idx))
            {
                doStop();
                if (moduleListener != null)
                    moduleListener.mediaEnded(this);
            }
        }

        ic.readReport();
    }

    @Override
    public void doClose()
    {
        multiplexer.close();
        closed = true;
        for (int i = 0; i < pauseSync.length; i++)
        {
            synchronized (pauseSync[i])
            {
                pauseSync[i].notifyAll();
            }
        }
    }

    @Override
    public void doDealloc()
    {
        // multiplexer.close();
    }

    @Override
    public void doFailedPrefetch()
    {
        prefetching = false;
    }

    @Override
    public boolean doPrefetch()
    {
        if (!((PlaybackEngine) controller).prefetchEnabled)
            return true;

        resetPrefetchMarkers();
        prefetching = true;
        resume();
        return true;
    }

    @Override
    public boolean doRealize()
    {
        if (multiplexer == null || inputs == null)
            return false;
        try
        {
            multiplexer.open();
        } catch (ResourceUnavailableException e)
        {
            return false;
        }
        prefetchMarkers = new boolean[ics.length];
        endMarkers = new boolean[ics.length];
        resettedMarkers = new boolean[ics.length];
        stopAtTimeMarkers = new boolean[ics.length];
        paused = new boolean[ics.length];
        prerollTrack = new boolean[ics.length];
        pauseSync = new Object[ics.length];
        elapseTime = new ElapseTime[ics.length];

        for (int i = 0; i < ics.length; i++)
        {
            prerollTrack[i] = false;
            pauseSync[i] = new Object();
            elapseTime[i] = new ElapseTime();
        }

        pause();

        return true;
    }

    @Override
    public void doStart()
    {
        super.doStart();
        resetEndMarkers();
        resetStopAtTimeMarkers();
        started = true;

        synchronized (prefetchSync)
        {
            prefetching = false;
            resume();
        }
    }

    @Override
    public void doStop()
    {
        super.doStop();
        started = false;
        resetPrefetchMarkers();
        prefetching = true;
    }

    public long getBitsWritten()
    {
        return bitsWritten;
    }

    @Override
    public Object getControl(String s)
    {
        return multiplexer.getControl(s);
    }

    @Override
    public Object[] getControls()
    {
        return multiplexer.getControls();
    }

    public DataSource getDataOutput()
    {
        return multiplexer.getDataOutput();
    }

    public int getFramesPlayed()
    {
        return framesPlayed;
    }

    public Multiplexer getMultiplexer()
    {
        return multiplexer;
    }

    @Override
    public boolean isThreaded()
    {
        return false;
    }

    /**
     * Internally, this pauses the processing thread from pushing more data into
     * the multiplexer.
     */
    void pause()
    {
        for (int i = 0; i < paused.length; i++)
            paused[i] = true;
    }

    // Not applicable.
    @Override
    protected void process()
    {
    }

    @Override
    public void reset()
    {
        super.reset();
        resetResettedMarkers();
        prefetching = false;
    }

    public void resetBitsWritten()
    {
        bitsWritten = 0;
    }

    void resetEndMarkers()
    {
        synchronized (endMarkers)
        {
            for (int i = 0; i < endMarkers.length; i++)
                endMarkers[i] = false;
        }
    }

    public void resetFramesPlayed()
    {
        framesPlayed = 0;
    }

    void resetPrefetchMarkers()
    {
        synchronized (prefetchMarkers)
        {
            for (int i = 0; i < prefetchMarkers.length; i++)
                prefetchMarkers[i] = false;
        }
    }

    void resetResettedMarkers()
    {
        synchronized (resettedMarkers)
        {
            for (int i = 0; i < resettedMarkers.length; i++)
                resettedMarkers[i] = false;
        }
    }

    void resetStopAtTimeMarkers()
    {
        synchronized (stopAtTimeMarkers)
        {
            for (int i = 0; i < stopAtTimeMarkers.length; i++)
                stopAtTimeMarkers[i] = false;
        }
    }

    /**
     * Internally, this resumes the processing thread to push data into the
     * multiplexer.
     */
    void resume()
    {
        for (int i = 0; i < pauseSync.length; i++)
        {
            synchronized (pauseSync[i])
            {
                paused[i] = false;
                pauseSync[i].notifyAll();
            }
        }
    }

    @Override
    public void setFormat(Connector connector, Format format)
    {
        if (format instanceof VideoFormat)
        {
            float fr = ((VideoFormat) format).getFrameRate();
            if (fr != Format.NOT_SPECIFIED)
                frameRate = fr;
        }
    }

    /**
     * Enable prerolling.
     */
    @Override
    public void setPreroll(long wanted, long actual)
    {
        super.setPreroll(wanted, actual);
        for (int i = 0; i < elapseTime.length; i++)
        {
            elapseTime[i].setValue(actual);

            // There's a bug in the MPEG packetizer that prevents prerolling
            // from working properly. The timestamps on the MPEG_RTP
            // buffers do not corresponds to the media time set after
            // a seek. So we're disabling prerolling for MPEG_RTP here.
            if (inputs[i] instanceof AudioFormat
                    && mpegAudio.matches(inputs[i]))
            {
                prerollTrack[i] = false;
            } else
                prerollTrack[i] = true;
        }
    }

    @Override
    public void triggerReset()
    {
        multiplexer.reset();
        synchronized (prefetchSync)
        {
            prefetching = false;
            if (resetted)
                resume();
        }
    }

}
