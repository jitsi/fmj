package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/QualityControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface QualityControl extends Control
{
    public float getPreferredQuality();

    public float getQuality();

    public boolean isTemporalSpatialTradeoffSupported();

    public float setQuality(float newQuality);
}
