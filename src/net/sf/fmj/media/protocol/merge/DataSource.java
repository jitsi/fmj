package net.sf.fmj.media.protocol.merge;

import java.io.*;

import javax.media.*;

/**
 * Protocol handler for "merge" protocol, allowing multiple merged data sources
 * to be specified with a single URL. Does nothing but return "merge" content
 * type, for a special content handler to pick up on (
 * {@link net.sf.fmj.media.content.merge.Handler}).
 *
 * @author Ken Larson
 *
 */
public class DataSource extends javax.media.protocol.DataSource
{
    @Override
    public void connect() throws IOException
    {
    }

    @Override
    public void disconnect()
    {
    }

    @Override
    public String getContentType()
    {
        return "merge";
    }

    @Override
    public Object getControl(String controlType)
    {
        return null;
    }

    @Override
    public Object[] getControls()
    {
        return new Object[0];
    }

    @Override
    public Time getDuration()
    {
        return Duration.DURATION_UNKNOWN;
    }

    @Override
    public void start() throws IOException
    {
    }

    @Override
    public void stop() throws IOException
    {
    }

}
