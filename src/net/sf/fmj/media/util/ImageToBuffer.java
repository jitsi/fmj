package net.sf.fmj.media.util;

import java.awt.*;
import java.awt.image.*;

import javax.media.*;
import javax.media.format.*;

/**
 * Implementation of javax.media.util.ImageToBuffer. TODO: need to take into
 * account line stride, if it is different from what one would expect.
 *
 * @author Ken Larson
 *
 */
public class ImageToBuffer
{
    private static BufferedImage convert(Image im)
    {
        BufferedImage bi = new BufferedImage(im.getWidth(null),
                im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public static Buffer createBuffer(java.awt.Image image, float frameRate)
    {
        final BufferedImage bi;
        if (image instanceof BufferedImage)
        {
            bi = (BufferedImage) image;
        } else
        {
            bi = convert(image);
        }

        final DataBuffer dataBuffer = bi.getRaster().getDataBuffer();

        final Object pixels;
        final int pixelsLength;
        final Class<?> dataType;

        if (dataBuffer instanceof DataBufferInt)
        {
            final int[] intPixels = ((DataBufferInt) dataBuffer).getData();
            pixels = intPixels;
            pixelsLength = intPixels.length;
            dataType = Format.intArray;
        } else if (dataBuffer instanceof DataBufferByte)
        {
            final byte[] bytePixels = ((DataBufferByte) dataBuffer).getData();
            pixels = bytePixels;
            pixelsLength = bytePixels.length;
            dataType = Format.byteArray;
        } else
        {
            throw new IllegalArgumentException(
                    "Unknown or unsupported data buffer type: " + dataBuffer);
        }

        final int bufferedImageType = bi.getType();

        final Buffer result = new Buffer();
        final Dimension size = new Dimension(bi.getWidth(), bi.getHeight());
        final int maxDataLength = -1; // TODO
        final int bitsPerPixel;

        final int red;
        final int green;
        final int blue;

        if (bufferedImageType == BufferedImage.TYPE_3BYTE_BGR)
        {
            bitsPerPixel = 24;
            red = 1;
            green = 2;
            blue = 3;
        } else if (bufferedImageType == BufferedImage.TYPE_INT_BGR)
        {
            bitsPerPixel = 32;
            // TODO: test
            red = 0xFF;
            green = 0xFF00;
            blue = 0xFF0000;
        } else if (bufferedImageType == BufferedImage.TYPE_INT_RGB)
        {
            bitsPerPixel = 32;
            red = 0xFF0000;
            green = 0xFF00;
            blue = 0xFF;
        } else if (bufferedImageType == BufferedImage.TYPE_INT_ARGB)
        {
            bitsPerPixel = 32;
            red = 0xFF0000;
            green = 0xFF00;
            blue = 0xFF;
            // just ignore alpha
        } else
        {
            if (bi.getColorModel() instanceof ComponentColorModel
                    && bi.getSampleModel() instanceof ComponentSampleModel)
            {
                final ComponentColorModel componentColorModel = (ComponentColorModel) bi
                        .getColorModel();
                final ComponentSampleModel componentSampleModel = (ComponentSampleModel) bi
                        .getSampleModel();
                final int[] offsets = componentSampleModel.getBandOffsets();
                if (dataBuffer instanceof DataBufferInt)
                {
                    // TODO: untested
                    bitsPerPixel = 32;
                    red = 0xFF << offsets[0];
                    green = 0xFF << offsets[1];
                    blue = 0xFF << offsets[2];

                } else if (dataBuffer instanceof DataBufferByte)
                {
                    bitsPerPixel = componentSampleModel.getPixelStride() * 8; // TODO:
                                                                              // should
                                                                              // it
                                                                              // always
                                                                              // be
                                                                              // 24?
                                                                              // or
                                                                              // are
                                                                              // the
                                                                              // pixel
                                                                              // stride
                                                                              // and
                                                                              // the
                                                                              // bits
                                                                              // per
                                                                              // pixel
                                                                              // unrelated?
                    red = 1 + offsets[0];
                    green = 1 + offsets[1];
                    blue = 1 + offsets[2];
                } else
                    throw new IllegalArgumentException(
                            "Unsupported buffered image type: "
                                    + bufferedImageType);
            } else if (bi.getColorModel() instanceof DirectColorModel)
            {
                // TODO: untested
                final DirectColorModel directColorModel = (DirectColorModel) bi
                        .getColorModel();
                if (dataBuffer instanceof DataBufferInt)
                {
                    bitsPerPixel = 32;
                    red = directColorModel.getRedMask();
                    green = directColorModel.getGreenMask();
                    blue = directColorModel.getBlueMask();
                } else
                    throw new IllegalArgumentException(
                            "Unsupported buffered image type: "
                                    + bufferedImageType);
            } else
                throw new IllegalArgumentException(
                        "Unsupported buffered image type: " + bufferedImageType);
        }
        result.setFormat(new RGBFormat(size, maxDataLength, dataType,
                frameRate, bitsPerPixel, red, green, blue));
        result.setData(pixels);
        result.setLength(pixelsLength);
        result.setOffset(0);

        return result;
    }

    public ImageToBuffer()
    {
        super();
        // no reason to ever instantiate this
    }
}
