package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/TrackControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface TrackControl extends FormatControl, Controls
{
    public void setCodecChain(Codec[] codecs)
            throws UnsupportedPlugInException, NotConfiguredError;

    public void setRenderer(Renderer renderer)
            throws UnsupportedPlugInException, NotConfiguredError;
}
