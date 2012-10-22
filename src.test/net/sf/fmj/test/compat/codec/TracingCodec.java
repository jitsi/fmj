package net.sf.fmj.test.compat.codec;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

/**
 * net.sf.fmj.test.compat.codec.TracingCodec
 * 
 * @author Ken Larson
 * 
 */
public class TracingCodec implements Codec
{
    private static void trace(String s)
    {
        System.out.println(s);
    }

    public void close()
    {
        trace("close");
    }

    public Object getControl(String controlType)
    {
        trace("getControl");
        return null;
    }

    public Object[] getControls()
    {
        trace("getControls");
        return null;
    }

    public String getName()
    {
        trace("getName");
        return "Tracing Codec";
    }

    public Format[] getSupportedInputFormats()
    {
        trace("getSupportedInputFormats");
        return new Format[] { new ContentDescriptor("zzz") };
    }

    public Format[] getSupportedOutputFormats(Format input)
    {
        trace("getSupportedOutputFormats " + input);
        return new Format[] { new RGBFormat() };
    }

    public void open() throws ResourceUnavailableException
    {
        trace("open");
    }

    public int process(Buffer input, Buffer output)
    {
        trace("process");
        return 0;
    }

    public void reset()
    {
        trace("reset");
    }

    public Format setInputFormat(Format format)
    {
        trace("setInputFormat " + format);
        return null;
    }

    public Format setOutputFormat(Format format)
    {
        trace("setOutputFormat " + format);
        return null;
    }

}
