package net.sf.fmj.test.tracing;

import javax.media.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class TracingControls implements Controls
{
    protected final StringBuffer b = new StringBuffer();

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

    public StringBuffer getStringBuffer()
    {
        return b;
    }

    protected void trace(String s)
    {
        // System.out.println(s);
        b.append(s + "\n");
    }

}
