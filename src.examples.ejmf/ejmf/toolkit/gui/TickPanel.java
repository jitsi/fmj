package ejmf.toolkit.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
* A TickPanel is a JPanel with tick marks drawn along the top.
*/

public class TickPanel extends JPanel {
    private int			min;
    private int			max;
    private int			tickIncrement;
    private int			labelIncrement = 5;
    private Dimension		size;
    private Font		labelFont;

	/** 
	* Create a TickPanel whose values range from <tt>min</tt>
	* to <tt>max</max> appearing <tt>increment</tt> units
	* apart.
	*		
	* The <tt>size</tt> argument determines the horizontal
	* dimension of the TickPanel.
	*/
    public TickPanel(int min, int max, int increment, Dimension size) {
	this.min = min;
        this.max = max;
        this.tickIncrement = increment;
        this.size = size;
	labelFont = Font.decode("Courier-10");
	setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    public Dimension getPreferredSize() {
	return size;
    }
  
    public Dimension getMaximumSize() {
	return size;
    }

    public Dimension getMinimumSize() {
   	return size;
    }
	
	/**
	* Sets the increment between tick marks.	
	*
	* @param labelIncrement	Increment between tick marks.
	*/
    public void setLabelIncrement(int labelIncrement) {
	this.labelIncrement = labelIncrement;
    }

	/**	
	* Paint the ticks in the panel.
	*/
    public void paint(Graphics g) {
	Graphics mygfx;

	if (labelFont != null) {
	    mygfx = g.create();
	    mygfx.setFont(labelFont);
	}  // Just in case, requested font unavailable
	else
	    mygfx = g;

	FontMetrics fm = mygfx.getFontMetrics();

	int offset = 0;
	double w = (double) size.width;
	double ppu = w / (max - min);
	while (ppu * tickIncrement < 2) {
	    tickIncrement += 10;
	}

	int pix_inc = (int)(tickIncrement * ppu);
	int labelSpan = labelIncrement * tickIncrement;

	for (int i = min, x = 0, j = 0; 
		i <= max; 
		i += tickIncrement, x += pix_inc) {
	    if (i % labelSpan == 0) {
	 	String label = String.valueOf(min + (j * labelSpan));
		offset = 0;
		// Place leftmost label to right
		if (i != min) 
		    offset = fm.stringWidth(label) / 2;
	        if (i >= max)  // Place rightmost label to left
		    offset = fm.stringWidth(label);
		
		mygfx.drawString(label, x - offset, size.height/2);
	        mygfx.drawLine(x, size.height/2, x, size.height); 
		j++;
	    } else 
		mygfx.drawLine(x, 3 * size.height/4, x, size.height);
        }

	if (labelFont != null)
	    mygfx.dispose();
    }
}
