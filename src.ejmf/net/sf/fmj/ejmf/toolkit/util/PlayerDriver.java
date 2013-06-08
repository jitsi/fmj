package net.sf.fmj.ejmf.toolkit.util;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.swing.*;

import net.sf.fmj.utility.*;

/**
 * The PlayerDriver class provides a basis for displaying a Java Media Player as
 * either an applet or an application.
 *
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 *
 * @author Steve Talley & Rob Gordon
 */
public abstract class PlayerDriver extends JApplet
{
    private static final Logger logger = LoggerSingleton.logger;

    /**
     * Should be called by the real main() once a PlayerDriver has been
     * constructed for the given arguments.
     */
    public static void main(PlayerDriver driver, String args[])
    {
        // Get the media filename
        if (args.length == 0)
        {
            logger.severe("Media parameter not specified");
            return;
        }

        MediaLocator locator = Utility.appArgToMediaLocator(args[0]);

        try
        {
            driver.initialize(locator);
        }

        catch (IOException e)
        {
            logger.log(Level.WARNING, "Could not connect to media: " + e, e);
            System.exit(1);
        }

        catch (NoPlayerException e)
        {
            logger.log(Level.WARNING, "Player not found for media: " + e, e);
            System.exit(1);
        }
    }

    private JFrame frame; // only used if not running as applet

    private PlayerPanel playerpanel;

    /**
     * Constructs a PlayerDriver.
     */
    public PlayerDriver()
    {
        // Swing-endorsed hack
        getRootPane().putClientProperty("defeatSystemEventQueueCheck",
                Boolean.TRUE);
    }

    /**
     * An abstract method that should be overridden to begin the playback of the
     * media once the GUI layout has taken place.
     */
    public abstract void begin();

    /**
     * This method is called by browser when page is left. Player must close or
     * else it will continue to render even after having left the page. This can
     * be an issue with audio.
     */
    @Override
    public void destroy()
    {
        super.destroy();
        if (getPlayerPanel() != null && getPlayerPanel().getPlayer() != null)
        {
            getPlayerPanel().getPlayer().stop();
            getPlayerPanel().getPlayer().close();
        }
    }

    /**
     * Gets the Frame in which the media is being displayed.
     */
    public JFrame getFrame()
    {
        return frame;
    }

    /**
     * Gets the PlayerPanel for this PlayerDriver.
     */
    public PlayerPanel getPlayerPanel()
    {
        return playerpanel;
    }

    /**
     * This method is run when PlayerDriver is an applet.
     */
    @Override
    public void init()
    {
        // JLabel running =
        // new JLabel("Media Applet Running", JLabel.CENTER);
        //
        // running.setBorder( BorderConstants.etchedBorder );
        // getContentPane().add(running);
        // pack();

        FmjStartup.initApplet();

        String media;

        // Get the media filename
        if ((media = getParameter("MEDIA")) == null)
        {
            logger.warning("Error: MEDIA parameter not specified");
            return;
        }

        MediaLocator locator = Utility.appletArgToMediaLocator(this, media);

        try
        {
            // initialize will open a new window.
            // initialize(locator);
            playerpanel = new PlayerPanel(locator);

            // Resize whenever new Component is added
            playerpanel.getMediaPanel().addContainerListener(
                    new ContainerListener()
                    {
                        public void componentAdded(ContainerEvent e)
                        {
                            pack();
                        }

                        public void componentRemoved(ContainerEvent e)
                        {
                            pack();
                        }
                    });

            getContentPane().add(playerpanel);
            pack();
            begin();
        }

        catch (IOException e)
        {
            logger.warning("Could not connect to media");
            destroy();
        }

        catch (NoPlayerException e)
        {
            logger.warning("Player not found for media");
            destroy();
        }
    }

    /**
     * Initializes the PlayerDriver with the given MediaLocator.
     *
     * @exception IOException
     *                If an I/O error occurs while accessing the media.
     *
     * @exception NoPlayerException
     *                If a Player cannot be created from the given MediaLocator.
     */
    public void initialize(MediaLocator locator) throws IOException,
            NoPlayerException
    {
        playerpanel = new PlayerPanel(locator);

        frame = new JFrame(locator.toString());

        // Allow window to close
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        // Resize frame whenever new Component is added
        playerpanel.getMediaPanel().addContainerListener(
                new ContainerListener()
                {
                    public void componentAdded(ContainerEvent e)
                    {
                        frame.pack();
                    }

                    public void componentRemoved(ContainerEvent e)
                    {
                        frame.pack();
                    }
                });

        Container c = frame.getContentPane();
        c.add(playerpanel);

        frame.pack();
        frame.setVisible(true);

        // Execute implementation-specific functionality
        begin();
    }

    public void pack()
    {
        setSize(getPreferredSize());
        validate();
    }

    /**
     * Redraws the Frame containing the media.
     */
    public void redraw()
    {
        frame.pack();
    }
}
