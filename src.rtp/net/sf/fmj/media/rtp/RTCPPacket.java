package net.sf.fmj.media.rtp;

import java.io.*;

import net.sf.fmj.media.rtp.util.*;

public abstract class RTCPPacket extends Packet
{
    public Packet base;
    public int type;
    public static final int SR = 200;
    public static final int RR = 201;
    public static final int SDES = 202;
    public static final int BYE = 203;
    public static final int APP = 204;
    public static final int COMPOUND = -1;

    public RTCPPacket()
    {
    }

    public RTCPPacket(Packet p)
    {
        super(p);
        base = p;
    }

    public RTCPPacket(RTCPPacket parent)
    {
        super(parent);
        base = parent.base;
    }

    abstract void assemble(DataOutputStream dataoutputstream)
            throws IOException;

    public abstract int calcLength();
}
