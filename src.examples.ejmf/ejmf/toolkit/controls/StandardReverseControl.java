package ejmf.toolkit.controls;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventListener;

import javax.media.Controller;
import javax.media.Time;

import ejmf.toolkit.gui.controls.ReverseButton;

/**
* Reverse Control for StandardControlPanel.
* This Control is operational only if Controller supports
* negative rate.
*/
public class StandardReverseControl extends MouseListenerControl  {

    public StandardReverseControl() {
    }

    public StandardReverseControl(Controller controller) {
	this();
	setController(controller);
    }

    protected Component createControlComponent() {
	return new ReverseButton();
    }

    /////////// Conditional Control Interface ///////////////////

    /**
      * Set the state of the listener. If <tt>isOperational</tt>
      * is passed as <tt>true</tt>, then the listener semantics
      * are applied in response to mouse activity. Otherwise, the
      * listener semantics are not applied.
      *
      * This is used to disable default semantics if client control
      * panel simulates reversing media. In this case, <tt>setController</tt>
      * will call <tt>setOperational</tt> with a <tt>true</tt> value.
      */
    protected void setOperational(boolean isOperational) {
	super.setOperational(isOperational);
	getControlComponent().setEnabled(isOperational);
    }

        /** 
        * Determine operational state of Control based on ability
	* to support negative rate. If a negative rate is supported, 
	* I can use setRate to affect Controller reverse. Test here 
	* for negative rate and then reset old rate.
	*/
    protected void setControllerHook(Controller controller) {
	float saveRate = controller.getRate();
	float rate = controller.setRate(-1.0f);
	setOperational(rate < 0.0f);
	getControlComponent().setEnabled(isOperational());
	getController().setRate(saveRate);
    }

    protected EventListener createControlListener() {
        return new MouseAdapter() {
            int     priorState;
            float   saveRate;
	    public void mousePressed(MouseEvent mouseEvent) {
    	        if (isOperational()) {
		    Controller controller = getController();
        	    saveRate = controller.getRate();
        	    priorState = controller.getState();
		    if (priorState == Controller.Started) {
		 	controller.stop();
		    }
        	    controller.setRate(-1.0f * saveRate);
		    Time now = controller.getTimeBase().getTime();
            	    controller.syncStart(now);
    	        }
	    }
	    public void mouseReleased(MouseEvent event) {
    	        if (isOperational()) {
		    Controller controller = getController();
        	    controller.setRate(saveRate);
        	    if (priorState != Controller.Started) {
            	        controller.stop();
        	    }
    	        }
	    }
        };
    }
}
	
