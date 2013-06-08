package com.sun.media.format;

import java.awt.*;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.codegen.*;
import net.sf.fmj.utility.*;

/**
 * TODO: test
 *
 * @author Ken Larson
 *
 */
public class AviVideoFormat extends VideoFormat
{
    protected int planes = NOT_SPECIFIED;
    protected int bitsPerPixel = NOT_SPECIFIED;
    protected int imageSize = NOT_SPECIFIED;
    protected int xPelsPerMeter = NOT_SPECIFIED;
    protected int yPelsPerMeter = NOT_SPECIFIED;
    protected int clrUsed = NOT_SPECIFIED;
    protected int clrImportant = NOT_SPECIFIED;
    protected byte[] codecSpecificHeader;

    public AviVideoFormat(String encoding)
    {
        super(encoding);
    }

    public AviVideoFormat(String encoding, Dimension size, int maxDataLength,
            Class<?> dataType, float frameRate, int planes, int bitsPerPixel,
            int imageSize, int xPelsPerMeter, int yPelsPerMeter, int clrUsed,
            int clrImportant, byte[] codecHeader)
    {
        super(encoding, size, maxDataLength, dataType, frameRate);
        this.planes = planes;
        this.bitsPerPixel = bitsPerPixel;
        this.imageSize = imageSize;
        this.xPelsPerMeter = xPelsPerMeter;
        this.yPelsPerMeter = yPelsPerMeter;
        this.clrUsed = clrUsed;
        this.clrImportant = clrImportant;
        this.codecSpecificHeader = codecHeader;

    }

    @Override
    public Object clone()
    {
        return new AviVideoFormat(encoding, size, maxDataLength, dataType,
                frameRate, planes, bitsPerPixel, imageSize, xPelsPerMeter,
                yPelsPerMeter, clrUsed, clrImportant, codecSpecificHeader);

    }

    @Override
    protected void copy(Format f)
    {
        super.copy(f);
        final AviVideoFormat oCast = (AviVideoFormat) f; // it has to be a
                                                         // WavAudioFormat, or
                                                         // ClassCastException
                                                         // will be thrown.
        this.planes = oCast.planes;
        this.bitsPerPixel = oCast.bitsPerPixel;
        this.imageSize = oCast.imageSize;
        this.xPelsPerMeter = oCast.xPelsPerMeter;
        this.yPelsPerMeter = oCast.yPelsPerMeter;
        this.clrUsed = oCast.clrUsed;
        this.clrImportant = oCast.clrImportant;
        this.codecSpecificHeader = oCast.codecSpecificHeader;
    }

    @Override
    public boolean equals(Object format)
    {
        if (!super.equals(format))
            return false;

        if (!(format instanceof AviVideoFormat))
        {
            return false;
        }

        final AviVideoFormat oCast = (AviVideoFormat) format;
        return this.planes == oCast.planes
                && this.bitsPerPixel == oCast.bitsPerPixel
                && this.imageSize == oCast.imageSize
                && this.xPelsPerMeter == oCast.xPelsPerMeter
                && this.yPelsPerMeter == oCast.yPelsPerMeter
                && this.clrUsed == oCast.clrUsed
                && this.clrImportant == oCast.clrImportant
                && FormatUtils.byteArraysEqual(this.codecSpecificHeader,
                        oCast.codecSpecificHeader);
    }

    public int getBitsPerPixel()
    {
        return bitsPerPixel;
    }

    public int getClrImportant()
    {
        return clrImportant;
    }

    public int getClrUsed()
    {
        return clrUsed;
    }

    public byte[] getCodecSpecificHeader()
    {
        return codecSpecificHeader;
    }

    public int getImageSize()
    {
        return imageSize;
    }

    public int getPlanes()
    {
        return planes;
    }

    public int getXPelsPerMeter()
    {
        return xPelsPerMeter;
    }

    public int getYPelsPerMeter()
    {
        return yPelsPerMeter;
    }

    @Override
    public Format intersects(Format other)
    {
        final Format result = super.intersects(other);

        if (other instanceof AviVideoFormat)
        {
            final AviVideoFormat resultCast = (AviVideoFormat) result;

            final AviVideoFormat oCast = (AviVideoFormat) other;
            if (getClass().isAssignableFrom(other.getClass()))
            {
                // "other" was cloned.

                if (FormatUtils.specified(this.planes))
                    resultCast.planes = this.planes;
                if (FormatUtils.specified(this.bitsPerPixel))
                    resultCast.bitsPerPixel = this.bitsPerPixel;
                if (FormatUtils.specified(this.imageSize))
                    resultCast.imageSize = this.imageSize;
                if (FormatUtils.specified(this.xPelsPerMeter))
                    resultCast.xPelsPerMeter = this.xPelsPerMeter;
                if (FormatUtils.specified(this.yPelsPerMeter))
                    resultCast.yPelsPerMeter = this.yPelsPerMeter;
                if (FormatUtils.specified(this.clrUsed))
                    resultCast.clrUsed = this.clrUsed;
                if (FormatUtils.specified(this.clrImportant))
                    resultCast.clrImportant = this.clrImportant;
                if (FormatUtils.specified(this.codecSpecificHeader))
                    resultCast.codecSpecificHeader = this.codecSpecificHeader;

            } else if (other.getClass().isAssignableFrom(getClass()))
            { // this was cloned

                if (FormatUtils.specified(this.planes))
                    resultCast.planes = oCast.planes;
                if (FormatUtils.specified(this.bitsPerPixel))
                    resultCast.bitsPerPixel = oCast.bitsPerPixel;
                if (FormatUtils.specified(this.imageSize))
                    resultCast.imageSize = oCast.imageSize;
                if (FormatUtils.specified(this.xPelsPerMeter))
                    resultCast.xPelsPerMeter = oCast.xPelsPerMeter;
                if (FormatUtils.specified(this.yPelsPerMeter))
                    resultCast.yPelsPerMeter = oCast.yPelsPerMeter;
                if (FormatUtils.specified(this.clrUsed))
                    resultCast.clrUsed = oCast.clrUsed;
                if (FormatUtils.specified(this.clrImportant))
                    resultCast.clrImportant = oCast.clrImportant;
                if (!FormatUtils.specified(resultCast.codecSpecificHeader))
                    resultCast.codecSpecificHeader = oCast.codecSpecificHeader;

            }
        }
        return result;
    }

    @Override
    public boolean matches(Format format)
    {
        if (!super.matches(format))
            return false;

        if (!(format instanceof AviVideoFormat))
            return true;

        final AviVideoFormat oCast = (AviVideoFormat) format;

        return FormatUtils.matches(this.planes, oCast.planes)
                && FormatUtils.matches(this.bitsPerPixel, oCast.bitsPerPixel)
                && FormatUtils.matches(this.imageSize, oCast.imageSize)
                && FormatUtils.matches(this.xPelsPerMeter, oCast.xPelsPerMeter)
                && FormatUtils.matches(this.yPelsPerMeter, oCast.yPelsPerMeter)
                && FormatUtils.matches(this.clrUsed, oCast.clrUsed)
                && FormatUtils.matches(this.clrImportant, oCast.clrImportant);// &&
        // FormatUtils.matches(this.codecSpecificHeader,
        // oCast.codecSpecificHeader);

    }

    @Override
    public Format relax()
    {
        final AviVideoFormat result = (AviVideoFormat) super.relax();
        result.imageSize = NOT_SPECIFIED;
        FormatTraceUtils.traceRelax(this, result);

        return result;
    }

    @Override
    public String toString()
    {
        final int extraBytes = codecSpecificHeader == null ? 0
                : codecSpecificHeader.length;
        return super.toString() + " " + extraBytes + " extra bytes";
    }
}
