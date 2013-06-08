package javax.media.protocol;

import java.io.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/PushSourceStream.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface PushSourceStream extends SourceStream
{
    public int getMinimumTransferSize();

    /**
     * According to API: Read from the stream without blocking. Returns -1 when
     * the end of the media is reached. This implies that it can return zero if
     * there is no data available.
     */
    public int read(byte[] buffer, int offset, int length) throws IOException;

    public void setTransferHandler(SourceTransferHandler transferHandler);
}
