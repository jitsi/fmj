package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/GainChangeEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class GainChangeEvent extends MediaEvent
{
    GainControl eventSrc;

    boolean newMute;
    float newDB;
    float newLevel;

    public GainChangeEvent(GainControl from, boolean mute, float dB, float level)
    {
        super(from);
        this.eventSrc = from;
        this.newMute = mute;
        this.newDB = dB;
        this.newLevel = level;
    }

    public float getDB()
    {
        return newDB;
    }

    public float getLevel()
    {
        return newLevel;
    }

    public boolean getMute()
    {
        return newMute;
    }

    @Override
    public Object getSource()
    {
        return eventSrc;
    }

    public GainControl getSourceGainControl()
    {
        return eventSrc;
    }
}
