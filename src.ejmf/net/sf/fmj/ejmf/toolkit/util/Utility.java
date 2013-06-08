package net.sf.fmj.ejmf.toolkit.util;

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import javax.media.*;
import javax.swing.*;

/**
 * Static utilites for common JMF routines
 *
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 *
 * @author Steve Talley & Rob Gordon
 */
public class Utility
{
    /**
     * Converts a string to a URL. Tries first to convert the string itself to a
     * URL. If not successful, assumes arg is a file and tries to create a file
     * URL using the full path name of the specified file. The return value of
     * this routine should be checked to ensure that a URL was successfully
     * created.
     *
     * @param arg
     *            the url string to convert
     * @return a URL if successful, null otherwise
     */
    public static MediaLocator appArgToMediaLocator(String arg)
    {
        URL url = null;

        // Check if arg is a valid url
        try
        {
            return new MediaLocator(new URL(arg));
        } catch (MalformedURLException e)
        {
        }

        // Check if it is a file name
        try
        {
            return new MediaLocator(fileToURL(arg));
        } catch (MalformedURLException e)
        {
        } catch (IOException e)
        {
        }

        // Default MediaLocator
        return new MediaLocator(arg);
    }

    /**
     * Converts an applet argument to a MediaLocator.
     *
     * @return A MediaLocator for the given argument.
     */
    public static MediaLocator appletArgToMediaLocator(Applet applet, String arg)
    {
        URL url = null;

        // Check if arg is a valid url
        try
        {
            return new MediaLocator(new URL(arg));
        } catch (MalformedURLException e)
        {
        }

        // Check if arg + doc base is a valid url
        try
        {
            return new MediaLocator(new URL(applet.getDocumentBase(), arg));
        } catch (MalformedURLException e)
        {
        }

        return new MediaLocator(arg);
    }

    /**
     * Create a disabled icon using a gray filter.
     */
    public static Icon createDisabledIcon(ImageIcon imageIcon)
    {
        Image i = GrayFilter.createDisabledImage(imageIcon.getImage());

        return new ImageIcon(i);
    }

    /**
     * Convert a File to a URL using file: protocol.
     *
     * @param fileName
     *            The file to convert
     *
     * @return a java.net.URL representing file named by input File argument.
     */
    public static URL fileToURL(String fileName) throws IOException,
            MalformedURLException
    {
        File file = new File(fileName);

        if (!file.exists())
        {
            throw new IOException("File " + fileName + " does not exist.");
        }

        // Prepend file protocol + "/" and create url
        return new URL("file:///" + file.getCanonicalPath());
    }

    /**
     * Given a File object, return a String representing the file's extension.
     * If there is no extension, return null.
     *
     * @param f
     *            A java.io.File object
     * @return A String representing file extension
     */
    public static String getExtension(File f)
    {
        return getExtension(f.getName());
    }

    /**
     * Given a String representing a file name, return a String representing the
     * file's extension. If there is no extension, return null.
     *
     * @param filename
     *            A filename as a java.lang.String object
     * @return A String representing file extension
     */
    public static String getExtension(String filename)
    {
        String ext = null;
        int i = filename.lastIndexOf('.');
        if (i > 0 && i < filename.length() - 1)
        {
            ext = filename.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Given an array of Controllers, calculate the maximum startup latency in
     * seconds. If a Controller reports LATENCY_UNKNOWN, its value is ignored.
     * <p>
     * All Controllers must be at least in the Realized state. If a Controller
     * is not in the realized state, its value is ignored.
     * <p>
     * If all Controllers report LATENCY_UNKNOWN, return LATENCY_UNKNOWN and let
     * the caller worry about what to do.
     * <p>
     *
     * @param controllers
     *            An array of javax.media.Controller objects all at least
     *            realized.
     *            <p>
     * @return javax.media.Time object representing the maximum startup latency
     *         across all the input Controllers.
     */
    public static Time getMaximumLatency(Controller[] controllers)
    {
        Time maxLatency = new Time(0.0);
        Time thisTime;
        double maxSeconds = 0.0;

        for (int i = 0; i < controllers.length; i++)
        {
            if (controllers[i].getState() < Controller.Realized
                    || (thisTime = controllers[i].getStartLatency()) == Controller.LATENCY_UNKNOWN)
            {
                continue;
            }

            double thisSeconds = thisTime.getSeconds();
            if (thisSeconds > maxSeconds)
                maxLatency = thisTime;
        }
        return maxLatency;
    }

    /**
     * Pick a master Player for an array of Players. Return first Player that
     * has a gain control. If no Player has a gain control, return 0.
     *
     * All the input Players are assumed to be realized, else they will return a
     * null gain control.
     *
     * @param players
     *            an array of Players
     * @return an index into the Player array of the Player chosen as the master
     *         Player.
     *
     */
    public static int pickAMaster(Player[] players)
    {
        for (int i = 0; i < players.length; i++)
        {
            GainControl gain = players[i].getGainControl();

            if (gain != null && gain.getControlComponent() != null)
            {
                return i;
            }
        }
        return 0;
    }

    /**
     * Displays each Control not currently being shown.
     */
    public static void showControls(Controller controller)
    {
        // Show all Controls not yet being shown
        Control[] controls = controller.getControls();

        for (int i = 0; i < controls.length; i++)
        {
            Component c = controls[i].getControlComponent();

            if (c != null && !c.isShowing())
            {
                JFrame frame = new JFrame(controls[i].getClass().getName());

                frame.getContentPane().add(c);
                frame.pack();
                frame.setVisible(true);
            }
        }
    }

    /**
     * Convert a Controller state to a string
     *
     * @param state
     *            the state to convert
     * @return a String representing the given state
     */
    public static String stateToString(int state)
    {
        switch (state)
        {
        case Controller.Unrealized:
            return "Unrealized";
        case Controller.Realizing:
            return "Realizing";
        case Controller.Realized:
            return "Realized";
        case Controller.Prefetching:
            return "Prefetching";
        case Controller.Prefetched:
            return "Prefetched";
        case Controller.Started:
            return "Started";
        }
        return null;
    }

}
