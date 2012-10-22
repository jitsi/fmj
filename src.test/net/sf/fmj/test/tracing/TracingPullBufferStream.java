package net.sf.fmj.test.tracing;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class TracingPullBufferStream implements PullBufferStream
{
    private final StringBuffer b = new StringBuffer();

    public boolean endOfStream()
    {
        b.append("endOfStream\n");
        return false;
    }

    public ContentDescriptor getContentDescriptor()
    {
        b.append("getContentDescriptor\n");
        return null;
    }

    public long getContentLength()
    {
        b.append("getContentLength\n");
        return 0;
    }

    public Object getControl(String controlType)
    {
        b.append("getControl\n");
        return null;
    }

    public Object[] getControls()
    {
        b.append("getControls\n");
        return null;
    }

    public Format getFormat()
    {
        b.append("getFormat\n");
        return null;
    }

    public StringBuffer getStringBuffer()
    {
        return b;
    }

    public void read(Buffer buffer) throws IOException
    {
        b.append("read\n");
    }

    public boolean willReadBlock()
    {
        b.append("willReadBlock\n");
        return false;
    }

}
