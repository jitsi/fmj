package javax.media.format;

import javax.media.*;

import net.sf.fmj.codegen.*;
import net.sf.fmj.utility.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/format/IndexedColorFormat.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 *
 */
public class IndexedColorFormat extends VideoFormat
{
    protected int lineStride;
    protected byte[] redValues;
    protected byte[] greenValues;
    protected byte[] blueValues;
    protected int mapSize;

    private static String ENCODING = IRGB;

    static
    { // for Serializable compatibility.
    }

    public IndexedColorFormat(java.awt.Dimension size, int maxDataLength,
            Class<?> dataType, float frameRate, int lineStride, int mapSize,
            byte[] red, byte[] green, byte[] blue)
    {
        super(ENCODING, size, maxDataLength, dataType, frameRate);
        this.lineStride = lineStride;
        this.mapSize = mapSize;
        this.redValues = red;
        this.greenValues = green;
        this.blueValues = blue;

    }

    @Override
    public Object clone()
    {
        return new IndexedColorFormat(FormatUtils.clone(size), maxDataLength,
                dataType, frameRate, lineStride, mapSize, redValues,
                greenValues, blueValues);
    }

    @Override
    protected void copy(Format f)
    {
        super.copy(f);
        final IndexedColorFormat oCast = (IndexedColorFormat) f; // it has to be
                                                                 // a
                                                                 // IndexedColorFormat,
                                                                 // or
                                                                 // ClassCastException
                                                                 // will be
                                                                 // thrown.
        this.lineStride = oCast.lineStride;
        this.mapSize = oCast.mapSize;
        this.redValues = oCast.redValues;
        this.greenValues = oCast.greenValues;
        this.blueValues = oCast.blueValues;
    }

    @Override
    public boolean equals(Object format)
    {
        if (!super.equals(format))
            return false;

        if (!(format instanceof IndexedColorFormat))
        {
            return false;
        }

        final IndexedColorFormat oCast = (IndexedColorFormat) format;
        return this.lineStride == oCast.lineStride
                && this.mapSize == oCast.mapSize
                && this.redValues == oCast.redValues
                && // strange, compare arrays using ==. This makes it so that
                   // serializing and deserializing this obj results in an obj
                   // that is not "equal"
                this.greenValues == oCast.greenValues
                && this.blueValues == oCast.blueValues;
    }

    public byte[] getBlueValues()
    {
        return blueValues;
    }

    public byte[] getGreenValues()
    {
        return greenValues;
    }

    public int getLineStride()
    {
        return lineStride;
    }

    public int getMapSize()
    {
        return mapSize;
    }

    public byte[] getRedValues()
    {
        return redValues;
    }

    @Override
    public Format intersects(Format other)
    {
        final Format result = super.intersects(other);

        if (other instanceof IndexedColorFormat)
        {
            final IndexedColorFormat resultCast = (IndexedColorFormat) result;

            final IndexedColorFormat oCast = (IndexedColorFormat) other;
            if (getClass().isAssignableFrom(other.getClass()))
            {
                // "other" was cloned.

                if (FormatUtils.specified(this.lineStride))
                    resultCast.lineStride = this.lineStride;
                if (FormatUtils.specified(this.mapSize))
                    resultCast.mapSize = this.mapSize;
                if (FormatUtils.specified(this.redValues))
                    resultCast.redValues = this.redValues;
                if (FormatUtils.specified(this.greenValues))
                    resultCast.greenValues = this.greenValues;
                if (FormatUtils.specified(this.blueValues))
                    resultCast.blueValues = this.blueValues;

            } else if (other.getClass().isAssignableFrom(getClass()))
            { // this was cloned

                if (!FormatUtils.specified(resultCast.lineStride))
                    resultCast.lineStride = oCast.lineStride;
                if (!FormatUtils.specified(resultCast.mapSize))
                    resultCast.mapSize = oCast.mapSize;
                if (!FormatUtils.specified(resultCast.redValues))
                    resultCast.redValues = oCast.redValues;
                if (!FormatUtils.specified(resultCast.greenValues))
                    resultCast.greenValues = oCast.greenValues;
                if (!FormatUtils.specified(resultCast.blueValues))
                    resultCast.blueValues = oCast.blueValues;

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

        if (!(format instanceof IndexedColorFormat))
        {
            final boolean result = true;
            FormatTraceUtils.traceMatches(this, format, result);
            return result;
        }

        final IndexedColorFormat oCast = (IndexedColorFormat) format;

        final boolean result = FormatUtils.matches(oCast.lineStride,
                this.lineStride)
                && FormatUtils.matches(oCast.mapSize, this.mapSize)
                && FormatUtils.matches(oCast.redValues, this.redValues)
                && FormatUtils.matches(oCast.greenValues, this.greenValues)
                && FormatUtils.matches(oCast.blueValues, this.blueValues);
        FormatTraceUtils.traceMatches(this, format, result);

        return result;

    }

    @Override
    public Format relax()
    {
        final IndexedColorFormat result = (IndexedColorFormat) super.relax();
        result.lineStride = NOT_SPECIFIED;
        return result;
    }
}
