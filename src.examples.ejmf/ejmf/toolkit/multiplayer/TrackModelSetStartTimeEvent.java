package ejmf.toolkit.multiplayer;

/**
 *  Signals a change in the start time to a Track 
 *  modelled by a TrackModel.
 */

public class TrackModelSetStartTimeEvent 
	extends TrackModelEvent {
	
    private double startTime;

 	/**  
        * Construct a TrackModelSetStartTimeEvent
    	* for the Track whose index is <tt>track</tt>.
        *  
	* @param track index of Track in TrackModel.
	* @param startTime new start time of media in Track.
        */ 
    public TrackModelSetStartTimeEvent(
		TrackModel tm, int track, double startTime) {

	super(tm, track);
	this.startTime = startTime;
    }

	/**
	* Return the new start time whose setting triggered
	* event.
	* @return a double representation of start time
	*/
    public double getValue() {
	return startTime;
    }
}

