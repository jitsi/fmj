package ejmf.toolkit.multiplayer;

import java.io.IOException;
import java.net.URL;

import javax.media.Controller;
import javax.media.Duration;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.Time;

import ejmf.toolkit.util.StateWaiter;

/** A Track maintains information on a Player over time.
 *  It does this in two ways. First, it simply provides a
 *  repository for Player data, such as start time and stop
 *  stop time. Additionally, it does a 'look-ahead', moving
 *  the Player to prefetched state, obtaining information that
 *  is only valid at that point like duration and start latency.
 *  By treating a Player as a Track, you don't have to worry 
 *  Player is in proper state to get that information. 
 *  <p>
 *  The Player is moved into prefetched with a call to
 *  <tt>setPlayer</tt>. This method is called at construction
 *  when the version of the constructor that takes a Player argument
 *  is called.
 *
 *  Since Track does not implement ControllerListener, if you want a 
 *  track to be more active and listen to its Player, you will need to 
 *  subclass Track.
 * 
 *  A Track may have a view associated with it. This is
 *  typically a slider and can be set with setTrackSlider
 *  method.
 *
 *  Each Track has a track number assigned upon creation.
 */
 
public class Track {
    private Player	player = null;

    private int			trackNumber;
    private MediaLocator	mediaLocator = null;

    private Time	duration;	// in seconds;
    private double	startTime;	// in seconds;
    private double	playingTime;	// in seconds;
    private long	latency;	// in nanos, once prefetched
    private int		state;		// either started or stopped

    private boolean	isAvailable;	

    private TrackSlider	trackSlider = null;

    /** Create a Track from a Player, including its media file
      * (or string representation of URL).
      *
      *  @param trackNumber Assigned track number
      *  @param player	    Player associated with Track
      *  @param fileName    Media file name of string representation of URL
      */
    public Track(int trackNumber, MediaLocator ml, Player player) {
	this.trackNumber = trackNumber;
	this.mediaLocator = ml;
	startTime = 0;
	duration = Duration.DURATION_UNKNOWN;
    	isAvailable = false;	
	setPlayer(player);
    }

    /** Create a track with an assigned track number. Other relevant
     *  data is set later.
     *
     *  @param trackNumber Assigned track number
     */
    public Track(int trackNumber) {
	this.trackNumber = trackNumber;
	mediaLocator = new MediaLocator("unassigned");
	player = null;
	startTime = 0;
	duration = Duration.DURATION_UNKNOWN;
	isAvailable = true;
    }

    /** Assign a Player to this Track. The Player
     *  is put in the Prefetch state so that it can
     *  get from the Player all the info it needs about it.
     *  The Player is moved to prefetched using StateWaiter's
     *  blockingPrefetch.
     *  If a Player is assigned to this track already, it
     *  it is closed.
     *
     *  @param player A javax.media.Player
     *  @see ejmf.toolkit.util.StateWaiter
     */
    public void setPlayer(Player player) {
	
	if (this.player != null) {
	    close();
	}
	this.player = player;
	startTime = 0;
	TrackSlider ts = getTrackSlider();
	if (ts != null) {
	   ts.setEnabled(true);
	}
	// Set the player up so that all info can be accessed.
	StateWaiter sw = new StateWaiter(player);
	sw.blockingPrefetch();

	duration = player.getDuration();
	if (duration == Duration.DURATION_UNKNOWN ||
	    duration == Duration.DURATION_UNBOUNDED) {
	    	playingTime = -1;
	} else {
	    playingTime = player.getDuration().getSeconds();
	}

	Time lat = player.getStartLatency();
	if (lat == Controller.LATENCY_UNKNOWN) {
	    latency = 0;
	} else {
	    latency = lat.getNanoseconds();
	}

	setAvailable(false);
    }
 
    /** Close Track. Stop and deallocate player if necessary,
     *  finally, closing the Player.
     *
     */
    public void close() {
	if (player.getState() == Controller.Started) {
	    player.stop();
	}
	player.deallocate();
	player.close();
	trackSlider.setEnabled(false);
	setAvailable(true);
    }

    /** Get Player associated with Track
	* @return A player associated with Track
     */
    public Player getPlayer() {
	return player;
    }
   
    /** Get track number associated with Track
	* @param the index of Track
     */
    public int getTrackNumber() {
	return trackNumber;
    }

    /** Set MediaLocator of Player associated with Track
     * @param ml MediaLocator of Player associated with Track
     */
    public void setMediaLocator(MediaLocator ml) {
	mediaLocator = ml;
    }

    /** Get media locator of Player associated with Track
     * @return MediaLocator of Player associated with Track
     */
    public MediaLocator getMediaLocator() {
	return mediaLocator;
    }

    /** Get media file of Player associated with Track
     * @return The URL associated with the Track's MediaLocator.
     */
    public URL getMediaURL() {
	URL url = null;
	try {
	    url = mediaLocator.getURL();
	} catch (Exception e) { }
	return url;
    }

    /** Set playing time of Player associated with Track
     *  This is different than duration, ie. it may be shorter.
     * @param seconds Playing time in milliseconds
     */
    public void setPlayingTime(double seconds) {
	playingTime = seconds;
    }

    /** Get playing time of Player associated with Track
     * @return Playing time in milliseconds
     */
    public double getPlayingTime() {
	return playingTime;
    }

    /**
     *  Get the total duration of the media associated with this
     *  track. This value can not be changed. 
     * @return A javax.media.Time object representing duration of Player
     */
    public Time getDuration() {
	return duration;
    }
	
    /** Set start time of Player associated with Track
     *  @param startTime  Offset in milliseconds at which Player
     *  should be started.
     *  
     */
    public void setStartTime(double startTime) {
	this.startTime = startTime;
    }

    /** Get start time of Player associated with Track
     *  @return Offset in milliseconds at which Player
     *  is to be started.
     */
    public double getStartTime() {
	return startTime;
    }

    /** Get start latency of Player associated with Track
     * @return Startup latency of Player associated with this
     *  track. If Player reports UNKNOWN, getLatency will report
     *  zero.
     */
    public long getLatency() {
	return latency;
    }

    /** Tracks can be used for different Players. This method
     *  reports whether this Track is available for use. A
     *  Track may is available after its close method is called.
     *  Additionally, a Track may be created without an associated
     *  Player. In that case also isAvailable returns true.
	* @return true if Track is available.
     */
    public synchronized boolean isAvailable() {
	return isAvailable;
    }

    /** @return true if this Track has a Player assigned to it.
     */
    public boolean isAssigned() {
	return !isAvailable();
    }

    /** Make a Track available
	* @param flag Availability of track is set to value of 
	* argument.
     */

    synchronized void setAvailable(boolean flag) {
	isAvailable = flag;
    }

    // For accessing the view
    /** Set slider view. 
	* @param trackSlider Display representation of Track
     */
    public void setTrackSlider(TrackSlider trackSlider) {
	this.trackSlider = trackSlider;
    }

    /** Get slider view. 
	* @return Display representation of Track
     */
    public TrackSlider getTrackSlider() {
	return trackSlider;
    }

    public void setState(int state) {
	this.state = state;
    }

    public int getState() {
	return state;
    }

	/**	
	* Create and return a Track from input values.
	* @param i The index of Track
	* @param ml The MediaLocator for locating Player
	* @param startTime start time offset in milliseconds
	* @param playingTime playing time in milliseconds
	* @exception IOException thrown if DataSource can not
	* be connected to.
	* @exception NoPlayerException thrown if no handler exists
	* for Player
	*/
    public static Track createTrack(int i, 
			MediaLocator ml, 
			double startTime, 
			double playingTime) 
		throws IOException, NoPlayerException {

        Player p = Manager.createPlayer(ml);
        Track track = new Track(i);
        track.setPlayer(p);
        track.setMediaLocator(ml);
        track.setStartTime(startTime);
        track.setPlayingTime(playingTime);
	return track;
    }

}
