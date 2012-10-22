package ejmf.toolkit.gui;

import java.awt.Point;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 * An JInternalFrame that can be appropriately sized 
 * and positioned within its parent.
 *
 * @see            java.awt.swing.JInternalFrame
 */
public class ViewScreen extends JInternalFrame {
    /**
     * Create a new JInternalFrame at the specified Point 
     * within its parent Container.
     *
     * @param          p
     *                 java.awt.Point at which JInternalFrame
     *		       will be positioned within parent.
     */
    public ViewScreen(Point p) {
	super();
        setClosable(false);
        setMaximizable(false);
	setResizable(false);
	setLocation(p);
    }

    /**
     * Create a new JInternalFrame using the panel sent
     * as argument as its content pane.
     *
     * @param          panel
     *                 A JPanel used as JInternalFrame's content pane.
     */
    public ViewScreen(JPanel panel) {
	super();
        setClosable(false);
        setMaximizable(false);
	setResizable(false);
	setContentPane(panel);
    }
}
