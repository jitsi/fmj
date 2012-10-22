package net.sf.fmj.test.compat.buffers;

import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.util.*;

import junit.framework.*;
import net.sf.fmj.codegen.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class BufferToImageTest extends TestCase
{
    public void gen(RGBFormat f)
    {
        {
            System.out.println("{");
            System.out.println("\tfinal RGBFormat f = "
                    + MediaCGUtils.formatToStr(f) + ";");

            System.out.println("\tassertEquals(f.getLineStride(), "
                    + f.getLineStride() + ");");
            System.out.println("\tassertEquals(f.getPixelStride(), "
                    + f.getPixelStride() + ");");
            System.out.println("\tassertEquals(f.getEndian(), " + f.getEndian()
                    + ");");
            System.out.println("\tassertEquals(f.getFlipped(), "
                    + f.getFlipped() + ");");

            final BufferToImage b2i = new BufferToImage(f);
            System.out
                    .println("\tfinal BufferToImage b2i = new BufferToImage(f);");

            final Buffer buffer = new Buffer();
            buffer.setFormat(f);

            System.out.println("\tfinal Buffer buffer = new Buffer();");
            System.out.println("\tbuffer.setFormat(f);");

            // System.out.println(format.getMaxDataLength());
            final byte[] ba = new byte[f.getMaxDataLength()];
            for (int i = 0; i < ba.length; ++i)
            {
                ba[i] = (byte) i;
            }
            buffer.setData(ba);
            System.out.println("\tbuffer.setData(" + CGUtils.toLiteral(ba)
                    + ");");

            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            System.out
                    .println("\tfinal BufferedImage i = (BufferedImage) b2i.createImage(buffer);");

            assertTrue(i != null);
            assertEquals(i.getWidth(), f.getSize().width);
            assertEquals(i.getHeight(), f.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);

            System.out.println("\tassertTrue(i != null);");
            System.out
                    .println("\tassertEquals(i.getWidth(), f.getSize().width);");
            System.out
                    .println("\tassertEquals(i.getHeight(), f.getSize().height);");
            System.out
                    .println("\tassertEquals(i.getColorModel().getColorSpace().getType(), ColorSpace.TYPE_RGB);");
            System.out
                    .println("\tassertEquals(i.getType(), BufferedImage.TYPE_INT_RGB);");
            // TODO: check other attributes, and pixels

            System.out.print("\tfinal int[][] target = new int[][] {");

            for (int y = 0; y < f.getSize().height; ++y)
            {
                for (int x = 0; x < f.getSize().width; ++x)
                {
                    int[] pix = i.getRaster().getPixel(x, y, (int[]) null);
                    System.out.print("" + CGUtils.toLiteral(pix) + ",");
                    // System.out.println("\t{");
                    // System.out.println("\t\tfinal int[] pix = i.getRaster().getPixel("
                    // + x + ", " + y + ", (int[]) null);");
                    // System.out.print("\t\tassertEquals(pix[0], " + pix[0] +
                    // ");");
                    // System.out.print(" assertEquals(pix[1], " + pix[1] +
                    // ");");
                    // System.out.println(" assertEquals(pix[2], " + pix[2] +
                    // ");");
                    // System.out.println("\t}");
                }
            }

            System.out.println("};");

            System.out
                    .println("\tfor (int y = 0; y < f.getSize().height; ++y)");
            System.out.println("\t{");
            System.out
                    .println("\t\tfor (int x = 0; x < f.getSize().width; ++x)");
            System.out.println("\t\t{");
            System.out
                    .println("\t\t\tfinal int[] pix = i.getRaster().getPixel(x, y, (int[]) null);");
            System.out
                    .println("\t\t\tassertEquals(pix[0], target[x + y * f.getSize().width][0]);");
            System.out
                    .println("\t\t\tassertEquals(pix[1], target[x + y * f.getSize().width][1]);");
            System.out
                    .println("\t\t\tassertEquals(pix[2], target[x + y * f.getSize().width][2]);");

            System.out.println("\t\t}");
            System.out.println("\t}");

            System.out.println("}");

            // System.out.println(i.getClass());
        }
    }

    public void testGen()
    {
        if (true)
            return; // disabled, since currently generated code is already
                    // pasted into this class.

        // 24bit rgb: r,g,b:
        gen(new RGBFormat(new Dimension(4, 4), 4 * 4 * 3, Format.byteArray,
                -1.f, 24, 1, 2, 3));
        gen(new RGBFormat(new Dimension(4, 4), 4 * 4 * 3, Format.byteArray,
                -1.f, 24, 3, 2, 1));
        gen(new RGBFormat(new Dimension(4, 4), 4 * 4 * 4, Format.byteArray,
                -1.f, 32, 3, 2, 1));
        gen(new RGBFormat(new Dimension(4, 4), 48, Format.byteArray, -1.0f, 24,
                0x1, 0x2, 0x3, 3, 12, 1, -1)); // flipped
        // TODO: test other -1s

    }

    /** @deprecated see XXXtestBufferToImage */
    @Deprecated
    public void XXXtestBig()
    {
        {
            final RGBFormat f = new RGBFormat(new java.awt.Dimension(4, 4), 48,
                    Format.byteArray, -1.0f, 24, 0x1, 0x2, 0x3, 3, 12, 0, -1);
            assertEquals(f.getLineStride(), 12);
            assertEquals(f.getPixelStride(), 3);
            assertEquals(f.getEndian(), -1);
            assertEquals(f.getFlipped(), 0);
            final BufferToImage b2i = new BufferToImage(f);
            final Buffer buffer = new Buffer();
            buffer.setFormat(f);
            buffer.setData(new byte[] { (byte) 0, (byte) 1, (byte) 2, (byte) 3,
                    (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9,
                    (byte) 10, (byte) 11, (byte) 12, (byte) 13, (byte) 14,
                    (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19,
                    (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24,
                    (byte) 25, (byte) 26, (byte) 27, (byte) 28, (byte) 29,
                    (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34,
                    (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39,
                    (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44,
                    (byte) 45, (byte) 46, (byte) 47 });
            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            assertTrue(i != null);
            assertEquals(i.getWidth(), f.getSize().width);
            assertEquals(i.getHeight(), f.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);
            assertEquals(i.getType(), BufferedImage.TYPE_INT_RGB);
            final int[][] target = new int[][] { new int[] { 0, 1, 2 },
                    new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 },
                    new int[] { 9, 10, 11 }, new int[] { 12, 13, 14 },
                    new int[] { 15, 16, 17 }, new int[] { 18, 19, 20 },
                    new int[] { 21, 22, 23 }, new int[] { 24, 25, 26 },
                    new int[] { 27, 28, 29 }, new int[] { 30, 31, 32 },
                    new int[] { 33, 34, 35 }, new int[] { 36, 37, 38 },
                    new int[] { 39, 40, 41 }, new int[] { 42, 43, 44 },
                    new int[] { 45, 46, 47 }, };
            for (int y = 0; y < f.getSize().height; ++y)
            {
                for (int x = 0; x < f.getSize().width; ++x)
                {
                    final int[] pix = i.getRaster()
                            .getPixel(x, y, (int[]) null);
                    assertEquals(pix[0], target[x + y * f.getSize().width][0]);
                    assertEquals(pix[1], target[x + y * f.getSize().width][1]);
                    assertEquals(pix[2], target[x + y * f.getSize().width][2]);
                }
            }
        }
        {
            final RGBFormat f = new RGBFormat(new java.awt.Dimension(4, 4), 48,
                    Format.byteArray, -1.0f, 24, 0x3, 0x2, 0x1, 3, 12, 0, -1);
            assertEquals(f.getLineStride(), 12);
            assertEquals(f.getPixelStride(), 3);
            assertEquals(f.getEndian(), -1);
            assertEquals(f.getFlipped(), 0);
            final BufferToImage b2i = new BufferToImage(f);
            final Buffer buffer = new Buffer();
            buffer.setFormat(f);
            buffer.setData(new byte[] { (byte) 0, (byte) 1, (byte) 2, (byte) 3,
                    (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9,
                    (byte) 10, (byte) 11, (byte) 12, (byte) 13, (byte) 14,
                    (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19,
                    (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24,
                    (byte) 25, (byte) 26, (byte) 27, (byte) 28, (byte) 29,
                    (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34,
                    (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39,
                    (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44,
                    (byte) 45, (byte) 46, (byte) 47 });
            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            assertTrue(i != null);
            assertEquals(i.getWidth(), f.getSize().width);
            assertEquals(i.getHeight(), f.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);
            assertEquals(i.getType(), BufferedImage.TYPE_INT_RGB);
            final int[][] target = new int[][] { new int[] { 2, 1, 0 },
                    new int[] { 5, 4, 3 }, new int[] { 8, 7, 6 },
                    new int[] { 11, 10, 9 }, new int[] { 14, 13, 12 },
                    new int[] { 17, 16, 15 }, new int[] { 20, 19, 18 },
                    new int[] { 23, 22, 21 }, new int[] { 26, 25, 24 },
                    new int[] { 29, 28, 27 }, new int[] { 32, 31, 30 },
                    new int[] { 35, 34, 33 }, new int[] { 38, 37, 36 },
                    new int[] { 41, 40, 39 }, new int[] { 44, 43, 42 },
                    new int[] { 47, 46, 45 }, };
            for (int y = 0; y < f.getSize().height; ++y)
            {
                for (int x = 0; x < f.getSize().width; ++x)
                {
                    final int[] pix = i.getRaster()
                            .getPixel(x, y, (int[]) null);
                    assertEquals(pix[0], target[x + y * f.getSize().width][0]);
                    assertEquals(pix[1], target[x + y * f.getSize().width][1]);
                    assertEquals(pix[2], target[x + y * f.getSize().width][2]);
                }
            }
        }
        {
            final RGBFormat f = new RGBFormat(new java.awt.Dimension(4, 4), 64,
                    Format.byteArray, -1.0f, 32, 0x3, 0x2, 0x1, 4, 16, 0, -1);
            assertEquals(f.getLineStride(), 16);
            assertEquals(f.getPixelStride(), 4);
            assertEquals(f.getEndian(), -1);
            assertEquals(f.getFlipped(), 0);
            final BufferToImage b2i = new BufferToImage(f);
            final Buffer buffer = new Buffer();
            buffer.setFormat(f);
            buffer.setData(new byte[] { (byte) 0, (byte) 1, (byte) 2, (byte) 3,
                    (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9,
                    (byte) 10, (byte) 11, (byte) 12, (byte) 13, (byte) 14,
                    (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19,
                    (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24,
                    (byte) 25, (byte) 26, (byte) 27, (byte) 28, (byte) 29,
                    (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34,
                    (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39,
                    (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44,
                    (byte) 45, (byte) 46, (byte) 47, (byte) 48, (byte) 49,
                    (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54,
                    (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 59,
                    (byte) 60, (byte) 61, (byte) 62, (byte) 63 });
            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            assertTrue(i != null);
            assertEquals(i.getWidth(), f.getSize().width);
            assertEquals(i.getHeight(), f.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);
            assertEquals(i.getType(), BufferedImage.TYPE_INT_RGB);
            final int[][] target = new int[][] { new int[] { 2, 1, 0 },
                    new int[] { 6, 5, 4 }, new int[] { 10, 9, 8 },
                    new int[] { 14, 13, 12 }, new int[] { 18, 17, 16 },
                    new int[] { 22, 21, 20 }, new int[] { 26, 25, 24 },
                    new int[] { 30, 29, 28 }, new int[] { 34, 33, 32 },
                    new int[] { 38, 37, 36 }, new int[] { 42, 41, 40 },
                    new int[] { 46, 45, 44 }, new int[] { 50, 49, 48 },
                    new int[] { 54, 53, 52 }, new int[] { 58, 57, 56 },
                    new int[] { 62, 61, 60 }, };
            for (int y = 0; y < f.getSize().height; ++y)
            {
                for (int x = 0; x < f.getSize().width; ++x)
                {
                    final int[] pix = i.getRaster()
                            .getPixel(x, y, (int[]) null);
                    assertEquals(pix[0], target[x + y * f.getSize().width][0]);
                    assertEquals(pix[1], target[x + y * f.getSize().width][1]);
                    assertEquals(pix[2], target[x + y * f.getSize().width][2]);
                }
            }
        }
        {
            final RGBFormat f = new RGBFormat(new java.awt.Dimension(4, 4), 48,
                    Format.byteArray, -1.0f, 24, 0x1, 0x2, 0x3, 3, 12, 1, -1);
            assertEquals(f.getLineStride(), 12);
            assertEquals(f.getPixelStride(), 3);
            assertEquals(f.getEndian(), -1);
            assertEquals(f.getFlipped(), 1);
            final BufferToImage b2i = new BufferToImage(f);
            final Buffer buffer = new Buffer();
            buffer.setFormat(f);
            buffer.setData(new byte[] { (byte) 0, (byte) 1, (byte) 2, (byte) 3,
                    (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9,
                    (byte) 10, (byte) 11, (byte) 12, (byte) 13, (byte) 14,
                    (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19,
                    (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24,
                    (byte) 25, (byte) 26, (byte) 27, (byte) 28, (byte) 29,
                    (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34,
                    (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39,
                    (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44,
                    (byte) 45, (byte) 46, (byte) 47 });
            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            assertTrue(i != null);
            assertEquals(i.getWidth(), f.getSize().width);
            assertEquals(i.getHeight(), f.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);
            assertEquals(i.getType(), BufferedImage.TYPE_INT_RGB);
            final int[][] target = new int[][] { new int[] { 36, 37, 38 },
                    new int[] { 39, 40, 41 }, new int[] { 42, 43, 44 },
                    new int[] { 45, 46, 47 }, new int[] { 24, 25, 26 },
                    new int[] { 27, 28, 29 }, new int[] { 30, 31, 32 },
                    new int[] { 33, 34, 35 }, new int[] { 12, 13, 14 },
                    new int[] { 15, 16, 17 }, new int[] { 18, 19, 20 },
                    new int[] { 21, 22, 23 }, new int[] { 0, 1, 2 },
                    new int[] { 3, 4, 5 }, new int[] { 6, 7, 8 },
                    new int[] { 9, 10, 11 }, };
            for (int y = 0; y < f.getSize().height; ++y)
            {
                for (int x = 0; x < f.getSize().width; ++x)
                {
                    final int[] pix = i.getRaster()
                            .getPixel(x, y, (int[]) null);
                    assertEquals(pix[0], target[x + y * f.getSize().width][0]);
                    assertEquals(pix[1], target[x + y * f.getSize().width][1]);
                    assertEquals(pix[2], target[x + y * f.getSize().width][2]);
                }
            }
        }

    }

    // no longer passes on FMJ because FMJ is cleverer with conversions than
    // JMF.
    // JMF will convert a byte[] image to an int[] buffered image, but
    // FMJ will convert it to a byte[] buffered image.
    /** @deprecated */
    @Deprecated
    public void XXXtestBufferToImage()
    {
        {
            try
            {
                final VideoFormat format = new RGBFormat();
                final BufferToImage b2i = new BufferToImage(format);
                assertTrue(false);
            } catch (NullPointerException e)
            {
            }
        }

        {
            // 24bit rgb: r,g,b:
            final RGBFormat format = new RGBFormat(new Dimension(16, 16),
                    16 * 16 * 3, Format.byteArray, -1.f, 24, 1, 2, 3);
            assertEquals(format.getLineStride(), 16 * 3);
            assertEquals(format.getPixelStride(), 3);
            assertEquals(format.getEndian(), -1);
            assertEquals(format.getFlipped(), 0);

            final BufferToImage b2i = new BufferToImage(format);

            final Buffer buffer = new Buffer();
            buffer.setFormat(format);
            // System.out.println(format.getMaxDataLength());
            final byte[] ba = new byte[format.getMaxDataLength()];
            ba[0] = 2;
            buffer.setData(ba);

            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            assertTrue(i != null);
            assertEquals(i.getWidth(), format.getSize().width);
            assertEquals(i.getHeight(), format.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);
            assertEquals(i.getType(), BufferedImage.TYPE_INT_RGB);
            // TODO: check other attributes, and pixels
            int[] pix = i.getRaster().getPixel(0, 0, (int[]) null);
            assertEquals(pix[0], 2);
            assertEquals(pix[1], 0);
            assertEquals(pix[2], 0);

            // System.out.println(i.getClass());
        }

        {
            // 24bit rgb: r,g,b:
            final RGBFormat format = new RGBFormat(new Dimension(4, 4),
                    4 * 4 * 3, Format.byteArray, -1.f, 24, 1, 2, 3);
            assertEquals(format.getLineStride(), 4 * 3);
            assertEquals(format.getPixelStride(), 3);
            assertEquals(format.getEndian(), -1);
            assertEquals(format.getFlipped(), 0);

            final BufferToImage b2i = new BufferToImage(format);

            final Buffer buffer = new Buffer();
            buffer.setFormat(format);
            // System.out.println(format.getMaxDataLength());
            final byte[] ba = new byte[format.getMaxDataLength()];
            ba[0] = 2;
            buffer.setData(ba);

            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            assertTrue(i != null);
            assertEquals(i.getWidth(), format.getSize().width);
            assertEquals(i.getHeight(), format.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);
            assertEquals(i.getType(), BufferedImage.TYPE_INT_RGB);
            // TODO: check other attributes, and pixels
            int[] pix = i.getRaster().getPixel(0, 0, (int[]) null);
            assertEquals(pix[0], 2);
            assertEquals(pix[1], 0);
            assertEquals(pix[2], 0);

            // System.out.println(i.getClass());
        }

    }
}
