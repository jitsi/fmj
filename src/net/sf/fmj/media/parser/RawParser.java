package net.sf.fmj.media.parser;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;

public abstract class RawParser extends BasicPlugIn implements Demultiplexer
{
    static final String NAME = "Raw parser";

    protected DataSource source;
    ContentDescriptor supported[];

    public RawParser()
    {
        supported = new ContentDescriptor[] { new ContentDescriptor(
                ContentDescriptor.RAW) };
    }

    @Override
    public Object[] getControls()
    {
        return source.getControls();
    }

    public Time getDuration()
    {
        return (source == null ? Duration.DURATION_UNKNOWN : source
                .getDuration());
    }

    public Time getMediaTime()
    {
        return Time.TIME_UNKNOWN;
    }

    public String getName()
    {
        return NAME;
    }

    /**
     * Lists the possible input formats supported by this plug-in.
     */
    public ContentDescriptor[] getSupportedInputContentDescriptors()
    {
        return supported;
    }

    public Track[] getTracks()
    {
        return null;
    }

    public boolean isPositionable()
    {
        return source instanceof Positionable;
    }

    public boolean isRandomAccess()
    {
        return source instanceof Positionable
                && ((Positionable) source).isRandomAccess();
    }

    /**
     * Resets the state of the plug-in. Typically at end of media or when media
     * is repositioned.
     */
    public void reset()
    {
    }

    public Time setPosition(Time when, int round)
    {
        if (source instanceof Positionable)
            return ((Positionable) source).setPosition(when, round);
        return when;
    }

}
