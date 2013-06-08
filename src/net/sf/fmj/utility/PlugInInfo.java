package net.sf.fmj.utility;

import javax.media.*;

/**
 * Holds information about a plugin.
 *
 * @author Ken Larson
 *
 */
public class PlugInInfo
{
    public final String className;
    public final int type;
    public final Format[] in;
    public final Format[] out;

    public PlugInInfo(String className, Format[] in, Format[] out, int type)
    {
        super();
        this.className = className;
        this.type = type;
        this.in = in;
        this.out = out;
    }
}
