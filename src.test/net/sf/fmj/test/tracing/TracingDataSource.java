package net.sf.fmj.test.tracing;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class TracingDataSource extends DataSource
{
    private final StringBuffer b = new StringBuffer();

    // @Override
    @Override
    public void connect() throws IOException
    {
        b.append("connect\n");
    }

    // @Override
    @Override
    public void disconnect()
    {
        b.append("disconnect\n");
    }

    // @Override
    @Override
    public String getContentType()
    {
        b.append("getContentType\n");
        return null;
    }

    // @Override
    @Override
    public Object getControl(String controlType)
    {
        b.append("getControl\n");
        return null;
    }

    // @Override
    @Override
    public Object[] getControls()
    {
        b.append("getControls\n");
        return null;
    }

    // @Override
    @Override
    public Time getDuration()
    {
        b.append("getDuration\n");
        return null;
    }

    public StringBuffer getStringBuffer()
    {
        return b;
    }

    // @Override
    @Override
    public void start() throws IOException
    {
        b.append("start\n");
    }

    // @Override
    @Override
    public void stop() throws IOException
    {
        b.append("stop\n");
    }

}
