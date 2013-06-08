package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/Track.html"
 * target="_blank">this class in the JMF Javadoc</a>.
 *
 * Complete.
 *
 * @author Ken Larson
 *
 */
public interface Track extends Duration
{
    public static int FRAME_UNKNOWN = Integer.MAX_VALUE;

    public static Time TIME_UNKNOWN = Time.TIME_UNKNOWN;

    public Format getFormat();

    public Time getStartTime();

    public boolean isEnabled();

    public Time mapFrameToTime(int frameNumber);

    public int mapTimeToFrame(Time t);

    /**
     * TODO: the API is not clear as to what readFrame should do in the case of
     * an error, like an IOException.
     */
    public void readFrame(Buffer buffer);

    public void setEnabled(boolean t);

    public void setTrackListener(TrackListener listener);
}
