package ejmf.toolkit.multiplayer;

/**
* Exception thrown by MultiPlayer when an operation
* is attempted while MultiPlayer is in illegal state
* for that operation.
*/

public class MultiPlayerStateException extends MultiPlayerException {
	/** Create a MultiPlayerStateException. */
    public MultiPlayerStateException() {
	super();
    }

	/** Create a MultiPlayerStateException and include a
	* descriptive message. 
	* @param msg Description of exception.
	*/
    public MultiPlayerStateException(String msg) {
	super(msg);
    }
}
