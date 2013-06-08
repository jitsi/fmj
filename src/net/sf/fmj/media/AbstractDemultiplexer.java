package net.sf.fmj.media;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

/**
 * Abstract base class to implement Demultiplexer.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractDemultiplexer extends AbstractPlugIn implements
        Demultiplexer
{
    public Time getDuration()
    {
        return DURATION_UNKNOWN;
    }

    public Time getMediaTime()
    {
        return Time.TIME_UNKNOWN;
    }

    public abstract ContentDescriptor[] getSupportedInputContentDescriptors();

    public abstract Track[] getTracks() throws IOException, BadHeaderException;

    public boolean isPositionable()
    {
        return false;
    }

    public boolean isRandomAccess()
    {
        return false;
    }

    // subclasses must override if they override isPositionable.
    public Time setPosition(Time where, int rounding)
    {
        return Time.TIME_UNKNOWN;
        // TODO returning null will cause this:
        // Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException
        // at
        // com.sun.media.BasicSourceModule.setPosition(BasicSourceModule.java:474)
        // interestingly, BasicSourceModule will call setPosition even if
        // isPositionable
        // returns false.
    }

    public abstract void setSource(DataSource source) throws IOException,
            IncompatibleSourceException;

    public void start() throws IOException
    {
    }

    public void stop()
    {
    }

}
