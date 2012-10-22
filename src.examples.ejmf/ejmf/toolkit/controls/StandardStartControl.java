package ejmf.toolkit.controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.media.Controller;
import javax.media.TimeBase;

import ejmf.toolkit.gui.controls.StartButton;
import ejmf.toolkit.util.StateWaiter;

/*
* Start Control for StandardControlPanel.
*/

public class StandardStartControl extends ActionListenerControl {

    public StandardStartControl(Controller controller) {
	super(controller);
	getControlComponent().setEnabled(true);
    }
 
    public StandardStartControl() {
	super();
	getControlComponent().setEnabled(true);
    }

	/**	
	* Create StartButton.	
	* @see ejmf.toolkit.gui.control.StartButton
	*/

    protected Component createControlComponent() {
	return new StartButton();
    }

	/**
	* Creates an ActionListener for start button
	* that starts Controller when clicked.	
	* <p>	
	* Since syncStart is used to start Controller is not in at 
	* least Prefetched state, it is move there.
	*/
    protected EventListener createControlListener() {
	return new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
		    Controller controller = getController();
		    int state = controller.getState();

		    if (state == Controller.Started) 
			return;

		    if (state < Controller.Prefetched) {
			StateWaiter w = new StateWaiter(controller);
			w.blockingPrefetch();
		    }

		    TimeBase tb = controller.getTimeBase();
            	    controller.syncStart(tb.getTime());
        	}
	    };
    }
}
