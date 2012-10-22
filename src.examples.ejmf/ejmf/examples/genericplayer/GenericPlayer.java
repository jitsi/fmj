package ejmf.examples.genericplayer;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.swing.SwingUtilities;

import ejmf.toolkit.install.PackageUtility;
import ejmf.toolkit.util.PlayerDriver;
import ejmf.toolkit.util.PlayerPanel;

/**
 * This example implements a generic media player.  It will
 * initially display a message indicating that the media is
 * loading.  When the player has been realized, the media
 * components are added and the player is started.
 *
 * @see            ejmf.toolkit.util.PlayerPanel
 */
public class GenericPlayer extends PlayerDriver
    implements ControllerListener
{
    private PlayerPanel playerpanel;
    private Player player;

    public static void main(String args[]) {
        PackageUtility.addContentPrefix("ejmf.toolkit", false);
    	main(new GenericPlayer(), args);
    }

    public void begin() {
        playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();

        // Add ourselves as a listener to the player's events
        player.addControllerListener(this);

        // Start Player
        player.start();
    }

    /**
     * This controllerUpdate function must be defined in order to
     * implement a ControllerListener interface. This 
     * function will be called whenever there is a media event.
     *
     * @param          event
     *                 the media event
     */
    public synchronized void controllerUpdate(ControllerEvent event) {

        if( event instanceof RealizeCompleteEvent ) {

            Runnable r = new Runnable() {
                public void run() {
                    // Add Control Panel Component
                    playerpanel.addControlComponent();

                    // Add Visual Component
                    playerpanel.addVisualComponent();
                }
            };

            SwingUtilities.invokeLater(r);
        } else

        if (event instanceof EndOfMediaEvent) {
            // End of the media -- rewind
            player.setMediaTime(new Time(0));
        }
    }
}   
