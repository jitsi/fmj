package ejmf.toolkit.controls;

import java.awt.Component;

import ejmf.toolkit.gui.controls.EjmfProgressBar;
/**
* Progess Control for EJMF Control Panel
*/

public class EjmfProgressControl extends StandardProgressControl {
	/**
	* Create EjmfProgressBar
	*/
    protected Component createControlComponent() {
	return new EjmfProgressBar();
    }
}
