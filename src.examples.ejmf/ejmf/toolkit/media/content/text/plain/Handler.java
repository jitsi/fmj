package ejmf.toolkit.media.content.text.plain;

import java.awt.Component;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import javax.media.ClockStoppedException;
import javax.media.ControllerEvent;
import javax.media.IncompatibleSourceException;
import javax.media.ResourceUnavailableEvent;
import javax.media.Time;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;
import javax.media.protocol.SourceStream;

import ejmf.toolkit.gui.controlpanel.EjmfControlPanel;
import ejmf.toolkit.gui.tickertape.TickerTape;
import ejmf.toolkit.io.PullSourceInputStream;
import ejmf.toolkit.media.AbstractPlayer;
import ejmf.toolkit.media.content.text.plain.controls.ColorControl;
import ejmf.toolkit.media.content.text.plain.controls.FontControl;

/**
 * This class provides a Player for .txt (text) files.
 * It uses a TickerTape to display the media.
 *
 * @see        ejmf.toolkit.gui.tickertape.TickerTape
 * @see        JMFTickerTape
 *
 * @author     Steve Talley
 */
public class Handler extends AbstractPlayer {
    private PullSourceStream stream;
    private TickerTape tape;
    private boolean prefetchNeeded = true;

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
     * Create the TickerTape component.
     */
    public boolean doPlayerRealize() {
        tape = new JMFTickerTape(this);
        tape.setLoop(true);

        //  Set the visualComponent
        setVisualComponent(tape);

        //  Add custom Controls
        addControl( new FontControl(tape) );
        addControl( new ColorControl(tape) );

        return true;
    }

    /**
     * Read in each video frame from the stream and call
     * tape.setMessage()
     */
    public boolean doPlayerPrefetch() {
        //  Has the data already been prefetched?
        if( ! prefetchNeeded ) return true;

        DataInputStream in = null;
        byte[] b;

        try {
            //  Create an input stream
            in = new DataInputStream(
                new PullSourceInputStream(stream) );

            //  Get the length
            long length = stream.getContentLength();

            if( length != SourceStream.LENGTH_UNKNOWN ) {
                //  Load the entire text file into a byte array
                b = new byte[(int)length];
                in.readFully(b,0,(int)length);
            } else {
                System.err.println(
                    "Unknown content length while reading data");

                postEvent(
                    new ResourceUnavailableEvent(this,
                        "Could not get content length from data stream") );

                return false;
            }
        }
        
        catch(EOFException e) {
            System.err.println("Unexpected EOF while reading data");

            postEvent(
                new ResourceUnavailableEvent(this,
                    "Unexpected EOF occurred while reading data stream") );

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

        //  Convert the byte array to a String and set the
        //  TickerTape message
        tape.setMessage( new String(b,0,b.length) );

        //  We don't need to do this more than once
        prefetchNeeded = false;

        return true;
    }

    /**
     * Starts the TickerTape
     */
    public boolean doPlayerSyncStart(Time t) {
        blockUntilStart(t);
        tape.start();
        return true;
    }

    /**
     * Since the media is buffered in the TickerTape, we
     * don't need to free any resources.  Set the media time to 0
     * and return.
     */
    public boolean doPlayerDeallocate() {
        return true;
    }

    /**
     * Stop the TickerTape
     */
    public boolean doPlayerStop() {
        tape.stop();
        return true;
    }

    /**
     * Release as many resources as possible
     */
    public void doPlayerClose() {
        tape = null;
        stream = null;
    }

    /**
     * Set the rate of the media
     *
     * @param      rate
     *             The desired rate
     *
     * @return     The actual rate set.  For a TickerTape,
     *             this will be equal to the desired rate.
     */
    public float doPlayerSetRate(float rate) {
        int intRate = Math.round(rate);
        tape.setRate(intRate);
        return intRate;
    }

    /**
     * Not implemented.
     */
    public void doPlayerSetMediaTime(Time t) {}

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
     * Get the length of the media from the TickerTape.
     * The duration is taken from the TickerTape in
     * nanoseconds and converted into a Time object.
     *
     * @return     A Time object representing the duration of
     *             the media.
     */
    public Time getPlayerDuration() {
        return DURATION_UNKNOWN;
    }

    /**
     * Post a ControllerEvent to the ControllerEventQueue.  This
     * method is re-implemented here to allow all classes within
     * this package to post ControllerEvents.
     *
     * @param      e
     *             The ControllerEvent to post to the queue
     */
    protected void postEvent(ControllerEvent e) {
        super.postEvent(e);
    }

    /**
     * To be called when the end of media has been reached.  This
     * method is re-implemented here to allow all classes within
     * this package to post ControllerEvents.
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
