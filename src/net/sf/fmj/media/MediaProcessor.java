package net.sf.fmj.media;

import java.awt.*;
import java.io.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;

/**
 * MediaProcessor extends BasicProcessor and uses ProcessEngine to process
 * media.
 */

public class MediaProcessor extends BasicProcessor
{
    protected ProcessEngine engine;

    public MediaProcessor()
    {
        engine = new ProcessEngine(this);
    }

    @Override
    protected boolean audioEnabled()
    {
        return engine.audioEnabled();
    }

    /**
     * Return the output content-type.
     */
    @Override
    public ContentDescriptor getContentDescriptor() throws NotConfiguredError
    {
        return engine.getContentDescriptor();
    }

    /**
     * Return the output DataSource of the Processor.
     */
    @Override
    public DataSource getDataOutput() throws NotRealizedError
    {
        return engine.getDataOutput();
    }

    /**
     * Obtain the gain control from the media engine.
     */
    @Override
    public GainControl getGainControl()
    {
        super.getGainControl(); // check for valid states.
        return engine.getGainControl();
    }

    /**
     * Obtain the time base from the media engine.
     */
    @Override
    protected TimeBase getMasterTimeBase()
    {
        return engine.getTimeBase();
    }

    @Override
    public long getMediaNanoseconds()
    {
        // When add controller is used, we want to use the
        // less accurate clock but still allows the time base
        // to take consideration of the slave controllers.
        // Otherwise, we'll use the more accurate engine time.
        if (controllerList.size() > 1)
            return super.getMediaNanoseconds();
        else
            return engine.getMediaNanoseconds();
    }

    /**
     * Obtain media time directly from the engine.
     */
    @Override
    public Time getMediaTime()
    {
        // When add controller is used, we want to use the
        // less accurate clock but still allows the time base
        // to take consideration of the slave controllers.
        // Otherwise, we'll use the more accurate engine time.
        if (controllerList.size() > 1)
            return super.getMediaTime();
        else
            return engine.getMediaTime();
    }

    /**
     * Return all the content-types which this Processor's output supports.
     */
    @Override
    public ContentDescriptor[] getSupportedContentDescriptors()
            throws NotConfiguredError
    {
        return engine.getSupportedContentDescriptors();
    }

    /**
     * Return the tracks in the media. This method can only be called after the
     * Processor has been configured.
     */
    @Override
    public TrackControl[] getTrackControls() throws NotConfiguredError
    {
        return engine.getTrackControls();
    }

    /**
     * Obtain the visual component from the media engine.
     */
    @Override
    public Component getVisualComponent()
    {
        /**
         * Call the superclass method to ensure that restrictions on player
         * methods are enforced
         */
        super.getVisualComponent();
        return engine.getVisualComponent();
    }

    /**
     * Set the output content-type.
     */
    @Override
    public ContentDescriptor setContentDescriptor(ContentDescriptor ocd)
            throws NotConfiguredError
    {
        return engine.setContentDescriptor(ocd);
    }

    @Override
    public void setSource(javax.media.protocol.DataSource source)
            throws IOException, IncompatibleSourceException
    {
        // Ask the engine to verify the source.
        engine.setSource(source);

        // Put the media engine under the management of this player.
        // BasicPlayer will be responsible to transition the engine
        // to the realized state.
        manageController(engine);

        super.setSource(source);
    }

    @Override
    public void updateStats()
    {
        engine.updateRates();
    }

    @Override
    protected boolean videoEnabled()
    {
        return engine.videoEnabled();
    }
}
