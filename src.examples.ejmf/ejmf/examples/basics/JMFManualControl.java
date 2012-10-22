package ejmf.examples.basics;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.media.Manager;
import javax.media.Player;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
* Illustrates programmatic control over starting
* Player. Control panel is not used.
*
* Note: Perhaps this class is misnamed. :(
*/

public class JMFManualControl extends JApplet {
    private Player 	player;
    private String 	media;
    private JPanel	playerPanel;

    public static void main(String args[]) {
    	JPanel	playerPanel;
    	Player 	player;

        if( args.length != 1 ) {
            System.err.println("Usage:");
            System.err.println("java ejmf.examples.basics.JMFManualControl <Media>");
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
	    player.addControllerListener(new AudioControlListener(playerPanel));
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
	playerPanel = new JPanel();
	playerPanel.setLayout(new BorderLayout());
	getContentPane().add(playerPanel);

        // Get the media filename
        if((media = getParameter("MEDIA")) == null) {
            System.err.println("Invalid MEDIA file parameter");
            return;
        }
	try {
	    URL url = new URL(getCodeBase(), media);
	    player = Manager.createPlayer(url);
            player.addControllerListener(new AudioControlListener(playerPanel));
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

