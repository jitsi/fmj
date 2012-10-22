package net.sf.fmj.test.compat.sun;

import java.awt.*;

import javax.media.*;

import junit.framework.*;

import com.sun.media.format.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class AviVideoFormatTest extends TestCase
{
    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    public void testClone()
    {
        {
            byte[] codecHeader = new byte[] { 0x10, 0x11 };
            assertEquals(
                    new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                            Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7,
                            codecHeader).clone(), new AviVideoFormat("xyz",
                            new Dimension(1, 2), 2000, Format.byteArray, 2.f,
                            1, 2, 3, 4, 5, 6, 7, codecHeader));
        }

        {
            byte[] codecHeader1 = new byte[] { 0x10, 0x11 };
            byte[] codecHeader2 = new byte[] { 0x10, 0x11 };
            assertFalse(codecHeader1 == codecHeader2);
            assertEquals(
                    new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                            Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7,
                            codecHeader1).clone(), new AviVideoFormat("xyz",
                            new Dimension(1, 2), 2000, Format.byteArray, 2.f,
                            1, 2, 3, 4, 5, 6, 7, codecHeader2));
        }

        {
            byte[] codecHeader = new byte[] { 0x10, 0x11 };
            AviVideoFormat clone = (AviVideoFormat) new AviVideoFormat("xyz",
                    new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2, 3,
                    4, 5, 6, 7, codecHeader).clone();
            assertTrue(clone.getCodecSpecificHeader() == codecHeader);

        }
    }

    public void testConstructors()
    {
        assertEquals(new AviVideoFormat("abc").getBitsPerPixel(), -1);
        assertEquals(new AviVideoFormat("abc").getEncoding(), "abc");
        assertEquals(new AviVideoFormat("abc").getPlanes(), -1);
        assertEquals(new AviVideoFormat("abc").getBitsPerPixel(), -1);
        assertEquals(new AviVideoFormat("abc").getImageSize(), -1);
        assertEquals(new AviVideoFormat("abc").getXPelsPerMeter(), -1);
        assertEquals(new AviVideoFormat("abc").getYPelsPerMeter(), -1);
        assertEquals(new AviVideoFormat("abc").getClrUsed(), -1);
        assertEquals(new AviVideoFormat("abc").getClrImportant(), -1);
        assertTrue(new AviVideoFormat("abc").getCodecSpecificHeader() == null);

        {
            // String encoding, Dimension size, int maxDataLength, Class
            // dataType, float frameRate,
            // int planes, int bitsPerPixel, int imageSize, int xPelsPerMeter,
            // int yPelsPerMeter, int clrUsed, int clrImportant, byte[]
            // codecHeader)

            byte[] codecHeader = new byte[] { 0x10, 0x11 };
            final AviVideoFormat f2 = new AviVideoFormat("xyz", new Dimension(
                    1, 2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7,
                    codecHeader);
            assertEquals(f2.getEncoding(), "xyz");
            assertEquals(f2.getSize(), new Dimension(1, 2));
            assertEquals(f2.getMaxDataLength(), 2000);
            assertEquals(f2.getDataType(), byte[].class);
            assertEquals(f2.getFrameRate(), 2.f);
            assertEquals(f2.getPlanes(), 1);
            assertEquals(f2.getBitsPerPixel(), 2);
            assertEquals(f2.getImageSize(), 3);
            assertEquals(f2.getXPelsPerMeter(), 4);
            assertEquals(f2.getYPelsPerMeter(), 5);
            assertEquals(f2.getClrUsed(), 6);
            assertEquals(f2.getClrImportant(), 7);
            assertTrue(f2.getCodecSpecificHeader() == codecHeader);

            final AviVideoFormat f3 = (AviVideoFormat) f2.relax();

            assertEquals(f3.getEncoding(), "xyz");
            assertTrue(f3.getSize() == null);
            assertEquals(f3.getMaxDataLength(), -1);
            assertEquals(f3.getDataType(), byte[].class);
            assertEquals(f3.getFrameRate(), -1.f);
            assertEquals(f3.getPlanes(), 1);
            assertEquals(f3.getBitsPerPixel(), 2);
            assertEquals(f3.getImageSize(), -1);
            assertEquals(f3.getXPelsPerMeter(), 4);
            assertEquals(f3.getYPelsPerMeter(), 5);
            assertEquals(f3.getClrUsed(), 6);
            assertEquals(f3.getClrImportant(), 7);
            assertTrue(f3.getCodecSpecificHeader() == codecHeader);

        }
    }

    public void testIntersects()
    {
        byte[] codecHeader = new byte[] { 0x10, 0x11 };
        byte[] codecHeader2 = new byte[] { 0x10, 0x05 };
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat(null,
                                new Dimension(1, 2), 2000, Format.byteArray,
                                2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)),
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("abc", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, 7, codecHeader)), new AviVideoFormat("abc",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", null, 2000,
                                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7,
                                codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                3), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), -1, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 3000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), -1,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, null, 2.f, 1, 2, 3, 4, 5, 6, 7,
                                codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000, null, 2.f,
                        1, 2, 3, 4, 5, 6, 7, codecHeader).intersects(new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)),
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.intArray, 2.f, 1, 2, 3, 4, 5,
                                6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.intArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, -1.f, 1, 2, 3, 4,
                                5, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, -1.f, 1, 2, 3, 4, 5, 6, 7,
                        codecHeader).intersects(new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 3.f, 1, 2, 3, 4, 5,
                                6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, -1, 2, 3, 4,
                                5, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, -1, 2, 3, 4, 5, 6, 7,
                        codecHeader).intersects(new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 2, 2, 3, 4, 5,
                                6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, -1, 3, 4,
                                5, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, -1, 3, 4, 5, 6, 7,
                        codecHeader).intersects(new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 12, 3, 4,
                                5, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, -1, 4,
                                5, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, -1, 4, 5, 6, 7,
                        codecHeader).intersects(new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 13, 4,
                                5, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, -1,
                                5, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, -1, 5, 6, 7,
                        codecHeader).intersects(new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 14,
                                5, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4,
                                -1, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, -1, 6, 7,
                        codecHeader).intersects(new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4,
                                15, 6, 7, codecHeader)), new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                -1, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, -1, 7,
                        codecHeader).intersects(new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                16, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, -1, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, -1,
                        codecHeader).intersects(new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, 8, codecHeader)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, 7, null)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, null).intersects(new AviVideoFormat(
                        "xyz", new Dimension(1, 2), 2000, Format.byteArray,
                        2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)),
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader));
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .intersects(new AviVideoFormat("xyz", new Dimension(1,
                                2), 2000, Format.byteArray, 2.f, 1, 2, 3, 4, 5,
                                6, 7, codecHeader2)), new AviVideoFormat("xyz",
                        new Dimension(1, 2), 2000, Format.byteArray, 2.f, 1, 2,
                        3, 4, 5, 6, 7, codecHeader));

    }

    public void testMatches()
    {
        byte[] codecHeader = new byte[] { 0x10, 0x11 };
        byte[] codecHeader2 = new byte[] { 0x10, 0x11 };
        byte[] codecHeader3 = new byte[] { 0x10, 0x11, 0x05 };
        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xya", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 3), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 3000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.intArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 3.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 11, 2, 3, 4, 5, 6, 7,
                        codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 12, 3, 4, 5, 6, 7,
                        codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 13, 4, 5, 6, 7,
                        codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 14, 5, 6, 7,
                        codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 15, 6, 7,
                        codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 16, 7,
                        codecHeader)));

        assertFalse(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 17,
                        codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7,
                        codecHeader2)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7,
                        codecHeader3)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat(null, new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", null, 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), -1,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        null, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, -1.f, 1, 2, 3, 4, 5, 6, 7,
                        codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, -1, 2, 3, 4, 5, 6, 7,
                        codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, -1, 3, 4, 5, 6, 7,
                        codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, -1, 4, 5, 6, 7,
                        codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, -1, 5, 6, 7,
                        codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, -1, 6, 7,
                        codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, -1, 7,
                        codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, -1,
                        codecHeader)));

        assertTrue(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                .matches(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, null)));

    }

    public void testToString()
    {
        byte[] codecHeader = new byte[] { 0x10, 0x11 };
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, codecHeader)
                        .toString(),
                "XYZ, 1x2, FrameRate=2.0, Length=2000 2 extra bytes");

        byte[] codecHeader2 = new byte[] { 0x10, 0x11, 0x01 };
        assertEquals(
                new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                        Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7,
                        codecHeader2).toString(),
                "XYZ, 1x2, FrameRate=2.0, Length=2000 3 extra bytes");

        assertEquals(new AviVideoFormat("xyz", new Dimension(1, 2), 2000,
                Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, null).toString(),
                "XYZ, 1x2, FrameRate=2.0, Length=2000 0 extra bytes");

    }

}
