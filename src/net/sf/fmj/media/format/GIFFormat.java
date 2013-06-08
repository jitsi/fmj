package net.sf.fmj.media.format;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.codegen.*;
import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * GIF video format. Used for MGIF, which is like MJPEG but with GIF.
 *
 * @author Ken Larson
 *
 */
public class GIFFormat extends VideoFormat
{
    public GIFFormat()
    {
        super(BonusVideoFormatEncodings.GIF);
        dataType = Format.byteArray;

    }

    public GIFFormat(java.awt.Dimension size, int maxDataLength,
            Class<?> dataType, float frameRate)
    {
        super(BonusVideoFormatEncodings.GIF, size, maxDataLength, dataType,
                frameRate);

    }

    @Override
    public Object clone()
    {
        return new GIFFormat(FormatUtils.clone(size), maxDataLength, dataType,
                frameRate);
    }

    @Override
    protected void copy(Format f)
    {
        super.copy(f);
        final GIFFormat oCast = (GIFFormat) f; // it has to be a GIFFormat, or
                                               // ClassCastException will be
                                               // thrown.
    }

    @Override
    public boolean equals(Object format)
    {
        if (!super.equals(format))
            return false;

        if (!(format instanceof GIFFormat))
        {
            return false;
        }

        final GIFFormat oCast = (GIFFormat) format;
        return true;
    }

    @Override
    public Format intersects(Format other)
    {
        final Format result = super.intersects(other);

        if (other instanceof GIFFormat)
        {
            final GIFFormat resultCast = (GIFFormat) result;

            final GIFFormat oCast = (GIFFormat) other;
            if (getClass().isAssignableFrom(other.getClass()))
            {
                // "other" was cloned.
            } else if (other.getClass().isAssignableFrom(getClass()))
            { // this was cloned
            }
        }

        FormatTraceUtils.traceIntersects(this, other, result);

        return result;
    }

    @Override
    public boolean matches(Format format)
    {
        if (!super.matches(format))
        {
            FormatTraceUtils.traceMatches(this, format, false);
            return false;
        }

        if (!(format instanceof GIFFormat))
        {
            final boolean result = true;
            FormatTraceUtils.traceMatches(this, format, result);
            return result;
        }

        final GIFFormat oCast = (GIFFormat) format;

        final boolean result = true;

        FormatTraceUtils.traceMatches(this, format, result);

        return result;
    }

    @Override
    public String toString()
    {
        final StringBuffer b = new StringBuffer();
        b.append("GIF video format:");
        if (FormatUtils.specified(size))
            b.append(" size = " + size.width + "x" + size.height);
        if (FormatUtils.specified(frameRate))
            b.append(" FrameRate = " + frameRate);
        if (FormatUtils.specified(maxDataLength))
            b.append(" maxDataLength = " + maxDataLength);
        if (FormatUtils.specified(dataType))
            b.append(" dataType = " + dataType);
        return b.toString();
    }
}
