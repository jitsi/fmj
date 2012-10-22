package net.sf.fmj.test.tracing;

import javax.media.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class TracingPlugIn extends TracingControls implements PlugIn
{
    public void close()
    {
        trace("close");
        // try
        // {
        // throw new Exception("stack trace");
        // }
        // catch (Exception e)
        // { e.printStackTrace();
        // }
    }

    public String getName()
    {
        trace("getName");
        return null;
    }

    public void open() throws ResourceUnavailableException
    {
        trace("open");
    }

    public void reset()
    {
        trace("reset");
    }

}
