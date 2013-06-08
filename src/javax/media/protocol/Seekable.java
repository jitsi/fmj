package javax.media.protocol;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/Seekable.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface Seekable
{
    public boolean isRandomAccess();

    public long seek(long where);

    public long tell();
}
