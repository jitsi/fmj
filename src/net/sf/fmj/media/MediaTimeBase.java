package net.sf.fmj.media;

import javax.media.*;

/**
 * This is the abstract base class to create a time base out of the media time
 * of a component. e.g., this can be used to generate a time base from an audio
 * device with reports time ticks as samples are played. A TimeBase ticks even
 * when the media has stopped. This class takes care of that by internally
 * maintaining a system time base that takes over when the media has ended. A
 * TimeBase needs to be monotonically increasing. If the media time is set or
 * wrapped around, this class will takes care of that.
 */
abstract public class MediaTimeBase implements TimeBase
{
    long origin = 0;
    long offset = 0;
    long time = 0;
    TimeBase systemTimeBase = null;

    public MediaTimeBase()
    {
        mediaStopped();
    }

    abstract public long getMediaTime();

    public synchronized long getNanoseconds()
    {
        long t;
        if (systemTimeBase != null)
            time = origin + systemTimeBase.getNanoseconds() - offset;
        else
            time = origin + getMediaTime() - offset;
        return time;
    }

    public Time getTime()
    {
        return new Time(getNanoseconds());
    }

    public synchronized void mediaStarted()
    {
        systemTimeBase = null;
        offset = getMediaTime();
        origin = time;
    }

    public synchronized void mediaStopped()
    {
        systemTimeBase = new SystemTimeBase();
        offset = systemTimeBase.getNanoseconds();
        origin = time;
    }

}
