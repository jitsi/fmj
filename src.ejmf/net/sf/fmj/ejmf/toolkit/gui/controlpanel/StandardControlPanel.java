package net.sf.fmj.ejmf.toolkit.gui.controlpanel;

import java.awt.event.*;

import javax.media.*;
import javax.swing.*;
import javax.swing.event.*;

import net.sf.fmj.ejmf.toolkit.gui.controls.*;
import net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.*;
import net.sf.fmj.ejmf.toolkit.gui.controls.skins.two.*;

/**
 * StandardControlPanel extends JPanel and arranges the standard controls in a
 * left-to-right fashion using FlowLayout and add(Component, index)
 *
 * Setters are defined that allow the user to change the visual component
 * associated with any control. A version of these setters allows for the
 * definition of a new listener.
 *
 * Two constructors are defined. The first builds all the default controls into
 * the control panel. The second takes a flag which defines the controls to be
 * built and displayed by the control panel. The flag values are defined by
 * public static values provided by the class. These values can be OR'd together
 * to set a flag value.
 *
 * @see net.sf.fmj.ejmf.toolkit.gui.controlpanel.AbstractControlPanel
 */

public class StandardControlPanel extends AbstractControlPanel
{
    /**
     * Flags for which controls will appear in panel. It is OR of values below.
     */
    private int buttonFlags;

    private static final Skin DEFAULT_SKIN = new SkinTwo();

    /**
     * Build a StandardControlPanel with all of the default controls.
     *
     * @param p
     *            A player with which control panel is associated.
     * @see javax.media.Player
     */
    public StandardControlPanel(Player p)
    {
        this(DEFAULT_SKIN, p, 0xffffffff);

    }

    /**
     * Build a StandardControlPanel designating which controls are desired.
     *
     * @param p
     *            A player with which control panel is associated.
     * @param buttonFlags
     *            Control values OR'd together which determine the controls that
     *            are displayed.
     * @see javax.media.Player
     */
    public StandardControlPanel(Player p, int buttonFlags)
    {
        this(DEFAULT_SKIN, p, buttonFlags);
    }

    public StandardControlPanel(Skin skin, Player p)
    {
        this(skin, p, 0xffffffff);
    }

    public StandardControlPanel(Skin skin, Player p, int buttonFlags)
    {
        super(skin, p, buttonFlags);
    }

    /**
     * Does the work of building the control components and adding them to the
     * layout. The default behavior is simply to use the components provided by
     * BasicControlPanel.
     *
     * This method may be over-ridden for a wholesale customization of the
     * control panel.
     *
     */
    @Override
    public void addComponents(int flags)
    {
        AbstractListenerControl c;
        String name;

        c = getControl(StandardControls.START_CONTROL);
        if ((c != null) && ((flags & USE_START_CONTROL) != 0))
            add(c.getControlComponent());

        c = getControl(StandardControls.PAUSE_CONTROL);
        if ((c != null) && ((flags & USE_PAUSE_CONTROL) != 0))
            add(c.getControlComponent());

        c = getControl(StandardControls.FF_CONTROL);
        if ((c != null) && ((flags & USE_FF_CONTROL) != 0))
            add(c.getControlComponent());

        c = getControl(StandardControls.PROGRESS_CONTROL);
        if ((c != null) && ((flags & USE_PROGRESS_CONTROL) != 0))
            add(c.getControlComponent());

        c = getControl(StandardControls.REVERSE_CONTROL);
        if ((c != null) && ((flags & USE_REVERSE_CONTROL) != 0)
                && c.isOperational())
            add(c.getControlComponent());

        c = getControl(StandardControls.STOP_CONTROL);
        if ((c != null) && (flags & USE_STOP_CONTROL) != 0)
            add(c.getControlComponent());

        c = getControl(StandardControls.GAIN_CONTROL);
        if ((c != null) && ((flags & USE_GAIN_CONTROL) != 0)
                && c.isOperational())
            add(c.getControlComponent());

        c = getControl(StandardControls.GAINMETER_CONTROL);
        if ((c != null) && ((flags & USE_GAINMETER_CONTROL) != 0)
                && c.isOperational())
            add(c.getControlComponent());
    }

    /**
     * Creates Controls associated with this Control Panel.
     *
     * @param player
     *            Associated Player
     */
    @Override
    protected AbstractControls createControls(Player player)
    {
        return new StandardControls(skin, player);
    }

    // //////// Convenience Methods for Setting Control Components ///////

    /**
     * Get the fast forward button control
     *
     * @return An AbstractButton
     */
    public AbstractButton getFastForwardButton()
    {
        return (AbstractButton) getControl(StandardControls.FF_CONTROL)
                .getControlComponent();
    }

    /**
     * Get the gain button panel
     *
     * @return An AbstractGainButtonPanel
     */
    public AbstractGainButtonPanel getGainButtonPanel()
    {
        return (AbstractGainButtonPanel) getControl(
                StandardControls.GAIN_CONTROL).getControlComponent();
    }

    /**
     * Get the gain meter button control
     *
     * @return An AbstractButton
     */
    public AbstractButton getGainMeterButton()
    {
        return (AbstractButton) getControl(StandardControls.GAINMETER_CONTROL)
                .getControlComponent();
    }

    /**
     * Get the pause button control
     *
     * @return An AbstractButton
     */
    public AbstractButton getPauseControl()
    {
        return (AbstractButton) getControl(StandardControls.PAUSE_CONTROL)
                .getControlComponent();
    }

    /**
     * Get the progress control
     *
     * @return A ProgressSlider
     */
    public ProgressSlider getProgressComponent()
    {
        StandardProgressControl spc = (StandardProgressControl) getControl(StandardControls.PROGRESS_CONTROL);
        return (ProgressSlider) spc.getControlComponent();
    }

    /**
     * Get the reverse button control
     *
     * @return An AbstractButton
     */
    public AbstractButton getReverseButton()
    {
        return (AbstractButton) getControl(StandardControls.REVERSE_CONTROL)
                .getControlComponent();
    }

    /**
     * Get the start button control
     *
     * @return An AbstractButton
     */
    public AbstractButton getStartButton()
    {
        return (AbstractButton) getControl(StandardControls.START_CONTROL)
                .getControlComponent();
    }

    /**
     * Get the stop button control
     *
     * @return An AbstractButton
     */
    public AbstractButton getStopButton()
    {
        return (AbstractButton) getControl(StandardControls.STOP_CONTROL)
                .getControlComponent();
    }

    /**
     * Set the control button for fast forwarding the player associated with
     * this control panel.
     *
     * @param c
     *            An AbstractButton
     */
    public void setFastForwardButton(AbstractButton c)
    {
        MouseListenerControl control = (MouseListenerControl) getControl(StandardControls.FF_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the control button for fast forwarding the player associated with
     * this control panel. Supply a listener to implement control semantics.
     *
     * @param c
     *            An AbstractButton
     * @param l
     *            A MouseListener that implements button semantics.
     */
    public void setFastForwardButton(AbstractButton c, MouseListener l)
    {
        MouseListenerControl control = (MouseListenerControl) getControl(StandardControls.FF_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponentAndListener(c, l);
    }

    /**
     * Set the control component for gain Control
     *
     * @param c
     *            an AbstractGainButtonPanel
     * @see net.sf.fmj.ejmf.toolkit.gui.controls.AbstractGainButtonPanel
     */
    public void setGainButtonPanel(AbstractGainButtonPanel c)
    {
        StandardGainControl control = (StandardGainControl) getControl(StandardControls.GAIN_CONTROL);

        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the control component and listener semantics for gain Control
     *
     * @param c
     *            an AbstractGainButtonPanel
     * @param l
     *            An ActionListener that implements Control semantics.
     */
    public void setGainButtonPanel(AbstractGainButtonPanel c, ActionListener l)
    {
        StandardGainControl control = (StandardGainControl) getControl(StandardControls.GAIN_CONTROL);

        replaceControlComponent(control.getControlComponent(), c);
        control.setComponentAndListener(c, l);
    }

    /**
     * Set the control component for gain meter Control
     *
     * @param c
     *            An AbstractButton
     */
    public void setGainMeterButton(AbstractButton c)
    {
        ActionListenerControl control = (ActionListenerControl) getControl(StandardControls.GAINMETER_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the control component and listener semantics for gain meter Control
     *
     * @param c
     *            An AbstractButton
     * @param l
     *            An ActionListener that implements Control semantics.
     */
    public void setGainMeterButton(AbstractButton c, ActionListener l)
    {
        ActionListenerControl control = (ActionListenerControl) getControl(StandardControls.GAINMETER_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponentAndListener(c, l);
    }

    /**
     * Set the control button for pausing the player associated with this
     * control panel.
     *
     * @param c
     *            An AbstractButton
     */
    public void setPauseButton(AbstractButton c)
    {
        ActionListenerControl control = (ActionListenerControl) getControl(StandardControls.PAUSE_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the pause button with use-supplied semantics.
     *
     * @param c
     *            An AbstractButton to affect pause.
     * @param l
     *            A ActionListener that implements button semantics.
     */
    public void setPauseButton(AbstractButton c, ActionListener l)
    {
        ActionListenerControl control = (ActionListenerControl) getControl(StandardControls.PAUSE_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the control component for progress bar associated with this control
     * panel.
     *
     * @param c
     *            A ProgressBar
     * @see net.sf.fmj.ejmf.toolkit.gui.controls.ProgressBar
     */
    public void setProgressSlider(ProgressSlider c)
    {
        ChangeListenerControl control = (ChangeListenerControl) getControl(StandardControls.PROGRESS_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the control component for displaying the player progress slider
     * associated with this control panel. Supply a ChangeListener to implement
     * control semantics.
     *
     * @param c
     *            A ProgressBar
     * @see net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.ProgressSlider
     */
    public void setProgressSlider(ProgressSlider c, ChangeListener l)
    {
        ChangeListenerControl control = (ChangeListenerControl) getControl(StandardControls.PROGRESS_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponentAndListener(c, l);
    }

    /**
     * Set the control button for reversing the player associated with this
     * control panel.
     *
     * @param c
     *            An AbstractButton
     */
    public void setReverseButton(AbstractButton c)
    {
        MouseListenerControl control = (MouseListenerControl) getControl(StandardControls.REVERSE_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the control button for reversing the player associated with this
     * control panel. Supply a listener to implement control semantics.
     *
     * @param c
     *            An AbstractButton
     * @param l
     *            A MouseListener that implements button semantics.
     */
    public void setReverseButton(AbstractButton c, MouseListener l)
    {
        MouseListenerControl control = (MouseListenerControl) getControl(StandardControls.REVERSE_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the control button for starting the player associated with this
     * control panel.
     *
     * @param c
     *            An AbstractButton
     */
    public void setStartButton(AbstractButton c)
    {
        ActionListenerControl control;
        control = (ActionListenerControl) getControl(StandardControls.START_CONTROL);

        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the control button for starting the player associated with this
     * control panel. Supply a listener to implement control semantics.
     *
     * @param c
     *            An AbstractButton
     * @param l
     *            A MouseListener that implements button semantics.
     */

    public void setStartButton(AbstractButton c, ActionListener l)
    {
        ActionListenerControl control;
        control = (ActionListenerControl) getControl(StandardControls.START_CONTROL);

        replaceControlComponent(control.getControlComponent(), c);
        control.setComponentAndListener(c, l);
    }

    /**
     * Set the control button for stopping the player associated with this
     * control panel.
     *
     * @param c
     *            An AbstractButton
     */
    public void setStopButton(AbstractButton c)
    {
        ActionListenerControl control = (ActionListenerControl) getControl(StandardControls.STOP_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponent(c);
    }

    /**
     * Set the control button for stopping the player associated with this
     * control panel. Supply a listener to implement control semantics.
     *
     * @param c
     *            An AbstractButton
     * @param l
     *            An ActionListener that implements button semantics.
     */
    public void setStopButton(AbstractButton c, ActionListener l)
    {
        ActionListenerControl control = (ActionListenerControl) getControl(StandardControls.STOP_CONTROL);
        replaceControlComponent(control.getControlComponent(), c);
        control.setComponentAndListener(c, l);
    }
}
