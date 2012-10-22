package ejmf.toolkit.util;

import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The PlayerDriver class provides a basis for displaying a Java
 * Media Player as either an applet or an application.
 *
 * @author     Steve Talley & Rob Gordon
 */
public abstract class PlayerDriver extends JApplet {

    private JFrame frame;
    private PlayerPanel playerpanel;

    /**
     * Constructs a PlayerDriver.
     */
    public PlayerDriver() {
        //  Swing-endorsed hack
        getRootPane().putClientProperty(
            "defeatSystemEventQueueCheck", Boolean.TRUE);
    }

    /**
     * An abstract method that should be overridden to begin the
     * playback of the media once the GUI layout has taken place.
     */
    public abstract void begin();

    /**
     * This method is run when PlayerDriver is an applet.
     */
    public void init() {
        JLabel running =
            new JLabel("Media Applet Running", JLabel.CENTER);

        running.setBorder( BorderConstants.etchedBorder );
        getContentPane().add(running);
        pack();

        String media;

        // Get the media filename
        if((media = getParameter("MEDIA")) == null) {
            System.err.println("Error: MEDIA parameter not specified");
            return;
        }

        MediaLocator locator
            = Utility.appletArgToMediaLocator(this, media);

        try {
            initialize(locator);
        }

        catch(IOException e) {
            System.err.println("Could not connect to media");
            destroy();
        }

        catch(NoPlayerException e) {
            System.err.println("Player not found for media");
            destroy();
        }
    }

    /**
     * This method is called by browser when page is left.
     * Player must close or else it will continue to render
     * even after having left the page. This can be an issue
     * with audio.
     */
    public void destroy() {
        super.destroy();
        getPlayerPanel().getPlayer().stop();
        getPlayerPanel().getPlayer().close();
    }

    /**
     * Should be called by the real main() once a PlayerDriver
     * has been constructed for the given arguments.
     */
    public static void main(PlayerDriver driver, String args[]) {
        // Get the media filename
        if(args.length == 0) {
            System.err.println("Media parameter not specified");
            return;
        }

        MediaLocator locator
            = Utility.appArgToMediaLocator(args[0]);

        try {
            driver.initialize(locator);
        }

        catch(IOException e) {
        	e.printStackTrace();
            System.err.println("Could not connect to media");
            System.exit(1);
        }

        catch(NoPlayerException e) {
        	e.printStackTrace();
            System.err.println("Player not found for media");
            System.exit(1);
        }
    }

    /**
     * Initializes the PlayerDriver with the given MediaLocator.
     *
     * @exception  IOException
     *             If an I/O error occurs while accessing the media.
     *
     * @exception  NoPlayerException
     *             If a Player cannot be created from the given
     *             MediaLocator.
     */
    public void initialize(MediaLocator locator)
        throws IOException, NoPlayerException
    {
        playerpanel =
            new PlayerPanel(locator);

        frame = new JFrame( locator.toString() );

        //  Allow window to close
        frame.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            }
        );

        //  Resize frame whenever new Component is added
        playerpanel.getMediaPanel().addContainerListener(
            new ContainerListener() {
                public void componentAdded(ContainerEvent e) {
                    frame.pack();
                }
                public void componentRemoved(ContainerEvent e) {
                    frame.pack();
                }
            }
        );

        Container c = frame.getContentPane();
        c.add(playerpanel);

        frame.pack();
        frame.setVisible(true);

        //  Execute implementation-specific functionality
        begin();
    }

    /**
     * Gets the Frame in which the media is being displayed.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Gets the PlayerPanel for this PlayerDriver.
     */
    public PlayerPanel getPlayerPanel() {
        return playerpanel;
    }

    public void pack() {
        setSize(getPreferredSize());
        validate();
    }

    /**
     * Redraws the Frame containing the media.
     */
    public void redraw() {
        frame.pack();
    }
}
