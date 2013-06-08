package net.sf.fmj.media.multiplexer;

import java.io.*;
import java.util.logging.*;

import javax.media.protocol.*;

import net.sf.fmj.utility.*;

/**
 * Adapater from {@link InputStream} to {@link PushSourceStream}.
 *
 * @author Ken Larson
 *
 */
public class InputStreamPushSourceStream implements PushSourceStream
{
    private static final Logger logger = LoggerSingleton.logger;

    private final ContentDescriptor outputContentDescriptor;
    private final InputStream is;

    private boolean eos;

    private SourceTransferHandler transferHandler;

    public InputStreamPushSourceStream(
            ContentDescriptor outputContentDescriptor, final InputStream is)
    {
        super();
        this.outputContentDescriptor = outputContentDescriptor;
        this.is = is;
    }

    public boolean endOfStream()
    {
        logger.finer(getClass().getSimpleName() + " endOfStream");
        return eos;
    }

    public ContentDescriptor getContentDescriptor()
    {
        logger.finer(getClass().getSimpleName() + " getContentDescriptor");
        return outputContentDescriptor;
    }

    public long getContentLength()
    {
        logger.finer(getClass().getSimpleName() + " getContentLength");
        return 0; // TODO
    }

    public Object getControl(String controlType)
    {
        logger.finer(getClass().getSimpleName() + " getControl");
        return null;
    }

    public Object[] getControls()
    {
        logger.finer(getClass().getSimpleName() + " getControls");
        return new Object[0];
    }

    public int getMinimumTransferSize()
    {
        logger.finer(getClass().getSimpleName() + " getMinimumTransferSize");
        return 0;
    }

    /**
     * Not a JMF API method, but allows us to get the transfer handler to do a
     * similar hack to JMF: how to go back and update a header for a file you've
     * already written.
     */
    public SourceTransferHandler getTransferHandler()
    {
        return transferHandler;
    }

    public void notifyDataAvailable()
    {
        if (transferHandler != null) // TODO; synchronization issues on
                                     // transferHandler
            transferHandler.transferData(this);
    }

    public int read(byte[] buffer, int offset, int length) throws IOException
    {
        // logger.finer(getClass().getSimpleName() + " read");
        int result = is.read(buffer, offset, length);
        if (result < 0)
            eos = true;
        return result;
    }

    public void setTransferHandler(SourceTransferHandler transferHandler)
    {
        logger.finer(getClass().getSimpleName() + " setTransferHandler");
        this.transferHandler = transferHandler;
    }
}