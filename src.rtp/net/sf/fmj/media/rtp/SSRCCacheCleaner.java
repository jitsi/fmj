package net.sf.fmj.media.rtp;

import java.util.*;

import javax.media.rtp.*;
import javax.media.rtp.event.*;

import net.sf.fmj.media.rtp.util.*;

public class SSRCCacheCleaner
    implements Runnable
{
    /**
     * The maximum interval in milliseconds between consecutive invocations of
     * {@link #cleannow()}.
     */
    private static final long RUN_INTERVAL = 5000;

    private static final int TIMEOUT_MULTIPLIER = 5;

    private final SSRCCache cache;
    private boolean killed;
    private long lastCleaned;
    private final StreamSynch streamSynch;
    private final RTPMediaThread thread;

    public SSRCCacheCleaner(SSRCCache cache, StreamSynch streamSynch)
    {
        this.cache = cache;
        this.streamSynch = streamSynch;

        killed = false;
        lastCleaned = -1L;
        thread = new RTPMediaThread(this, "SSRC Cache Cleaner");
        thread.useControlPriority();
        thread.setDaemon(true);
        thread.start();
    }

    private void cleannow(long time)
    {
        if (cache.ourssrc == null)
            return;

        double reportInterval
            = cache.calcReportInterval(cache.ourssrc.sender, true);

        synchronized (cache.cache)
        {

        for (Enumeration<SSRCInfo> elements = cache.cache.elements();
                elements.hasMoreElements();)
        {
            SSRCInfo info = elements.nextElement();

            if (info.ours)
                continue;

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

                RTPSourceInfo sourceInfo = info.sourceInfo;
                ReceiveStream receiveStream;

                if (info instanceof RecvSSRCInfo)
                    receiveStream = (ReceiveStream) info;
                else if (info instanceof PassiveSSRCInfo)
                    receiveStream = null;
                else
                    continue;

                ByeEvent ev
                    = new ByeEvent(
                            cache.sm,
                            sourceInfo,
                            receiveStream,
                            info.byereason,
                            sourceInfo != null
                                && sourceInfo.getStreamCount() == 0);

                cache.eventhandler.postEvent(ev);
            }
            else if (info.lastHeardFrom + reportInterval <= time)
            {
                if (!info.inactivesent)
                {
                    InactiveReceiveStreamEvent ev = null;
                    RTPSourceInfo sourceInfo = info.sourceInfo;
                    boolean laststream
                        = (sourceInfo != null
                            && sourceInfo.getStreamCount() == 1);

                    if (info instanceof ReceiveStream)
                    {
                        ev
                            = new InactiveReceiveStreamEvent(
                                    cache.sm,
                                    sourceInfo,
                                    (ReceiveStream) info,
                                    laststream);
                    }
                    else if (info.lastHeardFrom
                                + reportInterval * TIMEOUT_MULTIPLIER
                            <= time)
                    {
                        ev
                            = new InactiveReceiveStreamEvent(
                                    cache.sm,
                                    sourceInfo,
                                    null,
                                    laststream);
                    }
                    if (ev != null)
                    {
                        cache.eventhandler.postEvent(ev);
                        info.quiet = true;
                        info.inactivesent = true;
                        info.setAlive(false);
                    }
                }
                // 30 minutes without hearing from an SSRC sounded like an awful
                // lot so it was reduced to what was considered a more
                // reasonable value in practical situations.
                else if (info.lastHeardFrom + (5 * 1000) <= time)
                {
                    cache.remove(info.ssrc);

                    RTPSourceInfo sourceInfo = info.sourceInfo;
                    TimeoutEvent ev
                        = new TimeoutEvent(
                                cache.sm,
                                sourceInfo,
                                (info instanceof ReceiveStream)
                                    ? (ReceiveStream) info
                                    : null,
                                (sourceInfo != null)
                                    && (sourceInfo.getStreamCount() == 0));

                    cache.eventhandler.postEvent(ev);
                }
            }
        }

        } // synchronized (cache.cache)
    }

    @Override
    public void run()
    {
        do
        {
            long now;
            long timeout;

            synchronized (this)
            {
                if (killed)
                {
                    break;
                }
                else
                {
                    now = System.currentTimeMillis();
                    timeout
                        =  (lastCleaned == -1L)
                            ? 0L
                            : (lastCleaned + RUN_INTERVAL - now);
                    if (timeout <= 0)
                    {
                        // We are going to invoke cleannow(long) immediately, we
                        // merely want to leave the synchronized block.
                        lastCleaned = now;
                    }
                    else
                    {
                        try
                        {
                            wait(timeout);
                        }
                        catch (InterruptedException iex)
                        {
                        }
                        continue;
                    }
                }
            }

            try
            {
                cleannow(now);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        while (true);
    }

    public synchronized void setClean()
    {
        lastCleaned = -1L;
        notifyAll();
    }

    public synchronized void stop()
    {
        killed = true;
        notifyAll();
    }
}
