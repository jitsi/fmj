package net.sf.fmj.apps.mediaserver;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.datasink.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

import com.lti.utils.synchronization.*;

/**
 * Data sink that writes its output to an arbitrary OutputStream. Cannot be
 * constructed by Manager, must be constructed explicitly, because the
 * OutputStream must be specified on construction.
 *
 * @author Ken Larson
 *
 */
public class StreamDataSink extends AbstractDataSink
{
    // if we don't implement Seekable, Sun's code will throw a class cast
    // exception.
    // very strange that we need to be able to seek just because we call
    // setTransferHandler.
    private class WriterThread extends CloseableThread implements
            SourceTransferHandler
    {
        private final PushSourceStream sourceStream;

        private final OutputStream os;
        private SynchronizedBoolean dataAvailable = new SynchronizedBoolean();

        private static final boolean USE_TRANSFER_HANDLER = true;

        private static final int DEFAULT_BUFFER_SIZE = 10000;

        public WriterThread(final PushSourceStream sourceStream, OutputStream os)
        {
            super();
            this.sourceStream = sourceStream;
            this.os = os;
            if (USE_TRANSFER_HANDLER)
                sourceStream.setTransferHandler(this);
        }

        public boolean isRandomAccess()
        {
            return false;
        }

        @Override
        public void run()
        {
            try
            {
                logger.fine("getMinimumTransferSize: "
                        + sourceStream.getMinimumTransferSize());
                final byte[] buffer = new byte[sourceStream
                        .getMinimumTransferSize() > DEFAULT_BUFFER_SIZE ? sourceStream
                        .getMinimumTransferSize() : DEFAULT_BUFFER_SIZE];

                boolean eos = false;
                while (!isClosing() && !eos)
                {
                    if (USE_TRANSFER_HANDLER)
                    {
                        synchronized (dataAvailable)
                        {
                            dataAvailable.waitUntil(true);
                            dataAvailable.setValue(false);
                        }
                    }

                    while (true) // read as long as data keeps coming in
                    {
                        int read = sourceStream.read(buffer, 0, buffer.length);
                        if (read == 0)
                        {
                            break;
                        } else if (read < 0)
                        {
                            eos = true;
                            os.close();
                            logger.fine("EOS");
                            notifyDataSinkListeners(new EndOfStreamEvent(
                                    StreamDataSink.this, "EOS")); // TODO:
                                                                  // needed?
                            break;
                        } else
                        {
                            os.write(buffer, 0, read);
                        }
                    }

                }
                if (!eos)
                    logger.warning("Closed before EOS");
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                notifyDataSinkListeners(new DataSinkErrorEvent(
                        StreamDataSink.this, e.getMessage()));

            } catch (InterruptedException e)
            {
                logger.log(Level.WARNING, "" + e, e);
            } finally
            {
                setClosed();
            }
        }

        public void transferData(PushSourceStream stream)
        {
            dataAvailable.setValue(true);
        }

    }

    private static final Logger logger = LoggerSingleton.logger;
    private PushDataSource source;
    private WriterThread writerThread;

    // TODO: additional listener notifications?

    private final OutputStream os;

    public StreamDataSink(OutputStream os)
    {
        super();
        this.os = os;
    }

    public void close()
    {
        try
        {
            stop();
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }

        // TODO: disconnect source?
    }

    public String getContentType()
    {
        // TODO: do we get this from the source, or the outputLocator?
        if (source != null)
            return source.getContentType();
        else
            return null;
    }

    public Object getControl(String controlType)
    {
        logger.warning("TODO: getControl " + controlType);
        return null;
    }

    public Object[] getControls()
    {
        logger.warning("TODO: getControls");
        return new Object[0];
    }

    public void open() throws IOException, SecurityException
    {
        // TODO: check that there is at least 1 stream.
        // TODO: move this code to start() ?
        PushSourceStream[] streams = source.getStreams();
        // System.out.println("streams: " + streams.length);

        source.connect();

        writerThread = new WriterThread(source.getStreams()[0], os); // TODO
                                                                     // other
                                                                     // tracks?
        writerThread.setName("WriterThread for " + os);
        writerThread.setDaemon(true);

    }

    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        logger.finer("setSource: " + source);
        if (!(source instanceof PushDataSource))
            throw new IncompatibleSourceException();
        this.source = (PushDataSource) source;
    }

    public void start() throws IOException
    {
        source.start();

        writerThread.start();
    }

    public void stop() throws IOException
    {
        if (writerThread != null)
        {
            writerThread.close();
            try
            {
                writerThread.waitUntilClosed();
            } catch (InterruptedException e)
            {
                throw new InterruptedIOException();
            } finally
            {
                writerThread = null;
            }
        }

        if (source != null)
            source.stop();

    }

}
