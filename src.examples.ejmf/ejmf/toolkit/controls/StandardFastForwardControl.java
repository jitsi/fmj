package ejmf.toolkit.controls;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventListener;

import javax.media.Controller;
import javax.media.Time;

import ejmf.toolkit.gui.controls.FastForwardButton;

/*
* Fast forward Control for Standard EJMF Control Panel.
*/


public class StandardFastForwardControl extends MouseListenerControl {

    private static final float DEFAULT_FF_RATE	= 2.0f;

    private float	fastForwardRate;

	/**
	* Create StandardFastForwardControl.
	* @param controller Associates Controller with Control.
	* @param rate fast forward rate.
 	*/	
    public StandardFastForwardControl(Controller controller, float rate) {
	super(controller);
	fastForwardRate = (rate < 1.0f) ? 
			DEFAULT_FF_RATE : rate;
	getControlComponent().setEnabled(true);
    }

	/**
	* Create StandardFastForwardControl.
	* @param controller Associates Controller with Control.
	*/
    public StandardFastForwardControl(Controller controller) {
	this(controller, DEFAULT_FF_RATE);
    }

	/**
	* Create StandardFastForwardControl.
	*/
    public StandardFastForwardControl() {
	super();
	fastForwardRate = DEFAULT_FF_RATE;
	getControlComponent().setEnabled(true);
    }

	/**
	* Create FastForwardButton
	* @return a component for display by control.
	* @see ejmf.toolkit.gui.controls.FastForwardButon
	*/
    protected Component createControlComponent() {
	return new FastForwardButton();
    }

	/**
	* Create MouseListener that implements
	* Control semantics.	
	* @return listener that listens on control's component
	* and implements fast forward semantics.
	*/
    protected EventListener createControlListener() {
	return new MouseAdapter() {
            private float   saveRate;
            private int     priorState;

		/* Stop the controller, set the new rate
		* and restart controller with "fast" rate.
		* Remember previous rate so it can be reset.
		*/
            public void mousePressed(MouseEvent mouseEvent) {
		Controller controller = getController();
                saveRate = controller.getRate();
                priorState = controller.getState();

		if (priorState == Controller.Started) {
		    controller.stop();	
	        }

                controller.setRate(fastForwardRate);

		// Always must start, since if controller was
                // started, it needed to be stopped to setRate.
	 	Time now = controller.getTimeBase().getTime();
                controller.syncStart(now);
            }

		/* Reset previous rate and restart controller.
		*/
            public void mouseReleased(MouseEvent mouseEvent) {
		Controller controller = getController();
                controller.setRate(saveRate);
                if (priorState != Controller.Started) {
                    controller.stop();
                }
            }
        };
    }
}
	
