package net.sf.fmj.media;

import java.io.*;

import javax.media.protocol.*;

/**
 * Implements an InputStream by wrapping a PullSourceStream.
 *
 * @author Ken Larson
 *
 */
public class PullSourceStreamInputStream extends InputStream
{
    private final PullSourceStream pss;
    private final Seekable seekable; // == pss if pss instanceof Seekable. Used
                                     // for mark/reset

    private long markPosition = -1L;

    public PullSourceStreamInputStream(PullSourceStream pss)
    {
        super();
        this.pss = pss;
        if (pss instanceof Seekable)
            seekable = (Seekable) pss;
        else
            seekable = null;
    }

    // @Override
    @Override
    public synchronized void mark(int readlimit)
    {
        if (!markSupported())
            super.mark(readlimit);

        markPosition = seekable.tell();
    }

    // @Override
    @Override
    public boolean markSupported()
    {
        return seekable != null;
    }

    // @Override
    @Override
    public int read() throws IOException
    {
        final byte[] buffer = new byte[1];
        final int nRead = pss.read(buffer, 0, 1);
        if (nRead <= 0)
            return -1;
        return buffer[0] & 0xff;
    }

    // @Override
    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        final int result = pss.read(b, off, len);
        return result;
    }

    // @Override
    @Override
    public synchronized void reset() throws IOException
    {
        if (!markSupported())
            super.reset();

        if (markPosition < 0)
            throw new IOException("mark must be called before reset");

        seekable.seek(markPosition);

    }

    // @Override
    @Override
    public long skip(long n) throws IOException
    {
        if (seekable == null)
        {
            return super.skip(n);
        } else
        {
            if (n <= 0)
                return 0;

            final long beforeSeek = seekable.tell();
            final long afterSeek = seekable.seek(beforeSeek + n);
            return afterSeek - beforeSeek;
        }
    }
}
