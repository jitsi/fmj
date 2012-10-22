package ejmf.toolkit.gui.controls;

import javax.swing.ImageIcon;

import ejmf.toolkit.util.Utility;

/**
* The EjmfPauseButton consists of two vertical bars.
* <p>
* It is constructed from the image named pauseButton_image in
* the $EJMF_HOME/classes/lib/ejmf.properties file.
*/

public class EjmfPauseButton extends EjmfControlButton {
	/**
	* Create an EjmfPauseButton for EJMF control panel.
	*/
    public EjmfPauseButton() {
	ImageIcon im = Utility.getImageResource("pauseButton_image");
	setIcon(im);
	setDisabledIcon(Utility.getImageResource("disabledPauseButton_image"));
    }
}
