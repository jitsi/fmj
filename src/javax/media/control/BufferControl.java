package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/BufferControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface BufferControl extends Control
{
    public static final long DEFAULT_VALUE = -1;
    public static final long MAX_VALUE = -2;

    public long getBufferLength();

    public boolean getEnabledThreshold();

    public long getMinimumThreshold();

    public long setBufferLength(long time);

    public void setEnabledThreshold(boolean b);

    public long setMinimumThreshold(long time);
}
