package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/KeyFrameControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface KeyFrameControl extends Control
{
    public int getKeyFrameInterval();

    public int getPreferredKeyFrameInterval();

    public int setKeyFrameInterval(int frames);
}
