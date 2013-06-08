package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/StopEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class StopEvent extends TransitionEvent
{
    private Time mediaTime;

    public StopEvent(Controller from, int previous, int current, int target,
            Time mediaTime)
    {
        super(from, previous, current, target);
        this.mediaTime = mediaTime;
    }

    public Time getMediaTime()
    {
        return mediaTime;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[source=" + getSource()
                + ",previousState=" + getPreviousState() + ",currentState="
                + getCurrentState() + ",targetState=" + getTargetState()
                + ",mediaTime=" + mediaTime + "]";
    }
}
