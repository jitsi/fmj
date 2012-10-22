package ejmf.examples.mixer;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
* Driver class for Mixer example.
*/

public class MixerMain {
	/**
	* Launch SimpleMixer
	* @param args An array of string. MixerMain looks
	* at only first argument to determine how many tracks
	* mixer will have.
	*/
    public static void main(String args[]) {
	int numberOfTracks = 4;
	JFrame f = new JFrame();

	try {
	    UIManager.setLookAndFeel(
		UIManager.getCrossPlatformLookAndFeelClassName());
	} catch (Exception e) {
	    e.printStackTrace();
        }

	// This registers the customized L&F for the slider
	// which represents a Player track.
	UIManager.put("TrackSliderUI", "ejmf.toolkit.multiplayer.TrackSliderUI");

	if (args.length == 1)	
	    numberOfTracks = Integer.parseInt(args[0]);

	SimpleMixer mixer = new SimpleMixer(numberOfTracks);
	mixer.addContainerListener(
		new SimpleMixerListener(f));
	f.setJMenuBar(mixer.getMenuBar());
	f.getContentPane().add(mixer);
	f.setResizable(false);
	f.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		    System.exit(0);
 	    }
	});
	f.pack();
	f.setTitle("EJMF Mixer");
	f.setVisible(true);
    }
}

class SimpleMixerListener implements ContainerListener {
    private JFrame	frame;

    public SimpleMixerListener(JFrame frame) {
	this.frame = frame;
    }
    
    public void componentAdded(ContainerEvent e) {
	frame.pack();
    }

    public void componentRemoved(ContainerEvent e) {
	frame.pack(); 
    }
}
