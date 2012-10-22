package ejmf.toolkit.controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.Controller;
import javax.media.Time;

import ejmf.toolkit.gui.controls.EjmfStartButton;
/**
* Start Control for EJMF Control Panel
*/

public class EjmfStartControl extends StandardStartControl {
    
	/**
	* Create an EjmfStartControl, creating a custom
	* ActionListener.  This listener handles the start/pause
        * combination used for the start control.
	* The button is never disabled, so we simply      
        * check the state of the controller to determine
        * what to do.
	*/
    public EjmfStartControl() {

	setControlListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		Controller controller = getController();
		if (controller.getState() == Controller.Started) {
		    controller.stop();
		} else {
		    Time now = controller.getTimeBase().getTime();
            	    controller.syncStart(now);
                }
	    }
	});
    }

	/**
	* Create EjmfGainStartButton
	*/
    protected Component createControlComponent() {
	return new EjmfStartButton();
    }
}
