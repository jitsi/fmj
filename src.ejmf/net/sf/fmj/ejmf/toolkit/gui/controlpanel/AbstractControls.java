package net.sf.fmj.ejmf.toolkit.gui.controlpanel;

import java.util.*;

import javax.media.*;
import javax.swing.*;

import net.sf.fmj.ejmf.toolkit.gui.controls.*;

/**
 * The AbstractControls class:
 * <ul>
 * <li>maintains a Player reference
 * <li>maintains a Hashtable of Controls
 * <li>invokes setController on all of its Controls
 * <li>registers itself on a Player as a ControllerListener
 * </ul>
 * The controllerUpdate method implements default behavior for changing control
 * component state to reflect Player state.
 * <p>
 * Subclasses must supply definitions of the following methods:
 * <ul>
 *
 * <li>void makeControls();
 * <li>void setControlComponentState(int state);
 * </ul>
 */

abstract class AbstractControls implements ControllerListener
{
    class ControllerEventThread implements Runnable
    {
        private int state;

        public ControllerEventThread(int state)
        {
            this.state = state;
        }

        public void run()
        {
            setControlComponentState(state);
        }
    }

    private final Player player;

    private final Hashtable<String,AbstractListenerControl> controlTable
        = new Hashtable<String,AbstractListenerControl>();

    protected AbstractControls(Skin skin, Player player)
    {
        if (player.getState() < Controller.Realized)
            throw new NotRealizedError("Player must be realized");

        this.player = player;
        makeControls(skin);
        setControlsPlayer(player);
        player.addControllerListener(this);
        setControlComponentState(player.getState()); // KAL: added because there
                                                     // seems to be a race
                                                     // condition where the
                                                     // player can change states
                                                     // before our listener gets
                                                     // registered.
    }

    /**
     * Add a Control to this AbstractControls object.
     *
     * @param name
     *            Name of control
     * @param alc
     *            An AbstractListenerControl reference.
     */
    protected void addControl(String name, AbstractListenerControl alc)
    {
        controlTable.put(name, alc);
    }

    /**
     * This method fields ControllerEvents. Specifically, it looks for start,
     * stop and error events and calls setControlComponentState with either
     * Controller.Started or Controller.Prefetched to signal whether Controller
     * is started or not.
     * <p>
     * Subclasses that override this method should invoke
     * super.controllerUpdate() if default behavior is desired. to do anything
     * meaningful.
     *
     * @param event
     *            a ControllerEvent
     */
    public void controllerUpdate(ControllerEvent event)
    {
        if (event instanceof StartEvent)
        {
            SwingUtilities.invokeLater(new ControllerEventThread(
                    Controller.Started));

        } else if (event instanceof StopEvent
                || event instanceof ControllerErrorEvent)
        {
            SwingUtilities.invokeLater(new ControllerEventThread(
                    Controller.Prefetched));
        }
    }

    /**
     * Returns a Control with a given name.
     *
     * @param name
     *            String identifying an AbstractControlListener
     * @return an AbstractControlListener identified by the name argument.
     */
    public AbstractListenerControl getControl(String name)
    {
        return controlTable.get(name);
    }

    /**
     * Returns an array of Controls.
     *
     * @return an array of AbstractListenerControls associated with this Control
     *         Panel.
     */
    public AbstractListenerControl[] getControls()
    {
        Vector<AbstractListenerControl> v
            = new Vector<AbstractListenerControl>();
        Enumeration<AbstractListenerControl> elements = controlTable.elements();

        while (elements.hasMoreElements())
            v.addElement(elements.nextElement());

        AbstractListenerControl[] controls
            = new AbstractListenerControl[v.size()];

        v.copyInto(controls);
        return controls;
    }

    /**
     * Return the Player associated with this AbstractControls object.
     *
     * @return The Player associated with these controls.
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Build the controls managed by this AbstractControls object.
     */
    protected abstract void makeControls(Skin skin);

    /**
     * Set the display state of control components based on the state of the
     * Player.
     *
     * @param state
     *            The current state of the Player.
     */
    protected abstract void setControlComponentState(int state);

    /**
     * For each control, calls its setPlayer method to establish the controls
     * association with a Player.
     *
     * @param player
     *            Player associated with this set of control.
     */
    private void setControlsPlayer(Player player)
    {
        Controller c = player;
        Enumeration<AbstractListenerControl> e = controlTable.elements();

        while (e.hasMoreElements())
            e.nextElement().setController(c);
    }
}
