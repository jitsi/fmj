package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/BitRateControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface BitRateControl extends Control
{
    public int getBitRate();

    public int getMaxSupportedBitRate();

    public int getMinSupportedBitRate();

    public int setBitRate(int bitrate);
}
