package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/ConnectionErrorEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class ConnectionErrorEvent extends ControllerErrorEvent
{
    public ConnectionErrorEvent(Controller from)
    {
        super(from);
    }

    public ConnectionErrorEvent(Controller from, String why)
    {
        super(from, why);
    }

}
