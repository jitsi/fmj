package net.sf.fmj.media.multiplexer;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.utility.*;

import com.lti.utils.synchronization.*;

/**
 * PushDataSource implemented by copying streams. By default, just copies the
 * input streams to the output. Subclasses can override to modify the copy
 * operation.
 *
 * @author Ken Larson
 *
 */
public class StreamCopyPushDataSource extends PushDataSource
{
    private class WriterThread extends CloseableThread
    {
        private final int trackID;
        private final InputStream in;
        private final OutputStream out;
        private Format format;

        public WriterThread(final int trackID, final InputStream in,
                final OutputStream out, Format format)
        {
            super();
            this.trackID = trackID;
            this.in = in;
            this.out = out;
            this.format = format;
        }

        @Override
        public void run()
        {
            try
            {
                write(in, out, trackID);
                logger.finer("WriterThread closing output stream");
                out.close();
            } catch (InterruptedIOException e)
            {
                logger.log(Level.FINE, "" + e, e);
                return;
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                // TODO: how to propagate this?
            } finally
            {
                setClosed();
            }
        }
    }

    private static final Logger logger = LoggerSingleton.logger;
    private final ContentDescriptor outputContentDescriptor;
    private final int numTracks;
    private final InputStream[] inputStreams;
    private final Format[] inputFormats;
    private InputStreamPushSourceStream[] pushSourceStreams;

    private WriterThread[] writerThreads;

    public StreamCopyPushDataSource(ContentDescriptor outputContentDescriptor,
            int numTracks, InputStream[] inputStreams, Format[] inputFormats)
    {
        super();
        this.outputContentDescriptor = outputContentDescriptor;
        this.numTracks = numTracks;
        this.inputStreams = inputStreams;
        this.inputFormats = inputFormats;

    }

    @Override
    public void connect() throws IOException
    {
        logger.finer(getClass().getSimpleName() + " connect");
        this.pushSourceStreams = new InputStreamPushSourceStream[numTracks];
        this.writerThreads = new WriterThread[numTracks];
        for (int track = 0; track < numTracks; ++track)
        {
            final StreamPipe p = new StreamPipe();
            pushSourceStreams[track] = new InputStreamPushSourceStream(
                    outputContentDescriptor, p.getInputStream());
            writerThreads[track] = new WriterThread(track, inputStreams[track],
                    p.getOutputStream(), inputFormats[track]);
            writerThreads[track].setName("WriterThread for track " + track);
            writerThreads[track].setDaemon(true);

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
        for (int track = 0; track < numTracks; ++track)
        {
            writerThreads[track].start();
        }
    }

    @Override
    public void stop() throws IOException
    {
        logger.finer(getClass().getSimpleName() + " stop");
        for (int track = 0; track < numTracks; ++track)
        {
            writerThreads[track].close();
        }

        try
        {
            for (int track = 0; track < numTracks; ++track)
            {
                writerThreads[track].waitUntilClosed();
            }
        } catch (InterruptedException e)
        {
            throw new InterruptedIOException();
        }
    }

    public void waitUntilFinished() throws InterruptedException
    {
        try
        {
            for (int track = 0; track < numTracks; ++track)
            {
                writerThreads[track].waitUntilClosed();
            }
        } catch (InterruptedException e)
        {
            throw e;
        }
    }

    protected void write(InputStream in, OutputStream out, int track)
            throws IOException
    {
        IOUtils.copyStream(in, out);
    }

}