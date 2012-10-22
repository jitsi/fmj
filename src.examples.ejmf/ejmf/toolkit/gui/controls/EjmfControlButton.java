package ejmf.toolkit.gui.controls;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/** 
  * A class of controls that do attract nor paint the
  * focus.
  * <p>
  * This class simply extends JButton and over-rides
  * <ul>
  * <li>isFocusTraversable
  * <li>isFocusPainted
  * </ul>
  */

public class EjmfControlButton extends JButton {

    ImageIcon im;

	/** Create an EjmfControlButton
	*/
    public EjmfControlButton() {
	super();
	setAttributes();
    }

	/** Create an EjmfControlButton
	* from an ImageIcon.
	*/
    public EjmfControlButton(ImageIcon im) {
	super(im);
	this.im = im;
	setAttributes();
    }

    private void setAttributes() {
	setOpaque(false);
	setBorder(new EmptyBorder(0,0,0,0));
    }

	/** Force to false so that button does not appear
	* in focus traversal list.
	* @return Always return false.
	*/
    public boolean isFocusTraversable() {
	return false;
    }

	/** Force to false so that focus box does not get drawn
	* around button.
	* @return Always return false.
	*/
    public boolean isFocusPainted() {
	return false;
    }

	/**
	* Return preferred size of button. Button size is
	* based on its label icon.
	* @return preferred size of button.
	*/ 
    public Dimension getPreferredSize() {
	if (im != null)
	    return new Dimension(im.getIconWidth()+4,
				im.getIconHeight()+4);
	else 
	    return new Dimension(20, 20);
    }
}
