package net.sf.fmj.test.compat.sun;

import java.awt.*;

import javax.media.format.*;

import junit.framework.*;

import com.sun.media.vfw.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class BitMapInfoTest extends TestCase
{
    // TODO: test all video formats:
    // H261Format
    // H263Format
    // IndexedColorFormat
    // JPEGFormat
    // YUVFOrmat

    private static void dump(BitMapInfo b)
    {
        // System.out.println(b);

        System.out.println("[biBitCount=" + b.biBitCount);
        System.out.println("biWidth=" + b.biWidth);
        System.out.println("biHeight=" + b.biHeight);
        System.out.println("biPlanes=" + b.biPlanes);
        System.out.println("biSizeImage=" + b.biSizeImage);
        System.out.println("fourcc=" + b.fourcc);
        System.out.println("biXPelsPerMeter=" + b.biXPelsPerMeter);
        System.out.println("biYPelsPerMeter=" + b.biYPelsPerMeter);
        System.out.println("biClrUsed=" + b.biClrUsed);
        System.out.println("biClrImportant=" + b.biClrImportant);
        System.out.println("extraSize=" + b.extraSize);
        System.out.println("extraBytes=" + b.extraBytes + "]");

    }

    private void assertEquals(float f1, float f2)
    {
        assertTrue(f1 == f2);
    }

    void test(BitMapInfo b)
    {
        Class[] classes = new Class[] { byte[].class, short[].class,
                int[].class };
        float[] frameRates = new float[] { -2.f, -1.f, 0.f };
        for (int i = 0; i < classes.length; ++i)
            for (int j = 0; j < frameRates.length; ++j)
            {
                // {
                // final VideoFormat f;
                //
                // if (j == 0)
                // f = b.createVideoFormat(classes[i]);
                // else
                // f = b.createVideoFormat(classes[i], frameRates[j]);
                //
                // BitMapInfo b2 = new BitMapInfo(f);
                //
                // assertEquals(b2.biBitCount, b.biBitCount);
                // assertEquals(b2.biWidth, b.biWidth);
                // assertEquals(b2.biHeight, b.biHeight);
                // assertEquals(b2.biPlanes, b.biPlanes);
                // //assertEquals(b2.biSizeImage, b.biSizeImage);
                // assertEquals(b2.fourcc, b.fourcc);
                // assertEquals(b2.biXPelsPerMeter, 0);
                // assertEquals(b2.biYPelsPerMeter, 0);
                // assertEquals(b2.biClrUsed, b.biClrUsed);
                // assertEquals(b2.biClrImportant, b.biClrImportant);
                // assertEquals(b2.extraSize, 0);
                // assertEquals(b2.extraBytes, null);
                //
                // }

                if (b.fourcc.equals("RGB"))
                {
                    final RGBFormat f;

                    if (b.biWidth == 0)
                        continue;
                    if (b.biHeight == 0)
                        continue;

                    try
                    {
                        if (j == 0)
                            f = (RGBFormat) b.createVideoFormat(classes[i]);
                        else
                            f = (RGBFormat) b.createVideoFormat(classes[i],
                                    frameRates[j]);
                    } catch (ArithmeticException e)
                    {
                        e.printStackTrace();
                        continue;
                    }
                    assertEquals(f.getEncoding(), b.fourcc.toLowerCase());
                    assertEquals(f.getBitsPerPixel(), b.biBitCount);
                    assertEquals(f.getDataType(), classes[i]);
                    assertEquals(f.getFrameRate(), j == 0 ? -1.f
                            : frameRates[j]);
                    // assertEquals(f.getMaxDataLength(), -2); // TODO:
                    // sometimes -1, sometimes -2
                    assertEquals(f.getSize(), new Dimension(b.biWidth,
                            b.biHeight));

                    if (f.getBitsPerPixel() == 24)
                    {
                        assertEquals(f.getBlueMask(), 1);
                        assertEquals(f.getGreenMask(), 2);
                        assertEquals(f.getRedMask(), 3);
                        if (classes[i] == byte[].class)
                        {
                            assertEquals(f.getPixelStride(), 3);
                            assertEquals(f.getLineStride(), f.getPixelStride()
                                    * b.biWidth);

                            assertEquals(f.getMaxDataLength(), b.biSizeImage);

                        } else if (classes[i] == short[].class)
                        {
                            assertEquals(f.getPixelStride(), 1);
                            // if (f.getLineStride() != b.biWidth)
                            // System.out.println(b.biWidth);
                            // assertEquals(f.getLineStride(), b.biWidth); // 1,
                            // 2, or 3...
                            // assertEquals(f.getLineStride(),
                            // f.getPixelStride() * b.biWidth); // TODO

                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 2);
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 3);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 6);
                            else
                                System.out.println(b.biSizeImage);

                        } else if (classes[i] == int[].class)
                        {
                            assertEquals(f.getPixelStride(), 0);
                            // assertEquals(f.getLineStride(),
                            // f.getPixelStride() * b.biWidth); // TODO
                            // assertEquals(f.getMaxDataLength(), 0);

                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 4);
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 3);
                            else
                                System.out.println(b.biSizeImage);

                        }

                    } else if (f.getBitsPerPixel() == 32)
                    {
                        if (classes[i] == byte[].class)
                        {
                            assertEquals(f.getBlueMask(), 1);
                            assertEquals(f.getGreenMask(), 2);
                            assertEquals(f.getRedMask(), 3);

                            assertEquals(f.getPixelStride(), 4);
                            assertEquals(f.getLineStride(), f.getPixelStride()
                                    * b.biWidth);

                            assertEquals(f.getMaxDataLength(), b.biSizeImage);

                        } else if (classes[i] == int[].class)
                        {
                            assertEquals(f.getBlueMask(), 0xFF);
                            assertEquals(f.getGreenMask(), 0xFF00);
                            assertEquals(f.getRedMask(), 0xFF0000);

                            assertEquals(f.getPixelStride(), 1);
                            assertEquals(f.getLineStride(), f.getPixelStride()
                                    * b.biWidth);

                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 4);

                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 3);
                            else
                                System.out.println(b.biSizeImage);

                        } else if (classes[i] == short[].class)
                        {
                            assertEquals(f.getBlueMask(), 1);
                            assertEquals(f.getGreenMask(), 2);
                            assertEquals(f.getRedMask(), 3);
                            assertEquals(f.getPixelStride(), 2);
                            assertEquals(f.getLineStride(), f.getPixelStride()
                                    * b.biWidth);

                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 2);
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 3);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 6);
                            else
                                System.out.println(b.biSizeImage);
                        }
                    } else if (f.getBitsPerPixel() == 16)
                    {
                        assertEquals(f.getBlueMask(), 31);
                        assertEquals(f.getGreenMask(), 992);
                        assertEquals(f.getRedMask(), 31744);

                        if (classes[i] == byte[].class)
                        {
                            assertEquals(f.getPixelStride(), 2);

                            assertEquals(f.getMaxDataLength(), b.biSizeImage);
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 2);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 3);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 6);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 12);
                            else
                                System.out.println(b.biSizeImage);

                        } else if (classes[i] == short[].class)
                        {
                            assertEquals(f.getPixelStride(), 1);

                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 2);
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 3);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 6);
                            else
                                System.out.println(b.biSizeImage);

                        } else if (classes[i] == int[].class)
                        {
                            assertEquals(f.getPixelStride(), 0);

                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 4);
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 3);
                            else
                                System.out.println(b.biSizeImage);
                        }

                    } else if (f.getBitsPerPixel() == 8
                            || f.getBitsPerPixel() == 12)
                    {
                        assertEquals(f.getBlueMask(), -1);
                        assertEquals(f.getGreenMask(), -1);
                        assertEquals(f.getRedMask(), -1);

                        if (classes[i] == byte[].class)
                        {
                            assertEquals(f.getPixelStride(), 1);

                            assertEquals(f.getMaxDataLength(), b.biSizeImage);
                            // same as 16-bit
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 2)
                            { // dump(b);
                                assertEquals(f.getMaxDataLength(), 2);
                            } else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 3);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 6);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 12);
                            else
                                System.out.println(b.biSizeImage);
                        } else if (classes[i] == short[].class)
                        {
                            assertEquals(f.getPixelStride(), 0);

                            // same as 16-bit: ??

                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 2);
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 3);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 6);
                            else
                                System.out.println(b.biSizeImage);

                        } else if (classes[i] == int[].class)
                        {
                            assertEquals(f.getPixelStride(), 0);

                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 4);
                            // same as 16-bit:
                            if (b.biSizeImage == 0)
                            {
                                // dump(b);
                                assertEquals(f.getMaxDataLength(), 0);
                            } else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 2)
                            {
                                // dump(b);
                                assertEquals(f.getMaxDataLength(), 0);
                            } else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 3);
                            else
                                System.out.println(b.biSizeImage);
                        }

                    } else
                    {
                        assertEquals(f.getBlueMask(), -1);
                        assertEquals(f.getGreenMask(), -1);
                        assertEquals(f.getRedMask(), -1);

                        assertEquals(f.getPixelStride(), 0); // TODO: 0 or 1
                        // assertEquals(f.getMaxDataLength(), -2); // TODO: -1
                        // or -2

                        if (classes[i] == byte[].class)
                        {
                            assertEquals(f.getMaxDataLength(), b.biSizeImage);

                            // same as 16-bit, except -2 case is new
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 2);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 3);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 6);
                            else if (b.biSizeImage == -2)
                                assertEquals(f.getMaxDataLength(), -2);
                            else
                                System.out.println(b.biSizeImage);
                        } else if (classes[i] == short[].class)
                        {
                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 2);
                            // same as 16-bit:
                            assertEquals(f.getMaxDataLength(), -1);
                        } else if (classes[i] == int[].class)
                        {
                            assertEquals(f.getMaxDataLength(),
                                    b.biSizeImage / 4);

                            // same as 16-bit:, except -2 case is new
                            if (b.biSizeImage == 0)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 1)
                                assertEquals(f.getMaxDataLength(), 0);
                            else if (b.biSizeImage == 2)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 3)
                                assertEquals(f.getMaxDataLength(), 1);
                            else if (b.biSizeImage == 6)
                                assertEquals(f.getMaxDataLength(), 3);
                            else if (b.biSizeImage == 12)
                                assertEquals(f.getMaxDataLength(), 6);
                            else if (b.biSizeImage == -2)
                                assertEquals(f.getMaxDataLength(), 0);
                            else
                                System.out.println("X" + b.biSizeImage);
                        }

                    }
                    assertEquals(f.getEndian(), 1);
                    assertEquals(f.getFlipped(), 1);
                    // assertEquals(f.getLineStride(), -40); // TODO: varies,
                    // sometimes -40, sometimes -20

                } else if (b.fourcc.equals("YV12"))
                {
                    final YUVFormat f;
                    if (j == 0)
                        f = (YUVFormat) b.createVideoFormat(classes[i]);
                    else
                        f = (YUVFormat) b.createVideoFormat(classes[i],
                                frameRates[j]);

                    assertEquals(f.getEncoding(), "yuv");
                    assertEquals(f.getYuvType(), YUVFormat.YUV_420);
                    assertEquals(f.getDataType(), byte[].class);
                    assertEquals(f.getFrameRate(), j == 0 ? -1.f
                            : frameRates[j]);
                    assertEquals(f.getSize(), new Dimension(b.biWidth,
                            b.biHeight));

                    assertEquals(f.getMaxDataLength(), b.biSizeImage); // sometimes
                                                                       // 0
                    assertEquals(f.getOffsetY(), 0);
                    assertEquals(f.getOffsetU(), b.biWidth * b.biHeight
                            + (b.biWidth * b.biHeight) / 4); // empirically
                                                             // determined
                    // 320x240: 96000
                    // 96000 - 76800 = 19200. 96000 / 76800 = 1.25
                    //
                    assertEquals(f.getOffsetV(), b.biWidth * b.biHeight);

                    assertEquals(f.getStrideUV(), b.biWidth / 2);
                    assertEquals(f.getStrideY(), b.biWidth);

                } else
                {
                    final com.sun.media.format.AviVideoFormat f;
                    if (j == 0)
                        f = (com.sun.media.format.AviVideoFormat) b
                                .createVideoFormat(classes[i]);
                    else
                        f = (com.sun.media.format.AviVideoFormat) b
                                .createVideoFormat(classes[i], frameRates[j]);
                    assertEquals(f.getEncoding(), b.fourcc);
                    assertEquals(f.getBitsPerPixel(), b.biBitCount);
                    assertEquals(f.getClrImportant(), b.biClrImportant);
                    assertEquals(f.getClrUsed(), b.biClrUsed);
                    assertEquals(f.getCodecSpecificHeader(), b.extraBytes);
                    assertEquals(f.getDataType(), classes[i]);
                    assertEquals(f.getFrameRate(), j == 0 ? -1.f
                            : frameRates[j]);
                    assertEquals(f.getImageSize(), b.biSizeImage);
                    assertEquals(f.getMaxDataLength(), b.biSizeImage);
                    assertEquals(f.getPlanes(), b.biPlanes);
                    assertEquals(f.getSize(), new Dimension(b.biWidth,
                            b.biHeight));
                    assertEquals(f.getXPelsPerMeter(), b.biXPelsPerMeter);
                    assertEquals(f.getYPelsPerMeter(), b.biYPelsPerMeter);
                }
            }

    }

    // public void testBitMapInfo5()
    // {
    // {
    // BitMapInfo b = new BitMapInfo(new IndexedColorFormat());
    // assertEquals(b.biBitCount, 24);
    // assertEquals(b.biWidth, 320);
    // assertEquals(b.biHeight, 240);
    // assertEquals(b.biPlanes, 1);
    // assertEquals(b.biSizeImage, -1);
    // assertEquals(b.fourcc, "h263");
    // assertEquals(b.biXPelsPerMeter, 0);
    // assertEquals(b.biYPelsPerMeter, 0);
    // assertEquals(b.biClrUsed, 0);
    // assertEquals(b.biClrImportant, 0);
    // assertEquals(b.extraSize, 0);
    // assertEquals(b.extraBytes, null);
    // test(b);
    //
    // }
    //
    //
    // }

    public void testBitMapInfo1()
    {
        {
            BitMapInfo b = new BitMapInfo();
            assertEquals(b.biBitCount, 24);
            assertEquals(b.biWidth, 0);
            assertEquals(b.biHeight, 0);
            assertEquals(b.biPlanes, 1);
            assertEquals(b.biSizeImage, 0);
            assertEquals(b.fourcc, "");
            assertEquals(b.biXPelsPerMeter, 0);
            assertEquals(b.biYPelsPerMeter, 0);
            assertEquals(b.biClrUsed, 0);
            assertEquals(b.biClrImportant, 0);
            assertEquals(b.extraSize, 0);
            assertEquals(b.extraBytes, null);
            assertEquals(
                    b.toString(),
                    "Size = 0 x 0	Planes = 1	BitCount = 24	FourCC = 	SizeImage = 0\nClrUsed = 0\nClrImportant = 0\nExtraSize = 0\n");
            test(b);

            final String[] fourccs = new String[] { "", "xyzq", "RGB", "YV12" };
            final byte[][] extras = new byte[][] { null, new byte[] { 11, 12 } };
            final int[] bitcounts = new int[] { 8, 12, 16, 24, 32 };// , 16, 24,
                                                                    // 32};
            final int[] sizes = new int[] { 0, 1, 2, 3, 6, 12 };

            for (int k = 0; k < bitcounts.length; ++k)
                for (b.biWidth = 0; b.biWidth < 3; ++b.biWidth)
                    for (b.biHeight = 0; b.biHeight < 3; ++b.biHeight)
                        for (b.biPlanes = 0; b.biPlanes <= 2; ++b.biPlanes)
                            // for (b.biSizeImage = 0; b.biSizeImage < 3;
                            // ++b.biSizeImage)
                            for (int m = 0; m < sizes.length; ++m)
                                for (int j = 0; j < extras.length; ++j)
                                    for (b.biXPelsPerMeter = 0; b.biXPelsPerMeter < 2; ++b.biXPelsPerMeter)
                                        for (b.biYPelsPerMeter = 0; b.biYPelsPerMeter < 2; ++b.biYPelsPerMeter)
                                            for (b.biClrUsed = 0; b.biClrUsed < 2; ++b.biClrUsed)
                                                for (b.biClrImportant = 0; b.biClrImportant < 3; ++b.biClrImportant)
                                                    for (b.extraSize = 0; b.extraSize < 2; ++b.extraSize)
                                                        for (int i = 0; i < fourccs.length; ++i)
                                                        {
                                                            b.biBitCount = bitcounts[k];
                                                            b.fourcc = fourccs[i];
                                                            b.extraBytes = extras[j];
                                                            b.biSizeImage = sizes[m];

                                                            test(b);
                                                            {
                                                                BitMapInfo b2 = new BitMapInfo(
                                                                        b.fourcc,
                                                                        b.biWidth,
                                                                        b.biHeight);
                                                                assertEquals(
                                                                        b2.biBitCount,
                                                                        24);
                                                                assertEquals(
                                                                        b2.biWidth,
                                                                        b.biWidth);
                                                                assertEquals(
                                                                        b2.biHeight,
                                                                        b.biHeight);
                                                                assertEquals(
                                                                        b2.biPlanes,
                                                                        1);
                                                                if (b2.fourcc
                                                                        .equals("RGB"))
                                                                    assertEquals(
                                                                            b2.biSizeImage,
                                                                            b2.biWidth
                                                                                    * b2.biHeight
                                                                                    * (b2.biBitCount / 8)); // TODO
                                                                else
                                                                    assertEquals(
                                                                            b2.biSizeImage,
                                                                            0);
                                                                assertEquals(
                                                                        b2.fourcc,
                                                                        b.fourcc);
                                                                assertEquals(
                                                                        b2.biXPelsPerMeter,
                                                                        0);
                                                                assertEquals(
                                                                        b2.biYPelsPerMeter,
                                                                        0);
                                                                assertEquals(
                                                                        b2.biClrUsed,
                                                                        0);
                                                                assertEquals(
                                                                        b2.biClrImportant,
                                                                        0);
                                                                assertEquals(
                                                                        b2.extraSize,
                                                                        0);
                                                                assertEquals(
                                                                        b2.extraBytes,
                                                                        null);
                                                                test(b2);
                                                            }
                                                            {
                                                                BitMapInfo b2 = new BitMapInfo(
                                                                        b.fourcc,
                                                                        b.biWidth,
                                                                        b.biHeight,
                                                                        b.biPlanes,
                                                                        b.biBitCount,
                                                                        b.biSizeImage,
                                                                        b.biClrUsed,
                                                                        b.biClrImportant);
                                                                assertEquals(
                                                                        b2.biBitCount,
                                                                        b.biBitCount);
                                                                assertEquals(
                                                                        b2.biWidth,
                                                                        b.biWidth);
                                                                assertEquals(
                                                                        b2.biHeight,
                                                                        b.biHeight);
                                                                assertEquals(
                                                                        b2.biPlanes,
                                                                        b.biPlanes);
                                                                assertEquals(
                                                                        b2.biSizeImage,
                                                                        b.biSizeImage);
                                                                assertEquals(
                                                                        b2.fourcc,
                                                                        b.fourcc);
                                                                assertEquals(
                                                                        b2.biXPelsPerMeter,
                                                                        0);
                                                                assertEquals(
                                                                        b2.biYPelsPerMeter,
                                                                        0);
                                                                assertEquals(
                                                                        b2.biClrUsed,
                                                                        b.biClrUsed);
                                                                assertEquals(
                                                                        b2.biClrImportant,
                                                                        b.biClrImportant);
                                                                assertEquals(
                                                                        b2.extraSize,
                                                                        0);
                                                                assertEquals(
                                                                        b2.extraBytes,
                                                                        null);
                                                                test(b2);
                                                            }

                                                        }

        }
    }

    public void testBitMapInfo2()
    {
        {
            BitMapInfo b = new BitMapInfo(new RGBFormat());
            assertEquals(b.biBitCount, -1);
            assertEquals(b.biWidth, 320);
            assertEquals(b.biHeight, 240);
            assertEquals(b.biPlanes, 1);
            assertEquals(b.biSizeImage, -2);
            assertEquals(b.fourcc, "RGB");
            assertEquals(b.biXPelsPerMeter, 0);
            assertEquals(b.biYPelsPerMeter, 0);
            assertEquals(b.biClrUsed, 0);
            assertEquals(b.biClrImportant, 0);
            assertEquals(b.extraSize, 0);
            assertEquals(b.extraBytes, null);
            assertEquals(
                    b.toString(),
                    "Size = 320 x 240	Planes = 1	BitCount = -1	FourCC = RGB	SizeImage = -2\nClrUsed = 0\nClrImportant = 0\nExtraSize = 0\n");
            test(b);

        }

    }

    public void testBitMapInfo3()
    {
        {
            BitMapInfo b = new BitMapInfo(new H261Format());
            assertEquals(b.biBitCount, 24);
            assertEquals(b.biWidth, 320);
            assertEquals(b.biHeight, 240);
            assertEquals(b.biPlanes, 1);
            assertEquals(b.biSizeImage, -1);
            assertEquals(b.fourcc, "h261");
            assertEquals(b.biXPelsPerMeter, 0);
            assertEquals(b.biYPelsPerMeter, 0);
            assertEquals(b.biClrUsed, 0);
            assertEquals(b.biClrImportant, 0);
            assertEquals(b.extraSize, 0);
            assertEquals(b.extraBytes, null);
            test(b);

        }

    }

    public void testBitMapInfo4()
    {
        {
            BitMapInfo b = new BitMapInfo(new H263Format());
            assertEquals(b.biBitCount, 24);
            assertEquals(b.biWidth, 320);
            assertEquals(b.biHeight, 240);
            assertEquals(b.biPlanes, 1);
            assertEquals(b.biSizeImage, -1);
            assertEquals(b.fourcc, "h263");
            assertEquals(b.biXPelsPerMeter, 0);
            assertEquals(b.biYPelsPerMeter, 0);
            assertEquals(b.biClrUsed, 0);
            assertEquals(b.biClrImportant, 0);
            assertEquals(b.extraSize, 0);
            assertEquals(b.extraBytes, null);
            test(b);

        }

    }

    public void testBitMapInfo6()
    {
        {
            BitMapInfo b = new BitMapInfo(new JPEGFormat());
            assertEquals(b.biBitCount, 24);
            assertEquals(b.biWidth, 320);
            assertEquals(b.biHeight, 240);
            assertEquals(b.biPlanes, 1);
            assertEquals(b.biSizeImage, -1);
            assertEquals(b.fourcc, "jpeg");
            assertEquals(b.biXPelsPerMeter, 0);
            assertEquals(b.biYPelsPerMeter, 0);
            assertEquals(b.biClrUsed, 0);
            assertEquals(b.biClrImportant, 0);
            assertEquals(b.extraSize, 0);
            assertEquals(b.extraBytes, null);
            test(b);

        }

    }

    public void testBitMapInfo7()
    {
        {
            BitMapInfo b = new BitMapInfo(new YUVFormat());
            assertEquals(b.biBitCount, 24);
            assertEquals(b.biWidth, 320);
            assertEquals(b.biHeight, 240);
            assertEquals(b.biPlanes, 1);
            assertEquals(b.biSizeImage, -1);
            assertEquals(b.fourcc, "yuv");
            assertEquals(b.biXPelsPerMeter, 0);
            assertEquals(b.biYPelsPerMeter, 0);
            assertEquals(b.biClrUsed, 0);
            assertEquals(b.biClrImportant, 0);
            assertEquals(b.extraSize, 0);
            assertEquals(b.extraBytes, null);
            test(b);

        }

        int[] yuvTypes = new int[] { YUVFormat.YUV_411, YUVFormat.YUV_420,
                YUVFormat.YUV_422, YUVFormat.YUV_111, YUVFormat.YUV_YVU9,
                YUVFormat.YUV_YUYV, YUVFormat.YUV_SIGNED };
        for (int i = 0; i < yuvTypes.length; ++i)
        {
            final int yuvType = yuvTypes[i];
            // System.out.println(yuvType);

            // TODO: test other constructors
            // TODO: how to get I420?

            YUVFormat f = new YUVFormat(yuvType);
            // System.out.println(f.getEncoding());
            BitMapInfo b = new BitMapInfo(f);
            if (yuvType == YUVFormat.YUV_411)
            {
                assertEquals(b.biBitCount, 24);
                assertEquals(b.fourcc, "yuv");
            } else if (yuvType == YUVFormat.YUV_420)
            {
                assertEquals(b.biBitCount, 12);
                assertEquals(b.fourcc, "YV12");
            } else if (yuvType == YUVFormat.YUV_422)
            {
                assertEquals(b.biBitCount, 24);
                assertEquals(b.fourcc, "yuv");
            } else if (yuvType == YUVFormat.YUV_111)
            {
                assertEquals(b.biBitCount, 24);
                assertEquals(b.fourcc, "yuv");
            } else if (yuvType == YUVFormat.YUV_YVU9)
            {
                assertEquals(b.biBitCount, 24);
                assertEquals(b.fourcc, "yuv");
            } else if (yuvType == YUVFormat.YUV_YUYV)
            {
                assertEquals(b.biBitCount, 24);
                assertEquals(b.fourcc, "yuv");
            } else if (yuvType == YUVFormat.YUV_SIGNED)
            {
                assertEquals(b.biBitCount, 24);
                assertEquals(b.fourcc, "yuv");
            }
            assertEquals(b.biWidth, 320);
            assertEquals(b.biHeight, 240);
            assertEquals(b.biPlanes, 1);
            assertEquals(b.biSizeImage, -1);

            assertEquals(b.biXPelsPerMeter, 0);
            assertEquals(b.biYPelsPerMeter, 0);
            assertEquals(b.biClrUsed, 0);
            assertEquals(b.biClrImportant, 0);
            assertEquals(b.extraSize, 0);
            assertEquals(b.extraBytes, null);
            test(b);

        }

    }

    public void testBitMapInfo8()
    {
        {
            BitMapInfo b = new BitMapInfo(new VideoFormat("xyz"));
            assertEquals(b.biBitCount, 24);
            assertEquals(b.biWidth, 320);
            assertEquals(b.biHeight, 240);
            assertEquals(b.biPlanes, 1);
            assertEquals(b.biSizeImage, -1);
            assertEquals(b.fourcc, "xyz");
            assertEquals(b.biXPelsPerMeter, 0);
            assertEquals(b.biYPelsPerMeter, 0);
            assertEquals(b.biClrUsed, 0);
            assertEquals(b.biClrImportant, 0);
            assertEquals(b.extraSize, 0);
            assertEquals(b.extraBytes, null);
            test(b);

        }

    }

    void testRGBFormat(BitMapInfo b)
    {
        Class[] classes = new Class[] { byte[].class, short[].class,
                int[].class };
        float[] frameRates = new float[] { -2.f, -1.f, 0.f };
        for (int i = 0; i < classes.length; ++i)
            for (int j = 0; j < frameRates.length; ++j)
            {
                final RGBFormat f;
                if (j == 0)
                    f = (RGBFormat) b.createVideoFormat(classes[i]);
                else
                    f = (RGBFormat) b.createVideoFormat(classes[i],
                            frameRates[j]);
                assertEquals(f.getEncoding(), b.fourcc.toLowerCase());
                assertEquals(f.getBitsPerPixel(), b.biBitCount);
                assertEquals(f.getDataType(), classes[i]);
                assertEquals(f.getFrameRate(), j == 0 ? -1.f : frameRates[j]);
                // assertEquals(f.getMaxDataLength(), -2); // TODO: sometimes
                // -1, sometimes -2
                assertEquals(f.getSize(), new Dimension(b.biWidth, b.biHeight));
            }

    }
}
