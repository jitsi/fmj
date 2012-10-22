package ejmf.toolkit.multiplayer;

import java.util.EventListener;

/**
* Interface that marks a class as a listener on a 
* TrackModel.
*/

public interface TrackModelListener extends EventListener {
	/**
	* Invoked with a TrackModelEvent occurs.
	* @param tme A TrackModelEvent.
	*/
    public void trackModelUpdate(TrackModelEvent tme);
} 
