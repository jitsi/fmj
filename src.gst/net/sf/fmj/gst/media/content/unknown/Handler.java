package net.sf.fmj.gst.media.content.unknown;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.Time;
import javax.media.protocol.*;

import net.sf.fmj.ejmf.toolkit.media.*;
import net.sf.fmj.gui.controlpanelfactory.*;
import net.sf.fmj.utility.*;

import org.gstreamer.*;
import org.gstreamer.elements.*;
import org.gstreamer.swing.*;

/**
 *
 * Handler for GStreamer, which bypasses most of JMF (parsers, codecs). TODO:
 * properly indicate EOM.
 *
 * @author Ken Larson
 *
 */
public class Handler extends AbstractPlayer
{
    private static final Logger logger = LoggerSingleton.logger;

    /** Copied from GstVideoPlayer */
    private static URI parseURI(String uri)
    {
        try
        {
            URI u = new URI(uri);
            if (u.getScheme() == null)
            {
                throw new URISyntaxException(uri, "Invalid URI scheme");
            }
            return u;
        } catch (URISyntaxException e)
        {
            File f = new File(uri);
            if (!f.exists())
            {
                throw new IllegalArgumentException("Invalid URI/file " + uri, e);
            }
            return f.toURI();
        }
    }

    private boolean prefetchNeeded = true;

    private static final boolean TRACE = true;
    private PlayBin playbin;

    private GstVideoComponent videoComponent;
    // TODO: audio component?
    private static boolean gstInitialized;

    private static void initGstreamer()
    {
        if (gstInitialized)
            return;

        logger.info("Initializing gstreamer");

        try
        {
            Gst.init("FMJ GStreamer Handler", new String[] {});

            Thread t = new Thread("GST MainLoop Thread")
            {
                @Override
                public void run()
                {
                    new MainLoop().run(); // TODO: what do we do with this?
                }
            };
            t.setDaemon(true);
            t.start();

            gstInitialized = true;
        } catch (Throwable t)
        {
            logger.log(Level.WARNING, "Unable to initialize gstreamer: " + t, t);
        }
    }

    @Override
    public void doPlayerClose()
    {
        // TODO
        logger.info("Handler.doPlayerClose");
    }

    @Override
    public boolean doPlayerDeallocate()
    {
        logger.info("Handler.doPlayerDeallocate");
        // if (playbin != null)
        // playbin.dispose(); // causes JVM crash
        return true;
    }

    @Override
    public boolean doPlayerPrefetch()
    {
        if (!prefetchNeeded)
            return true;

        prefetchNeeded = false;

        return true;
    }

    @Override
    public boolean doPlayerRealize()
    {
        MediaLocator locator = getSource().getLocator();

        // workaround because JMF does not like relative file URLs.
        if (locator.getProtocol().equals("file"))
        {
            final String newUrl = URLUtils.createAbsoluteFileUrl(locator
                    .toExternalForm());
            if (newUrl != null)
            {
                final MediaLocator newSource = new MediaLocator(newUrl);
                if (!locator.toExternalForm()
                        .equals(newSource.toExternalForm()))
                {
                    logger.warning("Changing file URL to absolute, from "
                            + locator.toExternalForm() + " to " + newSource);
                    locator = newSource;
                }
            }
        }

        final String path = locator.toExternalForm();

        // logic copied from GstVideoPlayer
        URI uri = parseURI(path);
        playbin = new PlayBin(uri.toString());
        playbin.setURI(uri);
        videoComponent = new GstVideoComponent();

        playbin.getBus().connect(new Bus.EOS()
        {
            public void eosMessage(GstObject source)
            {
                logger.info("GST eosMessage");

                final Thread thread = new Thread("GST EOS Thread")
                {
                    @Override
                    public void run()
                    {
                        playbin.stop(); // we have to do this in a thread, or it
                                        // will hang
                        // if we don't do it at all, and the GUI calls
                        // player.setMediaTime(new Time(0));
                        // we end up playing it again.

                        logger.info("GST eosMessage, posting EOM");
                        try
                        {
                            endOfMedia();
                        } catch (ClockStoppedException e)
                        {
                            logger.log(Level.WARNING, "" + e, e);
                        }
                        logger.info("GST eosMessage, done");

                    }
                };

                getThreadQueue().addThread(thread);

            }
        });
        playbin.setVideoSink(videoComponent.getElement());

        videoComponent.setPreferredSize(new Dimension(640, 480)); // TODO: get
                                                                  // media size
        return true;
    }

    @Override
    public void doPlayerSetMediaTime(Time t)
    {
        logger.info("Handler.doPlayerSetMediaTime " + t.getNanoseconds());
        // TODO: untested since getPosition is always returning 0, the FMJStudio
        // app does not allow us to seek.
        playbin.setPosition(new org.gstreamer.Time(t.getNanoseconds()));

    }

    @Override
    public float doPlayerSetRate(float rate)
    {
        logger.fine("Handler.doPlayerSetRate " + rate);
        return rate; // TODO
    }

    @Override
    public boolean doPlayerStop()
    {
        logger.fine("Handler.doPlayerStop ");
        playbin.stop();
        return true;
    }

    @Override
    public boolean doPlayerSyncStart(Time t)
    {
        logger.info("Handler.doPlayerSyncStart" + t);

        if (!playbin.isPlaying())
        {
            playbin.play();
        }

        return true;
    }

    // until we figure out how to isolate GST's control panel component (get it
    // without the movie panel),
    // we'll use FMJ's control panel. See GstVideoPlayer
    @Override
    public Component getControlPanelComponent()
    {
        Component c = super.getControlPanelComponent();

        if (c == null)
        {
            c = ControlPanelFactorySingleton.getInstance()
                    .getControlPanelComponent(this);
            setControlPanelComponent(c);
        }

        return c;
    }

    @Override
    public synchronized Time getMediaTime()
    {
        if (getState() < Realized)
        {
            return super.getMediaTime();
        } else
        {
            return new Time(playbin.getPosition().longValue());
        }
    }

    @Override
    public Time getPlayerDuration()
    {
        if (getState() < Realized)
        {
            return DURATION_UNKNOWN;
        } else
        {
            final long durationNanos = playbin.getDuration().longValue();
            if (durationNanos <= 0) // TODO: why are we getting 0?
                return DURATION_UNKNOWN;
            return new Time(durationNanos);
        }
    }

    @Override
    public Time getPlayerStartLatency()
    {
        return new Time(0);
    }

    @Override
    public Component getVisualComponent()
    {
        return videoComponent;
    }

    // @Override
    // public Component getControlPanelComponent()
    // {
    // if (qtcMovieController == null)
    // return null;
    // return qtcMovieController.asComponent();
    // }
    @Override
    public void setSource(DataSource source) throws IncompatibleSourceException
    {
        if (TRACE)
            logger.fine("DataSource: " + source);

        initGstreamer();
        if (!gstInitialized)
            throw new IncompatibleSourceException(
                    "Unable to initialize gstreamer");

        if (source.getLocator() == null)
            throw new IncompatibleSourceException("null source locator");

        super.setSource(source);

    }

}
