package net.sf.fmj.media.rtp;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

import javax.media.rtp.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.rtp.util.*;

public class RTPRawReceiver extends PacketFilter
{
    private OverallStats stats = null;

    private boolean recvBufSizeSet = false;

    public DatagramSocket socket;

    private RTPConnector rtpConnector = null;

    private final RTPPacketParser parser = new RTPPacketParser();

    public RTPRawReceiver()
    {
    }

    public RTPRawReceiver(DatagramSocket datagramsocket,
            OverallStats overallstats)
    {
        setSource(new UDPPacketReceiver(datagramsocket, 2000));
        stats = overallstats;
    }

    public RTPRawReceiver(int i, String s, OverallStats overallstats)
            throws UnknownHostException, IOException, SocketException
    {
        UDPPacketReceiver udppacketreceiver;
        setSource(udppacketreceiver = new UDPPacketReceiver(i & -2, s, -1,
                null, 2000, null));
        socket = udppacketreceiver.getSocket();
        stats = overallstats;
    }

    public RTPRawReceiver(RTPConnector rtpconnector, OverallStats overallstats)
    {
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
        setSource(new RTPPacketReceiver(rtppushdatasource));
        stats = overallstats;
    }

    public RTPRawReceiver(SessionAddress sessionaddress,
            SessionAddress sessionaddress1, OverallStats overallstats,
            DatagramSocket datagramsocket) throws UnknownHostException,
            IOException, SocketException
    {
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
            Class<?> class1 = socket.getClass();
            Method method = class1.getMethod("getReceiveBufferSize");
            integer = (Integer) method.invoke(socket);
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
        stats.update(OverallStats.PACKETRECD, 1);
        stats.update(OverallStats.BYTESRECD, packet.length);
        RTPPacket rtppacket;
        try
        {
            rtppacket = parser.parse(packet);
        } catch (BadFormatException badformatexception)
        {
            stats.update(OverallStats.BADRTPPACKET, 1);
            return null;
        }
        if (!recvBufSizeSet)
        {
            recvBufSizeSet = true;
            switch (rtppacket.payloadType)
            {
            case 14: // '\016'  //MPA
            case 26: // '\032'  //JPEG
            case 34: // '"'     //H263
            case 42: // '*'     //Unassigned?
                setRecvBufSize(64000);
                break;

            case 31: // '\037'  //H261
                setRecvBufSize(0x1f400);
                break;

            case 32: // ' '     //MPV
                setRecvBufSize(0x1f400);
                break;

            default:
                //all dynamic
                if (rtppacket.payloadType >= 96 && rtppacket.payloadType <= 127)
                    setRecvBufSize(64000);
                break;
            }
            //Note: for assigned payload types not explicitly specified above
            //setRecvBufSize is not called (PCMA, PCMU, G72{2,9}, GSM)
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
