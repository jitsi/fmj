package ejmf.toolkit.gui.controls;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import ejmf.toolkit.util.Utility;

/**
* EjmfProgressBar is a stipped-down JSlider. 
*<p>
* The thumb image is constructed from the image in 
* $EJMF_HOME/classes/lib/ejmf.properties named by
* greenBall.
*/

public class EjmfProgressBar extends ProgressSlider {

    private ImageIcon	thumbImage;

	/** Create a progress slider for EJMF control panel.	
	*/
    public EjmfProgressBar() {
       	thumbImage = Utility.getImageResource("greenBall");
	setValue(0);
    }

	/** Draw thumb in progress bar. Default thumb is a 
	* rectangle.
	* @param g Graphics in which to draw thumb.
	*/
    public void paintThumb(Graphics g)  {
	Rectangle rect = getThumbRect();
	thumbImage.paintIcon(this, g, 0, 0);
    }

	/** Calculate size of thumb.
	* @param a Rectangle specifying size of thumb.	
	*/
    public Rectangle computeThumbRectangle() {
        return new Rectangle(thumbImage.getIconWidth(),
			     thumbImage.getIconHeight());
    }
	/** Return size of progress slider.
	* @return Dimension of progress slider.
	*/
    public Dimension getPreferredSize() {
	int len = getMaximum() - getMinimum();
	return new Dimension(len + thumbImage.getIconWidth(),
				thumbImage.getIconHeight());
    }
}
