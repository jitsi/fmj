package ejmf.toolkit.controls;

import java.awt.event.MouseListener;
import java.util.EventListener;

import javax.media.Controller;
import javax.swing.AbstractButton;

/**
  * All button which require a mouse listener extend MouseListenerControl.
  * Such buttons, for example,  need to recognize mousePressed and 
  * mouseReleased as different events.
  *
  * MouseListenerControl supplies public methods for manipulating
  * a control's EventListener as an MouseListener thereby providing
  * type-safety at runtime.
  */

public abstract class MouseListenerControl extends AbstractListenerControl {

	/** Create a MouseListenerControl and associate it
	* with a Controller.
	* @param controller A Controller with which control
	* is to be associated.
	*/
    protected MouseListenerControl(Controller controller)  {
	super(controller);
    }

	/** Create a MouseListenerControl
	*/
    protected MouseListenerControl()  {
    }

	/**	
	* Add control semantics to this Control.
	* <p>	
	* @param listener java.util.EventListener representing 
	* 		control semantics to be added.
	*/
    protected void addControlListener(EventListener  listener) {
        ((AbstractButton) getControlComponent()).addMouseListener(
		(MouseListener) listener);
    }

	/**	
	* Remove control semantics from this Control.
	* <p>	
	* @param listener java.util.EventListener representing 
	* 		control semantics to be removed.
	*/
    protected void removeControlListener(EventListener  listener)  {
	((AbstractButton) getControlComponent()).removeMouseListener(
		(MouseListener) listener);
    }

	/**
	* Type-safe way to set Control Component and control listener.
	* @param button An AbstractButton that serves as Control	
	* component.
	* @param listener A ChangeListener that implements
	* Control semantics.
	*/
    public void setComponentAndListener(AbstractButton button, MouseListener listener) {
	super.setComponentAndListener(button, listener);
    }

	/**
	* Type-safe way to set Control listener.
	* @param listener A ChangeListener that implements
	* Control semantics.
	*/	
    public void setControlListener(MouseListener listener) {
 	super.setControlListener(listener);
    }

	/**
	* Type-safe way to set Control Component.
	* @param button An AbstractButton that acts as control
	* component.
	*/
    public void setComponent(AbstractButton button) {
	super.setComponent(button);
    }
}
