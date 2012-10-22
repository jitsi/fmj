package ejmf.toolkit.multiplayer;

/**
* Root class of TrackModel events.
*/
public class TrackModelEvent {
    private TrackModel 	tm;
    private int		index;
 
	/**
	* Create a new TrackModel event.
	*
	* @param tm TrackModel in which some change occurs.
	* @param index Index within TrackModel at which change occurs.
	*/
    public TrackModelEvent(TrackModel tm, int index) {
	this.tm = tm;
        this.index = index;
    }

	/** 
	* Reports index at which update occurred.
	* @return index of Track at which update occurred.
	*/
    public int getIndex() {
	return index;
    }

	/**
	* Reports TrackModel in which update occurred.
	* @return the TrackModel in which update occurred.
	*/
    public TrackModel getTrackModel() {
	return tm;
    }

}
