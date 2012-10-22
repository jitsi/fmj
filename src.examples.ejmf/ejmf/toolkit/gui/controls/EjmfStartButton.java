package ejmf.toolkit.gui.controls;

import javax.swing.ImageIcon;

import ejmf.toolkit.util.Utility;

/**
* The EjmfReverseButton is a circular button. When the Player is
* stopped, it is displayed as solid green. When the Player is stopped,
* it is displayed in 'pause' state depicted by two vertical bars.
* <p>
* It is constructed from the image named startButton_image in
* the $EJMF_HOME/classes/lib/ejmf.properties file.
* <p>
* The following images also are used:
* <ul>
* <li>disabledStartButton
* <li>pressedStartButton
* <li>rolloverStartButton
* </ul>
* <p>
* Because EjmfStartButton is a hybrid, acting as start and pause
* Control, it also relies on the following images from ejmf.properties file.
* <ul>
* <li>pauseButton_image
* <li>disabledPauseButton
* <li>pressedPauseButton
* <li>rolloverPauseButton
* </ul>
*/
public class EjmfStartButton extends EjmfControlButton {
  
    private ImageIcon startActive;
    private ImageIcon startPressed;
    private ImageIcon startRollover;
    private ImageIcon startDisabled;

    private ImageIcon pauseActive;
    private ImageIcon pausePressed;
    private ImageIcon pauseRollover;
    private ImageIcon pauseDisabled;

	/** Create an EjmfStartButton	 for EJMF control panel.
	*/
    public EjmfStartButton() {
	startActive = Utility.getImageResource("startButton_image");
	startRollover = Utility.getImageResource("rolloverStart_image");
	startPressed = Utility.getImageResource("pressedStart_image");
	startDisabled = Utility.getImageResource("disabledStart_image");

	displayAsStart();

	pauseActive = Utility.getImageResource("pauseButton_image");
	pauseRollover = Utility.getImageResource("rolloverPause_image");
	pausePressed = Utility.getImageResource("pressedPause_image");
	pauseDisabled = Utility.getImageResource("disabledPause_image");
    }

	/** Display start button as round green button.
	*/
    public void displayAsStart() {
      	setIcon(startActive);
	setRolloverIcon(startRollover);
	setPressedIcon(startPressed);
	setDisabledIcon(startDisabled);
    }

	/** Display as two red vertical "pause" bars. 	
	*/
    public void displayAsPause() {
      	setIcon(pauseActive);
	setRolloverIcon(pauseRollover);
	setPressedIcon(pausePressed);
	setDisabledIcon(pauseDisabled);
    }
}
