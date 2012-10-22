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
public class BufferToImageTest_Int extends TestCase
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
            final int[] ba = new int[f.getMaxDataLength()];
            for (int i = 0; i < ba.length; ++i)
            {
                ba[i] = i;
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
            if (i.getType() == BufferedImage.TYPE_INT_BGR)
                System.out
                        .println("\tassertEquals(i.getType(), BufferedImage.TYPE_INT_BGR);");
            else if (i.getType() == BufferedImage.TYPE_INT_RGB)
                System.out
                        .println("\tassertEquals(i.getType(), BufferedImage.TYPE_INT_RGB);");
            else
                throw new RuntimeException();
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

    public void testBig()
    {
        {
            final RGBFormat f = new RGBFormat(new java.awt.Dimension(4, 4), 16,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    4, 0, -1);
            assertEquals(f.getLineStride(), 4);
            assertEquals(f.getPixelStride(), 1);
            assertEquals(f.getEndian(), -1);
            assertEquals(f.getFlipped(), 0);
            final BufferToImage b2i = new BufferToImage(f);
            final Buffer buffer = new Buffer();
            buffer.setFormat(f);
            buffer.setData(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15 });
            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            assertTrue(i != null);
            assertEquals(i.getWidth(), f.getSize().width);
            assertEquals(i.getHeight(), f.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);
            assertEquals(i.getType(), BufferedImage.TYPE_INT_BGR);
            final int[][] target = new int[][] { new int[] { 0, 0, 0 },
                    new int[] { 1, 0, 0 }, new int[] { 2, 0, 0 },
                    new int[] { 3, 0, 0 }, new int[] { 4, 0, 0 },
                    new int[] { 5, 0, 0 }, new int[] { 6, 0, 0 },
                    new int[] { 7, 0, 0 }, new int[] { 8, 0, 0 },
                    new int[] { 9, 0, 0 }, new int[] { 10, 0, 0 },
                    new int[] { 11, 0, 0 }, new int[] { 12, 0, 0 },
                    new int[] { 13, 0, 0 }, new int[] { 14, 0, 0 },
                    new int[] { 15, 0, 0 }, };
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
            final RGBFormat f = new RGBFormat(new java.awt.Dimension(4, 4), 16,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    4, 0, -1);
            assertEquals(f.getLineStride(), 4);
            assertEquals(f.getPixelStride(), 1);
            assertEquals(f.getEndian(), -1);
            assertEquals(f.getFlipped(), 0);
            final BufferToImage b2i = new BufferToImage(f);
            final Buffer buffer = new Buffer();
            buffer.setFormat(f);
            buffer.setData(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15 });
            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            assertTrue(i != null);
            assertEquals(i.getWidth(), f.getSize().width);
            assertEquals(i.getHeight(), f.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);
            assertEquals(i.getType(), BufferedImage.TYPE_INT_RGB);
            final int[][] target = new int[][] { new int[] { 0, 0, 0 },
                    new int[] { 0, 0, 1 }, new int[] { 0, 0, 2 },
                    new int[] { 0, 0, 3 }, new int[] { 0, 0, 4 },
                    new int[] { 0, 0, 5 }, new int[] { 0, 0, 6 },
                    new int[] { 0, 0, 7 }, new int[] { 0, 0, 8 },
                    new int[] { 0, 0, 9 }, new int[] { 0, 0, 10 },
                    new int[] { 0, 0, 11 }, new int[] { 0, 0, 12 },
                    new int[] { 0, 0, 13 }, new int[] { 0, 0, 14 },
                    new int[] { 0, 0, 15 }, };
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

    public void testBufferToImage()
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
            final RGBFormat format = new RGBFormat(new Dimension(16, 16),
                    16 * 16, Format.intArray, 1.3414634f, 32, 0xff, 0xff00,
                    0xff0000, 1, 16, 0, -1);

            assertEquals(format.getLineStride(), 16);
            assertEquals(format.getPixelStride(), 1);
            assertEquals(format.getEndian(), -1);
            assertEquals(format.getFlipped(), 0);

            final BufferToImage b2i = new BufferToImage(format);

            final Buffer buffer = new Buffer();
            buffer.setFormat(format);
            // System.out.println(format.getMaxDataLength());
            final int[] ba = new int[format.getMaxDataLength()];
            ba[0] = 2;
            buffer.setData(ba);

            final BufferedImage i = (BufferedImage) b2i.createImage(buffer);
            assertTrue(i != null);
            assertEquals(i.getWidth(), format.getSize().width);
            assertEquals(i.getHeight(), format.getSize().height);
            assertEquals(i.getColorModel().getColorSpace().getType(),
                    ColorSpace.TYPE_RGB);
            assertEquals(i.getType(), BufferedImage.TYPE_INT_BGR);
            // TODO: check other attributes, and pixels
            int[] pix = i.getRaster().getPixel(0, 0, (int[]) null);
            assertEquals(pix[0], 2);
            assertEquals(pix[1], 0);
            assertEquals(pix[2], 0);

            // System.out.println(i.getClass());
        }

    }

    public void testGen()
    {
        if (true)
            return; // disabled, since currently generated code is already
                    // pasted into this class.

        gen(new RGBFormat(new Dimension(4, 4), 4 * 4, Format.intArray,
                1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, 4, 0, -1));
        gen(new RGBFormat(new Dimension(4, 4), 4 * 4, Format.intArray,
                1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1, 4, 0, -1));

        // TODO: flipped, others

    }
}
