package ejmf.toolkit.gui.controls;

import ejmf.toolkit.util.Utility;

/**
* The EjmfStopButton is a red circular button.
* <p>
* It is constructed from the image named stopButton_image in
* the $EJMF_HOME/classes/lib/ejmf.properties file.
* <p>
* The following images also are used:
* <ul>
* <li>disabledStopButton
* <li>pressedStopButton
* <li>rolloverStopButton
* </ul>
*/

public class EjmfStopButton extends EjmfControlButton {
	/** Create an EjmfStopButton for EJMF control panel.
	*/
    public EjmfStopButton() {
	setIcon(Utility.getImageResource("stopButton_image"));
	setDisabledIcon(Utility.getImageResource("disabledStop_image"));
	setRolloverIcon(Utility.getImageResource("rolloverStop_image"));
	setPressedIcon(Utility.getImageResource("pressedStop_image"));
    }
}
