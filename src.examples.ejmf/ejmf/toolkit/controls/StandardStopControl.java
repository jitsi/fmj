package ejmf.toolkit.controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.media.Controller;
import javax.media.Time;

import ejmf.toolkit.gui.controls.StopButton;

/*
* Stop Control for StandardControlPanel.
*/
public class StandardStopControl extends ActionListenerControl {

    public StandardStopControl() {
	getControlComponent().setEnabled(false);
    }

    public StandardStopControl(Controller controller) {
	super(controller);
	getControlComponent().setEnabled(false);
    }

	/** 
	* Create StopButton	
	* @see ejmf.toolkit.gui.controls.StopButton
	*/
    protected Component createControlComponent() {
	return new StopButton();
    }

	/**
	* Create ActionListener to respond to StopButton
	* clicks.
	*/
    protected EventListener createControlListener() {
	return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
		Controller controller = getController();
                controller.stop();
	        controller.setMediaTime(new Time(0.0));
            }
        };
    }
}
	
