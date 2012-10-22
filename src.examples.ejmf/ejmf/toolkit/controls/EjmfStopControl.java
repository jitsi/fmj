package ejmf.toolkit.controls;

import java.awt.Component;

import ejmf.toolkit.gui.controls.EjmfStopButton;
/**
* Stop Control for EJMF Control Panel
*/

public class EjmfStopControl extends StandardStopControl {
	/**
	* Create EjmfStopButton
	*/
    protected Component createControlComponent() {
	return new EjmfStopButton();
    }
}
