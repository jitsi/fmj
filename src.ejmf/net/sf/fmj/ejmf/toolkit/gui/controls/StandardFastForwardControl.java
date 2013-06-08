package net.sf.fmj.ejmf.toolkit.gui.controls;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.media.*;

/*
 * Fast forward Control for Standard EJMF Control Panel.
 */

public class StandardFastForwardControl extends MouseListenerControl
{
    private static final float DEFAULT_FF_RATE = 2.0f;

    private float fastForwardRate;

    /**
     * Create StandardFastForwardControl.
     */
    public StandardFastForwardControl(Skin skin)
    {
        super(skin);
        fastForwardRate = DEFAULT_FF_RATE;
        getControlComponent().setEnabled(true);
    }

    /**
     * Create StandardFastForwardControl.
     *
     * @param controller
     *            Associates Controller with Control.
     */
    public StandardFastForwardControl(Skin skin, Controller controller)
    {
        this(skin, controller, DEFAULT_FF_RATE);
    }

    /**
     * Create StandardFastForwardControl.
     *
     * @param controller
     *            Associates Controller with Control.
     * @param rate
     *            fast forward rate.
     */
    public StandardFastForwardControl(Skin skin, Controller controller,
            float rate)
    {
        super(skin, controller);
        fastForwardRate = (rate < 1.0f) ? DEFAULT_FF_RATE : rate;
        getControlComponent().setEnabled(true);
    }

    /**
     * Create FastForwardButton
     *
     * @return a component for display by control.
     * @see net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.FastForwardButton
     */
    @Override
    protected Component createControlComponent(Skin skin)
    {
        return skin.createFastForwardButton();
    }

    /**
     * Create MouseListener that implements Control semantics.
     *
     * @return listener that listens on control's component and implements fast
     *         forward semantics.
     */
    @Override
    protected EventListener createControlListener()
    {
        return new MouseAdapter()
        {
            private float saveRate;
            private int priorState;

            /*
             * Stop the controller, set the new rate and restart controller with
             * "fast" rate. Remember previous rate so it can be reset.
             */
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                Controller controller = getController();
                saveRate = controller.getRate();
                priorState = controller.getState();

                if (priorState == Controller.Started)
                {
                    controller.stop();
                }

                controller.setRate(fastForwardRate);

                // Always must start, since if controller was
                // started, it needed to be stopped to setRate.
                Time now = controller.getTimeBase().getTime();
                controller.syncStart(now);
            }

            /*
             * Reset previous rate and restart controller.
             */
            @Override
            public void mouseReleased(MouseEvent mouseEvent)
            {
                Controller controller = getController();
                controller.setRate(saveRate);
                if (priorState != Controller.Started)
                {
                    controller.stop();
                }
            }
        };
    }
}
