package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/MediaTimeSetEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class MediaTimeSetEvent extends ControllerEvent
{
    Time mediaTime;

    public MediaTimeSetEvent(Controller from, Time newMediaTime)
    {
        super(from);
        this.mediaTime = newMediaTime;
    }

    public Time getMediaTime()
    {
        return mediaTime;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[source=" + getSource() + ",mediaTime="
                + mediaTime + "]";

    }
}
