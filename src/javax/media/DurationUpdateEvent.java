package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/DurationUpdateEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class DurationUpdateEvent extends ControllerEvent
{
    Time duration;

    public DurationUpdateEvent(Controller from, Time newDuration)
    {
        super(from);
        this.duration = newDuration;
    }

    public Time getDuration()
    {
        return duration;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[source=" + getSource() + ",duration="
                + duration + "]";

    }
}
