package net.sf.fmj.media.multiplexer;

import java.io.*;
import java.util.logging.*;

import javax.media.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * A way of converting an output stream to an input stream. TODO: Java has a way
 * of doing this already, using PipedInputStream and PipedOutputStream.
 *
 * @author Ken Larson
 *
 */
public class StreamPipe
{
    private class MyOutputStream extends OutputStream
    {
        @Override
        public void close() throws IOException
        {
            logger.finer("MyOutputStream Closing, putting EOM buffer");
            is.blockingPut(createEOMBuffer());

            super.close();
        }

        @Override
        public void write(byte[] b) throws IOException
        {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            is.blockingPut(createBuffer(b, off, len));
        }

        @Override
        public void write(int b) throws IOException
        {
            write(new byte[] { (byte) b });
        }

    }

    private static final Logger logger = LoggerSingleton.logger;
    private final BufferQueueInputStream is = new BufferQueueInputStream();

    private final MyOutputStream os = new MyOutputStream();

    private Buffer createBuffer(byte[] data, int offset, int length)
    {
        Buffer b = new Buffer();
        // TODO: set format to something? - doesn't seem needed.
        b.setData(data);
        b.setOffset(offset);
        b.setLength(length);
        return b;
    }

    private Buffer createEOMBuffer()
    {
        Buffer b = new Buffer();
        // TODO: set format to something? - doesn't seem needed.
        b.setData(new byte[0]);
        b.setOffset(0);
        b.setLength(0);
        b.setEOM(true);
        return b;
    }

    public InputStream getInputStream()
    {
        return is;
    }

    public OutputStream getOutputStream()
    {
        return os;
    }
}
