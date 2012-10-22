package ejmf.examples.xsync;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.IncompatibleTimeBaseException;
import javax.media.Manager;
import javax.media.Player;
import javax.media.Time;
import javax.media.TimeBase;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ejmf.toolkit.gui.controlpanel.AbstractControlPanel;
import ejmf.toolkit.gui.controlpanel.StandardControlPanel;
import ejmf.toolkit.util.StateWaiter;
import ejmf.toolkit.util.Utility;

/**
* Explicit synchronization of multiple Players
* using <tt>syncStart</tt>
*/

public class XSync {

    private static Player[]		players;
    private static JFrame		frame;
    private static int			stopped;
    private static StandardControlPanel	cp;

    public static void main(String args[]) {
        if( args.length == 0 ) {
            System.out.println("Specify at least one media URL/file");
  	    return;
        }
       
        Vector v = new Vector();

        //  Create a Vector of Players
        for(int i = 0; i < args.length; i++) {
            try {
                Player p = Manager.createPlayer(
                    Utility.appArgToMediaLocator(args[i]) );
                new StateWaiter(p).blockingRealize();
                v.addElement(p);
            } catch(Exception e) {
                System.out.println( "Could not create Player for " + args[i]);
		e.printStackTrace();
            }
        }

        //  Choose a Master
        players = new Player[ v.size() ];
        v.copyInto(players);
	new XSync(players);
    }

    public XSync(Player[] players) {
    	XSync.players = players;
        int master = 0;

	// Master TimeBase
	TimeBase masterTB = players[master].getTimeBase();

	// Determine a max latency.
	double latency = Utility.getMaximumLatency(players).getSeconds();

        // Add each Player to the Master
	// Prefetch each Player
        for(int i = 0; i < players.length; i++) {
            if( players[i] != players[master] ) {
                try {
                    players[i].setTimeBase(masterTB);
                } catch(IncompatibleTimeBaseException e) {
                    System.out.println( "Incompatible TimeBase, skipping..." );
                }
            }
	    new StateWaiter(players[i]).blockingPrefetch();
        }

        //  Show each Player's Visual Component
        for(int i = 0; i < players.length; i++) {
            frame = new JFrame();
            frame.getContentPane().setLayout( new BorderLayout() );

            Component vis =
                players[i].getVisualComponent();

            if( vis != null ) {
                frame.getContentPane().add(vis, BorderLayout.CENTER);
            }

            if( i == master ) {
                frame.setTitle("Master Player");
	        frame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			System.exit(0);
		    }
		});

		cp = new StandardControlPanel(players[i],
				AbstractControlPanel.USE_START_CONTROL);
		cp.setStartButton( 
			cp.getStartButton(),
			new StartListener(players, latency));
                frame.getContentPane().add(cp, BorderLayout.SOUTH);
            } else {
                frame.setTitle("Managed Controller");
            }

	    players[i].addControllerListener(new XSListener());

            if( frame.getComponentCount() != 0 ) {
		Runnable r = new Runnable() {
		    public void run() {
             	        frame.pack();
                        frame.setVisible(true);
		    }
		};
		SwingUtilities.invokeLater(r);
            }
        }
    }

    class XSListener implements ControllerListener {

        public void controllerUpdate(ControllerEvent event) {
	    Controller c = event.getSourceController();
	    if (event instanceof EndOfMediaEvent) {
	        stopped++;
	        c.setMediaTime(new Time(0));
	        if (stopped == players.length) {
		    stopped = 0;
	        }
	    }
        }
    }
}

class StartListener implements ActionListener {
    private Player[]		players;
    private double		latency;
    private static final double FUDGE 	= 0.1;

    public StartListener(Player[] players, double latency) {
	this.players = players;
	this.latency = latency;
    }

    public void actionPerformed(ActionEvent event) {
	((AbstractButton)event.getSource()).setEnabled(false);
	double now = players[0].getTimeBase().getTime().getSeconds();
	for (int j = 0; j < players.length; j++) {
	    players[j].syncStart(new Time(now + latency + FUDGE));
	}
    }
}
