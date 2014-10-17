package net.sf.fmj.media.rtp;

import javax.media.rtp.*;
import javax.media.rtp.event.*;

import net.sf.fmj.media.rtp.util.*;

/**
 *
 * @author Lyubomir Marinov
 */
public class SSRCCacheCleaner
    implements Runnable
{
    /**
     * The maximum interval in milliseconds between consecutive invocations of
     * {@link #cleannow()}.
     */
    private static final long RUN_INTERVAL = 5000L;

    private static final int TIMEOUT_MULTIPLIER = 5;

    private final SSRCCache cache;
    private boolean killed;
    private long lastCleaned;

    /**
     * The synchronization source identifiers (SSRCs) of {@link #cache}
     * retrieved during the last execution of {@link #cleannow(long)}. Cached
     * for the purposes of reducing the number of allocations in particular and
     * the effects of the garbage collector in general. 
     */
    private int[] ssrcs;

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

    private long cleannow(long time)
    {
        SSRCInfo ourssrc = cache.ourssrc;
        long timeUntilNextProcess = Long.MAX_VALUE;

        if (ourssrc == null)
            return timeUntilNextProcess;

        double reportInterval = cache.calcReportInterval(ourssrc.sender, true);

        // Synchronizing on cache.cache appears to be too much (of a good thing)
        // because it causes (a multitude of) deadlocks. Besides, the
        // synchronization on cache.cache is inconsistent throughout the project
        // and, consequently, is expendable here.
        SSRCTable<SSRCInfo> infos = cache.cache;

        for (int ssrc : (ssrcs = infos.keysToArray(ssrcs)))
        {
            if (ssrc == 0)
                continue;

            SSRCInfo info = infos.get(ssrc);

            if (info == null || info.ours)
                continue;

            if (info.byeReceived)
            {
                long byeTimeout = 1000L - time + info.byeTime;

                if (byeTimeout > 0)
                {
                    if (byeTimeout < timeUntilNextProcess)
                        timeUntilNextProcess = byeTimeout;
                    continue;
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
                    RTPSourceInfo sourceInfo = info.sourceInfo;
                    ReceiveStream receiveStream;

                    if (info instanceof ReceiveStream)
                    {
                        receiveStream = (ReceiveStream) info;
                    }
                    else if (info.lastHeardFrom
                                + reportInterval * TIMEOUT_MULTIPLIER
                            <= time)
                    {
                        receiveStream = null;
                    }
                    else
                    {
                        continue;
                    }

                    InactiveReceiveStreamEvent ev
                        = new InactiveReceiveStreamEvent(
                                cache.sm,
                                sourceInfo,
                                receiveStream,
                                sourceInfo != null
                                    && sourceInfo.getStreamCount() == 1);

                    cache.eventhandler.postEvent(ev);
                    info.quiet = true;
                    info.inactivesent = true;
                    info.setAlive(false);
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

        return timeUntilNextProcess;
    }

    @Override
    public void run()
    {
        long timeout = Long.MAX_VALUE;

        do
        {
            long now;

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
                            : Math.min(
                                    lastCleaned + RUN_INTERVAL - now,
                                    timeout);
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
                        timeout = Long.MAX_VALUE;
                        continue;
                    }
                }
            }

            try
            {
                timeout = cleannow(now);
                if (timeout <= 0)
                    timeout = Long.MAX_VALUE;
            }
            catch (Exception ex)
            {
                timeout = Long.MAX_VALUE;
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
