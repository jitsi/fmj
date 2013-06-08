package net.sf.fmj.apps.applet;

import javax.media.*;
import javax.swing.*;

import net.sf.fmj.ejmf.toolkit.util.*;
import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * Media playback applet. Based on EJMF GenericPlayer.
 *
 * @author Ken Larson
 *
 */
public class FmjApplet extends PlayerDriver implements ControllerListener
{
    public static void main(String args[])
    {
        main(new FmjApplet(), args);
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

    @Override
    public void init()
    {
        RegistryDefaults.setDefaultFlags(RegistryDefaults.FMJ);

        if (!ClasspathChecker.checkManagerImplementation())
        { // JMF manager is in charge, we need to wipe out anything in its
          // registry, and register ours.
          // let's get rid of any JMF entries in registry.
            RegistryDefaults.unRegisterAll(RegistryDefaults.JMF
                    | RegistryDefaults.FMJ_NATIVE
                    | RegistryDefaults.THIRD_PARTY);
            RegistryDefaults.registerAll(RegistryDefaults.getDefaultFlags());
        }

        super.init();
    }
}
