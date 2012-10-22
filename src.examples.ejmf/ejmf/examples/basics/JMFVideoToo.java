package ejmf.examples.basics;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.media.Manager;
import javax.media.Player;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
* Biulds on simpler example and displays
* visual component. Note much new here except
* attention to use of SwingUtilities.
*/
public class JMFVideoToo extends Applet {

    public static void main(String args[]) {
    	JPanel	playerPanel;
        Player 	player;


        if( args.length != 1 ) {
            System.err.println("Usage:");
            System.err.println("java ejmf.examples.basics.JMFVideoToo <Media>");
            return;
        }

        String media = args[0];

	JFrame f = new JFrame(media);

	f.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent event) {
	 	System.exit(0);
	    }
	});

	playerPanel = new JPanel();
	playerPanel.setLayout(new BorderLayout());
	f.getContentPane().add(playerPanel);
	f.pack();
	f.setVisible(true);

	try {
	    URL url = new URL("file:///" +  new File(media).getCanonicalPath());
	    player = Manager.createPlayer(url);
	    player.addControllerListener(new VideoTooListener(playerPanel));
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
	Player 	player;
        String 	media;
	JPanel	playerPanel;

	playerPanel = new JPanel();
	playerPanel.setLayout(new BorderLayout());
	add(playerPanel);

        // Get the media filename
        if((media = getParameter("MEDIA")) == null) {
            System.err.println("Invalid MEDIA file parameter");
            return;
        }
	try {
	    String name = new File(media).getCanonicalPath();
	    URL url = new URL("file:///" + name);
	    player = Manager.createPlayer(url);
            player.addControllerListener(new VideoTooListener(playerPanel));
            player.start();
        }
	catch (Exception e) {
	    e.printStackTrace();
	}

    }
}

