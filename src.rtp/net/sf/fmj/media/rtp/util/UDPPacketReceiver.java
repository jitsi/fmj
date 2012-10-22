package net.sf.fmj.media.rtp.util;

import java.io.*;
import java.net.*;

public class UDPPacketReceiver implements PacketSource
{
    private DatagramSocket sock;
    private int maxsize;
    byte dataBuf[];

    public UDPPacketReceiver(DatagramSocket sock, int maxsize)
    {
        dataBuf = new byte[1];
        this.sock = sock;
        this.maxsize = maxsize;
        try
        {
            sock.setSoTimeout(5000);
        } catch (SocketException e)
        {
            System.out.println("could not set timeout on socket");
        }
    }

    public UDPPacketReceiver(int localPort, String localAddress,
            int remotePort, String remoteAddress, int maxsize,
            DatagramSocket localSocket) throws SocketException,
            UnknownHostException, IOException
    {
        dataBuf = new byte[1];
        InetAddress localInetAddr = InetAddress.getByName(localAddress);
        InetAddress remoteInetAddr = InetAddress.getByName(remoteAddress);
        if (remoteInetAddr.isMulticastAddress())
        {
            MulticastSocket sock = new MulticastSocket(remotePort);
            sock.joinGroup(remoteInetAddr);
            this.sock = sock;
            this.maxsize = maxsize;
        } else
        {
            if (localSocket != null)
                this.sock = localSocket;
            else
                this.sock = new DatagramSocket(localPort, localInetAddr);
            if (remoteAddress == null)
                ;
            this.maxsize = maxsize;
        }
        try
        {
            this.sock.setSoTimeout(5000);
        } catch (SocketException e)
        {
            System.out.println("could not set timeout on socket");
        }
    }

    public void closeSource()
    {
        if (sock != null)
        {
            sock.close();
            sock = null;
        }
    }

    public DatagramSocket getSocket()
    {
        return sock;
    }

    public Packet receiveFrom() throws IOException
    {
        DatagramPacket dp;
        int len;
        do
        {
            if (dataBuf.length < maxsize)
                dataBuf = new byte[maxsize];
            dp = new DatagramPacket(dataBuf, maxsize);
            sock.receive(dp);
            len = dp.getLength();
            if (len > maxsize >> 1)
                maxsize = len << 1;
        } while (len >= dp.getData().length);
        UDPPacket p = new UDPPacket();
        p.receiptTime = System.currentTimeMillis();
        p.data = dp.getData();
        p.offset = 0;
        p.length = len;
        p.datagrampacket = dp;
        p.localPort = sock.getLocalPort();
        p.remotePort = dp.getPort();
        p.remoteAddress = dp.getAddress();
        return p;
    }

    public String sourceString()
    {
        String s = "UDP Datagram Packet Receiver on port "
                + sock.getLocalPort() + "on local address "
                + sock.getLocalAddress();
        return s;
    }
}
