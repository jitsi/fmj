package ejmf.toolkit.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.event.EventListenerList;

/**
 * The SourcedTimer class implements a timer dependent
 * on a named source. This is a generalization of a timer simply dependent
 * on a monotonically increasing clock. 
 * 
 * An instance of SourcedTimer creates a java.swing.Timer and becomes
 * a listener on that timer. This timer is called the base timer.
 * Every time the base timer fires, the SourcedTimer object asks its 
 * source what time it is and then notifies its listeners.
 *
 * This class is used by the TimerPlayer to track media time.  
 *
 * @see            java.awt.swing.Timer
 * @see		   ejmf.toolkit.TimeSource
 * @see		   ejmf.examples.timerplayer.TimerPlayer
 * @version        1.0
 * @author         Rob Gordon & Steve Talley
 */
public class SourcedTimer implements ActionListener {
    /**
     * How often in milliseconds the baseTimer triggers
     */
    protected static int 	_defaultGran = 1000;
    /**
     * The source from which the SourcedTimer gets its idea of time
     */
    private TimeSource		source;
    /**
     * The base timer
     */
    private Timer 		baseTimer;
    /**
     * These are listeners on the SourcedTimer
     */
    private EventListenerList 	listenerList = null;
    /**
     * This event is sent to all of my listeners
     */
    private SourcedTimerEvent 	event;
    /**
     * Flag that tracks whether base timer is running.
     */
    private boolean 		started = false;

    // This needs to be global since it is used by inner class
    private Object[]		listeners;

    /**
     * Create a SourcedTimer for the given source using 
     * default granularity.
     *
     * @param          src
     *                 An object that implements the TimeSource interface.
     */
    public SourcedTimer(TimeSource src) {
	this(src, _defaultGran);
    }

    /**  
     *  Create a SourcedTimer for the given source. Use the Timer
     *  passed as an argument for the base timer.
     *
     *  @param		src
     * 			An object that implements the TimeSource interface.
     *  @param		timer
     *			A java.swing.Timer object for use as base timer.
     */
     public SourcedTimer(TimeSource src, Timer timer) {
	source = src;
	event = new SourcedTimerEvent(this, 0);
	baseTimer = timer;
     }

    /**
     * Create a SourcedTimer for the given source with the specified
     * granularity.
     *
     * @param          src
     *                 An object that implements the TimeSource interface.
     * @param          granularity
     *                 Periood in milliseconds that base timer should fire.
     */
    public SourcedTimer(TimeSource src, int granularity) {
	source = src;
	event = new SourcedTimerEvent(this, 0);
	baseTimer = new Timer(granularity, this);
	baseTimer.setInitialDelay(0);
    }

    /**
     * Start the timer. The associated base timer is started if
     * it is not already running.
     *
     */
    public void start() {
	if (started == false) {
	    baseTimer.start();	
	    runNotifyThread(0);
	}
    }

    /**
     * Stop the timer. The associated base timer is stopped.
     *
     */
    public void stop() {
	started = false;
	baseTimer.stop();

	// Force one last notification. This call acts
        // like a 'flush' event and ensues a final event
        // occurs when stopping Timer. We do it after stop
        // in case it takes a while.
	runNotifyThread(source.getTime());
    }

    /**
     * Called in response to an ActionEvent from the associated
     * base timer. Nominally this method is called every granularity
     * milliseconds.
     *
     * @param          e
     *                 ActionEvent from base timer.
     */
    public void actionPerformed(ActionEvent e) {
	runNotifyThread(source.getTime());
    }

    /**
     * Starts a thread that is responsible for notifying any
     * listeners on this SourcedTimer. For each listener,
     * its timerUpdate method is called.
     *
     * @param          nsecs
     *                 Time in nanoseconds from base timer.
     */
    private void runNotifyThread(long nsecs) {
	event.setTime(nsecs);
	listeners = listenerList.getListenerList();
	Thread nThread = new Thread() {
	    public void run() {
		// This is canonical means for traversing a
		// Listener list, notifying listeners
		for (int i = listeners.length-2; i >= 0; i -= 2)
		    if (listeners[i] == SourcedTimerListener.class)
		        ((SourcedTimerListener)listeners[i+1]).timerUpdate(event);
            }
	};
	nThread.start();
    }

    /**
     * Add a listener to this object.
     *
     * @param          l
     *                 An object that implements SourcedTimerListener interface.
     */
    public void addSourcedTimerListener(SourcedTimerListener l) {
	if (listenerList == null)
	    listenerList = new EventListenerList();
	listenerList.add(SourcedTimerListener.class, l);
    }

    /**
     * A client of SourcedTimer may need to convert source timer time
     * from raw units to some other units for display purposes.
     * This method is available to listeners who have a reference
     * to a SourcedTimer but not necessarily the source itself. 
     * It simply delegates to the source and asks what number are
     * raw units divider by to arrive at seconds. 
     *
     *
     * @return         A number used to divide base timer time
     * 			for conversion to seconds.
     */
    public long getConversionDivisor() {
	return source.getConversionDivisor();
    }
}
