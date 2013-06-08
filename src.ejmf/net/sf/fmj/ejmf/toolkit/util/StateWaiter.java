package net.sf.fmj.ejmf.toolkit.util;

import javax.media.*;

/**
 * The StateWaiter class provides routines to allow the current thread to wait
 * for a Controller to reach a particular state. A StateWaiter object will first
 * register itself as a ControllerListener on the given Controller object. It
 * will then set the state to be waited for, and block the current thread until
 * either
 * <UL>
 * <LI>the Controller posts a TransitionEvent indicating that it has moved to
 * the desired state, or</LI>
 * <LI>the Controller posts a ControllerErrorEvent, indicating that an error
 * occurred andthe transition failed</LI>
 * </UL>
 *
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 *
 * @author Steve Talley and Rob Gordon
 */
public class StateWaiter implements ControllerListener
{
    /**
     * The state to be waited for
     */
    private int state;

    /**
     * Indicates whether the desired state has been reached
     */
    private boolean stateReached = false;

    /**
     * Indicates whether this StateWaiter is already a ControllerListener of its
     * Controller.
     */
    private boolean listening = false;

    /**
     * The Controller on which to wait for the desired state
     */
    private Controller controller;

    /**
     * Construct a StateWaiter object for the given Controller
     *
     * @param controller
     *            the Controller on which to wait for the desired state
     */
    public StateWaiter(Controller controller)
    {
        this.controller = controller;
    }

    /**
     * Adds this StateWaiter as a ControllerListener of the Controller
     */
    private void addAsListener()
    {
        if (!listening)
        {
            controller.addControllerListener(this);
            listening = true;
        }
    }

    public boolean blockingConfigure()
    {
        setState(Processor.Configured);
        ((Processor) controller).configure();
        return waitForState();
    }

    /**
     * Calls prefetch() on the Controller and blocks the current thread until
     * the Controller is Prefetched.
     *
     * @return boolean indicating whether the transition was successful.
     */
    public boolean blockingPrefetch()
    {
        setState(Controller.Prefetched);
        controller.prefetch();
        return waitForState();
    }

    /**
     * Calls realize() on the Controller and blocks the current thread until the
     * Controller is Realized.
     *
     * @return boolean indicating whether the transition was successful.
     */
    public boolean blockingRealize()
    {
        setState(Controller.Realized);
        controller.realize();
        return waitForState();
    }

    /**
     * Casts the Controller to a Player, calls start(), and blocks the current
     * thread until the player is Started.
     *
     * @return boolean indicating whether the transition was successful.
     *
     * @exception ClassCastException
     *                If the Controller is not a Player
     */
    public boolean blockingStart()
    {
        setState(Controller.Started);
        Player player = (Player) controller;
        player.start();
        return waitForState();
    }

    /**
     * Calls syncStart() on the Controller and blocks the current thread until
     * the Controller is Started. This could throw a ClockStartedError if the
     * Controller is not in the Prefetched state.
     *
     * @return boolean indicating whether the transition was successful.
     */
    public boolean blockingSyncStart(Time t)
    {
        setState(Controller.Started);
        controller.syncStart(t);
        return waitForState();
    }

    public boolean blockingWait(int state)
    {
        setState(state);
        return waitForState();
    }

    /**
     * Listens for a transition to the state that this StateWaiter is waiting
     * for. Notifies the the waiting thread and stops listening if any of the
     * following occur:
     * <p>
     * <UL>
     * <LI>A TransitionEvent to the desired state is posted</LI>
     * <LI>A StopEvent is posted</LI>
     * <LI>A ControllerClosedEvent is posted (indicating a failure)</LI>
     * </UL>
     *
     * @param event
     *            the media event
     */
    public synchronized void controllerUpdate(ControllerEvent event)
    {
        if (event.getSourceController() != controller)
        {
            return;
        }

        if (event instanceof TransitionEvent)
        {
            int currState = ((TransitionEvent) event).getCurrentState();

            stateReached = (currState >= state);
        }

        // Is this a stop event or a Controller Error?
        if (event instanceof StopEvent
                || event instanceof ControllerClosedEvent || stateReached)
        {
            // Stop listening
            removeAsListener();

            // Notify threads waiting for state change
            notifyAll();
        }
    }

    /**
     * Removes this StateWaiter as a ControllerListener of the Controller
     */
    private void removeAsListener()
    {
        controller.removeControllerListener(this);
        listening = false;
    }

    /**
     * Sets the state to wait for
     */
    private void setState(int state)
    {
        this.state = state;
        stateReached = false;
        addAsListener();
    }

    /**
     * Blocks the current thread until the Controller has reached the previously
     * specified state or an error has occurred.
     *
     * @return boolean indicating whether the transition was successful.
     */
    private synchronized boolean waitForState()
    {
        while (listening)
        {
            try
            {
                wait();
            } catch (InterruptedException e)
            {
            }
        }

        return stateReached;
    }
}
