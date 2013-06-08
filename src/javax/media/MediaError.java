package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/MediaError.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class MediaError extends Error
{
    public MediaError()
    {
        super();
    }

    public MediaError(String message)
    {
        super(message);
    }

}
