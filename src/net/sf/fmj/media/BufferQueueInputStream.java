/**
 *
 */
package net.sf.fmj.media;

import java.io.*;
import java.util.logging.*;

import javax.media.*;

import net.sf.fmj.utility.*;

import com.lti.utils.synchronization.*;

/**
 * Turns a queue of Buffer objects into an InputStream.
 *
 * @author Ken Larson
 *
 */
public class BufferQueueInputStream extends InputStream
{
    private static final Logger logger = LoggerSingleton.logger;

    // TODO: we have to be careful about buffering up too much, on a large file,
    // we can
    // run out of heap space.
    private final ProducerConsumerQueue q;

    private static final int DEFAULT_QUEUE_SIZE = 20;

    private boolean trace = false;

    private Buffer buffer;

    private int available = 0; // keep track of # of available bytes, in case
                               // available() is called. Must be synchronized on
                               // q to use.

    public BufferQueueInputStream()
    {
        this(DEFAULT_QUEUE_SIZE);
    }

    public BufferQueueInputStream(int qSize)
    {
        this.q = new ProducerConsumerQueue(qSize);
    }

    public BufferQueueInputStream(ProducerConsumerQueue q)
    {
        this.q = q;
    }

    @Override
    public int available()
    {
        synchronized (q)
        {
            if (trace)
                logger.fine(this + " available: available=" + available);

            return available;
        }
    }

    public void blockingPut(Buffer b)
    {
        blockingPut(b, true);
    }

    public void blockingPut(Buffer b, boolean clone)
    {
        put(b, true, clone);
    }

    private void fillBuffer() throws IOException
    {
        try
        {
            synchronized (q)
            {
                do
                {
                    if (buffer != null)
                    {
                        if (buffer.isEOM())
                            return;

                        if (buffer.getLength() > 0)
                            return; // still have data in buffer
                    }
                    // TODO: any fields to set?

                    buffer = (Buffer) q.get();
                    if (trace)
                        logger.fine(this + " Getting buffer: "
                                + buffer.getLength());

                    if (buffer.getLength() == 0 && !buffer.isDiscard())
                        if (trace)
                            logger.fine("Skipping zero-length buffer in queue");

                } while (buffer.isDiscard() || buffer.getLength() == 0);
            }

        } catch (InterruptedException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new InterruptedIOException();
        }
    }

    public boolean put(Buffer b)
    {
        return put(b, true);
    }

    public boolean put(Buffer b, boolean clone)
    {
        return put(b, false, clone);
    }

    /**
     * Note: buffers should be cloned before passing in. Returns false if no
     * room.
     */
    private boolean put(Buffer b, boolean block, boolean clone)
    {
        if (b.getLength() == -1)
            throw new IllegalArgumentException();
        if (!(b.getData() instanceof byte[]))
            throw new IllegalArgumentException("Expected byte array: "
                    + b.getData());
        if (b.isEOM())
        {
            logger.fine("putting EOM buffer");
        } else
        {
            if (b.getLength() == 0)
            {
                if (trace)
                    logger.fine("Skipping zero length buffer, not adding to queue");
                return true;
            }
            if (b.isDiscard())
            {
                if (trace)
                    logger.fine("Skipping discard buffer, not adding to queue");
                return true;
            }
        }

        if (trace)
            logger.fine(this
                    + " BufferQueueInputStream.put: Putting buffer, length="
                    + b.getLength() + " eom=" + b.isEOM());
        try
        {
            synchronized (q)
            {
                if (!block && q.isFull())
                    return false;
                available += b.getLength();
                q.put(clone ? (Buffer) b.clone() : b);
                if (trace)
                    logger.fine(this + " put: available=" + available);
                return true;
            }

        } catch (InterruptedException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new RuntimeException(e);
        }
    }

    // @Override
    @Override
    public int read() throws IOException
    {
        // TODO: how do we detect IOException?
        fillBuffer();
        if (buffer.getLength() <= 0 && buffer.isEOM()) // TODO: will always be
                                                       // EOM if length is 0
        {
            if (trace)
                logger.fine(this + " BufferQueueInputStream.read: returning -1");
            return -1;
        }
        final byte[] data = (byte[]) buffer.getData();
        final int result = data[buffer.getOffset()] & 0xff;
        buffer.setOffset(buffer.getOffset() + 1);
        buffer.setLength(buffer.getLength() - 1);
        synchronized (q)
        {
            available -= 1;
            if (trace)
                logger.fine(this + " read: available=" + available);

        }

        if (trace)
            logger.fine(this + " BufferQueueInputStream.read: returning "
                    + result);

        return result;

    }

    // @Override
    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        // TODO: how do we detect IOException?
        fillBuffer();
        if (buffer.getLength() <= 0 && buffer.isEOM()) // TODO: will always be
                                                       // EOM if length is 0
        {
            if (trace)
                logger.fine(this + " BufferQueueInputStream.read: returning -1");
            return -1;
            // TODO: how can buffer.getLength() == -1?
        }
        final byte[] data = (byte[]) buffer.getData();
        if (data == null)
            throw new NullPointerException("Buffer has null data.  length="
                    + buffer.getLength() + " offset=" + buffer.getOffset());

        int lengthToCopy = buffer.getLength() < len ? buffer.getLength() : len;
        if (trace)
            logger.fine(this + " BufferQueueInputStream.read: lengthToCopy="
                    + lengthToCopy + " buffer.getLength()="
                    + buffer.getLength() + " buffer.getOffset()="
                    + buffer.getOffset() + " b.length=" + b.length + " len="
                    + len + " off=" + off);
        System.arraycopy(data, buffer.getOffset(), b, off, lengthToCopy);
        buffer.setOffset(buffer.getOffset() + lengthToCopy);
        buffer.setLength(buffer.getLength() - lengthToCopy);

        synchronized (q)
        {
            available -= lengthToCopy;
            if (trace)
                logger.fine(this + " read: available=" + available);

        }

        if (trace)
            logger.fine(this + " BufferQueueInputStream.read[]: returning "
                    + lengthToCopy);

        return lengthToCopy;
    }

    public void setTrace(boolean value)
    {
        this.trace = value;
    }

}