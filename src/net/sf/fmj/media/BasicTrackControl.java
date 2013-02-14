package net.sf.fmj.media;

import java.util.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;

import net.sf.fmj.media.control.*;

/**
 * Basic track control for use with a Processor or Player.
 */
public class BasicTrackControl implements javax.media.control.TrackControl
{
    final static String realizeErr = "Cannot get CodecControl before reaching the realized state.";
    final static String connectErr = "Cannot set a PlugIn before reaching the configured state.";

    PlaybackEngine engine;
    Track track;
    OutputConnector firstOC, lastOC;

    // The modules used for this track.
    protected Vector modules = new Vector(7);

    // The selected renderer for this track.
    protected BasicRendererModule rendererModule;

    protected BasicMuxModule muxModule = null;

    protected boolean prefetchFailed = false;
    protected boolean rendererFailed = false;

    float lastFrameRate = 0.0f;

    long lastStatsTime = 0; // so now - lastStatsTime will not be 0.

    public BasicTrackControl(PlaybackEngine engine, Track track,
            OutputConnector oc)
    {
        this.engine = engine;
        this.track = track;
        this.firstOC = oc;
        this.lastOC = oc;
        setEnabled(track.isEnabled());
    }

    /**
     * Top level routine to build a single track.
     */
    public boolean buildTrack(int trackID, int numTracks)
    {
        return false;
    }

    protected FrameRateControl frameRateControl()
    {
        return null;
    }

    public Object getControl(String type)
    {
        Class<?> cls;
        try
        {
            // cls = Class.forName(type);
            cls = BasicPlugIn.getClassForName(type);
        } catch (ClassNotFoundException e)
        {
            return null;
        }
        Object cs[] = getControls();
        for (int i = 0; i < cs.length; i++)
        {
            if (cls.isInstance(cs[i]))
                return cs[i];
        }
        return null;
    }

    public java.awt.Component getControlComponent()
    {
        return null;
    }

    public Object[] getControls() throws NotRealizedError
    {
        if (engine.getState() < Controller.Realized)
            throw (new NotRealizedError(realizeErr));

        InputConnector ic;
        OutputConnector oc = firstOC;
        Module m;
        PlugIn p = null;

        Control controls[];
        Vector cv = new Vector();
        Control c;
        Object cs[];
        int i, size;

        while (oc != null && (ic = oc.getInputConnector()) != null)
        {
            m = ic.getModule();
            cs = m.getControls();
            if (cs != null)
            {
                for (i = 0; i < cs.length; i++)
                {
                    cv.addElement(cs[i]);
                }
            }
            oc = m.getOutputConnector(null);
        }

        size = cv.size();
        controls = new Control[size];
        for (i = 0; i < size; i++)
            controls[i] = (Control) cv.elementAt(i);

        return controls;
    }

    public Format getFormat()
    {
        return track.getFormat();
    }

    public Format getOriginalFormat()
    {
        return track.getFormat();
    }

    public Format[] getSupportedFormats()
    {
        return new Format[] { track.getFormat() };
    }

    public boolean isCustomized()
    {
        return false;
    }

    public boolean isEnabled()
    {
        return track.isEnabled();
    }

    /**
     * Returns true if this track holds the master time base.
     */
    public boolean isTimeBase()
    {
        return false;
    }

    /**
     * Prefetch the modules for this track.
     */
    public boolean prefetchTrack()
    {
        BasicModule bm;
        for (int j = 0; j < modules.size(); j++)
        {
            bm = (BasicModule) modules.elementAt(j);
            if (!bm.doPrefetch())
            {
                setEnabled(false);
                prefetchFailed = true;
                if (bm instanceof BasicRendererModule)
                    rendererFailed = true;
                return false;
            }
        }
        // If it failed to prefetch before but now it's alright again,
        // we'll re-enable the track.
        if (prefetchFailed)
        {
            setEnabled(true);
            prefetchFailed = false;
            rendererFailed = false;
        }
        return true;
    }

    public void prError()
    {
        Log.error("  Unable to handle format: " + getOriginalFormat());
        Log.write("\n");
    };

    protected ProgressControl progressControl()
    {
        return null;
    };

    public void setCodecChain(Codec codec[]) throws NotConfiguredError,
            UnsupportedPlugInException
    {
        if (engine.getState() > BasicController.Configured)
            throw new NotConfiguredError(connectErr);
        if (codec.length < 1)
            throw new UnsupportedPlugInException(
                    "No codec specified in the array.");
    }

    public void setEnabled(boolean enabled)
    {
        track.setEnabled(enabled);
    }

    public Format setFormat(Format format)
    {
        if (format != null && format.matches(getFormat()))
            return getFormat();
        return null;
    }

    public void setRenderer(Renderer renderer) throws NotConfiguredError
    {
        if (engine.getState() > BasicController.Configured)
            throw new NotConfiguredError(connectErr);
    }

    /**
     * Start the modules for this track.
     */
    public void startTrack()
    {
        for (int j = 0; j < modules.size(); j++)
        {
            ((BasicModule) modules.elementAt(j)).doStart();
        }
    }

    /**
     * Start the modules for this track.
     */
    public void stopTrack()
    {
        for (int j = 0; j < modules.size(); j++)
        {
            ((BasicModule) modules.elementAt(j)).doStop();
        }
    }

    /**
     * Update the format per track on the progress control.
     */
    public void updateFormat()
    {
        if (!track.isEnabled())
            return;

        ProgressControl pc;

        if ((pc = progressControl()) == null)
            return;

        StringControl sc;
        if (track.getFormat() instanceof AudioFormat)
        {
            String channel = "";
            AudioFormat afmt = (AudioFormat) track.getFormat();
            sc = pc.getAudioCodec();
            sc.setValue(afmt.getEncoding());
            sc = pc.getAudioProperties();
            if (afmt.getChannels() == 1)
                channel = "mono";
            else
                channel = "stereo";
            sc.setValue(afmt.getSampleRate() / 1000.0 + " KHz, "
                    + afmt.getSampleSizeInBits() + "-bit, " + channel);
        }
        if (track.getFormat() instanceof VideoFormat)
        {
            VideoFormat vfmt = (VideoFormat) track.getFormat();
            sc = pc.getVideoCodec();
            sc.setValue(vfmt.getEncoding());
            sc = pc.getVideoProperties();
            if (vfmt.getSize() != null)
                sc.setValue(vfmt.getSize().width + " X "
                        + vfmt.getSize().height);
        }
    }

    /**
     * Update the frame rate per track on the progress control.
     */
    public void updateRates(long now)
    {
        FrameRateControl prc;

        if ((prc = frameRateControl()) == null)
            return;

        if (!track.isEnabled() || !(track.getFormat() instanceof VideoFormat)
                || (rendererModule == null && muxModule == null))
            return;

        float rate, avg;

        if (now == lastStatsTime)
            rate = lastFrameRate;
        else
        {
            int framesPlayed;
            if (rendererModule != null)
                framesPlayed = rendererModule.getFramesPlayed();
            else
                framesPlayed = muxModule.getFramesPlayed();
            rate = ((float) framesPlayed / (now - lastStatsTime) * 1000.0f);
        }
        avg = ((int) (((lastFrameRate + rate) / 2) * 10)) / 10.f;
        prc.setFrameRate(avg);
        lastFrameRate = rate;
        lastStatsTime = now;
        if (rendererModule != null)
            rendererModule.resetFramesPlayed();
        else
            muxModule.resetFramesPlayed();
    }
}
