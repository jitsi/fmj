package ejmf.toolkit.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Hashtable;

import javax.media.Player;
import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ejmf.toolkit.util.Debug;

/**
 * ViewingPanel provides a JDesktopPane in which can be displayed
 * any number of screen for viewing a Player. Screens are 
 * added with addScreen. Screens are removed from the desktop
 * using removeScreen. 
 *
 * @see		   java.awt.swing.JLayeredPane
 * @see		   java.awt.swing.JDesktopPane
 * @see            javax.media.Player
 */
public class ViewingPanel extends JPanel  {

    /** 
     *  This value is OR'd into third argument to addScreen
     *  if the screen should display the Player visual
     *  component.
     */
    public static final int 	DISPLAY_VISUAL = 1;
    /** 
     *  This value is OR'd into third argument to addScreen
     *  if the screen should display the Player control
     *  component.
     */
    public static final int 	DISPLAY_CONTROL = 2;

    /** A convenience value used to display both visual and
     * control component of a Player when adding to ViewingPanel.
     */
    public static final int 	DISPLAY_BOTH = 3;

    private JLayeredPane	desktop;
   
    private Hashtable		hash;	// screen <-> player

    /**
     * Create a panel in which a JDesktopPane is used to
     * to display screens for viewing a Player.
     *
     * @param          title
     *                 Title displayed in border of JDesktopPane
     */
    public ViewingPanel(String title) {
	TitledBorder	tb;
	setLayout(new BorderLayout());
	desktop = new JDesktopPane();
	desktop.setLayout(new TileLayout());
	desktop.setBorder(tb = new TitledBorder(
				new CompoundBorder(
					new EtchedBorder(),
					new EmptyBorder(4, 4, 4, 4)),
				title));

	tb.setTitleColor(Color.black);
	desktop.setOpaque(false);
  	desktop.setBackground(UIManager.getColor("control"));
        add(desktop, BorderLayout.CENTER);
	hash = new Hashtable();
    }

    /**
     * Returns the preferred dimensions for the desktop.
     *
     * @return         java.awt.Dimension 
     */
    public Dimension getPreferredSize() {
	return new Dimension(500,300);
    }

    /**
     * Create a JInternalFrame and add it to the desktop.
     * The video component of Player is displayed
     * in this JInternalFrame. The control panel component
     * is not displayed. Use addScreen(String, Player, true)
     * if control panel should be displayed.
     *
     * Player must be in Realized state so that visual and
     * control components can be retieved.
     *
     * @param          title
     *                 Title displayed in JInternalFrame border.
     * @param          player
     *                 A Player in the Realized state.
     */
    public void addScreen(String title, Player player) {
	this.addScreen(title, player, DISPLAY_VISUAL);
    }

    /**
     * Create a JInternalFrame and add it to the desktop.
     * The video component of Player is always displayed
     * in this JInternalFrame if displayFlags & DISPLAY_VISUAL
     * is true. The control panel component
     * is displayed if displayFlags & DISPLAY_CONTROL is true.
     *
     * Player must be in Realized state so that visual and
     * control components can be retieved.
     *
     * @param          title
     *                 Title displayed in JInternalFrame border.
     * @param          player
     *                 A Player in the Realized state.
     * @param	       displayFlags
     *			Flag which determines which AWT components
     * 			associated with Player are diplayed.
     */
    public void addScreen(String title, 
			Player player, 
			int displayFlags) 
    {
	Debug.printObject("addScreen for " + title);
	int displayCount = 0;

        JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	panel.setBorder(new EmptyBorder(0, 0, 0, 0));
	if ((displayFlags & DISPLAY_VISUAL) != 0) {
	    Component vc = player.getVisualComponent();
	    if (vc != null) {
	        panel.add(vc, BorderLayout.CENTER);
		displayCount++;
 	    }
	}

        if ((displayFlags & DISPLAY_CONTROL) != 0) {
	    Component cp = player.getControlPanelComponent();
	    if (cp != null) {
	        panel.add(cp, BorderLayout.SOUTH);
		displayCount++;
 	    }
        }

	if (displayCount == 0)
	    return;

	panel.validate();
	panel.repaint();

	ViewScreen screen = new ViewScreen(panel);
        screen.setTitle(title);
	screen.pack();

	desktop.add(screen, JLayeredPane.PALETTE_LAYER);
	desktop.validate();
	hash.put(player, screen);
    }

    /**
     * Remove a viewing screen from the desktop.
     *
     * @param          player
     *                 The Player whose screen is to be removed.
     */
    public void removeScreen(Player player) {
	Debug.printObject("removeScreen");
	ViewScreen screen = (ViewScreen) hash.get(player);
	if (screen == null) // Audio only media or already gone
	    return;

	hash.remove(player);
	desktop.remove(screen);
	desktop.repaint();
    }
}
