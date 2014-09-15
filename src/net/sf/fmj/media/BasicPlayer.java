package net.sf.fmj.media;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;

import net.sf.fmj.media.control.*;
import net.sf.fmj.media.util.*;

/**
 * BasicPlayer implements the bases of a javax.media.Player. It handles all the
 * Player state transitions, event handling and management of any Controller
 * under its control.
 */
public abstract class BasicPlayer extends BasicController implements Player,
        ControllerListener, DownloadProgressListener
{
    // private MediaSource source = null;
    protected javax.media.protocol.DataSource source = null;
    protected Vector controllerList = new Vector();
    private Vector optionalControllerList = new Vector();
    private Vector removedControllerList = new Vector();
    private Vector currentControllerList = new Vector();
    private Vector potentialEventsList = null;
    private Vector receivedEventList = new Vector();
    private boolean receivedAllEvents = false;
    private Vector configureEventList = new Vector();
    private Vector realizeEventList = new Vector();
    private Vector prefetchEventList = new Vector();
    private Vector stopEventList = new Vector();

    private Controller restartFrom = null;
    private Vector eomEventsReceivedFrom = new Vector();
    private Vector stopAtTimeReceivedFrom = new Vector();
    private PlayThread playThread = null;
    private StatsThread statsThread = null;
    private Time duration = DURATION_UNKNOWN;
    private Time startTime, mediaTimeAtStart;
    private boolean aboutToRestart = false;
    private boolean closing = false;
    private boolean prefetchFailed = false;
    protected boolean framePositioning = true;

    protected Control[] controls = null;
    protected Component controlComp = null;

    // Information controls
    public SliderRegionControl regionControl = null;

    protected CachingControl cachingControl = null;
    protected ExtendedCachingControl extendedCachingControl = null;
    protected BufferControl bufferControl = null;

    private Object startSync = new Object();
    private Object mediaTimeSync = new Object();

    long lastTime = 0;

    static final int LOCAL_STOP = 0;

    static final int STOP_BY_REQUEST = 1;

    static final int RESTARTING = 2;

    public BasicPlayer()
    {
        configureEventList.addElement("javax.media.ConfigureCompleteEvent");
        configureEventList.addElement("javax.media.ResourceUnavailableEvent");

        realizeEventList.addElement("javax.media.RealizeCompleteEvent");
        realizeEventList.addElement("javax.media.ResourceUnavailableEvent");

        prefetchEventList.addElement("javax.media.PrefetchCompleteEvent");
        prefetchEventList.addElement("javax.media.ResourceUnavailableEvent");

        stopEventList.addElement("javax.media.StopEvent");
        stopEventList.addElement("javax.media.StopByRequestEvent");
        stopEventList.addElement("javax.media.StopAtTimeEvent");
        stopThreadEnabled = false;
    }

    /**
     * Called when the prefetch() is aborted, i.e. deallocate() was called while
     * prefetching. Release all resources claimed previously by the prefetch
     * call.
     */
    @Override
    protected final void abortPrefetch()
    {
        if (controllerList != null)
        {
            int i = controllerList.size();
            while (--i >= 0)
            {
                Controller c = (Controller) controllerList.elementAt(i);
                c.deallocate();
            }
        }
        synchronized (this)
        {
            notify();
        }
    }

    /**
     * Called when the realize() is aborted, i.e. deallocate() was called while
     * realizing. Release all resources claimed previously by the realize()
     * call.
     */
    @Override
    protected final void abortRealize()
    {
        if (controllerList != null)
        {
            int i = controllerList.size();
            while (--i >= 0)
            {
                Controller c = (Controller) controllerList.elementAt(i);
                c.deallocate();
            }
        }
        synchronized (this)
        {
            notify();
        }
    }

    /**
     * Assume control of another Controller. A Player can accept responsibility
     * for controlling another Controller. Once a Controller has been added this
     * Player will:
     * <ul>
     * <li>Slave the Controller to the Player's time-base.
     * <li>Use the Controller in the Player's computation of start latency. The
     * value the Player returns in its <b>getStartLatency</b> method is the
     * larger of: <b>getStartLatency</b> before the Controller was added, or
     * <b>getStartLatency</b> of the Controller.
     * <li>Pass along, as is appropriate, events that the Controller generates.
     * <li>Invoke all Controller methods on the Controller.
     * <li>For all asynchronous methods (realize, prefetch) a completion event
     * will not be generated until all added Controllers have generated
     * completion events.
     * </ul>
     * <p>
     *
     * <b>Note:</b> It is undefined what will happen if a Controller is under
     * the control of a Player and any of the Controller's methods are called
     * outside of the controlling Player.
     *
     * @param newController
     *            the Controller this Player will control.
     * @exception IncompatibleTimeBaseException
     *                thrown if the new controller will not accept the player's
     *                timebase.
     */
    public synchronized void addController(Controller newController)
            throws IncompatibleTimeBaseException
    {
        int playerState = getState();

        if (playerState == Started)
        {
            throwError(new ClockStartedError(
                    "Cannot add controller to a started player"));
        }

        if ((playerState == Unrealized) || (playerState == Realizing))
        {
            throwError(new NotRealizedError(
                    "A Controller cannot be added to an Unrealized Player"));
        }

        if (newController == null || newController == this)
            return;

        int controllerState = newController.getState();
        if ((controllerState == Unrealized) || (controllerState == Realizing))
        {
            throwError(new NotRealizedError(
                    "An Unrealized Controller cannot be added to a Player"));
        }

        if (controllerList.contains(newController))
        {
            return;
        }

        if (playerState == Prefetched)
        {
            if ((controllerState == Realized)
                    || (controllerState == Prefetching))
            {
                // System.out.println("Calling deallocate");
                deallocate(); // Transition back to realized state
            }
        }

        manageController(newController);

        // Synchronize the players.
        newController.setTimeBase(getTimeBase());
        newController.setMediaTime(getMediaTime());
        newController.setStopTime(getStopTime());

        if (newController.setRate(getRate()) != getRate())
        {
            // The slave does not support the master's rate.
            // We'll reset everything back to rate 1.0.
            setRate(1.0f);
        }
    }

    /**
     * Return true if the player is currently playing media with an audio track.
     *
     * @return true if the player is playing audio.
     */
    protected abstract boolean audioEnabled();

    /**
     * Check if the given rate configureable supports the given rate. if not,
     * returns the closest match.
     */
    float checkRateConfig(RateConfigureable rc, float rate)
    {
        RateConfiguration config[] = rc.getRateConfigurations();
        if (config == null)
            return 1.0f;

        RateConfiguration c;
        RateRange rr;
        float corrected = 1.0f;
        for (int i = 0; i < config.length; i++)
        {
            rr = config[i].getRate();
            if (rr != null && rr.inRange(rate))
            {
                rr.setCurrentRate(rate);
                corrected = rate;
                c = rc.setRateConfiguration(config[i]);
                if (c != null && (rr = c.getRate()) != null)
                    corrected = rr.getCurrentRate();
                break;
            }
        }
        return corrected;
    }

    /**
     * Called as a last step to complete the configure call.
     */
    @Override
    protected void completeConfigure()
    {
        super.completeConfigure();
        synchronized (this)
        {
            notify();
        }
    }

    /**
     * Called as a last step to complete the prefetch call.
     */
    @Override
    protected void completePrefetch()
    {
        super.completePrefetch();
        synchronized (this)
        {
            notify();
        }
    }

    /**
     * Called as a last step to complete the realize call.
     */
    @Override
    protected void completeRealize()
    {
        state = Realized;
        try
        {
            slaveToMasterTimeBase(getMasterTimeBase());
        } catch (IncompatibleTimeBaseException e)
        {
            Log.error(e);
        }
        super.completeRealize();
        synchronized (this)
        {
            notify();
        }
    }

    /**
     * This is for subclass to access Controller's implementation of
     * setStopTime.
     */
    protected void controllerSetStopTime(Time t)
    {
        super.setStopTime(t);
    }

    /**
     * This is for subclass to access Controller's implementation of stopAtTime.
     */
    protected void controllerStopAtTime()
    {
        super.stopAtTime();
    }

    /**
     * This get called when some Controller notifies this player of any event.
     */
    final public void controllerUpdate(ControllerEvent evt)
    {
        processEvent(evt);
    }

    /**
     * Check the given controller to see if it's busy or not. Needs to be
     * overridden by subclass. The subclass method should change the master
     * timebase if necessary. It should handle audio only or video only tracks
     * properly when the device is busy.
     *
     * @return true if the given controller is usable; false if the controller
     *         cannot be used.
     */
    protected boolean deviceBusy(BasicController mc)
    {
        return true;
    }

    /**
     * This is called when close() is invoked on the Player. close() takes care
     * of the general behavior before invoking doClose(). Subclasses should
     * implement this only if it needs to do something specific to close the
     * player.
     */
    @Override
    protected void doClose()
    {
        synchronized (this)
        {
            closing = true;
            notifyAll();
        }

        if (getState() == Controller.Started)
        {
            // Stop everything first.
            stop(LOCAL_STOP);
        }

        // Ask all its controllers to close themselves.
        if (controllerList != null)
        {
            Controller c;
            while (!controllerList.isEmpty())
            {
                c = (Controller) controllerList.firstElement();
                c.close();
                controllerList.removeElement(c);
            }
        }

        // Close the ui components.
        // if (controlComp != null)
        // ((DefaultControlPanel)controlComp).dispose();
        controlComp = null;

        if (statsThread != null)
            statsThread.kill();

        sendEvent(new ControllerClosedEvent(this));
    }

    /**
     * The stub function (invoked from configure()) to perform the steps to
     * configure the player. It performs the following:
     * <ul>
     * <li>call configure() on each controller managed by this player.
     * <li>wait for ConfigureCompleteEvent from each controller;
     * </ul>
     * Subclasses are allowed to override doConfigure(). But this should be done
     * in caution. Subclass should also invoke super.doConfigure(). This is
     * called from a separately running thread.
     *
     * @return true if successful.
     */
    @Override
    protected synchronized boolean doConfigure()
    {
        potentialEventsList = configureEventList; // List of potential events
                                                  // for the
                                                  // configure() method
        resetReceivedEventList(); // Reset list of received events
        receivedAllEvents = false;
        currentControllerList.removeAllElements();

        int i = controllerList.size();
        while (--i >= 0)
        {
            Controller c = (Controller) controllerList.elementAt(i);
            if (c.getState() == Unrealized
                    && (c instanceof Processor || c instanceof BasicController))
            {
                currentControllerList.addElement(c);
            }
        }

        i = currentControllerList.size();
        while (--i >= 0)
        {
            Controller c = (Controller) currentControllerList.elementAt(i);
            if (c instanceof Processor)
                ((Processor) c).configure();
            else if (c instanceof BasicController)
                ((BasicController) c).configure();
        }

        if (!currentControllerList.isEmpty())
        {
            try
            {
                while (!closing && !receivedAllEvents)
                    wait();
            } catch (InterruptedException e)
            {
            }
            currentControllerList.removeAllElements();
        }

        // Make sure all the controllers are in in Configured State.
        // If not, it means that configure failed on one or more controllers.
        // Currenly, if configure fails then you get a ResourceUnavailableEvent
        // instead of RealizeCompleteEvent

        i = controllerList.size();
        while (--i >= 0)
        {
            Controller c = (Controller) controllerList.elementAt(i);
            if ((c instanceof Processor || c instanceof BasicController)
                    && c.getState() < Configured)
            {
                Log.error("Error: Unable to configure " + c);
                source.disconnect();
                return false;
            }
        }

        // subclass will implement this to configure up the signal graph.
        return true;
    }

    /**
     * Called when configure fails.
     */
    @Override
    protected void doFailedConfigure()
    {
        super.doFailedConfigure();
        synchronized (this)
        {
            notify();
        }
        close();
    }

    /**
     * Called when prefetch fails.
     */
    @Override
    protected void doFailedPrefetch()
    {
        super.doFailedPrefetch();
        synchronized (this)
        {
            notify();
        }
    }

    /**
     * Called when realize fails.
     */
    @Override
    protected void doFailedRealize()
    {
        super.doFailedRealize();
        synchronized (this)
        {
            notify();
        }
        close();
    }

    /**
     * The stub function to perform the steps to prefetch the controller. This
     * will call prefetch() on every controller in the controller list and wait
     * for their completion events. This is called from a separately running
     * thread.
     *
     * @return true if successful.
     */
    @Override
    protected/* synchronized */boolean doPrefetch()
    {
        potentialEventsList = prefetchEventList; // List of potential events for
                                                 // the
                                                 // prefetch() method
        resetReceivedEventList(); // Reset list of received events
        receivedAllEvents = false;
        currentControllerList.removeAllElements();

        Vector list = controllerList;

        if (list == null)
        {
            return false;
        }

        int i = list.size();
        while (--i >= 0)
        {
            Controller c = (Controller) list.elementAt(i);
            if (c.getState() == Realized)
            {
                currentControllerList.addElement(c);
                c.prefetch();
            }
        }
        if (!currentControllerList.isEmpty())
        {
            synchronized (this)
            {
                try
                {
                    while (!closing && !receivedAllEvents)
                        wait();
                } catch (InterruptedException e)
                {
                }
                currentControllerList.removeAllElements();
            }
        }

        // Make sure all the controllers are in in Prefetched State.
        // If not, it means that prefetch failed on one or more controllers.
        // Currenly, if prefetch fails then you get a ResourceUnavailableEvent
        // instead of PrefetchCompleteEvent

        i = list.size();
        while (--i >= 0)
        {
            Controller c = (Controller) list.elementAt(i);
            if (c.getState() < Prefetched)
            {
                Log.error("Error: Unable to prefetch " + c + "\n");
                if (optionalControllerList.contains(c))
                {
                    // System.out.println(c +
                    // " Controller is optional... continuing");
                    removedControllerList.addElement(c);
                } else
                {
                    // Notify the play thread which could still be waiting.
                    synchronized (this)
                    {
                        prefetchFailed = true;
                        notifyAll();
                    }
                    return false;
                }
            }
        }
        if (removedControllerList != null)
        {
            i = removedControllerList.size();
            while (--i >= 0)
            {
                Object o = removedControllerList.elementAt(i);
                controllerList.removeElement(o);
                ((BasicController) o).close();
                if (!deviceBusy((BasicController) o))
                {
                    // Notify the play thread which could still be waiting.
                    synchronized (this)
                    {
                        prefetchFailed = true;
                        notifyAll();
                    }
                    return false; // prefetch failed
                }
            }
            removedControllerList.removeAllElements();
            // $ System.err.println("final list of controllers: " + list);
        }

        return true;
    }

    /**
     * The stub function (invoked from configure()) to perform the steps to
     * configure the player. It performs the following:
     * <ul>
     * <li>call realize() on each controller managed by this player.
     * <li>wait for RealizeCompleteEvent from each controller;
     * </ul>
     * Subclasses are allowed to override doRealize(). But this should be done
     * in caution. Subclass should also invoke super.doRealize(). This is called
     * from a separately running thread.
     *
     * @return true if successful.
     */
    @Override
    protected synchronized boolean doRealize()
    {
        potentialEventsList = realizeEventList; // List of potential events for
                                                // the
                                                // realize() method
        resetReceivedEventList(); // Reset list of received events
        receivedAllEvents = false;
        currentControllerList.removeAllElements();

        int i = controllerList.size();
        while (--i >= 0)
        {
            Controller c = (Controller) controllerList.elementAt(i);
            if (c.getState() == Unrealized || c.getState() == Configured)
            {
                currentControllerList.addElement(c);
            }
        }

        i = currentControllerList.size();
        while (--i >= 0)
        {
            Controller c = (Controller) currentControllerList.elementAt(i);
            c.realize();
        }

        if (!currentControllerList.isEmpty())
        {
            try
            {
                while (!closing && !receivedAllEvents)
                    wait();
            } catch (InterruptedException e)
            {
            }
            currentControllerList.removeAllElements();
        }

        // Make sure all the controllers are in in Realized State.
        // If not, it means that realize failed on one or more controllers.
        // Currenly, if realize fails then you get a ResourceUnavailableEvent
        // instead of RealizeCompleteEvent

        i = controllerList.size();
        while (--i >= 0)
        {
            Controller c = (Controller) controllerList.elementAt(i);
            if (c.getState() < Realized)
            {
                Log.error("Error: Unable to realize " + c);
                source.disconnect();
                return false;
            }
        }

        updateDuration();

        statsThread = new StatsThread(this);
        statsThread.start();

        // subclass will implement this to connect up the signal graph.
        return true;
    }

    /**
     * Called from setMediaTime. This is used for subclasses to add in their own
     * behavior.
     *
     * @param now
     *            the target media time.
     */
    @Override
    protected void doSetMediaTime(Time now)
    {
    }

    @Override
    protected float doSetRate(float factor)
    {
        return factor;
    }

    private void doSetStopTime(Time t)
    {
        getClock().setStopTime(t);
        Vector list = controllerList;
        int i = list.size();
        while (--i >= 0)
        {
            Controller c = (Controller) controllerList.elementAt(i);
            c.setStopTime(t);
        }
    }

    /**
     * Invoked by start() or syncstart(). Called from a separate thread called
     * TimedStart thread. subclasses can override this method to implement its
     * specific behavior.
     */
    @Override
    protected void doStart()
    {
    }

    /**
     * The stop() method calls doStop() so that subclasses can add additional
     * behavior.
     */
    @Override
    protected void doStop()
    {
    }

    public void downloadUpdate()
    {
        if (extendedCachingControl == null)
            return;

        // It will be nice if we can avoid the cast to BasicController
        sendEvent(new CachingControlEvent(this, cachingControl,
                cachingControl.getContentProgress()));

        if (regionControl == null)
            return;

        long contentLength = cachingControl.getContentLength();
        int maxValuePercent;
        if ((contentLength == javax.media.protocol.SourceStream.LENGTH_UNKNOWN)
                || (contentLength <= 0))
        {
            maxValuePercent = 0;
        } else
        {
            long endOffset = extendedCachingControl.getEndOffset();
            maxValuePercent = (int) ((100.0 * endOffset) / contentLength);
            if (maxValuePercent < 0)
                maxValuePercent = 0;
            else if (maxValuePercent > 100)
                maxValuePercent = 100;
        }
        regionControl.setMinValue(0);
        regionControl.setMaxValue(maxValuePercent);
    }

    public String getContentType()
    {
        if (source != null)
            return source.getContentType();
        return null;

    }

    /**
     * Return the list of BasicControllers supported by this Player.
     *
     * @return a vector of the BasicControllers supported by this Player.
     */
    public final Vector getControllerList()
    {
        return controllerList;
    }

    /**
     * Get the Component with the default user interface for controlling this
     * player. If this player has no default control panel null is returned.
     * Subclasses should override this method and return the control panel
     * component but call this method first to ensure that the restrictions on
     * player methods are enforced.
     *
     * @return the default control panel GUI.
     */
    public Component getControlPanelComponent()
    {
        int state = getState();
        if (state < Realized)
        {
            throwError(new NotRealizedError(
                    "Cannot get control panel component on an unrealized player"));
        }
        // if (controlComp == null) {
        // controlComp = new DefaultControlPanel( this );
        // }
        return controlComp;
    }

    /**
     * Return the list of controls from its slave controllers plus the ones that
     * this player supports.
     *
     * @return the list of controls supported by this player.
     */
    @Override
    public Control[] getControls()
    {
        if (controls != null)
            return controls;

        // build the list of controls. It is the total of all the
        // controls from each controllers plus the ones that are maintained
        // by the player itself (e.g. playbackControl).

        Vector cv = new Vector();

        if (cachingControl != null)
            cv.addElement(cachingControl);

        if (bufferControl != null)
            cv.addElement(bufferControl);

        Control c;
        Object cs[];
        Controller ctrller;
        int i, size = controllerList.size();
        for (i = 0; i < size; i++)
        {
            ctrller = (Controller) controllerList.elementAt(i);
            cs = ctrller.getControls();
            if (cs == null)
                continue;
            for (int j = 0; j < cs.length; j++)
            {
                cv.addElement(cs[j]);
            }
        }

        Control ctrls[];
        size = cv.size();
        ctrls = new Control[size];

        for (i = 0; i < size; i++)
        {
            ctrls[i] = (Control) cv.elementAt(i);
        }

        // If the player has already been realized, we'll save what
        // we've collected this time. Then next time, we won't need
        // to go through this expensive search again.
        if (getState() >= Realized)
            controls = ctrls;

        return ctrls;
    }

    /**
     * Get the duration of the movie.
     *
     * @return the duration of the movie.
     */
    @Override
    public Time getDuration()
    {
        long t;
        if ((t = getMediaNanoseconds()) > lastTime)
        {
            lastTime = t;
            updateDuration();
        }
        return duration;
    }

    /**
     * Get the object for controlling audio gain. Return null if this player
     * does not have a GainControl (e.g. no audio).
     *
     * @return the GainControl object for this player.
     */
    public GainControl getGainControl()
    {
        int state = getState();
        if (state < Realized)
        {
            throwError(new NotRealizedError(
                    "Cannot get gain control on an unrealized player"));
        } else
        {
            return (GainControl) getControl("javax.media.GainControl");
        }
        return null;
    }

    /**
     * This should be implemented by the subclass. The subclass method should
     * return the master TimeBase -- the TimeBase that all other controllers
     * slave to. Use SystemTimeBase if unsure.
     *
     * @return the master time base.
     */
    protected abstract TimeBase getMasterTimeBase();

    public MediaLocator getMediaLocator()
    {
        if (source != null)
            return source.getLocator();
        return null;

    }

    private Vector getPotentialEventsList()
    {
        return potentialEventsList;
    }

    /**
     * Return the list of received events
     */
    private Vector getReceivedEventsList()
    {
        return receivedEventList;
    }

    /**
     * Get the DataSource used by this player.
     *
     * @return the DataSource used by this player.
     */
    protected javax.media.protocol.DataSource getSource()
    {
        return source;
    }

    @Override
    public Time getStartLatency()
    {
        super.getStartLatency();

        Time latency;
        long t = 0;

        // Find the longest start latency from all the slave Controllers.
        for (int i = 0; i < controllerList.size(); i++)
        {
            Controller c = (Controller) controllerList.elementAt(i);
            latency = c.getStartLatency();

            if (latency == LATENCY_UNKNOWN)
                continue;

            if (latency.getNanoseconds() > t)
                t = latency.getNanoseconds();
        }

        if (t == 0)
            return LATENCY_UNKNOWN;

        return new Time(t);
    }

    /**
     * Get the Component this player will output its visual media to. If this
     * player has no visual component (e.g. audio only) getVisualComponent()
     * will return null. Subclasses should override this method and return the
     * visual component but call this method first to ensure that the
     * restrictions on player methods are enforced.
     *
     * @return the media display component.
     */
    public Component getVisualComponent()
    {
        int state = getState();
        if (state < Realized)
        {
            throwError(new NotRealizedError(
                    "Cannot get visual component on an unrealized player"));
        }
        return null;
    }

    /**
     * Return true if the player is about to restart again.
     */
    public boolean isAboutToRestart()
    {
        return aboutToRestart;
    }

    /**
     * A player is not configurable.
     */
    @Override
    protected boolean isConfigurable()
    {
        return false;
    }

    /**
     * Will return true if the player can do frame positioning. Hack for now,
     * should be removed when players actually implement the framePositioning
     * control.
     */
    public boolean isFramePositionable()
    {
        return framePositioning;
    }

    /**
     * Add a Controller to the list of Controllers under this Player's
     * management. This is a protected method use only by subclasses. Use
     * addController() for public access.
     */
    protected final void manageController(Controller controller)
    {
        manageController(controller, false);
    }

    /**
     * Add a Controller to the list of Controllers under this Player's
     * management.
     */
    protected final void manageController(Controller controller,
            boolean optional)
    {
        if (controller != null)
        {
            if (!controllerList.contains(controller))
            {
                controllerList.addElement(controller);
                if (optional)
                    optionalControllerList.addElement(controller);
                controller.addControllerListener(this);
            }
        }
        updateDuration();
    }

    private/* synchronized */void notifyIfAllEventsArrived(
            Vector controllerList, Vector receivedEventList)
    {
        if ((receivedEventList != null)
                && (receivedEventList.size() == currentControllerList.size()))
        {
            receivedAllEvents = true;
            resetReceivedEventList(); // Reset list of received events
            synchronized (this)
            {
                notifyAll();
            }
        }
    }

    /**
     * This method gets run in a separate thread called PlayThread
     */
    final synchronized void play()
    {
        boolean status;

        // If a deallocate() happens before this thread gets to run.
        // or if a stop() happens before this thread gets to run.
        if (getTargetState() != Started)
        {
            return;
        }

        prefetchFailed = false;

        // The following completed checks should be looked at seriously.
        // It should be something like (state < Prefetched) etc.
        // It's too late for 2.1.1 release. We'll leave it this way. --ivg

        int state = getState();
        if ((state == Unrealized) || (state == Configured)
                || (state == Realized))
        {
            prefetch();
        }
        while (!closing
                && !prefetchFailed
                && (getState() == Configuring || getState() == Realizing
                        || getState() == Realized || getState() == Prefetching))
        {
            try
            {
                wait();
            } catch (InterruptedException e)
            {
            }
        }

        if (getState() != Started && getTargetState() == Started
                && getState() == Prefetched)
        {
            syncStart(getTimeBase().getTime());
        }
    }

    protected final void processEndOfMedia()
    {
        super.stop();
        sendEvent(new EndOfMediaEvent(this, Started, Prefetched,
                getTargetState(), getMediaTime()));
    }

    protected/* synchronized */void processEvent(ControllerEvent evt)
    {
        Controller source = evt.getSourceController();

        if (evt instanceof AudioDeviceUnavailableEvent)
        {
            sendEvent(new AudioDeviceUnavailableEvent(this));
            return;
        }

        // If this is a closed event triggered by one of the
        // managed controllers, not triggered by the player,
        // then we'll need to programmtically close all the
        // controllers and the player itself.
        if (evt instanceof ControllerClosedEvent && !closing
                && controllerList.contains(source)
                && !(evt instanceof ResourceUnavailableEvent))
        {
            // The source of the error event should have been closed
            // already. So we'll just remove it from the list of
            // managed controllers.
            controllerList.removeElement(source);

            if (evt instanceof ControllerErrorEvent)
                sendEvent(new ControllerErrorEvent(this,
                        ((ControllerErrorEvent) evt).getMessage()));
            close();
        }

        //
        // Send SizeChangeEvent down to Player
        //
        if ((evt instanceof SizeChangeEvent) && controllerList.contains(source))
        {
            // System.err.println("width = " +
            // ((SizeChangeEvent)evt).getWidth());
            // System.err.println("height = " +
            // ((SizeChangeEvent)evt).getHeight());
            sendEvent(new SizeChangeEvent(this,
                    ((SizeChangeEvent) evt).getWidth(),
                    ((SizeChangeEvent) evt).getHeight(),
                    ((SizeChangeEvent) evt).getScale()));
            return;
        }

        //
        // Send UnsupportedFormatEvent down to Player
        //
        /*
         * if ( (evt instanceof UnsupportedFormatEvent) &&
         * controllerList.contains(source) ) { // System.err.println("Reason = "
         * + ((UnsupportedFormatEvent)evt).toString()); sendEvent(new
         * UnsupportedFormatEvent(this,
         * ((UnsupportedFormatEvent)evt).getFormat())); return; }
         */

        // If we get a DurationUpdateEvent from one of the controllers,
        // update the duration of the player
        if ((evt instanceof DurationUpdateEvent)
                && controllerList.contains(source))
        {
            updateDuration();
            return;
        }

        // HANGS.
        // if ((evt instanceof RestartingEvent) &&
        // controllerList.contains(source)) {
        // System.out.println("MP: Got RestartingEvent from " + source);
        // stop(LOCAL_STOP); // Stop without sending any stop event
        // sendEvent(new RestartingEvent(this, Started, Prefetching, Started,
        // getMediaTime()));
        // }

        // So I am handling RestartingEvent this way
        if ((evt instanceof RestartingEvent) && controllerList.contains(source))
        {
            restartFrom = source;
            int i = controllerList.size();
            super.stop(); // Added
            setTargetState(Prefetched); // necessary even if super.stop is
                                        // called.

            for (int ii = 0; ii < i; ii++)
            {
                Controller c = (Controller) controllerList.elementAt(ii);
                if (c != source)
                {
                    c.stop();
                }
            }
            super.stop();
            // doStop(); // Allow subclasses to extend the behavior
            sendEvent(new RestartingEvent(this, Started, Prefetching, Started,
                    getMediaTime()));
        }

        if ((evt instanceof StartEvent) && (source == restartFrom))
        {
            restartFrom = null;
            // $$ TODO: Should probably send PrefetchCompleteEvent
            start();
        }

        if ((evt instanceof SeekFailedEvent) && controllerList.contains(source))
        {
            int i = controllerList.size();
            super.stop(); // Added
            setTargetState(Prefetched); // necessary even if super.stop is
                                        // called.

            for (int ii = 0; ii < i; ii++)
            {
                Controller c = (Controller) controllerList.elementAt(ii);
                if (c != source)
                {
                    c.stop();
                }
            }
            /*
             * super.stop(); setMediaTime(new Time(0)); start();
             */
            sendEvent(new SeekFailedEvent(this, Started, Prefetched,
                    Prefetched, getMediaTime()));
        }

        if ((evt instanceof EndOfMediaEvent) && controllerList.contains(source))
        {
            if (eomEventsReceivedFrom.contains(source))
            {
                return;
            }

            eomEventsReceivedFrom.addElement(source);
            if (eomEventsReceivedFrom.size() == controllerList.size())
            {
                super.stop();
                sendEvent(new EndOfMediaEvent(this, Started, Prefetched,
                        getTargetState(), getMediaTime()));
            }
            return;
        }

        if ((evt instanceof StopAtTimeEvent) && controllerList.contains(source)
                && (getState() == Started))
        {
            synchronized (stopAtTimeReceivedFrom)
            {
                if (stopAtTimeReceivedFrom.contains(source))
                    return;

                stopAtTimeReceivedFrom.addElement(source);

                boolean allStopped = (stopAtTimeReceivedFrom.size() == controllerList
                        .size());

                if (!allStopped)
                {
                    // Now check if the other controllers have already EOM'ed.
                    allStopped = true;
                    for (int i = 0; i < controllerList.size(); i++)
                    {
                        Controller c = (Controller) controllerList.elementAt(i);
                        if (!stopAtTimeReceivedFrom.contains(c)
                                && !eomEventsReceivedFrom.contains(c))
                        {
                            allStopped = false;
                            break;
                        }
                    }
                }

                if (allStopped)
                {
                    super.stop();
                    doSetStopTime(Clock.RESET);
                    sendEvent(new StopAtTimeEvent(this, Started, Prefetched,
                            getTargetState(), getMediaTime()));
                }
                return;

            } // synchronized stopAtTimeReceivedFrom
        }

        if ((evt instanceof CachingControlEvent)
                && controllerList.contains(source))
        {
            CachingControl mcc = ((CachingControlEvent) evt)
                    .getCachingControl();
            sendEvent(new CachingControlEvent(this, mcc,
                    mcc.getContentProgress()));
            return;
        }

        Vector eventList = potentialEventsList;

        if (controllerList != null && controllerList.contains(source)
                && eventList != null
                && eventList.contains(evt.getClass().getName()))
        {
            updateReceivedEventsList(evt);
            notifyIfAllEventsArrived(controllerList, getReceivedEventsList());
        }
    }

    /**
     * Stop controlling a Controller.
     *
     * @param oldController
     *            the Controller to stop controlling.
     */
    public final synchronized void removeController(Controller oldController)
    {
        int state = getState();

        if (state < Realized)
        {
            throwError(new NotRealizedError(
                    "Cannot remove controller from a unrealized player"));
        }

        if (state == Started)
        {
            throwError(new ClockStartedError(
                    "Cannot remove controller from a started player"));
        }

        if (oldController == null)
            return;

        if (controllerList.contains(oldController))
        {
            controllerList.removeElement(oldController);
            oldController.removeControllerListener(this);
            updateDuration();
            // Reset the controller to its default time base.
            try
            {
                oldController.setTimeBase(null);
            } catch (IncompatibleTimeBaseException e)
            {
            }
        }
    }

    /**
     * Resets the list of received events
     */
    private void resetReceivedEventList()
    {
        if (receivedEventList != null)
            receivedEventList.removeAllElements();
    }

    /**
     * Set the upper bound of the media time.
     *
     * @param t the duration in nanoseconds.
     */
    @Override
    protected void setMediaLength(long t)
    {
        duration = new Time(t);
        super.setMediaLength(t);
    }

    /**
     * Loops through the list of controllers maintained by this player and
     * invoke setMediaTime on each of them. This is a "final" method and cannot
     * be overridden by subclasses.
     *
     * @param now
     *            the target media time.
     */
    @Override
    public final void setMediaTime(Time now)
    {
        if (state < Realized)
            throwError(new NotRealizedError(MediaTimeError));

        // Set Media time from EOM and user click on the slider is
        // trampling on one another. Causing the player to hang
        // this mediaTimeSync will guard against that.
        synchronized (mediaTimeSync)
        {
            if (syncStartInProgress())
                return;

            if (getState() == Controller.Started)
            {
                aboutToRestart = true;
                stop(RESTARTING);
            }

            // If source is Positionable, we'll take care of this
            // at the top level.
            if (source instanceof Positionable)
                now = ((Positionable) source).setPosition(now,
                        Positionable.RoundDown);

            super.setMediaTime(now);

            int i = controllerList.size();
            while (--i >= 0)
            {
                ((Controller) controllerList.elementAt(i)).setMediaTime(now);
            }

            // For subclasses to add in their own behavior.
            doSetMediaTime(now);

            if (aboutToRestart)
            {
                syncStart(getTimeBase().getTime());
                aboutToRestart = false;
            }
        }
    }

    /**
     * Set the playback rate on the player. It loops through its list of
     * controllers and invoke setRate on each of them.
     */
    @Override
    public float setRate(float rate)
    {
        if (state < Realized)
        {
            throwError(new NotRealizedError(
                    "Cannot set rate on an unrealized Player."));
        }

        // Verify to see if the DataSource supports that rate.
        if (source instanceof RateConfigureable)
            rate = checkRateConfig((RateConfigureable) source, rate);

        float oldRate = getRate();

        if (oldRate == rate)
            return rate;

        if (getState() == Controller.Started)
        {
            aboutToRestart = true;
            stop(RESTARTING);
        }

        float rateSet; // Actual rate set
        if (!trySetRate(rate))
        {
            if (!trySetRate(oldRate))
            { // try to go back to the oldRate
                trySetRate(1.0F); // try setRate(1.0) which shouldn't fail
                rateSet = 1.0F;
            } else
            {
                rateSet = oldRate;
            }
        } else
        {
            rateSet = rate;
        }
        super.setRate(rateSet);

        if (aboutToRestart)
        {
            syncStart(getTimeBase().getTime());
            aboutToRestart = false;
        }
        return rateSet;
    }

    /**
     * Set the DataSource that provides the media for this player. BasicPlayer
     * only supports PullDataSource by default. Subclasses can override this
     * method to support other DataSources.
     *
     * @param source
     *            of media for this player.
     * @exception IOException
     *                thrown when an i/o error occurs in reading information
     *                from the data source.
     * @exception IncompatibleSourceException
     *                thrown if the Player can't use this source.
     */
    public void setSource(javax.media.protocol.DataSource source)
            throws IOException, IncompatibleSourceException
    {
        this.source = source;

        try
        {
            cachingControl = (CachingControl) source
                    .getControl(CachingControl.class.getName());
            if ((cachingControl != null)
                    && (cachingControl instanceof ExtendedCachingControl))
            {
                extendedCachingControl = (ExtendedCachingControl) cachingControl;
                if (extendedCachingControl != null)
                {
                    // update progress every 100 kilobytes
                    regionControl = new SliderRegionControlAdapter();
                    extendedCachingControl.addDownloadProgressListener(this,
                            100);
                }
            }
        } catch (ClassCastException e)
        {
        }
    }

    @Override
    public void setStopTime(Time t)
    {
        if (state < Realized)
        {
            throwError(new NotRealizedError(
                    "Cannot set stop time on an unrealized controller."));
        }

        if (getClock().getStopTime() == null
                || getClock().getStopTime().getNanoseconds() != t
                        .getNanoseconds())
        {
            sendEvent(new StopTimeChangeEvent(this, t));
        }

        doSetStopTime(t);
    }

    /**
     * Assigns a timebase for the player. If the BasicPlayer plays back audio,
     * the timebase can be none other than the master timebase as returned by
     * getMasterTimeBase(). This is to ensure that we won't set to a timebase
     * the audio cannot handle at this point. If the playback is video only, the
     * timebase can be set to any timebase desired.
     *
     * @param tb
     *            time base to be used by the Player.
     * @exception IncompatibleTimeBaseException
     *                thrown when a time base other than the master time base is
     *                set when audio is enabled.
     */
    @Override
    public void setTimeBase(TimeBase tb) throws IncompatibleTimeBaseException
    {
        TimeBase oldTimeBase = getMasterTimeBase();

        if (tb == null)
            tb = oldTimeBase;

        Controller c = null;
        int i;

        if (controllerList != null)
        {
            try
            {
                i = controllerList.size();
                while (--i >= 0)
                {
                    c = (Controller) controllerList.elementAt(i);
                    c.setTimeBase(tb);
                }
            } catch (IncompatibleTimeBaseException e)
            {
                // SetTimeBase had failed on one Controller. Some
                // controllers may have already assigned the new timeBase
                // We'll need to reverse that now.
                Controller cx;
                i = controllerList.size();
                while (--i >= 0)
                {
                    cx = (Controller) controllerList.elementAt(i);
                    if (cx == c)
                        break;
                    cx.setTimeBase(oldTimeBase);
                }
                Log.dumpStack(e);
                throw e;
            }
        }

        super.setTimeBase(tb);
    }

    /**
     * Slave all the controllers to the master time base. The controllers should
     * be in realized or greater state This differs from the setTimeBase() as it
     * loops through each controllers and call setTimeBase on each of them.
     *
     * @param tb
     *            the time base to be used by all controllers.
     * @exception IncompatibleTimeBaseException
     *                thrown if any controller will not accept the player's
     *                timebase.
     */
    protected void slaveToMasterTimeBase(TimeBase tb)
            throws javax.media.IncompatibleTimeBaseException
    {
        // $$ System.out.println("slaveToMasterTimeBase: master timebase is " +
        // tb);
        // $ System.out.println("Setting master " + tb + " on " + this);
        this.setTimeBase(tb); // For the player
    }

    /**
     * Start the Player as soon as possible. Start attempts to transition the
     * player into the started state. If the player has not been realized, or
     * prefetched, then the equivalent of those actions will occur, and the
     * appropriate events will be generated. If the implied realize or prefetch
     * fail, a failure event will be generated and the Player will remain in one
     * of the non-started states.
     * <p>
     * This is a "final" method. Subclasses should override doStart() to
     * implement its own specific behavior.
     */
    public final void start()
    {
        synchronized (startSync)
        {
            if (restartFrom != null)
            {
                return;
            }

            if (getState() == Started)
            {
                sendEvent(new StartEvent(this, Started, Started, Started,
                        mediaTimeAtStart, startTime));
                return; // ignored according to JMF spec.
            }
            if ((playThread == null) || (!playThread.isAlive()))
            {
                setTargetState(Started);

                playThread = new PlayThread(this);
                playThread.start();

            } else
            {
                // $$$$
                // System.out.print("WARNING: playThread is alive. start ignored");
                // // $$$
                // System.out.println(": MP State: " + getState());
            }

        } // startSync
    }

    /**
     * Stop the player. If current state is Started, sends stop() to all the
     * managed controllers, and waits for a StopEvent from all of them. It then
     * sends a StopEvent to any listener(s).
     */
    @Override
    public final/* synchronized */void stop()
    {
        stop(STOP_BY_REQUEST);
    }

    private/* synchronized */void stop(int stopType)
    {
        int state;

        switch (state = getState())
        {
        case Unrealized:
        case Realized:
        case Prefetched:
            setTargetState(state);
            break;
        case Realizing:
            setTargetState(Realized);
            break;
        case Prefetching:
        case Started:
            setTargetState(Prefetched);
            break;
        }

        if (getState() != Started)
        {
            switch (stopType)
            {
            case STOP_BY_REQUEST:
                sendEvent(new StopByRequestEvent(this, getState(), getState(),
                        getTargetState(), getMediaTime()));
                break;
            case RESTARTING:
                sendEvent(new RestartingEvent(this, getState(), getState(),
                        Started, getMediaTime()));
                break;
            default:
                sendEvent(new StopEvent(this, getState(), getState(),
                        getTargetState(), getMediaTime()));
                break;
            }

        } else if (getState() == Started)
        {
            synchronized (this)
            {
                // List of potential events for stop()
                potentialEventsList = stopEventList;
                // Reset list of received events
                resetReceivedEventList();
                receivedAllEvents = false;
                currentControllerList.removeAllElements();

                int i = controllerList.size();
                while (--i >= 0)
                {
                    Controller c = (Controller) controllerList.elementAt(i);
                    currentControllerList.addElement(c);
                    c.stop();
                }
                if (currentControllerList == null)
                    return;
                if (!currentControllerList.isEmpty())
                {
                    try
                    {
                        while (!closing && !receivedAllEvents)
                            wait();
                    } catch (InterruptedException e)
                    {
                    }
                    currentControllerList.removeAllElements();
                }
                super.stop();
                // doStop(); // Allow subclasses to extend the behavior

                switch (stopType)
                {
                case STOP_BY_REQUEST:
                    sendEvent(new StopByRequestEvent(this, Started, getState(),
                            getTargetState(), getMediaTime()));
                    break;
                case RESTARTING:
                    sendEvent(new RestartingEvent(this, Started, getState(),
                            Started, getMediaTime()));
                    break;
                default:
                    sendEvent(new StopEvent(this, Started, getState(),
                            getTargetState(), getMediaTime()));
                    break;
                }
            }
        }
    }

    /**
     * Stop because stop time has been reached. Subclasses should override this
     * method.
     */
    @Override
    protected void stopAtTime()
    {
        // We'll overwrite the parent method which stops the controller.
        // For the player, we don't have to do anything in particular
        // since the controllers are supposed to stop themselves.
    }

    /**
     * Start at the given time base time. This overrides Clock.syncStart() and
     * obeys all its semantics.
     * <p>
     * This is a "final" method. Subclasses should override doStart() to
     * implement its own specific behavior.
     *
     * @param tbt
     *            the time base time to start the player.
     */
    @Override
    public final void syncStart(Time tbt)
    {
        /**
         * To guard against conflict with setMediaTime.
         */
        synchronized (mediaTimeSync)
        {
            if (syncStartInProgress())
                return;

            int state = getState();

            if (state == Started)
            {
                throwError(new ClockStartedError(
                        "syncStart() cannot be used on an already started player"));
            }

            if (state != Prefetched)
            {
                throwError(new NotPrefetchedError(
                        "Cannot start player before it has been prefetched"));
            }

            // Clear the EOM and StopAtTime lists.
            eomEventsReceivedFrom.removeAllElements();
            stopAtTimeReceivedFrom.removeAllElements();

            setTargetState(Started);

            int i = controllerList.size();
            // The start(tbt) will throw a NotPrefetchedError if
            // a controller is not in Prefetched state
            while (--i >= 0)
            {
                if (getTargetState() == Started)
                { // ADDED
                    ((Controller) controllerList.elementAt(i)).syncStart(tbt);
                }
            }

            if (getTargetState() == Started)
            { // ADDED
              // If control comes here, the controllers
              // are in Started state.
                startTime = tbt;
                mediaTimeAtStart = getMediaTime();
                super.syncStart(tbt); // To start the clock and set the state to
                                      // Started
            }
        }
    }

    private boolean trySetRate(float rate)
    {
        int i = controllerList.size();

        while (--i >= 0)
        {
            Controller c = (Controller) controllerList.elementAt(i);
            if (c.setRate(rate) != rate)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove a Controller from the list of Controllers under this Player's
     * management. This is a protected method use only by subclasses. Use
     * removeController() for public access.
     */
    public final void unmanageController(Controller controller)
    {
        if (controller != null)
            if (controllerList.contains(controller))
            {
                controllerList.removeElement(controller);
                controller.removeControllerListener(this);
            }
    }

    protected synchronized void updateDuration()
    {
        Time oldDuration = duration;
        duration = DURATION_UNKNOWN;
        for (int i = 0; i < controllerList.size(); i++)
        {
            Controller c = (Controller) controllerList.elementAt(i);
            Time dur = c.getDuration();
            if (dur.equals(DURATION_UNKNOWN))
            {
                if (!(c instanceof BasicController))
                {
                    duration = DURATION_UNKNOWN;
                    break;
                }
            } else if (dur.equals(DURATION_UNBOUNDED))
            {
                duration = DURATION_UNBOUNDED;
                break;
            } else
            {
                if (duration.equals(DURATION_UNKNOWN))
                    duration = dur;
                else if (duration.getNanoseconds() < dur.getNanoseconds())
                    duration = dur;
            }
        }
        if (duration.getNanoseconds() != oldDuration.getNanoseconds())
        {
            setMediaLength(duration.getNanoseconds());
            sendEvent(new DurationUpdateEvent(this, duration));
        }
    }

    /**
     * Updates the list of received events. Sources are stored.
     */
    private void updateReceivedEventsList(ControllerEvent event)
    {
        if (receivedEventList != null)
        {
            Controller source = event.getSourceController();

            if (receivedEventList.contains(source))
            {
                // System.out.println("DUPLICATE " + event +
                // " received from: " + source);
                return;
            }
            receivedEventList.addElement(source);
        }
    }

    /**
     * This is being called from a looping thread to update the stats.
     */
    abstract public void updateStats();

    /**
     * Return true if the player is currently playing media with a video track.
     *
     * @return true if the player is playing video.
     */
    protected abstract boolean videoEnabled();

    /*************************************************************************
     * INNER CLASSES
     *************************************************************************/
}

// PlayThread and StatsThread are no longer inner classes
class PlayThread extends MediaThread
{
    BasicPlayer player;

    public PlayThread(BasicPlayer player)
    {
        this.player = player;
        setName(getName() + " (PlayThread)");
        useControlPriority();
    }

    @Override
    public void run()
    {
        player.play();
    }
}

class StatsThread extends LoopThread
{
    BasicPlayer player;
    int pausecount = -1;

    public StatsThread(BasicPlayer p)
    {
        this.player = p;
    }

    @Override
    protected boolean process()
    {
        try
        {
            Thread.sleep(1000);
        } catch (Exception e)
        {
        }

        // Check to see if the thread was killed.
        // If so exits.
        if (!waitHereIfPaused())
            return false;

        if (player.getState() == Controller.Started)
        {
            pausecount = -1;
            player.updateStats();
        } else if (pausecount < 5)
        {
            pausecount++;
            player.updateStats();
        }

        return true;
    }

}
