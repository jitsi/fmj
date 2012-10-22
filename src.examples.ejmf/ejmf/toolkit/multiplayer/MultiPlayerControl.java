package ejmf.toolkit.multiplayer;

import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Player;

import ejmf.toolkit.util.StateWaiter;

/**
  * The MultiPlayerControl inteface defines a control
  * stategy pattern for use with a MultiPlayer client.
  *
  * The interface defines the following methods:
  *
  * <ul><tt>
  * <li>close
  * <li>init
  * <li>restart
  * <li>rewind
  * <li>start
  * <li>stop
  * <li>update
  * </tt></ul>
  * 
  * An implementation of MultiPlayerControl is responsible for
  * maintaining a TrackList upon which all the methods operate.
  * This is typically passed to the object at construction time.
  * <p>
  * Implementations of a MultiPlayerControl's methods should not
  * assume anything about the state of a Controller. Any 
  * implementation to ensure the Controller is in the appropriate
  * state for performng an operation.
  * <p>
  * The <tt>update</tt> takes a TrackList as an argument and
  * provides a mechanism of providing a new TrackList to the control 
  * stratgey.
  *
  *  @see ejmf.toolkit.multiplayer.MultiPlayer
  *  @see ejmf.toolkit.multiplayer.TrackList
  *  @see ejmf.toolkit.multiplayer.DefaultMultiPlayerControl
  *  @see ejmf.toolkit.multiplayer.MultiPlayerTimerControl
  *  @see ejmf.toolkit.multiplayer.SyncStartControl
  */

public abstract class MultiPlayerControl implements ControllerListener {
    private TrackList		tracks;

	/**	
	* Create a MultiPlayer from a list of Tracks.	
	*/
    public MultiPlayerControl(TrackList tracks) {
 	this.tracks = tracks;
    }

    /** 
     *  Deallocate and close all Players
     */
    public abstract void close();

    /** Initialize the MultiPlayerControl
     *  @return true if initialization was successful, 
     *    otherwise return false.
     */
    public abstract boolean init();

    /** Restart the MultiPlayer
     */
    public abstract void restart();

    /** Rewind the MultiPlayer. 
     */
    public abstract void rewind();

    /** Start the MultiPlayerControl. Starts all
     *  the Players in the TrackList.
     */
    public abstract void start();

    /** Stop the MultiPlayerControl. Stops all
     *  Players in the TrackList.
     */
    public abstract void stop();

    /** Update the MultiPlayerControl. This method
     *  is typically called when the TrackList is 
     *  changed in such a way as to impact the control
     *  strategy, ie. an addition or deletion of a Track.
     *
     *  @param tracks  a ejmf.toolkit.multiplayer.TrackList
     *  @return true if update was successful, otherwise
     * 		return false.
     */
    public abstract boolean update(TrackList tracks);

	/** 
	 *  This method provides subclass action in 
	* response to a ControllerEvent. It is called
	* by controllerUpdate.
	*/
    protected abstract void controllerUpdateHook(ControllerEvent event);

	/** 	
	* Get the TrackList associated with this 
	* MultiPlayerControl.
	*/
    public TrackList getTrackList() {
	return tracks;
    }

	/** 
	* Allow subclasses to set TrackList	
	*/
    protected void setTrackList(TrackList tracks) {
	this.tracks = tracks;
    }

	/**	
	* Prefetch all Players under control of MultiPlayer
	*/
    protected boolean prefetchPlayers() {
	for (int i = 0; i < tracks.getNumberOfTracks(); i++) {
	    if (tracks.getTrack(i).isAvailable())
		continue;

            Player player = tracks.getTrack(i).getPlayer();
            if (!blockingPrefetch(player)) {
		return false;
	    }
	}
	return true;
    }

	/**
	*  A wrapper around StateWaiter's <tt>blockingPrefetch<tt>
        * that stops the Player if necessary before testing state.     
        * If Controller state is less than <tt>Prefetched</tt>, then
     	* Controller is moved to this state.
        * 
        * This method performs a slight optimization by checking whether
        * Controller is already in the <tt>Prefetched</tt> state.
	* If so, return is immediate.
        *
        * @param player A javax.media.Player
   	@ @see ejmf.toolkit.util.StateWaiter
      	*/
    private boolean blockingPrefetch(Player player) {

	int state = player.getState();
 	if (state == Controller.Prefetched) {
	    return true;
	}

	if (state == Controller.Started) {
	    player.stop();
	}

	if (state < Controller.Prefetched) {
	    StateWaiter sw = new StateWaiter(player);
            return sw.blockingPrefetch();
	}
	return true;
    }

    ////////// ControllerListener Implementation //////////////

	/**
	* Call out to controllerUpdateHook and force subclass
	* to provide whatever control they need.
	*/

    public void controllerUpdate(ControllerEvent event) {
	controllerUpdateHook(event);
    }
}
