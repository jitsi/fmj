package ejmf.toolkit.controls;

import java.awt.Component;

import ejmf.toolkit.gui.controls.EjmfGainButtonPanel;

/**
* Gain Control for EJMF Control Panel
*/
public class EjmfGainControl extends StandardGainControl  {

	/**
	* Create EjmfGainButtonPanel
	*/
    protected Component createControlComponent() {
	return new EjmfGainButtonPanel();
    }
}
