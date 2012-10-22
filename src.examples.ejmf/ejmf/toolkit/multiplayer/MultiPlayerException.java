package ejmf.toolkit.multiplayer;

/**
* Exception class used by MultiPlayer
*/

public class MultiPlayerException extends Exception {
	/** Create a MultiPlayerException
	*/
    public MultiPlayerException() {
	super();
    }

	/** Create a MultiPlayer with a message string.
	* @param msg A string describing message.
	*/
    public MultiPlayerException(String msg) {
	super(msg);
    }
}
