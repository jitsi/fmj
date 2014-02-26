package net.sf.fmj.media.protocol;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.util.*;

public class CloneableSourceStreamAdapter
{
    class PullBufferStreamAdapter extends SourceStreamAdapter implements
            PullBufferStream
    {
        public javax.media.Format getFormat()
        {
            return ((PullBufferStream) master).getFormat();
        }

        public void read(Buffer buffer) throws IOException
        {
            copyAndRead(buffer);
        }

        public boolean willReadBlock()
        {
            return ((PullBufferStream) master).willReadBlock();
        }
    }

    class PullSourceStreamAdapter extends SourceStreamAdapter implements
            PullSourceStream
    {
        public int read(byte[] buffer, int offset, int length)
                throws IOException
        {
            return copyAndRead(buffer, offset, length);
        }

        public boolean willReadBlock()
        {
            return ((PullSourceStream) master).willReadBlock();
        }
    }

    class PushBufferStreamAdapter extends SourceStreamAdapter implements
            PushBufferStream, BufferTransferHandler
    {
        BufferTransferHandler handler;

        public javax.media.Format getFormat()
        {
            return ((PushBufferStream) master).getFormat();
        }

        public void read(Buffer buffer) throws IOException
        {
            copyAndRead(buffer);
        }

        public void setTransferHandler(BufferTransferHandler transferHandler)
        {
            handler = transferHandler;
            ((PushBufferStream) master).setTransferHandler(this);
        }

        public void transferData(PushBufferStream stream)
        {
            if (handler != null)
                handler.transferData(this);
        }
    }

    class PushBufferStreamSlave extends PushStreamSlave implements
            PushBufferStream, Runnable
    {
        BufferTransferHandler handler;
        private Buffer b;

        public javax.media.Format getFormat()
        {
            if (master instanceof PullBufferStream)
                return ((PullBufferStream) master).getFormat();
            if (master instanceof PushBufferStream)
                return ((PushBufferStream) master).getFormat();
            return null;
        }

        BufferTransferHandler getTransferHandler()
        {
            return handler;
        }

        public synchronized void read(Buffer buffer) throws IOException
        {
            // block till we have a buffer to read from
            while (b == null && connected)
            {
                try
                {
                    wait(50);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace(System.err);
                }
            }

            if (!connected)
                throw new IOException("DataSource is not connected");

            buffer.copy(b);
            b = null;
        }

        /**
         * Implementation of Runnable interface.
         */
        public void run()
        {
            while (!endOfStream() && connected)
            {
                try
                {
                    synchronized (this)
                    {
                        wait(); // till we will be notified that a read occurred
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace(System.err);
                }
                if (connected && handler != null)
                    handler.transferData(this);
            }
        }

        /**
         * Set the buffer this stream can provide for the next read
         */
        synchronized void setBuffer(Buffer b)
        {
            this.b = b;
            notifyAll();
        }

        public void setTransferHandler(BufferTransferHandler transferHandler)
        {
            handler = transferHandler;
        }
    }

    class PushSourceStreamAdapter extends SourceStreamAdapter implements
            PushSourceStream, SourceTransferHandler
    {
        SourceTransferHandler handler;

        public int getMinimumTransferSize()
        {
            return ((PushSourceStream) master).getMinimumTransferSize();
        }

        public int read(byte[] buffer, int offset, int length)
                throws IOException
        {
            return copyAndRead(buffer, offset, length);
        }

        public void setTransferHandler(SourceTransferHandler transferHandler)
        {
            handler = transferHandler;
            ((PushSourceStream) master).setTransferHandler(this);
        }

        public void transferData(PushSourceStream stream)
        {
            if (handler != null)
                handler.transferData(this);
        }
    }

    class PushSourceStreamSlave extends PushStreamSlave implements
            PushSourceStream, Runnable
    {
        SourceTransferHandler handler;
        private byte[] buffer;

        public int getMinimumTransferSize()
        {
            return
                (master instanceof PushSourceStream)
                    ? ((PushSourceStream) master).getMinimumTransferSize()
                    : 0;
        }

        SourceTransferHandler getTransferHandler()
        {
            return handler;
        }

        public synchronized int read(byte[] buffer, int offset, int length)
                throws IOException
        {
            if (length + offset > buffer.length)
                throw new IOException("buffer is too small");

            // block till we have a buffer to read from
            while (this.buffer == null && connected)
            {
                try
                {
                    wait(50);
                }
                catch (InterruptedException e)
                {
                    System.out.println("Exception: " + e);
                }
            }

            if (!connected)
                throw new IOException("DataSource is not connected");

            int copyLength = (length > this.buffer.length ? this.buffer.length
                    : length);
            System.arraycopy(this.buffer, 0, buffer, offset, copyLength);
            this.buffer = null;

            return copyLength;
        }

        /**
         * Implementation of Runnable interface.
         */
        public void run()
        {
            while (!endOfStream() && connected)
            {
                try
                {
                    synchronized (this)
                    {
                        wait(); // till we will be notified that a read occurred
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace(System.err);
                }

                if (connected && handler != null)
                    handler.transferData(this);
            }
        }

        /**
         * Set the buffer this stream can provide for the next read
         */
        synchronized void setBuffer(byte[] buffer)
        {
            this.buffer = buffer;
            notifyAll();
        }

        public void setTransferHandler(SourceTransferHandler transferHandler)
        {
            handler = transferHandler;
        }
    }

    abstract class PushStreamSlave extends SourceStreamAdapter implements
            SourceStreamSlave, Runnable
    {
        MediaThread notifyingThread;
        boolean connected = false;

        public synchronized void connect()
        {
            if (connected)
                return;

            connected = true;

            notifyingThread = new MediaThread(this);
            if (notifyingThread != null)
            {
                if (this instanceof PushBufferStream)
                {
                    if (((PushBufferStream) this).getFormat()
                            instanceof VideoFormat)
                        notifyingThread.useVideoPriority();
                    else
                        notifyingThread.useAudioPriority();
                }
                notifyingThread.start(); // You don't need permission for start
            }

        }

        public synchronized void disconnect()
        {
            connected = false;
            notifyAll();
        }
    }

    class SourceStreamAdapter implements SourceStream
    {
        public boolean endOfStream()
        {
            return master.endOfStream();
        }

        public ContentDescriptor getContentDescriptor()
        {
            return master.getContentDescriptor();
        }

        public long getContentLength()
        {
            return master.getContentLength();
        }

        public Object getControl(String controlType)
        {
            return master.getControl(controlType);
        }

        public Object[] getControls()
        {
            return master.getControls();
        }
    }

    SourceStream master;

    SourceStream adapter = null;

    // //////////////////////////
    //
    // INNER CLASSES
    // //////////////////////////

    Vector<SourceStream> slaves = new Vector<SourceStream>();

    protected int numTracks = 0;

    protected Format[] trackFormats;

    /**
     * Constructor
     */
    CloneableSourceStreamAdapter(SourceStream master)
    {
        this.master = master;

        // create the matching adapter according to the stream's type
        if (master instanceof PullSourceStream)
            adapter = new PullSourceStreamAdapter();
        if (master instanceof PullBufferStream)
            adapter = new PullBufferStreamAdapter();
        if (master instanceof PushSourceStream)
            adapter = new PushSourceStreamAdapter();
        if (master instanceof PushBufferStream)
            adapter = new PushBufferStreamAdapter();
    }

    void copyAndRead(Buffer b) throws IOException
    {
        if (master instanceof PullBufferStream)
            ((PullBufferStream) master).read(b);
        else if (master instanceof PushBufferStream)
            ((PushBufferStream) master).read(b);

        for (Enumeration<SourceStream> e = slaves.elements();
                e.hasMoreElements();)
        {
            SourceStream stream = e.nextElement();
            ((PushBufferStreamSlave) stream).setBuffer((Buffer) b.clone());
            Thread.yield();
        }
    }

    int copyAndRead(byte[] buffer, int offset, int length) throws IOException
    {
        int totalRead = 0;

        if (master instanceof PullSourceStream)
            totalRead
                = ((PullSourceStream) master).read(buffer, offset, length);
        else if (master instanceof PushSourceStream)
            totalRead
                = ((PushSourceStream) master).read(buffer, offset, length);

        for (Enumeration<SourceStream> e = slaves.elements();
                e.hasMoreElements();)
        {
            SourceStream stream = e.nextElement();
            byte[] copyBuffer = new byte[totalRead];
            System.arraycopy(buffer, offset, copyBuffer, 0, totalRead);
            ((PushSourceStreamSlave) stream).setBuffer(copyBuffer);
        }

        return totalRead;
    }

    /**
     * This method should be could only by the <tt>CloneableDataSource</tt>.
     *
     * @return a slave <tt>SourceStream</tt> which will either a
     *         <tt>PushSourceStream</tt> or a <tt>PushBufferStream.
     */
    SourceStream createSlave()
    {
        SourceStream slave = null;

        if ((master instanceof PullSourceStream)
                || (master instanceof PushSourceStream))
            slave = new PushSourceStreamSlave();
        else if ((master instanceof PullBufferStream)
                || (master instanceof PushBufferStream))
            slave = new PushBufferStreamSlave();
        slaves.addElement(slave);

        return slave;
    }

    /**
     * Return the stream adapter to be used by the Handler. There is only one
     * adapter per stream since there is only one master stream.
     */
    SourceStream getAdapter()
    {
        return adapter;
    }
}
