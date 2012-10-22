package ejmf.examples.mixapplet;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JApplet;

import ejmf.toolkit.gui.NPlayerPanel;
import ejmf.toolkit.multiplayer.MultiPlayer;
import ejmf.toolkit.multiplayer.MultiPlayerListener;
import ejmf.toolkit.multiplayer.TimerMultiPlayerControl;
import ejmf.toolkit.multiplayer.TrackList;
import ejmf.toolkit.util.Utility;

public class MPApplet extends JApplet 
		implements MultiPlayerListener, ContainerListener {
    private MultiPlayer mp;

    public void init() {
	boolean loop;
	String[] rawmix = Utility.vectorizeParameter(this, "TRACKDATA");
	TrackList trackList = TrackList.parseTrackData(rawmix);

	if (trackList.getNumberOfTracks() > 0) {
	    addContainerListener(this);
	    mp = new MultiPlayer(trackList,
	    		new TimerMultiPlayerControl(trackList));

	    String ls;
	    if (((ls = getParameter("LOOP")) != null) && 
		Boolean.valueOf(ls).booleanValue() == true) {
	        mp.addMultiPlayerListener(this);
	    }

	    NPlayerPanel npp = new NPlayerPanel(mp);
	    getContentPane().add(npp);
	}
    }

    public void multiPlayerUpdate(int state) {
	if (state == MultiPlayer.STOPPED) {
	    mp.start();
	}
    }

    public void start() {
	mp.start();
    }

    public void destroy() {
	mp.stop();
	mp.close();
    }

    public void componentAdded(ContainerEvent event) {
	pack();
    }
    
    public void componentRemoved(ContainerEvent event) {
	pack();
    }

    private void pack() {
	setSize(getPreferredSize());
	validate();
    }
} 

