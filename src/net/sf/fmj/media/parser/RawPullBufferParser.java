package net.sf.fmj.media.parser;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

public class RawPullBufferParser extends RawPullStreamParser
{
    class FrameTrack implements Track
    {
        Demultiplexer parser;
        PullBufferStream pbs;
        boolean enabled = true;
        Format format = null;
        TrackListener listener;
        Integer stateReq = new Integer(0);

        public FrameTrack(Demultiplexer parser, PullBufferStream pbs)
        {
            this.pbs = pbs;
            format = pbs.getFormat();
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
            // If the buffer is empty, just allocate some random number.
            if (buffer.getData() == null)
                buffer.setData(new byte[500]);

            try
            {
                pbs.read(buffer);
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

    static final String NAME = "Raw pull stream parser";

    @Override
    public String getName()
    {
        return NAME;
    }

    /**
     * Opens the plug-in software or hardware component and acquires necessary
     * resources. If all the needed resources could not be acquired, it throws a
     * ResourceUnavailableException. Data should not be passed into the plug-in
     * without first calling this method.
     */
    @Override
    public void open()
    {
        if (tracks != null)
            return;
        tracks = new Track[streams.length];
        for (int i = 0; i < streams.length; i++)
        {
            tracks[i] = new FrameTrack(this, (PullBufferStream) streams[i]);
        }
    }

    @Override
    public void setSource(DataSource source)
        throws IncompatibleSourceException, IOException
    {
        SourceStream[] streams;

        if (!(source instanceof PullBufferDataSource))
        {
            throw new IncompatibleSourceException(
                    "DataSource not supported: " + source);
        } else
        {
            streams = ((PullBufferDataSource) source).getStreams();
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

    // //////////////////////
    //
    // Inner class
    // //////////////////////

    /**
     * Override this if the Parser has additional requirements from the
     * PullBufferStream
     */
    @Override
    protected boolean supports(SourceStream[] streams)
    {
        return ((streams[0] != null) && (streams[0] instanceof PullBufferStream));
    }
}
