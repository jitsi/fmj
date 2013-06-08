package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/StartEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class StartEvent extends TransitionEvent
{
    private Time mediaTime;
    private Time timeBaseTime;

    public StartEvent(Controller from, int previous, int current, int target,
            Time mediaTime, Time tbTime)
    {
        super(from, previous, current, target);
        this.mediaTime = mediaTime;
        this.timeBaseTime = tbTime;
    }

    public Time getMediaTime()
    {
        return mediaTime;
    }

    public Time getTimeBaseTime()
    {
        return timeBaseTime;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[source=" + getSource()
                + ",previousState=" + getPreviousState() + ",currentState="
                + getCurrentState() + ",targetState=" + getTargetState()
                + ",mediaTime=" + mediaTime + ",timeBaseTime=" + timeBaseTime
                + "]";
    }
}
