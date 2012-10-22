package ejmf.examples.basics;

import java.applet.Applet;
import java.io.File;
import java.net.URL;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.Player;
import javax.media.Time;
import javax.swing.JFrame;

/**
* Illustrates looping audio. Media time is reset and
* Controller is restarted.
*/
public class JMFAudioLoop extends Applet {
    private Player 	player;
    private String 	media;

    public static void main(String args[]) {
    	Player player;

	// This is little trick simply to keep
	// the application running indefinitely.
	JFrame f = new JFrame();

        if( args.length != 1 ) {
            System.err.println("Usage:");
            System.err.println("java ejmf.examples.basics.JMFAudioLoop <Media>");
            return;
        }

        String media = args[0];

	try {
	    URL url = new URL("file:///" +  new File(media).getCanonicalPath());
	    player = Manager.createPlayer(url);
	    player.addControllerListener(new LoopListener());
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
            player.addControllerListener(new LoopListener());
        }
	catch (Exception e) {
	    e.printStackTrace();
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

    class LoopListener implements ControllerListener {

	public void controllerUpdate(ControllerEvent event) {
	    Player p = (Player)event.getSourceController();
	    if (event instanceof EndOfMediaEvent) {
	    	p.setMediaTime(new Time(0));
	    	p.start();
            }
	}
    }
