package ejmf.toolkit.gui.multiimage;

import java.awt.Dimension;

import javax.swing.JLabel;

/**
 * Displays a series of MultiImageFrames.  This could be used to
 * display a slideshow, animation, etc.
 *
 * @see        MultiImageFrames
 *
 * @author     Steve Talley
 */
public class MultiImageRenderer extends JLabel implements Runnable {
    /**
     * The number of nanoseconds in a second.
     */
    public static long SEC_TO_NANO = 1000000000L;
    /**
     * The number of nanoseconds in a millisecond.
     */
    public static long MILLI_TO_NANO = 1000000L;

    private MultiImageFrame[] frames;
    private long[] showTimes;
    private int initFrame = 0;
    private Dimension d;
    private Thread playthread;
    private long duration = 0;
    private long nanoseconds;
    private float rate;

    /**
     * Constructs a MultiImageRenderer.  setFrames() must be
     * called before this MultiImageRenderer will display any
     * images.
     */
    public MultiImageRenderer() {
        super();
        reset();
    }

    /**
     * Constructs a MultiImageRenderer for the given
     * MultiImageFrames.
     *
     * @param      frames
     *             The images to display
     */
    public MultiImageRenderer(MultiImageFrame[] frames) {
        this();
        setFrames(frames);
    }

    /**
     * Sets the images to display in this MultiImageRenderer.
     */
    public void setFrames(MultiImageFrame[] frames) {
        this.frames = frames;
        calculatePreferredSize();

        showTimes = new long[frames.length + 1];

        //  Calculate duration and the exact times each frame
        //  should be displayed
        int i;
        duration = 0;
        for(i = 0; i < frames.length; i++) {
            showTimes[i] = duration;
            duration += frames[i].delay;
        }
        showTimes[i] = duration;

        calcInitFrame();
    }

    /**
     * Sets initial values for time and rate
     */
    public void reset() {
        setMediaTime(0);
        setRate(1);
    }
    
    /**
     * Set the preferred size to be the max
     * width x max height of all of the ImageIcons
     */
    public void calculatePreferredSize() {
        int width = 0;
        int height = 0;
        for(int i = 0; i < frames.length; i++ ) {
            int w = frames[i].icon.getIconWidth();
            int h = frames[i].icon.getIconHeight();
            if( w > width ) width = w;
            if( h > height ) height = h;
        } 
        d = new Dimension(width,height);
    }

    /**
     * Sets the offset in nanoseconds from the beginning of the
     * video.  If the video is started, the player is stopped and
     * restarted.
     */
    public void setMediaTime(long nanoseconds) {
        //  Enforce bounds
        if( nanoseconds > duration ) {
            nanoseconds = duration;
        } else

        if( nanoseconds < 0 ) {
            nanoseconds = 0;
        }

        //  If the video is currently playing and hasn't reached
        //  the end, then stop it, reset the nanosecond offset,
        //  and restart it.  Otherwise, just set the nanosecond
        //  offset.

        if( playthread != null &&
            playthread.isAlive() &&
            ! endOfMedia() )
        {
            stop();
            this.nanoseconds = nanoseconds;
            start();
        } else {
            this.nanoseconds = nanoseconds;
        }
        calcInitFrame();
    }

    /**
     * Gets the current video time in nanoseconds.
     */
    public long getNanoseconds() {
        return nanoseconds;
    }

    /**
     * Sets the offset in seconds from the beginning of the video.
     * If the video is started, the player is stopped and
     * restarted.
     */
    public void setSeconds(double seconds) {
        setMediaTime((long)(seconds * SEC_TO_NANO));
    }

    /**
     * Gets the current video time in seconds.
     */
    public double getSeconds() {
        return (double)getNanoseconds() / (double)SEC_TO_NANO;
    }

    /**
     * Gets the duration of the video in nanoseconds.
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Starts the display of the video.
     */
    public synchronized void start() {
        if( frames == null ) {
            return;
        }

        if( playthread == null || ! playthread.isAlive() ) {
            //  Create playthread
            playthread = new Thread(this);
            playthread.setDaemon(true);
            playthread.start();
        }
    }

    /**
     * Sets the rate of the video.
     *
     * @param      rate
     *             The desired rate
     *
     * @return     The actual rate set.  This will not be
     *             different from the desired rate unless the
     *             desired rate is zero.  In that case no new rate
     *             is set and setRate() returns the current rate.
     */
    public float setRate(float rate) {
        //  Zero is the only invalid rate
        if( rate == 0 ) return this.rate;

        //  If the video is currently playing and hasn't reached
        //  the end, then stop it, reset the rate, and restart it.
        //  Otherwise, just set the nanosecond offset.

        if( playthread != null &&
            playthread.isAlive() &&
            ! endOfMedia() )
        {
            stop();
            this.rate = rate;
            start();
        } else {
            this.rate = rate;
        }
        calcInitFrame();
        return rate;
    }

    /**
     * Gets the current rate.
     */
    public float getRate() {
        return rate;
    }

    /**
     * Stops the display of the video.
     */
    public synchronized void stop() {
        if( playthread != null && playthread.isAlive() ) {
            playthread.stop();
        }
    }

    /**
     * Calculate which frame to begin on and how long to sleep
     * initially.  This will depend on the current video time and
     * rate.
     */
    private void calcInitFrame() {
        if( frames == null ) return;

        //  Find the initial frame
        for(initFrame = 0; initFrame < frames.length - 1; initFrame++) {
            if( showTimes[initFrame+1] > nanoseconds ) break;
        }
    }

    /**
     * Run by the thread that displays the video.
     */
    public void run() {

        //  Store the start times
        long mStart = nanoseconds;
        long tbStart = System.currentTimeMillis();

        for(int i = initFrame; i >= 0 && i < frames.length;) {

            //  Set the icon
            setIcon(frames[i].icon);

            //  How much time should we sleep?
            long mTarget = (rate < 0 ? showTimes[i] : showTimes[i+1]);
            long tbTarget = (long)((mTarget - mStart)/(rate * MILLI_TO_NANO)) + tbStart;
            long tbNow = System.currentTimeMillis();
            long sleep = tbTarget - tbNow;

            if( sleep > 0 ) {
                //  Sleep until the next frame
                try { Thread.sleep(sleep); }
                catch( InterruptedException e) {}
            }

            //  Update the video time
            nanoseconds = mTarget;

            if( rate > 0 ) {
                i++;
            } else {
                i--;
            }
        } 
    }

    /**
     * Checks to see if the end of the video has been reached.  If
     * the rate is negative, the end of the video is at the
     * beginning (0).
     */
    public boolean endOfMedia() {
        boolean negative = (getRate() < 0);
        long duration = getDuration();
        long nanos = getNanoseconds();

        return ( (negative && nanos == 0) ||
                 (!negative && nanos == duration) );
    }
    
    /**
     * Return the dimension of the largest image.
     */
    public Dimension getPreferredSize() {
        if( d == null ) {
            return super.getPreferredSize();
        }
        return d;
    }

    /**
     * Sets the preferred size of the video component.  Call
     * calculatePreferredSize() to reset the preferred size based
     * on the size of the images displayed.
     */
    public void setImageSize(Dimension d) {
        this.d = d;
    }
}
