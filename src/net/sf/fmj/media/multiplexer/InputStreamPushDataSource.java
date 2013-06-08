package net.sf.fmj.media.multiplexer;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.utility.*;

/**
 * Adapter from {@link InputStream} to {@link PushDataSource}.
 *
 * @author Ken Larson
 *
 */
public class InputStreamPushDataSource extends PushDataSource
{
    private static final Logger logger = LoggerSingleton.logger;

    private final ContentDescriptor outputContentDescriptor;
    private final int numTracks;
    private final InputStream[] inputStreams;
    private InputStreamPushSourceStream[] pushSourceStreams;

    public InputStreamPushDataSource(ContentDescriptor outputContentDescriptor,
            int numTracks, InputStream[] inputStreams)
    {
        super();
        this.outputContentDescriptor = outputContentDescriptor;
        this.numTracks = numTracks;
        this.inputStreams = inputStreams;
    }

    @Override
    public void connect() throws IOException
    {
        logger.finer(getClass().getSimpleName() + " connect");
        this.pushSourceStreams = new InputStreamPushSourceStream[numTracks];
        for (int track = 0; track < numTracks; ++track)
        {
            pushSourceStreams[track] = new InputStreamPushSourceStream(
                    outputContentDescriptor, inputStreams[track]);
        }

    }

    @Override
    public void disconnect()
    {
        logger.finer(getClass().getSimpleName() + " disconnect");

    }

    @Override
    public String getContentType()
    {
        logger.finer(getClass().getSimpleName() + " getContentType");
        return outputContentDescriptor.getContentType();
    }

    @Override
    public Object getControl(String controlType)
    {
        logger.finer(getClass().getSimpleName() + " getControl");
        return null;
    }

    @Override
    public Object[] getControls()
    {
        logger.finer(getClass().getSimpleName() + " getControls");
        return new Object[0];
    }

    @Override
    public Time getDuration()
    {
        logger.finer(getClass().getSimpleName() + " getDuration");
        return Time.TIME_UNKNOWN; // TODO
    }

    @Override
    public PushSourceStream[] getStreams()
    {
        logger.finer(getClass().getSimpleName() + " getStreams");
        return pushSourceStreams;
    }

    public void notifyDataAvailable(int track)
    {
        pushSourceStreams[track].notifyDataAvailable();
    }

    @Override
    public void start() throws IOException
    {
        logger.finer(getClass().getSimpleName() + " start");
    }

    @Override
    public void stop() throws IOException
    {
        logger.finer(getClass().getSimpleName() + " stop");
    }
}