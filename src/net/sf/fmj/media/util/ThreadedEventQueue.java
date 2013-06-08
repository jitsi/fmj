package net.sf.fmj.media.util;

import java.util.*;

import javax.media.*;

/**
 * A utility class to manage an event queue in a thread. To use it, subclass
 * from it and implement the processEvent() method.
 *
 * @version 1.4, 02/08/21
 */
public abstract class ThreadedEventQueue extends MediaThread
{
    private List<ControllerEvent> eventQueue = new Vector<ControllerEvent>();
    private boolean killed = false;

    public ThreadedEventQueue()
    {
        useControlPriority();
    }

    /**
     * Wait until there is something in the event queue to process. Then
     * dispatch the event to the listeners.The entire method does not need to be
     * synchronized since this includes taking the event out from the queue and
     * processing the event. We only need to provide exclusive access over the
     * code where an event is removed from the queue.
     */
    protected boolean dispatchEvents()
    {
        ControllerEvent evt = null;

        synchronized (this)
        {
            // Wait till there is an event in the event queue.
            try
            {
                while (!killed && eventQueue.size() == 0)
                    wait();
            } catch (InterruptedException e)
            {
                System.err.println("MediaNode event thread " + e);
                return true;
            }

            // Remove the event from the queue and dispatch it to the listeners.
            if (eventQueue.size() > 0)
                evt = eventQueue.remove(0);

        } // end of synchronized

        if (evt != null)
            processEvent(evt);

        // We have to finish delivering all the events before dying.

        return (!killed || eventQueue.size() != 0);
    }

    /**
     * kill the thread.
     */
    public synchronized void kill()
    {
        killed = true;
        notifyAll();
    }

    /**
     * Queue the given event in the event queue.
     */
    public synchronized void postEvent(ControllerEvent evt)
    {
        eventQueue.add(evt);
        notifyAll();
    }

    /**
     * Invoked when there is at least one event in the queue. Implement this as
     * a callback to process one event.
     */
    protected abstract void processEvent(ControllerEvent evt);

    /**
     * An inifinite while loop to dispatch ControllerEvent.
     */
    @Override
    public void run()
    {
        while (dispatchEvents())
        {
            // Deliberately empty.
        }
    }
}
