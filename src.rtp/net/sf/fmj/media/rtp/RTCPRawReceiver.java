package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.media.rtp.*;

import net.sf.fmj.media.rtp.util.*;

public class RTCPRawReceiver extends PacketFilter
{
    public DatagramSocket socket;
    private StreamSynch streamSynch;
    private OverallStats stats;

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
        RTCPPacket result;
        try
        {
            result = parse(p);
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

    public RTCPPacket parse(Packet packet) throws BadFormatException
    {
        RTCPCompoundPacket base = new RTCPCompoundPacket(packet);
        Vector<RTCPPacket> subpackets = new Vector<RTCPPacket>(2);
        DataInputStream in
            = new DataInputStream(
                    new ByteArrayInputStream(
                            base.data,
                            base.offset,
                            base.length));
        try
        {
            int length;
            for (int offset = 0; offset < base.length; offset += length)
            {
                int firstbyte = in.readUnsignedByte();
                if ((firstbyte & 0xc0) != 128)
                    throw new BadFormatException();
                int type = in.readUnsignedByte();
                length = in.readUnsignedShort();
                length = length + 1 << 2;
                int padlen = 0;
                if (offset + length > base.length)
                    throw new BadFormatException();
                if (offset + length == base.length)
                {
                    if ((firstbyte & 0x20) != 0)
                    {
                        padlen = base.data[base.offset + base.length - 1] & 0xff;
                        if (padlen == 0)
                            throw new BadFormatException();
                    }
                } else if ((firstbyte & 0x20) != 0)
                    throw new BadFormatException();
                int inlength = length - padlen;
                firstbyte &= 0x1f;
                RTCPPacket p;
                switch (type)
                {
                case RTCPPacket.SR:
                    stats.update(OverallStats.SRRECD, 1);
                    if (inlength != 28 + 24 * firstbyte)
                    {
                        stats.update(OverallStats.MALFORMEDSR, 1);
                        System.out.println("bad format.");
                        throw new BadFormatException();
                    }
                    RTCPSRPacket srp = new RTCPSRPacket(base);
                    p = srp;
                    srp.ssrc = in.readInt();
                    srp.ntptimestampmsw = in.readInt() & 0xffffffffL;
                    srp.ntptimestamplsw = in.readInt() & 0xffffffffL;
                    srp.rtptimestamp = in.readInt() & 0xffffffffL;
                    srp.packetcount = in.readInt() & 0xffffffffL;
                    srp.octetcount = in.readInt() & 0xffffffffL;
                    srp.reports = new RTCPReportBlock[firstbyte];
                    streamSynch.update(srp.ssrc, srp.rtptimestamp,
                            srp.ntptimestampmsw, srp.ntptimestamplsw);
                    for (int i = 0; i < srp.reports.length; i++)
                    {
                        RTCPReportBlock report = new RTCPReportBlock();
                        srp.reports[i] = report;
                        report.ssrc = in.readInt();
                        long val = in.readInt();
                        val &= 0xffffffffL;
                        report.fractionlost = (int) (val >> 24);
                        report.packetslost = (int) (val & 0xffffffL);
                        report.lastseq = in.readInt() & 0xffffffffL;
                        report.jitter = in.readInt();
                        report.lsr = in.readInt() & 0xffffffffL;
                        report.dlsr = in.readInt() & 0xffffffffL;
                    }

                    break;

                case RTCPPacket.RR:
                    if (inlength != 8 + 24 * firstbyte)
                    {
                        stats.update(OverallStats.MALFORMEDRR, 1);
                        throw new BadFormatException();
                    }
                    RTCPRRPacket rrp = new RTCPRRPacket(base);
                    p = rrp;
                    rrp.ssrc = in.readInt();
                    rrp.reports = new RTCPReportBlock[firstbyte];
                    for (int i = 0; i < rrp.reports.length; i++)
                    {
                        RTCPReportBlock report = new RTCPReportBlock();
                        rrp.reports[i] = report;
                        report.ssrc = in.readInt();
                        long val = in.readInt();
                        val &= 0xffffffffL;
                        report.fractionlost = (int) (val >> 24);
                        report.packetslost = (int) (val & 0xffffffL);
                        report.lastseq = in.readInt() & 0xffffffffL;
                        report.jitter = in.readInt();
                        report.lsr = in.readInt() & 0xffffffffL;
                        report.dlsr = in.readInt() & 0xffffffffL;
                    }

                    break;

                case RTCPPacket.SDES:
                    RTCPSDESPacket sdesp = new RTCPSDESPacket(base);
                    p = sdesp;
                    sdesp.sdes = new RTCPSDES[firstbyte];
                    int sdesoff = 4;
                    for (int i = 0; i < sdesp.sdes.length; i++)
                    {
                        RTCPSDES chunk = new RTCPSDES();
                        sdesp.sdes[i] = chunk;
                        chunk.ssrc = in.readInt();
                        sdesoff += 5;
                        Vector<RTCPSDESItem> items = new Vector<RTCPSDESItem>();
                        boolean gotcname = false;
                        int j;
                        while ((j = in.readUnsignedByte()) != 0)
                        {
                            if (j < 1 || j > 8)
                            {
                                stats.update(OverallStats.MALFORMEDSDES, 1);
                                throw new BadFormatException();
                            }
                            if (j == 1)
                                gotcname = true;
                            RTCPSDESItem item = new RTCPSDESItem();
                            items.addElement(item);
                            item.type = j;
                            int sdeslen = in.readUnsignedByte();
                            item.data = new byte[sdeslen];
                            in.readFully(item.data);
                            sdesoff += 2 + sdeslen;
                        }
                        if (!gotcname)
                        {
                            stats.update(OverallStats.MALFORMEDSDES, 1);
                            throw new BadFormatException();
                        }
                        chunk.items = new RTCPSDESItem[items.size()];
                        items.copyInto(chunk.items);
                        if ((sdesoff & 3) != 0)
                        {
                            in.skip(4 - (sdesoff & 3));
                            sdesoff = sdesoff + 3 & -4;
                        }
                    }

                    if (inlength != sdesoff)
                    {
                        stats.update(OverallStats.MALFORMEDSDES, 1);
                        throw new BadFormatException();
                    }
                    break;

                case RTCPPacket.BYE:
                    RTCPBYEPacket byep = new RTCPBYEPacket(base);
                    p = byep;
                    byep.ssrc = new int[firstbyte];
                    for (int i = 0; i < byep.ssrc.length; i++)
                        byep.ssrc[i] = in.readInt();

                    int reasonlen;
                    if (inlength > 4 + 4 * firstbyte)
                    {
                        reasonlen = in.readUnsignedByte();
                        byep.reason = new byte[reasonlen];
                        reasonlen++;
                    } else
                    {
                        reasonlen = 0;
                        byep.reason = new byte[0];
                    }
                    reasonlen = reasonlen + 3 & -4;
                    if (inlength != 4 + 4 * firstbyte + reasonlen)
                    {
                        stats.update(OverallStats.MALFORMEDBYE, 1);
                        throw new BadFormatException();
                    }
                    in.readFully(byep.reason);
                    in.skip(reasonlen - byep.reason.length);
                    break;

                case RTCPPacket.APP:
                    if (inlength < 12)
                        throw new BadFormatException();
                    RTCPAPPPacket appp = new RTCPAPPPacket(base);
                    p = appp;
                    appp.ssrc = in.readInt();
                    appp.name = in.readInt();
                    appp.subtype = firstbyte;
                    appp.data = new byte[inlength - 12];
                    in.readFully(appp.data);
                    in.skip(inlength - 12 - appp.data.length);
                    break;

                default:
                    stats.update(OverallStats.UNKNOWNTYPE, 1);
                    throw new BadFormatException();
                }
                p.offset = offset;
                p.length = length;
                subpackets.addElement(p);
                in.skipBytes(padlen);
            }

        } catch (EOFException e)
        {
            throw new BadFormatException("Unexpected end of RTCP packet");
        } catch (IOException e)
        {
            throw new IllegalArgumentException("Impossible Exception");
        }
        base.packets = new RTCPPacket[subpackets.size()];
        subpackets.copyInto(base.packets);
        return base;
    }
}
