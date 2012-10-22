package ejmf.toolkit.io;

import java.io.IOException;
import java.io.InputStream;

import javax.media.protocol.PullSourceStream;
import javax.media.protocol.PushSourceStream;

/**
 * The PushSourceInputStream class wraps a java.io.InputStream
 * around a javax.media.protocol.PushSourceStream, allowing
 * conventional I/O to access media data.
 *
 * @see        PullSourceStream
 *
 * @author     Steve Talley & Rob Gordon
 */
public class PushSourceInputStream extends InputStream {
    private PushSourceStream stream;

    /**
     * Constructs a PushSourceInputStream for the given
     * PushSourceStream.
     */
    public PushSourceInputStream(PushSourceStream stream) {
        this.stream = stream;
    }

    /////////////////////////////////////////////////////////////////
    //
    //  InputStream methods
    //
    /////////////////////////////////////////////////////////////////

    /**
     * Reads the next byte of data from this input stream.  The
     * value byte is returned as an int in the range 0 to 255.  If
     * no byte is available because the end of the stream has been
     * reached, the value -1 is returned.  This is directly from
     * the InputStream API.
     *
     * @return     The next byte of data, or -1 if the end of the
     *             stream is reached.
     *
     * @exception  IOException
     *             if an I/O error occurs.
     */
    public int read() throws IOException {
        byte[] b = new byte[1];
        if( stream.read(b,0,1) == -1 ) {
            return -1;
        } else {
            return (int)b[1];
        }
    }
        
    /**
     * A standard implementation of the InputStream.read() method.
     *
     * @param      b[]
     *             The byte array into which to read data.
     *
     * @param      off
     *             The offset in the array to begin writing data.
     *
     * @param      len
     *             The number of bytes to read.
     *
     * @return     The number of bytes read, or -1 if the end of
     *             the stream.
     *
     * @exception  IOException
     *             If an I/O error occurs while reading.
     */
    public int read(byte b[], int off, int len) throws IOException {
        return stream.read(b, off, len);
    }

    /////////////////////////////////////////////////////////////////
    //
    //  PushSourceStream methods
    //
    /////////////////////////////////////////////////////////////////

    /**
     * Get the PushSourceStream for this PushSourceInputStream.
     */
    public PushSourceStream getPushSourceStream() {
        return stream;
    }
}
