package ejmf.toolkit.gui.controls;

import java.awt.Dimension;

/**
* VolumeControlButton allows creation of a small upward
* or downward pointing arrow for use as a volume control.
*
* @see ejmf.toolkit.gui.controls.BasicArrowButton
*/

public class VolumeControlButton extends BasicArrowButton
{
	/**
	* Create a upward pointing arrow.
	*/
     public final static int INCREASE = BasicArrowButton.NORTH;
	/**
	* Create a downward pointing arrow.
	*/
     public final static int DECREASE = BasicArrowButton.SOUTH;

	/**
	* Create a VolumeControlButton.
	* @param orientation Determines which way arrow points, NORTH
	* or SOUTH.
	*/
     public VolumeControlButton(int orientation) {
	super(orientation);
     }

	/**	
	* Make it the right size.
	* @return Always return (10, 10)
	*/
     public Dimension getPreferredSize() {
	return new Dimension(10,10);
     }
}
