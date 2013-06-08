package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/FrameRateControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface FrameRateControl extends Control
{
    public float getFrameRate();

    public float getMaxSupportedFrameRate();

    public float getPreferredFrameRate();

    public float setFrameRate(float newFrameRate);

}
