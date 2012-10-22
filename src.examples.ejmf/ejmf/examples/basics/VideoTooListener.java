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


/**
* A ControllerListener class that adds visual
* component once realized state is reached.
*/
public class VideoTooListener implements ControllerListener {
	private JPanel panel;
	private Component cpc, vc;

	public VideoTooListener(JPanel panel) {
	    this.panel = panel;
        }

	public void controllerUpdate(ControllerEvent event) {
	    Player player = (Player) event.getSourceController();
	    if (event instanceof EndOfMediaEvent) {
	    	    player.setMediaTime(new Time(0));
            }
	    else if (event instanceof RealizeCompleteEvent) {
		SwingUtilities.invokeLater(new AddComponentsThread(player));
	    }
	}
  
        class AddComponentsThread implements Runnable {
	    private Player player;

	    public AddComponentsThread(Player player) {
		this.player = player;
	    }

	    public void run() {
	        cpc = player.getControlPanelComponent();
		if (cpc != null)
	            panel.add(cpc, BorderLayout.SOUTH);

    		vc = player.getVisualComponent();
		if (vc != null)
		    panel.add(vc, BorderLayout.NORTH);
	
		panel.validate();
	    }
	}
    }
