package ejmf.toolkit.controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.media.Controller;
import javax.media.GainChangeEvent;
import javax.media.GainChangeListener;
import javax.media.GainControl;
import javax.swing.SwingUtilities;

import ejmf.toolkit.gui.controls.GainMeter;
import ejmf.toolkit.gui.controls.GainMeterButton;

/**
* Gain meter Control for StandardControlPanel.
*/
public class StandardGainMeterControl extends AbstractGainControl
		implements GainChangeListener {

	/** Create a StandardGainMeterControl and associate it
	* with a Controller.
	* @param controller A Controller with which control is associated.
	*/
    public StandardGainMeterControl(Controller controller) {
	super(controller);
    }

	/** Create a StandardGainMeterControl */
    public StandardGainMeterControl() {
    }

	/**
	*  This method is called when <tt>setController</tt>
	* is called on an AbstractListenerControl.
	* @param newController A Controller with which this control
	* is associated.
	*/
    protected void setControllerHook(Controller newController) {
	super.setControllerHook(newController);
	if (isOperational()) {
	    GainControl gc = getGainControl();
	    setLevel(gc.getLevel());
	    setMute(gc.getMute());
	    gc.addGainChangeListener(this);
	}
    }

	/**
	* Create the GainMeter Component.	
	* @return The component that acts as gain meter control.
	* @see ejmf.toolkit.gui.controls.GainMeterButton
	*/
    protected Component createControlComponent() {
	return new GainMeterButton();
    }

	/**
	* Set the level value for this GainMeter.
	* The input argument is a level value from the
	* Player's GainControl. It is the GainMeter's
	* responsibility to convert it to a value that
	* can be meaningfully rendered.
	* 
	* @param level This argument represents a level
	* value returned by a Player's GainControl. 
	*/
    public void setLevel(float level) {
	((GainMeter) getControlComponent()).setLevel(level);
    }	

	/**
	* Set the mute value for this GainMeter. 
	* 
	* @param muted If muted is true, audio signal is
	* suppressed. Otherwise, audio signal is rendered.
	*/
    public void setMute(boolean muted) {
	((GainMeter) getControlComponent()).setMute(muted);
    }

	/**
	* Return the ActionListener used to toggle 
	* mute button.
	* @return An ActionListener for toggling mute state.
	*/
    protected EventListener createControlListener() {
	    return new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
                    GainControl gc = getGainControl();
		    boolean muted = gc.getMute();
		    gc.setMute(!muted);
		    setMute(!muted);
          	}
           };
    }
   	/**
     	* Implements GainChangeListener. Level and mute
     	* are set appropriately and the view updated for every
     	* change to the GainControl on which this GainMeter is a
     	* listener.
	* @param e An GainChangeEvent triggerd by a GainControl
	* @see javax.media.GainControl	
	* @see javax.media.GainChangeEvent
	*/
   public void gainChange(GainChangeEvent gce) {
       float level = gce.getSourceGainControl().getLevel();
       SwingUtilities.invokeLater(new SetLevelThread(level));
   }

	/* A convenience runnable for manipulating
	* level as displayed in GUI.
	*/
   class SetLevelThread implements Runnable {
	private float level;
        public SetLevelThread(float level) {
	    this.level = level;
        }
        public void run() {
	    setLevel(level);
        }
   }
}
