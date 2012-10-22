package ejmf.examples.blockingplayer;

import java.lang.reflect.InvocationTargetException;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Player;
import javax.media.Time;
import javax.swing.SwingUtilities;

import ejmf.toolkit.util.PlayerDriver;
import ejmf.toolkit.util.PlayerPanel;
import ejmf.toolkit.util.StateWaiter;


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
public class BlockingPlayer extends PlayerDriver
    implements ControllerListener
{
    private PlayerPanel playerpanel;
    private Player player;

    public static void main(String args[]) {
        main(new BlockingPlayer(), args);
    }

    public void begin() {
        playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();

        // Add ourselves as a listener to the player's events
        player.addControllerListener(this);

        StateWaiter waiter = new StateWaiter(player);

        // Wait for the Player to realize, then add the components
        if( ! waiter.blockingRealize() ) {
            System.err.println( "Could not realize Player" );
            return;
        }

        // Run Swing code on Swing Event thread
        Runnable r = new Runnable() {
            public void run() {
                // Add Control Panel Component
                playerpanel.addControlComponent();

                // Add Visual Component
                playerpanel.addVisualComponent();
            }
        };

        try {
            SwingUtilities.invokeAndWait(r);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if( ! waiter.blockingPrefetch() ) {
            System.err.println( "Could not prefetch Player" );
            return;
        }

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
    public void controllerUpdate(ControllerEvent event) {
        if (event instanceof EndOfMediaEvent) {
            // End of the media -- rewind
            player.setMediaTime(new Time(0));
        }
    }
}
