package javax.media.protocol;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/PullDataSource.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public abstract class PullDataSource extends DataSource
{
    public abstract PullSourceStream[] getStreams();
}
