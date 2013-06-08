package net.sf.fmj.media;

import java.io.*;
import java.util.logging.*;

import javax.media.*;

import net.sf.fmj.utility.*;

/**
 * Implements InputStream by wrapping a Track.
 *
 * @author Ken Larson
 *
 */
public class TrackInputStream extends InputStream
{
    private static final Logger logger = LoggerSingleton.logger;

    private final Track track;
    private Buffer buffer;

    public TrackInputStream(Track track)
    {
        super();
        this.track = track;
    }

    private void fillBuffer()
    {
        if (buffer == null)
        {
            buffer = new Buffer();
            buffer.setFormat(track.getFormat());

        }

        do
        {
            if (buffer.isEOM())
                return;

            if (buffer.getLength() > 0)
                return; // still have data in buffer
            // TODO: any fields to set?

            track.readFrame(buffer);
            logger.fine("Read buffer from track: " + buffer.getLength());

        } while (buffer.isDiscard());
    }

    public Buffer getBuffer()
    {
        return buffer;
    }

    // @Override
    @Override
    public int read() throws IOException
    {
        // TODO: how do we detect IOException?
        fillBuffer();
        if (buffer.getLength() == 0 && buffer.isEOM()) // TODO: will always be
                                                       // EOM if length is 0
            return -1;
        final byte[] data = (byte[]) buffer.getData();
        final int result = data[buffer.getOffset()] & 0xff;
        buffer.setOffset(buffer.getOffset() + 1);
        buffer.setLength(buffer.getLength() - 1);

        return result;

    }

    // @Override
    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        // TODO: how do we detect IOException?
        fillBuffer();
        if (buffer.getLength() == 0 && buffer.isEOM()) // TODO: will always be
                                                       // EOM if length is 0
            return -1;
        final byte[] data = (byte[]) buffer.getData();

        int lengthToCopy = buffer.getLength() < len ? buffer.getLength() : len;
        System.arraycopy(data, buffer.getOffset(), b, off, lengthToCopy);
        buffer.setOffset(buffer.getOffset() + lengthToCopy);
        buffer.setLength(buffer.getLength() - lengthToCopy);

        return lengthToCopy;
    }

}
