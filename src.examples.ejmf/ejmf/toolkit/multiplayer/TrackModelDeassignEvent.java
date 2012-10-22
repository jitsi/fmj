package ejmf.toolkit.multiplayer;

/*
 * This method is fired by TrackModel when a new Track
 * becomes available within a TrackModel
 */
public class TrackModelDeassignEvent extends TrackModelEvent {
	/**
	* Create a new TrackModelDeassignEvent.	
	*
	* @param tm TrackModel from which Track is deassigned.
	* @param index Index from which Track was deassigned.
	*/
    public TrackModelDeassignEvent(TrackModel tm, int index) {
	super(tm, index);
    }
}

