package ejmf.toolkit.gui.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
* Draws basic Control Panel buttons.
* Subclasses supply implementation of painIcon
* to draw button icon. 
* <p>
* This class takes care of drawing 'pressed' and 'enabled/disabled'
* look of button.
*/

public abstract class BasicControlButton extends JButton 	
		implements SwingConstants
{
    
    public BasicControlButton() {
	setBackground(UIManager.getColor("control"));
    }

	/**
	*  Paint icon into button Component
	*/
    protected abstract void paintIcon(Graphics g, 
			int x, int y, int size, boolean isEnabled);

	/**
	* Paint the background and border or button component.
	* Call out to painIcon to draw icon in button.
	* <p>
	* This method takes care of all bevellng, etc depending on
	* whether button is pressed of enabled/disabled.
	*/

    public void paint(Graphics g) {
	Color origColor;
	boolean isPressed, isEnabled;
	int w, h, size;

        w = getSize().width;
        h = getSize().height;
	origColor = g.getColor();
	isPressed = getModel().isPressed();
	isEnabled = isEnabled();

        g.setColor(getBackground());
        g.fillRect(1, 1, w-2, h-2);

        if (isPressed) {
            g.setColor(UIManager.getColor("controlShadow"));
            g.drawRect(0, 0, w-1, h-1);
        } else {
            g.drawLine(0, 0, 0, h-1);
            g.drawLine(1, 0, w-2, 0);

            g.setColor(UIManager.getColor("controlHighlight"));    // inner 3D border
            g.drawLine(1, 1, 1, h-3);
            g.drawLine(2, 1, w-3, 1);

            g.setColor(UIManager.getColor("controlShadow"));       // inner 3D border
            g.drawLine(1, h-2, w-2, h-2);
            g.drawLine(w-2, 1, w-2, h-3);

            g.setColor(UIManager.getColor("controlDkShadow"));     // black drop shadow 
            g.drawLine(0, h-1, w-1, h-1);
            g.drawLine(w-1, h-1, w-1, 0);
        }

        if (h < 5 || w < 5)      {
            g.setColor(origColor);
            return;
        }

        if (isPressed) {
            g.translate(1, 1);
        }

        size = Math.min((h - 4) / 3, (w - 4) / 3);
        size = Math.max(size, 2);
	paintIcon(g, (w - size) / 2, (h - size) / 2,
				size, isEnabled);

        // Reset the Graphics back to it's original settings
        if (isPressed) {
            g.translate(-1, -1);
	}
	g.setColor(origColor);

    }
	/**
	* All Control Panel buttons have same preferred size.	
	*/
    public Dimension getPreferredSize() {	
	return new Dimension(20, 20);
    }

	/**
	* Don't let button get so small that icon	
	* is unrecognizable.	
	*/
    public Dimension getMinimumSize() {
          return new Dimension(5, 5);
    }


	/** 	
	* Always return false.	
	*/
    public boolean isFocusTraversable() {
        return false;
    }

}
	
