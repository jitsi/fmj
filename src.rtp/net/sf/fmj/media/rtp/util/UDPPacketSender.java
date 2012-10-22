package net.sf.fmj.media.rtp.util;

import java.io.*;
import java.net.*;

public class UDPPacketSender implements PacketConsumer
{
    private DatagramSocket sock;
    private InetAddress address;
    private int port;
    private int ttl;

    public UDPPacketSender() throws IOException
    {
        this(new DatagramSocket());
    }

    public UDPPacketSender(DatagramSocket sock)
    {
        this.sock = sock;
    }

    public UDPPacketSender(InetAddress remoteAddress, int remotePort)
            throws IOException
    {
        if (remoteAddress.isMulticastAddress())
        {
            MulticastSocket sock = new MulticastSocket();
            this.sock = sock;
        } else
        {
            this.sock = new DatagramSocket();
        }
        setRemoteAddress(remoteAddress, remotePort);
    }

    public UDPPacketSender(int localPort) throws IOException
    {
        this(new DatagramSocket(localPort));
    }

    public UDPPacketSender(int localPort, InetAddress localAddress,
            InetAddress remoteAddress, int remotePort) throws IOException
    {
        if (remoteAddress.isMulticastAddress())
        {
            MulticastSocket sock = new MulticastSocket(localPort);
            if (localAddress != null)
                sock.setInterface(localAddress);
            this.sock = sock;
        } else if (localAddress != null)
            try
            {
                this.sock = new DatagramSocket(localPort, localAddress);
            } catch (SocketException e)
            {
                System.out.println(e);
                System.out.println("localPort: " + localPort);
                System.out.println("localAddress: " + localAddress);
                throw e;
            }
        else
            this.sock = new DatagramSocket(localPort);
        setRemoteAddress(remoteAddress, remotePort);
    }

    public void closeConsumer()
    {
        if (sock != null)
        {
            sock.close();
            sock = null;
        }
    }

    public String consumerString()
    {
        String s = "UDP Datagram Packet Sender on port " + sock.getLocalPort();
        if (address != null)
            s = s + " sending to address " + address + ", port " + port
                    + ", ttl" + ttl;
        return s;
    }

    public InetAddress getLocalAddress()
    {
        return sock.getLocalAddress();
    }

    public int getLocalPort()
    {
        return sock.getLocalPort();
    }

    public DatagramSocket getSocket()
    {
        return sock;
    }

    public void send(Packet p, InetAddress addr, int port) throws IOException
    {
        byte data[] = p.data;
        if (p.offset > 0)
            System.arraycopy(data, p.offset, data = new byte[p.length], 0,
                    p.length);
        DatagramPacket dp = new DatagramPacket(data, p.length, addr, port);
        sock.send(dp);
    }

    public void sendTo(Packet p) throws IOException
    {
        InetAddress addr = null;
        int port = 0;
        if (p instanceof UDPPacket)
        {
            UDPPacket udpp = (UDPPacket) p;
            addr = udpp.remoteAddress;
            port = udpp.remotePort;
        }
        if (addr == null)
        {
            throw new IllegalArgumentException("No address set");
        } else
        {
            send(p, addr, port);
            return;
        }
    }

    public void setRemoteAddress(InetAddress remoteAddress, int remotePort)
    {
        address = remoteAddress;
        port = remotePort;
    }

    public void setttl(int ttl) throws IOException
    {
        this.ttl = ttl;
        if (sock instanceof MulticastSocket)
            ((MulticastSocket) sock).setTTL((byte) this.ttl);
    }
}
