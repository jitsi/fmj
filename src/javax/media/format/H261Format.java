package javax.media.format;

import javax.media.*;

import net.sf.fmj.codegen.*;
import net.sf.fmj.utility.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/format/H261Format.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 *
 */
public class H261Format extends VideoFormat
{
    protected int stillImageTransmission = NOT_SPECIFIED;

    private static String ENCODING = H261;

    static
    { // for Serializable compatibility.
    }

    public H261Format()
    {
        super(ENCODING);
        dataType = Format.byteArray;

    }

    public H261Format(java.awt.Dimension size, int maxDataLength,
            Class<?> dataType, float frameRate, int stillImageTransmission)
    {
        super(ENCODING, size, maxDataLength, dataType, frameRate);
        this.stillImageTransmission = stillImageTransmission;
    }

    @Override
    public Object clone()
    {
        return new H261Format(FormatUtils.clone(size), maxDataLength, dataType,
                frameRate, stillImageTransmission);
    }

    @Override
    protected void copy(Format f)
    {
        super.copy(f);
        final H261Format oCast = (H261Format) f; // it has to be a H261Format,
                                                 // or ClassCastException will
                                                 // be thrown.
        this.stillImageTransmission = oCast.stillImageTransmission;

    }

    @Override
    public boolean equals(Object format)
    {
        if (!super.equals(format))
            return false;

        if (!(format instanceof H261Format))
        {
            return false;
        }

        final H261Format oCast = (H261Format) format;
        return this.stillImageTransmission == oCast.stillImageTransmission;
    }

    public int getStillImageTransmission()
    {
        return stillImageTransmission;
    }

    @Override
    public Format intersects(Format other)
    {
        final Format result = super.intersects(other);

        if (other instanceof H261Format)
        {
            final H261Format resultCast = (H261Format) result;

            final H261Format oCast = (H261Format) other;
            if (getClass().isAssignableFrom(other.getClass()))
            {
                // "other" was cloned.

                if (FormatUtils.specified(this.stillImageTransmission))
                    resultCast.stillImageTransmission = this.stillImageTransmission;

            } else if (other.getClass().isAssignableFrom(getClass()))
            { // this was cloned

                if (!FormatUtils.specified(resultCast.stillImageTransmission))
                    resultCast.stillImageTransmission = oCast.stillImageTransmission;

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

        if (!(format instanceof H261Format))
        {
            final boolean result = true;
            FormatTraceUtils.traceMatches(this, format, result);
            return result;
        }

        final H261Format oCast = (H261Format) format;

        final boolean result = FormatUtils.matches(this.stillImageTransmission,
                oCast.stillImageTransmission);

        FormatTraceUtils.traceMatches(this, format, result);

        return result;

    }

    @Override
    public String toString()
    {
        return "H.261 video format";
    }
}
