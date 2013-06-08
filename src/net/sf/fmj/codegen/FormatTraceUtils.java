package net.sf.fmj.codegen;

import javax.media.*;

/**
 * For tracing format operations.
 *
 * @author Ken Larson
 *
 */
public class FormatTraceUtils
{
    // public static void trace(String msg, Format o)
    // {
    // System.out.println(msg + MediaCGUtils.formatToStr(o));
    // }
    //
    // public static void trace(String msg, Object o)
    // {
    // if (o instanceof Format)
    // trace(msg, (Format) o);
    // else
    // System.out.println(msg + o);
    // }
    //
    // public static void trace(String msg, boolean o)
    // {
    // System.out.println(msg + o);
    // }

    private static final boolean TRACE = false;

    public static void traceClone(Format f1, Format f2)
    {
        if (!TRACE)
            return;
        System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1)
                + ".clone(), " + MediaCGUtils.formatToStr(f2) + ");");
        // checkSizeCloned(f1, f2);
    }

    // private static void checkSizeNotCloned(Format f1, Format result)
    // {
    // if (f1 != null && result != null)
    // {
    // if (f1 instanceof VideoFormat && result instanceof VideoFormat)
    // {
    // VideoFormat fCast1 = (VideoFormat) f1;
    // VideoFormat fCastResult = (VideoFormat) result;
    //
    // if (fCast1.getSize() != null &&
    // fCast1.getSize().equals(fCastResult.getSize()) && fCast1.getSize() !=
    // fCastResult.getSize())
    // throw new RuntimeException("Size CLONED!");
    //
    //
    // }
    // }
    // }
    //
    // private static void checkSizeCloned(Format f1, Format result)
    // {
    // if (f1 != null && result != null)
    // {
    // if (f1 instanceof VideoFormat && result instanceof VideoFormat)
    // {
    // VideoFormat fCast1 = (VideoFormat) f1;
    // VideoFormat fCastResult = (VideoFormat) result;
    //
    // if (fCast1.getSize() != null && fCast1.getSize() ==
    // fCastResult.getSize())
    // throw new RuntimeException("Size NOT CLONED!");
    //
    //
    // }
    // }
    // }

    public static void traceEquals(Format f1, Format f2, boolean result)
    {
        if (!TRACE)
            return;
        // System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1) +
        // ".equals(" + MediaCGUtils.formatToStr(f2) + "), " + result + ");");
    }

    public static void traceIntersects(Format f1, Format f2, Format result)
    {
        if (!TRACE)
            return;
        System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1)
                + ".intersects(" + MediaCGUtils.formatToStr(f2) + "), "
                + MediaCGUtils.formatToStr(result) + ");");
        // checkSizeNotCloned(f1, result);
        // checkSizeNotCloned(f2, result);
    }

    public static void traceMatches(Format f1, Format f2, boolean result)
    {
        if (!TRACE)
            return;
        System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1)
                + ".matches(" + MediaCGUtils.formatToStr(f2) + "), " + result
                + ");");
    }

    public static void traceRelax(Format f1, Format result)
    {
        if (!TRACE)
            return;

        System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1)
                + ".relax(), " + MediaCGUtils.formatToStr(result) + ");");
    }

}
