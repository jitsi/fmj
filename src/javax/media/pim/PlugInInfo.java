package javax.media.pim;

import javax.media.*;

/**
 * Part of internal implementation of javax.media.PlugInManager.
 *
 * @author Ken Larson
 *
 */
class PlugInInfo
{
    public String className;
    public Format[] inputFormats;
    public Format[] outputFormats;

    public PlugInInfo(String name, Format[] formats, Format[] formats2)
    {
        super();
        className = name;
        inputFormats = formats;
        outputFormats = formats2;
    }

    @Override
    public boolean equals(Object other)
    {
        return (other instanceof PlugInInfo && (className == ((PlugInInfo) other).className || className != null
                && className.equals(((PlugInInfo) other).className)));
    }

    @Override
    public int hashCode()
    {
        return className.hashCode();
    }
}
