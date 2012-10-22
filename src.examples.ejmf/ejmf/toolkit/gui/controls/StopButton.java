package ejmf.toolkit.gui.controls;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.UIManager;

public class StopButton extends BasicControlButton 	
{
	/** Create a StopButton
	*/
    public StopButton() {
	super();
    }
	/** 
	* Paint a small square into BasicControlButton
	* @param g Graphics into which rectangles are drawn.
	* @paran x, y  Original translation to point in button where	
	* where square is drawn.
	* @param Size of square.
	* @param isEnabled If true, square is drawn enabled (i.e. black), 
	* otherwise, they are offset by (1,1) and drawn with UIManager's
	* controlShadow color.
	*/
    protected void paintIcon(Graphics g, int x, int y, int size, boolean isEnabled)
    {
  	g.translate(x, y);
	if (isEnabled)
	    g.fillRect(0, 0, size, size);
	else {
  	    g.translate(1, 1);
	    Color oldColor = g.getColor();
	    g.setColor(UIManager.getColor("controlShadow"));
	    g.fill3DRect(0, 0, size, size, false);
	    g.setColor(oldColor);
  	    g.translate(-1, -1);
	}
  	g.translate(-x, -y);
    }
}
