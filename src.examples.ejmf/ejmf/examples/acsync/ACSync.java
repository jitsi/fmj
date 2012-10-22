package ejmf.examples.acsync;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
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
import javax.swing.JFrame;

import ejmf.toolkit.util.StateWaiter;
import ejmf.toolkit.util.Utility;

/**
* Synchronization of multiple Players using
* addController method.
*/

public class ACSync {

    public static void main(String args[]) {
        if( args.length == 0 ) {
            System.out.println("Specify at least one media URL/file");
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
            }
        }

        //  Choose a Master
        Player[] players = new Player[ v.size() ];
        v.copyInto(players);
        int master = Utility.pickAMaster(players);

        //  Add each Player to the Master
        for(int i = 0; i < players.length; i++) {
            if( players[i] != players[master] ) {
                try {
                    players[master].addController(players[i]);
                } catch(IncompatibleTimeBaseException e) {
                    System.out.println( "Incompatible TimeBase, skipping..." );
                }
            }
        }

	players[master].addControllerListener(new PlayerListener());

        //  Show each Player's Visual Component
        for(int i = 0; i < players.length; i++) {
            JFrame f = new JFrame();
	    Container pane = f.getContentPane();

            pane.setLayout( new BorderLayout() );

            Component vis =
                players[i].getVisualComponent();

            if ( vis != null ) {
                pane.add(vis, BorderLayout.CENTER);
            }

            if( i == master ) {
                f.setTitle("Master Player");
		f.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			System.exit(0);
		    }
                });

                Component cont =
                    players[i].getControlPanelComponent();

                if( cont != null ) {
                    pane.add(cont, BorderLayout.SOUTH);
                }
            } else {
                f.setTitle("Managed Controller");
            }

            if( f.getComponentCount() != 0 ) {
                f.pack();
                f.setVisible(true);
            }
        }
    }
}

class PlayerListener implements ControllerListener {

    public void controllerUpdate(ControllerEvent event) {
	Controller c = event.getSourceController();
	if (event instanceof EndOfMediaEvent) {
	    c.setMediaTime(new Time(0.0));
	}
    }
}
