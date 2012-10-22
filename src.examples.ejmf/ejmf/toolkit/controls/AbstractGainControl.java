package ejmf.toolkit.controls;

import javax.media.Controller;
import javax.media.GainControl;
import javax.media.Player;

/**
* Abstract class from which AbstractListenerControls that depend
* on a GainControl should extend. Upon construction it properly
* set the operational state of the Control.
* 
*/

public abstract class AbstractGainControl extends ActionListenerControl  {
    private GainControl	gc;

	/**
	* Create an AbstractGainControl and associate with 
	* controller.	
	* @param controller Controller with which this control is associated.
	*/
    protected AbstractGainControl(Controller controller) {
	super(controller);
    }
 
	/**
	* Create an AbstractGainControl. Controller will be assigned		
	* later.
	*/
    protected AbstractGainControl() {	
	super();
    }

	/**	
	* Invoked when Controller is associated with Control.
	* Properly sets operational state and initializes 
	* initializes private reference to GainControl if
	* Controller is a Player and it has a one.
	* @param controller Controller with which this control is associated.
	*/
    protected void setControllerHook(Controller controller) {
	if (controller instanceof Player) {
	    gc = ((Player)controller).getGainControl();
            setOperational(gc != null);
	} else {
	    setOperational(false);
        }
    }

	/**
	* @return GainControl associated with this AbstractGainControl.	
	*/
    protected GainControl getGainControl() {
	return gc;
    }
}
