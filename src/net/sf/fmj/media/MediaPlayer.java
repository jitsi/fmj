package net.sf.fmj.media;

import java.awt.*;
import java.io.*;

import javax.media.*;

import net.sf.fmj.media.control.*;

/**
 * MediaPlayer extends BasicPlayer and uses PlaybackEngine to play media.
 */

public class MediaPlayer extends BasicPlayer
{
    protected PlaybackEngine engine;

    public MediaPlayer()
    {
        engine = new PlaybackEngine(this);
    }

    @Override
    protected boolean audioEnabled()
    {
        return engine.audioEnabled();
    }

    /**
     * Obtain the gain control from the media engine.
     */
    @Override
    public GainControl getGainControl()
    {
        int state = getState();
        if (state < Realized)
        {
            throwError(new NotRealizedError(
                    "Cannot get gain control on an unrealized player"));
        }
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
     * Obtain the visiual component from the media engine.
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

    public void setProgressControl(ProgressControl p)
    {
        engine.setProgressControl(p);
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
