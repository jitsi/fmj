package net.sf.fmj.media.parser;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

public class RawPullStreamParser extends RawParser
{
    class FrameTrack implements Track
    {
        Demultiplexer parser;
        PullSourceStream pss;
        boolean enabled = true;
        Format format = null;
        TrackListener listener;
        Integer stateReq = new Integer(0);

        public FrameTrack(Demultiplexer parser, PullSourceStream pss)
        {
            this.pss = pss;
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
            byte data[] = (byte[]) buffer.getData();

            // If the buffer is empty, just allocate some random number.
            if (data == null)
            {
                data = new byte[500];
                buffer.setData(data);
            }

            try
            {
                int len = pss.read(data, 0, data.length);
                buffer.setLength(len);
            } catch (IOException e)
            {
                buffer.setDiscard(true);
            }
        }

        public void setEnabled(boolean t)
        {
            enabled = t;
        }

        public void setTrackListener(TrackListener l)
        {
            listener = l;
        }

    }

    protected SourceStream[] streams;

    protected Track[] tracks = null;

    static final String NAME = "Raw pull stream parser";

    public RawPullStreamParser()
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
            tracks[i] = new FrameTrack(this, (PullSourceStream) streams[i]);
        }
    }

    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        SourceStream[] streams;

        if (!(source instanceof PullDataSource))
        {
            throw new IncompatibleSourceException(
                    "DataSource not supported: " + source);
        } else
        {
            streams = ((PullDataSource) source).getStreams();
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
    }

    /**
     * Stop the parser.
     */
    public void stop()
    {
        try
        {
            source.stop();
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
     * PullSourceStream
     */
    protected boolean supports(SourceStream[] streams)
    {
        return ((streams[0] != null) && (streams[0] instanceof PullSourceStream));
    }
}
