package ejmf.toolkit.gui.controls;

import java.awt.Dimension;

/**
* Reverse button for StandardContolPanel.
* ReverseButton takes full advantage of BasicArrowButton
* to draw a west-facing arrow icon.
*/
public class ReverseButton extends BasicArrowButton 
{
	/**
	* Create a ReverseButton	
	*/
        public ReverseButton() {
	    super(BasicArrowButton.WEST);
	}
	/**
	* Make it the right size.
	* @return Always return (20,20)
	*/
        public Dimension getPreferredSize() {	
	    return new Dimension(20, 20);
        }
}
