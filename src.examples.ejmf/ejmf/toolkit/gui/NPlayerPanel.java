package ejmf.toolkit.gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.io.IOException;
import java.util.Vector;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ejmf.toolkit.multiplayer.MultiPlayer;
import ejmf.toolkit.multiplayer.Track;
import ejmf.toolkit.multiplayer.TrackList;
import ejmf.toolkit.util.MixFileData;
import ejmf.toolkit.util.MixTrackData;
import ejmf.toolkit.util.StateWaiter;
import ejmf.toolkit.util.Utility;

/**
*	Display the visual components from multiple Players
* 	from one of the following sources:
* <ul>
* <li>MultiPlayer
* <li>MixFileData
* <li>An array of Players
* <li>An array of MediaLocators
* <li>An array of Strings
* <li>A single MediaLocator and a count of how many
* times media is to be displayed.
* </ul>
*
* The Player's media is displayed within a JPanel using
* a GridLayout as the default. The grid is made as square as possible.
* To change the layout manager, override the <tt>createLayout</tt>
* method.
*
* Each visual component is bordered by a etched and titled border.
* To change the border drawn around the media, override the <tt>
* createBorder</tt> method.
*
*/

public class NPlayerPanel extends JPanel {
    private Player[]		players;
    private TrackList 		tracks;

	/** 
	* Create an NPlayerPanel from a array of MediaLocators.
	* The MediaLocators are used to construct the Players.
	*
	* @param mls An array of MediaLocators.
	* @exception NoPlayerException if Player can not be constructed.
	* @exception IOException if DataSource can not be connected to.
	*/ 
    public NPlayerPanel(MediaLocator[] mls)
		throws NoPlayerException, IOException {
	createAndDisplayPlayers(mls);
    } 

	/** 
	* Create an NPlayerPanel from a MultiPlayer.
	* In this case, Players are already constructed.
	* Players are moved to prefetch state.
	*/
    public NPlayerPanel(MultiPlayer mp) {
	tracks = mp.getTrackList();
	Vector	v 	= new Vector();
        for (int i = 0; i < tracks.getNumberOfTracks(); i++) {
	    Track track = tracks.getTrack(i);
	    if (track.isAssigned()) {
		v.addElement(track.getPlayer());
	    }
        }

	players = new Player[v.size()];
	v.copyInto(players);

	setLayout(createLayout(players.length));
	for (int i = 0; i < players.length; i++) {
	    StateWaiter waiter = new StateWaiter(players[i]);
	    waiter.blockingRealize();
	    displayPlayer(players[i], tracks.getTrack(i).getMediaLocator());
	    waiter.blockingPrefetch();
	}
    }

	/** 
	* Create an NPlayerPanel from an array of Players.
	* In this case, Players are already constructed.	
	* Players are moved to prefetch state.
	*/
    public NPlayerPanel(Player[] players) {
	StateWaiter	waiter;

	this.players = players;
	setLayout(createLayout(players.length));
	for (int i = 0; i < players.length; i++) {
	    waiter = new StateWaiter(players[i]);
	    waiter.blockingRealize();
	    displayPlayer(players[i], null);
	    waiter.blockingPrefetch();
	}
    }


	/*	
	* Add the Player's visual component to the
	* the Panel.	
	*/
    private void displayPlayer(Player p, MediaLocator ml) {
	Component c = p.getVisualComponent();
	if (c != null) {
           add(new GridPanel(createBorder(ml), c));
	   invalidate();
	}
    }

    /**
     * Create an NPlayerPanel from a MixFileData object
     * For each element a Player is created and
     * its visual component displayed.
     * 
     * @param mixList a MixFileData object
	* @exception NoPlayerException if Player can not be constructed.
	* @exception IOException if DataSource can not be connected to.
     * @see ejmf.toolkit.util.MixTrackData
     * @see ejmf.toolkit.util.MixFileData
     */
    public NPlayerPanel(MixFileData mixList) 
		throws NoPlayerException, IOException {

	int n = mixList.getNumberOfTracks();
	MediaLocator[] mls = new MediaLocator[n];
	for (int i = 0; i < n; i++) {
	    MixTrackData d = mixList.getMixTrackData(i);
	    mls[i] = Utility.appArgToMediaLocator(d.mediaFileName);
	}
	createAndDisplayPlayers(mls);
    }

    /** 
     	* Create an NPlayerPanel from an array of media file names
     	* For each file named in the array a MediaLocator is 
	* constructed. From each MediaLocator a Player is created and
     	* its visual component displayed.
     	* 
       	* @param mediaFiles An array of Strings which 
	* @exception NoPlayerException if Player can not be constructed.
	* @exception IOException if DataSource can not be connected to.
     */
    public NPlayerPanel(String[] mediaFiles) 
		throws NoPlayerException, IOException {

	MediaLocator[] mls = new MediaLocator[mediaFiles.length];
	for (int i = 0; i < mediaFiles.length; i++) {
	    mls[i] = Utility.appArgToMediaLocator(mediaFiles[i]);
        }
	createAndDisplayPlayers(mls);
    }

    /** 
     * Create an NPlayerPanel that displays a single media
     * source multiple times.
     *
     * @param nPlayers the number of Players to create
     * @param media A MediaLocator.
	* @exception NoPlayerException if Player can not be constructed.
	* @exception IOException if DataSource can not be connected to.
     */
    public NPlayerPanel(int nPlayers, MediaLocator media) 
		throws NoPlayerException, IOException {

   	MediaLocator[] mls = new MediaLocator[nPlayers];
	for (int i = 0; i < nPlayers; i++) {
	    mls[i] = media;
        }
	createAndDisplayPlayers(mls);
    }

	/*	
        * Given an array of MediaLocators, create Players and	
	* add them to display Panel.
	*/
    private void createAndDisplayPlayers(MediaLocator[] mls) 
			throws NoPlayerException, IOException {
	StateWaiter	waiter;

	setLayout(createLayout(mls.length));

	players = new Player[mls.length];
	for (int i = 0; i < mls.length; i++) {
	    players[i] = Manager.createPlayer(mls[i]);
	    waiter = new StateWaiter(players[i]);
	    waiter.blockingRealize();

	    displayPlayer(players[i], mls[i]);

	    waiter.blockingPrefetch();
	}
    }

    /**
     *  Create an etched and titled border based on a Player MediaLocator.
     *
     *  Default implementation creates an EtchedBorder with the
     *  title displayed at top left. If no title is provided,
     *  a simple EtchedBorder is drawn.
     *
     *  Subclasses should over-ride this method to create a
     *  custom border.
     *
     * @param ml A javax.media.MediaLocator associated with a Player
     * @return a javax.swing.border.Border
     */
     protected Border createBorder(MediaLocator ml) {
	return (ml != null)  ? 
		(Border) new TitledBorder(new EtchedBorder(), ml.toString()) :
		(Border) new EtchedBorder();
     }

    /**
     * 	Create the LayoutManager for the NPlayerPanel  
     *  The default layout is a GridLayout with a 10
     *  pixel horizontal and vertical gaps.
     * 
     *  Subclasses over-ride this method to provide their
     *  layout.
     *
     *  @param n the number of Players to be dispalyed.
     *  @return java.awt.LayoutManager
     */
    protected LayoutManager createLayout(int n) {
	double rt = Math.sqrt((double) n);
	int gdim = (int) rt;
	if ((double) gdim < rt)
	    gdim++;
        
	return new GridLayout(gdim, gdim, 10, 10);
    }

	/**		
	* Return an array of Player references naming Players
	* displayed in NPlayerPanel.	
	* 
	* @return An array of Players.	
	*/
    public Player[] getPlayers() {
 	return players;
    } 

    class GridPanel extends JPanel {
        public GridPanel(Component c) {
	    setBorder(new EtchedBorder());
	    add(c);
        }
        public GridPanel(Border border, Component c) {
	    setBorder(border);
	    add(c);
        }
    }
}
