package javax.media.format;

import javax.media.*;

import net.sf.fmj.codegen.*;
import net.sf.fmj.utility.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/format/JPEGFormat.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 *
 */
public class JPEGFormat extends VideoFormat
{
    public static final int DEC_422 = 0;
    public static final int DEC_420 = 1;
    public static final int DEC_444 = 2;
    public static final int DEC_402 = 3;
    public static final int DEC_411 = 4;

    int qFactor = NOT_SPECIFIED;
    int decimation = NOT_SPECIFIED;

    public JPEGFormat()
    {
        super(JPEG);
        dataType = Format.byteArray;

    }

    public JPEGFormat(java.awt.Dimension size, int maxDataLength,
            Class<?> dataType, float frameRate, int q, int dec)
    {
        super(JPEG, size, maxDataLength, dataType, frameRate);
        this.qFactor = q;
        this.decimation = dec;

    }

    @Override
    public Object clone()
    {
        return new JPEGFormat(FormatUtils.clone(size), maxDataLength, dataType,
                frameRate, qFactor, decimation);
    }

    @Override
    protected void copy(Format f)
    {
        super.copy(f);
        final JPEGFormat oCast = (JPEGFormat) f; // it has to be a JPEGFormat,
                                                 // or ClassCastException will
                                                 // be thrown.
        this.qFactor = oCast.qFactor;
        this.decimation = oCast.decimation;
    }

    @Override
    public boolean equals(Object format)
    {
        if (!super.equals(format))
            return false;

        if (!(format instanceof JPEGFormat))
        {
            return false;
        }

        final JPEGFormat oCast = (JPEGFormat) format;
        return this.qFactor == oCast.qFactor
                && this.decimation == oCast.decimation;
    }

    public int getDecimation()
    {
        return decimation;
    }

    public int getQFactor()
    {
        return qFactor;
    }

    @Override
    public Format intersects(Format other)
    {
        final Format result = super.intersects(other);

        if (other instanceof JPEGFormat)
        {
            final JPEGFormat resultCast = (JPEGFormat) result;

            final JPEGFormat oCast = (JPEGFormat) other;
            if (getClass().isAssignableFrom(other.getClass()))
            {
                // "other" was cloned.

                if (FormatUtils.specified(this.qFactor))
                    resultCast.qFactor = this.qFactor;
                if (FormatUtils.specified(this.decimation))
                    resultCast.decimation = this.decimation;

            } else if (other.getClass().isAssignableFrom(getClass()))
            { // this was cloned

                if (!FormatUtils.specified(resultCast.qFactor))
                    resultCast.qFactor = oCast.qFactor;
                if (!FormatUtils.specified(resultCast.decimation))
                    resultCast.decimation = oCast.decimation;

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

        if (!(format instanceof JPEGFormat))
        {
            final boolean result = true;
            FormatTraceUtils.traceMatches(this, format, result);
            return result;
        }

        final JPEGFormat oCast = (JPEGFormat) format;

        final boolean result = FormatUtils.matches(oCast.qFactor, this.qFactor)
                && FormatUtils.matches(oCast.decimation, this.decimation);

        FormatTraceUtils.traceMatches(this, format, result);

        return result;
    }

    @Override
    public String toString()
    {
        // examples:
        // jpeg video format: dataType = class [B
        // jpeg video format: size = 1x1 FrameRate = 1.0 maxDataLength = 1000
        // dataType = class [S q factor = 2 decimation = 3
        // jpeg video format: size = 1x1 FrameRate = 1.0 maxDataLength = 1000
        // dataType = class [S decimation = 3

        final StringBuffer b = new StringBuffer();
        b.append("jpeg video format:");
        if (FormatUtils.specified(size))
            b.append(" size = " + size.width + "x" + size.height);
        if (FormatUtils.specified(frameRate))
            b.append(" FrameRate = " + frameRate);
        if (FormatUtils.specified(maxDataLength))
            b.append(" maxDataLength = " + maxDataLength);
        if (FormatUtils.specified(dataType))
            b.append(" dataType = " + dataType);
        if (FormatUtils.specified(qFactor))
            b.append(" q factor = " + qFactor);
        if (FormatUtils.specified(decimation))
            b.append(" decimation = " + decimation);

        return b.toString();
    }
}
