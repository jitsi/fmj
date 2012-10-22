package net.sf.fmj.test.tracing;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class TracingPullBufferDataSource extends
        javax.media.protocol.PullBufferDataSource
{
    private final StringBuffer b = new StringBuffer();

    public PullBufferStream[] streams;

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

    // @Override
    @Override
    public MediaLocator getLocator()
    {
        b.append("getLocator\n");
        return super.getLocator();
    }

    // @Override
    @Override
    public PullBufferStream[] getStreams()
    {
        b.append("getStreams\n");
        return streams;
    }

    public StringBuffer getStringBuffer()
    {
        return b;
    }

    // @Override
    @Override
    protected void initCheck()
    {
        b.append("initCheck\n");
        super.initCheck();
    }

    // @Override
    @Override
    public void setLocator(MediaLocator source)
    {
        b.append("setLocator\n");
        super.setLocator(source);
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

    //

}
