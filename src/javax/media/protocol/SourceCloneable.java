package javax.media.protocol;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/SourceCloneable.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface SourceCloneable
{
    /**
     * Based on JMF testing, the clone is in the same state as the original
     * (opened and connected if the original is), but at the beginning of the
     * media, not whatever position the original is.
     */
    public DataSource createClone();
}
