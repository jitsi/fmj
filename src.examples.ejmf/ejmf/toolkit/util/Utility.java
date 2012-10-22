package ejmf.toolkit.util;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import javax.media.Control;
import javax.media.Controller;
import javax.media.GainControl;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.Time;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Static utilites for common JMF routines
 */
public class Utility {
    private static EJMFProperties properties = new EJMFProperties();

    /**
     * Gets the jmf.properties file for the system.
     */
    public static Properties getJMFProperties() {
        Properties p = null;
        String location = properties.getProperty("jmf.properties");
        
        if( location == null ) {
            System.out.println(
                "Could not locate property \"jmf.properties\"");
            return null;
        }

        try {
            FileInputStream in = new FileInputStream(location);
            p = new Properties();
            p.load(in);
        }
        
        catch(FileNotFoundException e) {
            System.err.println(
                "Could not locate file " + location );
        }
        
        catch(SecurityException e) {
            System.err.println(
                "Could not read file " + location );
        }
        
        catch(IOException e) {
            System.err.println(
                "Could not read file " + location );
        }

        return p;
    }

    /**
     * Gets an image property from the default EJMF properties
     * table, then constructs an ImageIcon from the named
     * resource.
     * <p>
     * Example:
     * Suppose the ejmf_proprties file contained the following:
     * <p>
     * <blockquote><pre>
     *     # Directory locations
     *     imagedir=/images
     *     mediadir=/media
     * <p>
     *     # Images
     *     duke_image=duke.gif
     * </pre></blockquote>
     *
     * To load duke.gif:
     * <blockquote><pre>
     *     ImageIcon dukeIcon = Utility.getImageResource(duke_image);
     *     JLabel l = new JLabel(dukeIcon);
     *     ...
     * </pre></blockquote>
     *
     * @param      key
     *             The image property to load
     *
     * @return     An ImageIcon constructed from the named image,
     *             or null if the resource could not be located.
     */
    public static ImageIcon getImageResource(String key) {
        String imageDir = properties.getProperty("imagedir");
        String imageName = properties.getProperty(key);
        
        if( imageName == null ) {
            return null;
        }

        //  Using "/" here is required by the API
        //  and remains platform-independent
        URL url = properties.getClass().getResource(
            imageDir + "/" + imageName);

        if(url == null) {
            return null;
        }

        return new ImageIcon(url);
    }

    /**
     * Gets an media file property from the default EJMF properties
     * table and constructs a URL for the file.
     * <p>
     * Example:
     * Suppose the ejmf_proprties file contained the following:
     * <p>
     * <blockquote><pre>
     *     # Directory locations
     *     mediadir=/media
     * <p>
     *     # Media files
     *     gulp_media=gulp.wav
     * </pre></blockquote>
     *
     * To load gulp.wav:
     * <blockquote><pre>
     *     URL url = Utility.getMediaResource("gulp_media");
     *     Manager.createPlayer(url);
     *     ...
     * </pre></blockquote>
     *
     * @param      key
     *             The media file property to load
     *
     * @return     A URL representing a media file
     *             or null if the resource could not be located.
     */
    public static URL getMediaResource(String key) {
        String mediaDir = properties.getProperty("mediadir");
        String mediaName = properties.getProperty(key);
        
        if( mediaName == null ) {
            return null;
        }

        //  Using "/" here is required by the API
        //  and remains platform-independent
        URL url = properties.getClass().getResource(
            mediaDir + "/" + mediaName);

        return url;
    }

    /**
     * Converts an applet argument to a MediaLocator.
     *
     * @return     A MediaLocator for the given argument.
     */
    public static MediaLocator appletArgToMediaLocator(
        Applet applet,
        String arg)
    {
        URL url = null;

        //  Check if arg is a valid url
        try {
            return new MediaLocator(
                new URL(arg) );
        } catch( MalformedURLException e ) {}

        //  Check if arg + doc base is a valid url
        try {
            return new MediaLocator(
                new URL( applet.getDocumentBase(), arg) );
        } catch( MalformedURLException e ) {}

        return new MediaLocator(arg);
    }

    /**
     * Convert a File to a URL using file: protocol.
     *
     * @param      file
     *             The file to convert
     *
     * @return     a java.net.URL representing file named by
     *             input File argument.
     */
    public static URL fileToURL(String fileName)
        throws IOException, MalformedURLException
    {
        File file = new File(fileName);

        if( ! file.exists() ) {
            throw new IOException(
                "File " + fileName + " does not exist.");
        }

        //  Prepend file protocol + "/" and create url
        return new URL( "file:///" + file.getCanonicalPath() );
    }

    /**
     * Converts a string to a URL.  Tries first to convert the
     * string itself to a URL.  If not successful, assumes arg
     * is a file and tries to create a file URL using the full
     * path name of the specified file.  The return value of this
     * routine should be checked to ensure that a URL was
     * successfully created.
     *
     * @param          arg
     *                 the url string to convert
     * @return         a URL if successful, null otherwise
     */
    public static MediaLocator appArgToMediaLocator(String arg) {
        URL url = null;

        //  Check if arg is a valid url
        try {
            return new MediaLocator(
                new URL(arg) );
        } catch( MalformedURLException e ) {}

        // Check if it is a file name
        try {
            return new MediaLocator(
                fileToURL(arg) );
        }
        catch( MalformedURLException e ) {}
        catch( IOException e ) {}

        // Default MediaLocator
        return new MediaLocator(arg);
    }

    /**
     * Convert a Controller state to a string
     *
     * @param          state
     *                 the state to convert
     * @return         a String representing the given state
     */
    public static String stateToString(int state) {
        switch(state) {
            case Controller.Unrealized:  return "Unrealized";
            case Controller.Realizing:   return "Realizing";
            case Controller.Realized:    return "Realized";
            case Controller.Prefetching: return "Prefetching";
            case Controller.Prefetched:  return "Prefetched";
            case Controller.Started:     return "Started";
        }
        return null;
    }

     /**
      * Given an array of Controllers, calculate the maximum
      * startup latency in seconds.  If a Controller reports
      * LATENCY_UNKNOWN, its value is ignored.
      * <p>
      * All Controllers must be at least in the Realized state.
      * If a Controller is not in the realized state, its value
      * is ignored.
      * <p>
      * If all Controllers report LATENCY_UNKNOWN, return
      * LATENCY_UNKNOWN and let the caller worry about what to
      * do.
      * <p>
      * @param      controllers
      *             An array of javax.media.Controller objects
      *             all at least realized.
      * <p>
      * @return     javax.media.Time object representing the
      *             maximum startup latency across all the input
      *             Controllers.
      */
    public static Time getMaximumLatency(Controller[] controllers) { 
        Time    maxLatency = new Time(0.0);
        Time    thisTime;
        double  maxSeconds = 0.0;

        for (int i = 0; i < controllers.length; i++) {
            if (controllers[i].getState() < Controller.Realized ||
               (thisTime = controllers[i].getStartLatency()) 
                    == Controller.LATENCY_UNKNOWN)
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
      * Given a File object, return a String representing
      * the file's extension. If there is no extension, return null.
      *
      * @param file A java.io.File object
      * @return A String representing file extension
      */
    public static String getExtension(File f) {
        return getExtension(f.getName());
    }

    /**
      * Given a String representing a file name, return a String representing
      * the file's extension. If there is no extension, return null.
      *
      * @param filename A filename as a java.lang.String object
      * @return A String representing file extension
      */
    public static String getExtension(String filename) {
        String ext = null;
        int i = filename.lastIndexOf('.');
        if (i > 0 &&  i < filename.length() - 1) {
            ext = filename.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /**
     * Displays each Control not currently being shown.
     */
    public static void showControls(Controller controller) {

        // Show all Controls not yet being shown
        Control[] controls = controller.getControls();

        for(int i = 0; i < controls.length; i++) {
            Component c =
                controls[i].getControlComponent();

            if( c != null && ! c.isShowing() ) {

                JFrame frame = new JFrame(
                    controls[i].getClass().getName() );

                frame.getContentPane().add(c);
                frame.pack();
                frame.setVisible(true);
            }
        } 
    }

    /**
     * Pick a master Player for an array of Players.
     * Return first Player that has a gain control.
     * If no Player has a gain control, return 
     * 0.
     *
     * All the input Players are assumed to be realized, else
     * they will return a null gain control.
     *
     * @param players an array of Players
     * @return an index into the Player array of the Player
     * chosen as the master Player.
     *
     */
    public static int pickAMaster(Player[] players) {
        for(int i = 0; i < players.length; i++) {
            GainControl gain =
                players[i].getGainControl();

            if( gain != null &&
                gain.getControlComponent() != null )
            {
                return i;
            }
        }
        return 0;
    }

    /** 
     * Create a disabled icon using a gray filter.
     */
    public static Icon createDisabledIcon(ImageIcon imageIcon) {
        Image i =
            GrayFilter.createDisabledImage(
                imageIcon.getImage() );

        return new ImageIcon(i);
    }

   /**
    *  For use by an applet to collect into an array 
    *  multiple parameters whose names differ only by the
    *  addition of a trailing succesive integer.
    *
    *  @param app The applet whose parameters are to be read.
    *  @param name The template from which parameter name is built. 
    *  Parameters are name <tt>name0</tt>, <tt>name1</tt>, etc.
    *
    *  @return An array of java.lang.String object representing
    *  the value of a vector of applet parameters.
    *  
    */
    public static String[] vectorizeParameter(Applet app, String name) {
        String      value;
        Vector      v = new Vector();
        int i = 0;

        while ((value = app.getParameter(name + i)) != null) {
            v.addElement(value);
            i++;
        }

        String[] ret = new String[v.size()];
        v.copyInto(ret);
        return ret;
    }
}
