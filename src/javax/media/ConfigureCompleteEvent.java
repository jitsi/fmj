package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/ConfigureCompleteEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class ConfigureCompleteEvent extends TransitionEvent
{
    public ConfigureCompleteEvent(Controller processor, int previous,
            int current, int target)
    {
        super(processor, previous, current, target);
    }
}
