package ejmf.examples.showcontrols;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Player;
import javax.media.Time;
import javax.swing.SwingUtilities;

import ejmf.toolkit.install.PackageUtility;
import ejmf.toolkit.util.PlayerDriver;
import ejmf.toolkit.util.PlayerPanel;
import ejmf.toolkit.util.StateWaiter;
import ejmf.toolkit.util.Utility;

/**
 * This example differs from GenericPlayer.java in that the
 * PlayerPanel will be sized for the media player's
 * visualComponent and controlComponent <b>before<\b> it is shown for
 * the first time.  It does this by calling blockingRealize()
 * before completing its initial GUI layout in the
 * constructor.
 *
 * @see            ejmf.toolkit.util.PlayerPanel
 * @see            ejmf.toolkit.genericplayer.GenericPlayer
 */
public class ShowControls extends PlayerDriver
    implements ControllerListener
{
    private PlayerPanel playerpanel;
    private Player player;

    public static void main(String args[]) {
        PackageUtility.addContentPrefix("ejmf.toolkit", false);
        
        main(new ShowControls(), args);
    }

    public void begin() {
        playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();

        // Add ourselves as a listener to the player's events
        player.addControllerListener(this);

        StateWaiter waiter = new StateWaiter(player);

        // Wait for the Player to realize, then add the components
        waiter.blockingRealize();

        // Run Swing code on Swing Event thread
        Runnable r = new Runnable() {
            public void run() {
                // Add Control Panel Component
                playerpanel.addControlComponent();

                // Add Visual Component
                playerpanel.addVisualComponent();

                // Show all Controls not yet being shown
                Utility.showControls(player);
            }
        };

        SwingUtilities.invokeLater(r);

        // Start Player
        player.start();

        getFrame().setTitle("ejmf.miv");
    }

    /**
     * This controllerUpdate function must be defined in order to
     * implement a ControllerListener interface. This 
     * function will be called whenever there is a media event.
     *
     * @param          event
     *                 the media event
     */
    public void controllerUpdate(ControllerEvent event) {
        if (event instanceof EndOfMediaEvent) {
            // End of the media -- rewind
            player.setMediaTime(new Time(0));
        }
    }
}
