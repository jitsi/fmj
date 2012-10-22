package net.sf.fmj.test.compat.plugins;

import javax.media.*;

/**
 * Copy of javax.media.pim.PlugInInfo, used to test.
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

}
