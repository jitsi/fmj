package ejmf.toolkit.multiplayer;

import java.io.File;

import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.RestartingEvent;
import javax.media.StartEvent;
import javax.media.StopEvent;
import javax.media.TimeBase;
import javax.swing.event.EventListenerList;

import ejmf.toolkit.gui.ViewingPanel;
import ejmf.toolkit.util.Debug;
import ejmf.toolkit.util.Utility;

/** 
 * Plays multiple players. The MultiPlayerControl strategy
 * associated with the MutliPlayer determines how the Players
 * are operated.
 *
 * A MultiPlayer has a TrackList. Each member of the TrackList
 * can be thought of as a media track in the sense of an a/v
 * mixing board. Each track is a Track object and maintains
 * the Player reference, its start time and stop time.
 *
 * If a MultiPlayer has a ViewingPanel associated with it,
 * a Player's GUI components are displayed.
 *
 * A MultiPlayer can be used in conjunction with a TrackModel
 * and implements the TrackModelListener interface to listen
 * to changes to the TrackModel.
 *
 * A MultiPlayer has one of four states: NOTINITIALIZED, INITIALIZED,
 * STARTED, STOPPED.
 *
 * @see ejmf.toolkit.multiplayer.TrackList
 * @see ejmf.toolkit.multiplayer.Track
 * @see ejmf.toolkit.multiplayer.MultiPlayerControl
 */

public class MultiPlayer 
	implements TrackModelListener, ControllerListener {


	/** No control strategy associated with MultiPlayer */
    final public static int	NOTINITIALIZED = -1;
	/** Control strategy has been associated with MultiPlayer */
    final public static int	INITIALIZED = 1;
	/** At least one of associated Players is started */
    final public static int	STARTED = 2;
	/** All Players have stopped. */
    final public static int	STOPPED = 3;

    private int			state = NOTINITIALIZED;

    private TrackList		tracks;
    private TrackModel		trackModel = null;
    private int			nActiveTracks;
    private int			startedCount;
    private int			totalToStart;

    private ViewingPanel	viewingPanel = null;

    private EventListenerList	listeners = null;
	
    // This is the Player whose TimeBase is used by all others.
    private Player		master;
    private TimeBase		masterTimeBase = null;

    // Reference to control strategy
    private MultiPlayerControl	mpc;
  
    /** 
     * Create a MultiPlayer from an array of media file names.
	* @param mediaFiles An array of media file names		
	* @param mpc A MultiPlayerControl strategy. 
     */

    public MultiPlayer(String[] mediaFiles, MultiPlayerControl mpc) {
	tracks = new TrackList(mediaFiles.length);
	for (int i = 0; i < mediaFiles.length; i++) {
	    try {
	        String path = new File(mediaFiles[i]).getCanonicalPath();
	        MediaLocator ml = Utility.appArgToMediaLocator(path);
	        Player player = Manager.createPlayer(ml);
	        tracks.addElement(new Track(i, ml, player));
	    } catch (Exception e) {
		System.err.println(
		    "Unable to create Player from " + mediaFiles[i]);
		continue;
 	    }
	}
	nActiveTracks = addAsListener(tracks);
	setControlStrategy(mpc);
    } 

    /** 
     * Create a MultiPlayer from an array of MediaLocators
	* @param mls An array of MediaLocators
	* @param mpc A MultiPlayerControl strategy. 
     */
    public MultiPlayer(MediaLocator[] mls, MultiPlayerControl mpc) {
	tracks = new TrackList(mls.length);
	for (int i = 0; i < mls.length; i++) {
	    try {
	        Player player = Manager.createPlayer(mls[i]);
	        tracks.addElement(new Track(i, mls[i], player));
	    } catch (Exception e) {
		System.err.println("Unable to create Player from " + 
			mls[i].toString());
		continue;
	    }
	}
	nActiveTracks = addAsListener(tracks);
	setControlStrategy(mpc);
    }

    /** Create a MultiPlayer from a TrackModel. This
     *  constructor adds the MultiPlayer as a TrackModelListener.
     *  The MultiPlayer's TackList is obtained from the TrackModel.
	* @param tm A TrackModel
	* @param mpc A MultiPlayerControl strategy. 
     */
    public MultiPlayer(TrackModel tm, MultiPlayerControl mpc) {
	tracks = tm.getTrackList();
	tm.addTrackModelListener(this);
	trackModel = tm;
	nActiveTracks = addAsListener(tracks);
	setControlStrategy(mpc);
    }

    /** 
     * Create a MultiPlayer from a TrackList. 
	* @param tracks A TrackList
	* @param mpc A MultiPlayerControl strategy. 
     */
    public MultiPlayer(TrackList tracks, MultiPlayerControl mpc) {
	this.tracks = tracks;
	nActiveTracks = addAsListener(tracks);
	setControlStrategy(mpc);
    }
	

    /**  
      * Return the TrackList associated with MultiPlayer.
	* @return TrackList associated with this MultiPlayer.
      */
    public TrackList getTrackList() {
	return tracks;
    }

    /**
     * Associate a ViewingPanel with this MultiPlayer for
     * display of Player GUI Components.
     * @param a ejmf.toolkit.gui.ViewingPanel in which	
	* player visual components are displayed.
     */
    public void setViewingPanel(ViewingPanel viewingPanel) {
	this.viewingPanel = viewingPanel;
    }

    /**
      * Return the ViewingPanel associated with this MultiPlayer.
      *
      * @return ejmf.toolkit.gui.ViewPanel used for display of Players.
      */
    public ViewingPanel getViewingPanel() {
	return viewingPanel;
    }

    /**  
      * Get current state of MultiPlayer.
      * @return current state of MultiPlayer
      */ 
    public int getState() {
	return state;
    }

    /** 
     * Shutdown all Players.
     */
    public synchronized void close() {
	if (state < INITIALIZED)
	    return;
	if (state == STARTED)
	    mpc.stop();
	mpc.close();
    }
     
    /** Stop all Players
     *
     */
    public synchronized void stop() {
	totalToStart = 0;
	mpc.stop();
    }

    /**  
      * Rewind the MultiPlayer, settings the media time of all its
      * Player to zero.
      *
      * @exception MultiPlayerStateException 
      * If the MultiPlayer is in the STARTED state
      * or has not been initialized, a MultiPlayerStateException is thrown.
      */
    public synchronized void rewind() throws MultiPlayerStateException {
	if (state == STARTED || state == INITIALIZED)
	    throw new MultiPlayerStateException(
		"Illegal state for rewind. MultiPlayer is " + 
		stateToString(state) + ".");

	mpc.rewind();
    }

    /** 
      * Start all Players. 
     */

    public synchronized void start()  {
	// Assumption 1: All Players are prefetched
	// Assumption 2: All Players share TimeBase

	if (state == STARTED) {
	    return;
	}

	// Reset number of Tracks started.
	startedCount = 0;

	// Set total number of Tracks to be started.
	totalToStart = nActiveTracks;

	mpc.start();

	state = STARTED;
	fireMultiPlayerUpdate(STARTED);
    }

     /* Set the control strategy for this MultiPlayer.
      *  A control stategy can only be set once. 
      *  MultiPlayer update event is posted and, 
      *  upon return, the MultiPlayer state has been set to
      *  INITIALIZED.
      */ 
     private void setControlStrategy(MultiPlayerControl mpc) {

	this.mpc = mpc;
        if (mpc.init() == true) {
	    state = INITIALIZED;
	    fireMultiPlayerUpdate(INITIALIZED);
	} 
     }

    /** Display all Player ViewScreens
     * 
     */
     public void displayPlayers() {
	if (viewingPanel == null)
	    return;

        for (int i = 0; i < tracks.getNumberOfTracks(); i++) {
	    Track track = tracks.getTrack(i);
	    if (track.isAvailable())
		continue;
	    Player player = track.getPlayer();
	    String path = track.getMediaLocator().toString();
	    viewingPanel.addScreen(new File(path).getName(), 
				player, 
				ViewingPanel.DISPLAY_BOTH);
	}
     }

    ////////// Listener/notification code ////////////////

    /** Inform listeners of a state change to MultiPlayer
	* @param The current state of the MultiPlayer.
     */
    protected void fireMultiPlayerUpdate(int state) {
	if (listeners == null)
	    return;
	Object[] l = listeners.getListenerList();
	for (int i = l.length-2; i>=0; i-=2) {
	    if (l[i]== MultiPlayerListener.class)
		((MultiPlayerListener)l[i+1]).multiPlayerUpdate(state);
	}
    }

    /** Add a MultiPlayerListener
     * @param a MultiPlayerListener
     *
     * @see ejmf.toolkit.multiplayer.MultiPlayerListener
     */
    public void addMultiPlayerListener(MultiPlayerListener mpl) {
	if (listeners == null)
	    listeners = new EventListenerList();

	listeners.add(MultiPlayerListener.class, mpl);
    }

    /** Remove a MultiPlayerListener
     *
     * @see ejmf.toolkit.multiplayer.MultiPlayerListener
     */
    public void removeMultiPlayerListener(MultiPlayerListener mpl) {
	if (listeners != null)
	    listeners.remove(MultiPlayerListener.class, mpl);
    }

    /**
      * A debugging hook for dumping contents of each Track
      * to the file.dbg.out.
      */
    public void query() {
	for (int i = 0; i < tracks.getNumberOfTracks(); i++) {
	    Track track = tracks.getTrack(i);
	    Player p = track.getPlayer();

	    System.out.println("++++++++++++++++++++");

	    if (track.isAvailable()) {
		System.out.println("Track " + i +  " not assigned");
	 	continue;
            }
	    System.out.println("player = " + track.getMediaLocator().toString());
            System.out.println("state = " + Utility.stateToString(p.getState()));
            System.out.println("media time = " + p.getMediaTime().getSeconds());
            System.out.println("stop time = " + p.getStopTime().getSeconds());
            System.out.println("duration = " + p.getDuration().getSeconds());
	}
    }

    /** Listen for changes to TrackModel
     *  In response to every update, the MultiPlayerControl's
     *  update method is called with a TrackModelEvent as an argument.
     *  <p>
     *  @param tme A TrackModelEvent
     * @see ejmf.toolkit.multiplayer.TrackModelEvent
     */
    public void trackModelUpdate(TrackModelEvent tme) {
	TrackModel tm = tme.getTrackModel();
	tracks = tm.getTrackList();
	int index = tme.getIndex();
	if (tme instanceof TrackModelDeassignEvent) {
	    uninstallPlayer(tm, index);
	} else if (tme instanceof TrackModelAssignEvent) {
	    installPlayer(tm, index);
	}
	if (tm.getNumberOfAssignedTracks() > 0) {
	    mpc.update(tracks);
	}
    }

    //////////// Track management //////////////
    //  Addition and deletion of new Player
    //  The following two methods are invoked by trackModelUpdate

    /** In response to TrackModelEvent, remove a Player   
     * 	and its screen from desktop.
     * 
     *  @param tm TrackModel from which Player is being removed.
     * 	@param index Index of Player in TrackList
     *  @see ejmf.toolkit.multiplayer.TrackList
     *  @see ejmf.toolkit.multiplayer.TrackModel
     */
    private void uninstallPlayer(TrackModel tm, int index) {
	Player player = tracks.getTrack(index).getPlayer();

	if (viewingPanel != null) {
	    viewingPanel.removeScreen(player);
	    Debug.printObject("uninstallPlayer : after remove screen");
	}
   	player.removeControllerListener(this);	
	synchronized (this) {
	    nActiveTracks--;
	}
	Debug.printObject("exit uninstallPlayer : " + index);
    }

    /** In response to TrackModelEvent, add a Player.
     *  initPlayer is called to bring Player to prefetched
     *  state. If a ViewingPanel exists, the Player screen
     *  is displayed. Newly added Player adopts TimeBase
     *  of current master Player.
     *
     *  @param tm TrackModel to which Player is being added.
     *  @param index which track is getting a new Player
     *  @see javax.media.TimeBase
     *  @see ejmf.toolkit.multiplayer.TrackModelAssignEvent 
     *  @see ejmf.toolkit.multiplayer.TrackModel
     *  @see ejmf.toolkit.gui.ViewingPanel
     *  @see ejmf.toolkit.gui.ViewScreen
     *   
     */
    private void installPlayer(TrackModel tm, int index)  {
	Debug.printObject("enter installPlayer : " + index);
	Track	track = tracks.getTrack(index);
	Player 	player = track.getPlayer();

	player.addControllerListener(this);
	synchronized (this) {
	    nActiveTracks++;
	}
	if (viewingPanel != null)
	    viewingPanel.addScreen(track.getMediaLocator().toString(),
				player, 
				ViewingPanel.DISPLAY_BOTH);
	Debug.printObject("exit installPlayer : " + index);
    }

    /** Track number of started Players, number of stopped
     * Players and fire a MultiPlayerUpdate event when all tracks
     * have been stopped or an error has occurred.
     * 
     * @param event A ControllerEvent 
     */
    public synchronized void controllerUpdate(ControllerEvent event) {
	Player p = (Player)event.getSourceController();
	Track track = tracks.findTrack(p);

	if (event instanceof StartEvent) {
	    startedCount++;
	    totalToStart--;
	    track.setState(STARTED);
        }

	if (event instanceof StopEvent &&
	    !(event instanceof RestartingEvent)) 
	{
	    // It is possible that multiple StopEvents come for
	    // one Player. Eg. if end of media is reached before
            // stop() request. A stop() call always generates a
	    // StopByRequestEvent even if Controller is already stopped.
	    // For this reason, we test state value in Track associated
	    // with Player object.

	    if (track.getState() == STARTED) {
	        startedCount--;
		track.setState(STOPPED);
	    }
	    if (startedCount == 0 &&  totalToStart == 0) {
		state = STOPPED;
		mpc.rewind();
		fireMultiPlayerUpdate(STOPPED);
	    }
	}
	if (event instanceof ControllerErrorEvent) {
	    // If this Player was part of TrackModel
	    // close it down in response to error.
	    if (trackModel != null) {
		trackModel.deassignTrack(p);
	    }
	}
    }

	/////////////// Helper Methods ////////////////////

     /*
      * Add MultiPlayer as listener on each Player.
      * Return how many Players.
      */
     private int addAsListener(TrackList tracks) {
	int count = 0;
	for (int i = 0; i < tracks.getNumberOfTracks(); i++) {
	    Track track = tracks.getTrack(i);
	    if (!track.isAvailable()) {
		track.getPlayer().addControllerListener(this);
		count++;
	    }
	}
	return count;
     }

 	/**
	* Convert MultiPlayer state to a string.
	* @param The current state of the MultiPlayer
	* @return A String representing current state of MultiPlayer.
	*/
     public static String stateToString(int state) {
	if (state == INITIALIZED)
	    return "Initialized";
        else if (state == STARTED)
	    return "Started";
	else if (state == STOPPED)
	    return "Stopped";
        return null;
     }
}
