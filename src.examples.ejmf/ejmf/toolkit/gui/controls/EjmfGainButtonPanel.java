package ejmf.toolkit.gui.controls;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import ejmf.toolkit.util.Utility;

/**
* The EjmfGainButtonPanel creates two buttons, once 
* each for gain increase and decrease.
* The images it uses are named in $EJMF_HOME/classes/lib/ejmf.properties file.
* It uses a different image for each of normal, disabled,
* pressed and rollover states.
*/

public class EjmfGainButtonPanel extends AbstractGainButtonPanel {

	/** Create the gain increase button.
	* @return an AbstractButton that functions as gain increase
	* control
	*/
    protected AbstractButton createGainIncreaseButton() {
        ImageIcon icon = Utility.getImageResource("upArrow_image");
        ImageIcon pIcon = Utility.getImageResource("pressedUpArrow_image");
        ImageIcon dIcon = Utility.getImageResource("disabledUpArrow_image");
        ImageIcon rIcon = Utility.getImageResource("rolloverUpArrow_image");

        JButton b = new EjmfControlButton(icon);
        b.setPressedIcon(pIcon);
	b.setDisabledIcon(dIcon);
	b.setRolloverIcon(rIcon);
	return b;
    }

	/** Create the gain decrease button.
	* @return an AbstractButton that functions as gain decrease
	* control
	*/
    protected AbstractButton createGainDecreaseButton() {
 	ImageIcon icon = Utility.getImageResource("downArrow_image");
 	ImageIcon pIcon = Utility.getImageResource("pressedDownArrow_image");
        ImageIcon dIcon = Utility.getImageResource("disabledDownArrow_image");
        ImageIcon rIcon = Utility.getImageResource("rolloverDownArrow_image");

        JButton b = new EjmfControlButton(icon);
        b.setPressedIcon(pIcon);
	b.setDisabledIcon(dIcon);
	b.setRolloverIcon(rIcon);
	return b;
    }
}
