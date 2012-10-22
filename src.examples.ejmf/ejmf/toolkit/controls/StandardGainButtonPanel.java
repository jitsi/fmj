package ejmf.toolkit.controls;

import javax.swing.AbstractButton;

import ejmf.toolkit.gui.controls.AbstractGainButtonPanel;
import ejmf.toolkit.gui.controls.VolumeControlButton;

/**
* Provides up/down arrow buttons for increasing/decreasing
* Player gain. This panel is used by StandardGainControl.
*/

class StandardGainButtonPanel extends AbstractGainButtonPanel {

	/**
	* Create button for increasing gain.
	* @return An AbstractButton that acts as gain increase
	* control.
	*/
    public AbstractButton createGainIncreaseButton() {
	return(new VolumeControlButton(VolumeControlButton.INCREASE));
    }

	/**
	* Create button for decreasing gain.
	* @return An AbstractButton that acts as gain decrease
	* control.
	*/
    public AbstractButton createGainDecreaseButton() {
 	return(new VolumeControlButton(VolumeControlButton.DECREASE));
    }
}
