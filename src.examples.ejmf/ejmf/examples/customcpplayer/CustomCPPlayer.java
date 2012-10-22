package ejmf.examples.customcpplayer;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Player;
import javax.media.Time;
import javax.swing.SwingUtilities;

import ejmf.toolkit.gui.controlpanel.StandardControlPanel;
import ejmf.toolkit.install.PackageUtility;
import ejmf.toolkit.util.PlayerDriver;
import ejmf.toolkit.util.PlayerPanel;
import ejmf.toolkit.util.StateWaiter;

/**
 *    CustomCPPlayer uses the StandardControlPanel to control
 *    the player. 
 * 
 *    blockingRealize and blockPrefetch to ensure video image is
 *    before starting player.
 *
 *    The begin method from PlayerPanel is over-ridden and is used to
 *    disable start button. The begin method does not start the player.
 *    Instead the start button starts the player.
 *
 * @see            ejmf.toolkit.util.PlayerPanel
 * @see            ejmf.toolkit.util.gui.StandardControlPanel
 * @version        1.0
 * @author         Rob Gordon & Steve Talley
 */

public class CustomCPPlayer extends PlayerDriver
    implements ControllerListener {

    private Player			player;
    private PlayerPanel			playerpanel;
    private StandardControlPanel	scp;
    
    public static void main(String args[]) {
        PackageUtility.addContentPrefix("ejmf.toolkit", false);
               main(new CustomCPPlayer(), args);
    }

    /**
     * Creates new StandardControlPanel and adds it to the media
     * panel. Queries player for visual component and adds it to
     * the media panel.
     *
     * @param          media
     *                 URL naming a media source
     */

    public void begin() {

	playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();

        // Add ourselves as a listener to the player's events
        player.addControllerListener(this);

	StateWaiter waiter = new StateWaiter(player);

        if (!waiter.blockingRealize()) {
	    System.err.println("Can't realize Player");
	    return;
	}

	scp = new StandardControlPanel(player);

	Runnable r = new Runnable() {
	    public void run() {
        	playerpanel.addControlComponent(scp);

        	playerpanel.addVisualComponent();
		redraw();
	    }
        };

	SwingUtilities.invokeLater(r);
	if (!waiter.blockingPrefetch()) {
	    System.err.println("Can't prefetch Player");
	    return;
	}
	player.start();
    }

    /**
     * This controllerUpdate function must be defined in order to
     * implement a ControllerListener interface. This 
     * function will be called whenever there is a media event
     */
    public synchronized void controllerUpdate(ControllerEvent event) {

        /*
         * Set the state of the buttons to the correct values.
         */
        if (event instanceof EndOfMediaEvent) {
            // End of the media -- rewind
            player.setMediaTime(new Time(0));
        }
    }
}   

