package net.sf.fmj.test.compat.formats;

import java.awt.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

import com.sun.media.format.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class FormatTest extends TestCase
{
    private static class CopiableFormat extends Format
    {
        public CopiableFormat(String arg0)
        {
            super(arg0);
        }

        public CopiableFormat(String arg0, Class arg1)
        {
            super(arg0, arg1);
        }

        public void doCopy(Format f)
        {
            copy(f);
        }
    }

    private static class CopiableVideoFormat extends VideoFormat
    {
        public CopiableVideoFormat(String arg0)
        {
            super(arg0);
        }

        public CopiableVideoFormat(String arg0, Dimension arg1, int arg2,
                Class arg3, float arg4)
        {
            super(arg0, arg1, arg2, arg3, arg4);
        }

        public void doCopy(Format f)
        {
            copy(f);
        }
    }

    class MyFormat extends Format
    {
        public MyFormat(String arg0)
        {
            super(arg0);
        }

        public MyFormat(String arg0, Class arg1)
        {
            super(arg0, arg1);
        }

        public Class getClazz()
        {
            return this.clz;
        }

    }

    private static long calc(AudioFormat f1, long length)
    {
        return 1000L * (long) ((length * 8 * 1000000.0 / (f1.getFrameRate() * (f1
                .getFrameSizeInBits()))));
    }

    private static void compare(AudioFormat f1, long length)
    {
        System.out.println(f1);
        System.out.println("length: " + length);
        long dur = f1.computeDuration(length);
        long calc = calc(f1, length);
        System.out.println("" + dur);
        System.out.println("" + calc);
        System.out.println("" + (double) dur / (double) calc);
        System.out.println();

    }

    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    private void assertNotEquals(Object a, Object b)
    {
        if (a == null && b == null)
            assertFalse(true);
        else if (a == null || b == null)
            return;

        assertFalse(a.equals(b));
    }

    public void testAudioFormat_computeDuration()
    {
        if (true)
            return;

        // unspecified:
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3);
            assertEquals(f1.computeDuration(0), -1L);
            assertEquals(f1.computeDuration(1), -1L);
            assertEquals(f1.computeDuration(1000), -1L);

        }

        final AudioFormat f0 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2,
                3, 4, 5, 6.0, Format.byteArray);

        {
            assertEquals(f0.computeDuration(0), 0L);
            assertEquals(f0.computeDuration(1), 266666000L);
            assertEquals(f0.computeDuration(1000), 266666666000L);

        }

        // public AudioFormat(String encoding, double sampleRate,
        // int sampleSizeInBits, int channels, int endian, int signed,
        // int frameSizeInBits, double frameRate, Class dataType)

        // sampleRate - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 4.0,
                    1, 2, 3, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // sampleRate - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3,
                    Format.NOT_SPECIFIED, 1, 2, 3, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // sampleSizeInBits - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    2, 2, 3, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }
        // sampleSizeInBits - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    16, 2, 3, 4, 5, 6.0, Format.shortArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }
        // sampleSizeInBits - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    32, 2, 3, 4, 5, 6.0, Format.intArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }
        // sampleSizeInBits - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    Format.NOT_SPECIFIED, 2, 3, 4, 5, 6.0, Format.intArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // channels - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    8, 3, 3, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }
        // channels - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    8, 1, 3, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }
        // channels - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    8, Format.NOT_SPECIFIED, 3, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // endian - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 4, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }
        // endian - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 0, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }
        // endian - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 1, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // endian - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, Format.NOT_SPECIFIED, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // signed: - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 3, 5, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // signed: - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 3, 0, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // signed: - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 3, 1, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // signed: - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 3, Format.NOT_SPECIFIED, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // dataType - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 3, 4, 5, 6.0, Format.intArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        // dataType - DOES NOT AFFECT.
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 3, 4, 5, 6.0, Format.shortArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));

        }

        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 4.0,
                    1, 2, 3, 4, 5, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), f0.computeDuration(0));
            assertEquals(f1.computeDuration(1), f0.computeDuration(1));
            assertEquals(f1.computeDuration(1000), f0.computeDuration(1000));
            compare(f1, 0);
            compare(f1, 1);
            compare(f1, 1000);

        }

        // assertEquals(f0.computeDuration(0), 0L);
        // assertEquals(f0.computeDuration(1), 266,666,000L);
        // assertEquals(f0.computeDuration(1000), 266666666000L);
        // frameSizeInBits: AFFECTS - inversely
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 3, 4, 6, 6.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), 0L);
            assertEquals(f1.computeDuration(1), 222222000L);
            assertEquals(f1.computeDuration(1000), 222222166000L);
            compare(f1, 0);
            compare(f1, 1);
            compare(f1, 1000);

        }

        // frameRate: AFFECTS - inversely
        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 3, 4, 5, 7.0, Format.byteArray);
            assertEquals(f1.computeDuration(0), 0L);
            assertEquals(f1.computeDuration(1), 228571000L);
            assertEquals(f1.computeDuration(1000), 228571428000L);
            compare(f1, 0);
            compare(f1, 1);
            compare(f1, 1000);

        }

    }

    public void testConstants()
    {
        assertEquals(new H261Format().getEncoding(), "h261");
        assertEquals(new H261Format().getStillImageTransmission(),
                Format.NOT_SPECIFIED);
        assertEquals(new H261Format().getFrameRate(), Format.NOT_SPECIFIED);
        assertEquals(new H261Format().getMaxDataLength(), Format.NOT_SPECIFIED);
        assertEquals(new H261Format().getSize(), null);
        assertEquals(new H261Format().getDataType(), Format.byteArray);

        assertEquals(new H263Format().getEncoding(), "h263");
        assertEquals(new H263Format().getDataType(), Format.byteArray);

        final IndexedColorFormat indexedColorFormat = new IndexedColorFormat(
                new java.awt.Dimension(0, 0), 0, null, 0.f, 0, 0, null, null,
                null);
        assertEquals(indexedColorFormat.getEncoding(), "irgb");
        assertEquals(indexedColorFormat.getDataType(), null);

        assertEquals(new JPEGFormat().getEncoding(), "jpeg");
        assertEquals(new JPEGFormat().getDecimation(), Format.NOT_SPECIFIED);
        assertEquals(new JPEGFormat().getQFactor(), Format.NOT_SPECIFIED);
        assertEquals(new JPEGFormat().getDataType(), Format.byteArray);

        assertEquals(new RGBFormat().getEncoding(), "rgb");
        assertEquals(new RGBFormat().getDataType(), null);

        assertEquals(new YUVFormat().getEncoding(), "yuv");
        assertEquals(new YUVFormat().getOffsetV(), -1);
        assertTrue(new YUVFormat().getSize() == null);
        assertEquals(new YUVFormat().getDataType(), Format.byteArray);

        assertEquals(new AudioFormat(AudioFormat.DOLBYAC3).getDataType(),
                Format.byteArray);

    }

    public void testCopy()
    {
        {
            final Format f1 = new Format("abc", Format.shortArray);
            final CopiableFormat f2 = new CopiableFormat("xyz",
                    Format.byteArray);
            f2.doCopy(f1);
            assertEquals(f1.getEncoding(), "abc");
            assertEquals(f2.getEncoding(), "xyz");
            assertEquals(f1.getDataType(), f2.getDataType());
        }

        {
            final Format f1 = new Format("abc", null);
            final CopiableFormat f2 = new CopiableFormat("xyz",
                    Format.byteArray);
            f2.doCopy(f1);
            assertEquals(f1.getEncoding(), "abc");
            assertEquals(f2.getEncoding(), "xyz");
            assertEquals(f1.getDataType(), f2.getDataType());
        }

        {
            final Format f1 = new Format("abc", null);
            final CopiableFormat f2 = new CopiableFormat(null, Format.byteArray);
            f2.doCopy(f1);
            assertEquals(f1.getEncoding(), "abc");
            assertEquals(f2.getEncoding(), null);
            assertEquals(f1.getDataType(), f2.getDataType());
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final CopiableVideoFormat f2 = new CopiableVideoFormat(
                    VideoFormat.CINEPAK, new Dimension(1, 0), 1001,
                    Format.shortArray, 2.f);
            f2.doCopy(f1);
            assertEquals(f1.getEncoding(), "mpeg");
            assertEquals(f2.getEncoding(), "cvid");
            assertEquals(f1.getDataType(), f2.getDataType());
            assertEquals(f1.getFrameRate(), f2.getFrameRate());
            assertEquals(f1.getMaxDataLength(), f2.getMaxDataLength());
            assertEquals(f1.getSize(), f2.getSize());
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), Format.NOT_SPECIFIED,
                    Format.byteArray, 1.f);
            final CopiableVideoFormat f2 = new CopiableVideoFormat(
                    VideoFormat.CINEPAK, null, 1001, Format.shortArray, 2.f);
            f2.doCopy(f1);
            assertEquals(f1.getEncoding(), "mpeg");
            assertEquals(f2.getEncoding(), "cvid");
            assertEquals(f1.getDataType(), f2.getDataType());
            assertEquals(f1.getFrameRate(), f2.getFrameRate());
            assertEquals(f1.getMaxDataLength(), f2.getMaxDataLength());
            assertEquals(f1.getSize(), f2.getSize());
        }

        try
        {
            final Format f1 = new Format(VideoFormat.MPEG, Format.byteArray);
            final CopiableVideoFormat f2 = new CopiableVideoFormat(
                    VideoFormat.CINEPAK, null, 1001, Format.shortArray, 2.f);
            f2.doCopy(f1);
            assertTrue(false);
        } catch (ClassCastException e)
        {
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), Format.NOT_SPECIFIED,
                    Format.byteArray, 1.f);
            final CopiableFormat f2 = new CopiableFormat(VideoFormat.CINEPAK,
                    Format.shortArray);
            f2.doCopy(f1);
            assertEquals(f1.getEncoding(), "mpeg");
            assertEquals(f2.getEncoding(), "cvid");
            assertEquals(f1.getDataType(), f2.getDataType());

        }

    }

    public void testEqualsMatches()
    {
        // Format:
        {
            final Format f1 = new Format("abc");
            final Format f2 = new Format("abc");
            assertTrue(f1.getEncoding().equals(f2.getEncoding()));
            assertTrue(f1.equals(f2));
            assertTrue(f1.matches(f2));

        }

        {
            final Format f1 = new Format("abc", Format.byteArray);
            final Format f2 = new Format("abc", Format.byteArray);
            assertTrue(f1.getEncoding().equals(f2.getEncoding()));
            assertTrue(f1.equals(f2));
            assertTrue(f1.matches(f2));

        }

        {
            final Format f1 = new Format("abc", Format.byteArray);
            final Format f2 = new Format("abc", null);
            assertFalse(f1.equals(f2));
            assertTrue(f1.matches(f2));

        }

        {
            final Format f1 = new Format("abc", Format.byteArray);
            final Format f2 = new Format("abc", Format.intArray);
            assertFalse(f1.equals(f2));
            assertFalse(f1.matches(f2));

        }

        {
            final Format f1 = new Format("abc", Format.byteArray);
            final Format f2 = new Format(null, Format.byteArray);
            assertFalse(f1.equals(f2));
            assertTrue(f1.matches(f2));

        }

        {
            final Format f1 = new Format("abc");
            final Format f2 = new VideoFormat("abc");
            assertFalse(f1.equals(f2));
            assertFalse(f2.equals(f1));
            assertTrue(f1.matches(f2));
            assertTrue(f2.matches(f2));

        }

        {
            final Format f1 = new Format(VideoFormat.MPEG);
            final Format f2 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.byteArray, 1.f);
            assertFalse(f1.equals(f2));
            assertFalse(f2.equals(f1));
            assertTrue(f1.matches(f2));
            assertTrue(f2.matches(f2));

        }
        {
            final Format f1 = new Format("abc", Format.byteArray);
            final Format f2 = f1.relax();
            assertTrue(f1.getEncoding().equals(f2.getEncoding()));
            assertTrue(f1.getDataType().equals(f2.getDataType()));

            assertTrue(f1.equals(f2));
            assertTrue(f1.matches(f2));

        }

        {
            final VideoFormat f1 = new VideoFormat(null);
            final VideoFormat f2 = new VideoFormat(null);
            assertTrue(f1.equals(f2));
            assertTrue(f1.matches(f2));

        }
        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG);
            final VideoFormat f2 = (VideoFormat) f1.clone();
            assertTrue(f1 != f2);
            assertTrue(f1.getEncoding().equals(f2.getEncoding()));
            assertTrue(f1.equals(f2));
            assertTrue(f1.matches(f2));

        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG);
            final VideoFormat f2 = new VideoFormat(VideoFormat.MPEG_RTP);
            assertFalse(f1.getEncoding().equals(f2.getEncoding()));
            assertFalse(f1.equals(f2));
            assertFalse(f1.matches(f2));

        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG);
            final VideoFormat f2 = (VideoFormat) f1.relax();
            assertEquals(f1.getEncoding(), f2.getEncoding());
            assertTrue(f1.equals(f2));
            assertTrue(f1.matches(f2));

        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = (VideoFormat) f1.relax();
            assertEquals(f1.getEncoding(), f2.getEncoding());
            assertEquals(null, f2.getSize());
            assertEquals(f1.getDataType(), f2.getDataType());
            assertEquals(Format.NOT_SPECIFIED, f2.getMaxDataLength());
            assertEquals(Format.NOT_SPECIFIED, f2.getFrameRate());
            assertFalse(f1.equals(f2));
            assertTrue(f1.matches(f2));

        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(1, 0), 1000, Format.byteArray, 1.f);
            assertFalse(f1.matches(f2));
            assertFalse(f2.matches(f1));
        }

        {
            final VideoFormat f1 = new VideoFormat(
                    VideoFormat.MPEG.toLowerCase(), new Dimension(0, 0), 1000,
                    Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(
                    VideoFormat.MPEG.toUpperCase(), new Dimension(0, 0), 1000,
                    Format.byteArray, 1.f);
            assertTrue(f1.matches(f2));
            assertTrue(f2.matches(f1));
            assertTrue(f1.equals(f2));
            assertTrue(f2.equals(f1));

        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(VideoFormat.MPEG, null,
                    1000, Format.byteArray, 1.f);
            assertTrue(f1.matches(f2));
            assertTrue(f2.matches(f1));
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1001, Format.byteArray, 1.f);
            assertTrue(f1.matches(f2));
            assertTrue(f2.matches(f1));
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), Format.NOT_SPECIFIED,
                    Format.byteArray, 1.f);
            assertTrue(f1.matches(f2));
            assertTrue(f2.matches(f1));
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(null, new Dimension(0, 0),
                    1000, Format.byteArray, 1.f);
            assertTrue(f1.matches(f2));
            assertTrue(f2.matches(f1));
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.intArray, 1.f);
            assertFalse(f1.matches(f2));
            assertFalse(f2.matches(f1));
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, null, 1.f);
            assertTrue(f1.matches(f2));
            assertTrue(f2.matches(f1));
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 2.f);
            assertFalse(f1.matches(f2));
            assertFalse(f2.matches(f1));
        }

        {
            final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray, 1.f);
            final VideoFormat f2 = new VideoFormat(VideoFormat.MPEG,
                    new Dimension(0, 0), 1000, Format.byteArray,
                    Format.NOT_SPECIFIED);
            assertTrue(f1.matches(f2));
            assertTrue(f2.matches(f1));
        }

    }

    public void testEqualsMatches_AudioFormat()
    {
        final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2,
                3, 4, 5, 6.0, Format.byteArray);

        // AudioFormat - equal and match:
        {
            final AudioFormat[] f2s = new AudioFormat[] {
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3, 4, 5,
                            6.0, Format.byteArray), (AudioFormat) f1.clone(),
                    (AudioFormat) f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                AudioFormat f2 = f2s[i];
                assertTrue(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertTrue(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

        // AudioFormat - not equal and not match:
        {
            final AudioFormat[] f2s = new AudioFormat[] {
                    new AudioFormat(AudioFormat.ALAW, 2.0, 1, 2, 3, 4, 5, 6.0,
                            Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 3.0, 1, 2, 3, 4, 5,
                            6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 11, 2, 3, 4, 5,
                            6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 12, 3, 4, 5,
                            6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 13, 4, 5,
                            6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3, 14, 5,
                            6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3, 4, 15,
                            6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3, 4, 5,
                            16.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3, 4, 5,
                            6.0, Format.intArray),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                AudioFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertFalse(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertFalse(f2.matches(f1));
            }
        }

        // AudioFormat - not equal but match:
        {
            final AudioFormat[] f2s = new AudioFormat[] {
                    new AudioFormat(null, 2.0, 1, 2, 3, 4, 5, 6.0,
                            Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, Format.NOT_SPECIFIED,
                            1, 2, 3, 4, 5, 6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                            Format.NOT_SPECIFIED, 2, 3, 4, 5, 6.0,
                            Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1,
                            Format.NOT_SPECIFIED, 3, 4, 5, 6.0,
                            Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2,
                            Format.NOT_SPECIFIED, 4, 5, 6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3,
                            Format.NOT_SPECIFIED, 5, 6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3, 4,
                            Format.NOT_SPECIFIED, 6.0, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3, 4, 5,
                            Format.NOT_SPECIFIED, Format.byteArray),
                    new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3, 4, 5,
                            6.0, null),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                AudioFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }
    }

    public void testEqualsMatches_Format()
    {
        final Format f1 = new Format("abc", Format.byteArray);

        // equal and match:
        {
            final Format[] f2s = new Format[] {
                    new Format("abc", Format.byteArray), (Format) f1.clone(),
                    f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                Format f2 = f2s[i];
                assertTrue(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertTrue(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

        // not equal and not match:
        {
            final Format[] f2s = new Format[] {
                    new Format("xyz", Format.byteArray),
                    new Format("abc", Format.shortArray),
                    new Format("abc", Format.intArray),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                Format f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertFalse(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertFalse(f2.matches(f1));
            }
        }

        // not equal but match:
        {
            final Format[] f2s = new Format[] {
                    new Format(null, Format.byteArray),
                    new Format("abc", null), };
            for (int i = 0; i < f2s.length; ++i)
            {
                Format f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }
    }

    public void testEqualsMatches_H261Format()
    {
        final H261Format f1 = new H261Format(new Dimension(1, 1), 2000,
                Format.byteArray, 3.f, 1);

        // H261Format - equal and match:
        {
            final H261Format[] f2s = new H261Format[] {
                    new H261Format(new Dimension(1, 1), 2000, Format.byteArray,
                            3.f, 1), (H261Format) f1.clone(),
                    (H261Format) f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                H261Format f2 = f2s[i];
                assertTrue(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertTrue(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

        // H261Format - not equal and not match:
        {
            final H261Format[] f2s = new H261Format[] {
                    new H261Format(new Dimension(1, 2), 2000, Format.byteArray,
                            3.f, 1),
                    new H261Format(new Dimension(1, 1), 2000, Format.intArray,
                            3.f, 1),
                    new H261Format(new Dimension(1, 1), 2000, Format.byteArray,
                            4.f, 1),
                    new H261Format(new Dimension(1, 1), 2000, Format.byteArray,
                            3.f, 11),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                H261Format f2 = f2s[i];

                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertFalse(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertFalse(f2.matches(f1));
            }
        }

        // H261Format - not equal but match:
        {
            final H261Format[] f2s = new H261Format[] {
                    new H261Format(new Dimension(1, 1), 3000, Format.byteArray,
                            3.f, 1),
                    new H261Format(null, 2000, Format.byteArray, 3.f, 1),
                    new H261Format(new Dimension(1, 1), Format.NOT_SPECIFIED,
                            Format.byteArray, 3.f, 1),
                    new H261Format(new Dimension(1, 1), 2000, null, 3.f, 1),
                    new H261Format(new Dimension(1, 1), 2000, Format.byteArray,
                            Format.NOT_SPECIFIED, 1),
                    new H261Format(new Dimension(1, 1), 2000, Format.byteArray,
                            3.f, Format.NOT_SPECIFIED),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                H261Format f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }
    }

    public void testEqualsMatches_H263Format()
    {
        final H263Format f1 = new H263Format(new Dimension(1, 1), 2000,
                Format.shortArray, 2.f, 1, 2, 3, 4, 5, 6);

        // equal and match:
        {
            final H263Format[] f2s = new H263Format[] {
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 2, 3, 4, 5, 6),
                    (H263Format) f1.clone(), (H263Format) f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                H263Format f2 = f2s[i];
                assertTrue(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertTrue(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

        // not equal and not match:
        {
            final H263Format[] f2s = new H263Format[] {
                    new H263Format(new Dimension(1, 2), 2000,
                            Format.shortArray, 2.f, 1, 2, 3, 4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 3.f, 1, 2, 3, 4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 11, 2, 3, 4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 12, 3, 4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 2, 13, 4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 2, 3, 14, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 2, 3, 4, 15, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 2, 3, 4, 5, 16),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                H263Format f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertFalse(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertFalse(f2.matches(f1));
            }
        }

        // not equal but match:
        {
            final H263Format[] f2s = new H263Format[] {
                    new H263Format(new Dimension(1, 1), 2001,
                            Format.shortArray, 2.f, 1, 2, 3, 4, 5, 6),
                    new H263Format(null, 2000, Format.shortArray, 2.f, 1, 2, 3,
                            4, 5, 6),
                    new H263Format(new Dimension(1, 1), Format.NOT_SPECIFIED,
                            Format.shortArray, 2.f, 1, 2, 3, 4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000, null, 2.f, 1, 2,
                            3, 4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, Format.NOT_SPECIFIED, 1, 2, 3,
                            4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, Format.NOT_SPECIFIED, 2, 3,
                            4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, Format.NOT_SPECIFIED, 3,
                            4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 2, Format.NOT_SPECIFIED,
                            4, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 2, 3,
                            Format.NOT_SPECIFIED, 5, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 2, 3, 4,
                            Format.NOT_SPECIFIED, 6),
                    new H263Format(new Dimension(1, 1), 2000,
                            Format.shortArray, 2.f, 1, 2, 3, 4, 5,
                            Format.NOT_SPECIFIED),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                H263Format f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }
    }

    public void testEqualsMatches_IndexedColorFormat1()
    {
        {
            final byte[] arr1 = new byte[] { 0, 0 };
            final byte[] arr2 = new byte[] { 0, 0 };
            final byte[] arr3 = new byte[] { 0, 0 };

            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            final IndexedColorFormat f2 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            assertTrue(f1.equals(f2));
            assertTrue(f1.matches(f2));
            assertTrue(f2.equals(f1));
            assertTrue(f2.matches(f1));

        }

        // dimension
        {
            final byte[] arr1 = new byte[] { 0, 0 };
            final byte[] arr2 = new byte[] { 0, 0 };
            final byte[] arr3 = new byte[] { 0, 0 };

            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            final IndexedColorFormat f2 = new IndexedColorFormat(new Dimension(
                    0, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            assertFalse(f1.equals(f2));
            assertFalse(f1.matches(f2));
            assertFalse(f2.equals(f1));
            assertFalse(f2.matches(f1));

        }

        // dataType
        {
            final byte[] arr1 = new byte[] { 0, 0 };
            final byte[] arr2 = new byte[] { 0, 0 };
            final byte[] arr3 = new byte[] { 0, 0 };

            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            final IndexedColorFormat f2 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.shortArray, 3.f, 1, 2, arr1, arr2, arr3);
            assertFalse(f1.equals(f2));
            assertFalse(f1.matches(f2));
            assertFalse(f2.equals(f1));
            assertFalse(f2.matches(f1));

        }

        // max
        {
            final byte[] arr1 = new byte[] { 0, 0 };
            final byte[] arr2 = new byte[] { 0, 0 };
            final byte[] arr3 = new byte[] { 0, 0 };

            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            final IndexedColorFormat f2 = new IndexedColorFormat(new Dimension(
                    1, 1), 2001, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            assertFalse(f1.equals(f2));
            assertTrue(f1.matches(f2));
            assertFalse(f2.equals(f1));
            assertTrue(f2.matches(f1));

        }

        {
            final byte[] arr1 = new byte[] { 0, 0 };
            final byte[] arr2 = new byte[] { 0, 0 };
            final byte[] arr3 = new byte[] { 0, 0 };

            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            final IndexedColorFormat f2 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 4.f, 1, 2, arr1, arr2, arr3);
            assertFalse(f1.equals(f2));
            assertFalse(f1.matches(f2));
            assertFalse(f2.equals(f1));
            assertFalse(f2.matches(f1));

        }

        {
            final byte[] arr1 = new byte[] { 0, 0 };
            final byte[] arr2 = new byte[] { 0, 0 };
            final byte[] arr3 = new byte[] { 0, 0 };

            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            final IndexedColorFormat f2 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 2, 2, arr1, arr2, arr3);
            assertFalse(f1.equals(f2));
            assertFalse(f1.matches(f2));
            assertFalse(f2.equals(f1));
            assertFalse(f2.matches(f1));

        }

        {
            final byte[] arr1 = new byte[] { 0, 0 };
            final byte[] arr2 = new byte[] { 0, 0 };
            final byte[] arr3 = new byte[] { 0, 0 };

            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            final IndexedColorFormat f2 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 3, arr1, arr2, arr3);
            assertFalse(f1.equals(f2));
            assertFalse(f1.matches(f2));
            assertFalse(f2.equals(f1));
            assertFalse(f2.matches(f1));

        }

        {
            final byte[] arr1 = new byte[] { 0, 0 };
            final byte[] arr2 = new byte[] { 0, 0 };
            final byte[] arr3 = new byte[] { 0, 0 };

            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            final IndexedColorFormat f2 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 3,
                    new byte[] { 0, 0 }, arr2, arr3);
            assertFalse(f1.equals(f2));
            assertFalse(f1.matches(f2));
            assertFalse(f2.equals(f1));
            assertFalse(f2.matches(f1));

        }

    }

    public void testEqualsMatches_IndexedColorFormat2()
    {
        final byte[] arr1 = new byte[] { 0, 0 };
        final byte[] arr2 = new byte[] { 0, 0 };
        final byte[] arr3 = new byte[] { 0, 0 };

        final IndexedColorFormat f1 = new IndexedColorFormat(
                new Dimension(1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1,
                arr2, arr3);

        // IndexedColorFormat - equal and match:
        {
            final IndexedColorFormat[] f2s = new IndexedColorFormat[] {
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3),
                    (IndexedColorFormat) f1.clone(),
                    (IndexedColorFormat) f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                IndexedColorFormat f2 = f2s[i];
                assertTrue(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertTrue(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

        // IndexedColorFormat - not equal and not match:
        {
            final IndexedColorFormat[] f2s = new IndexedColorFormat[] {
                    new IndexedColorFormat(new Dimension(1, 2), 2000,
                            Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.shortArray, 3.f, 1, 2, arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 4.f, 1, 2, arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 11, 2, arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 1, 12, arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 1, 2, arr2, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 1, 2, arr1, arr3, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 1, 2, arr1, arr2, arr1),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                IndexedColorFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertFalse(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertFalse(f2.matches(f1));
            }
        }

        // IndexedColorFormat - not equal but match:
        {
            final IndexedColorFormat[] f2s = new IndexedColorFormat[] {
                    new IndexedColorFormat(new Dimension(1, 1), 2001,
                            Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3),
                    new IndexedColorFormat(null, 2000, Format.byteArray, 3.f,
                            1, 2, arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1),
                            Format.NOT_SPECIFIED, Format.byteArray, 3.f, 1, 2,
                            arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000, null,
                            3.f, 1, 2, arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, Format.NOT_SPECIFIED, 1, 2, arr1,
                            arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, Format.NOT_SPECIFIED, 2,
                            arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 1, Format.NOT_SPECIFIED,
                            arr1, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 1, 2, null, arr2, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 1, 2, arr1, null, arr3),
                    new IndexedColorFormat(new Dimension(1, 1), 2000,
                            Format.byteArray, 3.f, 1, 2, arr1, arr2, null),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                IndexedColorFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }
    }

    public void testEqualsMatches_JPEGFormat()
    {
        final JPEGFormat f1 = new JPEGFormat(new Dimension(1, 1), 1000,
                Format.shortArray, 1.f, 2, 3);

        // equal and match:
        {
            final JPEGFormat[] f2s = new JPEGFormat[] {
                    new JPEGFormat(new Dimension(1, 1), 1000,
                            Format.shortArray, 1.f, 2, 3),
                    (JPEGFormat) f1.clone(), (JPEGFormat) f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                JPEGFormat f2 = f2s[i];
                assertTrue(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertTrue(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

        // not equal and not match:
        {
            final JPEGFormat[] f2s = new JPEGFormat[] {
                    new JPEGFormat(new Dimension(1, 2), 1000,
                            Format.shortArray, 1.f, 2, 3),
                    new JPEGFormat(new Dimension(1, 1), 1000, Format.byteArray,
                            1.f, 2, 3),
                    new JPEGFormat(new Dimension(1, 1), 1000,
                            Format.shortArray, 2.f, 2, 3),
                    new JPEGFormat(new Dimension(1, 1), 1000,
                            Format.shortArray, 1.f, 12, 3),
                    new JPEGFormat(new Dimension(1, 1), 1000,
                            Format.shortArray, 1.f, 2, 13),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                JPEGFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertFalse(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertFalse(f2.matches(f1));
            }
        }

        // not equal but match:
        {
            final JPEGFormat[] f2s = new JPEGFormat[] {
                    new JPEGFormat(new Dimension(1, 1), 2000,
                            Format.shortArray, 1.f, 2, 3),
                    new JPEGFormat(null, 1000, Format.shortArray, 1.f, 2, 3),
                    new JPEGFormat(new Dimension(1, 1), Format.NOT_SPECIFIED,
                            Format.shortArray, 1.f, 2, 3),
                    new JPEGFormat(new Dimension(1, 1), 1000, null, 1.f, 2, 3),
                    new JPEGFormat(new Dimension(1, 1), 1000,
                            Format.shortArray, Format.NOT_SPECIFIED, 2, 3),
                    new JPEGFormat(new Dimension(1, 1), 1000,
                            Format.shortArray, 1.f, Format.NOT_SPECIFIED, 3),
                    new JPEGFormat(new Dimension(1, 1), 1000,
                            Format.shortArray, 1.f, 2, Format.NOT_SPECIFIED),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                JPEGFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }
    }

    public void testEqualsMatches_RGBFormat()
    {
        final RGBFormat f1 = new RGBFormat(new Dimension(1, 1), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, 8);

        // RGBFormat - equal and match:
        {
            final RGBFormat[] f2s = new RGBFormat[] {
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 5, 6, 7, 8),
                    (RGBFormat) f1.clone(), (RGBFormat) f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                RGBFormat f2 = f2s[i];
                assertTrue(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertTrue(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

        // RGBFormat - not equal and not match:
        {
            final RGBFormat[] f2s = new RGBFormat[] {
                    new RGBFormat(new Dimension(1, 2), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.intArray,
                            2.f, 1, 2, 3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            1.f, 1, 2, 3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 11, 2, 3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 12, 3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 13, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 14, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 15, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 5, 6, 17, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 5, 6, 7, 18), };
            for (int i = 0; i < f2s.length; ++i)
            {
                RGBFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertFalse(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertFalse(f2.matches(f1));
            }
        }

        // RGBFormat - not equal but match:
        {
            final RGBFormat[] f2s = new RGBFormat[] {
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 5, 16, 7, 8),
                    new RGBFormat(null, 2000, Format.byteArray, 2.f, 1, 2, 3,
                            4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), Format.NOT_SPECIFIED,
                            Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, null, 2.f, 1, 2,
                            3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            Format.NOT_SPECIFIED, 1, 2, 3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, Format.NOT_SPECIFIED, 2, 3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, Format.NOT_SPECIFIED, 3, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, Format.NOT_SPECIFIED, 4, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, Format.NOT_SPECIFIED, 5, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, Format.NOT_SPECIFIED, 6, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 5, Format.NOT_SPECIFIED, 7, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 5, 6, Format.NOT_SPECIFIED, 8),
                    new RGBFormat(new Dimension(1, 1), 2000, Format.byteArray,
                            2.f, 1, 2, 3, 4, 5, 6, 7, Format.NOT_SPECIFIED),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                RGBFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

    }

    // intersection:

    public void testEqualsMatches_VideoFormat()
    {
        final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                0, 0), 1000, Format.byteArray, 1.f);

        // equal and match:
        {
            final VideoFormat[] f2s = new VideoFormat[] {
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.byteArray, 1.f),
                    (VideoFormat) f1.clone(), (VideoFormat) f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                VideoFormat f2 = f2s[i];
                assertTrue(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertTrue(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

        // not equal and not match:
        {
            final VideoFormat[] f2s = new VideoFormat[] {
                    new VideoFormat(VideoFormat.CINEPAK, new Dimension(0, 0),
                            1000, Format.byteArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(1, 0),
                            1000, Format.byteArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.shortArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.byteArray, 2.f),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                VideoFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertFalse(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertFalse(f2.matches(f1));
            }
        }

        // not equal but match:
        {
            final VideoFormat[] f2s = new VideoFormat[] {
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            2000, Format.byteArray, 1.f),
                    new VideoFormat(null, new Dimension(0, 0), 1000,
                            Format.byteArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, null, 1000,
                            Format.byteArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            Format.NOT_SPECIFIED, Format.byteArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, null, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.byteArray, Format.NOT_SPECIFIED), };
            for (int i = 0; i < f2s.length; ++i)
            {
                VideoFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }
    }

    public void testEqualsMatches_YUVFormat()
    {
        final YUVFormat f1 = new YUVFormat(new java.awt.Dimension(120, 200),
                1000, Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 3, 4, 5, 6);

        // YUVFormat - equal and match:
        {
            final YUVFormat[] f2s = new YUVFormat[] {
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 3, 4,
                            5, 6), (YUVFormat) f1.clone(),
                    (YUVFormat) f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                YUVFormat f2 = f2s[i];

                assertTrue(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertTrue(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }

        // YUVFormat - not equal and not match:
        {
            final YUVFormat[] f2s = new YUVFormat[] {
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_411, 2, 3, 4,
                            5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 12, 3, 4,
                            5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 13, 4,
                            5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 3, 14,
                            5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 3, 4,
                            15, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 3, 4,
                            5, 16), };
            for (int i = 0; i < f2s.length; ++i)
            {
                YUVFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertFalse(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertFalse(f2.matches(f1));
            }
        }

        // YUVFormat - not equal but match:
        {
            final YUVFormat[] f2s = new YUVFormat[] {
                    new YUVFormat(null, 1000, Format.byteArray, 1.f,
                            YUVFormat.YUV_111, 2, 3, 4, 5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200),
                            Format.NOT_SPECIFIED, Format.byteArray, 1.f,
                            YUVFormat.YUV_111, 2, 3, 4, 5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000, null,
                            1.f, YUVFormat.YUV_111, 2, 3, 4, 5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, Format.NOT_SPECIFIED,
                            YUVFormat.YUV_111, 2, 3, 4, 5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, Format.NOT_SPECIFIED, 2, 3,
                            4, 5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111,
                            Format.NOT_SPECIFIED, 3, 4, 5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 2,
                            Format.NOT_SPECIFIED, 4, 5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 3,
                            Format.NOT_SPECIFIED, 5, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 3, 4,
                            Format.NOT_SPECIFIED, 6),
                    new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                            Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 3, 4,
                            5, Format.NOT_SPECIFIED),

            };
            for (int i = 0; i < f2s.length; ++i)
            {
                YUVFormat f2 = f2s[i];
                // System.out.println(f2);
                assertFalse(f1.equals(f2));
                assertTrue(f1.matches(f2));
                assertFalse(f2.equals(f1));
                assertTrue(f2.matches(f1));
            }
        }
    }

    public void testIntersects()
    {
        // intersects:
        {
            final Format f1 = new Format("abc");
            final Format f2 = new Format("abc");
            assertEquals(f1.intersects(f2), f1);
            assertEquals(f1.intersects(f2), f2);
            assertEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f2);
        }

        {
            final Format f1 = new Format("abc");
            final Format f2 = new Format("xyz");
            assertEquals(f1.intersects(f2), f2);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f2);
            assertEquals(f2.intersects(f1), f1);
        }

        {
            final Format f1 = new Format("abc");
            final Format f2 = new Format(null);
            assertEquals(f1.intersects(f2), f1);
            assertNotEquals(f1.intersects(f2), f2);
            assertEquals(f2.intersects(f1), f1);
        }

        {
            final Format f1 = new Format("abc", Format.byteArray);
            final Format f2 = new Format(null);
            assertEquals(f1.intersects(f2), f1);
            assertNotEquals(f1.intersects(f2), f2);
            assertEquals(f2.intersects(f1), f1);
        }

        {
            final Format f1 = new Format("abc", Format.byteArray);
            final Format f2 = new Format("xyz", Format.shortArray);
            assertEquals(f1.intersects(f2), f2);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f2);
            assertEquals(f2.intersects(f1), f1);
        }

        {
            final Format f1 = new Format("abc");
            final Format f2 = new VideoFormat("abc");
            assertEquals(f1.intersects(f2), f2);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f2);
        }

        {
            final Format f1 = new Format("abc");
            final Format f2 = new VideoFormat("xyz");
            assertEquals(f1.intersects(f2), f2);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f2);
        }

        {
            final Format f1 = new Format("abc");
            final Format f2 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.byteArray, 1.f);
            assertEquals(f1.intersects(f2), f2);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f2);
        }

        {
            final Format f1 = new Format("abc", Format.intArray);
            final Format f2 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.byteArray, 1.f);
            assertEquals(f1.intersects(f2), f2);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f2);
        }

        {
            final Format f1 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.byteArray, 1.f);
            final Format f2 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    1, 0), 1000, Format.byteArray, 1.f);
            assertEquals(f1.intersects(f2), f1);
            assertNotEquals(f1.intersects(f2), f2);
            assertNotEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f2);
        }

        {
            final Format f1 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.byteArray, 1.f);
            final Format f2 = new VideoFormat(VideoFormat.MPEG, null, 1000,
                    Format.byteArray, 1.f);
            assertEquals(f1.intersects(f2), f1);
            assertNotEquals(f1.intersects(f2), f2);
            assertNotEquals(f2.intersects(f1), f2);
            assertEquals(f2.intersects(f1), f1);
        }

        {
            final Format f1 = new Format("abc", Format.intArray);
            final Format f2 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.byteArray, 1.f);
            assertEquals(f1.intersects(f2), f2);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f2);
        }

        {
            final Format f1 = new Format(null, Format.intArray);
            final Format f2 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, null, 1.f);
            final Format f3 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.intArray, 1.f);
            assertEquals(f1.intersects(f2), f3);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f3);
        }

        {
            final Format f1 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.intArray, 2.f);
            final Format f2 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    1, 0), 1000, Format.intArray, 1.f);
            final Format f3 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.intArray, 2.f);
            final Format f4 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    1, 0), 1000, Format.intArray, 1.f);
            assertEquals(f1.intersects(f2), f3);
            assertNotEquals(f1.intersects(f2), f2);
            assertNotEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f4);
        }

        {
            final Format f1 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.intArray, 2.f);
            final Format f2 = new RGBFormat(new Dimension(1, 1), 2000,
                    Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, 8);
            final Format f3 = new RGBFormat(new Dimension(0, 0), 1000,
                    Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, 8);
            // System.out.println(f1.intersects(f2));
            // System.out.println(f3);
            assertEquals(f1.intersects(f2), f3);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f1);
            assertEquals(f2.intersects(f1), f2);
        }

        {
            final Format f1 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                    0, 0), 1000, Format.intArray, Format.NOT_SPECIFIED);
            final Format f2 = new RGBFormat(new Dimension(1, 1), 2000, null,
                    2.f, 1, 2, 3, 4, 5, 6, 7, 8);
            final Format f3 = new RGBFormat(new Dimension(0, 0), 1000,
                    Format.intArray, 2.f, 1, 2, 3, 4, 5, 6, 7, 8);
            final Format f4 = new RGBFormat(new Dimension(1, 1), 2000,
                    Format.intArray, 2.f, 1, 2, 3, 4, 5, 6, 7, 8);
            // System.out.println(f1.intersects(f2));
            // System.out.println(f3);
            assertEquals(f1.intersects(f2), f3);
            assertNotEquals(f1.intersects(f2), f1);
            assertNotEquals(f2.intersects(f1), f1);
            // System.out.println(f2.intersects(f1));
            // System.out.println(f4);
            assertEquals(f2.intersects(f1), f4);
        }

        {
            final Format f1 = new RGBFormat(new Dimension(1, 1), 1000,
                    Format.byteArray, 2.f, 11, 2, 13, 14, 5, 6, 17, 8);
            final Format f2 = new RGBFormat(new Dimension(1, 0), 2000,
                    Format.intArray, 3.f, 1, 12, 3, 4, 15, 16, 7, 18);
            final Format f3 = new RGBFormat(new Dimension(1, 1), 1000,
                    Format.intArray, 2.f, 11, 2, 13, 14, 5, 6, 17, 8);
            final Format f4 = new RGBFormat(new Dimension(1, 0), 2000,
                    Format.byteArray, 3.f, 1, 12, 3, 4, 15, 16, 7, 18);
            // System.out.println(f1.intersects(f2));
            // System.out.println(f3);
            assertEquals(f1.intersects(f2), f3);
            assertNotEquals(f1.intersects(f2), f2);
            assertNotEquals(f2.intersects(f1), f1);
            // System.out.println(f2.intersects(f1));
            // System.out.println(f4);
            assertEquals(f2.intersects(f1), f4);
        }

        {
            final Format f1 = new RGBFormat(new Dimension(1, 1), 1000,
                    Format.byteArray, 2.f, 11, 2, 13, 14, 5, 6, 17, 8);
            final Format f2 = new RGBFormat();
            final Format f3 = (Format) f1.clone();
            final Format f4 = (Format) f1.clone();
            // System.out.println(f1.intersects(f2));
            // System.out.println(f3);
            // final Format f1_2 = f1.intersects(f2);
            assertEquals(f1.intersects(f2), f3);
            assertNotEquals(f1.intersects(f2), f2);
            assertEquals(f2.intersects(f1), f1);
            // final Format f2_1 = f1.intersects(f2);
            // System.out.println(f2.intersects(f1));
            // System.out.println(f4);
            assertEquals(f2.intersects(f1), f4);
        }

        {
            final Format f1 = new RGBFormat(new Dimension(1, 1), 1000,
                    Format.byteArray, 2.f, 11, 2, 13, 14, 5, 6, 17, 8);
            final Format f2 = new YUVFormat(new Dimension(1, 1), 2000,
                    Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6);
            assertEquals(f1.intersects(f2), null);
            assertEquals(f2.intersects(f1), null);
        }

    }

    public void testIntersects_Format()
    {
        final Format f1 = new Format("abc", Format.byteArray);

        // equals f1
        {
            final Format[] f2s = new Format[] {
                    new Format("abc", Format.byteArray), (Format) f1.clone(),
                    f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                Format f2 = f2s[i];
                final Format f3 = f1.intersects(f2);

                assertTrue(f1.equals(f3));
                assertTrue(f1.matches(f3));
                assertTrue(f3.equals(f1));
                assertTrue(f3.matches(f1));
            }
        }

        // explicit intersect results
        {
            final Format[] f2s = new Format[] {
                    new Format("xyz", Format.byteArray),
                    new Format("abc", Format.shortArray),
                    new Format("abc", Format.intArray),
                    new Format(null, Format.byteArray),
                    new Format("abc", null),

            };
            final Format[] f1_2s = new Format[] {
                    new Format("xyz", Format.byteArray),
                    new Format("abc", Format.shortArray),
                    new Format("abc", Format.intArray),
                    new Format("abc", Format.byteArray),
                    new Format("abc", Format.byteArray),

            };
            final Format[] f2_1s = new Format[] {
                    new Format("abc", Format.byteArray),
                    new Format("abc", Format.byteArray),
                    new Format("abc", Format.byteArray),
                    new Format("abc", Format.byteArray),
                    new Format("abc", Format.byteArray),

            };

            for (int i = 0; i < f2s.length; ++i)
            {
                final Format f2 = f2s[i];
                final Format f1_2 = f1_2s[i];
                final Format f2_1 = f2_1s[i];
                final Format f1_2_actual = f1.intersects(f2);
                final Format f2_1_actual = f2.intersects(f1);

                // System.out.println("" + f1_2);
                // System.out.println("" + f1_2_actual);
                assertTrue(f1_2.equals(f1_2_actual));
                assertTrue(f1_2.matches(f1_2_actual));
                assertTrue(f1_2_actual.equals(f1_2));
                assertTrue(f1_2_actual.matches(f1_2));

                // System.out.println("" + f2_1);
                // System.out.println("" + f2_1_actual);
                assertTrue(f2_1.equals(f2_1_actual));
                assertTrue(f2_1.matches(f2_1_actual));
                assertTrue(f2_1_actual.equals(f2_1));
                assertTrue(f2_1_actual.matches(f2_1));
            }
        }

    }

    public void testIntersects_VideoFormat()
    {
        if (false)
            return;
        final VideoFormat f1 = new VideoFormat(VideoFormat.MPEG, new Dimension(
                0, 0), 1000, Format.byteArray, 1.f);

        // equals f1
        {
            final VideoFormat[] f2s = new VideoFormat[] {
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.byteArray, 1.f),
                    (VideoFormat) f1.clone(), (VideoFormat) f1.intersects(f1) };
            for (int i = 0; i < f2s.length; ++i)
            {
                VideoFormat f2 = f2s[i];
                final VideoFormat f3 = (VideoFormat) f1.intersects(f2);

                assertTrue(f1.equals(f3));
                assertTrue(f1.matches(f3));
                assertTrue(f3.equals(f1));
                assertTrue(f3.matches(f1));
            }
        }

        // explicit intersect results
        {
            final VideoFormat[] f2s = new VideoFormat[] {
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.byteArray, 2.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(1, 0),
                            1000, Format.byteArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1001, Format.byteArray, 1.f),

            };
            final VideoFormat[] f1_2s = new VideoFormat[] {
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.byteArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.byteArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.byteArray, 1.f),

            };
            final VideoFormat[] f2_1s = new VideoFormat[] {
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1000, Format.byteArray, 2.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(1, 0),
                            1000, Format.byteArray, 1.f),
                    new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                            1001, Format.byteArray, 1.f),

            };

            for (int i = 0; i < f2s.length; ++i)
            {
                final VideoFormat f2 = f2s[i];
                final VideoFormat f1_2 = f1_2s[i];
                final VideoFormat f2_1 = f2_1s[i];
                final VideoFormat f1_2_actual = (VideoFormat) f1.intersects(f2);
                final VideoFormat f2_1_actual = (VideoFormat) f2.intersects(f1);

                // System.out.println(f1_2);
                // System.out.println(f1_2_actual);
                assertTrue(f1_2.equals(f1_2_actual));
                assertTrue(f1_2.matches(f1_2_actual));
                assertTrue(f1_2_actual.equals(f1_2));
                assertTrue(f1_2_actual.matches(f1_2));

                // System.out.println(f2_1);
                // System.out.println(f2_1_actual);
                assertTrue(f2_1.equals(f2_1_actual));
                assertTrue(f2_1.matches(f2_1_actual));
                assertTrue(f2_1_actual.equals(f2_1));
                assertTrue(f2_1_actual.matches(f2_1));
            }
        }

    }

    public void testRelax()
    {
        // relax:
        {
            final Format f1 = new Format("abc", Format.byteArray);
            assertTrue(f1.relax().equals(f1));
        }

        {
            final Format f1 = new Format(null, Format.byteArray);
            assertTrue(f1.relax().equals(f1));
        }

        {
            final Format f1 = new Format("abc");
            assertTrue(f1.relax().equals(f1));
        }

        {
            final Format f1 = new Format(null);
            assertTrue(f1.relax().equals(f1));
        }

        {
            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2,
                    new byte[] { 0, 0 }, new byte[] { 0, 0 },
                    new byte[] { 0, 0 });
            final IndexedColorFormat f2 = (IndexedColorFormat) f1.relax();
            assertFalse(f1.equals(f2));
            assertEquals(f2.getRedValues(), f1.getRedValues());
            assertEquals(f2.getGreenValues(), f1.getGreenValues());
            assertEquals(f2.getBlueValues(), f1.getBlueValues());
            assertEquals(f2.getEncoding(), f1.getEncoding());
            assertEquals(f2.getDataType(), f1.getDataType());
            assertEquals(f2.getFrameRate(), -1.f);
            assertEquals(f2.getLineStride(), -1);
            assertEquals(f2.getMapSize(), f1.getMapSize());
            assertEquals(f2.getMaxDataLength(), -1);
            assertEquals(f2.getSize(), null);

        }

        {
            final AudioFormat f1 = new AudioFormat(AudioFormat.DOLBYAC3, 2.0,
                    1, 2, 3, 4, 5, 6.0, Format.byteArray);
            final AudioFormat f2 = (AudioFormat) f1.relax();
            assertTrue(f1.equals(f2));
            assertEquals(f2.getSampleRate(), f1.getSampleRate());
            assertEquals(f2.getChannels(), f1.getChannels());
            assertEquals(f2.getEndian(), f1.getEndian());
            assertEquals(f2.getEncoding(), f1.getEncoding());
            assertEquals(f2.getDataType(), f1.getDataType());
            assertEquals(f2.getFrameRate(), f1.getFrameRate());
            assertEquals(f2.getFrameSizeInBits(), f1.getFrameSizeInBits());
            assertEquals(f2.getSampleSizeInBits(), f1.getSampleSizeInBits());
            assertEquals(f2.getSigned(), f1.getSigned());

        }

        {
            final RGBFormat f1 = new RGBFormat(new Dimension(1, 1), 2000,
                    Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, 8);
            final RGBFormat f2 = (RGBFormat) f1.relax();
            assertFalse(f1.equals(f2));
            assertEquals(f2.getRedMask(), f1.getRedMask());
            assertEquals(f2.getGreenMask(), f1.getGreenMask());
            assertEquals(f2.getBlueMask(), f1.getBlueMask());
            assertEquals(f2.getEncoding(), f1.getEncoding());
            assertEquals(f2.getDataType(), f1.getDataType());
            assertEquals(f2.getFrameRate(), -1.f);
            assertEquals(f2.getLineStride(), -1);
            assertEquals(f2.getEndian(), f1.getEndian());
            assertEquals(f2.getBitsPerPixel(), f1.getBitsPerPixel());
            assertEquals(f2.getFlipped(), f1.getFlipped());
            assertEquals(f2.getMaxDataLength(), -1);
            assertEquals(f2.getSize(), null);

        }

        {
            final YUVFormat f1 = new YUVFormat(new Dimension(1, 1), 2000,
                    Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6);
            final YUVFormat f2 = (YUVFormat) f1.relax();
            assertFalse(f1.equals(f2));
            assertEquals(f2.getEncoding(), f1.getEncoding());
            assertEquals(f2.getDataType(), f1.getDataType());
            assertEquals(f2.getFrameRate(), -1.f);
            assertEquals(f2.getMaxDataLength(), -1);
            assertEquals(f2.getSize(), null);
            assertEquals(f2.getOffsetU(), -1);
            assertEquals(f2.getOffsetV(), -1);
            assertEquals(f2.getOffsetY(), -1);
            assertEquals(f2.getStrideUV(), -1);
            assertEquals(f2.getStrideY(), -1);
            assertEquals(f2.getYuvType(), f1.getYuvType());

        }

        {
            final JPEGFormat f1 = new JPEGFormat(new Dimension(1, 1), 2000,
                    Format.byteArray, 2.f, 1, 2);
            final JPEGFormat f2 = (JPEGFormat) f1.relax();
            assertFalse(f1.equals(f2));
            assertEquals(f2.getEncoding(), f1.getEncoding());
            assertEquals(f2.getDataType(), f1.getDataType());
            assertEquals(f2.getFrameRate(), -1.f);
            assertEquals(f2.getMaxDataLength(), -1);
            assertEquals(f2.getSize(), null);
            assertEquals(f2.getQFactor(), f1.getQFactor());
            assertEquals(f2.getDecimation(), f1.getDecimation());

        }

        {
            final H261Format f1 = new H261Format(new Dimension(1, 1), 2000,
                    Format.byteArray, 2.f, 1);
            final H261Format f2 = (H261Format) f1.relax();
            assertFalse(f1.equals(f2));
            assertEquals(f2.getEncoding(), f1.getEncoding());
            assertEquals(f2.getDataType(), f1.getDataType());
            assertEquals(f2.getFrameRate(), -1.f);
            assertEquals(f2.getMaxDataLength(), -1);
            assertEquals(f2.getSize(), null);
            assertEquals(f2.getStillImageTransmission(),
                    f1.getStillImageTransmission());

        }

        {
            final H263Format f1 = new H263Format(new Dimension(1, 1), 2000,
                    Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6);
            final H263Format f2 = (H263Format) f1.relax();
            assertFalse(f1.equals(f2));
            assertEquals(f2.getEncoding(), f1.getEncoding());
            assertEquals(f2.getDataType(), f1.getDataType());
            assertEquals(f2.getFrameRate(), -1.f);
            assertEquals(f2.getMaxDataLength(), -1);
            assertEquals(f2.getSize(), null);
            assertEquals(f2.getAdvancedPrediction(), f1.getAdvancedPrediction());
            assertEquals(f2.getArithmeticCoding(), f1.getArithmeticCoding());
            assertEquals(f2.getErrorCompensation(), f1.getErrorCompensation());
            assertEquals(f2.getHrDB(), f1.getHrDB());
            assertEquals(f2.getPBFrames(), f1.getPBFrames());
            assertEquals(f2.getUnrestrictedVector(), f1.getUnrestrictedVector());

        }

    }

    public void testToString()
    {
        // strings
        assertEquals(new Format("abc").toString(), "abc");
        assertEquals(new Format(null).toString(), null);
        assertEquals(new Format("abc", Format.byteArray).toString(), "abc");
        assertEquals(new Format("abc", Format.intArray).toString(), "abc");
        assertEquals(new Format("abc", Format.shortArray).toString(), "abc");
        assertEquals(new VideoFormat("abc").toString(), "ABC");
        assertEquals(new VideoFormat(null).toString(), "N/A");
        assertEquals(new VideoFormat(VideoFormat.MPEG).toString(), "MPEG");

        assertEquals(new VideoFormat(VideoFormat.MPEG, new Dimension(0, 0),
                1000, Format.byteArray, 1.f).toString(),
                "MPEG, 0x0, FrameRate=1.0, Length=1000");
        assertEquals(new VideoFormat(VideoFormat.MPEG, null, 1000,
                Format.byteArray, 1.f).toString(),
                "MPEG, FrameRate=1.0, Length=1000");
        assertEquals(new VideoFormat(VideoFormat.MPEG, null,
                Format.NOT_SPECIFIED, Format.byteArray, 1.f).toString(),
                "MPEG, FrameRate=1.0");

        assertEquals(new VideoFormat(VideoFormat.CINEPAK).toString(), "CVID");
        assertEquals(new VideoFormat(VideoFormat.JPEG_RTP).toString(),
                "JPEG/RTP");
        assertEquals(new VideoFormat(VideoFormat.IRGB).toString(), "IRGB");
        assertEquals(new VideoFormat(VideoFormat.INDEO32).toString(), "IV32");

        assertEquals(
                new YUVFormat().toString(),
                "YUV Video Format: Size = null MaxDataLength = -1 DataType = class [B yuvType = -1 StrideY = -1 StrideUV = -1 OffsetY = -1 OffsetU = -1 OffsetV = -1\n");
        assertEquals(
                new YUVFormat(YUVFormat.YUV_111).toString(),
                "YUV Video Format: Size = null MaxDataLength = -1 DataType = class [B yuvType = 8 StrideY = -1 StrideUV = -1 OffsetY = -1 OffsetU = -1 OffsetV = -1\n");
        assertEquals(
                new YUVFormat(new java.awt.Dimension(120, 200), 1000,
                        Format.byteArray, 1.f, YUVFormat.YUV_111, 2, 3, 4, 5, 6)
                        .toString(),
                "YUV Video Format: Size = java.awt.Dimension[width=120,height=200] MaxDataLength = 1000 DataType = class [B yuvType = 8 StrideY = 2 StrideUV = 3 OffsetY = 4 OffsetU = 5 OffsetV = 6\n");

        assertEquals(new RGBFormat().toString(),
                "RGB, -1-bit, Masks=-1:-1:-1, PixelStride=-1, LineStride=-1");

        assertEquals(new JPEGFormat().toString(),
                "jpeg video format: dataType = class [B");
        assertEquals(
                new JPEGFormat(new Dimension(1, 1), 1000, Format.shortArray,
                        1.f, 2, 3).toString(),
                "jpeg video format: size = 1x1 FrameRate = 1.0 maxDataLength = 1000 dataType = class [S q factor = 2 decimation = 3");
        assertEquals(
                new JPEGFormat(new Dimension(1, 1), 1000, Format.shortArray,
                        1.f, -1, 3).toString(),
                "jpeg video format: size = 1x1 FrameRate = 1.0 maxDataLength = 1000 dataType = class [S decimation = 3");

        assertEquals(new H261Format().toString(), "H.261 video format");
        assertEquals(new H261Format(new Dimension(1, 1), 2000,
                Format.byteArray, 3.f, 1).toString(), "H.261 video format");
        assertEquals(new H263Format().toString(), "H.263 video format");

        assertEquals(new H263Format(new Dimension(1, 1), 2000,
                Format.shortArray, 2.f, 1, 2, 3, 4, 5, 6).toString(),
                "H.263 video format");

        assertEquals(new IndexedColorFormat(new Dimension(1, 1), 2000,
                Format.byteArray, 3.f, 1, 2, new byte[] { 0, 0 }, new byte[] {
                        0, 0 }, new byte[] { 0, 0 }).toString(),
                "IRGB, 1x1, FrameRate=3.0, Length=2000");
        assertEquals(new IndexedColorFormat(new Dimension(1, 1), 2000,
                Format.byteArray, -1.f, 1, 2, new byte[] { 0, 0 }, new byte[] {
                        0, 0 }, new byte[] { 0, 0 }).toString(),
                "IRGB, 1x1, Length=2000");
        assertEquals(new IndexedColorFormat(new Dimension(1, 1), -1,
                Format.byteArray, -1.f, 1, 2, new byte[] { 0, 0 }, new byte[] {
                        0, 0 }, new byte[] { 0, 0 }).toString(), "IRGB, 1x1");
        assertEquals(new IndexedColorFormat(null, -1, Format.byteArray, -1.f,
                1, 2, new byte[] { 0, 0 }, new byte[] { 0, 0 }, new byte[] { 0,
                        0 }).toString(), "IRGB");

        assertEquals(new AudioFormat(AudioFormat.DOLBYAC3).toString(),
                "dolbyac3, Unknown Sample Rate");
        assertEquals(new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 2, 3, 4, 5,
                6.0, Format.byteArray).toString(),
                "dolbyac3, 2.0 Hz, 1-bit, Stereo, Unsigned, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 1, 3, 4, 5,
                6.0, Format.byteArray).toString(),
                "dolbyac3, 2.0 Hz, 1-bit, Mono, Unsigned, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(
                new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 0, 3, 4, 5, 6.0,
                        Format.byteArray).toString(),
                "dolbyac3, 2.0 Hz, 1-bit, 0-channel, Unsigned, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(
                new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 3, 3, 4, 5, 6.0,
                        Format.byteArray).toString(),
                "dolbyac3, 2.0 Hz, 1-bit, 3-channel, Unsigned, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(
                new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 3, 3, 0, 5, 6.0,
                        Format.byteArray).toString(),
                "dolbyac3, 2.0 Hz, 1-bit, 3-channel, Unsigned, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 1, 3, 3, 1, 5,
                6.0, Format.byteArray).toString(),
                "dolbyac3, 2.0 Hz, 1-bit, 3-channel, Signed, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(
                new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 32, 3, 1, 1, 5, 6.0,
                        Format.byteArray).toString(),
                "dolbyac3, 2.0 Hz, 32-bit, 3-channel, BigEndian, Signed, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(
                new AudioFormat(AudioFormat.DOLBYAC3, 2.0, 32, 3, 0, 1, 5, 6.0,
                        Format.byteArray).toString(),
                "dolbyac3, 2.0 Hz, 32-bit, 3-channel, LittleEndian, Signed, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(
                new AudioFormat(AudioFormat.LINEAR, 2.0, 16, 3, 1, 1, 5, 6.0,
                        Format.byteArray).toString(),
                "LINEAR, 2.0 Hz, 16-bit, 3-channel, BigEndian, Signed, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(new AudioFormat(AudioFormat.LINEAR, 2.0, 8, 3, 1, 1, 5,
                6.0, Format.byteArray).toString(),
                "LINEAR, 2.0 Hz, 8-bit, 3-channel, Signed, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(
                new AudioFormat(AudioFormat.LINEAR, 2.0, 9, 3, 1, 1, 5, 6.0,
                        Format.byteArray).toString(),
                "LINEAR, 2.0 Hz, 9-bit, 3-channel, BigEndian, Signed, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(new AudioFormat(AudioFormat.LINEAR, 2.0, -1, 3, 1, 1, 5,
                6.0, Format.byteArray).toString(),
                "LINEAR, 2.0 Hz, 3-channel, Signed, 6.0 frame rate, FrameSize=5 bits");
        assertEquals(new AudioFormat(null).toString(),
                "null, Unknown Sample Rate");
    }

    public void testWavFormat()
    {
        {
            final WavAudioFormat f = new WavAudioFormat("abc");
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getSampleRate(), Format.NOT_SPECIFIED);
            assertEquals(f.getSampleSizeInBits(), Format.NOT_SPECIFIED);
            assertEquals(f.getChannels(), Format.NOT_SPECIFIED);
            assertEquals(f.getFrameSizeInBits(), Format.NOT_SPECIFIED);
            assertEquals(f.getAverageBytesPerSecond(), Format.NOT_SPECIFIED);
            assertEquals(f.getFrameRate(), Format.NOT_SPECIFIED);
            assertEquals(f.getEndian(), Format.NOT_SPECIFIED);
            assertEquals(f.getSigned(), Format.NOT_SPECIFIED);
            assertEquals(f.getDataType(), Format.byteArray);
        }

        {
            final WavAudioFormat f = new WavAudioFormat("abc", 1.0, 2, 3, 4, 5,
                    6, 7, 8.f, Format.byteArray, new byte[] { (byte) 0 });
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getSampleRate(), 1.0);
            assertEquals(f.getSampleSizeInBits(), 2);
            assertEquals(f.getChannels(), 3);
            assertEquals(f.getFrameSizeInBits(), 4);
            assertEquals(f.getAverageBytesPerSecond(), 5);
            assertEquals(f.getFrameRate(), 5.0);
            assertEquals(f.getEndian(), 6);
            assertEquals(f.getSigned(), 7);
            assertEquals(f.getDataType(), Format.byteArray);
        }

        {
            final WavAudioFormat f = new WavAudioFormat("abc", 1.0, 2, 3, 4,
                    10, 6, 7, 8.f, Format.byteArray, new byte[] { (byte) 0 });
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getSampleRate(), 1.0);
            assertEquals(f.getSampleSizeInBits(), 2);
            assertEquals(f.getChannels(), 3);
            assertEquals(f.getFrameSizeInBits(), 4);
            assertEquals(f.getAverageBytesPerSecond(), 10);
            assertEquals(f.getFrameRate(), 10.0);
            assertEquals(f.getEndian(), 6);
            assertEquals(f.getSigned(), 7);
            assertEquals(f.getDataType(), Format.byteArray);
        }

        {
            final WavAudioFormat f = new WavAudioFormat("abc", 1.0, 2, 3, 4,
                    Format.NOT_SPECIFIED, 6, 7, 8.f, Format.byteArray,
                    new byte[] { (byte) 0 });
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getSampleRate(), 1.0);
            assertEquals(f.getSampleSizeInBits(), 2);
            assertEquals(f.getChannels(), 3);
            assertEquals(f.getFrameSizeInBits(), 4);
            assertEquals(f.getAverageBytesPerSecond(), Format.NOT_SPECIFIED);
            assertEquals(f.getFrameRate(), Format.NOT_SPECIFIED);
            assertEquals(f.getEndian(), 6);
            assertEquals(f.getSigned(), 7);
            assertEquals(f.getDataType(), Format.byteArray);
        }

        {
            final WavAudioFormat f = new WavAudioFormat("abc", 1.0, 2, 3, 4, 5,
                    new byte[] { (byte) 0 });
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getSampleRate(), 1.0);
            assertEquals(f.getSampleSizeInBits(), 2);
            assertEquals(f.getChannels(), 3);
            assertEquals(f.getFrameSizeInBits(), 4);
            assertEquals(f.getAverageBytesPerSecond(), 5);
            assertEquals(f.getFrameRate(), 5.0);
            assertEquals(f.getEndian(), Format.NOT_SPECIFIED);
            assertEquals(f.getSigned(), Format.NOT_SPECIFIED);
            assertEquals(f.getDataType(), Format.byteArray);
        }

    }

}
