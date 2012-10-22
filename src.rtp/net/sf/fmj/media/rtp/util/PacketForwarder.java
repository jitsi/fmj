package net.sf.fmj.media.rtp.util;

import java.io.*;

public class PacketForwarder implements Runnable
{
    PacketSource source;
    PacketConsumer consumer;
    RTPMediaThread thread;
    boolean closed;
    private boolean paused;
    public IOException exception;

    public PacketForwarder(PacketSource s, PacketConsumer c)
    {
        source = null;
        consumer = null;
        closed = false;
        exception = null;
        source = s;
        consumer = c;
        closed = false;
        exception = null;
    }

    private boolean checkForClose()
    {
        if (closed && thread != null)
        {
            if (source != null)
                source.closeSource();
            return true;
        } else
        {
            return false;
        }
    }

    public void close()
    {
        closed = true;
        if (consumer != null)
            consumer.closeConsumer();
    }

    public PacketConsumer getConsumer()
    {
        return consumer;
    }

    public String getId()
    {
        if (thread == null)
        {
            System.err.println("the packetforwarders thread is null");
            return null;
        } else
        {
            return thread.getName();
        }
    }

    public PacketSource getSource()
    {
        return source;
    }

    public void run()
    {
        if (closed || exception != null)
        {
            if (source != null)
                source.closeSource();
            return;
        }
        try
        {
            do
            {
                try
                {
                    Packet p = source.receiveFrom();
                    if (checkForClose())
                        return;
                    if (p != null)
                        consumer.sendTo(p);
                } catch (InterruptedIOException iioe)
                {
                }
            } while (!checkForClose());
        } catch (IOException ioe)
        {
            if (!checkForClose())
                exception = ioe;
        } finally
        {
            consumer.closeConsumer();
        }
    }

    public void setVideoPriority()
    {
        thread.useVideoNetworkPriority();
    }

    public void startPF()
    {
        startPF(null);
    }

    public void startPF(String threadname)
    {
        if (thread != null)
            throw new IllegalArgumentException("Called start more than once");
        if (threadname == null)
            threadname = "RTPMediaThread";
        {
            thread = new RTPMediaThread(this, threadname);
            thread.useNetworkPriority();
        }
        thread.setDaemon(true);
        thread.start();
    }
}
