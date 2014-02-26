package net.sf.fmj.media.codec;

import java.io.*;

import javax.media.*;

import com.lti.utils.synchronization.*;

/**
 * Class to wrap an InputStream and do reads in a background thread, so that
 * read never blocks. Used for badly behaving input streams where available() is
 * not working right or useless.
 *
 * @author Ken Larson
 *
 */
public class InputStreamReader extends InputStream
{
    private static class ReaderThread extends CloseableThread
    {
        private final ProducerConsumerQueue emptyQueue;
        private final ProducerConsumerQueue fullQueue;
        private final InputStream is;
        private final int bufferSize;

        public ReaderThread(final ProducerConsumerQueue emptyQueue,
                final ProducerConsumerQueue fullQueue, final InputStream is,
                final int bufferSize)
        {
            super();
            this.emptyQueue = emptyQueue;
            this.fullQueue = fullQueue;
            this.is = is;
            this.bufferSize = bufferSize;
        }

        @Override
        public void run()
        {
            try
            {
                while (!isClosing())
                {
                    Buffer b = (Buffer) emptyQueue.get();
                    b.setEOM(false);
                    b.setLength(0);
                    b.setOffset(0);

                    int len = is.read((byte[]) b.getData(), 0, bufferSize);
                    if (len < 0)
                        b.setEOM(true);
                    else
                        b.setLength(len);
                    fullQueue.put(b);

                }
            } catch (InterruptedException e)
            {
            } catch (IOException e)
            {
                try
                {
                    fullQueue.put(e);
                } catch (InterruptedException e1)
                {
                }
            } finally
            {
                setClosed();
            }
        }
    }

    private ReaderThread readerThread;
    private final ProducerConsumerQueue emptyQueue = new ProducerConsumerQueue();

    private final ProducerConsumerQueue fullQueue = new ProducerConsumerQueue();

    private boolean readerThreadStarted;

    private Buffer readBuffer;

    private IOException readException;

    public InputStreamReader(final InputStream is, final int bufferSize)
    {
        for (int i = 0; i < 2; ++i)
        {
            final Buffer b = new Buffer();
            b.setData(new byte[bufferSize]);
            try
            {
                emptyQueue.put(b);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e); // should never happen
            }
        }

        readerThread = new ReaderThread(emptyQueue, fullQueue, is, bufferSize);
        readerThread.setName("ReaderThread for " + is);
        readerThread.setDaemon(true);

    }

    @Override
    public int available() throws IOException
    {
        if (readException != null)
            throw readException;
        if (readBuffer != null && readBuffer.getLength() > 0)
            return readBuffer.getLength();
        else
            return 0;
    }

    @Override
    public void close() throws IOException
    {
        super.close();

        if (readerThread != null)
        {
            readerThread.close();
            readerThread = null;
        }
    }

    @Override
    public int read() throws IOException
    {
        byte[] ba = new byte[1];
        int result = read(ba, 0, 1);
        if (result == -1)
            return -1;
        return ba[0] & 0xff;

    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        try
        {
            if (readBuffer == null && readException == null)
            {
                Object o = fullQueue.get();
                if (o instanceof IOException)
                    readException = (IOException) o;
                else
                    readBuffer = (Buffer) o;
            }

            if (readException != null)
                throw readException;

            if (readBuffer.isEOM())
                return -1;
            final byte[] readBufferData = (byte[]) readBuffer.getData();

            final int lenToCopy = readBuffer.getLength() < len ? readBuffer
                    .getLength() : len;
            System.arraycopy(readBufferData, readBuffer.getOffset(), b, off,
                    lenToCopy);
            readBuffer.setOffset(readBuffer.getOffset() + lenToCopy);
            readBuffer.setLength(readBuffer.getLength() - lenToCopy);
            if (readBuffer.getLength() == 0)
            {
                emptyQueue.put(readBuffer);
                readBuffer = null;
            }
            return lenToCopy;
        } catch (InterruptedException e)
        {
            throw new InterruptedIOException();
        }
    }

    public void startReaderThread()
    {
        if (!readerThreadStarted)
        {
            readerThread.start();
            readerThreadStarted = true;
        }
    }

}