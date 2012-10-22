package net.sf.fmj.test.compat.formats;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class FormatMatchesIntersectsTest extends TestCase
{
    public void testBig()
    {
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new AudioFormat(
                        "LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new AudioFormat(
                        "ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new AudioFormat(
                        "ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00,
                        0xff, 1, -1, 0, -1)), true);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00,
                        0xff, 1, -1, 0, -1)), true);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new VideoFormat(
                        "jpeg", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "jpeg", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);

        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "h263", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "h263/rtp", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, -1)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new RGBFormat(
                null, -1, Format.byteArray, -1.0f, 24, 0xffffffff, 0xffffffff,
                0xffffffff, -1, -1, -1, -1)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new RGBFormat(
                null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff,
                1, -1, -1, -1)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new RGBFormat(
                null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000,
                1, -1, -1, -1)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "msadpcm", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ULAW", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "alaw", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "dvi/rtp", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "g723", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "g723/rtp", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "gsm", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "gsm/rtp", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "gsm/ms", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ima4", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ima4/ms", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ULAW", -1.0, 8, 1, -1, -1, 8, -1.0, Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ULAW/rtp", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 16000.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 22050.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 24000.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 32000.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 44100.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 48000.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpegaudio/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "gsm", 8000.0, -1, 1, -1, -1, 264, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "g723", 8000.0, -1, 1, -1, -1, 192, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "jpeg", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "jpeg/rtp", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "mpeg", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "mpeg/rtp", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).intersects(new VideoFormat(
                        "cvid", null, -1, Format.byteArray, -1.0f)),
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f));
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new VideoFormat("jpeg", null, -1,
                Format.byteArray, -1.0f)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(new java.awt.Dimension(320, 200),
                64000, Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000,
                0, 0, 0, -1)), true);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 16000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 22050.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 24000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 32000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 44100.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 48000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new VideoFormat("cvid", null, -1,
                Format.byteArray, -1.0f)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new VideoFormat("h263", null, -1,
                Format.byteArray, -1.0f)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new VideoFormat("h263/rtp", null, -1,
                Format.byteArray, -1.0f)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, -1)), true);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(null, -1, Format.byteArray, -1.0f,
                24, 0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, -1, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, -1, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("msadpcm", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("alaw", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("dvi/rtp", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("g723", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("g723/rtp", -1.0, -1, -1, -1,
                -1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("gsm", -1.0, -1, -1, -1, -1, -1,
                -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("gsm/rtp", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("gsm/ms", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("ima4", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("ima4/ms", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1,
                -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1,
                -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1,
                -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1,
                -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("ULAW", -1.0, 8, 1, -1, -1, 8,
                -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("ULAW/rtp", -1.0, -1, -1, -1,
                -1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpeglayer3", 16000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpeglayer3", 22050.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpeglayer3", 24000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpeglayer3", 32000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpeglayer3", 44100.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpeglayer3", 48000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 16000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 22050.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 24000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 32000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 44100.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio", 48000.0, -1, -1,
                -1, 1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("mpegaudio/rtp", -1.0, -1, -1,
                -1, -1, -1, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("gsm", 8000.0, -1, 1, -1, -1,
                264, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new AudioFormat("g723", 8000.0, -1, 1, -1, -1,
                192, -1.0, Format.byteArray)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new VideoFormat("jpeg", null, -1,
                Format.byteArray, -1.0f)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new VideoFormat("jpeg/rtp", null, -1,
                Format.byteArray, -1.0f)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new VideoFormat("mpeg", null, -1,
                Format.byteArray, -1.0f)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new VideoFormat("mpeg/rtp", null, -1,
                Format.byteArray, -1.0f)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, -1)), true);
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, -1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff, 0xff00,
                        0xff0000, 0, 0, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 1, 320, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        1, 320, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        1, 320, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 1, 320, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        1, 320, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        1, 320, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 2, 640, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, -1, 1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, 1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, 1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, 1, 1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 2, 640, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, -1, 0).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, 0));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, 0)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, 1, 0));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 2, 640, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, -1, 1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, 1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, 1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, 0, 1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 2, 640, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, -1, 0).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, 0));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, 0)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f,
                        2, 640, 0, 0));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 1, 320, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        1, 320, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        1, 320, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 1, 320, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        1, 320, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        1, 320, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 2, 640, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, -1, 1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, 1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, 1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, 1, 1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 2, 640, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, -1, 0).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, 0));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, 0)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, 1, 0));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 2, 640, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, -1, 1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, 1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, 1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, 0, 1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0,
                0x1f, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                0xffffffff, 2, 640, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, -1, 0).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, 0));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, 0)),
                new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                        Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f,
                        2, 640, 0, 0));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x1, 0x2, 0x3, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                0xffffffff, 3, 960, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, -1, -1).intersects(new RGBFormat(null, -1, null,
                        -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff, -1, -1,
                        1, -1)), new RGBFormat(
                        new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x1, 0x2, 0x3, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                0xffffffff, 3, 960, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, -1, -1).intersects(new RGBFormat(null, -1, null,
                        -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff, -1, -1,
                        0, -1)), new RGBFormat(
                        new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x3, 0x2, 0x1, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                0xffffffff, 3, 960, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, -1, -1).intersects(new RGBFormat(null, -1, null,
                        -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff, -1, -1,
                        1, -1)), new RGBFormat(
                        new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x3, 0x2, 0x1, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                0xffffffff, 3, 960, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, -1, -1).intersects(new RGBFormat(null, -1, null,
                        -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff, -1, -1,
                        0, -1)), new RGBFormat(
                        new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00,
                0xff, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0xff0000, 0xff00, 0xff, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 1, 320, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xff0000, 0xff00, 0xff, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00,
                        0xff, 1, 320, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f,
                -1, 0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00,
                        0xff, 1, 320, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00,
                0xff, -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null,
                -1.0f, -1, 0xff0000, 0xff00, 0xff, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 1, 320, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xff0000, 0xff00, 0xff, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00,
                        0xff, 1, 320, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f,
                -1, 0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00,
                        0xff, 1, 320, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00,
                0xff0000, -1, -1, -1, -1).clone(), new RGBFormat(null, -1,
                null, -1.0f, -1, 0xff, 0xff00, 0xff0000, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 1, 320, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xff, 0xff00, 0xff0000, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff, 0xff00,
                        0xff0000, 1, 320, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                320, -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f,
                -1, 0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff, 0xff00,
                        0xff0000, 1, 320, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00,
                0xff0000, -1, -1, -1, -1).clone(), new RGBFormat(null, -1,
                null, -1.0f, -1, 0xff, 0xff00, 0xff0000, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 1, 320, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0xff, 0xff00, 0xff0000, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff, 0xff00,
                        0xff0000, 1, 320, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                320, -1, -1).intersects(new RGBFormat(null, -1, null, -1.0f,
                -1, 0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff, 0xff00,
                        0xff0000, 1, 320, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x1, 0x2, 0x3, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 4, 1280, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4,
                        1280, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280, -1,
                -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4,
                        1280, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x1, 0x2, 0x3, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 4, 1280, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4,
                        1280, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280, -1,
                -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4,
                        1280, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x3, 0x2, 0x1, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 4, 1280, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4,
                        1280, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280, -1,
                -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4,
                        1280, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x3, 0x2, 0x1, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 4, 1280, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4,
                        1280, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280, -1,
                -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4,
                        1280, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x2, 0x3, 0x4, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 4, 1280, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x2, 0x3, 0x4, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4,
                        1280, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280, -1,
                -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4,
                        1280, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x2, 0x3, 0x4, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 4, 1280, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x2, 0x3, 0x4, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4,
                        1280, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280, -1,
                -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4,
                        1280, 0, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x4, 0x3, 0x2, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 4, 1280, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x4, 0x3, 0x2, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4,
                        1280, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 1, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280, -1,
                -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4,
                        1280, 1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2,
                -1, -1, -1, -1).clone(), new RGBFormat(null, -1, null, -1.0f,
                -1, 0x4, 0x3, 0x2, -1, -1, -1, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                0xffffffff, 4, 1280, -1, -1).intersects(new RGBFormat(null, -1,
                null, -1.0f, -1, 0x4, 0x3, 0x2, -1, -1, -1, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4,
                        1280, -1, -1));
        assertEquals(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                0xffffffff, 0xffffffff, -1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280, -1,
                -1).intersects(new RGBFormat(null, -1, null, -1.0f, -1,
                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                        Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4,
                        1280, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                1, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                1, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                1, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                1, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 1, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 1, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 0, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 0, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 1, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 1, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 0, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 0, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000, 1,
                        -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 1, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 1, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 0, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), true);
        assertEquals(new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                0xff0000, 0xff00, 0xff, 1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff,
                1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 0, -1).intersects(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00,
                        0xff, 1, 320, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                1, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                1, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1, 320,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                1, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2, 640,
                0, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                1, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                1, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1, 320,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                1, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, 1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2, 640,
                0, 0).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 1, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 1, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 0, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3,
                        960, 0, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 1, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 1, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 0, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff, 1,
                        -1, 0, -1)), false);
        assertEquals(
                new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                        Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3,
                        960, 0, -1).matches(new RGBFormat(null, -1,
                        Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000, 1,
                        -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 1, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 1, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)), false);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 0, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), true);
        assertEquals(new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                0xff0000, 0xff00, 0xff, 1, -1, 0, -1).clone(), new RGBFormat(
                null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff,
                1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 0, -1).intersects(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00,
                        0xff, 1, 320, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 0, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), true);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                320, 0, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0, 0,
                0, -1).matches(new RGBFormat(new java.awt.Dimension(320, 200),
                64000, Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000,
                0, 0, 0, -1)), true);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new AudioFormat(
                        "LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray)), true);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new AudioFormat(
                        "ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                        Format.byteArray)), true);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00,
                        0xff, 1, -1, 0, -1)), false);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                        0xff0000, 1, -1, 0, -1)), false);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00,
                        0xff, 1, -1, 0, -1)), false);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                        0xff0000, 1, -1, 0, -1)), false);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new VideoFormat(
                        "jpeg", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", -1.0, -1,
                -1, -1, -1, -1, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                Format.byteArray).intersects(new AudioFormat("LINEAR", -1.0,
                -1, -1, -1, -1, -1, -1.0, Format.byteArray)), new AudioFormat(
                "LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0, Format.byteArray));
        assertEquals(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", -1.0, -1,
                -1, -1, -1, -1, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                Format.byteArray).intersects(new AudioFormat("LINEAR", -1.0,
                -1, -1, -1, -1, -1, -1.0, Format.byteArray)), new AudioFormat(
                "LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0, Format.byteArray));
        assertEquals(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), false);
        assertEquals(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), false);

    }

    public void testBig2()
    {
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new AudioFormat(
                        "LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new AudioFormat(
                        "ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new AudioFormat(
                        "ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00,
                        0xff, 1, -1, 0, -1)), true);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00,
                        0xff, 1, -1, 0, -1)), true);
        assertEquals(
                new VideoFormat(null, null, -1, null, -1.0f).matches(new VideoFormat(
                        "jpeg", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "jpeg", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "h263", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "h263/rtp", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new RGBFormat(
                null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff, 0xffffffff,
                -1, -1, -1, -1)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new RGBFormat(
                null, -1, Format.byteArray, -1.0f, 24, 0xffffffff, 0xffffffff,
                0xffffffff, -1, -1, -1, -1)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new RGBFormat(
                null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00, 0xff,
                1, -1, -1, -1)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new RGBFormat(
                null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000,
                1, -1, -1, -1)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "msadpcm", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ULAW", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "alaw", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "dvi/rtp", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "g723", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "g723/rtp", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "gsm", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "gsm/rtp", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "gsm/ms", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ima4", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ima4/ms", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ULAW", -1.0, 8, 1, -1, -1, 8, -1.0, Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "ULAW/rtp", -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 16000.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 22050.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 24000.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 32000.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 44100.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpeglayer3", 48000.0, -1, -1, -1, 1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                        "mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "mpegaudio/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "gsm", 8000.0, -1, 1, -1, -1, 264, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new AudioFormat(
                "g723", 8000.0, -1, 1, -1, -1, 192, -1.0, Format.byteArray)),
                false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "jpeg", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "jpeg/rtp", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "mpeg", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "mpeg/rtp", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f).intersects(new VideoFormat(
                        "cvid", null, -1, Format.byteArray, -1.0f)),
                new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                        16222, Format.byteArray, 1.3414634f));
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                16222, Format.byteArray, 1.3414634f).matches(new VideoFormat(
                "cvid", null, -1, Format.byteArray, -1.0f)), true);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new AudioFormat(
                        "LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray)), true);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new AudioFormat(
                        "ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                        Format.byteArray)), true);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00,
                        0xff, 1, -1, 0, -1)), false);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                        0xff0000, 1, -1, 0, -1)), false);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff0000, 0xff00,
                        0xff, 1, -1, 0, -1)), false);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new RGBFormat(
                        null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                        0xff0000, 1, -1, 0, -1)), false);
        assertEquals(
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null).matches(new VideoFormat(
                        "jpeg", null, -1, Format.byteArray, -1.0f)), false);
        assertEquals(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", -1.0, -1,
                -1, -1, -1, -1, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                Format.byteArray).intersects(new AudioFormat("LINEAR", -1.0,
                -1, -1, -1, -1, -1, -1.0, Format.byteArray)), new AudioFormat(
                "LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0, Format.byteArray));
        assertEquals(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", -1.0, -1,
                -1, -1, -1, -1, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                Format.byteArray).intersects(new AudioFormat("LINEAR", -1.0,
                -1, -1, -1, -1, -1, -1.0, Format.byteArray)), new AudioFormat(
                "LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0, Format.byteArray));
        assertEquals(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), false);
        assertEquals(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), true);
        assertEquals(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray).matches(new AudioFormat("LINEAR", 22050.0, 8,
                1, 1, 0, 8, -1.0, Format.byteArray)), false);

    }
}
