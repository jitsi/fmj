package ejmf.toolkit.gui.controls;

import javax.swing.ImageIcon;

import ejmf.toolkit.util.Utility;

/**
* The EjmfFastForwardButton is created from images
* named in $EJMF_HOME/classes/lib/ejmf.properties file.
* It uses a different image for each of normal, disabled,
* pressed and rollover states.
*/

public class EjmfFastForwardButton extends EjmfControlButton {
	/**
	* Create a fast forward button for EJMF control panel.
	*/
    public EjmfFastForwardButton() {
	ImageIcon im = Utility.getImageResource("fastForwardButton_image");
	setIcon(im);
	setDisabledIcon(
	    Utility.getImageResource("disabledFastForward_image"));
	setPressedIcon(
	    Utility.getImageResource("pressedFastForward_image"));
	setRolloverIcon(
	    Utility.getImageResource("rolloverFastForward_image"));
	
    }
}
