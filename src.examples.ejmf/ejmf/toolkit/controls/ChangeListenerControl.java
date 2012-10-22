
package ejmf.toolkit.controls;

import java.util.EventListener;

import javax.media.Controller;
import javax.swing.event.ChangeListener;

import ejmf.toolkit.gui.controls.ProgressSlider;

/**
  * A Control that requires ChangeListener will extend from 
  * ChangeListenerControl.
  */

public abstract class ChangeListenerControl extends AbstractListenerControl {

	/** Create a ChangeListenerControl and associate it with
	* a Controller.
	* @param controller A Controller with which listener is		
	* associated.
	*/
    protected ChangeListenerControl(Controller controller)  {
	super(controller);
    }

	/** Create a ChangeListenerControl
	*/
    protected ChangeListenerControl() {
 	super();	
    }

	/**
	* Add control semantics to this Control. Remove the
	* listener named by the <tt>listener</tt> argument.
	* <p>	
	* @param listener java.util.EventListener representing 
	* 		control semantics to be removed.
	*/
    protected void addControlListener(EventListener  listener)  {
        ((ProgressSlider) getControlComponent()).addChangeListener(
		(ChangeListener) listener);
    }

	/**	
	* Remove control semantics from this Control.
	* <p>	
	* @param listener java.util.EventListener representing 
	* 		control semantics to be added.
	*/
    protected void removeControlListener(EventListener  listener)  {
	((ProgressSlider) getControlComponent()).removeChangeListener(
		(ChangeListener) listener);
    }

	/**
	* Type-safe way to set Control Component and control listener.
	* @param slider A ProgressSlider that serves as Control	
	* component.
	* @param listener A ChangeListener that implements
	* Control semantics.
	*/	
    public void setComponentAndListener(ProgressSlider slider, ChangeListener listener) {
	super.setComponentAndListener(slider, listener);
    }

	/**
	* Type-safe way to set Control listener.
	* @param listener A ChangeListener that implements
	* Control semantics.
	*/	
    public void setControlListener(ChangeListener listener) {
 	super.setControlListener(listener);
    }

	/**
	* Type-safe way to set Control Component.
	*/
    public void setComponent(ProgressSlider slider) {
	super.setComponent(slider);
    }
}
