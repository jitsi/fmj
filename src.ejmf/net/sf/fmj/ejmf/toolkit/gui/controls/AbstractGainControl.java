package net.sf.fmj.ejmf.toolkit.gui.controls;

import javax.media.*;

/**
 * Abstract class from which AbstractListenerControls that depend on a
 * GainControl should extend. Upon construction it properly set the operational
 * state of the Control.
 *
 */

public abstract class AbstractGainControl extends ActionListenerControl
{
    private GainControl gc;

    /**
     * Create an AbstractGainControl. Controller will be assigned later.
     */
    protected AbstractGainControl(Skin skin)
    {
        super(skin);
    }

    /**
     * Create an AbstractGainControl and associate with controller.
     *
     * @param controller
     *            Controller with which this control is associated.
     */
    protected AbstractGainControl(Skin skin, Controller controller)
    {
        super(skin, controller);
    }

    /**
     * @return GainControl associated with this AbstractGainControl.
     */
    protected GainControl getGainControl()
    {
        return gc;
    }

    /**
     * Invoked when Controller is associated with Control. Properly sets
     * operational state and initializes initializes private reference to
     * GainControl if Controller is a Player and it has a one.
     *
     * @param controller
     *            Controller with which this control is associated.
     */
    @Override
    protected void setControllerHook(Controller controller)
    {
        if (controller instanceof Player)
        {
            gc = ((Player) controller).getGainControl();
            setOperational(gc != null);
        } else
        {
            setOperational(false);
        }
    }
}
