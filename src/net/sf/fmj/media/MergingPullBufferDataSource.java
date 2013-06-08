package net.sf.fmj.media;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.protocol.*;

/**
 * Merges multiple {@link MergingPullBufferDataSource}.
 *
 * @author Ken Larson
 *
 */
public class MergingPullBufferDataSource extends PullBufferDataSource
{
    protected final List<PullBufferDataSource> sources;

    public MergingPullBufferDataSource(List<PullBufferDataSource> sources)
    {
        super();
        this.sources = sources;
    }

    @Override
    public void connect() throws IOException
    {
        for (PullBufferDataSource source : sources)
            source.connect();
    }

    @Override
    public void disconnect()
    {
        for (PullBufferDataSource source : sources)
            source.disconnect();
    }

    @Override
    public String getContentType()
    {
        // if all content types the same, use it, otherwise,
        // ContentDescriptor.MIXED.
        for (int i = 0; i < sources.size(); ++i)
        {
            if (!sources.get(i).getContentType()
                    .equals(sources.get(0).getContentType()))
                return ContentDescriptor.MIXED;
        }
        return sources.get(0).getContentType();
    }

    @Override
    public Object getControl(String controlType)
    {
        for (PullBufferDataSource source : sources)
        {
            Object control = source.getControl(controlType);
            if (control != null)
                return control;
        }
        return null;
    }

    @Override
    public Object[] getControls()
    {
        final List<Object> controls = new ArrayList<Object>();
        for (PullBufferDataSource source : sources)
        {
            for (Object control : source.getControls())
                controls.add(control);
        }
        return controls.toArray(new Object[0]);
    }

    @Override
    public Time getDuration()
    {
        final List<Time> durations = new ArrayList<Time>();
        for (PullBufferDataSource source : sources)
        {
            durations.add(source.getDuration());
        }

        for (Time duration : durations)
        {
            if (duration.getNanoseconds() == Duration.DURATION_UNKNOWN
                    .getNanoseconds())
                return Duration.DURATION_UNKNOWN;
        }

        for (Time duration : durations)
        {
            if (duration.getNanoseconds() == Duration.DURATION_UNBOUNDED
                    .getNanoseconds())
                return Duration.DURATION_UNBOUNDED;
        }

        long max = -1;

        for (Time duration : durations)
        {
            if (duration.getNanoseconds() > max)
                max = duration.getNanoseconds();
        }
        if (max < 0)
            return Duration.DURATION_UNKNOWN; // should never happen
        return new Time(max);

    }

    @Override
    public PullBufferStream[] getStreams()
    {
        final List<PullBufferStream> streams = new ArrayList<PullBufferStream>();
        for (PullBufferDataSource source : sources)
        {
            for (PullBufferStream stream : source.getStreams())
                streams.add(stream);
        }
        return streams.toArray(new PullBufferStream[0]);
    }

    @Override
    public void start() throws IOException
    {
        for (PullBufferDataSource source : sources)
            source.start();
    }

    @Override
    public void stop() throws IOException
    {
        for (PullBufferDataSource source : sources)
            source.stop();
    }

}
