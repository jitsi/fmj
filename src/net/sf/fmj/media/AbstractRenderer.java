package net.sf.fmj.media;

import javax.media.*;

/**
 * Abstract implementation of Renderer, useful for subclassing.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractRenderer extends AbstractPlugIn implements
        Renderer
{
    protected Format inputFormat;

    public abstract Format[] getSupportedInputFormats();

    public abstract int process(Buffer buffer);

    public Format setInputFormat(Format format)
    {
        this.inputFormat = format;
        return inputFormat;
    }

    public void start()
    {
    }

    public void stop()
    {
    }

}
