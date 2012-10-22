package ejmf.examples.timerplayer;

import java.awt.Component;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Player;
import javax.media.Time;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ejmf.toolkit.controls.TimeDisplayControl;
import ejmf.toolkit.util.PlayerDriver;
import ejmf.toolkit.util.PlayerPanel;
import ejmf.toolkit.util.StateWaiter;

/**
*   	This example implements a media player and reports the
*    	playing time of that player. Media time is reported
*	using a TimeDisplayControl
*/

public class TimerPlayer extends PlayerDriver
    			implements ControllerListener {

    private Player	player;
    private PlayerPanel playerpanel;

    public static void main(String[] args) {
	main(new TimerPlayer(), args);
    }

    public void begin() {
        playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();

        player.addControllerListener(this);

	StateWaiter waiter = new StateWaiter(player);

	waiter.blockingRealize();


        Runnable r = new Runnable() {
            public void run() {
                // Add Control Panel Component
                playerpanel.addControlComponent();

                // Add Visual Component
                playerpanel.addVisualComponent();
                redraw();

		TimeDisplayControl tdc = new TimeDisplayControl(player);
		Component c = tdc.getControlComponent();
		JFrame f = new JFrame("Time Display Control");
		f.getContentPane().add(c);
		f.pack();
		f.setVisible(true);

            }
        };
        SwingUtilities.invokeLater(r);

	waiter.blockingPrefetch();
        // Start Player
        player.start();

    }
    
    public synchronized void controllerUpdate(ControllerEvent event) {
        // If we're getting messages from a dead player, just leave
        if (player == null) return;

        if (event instanceof EndOfMediaEvent) {
            player.setMediaTime(new Time(0));
        }
    }
}
