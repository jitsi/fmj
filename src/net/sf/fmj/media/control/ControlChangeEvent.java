package net.sf.fmj.media.control;

import javax.media.*;

/**
 * This event contains information about which Control has changed.
 */
public class ControlChangeEvent
{
    private Control c;

    /**
     * Creates a ControlChangeEvent with the specified control.
     */
    public ControlChangeEvent(Control c)
    {
        this.c = c;
    }

    /**
     * Returns the Control that generated this event.
     */
    public Control getControl()
    {
        return c;
    }
}
