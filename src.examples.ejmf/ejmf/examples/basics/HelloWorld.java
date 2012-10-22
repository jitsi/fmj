package ejmf.examples.basics;

//import java.awt.Frame;
import java.applet.Applet;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A class used to display a PlayerPanel as an applet or as an
 * application.  If run as an applet, the referring html document
 * should define two applet paraeters:
 * <p><p>
 * 1.  MEDIA -- the media url or file to play
 * <p><p>
 * If run as an application, the PlayerPanel and media file/url should be given as arguments.
 * <p><p>
 * Example file (when run as an applet):
 * <p>
 * <blockquote><pre>
 * < applet code=ejmf.toolkit.util.PlayerDriver.class width=570 height=570>
 * < param name=PLAYERPANEL value=com.MyCompany.jmf.MyPlayerPanel>
 * < param name=MEDIA value=xmas.avi>
 * < /applet>
 * </pre></blockquote>
 * Example command-line invocation (when run as an application):
 * <p>
 * <blockquote><pre>
 * java ejmf.examples.genericplayer.PlayerDriver com.MyCompany.jmf.MyPlayerPanel xmas.avi
 * </pre></blockquote>
 * If the given class does not descend from PlayerPanel, or if
 * the given class cannot be instantiated with a single URL as
 * an argument, an error message is printed and the
 * HelloWorld returns immediately.
 *
 */
public class HelloWorld extends Applet {

    /**
     * This method is run when PlayerDriver is an application.
     * Argument should be a media url or file argument
     *
     * @param          args[]
     */
    public static void main(String args[]) {
	Player	player;

        if( args.length != 1 ) {
            System.err.println("Usage:");
            System.err.println("java ejmf.examples.basics.HelloWorld <Media>");
            return;
        }

        String media = args[0];

        JFrame f = new JFrame(media);

        //  Allow window to close
        f.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            }
        );

        JPanel playerpanel = new JPanel();

	try {
	    URL url = new URL("file://" +  new File(media).getCanonicalPath());
	    player = Manager.createRealizedPlayer(url);

            f.getContentPane().add(playerpanel);
            f.pack();
            f.setVisible(true);

            player.start();
        }
	catch (MalformedURLException mfe) {
	    System.out.println("Bad URL");
	}
	catch (IOException ioe) {
	    System.out.println("IO Error");
	}
	catch (NoPlayerException npe) {
	    System.out.println("No player");
	} catch (CannotRealizeException e)
	{
		e.printStackTrace();
	} 
    }

    /**
     * This method is run when PlayerDriver is an applet.
     */
    public void init() {
        String 	media;
	Player	player;

        // Get the media filename
        if((media = getParameter("MEDIA")) == null) {
            System.err.println("Invalid MEDIA file parameter");
            return;
        }

        JFrame f = new JFrame(media);
        JPanel playerpanel = new JPanel();

	try {
	    URL url = new URL("file:///" + new File(media).getCanonicalPath());
	    player = Manager.createRealizedPlayer(url);
            add(playerpanel);
            player.start();
        }
	catch (MalformedURLException mfe) {
	    System.out.println("Bad URL");
	}
	catch (IOException ioe) {
	    System.out.println("IO Error");
	}
	catch (NoPlayerException npe) {
	    System.out.println("No player");
	} catch (CannotRealizeException e)
	{
		e.printStackTrace();
	} 
    }
}
