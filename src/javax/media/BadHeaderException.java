package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/BadHeaderException.html"
 * target="_blank">this class in the JMF Javadoc</a>.
 *
 * Complete.
 *
 * @author Ken Larson
 *
 */
public class BadHeaderException extends MediaException
{
    public BadHeaderException()
    {
        super();
    }

    public BadHeaderException(String message)
    {
        super(message);
    }
}
