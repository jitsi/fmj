package net.sf.fmj.media;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.protocol.*;

/**
 * Merges multiple {@link PushBufferDataSource}.
 *
 * @author Ken Larson
 *
 */
public class MergingPushBufferDataSource extends PushBufferDataSource
{
    protected final List<PushBufferDataSource> sources;

    public MergingPushBufferDataSource(List<PushBufferDataSource> sources)
    {
        super();
        this.sources = sources;
    }

    @Override
    public void connect() throws IOException
    {
        for (PushBufferDataSource source : sources)
            source.connect();
    }

    @Override
    public void disconnect()
    {
        for (PushBufferDataSource source : sources)
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
        for (PushBufferDataSource source : sources)
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
        for (PushBufferDataSource source : sources)
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
        for (PushBufferDataSource source : sources)
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
    public PushBufferStream[] getStreams()
    {
        final List<PushBufferStream> streams = new ArrayList<PushBufferStream>();
        for (PushBufferDataSource source : sources)
        {
            for (PushBufferStream stream : source.getStreams())
                streams.add(stream);
        }
        return streams.toArray(new PushBufferStream[0]);
    }

    @Override
    public void start() throws IOException
    {
        for (PushBufferDataSource source : sources)
            source.start();
    }

    @Override
    public void stop() throws IOException
    {
        for (PushBufferDataSource source : sources)
            source.stop();
    }

}
