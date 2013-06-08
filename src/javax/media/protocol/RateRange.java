package javax.media.protocol;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/RateRange.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class RateRange implements java.io.Serializable
{
    private float value;
    private float min;
    private float max;
    private boolean exact;

    public RateRange(float init, float min, float max, boolean isExact)
    {
        this.value = init;
        this.min = min;
        this.max = max;
        this.exact = isExact;
    }

    public RateRange(RateRange r)
    {
        this(r.value, r.min, r.max, r.exact);
    }

    public float getCurrentRate()
    {
        return value;
    }

    public float getMaximumRate()
    {
        return max;
    }

    public float getMinimumRate()
    {
        return min;
    }

    public boolean inRange(float rate)
    {
        if (true)
            throw new UnsupportedOperationException(); // TODO
        return rate >= min && rate <= max; // TODO: boundaries?
    }

    public boolean isExact()
    {
        return exact;
    }

    public float setCurrentRate(float rate)
    {
        // do not enforce min/max
        this.value = rate;

        return this.value;
    }
}
