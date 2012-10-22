package ejmf.toolkit.media.content.video.multi_image;

import java.awt.Component;
import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;

import javax.media.ClockStoppedException;
import javax.media.ControllerEvent;
import javax.media.IncompatibleSourceException;
import javax.media.ResourceUnavailableEvent;
import javax.media.Time;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;
import javax.swing.ImageIcon;

import ejmf.toolkit.gui.controlpanel.EjmfControlPanel;
import ejmf.toolkit.gui.multiimage.MultiImageFrame;
import ejmf.toolkit.gui.multiimage.MultiImageRenderer;
import ejmf.toolkit.io.PullSourceInputStream;
import ejmf.toolkit.media.AbstractPlayer;
import ejmf.toolkit.media.BasicCachingControl;

/**
 * This class provides a Player for the .mti (multi-image) format.
 * It uses a MultiImageRenderer to display the media.
 * <p>
 * The format specification for .mti is as follows:
 * <p>
 * <OL>
 * <LI>A Java int (32 bits) describing the width of the largest
 * image</LI>
 * <LI>A Java int (32 bits) describing the height of the largest
 * image</LI>
 * <LI>A Java long (64 bits) describing the total duration of the
 * video</LI>
 * <LI>One or more "image frames", defined as</LI>
 * <OL>
 * <LI>A Java long (64 bits) describing the length (in bytes) of
 * the image</LI>
 * <LI>A Java long (64 bits) describing the number of nanoseconds
 * to wait before displaying the next image frame</LI>
 * <LI>The media data<\LI>
 * </OL>
 * </OL>
 *
 * @see        ejmf.toolkit.gui.multiimage.MultiImageRenderer
 * @see        JMFMultiImageRenderer
 *
 * @author     Steve Talley
 */
public class Handler extends AbstractPlayer {
    private PullSourceStream stream;
    private MultiImageRenderer mic;
    private BasicCachingControl cache;
    private boolean prefetchNeeded = true;
    private long duration = 0L;

    /**
     * Construct a AbstractPlayer
     */
    public Handler() {
        super();
        cache = new BasicCachingControl(this, 0);
    }

    /**
     * Set the DataSource for this Player.
     *
     * @param      source
     *             The DataSource to try
     *
     * @exception  IncompatibleSourceException
     *             If the DataSource is not a PullDataSource or
     *             does not contain valid streams.
     */
    public void setSource(DataSource source)
        throws IncompatibleSourceException
    {
        //  Accept only PullDataSources
        if(! (source instanceof PullDataSource) ) {
            throw new IncompatibleSourceException(
                "MediaHandler " + getClass().getName() +
                " does not support " +
                "DataSource " + source.getClass().getName() );
        }

        PullSourceStream[] streams =
            ((PullDataSource)source).getStreams();

        if( streams == null || streams.length == 0 ) {
            throw new IncompatibleSourceException(
                "DataSource " + source.getClass().getName() +
                " does not contain valid streams." );
        }

        super.setSource(source);
        stream = streams[0];
    }
    
    /**
     * Get the control panel Component for this Player.
     *
     * @return     The control panel Component.
     */
    public Component getControlPanelComponent() {
        Component c = super.getControlPanelComponent();

        if( c == null ) {
            c = new EjmfControlPanel(this);
            setControlPanelComponent(c);
        }

        return c;
    }

    /**
     * Read in the video header from the stream.  This will tell
     * us how big to make the video component.
     */
    public boolean doPlayerRealize() {
        mic = new JMFMultiImageRenderer(this);
        DataInputStream in = null;

        try {
            //  Create an input stream
            in = new DataInputStream(
                new PullSourceInputStream(stream) );

            //  Read in the size of the video
            int w = in.readInt();
            int h = in.readInt();

            //  Read in the duration of the video (in nanos)
            duration = in.readLong();

            //  Set the preferred size
            mic.setImageSize( new Dimension(w,h) );
        }
        
        catch(EOFException e) {
            System.err.println("Unexpected EOF encountered in stream");

            postEvent(
                new ResourceUnavailableEvent(this,
                    "Unexpected EOF encountered while reading data stream") );

            return false;
        }

        catch(IOException e) {
            System.err.println("I/O Error reading data");

            postEvent(
                new ResourceUnavailableEvent(this,
                    "I/O error occurred while reading data stream") );

            return false;
        }
        
        finally {
            try { in.close(); } catch(Exception e) {}
        }

        //  Set the visualComponent
        setVisualComponent(mic);

        //  Reset the CachingControl
        cache.reset( stream.getContentLength() );

        return true;
    }

    /**
     * Read in each video frame from the stream and call
     * mic.setFrames()
     */
    public boolean doPlayerPrefetch() {
        //  Has the data already been prefetched?
        if( ! prefetchNeeded ) return true;

        Vector frameVector = new Vector();
        DataInputStream in = null;

        try {
            //  Create an input stream
            in = new DataInputStream(
                new PullSourceInputStream(stream) );

            //  Load every image, first processing the image
            //  length and frame delay in the header
            try {
                while(true) {
                    //  Get frame length
                    long length = in.readLong();

                    //  Get frame delay
                    long nanos = in.readLong();

                    //  Get frame image
                    byte[] b = new byte[(int)length];
                    in.readFully(b,0,(int)length);
                    ImageIcon icon = new ImageIcon(b);

                    //  Update CachingControl
                    cache.addToProgress(16 + length);

                    //  Create MultiImageFrame object
                    MultiImageFrame m = new MultiImageFrame(icon, nanos);
                    frameVector.addElement(m);
                }
            }
            
            //  Read until EOF
            catch(EOFException e) {
                cache.setDone();
            }
        }
        
        catch(IOException e) {
            System.err.println("I/O Error reading data");

            postEvent(
                new ResourceUnavailableEvent(this,
                    "I/O error occurred while reading data stream") );

            return false;
        }
        
        finally {
            try { in.close(); } catch(Exception e) {}
        }

        //  Convert the vector to an array and set the frames
        MultiImageFrame[] frames = new MultiImageFrame[frameVector.size()];
        frameVector.copyInto(frames);
        mic.setFrames(frames);

        //  We don't need to do this more than once
        prefetchNeeded = false;

        return true;
    }

    /**
     * Starts the MultiImageRenderer
     */
    public boolean doPlayerSyncStart(Time t) {
        blockUntilStart(t);
        mic.start();
        return true;
    }

    /**
     * Since the media is buffered in the MultiImageRenderer, we
     * don't need to free any resources.  Set the media time to 0
     * and return.
     */
    public boolean doPlayerDeallocate() {
        return true;
    }

    /**
     * Stop the MultiImageRenderer
     */
    public boolean doPlayerStop() {
        mic.stop();
        return true;
    }

    /**
     * Release as many resources as possible
     */
    public void doPlayerClose() {
        mic = null;
        stream = null;
        cache = null;
    }

    /**
     * Set the rate of the media
     *
     * @param      rate
     *             The desired rate
     *
     * @return     The actual rate set.  For a MultiImageRenderer,
     *             this will be equal to the desired rate.
     */
    public float doPlayerSetRate(float rate) {
        return mic.setRate(rate);
    }

    /**
     * Set the media time.  The time is first converted to
     * nanoseconds and then set in the MultiImageRenderer.
     *
     * @param      t
     *             The time to set
     */
    public void doPlayerSetMediaTime(Time t) {
        mic.setMediaTime( t.getNanoseconds() );
    }

    /**
     * Assume that there is no significant latency involved in
     * realizing or prefetching the media.
     *
     * @return     A Time of zero nanoseconds.
     */
    public Time getPlayerStartLatency() {
        return new Time(0);
    }

    /**
     * Get the length of the media from the MultiImageRenderer.
     * The duration is taken from the MultiImageRenderer in
     * nanoseconds and converted into a Time object.
     *
     * @return     A Time object representing the duration of
     *             the media.
     */
    public Time getPlayerDuration() {
        //  If the video has not yet been prefetched, then the
        //  MultiImageRenderer cannot calculate its duration.
        //  Instead, return the duration read from the header.
        //  (These should be the same, but mic.getDuration() will
        //  _always_ be correct if the frames have been set.)

        if( getState() < Realized ) {
            return DURATION_UNKNOWN;
        } else

        if( getState() < Prefetched ) {
            return new Time(duration);
        } else
        
        return new Time( mic.getDuration() );
    }

    /**
     * Post a ControllerEvent to the ControllerEventQueue.  This
     * method is re-implemented here to allow all classes within
     * this package to post ControllerEvents.
     *
     * @param      e
     *             <addtext>
     */
    protected void postEvent(ControllerEvent e) {
        super.postEvent(e);
    }

    /**
     * To be called when the end of media has been reached.
     *
     * @exception  ClockStoppedException
     *             If the Clock is stopped when this method is
     *             called.
     */
    protected void endOfMedia()
        throws ClockStoppedException
    {
        super.endOfMedia();
    }
}
