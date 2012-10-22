package net.sf.fmj.media.control;

import javax.media.*;

/**
 * A GroupControl is a parent to a set of smaller controls. This is a base class
 * interface for group controls such as VolumeControl, ColorControl,
 * PlaybackControl, etc.
 */
public interface GroupControl extends AtomicControl
{
    /**
     * Returns any controls that might constitute this control.
     */
    public Control[] getControls();
}
