package net.sf.fmj.media.rtp.util;

import java.util.*;

import javax.media.*;

import net.sf.fmj.media.*;

public class RTPTimeBase implements TimeBase
{
    static Vector timeBases = new Vector();

    static int SSRC_UNDEFINED = 0;

    public static RTPTimeBase find(RTPTimeReporter rtptimereporter, String s)
    {
        RTPTimeBase rtptimebase2;
        synchronized (timeBases)
        {
            RTPTimeBase rtptimebase1 = null;
            for (int i = 0; i < timeBases.size(); i++)
            {
                RTPTimeBase rtptimebase = (RTPTimeBase) timeBases.elementAt(i);
                // damencho safty check for null
                if (rtptimebase.cname == null || !rtptimebase.cname.equals(s))
                    continue;
                rtptimebase1 = rtptimebase;
                break;
            }

            if (rtptimebase1 == null)
            {
                Log.comment("Created RTP time base for session: " + s + "\n");
                rtptimebase1 = new RTPTimeBase(s);
                timeBases.addElement(rtptimebase1);
            }
            if (rtptimereporter != null)
            {
                if (rtptimebase1.getMaster() == null)
                    rtptimebase1.setMaster(rtptimereporter);
                rtptimebase1.reporters.addElement(rtptimereporter);
            }
            rtptimebase2 = rtptimebase1;
        }
        return rtptimebase2;
    }

    public static RTPTimeBase getMapper(String s)
    {
        RTPTimeBase rtptimebase;
        synchronized (timeBases)
        {
            rtptimebase = find(null, s);
        }
        return rtptimebase;
    }

    public static RTPTimeBase getMapperUpdatable(String s)
    {
        RTPTimeBase rtptimebase2;
        synchronized (timeBases)
        {
            RTPTimeBase rtptimebase = find(null, s);
            if (rtptimebase.offsetUpdatable)
            {
                rtptimebase.offsetUpdatable = false;
                RTPTimeBase rtptimebase1 = rtptimebase;
                return rtptimebase1;
            }
            rtptimebase2 = null;
        }
        return rtptimebase2;
    }

    public static void remove(RTPTimeReporter rtptimereporter, String s)
    {
        synchronized (timeBases)
        {
            for (int i = 0; i < timeBases.size(); i++)
            {
                RTPTimeBase rtptimebase = (RTPTimeBase) timeBases.elementAt(i);
                // damencho safty check for null
                if (rtptimebase.cname == null || s == null
                        || !rtptimebase.cname.equals(s))
                    continue;
                rtptimebase.reporters.removeElement(rtptimereporter);
                if (rtptimebase.reporters.size() == 0)
                {
                    rtptimebase.master = null;
                    timeBases.removeElement(rtptimebase);
                } else
                {
                    synchronized (rtptimebase)
                    {
                        if (rtptimebase.master == rtptimereporter)
                            rtptimebase
                                    .setMaster((RTPTimeReporter) rtptimebase.reporters
                                            .elementAt(0));
                    }
                }
                break;
            }

        }
    }

    public static void returnMapperUpdatable(RTPTimeBase rtptimebase)
    {
        synchronized (timeBases)
        {
            rtptimebase.offsetUpdatable = true;
        }
    }

    String cname;

    RTPTimeReporter master;

    Vector reporters;

    long origin;

    long offset;

    boolean offsetUpdatable;

    RTPTimeBase(String s)
    {
        master = null;
        reporters = new Vector();
        origin = 0L;
        offset = 0L;
        offsetUpdatable = true;
        cname = s;
    }

    public synchronized RTPTimeReporter getMaster()
    {
        return master;
    }

    public synchronized long getNanoseconds()
    {
        return master == null ? 0L : master.getRTPTime();
    }

    public long getOffset()
    {
        return offset;
    }

    public long getOrigin()
    {
        return origin;
    }

    public Time getTime()
    {
        return new Time(getNanoseconds());
    }

    public synchronized void setMaster(RTPTimeReporter rtptimereporter)
    {
        master = rtptimereporter;
    }

    public synchronized void setOffset(long l)
    {
        offset = l;
    }

    public synchronized void setOrigin(long l)
    {
        origin = l;
    }

}
