package net.sf.fmj.ejmf.toolkit.util;

import java.util.*;

/**
 * Implemented by those class that need to informed of time events from a
 * SourcedTimer.
 *
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 *
 * see ejmf.toolkit.SourcedTimer see ejmf.toolkit.SourcedTimerEvent
 *
 * @version 1.0
 * @author Rob Gordon & Steve Talley
 */
public interface SourcedTimerListener extends EventListener
{
    /**
     * Called in response to a SourcedTimer event
     */
    public void timerUpdate(SourcedTimerEvent e);
}
