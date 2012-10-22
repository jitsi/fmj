package ejmf.toolkit.media.content.text.plain;

import javax.media.ClockStoppedException;
import javax.media.InternalErrorEvent;

import ejmf.toolkit.gui.tickertape.TickerTape;

/**
 * This class allows a sparation between JMF and the
 * TickerTape class.  When the media has completed, an
 * EndOfMediaEvent will be posted by the given Handler.
 *
 * @author     Steve Talley
 */
public class JMFTickerTape extends TickerTape {
    private Handler player;

    /**
     * Construct a JMFTickerTape with the given Handler.  When
     * the media has completed, an EndOfMediaEvent will be posted
     * by this Handler.
     */
    public JMFTickerTape(Handler player) {
        super("");
        this.player = player;
    }

    /**
     * Calls super.run() and then calls player.endOfMedia().  If the
     * thread is stopped, player.endOfMedia() will not be called.
     */
    public void run() {
        super.run();
        
        //  Indicate that the end of media has been reached.  The
        //  Controller should still be in the Started state at
        //  this point.  If it is not, an InternalErrorEvent is
        //  posted.

        try {
            player.endOfMedia();
        } catch(ClockStoppedException e) {
            player.postEvent(
                new InternalErrorEvent(player,
                    "Controller not in Started state at EOM") );
        }
    }
}
