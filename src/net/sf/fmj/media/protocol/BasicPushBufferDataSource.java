package net.sf.fmj.media.protocol;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

public abstract class BasicPushBufferDataSource extends PushBufferDataSource
{
    protected Object[] controls = new Object[0];
    protected boolean started = false;
    protected String contentType = "content/unknown";
    protected boolean connected = false;
    protected Time duration = DURATION_UNKNOWN;

    @Override
    public void connect() throws IOException
    {
        if (connected)
            return;
        connected = true;
    }

    @Override
    public void disconnect()
    {
        try
        {
            if (started)
                stop();
        } catch (IOException e)
        {
        }
        connected = false;
    }

    @Override
    public String getContentType()
    {
        if (!connected)
        {
            System.err.println("Error: DataSource not connected");
            return null;
        }
        return contentType;
    }

    @Override
    public Object getControl(String controlType)
    {
        try
        {
            Class<?> cls = Class.forName(controlType);
            Object cs[] = getControls();
            for (int i = 0; i < cs.length; i++)
            {
                if (cls.isInstance(cs[i]))
                    return cs[i];
            }
            return null;

        } catch (Exception e)
        { // no such controlType or such control
            return null;
        }
    }

    @Override
    public Object[] getControls()
    {
        return controls;
    }

    @Override
    public Time getDuration()
    {
        return duration;
    }

    @Override
    public void start() throws IOException
    {
        // we need to throw error if connect() has not been called
        if (!connected)
            throw new java.lang.Error(
                    "DataSource must be connected before it can be started");
        if (started)
            return;
        started = true;
    }

    @Override
    public void stop() throws IOException
    {
        if ((!connected) || (!started))
            return;
        started = false;
    }
}
