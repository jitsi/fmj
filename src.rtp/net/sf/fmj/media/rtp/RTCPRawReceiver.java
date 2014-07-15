package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;

import javax.media.rtp.*;

import net.sf.fmj.media.rtp.util.*;

public class RTCPRawReceiver extends PacketFilter
        implements RTCPPacketParserListener
{
    public DatagramSocket socket;
    private StreamSynch streamSynch;
    private OverallStats stats;
    private RTCPPacketParser parser;

    public RTCPRawReceiver()
    {
        stats = null;
    }

    public RTCPRawReceiver(DatagramSocket sock, OverallStats stats,
            StreamSynch streamSynch)
    {
        this.stats = null;
        setSource(new UDPPacketReceiver(sock, 1000));
        this.stats = stats;
        this.streamSynch = streamSynch;
    }

    public RTCPRawReceiver(int localPort, String localAddress,
            OverallStats stats, StreamSynch streamSynch)
            throws UnknownHostException, IOException, SocketException
    {
        this.stats = null;
        this.streamSynch = streamSynch;
        this.stats = stats;
        UDPPacketReceiver recv = new UDPPacketReceiver(localPort, localAddress,
                -1, null, 1000, null);
        setSource(recv);
        socket = recv.getSocket();
    }

    public RTCPRawReceiver(RTPConnector rtpConnector, OverallStats stats,
            StreamSynch streamSynch)
    {
        this.stats = null;
        this.streamSynch = streamSynch;
        try
        {
            setSource(new RTPPacketReceiver(
                    rtpConnector.getControlInputStream()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        this.stats = stats;
    }

    public RTCPRawReceiver(RTPPushDataSource networkdatasource,
            OverallStats stats, StreamSynch streamSynch)
    {
        this.stats = null;
        this.streamSynch = streamSynch;
        setSource(new RTPPacketReceiver(networkdatasource));
        this.stats = stats;
    }

    public RTCPRawReceiver(SessionAddress localAddress,
            SessionAddress remoteAddress, OverallStats stats,
            StreamSynch streamSynch, DatagramSocket controlSocket)
            throws UnknownHostException, IOException, SocketException
    {
        this.stats = null;
        this.streamSynch = streamSynch;
        this.stats = stats;
        UDPPacketReceiver recv = new UDPPacketReceiver(
                localAddress.getControlPort(),
                localAddress.getControlHostAddress(),
                remoteAddress.getControlPort(),
                remoteAddress.getControlHostAddress(), 1000, controlSocket);
        setSource(recv);
        socket = recv.getSocket();
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
        return "RTCP Raw Receiver";
    }

    @Override
    public Packet handlePacket(Packet p)
    {
        stats.update(OverallStats.PACKETRECD, 1);
        stats.update(OverallStats.RTCPRECD, 1);
        stats.update(OverallStats.BYTESRECD, p.length);

        if (parser == null)
        {
            parser = new RTCPPacketParser();
            parser.addRTCPPacketParserListener(this);
        }

        RTCPPacket result;
        try
        {
            result = parser.parse(p);
        } catch (BadFormatException e)
        {
            stats.update(OverallStats.BADRTCPPACKET, 1);
            return null;
        }
        return result;
    }

    @Override
    public Packet handlePacket(Packet p, int i)
    {
        return null;
    }

    @Override
    public Packet handlePacket(Packet p, SessionAddress a)
    {
        return null;
    }

    public Packet handlePacket(Packet p, SessionAddress a, boolean control)
    {
        return null;
    }

    @Override
    public void enterSenderReport()
    {
        stats.update(OverallStats.SRRECD, 1);
    }

    @Override
    public void malformedSenderReport()
    {
        stats.update(OverallStats.MALFORMEDSR, 1);
    }

    @Override
    public void malformedReceiverReport()
    {
        stats.update(OverallStats.MALFORMEDRR, 1);
    }

    @Override
    public void malformedSourceDescription()
    {
        stats.update(OverallStats.MALFORMEDSDES, 1);
    }

    @Override
    public void malformedEndOfParticipation()
    {
        stats.update(OverallStats.MALFORMEDBYE, 1);
    }

    @Override
    public void uknownPayloadType()
    {
        stats.update(OverallStats.UNKNOWNTYPE, 1);
    }

    @Override
    public void visitSendeReport(RTCPSRPacket rtcpSRPacket)
    {
        streamSynch.update(rtcpSRPacket.ssrc, rtcpSRPacket.rtptimestamp,
                rtcpSRPacket.ntptimestampmsw, rtcpSRPacket.ntptimestamplsw);
    }
}
