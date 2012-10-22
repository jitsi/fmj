package ejmf.toolkit.gui.controlpanel;

import javax.media.Controller;
import javax.media.Player;

import ejmf.toolkit.controls.AbstractListenerControl;
import ejmf.toolkit.controls.EjmfFastForwardControl;
import ejmf.toolkit.controls.EjmfGainControl;
import ejmf.toolkit.controls.EjmfGainMeterControl;
import ejmf.toolkit.controls.EjmfPauseControl;
import ejmf.toolkit.controls.EjmfProgressControl;
import ejmf.toolkit.controls.EjmfReverseControl;
import ejmf.toolkit.controls.EjmfStartControl;
import ejmf.toolkit.controls.EjmfStopControl;
import ejmf.toolkit.controls.StandardProgressControl;
import ejmf.toolkit.gui.controls.EjmfStartButton;

/**
 * EjmfControls provides the Controls for EjmfControlPanel.
 * The control components are built and default listeners are 
 * added to them. No layout is done, ie.
 * components are not even added to the Panel. 
 * <p>
 * EjmfControls creates the following control components:
 * <ul>
 * <li>  Start Control
 * <li>  Stop  Control
 * <li>  Reverse Control
 * <li>  Fast Forward Control
 * <li>  Volume Control
 * <li>  Progress Bar Control
 * </ul>
 * <p>
 * Only if the Player's media duration can be ascertained, is a slider
 * created.
 * <p>
 * This class provides a collection of <tt>
 * <p>
 *    create<xxxx>Control</tt>
 * <p> 
 * methods that supply Controls. Any of these can be over-ridden	
 * to supply a different Control.
 * 
 * @see  ejmf.toolkit.gui.controlpanel.StandardControlPanel
 * @see  javax.media.ControllerListener
 *
*/

public class EjmfControls extends StandardControls {

	/** Create Controls for EJMF Control Panel
	* @param player Associated Player
	*/
    public EjmfControls(Player player) {
	super(player);
    }

    /*
     * Set the display state of the control components.
     *
     * We over-ride StandardControls because we don't want
     * to disable the start button.
	* @param state Current state of controls.
     */
    public void setControlComponentState(int state) {
	EjmfStartButton b = (EjmfStartButton)getStartButton();
	if (state == Controller.Started) {
	    b.displayAsPause();
	} else {
	    b.displayAsStart();
        }
    }

	/**	
	* Create EjmfFastForwardControl
	  * Subclasses should over-ride this to customize the
	  * the fast forward control.
	* @return fast forward control as AbstractListenerControl
	*/
    protected AbstractListenerControl createFastForwardControl() {
	return new EjmfFastForwardControl();
    }

	/** Create a reverse control.
	  * Subclasses should over-ride this to customize the
	  * the reverse control.
	* @return reverse control as AbstractListenerControl
	  */

    protected AbstractListenerControl createReverseControl() {
	return new EjmfReverseControl();
    }
 
	/** Create a start control.
	  * Subclasses should over-ride this to customize the
	  * the start control.
	* @return start control as AbstractListenerControl
	  */
    protected AbstractListenerControl createStartControl() {
	return new EjmfStartControl();
    }

	/** Create a stop control.
	  * Subclasses should over-ride this to customize the
	  * the stop control.
	* @return stop control as AbstractListenerControl
	  */

    protected AbstractListenerControl createStopControl() {
	return new EjmfStopControl();
    }
	/** Create a pause control.
	  * Subclasses should over-ride this to customize the
	  * the pause control.
	* @return pause control as AbstractListenerControl
	  */

    protected AbstractListenerControl createPauseControl() {
	return new EjmfPauseControl();	
    }

	/** Create a gain increase/decrease control.
	  * Subclasses should over-ride this to customize the
	  * the gain increase/decrease control.
	* @return gain control as AbstractListenerControl
	  */
    protected AbstractListenerControl createGainControl() {
	return new EjmfGainControl();
    }

	/** Create a gain meter control.
	  * Subclasses should over-ride this to customize the
	  * the gain meter control.
	* @return gain meter control as AbstractListenerControl
	  */
    protected AbstractListenerControl createGainMeterControl() {
	return new EjmfGainMeterControl();
    }

	/** Create a progress control.
	  * Subclasses should over-ride this to customize the
	  * the progress control.
	* @return progress control as StandardProgressControl
	  */
    protected StandardProgressControl createProgressControl() {
	return new EjmfProgressControl();
    }
}
