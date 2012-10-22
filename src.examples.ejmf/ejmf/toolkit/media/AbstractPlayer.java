package ejmf.toolkit.media;

import java.awt.Component;
import java.io.IOException;
import java.util.Vector;

import javax.media.Clock;
import javax.media.ClockStartedError;
import javax.media.ClockStoppedException;
import javax.media.Controller;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DurationUpdateEvent;
import javax.media.GainControl;
import javax.media.IncompatibleSourceException;
import javax.media.IncompatibleTimeBaseException;
import javax.media.NotRealizedError;
import javax.media.Player;
import javax.media.ResourceUnavailableEvent;
import javax.media.Time;
import javax.media.TransitionEvent;
import javax.media.protocol.DataSource;

import ejmf.toolkit.media.event.ManagedControllerErrorEvent;

/**
 * The AbstractPlayer class provides a basic implementation of a
 * javax.media.Player.  The abstract "do" methods from
 * AbstractController have been implemented here to provide support
 * for management of multiple Controllers.  Subclasses of this
 * class should instead implement the following abstract "do"
 * methods to transition their Controller:
 * <p>
 * <UL>
 * <LI>doPlayerRealize()</LI>
 * <LI>doPlayerPrefetch()</LI>
 * <LI>doPlayerSyncStart()</LI>
 * <LI>doPlayerDeallocate()</LI>
 * <LI>doPlayerStop()</LI>
 * </UL>
 * <p>
 * The rules for implementation of these methods are identical to
 * those for implementing a AbstractController:
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
 * <LI>doPlayerClose()</LI>
 * <LI>doPlayerSetMediaTime()</LI>
 * <LI>doPlayerSetRate()</LI>
 * <LI>getPlayerStartLatency()</LI>
 * <LI>getPlayerDuration()</LI>
 * </UL>
 *
 * @author     Steve Talley
 */
public abstract class AbstractPlayer extends AbstractController
    implements Player, ControllerListener
{
    private DataSource           source;
    private Vector               controllers;
    private Time                 duration;
    private ControllerErrorEvent controllerError;
    private GainControl          gainControl;
    private Component            visualComponent;
    private Component            controlPanelComponent;

    /**
     * Construct a AbstractPlayer.
     */
    public AbstractPlayer() {
        super();

        controllers = new Vector();
        duration = new Time(0);
    }

    ////////////////////////////////////////////////////////////
    //
    //  Abstract methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Implement to realize the Player.
     * <p>
     * This method should not be called directly.  Instead, call
     * realize().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doPlayerRealize();

    /**
     * Implement to prefetch the Player.
     * <p>
     * This method should not be called directly.  Instead, call
     * prefetch().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doPlayerPrefetch();

    /**
     * Implement to start the Player.
     * <p>
     * This method should not be called directly.  Instead, call
     * start().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doPlayerSyncStart(Time t);

    /**
     * Implement to deallocate the Player.
     * <p>
     * This method should not be called directly.  Instead, call
     * deallocate().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doPlayerDeallocate();

    /**
     * Implement to stop the Player.
     * <p>
     * This method should not be called directly.  Instead, call
     * stop().
     *
     * @return     True if successful, false otherwise.
     */
    public abstract boolean doPlayerStop();

    /**
     * Close the Player.  Typically this method will release as
     * many resources as possible, especially those that may be
     * needed by other Players.
     * <p>
     * This method should not be called directly.  Instead, call
     * close().
     */
    public abstract void doPlayerClose();

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
    public abstract void doPlayerSetMediaTime(Time t);

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
    public abstract float doPlayerSetRate(float rate);

    /**
     * Returns the start latency of the media played by <U>this</U>
     * Player only.  It does not consider any of the Controllers
     * that this Player may be managing.
     */
    public abstract Time getPlayerStartLatency();

    /**
     * Returns the duration of the media played by <U>this</U> Player
     * only.  It does not consider any of the Controllers that
     * this Player may be managing.
     */
    public abstract Time getPlayerDuration();


    ////////////////////////////////////////////////////////////
    //
    //  javax.media.Duration methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Gets the duration of this Player.
     */
    public synchronized final Time getDuration() {
        if( duration == null ) {
            updateDuration();
        }
        return duration;
    }

    /**
     * Update the duration of this Player.  It is defined to be
     * the longest duration between this Player and any of the
     * Controllers that this Player may be managing.  If any of
     * the Controllers returns DURATION_UNKNOWN or
     * DURATION_UNBOUNDED, then the duration is set to this value.
     */
    private synchronized final void updateDuration() {
        Time duration = getPlayerDuration();

        if( duration != DURATION_UNKNOWN )
        {
            for(int i = 0, n = controllers.size(); i < n; i++) {
                Controller c =
                    (Controller)controllers.elementAt(i);

                Time d = c.getDuration();

                if( d == DURATION_UNKNOWN ) {
                    duration = d;
                    break;
                }

                if( duration != DURATION_UNBOUNDED &&
                    ( d == DURATION_UNBOUNDED ||
                      d.getNanoseconds() >
                      duration.getNanoseconds() ) )
                {
                    duration = d;
                }
            }
        }

        boolean newDuration = false;

        if( duration      == DURATION_UNKNOWN   ||
            duration      == DURATION_UNBOUNDED ||
            this.duration == DURATION_UNKNOWN   ||
            this.duration == DURATION_UNBOUNDED)
        {
            if( this.duration != duration ) {
                newDuration = true;
            }
        } else

        if( duration.getNanoseconds() !=
            this.duration.getNanoseconds() )
        {
            newDuration = true;
        }

        //  If the duration has changed since it was last
        //  calculated, update it and post a DurationUpdateEvent

        if( newDuration ) {
            this.duration = duration;
            postEvent( new DurationUpdateEvent(this,duration) );
        }
    }


    ////////////////////////////////////////////////////////////
    //
    //  javax.media.Clock methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Sets the media time.  If the Player is in the Started
     * state, it is stopped before setting the media time and
     * restarted afterwards.
     *
     * @param      t
     *             The media time to set
     *
     * @exception  NotRealizedError
     *             If the Controller is not Realized.
     */
    public synchronized void setMediaTime(Time t) {
        boolean isStarted = (getState() == Started);
        
        if(isStarted) {
            stopInRestart();
        }

        super.setMediaTime(t);
        
        if(isStarted) {
            start();
        }
    }

    /**
     * Sets the rate.  If the Player is in the Started state, it
     * is stopped before setting the rate and restarted
     * afterwards.
     *
     * @param      rate
     *             The temporal scale factor (rate) to set.
     *
     * @exception  NotRealizedError
     *             If the Controller is not Realized.
     */
    public synchronized float setRate(float rate) {
        boolean isStarted = (getState() == Started);
        
        if(isStarted) {
            stopInRestart();
        }

        float newRate = super.setRate(rate);
        
        if(isStarted) {
            start();
        }

        return newRate;
    }

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.Controller methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Gets the start latency of this Player.  It is defined to
     * be the longest start latency between this Player and any
     * of the Controllers that this Player may be managing.  If
     * any of the Controllers returns LATENCY_UNKNOWN, it's value
     * is skipped in the calculation of the maximum latency.
     *
     * @return     The maximum start latency of this Player and
     *             all its managed Controllers, or
     *             LATENCY_UNKNOWN if the Player and its managed
     *             Controllers all return LATENCY_UNKNOWN.
     */
    public synchronized Time getStartLatency() {
        int currentState = getState();

        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot get start latency from an Unrealized Controller");
        }

        Time latency = getPlayerStartLatency();

        for(int i = 0, n = controllers.size(); i < n; i++) {
            Controller c = (Controller)controllers.elementAt(i);
            Time l = c.getStartLatency();

            if( l == LATENCY_UNKNOWN ) {
                continue;
            }

            if( latency == LATENCY_UNKNOWN ||
                l.getNanoseconds() > latency.getNanoseconds() )
            {
                latency = l;
            }
        }

        return latency;
    }

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.Player methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Start the player on a new thread.  If necessary, the
     * Player will be prefetched before being started.
     * Subclasses should override doSyncStart() to do the actual work
     * to transition the Controller.
     * <p>
     * Checks for Player state prerequisites and creates a
     * StartThread to start the AbstractPlayer.  If there
     * is already a thread transitioning the AbstractPlayer
     * forward, then the target state of the AbstractPlayer is
     * set to Started and the method returns.
     * <p>
     * Asynchronous method -- Start synchronous transition on
     * another thread and return ASAP.
     */
    public final void start() {
        int state  = getState();
        int target = getTargetState();

        //  Has this state already been reached?
        if( state == Started ) {
            postStartEvent();
            return;
        }

        //  Set the target state
        if( target < Started ) {
            setTargetState(Started);
        }

        //  Start on a separate thread
        Thread thread = new Thread() {
            public void run() {
                if( AbstractPlayer.this.getState() < Started ) {
                    synchronousStart();
                }
            }
        };

        getThreadQueue().addThread(thread);
    }


    /**
     * Adds a Controller to be controlled by this Player.
     *
     * @param      newController
     *             The Controller to add
     *
     * @exception  NotRealizedError
     *             If this Player or the new Controller are not
     *             Realized.
     *
     * @exception  ClockStartedError
     *             If this Player or the new Controller are in the
     *             Started state.
     *
     * @exception  IncompatibleTimeBaseException
     *             Thrown by newController.setTimeBase() if the
     *             new Controller cannot use this player's
     *             TimeBase
     */
    public synchronized void addController(Controller newController)
        throws IncompatibleTimeBaseException
    {
        //  Return immediately if the new Controller
        //  is already being managed by this Player

        if( controllers.contains(newController) ||
            this == newController )
        {
            return;
        }

        int currentState = getState();

        //  Enforce state reqs for this Player
        if( currentState == Unrealized ||
            currentState == Realizing )
        {
            throw new NotRealizedError(
                "Cannot add Controller to an Unrealized Player");
        }

        //  Enforce state reqs for this Player
        if(currentState == Started) {
            throw new ClockStartedError(
                "Cannot add Controller to a Started Player");
        }

        int controllerState = newController.getState();

        //  Enforce state reqs for new controller
        if( controllerState == Unrealized ||
            controllerState == Realizing )
        {
            throw new NotRealizedError(
                "Cannot add Unrealized Controller to a Player");
        }

        //  Enforce state reqs for this Player
        if(controllerState == Started) {
            throw new ClockStartedError(
                "Cannot add Started Controller to a Player");
        }

        //  Set the time base for the new Controller.  This may
        //  throw an IncompatibleTimeBaseException.
        newController.setTimeBase(getTimeBase());

        //  Stop any forward-transitions of this Player and the
        //  to-be-managed Controller.  This will allow us to
        //  stabilize and synchronize the two.  Also update the
        //  current states of each.

        stop();
        newController.stop();
        currentState = getState();
        controllerState = newController.getState();

        //  According to the API, if the new Controller is not
        //  Prefetched, and this Player is Prefetched or
        //  Prefetching, then this Player must be transitioned
        //  back to the Realized state.

        if( controllerState < Prefetched &&
            currentState > Realized )
        {
            deallocate();
        }
        
        //  Set the media time for the new Controller
        newController.setMediaTime( getMediaTime() );

        //  Reset the stop time for the new Controller.  It will
        //  be stopped automatically when this Player's stop time
        //  is reached.
        newController.setStopTime(Clock.RESET);

        //  Set the rate for the new Controller.  If the new
        //  Controller cannot accomodate the current rate, then
        //  the rate for all Controllers and this Player will be
        //  set to 1.0.

        float rate = getRate();
        if( rate != newController.setRate(rate) ) {
            newController.setRate(1.0F);
            setRate(1.0F);
        }

        //  Add the controller to the list
        controllers.addElement(newController);

        //  Add ourselves as a listener to the new Controller so
        //  we can tell when the Controller has completed an event
        //  transition
        newController.addControllerListener(this);

        //  Update the overall duration
        updateDuration();
    }

    /**
     * Remove a Controller from the list of Controllers managed by
     * this Player.
     *
     * @param      oldController
     *             The Controller to remove
     */
    public synchronized void removeController(Controller oldController) {
        int currentState = getState();

        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot remove Controller from an Unrealized Player");
        }

        if(currentState == Started) {
            throw new ClockStartedError(
                "Cannot remove Controller from a Started Player");
        }

        if( controllers.indexOf(oldController) == -1 ) {
            return;
        }

        //  Stop all transitions.  This is so that the
        //  doTransition methods don't need to synchronize on the
        //  controllers Vector.
        stop();

        //  This is already synchronized
        controllers.removeElement(oldController);

        //  Remove ourselves as a listener on this Controller
        oldController.removeControllerListener(this);

        //  Reset the timebase on the Controller
        try {
            oldController.setTimeBase(null);
        } catch(IncompatibleTimeBaseException e) {}

        //  Update the overall duration
        updateDuration();
    }

    /**
     * Gets the list of Controllers under control of this Player.
     */
    protected Vector getControllers() {
        return (Vector)controllers.clone();
    }

    /**
     * Set the GainControl for this AbstractPlayer.  If the
     * AbstractPlayer does not support audio media, this method
     * should return null.
     *
     * @param      c
     *             The GainControl allowing control of the volume
     *             of this AbstractPlayer's media.
     */
    protected void setGainControl(GainControl c) {
        if( gainControl != null ) {
            removeControl(gainControl);
        }
        addControl(c);
        gainControl = c;
    }

    /**
     * Get the Gain Control for this Player.
     *
     * @return     The GainControl object, or null if it has
     *             not been set.
     */
    public GainControl getGainControl() {
        int currentState = getState();

        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot get gain control on an Unrealized Player");
        }
        return gainControl;
    }

    /**
     * Set the visual Component for this AbstractPlayer.  If the
     * AbstractPlayer does not support video media, this method
     * should return null.
     *
     * @param      c
     *             A java.awt.Component on which the media is
     *             rendered.
     */
    protected void setVisualComponent(Component c) {
        visualComponent = c;
    }

    /**
     * Get the visual Component for this Player.
     *
     * @return     The visual Component, or null if it has
     *             not been set.
     */
    public Component getVisualComponent() {
        int currentState = getState();

        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot get visual Component on an Unrealized Player");
        }
        return visualComponent;
    }

    /**
     * Set the control panal Component for this AbstractPlayer.
     *
     * @param      c
     *             A java.awt.Component providing control over
     *             this AbstractPlayer's media.
     */
    protected void setControlPanelComponent(Component c) {
        controlPanelComponent = c;
    }

    /**
     * Get the control panel Component for this Player.
     *
     * @return     The control panel Component, or null if
     *             it has not been set.
     */
    public Component getControlPanelComponent() {
        int currentState = getState();

        if(currentState == Unrealized ||
           currentState == Realizing)
        {
            throw new NotRealizedError(
                "Cannot get control panel Component on an Unrealized Player");
        }

        return controlPanelComponent;
    }

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.MediaHandler methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Called by javax.media.Manager.  This is the litmus test
     * that tells whether this Player can support the given
     * DataSource.  If it can't, this method should throw a
     * IncompatibleSourceException.  Our only requirement here is
     * that the DataSource has not already been set.  Subclasses
     * may wish to override this method to extend the acceptance
     * criteria.
     *
     * @param      source
     *             The DataSource to test
     *
     * @exception  IncompatibleSourceException
     *             Thrown if the DataSource has already been set
     *             on this MediaHandler, or if the DataSource is
     *             not a PullDataSource
     */
    public void setSource(DataSource source)
        throws IncompatibleSourceException
    {
        //  Make sure the DataSource has only been set once
        if( this.source != null ) {
            throw new IncompatibleSourceException(
                "Datasource already set in MediaHandler " +
                    getClass().getName() );
        }

        this.source = source;
    }

    /**
     * Convenience method to get the DataSource for the Player.
     */
    public DataSource getSource() {
        return source;
    }

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.ControllerListener methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Used to monitor events posted by this Player's managed
     * Controllers.  By keeping track of the actions of each
     * Controller, the Player can synchronize the state
     * transtitions of multiple Controllers.
     * <p>
     * This method is final because the controller-management
     * functionality of the AbstractPlayer will not work if it is
     * overridden.
     *
     * @param      e
     *             The ControllerEvent posted by one of the
     *             managed Controllers.
     */
    public final void controllerUpdate(ControllerEvent e) {
        synchronized(controllers) {
            if( e instanceof TransitionEvent ) {
                controllers.notifyAll();
            } else

            if( e instanceof ControllerErrorEvent ) {
                setControllerError((ControllerErrorEvent)e);
                controllers.notifyAll();
            }
        }
    }


    ////////////////////////////////////////////////////////////
    //
    //  ejmf.toolkit.media.content.AbstractController methods
    //
    ////////////////////////////////////////////////////////////

    /**
     *  Realize player and all of its managed Controllers.
     *  Subclasses should override doPlayerRealize() to do the
     *  actual work to transition the Controller.
     *  <p>
     *  This method should not be called directly.  Instead, call
     *  realize().
     */
    public final boolean doRealize() {

        try {
            //  Initialte the data transfer
            source.start();
        }
        
        catch(IOException e) {
            postEvent(
                new ResourceUnavailableEvent(this,
                    "Could not start DataSource") );
            return false;
        }

        //  No multi-Controller management is needed here because
        //  all Controllers are guaranteed to be Realized when
        //  they are added to this Player.  Furthermore, the
        //  Player itself must be realized before any Controllers
        //  are added.

        if(! doPlayerRealize() ) {
            return false;
        }

        //  Now that the Player is realized, getDuration() may
        //  be more accurate.
        updateDuration();

        return true;
    }

    /**
     *  Prefetch player and all of its managed Controllers.
     *  Subclasses should override doPlayerPrefetch() to do the
     *  actual work to transition the Controller.
     *  <p>
     *  This method should not be called directly.  Instead, call
     *  prefetch().
     */
    public final boolean doPrefetch() {

        //  Synchronize on the controllers Vector so that
        //  Controllers cannot be added or deleted while we are
        //  transitioning them.

        resetControllerError();

        //  Prefetch each managed Controller
        for(int i = 0; i < controllers.size(); i++) {
            Controller c = (Controller)controllers.elementAt(i);
            c.prefetch();
        }
        
        //  Prefetch this Player.  If it fails, then assume a
        //  ControllerErrorEvent was posted and return false.

        if(! doPlayerPrefetch() ) {
            return false;
        }

        //  Now wait for our all of our managed Controllers to
        //  complete the state transition or post an error.
        //  controllerUpdate() will catch such events and
        //  notify us.

        synchronized(controllers) {
            while(  controllerError == null &&
                    ! isStateReached(Prefetched) )
            {
                try { controllers.wait(); }
                catch(InterruptedException e) {}
            }
        }

        if( controllerError != null ) {
            postManagedControllerErrorEvent();
            return false;
        }

        //  Now that the Player is prefetched, getDuration() may
        //  be more accurate.
        updateDuration();

        return true;
    }

    /**
     *  Start player and all of its managed Controllers at the
     *  given time.  Subclasses should override doPlayerSyncStart()
     *  to do the actual work to transition the Controller.
     *  <p>
     *  This method should not be called directly.  Instead, call
     *  syncStart().
     */
    public final boolean doSyncStart(Time t) {

        //  Synchronize on the controllers Vector so that
        //  Controllers cannot be added or deleted while we are
        //  transitioning them.

        resetControllerError();

        //  SyncStart each managed Controller
        for(int i = 0; i < controllers.size(); i++) {
            Controller c = (Controller)controllers.elementAt(i);
            c.syncStart(t);
        }
        
        //  SyncStart this Player.  If it fails, then assume a
        //  ControllerErrorEvent was posted and return false.
        if( ! doPlayerSyncStart(t) ) {
            return false;
        }

        //  Now wait for our all of our managed Controllers to
        //  complete the state transition or post an error.
        //  controllerUpdate() will catch such events and
        //  notify us.

        synchronized(controllers) {
            while(  controllerError == null &&
                    ! isStateReached(Started) )
            {
                try { controllers.wait(); }
                catch(InterruptedException e) {}
            }
        }

        if( controllerError != null ) {
            postManagedControllerErrorEvent();
            return false;
        }

        return true;
    }

    /**
     *  Deallocate player on current thread.  Subclasses should
     *  override doPlayerDeallocate() to do the actual work to
     *  transition the Controller.
     *  <p>
     *  This method should not be called directly.  Instead, call
     *  deallocate().
     */
    public final boolean doDeallocate() {

        //  Synchronize on the controllers Vector so that
        //  Controllers cannot be added or deleted while we are
        //  transitioning them.

        resetControllerError();

        //  If there is a large number of managed Controllers,
        //  then deallocating each synchronously could be time
        //  consuming.  Instead, spawn a thread to deallocate
        //  each controller.  Later on, after this Player has
        //  been deallocated, wait for each deallocating
        //  thread to finish before returning.
        
        int size = controllers.size();
        Thread[] threads = new Thread[size];

        for(int i = 0; i < size; i++) {
            final Controller c =
                (Controller)controllers.elementAt(i);

            threads[i] = new Thread() {
                public void run() {
                    c.deallocate();
                }
            };

            threads[i].start();
        } 

        //  Deallocate this Player.  If it fails, then assume a
        //  ControllerErrorEvent was posted and return false.
        if( ! doPlayerDeallocate() ) {
            return false;
        }

        //  Wait for each Controller to deallocate
        for(int i = 0; i < size; i++) {
            try { threads[i].join(); }
            catch(InterruptedException e) {}
        }
            
        //  Check for errors during deallocation
        if( controllerError != null ) {
            postManagedControllerErrorEvent();
            return false;
        }

        return true;
    }

    /**
     *  Stop player on current thread.  Subclasses should override
     *  doPlayerStop() to do the actual work to transition the
     *  Controller.
     *  <p>
     *  This method should not be called directly.  Instead, call
     *  stop().
     */
    public final boolean doStop() {

        //  Synchronize on the controllers Vector so that
        //  Controllers cannot be added or deleted while we are
        //  transitioning them.

        resetControllerError();

        //  If there is a large number of managed Controllers,
        //  then stopping each synchronously could be time
        //  consuming, and could leave each Controller stopped
        //  at different media times.  Instead, spawn a thread
        //  to stop each controller.  Later on, after this
        //  Player has been stopped, wait for each thread to
        //  finish before returning.
        
        int size = controllers.size();
        Thread[] threads = new Thread[size];

        for(int i = 0; i < size; i++) {
            final Controller c =
                (Controller)controllers.elementAt(i);

            threads[i] = new Thread() {
                public void run() {
                    c.stop();
                }
            };

            threads[i].start();
        } 

        //  Stop this Player.  If it fails, then assume a
        //  ControllerErrorEvent was posted and return false.
        if( ! doPlayerStop() ) {
            return false;
        }

        //  Wait for each Controller to stop
        for(int i = 0; i < size; i++) {
            try { threads[i].join(); }
            catch(InterruptedException e) {}
        }
        
        //  Check for errors while stoping
        if( controllerError != null ) {
            postManagedControllerErrorEvent();
            return false;
        }

        return true;
    }

    /**
     * Close the Player.  First close all Controllers under the
     * control of this Player.  Then release resources held by
     * this Player.  Subclasses should implement doPlayerClose()
     * to add additional functionality.
     * <p>
     * This method should not be called directly.  Instead, call
     * close().
     */
    public synchronized final void doClose() {
        Vector controllers = getControllers();

        //  Close all Controllers under the control of this Player
        for(int i = 0, n = controllers.size(); i < n; i++ ) {
            Controller c =
                (Controller)controllers.elementAt(i);
            c.close();
        }

        try {
            //  Stop the data-transfer
            source.stop();

            //  Disconnect the DataSource
            source.disconnect();
        }
        catch(IOException e) {}

        //  Call implementation-specific functionality
        doPlayerClose();

        //  Release as many resources as we can
        controllers = null;
        source = null;
        gainControl = null;
        duration = null;
        controllerError = null;
    }

    /**
     * Sets the media time for this Player and all of its managed
     * Controllers.
     * <p>
     * This method should not be called directly.  Instead, call
     * setMediaTime().
     *
     * @param      t
     *             The media time to set.
     */
    public synchronized final void doSetMediaTime(Time t) {
        //  First set the media time on all of the managed Controllers
        for(int i = 0, n = controllers.size(); i < n; i++ ) {
            Controller c =
                (Controller)controllers.elementAt(i);

            c.setMediaTime(t);
        }
        
        //  Set the media time on this Player
        doPlayerSetMediaTime(t);
    }
    
    /**
     * Sets the rate for this Player and all of its managed
     * Controllers.  If any of the above cannot accomodate the
     * given rate, then the rate is set to 1.0 for all of the
     * above.
     * <p>
     * This method should not be called directly.  Instead, call
     * setRate().
     *
     * @param      t
     *             The rate to set.
     */
    public synchronized final float doSetRate(float rate) {
        float actual;

        //  First set the rate on all of the managed Controllers
        for(int i = 0, n = controllers.size(); i < n; i++ ) {
            Controller c = (Controller)controllers.elementAt(i);
            actual = c.setRate(rate);

            if( rate != 1.0F && actual != rate ) {
                doSetRate(1.0F);
                return 1.0F;
            }
        }
        
        //  Set the rate on this Player
        actual = doPlayerSetRate(rate);

        if( ! controllers.isEmpty() && rate != 1.0F && actual != rate ) {
            doSetRate(1.0F);
            return 1.0F;
        }

        return actual;
    }
    
    ////////////////////////////////////////////////////////////
    //
    //  ejmf.toolkit.media.content.AbstractPlayer methods
    //
    ////////////////////////////////////////////////////////////

    /**
     * Sets the controller error that occurred while waiting for a
     * state transition.
     */
    private void setControllerError(ControllerErrorEvent e) {
        this.controllerError = e;
    }
    
    /**
     * Resets the ControllerErrorEvent.
     */
    private void resetControllerError() {
        setControllerError(null);
    }
    
    /**
     * Get the ControllerErrorEvent that occurred while waiting
     * for a state transition.
     */
    private ControllerErrorEvent getControllerError() {
        return controllerError;
    }
    
    /**
     * Checks to see if the given Controller state has been
     * reached in a forward-transitioning Controller.
     *
     * @param      state
     *             The desired state.
     *
     * @return     true if the state has been reached, false
     *             otherwise.
     */
    private boolean isStateReached(int state) {
        synchronized(controllers) {
            for(int i = 0, n = controllers.size(); i < n; i++) {
                Controller controller = (Controller)controllers.elementAt(i);
                if( controller.getState() < state ) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Checks to see if the all of this Player's managed
     * Controller have stopped.  Usually called from within
     * endOfMedia() to see if an EndOfMediaEvent can be posted.
     *
     * @return     true if all of this Player's managed
     *             Controller have stopped, false otherwise.
     */
    private boolean areControllersStopped() {
        synchronized(controllers) {
            for(int i = 0, n = controllers.size(); i < n; i++) {
                Controller controller = (Controller)controllers.elementAt(i);
                if( controller.getState() == Started ) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Post a ManagedControllerErrorEvent to the Media Event
     * Queue.  Automatically fill in the managing Player, the
     * ControllerErrorEvent, and the message properties of the
     * ManagedControllerErrorEvent.
     */
    private void postManagedControllerErrorEvent() {
        String message =
            "Managing Player " + getClass().getName() +
            " received ControllerErrorEvent from " +
            controllerError.getSourceController().getClass().getName();

        postEvent( new ManagedControllerErrorEvent(
            this, controllerError, message ) );

        resetControllerError();
    }

    /**
     * Indicates to the framework that the end of media has been
     * reached.  Marks the media time, sets the current and target
     * states to Prefetched, and posts an EndOfMediaEvent.
     *
     * @exception  ClockStoppedException
     *             If the AbstractController is not in the Started
     *             state.
     */
    protected void endOfMedia()
        throws ClockStoppedException
    {
        synchronized(controllers) {
            //  Wait for all of the managed Controllers to stop or
            //  post an error.  controllerUpdate() will catch such
            //  events and notify us.

            //  controllerError was reset when doSyncStart() was called

            while( ! areControllersStopped() )
            {
                try { controllers.wait(); }
                catch(InterruptedException e) {}
            }
        }

        super.endOfMedia();
    }

    ////////////////////////////////////////////////////////////
    //
    //  Synchronous state-changing methods
    //
    //  These routines are called indirectly from the
    //  TransitionQueueMonitor class by way of a
    //  TransitionThread.  When these methods are called, the
    //  following assumptions can be made:
    //  
    //  1.  The current state is less than the desired state.  If
    //      doSyncStart() is called, the Player is guaranteed to
    //      be in the Prefetched state.
    //  
    //  2.  The target state is greater than or equal to the
    //      desired state.
    //  
    //  3.  Any state-related exceptions have been thrown
    //      already.
    //  
    //  4.  There are no other state-changing threads running.
    //
    ////////////////////////////////////////////////////////////

    /**
     * Start the AbstractPlayer ASAP.
     * <p>
     * Synchronous method -- return when transition complete
     */
    protected void synchronousStart() {
        //  Does the controller need to be prefetched?
        if( getState() < Prefetched ) {
            synchronousPrefetch();
        }

        //  Start ASAP
        synchronousSyncStart( getTimeBase().getTime() );
    }
}
