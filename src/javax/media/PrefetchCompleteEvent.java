package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/PrefetchCompleteEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class PrefetchCompleteEvent extends TransitionEvent
{
    public PrefetchCompleteEvent(Controller from, int previous, int current,
            int target)
    {
        super(from, previous, current, target);
    }
}
