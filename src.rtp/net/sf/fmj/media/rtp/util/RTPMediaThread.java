package net.sf.fmj.media.rtp.util;

import net.sf.fmj.media.util.*;

public class RTPMediaThread extends MediaThread
{
    public RTPMediaThread()
    {
        this("RTP thread");
    }

    public RTPMediaThread(Runnable r)
    {
        this(r, "RTP thread");
    }

    public RTPMediaThread(Runnable r, String name)
    {
        super(r, name);
    }

    public RTPMediaThread(String name)
    {
        super(name);
    }
}
