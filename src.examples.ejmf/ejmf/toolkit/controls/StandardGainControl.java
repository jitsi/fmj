package ejmf.toolkit.controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.media.Controller;
import javax.media.GainChangeEvent;
import javax.media.GainChangeListener;
import javax.media.GainControl;
import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

import ejmf.toolkit.gui.controls.AbstractGainButtonPanel;

/**
* Gain Control for Standard Control Panel. Provides
* two Components, one each for increasing and decreasing
* gain.
* @see ejmf.toolkit.controls.AbstractGainControl
*/

public class StandardGainControl extends  AbstractGainControl  
			implements GainChangeListener {

	/** Create a StandardGainControl and associate it
	* with a Controller.
	* @param controller A Controller with which control is associated.
	*/
    public StandardGainControl(Controller controller) {
	super(controller);
    }

	/** Create a StandardGainControl */
    public StandardGainControl() {
    }

	/**
	*  This method is called when <tt>setController</tt>
	* is called on an AbstractListenerControl.
	* @param newController A Controller with which this control
	* is associated.
	*/
    protected void setControllerHook(Controller newController) {
	super.setControllerHook(newController);

	GainControl gc;
	if (isOperational()) {
	    gc = getGainControl();
 	    setState(gc.getLevel());
	    gc.addGainChangeListener(this);
	}
    }


	/**
	* Create Control Component for Gain Control.
	* @return component which acts as gain control.
	*/
    protected Component createControlComponent() {
	return new StandardGainButtonPanel();
    }

    /** 
      * Over-rides addControlListener from AbstractActionListener
      * because this Component is a JPanel and buttons need
      * to be extracted from it. Same listener is posted to
      * both buttons.
	* @param listener Listener that implements semantics for
	* gain control.
      */
    protected void addControlListener(EventListener listener) {
	if (listener instanceof ActionListener) {
	    AbstractButton ab;
	    AbstractGainButtonPanel p = 
		(AbstractGainButtonPanel) getControlComponent();

	    ab = p.getGainIncreaseButton();
            ab.addActionListener((ActionListener) listener);
	    ab = p.getGainDecreaseButton();
            ab.addActionListener((ActionListener) listener);
        } else {
	    throw new IllegalArgumentException("ActionListener required");
	}
    }

    /**
      * Remove listener from both gain increase button and gain
      * decrease button.
	* @param listener Listener is removed from listener list
	* associated with this control.
      */
    protected void removeControlListener(EventListener listener) {
	if (listener instanceof ActionListener) {
	    AbstractButton ab;
	    StandardGainButtonPanel p = 
		(StandardGainButtonPanel) getControlComponent();

	    ab = p.getGainIncreaseButton();
            ab.removeActionListener((ActionListener) listener);
	    ab = p.getGainDecreaseButton();
            ab.removeActionListener((ActionListener) listener);
        } else { 
	    throw new IllegalArgumentException("ActionListener required");
        }
    }

    /**  
      * Create and return the default listener for gain control.
      * Default listener increases gain level by 0.1 for each click
      * of up button and decreases gain level by 0.1 for each click
      * of down button.
      * @return Default listener for gain control.
      */
    protected EventListener createControlListener() {
	return new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		AbstractGainButtonPanel	gbp = 
			(AbstractGainButtonPanel) getControlComponent();
		GainControl gc = getGainControl();
 		float level = gc.getLevel();

		if (e.getSource() == gbp.getGainIncreaseButton()) {
		    level += 0.1f;
		    gc.setLevel(level > 1.0f ? 1.0f : level);
		} else if (e.getSource() == gbp.getGainDecreaseButton()) {
		    level -= 0.1f;
 		    gc.setLevel(level < 0.0f ? 0.0f : level);
		}
	    }
	};
    }

	/**
	* Set enable state of gain buttons based on gain level.
	* If level has reached maximum, disable increase button.
	* If level has reached minimum, enable descrease button.
	* @param e An GainChangeEvent triggerd by a GainControl
	* @see javax.media.GainControl	
	* @see javax.media.GainChangeEvent
	*/
    public void gainChange(GainChangeEvent e) {
 	SwingUtilities.invokeLater(new SetStateThread(e.getLevel()));
    }

	/*
	* Runnable that calls setState and is run on
	* AWT event queue.	
	*/
    class SetStateThread implements Runnable {
       	float level;
        public SetStateThread(float level) {
	    this.level = level;
        } 
	public  void run() {
	    setState(level);
        }
    }

	/* Isolate GUI update so that it can be run on 
	* separate thread.
	* This method simply sets the current state of the		
	* gain control buttons.
	*/
    private void setState(float level) {
	    AbstractGainButtonPanel gbp = 
		(AbstractGainButtonPanel) getControlComponent();
	    gbp.getGainIncreaseButton().setEnabled(level < 1.0f);
	    gbp.getGainDecreaseButton().setEnabled(level > 0.0f);
    }

	/**
	* Type-safe way to set Control Component and control listener.
	* @param bp An AbstractGainButtonPanel that serves as Control	
	* component.
	* @param listener An ActionListener that implements
	* Control semantics.
	*/
    public void setComponentAndListener(AbstractGainButtonPanel bp, ActionListener listener) {
	super.setComponentAndListener(bp, listener);
    }

	/**
	* Type-safe way to set Control listener.
	* @param listener An ActionListener that implements
	* Control semantics.
	*/	
    public void setComponent(AbstractGainButtonPanel bp) {
	super.setComponent(bp);
    }

	/**
	* Type-safe way to set Control Component.
	* @param listener An ActionListener that implements	
	* gain control button semantics.
	*/
    public void setControlListener(ActionListener listener) {
	super.setControlListener(listener);
    }
}
