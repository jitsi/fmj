package net.sf.fmj.media.util;

import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;

import javax.media.*;
import javax.media.format.*;

/**
 * Implementation of javax.media.util.BufferToImage. In progress
 *
 * @author Ken Larson
 *
 */
public class BufferToImage
{
    // TODO: support new methods to create MemoryImageSource
    // TODO: optimize!
    // private final VideoFormat format;
    // private final int w;
    // private final int h;
    // private final Class dataType;

    public BufferToImage(VideoFormat format)
    {
        /*
         * The code bellow throws a NullPointerException for the sake of
         * compatibility with JMF. However, such behavior is wrong. Firstly,
         * the format argument is not used. Secondly, Buffer instances have a
         * format property. Thirdly, SimpleAWTRenderer#setInputFormat(Format)
         * and SimpleSwingRenderer#setInputFormat(Format) may invoke the
         * constructor with a VideoFormat which does not have a size and such
         * an argument is perfectly valid for them.
         */
//        if (format.getSize() == null)
//            throw new NullPointerException(); // this is what JMF does.

        // TODO: we should be able to get this info up front, and do some
        // optimization
        // for down below.

        // TODO: in some cases, the size of the format might not be known up
        // front?
        // this happens with the fleck bass solo video, with the JMF unknown
        // handler.
        // this.format = format;
        //
        // w = format.getSize().width;
        // h = format.getSize().height;
        // dataType = format.getDataType();
    }

    public BufferedImage createBufferedImage(Buffer buffer)
    {
        final VideoFormat format;
        final int w;
        final int h;
        final Class<?> dataType;

        format = (VideoFormat) buffer.getFormat();

        w = format.getSize().width;
        h = format.getSize().height;
        dataType = format.getDataType();

        if (format instanceof RGBFormat)
        {
            final RGBFormat rgbFormat = (RGBFormat) format;
            final int bitsPerPixel = rgbFormat.getBitsPerPixel();
            final int redMask = rgbFormat.getRedMask();
            final int greenMask = rgbFormat.getGreenMask();
            final int blueMask = rgbFormat.getBlueMask();
            final int lineStride = rgbFormat.getLineStride();
            final int pixelStride = rgbFormat.getPixelStride();
            final boolean flipped = rgbFormat.getFlipped() == 1;

            // TODO: check for -1s

            if (dataType == Format.byteArray)
            {
                final byte[] bytes = (byte[]) buffer.getData();
                if (bitsPerPixel == 24)
                {
                    // this is much faster than iterating through the pixels.
                    // if we create a writable raster and then construct a
                    // buffered image,
                    // no new array is created and no data is copied.
                    // TODO: optimize other cases.
                    final DataBufferByte db = new DataBufferByte(
                            new byte[][] { bytes }, bytes.length);
                    final ComponentSampleModel sm = new ComponentSampleModel(
                            DataBuffer.TYPE_BYTE, w, h, pixelStride,
                            lineStride, new int[] { redMask - 1, greenMask - 1,
                                    blueMask - 1 });
                    final WritableRaster r = Raster.createWritableRaster(sm,
                            db, new Point(0, 0));
                    // construction borrowed from BufferedImage constructor, for
                    // BufferedImage.TYPE_3BYTE_BGR
                    final ColorSpace cs = ColorSpace
                            .getInstance(ColorSpace.CS_sRGB);
                    int[] nBits = { 8, 8, 8 };
                    // int[] bOffs = {2, 1, 0};
                    final ColorModel colorModel = new ComponentColorModel(cs,
                            nBits, false, false, Transparency.OPAQUE,
                            DataBuffer.TYPE_BYTE);
                    final BufferedImage bi = new BufferedImage(colorModel, r,
                            false, null);
                    return bi;
                } else if (bitsPerPixel == 32)
                {
                    final DataBufferByte db = new DataBufferByte(
                            new byte[][] { bytes }, bytes.length);
                    final ComponentSampleModel sm = new ComponentSampleModel(
                            DataBuffer.TYPE_BYTE, w, h, pixelStride,
                            lineStride, new int[] { redMask - 1, greenMask - 1,
                                    blueMask - 1, 3 }); // TODO: what to do with
                                                        // alpha?
                    final WritableRaster r = Raster.createWritableRaster(sm,
                            db, new Point(0, 0));
                    // construction borrowed from BufferedImage constructor, for
                    // BufferedImage.TYPE_4BYTE_ABGR
                    final ColorSpace cs = ColorSpace
                            .getInstance(ColorSpace.CS_sRGB);
                    int[] nBits = { 8, 8, 8, 8 };
                    // int[] bOffs = {3, 2, 1, 0};
                    final ColorModel colorModel = new ComponentColorModel(cs,
                            nBits, true, false, Transparency.TRANSLUCENT,
                            DataBuffer.TYPE_BYTE);
                    final BufferedImage bi = new BufferedImage(colorModel, r,
                            false, null);
                    return bi;
                } else if (bitsPerPixel == 8)
                {
                    final DataBufferByte db = new DataBufferByte(
                            new byte[][] { bytes }, bytes.length);
                    final SampleModel sm = new SinglePixelPackedSampleModel(
                            DataBuffer.TYPE_BYTE, w, h, lineStride, new int[] {
                                    redMask, greenMask, blueMask });
                    final WritableRaster r = Raster.createWritableRaster(sm,
                            db, new Point(0, 0));
                    final ColorModel colorModel = new DirectColorModel(
                            bitsPerPixel, redMask, greenMask, blueMask);
                    final BufferedImage bi = new BufferedImage(colorModel, r,
                            false, null);
                    return bi;
                } else
                {
                    final BufferedImage bi = new BufferedImage(w, h,
                            BufferedImage.TYPE_INT_RGB);
                    final int[] pixels = new int[w * h];
                    int pixelIndex = 0;
                    int lineOffset = 0;
                    if (flipped)
                        lineOffset = (h - 1) * lineStride;

                    for (int y = 0; y < h; ++y)
                    {
                        int off = lineOffset;
                        for (int x = 0; x < w; ++x)
                        {
                            final byte r = bytes[off + redMask - 1];
                            final byte g = bytes[off + greenMask - 1];
                            final byte b = bytes[off + blueMask - 1];
                            int pixel = 0;
                            pixel += r & 0xff; // red
                            pixel *= 256;
                            pixel += g & 0xff; // green
                            pixel *= 256;
                            pixel += b & 0xff; // blue
                            pixels[pixelIndex++] = pixel;
                            off += pixelStride;
                        }
                        if (flipped)
                            lineOffset -= lineStride;
                        else
                            lineOffset += lineStride;
                    }

                    bi.setRGB(0, 0, w, h, pixels, 0, w);
                    return bi;
                }

            } else if (dataType == Format.shortArray)
            {
                final short[] shorts = (short[]) buffer.getData();
                if (bitsPerPixel == 16)
                {
                    final DataBufferUShort db = new DataBufferUShort(
                            new short[][] { shorts }, shorts.length);
                    final SampleModel sm = new SinglePixelPackedSampleModel(
                            DataBuffer.TYPE_USHORT, w, h, lineStride,
                            new int[] { redMask, greenMask, blueMask });
                    final WritableRaster r = Raster.createWritableRaster(sm,
                            db, new Point(0, 0));
                    final ColorModel colorModel = new DirectColorModel(
                            bitsPerPixel, redMask, greenMask, blueMask);
                    final BufferedImage bi = new BufferedImage(colorModel, r,
                            false, null);
                    return bi;
                } else
                {
                    throw new UnsupportedOperationException(); // TODO
                }
            } else if (dataType == Format.intArray)
            {
                // if (true)
                {
                    // optimized, don't copy data or iterate through pixels:
                    final int[] bytes = (int[]) buffer.getData();
                    final DataBufferInt db = new DataBufferInt(
                            new int[][] { bytes }, bytes.length);
                    final SinglePixelPackedSampleModel sm = new SinglePixelPackedSampleModel(
                            DataBuffer.TYPE_INT, w, h, new int[] { redMask,
                                    greenMask, blueMask });
                    final WritableRaster r = Raster.createWritableRaster(sm,
                            db, new Point(0, 0));

                    final ColorModel colorModel = new DirectColorModel(24,
                            redMask, // Red
                            greenMask, // Green
                            blueMask, // Blue
                            0x0 // Alpha
                    );
                    final BufferedImage bi = new BufferedImage(colorModel, r,
                            false, null);
                    return bi;

                }
                // else
                // {
                // // old way, slow:
                // final int[] bytes = (int[]) buffer.getData();
                // {
                // final int bufferedImageType;
                // if (redMask < greenMask)
                // bufferedImageType = BufferedImage.TYPE_INT_BGR;
                // else
                // bufferedImageType = BufferedImage.TYPE_INT_RGB;
                //
                // final BufferedImage bi = new BufferedImage(w, h,
                // bufferedImageType);
                // final int [] pixels = new int[w * h];
                // int pixelIndex = 0;
                // int lineOffset = 0;
                // if (flipped)
                // lineOffset = (h - 1) * lineStride;
                //
                // for (int y = 0; y < h; ++y)
                // {
                // int off = lineOffset;
                // for (int x = 0; x < w; ++x)
                // {
                // int srcpixel = bytes[off];
                // final byte r = (byte) ((srcpixel & redMask) >>
                // shiftValue(redMask));
                // final byte g = (byte) ((srcpixel & greenMask) >>
                // shiftValue(greenMask));
                // final byte b = (byte) ((srcpixel & blueMask) >>
                // shiftValue(blueMask));
                //
                // int pixel = 0;
                // pixel += r & 0xff; // red
                // pixel *= 256;
                // pixel += g & 0xff; // green
                // pixel *= 256;
                // pixel += b & 0xff; // blue
                // pixels[pixelIndex++] = pixel;
                // off += pixelStride;
                // }
                // if (flipped)
                // lineOffset -= lineStride;
                // else
                // lineOffset += lineStride;
                // }
                //
                // bi.setRGB(0,0,w,h,pixels,0,w);
                // return bi;
                // }
                // }
            } else
            {
                throw new UnsupportedOperationException(); // TODO
            }

        } else
            throw new UnsupportedOperationException(); // TODO
    }

    public java.awt.Image createImage(Buffer buffer)
    {
        return createBufferedImage(buffer);
    }

    // private static int shiftValue(int mask)
    // {
    // if (mask == 0xFF)
    // return 0;
    // else if (mask == 0xFF00)
    // return 8;
    // else if (mask == 0xFF0000)
    // return 16;
    // else
    // throw new IllegalArgumentException("mask=" + mask); // TODO
    //
    // }
}
