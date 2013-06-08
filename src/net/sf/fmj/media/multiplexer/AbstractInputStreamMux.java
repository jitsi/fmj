package net.sf.fmj.media.multiplexer;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * An abstract Multiplexer which may be used as a base class for multiplexers
 * which need to simply write something to an output stream as each buffer comes
 * in.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractInputStreamMux extends AbstractMultiplexer
{
    private static final Logger logger = LoggerSingleton.logger;

    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;

    private InputStreamPushDataSource dataOutput;

    private final ContentDescriptor contentDescriptor;

    // TODO: deal with n tracks properly

    private static final int PIPE_SIZE = 200000; // TODO: get from format.

    // TODO: this has to be twice as big as the biggest buffer we are going to
    // process, if this is used
    // for the web server to stream, otherwise it will hang.
    // The reason is that the some data may need be read from the media to
    // realize, but the
    // piped input stream will not be read until after the realize.

    public AbstractInputStreamMux(final ContentDescriptor contentDescriptor)
    {
        super();
        this.contentDescriptor = contentDescriptor;

    }

    @Override
    public void close()
    {
        logger.finer(getClass().getSimpleName() + " close");
        super.close();

        // mgodehardt: disabled, otherwise trailer is not written, sink writes
        // async
        /*
         * if (pipedInputStream != null) { try { pipedInputStream.close(); }
         * catch (IOException e) { logger.log(Level.WARNING, "" + e, e); }
         * finally { pipedInputStream = null; } }
         *
         * if (pipedOutputStream != null) { try { pipedOutputStream.close(); }
         * catch (IOException e) { logger.log(Level.WARNING, "" + e, e); }
         * finally { pipedOutputStream = null; } }
         */

        if (dataOutput != null)
        {
            try
            {
                dataOutput.stop();
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
            }
            dataOutput.disconnect();
        }
    }

    protected InputStreamPushDataSource createInputStreamPushDataSource(
            ContentDescriptor outputContentDescriptor, int numTracks,
            InputStream[] inputStreams)
    {
        return new InputStreamPushDataSource(outputContentDescriptor,
                numTracks, inputStreams);
    }

    // intended to be overridden. This implementation makse this into a RawMux:
    protected void doProcess(Buffer buffer, int trackID, OutputStream os)
            throws IOException
    {
        if (buffer.isEOM())
        {
            os.close();
            return; // TODO: what if there is data in buffer?
        }
        os.write((byte[]) buffer.getData(), buffer.getOffset(),
                buffer.getLength());
    }

    public DataSource getDataOutput()
    {
        if (dataOutput == null)
            dataOutput = createInputStreamPushDataSource(
                    outputContentDescriptor, 1,
                    new InputStream[] { pipedInputStream });
        logger.finer(getClass().getSimpleName() + " getDataOutput");
        return dataOutput;
    }

    /**
     * Not a JMF public API method, just a way for subclasses to get the data
     * output to do notifications.
     */
    protected InputStreamPushDataSource getDataOutputNoInit()
    {
        return dataOutput;
    }

    protected OutputStream getOutputStream()
    {
        return pipedOutputStream;
    }

    public abstract Format[] getSupportedInputFormats();

    public ContentDescriptor[] getSupportedOutputContentDescriptors(
            Format[] inputs)
    {
        // TODO: should this match the # of entries in inputs?
        return new ContentDescriptor[] { contentDescriptor };
    }

    @Override
    public void open() throws ResourceUnavailableException
    {
        logger.finer(getClass().getSimpleName() + " open");
        super.open();
    }

    public int process(Buffer buffer, int trackID)
    {
        logger.finer(getClass().getSimpleName() + " process " + buffer + " "
                + trackID + " length " + buffer.getLength());

        try
        {
            doProcess(buffer, trackID, pipedOutputStream);
        } catch (IOException e1)
        {
            logger.log(Level.SEVERE, "" + e1, e1);
            return BUFFER_PROCESSED_FAILED;
        }

        if (dataOutput != null)
            dataOutput.notifyDataAvailable(0); // only 1 track

        return BUFFER_PROCESSED_OK;
    }

    @Override
    public int setNumTracks(int numTracks)
    {
        numTracks = super.setNumTracks(numTracks);

        try
        {
            pipedInputStream = new BigPipedInputStream(PIPE_SIZE);
            pipedOutputStream = new PipedOutputStream(pipedInputStream);

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return numTracks;
    }

    protected void writeInt(OutputStream os, long value) throws IOException
    {
        byte[] aBuffer = new byte[4];

        aBuffer[0] = (byte) ((value >> 24) & 0xff);
        aBuffer[1] = (byte) ((value >> 16) & 0xff);
        aBuffer[2] = (byte) ((value >> 8) & 0xff);
        aBuffer[3] = (byte) (value & 0xff);

        os.write(aBuffer, 0, aBuffer.length);
    }
}
