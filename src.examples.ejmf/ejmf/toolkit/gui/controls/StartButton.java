package ejmf.toolkit.gui.controls;

import java.awt.Dimension;

/**
* Start button for StandardContolPanel.
* StartButton takes full advantage of BasicArrowButton
* to draw an east-facing arrow icon.
*/
public class StartButton extends BasicArrowButton
{
	/**
	* Create a StartButton	
	*/
        public StartButton() {
	    super(BasicArrowButton.EAST);
	}
	/**
	* Make it the right size.
	* @return Always return (20, 20)
	*/
        public Dimension getPreferredSize() {	
	    return new Dimension(20, 20);
        }
}
