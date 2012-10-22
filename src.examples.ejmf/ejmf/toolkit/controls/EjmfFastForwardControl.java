package ejmf.toolkit.controls;

import java.awt.Component;

import ejmf.toolkit.gui.controls.EjmfFastForwardButton;

/**
* Fast forward Control for EJMF Control Panel
*/

public class EjmfFastForwardControl extends StandardFastForwardControl {
    	/**
	* Create EjmfFastForwardButton
	*/
    protected Component createControlComponent() {
	return new EjmfFastForwardButton();
    }
}
