package ejmf.toolkit.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/** A simple LayoutManager that tiles Components within
 *  a container. Components are laid out left-right, top-bottom.
 *  The height of each row is the max. height of all the Components
 *  in that row.
 *
 *  @see java.awt.LayoutManager
 */

public class TileLayout implements LayoutManager {
    public void addLayoutComponent(String name, Component c) {}
    public void removeLayoutComponent(Component c) {}
 
    public void layoutContainer(Container target) {
	Insets	insets = target.getInsets();
	int	ncomps = target.getComponentCount();

	int	currX = insets.left;
	int	currY = insets.top;
        int	maxY = -1;

        int 	maxW = target.getSize().width - insets.right;

	for (int i = 0; i < ncomps; i++) {
	    Component comp = target.getComponent(i);
	    Dimension ps = comp.getPreferredSize();
	    if (comp.isVisible()) {
        	if (currX + ps.width > maxW) {
	    	    currX = insets.left;
	    	    currY += maxY;
	    	    maxY = -1; 
		} 

	        // track max. height of current row
		if (ps.height > maxY) 
	    	    maxY = ps.height;

		comp.setBounds(currX, currY, ps.width, ps.height);
		currX += ps.width;
	    }
	}
    }
    public Dimension minimumLayoutSize(Container target) {
	return computeDimension(target, true);
    }

    public Dimension preferredLayoutSize(Container target) {
	return computeDimension(target, false);
    }

    private Dimension computeDimension(Container target, boolean minFlag) {
	int	ncomps = target.getComponentCount();
	Insets	insets = target.getInsets();
	int	width = 0, height = 0;

	width = insets.left + insets.right;
	height = insets.top + insets.bottom;

	for (int i = 0; i < ncomps; i++) {
	    Component c = target.getComponent(i);
	    Dimension d;
	    if (minFlag)
		d = c.getMinimumSize();
	    else
		d = c.getPreferredSize();
	    
 	    width += d.width;
	    height += d.height;
	}
	return new Dimension(width, height);
    }
}
