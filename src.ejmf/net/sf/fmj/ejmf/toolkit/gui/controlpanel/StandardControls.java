package net.sf.fmj.ejmf.toolkit.gui.controlpanel;

import javax.media.*;
import javax.swing.*;

import net.sf.fmj.ejmf.toolkit.gui.controls.*;

/**
 * StandardControls provides a complete set of JMF player control components.
 * The control components are built and default listeners are added to them. No
 * layout is done, ie. components are not even added to the Panel. A 'standard'
 * left to right layout is provided on a selective basis by
 * StandardControlPanel.
 * <p>
 * StandardControls creates the following control components:
 * <ul>
 * <li>Start Control
 * <li>Stop Control
 * <li>Pause Control
 * <li>Reverse Control
 * <li>Fast Forward Control
 * <li>Volume Control
 * <li>Progress Bar Control
 * </ul>
 * <p>
 * If player's media duration can be ascertained, then a slider is created.
 * <p>
 * Tool tips are also associated with the control buttons.
 * <p>
 * Custom control components can be supplied by setters associated with each
 * component. For example, to use a custom stop button you would use
 * <tt>setStopControl</tt>. Setters come in two flavors: one that allows
 * replacing default listener and one that does not. If you want to replace the
 * default stop listener you may use either of the following:
 * <ul>
 * <tt>
 * <li>setStopControl(button, actionListener, flag)
 * <li>setStopListener(actionListener)
 * </tt>
 * </ul>
 * The latter installs a new listener on an existing button. It is important to
 * keep in mind that <tt>setStopListener</tt> must be called to <em>replace</em>
 * the existing listener. Using <tt>addActionListener</tt> will simply augment
 * any existing listeners.
 * <p>
 * StandardControls illustrates how the user can circumvent the default control
 * panel provided by JMF and have direct control over player operation. This is
 * particularly useful if a uniform control panel is desired across JMF
 * implementations.
 * <p>
 *
 *
 * @see net.sf.fmj.ejmf.toolkit.gui.controlpanel.StandardControlPanel
 * @see javax.media.ControllerListener
 *
 */

class StandardControls extends AbstractControls
{
    public static final String START_CONTROL = "StandardStartControl";
    public static final String PAUSE_CONTROL = "StandardPauseControl";
    public static final String FF_CONTROL = "StandardFastForwardControl";
    public static final String PROGRESS_CONTROL = "StandardProgressControl";
    public static final String REVERSE_CONTROL = "StandardReverseControl";
    public static final String STOP_CONTROL = "StandardStopControl";
    public static final String GAIN_CONTROL = "StandardGainControl";
    public static final String GAINMETER_CONTROL = "StandardGainMeterControl";

    private AbstractListenerControl startControl;
    private AbstractListenerControl stopControl;
    private AbstractListenerControl pauseControl;
    private AbstractListenerControl fastForwardControl;
    private AbstractListenerControl reverseControl;

    private AbstractListenerControl gainControl;
    private AbstractListenerControl gainMeterControl;
    private StandardProgressControl progressControl;

    // Rate for fast forward
    private float fastForwardRate = 2.0f;

    /**
     * Builds the components comprising a player control panel. No layout is
     * done. Which components are built depends on the attributes of the player.
     *
     * @param player
     *            Player associated with control panel
     * @see StandardControlPanel
     */
    public StandardControls(Skin skin, Player player)
    {
        super(skin, player);
    }

    // These methods create the standard controls. Subclasses
    // over-ride them to define their own controls.

    /**
     * This method implements the ControllerListener interface. Each event from
     * the Controller causes this method to be invoked.
     * <p>
     * All it does it rewind media in response to EndOfMedia and then call
     * super.controllerUpdate.
     *
     * @see javax.media.ControllerListener
     * @see javax.media.Controller
     */

    @Override
    public synchronized void controllerUpdate(ControllerEvent event)
    {
        if (event instanceof EndOfMediaEvent)
        {
            getPlayer().setMediaTime(new Time(0));
        }
        super.controllerUpdate(event);
    }

    /**
     * Create a fast forward control. Subclasses should over-ride this to
     * customize the the fast forward control.
     *
     * @return AbstractListenerControl for fast forward operation
     */

    protected final AbstractListenerControl createFastForwardControl(Skin skin)
    {
        return new StandardFastForwardControl(skin);
    }

    /**
     * Create a gain increase/decrease control. Subclasses should over-ride this
     * to customize the the gain increase/decrease control.
     *
     * @return AbstractListenerControl for gain control operation
     */
    protected final AbstractListenerControl createGainControl(Skin skin)
    {
        return new StandardGainControl(skin);
    }

    /**
     * Create a gain meter control. Subclasses should over-ride this to
     * customize the the gain meter control.
     *
     * @return AbstractListenerControl for gain meter operation
     */
    protected final AbstractListenerControl createGainMeterControl(Skin skin)
    {
        return new StandardGainMeterControl(skin);
    }

    /**
     * Create a pause control. Subclasses should over-ride this to customize the
     * the pause control.
     *
     * @return AbstractListenerControl for pause operation
     */

    protected final AbstractListenerControl createPauseControl(Skin skin)
    {
        return new StandardPauseControl(skin);
    }

    /**
     * Create a progress control. Subclasses should over-ride this to customize
     * the the progress control.
     *
     * @return StandardProgressControl for display and operation of progress
     *         slider.
     */
    protected final StandardProgressControl createProgressControl(Skin skin)
    {
        return new StandardProgressControl(skin);
    }

    /**
     * Create a reverse control. Subclasses should over-ride this to customize
     * the the reverse control.
     *
     * @return AbstractListenerControl for reverse operation
     */

    protected final AbstractListenerControl createReverseControl(Skin skin)
    {
        return new StandardReverseControl(skin);
    }

    /**
     * Create a start control. Subclasses should over-ride this to customize the
     * the start control.
     *
     * @return AbstractListenerControl for start operation
     */
    protected final AbstractListenerControl createStartControl(Skin skin)
    {
        return new StandardStartControl(skin);
    }

    /**
     * Create a stop control. Subclasses should over-ride this to customize the
     * the stop control.
     *
     * @return AbstractListenerControl for stop operation
     */

    protected final AbstractListenerControl createStopControl(Skin skin)
    {
        return new StandardStopControl(skin);
    }

    /**
     * Retrieve the AbstractListenerControl that performs as the player fast
     * forward control.
     *
     * @return fast forward control as AbstractListenerControl
     * @see java.swing.AbstractButton
     */
    public AbstractListenerControl getFastForwardControl()
    {
        return getControl(FF_CONTROL);
    }

    /**
     * Retrieve the AbstractListenerControl that performs as the player gain
     * control.
     *
     * @return gain control as AbstractListenerControl
     */
    public AbstractListenerControl getGainControl()
    {
        return getControl(GAIN_CONTROL);
    }

    /**
     * Retrieve the AbstractListenerControl that performs as the player gain
     * meter control.
     *
     * @return gain meter control as AbstractListenerControl
     */
    public AbstractListenerControl getGainMeterControl()
    {
        return getControl(GAINMETER_CONTROL);
    }

    /**
     * Return button associated with pause control.
     *
     * @return pause button as AbstractButton
     */
    public AbstractButton getPauseButton()
    {
        return (AbstractButton) getControl(PAUSE_CONTROL).getControlComponent();
    }

    /**
     * Retrieve the AbstractListenerControl that performs as the player pause
     * button.
     *
     * @return pause control as AbstractListenerControl
     */
    public AbstractListenerControl getPauseControl()
    {
        return getControl(PAUSE_CONTROL);
    }

    /**
     * Retrieve the ProgressBar component that performs as the player progress
     * bar.
     *
     * @return progress slider as StandardProgressControl
     */
    public StandardProgressControl getProgressControl()
    {
        return (StandardProgressControl) getControl(PROGRESS_CONTROL);
    }

    /**
     * Return button associated with reverse control.
     *
     * @return reverse button as AbstractButton
     */
    public AbstractButton getReverseButton()
    {
        return (AbstractButton) getControl(REVERSE_CONTROL)
                .getControlComponent();
    }

    /**
     * Retrieve the AbstractListenerControl that performs as the player reverse
     * control.
     *
     * @return reverse control as AbstractListenerControl
     * @see java.swing.AbstractButton
     */
    public AbstractListenerControl getReverseControl()
    {
        return getControl(REVERSE_CONTROL);
    }

    // ////////////// End default control creation /////////////////

    /**
     * Return button associated with start control.
     *
     * @return start button as AbstractButton
     */
    public AbstractButton getStartButton()
    {
        return (AbstractButton) getControl(START_CONTROL).getControlComponent();
    }

    // Miscellaneous Accessor Methods

    /**
     * Retrieve the AbstractButton that performs as the player start control.
     *
     * @return start control as AbstractListenerControl
     * @see java.swing.AbstractButton
     */
    public AbstractListenerControl getStartControl()
    {
        return getControl(START_CONTROL);
    }

    /**
     * Return button associated with stop control.
     *
     * @return stop button as AbstractButton
     */
    public AbstractButton getStopButton()
    {
        return (AbstractButton) getControl(STOP_CONTROL).getControlComponent();
    }

    /**
     * Retrieve the AbstractListenerControl that performs as the player stop
     * control.
     *
     * @return stop control as AbstractListenerControl
     * @see java.swing.AbstractButton
     */
    public AbstractListenerControl getStopControl()
    {
        return getControl(STOP_CONTROL);
    }

    /**
     * Build the control components.
     */

    @Override
    protected void makeControls(Skin skin)
    {
        AbstractListenerControl c;

        c = fastForwardControl = createFastForwardControl(skin);
        addControl(FF_CONTROL, fastForwardControl);

        c = reverseControl = createReverseControl(skin);
        addControl(REVERSE_CONTROL, reverseControl);

        c = startControl = createStartControl(skin);
        addControl(START_CONTROL, startControl);

        c = stopControl = createStopControl(skin);
        addControl(STOP_CONTROL, stopControl);

        c = pauseControl = createPauseControl(skin);
        addControl(PAUSE_CONTROL, pauseControl);

        c = gainControl = createGainControl(skin);
        addControl(GAIN_CONTROL, gainControl);

        c = gainMeterControl = createGainMeterControl(skin);
        addControl(GAINMETER_CONTROL, gainMeterControl);

        c = progressControl = createProgressControl(skin);
        addControl(PROGRESS_CONTROL, progressControl);
    }

    /**
     *
     * Sets the enable state of the various controls based on the state of the
     * Controller.
     *
     * This method should be over-ridden by subclasses whose 'off' behavior is
     * different than being disabled. The ejmf.toolkit.gui.EjmfControlPanel is
     * an example of this.
     *
     * If special behavior is needed, this should be over-ridden.
     *
     * @param state
     *            current state of control panel
     *
     * @see javax.media.Controller @ @see ejmf.toolkit.gui.EjmfControlPanel
     */

    @Override
    public void setControlComponentState(int state)
    {
        boolean on = (state == Controller.Started);
        getStartButton().setEnabled(!on);
        getStopButton().setEnabled(on);
        getPauseButton().setEnabled(on);
        getReverseButton().setEnabled(on);
        if (state != Controller.Started)
        {
            getProgressControl().setValue(
                    getPlayer().getMediaTime().getNanoseconds());
        }
    }
}
