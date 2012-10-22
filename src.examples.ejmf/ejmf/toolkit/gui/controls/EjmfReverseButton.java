package ejmf.toolkit.gui.controls;

import ejmf.toolkit.util.Utility;
/**
* The EjmfReverseButton is a backward arrow. 
* <p>
* It is constructed from the image named reverseButton_image in
* the $EJMF_HOME/classes/lib/ejmf.properties file.
* <p>
* The following images also are used:
* <ul>
* <li>disabledReverseButton
* <li>pressedReverseButton
* <li>rolloverReverseButton
* </ul>
*/

public class EjmfReverseButton extends EjmfControlButton {
	/** Create an EjmfReverseButton	 for EJMF control panel.
	*/
    public EjmfReverseButton() {
	setIcon( Utility.getImageResource("reverseButton_image"));
	setDisabledIcon(
		Utility.getImageResource("disabledReverse_image"));
	setPressedIcon(
		Utility.getImageResource("pressedReverse_image"));
	setRolloverIcon(
		Utility.getImageResource("rolloverReverse_image"));
    }
}
