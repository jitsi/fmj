package ejmf.toolkit.multiplayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.Player;
import javax.media.Time;
import javax.swing.Timer;

import ejmf.toolkit.util.Debug;
import ejmf.toolkit.util.QuickSort;

/**
* TimerMultiPlayerControl is a timer-based control mechanism
* for driving the operation of multiple players. Based on the
* start times and playing times of the Tracks associated with
* the MultiPlayer passed at construction, an 
* event list is created. This list is a
* time-ordered list of MixerEvents. The time value recorded
* with each MixerEvent is an offset from
* the previous event. A timer is set to expire at the offset
* of first event. When the timer expires, the MixerCommand
* is executed and the timer is reset to expire at the 
* next offset.
* <p>
* Note: As an implementation note, the Players coordinated
* by a TimerMultiPlayerControl do not need to share a TimeBase
* since that is effectively the role of the Timer used by
* TimerMultiPlayerControl.
*
* @see ejmf.toolkit.multiplayer.MixerEvent
* @see ejmf.toolkit.multiplayer.MixerCommand
*/

public class TimerMultiPlayerControl extends MultiPlayerControl
		implements ActionListener {
    private Timer		timer;
    private MixerEvent[]	eventList = null;
    private int			totalEventCount;
    private int			nextEvent; // next track scheduled to start
    private TrackList		tracks;

	/**
	*  Create a TimerMultiPlayerControl for MultiPlayer
	* passed as argument.
	* @param tracks A list of tracks to be controlled by
	* TimerMultiPlayerControl.
	*/
    public TimerMultiPlayerControl(TrackList tracks) {
	super(tracks);
	this.tracks = tracks;
    }

	/**
	* Close all the Players
	*/
    public void close() {
	int n = tracks.getNumberOfTracks();
	for (int i = 0; i < n; i++) {
	    Track track = tracks.getTrack(i);
  	    if (track.isAssigned()) {
		track.getPlayer().close();
	    }
	}
    }

	/**	
	* Initialize the Players. 
	* Note: This method is a no-op since Track 
	* prefetches Players.
	* @param Always return true.
	*/
    public boolean init() { return true; }

	/**
	* Update the TimerMultiPlayerControl with new
	* Track information. Initialize the event list
	* based on new start time and playing time information. 
	* @param tracks A list of Tracks. Control strategy is
	* updated based on information in these tracks.
	* @return true if update was successful, otherwise false.
	*/
    public boolean update(TrackList tracks) {
	initEventList(tracks);
	return true;
    }

	/**
	* Start the timer, fire at next event offset.
	*/
    public void start() {
	if (eventList == null)
	    initEventList(tracks);

	nextEvent = 0;

	// Fire all time = 0 events
	fireEventsAtOffset((long) 0);

   	startTimer();
    }

	/**
	*  Rewind Players and reposition control to start
	* of event list.
	*/
    public void restart() {
	nextEvent = 0;
	rewind();
	start();
    }

	/**
	* Reposition each Player to zero media time.
	*/
    public void rewind() {
	for (int i = 0; i < tracks.getNumberOfTracks(); i++) {
	    Track track = tracks.getTrack(i);
	    if (track.isAssigned()) {
	        Player player = track.getPlayer();
	        player.setMediaTime(new Time(0));
	    }
	}
    }

	/**
	* Stop the timer and stop any started Players.
	*/
    public void stop() {
	timer.stop();
	for (int i = 0; i < tracks.getNumberOfTracks(); i++) {
	    Track track = tracks.getTrack(i);
	    if (track.isAssigned()) {
	        Player player = track.getPlayer();
                if (player.getTargetState() == Controller.Started) {
		    player.stop();
	        }
	    }
	}
    }

	/* Used internally to start timer.
	*/
    private void startTimer() {
	int millis = (int) (eventList[nextEvent].getTime());
 	timer.setInitialDelay(millis);
	timer.start();
    }

	/* Used internally to initialize event list.
	* Real work is done by <tt>converToEventList</tt>
	*/
    private void initEventList(TrackList tracks) {
	nextEvent = 0;
	if (timer == null) {
	    timer = new Timer(0, this);
	    timer.setRepeats(false);
	}
	convertToEventList(tracks);
    }

    // The time of each event is modified to reflect an
    // offset from the previous event. The start latency
    // has already been factored in. In fact, it could have
    // resulted in a negative event time. Since the event time
    // at eventList[0] is the earliest start time, it is tested 
    // to see if it is negative.  If so, its absolute value is added 
    // to all offsets to accommodate the fact an event can't 
    // happen at t < 0.  In order to remain sync'd all tracks need 
    // to shift to adjust for shifted start of first event.
    
    private void normalizeEventTimes(MixerEvent[] eventList) {
	long timeDelta;
	long prevTime = eventList[0].getTime();
	
	if (prevTime < 0) {
	    eventList[0].setTime(0);
	}

	for (int i = 1; i < totalEventCount; i++) {

	    // Compute millisecond offset from previous event.
	    timeDelta = Math.abs(eventList[i].getTime() - prevTime);

	    prevTime = eventList[i].getTime();
	    eventList[i].setTime(timeDelta);
	}
    }

    // Iterates over Track array and creates necessary
    // Player start and stop events. In the process,
    // start-up latency is taken into account. This is done
    // before sorting of event list.

    private void convertToEventList(TrackList tracks) {
	Debug.printObject("enter convertToEventList");

	totalEventCount = 0;

	// We'll have at most two events per player, ie. start and stop
	eventList = new MixerEvent[tracks.getNumberOfTracks() * 2];

	for (int i = 0; i < tracks.getNumberOfTracks(); i++) {
	    long eventTime;
	    Track track = tracks.getTrack(i);

	    // If track  was deallocated, skip it.
	    if (track.isAvailable())
	 	continue;

	    Player player = track.getPlayer();
	    double eventSecs = track.getStartTime();

	    if (player.getStartLatency() != Controller.LATENCY_UNKNOWN) {
		double latentSecs =  player.getStartLatency().getSeconds();

		// Subtract latency to move back syncStart call to
		// accommodate it.
		// If this value < 0, normalizeEventTime will adjust
		eventSecs = eventSecs - latentSecs;
	    }
	    eventTime = (long)(eventSecs * 1000.0);

 	    eventList[totalEventCount++] =  
		new MixerEvent(new StartCommand(player), 
				eventTime);

	    double playingTime = track.getPlayingTime();

	    // If user set playingTime longer than actual duration of
	    // media, force playingTime to actual duration.

	    playingTime = Math.min(playingTime, 
				   player.getDuration().getSeconds()); 

            // Just to be safe, only create StopCommand event if
 	    // playing time > 0. Otherwise, let media run to EndOfMedia.

	    if (playingTime > 0) {
	        long endTime = 
		    (long)((track.getStartTime() + playingTime) * 1000);
	        eventList[totalEventCount++] = 
		    new MixerEvent( new StopCommand(player), 
				endTime);
	    }
	}
	if (totalEventCount > 0) {
	    QuickSort.sort(eventList, 0, totalEventCount-1);
	    normalizeEventTimes(eventList);
	}
	Debug.printObject("exit convertToEventList");
    }

    private void printEvent(MixerEvent me) {
	Debug.printObject( "time = " + me.getTime() + 
			" cmd = " + me.getCommand().toString());
    }

    private void printEventList(MixerEvent[] elist) {
	Debug.printObject("in printEventList");
	for (int i = 0; i < totalEventCount; i++)
	    printEvent(elist[i]);
	Debug.printObject("exit printEventList");
    }

    /**
     * The actionPerformed method is called in response
     * to timer ticks. When an event arrives, those events
     * scheduled to be fired and fired. Then timer is then
     * reset to fire at time of next scheduled event.
	* @param e A timer 'tick' appearing as an ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
	long fireTime = eventList[nextEvent].getTime();
        fireEventsAtOffset(fireTime);

	if (nextEvent < totalEventCount) {
	    int millis = (int) (eventList[nextEvent].getTime());
 	    timer.setInitialDelay(millis);
	    timer.restart();
	}
    }

    // Fire events scheduled for time offset passed an
    // as argument. A command is executed by calling
    // its execute method.

    private void fireEventsAtOffset(long offset) {
	// Fire event that begins events at this offset
	if (nextEvent < totalEventCount &&
		eventList[nextEvent].getTime() == offset) {
	    eventList[nextEvent].execute();
	    nextEvent++;

	    // Fire other events at 'same time', ie. offset == 0
	    // from previous event.
	    while (nextEvent < totalEventCount &&
		    eventList[nextEvent].getTime() == 0) {
	        eventList[nextEvent].execute();
	        nextEvent++;
	    }
	} 
    }

    // Helper method

    /* Force all Players in TrackList to realized state
     */
    private void forceRealized(TrackList tracks) {
	for (int i = 0; i < tracks.getNumberOfTracks(); i++) {
	    Track track = tracks.getTrack(i);
	    if (track.isAssigned()) {
	        Player player = track.getPlayer();
	        int state = player.getState();
	        if (state == Controller.Started) {
		    player.stop();
		}

	        if (state > Controller.Realized) {
		    player.deallocate();
		}
	    }
	}
     }

    protected void controllerUpdateHook(ControllerEvent event) {
    }
}

/*
* Encapsulate StopCommand
*/

class StopCommand implements MixerCommand {
    private Player 			player;

    public StopCommand(Player player) {
	this.player = player;
    }

	// Stop player
    public void execute() {
	player.stop();
    }

    public String toString() {
	return "Stop";
    }
}

/*
* Encapsulate StartCommand
*/

class StartCommand implements MixerCommand {
    private Player 			player;

    public StartCommand(Player player) {
	this.player = player;
    }

	// Start player "now"
    public void execute() {
	long now = player.getTimeBase().getNanoseconds();
	player.syncStart(new Time(now));
    }

    public String toString() {
	return "Start";
    }
}
