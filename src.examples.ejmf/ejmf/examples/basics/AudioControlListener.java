package ejmf.examples.basics;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
* Example of ControllerListener that rewinds player.
*/

public class AudioControlListener implements ControllerListener {
	private JPanel panel;
	private Component cpc;

	public AudioControlListener(JPanel panel) {
	    this.panel = panel;
        }

	public void controllerUpdate(ControllerEvent event) {
	    Player p = (Player) event.getSourceController();
	    if (event instanceof EndOfMediaEvent) {
	    	    p.setMediaTime(new Time(0));
            }
	    else if (event instanceof RealizeCompleteEvent) {
		cpc = p.getControlPanelComponent();
		if (cpc != null)
	  	    SwingUtilities.invokeLater(new AddComponentsThread(cpc));
	    }
	}

	class AddComponentsThread implements Runnable {
	    private Component c;

	    public AddComponentsThread(Component component) {
	   	this.c = component;
	    }
	    public void run() {
	        panel.add(c, BorderLayout.CENTER);
		panel.validate();
	    }
	}
    }
