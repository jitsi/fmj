package net.sf.fmj.media.rtp;

import java.util.*;

import javax.media.rtp.*;
import javax.media.rtp.event.*;

import net.sf.fmj.media.rtp.util.*;

public class SSRCCacheCleaner implements Runnable
{
    private SSRCCache cache;
    private RTPMediaThread thread;
    private static final int DEATHTIME = 0x1b7740;
    private static final int TIMEOUT_MULTIPLIER = 5;
    boolean timeToClean;
    private boolean killed;
    private StreamSynch streamSynch;

    public SSRCCacheCleaner(SSRCCache cache, StreamSynch streamSynch)
    {
        timeToClean = false;
        killed = false;
        this.cache = cache;
        this.streamSynch = streamSynch;
        thread = new RTPMediaThread(this, "SSRC Cache Cleaner");
        thread.useControlPriority();
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void cleannow()
    {
        long time = System.currentTimeMillis();
        if (cache.ourssrc == null)
            return;
        double reportInterval
            = cache.calcReportInterval(cache.ourssrc.sender, true);
        for (Enumeration<SSRCInfo> elements = cache.cache.elements();
                elements.hasMoreElements();)
        {
            SSRCInfo info = elements.nextElement();
            if (!info.ours)
                if (info.byeReceived)
                {
                    if (time - info.byeTime < 1000L)
                    {
                        try
                        {
                            Thread.sleep((1000L - time) + info.byeTime);
                        }
                        catch (InterruptedException e)
                        {
                        }
                        time = System.currentTimeMillis();
                    }
                    info.byeTime = 0L;
                    info.byeReceived = false;
                    cache.remove(info.ssrc);
                    streamSynch.remove(info.ssrc);
                    boolean byepart = false;
                    RTPSourceInfo sourceInfo = info.sourceInfo;
                    if (sourceInfo != null && sourceInfo.getStreamCount() == 0)
                        byepart = true;
                    ByeEvent evtbye = null;
                    if (info instanceof RecvSSRCInfo)
                        evtbye = new ByeEvent(cache.sm, info.sourceInfo,
                                (ReceiveStream) info, info.byereason, byepart);
                    if (info instanceof PassiveSSRCInfo)
                        evtbye = new ByeEvent(cache.sm, info.sourceInfo, null,
                                info.byereason, byepart);
                    cache.eventhandler.postEvent(evtbye);
                }
                else if (info.lastHeardFrom + reportInterval <= time)
                {
                    InactiveReceiveStreamEvent event = null;
                    if (!info.inactivesent)
                    {
                        boolean laststream = false;
                        RTPSourceInfo si = info.sourceInfo;
                        if (si != null && si.getStreamCount() == 1)
                            laststream = true;
                        if (info instanceof ReceiveStream)
                        {
                            event = new InactiveReceiveStreamEvent(cache.sm,
                                    info.sourceInfo, (ReceiveStream) info,
                                    laststream);
                        } else
                        {
                            if (info.lastHeardFrom + reportInterval * 5 <= time)
                                event = new InactiveReceiveStreamEvent(
                                        cache.sm, info.sourceInfo, null,
                                        laststream);
                        }
                        if (event != null)
                        {
                            cache.eventhandler.postEvent(event);
                            info.quiet = true;
                            info.inactivesent = true;
                            info.setAlive(false);
                        }
                    }
                    /*
                     * 30 minutes without hearing from an SSRC sounded like an
                     * awful lot so it was reduced to what was considered a more
                     * reasonable value in practical situations.
                     */
                    else if (info.lastHeardFrom + (5 * 1000) <= time)
                    {
                        TimeoutEvent evt = null;
                        cache.remove(info.ssrc);
                        boolean byepart = false;
                        RTPSourceInfo sourceInfo = info.sourceInfo;
                        if (sourceInfo != null
                                && sourceInfo.getStreamCount() == 0)
                            byepart = true;
                        if (info instanceof ReceiveStream)
                            evt = new TimeoutEvent(cache.sm, info.sourceInfo,
                                    (ReceiveStream) info, byepart);
                        else
                            evt = new TimeoutEvent(cache.sm, info.sourceInfo,
                                    null, byepart);
                        cache.eventhandler.postEvent(evt);
                    }
                }
        }

    }

    public synchronized void run()
    {
        try
        {
            do
            {
                while (!timeToClean && !killed)
                    wait();
                if (killed)
                    return;
                cleannow();
                timeToClean = false;
            } while (true);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void setClean()
    {
        timeToClean = true;
        notifyAll();
    }

    public synchronized void stop()
    {
        killed = true;
        notifyAll();
    }
}
