package ejmf.toolkit.controls;

import java.awt.Component;

import ejmf.toolkit.gui.controls.EjmfReverseButton;
/**
* Reverse Control for EJMF Control Panel
*/

public class EjmfReverseControl extends StandardReverseControl {
	/**
	* Create EjmfReverseButton
	*/
    protected Component createControlComponent() {
	return new EjmfReverseButton();
    }
}
