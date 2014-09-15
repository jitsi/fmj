package net.sf.fmj.media;

import javax.media.*;

/**
 * Abstract base class to implement Track.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractTrack implements Track
{
    private boolean enabled = true; // default to enabled. JMF won't play the
                                    // track if it is not enabled. TODO: FMJ
                                    // should do the same.

    public Time getDuration()
    {
        return Duration.DURATION_UNKNOWN;
    }

    public abstract Format getFormat();

    public Time getStartTime()
    {
        return TIME_UNKNOWN;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public Time mapFrameToTime(int frameNumber)
    {
        return TIME_UNKNOWN;
    }

    public int mapTimeToFrame(Time t)
    {
        return FRAME_UNKNOWN;
    }

    public abstract void readFrame(Buffer buffer);

    public void setEnabled(boolean t)
    {
        this.enabled = t;
    }

    public void setTrackListener(TrackListener listener)
    {
        // TODO Auto-generated method stub
    }

}
