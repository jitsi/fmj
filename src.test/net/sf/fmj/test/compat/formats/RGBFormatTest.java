package net.sf.fmj.test.compat.formats;

import java.awt.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class RGBFormatTest extends TestCase
{
    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    public void testClone()
    {
        assertEquals(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                0xff00, 0xff0000, 1, -1, 0, -1).clone(), new RGBFormat(null,
                -1, Format.intArray, -1.0f, 32, 0xff, 0xff00, 0xff0000, 1, -1,
                0, -1));

    }

    public void testConstructors()
    {
        assertEquals(new RGBFormat().getBitsPerPixel(), -1);
        assertEquals(new RGBFormat().getEncoding(), "rgb");
        assertEquals(new RGBFormat().getBlueMask(), -1);
        assertEquals(new RGBFormat().getDataType(), null);
        assertEquals(new RGBFormat().getEndian(), -1);
        assertEquals(new RGBFormat().getFlipped(), -1);
        assertEquals(new RGBFormat().getFrameRate(), -1.f);
        assertEquals(new RGBFormat().getGreenMask(), -1);
        assertEquals(new RGBFormat().getLineStride(), -1);
        assertEquals(new RGBFormat().getMaxDataLength(), -1);
        assertEquals(new RGBFormat().getPixelStride(), -1);
        assertEquals(new RGBFormat().getRedMask(), -1);
        assertEquals(new RGBFormat().getSize(), null);

        {
            final RGBFormat f2 = new RGBFormat(new Dimension(1, 2), 2000,
                    Format.byteArray, 2.f, 1, 2, 3, 4, 5, 6, 7, 8);
            assertEquals(f2.getBitsPerPixel(), 1);
            assertEquals(f2.getEncoding(), "rgb");
            assertEquals(f2.getBlueMask(), 4);
            assertEquals(f2.getDataType(), byte[].class);
            assertEquals(f2.getEndian(), 8);
            assertEquals(f2.getFlipped(), 7);
            assertEquals(f2.getFrameRate(), 2.f);
            assertEquals(f2.getGreenMask(), 3);
            assertEquals(f2.getLineStride(), 6);
            assertEquals(f2.getMaxDataLength(), 2000);
            assertEquals(f2.getPixelStride(), 5);
            assertEquals(f2.getRedMask(), 2);
            assertEquals(f2.getSize(), new Dimension(1, 2));

            final RGBFormat f3 = (RGBFormat) f2.relax();

            assertEquals(f3.getBitsPerPixel(), 1);
            assertEquals(f3.getEncoding(), "rgb");
            assertEquals(f3.getBlueMask(), 4);
            assertEquals(f3.getDataType(), byte[].class);
            assertEquals(f3.getEndian(), 8);
            assertEquals(f3.getFlipped(), 7);
            assertEquals(f3.getFrameRate(), -1.f);
            assertEquals(f3.getGreenMask(), 3);
            assertEquals(f3.getLineStride(), -1);
            assertEquals(f3.getMaxDataLength(), -1);
            assertEquals(f3.getPixelStride(), -1);
            assertEquals(f3.getRedMask(), 2);
            assertEquals(f3.getSize(), null);

        }

        {
            final Dimension d = new Dimension(1, 2);
            final RGBFormat f2 = new RGBFormat(d, 2000, Format.byteArray, 2.f,
                    1, 2, 3, 4);
            assertEquals(f2.getBitsPerPixel(), 1);
            assertEquals(f2.getEncoding(), "rgb");
            assertEquals(f2.getBlueMask(), 4);
            assertEquals(f2.getDataType(), byte[].class);
            assertEquals(f2.getEndian(), -1);
            assertEquals(f2.getFlipped(), 0); // strange, this should be -1,
                                              // maybe there is some kind of
                                              // calculation.
            assertEquals(f2.getFrameRate(), 2.f);
            assertEquals(f2.getGreenMask(), 3);
            assertEquals(f2.getLineStride(), 0); // strange, this should be -1,
                                                 // maybe there is some kind of
                                                 // calculation.
            assertEquals(f2.getMaxDataLength(), 2000);
            assertEquals(f2.getPixelStride(), 0); // strange, this should be -1,
                                                  // maybe there is some kind of
                                                  // calculation.
            assertEquals(f2.getRedMask(), 2);
            assertEquals(f2.getSize(), new Dimension(1, 2));
            assertTrue(f2.getSize() != d);
            assertEquals(f2.getSize(), d);

        }

        // we need to vary some of the params of this constructor to see how the
        // others are derived.

        if (true)
        {
            final int[] redMasks = new int[] { -1, 0xff, 0xff00, 0xff0000 };
            final int[] greenMasks = new int[] { -1, 0xff, 0xff00, 0xff0000 };
            final int[] blueMasks = new int[] { -1, 0xff, 0xff00, 0xff0000 };
            final int[] lengths = new int[] { -1, 2000 };
            final Dimension[] sizes = new Dimension[] { null,
                    new Dimension(320, 200), new Dimension(321, 201),
                    new Dimension(322, 202), new Dimension(640, 480) };
            final Class[] dataTypes = new Class[] { null, byte[].class,
                    short[].class, int[].class };
            final float[] frameRates = new float[] { -1.f, 1.f, 2.f };
            final int[] bitsPerPixels = new int[] { -1, 1, 2, 4, 5, 6, 7, 8,
                    10, 12, 16, 24, 32 };

            final Set<Integer> possibleEndians = new HashSet<Integer>();
            possibleEndians.add(new Integer(-1));
            possibleEndians.add(new Integer(1));

            final Set<Integer> possibleFlippeds = new HashSet<Integer>();
            possibleFlippeds.add(new Integer(0));

            final Set<Integer> possibleLineStrides = new HashSet<Integer>();
            possibleLineStrides.add(new Integer(-1));
            possibleLineStrides.add(new Integer(0));
            for (int i = 0; i < sizes.length; ++i)
            {
                final Dimension size = sizes[i];
                if (size == null)
                    continue;
                possibleLineStrides.add(new Integer(size.width));
                possibleLineStrides.add(new Integer(size.width * 2));
                possibleLineStrides.add(new Integer(size.width * 3));
                possibleLineStrides.add(new Integer(size.width * 4));
            }

            final Set<Integer> possiblePixelStrides = new HashSet<Integer>();
            possiblePixelStrides.add(new Integer(-1));
            possiblePixelStrides.add(new Integer(0));
            possiblePixelStrides.add(new Integer(1));
            possiblePixelStrides.add(new Integer(2));
            possiblePixelStrides.add(new Integer(3));
            possiblePixelStrides.add(new Integer(4));

            for (int i1 = 0; i1 < lengths.length; ++i1)
            {
                final int length = lengths[i1];

                for (int i2 = 0; i2 < redMasks.length; ++i2)
                {
                    final int redMask = redMasks[i2];

                    for (int i3 = 0; i3 < greenMasks.length; ++i3)
                    {
                        final int greenMask = greenMasks[i3];

                        for (int i4 = 0; i4 < blueMasks.length; ++i4)
                        {
                            final int blueMask = blueMasks[i4];

                            for (int i5 = 0; i5 < sizes.length; ++i5)
                            {
                                final Dimension size = sizes[i5];

                                for (int i6 = 0; i6 < dataTypes.length; ++i6)
                                {
                                    final Class dataType = dataTypes[i6];

                                    for (int i7 = 0; i7 < frameRates.length; ++i7)
                                    {
                                        final float frameRate = frameRates[i7];

                                        for (int i8 = 0; i8 < bitsPerPixels.length; ++i8)
                                        {
                                            final int bitsPerPixel = bitsPerPixels[i8];

                                            final RGBFormat f2 = new RGBFormat(
                                                    size, length, dataType,
                                                    frameRate, bitsPerPixel,
                                                    redMask, greenMask,
                                                    blueMask);

                                            assertEquals(f2.getBitsPerPixel(),
                                                    bitsPerPixel);
                                            assertEquals(f2.getEncoding(),
                                                    "rgb");
                                            assertEquals(f2.getBlueMask(),
                                                    blueMask);
                                            assertEquals(f2.getDataType(),
                                                    dataType);
                                            assertEquals(f2.getFrameRate(),
                                                    frameRate);
                                            assertEquals(f2.getGreenMask(),
                                                    greenMask);
                                            assertEquals(f2.getMaxDataLength(),
                                                    length);
                                            assertEquals(f2.getRedMask(),
                                                    redMask);
                                            assertEquals(f2.getSize(), size);

                                            // values that seem to vary or are
                                            // unspecified:
                                            if (!possibleEndians
                                                    .contains(new Integer(f2
                                                            .getEndian())))
                                            {
                                                System.err
                                                        .println("getEndian: "
                                                                + f2.getEndian());
                                                // assertTrue(false);
                                            }

                                            if (!possibleFlippeds
                                                    .contains(new Integer(f2
                                                            .getFlipped())))
                                            {
                                                System.err
                                                        .println("getFlipped: "
                                                                + f2.getFlipped());
                                                // assertTrue(false);
                                            }

                                            if (!possibleLineStrides
                                                    .contains(new Integer(f2
                                                            .getLineStride())))
                                            {
                                                System.err
                                                        .println("getLineStride: "
                                                                + f2.getLineStride());
                                                // assertTrue(false);
                                            }

                                            if (!possiblePixelStrides
                                                    .contains(new Integer(f2
                                                            .getPixelStride())))
                                            {
                                                System.err
                                                        .println("getPixelStride: "
                                                                + f2.getPixelStride());
                                                // assertTrue(false);
                                            }

                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

            for (int i1 = 0; i1 < lengths.length; ++i1)
            {
                final int length = lengths[i1];

                for (int i2 = 0; i2 < redMasks.length; ++i2)
                {
                    final int redMask = redMasks[i2];

                    for (int i3 = 0; i3 < greenMasks.length; ++i3)
                    {
                        final int greenMask = greenMasks[i3];

                        for (int i4 = 0; i4 < blueMasks.length; ++i4)
                        {
                            final int blueMask = blueMasks[i4];

                            for (int i5 = 0; i5 < sizes.length; ++i5)
                            {
                                final Dimension size = sizes[i5];

                                for (int i6 = 0; i6 < dataTypes.length; ++i6)
                                {
                                    final Class dataType = dataTypes[i6];

                                    for (int i7 = 0; i7 < frameRates.length; ++i7)
                                    {
                                        final float frameRate = frameRates[i7];

                                        for (int i8 = 0; i8 < bitsPerPixels.length; ++i8)
                                        {
                                            final int bitsPerPixel = bitsPerPixels[i8];
                                            final RGBFormat f2 = new RGBFormat(
                                                    size, length, dataType,
                                                    frameRate, bitsPerPixel,
                                                    redMask, greenMask,
                                                    blueMask);

                                            // see if length affects - DOES
                                            // NOT.:
                                            {
                                                final RGBFormat f1 = new RGBFormat(
                                                        size, -1, dataType,
                                                        frameRate,
                                                        bitsPerPixel, redMask,
                                                        greenMask, blueMask);

                                                assertEquals(
                                                        f2.getBitsPerPixel(),
                                                        f1.getBitsPerPixel());
                                                assertEquals(f2.getEncoding(),
                                                        f1.getEncoding());
                                                assertEquals(f2.getBlueMask(),
                                                        f1.getBlueMask());
                                                assertEquals(f2.getDataType(),
                                                        f1.getDataType());
                                                assertEquals(f2.getFrameRate(),
                                                        f1.getFrameRate());
                                                assertEquals(f2.getGreenMask(),
                                                        f1.getGreenMask());
                                                // assertEquals(f2.getMaxDataLength(),
                                                // f1.getMaxDataLength());
                                                assertEquals(f2.getRedMask(),
                                                        f1.getRedMask());
                                                assertEquals(f2.getSize(),
                                                        f1.getSize());

                                                assertEquals(f2.getEndian(),
                                                        f1.getEndian());
                                                assertEquals(f2.getFlipped(),
                                                        f1.getFlipped());
                                                assertEquals(
                                                        f2.getLineStride(),
                                                        f1.getLineStride());
                                                assertEquals(
                                                        f2.getPixelStride(),
                                                        f1.getPixelStride());

                                            }

                                            // see if redMask affects - DOES
                                            // NOT.:
                                            {
                                                final RGBFormat f1 = new RGBFormat(
                                                        size, length, dataType,
                                                        frameRate,
                                                        bitsPerPixel, -1,
                                                        greenMask, blueMask);

                                                assertEquals(
                                                        f2.getBitsPerPixel(),
                                                        f1.getBitsPerPixel());
                                                assertEquals(f2.getEncoding(),
                                                        f1.getEncoding());
                                                assertEquals(f2.getBlueMask(),
                                                        f1.getBlueMask());
                                                assertEquals(f2.getDataType(),
                                                        f1.getDataType());
                                                assertEquals(f2.getFrameRate(),
                                                        f1.getFrameRate());
                                                assertEquals(f2.getGreenMask(),
                                                        f1.getGreenMask());
                                                assertEquals(
                                                        f2.getMaxDataLength(),
                                                        f1.getMaxDataLength());
                                                // assertEquals(f2.getRedMask(),
                                                // f1.getRedMask());
                                                assertEquals(f2.getSize(),
                                                        f1.getSize());

                                                assertEquals(f2.getEndian(),
                                                        f1.getEndian());
                                                assertEquals(f2.getFlipped(),
                                                        f1.getFlipped());
                                                assertEquals(
                                                        f2.getLineStride(),
                                                        f1.getLineStride());
                                                assertEquals(
                                                        f2.getPixelStride(),
                                                        f1.getPixelStride());

                                            }

                                            // see if greenMask affects - DOES
                                            // NOT.:
                                            {
                                                final RGBFormat f1 = new RGBFormat(
                                                        size, length, dataType,
                                                        frameRate,
                                                        bitsPerPixel, redMask,
                                                        -1, blueMask);

                                                assertEquals(
                                                        f2.getBitsPerPixel(),
                                                        f1.getBitsPerPixel());
                                                assertEquals(f2.getEncoding(),
                                                        f1.getEncoding());
                                                assertEquals(f2.getBlueMask(),
                                                        f1.getBlueMask());
                                                assertEquals(f2.getDataType(),
                                                        f1.getDataType());
                                                assertEquals(f2.getFrameRate(),
                                                        f1.getFrameRate());
                                                // assertEquals(f2.getGreenMask(),
                                                // f1.getGreenMask());
                                                assertEquals(
                                                        f2.getMaxDataLength(),
                                                        f1.getMaxDataLength());
                                                assertEquals(f2.getRedMask(),
                                                        f1.getRedMask());
                                                assertEquals(f2.getSize(),
                                                        f1.getSize());

                                                assertEquals(f2.getEndian(),
                                                        f1.getEndian());
                                                assertEquals(f2.getFlipped(),
                                                        f1.getFlipped());
                                                assertEquals(
                                                        f2.getLineStride(),
                                                        f1.getLineStride());
                                                assertEquals(
                                                        f2.getPixelStride(),
                                                        f1.getPixelStride());
                                            }

                                            // see if blueMask affects - DOES
                                            // NOT.:
                                            {
                                                final RGBFormat f1 = new RGBFormat(
                                                        size, length, dataType,
                                                        frameRate,
                                                        bitsPerPixel, redMask,
                                                        greenMask, -1);

                                                assertEquals(
                                                        f2.getBitsPerPixel(),
                                                        f1.getBitsPerPixel());
                                                assertEquals(f2.getEncoding(),
                                                        f1.getEncoding());
                                                // assertEquals(f2.getBlueMask(),
                                                // f1.getBlueMask());
                                                assertEquals(f2.getDataType(),
                                                        f1.getDataType());
                                                assertEquals(f2.getFrameRate(),
                                                        f1.getFrameRate());
                                                assertEquals(f2.getGreenMask(),
                                                        f1.getGreenMask());
                                                assertEquals(
                                                        f2.getMaxDataLength(),
                                                        f1.getMaxDataLength());
                                                assertEquals(f2.getRedMask(),
                                                        f1.getRedMask());
                                                assertEquals(f2.getSize(),
                                                        f1.getSize());

                                                assertEquals(f2.getEndian(),
                                                        f1.getEndian());
                                                assertEquals(f2.getFlipped(),
                                                        f1.getFlipped());
                                                assertEquals(
                                                        f2.getLineStride(),
                                                        f1.getLineStride());
                                                assertEquals(
                                                        f2.getPixelStride(),
                                                        f1.getPixelStride());
                                            }

                                            // see if size affects - affects
                                            // line stride
                                            {
                                                final RGBFormat f1 = new RGBFormat(
                                                        null, length, dataType,
                                                        frameRate,
                                                        bitsPerPixel, redMask,
                                                        greenMask, blueMask);

                                                assertEquals(
                                                        f2.getBitsPerPixel(),
                                                        f1.getBitsPerPixel());
                                                assertEquals(f2.getEncoding(),
                                                        f1.getEncoding());
                                                assertEquals(f2.getBlueMask(),
                                                        f1.getBlueMask());
                                                assertEquals(f2.getDataType(),
                                                        f1.getDataType());
                                                assertEquals(f2.getFrameRate(),
                                                        f1.getFrameRate());
                                                assertEquals(f2.getGreenMask(),
                                                        f1.getGreenMask());
                                                assertEquals(
                                                        f2.getMaxDataLength(),
                                                        f1.getMaxDataLength());
                                                assertEquals(f2.getRedMask(),
                                                        f1.getRedMask());
                                                // assertEquals(f2.getSize(),
                                                // f1.getSize());

                                                assertEquals(f2.getEndian(),
                                                        f1.getEndian());
                                                assertEquals(f2.getFlipped(),
                                                        f1.getFlipped());
                                                // assertEquals(f2.getLineStride(),
                                                // f1.getLineStride());
                                                assertEquals(
                                                        f2.getPixelStride(),
                                                        f1.getPixelStride());
                                            }

                                            // see if dataType affects - affects
                                            // pixel stride, endian, line stride
                                            {
                                                final RGBFormat f1 = new RGBFormat(
                                                        size, length, null,
                                                        frameRate,
                                                        bitsPerPixel, redMask,
                                                        greenMask, blueMask);

                                                assertEquals(
                                                        f2.getBitsPerPixel(),
                                                        f1.getBitsPerPixel());
                                                assertEquals(f2.getEncoding(),
                                                        f1.getEncoding());
                                                assertEquals(f2.getBlueMask(),
                                                        f1.getBlueMask());
                                                // assertEquals(f2.getDataType(),
                                                // f1.getDataType());
                                                assertEquals(f2.getFrameRate(),
                                                        f1.getFrameRate());
                                                assertEquals(f2.getGreenMask(),
                                                        f1.getGreenMask());
                                                assertEquals(
                                                        f2.getMaxDataLength(),
                                                        f1.getMaxDataLength());
                                                assertEquals(f2.getRedMask(),
                                                        f1.getRedMask());
                                                assertEquals(f2.getSize(),
                                                        f1.getSize());

                                                // assertEquals(f2.getEndian(),
                                                // f1.getEndian());
                                                assertEquals(f2.getFlipped(),
                                                        f1.getFlipped());
                                                // assertEquals(f2.getLineStride(),
                                                // f1.getLineStride());
                                                // assertEquals(f2.getPixelStride(),
                                                // f1.getPixelStride());
                                            }

                                            // see if frameRate affects - DOES
                                            // NOT.
                                            {
                                                final RGBFormat f1 = new RGBFormat(
                                                        size, length, dataType,
                                                        -1.f, bitsPerPixel,
                                                        redMask, greenMask,
                                                        blueMask);

                                                assertEquals(
                                                        f2.getBitsPerPixel(),
                                                        f1.getBitsPerPixel());
                                                assertEquals(f2.getEncoding(),
                                                        f1.getEncoding());
                                                assertEquals(f2.getBlueMask(),
                                                        f1.getBlueMask());
                                                assertEquals(f2.getDataType(),
                                                        f1.getDataType());
                                                // assertEquals(f2.getFrameRate(),
                                                // f1.getFrameRate());
                                                assertEquals(f2.getGreenMask(),
                                                        f1.getGreenMask());
                                                assertEquals(
                                                        f2.getMaxDataLength(),
                                                        f1.getMaxDataLength());
                                                assertEquals(f2.getRedMask(),
                                                        f1.getRedMask());
                                                assertEquals(f2.getSize(),
                                                        f1.getSize());

                                                assertEquals(f2.getEndian(),
                                                        f1.getEndian());
                                                assertEquals(f2.getFlipped(),
                                                        f1.getFlipped());
                                                assertEquals(
                                                        f2.getLineStride(),
                                                        f1.getLineStride());
                                                assertEquals(
                                                        f2.getPixelStride(),
                                                        f1.getPixelStride());
                                            }

                                            // see if bitsPerPixel affects -
                                            // affects pixel stride, endian,
                                            // line stride
                                            {
                                                final RGBFormat f1 = new RGBFormat(
                                                        size, length, dataType,
                                                        frameRate, -1, redMask,
                                                        greenMask, blueMask);

                                                // assertEquals(f2.getBitsPerPixel(),
                                                // f1.getBitsPerPixel());
                                                assertEquals(f2.getEncoding(),
                                                        f1.getEncoding());
                                                assertEquals(f2.getBlueMask(),
                                                        f1.getBlueMask());
                                                assertEquals(f2.getDataType(),
                                                        f1.getDataType());
                                                assertEquals(f2.getFrameRate(),
                                                        f1.getFrameRate());
                                                assertEquals(f2.getGreenMask(),
                                                        f1.getGreenMask());
                                                assertEquals(
                                                        f2.getMaxDataLength(),
                                                        f1.getMaxDataLength());
                                                assertEquals(f2.getRedMask(),
                                                        f1.getRedMask());
                                                assertEquals(f2.getSize(),
                                                        f1.getSize());

                                                // assertEquals(f2.getEndian(),
                                                // f1.getEndian());
                                                assertEquals(f2.getFlipped(),
                                                        f1.getFlipped());
                                                // assertEquals(f2.getLineStride(),
                                                // f1.getLineStride());
                                                // assertEquals(f2.getPixelStride(),
                                                // f1.getPixelStride());

                                            }

                                            testHypothesis(f2);

                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

            // summary:
            // see if size affects - affects line stride
            // see if dataType affects - affects pixel stride, endian, line
            // stride
            // see if bitsPerPixel affects - affects pixel stride, endian, line
            // stride

            // so, in reverse, line stride is affected by size, dataType, and
            // bitsPerPixel
            // pixelStride is affected by dataType and bitsPerPixel
            // endian is affected by dataType and bitsPerPixel

            // // dump out some csv to analyze in openoffice - first line stride
            // if (false)
            // {
            // System.out.println();
            // System.out.println("sizex,sizey,datatype,bpp,linestride");
            // for (int length : new int[] {-1})
            // {
            // for (int redMask : new int[] {-1})
            // {
            // for (int greenMask : new int[] {-1})
            // {
            // for (int blueMask : new int[] {-1})
            // {
            // for (Dimension size : sizes)
            // {
            // for (Class dataType : dataTypes)
            // {
            // for (float frameRate : new float[] {-1.f})
            // {
            // for (int bitsPerPixel : bitsPerPixels)
            // {
            // final RGBFormat f2 = new RGBFormat(size, length,
            // dataType, frameRate, bitsPerPixel, redMask, greenMask, blueMask);
            //
            // System.out.print(f2.getSize() == null ? "\"null\"": ("" +
            // f2.getSize().width));
            // System.out.print(",");
            // System.out.print(f2.getSize() == null ? "\"null\"": ("" +
            // f2.getSize().height));
            // System.out.print(",");
            // System.out.print("\"" + f2.getDataType() + "\"");
            // System.out.print(",");
            // System.out.print(f2.getBitsPerPixel());
            // System.out.print(",");
            // System.out.print("" + f2.getLineStride());
            // System.out.println();
            //
            // }
            // }
            // }
            // }
            //
            // }
            // }
            // }
            // }
            // }
            //
            // // pixel stride
            // if (false)
            // {
            // System.out.println();
            // System.out.println("datatype,bpp,pixelstride");
            // for (int length : new int[] {-1})
            // {
            // for (int redMask : new int[] {-1})
            // {
            // for (int greenMask : new int[] {-1})
            // {
            // for (int blueMask : new int[] {-1})
            // {
            // for (Dimension size : new Dimension[] {null})
            // {
            // for (Class dataType : dataTypes)
            // {
            // for (float frameRate : new float[] {-1.f})
            // {
            // for (int bitsPerPixel : bitsPerPixels)
            // {
            // final RGBFormat f2 = new RGBFormat(size, length,
            // dataType, frameRate, bitsPerPixel, redMask, greenMask, blueMask);
            //
            // System.out.print("\"" + f2.getDataType() + "\"");
            // System.out.print(",");
            // System.out.print(f2.getBitsPerPixel());
            // System.out.print(",");
            // System.out.print("" + f2.getPixelStride());
            // System.out.println();
            //
            // }
            // }
            // }
            // }
            //
            // }
            // }
            // }
            // }
            // }
            //
            //
            // // endian
            // if (false)
            // {
            // System.out.println();
            // System.out.println("datatype,bpp,endian");
            // for (int length : new int[] {-1})
            // {
            // for (int redMask : new int[] {-1})
            // {
            // for (int greenMask : new int[] {-1})
            // {
            // for (int blueMask : new int[] {-1})
            // {
            // for (Dimension size : new Dimension[] {null})
            // {
            // for (Class dataType : dataTypes)
            // {
            // for (float frameRate : new float[] {-1.f})
            // {
            // for (int bitsPerPixel : bitsPerPixels)
            // {
            // final RGBFormat f2 = new RGBFormat(size, length,
            // dataType, frameRate, bitsPerPixel, redMask, greenMask, blueMask);
            //
            // System.out.print("\"" + f2.getDataType() + "\"");
            // System.out.print(",");
            // System.out.print(f2.getBitsPerPixel());
            // System.out.print(",");
            // System.out.print("" + f2.getEndian());
            // System.out.println();
            //
            // }
            // }
            // }
            // }
            //
            // }
            // }
            // }
            // }
            // }

        }

    }

    // hypothesis from analysing the csv output:
    void testHypothesis(RGBFormat f2)
    {
        // pixel stride:
        if (f2.getDataType() == null || f2.getBitsPerPixel() == -1)
            assertEquals(f2.getPixelStride(), -1);
        else if (f2.getDataType() == byte[].class)
        {
            if (f2.getBitsPerPixel() < 8)
                assertEquals(f2.getPixelStride(), 0);
            else if (f2.getBitsPerPixel() < 16)
                assertEquals(f2.getPixelStride(), 1);
            else if (f2.getBitsPerPixel() < 24)
                assertEquals(f2.getPixelStride(), 2);
            else if (f2.getBitsPerPixel() < 32)
                assertEquals(f2.getPixelStride(), 3);
            else
                assertEquals(f2.getPixelStride(), 4); // TODO: what about higher
                                                      // values. looks like div
                                                      // by 8, truncate

            assertEquals(f2.getPixelStride(), f2.getBitsPerPixel() / 8);
        } else
        { // short, and int arrays
            assertEquals(f2.getPixelStride(), 1);
        }

        // endian

        if (f2.getDataType() == byte[].class && f2.getBitsPerPixel() == 16)
        {
            assertEquals(f2.getEndian(), 1);
        } else
        {
            assertEquals(f2.getEndian(), -1);
        }

        // line stride:
        if (f2.getDataType() == null || f2.getSize() == null
                || f2.getBitsPerPixel() == -1)
        {
            assertEquals(f2.getLineStride(), -1);
        } else
        {
            if (f2.getDataType() == byte[].class)
            {
                assertEquals(f2.getLineStride(),
                        f2.getSize().width * (f2.getBitsPerPixel() / 8));
            } else
            {
                assertEquals(f2.getLineStride(), f2.getSize().width);
            }
        }

    }

    public void testIntersects()
    {
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, -1,
                -1, -1, -1).intersects(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff, 0xff00,
                        0xff0000, 1, -1, 0, -1));

        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, -1,
                -1, -1, -1).intersects(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)),
                new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                        Format.intArray, 1.3414634f, 32, 0xff, 0xff00,
                        0xff0000, 1, -1, 0, -1));
        assertEquals(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, -1,
                0, -1), new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, -1,
                0, -1));
    }

    public void testMatches()
    {
        assertFalse(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, -1,
                -1, -1, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)));
        assertTrue(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, -1,
                -1, -1, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)));
        assertFalse(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, -1,
                -1, -1, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)));
        assertTrue(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, -1,
                -1, -1, -1).matches(new RGBFormat(null, -1, Format.intArray,
                -1.0f, 32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)));
        assertFalse(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, -1,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)));
        assertTrue(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, -1,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)));
        assertTrue(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, -1,
                0, -1).matches(new RGBFormat(new java.awt.Dimension(320, 200),
                64000, Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000,
                -1, -1, -1, -1)));
        assertFalse(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, -1,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff0000, 0xff00, 0xff, 1, -1, 0, -1)));
        assertTrue(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, -1,
                0, -1).matches(new RGBFormat(null, -1, Format.intArray, -1.0f,
                32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1)));
        assertTrue(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, -1,
                0, -1).matches(new RGBFormat(new java.awt.Dimension(320, 200),
                64000, Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000,
                -1, -1, -1, -1)));

    }

    public void testToString()
    {
        assertEquals(new RGBFormat().toString(),
                "RGB, -1-bit, Masks=-1:-1:-1, PixelStride=-1, LineStride=-1");

        assertEquals(
                new RGBFormat(new Dimension(1, 2), 2000, Format.byteArray, 2.f,
                        1, 2, 3, 4, 5, 6, 7, 8).toString(),
                "RGB, 1x2, FrameRate=2.0, Length=2000, 1-bit, Masks=2:3:4, PixelStride=5, LineStride=6");

        assertEquals(
                new RGBFormat(new Dimension(1, 2), 2000, Format.byteArray, 1.f,
                        1, 2, 3, 4, 5, 6, 7, 8).toString(),
                "RGB, 1x2, FrameRate=1.0, Length=2000, 1-bit, Masks=2:3:4, PixelStride=5, LineStride=6");

        assertEquals(new RGBFormat(new Dimension(1, 2), 2000, Format.byteArray,
                -1.f, 1, 2, 3, 4, 5, 6, 7, 8).toString(),
                "RGB, 1x2, Length=2000, 1-bit, Masks=2:3:4, PixelStride=5, LineStride=6");
        assertEquals(new RGBFormat(new Dimension(1, 2), -1, Format.byteArray,
                -1.f, 1, 2, 3, 4, 5, 6, 7, 8).toString(),
                "RGB, 1x2, 1-bit, Masks=2:3:4, PixelStride=5, LineStride=6");

        // System.out.println(new RGBFormat(new Dimension(1, 2), 2000,
        // Format.byteArray, -1.f, 1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals(
                new RGBFormat(new Dimension(1, 2), 2000, Format.byteArray, 1.f,
                        1, 2, 3, 4, 5, 6, 1, 1).toString(),
                "RGB, 1x2, FrameRate=1.0, Length=2000, 1-bit, Masks=2:3:4, PixelStride=5, LineStride=6, Flipped");
        assertEquals(
                new RGBFormat(new Dimension(1, 2), 2000, Format.byteArray, 1.f,
                        1, 2, 3, 4, 5, 6, 7, 0).toString(),
                "RGB, 1x2, FrameRate=1.0, Length=2000, 1-bit, Masks=2:3:4, PixelStride=5, LineStride=6");
        assertEquals(
                new RGBFormat(new Dimension(1, 2), 2000, Format.byteArray,
                        30.00003f, 1, 2, 3, 4, 5, 6, 7, 0).toString(),
                "RGB, 1x2, FrameRate=30.0, Length=2000, 1-bit, Masks=2:3:4, PixelStride=5, LineStride=6");
        assertEquals(
                new RGBFormat(new Dimension(1, 2), 2000, Format.byteArray,
                        14.999992f, 1, 2, 3, 4, 5, 6, 7, 0).toString(),
                "RGB, 1x2, FrameRate=14.9, Length=2000, 1-bit, Masks=2:3:4, PixelStride=5, LineStride=6");

    }

}
