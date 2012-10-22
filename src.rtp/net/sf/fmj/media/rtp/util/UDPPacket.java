package net.sf.fmj.media.rtp.util;

import java.net.*;
import java.util.*;

public class UDPPacket extends Packet
{
    public DatagramPacket datagrampacket;
    public int localPort;
    public int remotePort;
    public InetAddress remoteAddress;

    @Override
    public String toString()
    {
        String s = "UDP Packet of size " + super.length;
        if (super.received)
            s = s + " received at " + new Date(super.receiptTime) + " on port "
                    + localPort + " from " + remoteAddress + " port "
                    + remotePort;
        return s;
    }
}
