package net.sf.fmj.media.rtp;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

import javax.media.rtp.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.rtp.util.*;

public class RTPRawReceiver extends PacketFilter
{
    private OverallStats stats;

    private boolean recvBufSizeSet;

    public DatagramSocket socket;

    private RTPConnector rtpConnector;

    public RTPRawReceiver()
    {
        stats = null;
        recvBufSizeSet = false;
        rtpConnector = null;
    }

    public RTPRawReceiver(DatagramSocket datagramsocket,
            OverallStats overallstats)
    {
        stats = null;
        recvBufSizeSet = false;
        rtpConnector = null;
        setSource(new UDPPacketReceiver(datagramsocket, 2000));
        stats = overallstats;
    }

    public RTPRawReceiver(int i, String s, OverallStats overallstats)
            throws UnknownHostException, IOException, SocketException
    {
        stats = null;
        recvBufSizeSet = false;
        rtpConnector = null;
        UDPPacketReceiver udppacketreceiver;
        setSource(udppacketreceiver = new UDPPacketReceiver(i & -2, s, -1,
                null, 2000, null));
        socket = udppacketreceiver.getSocket();
        stats = overallstats;
    }

    public RTPRawReceiver(RTPConnector rtpconnector, OverallStats overallstats)
    {
        stats = null;
        recvBufSizeSet = false;
        rtpConnector = null;
        try
        {
            setSource(new RTPPacketReceiver(rtpconnector.getDataInputStream()));
        } catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        rtpConnector = rtpconnector;
        stats = overallstats;
    }

    public RTPRawReceiver(RTPPushDataSource rtppushdatasource,
            OverallStats overallstats)
    {
        stats = null;
        recvBufSizeSet = false;
        rtpConnector = null;
        setSource(new RTPPacketReceiver(rtppushdatasource));
        stats = overallstats;
    }

    public RTPRawReceiver(SessionAddress sessionaddress,
            SessionAddress sessionaddress1, OverallStats overallstats,
            DatagramSocket datagramsocket) throws UnknownHostException,
            IOException, SocketException
    {
        stats = null;
        recvBufSizeSet = false;
        rtpConnector = null;
        stats = overallstats;
        UDPPacketReceiver udppacketreceiver = new UDPPacketReceiver(
                sessionaddress.getDataPort(),
                sessionaddress.getDataHostAddress(),
                sessionaddress1.getDataPort(),
                sessionaddress1.getDataHostAddress(), 2000, datagramsocket);
        setSource(udppacketreceiver);
        socket = udppacketreceiver.getSocket();
    }

    @Override
    public void close()
    {
        if (socket != null)
            socket.close();
        if (getSource() instanceof RTPPacketReceiver)
            getSource().closeSource();
    }

    @Override
    public String filtername()
    {
        return "RTP Raw Packet Receiver";
    }

    public int getRecvBufSize()
    {
        Integer integer;

        try
        {
            Class class1 = socket.getClass();
            Method method = class1.getMethod("getReceiveBufferSize", null);
            integer = (Integer) method.invoke(socket, null);
            return integer.intValue();
        } catch (Exception e)
        {
            if (rtpConnector != null)
                return rtpConnector.getReceiveBufferSize();
        }

        return -1;
    }

    @Override
    public Packet handlePacket(Packet packet)
    {
        stats.update(0, 1);
        stats.update(1, packet.length);
        RTPPacket rtppacket;
        try
        {
            rtppacket = parse(packet);
        } catch (BadFormatException badformatexception)
        {
            stats.update(2, 1);
            return null;
        }
        if (!recvBufSizeSet)
        {
            recvBufSizeSet = true;
            switch (rtppacket.payloadType)
            {
            case 14: // '\016'
            case 26: // '\032'
            case 34: // '"'
            case 42: // '*'
                setRecvBufSize(64000);
                break;

            case 31: // '\037'
                setRecvBufSize(0x1f400);
                break;

            case 32: // ' '
                setRecvBufSize(0x1f400);
                break;

            default:
                if (rtppacket.payloadType >= 96 && rtppacket.payloadType <= 127)
                    setRecvBufSize(64000);
                break;
            }
        }
        return rtppacket;
    }

    @Override
    public Packet handlePacket(Packet packet, int i)
    {
        return null;
    }

    @Override
    public Packet handlePacket(Packet packet, SessionAddress sessionaddress)
    {
        return null;
    }

    public Packet handlePacket(Packet packet, SessionAddress sessionaddress,
            boolean flag)
    {
        return null;
    }

    public RTPPacket parse(Packet packet) throws BadFormatException
    {
        RTPPacket rtppacket = new RTPPacket(packet);
        DataInputStream datainputstream = new DataInputStream(
                new ByteArrayInputStream(((Packet) (rtppacket)).data,
                        ((Packet) (rtppacket)).offset,
                        ((Packet) (rtppacket)).length));
        try
        {
            int i = datainputstream.readUnsignedByte();
            if ((i & 0xc0) != 128)
                throw new BadFormatException();
            if ((i & 0x10) != 0)
                rtppacket.extensionPresent = true;
            int j = 0;
            if ((i & 0x20) != 0)
                j = ((Packet) (rtppacket)).data[(((Packet) (rtppacket)).offset + ((Packet) (rtppacket)).length) - 1] & 0xff;
            i &= 0xf;
            rtppacket.payloadType = datainputstream.readUnsignedByte();
            rtppacket.marker = rtppacket.payloadType >> 7;
            rtppacket.payloadType &= 0x7f;
            rtppacket.seqnum = datainputstream.readUnsignedShort();
            rtppacket.timestamp = datainputstream.readInt() & 0xffffffffL;
            rtppacket.ssrc = datainputstream.readInt();
            int k = 0;
            rtppacket.csrc = new int[i];
            for (int i1 = 0; i1 < rtppacket.csrc.length; i1++)
                rtppacket.csrc[i1] = datainputstream.readInt();

            k += 12 + (rtppacket.csrc.length << 2);
            if (rtppacket.extensionPresent)
            {
                rtppacket.extensionType = datainputstream.readUnsignedShort();
                int l = datainputstream.readUnsignedShort();
                l <<= 2;
                rtppacket.extension = new byte[l];
                datainputstream.readFully(rtppacket.extension);
                k += l + 4;
            }
            rtppacket.payloadlength = ((Packet) (rtppacket)).length - (k + j);
            if (rtppacket.payloadlength < 1)
                throw new BadFormatException();
            rtppacket.payloadoffset = k + ((Packet) (rtppacket)).offset;
        } catch (EOFException eofexception)
        {
            throw new BadFormatException("Unexpected end of RTP packet");
        } catch (IOException ioexception)
        {
            throw new IllegalArgumentException("Impossible Exception");
        }
        return rtppacket;
    }

    public void setRecvBufSize(int i)
    {
        try
        {
            if (socket == null && rtpConnector != null)
                rtpConnector.setReceiveBufferSize(i);
        } catch (Exception exception)
        {
            Log.comment("Cannot set receive buffer size: " + exception);
        }
    }
}
