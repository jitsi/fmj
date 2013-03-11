package net.sf.fmj.media.rtp;

import java.util.*;

import javax.media.rtp.*;
import javax.media.rtp.event.*;

import net.sf.fmj.media.rtp.util.*;

public class RTPEventHandler extends RTPMediaThread
{
    private RTPSessionMgr sm;
    private Vector<RTPEvent> eventQueue;
    private boolean killed;

    public RTPEventHandler(RTPSessionMgr sm)
    {
        super("RTPEventHandler");
        eventQueue = new Vector<RTPEvent>();
        killed = false;
        this.sm = sm;
        useControlPriority();
        setDaemon(true);
        start();
    }

    public synchronized void close()
    {
        killed = true;
        notifyAll();
    }

    protected void dispatchEvents()
    {
        RTPEvent evt;
        synchronized (this)
        {
            try
            {
                for (; eventQueue.size() == 0 && !killed; wait())
                    ;
            } catch (InterruptedException e)
            {
            }
            if (killed)
                return;
            evt = eventQueue.elementAt(0);
            eventQueue.removeElementAt(0);
        }
        processEvent(evt);
    }

    public synchronized void postEvent(RTPEvent evt)
    {
        eventQueue.addElement(evt);
        notifyAll();
    }

    protected void processEvent(RTPEvent evt)
    {
        if (evt instanceof SessionEvent)
        {
            for (int i = 0; i < sm.sessionlistener.size(); i++)
            {
                SessionListener sl = (SessionListener) sm.sessionlistener
                        .elementAt(i);
                if (sl != null)
                    sl.update((SessionEvent) evt);
            }

            return;
        }
        if (evt instanceof RemoteEvent)
        {
            for (int i = 0; i < sm.remotelistener.size(); i++)
            {
                RemoteListener sl = (RemoteListener) sm.remotelistener
                        .elementAt(i);
                if (sl != null)
                    sl.update((RemoteEvent) evt);
            }

            return;
        }
        if (evt instanceof ReceiveStreamEvent)
        {
            for (int i = 0; i < sm.streamlistener.size(); i++)
            {
                ReceiveStreamListener sl = (ReceiveStreamListener) sm.streamlistener
                        .elementAt(i);
                if (sl != null)
                    sl.update((ReceiveStreamEvent) evt);
            }

            return;
        }
        if (evt instanceof SendStreamEvent)
        {
            for (int i = 0; i < sm.sendstreamlistener.size(); i++)
            {
                SendStreamListener sl = (SendStreamListener) sm.sendstreamlistener
                        .elementAt(i);
                if (sl != null)
                    sl.update((SendStreamEvent) evt);
            }

        }
    }

    @Override
    public void run()
    {
        while (!killed)
            dispatchEvents();
    }
}
