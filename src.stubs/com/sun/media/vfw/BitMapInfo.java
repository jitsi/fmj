package com.sun.media.vfw;

import java.awt.*;

import javax.media.format.*;

import com.sun.media.format.*;

/**
 *
 * @author Ken Larson
 *
 */
public class BitMapInfo
{
    public int biWidth;
    public int biHeight;
    public int biPlanes;
    public int biBitCount;
    public String fourcc;
    public int biSizeImage;
    public int biXPelsPerMeter;
    public int biYPelsPerMeter;
    public int biClrUsed;
    public int biClrImportant;
    public int extraSize;
    public byte[] extraBytes;

    public BitMapInfo()
    {
        this.fourcc = "";
        this.biPlanes = 1;
        this.biBitCount = 24;

    }

    public BitMapInfo(String fourcc, int width, int height)
    {
        this.fourcc = fourcc;
        this.biPlanes = 1;
        this.biBitCount = 24;
        this.biWidth = width;
        this.biHeight = height;

        if (fourcc.equals("RGB"))
        {
            this.biSizeImage = biWidth * biHeight * (biBitCount / 8);
        }

    }

    public BitMapInfo(String fourcc, int width, int height, int planes,
            int bitcount, int sizeImage, int clrused, int clrimportant)
    {
        this.fourcc = fourcc;
        this.biPlanes = planes;
        this.biBitCount = bitcount;
        this.biWidth = width;
        this.biHeight = height;
        this.biSizeImage = sizeImage;
        this.biClrUsed = clrused;
        this.biClrImportant = clrimportant;
    }

    public BitMapInfo(VideoFormat format)
    {
        if (format instanceof RGBFormat)
        {
            final RGBFormat fCast = (RGBFormat) format;
            this.fourcc = fCast.getEncoding().toUpperCase();
            this.biPlanes = 1;
            this.biBitCount = fCast.getBitsPerPixel();
            if (fCast.getSize() == null)
            {
                this.biWidth = 320;
                this.biHeight = 240;
            } else
            {
                this.biWidth = fCast.getSize().width;
                this.biHeight = fCast.getSize().height;
            }
            if (biBitCount == -1)
                this.biSizeImage = -2;
            else
                this.biSizeImage = biWidth * biHeight * (biBitCount / 8);
            this.biClrUsed = 0;
            this.biClrImportant = 0;
        } else if (format instanceof AviVideoFormat)
        {
            final AviVideoFormat fCast = (AviVideoFormat) format;
            this.fourcc = fCast.getEncoding();
            this.biPlanes = fCast.getPlanes();
            this.biBitCount = fCast.getBitsPerPixel();
            this.biWidth = fCast.getSize().width;
            this.biHeight = fCast.getSize().height;
            this.biSizeImage = fCast.getImageSize();
            this.biClrUsed = fCast.getClrUsed();
            this.biClrImportant = fCast.getClrImportant();
        } else if (format instanceof YUVFormat)
        {
            final YUVFormat fCast = (YUVFormat) format;
            if (fCast.getYuvType() == YUVFormat.YUV_420)
            {
                this.fourcc = "YV12";
                this.biBitCount = 12;
            } else
            {
                this.fourcc = format.getEncoding();
                this.biBitCount = 24;
            }

            // TODO: what if size is set?
            this.biWidth = 320;
            this.biHeight = 240;
            this.biPlanes = 1;
            this.biSizeImage = -1;
        } else
        {
            this.fourcc = format.getEncoding();
            this.biBitCount = 24;
            // TODO: what if size is set?
            this.biWidth = 320;
            this.biHeight = 240;
            this.biPlanes = 1;
            this.biSizeImage = -1;
        }
    }

    public VideoFormat createVideoFormat(Class<?> arrayType)
    {
        return createVideoFormat(arrayType, -1.f);
    }

    public VideoFormat createVideoFormat(Class<?> arrayType, float frameRate)
    {
        if (fourcc.equals("RGB"))
        {
            final int red, green, blue;
            final int pixelStride;
            final int lineStride;
            final int maxDataLength;

            // TODO: this was determined by black-box testing, so we may have
            // missed some cases.

            if (biBitCount == 32)
            {
                if (arrayType == int[].class)
                {
                    red = 0xFF0000;
                    green = 0xFF00;
                    blue = 0xFF;
                } else
                {
                    red = 3;
                    green = 2;
                    blue = 1;
                }
            } else if (biBitCount == 24)
            {
                red = 3;
                green = 2;
                blue = 1;
            } else if (biBitCount == 16)
            {
                red = 31744;
                green = 992;
                blue = 31;
            } else
            {
                red = green = blue = -1;
            }

            if (arrayType == int[].class)
            {
                pixelStride = biBitCount / 32;
                maxDataLength = biSizeImage / 4;
            } else if (arrayType == byte[].class)
            {
                pixelStride = biBitCount / 8;
                maxDataLength = biSizeImage;
            } else if (arrayType == short[].class)
            {
                pixelStride = biBitCount / 16;
                maxDataLength = biSizeImage / 2;

            } else
            {
                pixelStride = 0;
                maxDataLength = 0;
                throw new IllegalArgumentException();
            }

            lineStride = pixelStride * biWidth;

            // TODO: other biBitCount values?

            return new RGBFormat(new Dimension(biWidth, biHeight),
                    maxDataLength, arrayType, frameRate, biBitCount, red,
                    green, blue, pixelStride, lineStride, 1, // flipped,
                    1 // endian
            );

        } else if (fourcc.equals("YV12"))
        {
            return new YUVFormat(new Dimension(biWidth, biHeight), biSizeImage,
                    byte[].class, frameRate, YUVFormat.YUV_420, biWidth, // stride
                                                                         // y
                    biWidth / 2, // stride uv
                    0, // offset y
                    biWidth * biHeight + (biWidth * biHeight) / 4, // offset u
                    biWidth * biHeight // offset v
            );

        } else if (fourcc.equals("I420"))
        {
            return new YUVFormat(new Dimension(biWidth, biHeight), biSizeImage,
                    byte[].class, frameRate, YUVFormat.YUV_420, biWidth, // stride
                                                                         // y
                    biWidth / 2, // stride uv
                    0, // offset y
                       // u/v offsts swapped from YV12: (empirically determined)
                    biWidth * biHeight, // offset u
                    biWidth * biHeight + (biWidth * biHeight) / 4 // offset v
            );

        } else
        {
            return new AviVideoFormat(fourcc, new Dimension(biWidth, biHeight),
                    biSizeImage, arrayType, frameRate, biPlanes, biBitCount,
                    biSizeImage, biXPelsPerMeter, biYPelsPerMeter, biClrUsed,
                    biClrImportant, extraBytes);
        }

    }

    @Override
    public String toString()
    {
        return "Size = " + biWidth + " x " + biHeight + "	Planes = " + biPlanes
                + "	BitCount = " + biBitCount + "	FourCC = " + fourcc
                + "	SizeImage = " + biSizeImage + "\nClrUsed = " + biClrUsed
                + "\nClrImportant = " + biClrImportant + "\nExtraSize = "
                + extraSize + "\n";
    }
}
