package javax.media.protocol;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/SourceStream.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface SourceStream extends Controls
{
    public static final long LENGTH_UNKNOWN = -1L;

    public boolean endOfStream();

    public ContentDescriptor getContentDescriptor();

    public long getContentLength();
}
