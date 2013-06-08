package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/DataLostErrorEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class DataLostErrorEvent extends ControllerClosedEvent
{
    public DataLostErrorEvent(Controller from)
    {
        super(from);
    }

    public DataLostErrorEvent(Controller from, String why)
    {
        super(from, why);
    }

}
