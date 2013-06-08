package net.sf.fmj.media.datasink.render;

import java.awt.event.*;
import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;
import javax.swing.*;

import net.sf.fmj.ejmf.toolkit.util.*;
import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * DataSink that creates a player and renders. Really only for testing.
 *
 * @author Ken Larson
 *
 */
public class Handler extends AbstractDataSink
{
    private static final Logger logger = LoggerSingleton.logger;

    private DataSource source;

    // TODO: additional listener notifications?

    private Player player;

    public void close()
    {
        try
        {
            stop();
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }

        // TODO: disconnect source?
    }

    public String getContentType()
    {
        // TODO: do we get this from the source, or the outputLocator?
        if (source != null)
            return source.getContentType();
        else
            return null;
    }

    public Object getControl(String controlType)
    {
        logger.warning("TODO: getControl " + controlType);
        return null;
    }

    public Object[] getControls()
    {
        logger.warning("TODO: getControls");
        return new Object[0];
    }

    public void open() throws IOException, SecurityException
    {
        // source.connect(); // Manager/player will take care of this.

        try
        {
            player = Manager.createRealizedPlayer(source);
        } catch (NoPlayerException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new IOException("" + e);
        } catch (CannotRealizeException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new IOException("" + e);
        }

        // TODO: GUI

    }

    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        this.source = source;
    }

    public void start() throws IOException
    {
        // no need to open GUI for just audio.

        // create GUI frame, add player's GUI components to it:
        if (player.getVisualComponent() != null)
        {
            final PlayerPanel playerpanel;
            try
            {
                playerpanel = new PlayerPanel(player);
            } catch (NoPlayerException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                throw new IOException("" + e);
            }

            // already realized so this will work:
            // playerpanel.addControlComponent(); // no need for control
            // component
            playerpanel.addVisualComponent();

            final JFrame frame = new JFrame("Renderer");

            // exit on close:
            // Allow window to close
            frame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    // TODO: close player?
                    // System.exit(0);
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

            // finish constructing window, and open it
            frame.getContentPane().add(playerpanel);

            frame.pack();
            frame.setVisible(true);
        }

        player.start();
    }

    public void stop() throws IOException
    {
        if (player != null)
            player.stop();

    }

}
