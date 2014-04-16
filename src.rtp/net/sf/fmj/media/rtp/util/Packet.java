package net.sf.fmj.media.rtp.util;

import java.util.*;

/**
 *
 * @author Lyubomir Marinov
 */
public class Packet
{
    public byte data[];
    public int offset;
    public int length;
    public boolean received;
    public long receiptTime;

    /**
     * The bitmap/flag mask that specifies the set of boolean attributes enabled
     * for this <tt>RawPacket</tt>. The value is the logical sum of all of the
     * set flags. The possible flags are defined by the <tt>FLAG_XXX</tt>
     * constants of FMJ's {@link Buffer} class.
     */
    public int flags;

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

        flags = p.flags;
    }

    @Override
    public Packet clone()
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
