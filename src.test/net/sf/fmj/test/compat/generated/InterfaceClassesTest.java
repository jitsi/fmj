package net.sf.fmj.test.compat.generated;

import java.lang.reflect.*;

import javax.media.*;
import javax.media.protocol.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class InterfaceClassesTest extends TestCase
{
    public void test_javax_media_CachingControl() throws Exception
    {
        assertEquals(javax.media.CachingControl.class.getModifiers(), 1537);
        assertTrue(javax.media.CachingControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.CachingControl.class));
        // Static fields:
        assertTrue(javax.media.CachingControl.LENGTH_UNKNOWN == 9223372036854775807L);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.CachingControl.class.getMethod(
                        "getContentLength", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.CachingControl.class.getMethod(
                        "getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.CachingControl.class.getMethod(
                        "isDownloading", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.CachingControl.class.getMethod(
                        "getContentProgress", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.CachingControl.class.getMethod(
                        "getProgressBarComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.CachingControl.class
                        .getField("LENGTH_UNKNOWN");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.CachingControl o = null;
            o.getContentLength();
            o.getControlComponent();
            o.isDownloading();
            o.getContentProgress();
            o.getProgressBarComponent();
        }
    }

    public void test_javax_media_Clock() throws Exception
    {
        assertEquals(javax.media.Clock.class.getModifiers(), 1537);
        assertTrue(javax.media.Clock.class.isInterface());
        // Static fields:
        // TODO: test RESET of type javax.media.Time

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Clock.class.getMethod("stop",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod(
                        "setTimeBase",
                        new Class[] { javax.media.TimeBase.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod("syncStart",
                        new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod(
                        "setStopTime", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod(
                        "getStopTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod(
                        "setMediaTime", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod(
                        "getMediaTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod(
                        "getMediaNanoseconds", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod(
                        "getSyncTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod(
                        "getTimeBase", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.TimeBase.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class
                        .getMethod("mapToTimeBase",
                                new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod("getRate",
                        new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Clock.class.getMethod("setRate",
                        new Class[] { float.class });
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Clock.class.getField("RESET");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Clock o = null;
            o.stop();
            o.setTimeBase((javax.media.TimeBase) null);
            o.syncStart((javax.media.Time) null);
            o.setStopTime((javax.media.Time) null);
            o.getStopTime();
            o.setMediaTime((javax.media.Time) null);
            o.getMediaTime();
            o.getMediaNanoseconds();
            o.getSyncTime();
            o.getTimeBase();
            o.mapToTimeBase((javax.media.Time) null);
            o.getRate();
            o.setRate(0.f);
        }
    }

    public void test_javax_media_Codec() throws Exception
    {
        assertEquals(javax.media.Codec.class.getModifiers(), 1537);
        assertTrue(javax.media.Codec.class.isInterface());
        assertTrue(javax.media.PlugIn.class
                .isAssignableFrom(javax.media.Codec.class));
        // Static fields:
        assertTrue(PlugIn.BUFFER_PROCESSED_OK == 0);
        assertTrue(PlugIn.BUFFER_PROCESSED_FAILED == 1);
        assertTrue(PlugIn.INPUT_BUFFER_NOT_CONSUMED == 2);
        assertTrue(PlugIn.OUTPUT_BUFFER_NOT_FILLED == 4);
        assertTrue(PlugIn.PLUGIN_TERMINATED == 8);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Codec.class.getMethod(
                        "getSupportedInputFormats", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod(
                        "getSupportedOutputFormats",
                        new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod(
                        "setInputFormat",
                        new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod(
                        "setOutputFormat",
                        new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod("process",
                        new Class[] { javax.media.Buffer.class,
                                javax.media.Buffer.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod("getName",
                        new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod("reset",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod("close",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod("open",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Codec.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Codec.class
                        .getField("BUFFER_PROCESSED_OK");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Codec.class
                        .getField("BUFFER_PROCESSED_FAILED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Codec.class
                        .getField("INPUT_BUFFER_NOT_CONSUMED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Codec.class
                        .getField("OUTPUT_BUFFER_NOT_FILLED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Codec.class
                        .getField("PLUGIN_TERMINATED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Codec o = null;
            o.getSupportedInputFormats();
            o.getSupportedOutputFormats((javax.media.Format) null);
            o.setInputFormat((javax.media.Format) null);
            o.setOutputFormat((javax.media.Format) null);
            o.process((javax.media.Buffer) null, (javax.media.Buffer) null);
            o.getName();
            o.reset();
            o.close();
            o.open();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_Control() throws Exception
    {
        assertEquals(javax.media.Control.class.getModifiers(), 1537);
        assertTrue(javax.media.Control.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Control.class.getMethod(
                        "getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Control o = null;
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_BitRateControl() throws Exception
    {
        assertEquals(javax.media.control.BitRateControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.BitRateControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.BitRateControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.BitRateControl.class
                        .getMethod("getBitRate", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BitRateControl.class
                        .getMethod("setBitRate", new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BitRateControl.class
                        .getMethod("getMinSupportedBitRate", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BitRateControl.class
                        .getMethod("getMaxSupportedBitRate", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BitRateControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.BitRateControl o = null;
            o.getBitRate();
            o.setBitRate(0);
            o.getMinSupportedBitRate();
            o.getMaxSupportedBitRate();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_BufferControl() throws Exception
    {
        assertEquals(javax.media.control.BufferControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.BufferControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.BufferControl.class));
        // Static fields:
        assertTrue(javax.media.control.BufferControl.DEFAULT_VALUE == -1L);
        assertTrue(javax.media.control.BufferControl.MAX_VALUE == -2L);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.BufferControl.class
                        .getMethod("getBufferLength", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BufferControl.class
                        .getMethod("setBufferLength",
                                new Class[] { long.class });
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BufferControl.class
                        .getMethod("getMinimumThreshold", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BufferControl.class
                        .getMethod("setMinimumThreshold",
                                new Class[] { long.class });
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BufferControl.class
                        .getMethod("setEnabledThreshold",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BufferControl.class
                        .getMethod("getEnabledThreshold", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.BufferControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.control.BufferControl.class
                        .getField("DEFAULT_VALUE");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.BufferControl.class
                        .getField("MAX_VALUE");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.BufferControl o = null;
            o.getBufferLength();
            o.setBufferLength(0L);
            o.getMinimumThreshold();
            o.setMinimumThreshold(0L);
            o.setEnabledThreshold(false);
            o.getEnabledThreshold();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_FormatControl() throws Exception
    {
        assertEquals(javax.media.control.FormatControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.FormatControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.FormatControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.FormatControl.class
                        .getMethod("getFormat", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FormatControl.class
                        .getMethod("setFormat",
                                new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FormatControl.class
                        .getMethod("getSupportedFormats", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FormatControl.class
                        .getMethod("isEnabled", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FormatControl.class
                        .getMethod("setEnabled", new Class[] { boolean.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FormatControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.FormatControl o = null;
            o.getFormat();
            o.setFormat((javax.media.Format) null);
            o.getSupportedFormats();
            o.isEnabled();
            o.setEnabled(false);
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_FrameGrabbingControl()
            throws Exception
    {
        assertEquals(
                javax.media.control.FrameGrabbingControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.FrameGrabbingControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.FrameGrabbingControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.FrameGrabbingControl.class
                        .getMethod("grabFrame", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Buffer.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FrameGrabbingControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.FrameGrabbingControl o = null;
            o.grabFrame();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_FramePositioningControl()
            throws Exception
    {
        assertEquals(
                javax.media.control.FramePositioningControl.class
                        .getModifiers(),
                1537);
        assertTrue(javax.media.control.FramePositioningControl.class
                .isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.FramePositioningControl.class));
        // Static fields:
        // TODO: test TIME_UNKNOWN of type javax.media.Time
        assertTrue(javax.media.control.FramePositioningControl.FRAME_UNKNOWN == 2147483647);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.FramePositioningControl.class
                        .getMethod("skip", new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FramePositioningControl.class
                        .getMethod("seek", new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FramePositioningControl.class
                        .getMethod("mapFrameToTime", new Class[] { int.class });
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FramePositioningControl.class
                        .getMethod("mapTimeToFrame",
                                new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FramePositioningControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.control.FramePositioningControl.class
                        .getField("TIME_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.FramePositioningControl.class
                        .getField("FRAME_UNKNOWN");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.FramePositioningControl o = null;
            o.skip(0);
            o.seek(0);
            o.mapFrameToTime(0);
            o.mapTimeToFrame((javax.media.Time) null);
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_FrameProcessingControl()
            throws Exception
    {
        assertEquals(
                javax.media.control.FrameProcessingControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.FrameProcessingControl.class
                .isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.FrameProcessingControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.FrameProcessingControl.class
                        .getMethod("setFramesBehind",
                                new Class[] { float.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FrameProcessingControl.class
                        .getMethod("setMinimalProcessing",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FrameProcessingControl.class
                        .getMethod("getFramesDropped", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FrameProcessingControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.FrameProcessingControl o = null;
            o.setFramesBehind(0.f);
            o.setMinimalProcessing(false);
            o.getFramesDropped();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_FrameRateControl() throws Exception
    {
        assertEquals(javax.media.control.FrameRateControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.FrameRateControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.FrameRateControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.FrameRateControl.class
                        .getMethod("getFrameRate", new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FrameRateControl.class
                        .getMethod("setFrameRate", new Class[] { float.class });
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FrameRateControl.class
                        .getMethod("getMaxSupportedFrameRate", new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FrameRateControl.class
                        .getMethod("getPreferredFrameRate", new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.FrameRateControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.FrameRateControl o = null;
            o.getFrameRate();
            o.setFrameRate(0.f);
            o.getMaxSupportedFrameRate();
            o.getPreferredFrameRate();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_H261Control() throws Exception
    {
        assertEquals(javax.media.control.H261Control.class.getModifiers(), 1537);
        assertTrue(javax.media.control.H261Control.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.H261Control.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.H261Control.class
                        .getMethod("isStillImageTransmissionSupported",
                                new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H261Control.class
                        .getMethod("setStillImageTransmission",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H261Control.class
                        .getMethod("getStillImageTransmission", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H261Control.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.H261Control o = null;
            o.isStillImageTransmissionSupported();
            o.setStillImageTransmission(false);
            o.getStillImageTransmission();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_H263Control() throws Exception
    {
        assertEquals(javax.media.control.H263Control.class.getModifiers(), 1537);
        assertTrue(javax.media.control.H263Control.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.H263Control.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("isUnrestrictedVectorSupported",
                                new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("setUnrestrictedVector",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("getUnrestrictedVector", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("isArithmeticCodingSupported",
                                new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("setArithmeticCoding",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("getArithmeticCoding", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("isAdvancedPredictionSupported",
                                new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("setAdvancedPrediction",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("getAdvancedPrediction", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("isPBFramesSupported", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("setPBFrames", new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("getPBFrames", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("isErrorCompensationSupported",
                                new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("setErrorCompensation",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("getErrorCompensation", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("getHRD_B", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("getBppMaxKb", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.H263Control.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.H263Control o = null;
            o.isUnrestrictedVectorSupported();
            o.setUnrestrictedVector(false);
            o.getUnrestrictedVector();
            o.isArithmeticCodingSupported();
            o.setArithmeticCoding(false);
            o.getArithmeticCoding();
            o.isAdvancedPredictionSupported();
            o.setAdvancedPrediction(false);
            o.getAdvancedPrediction();
            o.isPBFramesSupported();
            o.setPBFrames(false);
            o.getPBFrames();
            o.isErrorCompensationSupported();
            o.setErrorCompensation(false);
            o.getErrorCompensation();
            o.getHRD_B();
            o.getBppMaxKb();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_KeyFrameControl() throws Exception
    {
        assertEquals(javax.media.control.KeyFrameControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.KeyFrameControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.KeyFrameControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.KeyFrameControl.class
                        .getMethod("setKeyFrameInterval",
                                new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.KeyFrameControl.class
                        .getMethod("getKeyFrameInterval", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.KeyFrameControl.class
                        .getMethod("getPreferredKeyFrameInterval",
                                new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.KeyFrameControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.KeyFrameControl o = null;
            o.setKeyFrameInterval(0);
            o.getKeyFrameInterval();
            o.getPreferredKeyFrameInterval();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_MonitorControl() throws Exception
    {
        assertEquals(javax.media.control.MonitorControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.MonitorControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.MonitorControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.MonitorControl.class
                        .getMethod("setEnabled", new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MonitorControl.class
                        .getMethod("setPreviewFrameRate",
                                new Class[] { float.class });
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MonitorControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.MonitorControl o = null;
            o.setEnabled(false);
            o.setPreviewFrameRate(0.f);
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_MpegAudioControl() throws Exception
    {
        assertEquals(javax.media.control.MpegAudioControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.MpegAudioControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.MpegAudioControl.class));
        // Static fields:
        assertTrue(javax.media.control.MpegAudioControl.LAYER_1 == 1);
        assertTrue(javax.media.control.MpegAudioControl.LAYER_2 == 2);
        assertTrue(javax.media.control.MpegAudioControl.LAYER_3 == 4);
        assertTrue(javax.media.control.MpegAudioControl.SAMPLING_RATE_16 == 1);
        assertTrue(javax.media.control.MpegAudioControl.SAMPLING_RATE_22_05 == 2);
        assertTrue(javax.media.control.MpegAudioControl.SAMPLING_RATE_24 == 4);
        assertTrue(javax.media.control.MpegAudioControl.SAMPLING_RATE_32 == 8);
        assertTrue(javax.media.control.MpegAudioControl.SAMPLING_RATE_44_1 == 16);
        assertTrue(javax.media.control.MpegAudioControl.SAMPLING_RATE_48 == 32);
        assertTrue(javax.media.control.MpegAudioControl.SINGLE_CHANNEL == 1);
        assertTrue(javax.media.control.MpegAudioControl.TWO_CHANNELS_STEREO == 2);
        assertTrue(javax.media.control.MpegAudioControl.TWO_CHANNELS_DUAL == 4);
        assertTrue(javax.media.control.MpegAudioControl.THREE_CHANNELS_2_1 == 4);
        assertTrue(javax.media.control.MpegAudioControl.THREE_CHANNELS_3_0 == 8);
        assertTrue(javax.media.control.MpegAudioControl.FOUR_CHANNELS_2_0_2_0 == 16);
        assertTrue(javax.media.control.MpegAudioControl.FOUR_CHANNELS_2_2 == 32);
        assertTrue(javax.media.control.MpegAudioControl.FOUR_CHANNELS_3_1 == 64);
        assertTrue(javax.media.control.MpegAudioControl.FIVE_CHANNELS_3_0_2_0 == 128);
        assertTrue(javax.media.control.MpegAudioControl.FIVE_CHANNELS_3_2 == 256);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("getSupportedAudioLayers", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("getSupportedSamplingRates", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("getSupportedChannelLayouts", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("isLowFrequencyChannelSupported",
                                new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("isMultilingualModeSupported",
                                new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("setAudioLayer", new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("getAudioLayer", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("setChannelLayout",
                                new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("getChannelLayout", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("setLowFrequencyChannel",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("getLowFrequencyChannel", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("setMultilingualMode",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("getMultilingualMode", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.MpegAudioControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("LAYER_1");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("LAYER_2");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("LAYER_3");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("SAMPLING_RATE_16");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("SAMPLING_RATE_22_05");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("SAMPLING_RATE_24");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("SAMPLING_RATE_32");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("SAMPLING_RATE_44_1");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("SAMPLING_RATE_48");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("SINGLE_CHANNEL");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("TWO_CHANNELS_STEREO");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("TWO_CHANNELS_DUAL");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("THREE_CHANNELS_2_1");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("THREE_CHANNELS_3_0");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("FOUR_CHANNELS_2_0_2_0");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("FOUR_CHANNELS_2_2");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("FOUR_CHANNELS_3_1");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("FIVE_CHANNELS_3_0_2_0");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.MpegAudioControl.class
                        .getField("FIVE_CHANNELS_3_2");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.MpegAudioControl o = null;
            o.getSupportedAudioLayers();
            o.getSupportedSamplingRates();
            o.getSupportedChannelLayouts();
            o.isLowFrequencyChannelSupported();
            o.isMultilingualModeSupported();
            o.setAudioLayer(0);
            o.getAudioLayer();
            o.setChannelLayout(0);
            o.getChannelLayout();
            o.setLowFrequencyChannel(false);
            o.getLowFrequencyChannel();
            o.setMultilingualMode(false);
            o.getMultilingualMode();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_PacketSizeControl() throws Exception
    {
        assertEquals(
                javax.media.control.PacketSizeControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.PacketSizeControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.PacketSizeControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.PacketSizeControl.class
                        .getMethod("setPacketSize", new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.PacketSizeControl.class
                        .getMethod("getPacketSize", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.PacketSizeControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.PacketSizeControl o = null;
            o.setPacketSize(0);
            o.getPacketSize();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_PortControl() throws Exception
    {
        assertEquals(javax.media.control.PortControl.class.getModifiers(), 1537);
        assertTrue(javax.media.control.PortControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.PortControl.class));
        // Static fields:
        assertTrue(javax.media.control.PortControl.MICROPHONE == 1);
        assertTrue(javax.media.control.PortControl.LINE_IN == 2);
        assertTrue(javax.media.control.PortControl.SPEAKER == 4);
        assertTrue(javax.media.control.PortControl.HEADPHONE == 8);
        assertTrue(javax.media.control.PortControl.LINE_OUT == 16);
        assertTrue(javax.media.control.PortControl.COMPACT_DISC == 32);
        assertTrue(javax.media.control.PortControl.SVIDEO == 64);
        assertTrue(javax.media.control.PortControl.COMPOSITE_VIDEO == 128);
        assertTrue(javax.media.control.PortControl.TV_TUNER == 256);
        assertTrue(javax.media.control.PortControl.COMPOSITE_VIDEO_2 == 512);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.PortControl.class
                        .getMethod("setPorts", new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.PortControl.class
                        .getMethod("getPorts", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.PortControl.class
                        .getMethod("getSupportedPorts", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.PortControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("MICROPHONE");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("LINE_IN");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("SPEAKER");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("HEADPHONE");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("LINE_OUT");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("COMPACT_DISC");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("SVIDEO");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("COMPOSITE_VIDEO");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("TV_TUNER");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.control.PortControl.class
                        .getField("COMPOSITE_VIDEO_2");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.PortControl o = null;
            o.setPorts(0);
            o.getPorts();
            o.getSupportedPorts();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_QualityControl() throws Exception
    {
        assertEquals(javax.media.control.QualityControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.QualityControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.QualityControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.QualityControl.class
                        .getMethod("getQuality", new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.QualityControl.class
                        .getMethod("setQuality", new Class[] { float.class });
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.QualityControl.class
                        .getMethod("getPreferredQuality", new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.QualityControl.class
                        .getMethod("isTemporalSpatialTradeoffSupported",
                                new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.QualityControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.QualityControl o = null;
            o.getQuality();
            o.setQuality(0.f);
            o.getPreferredQuality();
            o.isTemporalSpatialTradeoffSupported();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_RtspControl() throws Exception
    {
        assertEquals(javax.media.control.RtspControl.class.getModifiers(), 1537);
        assertTrue(javax.media.control.RtspControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.RtspControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.RtspControl.class
                        .getMethod("getRTPManagers", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.RTPManager[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.RtspControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.RtspControl o = null;
            o.getRTPManagers();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_SilenceSuppressionControl()
            throws Exception
    {
        assertEquals(
                javax.media.control.SilenceSuppressionControl.class
                        .getModifiers(),
                1537);
        assertTrue(javax.media.control.SilenceSuppressionControl.class
                .isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.SilenceSuppressionControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.SilenceSuppressionControl.class
                        .getMethod("getSilenceSuppression", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.SilenceSuppressionControl.class
                        .getMethod("setSilenceSuppression",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.SilenceSuppressionControl.class
                        .getMethod("isSilenceSuppressionSupported",
                                new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.SilenceSuppressionControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.SilenceSuppressionControl o = null;
            o.getSilenceSuppression();
            o.setSilenceSuppression(false);
            o.isSilenceSuppressionSupported();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_StreamWriterControl() throws Exception
    {
        assertEquals(
                javax.media.control.StreamWriterControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.StreamWriterControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.control.StreamWriterControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.StreamWriterControl.class
                        .getMethod("setStreamSizeLimit",
                                new Class[] { long.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.StreamWriterControl.class
                        .getMethod("getStreamSize", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.StreamWriterControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.StreamWriterControl o = null;
            o.setStreamSizeLimit(0L);
            o.getStreamSize();
            o.getControlComponent();
        }
    }

    public void test_javax_media_control_TrackControl() throws Exception
    {
        assertEquals(javax.media.control.TrackControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.control.TrackControl.class.isInterface());
        assertTrue(javax.media.control.FormatControl.class
                .isAssignableFrom(javax.media.control.TrackControl.class));
        assertTrue(javax.media.Controls.class
                .isAssignableFrom(javax.media.control.TrackControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("setCodecChain",
                                new Class[] { javax.media.Codec[].class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("setRenderer",
                                new Class[] { javax.media.Renderer.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("getFormat", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("setFormat",
                                new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("getSupportedFormats", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("isEnabled", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("setEnabled", new Class[] { boolean.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.control.TrackControl.class
                        .getMethod("getControl",
                                new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.control.TrackControl o = null;
            o.setCodecChain((javax.media.Codec[]) null);
            o.setRenderer((javax.media.Renderer) null);
            o.getFormat();
            o.setFormat((javax.media.Format) null);
            o.getSupportedFormats();
            o.isEnabled();
            o.setEnabled(false);
            o.getControlComponent();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_Controller() throws Exception
    {
        assertEquals(javax.media.Controller.class.getModifiers(), 1537);
        assertTrue(javax.media.Controller.class.isInterface());
        assertTrue(javax.media.Clock.class
                .isAssignableFrom(javax.media.Controller.class));
        assertTrue(javax.media.Duration.class
                .isAssignableFrom(javax.media.Controller.class));
        // Static fields:
        // TODO: test LATENCY_UNKNOWN of type javax.media.Time
        assertTrue(javax.media.Controller.Unrealized == 100);
        assertTrue(javax.media.Controller.Realizing == 200);
        assertTrue(javax.media.Controller.Realized == 300);
        assertTrue(javax.media.Controller.Prefetching == 400);
        assertTrue(javax.media.Controller.Prefetched == 500);
        assertTrue(javax.media.Controller.Started == 600);
        // TODO: test RESET of type javax.media.Time
        // TODO: test DURATION_UNBOUNDED of type javax.media.Time
        // TODO: test DURATION_UNKNOWN of type javax.media.Time

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getState", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "close", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getTargetState", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "realize", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "prefetch", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "deallocate", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getStartLatency", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Control[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), javax.media.Control.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "addControllerListener",
                        new Class[] { javax.media.ControllerListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "removeControllerListener",
                        new Class[] { javax.media.ControllerListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod("stop",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "setTimeBase",
                        new Class[] { javax.media.TimeBase.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "syncStart", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "setStopTime", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getStopTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "setMediaTime", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getMediaTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getMediaNanoseconds", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getSyncTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getTimeBase", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.TimeBase.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class
                        .getMethod("mapToTimeBase",
                                new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getRate", new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "setRate", new Class[] { float.class });
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controller.class.getMethod(
                        "getDuration", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Controller.class
                        .getField("LATENCY_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Controller.class
                        .getField("Unrealized");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Controller.class
                        .getField("Realizing");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Controller.class
                        .getField("Realized");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Controller.class
                        .getField("Prefetching");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Controller.class
                        .getField("Prefetched");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Controller.class
                        .getField("Started");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Controller.class.getField("RESET");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Controller.class
                        .getField("DURATION_UNBOUNDED");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Controller.class
                        .getField("DURATION_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Controller o = null;
            o.getState();
            o.close();
            o.getTargetState();
            o.realize();
            o.prefetch();
            o.deallocate();
            o.getStartLatency();
            o.getControls();
            o.getControl((java.lang.String) null);
            o.addControllerListener((javax.media.ControllerListener) null);
            o.removeControllerListener((javax.media.ControllerListener) null);
            o.stop();
            o.setTimeBase((javax.media.TimeBase) null);
            o.syncStart((javax.media.Time) null);
            o.setStopTime((javax.media.Time) null);
            o.getStopTime();
            o.setMediaTime((javax.media.Time) null);
            o.getMediaTime();
            o.getMediaNanoseconds();
            o.getSyncTime();
            o.getTimeBase();
            o.mapToTimeBase((javax.media.Time) null);
            o.getRate();
            o.setRate(0.f);
            o.getDuration();
        }
    }

    public void test_javax_media_ControllerListener() throws Exception
    {
        assertEquals(javax.media.ControllerListener.class.getModifiers(), 1537);
        assertTrue(javax.media.ControllerListener.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.ControllerListener.class
                        .getMethod(
                                "controllerUpdate",
                                new Class[] { javax.media.ControllerEvent.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.ControllerListener o = null;
            o.controllerUpdate((javax.media.ControllerEvent) null);
        }
    }

    public void test_javax_media_Controls() throws Exception
    {
        assertEquals(javax.media.Controls.class.getModifiers(), 1537);
        assertTrue(javax.media.Controls.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Controls.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Controls.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Controls o = null;
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_DataSink() throws Exception
    {
        assertEquals(javax.media.DataSink.class.getModifiers(), 1537);
        assertTrue(javax.media.DataSink.class.isInterface());
        assertTrue(javax.media.MediaHandler.class
                .isAssignableFrom(javax.media.DataSink.class));
        assertTrue(javax.media.Controls.class
                .isAssignableFrom(javax.media.DataSink.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.DataSink.class.getMethod("start",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class.getMethod("stop",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class.getMethod("close",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class.getMethod("open",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class.getMethod(
                        "getContentType", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class.getMethod(
                        "setOutputLocator",
                        new Class[] { javax.media.MediaLocator.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class.getMethod(
                        "getOutputLocator", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.MediaLocator.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class
                        .getMethod(
                                "addDataSinkListener",
                                new Class[] { javax.media.datasink.DataSinkListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class
                        .getMethod(
                                "removeDataSinkListener",
                                new Class[] { javax.media.datasink.DataSinkListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class.getMethod(
                        "setSource",
                        new Class[] { javax.media.protocol.DataSource.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSink.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.DataSink o = null;
            o.start();
            o.stop();
            o.close();
            o.open();
            o.getContentType();
            o.setOutputLocator((javax.media.MediaLocator) null);
            o.getOutputLocator();
            o.addDataSinkListener((javax.media.datasink.DataSinkListener) null);
            o.removeDataSinkListener((javax.media.datasink.DataSinkListener) null);
            o.setSource((javax.media.protocol.DataSource) null);
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_datasink_DataSinkListener() throws Exception
    {
        assertEquals(
                javax.media.datasink.DataSinkListener.class.getModifiers(),
                1537);
        assertTrue(javax.media.datasink.DataSinkListener.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.datasink.DataSinkListener.class
                        .getMethod(
                                "dataSinkUpdate",
                                new Class[] { javax.media.datasink.DataSinkEvent.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.datasink.DataSinkListener o = null;
            o.dataSinkUpdate((javax.media.datasink.DataSinkEvent) null);
        }
    }

    public void test_javax_media_DataSinkProxy() throws Exception
    {
        assertEquals(javax.media.DataSinkProxy.class.getModifiers(), 1537);
        assertTrue(javax.media.DataSinkProxy.class.isInterface());
        assertTrue(javax.media.MediaProxy.class
                .isAssignableFrom(javax.media.DataSinkProxy.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.DataSinkProxy.class.getMethod(
                        "getContentType",
                        new Class[] { javax.media.MediaLocator.class });
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSinkProxy.class.getMethod(
                        "getDataSource", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.DataSource.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.DataSinkProxy.class.getMethod(
                        "setSource",
                        new Class[] { javax.media.protocol.DataSource.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.DataSinkProxy o = null;
            o.getContentType((javax.media.MediaLocator) null);
            o.getDataSource();
            o.setSource((javax.media.protocol.DataSource) null);
        }
    }

    public void test_javax_media_Demultiplexer() throws Exception
    {
        assertEquals(javax.media.Demultiplexer.class.getModifiers(), 1537);
        assertTrue(javax.media.Demultiplexer.class.isInterface());
        assertTrue(javax.media.PlugIn.class
                .isAssignableFrom(javax.media.Demultiplexer.class));
        assertTrue(javax.media.MediaHandler.class
                .isAssignableFrom(javax.media.Demultiplexer.class));
        assertTrue(javax.media.Duration.class
                .isAssignableFrom(javax.media.Demultiplexer.class));
        // Static fields:
        assertTrue(PlugIn.BUFFER_PROCESSED_OK == 0);
        assertTrue(PlugIn.BUFFER_PROCESSED_FAILED == 1);
        assertTrue(PlugIn.INPUT_BUFFER_NOT_CONSUMED == 2);
        assertTrue(PlugIn.OUTPUT_BUFFER_NOT_FILLED == 4);
        assertTrue(PlugIn.PLUGIN_TERMINATED == 8);
        // TODO: test DURATION_UNBOUNDED of type javax.media.Time
        // TODO: test DURATION_UNKNOWN of type javax.media.Time

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "start", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "stop", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "getMediaTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "getDuration", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "getSupportedInputContentDescriptors", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "getTracks", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Track[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "isPositionable", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "isRandomAccess", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "setPosition", new Class[] { javax.media.Time.class,
                                int.class });
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "getName", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "reset", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "close", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "open", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Demultiplexer.class.getMethod(
                        "setSource",
                        new Class[] { javax.media.protocol.DataSource.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Demultiplexer.class
                        .getField("BUFFER_PROCESSED_OK");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Demultiplexer.class
                        .getField("BUFFER_PROCESSED_FAILED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Demultiplexer.class
                        .getField("INPUT_BUFFER_NOT_CONSUMED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Demultiplexer.class
                        .getField("OUTPUT_BUFFER_NOT_FILLED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Demultiplexer.class
                        .getField("PLUGIN_TERMINATED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Demultiplexer.class
                        .getField("DURATION_UNBOUNDED");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Demultiplexer.class
                        .getField("DURATION_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Demultiplexer o = null;
            o.start();
            o.stop();
            o.getMediaTime();
            o.getDuration();
            o.getSupportedInputContentDescriptors();
            o.getTracks();
            o.isPositionable();
            o.isRandomAccess();
            o.setPosition((javax.media.Time) null, 0);
            o.getName();
            o.reset();
            o.close();
            o.open();
            o.getControls();
            o.getControl((java.lang.String) null);
            o.setSource((javax.media.protocol.DataSource) null);
        }
    }

    public void test_javax_media_DownloadProgressListener() throws Exception
    {
        assertEquals(javax.media.DownloadProgressListener.class.getModifiers(),
                1537);
        assertTrue(javax.media.DownloadProgressListener.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.DownloadProgressListener.class
                        .getMethod("downloadUpdate", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.DownloadProgressListener o = null;
            o.downloadUpdate();
        }
    }

    public void test_javax_media_Drainable() throws Exception
    {
        assertEquals(javax.media.Drainable.class.getModifiers(), 1537);
        assertTrue(javax.media.Drainable.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Drainable.class.getMethod("drain",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Drainable o = null;
            o.drain();
        }
    }

    public void test_javax_media_Duration() throws Exception
    {
        assertEquals(javax.media.Duration.class.getModifiers(), 1537);
        assertTrue(javax.media.Duration.class.isInterface());
        // Static fields:
        // TODO: test DURATION_UNBOUNDED of type javax.media.Time
        // TODO: test DURATION_UNKNOWN of type javax.media.Time

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Duration.class.getMethod(
                        "getDuration", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Duration.class
                        .getField("DURATION_UNBOUNDED");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Duration.class
                        .getField("DURATION_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Duration o = null;
            o.getDuration();
        }
    }

    public void test_javax_media_Effect() throws Exception
    {
        assertEquals(javax.media.Effect.class.getModifiers(), 1537);
        assertTrue(javax.media.Effect.class.isInterface());
        assertTrue(javax.media.Codec.class
                .isAssignableFrom(javax.media.Effect.class));
        // Static fields:
        assertTrue(PlugIn.BUFFER_PROCESSED_OK == 0);
        assertTrue(PlugIn.BUFFER_PROCESSED_FAILED == 1);
        assertTrue(PlugIn.INPUT_BUFFER_NOT_CONSUMED == 2);
        assertTrue(PlugIn.OUTPUT_BUFFER_NOT_FILLED == 4);
        assertTrue(PlugIn.PLUGIN_TERMINATED == 8);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Effect.class.getMethod(
                        "getSupportedInputFormats", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod(
                        "getSupportedOutputFormats",
                        new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod(
                        "setInputFormat",
                        new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod(
                        "setOutputFormat",
                        new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod("process",
                        new Class[] { javax.media.Buffer.class,
                                javax.media.Buffer.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod("getName",
                        new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod("reset",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod("close",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod("open",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Effect.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Effect.class
                        .getField("BUFFER_PROCESSED_OK");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Effect.class
                        .getField("BUFFER_PROCESSED_FAILED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Effect.class
                        .getField("INPUT_BUFFER_NOT_CONSUMED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Effect.class
                        .getField("OUTPUT_BUFFER_NOT_FILLED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Effect.class
                        .getField("PLUGIN_TERMINATED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Effect o = null;
            o.getSupportedInputFormats();
            o.getSupportedOutputFormats((javax.media.Format) null);
            o.setInputFormat((javax.media.Format) null);
            o.setOutputFormat((javax.media.Format) null);
            o.process((javax.media.Buffer) null, (javax.media.Buffer) null);
            o.getName();
            o.reset();
            o.close();
            o.open();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_ExtendedCachingControl() throws Exception
    {
        assertEquals(javax.media.ExtendedCachingControl.class.getModifiers(),
                1537);
        assertTrue(javax.media.ExtendedCachingControl.class.isInterface());
        assertTrue(javax.media.CachingControl.class
                .isAssignableFrom(javax.media.ExtendedCachingControl.class));
        // Static fields:
        assertTrue(CachingControl.LENGTH_UNKNOWN == 9223372036854775807L);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("setBufferSize",
                                new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("getBufferSize", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("pauseDownload", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("resumeDownload", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("getStartOffset", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("getEndOffset", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("addDownloadProgressListener", new Class[] {
                                javax.media.DownloadProgressListener.class,
                                int.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod(
                                "removeDownloadProgressListener",
                                new Class[] { javax.media.DownloadProgressListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("getContentLength", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("isDownloading", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("getContentProgress", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.ExtendedCachingControl.class
                        .getMethod("getProgressBarComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.ExtendedCachingControl.class
                        .getField("LENGTH_UNKNOWN");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.ExtendedCachingControl o = null;
            o.setBufferSize((javax.media.Time) null);
            o.getBufferSize();
            o.pauseDownload();
            o.resumeDownload();
            o.getStartOffset();
            o.getEndOffset();
            o.addDownloadProgressListener(
                    (javax.media.DownloadProgressListener) null, 0);
            o.removeDownloadProgressListener((javax.media.DownloadProgressListener) null);
            o.getContentLength();
            o.getControlComponent();
            o.isDownloading();
            o.getContentProgress();
            o.getProgressBarComponent();
        }
    }

    public void test_javax_media_GainChangeListener() throws Exception
    {
        assertEquals(javax.media.GainChangeListener.class.getModifiers(), 1537);
        assertTrue(javax.media.GainChangeListener.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.GainChangeListener.class
                        .getMethod(
                                "gainChange",
                                new Class[] { javax.media.GainChangeEvent.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.GainChangeListener o = null;
            o.gainChange((javax.media.GainChangeEvent) null);
        }
    }

    public void test_javax_media_GainControl() throws Exception
    {
        assertEquals(javax.media.GainControl.class.getModifiers(), 1537);
        assertTrue(javax.media.GainControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.GainControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.GainControl.class.getMethod(
                        "setMute", new Class[] { boolean.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.GainControl.class.getMethod(
                        "getMute", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.GainControl.class.getMethod(
                        "setDB", new Class[] { float.class });
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.GainControl.class.getMethod(
                        "getDB", new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.GainControl.class.getMethod(
                        "setLevel", new Class[] { float.class });
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.GainControl.class.getMethod(
                        "getLevel", new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.GainControl.class.getMethod(
                        "addGainChangeListener",
                        new Class[] { javax.media.GainChangeListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.GainControl.class.getMethod(
                        "removeGainChangeListener",
                        new Class[] { javax.media.GainChangeListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.GainControl.class.getMethod(
                        "getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.GainControl o = null;
            o.setMute(false);
            o.getMute();
            o.setDB(0.f);
            o.getDB();
            o.setLevel(0.f);
            o.getLevel();
            o.addGainChangeListener((javax.media.GainChangeListener) null);
            o.removeGainChangeListener((javax.media.GainChangeListener) null);
            o.getControlComponent();
        }
    }

    public void test_javax_media_MediaHandler() throws Exception
    {
        assertEquals(javax.media.MediaHandler.class.getModifiers(), 1537);
        assertTrue(javax.media.MediaHandler.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.MediaHandler.class.getMethod(
                        "setSource",
                        new Class[] { javax.media.protocol.DataSource.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.MediaHandler o = null;
            o.setSource((javax.media.protocol.DataSource) null);
        }
    }

    public void test_javax_media_MediaProxy() throws Exception
    {
        assertEquals(javax.media.MediaProxy.class.getModifiers(), 1537);
        assertTrue(javax.media.MediaProxy.class.isInterface());
        assertTrue(javax.media.MediaHandler.class
                .isAssignableFrom(javax.media.MediaProxy.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.MediaProxy.class.getMethod(
                        "getDataSource", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.DataSource.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.MediaProxy.class.getMethod(
                        "setSource",
                        new Class[] { javax.media.protocol.DataSource.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.MediaProxy o = null;
            o.getDataSource();
            o.setSource((javax.media.protocol.DataSource) null);
        }
    }

    public void test_javax_media_Multiplexer() throws Exception
    {
        assertEquals(javax.media.Multiplexer.class.getModifiers(), 1537);
        assertTrue(javax.media.Multiplexer.class.isInterface());
        assertTrue(javax.media.PlugIn.class
                .isAssignableFrom(javax.media.Multiplexer.class));
        // Static fields:
        assertTrue(PlugIn.BUFFER_PROCESSED_OK == 0);
        assertTrue(PlugIn.BUFFER_PROCESSED_FAILED == 1);
        assertTrue(PlugIn.INPUT_BUFFER_NOT_CONSUMED == 2);
        assertTrue(PlugIn.OUTPUT_BUFFER_NOT_FILLED == 4);
        assertTrue(PlugIn.PLUGIN_TERMINATED == 8);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Multiplexer.class
                        .getMethod(
                                "setContentDescriptor",
                                new Class[] { javax.media.protocol.ContentDescriptor.class });
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "getDataOutput", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.DataSource.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "getSupportedInputFormats", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "setInputFormat", new Class[] {
                                javax.media.Format.class, int.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "process", new Class[] { javax.media.Buffer.class,
                                int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "getSupportedOutputContentDescriptors",
                        new Class[] { javax.media.Format[].class });
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "setNumTracks", new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "getName", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "reset", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "close", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "open", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Multiplexer.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Multiplexer.class
                        .getField("BUFFER_PROCESSED_OK");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Multiplexer.class
                        .getField("BUFFER_PROCESSED_FAILED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Multiplexer.class
                        .getField("INPUT_BUFFER_NOT_CONSUMED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Multiplexer.class
                        .getField("OUTPUT_BUFFER_NOT_FILLED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Multiplexer.class
                        .getField("PLUGIN_TERMINATED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Multiplexer o = null;
            o.setContentDescriptor((javax.media.protocol.ContentDescriptor) null);
            o.getDataOutput();
            o.getSupportedInputFormats();
            o.setInputFormat((javax.media.Format) null, 0);
            o.process((javax.media.Buffer) null, 0);
            o.getSupportedOutputContentDescriptors((javax.media.Format[]) null);
            o.setNumTracks(0);
            o.getName();
            o.reset();
            o.close();
            o.open();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_Owned() throws Exception
    {
        assertEquals(javax.media.Owned.class.getModifiers(), 1537);
        assertTrue(javax.media.Owned.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Owned.class.getMethod("getOwner",
                        new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Owned o = null;
            o.getOwner();
        }
    }

    public void test_javax_media_Player() throws Exception
    {
        assertEquals(javax.media.Player.class.getModifiers(), 1537);
        assertTrue(javax.media.Player.class.isInterface());
        assertTrue(javax.media.MediaHandler.class
                .isAssignableFrom(javax.media.Player.class));
        assertTrue(javax.media.Controller.class
                .isAssignableFrom(javax.media.Player.class));
        // Static fields:
        // TODO: test LATENCY_UNKNOWN of type javax.media.Time
        assertTrue(Controller.Unrealized == 100);
        assertTrue(Controller.Realizing == 200);
        assertTrue(Controller.Realized == 300);
        assertTrue(Controller.Prefetching == 400);
        assertTrue(Controller.Prefetched == 500);
        assertTrue(Controller.Started == 600);
        // TODO: test RESET of type javax.media.Time
        // TODO: test DURATION_UNBOUNDED of type javax.media.Time
        // TODO: test DURATION_UNKNOWN of type javax.media.Time

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Player.class.getMethod("start",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getVisualComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getGainControl", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.GainControl.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getControlPanelComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "addController",
                        new Class[] { javax.media.Controller.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "removeController",
                        new Class[] { javax.media.Controller.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "setSource",
                        new Class[] { javax.media.protocol.DataSource.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod("getState",
                        new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod("close",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getTargetState", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod("realize",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod("prefetch",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "deallocate", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getStartLatency", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Control[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), javax.media.Control.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "addControllerListener",
                        new Class[] { javax.media.ControllerListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "removeControllerListener",
                        new Class[] { javax.media.ControllerListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod("stop",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "setTimeBase",
                        new Class[] { javax.media.TimeBase.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "syncStart", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "setStopTime", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getStopTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "setMediaTime", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getMediaTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getMediaNanoseconds", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getSyncTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getTimeBase", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.TimeBase.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class
                        .getMethod("mapToTimeBase",
                                new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod("getRate",
                        new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod("setRate",
                        new Class[] { float.class });
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Player.class.getMethod(
                        "getDuration", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Player.class
                        .getField("LATENCY_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Player.class.getField("Unrealized");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Player.class.getField("Realizing");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Player.class.getField("Realized");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Player.class
                        .getField("Prefetching");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Player.class.getField("Prefetched");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Player.class.getField("Started");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Player.class.getField("RESET");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Player.class
                        .getField("DURATION_UNBOUNDED");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Player.class
                        .getField("DURATION_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Player o = null;
            o.start();
            o.getVisualComponent();
            o.getGainControl();
            o.getControlPanelComponent();
            o.addController((javax.media.Controller) null);
            o.removeController((javax.media.Controller) null);
            o.setSource((javax.media.protocol.DataSource) null);
            o.getState();
            o.close();
            o.getTargetState();
            o.realize();
            o.prefetch();
            o.deallocate();
            o.getStartLatency();
            o.getControls();
            o.getControl((java.lang.String) null);
            o.addControllerListener((javax.media.ControllerListener) null);
            o.removeControllerListener((javax.media.ControllerListener) null);
            o.stop();
            o.setTimeBase((javax.media.TimeBase) null);
            o.syncStart((javax.media.Time) null);
            o.setStopTime((javax.media.Time) null);
            o.getStopTime();
            o.setMediaTime((javax.media.Time) null);
            o.getMediaTime();
            o.getMediaNanoseconds();
            o.getSyncTime();
            o.getTimeBase();
            o.mapToTimeBase((javax.media.Time) null);
            o.getRate();
            o.setRate(0.f);
            o.getDuration();
        }
    }

    public void test_javax_media_PlugIn() throws Exception
    {
        assertEquals(javax.media.PlugIn.class.getModifiers(), 1537);
        assertTrue(javax.media.PlugIn.class.isInterface());
        assertTrue(javax.media.Controls.class
                .isAssignableFrom(javax.media.PlugIn.class));
        // Static fields:
        assertTrue(javax.media.PlugIn.BUFFER_PROCESSED_OK == 0);
        assertTrue(javax.media.PlugIn.BUFFER_PROCESSED_FAILED == 1);
        assertTrue(javax.media.PlugIn.INPUT_BUFFER_NOT_CONSUMED == 2);
        assertTrue(javax.media.PlugIn.OUTPUT_BUFFER_NOT_FILLED == 4);
        assertTrue(javax.media.PlugIn.PLUGIN_TERMINATED == 8);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.PlugIn.class.getMethod("getName",
                        new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.PlugIn.class.getMethod("reset",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.PlugIn.class.getMethod("close",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.PlugIn.class.getMethod("open",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.PlugIn.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.PlugIn.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.PlugIn.class
                        .getField("BUFFER_PROCESSED_OK");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.PlugIn.class
                        .getField("BUFFER_PROCESSED_FAILED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.PlugIn.class
                        .getField("INPUT_BUFFER_NOT_CONSUMED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.PlugIn.class
                        .getField("OUTPUT_BUFFER_NOT_FILLED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.PlugIn.class
                        .getField("PLUGIN_TERMINATED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.PlugIn o = null;
            o.getName();
            o.reset();
            o.close();
            o.open();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_Prefetchable() throws Exception
    {
        assertEquals(javax.media.Prefetchable.class.getModifiers(), 1537);
        assertTrue(javax.media.Prefetchable.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Prefetchable.class.getMethod(
                        "isPrefetched", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Prefetchable o = null;
            o.isPrefetched();
        }
    }

    public void test_javax_media_Processor() throws Exception
    {
        assertEquals(javax.media.Processor.class.getModifiers(), 1537);
        assertTrue(javax.media.Processor.class.isInterface());
        assertTrue(javax.media.Player.class
                .isAssignableFrom(javax.media.Processor.class));
        // Static fields:
        assertTrue(javax.media.Processor.Configuring == 140);
        assertTrue(javax.media.Processor.Configured == 180);
        // TODO: test LATENCY_UNKNOWN of type javax.media.Time
        assertTrue(Controller.Unrealized == 100);
        assertTrue(Controller.Realizing == 200);
        assertTrue(Controller.Realized == 300);
        assertTrue(Controller.Prefetching == 400);
        assertTrue(Controller.Prefetched == 500);
        assertTrue(Controller.Started == 600);
        // TODO: test RESET of type javax.media.Time
        // TODO: test DURATION_UNBOUNDED of type javax.media.Time
        // TODO: test DURATION_UNKNOWN of type javax.media.Time

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "configure", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getTrackControls", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.control.TrackControl[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getSupportedContentDescriptors", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class
                        .getMethod(
                                "setContentDescriptor",
                                new Class[] { javax.media.protocol.ContentDescriptor.class });
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getContentDescriptor", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getDataOutput", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.DataSource.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod("start",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getVisualComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getGainControl", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.GainControl.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getControlPanelComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "addController",
                        new Class[] { javax.media.Controller.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "removeController",
                        new Class[] { javax.media.Controller.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "setSource",
                        new Class[] { javax.media.protocol.DataSource.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getState", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod("close",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getTargetState", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "realize", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "prefetch", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "deallocate", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getStartLatency", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Control[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), javax.media.Control.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "addControllerListener",
                        new Class[] { javax.media.ControllerListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "removeControllerListener",
                        new Class[] { javax.media.ControllerListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod("stop",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "setTimeBase",
                        new Class[] { javax.media.TimeBase.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "syncStart", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "setStopTime", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getStopTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "setMediaTime", new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getMediaTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getMediaNanoseconds", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getSyncTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getTimeBase", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.TimeBase.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class
                        .getMethod("mapToTimeBase",
                                new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getRate", new Class[] {});
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "setRate", new Class[] { float.class });
                assertEquals(m.getReturnType(), float.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Processor.class.getMethod(
                        "getDuration", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Processor.class
                        .getField("Configuring");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class
                        .getField("Configured");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class
                        .getField("LATENCY_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class
                        .getField("Unrealized");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class
                        .getField("Realizing");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class
                        .getField("Realized");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class
                        .getField("Prefetching");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class
                        .getField("Prefetched");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class.getField("Started");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class.getField("RESET");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class
                        .getField("DURATION_UNBOUNDED");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Processor.class
                        .getField("DURATION_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Processor o = null;
            o.configure();
            o.getTrackControls();
            o.getSupportedContentDescriptors();
            o.setContentDescriptor((javax.media.protocol.ContentDescriptor) null);
            o.getContentDescriptor();
            o.getDataOutput();
            o.start();
            o.getVisualComponent();
            o.getGainControl();
            o.getControlPanelComponent();
            o.addController((javax.media.Controller) null);
            o.removeController((javax.media.Controller) null);
            o.setSource((javax.media.protocol.DataSource) null);
            o.getState();
            o.close();
            o.getTargetState();
            o.realize();
            o.prefetch();
            o.deallocate();
            o.getStartLatency();
            o.getControls();
            o.getControl((java.lang.String) null);
            o.addControllerListener((javax.media.ControllerListener) null);
            o.removeControllerListener((javax.media.ControllerListener) null);
            o.stop();
            o.setTimeBase((javax.media.TimeBase) null);
            o.syncStart((javax.media.Time) null);
            o.setStopTime((javax.media.Time) null);
            o.getStopTime();
            o.setMediaTime((javax.media.Time) null);
            o.getMediaTime();
            o.getMediaNanoseconds();
            o.getSyncTime();
            o.getTimeBase();
            o.mapToTimeBase((javax.media.Time) null);
            o.getRate();
            o.setRate(0.f);
            o.getDuration();
        }
    }

    public void test_javax_media_protocol_BufferTransferHandler()
            throws Exception
    {
        assertEquals(
                javax.media.protocol.BufferTransferHandler.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.BufferTransferHandler.class
                .isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.BufferTransferHandler.class
                        .getMethod(
                                "transferData",
                                new Class[] { javax.media.protocol.PushBufferStream.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.BufferTransferHandler o = null;
            o.transferData((javax.media.protocol.PushBufferStream) null);
        }
    }

    public void test_javax_media_protocol_CachedStream() throws Exception
    {
        assertEquals(javax.media.protocol.CachedStream.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.CachedStream.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.CachedStream.class
                        .getMethod("setEnabledBuffering",
                                new Class[] { boolean.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.CachedStream.class
                        .getMethod("getEnabledBuffering", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.CachedStream.class
                        .getMethod("willReadBytesBlock", new Class[] {
                                long.class, int.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.CachedStream.class
                        .getMethod("willReadBytesBlock",
                                new Class[] { int.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.CachedStream.class
                        .getMethod("abortRead", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.CachedStream o = null;
            o.setEnabledBuffering(false);
            o.getEnabledBuffering();
            o.willReadBytesBlock(0L, 0);
            o.willReadBytesBlock(0);
            o.abortRead();
        }
    }

    public void test_javax_media_protocol_CaptureDevice() throws Exception
    {
        assertEquals(javax.media.protocol.CaptureDevice.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.CaptureDevice.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.CaptureDevice.class
                        .getMethod("start", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.CaptureDevice.class
                        .getMethod("stop", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.CaptureDevice.class
                        .getMethod("connect", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.CaptureDevice.class
                        .getMethod("getCaptureDeviceInfo", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.CaptureDeviceInfo.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.CaptureDevice.class
                        .getMethod("getFormatControls", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.control.FormatControl[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.CaptureDevice.class
                        .getMethod("disconnect", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.CaptureDevice o = null;
            o.start();
            o.stop();
            o.connect();
            o.getCaptureDeviceInfo();
            o.getFormatControls();
            o.disconnect();
        }
    }

    public void test_javax_media_protocol_Controls() throws Exception
    {
        assertEquals(javax.media.protocol.Controls.class.getModifiers(), 1537);
        assertTrue(javax.media.protocol.Controls.class.isInterface());
        assertTrue(javax.media.Controls.class
                .isAssignableFrom(javax.media.protocol.Controls.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.Controls.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.Controls.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.Controls o = null;
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_protocol_Positionable() throws Exception
    {
        assertEquals(javax.media.protocol.Positionable.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.Positionable.class.isInterface());
        // Static fields:
        assertTrue(javax.media.protocol.Positionable.RoundUp == 1);
        assertTrue(javax.media.protocol.Positionable.RoundDown == 2);
        assertTrue(javax.media.protocol.Positionable.RoundNearest == 3);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.Positionable.class
                        .getMethod("isRandomAccess", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.Positionable.class
                        .getMethod("setPosition", new Class[] {
                                javax.media.Time.class, int.class });
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.protocol.Positionable.class
                        .getField("RoundUp");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.protocol.Positionable.class
                        .getField("RoundDown");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.protocol.Positionable.class
                        .getField("RoundNearest");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.Positionable o = null;
            o.isRandomAccess();
            o.setPosition((javax.media.Time) null, 0);
        }
    }

    public void test_javax_media_protocol_PullBufferStream() throws Exception
    {
        assertEquals(
                javax.media.protocol.PullBufferStream.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.PullBufferStream.class.isInterface());
        assertTrue(javax.media.protocol.SourceStream.class
                .isAssignableFrom(javax.media.protocol.PullBufferStream.class));
        // Static fields:
        assertTrue(SourceStream.LENGTH_UNKNOWN == -1L);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.PullBufferStream.class
                        .getMethod("read",
                                new Class[] { javax.media.Buffer.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullBufferStream.class
                        .getMethod("getFormat", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullBufferStream.class
                        .getMethod("willReadBlock", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullBufferStream.class
                        .getMethod("getContentLength", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullBufferStream.class
                        .getMethod("getContentDescriptor", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullBufferStream.class
                        .getMethod("endOfStream", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullBufferStream.class
                        .getMethod("getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullBufferStream.class
                        .getMethod("getControl",
                                new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.protocol.PullBufferStream.class
                        .getField("LENGTH_UNKNOWN");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.PullBufferStream o = null;
            o.read((javax.media.Buffer) null);
            o.getFormat();
            o.willReadBlock();
            o.getContentLength();
            o.getContentDescriptor();
            o.endOfStream();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_protocol_PullSourceStream() throws Exception
    {
        assertEquals(
                javax.media.protocol.PullSourceStream.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.PullSourceStream.class.isInterface());
        assertTrue(javax.media.protocol.SourceStream.class
                .isAssignableFrom(javax.media.protocol.PullSourceStream.class));
        // Static fields:
        assertTrue(SourceStream.LENGTH_UNKNOWN == -1L);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.PullSourceStream.class
                        .getMethod("read", new Class[] { byte[].class,
                                int.class, int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullSourceStream.class
                        .getMethod("willReadBlock", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullSourceStream.class
                        .getMethod("getContentLength", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullSourceStream.class
                        .getMethod("getContentDescriptor", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullSourceStream.class
                        .getMethod("endOfStream", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullSourceStream.class
                        .getMethod("getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PullSourceStream.class
                        .getMethod("getControl",
                                new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.protocol.PullSourceStream.class
                        .getField("LENGTH_UNKNOWN");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.PullSourceStream o = null;
            o.read((byte[]) null, 0, 0);
            o.willReadBlock();
            o.getContentLength();
            o.getContentDescriptor();
            o.endOfStream();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_protocol_PushBufferStream() throws Exception
    {
        assertEquals(
                javax.media.protocol.PushBufferStream.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.PushBufferStream.class.isInterface());
        assertTrue(javax.media.protocol.SourceStream.class
                .isAssignableFrom(javax.media.protocol.PushBufferStream.class));
        // Static fields:
        assertTrue(SourceStream.LENGTH_UNKNOWN == -1L);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.PushBufferStream.class
                        .getMethod("read",
                                new Class[] { javax.media.Buffer.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushBufferStream.class
                        .getMethod("getFormat", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushBufferStream.class
                        .getMethod(
                                "setTransferHandler",
                                new Class[] { javax.media.protocol.BufferTransferHandler.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushBufferStream.class
                        .getMethod("getContentLength", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushBufferStream.class
                        .getMethod("getContentDescriptor", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushBufferStream.class
                        .getMethod("endOfStream", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushBufferStream.class
                        .getMethod("getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushBufferStream.class
                        .getMethod("getControl",
                                new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.protocol.PushBufferStream.class
                        .getField("LENGTH_UNKNOWN");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.PushBufferStream o = null;
            o.read((javax.media.Buffer) null);
            o.getFormat();
            o.setTransferHandler((javax.media.protocol.BufferTransferHandler) null);
            o.getContentLength();
            o.getContentDescriptor();
            o.endOfStream();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_protocol_PushSourceStream() throws Exception
    {
        assertEquals(
                javax.media.protocol.PushSourceStream.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.PushSourceStream.class.isInterface());
        assertTrue(javax.media.protocol.SourceStream.class
                .isAssignableFrom(javax.media.protocol.PushSourceStream.class));
        // Static fields:
        assertTrue(SourceStream.LENGTH_UNKNOWN == -1L);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.PushSourceStream.class
                        .getMethod("read", new Class[] { byte[].class,
                                int.class, int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushSourceStream.class
                        .getMethod(
                                "setTransferHandler",
                                new Class[] { javax.media.protocol.SourceTransferHandler.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushSourceStream.class
                        .getMethod("getMinimumTransferSize", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushSourceStream.class
                        .getMethod("getContentLength", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushSourceStream.class
                        .getMethod("getContentDescriptor", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushSourceStream.class
                        .getMethod("endOfStream", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushSourceStream.class
                        .getMethod("getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.PushSourceStream.class
                        .getMethod("getControl",
                                new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.protocol.PushSourceStream.class
                        .getField("LENGTH_UNKNOWN");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.PushSourceStream o = null;
            o.read((byte[]) null, 0, 0);
            o.setTransferHandler((javax.media.protocol.SourceTransferHandler) null);
            o.getMinimumTransferSize();
            o.getContentLength();
            o.getContentDescriptor();
            o.endOfStream();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_protocol_RateConfiguration() throws Exception
    {
        assertEquals(
                javax.media.protocol.RateConfiguration.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.RateConfiguration.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.RateConfiguration.class
                        .getMethod("getRate", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.RateRange.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.RateConfiguration.class
                        .getMethod("getStreams", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.SourceStream[].class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.RateConfiguration o = null;
            o.getRate();
            o.getStreams();
        }
    }

    public void test_javax_media_protocol_RateConfigureable() throws Exception
    {
        assertEquals(
                javax.media.protocol.RateConfigureable.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.RateConfigureable.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.RateConfigureable.class
                        .getMethod("getRateConfigurations", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.RateConfiguration[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.RateConfigureable.class
                        .getMethod(
                                "setRateConfiguration",
                                new Class[] { javax.media.protocol.RateConfiguration.class });
                assertEquals(m.getReturnType(),
                        javax.media.protocol.RateConfiguration.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.RateConfigureable o = null;
            o.getRateConfigurations();
            o.setRateConfiguration((javax.media.protocol.RateConfiguration) null);
        }
    }

    public void test_javax_media_protocol_Seekable() throws Exception
    {
        assertEquals(javax.media.protocol.Seekable.class.getModifiers(), 1537);
        assertTrue(javax.media.protocol.Seekable.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.Seekable.class.getMethod(
                        "seek", new Class[] { long.class });
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.Seekable.class.getMethod(
                        "isRandomAccess", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.Seekable.class.getMethod(
                        "tell", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.Seekable o = null;
            o.seek(0L);
            o.isRandomAccess();
            o.tell();
        }
    }

    public void test_javax_media_protocol_SourceCloneable() throws Exception
    {
        assertEquals(javax.media.protocol.SourceCloneable.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.SourceCloneable.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.SourceCloneable.class
                        .getMethod("createClone", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.DataSource.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.SourceCloneable o = null;
            o.createClone();
        }
    }

    public void test_javax_media_protocol_SourceStream() throws Exception
    {
        assertEquals(javax.media.protocol.SourceStream.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.SourceStream.class.isInterface());
        assertTrue(javax.media.protocol.Controls.class
                .isAssignableFrom(javax.media.protocol.SourceStream.class));
        // Static fields:
        assertTrue(javax.media.protocol.SourceStream.LENGTH_UNKNOWN == -1L);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.SourceStream.class
                        .getMethod("getContentLength", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.SourceStream.class
                        .getMethod("getContentDescriptor", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.ContentDescriptor.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.SourceStream.class
                        .getMethod("endOfStream", new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.SourceStream.class
                        .getMethod("getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.protocol.SourceStream.class
                        .getMethod("getControl",
                                new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.protocol.SourceStream.class
                        .getField("LENGTH_UNKNOWN");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.SourceStream o = null;
            o.getContentLength();
            o.getContentDescriptor();
            o.endOfStream();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_protocol_SourceTransferHandler()
            throws Exception
    {
        assertEquals(
                javax.media.protocol.SourceTransferHandler.class.getModifiers(),
                1537);
        assertTrue(javax.media.protocol.SourceTransferHandler.class
                .isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.protocol.SourceTransferHandler.class
                        .getMethod(
                                "transferData",
                                new Class[] { javax.media.protocol.PushSourceStream.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.protocol.SourceTransferHandler o = null;
            o.transferData((javax.media.protocol.PushSourceStream) null);
        }
    }

    public void test_javax_media_Renderer() throws Exception
    {
        assertEquals(javax.media.Renderer.class.getModifiers(), 1537);
        assertTrue(javax.media.Renderer.class.isInterface());
        assertTrue(javax.media.PlugIn.class
                .isAssignableFrom(javax.media.Renderer.class));
        // Static fields:
        assertTrue(PlugIn.BUFFER_PROCESSED_OK == 0);
        assertTrue(PlugIn.BUFFER_PROCESSED_FAILED == 1);
        assertTrue(PlugIn.INPUT_BUFFER_NOT_CONSUMED == 2);
        assertTrue(PlugIn.OUTPUT_BUFFER_NOT_FILLED == 4);
        assertTrue(PlugIn.PLUGIN_TERMINATED == 8);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Renderer.class.getMethod("start",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod("stop",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod(
                        "getSupportedInputFormats", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod(
                        "setInputFormat",
                        new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod(
                        "process", new Class[] { javax.media.Buffer.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod(
                        "getName", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod("reset",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod("close",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod("open",
                        new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod(
                        "getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Renderer.class.getMethod(
                        "getControl", new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Renderer.class
                        .getField("BUFFER_PROCESSED_OK");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Renderer.class
                        .getField("BUFFER_PROCESSED_FAILED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Renderer.class
                        .getField("INPUT_BUFFER_NOT_CONSUMED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Renderer.class
                        .getField("OUTPUT_BUFFER_NOT_FILLED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Renderer.class
                        .getField("PLUGIN_TERMINATED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Renderer o = null;
            o.start();
            o.stop();
            o.getSupportedInputFormats();
            o.setInputFormat((javax.media.Format) null);
            o.process((javax.media.Buffer) null);
            o.getName();
            o.reset();
            o.close();
            o.open();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_renderer_VideoRenderer() throws Exception
    {
        assertEquals(javax.media.renderer.VideoRenderer.class.getModifiers(),
                1537);
        assertTrue(javax.media.renderer.VideoRenderer.class.isInterface());
        assertTrue(javax.media.Renderer.class
                .isAssignableFrom(javax.media.renderer.VideoRenderer.class));
        // Static fields:
        assertTrue(PlugIn.BUFFER_PROCESSED_OK == 0);
        assertTrue(PlugIn.BUFFER_PROCESSED_FAILED == 1);
        assertTrue(PlugIn.INPUT_BUFFER_NOT_CONSUMED == 2);
        assertTrue(PlugIn.OUTPUT_BUFFER_NOT_FILLED == 4);
        assertTrue(PlugIn.PLUGIN_TERMINATED == 8);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("getComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("setComponent",
                                new Class[] { java.awt.Component.class });
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("setBounds",
                                new Class[] { java.awt.Rectangle.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("getBounds", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Rectangle.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("start", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("stop", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("getSupportedInputFormats", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("setInputFormat",
                                new Class[] { javax.media.Format.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("process",
                                new Class[] { javax.media.Buffer.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("getName", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("reset", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("close", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("open", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.renderer.VideoRenderer.class
                        .getMethod("getControl",
                                new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.renderer.VideoRenderer.class
                        .getField("BUFFER_PROCESSED_OK");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.renderer.VideoRenderer.class
                        .getField("BUFFER_PROCESSED_FAILED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.renderer.VideoRenderer.class
                        .getField("INPUT_BUFFER_NOT_CONSUMED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.renderer.VideoRenderer.class
                        .getField("OUTPUT_BUFFER_NOT_FILLED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.renderer.VideoRenderer.class
                        .getField("PLUGIN_TERMINATED");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.renderer.VideoRenderer o = null;
            o.getComponent();
            o.setComponent((java.awt.Component) null);
            o.setBounds((java.awt.Rectangle) null);
            o.getBounds();
            o.start();
            o.stop();
            o.getSupportedInputFormats();
            o.setInputFormat((javax.media.Format) null);
            o.process((javax.media.Buffer) null);
            o.getName();
            o.reset();
            o.close();
            o.open();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_renderer_VisualContainer() throws Exception
    {
        assertEquals(javax.media.renderer.VisualContainer.class.getModifiers(),
                1537);
        assertTrue(javax.media.renderer.VisualContainer.class.isInterface());
        // Methods (reflection):
        if (true)
        {
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.renderer.VisualContainer o = null;
        }
    }

    public void test_javax_media_rtp_DataChannel() throws Exception
    {
        assertEquals(javax.media.rtp.DataChannel.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.DataChannel.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.DataChannel.class.getMethod(
                        "getControlChannel", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.RTPPushDataSource.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.DataChannel o = null;
            o.getControlChannel();
        }
    }

    public void test_javax_media_rtp_GlobalReceptionStats() throws Exception
    {
        assertEquals(javax.media.rtp.GlobalReceptionStats.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.GlobalReceptionStats.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getPacketsRecd", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getBytesRecd", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getBadRTPkts", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getLocalColls", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getRemoteColls", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getPacketsLooped", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getTransmitFailed", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getRTCPRecd", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getSRRecd", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getBadRTCPPkts", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getUnknownTypes", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getMalformedRR", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getMalformedSDES", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getMalformedBye", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalReceptionStats.class
                        .getMethod("getMalformedSR", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.GlobalReceptionStats o = null;
            o.getPacketsRecd();
            o.getBytesRecd();
            o.getBadRTPkts();
            o.getLocalColls();
            o.getRemoteColls();
            o.getPacketsLooped();
            o.getTransmitFailed();
            o.getRTCPRecd();
            o.getSRRecd();
            o.getBadRTCPPkts();
            o.getUnknownTypes();
            o.getMalformedRR();
            o.getMalformedSDES();
            o.getMalformedBye();
            o.getMalformedSR();
        }
    }

    public void test_javax_media_rtp_GlobalTransmissionStats() throws Exception
    {
        assertEquals(
                javax.media.rtp.GlobalTransmissionStats.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.GlobalTransmissionStats.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.GlobalTransmissionStats.class
                        .getMethod("getLocalColls", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalTransmissionStats.class
                        .getMethod("getRemoteColls", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalTransmissionStats.class
                        .getMethod("getTransmitFailed", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalTransmissionStats.class
                        .getMethod("getRTPSent", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalTransmissionStats.class
                        .getMethod("getBytesSent", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.GlobalTransmissionStats.class
                        .getMethod("getRTCPSent", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.GlobalTransmissionStats o = null;
            o.getLocalColls();
            o.getRemoteColls();
            o.getTransmitFailed();
            o.getRTPSent();
            o.getBytesSent();
            o.getRTCPSent();
        }
    }

    public void test_javax_media_rtp_LocalParticipant() throws Exception
    {
        assertEquals(javax.media.rtp.LocalParticipant.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.LocalParticipant.class.isInterface());
        assertTrue(javax.media.rtp.Participant.class
                .isAssignableFrom(javax.media.rtp.LocalParticipant.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.LocalParticipant.class
                        .getMethod(
                                "setSourceDescription",
                                new Class[] { javax.media.rtp.rtcp.SourceDescription[].class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.LocalParticipant.class
                        .getMethod("getStreams", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.LocalParticipant.class
                        .getMethod("getReports", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.LocalParticipant.class
                        .getMethod("getCNAME", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.LocalParticipant.class
                        .getMethod("getSourceDescription", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.LocalParticipant o = null;
            o.setSourceDescription((javax.media.rtp.rtcp.SourceDescription[]) null);
            o.getStreams();
            o.getReports();
            o.getCNAME();
            o.getSourceDescription();
        }
    }

    public void test_javax_media_rtp_OutputDataStream() throws Exception
    {
        assertEquals(javax.media.rtp.OutputDataStream.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.OutputDataStream.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.OutputDataStream.class
                        .getMethod("write", new Class[] { byte[].class,
                                int.class, int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.OutputDataStream o = null;
            o.write((byte[]) null, 0, 0);
        }
    }

    public void test_javax_media_rtp_Participant() throws Exception
    {
        assertEquals(javax.media.rtp.Participant.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.Participant.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.Participant.class.getMethod(
                        "getStreams", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.Participant.class.getMethod(
                        "getReports", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.Participant.class.getMethod(
                        "getCNAME", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.Participant.class.getMethod(
                        "getSourceDescription", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.Participant o = null;
            o.getStreams();
            o.getReports();
            o.getCNAME();
            o.getSourceDescription();
        }
    }

    public void test_javax_media_rtp_ReceiveStream() throws Exception
    {
        assertEquals(javax.media.rtp.ReceiveStream.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.ReceiveStream.class.isInterface());
        assertTrue(javax.media.rtp.RTPStream.class
                .isAssignableFrom(javax.media.rtp.ReceiveStream.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.ReceiveStream.class.getMethod(
                        "getSourceReceptionStats", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.ReceptionStats.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.ReceiveStream.class.getMethod(
                        "getSSRC", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.ReceiveStream.class.getMethod(
                        "getDataSource", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.DataSource.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.ReceiveStream.class.getMethod(
                        "getParticipant", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.Participant.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.ReceiveStream.class.getMethod(
                        "getSenderReport", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.rtcp.SenderReport.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.ReceiveStream o = null;
            o.getSourceReceptionStats();
            o.getSSRC();
            o.getDataSource();
            o.getParticipant();
            o.getSenderReport();
        }
    }

    public void test_javax_media_rtp_ReceiveStreamListener() throws Exception
    {
        assertEquals(
                javax.media.rtp.ReceiveStreamListener.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.ReceiveStreamListener.class.isInterface());
        assertTrue(java.util.EventListener.class
                .isAssignableFrom(javax.media.rtp.ReceiveStreamListener.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.ReceiveStreamListener.class
                        .getMethod(
                                "update",
                                new Class[] { javax.media.rtp.event.ReceiveStreamEvent.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.ReceiveStreamListener o = null;
            o.update((javax.media.rtp.event.ReceiveStreamEvent) null);
        }
    }

    public void test_javax_media_rtp_ReceptionStats() throws Exception
    {
        assertEquals(javax.media.rtp.ReceptionStats.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.ReceptionStats.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.ReceptionStats.class
                        .getMethod("getPDUlost", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.ReceptionStats.class
                        .getMethod("getPDUProcessed", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.ReceptionStats.class
                        .getMethod("getPDUMisOrd", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.ReceptionStats.class
                        .getMethod("getPDUInvalid", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.ReceptionStats.class
                        .getMethod("getPDUDuplicate", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.ReceptionStats o = null;
            o.getPDUlost();
            o.getPDUProcessed();
            o.getPDUMisOrd();
            o.getPDUInvalid();
            o.getPDUDuplicate();
        }
    }

    public void test_javax_media_rtp_RemoteListener() throws Exception
    {
        assertEquals(javax.media.rtp.RemoteListener.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.RemoteListener.class.isInterface());
        assertTrue(java.util.EventListener.class
                .isAssignableFrom(javax.media.rtp.RemoteListener.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.RemoteListener.class
                        .getMethod(
                                "update",
                                new Class[] { javax.media.rtp.event.RemoteEvent.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.RemoteListener o = null;
            o.update((javax.media.rtp.event.RemoteEvent) null);
        }
    }

    public void test_javax_media_rtp_RemoteParticipant() throws Exception
    {
        assertEquals(javax.media.rtp.RemoteParticipant.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.RemoteParticipant.class.isInterface());
        assertTrue(javax.media.rtp.Participant.class
                .isAssignableFrom(javax.media.rtp.RemoteParticipant.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.RemoteParticipant.class
                        .getMethod("getStreams", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RemoteParticipant.class
                        .getMethod("getReports", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RemoteParticipant.class
                        .getMethod("getCNAME", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RemoteParticipant.class
                        .getMethod("getSourceDescription", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.RemoteParticipant o = null;
            o.getStreams();
            o.getReports();
            o.getCNAME();
            o.getSourceDescription();
        }
    }

    public void test_javax_media_rtp_rtcp_Feedback() throws Exception
    {
        assertEquals(javax.media.rtp.rtcp.Feedback.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.rtcp.Feedback.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.rtcp.Feedback.class.getMethod(
                        "getSSRC", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.Feedback.class.getMethod(
                        "getFractionLost", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.Feedback.class.getMethod(
                        "getNumLost", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.Feedback.class.getMethod(
                        "getXtndSeqNum", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.Feedback.class.getMethod(
                        "getJitter", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.Feedback.class.getMethod(
                        "getLSR", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.Feedback.class.getMethod(
                        "getDLSR", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.rtcp.Feedback o = null;
            o.getSSRC();
            o.getFractionLost();
            o.getNumLost();
            o.getXtndSeqNum();
            o.getJitter();
            o.getLSR();
            o.getDLSR();
        }
    }

    public void test_javax_media_rtp_rtcp_ReceiverReport() throws Exception
    {
        assertEquals(javax.media.rtp.rtcp.ReceiverReport.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.rtcp.ReceiverReport.class.isInterface());
        assertTrue(javax.media.rtp.rtcp.Report.class
                .isAssignableFrom(javax.media.rtp.rtcp.ReceiverReport.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.rtcp.ReceiverReport.class
                        .getMethod("getSSRC", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.ReceiverReport.class
                        .getMethod("getSourceDescription", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.ReceiverReport.class
                        .getMethod("getParticipant", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.Participant.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.ReceiverReport.class
                        .getMethod("getFeedbackReports", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.rtcp.ReceiverReport o = null;
            o.getSSRC();
            o.getSourceDescription();
            o.getParticipant();
            o.getFeedbackReports();
        }
    }

    public void test_javax_media_rtp_rtcp_Report() throws Exception
    {
        assertEquals(javax.media.rtp.rtcp.Report.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.rtcp.Report.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.rtcp.Report.class.getMethod(
                        "getSSRC", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.Report.class.getMethod(
                        "getSourceDescription", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.Report.class.getMethod(
                        "getParticipant", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.Participant.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.Report.class.getMethod(
                        "getFeedbackReports", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.rtcp.Report o = null;
            o.getSSRC();
            o.getSourceDescription();
            o.getParticipant();
            o.getFeedbackReports();
        }
    }

    public void test_javax_media_rtp_rtcp_SenderReport() throws Exception
    {
        assertEquals(javax.media.rtp.rtcp.SenderReport.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.rtcp.SenderReport.class.isInterface());
        assertTrue(javax.media.rtp.rtcp.Report.class
                .isAssignableFrom(javax.media.rtp.rtcp.SenderReport.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getStream", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.rtp.RTPStream.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getSenderPacketCount", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getSenderByteCount", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getNTPTimeStampMSW", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getNTPTimeStampLSW", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getRTPTimeStamp", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getSenderFeedback", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.rtcp.Feedback.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getSSRC", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getSourceDescription", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getParticipant", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.Participant.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.rtcp.SenderReport.class
                        .getMethod("getFeedbackReports", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.rtcp.SenderReport o = null;
            o.getStream();
            o.getSenderPacketCount();
            o.getSenderByteCount();
            o.getNTPTimeStampMSW();
            o.getNTPTimeStampLSW();
            o.getRTPTimeStamp();
            o.getSenderFeedback();
            o.getSSRC();
            o.getSourceDescription();
            o.getParticipant();
            o.getFeedbackReports();
        }
    }

    public void test_javax_media_rtp_RTPConnector() throws Exception
    {
        assertEquals(javax.media.rtp.RTPConnector.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.RTPConnector.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "close", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "getDataInputStream", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.PushSourceStream.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "getDataOutputStream", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.OutputDataStream.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "getControlInputStream", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.PushSourceStream.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "getControlOutputStream", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.OutputDataStream.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "setReceiveBufferSize", new Class[] { int.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "getReceiveBufferSize", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "setSendBufferSize", new Class[] { int.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "getSendBufferSize", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "getRTCPBandwidthFraction", new Class[] {});
                assertEquals(m.getReturnType(), double.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPConnector.class.getMethod(
                        "getRTCPSenderBandwidthFraction", new Class[] {});
                assertEquals(m.getReturnType(), double.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.RTPConnector o = null;
            o.close();
            o.getDataInputStream();
            o.getDataOutputStream();
            o.getControlInputStream();
            o.getControlOutputStream();
            o.setReceiveBufferSize(0);
            o.getReceiveBufferSize();
            o.setSendBufferSize(0);
            o.getSendBufferSize();
            o.getRTCPBandwidthFraction();
            o.getRTCPSenderBandwidthFraction();
        }
    }

    public void test_javax_media_rtp_RTPControl() throws Exception
    {
        assertEquals(javax.media.rtp.RTPControl.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.RTPControl.class.isInterface());
        assertTrue(javax.media.Control.class
                .isAssignableFrom(javax.media.rtp.RTPControl.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.RTPControl.class.getMethod(
                        "getFormat", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPControl.class.getMethod(
                        "getFormat", new Class[] { int.class });
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPControl.class.getMethod(
                        "addFormat", new Class[] { javax.media.Format.class,
                                int.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPControl.class.getMethod(
                        "getReceptionStats", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.ReceptionStats.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPControl.class.getMethod(
                        "getGlobalStats", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.GlobalReceptionStats.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPControl.class.getMethod(
                        "getFormatList", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPControl.class.getMethod(
                        "getControlComponent", new Class[] {});
                assertEquals(m.getReturnType(), java.awt.Component.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.RTPControl o = null;
            o.getFormat();
            o.getFormat(0);
            o.addFormat((javax.media.Format) null, 0);
            o.getReceptionStats();
            o.getGlobalStats();
            o.getFormatList();
            o.getControlComponent();
        }
    }

    public void test_javax_media_rtp_RTPStream() throws Exception
    {
        assertEquals(javax.media.rtp.RTPStream.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.RTPStream.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.RTPStream.class.getMethod(
                        "getSSRC", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPStream.class.getMethod(
                        "getDataSource", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.DataSource.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPStream.class.getMethod(
                        "getParticipant", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.Participant.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.RTPStream.class.getMethod(
                        "getSenderReport", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.rtcp.SenderReport.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.RTPStream o = null;
            o.getSSRC();
            o.getDataSource();
            o.getParticipant();
            o.getSenderReport();
        }
    }

    public void test_javax_media_rtp_SendStream() throws Exception
    {
        assertEquals(javax.media.rtp.SendStream.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.SendStream.class.isInterface());
        assertTrue(javax.media.rtp.RTPStream.class
                .isAssignableFrom(javax.media.rtp.SendStream.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.SendStream.class.getMethod(
                        "start", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SendStream.class.getMethod(
                        "stop", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SendStream.class.getMethod(
                        "close", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SendStream.class.getMethod(
                        "setBitRate", new Class[] { int.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SendStream.class
                        .getMethod(
                                "setSourceDescription",
                                new Class[] { javax.media.rtp.rtcp.SourceDescription[].class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SendStream.class.getMethod(
                        "getSourceTransmissionStats", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.TransmissionStats.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SendStream.class.getMethod(
                        "getSSRC", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SendStream.class.getMethod(
                        "getDataSource", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.protocol.DataSource.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SendStream.class.getMethod(
                        "getParticipant", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.Participant.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SendStream.class.getMethod(
                        "getSenderReport", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.rtcp.SenderReport.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.SendStream o = null;
            o.start();
            o.stop();
            o.close();
            o.setBitRate(0);
            o.setSourceDescription((javax.media.rtp.rtcp.SourceDescription[]) null);
            o.getSourceTransmissionStats();
            o.getSSRC();
            o.getDataSource();
            o.getParticipant();
            o.getSenderReport();
        }
    }

    public void test_javax_media_rtp_SendStreamListener() throws Exception
    {
        assertEquals(javax.media.rtp.SendStreamListener.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.SendStreamListener.class.isInterface());
        assertTrue(java.util.EventListener.class
                .isAssignableFrom(javax.media.rtp.SendStreamListener.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.SendStreamListener.class
                        .getMethod(
                                "update",
                                new Class[] { javax.media.rtp.event.SendStreamEvent.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.SendStreamListener o = null;
            o.update((javax.media.rtp.event.SendStreamEvent) null);
        }
    }

    public void test_javax_media_rtp_SessionListener() throws Exception
    {
        assertEquals(javax.media.rtp.SessionListener.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.SessionListener.class.isInterface());
        assertTrue(java.util.EventListener.class
                .isAssignableFrom(javax.media.rtp.SessionListener.class));
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.SessionListener.class
                        .getMethod(
                                "update",
                                new Class[] { javax.media.rtp.event.SessionEvent.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.SessionListener o = null;
            o.update((javax.media.rtp.event.SessionEvent) null);
        }
    }

    public void test_javax_media_rtp_SessionManager() throws Exception
    {
        assertEquals(javax.media.rtp.SessionManager.class.getModifiers(), 1537);
        assertTrue(javax.media.rtp.SessionManager.class.isInterface());
        assertTrue(javax.media.Controls.class
                .isAssignableFrom(javax.media.rtp.SessionManager.class));
        // Static fields:
        assertTrue(javax.media.rtp.SessionManager.SSRC_UNSPEC == 0L);

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("addFormat", new Class[] {
                                javax.media.Format.class, int.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("initSession", new Class[] {
                                javax.media.rtp.SessionAddress.class,
                                long.class,
                                javax.media.rtp.rtcp.SourceDescription[].class,
                                double.class, double.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("initSession", new Class[] {
                                javax.media.rtp.SessionAddress.class,
                                javax.media.rtp.rtcp.SourceDescription[].class,
                                double.class, double.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("startSession",
                                new Class[] {
                                        javax.media.rtp.SessionAddress.class,
                                        int.class,
                                        javax.media.rtp.EncryptionInfo.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("startSession", new Class[] {
                                javax.media.rtp.SessionAddress.class,
                                javax.media.rtp.SessionAddress.class,
                                javax.media.rtp.SessionAddress.class,
                                javax.media.rtp.EncryptionInfo.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("startSession", new Class[] { int.class,
                                javax.media.rtp.EncryptionInfo.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "addSessionListener",
                                new Class[] { javax.media.rtp.SessionListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "addRemoteListener",
                                new Class[] { javax.media.rtp.RemoteListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "addReceiveStreamListener",
                                new Class[] { javax.media.rtp.ReceiveStreamListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "addSendStreamListener",
                                new Class[] { javax.media.rtp.SendStreamListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "removeSessionListener",
                                new Class[] { javax.media.rtp.SessionListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "removeRemoteListener",
                                new Class[] { javax.media.rtp.RemoteListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "removeReceiveStreamListener",
                                new Class[] { javax.media.rtp.ReceiveStreamListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "removeSendStreamListener",
                                new Class[] { javax.media.rtp.SendStreamListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getDefaultSSRC", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getRemoteParticipants", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getActiveParticipants", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getPassiveParticipants", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getLocalParticipant", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.LocalParticipant.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getAllParticipants", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getReceiveStreams", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getSendStreams", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getStream", new Class[] { long.class });
                assertEquals(m.getReturnType(), javax.media.rtp.RTPStream.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getMulticastScope", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("setMulticastScope",
                                new Class[] { int.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("closeSession",
                                new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("generateCNAME", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.String.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("generateSSRC", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getSessionAddress", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.SessionAddress.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getLocalSessionAddress", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.SessionAddress.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getGlobalReceptionStats", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.GlobalReceptionStats.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getGlobalTransmissionStats", new Class[] {});
                assertEquals(m.getReturnType(),
                        javax.media.rtp.GlobalTransmissionStats.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("createSendStream", new Class[] { int.class,
                                javax.media.protocol.DataSource.class,
                                int.class });
                assertEquals(m.getReturnType(),
                        javax.media.rtp.SendStream.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("createSendStream", new Class[] {
                                javax.media.protocol.DataSource.class,
                                int.class });
                assertEquals(m.getReturnType(),
                        javax.media.rtp.SendStream.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "addPeer",
                                new Class[] { javax.media.rtp.SessionAddress.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod(
                                "removePeer",
                                new Class[] { javax.media.rtp.SessionAddress.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("removeAllPeers", new Class[] {});
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getPeers", new Class[] {});
                assertEquals(m.getReturnType(), java.util.Vector.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getControls", new Class[] {});
                assertEquals(m.getReturnType(), java.lang.Object[].class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.SessionManager.class
                        .getMethod("getControl",
                                new Class[] { java.lang.String.class });
                assertEquals(m.getReturnType(), java.lang.Object.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.rtp.SessionManager.class
                        .getField("SSRC_UNSPEC");
                assertEquals(f.getType(), long.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.SessionManager o = null;
            o.addFormat((javax.media.Format) null, 0);
            o.initSession((javax.media.rtp.SessionAddress) null, 0L,
                    (javax.media.rtp.rtcp.SourceDescription[]) null, 0.0, 0.0);
            o.initSession((javax.media.rtp.SessionAddress) null,
                    (javax.media.rtp.rtcp.SourceDescription[]) null, 0.0, 0.0);
            o.startSession((javax.media.rtp.SessionAddress) null, 0,
                    (javax.media.rtp.EncryptionInfo) null);
            o.startSession((javax.media.rtp.SessionAddress) null,
                    (javax.media.rtp.SessionAddress) null,
                    (javax.media.rtp.SessionAddress) null,
                    (javax.media.rtp.EncryptionInfo) null);
            o.startSession(0, (javax.media.rtp.EncryptionInfo) null);
            o.addSessionListener((javax.media.rtp.SessionListener) null);
            o.addRemoteListener((javax.media.rtp.RemoteListener) null);
            o.addReceiveStreamListener((javax.media.rtp.ReceiveStreamListener) null);
            o.addSendStreamListener((javax.media.rtp.SendStreamListener) null);
            o.removeSessionListener((javax.media.rtp.SessionListener) null);
            o.removeRemoteListener((javax.media.rtp.RemoteListener) null);
            o.removeReceiveStreamListener((javax.media.rtp.ReceiveStreamListener) null);
            o.removeSendStreamListener((javax.media.rtp.SendStreamListener) null);
            o.getDefaultSSRC();
            o.getRemoteParticipants();
            o.getActiveParticipants();
            o.getPassiveParticipants();
            o.getLocalParticipant();
            o.getAllParticipants();
            o.getReceiveStreams();
            o.getSendStreams();
            o.getStream(0L);
            o.getMulticastScope();
            o.setMulticastScope(0);
            o.closeSession((java.lang.String) null);
            o.generateCNAME();
            o.generateSSRC();
            o.getSessionAddress();
            o.getLocalSessionAddress();
            o.getGlobalReceptionStats();
            o.getGlobalTransmissionStats();
            o.createSendStream(0, (javax.media.protocol.DataSource) null, 0);
            o.createSendStream((javax.media.protocol.DataSource) null, 0);
            o.addPeer((javax.media.rtp.SessionAddress) null);
            o.removePeer((javax.media.rtp.SessionAddress) null);
            o.removeAllPeers();
            o.getPeers();
            o.getControls();
            o.getControl((java.lang.String) null);
        }
    }

    public void test_javax_media_rtp_TransmissionStats() throws Exception
    {
        assertEquals(javax.media.rtp.TransmissionStats.class.getModifiers(),
                1537);
        assertTrue(javax.media.rtp.TransmissionStats.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.rtp.TransmissionStats.class
                        .getMethod("getRTCPSent", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.TransmissionStats.class
                        .getMethod("getPDUTransmitted", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.rtp.TransmissionStats.class
                        .getMethod("getBytesTransmitted", new Class[] {});
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.rtp.TransmissionStats o = null;
            o.getRTCPSent();
            o.getPDUTransmitted();
            o.getBytesTransmitted();
        }
    }

    public void test_javax_media_TimeBase() throws Exception
    {
        assertEquals(javax.media.TimeBase.class.getModifiers(), 1537);
        assertTrue(javax.media.TimeBase.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.TimeBase.class.getMethod(
                        "getTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.TimeBase.class.getMethod(
                        "getNanoseconds", new Class[] {});
                assertEquals(m.getReturnType(), long.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.TimeBase o = null;
            o.getTime();
            o.getNanoseconds();
        }
    }

    public void test_javax_media_Track() throws Exception
    {
        assertEquals(javax.media.Track.class.getModifiers(), 1537);
        assertTrue(javax.media.Track.class.isInterface());
        assertTrue(javax.media.Duration.class
                .isAssignableFrom(javax.media.Track.class));
        // Static fields:
        // TODO: test TIME_UNKNOWN of type javax.media.Time
        assertTrue(javax.media.Track.FRAME_UNKNOWN == 2147483647);
        // TODO: test DURATION_UNBOUNDED of type javax.media.Time
        // TODO: test DURATION_UNKNOWN of type javax.media.Time

        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.Track.class.getMethod("getFormat",
                        new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Format.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Track.class.getMethod("isEnabled",
                        new Class[] {});
                assertEquals(m.getReturnType(), boolean.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Track.class.getMethod(
                        "setEnabled", new Class[] { boolean.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Track.class.getMethod(
                        "mapFrameToTime", new Class[] { int.class });
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Track.class.getMethod(
                        "mapTimeToFrame",
                        new Class[] { javax.media.Time.class });
                assertEquals(m.getReturnType(), int.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Track.class.getMethod(
                        "getStartTime", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Track.class.getMethod("readFrame",
                        new Class[] { javax.media.Buffer.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Track.class.getMethod(
                        "setTrackListener",
                        new Class[] { javax.media.TrackListener.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
            {
                final Method m = javax.media.Track.class.getMethod(
                        "getDuration", new Class[] {});
                assertEquals(m.getReturnType(), javax.media.Time.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
            {
                final Field f = javax.media.Track.class
                        .getField("TIME_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Track.class
                        .getField("FRAME_UNKNOWN");
                assertEquals(f.getType(), int.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Track.class
                        .getField("DURATION_UNBOUNDED");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
            {
                final Field f = javax.media.Track.class
                        .getField("DURATION_UNKNOWN");
                assertEquals(f.getType(), javax.media.Time.class);
                assertEquals(f.getModifiers(), 25);
            }
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.Track o = null;
            o.getFormat();
            o.isEnabled();
            o.setEnabled(false);
            o.mapFrameToTime(0);
            o.mapTimeToFrame((javax.media.Time) null);
            o.getStartTime();
            o.readFrame((javax.media.Buffer) null);
            o.setTrackListener((javax.media.TrackListener) null);
            o.getDuration();
        }
    }

    public void test_javax_media_TrackListener() throws Exception
    {
        assertEquals(javax.media.TrackListener.class.getModifiers(), 1537);
        assertTrue(javax.media.TrackListener.class.isInterface());
        // Methods (reflection):
        if (true)
        {
            {
                final Method m = javax.media.TrackListener.class.getMethod(
                        "readHasBlocked",
                        new Class[] { javax.media.Track.class });
                assertEquals(m.getReturnType(), void.class);
                assertEquals(m.getModifiers(), 1025);
            }
        }

        // Constructors (reflection):
        if (true)
        {
        }

        // Fields (reflection):
        if (true)
        {
        }

        // Methods (compilation):
        if (false)
        {
            javax.media.TrackListener o = null;
            o.readHasBlocked((javax.media.Track) null);
        }
    }

}
