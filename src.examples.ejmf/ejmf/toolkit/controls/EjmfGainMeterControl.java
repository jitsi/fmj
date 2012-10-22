package ejmf.toolkit.controls;

import java.awt.Component;

import ejmf.toolkit.gui.controls.EjmfGainMeterButton;

/**
* Gain meter Control for EJMF Control Panel
*/
public class EjmfGainMeterControl extends StandardGainMeterControl {
	/**
	* Create EjmfGainMeterButton
	*/
    protected Component createControlComponent() {
	return new EjmfGainMeterButton();
    }
}
