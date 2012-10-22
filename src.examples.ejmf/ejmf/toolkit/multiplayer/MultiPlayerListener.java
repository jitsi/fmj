package ejmf.toolkit.multiplayer;

import java.util.EventListener;

/**
* Identifies a class as being capable of fielding
* MultiPlayer update events.
*/

public interface MultiPlayerListener extends EventListener {
	/**
	* Called in response to state change in MultiPlayer.
	* @param state Current state of MultiPlayer.
	*/
    public void multiPlayerUpdate(int state);
}
