package net.sf.fmj.ejmf.toolkit.gui.controls;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.media.*;

import net.sf.fmj.ejmf.toolkit.util.*;

/*
 * Start Control for StandardControlPanel.
 */

public class StandardStartControl extends ActionListenerControl
{
    public StandardStartControl(Skin skin)
    {
        super(skin);
        getControlComponent().setEnabled(true);
    }

    public StandardStartControl(Skin skin, Controller controller)
    {
        super(skin, controller);
        getControlComponent().setEnabled(true);
    }

    /**
     * Create StartButton.
     *
     * @see net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.StartButton
     */
    @Override
    protected Component createControlComponent(Skin skin)
    {
        return skin.createStartButton();
    }

    /**
     * Creates an ActionListener for start button that starts Controller when
     * clicked.
     * <p>
     * Since syncStart is used to start Controller is not in at least Prefetched
     * state, it is move there.
     */
    @Override
    protected EventListener createControlListener()
    {
        return new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Controller controller = getController();
                int state = controller.getState();

                if (state == Controller.Started)
                    return;

                if (state < Controller.Prefetched)
                {
                    StateWaiter w = new StateWaiter(controller);
                    w.blockingPrefetch();
                }

                TimeBase tb = controller.getTimeBase();
                controller.syncStart(tb.getTime());
            }
        };
    }
}
