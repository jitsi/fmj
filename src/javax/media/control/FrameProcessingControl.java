package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/FrameProcessingControl.html"
 * target="_blank">this class in the JMF Javadoc</a>.
 *
 * Complete.
 *
 * @author Ken Larson
 *
 */
public interface FrameProcessingControl extends Control
{
    public int getFramesDropped();

    public void setFramesBehind(float numFrames);

    public boolean setMinimalProcessing(boolean newMinimalProcessing);
}
