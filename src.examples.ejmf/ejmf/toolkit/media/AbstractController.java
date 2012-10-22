package ejmf.toolkit.media;

import java.util.Vector;

import javax.media.ClockStartedError;
import javax.media.ClockStoppedException;
import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataStarvedEvent;
import javax.media.DeallocateEvent;
import javax.media.EndOfMediaEvent;
import javax.media.IncompatibleTimeBaseException;
import javax.media.MediaTimeSetEvent;
import javax.media.NotPrefetchedError;
import javax.media.NotRealizedError;
import javax.media.PrefetchCompleteEvent;
import javax.media.RateChangeEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.RestartingEvent;
import javax.media.StartEvent;
import javax.media.StopAtTimeEvent;
import javax.media.StopByRequestEvent;
import javax.media.StopEvent;
import javax.media.StopTimeChangeEvent;
import javax.media.Time;
import javax.media.TimeBase;
import javax.media.TransitionEvent;

import ejmf.toolkit.controls.RateControl;

/**
 * The AbstractController class provides a basic implementation of a
 * javax.media.Controller.  Subclasses should implement the
 * following abstract "do" methods to transition their Controller:
 * <p>
 * <UL>
 * <LI>doRealize()</LI>
 * <LI>doPrefetch()</LI>
 * <LI>doSyncStart()</LI>
 * <LI>doDeallocate()</LI>
 * <LI>doStop()</LI>
 * </UL>
 * <p>
 * Follow these rules when implementing these methods:
 * <p>
 * <OL>
 * <LI>Do not return until the state change is complete.  Once the
 *    state change is complete, return ASAP.</LI>
 * <p>
 * <LI>Do not call one another.  They will be called in the correct
 *    order at the correct time.</LI>
 * <p>
 * <LI>Do not set the current or target states.  They are set
 *    automatically.</LI>
 * <p>
 * <LI>Do not post any TransitionEvents.  They are posted
 *    automatically.</LI>
 * <p>
 * <LI>Do not call any of the Clock routines.  They will be called
 *    automatically.</LI>
 * <p>
 * <LI>Return true if successful.  If unsuccessful, post an
 *    appropriate ControllerErrorEvent and return false.</LI>
 * <p>
 * <LI>When the end of the media has been reached, call endOfMedia().
 *    This will post an EndOfMediaEvent and set the appropriate
 *    states.  Do not post an EndOfMediaEvent in any other way.</LI>
 * </OL>
 * <p>
 * Other abstact methods that should be implemented are:
 * <p>
 * <UL>
 * <LI>doClose()</LI>
 * <LI>doSetMediaTime()</LI>
 * <LI>doSetRate()</LI>
 * </UL>
 *
 * @see        AbstractPlayer
 *
 * @author     Steve Talley
 */

public abstract class AbstractController extends AbstractClock
    implements Controller
{
    private int previousState;
    private int currentState = Unrealized;
    private int targetState;

    private StopTimeMonitor stopTimeMonitor;
    private ControllerEventQueue eventqueue;
    private ThreadQueue threadqueue;

    private Vector controls = new Vector();
    private Vector listeners = new Vector();

    
    /**
     * Construct a AbstractController.
     */
    public AbstractController() {
        super();
        eventqueue      = new ControllerEventQueue(listeners);
        stopTimeMonitor = new StopTimeMonitor(this);
        threadqueue     = new ThreadQueue();
        addControl( new RateControl(this) );
    }
    
    ////////////////////////////////////////////////////////////
    //
    //  Abstract methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Implement to realize the Controller.
     * <p>
     * This method should not be called directly.  Instead, call
     * realize().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doRealize();

    /**
     * Implement to prefetch the Controller.
     * <p>
     * This method should not be called directly.  Instead, call
     * prefetch().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doPrefetch();

    /**
     * Implement to start the Controller.
     * <p>
     * This method should not be called directly.  Instead, call
     * syncStart().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doSyncStart(Time t);

    /**
     * Implement to deallocate the Controller.
     * <p>
     * This method should not be called directly.  Instead, call
     * prefetch().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doDeallocate();

    /**
     * Implement to stop the Controller.
     * <p>
     * This method should not be called directly.  Instead, call
     * stop().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doStop();

    /**
     * Close the Controller.  Typically this method will release as
     * many resources as possible, especially those that may be
     * needed by other Controllers.
     * <p>
     * This method should not be called directly.  Instead, call
     * close().
     */
    public abstract void doClose();

    /**
     * Override to provide implementation-specific functionality.
     * When this method is called, it is guaranteed that the
     * Controller is Stopped and that the given time is within the
     * Controller's duration.
     * <p>
     * This method should not be called directly.  Instead, call
     * setMediaTime().
     *
     * @param      t
     *             The media time to set
     */
    public abstract void doSetMediaTime(Time t);

    /**
     * Override to provide implementation-specific functionality.
     * When this method is called, it is guaranteed that the
     * Controller is Stopped.
     * <p>
     * This method should not be called directly.  Instead, call
     * setRate().
     *
     * @param      rate
     *             The requested rate to set
     *
     * @return     The actual rate that was set
     */
    public abstract float doSetRate(float rate);


    ////////////////////////////////////////////////////////
    //
    //  javax.media.Clock methods
    //
    ////////////////////////////////////////////////////////

    /**
     * Set the <tt>TimeBase</tt> for this <tt>Clock</tt>.
     * This method can only be called on a <i>Stopped</i>
     * <tt>Clock</tt>. A <tt>ClockStartedError</tt> is
     * thrown if <tt>setTimeBase</tt> is called on a
     * <i>Started</i> <tt>Clock</tt>.
     * <p>
     * A <tt>Clock</tt> has a default <tt>TimeBase</tt> that
     * is determined by the implementation.  To reset a
     * <tt>Clock</tt> to its default <tt>TimeBase</tt>,
     * call <tt>setTimeBase(null)</tt>.
     *
     * @param      timebase
     *             The new <tt>TimeBase</tt> or
     *             <tt>null</tt> to reset the
     *             <tt>Clock</tt> to its default
     *             <tt>TimeBase</tt>.
     *
     * @exception  IncompatibleTimeBaseException
     *             Thrown if the <tt>Clock</tt> can't use the
     *             specified <tt>TimeBase</tt>.
     */
    public synchronized void setTimeBase(TimeBase timebase)
        throws IncompatibleTimeBaseException
    {
        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot set TimeBase on an Unrealized Controller.");
        }
    
        super.setTimeBase(timebase);
    }

    /**
     * Get the TimeBase that this Controller is using.
     */
    public synchronized TimeBase getTimeBase() {
        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot get time base from an Unrealized Controller");
        }
        return super.getTimeBase();
    }

    /**
     * Sets the stop time for this AbstractController.  Posts a
     * StopTimeChangeEvent if the stop time given is different
     * than the current stop time.
     *
     * @param      mediaStopTime
     *             The time at which you want the
     *             <tt>Clock</tt> to stop, in <i>media
     *             time</i>.
     *
     * @exception  NotRealizedError
     *             If the Controller is not Realized.
     *
     * @exception  ClockStartedError
     *             If the Controller is Started.
     */
    public synchronized void setStopTime(Time mediaStopTime) {
        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot set stop time on an unrealized Controller");
        }

        Time oldStopTime = getStopTime();

        //  If the stop time has changed, post an event
        if( mediaStopTime.getNanoseconds() !=
            oldStopTime.getNanoseconds() )
        {
            //  Set in superclass
            super.setStopTime(mediaStopTime);

            //  Post event
            postEvent(
                new StopTimeChangeEvent(this, mediaStopTime) );
        }
    }

    /**
     * Sets the media time.
     *
     * @param      t
     *             The media time to set
     *
     * @exception  NotRealizedError
     *             If the Controller is not Realized.
     *
     * @exception  ClockStartedError
     *             If the Controller is Started.
     */
    public synchronized void setMediaTime(Time t) {
        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot set media time on an Unrealized Controller");
        }

        long nano = t.getNanoseconds();
        Time duration = getDuration();

        //  Enforce upper bound on start time
        if( duration != DURATION_UNKNOWN &&
            duration != DURATION_UNBOUNDED )
        {
            long limit = duration.getNanoseconds();
            if( nano > limit ) {
                t = new Time(limit);
            }
        }

        //  Set the media time
        super.setMediaTime(t);

        //  Call implementation-specific functionality
        doSetMediaTime(t);

        //  Post MediaTimeSetEvent
        postEvent( new MediaTimeSetEvent(this, t) );
    }

    /**
     * Calculates the current media time based on the current
     * time-base time, the time-base start time, the media start
     * time, and the rate.
     *
     * @return     The current media time
     */
    public synchronized Time getMediaTime() {
        Time mediaTime = super.getMediaTime();
        Time duration = getDuration();

        //  Compare media time with duration
        if( duration != DURATION_UNKNOWN &&
            duration != DURATION_UNBOUNDED &&
            mediaTime.getNanoseconds() > duration.getNanoseconds() )
        {
            return duration;
        }

        return mediaTime;
    }

    /**
     * Set the temporal scale factor.  The argument
     * <i>suggests</i> the scale factor to use.
     * <p>
     * The <tt>setRate</tt> method returns the actual rate set
     * by the <tt>Clock</tt>. <tt>Clocks</tt> should set
     * their rate as close to the requested value as possible, but
     * are not required to set the rate to the exact value of any
     * argument other than 1.0.  A <tt>Clock</tt> is only
     * guaranteed to set its rate exactly to 1.0.
     *
     * @param      rate
     *             The temporal scale factor (rate) to set.
     *
     * @exception  NotRealizedError
     *             If the Controller is not Realized.
     *
     * @exception  ClockStartedError
     *             If the Controller is Started.
     *
     * @return     The actual rate set.
     *
     */
    public synchronized float setRate(float rate) {
        if( currentState == Unrealized ||
            currentState == Realizing )
        {
            throw new NotRealizedError(
                "Cannot set rate on an Unrealized Controller.");
        }

        //  Save the current rate
        float oldRate = getRate();

        //  Enforce superclass reqs
        float superRate = super.setRate(rate);

        //  Set the rate in the subclass
        float subRate = doSetRate(superRate);

        //  If the rate has changed since setting in the
        //  superclass, set it agagin
        if( rate != 1.0F && superRate != subRate ) {
            superRate = super.setRate(subRate);

            //  If it has changed again, give up and set to the
            //  only rate guaranteed to be accepted
            if( superRate != subRate ) {
                return setRate(1.0F);
            }
        }

        //  If the rate has changed, commit it and post an event.
        if(superRate != oldRate) {
            postEvent( new RateChangeEvent(this, superRate) );
        }

        return superRate;
    }


    ////////////////////////////////////////////////////////////
    //
    //  javax.media.Duration methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Returns DURATION_UNKNOWN.  This method should be
     * overridden to report a more precise duration.
     */
    public Time getDuration() {
        return DURATION_UNKNOWN;
    }


    ////////////////////////////////////////////////////////////
    //
    //  javax.media.Controller methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Realize Controller on new thread.  Subclasses should
     * override doRealize() to do the actual work to transition
     * the Controller.
     * <p>
     * Checks for Controller state prerequisites and creates a
     * RealizeThread to realize the AbstractController.  If there
     * is already a thread transitioning the AbstractController
     * forward, then the target state of the AbstractController is
     * set to Realized and the method returns.
     * <p>
     * Asynchronous method -- Start synchronous transition on
     * another thread and return ASAP.
     */
    public final synchronized void realize() {
        //  Has this state already been reached?
        if( currentState >= Realized ) {
            postRealizeCompleteEvent();
            return;
        }

        //  Set the target state
        if( targetState < Realized ) {
            setTargetState(Realized);
        }

        //  Realize on a separate thread
        Thread thread = new Thread() {
            public void run() {
                if( AbstractController.this.getState() < Realized ) {
                    synchronousRealize();
                }
            }
        };

        threadqueue.addThread(thread);
    }

    /**
     * Prefetch Controller on new thread.  Subclasses should
     * override doPrefetch() to do the actual work to transition
     * the Controller.
     * <p>
     * Checks for Controller state prerequisites and creates a
     * PrefetchThread to prefetch the AbstractController.  If there
     * is already a thread transitioning the AbstractController
     * forward, then the target state of the AbstractController is
     * set to Prefetched and the method returns.
     * <p>
     * Asynchronous method -- Start synchronous transition on
     * another thread and return ASAP.
     */
    public final synchronized void prefetch() {
        //  Has this state already been reached?
        if( currentState >= Prefetched ) {
            postPrefetchCompleteEvent();
            return;
        }

        //  Set the target state
        if( targetState < Prefetched ) {
            setTargetState(Prefetched);
        }

        //  Prefetch on a separate thread
        Thread thread = new Thread() {
            public void run() {
                if( AbstractController.this.getState() < Prefetched ) {
                    synchronousPrefetch();
                }
            }
        };

        threadqueue.addThread(thread);
    }

    /**
     * SyncStart Controller on new thread.  Subclasses should
     * override doSyncStart() to do the actual work to transition the
     * Controller.
     * <p>
     * Checks for Controller state prerequisites and creates a
     * SyncStartThread to syncstart the AbstractController.  The
     * target state of the AbstractController is then set to
     * Started and the thread is started.
     * <p>
     * Asynchronous method -- Start synchronous transition on
     * another thread and return ASAP.
     */
    public final synchronized void syncStart(final Time t) {
        //  Enforce state prereqs
        if (currentState == Started) {
            throw new ClockStartedError(
                "syncStart() cannot be called on a started Clock");
        }

        //  Enforce state prereqs
        if(currentState != Prefetched) {
            throw new NotPrefetchedError(
                "Cannot start the Controller before it has been prefetched");
        }

        //  Set the target state
        setTargetState(Started);

        //  SyncStart on a separate thread
        Thread thread = new Thread() {
            public void run() {
                if( AbstractController.this.getState() < Started ) {
                    synchronousSyncStart(t);
                }
            }
        };

        threadqueue.addThread(thread);
    }

    /**
     * Deallocate Controller on current thread.  Subclasses
     * should override doDeallocate() to do the actual work to
     * transition the Controller.  After ensuring state
     * prerequisites, this method will call doDeallocate().  If
     * doDeallocate() returns true, then the Controller is placed
     * in the appropriate state and a DeallocateCompleteEvent is
     * posted.  Otherwise, it is assumed that the controller has
     * posted a ControllerErrorEvent detailing the reasons for
     * it's failure.
     * <p>
     * Synchronous method -- return when transition complete
     */
    public final synchronized void deallocate() {
        int state;

        //  Enforce state prereq
        if( currentState == Started ) {
            throw new ClockStartedError(
                "deallocate() cannot be called on a started Controller");
        }

        //  Kill any forward-transitioning thread
        threadqueue.stopThreads();

        //  Do the actual deallocating.  If this returns false,
        //  the deallocate was unsuccessful.  Rely on the Controller
        //  to post the ControllerErrorEvent and return without
        //  modifying the current or target states.

        if( doDeallocate() ) {

            //  The deallocate was successful

            //  Return to previous state as dictated by the spec
            if( currentState == Unrealized ||
                currentState == Realizing )
            {
                state = Unrealized;
            } else {
                state = Realized;
            }

            //  Set current and target states and post event
            setState(state);
            setTargetState(state);
            postDeallocateEvent();
        }
    }


    /**
     *  Stop Controller on current thread and post a
     *  StopByRequestEvent.  Subclasses should override doStop()
     *  to do the actual work to stop the Controller.
     */
    public final void stop() {
        if( stopController() ) {
            postStopByRequestEvent();
        }
    }

    /**
     * Stop Controller on current thread and post a
     * StopAtTimeEvent.  Subclasses should override doStop() to
     * do the actual work to stop the Controller.
     * <p>
     * This method is usually only called (indirectly) by the
     * StopTimeMonitor class.
     * <p>
     * Synchronous method -- return when transition complete
     */
    protected void stopAtTime() {
        if( stopController() ) {
            postStopAtTimeEvent();
        }
    }

    /**
     * Stop Controller on current thread and post a
     * RestartingEvent.  Subclasses should override doStop() to
     * do the actual work to stop the Controller.
     * <p>
     * This method is usually only called (indirectly) by
     * Player.setMediaTime() or Player.setRate() when a managed
     * Controller must be stopped before its media time and rate,
     * respectively, can be set.
     * <p>
     * Synchronous method -- return when transition complete
     */
    protected void stopInRestart() {
        if( stopController() ) {
            postRestartingEvent();
        }
    }

    /**
     * Stop the controller.  If the Controller is Realizing or
     * Prefetching, then the target state will be set to Realized
     * or Prefetched, respectively, and the Controller will stop
     * when it completes the transition.  If the Controller is
     * Started, this method will call doStop().  If doStop()
     * returns true, then the Controller is placed in the
     * Prefetched state and a StopEvent is posted.  If doStop()
     * returns false, it is assumed that the controller has
     * posted a ControllerErrorEvent detailing the reasons for
     * it's failure.
     * <p>
     * Synchronous method -- return when transition complete
     *
     * @return     boolean indicating whether the
     *             stop was successful.
     */
    protected synchronized boolean stopController() {
        //  Kill any forward-transitioning threads.
        threadqueue.stopThreads();

        switch(currentState) {

            //  Stop any scheduled forward transitions

            case Unrealized:
            case Realized:
            case Prefetched:
                setTargetState(currentState);
                return true;

            //  If the Controller was Realizing, return it to the
            //  Unrealized state.

            case Realizing:
                setState(Unrealized);
                setTargetState(Unrealized);
                return true;

            //  If the Controller was Prefetching, return it to
            //  the Realized state.

            case Prefetching:
                setState(Realized);
                setTargetState(Realized);
                return true;
        }

        //  If we are here, then the Controller is Started

        //  Do the actual stopping.  If this returns false, the
        //  stop was unsuccessful.  Rely on the Controller to post the
        //  ControllerErrorEvent and return without modifying the
        //  current or target states.

        if(! doStop() ) {
            return false;
        }

        //  The stop was successful

        //  Stop the Clock
        super.stop();

        //  Set state.  The StopEvent will be posted by
        //  one of the protected synchronous Stop methods
        setState(Prefetched);
        setTargetState(Prefetched);

        return true;
    }


    /**
     * Close the Controller.  Release resources held by this
     * Controller.  Subclasses should implement doClose() to
     * add additional functionality.
     */
    public synchronized final void close() {

        //  Stop the Controller in case it is Started
        stop();

        //  Call implementation-specific functionality
        doClose();

        //  Set some resources to null
        controls = null;
        threadqueue = null;

        //  Post a ControllerClosedEvent
        postControllerClosedEvent();
    }

    ////////////////////////////////////////////////////////////
    //
    //  Control methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Add a Control to this Controller.
     *
     * @param      newControl
     *             The Control to add.
     */
    public void addControl(Control newControl) {
        synchronized(controls) {
            if(! controls.contains(newControl) ) {
                controls.addElement(newControl);
            }
        }
    }

    /**
     * Remove a Control from this Controller.
     *
     * @param      oldControl
     *             The Control to remove.
     */
    public void removeControl(Control oldControl) {
        controls.removeElement(oldControl);
    }

    /**
     * Get a list of the Control objects that this Controller
     * supports.  If there are no controls, an array of length
     * zero is returned.
     *
     * @return     A list of Controller Controls.
     *
     */
    public Control[] getControls() {
        Control[] array;
        synchronized(controls) {
            array = new Control[ controls.size() ];
            controls.copyInto(array);
        }
        return array;
    }

    /**
     * Get the Control that supports the class or interface
     * specified.  The full class or interface name should be
     * specified.  Null is returned if the Control is not
     * supported.
     *
     * @return     Control for the given class or interface
     *             name, or null if no such Control is supported.
     */
    public Control getControl(String forName) {
        Class c;

        try {
            c = Class.forName(forName);
        } catch(Exception e) {
            return null;
        }

        synchronized(controls) {
            for(int i = 0, n = controls.size(); i < n; i++) {
                Control control = (Control)controls.elementAt(i);
                if( c.isInstance(control) ) {
                    return control;
                }
            }
        }

        return null;
    }

    ////////////////////////////////////////////////////////////
    //
    //  Listener methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Specify a ControllerListener to which this Controller will
     * send events.
     *
     * @param      listener
     *             The listener to which the Controller will post
     *             events.
     */
    public void addControllerListener(ControllerListener listener) {
        synchronized(listeners) {
            if(! listeners.contains(listener) ) {
                listeners.addElement(listener);
            }
        }
    }

    /**
     * Remove the specified listener from this Controller's
     * listener list.
     *
     * @param      listener
     *             The listener that has been receiving events
     *             from this Controller.
     *
     */
    public void removeControllerListener(ControllerListener listener) {
        synchronized(listeners) {
            listeners.removeElement(listener);
        }
    }

    ////////////////////////////////////////////////////////////
    //
    //  State methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Get the previous state of this Controller.
     */
    public int getPreviousState() {
        return previousState;
    }

    /**
     * Set the current state of this Controller.  This will
     * implicitly set the previous state as well.
     */
    protected synchronized void setState(int state) {
        if(state == currentState) return;
        previousState = currentState;
        currentState = state;
    }

    /**
     * Get the current state of this Controller.
     */
    public int getState() {
        return currentState;
    }

    /**
     * Set the targetState state of this Controller.
     */
    protected void setTargetState(int state) {
        targetState = state;
    }

    /**
     * Get the target state of this Controller.
     */
    public int getTargetState() {
        return targetState;
    }

    ////////////////////////////////////////////////////////////
    //
    //  AbstractController methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Indicates to the framework that the end of media has been
     * reached.  Marks the media time, sets the current and target
     * states to Prefetched, and posts an EndOfMediaEvent.
     *
     * @exception  ClockStoppedException
     *             If the AbstractController is not in the Started
     *             state.
     */
    protected synchronized void endOfMedia()
        throws ClockStoppedException
    {
        //  Enforce state prereq
        if( currentState != Started ) {
            throw new ClockStoppedException();
        }

        //  Stop the Clock
        super.stop();

        //  Set the state and post an EndOfMediaEvent
        setState(Prefetched);
        setTargetState(Prefetched);
        postEndOfMediaEvent();
    }
    
    ////////////////////////////////////////////////////////////
    //
    //  Event methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Post a ControllerEvent to the Media Event Queue
     *
     * @param      event
     *             The ControllerEvent to post.
     */
    protected void postEvent(ControllerEvent event) {
        eventqueue.postEvent(event);
    }

    /**
     * Post a TransitionEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * and Target State properties of the TransitionEvent.
     */
    protected void postTransitionEvent() {
        postEvent( new TransitionEvent(
            this, previousState, currentState, targetState) );
    }

    /**
     * Post a RealizeCompleteEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * and Target State properties of the RealizeCompleteEvent.
     */
    protected void postRealizeCompleteEvent() {
        postEvent( new RealizeCompleteEvent(
            this, previousState, currentState, targetState) );
    }

    /**
     * Post a PrefetchCompleteEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * and Target State properties of the PrefetchCompleteEvent.
     */
    protected void postPrefetchCompleteEvent() {
        postEvent( new PrefetchCompleteEvent(
            this, previousState, currentState, targetState) );
    }

    /**
     * Post a DeallocateEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * Target State, and Media Time properties of the
     * DeallocateEvent.
     */
    protected void postDeallocateEvent() {
        postEvent( new DeallocateEvent(
            this, previousState, currentState, targetState, getMediaTime()) );
    }

    /**
     * Post a StopEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * Target State, and Media Time properties of the
     * StopEvent.
     */
    protected void postStopEvent() {
        postEvent( new StopEvent(
            this, previousState, currentState, targetState, getMediaTime()) );
    }

    /**
     * Post a StopAtTimeEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * Target State, and Media Time properties of the
     * StopAtTimeEvent.
     */
    protected void postStopAtTimeEvent() {
        postEvent( new StopAtTimeEvent(
            this, previousState, currentState, targetState, getMediaTime()) );
    }

    /**
     * Post a StartEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * Target State, Media Time, and Time-base Time properties of
     * the StartEvent.
     */
    protected void postStartEvent() {
        postEvent( new StartEvent(
            this, previousState, currentState, targetState,
            getMediaStartTime(), getTimeBaseStartTime()) );
    }

    /**
     * Post a DataStarvedEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * Target State, and Media Time properties of the
     * DataStarvedEvent.
     */
    protected void postDataStarvedEvent() {
        postEvent( new DataStarvedEvent(
            this, previousState, currentState, targetState, getMediaTime()) );
    }

    /**
     * Post a EndOfMediaEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * Target State, and Media Time properties of the
     * EndOfMediaEvent.
     */
    protected void postEndOfMediaEvent() {
        postEvent( new EndOfMediaEvent(
            this, previousState, currentState, targetState, getMediaTime()) );
    }

    /**
     * Post a RestartingEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * Target State, and Media Time properties of the
     * RestartingEvent.
     */
    protected void postRestartingEvent() {
        postEvent( new RestartingEvent(
            this, previousState, currentState, targetState, getMediaTime()) );
    }

    /**
     * Post a StopByRequestEvent to the Media Event Queue.
     * Automatically fill in the Previous State, Current State,
     * Target State, and Media Time properties of the
     * StopByRequestEvent.
     */
    protected void postStopByRequestEvent() {
        postEvent( new StopByRequestEvent(
            this, previousState, currentState, targetState, getMediaTime()) );
    }

    /**
     * Post a ControllerClosedEvent to the Media Event Queue.
     */
    protected void postControllerClosedEvent() {
        postEvent( new ControllerClosedEvent(this) );
    }

    ////////////////////////////////////////////////////////////
    //
    //  Synchronous state-changing methods
    //
    //  These routines are called by way of a TransitionThread.
    //  When these methods are called, the following assumptions
    //  can be made:
    //  
    //  1. The current state is less than the desired state.  If
    //     doSyncStart() is called, the Controller is guaranteed
    //     to be in the Prefetched state.
    //  
    //  2. The target state is greater than or equal to the
    //     desired state.
    //  
    //  3. Any state-related exceptions have been thrown already.
    //  
    //  4. There are no other state-changing threads running.
    //
    ////////////////////////////////////////////////////////////

    /**
     * Gets the ThreadQueue object for this AbstractController.
     */
    protected ThreadQueue getThreadQueue() {
        return threadqueue;
    }

    /**
     * Realize the AbstractController synchronously.
     * <p>
     * This method should not be called directly.  Instead, call
     * realize().
     * <p>
     * Synchronous method -- return when transition complete
     */
    protected void synchronousRealize() {
        //  Set the current state and post event
        setState( Realizing );
        postTransitionEvent();

        //  Do the actual realizing
        if( doRealize() ) {

            //  The realize was successful

            //  Set the current state and post event
            setState( Realized );
            postRealizeCompleteEvent();

            //  Set the initial rate
            setRate(1);

            //  Set the initial media time
            setMediaTime( new Time(0) );

        } else {

            //  The realize was unsuccessful
            //  Rely on the Controller to post the
            //  ControllerErrorEvent

            //  Reset the current and target states
            setState( Unrealized );
            setTargetState( Unrealized );
        }
    }

    /**
     * Realize the AbstractController synchronously.
     * <p>
     * This method should not be called directly.  Instead, call
     * prefetch().
     * <p>
     * Synchronous method -- return when transition complete
     */
    protected void synchronousPrefetch() {
        //  Does the controller need to be realized?
        if( currentState < Realized ) {
            synchronousRealize();
        }

        //  Set the current state and post event
        setState( Prefetching );
        postTransitionEvent();

        //  Do the actual prefetching
        if( doPrefetch() ) {

            //  The prefetch was successful

            //  Set the current state and post event
            setState( Prefetched );
            postPrefetchCompleteEvent();

            //  Set the initial media time
            setMediaTime( new Time(0) );

        } else {

            //  The prefetch was unsuccessful.
            //  Rely on the Controller to post the
            //  ControllerErrorEvent

            //  Reset the current and target states
            setState( Realized );
            setTargetState( Realized );
        }
    }

    /**
     * Returns LATENCY_UNKNOWN.  This method should be
     * overridden to report a more precise start latency.
     */
    public Time getStartLatency() {
        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot get start latency from an unrealized Controller.");
        }
    
        return LATENCY_UNKNOWN;
    }

    /**
     * SyncStart the AbstractController synchronously at the
     * previously-specified time-base start time.
     * <p>
     * This method should not be called directly.  Instead, call
     * syncStart().
     * <p>
     * Synchronous method -- return when transition complete
     */
    protected void synchronousSyncStart(Time t) {

        //  Set the state and post event
        setState( Started );
        postStartEvent();

        //  Calculate start latency.  If unknown, asssume zero.
        Time latencyTime = getStartLatency();
        long latency;

        if( latencyTime == LATENCY_UNKNOWN ) {
            latency = 0;
        } else {
            latency = latencyTime.getNanoseconds();
        }

        long start = t.getNanoseconds();
        long now = getTimeBase().getNanoseconds();

        //  If the start time is in the past, change it to now
        if(now  + latency > start) t = new Time(now + latency);

        //  Start the clock
        super.syncStart(t);

        //  Do the actual syncStarting
        if(! doSyncStart(t) ) {

            //  The syncStart was unsuccessful
            //  Rely on the Controller to post the
            //  ControllerErrorEvent

            //  Reset the states
            setState( Prefetched );
            setTargetState( Prefetched );
        }
    }

    /**
     * For a given time-base start time, block until the
     * AbstractController should be started.  For a given time-
     * base start time (t), this method will get the
     * AbstractController's start latency (l) and block until (t
     * - l).  This method is useful for implementations of
     * doSyncStart().
     * <p>
     * If the time-base time (t - l) has already passed, return
     * immediately.
     */
    public void blockUntilStart(Time t) {

        //  Calculate start latency.  If unknown, asssume zero.
        Time latencyTime = getStartLatency();
        long latency;

        if( latencyTime == LATENCY_UNKNOWN ) {
            latency = 0;
        } else {
            latency = latencyTime.getNanoseconds();
        }

        long start = t.getNanoseconds();
        long now = getTimeBase().getNanoseconds();
        long delay = (start - latency - now)/1000000;

        //  Wait for the syncTime

        if( delay > 0 ) {
            try {
                Thread.sleep(delay);
            } catch(InterruptedException e) {}
        }
    }
}
