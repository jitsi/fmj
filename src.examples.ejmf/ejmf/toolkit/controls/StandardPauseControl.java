package ejmf.toolkit.controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.media.Controller;

import ejmf.toolkit.gui.controls.PauseButton;
/**
* Pause Control for StandardControlPanel.
*/

public class StandardPauseControl extends ActionListenerControl {

	/** Create a StandardPauseControl and associate it
	* with a Controller.
	* @param controller A Controller with which control is associated.
	*/
    public StandardPauseControl(Controller controller) {
	super();
	getControlComponent().setEnabled(false);
    }

	/** Create a StandardPauseControl */
    public StandardPauseControl() {
	getControlComponent().setEnabled(false);
    }

	/**
	* Create PauseButton.
	* @return The component that acts as pause button.
	* @see ejmf.toolkit.gui.controls.PauseButton
	*/
    protected Component createControlComponent() {
	return new PauseButton();
    }

	/** Create and return an ActionListener that
	* implements pause semantics.
	* @return An ActionListener for pausing controller.
	*/
    protected EventListener createControlListener() {
	return new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
	    	    getController().stop();
		}
            };
    }
}
	
