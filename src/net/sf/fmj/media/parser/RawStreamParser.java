package net.sf.fmj.media.parser;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;

public class RawStreamParser extends RawParser
{
    class FrameTrack implements Track, SourceTransferHandler
    {
        Demultiplexer parser;
        PushSourceStream pss;
        boolean enabled = true;
        CircularBuffer bufferQ;
        Format format = null;
        TrackListener listener;
        Integer stateReq = new Integer(0);
        boolean stopped = true;

        public FrameTrack(Demultiplexer parser, PushSourceStream pss,
                int numOfBufs)
        {
            this.pss = pss;
            pss.setTransferHandler(this);
            bufferQ = new CircularBuffer(numOfBufs);
        }

        public Time getDuration()
        {
            return parser.getDuration();
        }

        public Format getFormat()
        {
            return format;
        }

        public Time getStartTime()
        {
            return new Time(0);
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public Time mapFrameToTime(int frameNumber)
        {
            return new Time(0);
        }

        public int mapTimeToFrame(Time t)
        {
            return -1;
        }

        public void readFrame(Buffer buffer)
        {
            // Retrieve a filled buffer.
            Buffer filled;
            synchronized (stateReq)
            {
                if (stopped)
                {
                    buffer.setDiscard(true);
                    buffer.setFormat(format);
                    return;
                }
            }
            synchronized (bufferQ)
            {
                while (!bufferQ.canRead())
                {
                    try
                    {
                        bufferQ.wait();
                        synchronized (stateReq)
                        {
                            if (stopped)
                            {
                                buffer.setDiscard(true);
                                buffer.setFormat(format);
                                return;
                            }
                        }
                    } catch (Exception e)
                    {
                    }
                }
                filled = bufferQ.read();
                bufferQ.notifyAll();
            }

            // exchange the buffers.
            byte data[] = (byte[]) filled.getData();
            ;
            filled.setData(buffer.getData());
            buffer.setData(data);
            buffer.setLength(filled.getLength());
            buffer.setFormat(format);
            buffer.setRtpTimeStamp(filled.getRtpTimeStamp());
            buffer.setHeaderExtension(filled.getHeaderExtension());
            buffer.setTimeStamp(Buffer.TIME_UNKNOWN);

            synchronized (bufferQ)
            {
                bufferQ.readReport();
                bufferQ.notifyAll();
            }

        }

        public void setEnabled(boolean t)
        {
            if (t)
                pss.setTransferHandler(this);
            else
                pss.setTransferHandler(null);
            enabled = t;
        }

        public void setTrackListener(TrackListener l)
        {
            listener = l;
        }

        public void start()
        {
            // we need to ensure that readFrame is returned to its
            // original state and does not return w/o blocking.
            synchronized (stateReq)
            {
                stopped = false;
            }
            synchronized (bufferQ)
            {
                bufferQ.notifyAll();
            }
        }

        public void stop()
        {
            // we basically need to ensure that readFrame will return
            // immediately.and also make sure that if it is called in
            // the stopped state, it returns w/o blocking.
            synchronized (stateReq)
            {
                stopped = true;
            }
            synchronized (bufferQ)
            {
                bufferQ.notifyAll();
            }
        }

        public void transferData(PushSourceStream pss)
        {
            // Retrieve an empty buffer for the PSS to write into.
            Buffer buffer;
            synchronized (bufferQ)
            {
                while (!bufferQ.canWrite())
                {
                    try
                    {
                        bufferQ.wait();
                    } catch (Exception e)
                    {
                    }
                }
                buffer = bufferQ.getEmptyBuffer();
                bufferQ.notifyAll();
            }

            int size = pss.getMinimumTransferSize();
            byte data[];
            if ((data = (byte[]) buffer.getData()) == null
                    || data.length < size)
            {
                data = new byte[size];
                buffer.setData(data);
            }

            try
            {
                int len = pss.read(data, 0, size);
                buffer.setLength(len);
            } catch (IOException e)
            {
                buffer.setDiscard(true);
            }

            // Put the filled buffer back to the queue for consumption.
            synchronized (bufferQ)
            {
                bufferQ.writeReport();
                bufferQ.notifyAll();
            }
        }
    }

    protected SourceStream[] streams;

    protected Track[] tracks = null;

    static final String NAME = "Raw stream parser";

    public RawStreamParser()
    {
    }

    /**
     * Closes the plug-in component and releases resources. No more data will be
     * accepted by the plug-in after a call to this method. The plug-in can be
     * reinstated after being closed by calling <tt>open</tt>.
     */
    public void close()
    {
        if (source != null)
        {
            try
            {
                source.stop();
                // stop each of the tracks, so that readFrame() can be
                // released.
                for (int i = 0; i < tracks.length; i++)
                    ((FrameTrack) tracks[i]).stop();

                source.disconnect();
            } catch (IOException e)
            {
                // Internal error?
            }
            source = null;
        }
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Track[] getTracks()
    {
        return tracks;
    }

    /**
     * Opens the plug-in software or hardware component and acquires necessary
     * resources. If all the needed resources could not be acquired, it throws a
     * ResourceUnavailableException. Data should not be passed into the plug-in
     * without first calling this method.
     */
    public void open()
    {
        if (tracks != null)
            return;
        tracks = new Track[streams.length];
        for (int i = 0; i < streams.length; i++)
        {
            tracks[i] = new FrameTrack(this, (PushSourceStream) streams[i], 5);
        }
    }

    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        SourceStream[] streams;

        if (!(source instanceof PushDataSource))
        {
            throw new IncompatibleSourceException(
                    "DataSource not supported: " + source);
        } else
        {
            streams = ((PushDataSource) source).getStreams();
        }

        if (streams == null)
        {
            throw new IOException("Got a null stream from the DataSource");
        }

        if (streams.length == 0)
        {
            throw new IOException(
                    "Got a empty stream array from the DataSource");
        }

        if (!supports(streams))
        {
            throw new IncompatibleSourceException(
                    "DataSource not supported: " + source);
        }

        this.source = source;
        this.streams = streams;
    }

    /**
     * Start the parser.
     */
    public void start() throws IOException
    {
        source.start();
        for (int i = 0; i < tracks.length; i++)
            ((FrameTrack) tracks[i]).start();
    }

    /**
     * Stop the parser.
     */
    public void stop()
    {
        try
        {
            source.stop();
            // stop each of the tracks, so that readFrame can be released
            for (int i = 0; i < tracks.length; i++)
                ((FrameTrack) tracks[i]).stop();
        } catch (IOException e)
        {
            // Internal errors?
        }
    }

    // //////////////////////
    //
    // Inner class
    // //////////////////////

    /**
     * Override this if the Parser has additional requirements from the
     * PushSourceStream
     */
    protected boolean supports(SourceStream[] streams)
    {
        return ((streams[0] != null) && (streams[0] instanceof PushSourceStream));
    }
}
