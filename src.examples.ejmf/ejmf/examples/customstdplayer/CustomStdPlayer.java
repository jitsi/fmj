package ejmf.examples.customstdplayer;

import java.awt.Component;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Player;
import javax.media.Time;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import ejmf.toolkit.gui.controlpanel.AbstractControlPanel;
import ejmf.toolkit.gui.controlpanel.StandardControlPanel;
import ejmf.toolkit.install.PackageUtility;
import ejmf.toolkit.util.PlayerDriver;
import ejmf.toolkit.util.PlayerPanel;
import ejmf.toolkit.util.StateWaiter;

/**
 * CustomStdPlayer eschews the default control panel
 * defined for a player and builds its own using StandardControlPanel.
 * It builds a control panel with only start and stop buttons. 
 *
 * @see            ejmf.toolkit.util.gui.StandardControlPanel
 * @version        1.0
 * @author         Rob Gordon & Steve Talley
 */
public class CustomStdPlayer extends PlayerDriver
    implements ControllerListener {

    private StandardControlPanel 	scp;
    private Component			visualComponent;

    private PlayerPanel playerpanel;
    private Player player;


    public static void main(String[] args) {
        PackageUtility.addContentPrefix("ejmf.toolkit", false);
        main(new CustomStdPlayer(), args);
    }
    
    /**
     * Creates a player adds itself as a ControllerListener,
     * builds a StandardControlPanel containing only start/stop
     * buttons.
     *
     * The stop/start buttons are created using getImageResource.
     *
     * @param          media
     *                 URL naming a media source
     */
    public void begin() {
        playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();
	StateWaiter waiter = new StateWaiter(player);

        // Add ourselves as a listener to the player's events
        player.addControllerListener(this);

        // Wait for the Player to realize, then add the components
        if (!waiter.blockingRealize()) {
	    System.err.println("Can't realize Player");
	    return;
	}

	Runnable r = new Runnable() {
	    public void run() {

	        scp = new StandardControlPanel(player,
			AbstractControlPanel.USE_START_CONTROL |
			AbstractControlPanel.USE_STOP_CONTROL);

	        JButton startButton = new JButton("Start");
		scp.setStartButton(startButton);
		
		JButton stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
	        scp.setStopButton(stopButton);
		
	        playerpanel.addControlComponent(scp);
	        playerpanel.addVisualComponent();
		redraw();
	    }
	};
	try {
	    SwingUtilities.invokeAndWait(r);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	if (!waiter.blockingPrefetch()) {
	    System.err.println("Can't prefetch Player");
	    return;
	}
    }
	
    /**
     * This controllerUpdate function must be defined in order to
     * implement a ControllerListener interface. This 
     * function will be called whenever there is a media event
     */
    public /*synchronized */ void controllerUpdate(ControllerEvent event) {
        // If we're getting messages from a dead player, just leave
        if (player == null) return;
        

        if (event instanceof EndOfMediaEvent) {
            // End of the media -- rewind
            player.setMediaTime(new Time(0));
        }
    }
}   

