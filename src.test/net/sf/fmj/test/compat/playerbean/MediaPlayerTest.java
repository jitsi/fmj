package net.sf.fmj.test.compat.playerbean;

import javax.media.*;
import javax.media.bean.playerbean.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class MediaPlayerTest extends TestCase
{
    private void assertEquals(float f1, float f2)
    {
        assertTrue(f1 == f2);
    }

    public void testMediaPlayer() throws Exception
    {
        synchronized (PackageManager.class)
        {
            {
                MediaPlayer p = new MediaPlayer();

                assertEquals(p.getState(), Controller.Unrealized);
                assertEquals(p.getMediaTime().getNanoseconds(), Long.MAX_VALUE);
                assertEquals(p.getSyncTime().getNanoseconds(), Long.MAX_VALUE);
                assertEquals(p.getStartLatency().getNanoseconds(),
                        Long.MAX_VALUE);
                assertEquals(p.getDuration().getNanoseconds(),
                        Long.MAX_VALUE - 1);
                assertEquals(p.getStopTime(), null);
                /* ! */assertEquals(p.getMediaLocation(), "");
                assertEquals(p.getTargetState(), Controller.Unrealized);
                assertEquals(p.getRate(), 0.f);
                assertEquals(p.getMediaNanoseconds(), Long.MAX_VALUE);
                assertEquals(p.getTimeBase(), null);
                assertEquals(p.getVisualComponent(), null);
                assertEquals(p.mapToTimeBase(new Time(5L)).getNanoseconds(),
                        Long.MAX_VALUE);
                p.removeController(null);
                p.removeControllerListener(null);
                p.addController(null);
                p.addControllerListener(null);
                assertEquals(p.getControl("abc"), null);
                assertEquals(p.getControlPanelComponent(), null);
                assertEquals(p.getControls().length, 0);
                assertEquals(p.getGainControl(), null);
                p.setMediaTime(new Time(0));
                assertEquals(p.getMediaTime().getNanoseconds(), Long.MAX_VALUE);
                assertEquals(p.setRate(2.f), 0.f);
                assertEquals(p.getRate(), 0.f);
                p.setSource(null);
                p.setStopTime(new Time(0));
                p.setTimeBase(null);

                p.setMediaLocator(new MediaLocator(
                        "file:samplemedia/betterway.wav2"));

                assertTrue(p.getPlayer() == null);
                assertEquals(p.getState(), Controller.Unrealized);
                assertEquals(p.getMediaTime().getNanoseconds(), Long.MAX_VALUE);
                assertEquals(p.getSyncTime().getNanoseconds(), Long.MAX_VALUE);
                assertEquals(p.getStartLatency().getNanoseconds(),
                        Long.MAX_VALUE);
                assertEquals(p.getDuration().getNanoseconds(),
                        Long.MAX_VALUE - 1);
                assertEquals(p.getStopTime(), null);
                assertEquals(p.getMediaLocation(), " ");
                assertEquals(p.getTargetState(), Controller.Unrealized);
                assertEquals(p.getRate(), 0.f);
                assertEquals(p.getMediaNanoseconds(), Long.MAX_VALUE);
                assertEquals(p.getTimeBase(), null);
                assertEquals(p.getVisualComponent(), null);
                assertEquals(p.mapToTimeBase(new Time(5L)).getNanoseconds(),
                        Long.MAX_VALUE);
                p.removeController(null);
                p.removeControllerListener(null);
                p.addController(null);
                p.addControllerListener(null);
                assertEquals(p.getControl("abc"), null);
                assertEquals(p.getControlPanelComponent(), null);
                assertEquals(p.getControls().length, 0);
                assertEquals(p.getGainControl(), null);
                p.setMediaTime(new Time(0));
                assertEquals(p.getMediaTime().getNanoseconds(), Long.MAX_VALUE);
                assertEquals(p.setRate(2.f), 0.f);
                assertEquals(p.getRate(), 0.f);
                p.setSource(null);
                p.setStopTime(new Time(0));
                p.setTimeBase(null);

                p.syncStart(new Time(0));
                p.start();
                p.stop();
                p.prefetch();
                p.realize();
                p.close();
                p.deallocate();

            }

            // assertEquals(p.getState(), MediaPlayer.Unrealized);
            // p.start();

        }
    }
}
