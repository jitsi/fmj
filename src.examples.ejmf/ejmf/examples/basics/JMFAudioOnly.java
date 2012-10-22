package ejmf.examples.basics;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JFrame;

/**
* Illustrates an audio only Player
*/

public class JMFAudioOnly extends Applet {
    private Player	player;
    private String	media;

    public static void main(String args[]) {
	Player	player;
	// This is little trick simply to keep
	// the application running indefinitely.
	JFrame f = new JFrame();

        if( args.length != 1 ) {
            System.err.println("Usage:");
            System.err.println("java ejmf.examples.basics.JMFAudioOnly <Media>");
            return;
        }

        String media = args[0];
	try {
	    URL url = new URL("file:///" +  new File(media).getCanonicalPath());
	    player = Manager.createPlayer(url);

            player.start();
        }
	catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * This method is run when PlayerDriver is an applet.
     */
    public void init() {

        // Get the media filename
        if((media = getParameter("MEDIA")) == null) {
            System.err.println("Invalid MEDIA file parameter");
            return;
        }
	try {
	    URL url = new URL(getCodeBase(), media);
	    player = Manager.createPlayer(url);
        } catch (NoPlayerException e) {
	    System.out.println("Could not create player");
	} catch (MalformedURLException mfe) {
	    System.out.println("Bad URL");
	} catch (IOException ioe) {
	    System.out.println("IO error creating player");
	}
    }

    public void start() {
            player.start();
    }

    public void destroy() {
	player.stop();
	player.close();
    }
}
