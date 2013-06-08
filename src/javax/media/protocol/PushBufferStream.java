package javax.media.protocol;

import java.io.*;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/PushBufferStream.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface PushBufferStream extends SourceStream
{
    public Format getFormat();

    public void read(Buffer buffer) throws IOException;

    public void setTransferHandler(BufferTransferHandler transferHandler);
}
