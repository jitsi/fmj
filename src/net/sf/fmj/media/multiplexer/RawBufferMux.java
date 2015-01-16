package net.sf.fmj.media.multiplexer;

import java.io.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.control.*;
import net.sf.fmj.media.util.*;

/**
 * This raw mux doesn't impose any sync on the output. It pushes out data in the
 * order that they arrived.
 */
public class RawBufferMux extends BasicPlugIn implements Multiplexer, Clock
{
    // /////////////////////////////////////////////////////////////////////////
    // /
    // / INNER CLASS: BUFFERDATASOURCE of MULTIPLEXER
    // ////////////////////////////////////////////////////////////////////////
    class RawBufferDataSource extends
            net.sf.fmj.media.protocol.BasicPushBufferDataSource
    {
        public RawBufferDataSource()
        {
            super();
            if (contentDesc == null)
                return;
            contentType = contentDesc.getContentType();
        }

        @Override
        public void connect() throws IOException
        {
            super.connect();
            sourceDisconnected = false;
        }

        @Override
        public void disconnect()
        {
            super.disconnect();
            sourceDisconnected = true;
            // close each of the streams.
            for (int i = 0; i < streams.length; i++)
            {
                streams[i].stop();
                streams[i].close();
            }
        }

        /**
         * Get the collection of streams that this source manages. The
         * collection of streams is entirely content dependent. The
         * <tt>ContentDescriptor</tt> of this <tt>DataSource</tt> provides the
         * only indication of what streams can be available on this connection.
         *
         * @return The collection of streams for this source.
         */
        @Override
        public PushBufferStream[] getStreams()
        {
            return streams;
        }

        private void initialize(Format[] trackFormats)
        {
            streams = new RawBufferSourceStream[trackFormats.length];
            for (int i = 0; i < trackFormats.length; i++)
            {
                streams[i] = new RawBufferSourceStream(trackFormats[i]);
            }
        }

        @Override
        public void start() throws IOException
        {
            super.start();
            // start each of the streams
            for (int i = 0; i < streams.length; i++)
                streams[i].start();
        }

        @Override
        public void stop() throws IOException
        {
            super.stop();
            // stop each of the streams
            for (int i = 0; i < streams.length; i++)
                streams[i].stop();
        }

    } // end of RawBufferDataSource

    class RawBufferSourceStream extends
            net.sf.fmj.media.protocol.BasicSourceStream implements
            PushBufferStream, Runnable
    {
        Format format = null;
        CircularBuffer bufferQ;
        boolean started = false;
        Object startReq = new Integer(0);
        BufferTransferHandler handler = null;
        Thread streamThread = null;
        boolean closed = false;
        boolean draining = false;
        Object drainSync = new Object(); // Allow dropping of data.

        public RawBufferSourceStream(Format fmt)
        {
            super();
            contentDescriptor = contentDesc;
            format = fmt;
            bufferQ = new CircularBuffer(5);

            streamThread = new MediaThread(this, "RawBufferStream Thread");
            if (streamThread != null)
                streamThread.start(); // I don't think you need permission for
                                      // start

        }

        // ///////////////////////////////////////////////////////////
        // PushBufferStream Methods
        // ///////////////////////////////////////////////////////////

        protected void close()
        {
            closed = true;
            if (streamThread != null)
            {
                try
                {
                    // Reset the bufferQ.
                    reset();
                    synchronized (startReq)
                    {
                        startReq.notifyAll();
                    }
                    // streamThread.stop();
                } catch (Exception e)
                {
                }
            }
        }

        public Format getFormat()
        {
            return format;
        }

        protected int process(Buffer filled)
        {
            Buffer buffer;

            synchronized (bufferQ)
            {
                if (allowDrop)
                {
                    // If the buffer is full and there are data in the
                    // queue to be read, then we can drop that data.
                    if (!bufferQ.canWrite() && bufferQ.canRead())
                    {
                        // Peek into the head of the buffer to determine
                        // if we are allowed to drop that packet.
                        Buffer tmp = bufferQ.peek();
                        if ((tmp.getFlags() & Buffer.FLAG_NO_DROP) == 0)
                        {
                            // Drop the head of the queue.
                            bufferQ.read();
                            bufferQ.readReport();
                            // System.err.println("drop data");
                        }
                    }
                }

                // Grab an empty buffer to write.
                while (!bufferQ.canWrite() && !closed)
                {
                    try
                    {
                        bufferQ.wait();
                    } catch (Exception e)
                    {
                    }
                }
                if (closed)
                    return PlugIn.BUFFER_PROCESSED_OK;
                buffer = bufferQ.getEmptyBuffer();
            }

            // get a handle over the empty buffer's data and header object
            // we will eventually set this as the filled buffer's data
            // and header object and store the filled buffer's data
            // and header in the muxes' buffers.
            Object bdata = buffer.getData();
            Object bheader = buffer.getHeader();
            buffer.setData(filled.getData());
            buffer.setHeader(filled.getHeader());
            filled.setData(bdata);
            filled.setHeader(bheader);

            // now set the empty buffer's other attributes
            buffer.setLength(filled.getLength());
            buffer.setEOM(filled.isEOM());
            buffer.setFlags(filled.getFlags());
            buffer.setRtpTimeStamp(filled.getRtpTimeStamp());
            buffer.setHeaderExtension(filled.getHeaderExtension());
            buffer.setTimeStamp(filled.getTimeStamp());
            buffer.setFormat(filled.getFormat());
            buffer.setOffset(filled.getOffset());
            buffer.setSequenceNumber(filled.getSequenceNumber());
            // Put the empty buffer which is now filled into queue for
            // consumption

            if (filled.isEOM())
                draining = true;

            synchronized (bufferQ)
            {
                bufferQ.writeReport();
                bufferQ.notifyAll();
            }

            // If the incoming buffer is EOM, we'll need to drain
            // that buffer before returning.
            if (filled.isEOM())
            {
                synchronized (drainSync)
                {
                    try
                    {
                        if (draining)
                        {
                            // Wait at most 3 secs.
                            drainSync.wait(3000);
                        }
                    } catch (Exception e)
                    {
                    }
                }
            }

            return PlugIn.BUFFER_PROCESSED_OK;
        }

        // //////////////////////////////////////////////////////////////
        // Local methods
        // //////////////////////////////////////////////////////////////
        public void read(javax.media.Buffer buffer) throws IOException
        {
            if (closed)
            {
                throw new IOException("The source stream is closed");
            }

            Buffer current = null;

            // we shouldnt need to wait for data here since we were
            // notified of data being available before. But just to
            // be safe.
            synchronized (bufferQ)
            {
                while (!bufferQ.canRead())
                {
                    try
                    {
                        bufferQ.wait();
                    } catch (Exception e)
                    {
                    }
                }
                current = bufferQ.read();
            }

            if (current.isEOM())
            {
                synchronized (drainSync)
                {
                    if (draining)
                    {
                        draining = false;
                        drainSync.notifyAll();
                    }
                }
            }

            // Copy all the attributes from current to buffer.
            Object data = buffer.getData();
            Object hdr = buffer.getHeader();
            buffer.copy(current);
            current.setData(data);
            current.setHeader(hdr);

            // return this buffer as being a free buffer
            synchronized (bufferQ)
            {
                hasRead = true;
                bufferQ.readReport();
                bufferQ.notifyAll();
            }
        }

        /**
         * Flush the bufferQ.
         */
        protected void reset()
        {
            synchronized (bufferQ)
            {
                while (bufferQ.canRead())
                {
                    Buffer b = bufferQ.read();
                    bufferQ.readReport();
                }
                bufferQ.notifyAll();
            }
            // A reset will break the lock in drain.
            synchronized (drainSync)
            {
                if (draining)
                {
                    draining = false;
                    drainSync.notifyAll();
                }
            }
        }

        public void run()
        {
            for (;;)
            {
                try
                {
                    // Check to see if the thread has been started.
                    synchronized (startReq)
                    {
                        while (!started && !closed)
                        {
                            startReq.wait();
                        }
                    }

                    synchronized (bufferQ)
                    {
                        do
                        {
                            //
                            // If the data hasn't been read, then we won't
                            // keep this loop going.
                            // This wait is waken up if either the reader has
                            // read the data or if there's new data
                            // coming in (via process()).
                            //
                            // The reason for the 250 time limit goes like
                            // this: if the buffer is filled buffer the
                            // reader hasn't receive the first transferData,
                            // it will never receive any notification again.
                            // So we need to artificially wake up once a
                            // while to call transferData.
                            //
                            if (!hasRead)
                                bufferQ.wait(250);
                            hasRead = false;
                        } while (!bufferQ.canRead() && !closed && started);
                    }

                    // Came out due to a close
                    if (closed)
                        return;

                    // Came out due to a stop
                    if (!started)
                        continue;

                    // there is some data ready to be sent over
                    if (handler != null)
                        handler.transferData(this);

                } catch (InterruptedException e)
                {
                    System.err.println("Thread " + e.getMessage());
                    return;
                }
            }

        }// end of run

        public void setTransferHandler(BufferTransferHandler handler)
        {
            this.handler = handler;
        }

        protected void start()
        {
            synchronized (startReq)
            {
                if (started)
                    return;
                started = true;
                startReq.notifyAll();
            }
            synchronized (bufferQ)
            {
                hasRead = true;
                bufferQ.notifyAll();
            }
        }

        // ////////////////////////////////////////////////////////////
        // Runnable Method
        // ////////////////////////////////////////////////////////////

        protected void stop()
        {
            synchronized (startReq)
            {
                started = false;
            }
            synchronized (bufferQ)
            {
                bufferQ.notifyAll();
            }

        }

    }// end of RawBufferStream

    // ////////////////////////////////////////////////////////////////////
    // /
    // / TIMEBASE : INNER CLASS
    // ////////////////////////////////////////////////////////////////////
    class RawMuxTimeBase extends MediaTimeBase
    {
        long ticks = 0;
        boolean updated = false;

        @Override
        public long getMediaTime()
        {
            if (masterTrackID >= 0)
            {
                return mediaTime[masterTrackID];
            }

            if (!updated)
                return ticks;

            if (mediaTime.length == 1)
            {
                ticks = mediaTime[0];
            } else
            {
                ticks = mediaTime[0];
                for (int i = 1; i < mediaTime.length; i++)
                {
                    if (mediaTime[i] < ticks)
                        ticks = mediaTime[i];
                }
            }

            updated = false;

            return ticks;
        }

        public void update()
        {
            updated = true;
        }

    }// end of RawMuxTimeBase

    // array of content descriptors this mux supports
    protected ContentDescriptor[] supported = null;

    // the content descriptor set on the mux
    protected ContentDescriptor contentDesc = null;

    // the datasource of this multiplexer
    protected RawBufferDataSource source = null;
    // the sourcestreams of the datasource
    protected RawBufferSourceStream[] streams = null;
    // Clock provided by this mux. Clock is non null only if
    // multiplexer implements the clock interface
    protected BasicClock clock = null;

    // Keeps track of the media time per track.
    protected RawMuxTimeBase timeBase = null;
    protected long mediaTime[];

    protected int masterTrackID = -1;

    boolean sourceDisconnected = false;
    boolean allowDrop = false;

    boolean hasRead = false; // True if the reader has read the data.

    protected int numTracks = 0;

    protected Format[] trackFormats;

    // /////////////////////////////////////////////////////////////////////////
    // PLUGIN METHODS
    // /////////////////////////////////////////////////////////////////////////

    // Monitor stuff
    protected MonitorAdapter mc[] = null;

    // For comparing formats.
    static AudioFormat mpegAudio = new AudioFormat(AudioFormat.MPEG_RTP);

    // ////////////////////////////////////////////////////////////////////
    // / CLOCK METHODS
    // /
    // ////////////////////////////////////////////////////////////////////
    Object timeSetSync = new Object();

    boolean started = false;

    // /////////////////////////////////////////////////////////////////////////
    // MULTIPLEXER METHODS
    // /////////////////////////////////////////////////////////////////////////

    // Times are in millisecs range.
    long systemStartTime = -1;

    long mediaStartTime = -1;

    public RawBufferMux()
    {
        supported = new ContentDescriptor[1];
        supported[0] = new ContentDescriptor(ContentDescriptor.RAW);
        timeBase = new RawMuxTimeBase();
        clock = new BasicClock();
        try
        {
            clock.setTimeBase(timeBase);
        } catch (Exception e)
        {
        }
    }

    /**
     * Closes the plug-in component and releases resources. No more data will be
     * accepted by the plug-in after a call to this method. The plug-in can be
     * reinstated after being closed by calling <tt>open</tt>.
     */
    public void close()
    {
        // stop() and disconnect() datasource and set it to null
        if (source != null)
        {
            try
            {
                source.stop();
                source.disconnect();
            } catch (IOException e)
            {
            }
            source = null;
        }

        for (int i = 0; i < mc.length; i++)
        {
            if (mc[i] != null)
                mc[i].close();
        }
    }

    /**
     * Get the output <tt>DataSource</tt> from this multiplexer. The DataSource
     * returned can be a push or pull datasource. i.e. a
     * <tt>Push[Pull]DataSource</tt> or <tt>Push[Pull]BufferDataSource</tt>. <BR>
     * The datasource must be returned in the connected state.
     *
     * @return the output <tt>DataSource</tt>
     */
    public DataSource getDataOutput()
    {
        return source;
    }

    public long getMediaNanoseconds()
    {
        return clock.getMediaNanoseconds();
    }

    public Time getMediaTime()
    {
        return clock.getMediaTime();

    }

    /**
     * Returns a descriptive name for the plug-in. This is a user readable
     * string.
     */
    public String getName()
    {
        return "Raw Buffer Multiplexer";
    }

    public float getRate()
    {
        return clock.getRate();
    }

    public Time getStopTime()
    {
        return clock.getStopTime();
    }

    public Format[] getSupportedInputFormats()
    {
        return new Format[] { new AudioFormat(null), new VideoFormat(null) };
    }

    /**
     * Lists the possible output contentdescriptors of the processed data. If
     * <tt>input</tt> is non-null, then it lists the possible output
     * contentdescriptors given that the input data are of the formats specified
     * by <tt>inputs</tt>. If <tt>inputs</tt> is null, then it lists all
     * possible output content descriptors that this plug-in advertises.
     */
    public ContentDescriptor[] getSupportedOutputContentDescriptors(Format[] fmt)
    {
        // we support a raw format, so we dont really need to check the input
        // formats here as we are just going to pass the stream on
        // without looking at the format.
        return supported;
    }

    public Time getSyncTime()
    {
        return clock.getSyncTime();

    }

    public TimeBase getTimeBase()
    {
        return clock.getTimeBase();
    }

    /**
     * Initialize the tracks in the multiplexer with formats given in an array
     * of track formats. The indexes used in the format array are used
     * subsequently as keys to identify each individual track in the
     * <tt>process</tt> method. This methods should be called only once. A
     * java.lang.Error is thrown if it's called more than once.
     *
     * @param trackFormats
     *            an array for formats specifying the formats for each track in
     *            the multiplexer.
     * @return false if one or more of the input formats are not supported.
     */
    public boolean initializeTracks(Format[] trackFormats)
    {
        if (source.getStreams() != null)
            throw new java.lang.Error(
                    "initializeTracks has been called previously. ");
        source.initialize(trackFormats);
        streams = (RawBufferSourceStream[]) source.getStreams();
        // we support any input format, so always return true
        return true;
    }

    public Time mapToTimeBase(Time t) throws ClockStoppedException
    {
        return clock.mapToTimeBase(t);
    }

    /**
     * Opens the plug-in software or hardware component and acquires necessary
     * resources. If all the needed resources could not be acquired, it throws a
     * ResourceUnavailableException. Data should not be passed into the plug-in
     * without first calling this method.
     */
    public void open() throws ResourceUnavailableException
    {
        // the datasource must be created in
        // setContentDescriptor & streams created in
        // initializeTracks(). Make sure the source and streams were
        // created and connect the source.
        initializeTracks(trackFormats);

        if ((source == null) || (source.getStreams() == null))
            throw new ResourceUnavailableException(
                    "DataSource and SourceStreams were not created succesfully.");
        try
        {
            source.connect();
        } catch (IOException e)
        {
            throw new ResourceUnavailableException(e.getMessage());
        }

        int len = 0;
        int i;

        mediaTime = new long[trackFormats.length];
        mc = new MonitorAdapter[trackFormats.length];

        for (i = 0; i < trackFormats.length; i++)
        {
            mediaTime[i] = 0;
            if (trackFormats[i] instanceof VideoFormat
                    || trackFormats[i] instanceof AudioFormat)
            {
                mc[i] = new MonitorAdapter(trackFormats[i], this);
                if (mc[i] != null)
                    len++;
            }
        }

        int j = 0;
        controls = new Control[len];
        for (i = 0; i < mc.length; i++)
        {
            if (mc[i] != null)
                controls[j++] = mc[i];
        }
    }

    /**
     * Process the buffer and multiplex it with data from other tracks. The
     * multiplexed output is sent to the output <tt>DataSource</tt>.
     *
     * @param buffer
     *            the input buffer
     * @param trackID
     *            the index identifying the track where the input buffer
     *            belongs.
     * @return BUFFER_PROCESSED_OK if the processing is successful. Other
     *         possible return codes are defined in PlugIn.
     * @see PlugIn
     */
    public int process(Buffer buffer, int trackID)
    {
        // If the processor starts out having RTP times, before the
        // data comes out of this processor, we should reset the
        // RTP flag and sets it to RELATIVE time. Otherwise, the
        // next guy in the processing chain may compute the time
        // incorrectly.
        if ((buffer.getFlags() & Buffer.FLAG_RTP_TIME) != 0)
        {
            buffer.setFlags((buffer.getFlags() & ~Buffer.FLAG_RTP_TIME)
                    | Buffer.FLAG_RELATIVE_TIME);
        }

        // If the monitor is enabled, we'll send the data to the monitor.
        if (mc[trackID] != null && mc[trackID].isEnabled())
            mc[trackID].process(buffer);

        if ((streams == null) || (buffer == null)
                || (trackID >= streams.length))
        {
            return PlugIn.BUFFER_PROCESSED_FAILED;
        }

        updateTime(buffer, trackID);

        return streams[trackID].process(buffer);
    }

    /**
     * Resets the state of the plug-in. Typically at end of media or when media
     * is repositioned.
     */
    public void reset()
    {
        for (int i = 0; i < streams.length; i++)
        {
            streams[i].reset();
            if (mc[i] != null)
                mc[i].reset();
        }
    }

    /**
     * Set the output content-type.
     *
     * @param outputContentDescriptor
     *            the content-type of the output.
     * @exception UnsupportedFormatException
     *                if the <tt>outputContentDescriptor</tt> cannot be
     *                supported by the Multiple xer.
     * @exception FormatChangeException
     *                if the Multiplexer does not support format changes after
     *                it has been set.
     */
    public ContentDescriptor setContentDescriptor(
            ContentDescriptor outputContentDescriptor)
    {
        // we support changes in contentdescriptor after it has
        // been set, so no need to check to see if its set and no
        // need to return FormatChangeException

        if (matches(outputContentDescriptor, supported) == null)
            return null;

        // create the datasource and set its output
        // contentdescriptor
        contentDesc = outputContentDescriptor;
        source = new RawBufferDataSource();

        return contentDesc;
    }

    public Format setInputFormat(Format input, int trackID)
    {
        if (trackID < numTracks)
            trackFormats[trackID] = input;
        for (int i = 0; i < numTracks; i++)
            if (trackFormats[i] == null)
                return input;
        // all formats are set
        // initializeTracks(trackFormats);
        return input;
    }

    public void setMediaTime(Time now)
    {
        synchronized (timeSetSync)
        {
            clock.setMediaTime(now);
            for (int i = 0; i < mediaTime.length; i++)
                mediaTime[i] = now.getNanoseconds();
            timeBase.update();
            systemStartTime = System.currentTimeMillis();
            mediaStartTime = now.getNanoseconds() / 1000000;
        }
    }

    public int setNumTracks(int nTracks)
    {
        numTracks = nTracks;
        trackFormats = new Format[nTracks];
        for (int i = 0; i < nTracks; i++)
            trackFormats[i] = null;
        return nTracks;
    }

    public float setRate(float factor)
    {
        if (factor == clock.getRate())
            return factor;
        return clock.setRate(1.0f);

    }

    public void setStopTime(Time stopTime)
    {
        clock.setStopTime(stopTime);
    }

    public void setTimeBase(TimeBase master)
            throws IncompatibleTimeBaseException
    {
        if (master != timeBase)
            throw new IncompatibleTimeBaseException();
    }

    public void stop()
    {
        synchronized (timeSetSync)
        {
            if (!started)
                return;
            started = false;
            clock.stop();
            timeBase.mediaStopped();
        }
    }

    public void syncStart(Time at)
    {
        synchronized (timeSetSync)
        {
            if (started)
                return;
            started = true;
            clock.syncStart(at);
            timeBase.mediaStarted();
            systemStartTime = System.currentTimeMillis();
            mediaStartTime = getMediaNanoseconds() / 1000000;
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // /
    // / INNER CLASS : PushBufferStream of Multiplexer
    // ////////////////////////////////////////////////////////////////////////

    /**
     * Update the media time per track.
     */
    protected void updateTime(Buffer buf, int trackID)
    {
        if (buf.getFormat() instanceof AudioFormat)
        {
            if (mpegAudio.matches(buf.getFormat()))
            {
                if (buf.getTimeStamp() < 0)
                {
                    if (systemStartTime >= 0)
                        mediaTime[trackID] = (mediaStartTime
                                + System.currentTimeMillis() - systemStartTime) * 1000000;
                } else
                    mediaTime[trackID] = buf.getTimeStamp();
            } else
            {
                // If it's audio data and the time stamp is undefined,
                // we'll compute from the audio duration.
                long t = ((AudioFormat) buf.getFormat()).computeDuration(buf
                        .getLength());
                if (t >= 0)
                    mediaTime[trackID] += t;
                else
                    mediaTime[trackID] = buf.getTimeStamp();
            }

        } else if (buf.getTimeStamp() < 0)
        {
            // This is video with TIME_UNKNOWN.
            if (systemStartTime >= 0)
                mediaTime[trackID] = (mediaStartTime
                        + System.currentTimeMillis() - systemStartTime) * 1000000;
        } else
            mediaTime[trackID] = buf.getTimeStamp();

        timeBase.update();
    }

}// end of class RawBufferMultiplexer

