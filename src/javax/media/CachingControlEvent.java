package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/CachingControlEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class CachingControlEvent extends ControllerEvent
{
    CachingControl cachingControl;
    long progress;

    public CachingControlEvent(Controller from, CachingControl cachingControl,
            long progress)
    {
        super(from);
        this.cachingControl = cachingControl;
        this.progress = progress;

    }

    public CachingControl getCachingControl()
    {
        return cachingControl;
    }

    public long getContentProgress()
    {
        return progress;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[source=" + getSource()
                + ",cachingControl=" + cachingControl + ",progress=" + progress
                + "]";
    }
}
