package net.sf.fmj.media;

import javax.media.*;

/**
 * A <tt>SeekFailedEvent</tt> indicates that the <tt>Controller</tt> could not
 * start at the current media time (set using setMediaTime).
 */

public class SeekFailedEvent extends StopEvent
{
    public SeekFailedEvent(Controller from, int previous, int current,
            int target, Time mediaTime)
    {
        super(from, previous, current, target, mediaTime);
    }
}
