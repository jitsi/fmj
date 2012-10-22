package ejmf.toolkit.multiplayer;

/**
 *  Signals a change in the playing time of a Track 
 *  modelled by a TrackModel.
 */

public class TrackModelSetPlayingTimeEvent 
	extends TrackModelEvent {
	
    private double playingTime;

 	/**  
        * Construct a TrackModelSetPlayingTimeEvent
    	* for the Track whose index is <tt>track</tt>.
        *  
	* @param track index of Track in TrackModel.
	* @param playingTime new playing time of media in Track.
        */ 
    public TrackModelSetPlayingTimeEvent(
		TrackModel tm, int track, double playingTime) {

	super(tm, track);
	this.playingTime = playingTime;
    }

	/**
	* Return the new playing time whose setting triggered
	* event.
	* @return A double representing playing time.
	*/
    public double getValue() {
	return playingTime;
    }
}

