package net.sf.fmj.media.multiplexer;

import java.io.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.control.*;
import net.sf.fmj.media.datasink.*;

public abstract class BasicMux extends BasicPlugIn implements
        javax.media.Multiplexer, Clock
{
    class BasicMuxDataSource extends PushDataSource
    {
        private BasicMux mux;
        private ContentDescriptor cd;
        private BasicMuxPushStream[] streams;
        private BasicMuxPushStream stream;
        private boolean connected = false;
        private boolean started = false;

        public BasicMuxDataSource(BasicMux mux, ContentDescriptor cd)
        {
            this.cd = cd;
            this.mux = mux;
        }

        @Override
        public void connect() throws IOException
        {
            if (streams == null)
                getStreams();
            connected = true;
            synchronized (sourceLock)
            {
                sourceLock.notifyAll();
            }
        }

        @Override
        public void disconnect()
        {
            connected = false;
        }

        @Override
        public String getContentType()
        {
            return cd.getContentType();
        }

        @Override
        public Object getControl(String s)
        {
            return null;
        }

        @Override
        public Object[] getControls()
        {
            return new Control[0];
        }

        @Override
        public Time getDuration()
        {
            return Duration.DURATION_UNKNOWN;
        }

        @Override
        public PushSourceStream[] getStreams()
        {
            if (streams == null)
            {
                streams = new BasicMuxPushStream[1];
                stream = new BasicMuxPushStream(cd);
                streams[0] = stream;
                setStream(stream);
            }
            return streams;
        }

        boolean isConnected()
        {
            return connected;
        }

        boolean isStarted()
        {
            return started;
        }

        @Override
        public void start() throws IOException
        {
            if (streams == null || !connected)
                throw new IOException("Source not connected yet!");
            started = true;
            synchronized (sourceLock)
            {
                sourceLock.notifyAll();
            }
        }

        @Override
        public void stop()
        {
            started = false;
        }
    }

    /****************************************************************
     * BasicMuxPushStream
     ****************************************************************/

    class BasicMuxPushStream implements PushSourceStream
    {
        private ContentDescriptor cd;
        private byte[] data;
        private int dataLen;
        private int dataOff;
        private Integer writeLock = new Integer(0);

        public BasicMuxPushStream(ContentDescriptor cd)
        {
            this.cd = cd;
        }

        public boolean endOfStream()
        {
            return isEOS();
        }

        public ContentDescriptor getContentDescriptor()
        {
            return cd;
        }

        public long getContentLength()
        {
            return LENGTH_UNKNOWN;
        }

        public Object getControl(String s)
        {
            return null;
        }

        public Object[] getControls()
        {
            return new Control[0];
        }

        public int getMinimumTransferSize()
        {
            return dataLen;
        }

        public int read(byte[] buffer, int offset, int length)
                throws IOException
        {
            int transferred = 0;

            synchronized (writeLock)
            {
                if (dataLen == -1)
                    transferred = -1;
                else
                {
                    if (length >= dataLen)
                    {
                        transferred = dataLen;
                    } else
                    {
                        transferred = length;
                    }
                    System.arraycopy(data, dataOff, buffer, offset, transferred);
                    dataLen -= transferred;
                    dataOff += transferred;
                }
                writeLock.notifyAll();
                return transferred;
            }
        }

        synchronized int seek(int location)
        {
            if (sth != null)
            {
                ((Seekable) sth).seek(location);
                int seekVal = (int) (((Seekable) sth).tell());
                return seekVal;
            }
            return -1;
        }

        public void setTransferHandler(SourceTransferHandler sth)
        {
            synchronized (writeLock)
            {
                BasicMux.this.sth = sth;
                if (sth != null && needsSeekable()
                        && !(sth instanceof Seekable))
                {
                    throw new java.lang.Error(
                            "SourceTransferHandler needs to be seekable");
                }
                boolean requireTwoPass = BasicMux.this.requireTwoPass();

                if (requireTwoPass)
                {
                    if ((sth != null) && (sth instanceof RandomAccess))
                    {
                        RandomAccess st = (RandomAccess) sth;
                        st.setEnabled(true);
                    }
                }
                writeLock.notifyAll();
            }
        }

        synchronized int write(byte[] data, int offset, int length)
        {
            if (sth == null)
                return 0;

            if (isLiveData && sth instanceof Syncable)
                ((Syncable) sth).setSyncEnabled();

            synchronized (writeLock)
            {
                this.data = data;
                dataOff = offset;
                dataLen = length;
                // tell the source transfer handler that data is available
                // (even if its an eos)
                sth.transferData(this);
                while (dataLen > 0)
                {
                    if (dataLen == length)
                    {
                        try
                        {
                            writeLock.wait();
                        } catch (InterruptedException ie)
                        {
                        }
                    }
                    if (sth == null)
                        break;
                    // If its not fully consumed but atleast partially consumed
                    if (dataLen > 0 && dataLen != length)
                    {
                        length = dataLen;
                        sth.transferData(this);
                    }
                }
            }
            return length; // what is this value for?
        }

    }

    class BasicMuxTimeBase extends MediaTimeBase
    {
        long ticks = 0;
        boolean updated = false;

        @Override
        public long getMediaTime()
        {
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

    }// end of BasicMuxTimeBase

    /****************************************************************
     * INNER CLASSES
     ****************************************************************/

    class SWC implements StreamWriterControl, Owned
    {
        private BasicMux bmx;

        public SWC(BasicMux bmx)
        {
            this.bmx = bmx;
        }

        public java.awt.Component getControlComponent()
        {
            return null;
        }

        public Object getOwner()
        {
            return bmx;
        }

        public long getStreamSize()
        {
            return bmx.getStreamSize();
        }

        public boolean setStreamSizeLimit(long limit)
        {
            bmx.fileSizeLimit = limit;
            return streamSizeLimitSupported;
        }
    }

    /****************************************************************
     * Variables and Constants
     ****************************************************************/

    protected Format[] supportedInputs;
    protected ContentDescriptor[] supportedOutputs;
    protected int numTracks = 0;
    protected Format[] inputs;
    protected BasicMuxDataSource source; // push data source
    protected BasicMuxPushStream stream;
    protected ContentDescriptor outputCD;
    protected boolean flushing = false;
    protected Integer sourceLock = new Integer(0);
    protected boolean eos = false;
    protected boolean firstBuffer = true;
    protected int fileSize = 0;
    protected int filePointer = 0;
    protected long fileSizeLimit = -1;

    protected boolean streamSizeLimitSupported = true;
    protected boolean fileSizeLimitReached = false;

    protected SourceTransferHandler sth = null;

    protected boolean isLiveData = false;

    protected StreamWriterControl swc = null;

    protected MonitorAdapter mc[] = null;
    // the timebase exported by this clock
    protected BasicMuxTimeBase timeBase = null;

    // synchronisation object on which streams must wait.
    Object startup = new Integer(0);
    // to be used in audio timestamps in case they are -1.
    // to be used to block video till audio is received
    boolean readyToStart = false;

    // Keeps track of the media time per track.
    long mediaTime[];

    boolean ready[];

    protected BasicClock clock = null;

    // index of the master stream incase this mux is a clock and does
    // synchronisation
    int master = 0;

    boolean mClosed = false;

    boolean dataReady = false;

    boolean startCompensated = false;

    Object dataLock = new Object();

    Buffer firstBuffers[];

    boolean firstBuffersDone[];

    int nonKeyCount[];

    long masterTime = -1;

    // Check for these special formats since the key frame flag is
    // not reliably set.
    VideoFormat jpegFmt = new VideoFormat(VideoFormat.JPEG);
    VideoFormat mjpgFmt = new VideoFormat(VideoFormat.MJPG);
    VideoFormat rgbFmt = new VideoFormat(VideoFormat.RGB);
    VideoFormat yuvFmt = new VideoFormat(VideoFormat.YUV);
    protected int maxBufSize = 32768; // Don't reduce this value below 2048
    protected byte[] buf = new byte[maxBufSize];
    protected int bufOffset;

    protected int bufLength;
    /****************************************************************
     * Clock methods
     ****************************************************************/

    Object timeSetSync = new Object();
    boolean started = false;
    long systemStartTime = System.currentTimeMillis() * 1000000;

    /****************************************************************
     * Multiplexer methods
     ****************************************************************/

    public BasicMux()
    {
        timeBase = new BasicMuxTimeBase();
        clock = new BasicClock();
        try
        {
            clock.setTimeBase(timeBase);
        } catch (Exception e)
        {
        }

        swc = new SWC(this);
        controls = new Control[] { swc };
    }

    protected void bufClear()
    {
        bufOffset = 0;
        bufLength = 0;
    }

    protected void bufFlush()
    {
        filePointer -= bufLength; // It is going to be incremented in write()
        write(buf, 0, bufLength);
    }

    protected void bufSkip(int size)
    {
        bufOffset += size;
        bufLength += size;
        filePointer += size;
    }

    protected void bufWriteByte(byte value)
    {
        buf[bufOffset] = value;
        bufOffset++;
        bufLength++;
        filePointer++;
    }

    protected void bufWriteBytes(byte[] bytes)
    {
        System.arraycopy(bytes, 0, buf, bufOffset, bytes.length);
        bufOffset += bytes.length;
        bufLength += bytes.length;
        filePointer += bytes.length;
    }

    protected void bufWriteBytes(String s)
    {
        byte[] bytes = s.getBytes();
        bufWriteBytes(bytes);
    }

    protected void bufWriteInt(int value)
    {
        buf[bufOffset + 0] = (byte) ((value >> 24) & 0xFF);
        buf[bufOffset + 1] = (byte) ((value >> 16) & 0xFF);
        buf[bufOffset + 2] = (byte) ((value >> 8) & 0xFF);
        buf[bufOffset + 3] = (byte) ((value >> 0) & 0xFF);
        bufOffset += 4;
        bufLength += 4;
        filePointer += 4;
    }

    protected void bufWriteIntLittleEndian(int value)
    {
        buf[bufOffset + 3] = (byte) ((value >>> 24) & 0xFF);
        buf[bufOffset + 2] = (byte) ((value >>> 16) & 0xFF);
        buf[bufOffset + 1] = (byte) ((value >>> 8) & 0xFF);
        buf[bufOffset + 0] = (byte) ((value >>> 0) & 0xFF);
        bufOffset += 4;
        bufLength += 4;
        filePointer += 4;
    }

    protected void bufWriteShort(short value)
    {
        buf[bufOffset + 0] = (byte) ((value >> 8) & 0xFF);
        buf[bufOffset + 1] = (byte) ((value >> 0) & 0xFF);
        bufOffset += 2;
        bufLength += 2;
        filePointer += 2;
    }

    protected void bufWriteShortLittleEndian(short value)
    {
        buf[bufOffset + 1] = (byte) ((value >> 8) & 0xFF);
        buf[bufOffset + 0] = (byte) ((value >> 0) & 0xFF);
        bufOffset += 2;
        bufLength += 2;
        filePointer += 2;
    }

    private boolean checkReady()
    {
        if (readyToStart)
            return true;
        for (int i = 0; i < ready.length; i++)
        {
            if (!ready[i])
                return false;
        }
        readyToStart = true;
        return true;
    }

    public void close()
    {
        if (sth != null)
        {
            writeFooter();
            write(null, 0, -1);
        }

        for (int i = 0; i < mc.length; i++)
        {
            if (mc[i] != null)
                mc[i].close();
        }

        synchronized (dataLock)
        {
            mClosed = true;
            dataLock.notifyAll();
        }
    }

    private boolean compensateStart(Buffer buffer, int trackID)
    {
        synchronized (dataLock)
        {
            // This is the case when all the data have arrived, but
            // some buffers should have been dropped. The following
            // code throw away the buffers that are behind. For video,
            // key frames are carefully considered.
            if (dataReady)
            {
                if (!firstBuffersDone[trackID])
                {
                    if (buffer.getTimeStamp() < masterTime)
                    {
                        // Drop this frame.
                        return false;
                    } else
                    {
                        if (buffer.getFormat() instanceof VideoFormat)
                        {
                            Format fmt = buffer.getFormat();
                            boolean isKey = (jpegFmt.matches(fmt)
                                    || mjpgFmt.matches(fmt)
                                    || rgbFmt.matches(fmt) || yuvFmt
                                    .matches(fmt));
                            if (isKey
                                    || (buffer.getFlags() & Buffer.FLAG_KEY_FRAME) != 0
                                    || nonKeyCount[trackID]++ > 30)
                            {
                                buffer.setTimeStamp(masterTime);
                                firstBuffersDone[trackID] = true;
                            } else
                                return false;
                        } else
                        {
                            // For everything else, the media time has exceeded
                            // the master time, we reset the timestamps.
                            buffer.setTimeStamp(masterTime);
                            firstBuffersDone[trackID] = true;
                        }

                        // Check to see if all the first buffers are being
                        // compensated.
                        for (int i = 0; i < firstBuffersDone.length; i++)
                        {
                            if (!firstBuffersDone[i])
                                return true;
                        }
                        startCompensated = true;
                        return true;
                    }
                }
                return true;
            }

            if (buffer.getTimeStamp() < 0)
            {
                // At least one of the tracks have undefined timestamps,
                // synchronization is deem to fail. We won't attempt any
                // compensation.
                startCompensated = true;
                dataReady = true;
                dataLock.notifyAll();
                return true;

            }

            firstBuffers[trackID] = buffer;

            // Check to see if all the buffers have arrived.
            boolean done = true;

            for (int i = 0; i < firstBuffers.length; i++)
            {
                if (firstBuffers[i] == null)
                    done = false;
            }

            if (!done)
            {
                // If not, we'll wait here until all the buffers have arrived.
                while (!dataReady && !mClosed)
                {
                    try
                    {
                        dataLock.wait();
                    } catch (Exception e)
                    {
                    }
                }

                if (mClosed || firstBuffers[trackID] == null)
                {
                    // We'll drop this buffer after being compensated for.
                    return false;
                }
                return true;
            }

            // The first buffers have all arrived.

            // Find the master time. If audio is there, we
            // use it. Otherwise, choose the smallest time to
            // be the master time.

            masterTime = firstBuffers[0].getTimeStamp();

            for (int i = 0; i < firstBuffers.length; i++)
            {
                if (firstBuffers[i].getFormat() instanceof AudioFormat)
                {
                    masterTime = firstBuffers[i].getTimeStamp();
                    break;
                }
                if (firstBuffers[i].getTimeStamp() < masterTime)
                    masterTime = firstBuffers[i].getTimeStamp();
            }

            // For times bigger than master time, sets it to the
            // master time. If not, we need to drop the frame.

            startCompensated = true;
            for (int i = 0; i < firstBuffers.length; i++)
            {
                if (firstBuffers[i].getTimeStamp() >= masterTime)
                {
                    firstBuffers[i].setTimeStamp(masterTime);
                    firstBuffersDone[i] = true;
                } else
                {
                    firstBuffers[i] = null;
                    startCompensated = false;
                }
            }

            // Release the lock and buffer waiting to be processed since
            // all the initial buffers have arrived.
            synchronized (dataLock)
            {
                dataReady = true;
                dataLock.notifyAll();
            }

            return (firstBuffers[trackID] != null);

        } // dataLock.
    }

    /****************************************************************
     * Local methods
     ****************************************************************/

    protected int doProcess(Buffer buffer, int trackID)
    {
        // Simple mux - just write the contents of the buffer
        byte[] data = (byte[]) buffer.getData();
        int dataLen = buffer.getLength();
        if (!buffer.isEOM())
            write(data, buffer.getOffset(), dataLen);
        return BUFFER_PROCESSED_OK;
    }

    public DataSource getDataOutput()
    {
        if (source == null)
        {
            source = new BasicMuxDataSource(this, outputCD);
            synchronized (sourceLock)
            {
                sourceLock.notifyAll();
            }
        }
        return source;
    }

    private long getDuration(Buffer buffer)
    {
        javax.media.format.AudioFormat format = (javax.media.format.AudioFormat) buffer
                .getFormat();

        long duration = format.computeDuration(buffer.getLength());

        if (duration < 0)
            return 0;

        return duration;
    }

    public long getMediaNanoseconds()
    {
        return clock.getMediaNanoseconds();
    }

    public Time getMediaTime()
    {
        return clock.getMediaTime();

    }

    public float getRate()
    {
        return clock.getRate();
    }

    public Time getStopTime()
    {
        return clock.getStopTime();
    }

    long getStreamSize()
    {
        return fileSize;
    }

    public Format[] getSupportedInputFormats()
    {
        return supportedInputs;
    }

    public ContentDescriptor[] getSupportedOutputContentDescriptors(
            Format[] inputs)
    {
        return supportedOutputs;
    }

    public Time getSyncTime()
    {
        return clock.getSyncTime();

    }

    public TimeBase getTimeBase()
    {
        return clock.getTimeBase();
    }

    boolean isEOS()
    {
        return eos;
    }

    public Time mapToTimeBase(Time t) throws ClockStoppedException
    {
        return clock.mapToTimeBase(t);
    }

    /* Should return true if it requires a seekable transfer handler */
    boolean needsSeekable()
    {
        return false;
    }

    public void open()
    {
        int i;
        firstBuffer = true;
        firstBuffers = new Buffer[inputs.length];
        firstBuffersDone = new boolean[inputs.length];
        nonKeyCount = new int[inputs.length];
        mediaTime = new long[inputs.length];

        for (i = 0; i < inputs.length; i++)
        {
            firstBuffers[i] = null;
            firstBuffersDone[i] = false;
            nonKeyCount[i] = 0;
            mediaTime[i] = 0;
        }

        ready = new boolean[inputs.length];
        resetReady();

        int len = 0;
        mc = new MonitorAdapter[inputs.length];

        for (i = 0; i < inputs.length; i++)
        {
            if (inputs[i] instanceof VideoFormat
                    || inputs[i] instanceof AudioFormat)
            {
                mc[i] = new MonitorAdapter(inputs[i], this);
                if (mc[i] != null)
                    len++;
            }
        }

        int j = 0;
        controls = new Control[len + 1];
        for (i = 0; i < mc.length; i++)
        {
            if (mc[i] != null)
                controls[j++] = mc[i];
        }
        controls[j] = swc;
    }

    public int process(Buffer buffer, int trackID)
    {
        if (buffer.isDiscard())
            return BUFFER_PROCESSED_OK;
        if (!isLiveData && (buffer.getFlags() & Buffer.FLAG_LIVE_DATA) > 0)
        {
            isLiveData = true;
        }
        // Wait until the datasource is created, connected and started
        while (source == null || !source.isConnected() || !source.isStarted())
        {
            synchronized (sourceLock)
            {
                try
                {
                    sourceLock.wait(500);
                } catch (InterruptedException ie)
                {
                }
                if (flushing)
                {
                    flushing = false;
                    buffer.setLength(0); // flush the buffer and dont process it
                    return BUFFER_PROCESSED_OK;
                }
            }
        }

        synchronized (this)
        {
            if (firstBuffer)
            {
                writeHeader();
                firstBuffer = false;
            }
        }

        if (numTracks > 1)
        {
            // For buffers with RTP time stamps, we'll skip until
            // we reach the buffers with non-zero time. That's
            // when synchronization is possible.
            if ((buffer.getFlags() & Buffer.FLAG_RTP_TIME) != 0)
            {
                if (buffer.getTimeStamp() <= 0)
                    return BUFFER_PROCESSED_OK;
            }

            if (!startCompensated)
            {
                if (!compensateStart(buffer, trackID))
                {
                    // Drop the buffer.
                    return BUFFER_PROCESSED_OK;
                }
            }
        }

        updateClock(buffer, trackID);
        if (mc[trackID] != null && mc[trackID].isEnabled())
            mc[trackID].process(buffer);
        int processResult = doProcess(buffer, trackID);
        if (fileSizeLimitReached)
            processResult |= PLUGIN_TERMINATED;
        return processResult;

    }

    /**
     * sub classes should override this method and return true if two passes are
     * required to create the media file. Two passes may be required for example
     * to rearrange the media file so that the resultant file is Streamable
     */
    public boolean requireTwoPass()
    {
        return false;
    }

    public void reset()
    {
        // firstBuffer = true;
        for (int i = 0; i < mediaTime.length; i++)
        {
            mediaTime[i] = 0;
            if (mc[i] != null)
                mc[i].reset();
        }
        timeBase.update();
        resetReady();
        synchronized (sourceLock)
        {
            flushing = true;
            sourceLock.notifyAll();
        }
    }

    private void resetReady()
    {
        for (int i = 0; i < ready.length; i++)
            ready[i] = false;
        readyToStart = false;
        synchronized (startup)
        {
            startup.notifyAll();
        }
    }

    protected int seek(int location)
    {
        if (source == null || !source.isConnected())
            return location;
        filePointer = stream.seek(location);
        return filePointer;
    }

    public ContentDescriptor setContentDescriptor(ContentDescriptor outputCD)
    {
        if (matches(outputCD, supportedOutputs) == null)
            return null;

        // create the datasource and set its output
        // contentdescriptor
        this.outputCD = outputCD;
        return outputCD;
    }

    /*
     * Override this method to filter out formats unsuitable for the specified
     * content-type
     */
    public Format setInputFormat(Format format, int trackID)
    {
        inputs[trackID] = format;
        return format;
    }

    public void setMediaTime(Time now)
    {
        synchronized (timeSetSync)
        {
            clock.setMediaTime(now);
            for (int i = 0; i < mediaTime.length; i++)
                mediaTime[i] = now.getNanoseconds();
            timeBase.update();
        }
    }

    public int setNumTracks(int numTracks)
    {
        this.numTracks = numTracks;

        if (inputs == null)
            inputs = new Format[numTracks];
        else
        {
            Format[] newInputs = new Format[numTracks];
            for (int i = 0; i < inputs.length; i++)
            {
                newInputs[i] = inputs[i];
            }
            inputs = newInputs;
        }

        return numTracks;
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

    void setStream(BasicMuxPushStream ps)
    {
        stream = ps;
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
            systemStartTime = System.currentTimeMillis() * 1000000;
        }
    }

    private void updateClock(Buffer buffer, int trackID)
    {
        // Initially (after reset), block until all the streams
        // have arrived.
        if (!readyToStart && numTracks > 1)
        {
            synchronized (startup)
            {
                ready[trackID] = true;
                if (checkReady())
                {
                    startup.notifyAll();
                } else
                {
                    try
                    {
                        // wait at most for 1 seconds.
                        while (!readyToStart)
                            startup.wait(1000);
                    } catch (Exception e)
                    {
                    }
                }
            }
        }

        // get the timestamp on the incoming buffer
        long timestamp = buffer.getTimeStamp();

        if (timestamp <= 0 && buffer.getFormat() instanceof AudioFormat)
        {
            // If it's audio data and the time stamp is undefined,
            // we'll compute from the audio duration.
            timestamp = mediaTime[trackID];
            mediaTime[trackID] += getDuration(buffer);
        } else if (timestamp <= 0)
        {
            // This is video with TIME_UNKNOWN.
            mediaTime[trackID] = System.currentTimeMillis() * 1000000
                    - systemStartTime;
        } else
            mediaTime[trackID] = timestamp;

        timeBase.update();
    }

    protected int write(byte[] data, int offset, int length)
    {
        if (source == null || !source.isConnected())
            return length;
        if (length > 0)
        {
            filePointer += length;
            if (filePointer > fileSize)
                fileSize = filePointer;
            if (fileSizeLimit > 0 && fileSize >= fileSizeLimit)
                fileSizeLimitReached = true;
        }
        return stream.write(data, offset, length);
    }

    protected void writeFooter()
    {
    }

    protected void writeHeader()
    {
    }
}
