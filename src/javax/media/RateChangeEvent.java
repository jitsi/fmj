package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/RateChangeEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class RateChangeEvent extends ControllerEvent
{
    float rate;

    public RateChangeEvent(Controller from, float newRate)
    {
        super(from);
        this.rate = newRate;
    }

    public float getRate()
    {
        return rate;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "[source=" + getSource() + ",rate="
                + rate + "]";
    }
}
