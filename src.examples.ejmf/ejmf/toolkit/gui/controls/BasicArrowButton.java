/*
 * @(#)BasicArrowButton.java	1.8 97/11/11
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package ejmf.toolkit.gui.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.UIManager;

/**
 * JButton object that draws a scaled Arrow in one of the cardinal directions.
 *
 * @version 1.8 11/11/97
 * @author David Kloba
 */
public class BasicArrowButton extends BasicControlButton
{
        protected int direction;

        public BasicArrowButton(int direction)            {
            setDirection(direction);
            setBackground(UIManager.getColor("control"));
        }

        public int getDirection() { return direction; }

        public void setDirection(int dir) { direction = dir; }

        protected void paintIcon(Graphics g, 
			int x, int y, int size, boolean isEnabled) {
	    // paint is over-ridden from BasicControlButton
	    // so paintIcon is no-op.
	}

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

            /// Draw the proper Border
            if (isPressed) {
                g.setColor(UIManager.getColor("controlShadow"));
                g.drawRect(0, 0, w-1, h-1);
            } else {
                // Using the background color set above
                g.drawLine(0, 0, 0, h-1);
                g.drawLine(1, 0, w-2, 0);

                g.setColor(UIManager.getColor("controlHighlight"));    // inner 3D border
                g.drawLine(1, 1, 1, h-3);
                g.drawLine(2, 1, w-3, 1);

                g.setColor(UIManager.getColor("controlShadow"));       // inner 3D border
                g.drawLine(1, h-2, w-2, h-2);
                g.drawLine(w-2, 1, w-2, h-3);

                g.setColor(UIManager.getColor("controlDkShadow"));     // black drop shadow  __|
                g.drawLine(0, h-1, w-1, h-1);
                g.drawLine(w-1, h-1, w-1, 0);
            }

            // If there's no room to draw arrow, bail
            if(h < 5 || w < 5)      {
                g.setColor(origColor);
                return;
            }

            if (isPressed) {
                g.translate(1, 1);
            }

            // Draw the arrow
            size = Math.min((h - 4) / 3, (w - 4) / 3);
            size = Math.max(size, 2);
	    paintTriangle(g, (w - size) / 2, (h - size) / 2,
				size, direction, isEnabled);

            // Reset the Graphics back to it's original settings
            if (isPressed) {
                g.translate(-1, -1);
	    }
	    g.setColor(origColor);

        }

        public Dimension getPreferredSize() {
            return new Dimension(16, 16);
        }

        public Dimension getMinimumSize() {
            return new Dimension(5, 5);
        }

        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    
    	public boolean isFocusTraversable() {
	  return false;
	}

	
	public void paintTriangle(Graphics g, int x, int y, int size, 
					int direction, boolean isEnabled) {
	    Color oldColor = g.getColor();
	    int mid, i, j;

	    j = 0;
            size = Math.max(size, 2);
	    mid = size / 2;
	
	    g.translate(x, y);
	    if(isEnabled)
		g.setColor(UIManager.getColor("controlDkShadow"));
	    else
		g.setColor(UIManager.getColor("controlShadow"));

            switch(direction)       {
            case NORTH:
                for(i = 0; i < size; i++)      {
                    g.drawLine(mid-i, i, mid+i, i);
                }
                if(!isEnabled)  {
                    g.setColor(UIManager.getColor("controlHighlight"));
                    g.drawLine(mid-i+2, i, mid+i, i);
                }
                break;
            case SOUTH:
                if(!isEnabled)  {
                    g.translate(1, 1);
                    g.setColor(UIManager.getColor("controlHighlight"));
                    for(i = size-1; i >= 0; i--)   {
                        g.drawLine(mid-i, j, mid+i, j);
                        j++;
                    }
		    g.translate(-1, -1);
		    g.setColor(UIManager.getColor("controlShadow"));
		}
		
		j = 0;
                for(i = size-1; i >= 0; i--)   {
                    g.drawLine(mid-i, j, mid+i, j);
                    j++;
                }
                break;
            case WEST:
                for(i = 0; i < size; i++)      {
                    g.drawLine(i, mid-i, i, mid+i);
                }
                if(!isEnabled)  {
                    g.setColor(UIManager.getColor("controlHighlight"));
                    g.drawLine(i, mid-i+2, i, mid+i);
                }
                break;
            case EAST:
                if(!isEnabled)  {
                    g.translate(1, 1);
                    g.setColor(UIManager.getColor("controlHighlight"));
                    for(i = size-1; i >= 0; i--)   {
                        g.drawLine(j, mid-i, j, mid+i);
                        j++;
                    }
		    g.translate(-1, -1);
		    g.setColor(UIManager.getColor("controlShadow"));
                }

		j = 0;
                for(i = size-1; i >= 0; i--)   {
                    g.drawLine(j, mid-i, j, mid+i);
                    j++;
                }
		break;
            }
	    g.translate(-x, -y);	
	    g.setColor(oldColor);
	}
	
}

