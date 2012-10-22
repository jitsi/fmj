package ejmf.toolkit.gui.controlpanel;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.media.Controller;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.NotRealizedError;
import javax.media.Player;
import javax.media.StartEvent;
import javax.media.StopEvent;
import javax.swing.SwingUtilities;

import ejmf.toolkit.controls.AbstractListenerControl;

/**  
  * The AbstractControls class:
  * <ul>
  * <li>maintains a Player reference
  * <li>maintains a Hashtable of Controls
  * <li>invokes setController on all of its Controls
  * <li>registers itself on a Player as a ControllerListener
  * </ul> 
  * The controllerUpdate method implements default behavior for
  * changing control component state to reflect Player state. 
  * <p>
  * Subclasses must supply definitions of the following methods:
  * <ul>
  *
  * <li>void makeControls();
  * <li>void setControlComponentState(int state);
  * </ul>
  */

abstract class AbstractControls implements ControllerListener {
    private Player 			player;
    private Hashtable			controlTable = new Hashtable();

    // Make explicit requirement of having subclass call 
    // single argument constructor.
    private AbstractControls() {}

    protected AbstractControls(Player player) {

	if (player.getState() < Controller.Realized)
	    throw new NotRealizedError("Player must be realized");

	this.player = player;
        makeControls();
        setControlsPlayer(player);
	player.addControllerListener(this);
    }

	/**
	* Returns a Control with a given name.
	* @param name String identifying an AbstractControlListener
	* @return an AbstractControlListener identified by the	
	* name argument.
	*/
    public AbstractListenerControl getControl(String name) {
	return (AbstractListenerControl) controlTable.get(name);
    }

	/**	
	* Returns an array of Controls.
	* @return an array of AbstractListenerControls
	* associated with this Control Panel.
	*/
    public AbstractListenerControl[] getControls() {
	Vector	v = new Vector();
	Enumeration elements = controlTable.elements();
	while (elements.hasMoreElements()) {
	    v.addElement(elements.nextElement());
	}
	AbstractListenerControl[] controls = 
			new AbstractListenerControl[v.size()];
	v.copyInto(controls);
        return controls;
    }

    /**
      	* For each control, calls its setPlayer method to
      	* establish the controls association with a Player.
	* @param player Player associated with this set of control.
      */
    private void setControlsPlayer(Player player) {
	Controller c = (Controller) player;
	Enumeration e = controlTable.elements();
        while (e.hasMoreElements()) {
	    AbstractListenerControl alc = 
		(AbstractListenerControl) e.nextElement();
	    alc.setController(c);
	}
    }

	/** 
	* Add a Control to this AbstractControls object.
	* @param name Name of control	
	* @param alc An AbstractListenerControl reference.
        */
    protected void addControl(String name, AbstractListenerControl alc) {
	controlTable.put(name, alc);
    }
  
	/**
	* Return the Player associated with this AbstractControls
	* object.
	* @return The Player associated with these controls.
	*/
    public Player getPlayer() {
	return player;
    }

    /** 
      * This method fields ControllerEvents. Specifically,
      * it looks for start, stop and error events and calls
      * setControlComponentState with either Controller.Started
      * or Controller.Prefetched to signal whether Controller
      * is started or not.
      * <p>
      * Subclasses that override this method should
      * invoke super.controllerUpdate() if default
      * behavior is desired. 
      * to do anything meaningful.
	* @param event a ControllerEvent
      */
    public void controllerUpdate(ControllerEvent event) {
	if (event instanceof StartEvent) {
	    SwingUtilities.invokeLater(
		new ControllerEventThread(Controller.Started));

	} else if (event instanceof StopEvent ||
	           event instanceof ControllerErrorEvent) {

	    SwingUtilities.invokeLater(
		new ControllerEventThread(Controller.Prefetched));
	}
    }

    class ControllerEventThread implements Runnable {
        private int state;
	public ControllerEventThread(int state) {
	       this.state = state;
        }
   
	public void run() {
	    setControlComponentState(state);
        }
     }

	/**
	* Build the controls managed by this AbstractControls
 	* object.
	*/
    protected abstract void makeControls();
	/** 
	* Set the display state of control components based on the
	* state of the Player.
	* @param state The current state of the Player.
	*/
    protected abstract void setControlComponentState(int state);
}
