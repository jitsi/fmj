package javax.media.rtp;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/InvalidSessionAddressException.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class InvalidSessionAddressException extends SessionManagerException
{
    public InvalidSessionAddressException()
    {
        super();
    }

    public InvalidSessionAddressException(String reason)
    {
        super(reason);
    }
}
