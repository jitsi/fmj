package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/NotConfiguredError.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class NotConfiguredError extends MediaError
{
    public NotConfiguredError()
    {
        super();
    }

    public NotConfiguredError(String message)
    {
        super(message);
    }

}
