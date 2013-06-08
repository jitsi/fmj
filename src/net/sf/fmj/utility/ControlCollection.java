package net.sf.fmj.utility;

import java.util.*;

import javax.media.*;

/**
 * Maintains a list of Control objects. The Controls may be retrieved by
 * classname or interface name.
 *
 * @author Warren Bloomer
 *
 */
public class ControlCollection
{
    private static final Control[] CONTROL_SPEC = new Control[] {};

    private Vector controls = new Vector();

    /**
     * Add a Control to the list.
     *
     * @param control
     */
    public void addControl(Control control)
    {
        synchronized (controls)
        {
            controls.add(control);
        }
    }

    public void clear()
    {
        synchronized (controls)
        {
            controls.clear();
        }
    }

    /**
     * Retrieve the first Control that implements the given Class or Interface.
     * The full class name must be used. If the control is not supported then
     * null is returned.
     *
     * @return the object that implements the control, or null.
     */
    public Control getControl(String controlType)
    {
        try
        {
            Class<?> cls = Class.forName(controlType);
            synchronized (controls)
            {
                Control cs[] = getControls();
                for (int i = 0; i < cs.length; i++)
                {
                    if (cls.isInstance(cs[i]))
                    {
                        return cs[i];
                    }
                }
            }
            return null;

        } catch (Exception e)
        {
            // no such controlType or such control
            return null;
        }
    }

    /**
     * Retrieve an array of Controls that control the object. If no controls are
     * supported, a zero length array is returned.
     *
     * @return the array of object controls
     */
    public Control[] getControls()
    {
        synchronized (controls)
        {
            return (Control[]) controls.toArray(CONTROL_SPEC);
        }
    }

    /**
     * Remove a Control from the list.
     *
     * @param control
     */
    public void removeControl(Control control)
    {
        synchronized (controls)
        {
            controls.remove(control);
        }
    }
}
