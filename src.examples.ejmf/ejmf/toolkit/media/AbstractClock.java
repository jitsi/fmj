package ejmf.toolkit.media;

import javax.media.Clock;
import javax.media.ClockStartedError;
import javax.media.ClockStoppedException;
import javax.media.IncompatibleTimeBaseException;
import javax.media.Manager;
import javax.media.StopTimeSetError;
import javax.media.Time;
import javax.media.TimeBase;

/**
 * The AbstractClock provides an abstract implementation of the
 * javax.media.Clock interface.  All methods are implemented
 * except for setStopTime() and getStopTime().  These methods will
 * invariably require implementation-specific functionality and
 * therefore are left to the subclasses to implement.
 *
 * @see        AbstractController
 * @see        StopTimeMonitor
 *
 * @author     Steve Talley & Rob Gordon
 */
public class AbstractClock
    implements Clock
{
    private TimeBase systemtimebase = Manager.getSystemTimeBase();
    private TimeBase timebase       = systemtimebase;
    private Time mediaStartTime     = new Time(0);
    private Time mediaStopTime      = Clock.RESET;
    private Time timeBaseStartTime;
    private float rate              = 1.0F;
    private boolean isStarted       = false;

    /**
     * Constructs an AbstractClock.
     */
    public AbstractClock() {
        super();
    }

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
        if(isStarted) {
            throw new ClockStartedError(
                "Cannot set time base on a Started Clock");
        }

        if( timebase == null ) {
            this.timebase = systemtimebase;
        } else {
            this.timebase = timebase;
        }
    }

    /**
     * Get the TimeBase that this Clock is using.
     */
    public synchronized TimeBase getTimeBase() {
        return timebase;
    }

    /**
     * Sets the media time.
     *
     * @param      t
     *             The media time to set
     *
     * @exception  ClockStartedError
     *             If the Clock is Started.
     */
    public synchronized void setMediaTime(Time t) {
        if(isStarted) {
            throw new ClockStartedError(
                "Cannot set media time on a Started Clock");
        }

        mediaStartTime = t;
    }

    /**
     * Get the media time the media is scheduled to start (if the
     * Clock is stopped), or the media time at which the Clock
     * started (if the Clock is started).
     */
    protected Time getMediaStartTime() {
        return mediaStartTime;
    }

    /**
     * Get the time-base time the media is scheduled to start (if
     * the Clock is stopped), or the time-base time at which the
     * Clock started (if the Clock is started).
     */
    protected Time getTimeBaseStartTime() {
        return timeBaseStartTime;
    }

    /**
     * Calculates the current media time based on the current
     * time-base time, the time-base start time, the media start
     * time, and the rate.
     *
     * @return     The current media time
     */
    public synchronized Time getMediaTime() {
        if( ! isStarted ) {
            //  If the Clock is stopped return it's starting
            //  media-time
            return mediaStartTime;
        } else {
            //  Calculate the media time
            return calculateMediaTime();
        }
    }

    /**
     * Get the media time in nanoseconds.
     */
    public synchronized long getMediaNanoseconds() {
        return getMediaTime().getNanoseconds();
    }

    /**
     * Calculates the media time on a started Clock based on the
       assumption that the Clock is started.
     */
    private synchronized Time calculateMediaTime() {
        long tbCurrent = timebase.getNanoseconds();
        long tbStart = timeBaseStartTime.getNanoseconds();

        //  If we are scheduled to start but haven't yet reached
        //  the scheduled start time, return the media start time
        if(tbCurrent < tbStart) {
            return mediaStartTime;
        }
        
        long mStart = mediaStartTime.getNanoseconds();
        long mCurrent =
            (long)((tbCurrent - tbStart)*rate + mStart);

        return new Time(mCurrent);
    }

    /**
     * Gets the time until the Clock's media synchronizes with its
     * time-base.
     *
     * @return      The time remaining until the time-base
     *              start-time if this Clock is Started and the
     *              time-base start-time has not yet been reached,
     *              or the media time otherwise.
     */
    public synchronized Time getSyncTime() {
        if(isStarted) {
            long startNano = timeBaseStartTime.getNanoseconds();
            long nowNano = getTimeBase().getNanoseconds();

            if( startNano >= nowNano ) {
                return new Time((long)(nowNano - startNano));
            }
        }

        return getMediaTime();
    }

    /**
     * Given a media time, returns the corresponding time-base
     * time.  Uses the current rate, the time-base start time, and
     * the media start time to calculate.
     *
     * @param      mediaStartTime
     *             A media time to be mapped to a time-base time.
     *
     * @return     A time-base time.
     *
     * @exception  ClockStoppedException
     *             If the clock has not started.
     */
    public synchronized Time mapToTimeBase(Time t)
        throws ClockStoppedException
    {
        if( ! isStarted ) {
            throw new ClockStoppedException(
                "Cannot map media time to time-base time on a Stopped Clock");
        }

        long mCurrent = t.getNanoseconds();
        long mStart = mediaStartTime.getNanoseconds();
        long tbStart = timeBaseStartTime.getNanoseconds();

        return new Time((long)
            (((mCurrent - mStart)/rate) + tbStart));
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
     * @exception  ClockStartedError
     *             If the Clock is Started.
     *
     * @return     The actual rate set.
     *
     */
    public synchronized float setRate(float rate) {
        if(isStarted) {
            throw new ClockStartedError(
                "Cannot set rate on a Started Clock");
        }

        if( rate != 0.0F ) {
            this.rate = rate;
        }

        return this.rate;
    }

    /**
     * Get the current temporal scale factor.  The scale factor
     * defines the relationship between the
     * <tt>Clock's</tt> <i>media time</i> and its
     * <tt>TimeBase</tt>.
     * <p>
     * For example, a rate of 2.0 indicates that <i>media time</i>
     * will pass twice as fast as the <tt>TimeBase</tt> time
     * once the <tt>Clock</tt> starts.  Similarly, a negative
     * rate indicates that the <tt>Clock</tt> runs in the
     * opposite direction of its <tt>TimeBase</tt>. All
     * <tt>Clocks</tt> are guaranteed to support a rate of
     * 1.0, the default rate.  <tt>Clocks</tt> are not
     * required to support any other rate.<p>
     */
    public synchronized float getRate() {
        return rate;
    }

    /**
     * Set the <i>media time</i> at which you want the
     * <tt>Clock</tt> to stop.  The <tt>Clock</tt> will
     * stop when its <i>media time</i> passes the stop-time.  To
     * clear the stop time, set it to: <tt>Clock.RESET</tt>.
     * <p>
     * You can always call <tt>setStopTime</tt> on a
     * <i>Stopped</i> <tt>Clock</tt>.
     * <p>
     * On a <i>Started</i> <tt>Clock</tt>, the stop-time can
     * only be set <I>once</I>.  A <tt>StopTimeSetError</tt> is
     * thrown if <tt>setStopTime</tt> is called and the
     * <i>media stop-time</i> has already been set.
     *
     * @param      mediaStopTime
     *             The time at which you want the
     *             <tt>Clock</tt> to stop, in <i>media
     *             time</i>.
     */
    public synchronized void setStopTime(Time mediaStopTime) {
        if( isStarted && this.mediaStopTime != RESET )
        {
            throw new StopTimeSetError(
                "Stop time may be set only once on a Started Clock");
        }

        this.mediaStopTime = mediaStopTime;
    }

    /**
     * Get the last value successfully set by setStopTime.
     * Returns the constant Clock.RESET if no stop time is set
     *
     * @return     The current stop time.
     */
    public synchronized Time getStopTime() {
        return mediaStopTime;
    }

    /**
     * syncStart the AbstractClock at the previously-
     * specified time-base start time.
     * <p>
     * Synchronous method -- return when transition complete
     */
    public synchronized void syncStart(Time t) {

        //  Enforce state prereqs
        if(isStarted) {
            throw new ClockStartedError(
                "syncStart() cannot be called on a started Clock");
        }

        long now = getTimeBase().getNanoseconds();
        long start = t.getNanoseconds();

        if( start - now > 0 ) {

            //  Start time is in the future

            //  Set the time-base start time
            this.timeBaseStartTime = new Time(start);

        } else {

            //  Start time is in the past

            //  Set the time-base start time to be now
            this.timeBaseStartTime = new Time(now);
        }

        isStarted = true;
    }

    /**
     * Stop the Clock.
     */
    public synchronized void stop() {
        if(isStarted) {
            mediaStartTime = calculateMediaTime();
//  Commented out -- is this necessary?
//          timeBaseStartTime = timebase.getTime();
            isStarted = false;
        }
    }
}
