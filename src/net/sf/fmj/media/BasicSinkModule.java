package net.sf.fmj.media;

import javax.media.*;

/**
 * BasicSinkModule is the base class for the modules that are the end points of
 * a flow graph. Renderer modules and Multiplexer modules are two decendents of
 * it. It manages a clock that could be used as the time base of the entire
 * Player or Processor.
 */
public abstract class BasicSinkModule extends BasicModule
{
    private Clock clock;
    protected boolean prerolling = false;
    protected float rate = 1.0f;

    protected long stopTime = -1;

    public void doneReset()
    {
    }

    @Override
    public void doSetMediaTime(Time t)
    {
        if (clock != null)
            clock.setMediaTime(t);
    }

    @Override
    public float doSetRate(float r)
    {
        if (clock != null)
            rate = clock.setRate(r);
        else
            rate = r;
        return rate;
    }

    @Override
    public void doStart()
    {
        super.doStart();
        if (clock != null)
            clock.syncStart(clock.getTimeBase().getTime());
    }

    @Override
    public void doStop()
    {
        if (clock != null)
            clock.stop();
    }

    public Clock getClock()
    {
        return clock;
    }

    @Override
    public long getMediaNanoseconds()
    {
        if (clock != null)
            return clock.getMediaNanoseconds();
        else
            return controller.getMediaNanoseconds();
    }

    @Override
    public Time getMediaTime()
    {
        if (clock != null)
            return clock.getMediaTime();
        else
            return controller.getMediaTime();
    }

    public TimeBase getTimeBase()
    {
        if (clock != null)
            return clock.getTimeBase();
        else
            return controller.getTimeBase();
    }

    protected void setClock(Clock c)
    {
        clock = c;
    }

    /**
     * Enable prerolling.
     *
     * @param actual the media time when the prerolling starts. Let's say the
     * requested media time is set to 100 and the current location is set to 90
     * (the previous key frame where the parser can seek to). The parameter
     * <tt>actual</tt> should be set to 90.
     */
    public void setPreroll(long wanted, long actual)
    {
        // If the time actually set on the parser is less than
        // the time requested, we'll preroll the media
        // to the requested time.
        if (actual < wanted)
            prerolling = true;
    }

    public void setStopTime(Time t)
    {
        if (t == Clock.RESET)
            stopTime = -1;
        else
            stopTime = t.getNanoseconds();
    }

    public void setTimeBase(TimeBase tb) throws IncompatibleTimeBaseException
    {
        if (clock != null)
            clock.setTimeBase(tb);
    }

    public void triggerReset()
    {
    }

}
