package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.media.rtp.*;

import net.sf.fmj.media.rtp.util.*;

public class RTCPRawSender extends PacketFilter
{
    private InetAddress destaddr;
    private int destport;

    public RTCPRawSender(int port, String address) throws UnknownHostException,
            IOException
    {
        destaddr = InetAddress.getByName(address);
        destport = port | 1;
        super.destAddressList = null;
    }

    public RTCPRawSender(int port, String address, UDPPacketSender sender)
            throws UnknownHostException, IOException
    {
        this(port, address);
        setConsumer(sender);
        super.destAddressList = null;
    }

    public RTCPRawSender(RTPPacketSender sender)
    {
        setConsumer(sender);
    }

    public void addDestAddr(InetAddress newaddr)
    {
        int i = 0;
        if (super.destAddressList == null)
        {
            super.destAddressList = new Vector();
            super.destAddressList.addElement(destaddr);
        }
        for (i = 0; i < super.destAddressList.size(); i++)
        {
            InetAddress curraddr = (InetAddress) super.destAddressList
                    .elementAt(i);
            if (curraddr.equals(newaddr))
                break;
        }

        if (i == super.destAddressList.size())
            super.destAddressList.addElement(newaddr);
    }

    public void assemble(RTCPCompoundPacket p)
    {
        int len = p.calcLength();
        p.assemble(len, false);
    }

    @Override
    public String filtername()
    {
        return "RTCP Raw Packet Sender";
    }

    public InetAddress getRemoteAddr()
    {
        return destaddr;
    }

    @Override
    public Packet handlePacket(Packet p)
    {
        assemble((RTCPCompoundPacket) p);
        PacketConsumer consumer = getConsumer();
        if (consumer instanceof RTPPacketSender)
        {
            return p;
        } else
        {
            UDPPacket udpp = new UDPPacket();
            udpp.received = false;
            udpp.data = p.data;
            udpp.offset = p.offset;
            udpp.length = p.length;
            udpp.remoteAddress = destaddr;
            udpp.remotePort = destport;
            return udpp;
        }
    }

    @Override
    public Packet handlePacket(Packet p, int index)
    {
        assemble((RTCPCompoundPacket) p);
        UDPPacket udpp = new UDPPacket();
        udpp.received = false;
        udpp.data = p.data;
        udpp.offset = p.offset;
        udpp.length = p.length;
        udpp.remoteAddress = (InetAddress) super.destAddressList
                .elementAt(index);
        udpp.remotePort = destport;
        return udpp;
    }

    @Override
    public Packet handlePacket(Packet p, SessionAddress sessionAddress)
    {
        assemble((RTCPCompoundPacket) p);
        PacketConsumer consumer = getConsumer();
        if (consumer instanceof RTPPacketSender)
        {
            return p;
        } else
        {
            UDPPacket udpp = new UDPPacket();
            udpp.received = false;
            udpp.data = p.data;
            udpp.offset = p.offset;
            udpp.length = p.length;
            udpp.remoteAddress = sessionAddress.getControlAddress();
            udpp.remotePort = sessionAddress.getControlPort();
            return udpp;
        }
    }

    public void setDestAddresses(Vector destAddresses)
    {
        super.destAddressList = destAddresses;
    }
}
