package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/FramePositioningControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface FramePositioningControl extends Control
{
    public static final Time TIME_UNKNOWN = Time.TIME_UNKNOWN;
    public static final int FRAME_UNKNOWN = Integer.MAX_VALUE;

    public Time mapFrameToTime(int frameNumber);

    public int mapTimeToFrame(Time mediaTime);

    public int seek(int frameNumber);

    public int skip(int framesToSkip);
}
