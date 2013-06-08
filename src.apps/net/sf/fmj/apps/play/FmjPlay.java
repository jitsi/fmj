package net.sf.fmj.apps.play;

import javax.media.*;
import javax.swing.*;

import net.sf.fmj.ejmf.toolkit.util.*;
import net.sf.fmj.utility.*;

/**
 * A simple FMJ player application. Does not contain the large number of
 * features that FMJ studio has. Based on EJMF GenericPlayer.
 *
 * @author Ken Larson
 *
 */
public class FmjPlay extends PlayerDriver implements ControllerListener
{
    public static void main(String args[])
    {
        FmjStartup.init();

        main(new FmjPlay(), args);
    }

    private PlayerPanel playerpanel;

    private Player player;

    @Override
    public void begin()
    {
        playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();

        // Add ourselves as a listener to the player's events
        player.addControllerListener(this);

        // Start Player
        player.start();
    }

    /**
     * This controllerUpdate function must be defined in order to implement a
     * ControllerListener interface. This function will be called whenever there
     * is a media event.
     *
     * @param event
     *            the media event
     */
    public synchronized void controllerUpdate(ControllerEvent event)
    {
        if (event instanceof RealizeCompleteEvent)
        {
            Runnable r = new Runnable()
            {
                public void run()
                {
                    // Add Control Panel Component
                    playerpanel.addControlComponent();

                    // Add Visual Component
                    playerpanel.addVisualComponent();
                }
            };

            SwingUtilities.invokeLater(r);
        } else

        if (event instanceof EndOfMediaEvent)
        {
            // End of the media -- rewind
            player.setMediaTime(new Time(0));
        }
    }
}
