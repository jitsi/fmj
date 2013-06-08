package net.sf.fmj.media.control;

import java.awt.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;

import net.sf.fmj.media.*;

/**
 * The <tt>FramePositioningControl</tt> is the interface to control precise
 * positioning to a video frame for <tt>Players</tt> and <tt>Processors</tt>.
 */
public class FramePositioningAdapter implements FramePositioningControl,
        Reparentable
{
    static public Track getMasterTrack(Track tracks[])
    {
        Track master = null;
        Format f;
        float rate = Format.NOT_SPECIFIED;

        for (int i = 0; i < tracks.length; i++)
        {
            if (tracks[i] == null || ((f = tracks[i].getFormat()) == null))
                continue;

            if (!(f instanceof VideoFormat))
                continue;

            master = tracks[i];

            if ((rate = ((VideoFormat) f).getFrameRate()) != Format.NOT_SPECIFIED
                    && rate != 0f)
            {
                return master;
            }
        }

        if (master != null
                && master.mapTimeToFrame(new Time(0)) != FramePositioningControl.FRAME_UNKNOWN)
            return master;
        else
            return null;
    }

    Object owner;
    Player player;
    Track master = null;
    long frameStep = -1;

    public FramePositioningAdapter(Player p, Track track)
    {
        this.player = p;
        this.master = track;

        // Base on the frame rate, compute the inter-frame duration.
        // This is not very accurate since the frame rate reported is not
        // very accurate anyway.
        Format f = track.getFormat();
        if (f instanceof VideoFormat)
        {
            float rate = ((VideoFormat) f).getFrameRate();
            if (rate != Format.NOT_SPECIFIED && rate != 0f)
                frameStep = (long) (Time.ONE_SECOND / rate);
        }
    }

    public Component getControlComponent()
    {
        return null;
    }

    public Object getOwner()
    {
        if (owner == null)
            return this;
        else
            return owner;
    }

    /**
     * Converts the given frame number to the corresponding media time.
     * <p>
     *
     * @param frameNumber
     *            the input frame number for the conversion.
     * @return the converted media time for the given frame. If the conversion
     *         fails, TIME_UNKNOWN is returned.
     */
    public Time mapFrameToTime(int frameNumber)
    {
        return master.mapFrameToTime(frameNumber);
    }

    /**
     * Converts the given media time to the corresponding frame number.
     * <p>
     * The frame returned is the nearest frame that has a media time less than
     * or equal to the given media time.
     * <p>
     *
     * @param mediaTime
     *            the input media time for the conversion.
     * @return the converted frame number the given media time. If the
     *         conversion fails, FRAME_UNKNOWN is returned.
     */
    public int mapTimeToFrame(Time mediaTime)
    {
        return master.mapTimeToFrame(mediaTime);
    }

    /**
     * Seek to a given video frame.
     *
     * @param frameNumber
     *            the frame to seek to.
     * @return the actual frame that the Player has seeked to.
     */
    public int seek(int frameNumber)
    {
        Time seekTo = master.mapFrameToTime(frameNumber);
        if (seekTo != null && seekTo != FramePositioningControl.TIME_UNKNOWN)
        {
            player.setMediaTime(seekTo);
            return master.mapTimeToFrame(seekTo);
        } else
        {
            // Can't do any thing if mapFrameToTime fails.
            return FramePositioningControl.FRAME_UNKNOWN;
        }
    }

    public void setOwner(Object newOwner)
    {
        owner = newOwner;
    }

    /**
     * Skip a given number of frames from the current position.
     *
     * @param framesToSkip
     *            the number of frames to skip from the current position. If
     *            framesToSkip is positive, it will seek forward by framesToSkip
     *            number of frames. If framesToSkip is negative, it will seek
     *            backward by framesToSkip number of frames. e.g. skip(-1) will
     *            step backward one frame.
     * @return the actual number of frames skipped.
     */
    public int skip(int framesToSkip)
    {
        if (frameStep != -1)
        {
            // Use interframe duration.
            long t = player.getMediaNanoseconds() + (framesToSkip * frameStep);
            player.setMediaTime(new Time(t));
            return framesToSkip;

        } else
        {
            int currentFrame = master.mapTimeToFrame(player.getMediaTime());
            if (currentFrame != 0
                    && currentFrame != FramePositioningControl.FRAME_UNKNOWN)
            {
                int newFrame = seek(currentFrame + framesToSkip);
                return newFrame - currentFrame;
            } else
            {
                // Ran out of options.
                return FramePositioningControl.FRAME_UNKNOWN;
            }
        }
    }
}
