package ejmf.toolkit.multiplayer;

/*
 * This method is fired by TrackModel when a new Track
 * is assigned to the TrackModel's TrackList.
 */

public class TrackModelAssignEvent extends TrackModelEvent {
	/**
	* Create a new TrackModelAssignEvent.	
	*
	* @param tm TrackModel to which Track is assigned.
	* @param index Index at which Track was assigned.
	*/
    public TrackModelAssignEvent(TrackModel tm, int index) {
	super(tm, index);
    }
}

