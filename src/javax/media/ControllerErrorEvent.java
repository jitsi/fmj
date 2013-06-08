package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/ControllerErrorEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class ControllerErrorEvent extends ControllerClosedEvent
{
    public ControllerErrorEvent(Controller from)
    {
        super(from);
    }

    public ControllerErrorEvent(Controller from, String why)
    {
        super(from, why);
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[source=" + getSource() + ",message="
                + message + "]";

    }

}
