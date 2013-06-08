package javax.media.protocol;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/PushDataSource.html"
 * target="_blank">this class in the JMF Javadoc</a>.
 *
 * @author Ken Larson
 *
 */
public abstract class PushDataSource extends DataSource
{
    public PushDataSource()
    {
        super(); // TODO: anything to do?
    }

    public abstract PushSourceStream[] getStreams();
}
