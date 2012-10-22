package ejmf.toolkit.controls;

import java.awt.Component;

import ejmf.toolkit.gui.controls.EjmfPauseButton;
/**
* Pause Control for EJMF Control Panel
*/

public class EjmfPauseControl extends StandardPauseControl {
	/**
	* Create EjmfPauseButton
	*/
    protected Component createControlComponent() {
	return new EjmfPauseButton();
    }
}
