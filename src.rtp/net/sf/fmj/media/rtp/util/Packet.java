package net.sf.fmj.media.rtp.util;

import java.util.*;

public class Packet
{
    public byte data[];
    public int offset;
    public int length;
    public boolean received;
    public long receiptTime;

    public Packet()
    {
        received = true;
    }

    public Packet(Packet p)
    {
        received = true;
        data = p.data;
        offset = p.offset;
        length = p.length;
        received = p.received;
        receiptTime = p.receiptTime;
    }

    @Override
    public Object clone()
    {
        Packet p = new Packet(this);
        p.data = data.clone();
        return p;
    }

    @Override
    public String toString()
    {
        String s = "Packet of size " + length;
        if (received)
            s = s + " received at " + new Date(receiptTime);
        return s;
    }
}
