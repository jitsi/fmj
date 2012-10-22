package ejmf.toolkit.util;

import java.util.EventListener;
/**
 * Implemented by those class that need to informed of 
 * time events from a SourcedTimer.
 *
 * @see		   ejmf.toolkit.SourcedTimer
 * @see		   ejmf.toolkit.SourcedTimerEvent
 * @version        1.0
 * @author         Rob Gordon & Steve Talley
 */
public interface SourcedTimerListener extends EventListener {
    /** 
     * Called in response to a SourcedTimer event
     */
    public void timerUpdate(SourcedTimerEvent e);
}
