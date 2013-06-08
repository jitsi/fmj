package net.sf.fmj.media;

import javax.media.*;

import net.sf.fmj.utility.*;

/**
 * Abstract base class to implement Codec.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractCodec extends AbstractPlugIn implements Codec
{
    protected Format inputFormat = null;
    protected Format outputFormat = null;
    protected boolean opened = false;
    protected Format[] inputFormats = new Format[0];

    protected boolean checkInputBuffer(Buffer b)
    {
        return true; // TODO
    }

    protected final void dump(String label, Buffer buffer)
    {
        System.out.println(label + ": "
                + LoggingStringUtils.bufferToStr(buffer));

    }

    protected Format getInputFormat()
    {
        return inputFormat;
    }

    protected Format getOutputFormat()
    {
        return outputFormat;
    }

    public Format[] getSupportedInputFormats()
    {
        return inputFormats;
    }

    public abstract Format[] getSupportedOutputFormats(Format input);

    protected boolean isEOM(Buffer b)
    {
        return b.isEOM();
    }

    public abstract int process(Buffer input, Buffer output);

    protected void propagateEOM(Buffer b)
    {
        b.setEOM(true);
    }

    public Format setInputFormat(Format format)
    {
        this.inputFormat = format;
        return inputFormat;
    }

    public Format setOutputFormat(Format format)
    {
        this.outputFormat = format;
        return outputFormat;
    }

}
