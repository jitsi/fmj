package com.t4l.jmf;

import java.awt.*;
import java.awt.image.*;

import javax.media.*;
import javax.media.format.*;

/**
 * Alternate implementation of BufferToImage. TODO: move the best of this code
 * into BufferToImage.
 *
 * @author Jeremy Wood
 *
 */
public class RGBConverter
{
    public static void flipVertical(int[] data, int width, int height)
    {
        int[] row1 = new int[width];
        int[] row2 = new int[width];
        int y2, offset1, offset2;
        for (int y = 0; y < height / 2; y++)
        {
            y2 = height - 1 - y;
            offset1 = y * width;
            offset2 = y2 * width;

            // For some reason this code doesn't work:
            // System.arraycopy(outputData,offset1,row1,0,size.width);
            // System.arraycopy(outputData,offset2,outputData,offset1,size.width);
            // System.arraycopy(row1,0,outputData,offset1,size.width);

            // so we have to use the two extra arrays instead of just one:
            System.arraycopy(data, offset1, row1, 0, width);
            System.arraycopy(data, offset2, row2, 0, width);
            System.arraycopy(row1, 0, data, offset2, width);
            System.arraycopy(row2, 0, data, offset1, width);
        }

        // want to flip horizontal? here it is:
        /*
         * int t, offset; for(int y = 0; y<size.height; y++) { offset =
         * y*size.width; for(int x = 0; x<size.width/2; x++) { t =
         * data[offset+size.width-1-x]; data[offset+size.width-1-x] =
         * data[offset+x]; data[offset+x] = t; } }
         */
    }

    /**
     *
     * @param mask
     *            the component mask
     * @return the number of bits to shift an object with this mask.
     *         <P>
     *         For example, if you pass "0xff" this should return zero, because
     *         the data is already in the domain of [0,255]. If you pass
     *         "0xff0000" this should return 16, because you need to call: <BR>
     *         <tt> (v & 0xff0000) >> 16</tt> <BR>
     *         To map this to a [0,255] value.
     */
    private static int getShift(int mask)
    {
        int i = mask;
        int k = 0;
        while (true)
        {
            if (i == 255)
            {
                return k;
            } else if (i < 255)
            {
                throw new IllegalArgumentException("Unsupported mask: "
                        + Integer.toString(mask, 16));
            } else
            {
                k++;
                i = i / 2;
            }
        }
    }

    /**
     * Extracts the image data from the BufferedImage and stores it in the array
     * provided.
     *
     * @param image
     *            this needs to be of type INT_ARGB, INT_RGB or INT_ARGB_PRE for
     *            best performance
     * @param dest
     *            the int array to populate with red, green and blue components.
     * @param format
     *            the format the dest array needs to be written in
     */
    public static void populateArray(BufferedImage image, int[] dest,
            RGBFormat format)
    {
        int imageType = image.getType();
        int width = image.getWidth();
        int height = image.getHeight();
        if (format == null)
            throw new NullPointerException();

        int pixelsPerRow = format.getLineStride();
        if (dest.length < pixelsPerRow * height)
        {
            throw new IllegalArgumentException("Illegal array size: "
                    + dest.length + "<" + (pixelsPerRow * height));
        }

        if (imageType == BufferedImage.TYPE_INT_ARGB
                || imageType == BufferedImage.TYPE_INT_ARGB_PRE
                || imageType == BufferedImage.TYPE_INT_RGB)
        {
            image.getRaster().getDataElements(0, 0, width, height, dest);
        } else
        {
            image.getRGB(0, 0, width, height, dest, 0, width);
        }

        int rMask = format.getRedMask();
        int gMask = format.getGreenMask();
        int bMask = format.getBlueMask();

        if (!(rMask == 0xff0000 && gMask == 0xff00 && bMask == 0xff
                && format.getLineStride() == width && format.getPixelStride() == 1))
        {
            int rShift = getShift(rMask);
            int gShift = getShift(gMask);
            int bShift = getShift(bMask);

            int r, g, b;
            int i;
            int pixelSize = format.getPixelStride(); // hopefully getPixelStride
                                                     // is 1, but just in
                                                     // case...
            for (int y = height - 1; y >= 0; y--)
            {
                for (int x = width - 1; x >= 0; x--)
                {
                    i = y * width + x;
                    r = (dest[i] >> 16) & 0xff;
                    g = (dest[i] >> 8) & 0xff;
                    b = (dest[i] >> 0) & 0xff;
                    i = y * pixelsPerRow + x * pixelSize;
                    dest[i] = (r << rShift) + (g << gShift) + (b << bShift);
                }
            }
        }

        if (format.getFlipped() == Format.TRUE)
        {
            flipVertical(dest, width, height);
        }
    }

    /**
     * Takes the data in <tt>array</tt> and fills the BufferedImage with that
     * image.
     *
     * @param array
     *            the array of pixel data
     * @param image
     *            the image to store the data in
     * @param vf
     *            the format the array is provided in
     */
    public static void populateImage(int[] array, int offset,
            BufferedImage image, RGBFormat vf)
    {
        int imageType = image.getType();
        int targetType;

        if (imageType == BufferedImage.TYPE_INT_ARGB
                || imageType == BufferedImage.TYPE_INT_ARGB_PRE)
        {
            targetType = BufferedImage.TYPE_INT_ARGB;
        } else
        {
            targetType = BufferedImage.TYPE_INT_RGB;
        }
        processData(array, offset, vf, targetType);

        int width = image.getWidth();
        int height = image.getHeight();
        if (imageType == BufferedImage.TYPE_INT_ARGB
                || imageType == BufferedImage.TYPE_INT_RGB)
        {
            image.getRaster().setDataElements(0, 0, width, height, array);
        } else
        {
            image.setRGB(0, 0, width, height, array, 0, width);
        }
    }

    /**
     * This takes the array of generic red, green and blue data from the Buffer
     * and converts it into RGB space.
     * <P>
     * Note that just because the data is in an RGBFormat does NOT mean that the
     * pixel data is ordered red-green-blue. It could be BGR. The masks may
     * vary, there is the scanline stride to account for, etc.
     * <P>
     * This method rewrites <tt>array</tt> so it is simply RGB data. (Or ARGB
     * data, if targetType is TYPE_INT_ARGB)
     *
     * @param array
     *            the raw data
     * @param vf
     *            the RGBFormat the <tt>array</tt> argument is encoded in
     * @param targetType
     *            this should be BufferedImage.TYPE_INT_RGB or
     *            BufferedImage.TYPE_INT_ARGB
     */
    private static void processData(int[] array, int arrayOffset, RGBFormat vf,
            int targetType)
    {
        Dimension size = vf.getSize();
        int width = size.width;
        int height = size.height;

        int rMask = vf.getRedMask();
        int gMask = vf.getGreenMask();
        int bMask = vf.getBlueMask();

        int rShift = getShift(rMask);
        int gShift = getShift(gMask);
        int bShift = getShift(bMask);
        int padding = vf.getLineStride() - width;

        if (arrayOffset == 0 && vf.getPixelStride() == 1 && padding == 0
                && rMask == 0xff0000 && gMask == 0xff00 && bMask == 0xff)
        { // it's already encoded as RGB, with no padding
            if (targetType == BufferedImage.TYPE_INT_RGB)
            {
                // woohoo! We don't have to lift a finger:
                return;
            }
            // we need to make sure everything has alpha:
            int area = width * height;
            for (int a = 0; a < area; a++)
            {
                array[a] = (array[a] & 0xffffff) + 0xff000000; // add a 255
                                                               // alpha
                                                               // component
            }
            return;
        }
        // TODO:
        // you can add another special case where when the RGB masks are
        // in the default order but we have to deal with padding between rows.

        /**
         * This is the generic catch-all solution. This handles arbitrary RGB
         * masks and padding:
         */
        int color, r, g, b;
        int i = 0;
        int base;

        if (targetType == BufferedImage.TYPE_INT_ARGB)
        {
            for (int y = 0; y < height; y++)
            {
                base = y * width;
                for (int x = 0; x < width; x++)
                {
                    color = array[i + arrayOffset];
                    r = (color >> rShift) & 0xff;
                    g = (color >> gShift) & 0xff;
                    b = (color >> bShift) & 0xff;
                    color = 0xff000000 + (r << 16) + (g << 8) + (b);
                    array[base + x] = color;
                    i++;
                }
                i += padding;
            }
        } else
        {
            for (int y = 0; y < height; y++)
            {
                base = y * width;
                for (int x = 0; x < width; x++)
                {
                    color = array[i + arrayOffset];
                    r = (color >> rShift) & 0xff;
                    g = (color >> gShift) & 0xff;
                    b = (color >> bShift) & 0xff;
                    color = (r << 16) + (g << 8) + (b);
                    array[base + x] = color;
                    i++;
                }
                i += padding;
            }
        }

        if (vf.getFlipped() == Format.TRUE)
        {
            flipVertical(array, width, height);
        }
    }
}