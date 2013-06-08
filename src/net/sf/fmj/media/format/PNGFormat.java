package net.sf.fmj.media.format;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.codegen.*;
import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * PNG video format. Used for MPNG, which is like MJPEG but with PNG.
 *
 * @author Ken Larson
 *
 */
public class PNGFormat extends VideoFormat
{
    public PNGFormat()
    {
        super(BonusVideoFormatEncodings.PNG);
        dataType = Format.byteArray;

    }

    public PNGFormat(java.awt.Dimension size, int maxDataLength,
            Class<?> dataType, float frameRate)
    {
        super(BonusVideoFormatEncodings.PNG, size, maxDataLength, dataType,
                frameRate);

    }

    @Override
    public Object clone()
    {
        return new PNGFormat(FormatUtils.clone(size), maxDataLength, dataType,
                frameRate);
    }

    @Override
    protected void copy(Format f)
    {
        super.copy(f);
        final PNGFormat oCast = (PNGFormat) f; // it has to be a PNGFormat, or
                                               // ClassCastException will be
                                               // thrown.
    }

    @Override
    public boolean equals(Object format)
    {
        if (!super.equals(format))
            return false;

        if (!(format instanceof PNGFormat))
        {
            return false;
        }

        final PNGFormat oCast = (PNGFormat) format;
        return true;
    }

    @Override
    public Format intersects(Format other)
    {
        final Format result = super.intersects(other);

        if (other instanceof PNGFormat)
        {
            final PNGFormat resultCast = (PNGFormat) result;

            final PNGFormat oCast = (PNGFormat) other;
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

        if (!(format instanceof PNGFormat))
        {
            final boolean result = true;
            FormatTraceUtils.traceMatches(this, format, result);
            return result;
        }

        final PNGFormat oCast = (PNGFormat) format;

        final boolean result = true;

        FormatTraceUtils.traceMatches(this, format, result);

        return result;
    }

    @Override
    public String toString()
    {
        final StringBuffer b = new StringBuffer();
        b.append("PNG video format:");
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
