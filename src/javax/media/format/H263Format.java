package javax.media.format;

import javax.media.*;

import net.sf.fmj.codegen.*;
import net.sf.fmj.utility.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/format/H263Format.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 *
 */
public class H263Format extends VideoFormat
{
    private static String ENCODING = H263;

    protected int advancedPrediction = NOT_SPECIFIED;

    protected int arithmeticCoding = NOT_SPECIFIED;

    protected int errorCompensation = NOT_SPECIFIED;

    protected int hrDB = NOT_SPECIFIED; // strange, defaults to zero.

    protected int pbFrames = NOT_SPECIFIED;

    protected int unrestrictedVector = NOT_SPECIFIED;

    static
    { // for Serializable compatibility.
    }

    public H263Format()
    {
        super(ENCODING);
        dataType = Format.byteArray;

    }

    public H263Format(java.awt.Dimension size, int maxDataLength,
            Class<?> dataType, float frameRate, int advancedPrediction,
            int arithmeticCoding, int errorCompensation, int hrDB,
            int pbFrames, int unrestrictedVector)
    {
        super(ENCODING, size, maxDataLength, dataType, frameRate);
        this.advancedPrediction = advancedPrediction;
        this.arithmeticCoding = arithmeticCoding;
        this.errorCompensation = errorCompensation;
        this.hrDB = hrDB;
        this.pbFrames = pbFrames;
        this.unrestrictedVector = unrestrictedVector;

    }

    @Override
    public Object clone()
    {
        return new H263Format(FormatUtils.clone(size), maxDataLength, dataType,
                frameRate, advancedPrediction, arithmeticCoding,
                errorCompensation, hrDB, pbFrames, unrestrictedVector);
    }

    @Override
    protected void copy(Format f)
    {
        super.copy(f);
        final H263Format oCast = (H263Format) f; // it has to be a H263Format,
                                                 // or ClassCastException will
                                                 // be thrown.
        this.advancedPrediction = oCast.advancedPrediction;
        this.arithmeticCoding = oCast.arithmeticCoding;
        this.errorCompensation = oCast.errorCompensation;
        this.hrDB = oCast.hrDB;
        this.pbFrames = oCast.pbFrames;
        this.unrestrictedVector = oCast.unrestrictedVector;

    }

    @Override
    public boolean equals(Object format)
    {
        if (!super.equals(format))
            return false;

        if (!(format instanceof H263Format))
        {
            return false;
        }

        final H263Format oCast = (H263Format) format;
        return this.advancedPrediction == oCast.advancedPrediction
                && this.arithmeticCoding == oCast.arithmeticCoding
                && this.errorCompensation == oCast.errorCompensation
                && this.hrDB == oCast.hrDB && this.pbFrames == oCast.pbFrames
                && this.unrestrictedVector == oCast.unrestrictedVector;
    }

    public int getAdvancedPrediction()
    {
        return advancedPrediction;
    }

    public int getArithmeticCoding()
    {
        return arithmeticCoding;
    }

    public int getErrorCompensation()
    {
        return errorCompensation;
    }

    public int getHrDB()
    {
        return hrDB;
    }

    public int getPBFrames()
    {
        return pbFrames;
    }

    public int getUnrestrictedVector()
    {
        return unrestrictedVector;
    }

    @Override
    public Format intersects(Format other)
    {
        final Format result = super.intersects(other);

        if (other instanceof H263Format)
        {
            final H263Format resultCast = (H263Format) result;

            final H263Format oCast = (H263Format) other;
            if (getClass().isAssignableFrom(other.getClass()))
            {
                // "other" was cloned.

                if (FormatUtils.specified(this.advancedPrediction))
                    resultCast.advancedPrediction = this.advancedPrediction;
                if (FormatUtils.specified(this.arithmeticCoding))
                    resultCast.arithmeticCoding = this.arithmeticCoding;
                if (FormatUtils.specified(this.errorCompensation))
                    resultCast.errorCompensation = this.errorCompensation;
                if (FormatUtils.specified(this.hrDB))
                    resultCast.hrDB = this.hrDB;
                if (FormatUtils.specified(this.pbFrames))
                    resultCast.pbFrames = this.pbFrames;
                if (FormatUtils.specified(this.unrestrictedVector))
                    resultCast.unrestrictedVector = this.unrestrictedVector;

            } else if (other.getClass().isAssignableFrom(getClass()))
            { // this was cloned

                if (!FormatUtils.specified(resultCast.advancedPrediction))
                    resultCast.advancedPrediction = oCast.advancedPrediction;
                if (!FormatUtils.specified(resultCast.arithmeticCoding))
                    resultCast.arithmeticCoding = oCast.arithmeticCoding;
                if (!FormatUtils.specified(resultCast.errorCompensation))
                    resultCast.errorCompensation = oCast.errorCompensation;
                if (!FormatUtils.specified(resultCast.hrDB))
                    resultCast.hrDB = oCast.hrDB;
                if (!FormatUtils.specified(resultCast.pbFrames))
                    resultCast.pbFrames = oCast.pbFrames;
                if (!FormatUtils.specified(resultCast.unrestrictedVector))
                    resultCast.unrestrictedVector = oCast.unrestrictedVector;

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

        if (!(format instanceof H263Format))
        {
            final boolean result = true;
            FormatTraceUtils.traceMatches(this, format, result);
            return result;
        }

        final H263Format oCast = (H263Format) format;

        final boolean result = FormatUtils.matches(this.advancedPrediction,
                oCast.advancedPrediction)
                && FormatUtils.matches(this.arithmeticCoding,
                        oCast.arithmeticCoding)
                && FormatUtils.matches(this.errorCompensation,
                        oCast.errorCompensation)
                && FormatUtils.matches(this.hrDB, oCast.hrDB)
                && FormatUtils.matches(this.pbFrames, oCast.pbFrames)
                && FormatUtils.matches(this.unrestrictedVector,
                        oCast.unrestrictedVector);

        FormatTraceUtils.traceMatches(this, format, result);

        return result;
    }

    @Override
    public String toString()
    {
        return "H.263 video format";
    }
}
