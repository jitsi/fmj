package ejmf.toolkit.gui.controlpanel;

import java.awt.Color;

import javax.media.Player;

import ejmf.toolkit.gui.controls.AbstractGainButtonPanel;

/** 
  * EjmfControlPanel extends StandardControlPanel and 
  * provides a look and feel similar to Sun's JMF for
  * cross-platform use.
  *
  */
public class EjmfControlPanel extends StandardControlPanel {
    private static final Color		myBackground = Color.white;

	/**
	* Create the controls for Player managed by this control panel.
	* @param player Player for which controls are built.
	*/
    protected AbstractControls createControls(Player player) {
	return new EjmfControls(player);
    }
  
	/**
	* Create the control panel for Player.
	* @param player Player for which control panel is built.
	* @param buttonFlags	Determines which control buttons
	* will appear in this control panel.
	*/
    protected EjmfControlPanel(Player player, int buttonFlags) {
	super(player, buttonFlags);
    	setBackground(myBackground);
    }

	/**	
	* Create an EJMF Control Panel.
	* Start button doubles as pause button when	
	* Player is playing.	
	* @param player Player for which control panel is built.
	*/
    public EjmfControlPanel(Player player) {

	// Set up default semantics
	this( player,  AbstractControlPanel.USE_START_CONTROL |
	       AbstractControlPanel.USE_GAIN_CONTROL |
	       AbstractControlPanel.USE_REVERSE_CONTROL |
	       AbstractControlPanel.USE_FF_CONTROL |
	       AbstractControlPanel.USE_GAINMETER_CONTROL |
	       AbstractControlPanel.USE_PROGRESS_CONTROL);

    }

	/** 	
	* Set the background of the Panel that contains	
	* Control Components.	
        * @param bg The background color for control panel.
	*/
    public void setBackground(Color bg) {
	super.setBackground(bg);
	if (getControls() != null) {
	    AbstractGainButtonPanel gbp = getGainButtonPanel();
	    gbp.setBackground(bg);
	}
    }
}

