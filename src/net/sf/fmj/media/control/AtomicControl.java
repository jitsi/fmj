package net.sf.fmj.media.control;

import javax.media.*;

/**
 * Some stuff that could be common to all controls. ???
 */
public interface AtomicControl extends Control
{
    /**
     * Adds a listener that should be informed if any state of this control
     * changes.
     */
    public void addControlChangeListener(ControlChangeListener ccl);

    /**
     * Returns the enabled/disabled state of the control.
     */
    public boolean getEnabled();

    /**
     * Returns the control group to which this control belongs, if any.
     * Otherwise it returns null.
     */
    public Control getParent();

    /**
     * <B> Sun specific - </B> Returns the description string for this control.
     */
    public String getTip();

    /**
     * Returns true if this control is available on the control panel. ???
     */
    public boolean getVisible();

    /**
     * Returns true if this control is available on the default control panel
     * returned for the player in question.
     */
    public boolean isDefault();

    /**
     * Returns true if the control is a read-only control and no value can be
     * set on it. For example, progress controls that display status information
     * will mostly be read-only.
     */
    public boolean isReadOnly();

    /**
     * Remove an already added listener. Does nothing if the listener was not
     * previously added.
     */
    public void removeControlChangeListener(ControlChangeListener ccl);

    /**
     * Set the enabled/disabled state of the control. Can be useful to
     * temporarily gray out a control due to some constraints.
     */
    public void setEnabled(boolean enabled);

    /**
     * <B> Sun specific - </B> Sets the description string for this control.
     * Should be short since it will be displayed as a tool tip when the mouse
     * hovers over the control for a few seconds.
     */
    public void setTip(String tip);

    /**
     * Specify whether this control should be available on the control panel.
     * ???
     */
    public void setVisible(boolean visible);
}
