package net.sf.fmj.ejmf.toolkit.gui.controls;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.media.*;

/**
 * Reverse Control for StandardControlPanel. This Control is operational only if
 * Controller supports negative rate.
 */
public class StandardReverseControl extends MouseListenerControl
{
    public StandardReverseControl(Skin skin)
    {
        super(skin);
    }

    public StandardReverseControl(Skin skin, Controller controller)
    {
        this(skin);
        setController(controller);
    }

    @Override
    protected Component createControlComponent(Skin skin)
    {
        return skin.createReverseButton();
    }

    // ///////// Conditional Control Interface ///////////////////

    @Override
    protected EventListener createControlListener()
    {
        return new MouseAdapter()
        {
            int priorState;
            float saveRate;

            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (isOperational())
                {
                    Controller controller = getController();
                    saveRate = controller.getRate();
                    priorState = controller.getState();
                    if (priorState == Controller.Started)
                    {
                        controller.stop();
                    }
                    controller.setRate(-1.0f * saveRate);
                    Time now = controller.getTimeBase().getTime();
                    controller.syncStart(now);
                }
            }

            @Override
            public void mouseReleased(MouseEvent event)
            {
                if (isOperational())
                {
                    Controller controller = getController();
                    controller.setRate(saveRate);
                    if (priorState != Controller.Started)
                    {
                        controller.stop();
                    }
                }
            }
        };
    }

    /**
     * Determine operational state of Control based on ability to support
     * negative rate. If a negative rate is supported, I can use setRate to
     * affect Controller reverse. Test here for negative rate and then reset old
     * rate.
     */
    @Override
    protected void setControllerHook(Controller controller)
    {
        if (true)
        {
            setOperational(false); // disable because setRate causes problems
            return;
        }
        float saveRate = controller.getRate();
        float rate = controller.setRate(-1.0f);
        setOperational(rate < 0.0f);
        getControlComponent().setEnabled(isOperational());
        getController().setRate(saveRate);
    }

    /**
     * Set the state of the listener. If <tt>isOperational</tt> is passed as
     * <tt>true</tt>, then the listener semantics are applied in response to
     * mouse activity. Otherwise, the listener semantics are not applied.
     *
     * This is used to disable default semantics if client control panel
     * simulates reversing media. In this case, <tt>setController</tt> will call
     * <tt>setOperational</tt> with a <tt>true</tt> value.
     */
    @Override
    protected void setOperational(boolean isOperational)
    {
        super.setOperational(isOperational);
        getControlComponent().setEnabled(isOperational);
    }
}
