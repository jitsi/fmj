package javax.media;

/**
 *
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/ResourceUnavailableException.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class ResourceUnavailableException extends MediaException
{
    public ResourceUnavailableException()
    {
        super();
    }

    public ResourceUnavailableException(String message)
    {
        super(message);
    }

}
