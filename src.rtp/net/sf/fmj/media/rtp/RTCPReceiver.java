package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;

import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.media.rtp.rtcp.*;

import net.sf.fmj.media.rtp.util.*;

public class RTCPReceiver implements PacketConsumer
{
    private boolean rtcpstarted;

    SSRCCache cache;

    private int type;

    public RTCPReceiver(SSRCCache ssrccache)
    {
        rtcpstarted = false;
        type = 0;
        cache = ssrccache;

        ssrccache.lookup(ssrccache.ourssrc.ssrc);
    }

    public RTCPReceiver(SSRCCache ssrccache, DatagramSocket datagramsocket,
            StreamSynch streamsynch)
    {
        this(ssrccache, (new RTCPRawReceiver(datagramsocket,
                ssrccache.sm.defaultstats, streamsynch)));
    }

    public RTCPReceiver(SSRCCache ssrccache, int i, String s,
            StreamSynch streamsynch) throws UnknownHostException, IOException
    {
        this(ssrccache, (new RTCPRawReceiver(i | 1, s,
                ssrccache.sm.defaultstats, streamsynch)));
    }

    public RTCPReceiver(SSRCCache ssrccache, PacketSource packetsource)
    {
        this(ssrccache);
        PacketForwarder packetforwarder = new PacketForwarder(packetsource,
                this);
        packetforwarder.startPF();
    }

    public void closeConsumer()
    {
    }

    public String consumerString()
    {
        return "RTCP Packet Receiver/Collector";
    }

    public void sendTo(Packet packet)
    {
        sendTo((RTCPPacket) packet);
    }

    public void sendTo(RTCPPacket rtcppacket)
    {
        SSRCInfo ssrcinfo = null;
        boolean flag = cache.sm.isUnicast();
        if (flag)
        {
            InetAddress remoteAddress
                = ((UDPPacket) rtcppacket.base).remoteAddress;
            if (!rtcpstarted)
            {
                cache.sm.startRTCPReports(remoteAddress);
                rtcpstarted = true;
                byte abyte0[] = cache.sm.controladdress.getAddress();
                int i = abyte0[3] & 0xff;
                if ((i & 0xff) == 255)
                {
                    cache.sm.addUnicastAddr(cache.sm.controladdress);
                } else
                {
                    InetAddress inetaddress;
                    try
                    {
                        inetaddress = InetAddress.getLocalHost();
                    } catch (UnknownHostException unknownhostexception)
                    {
                        inetaddress = null;
                    }
                    if (inetaddress != null)
                        cache.sm.addUnicastAddr(inetaddress);
                }
            } else if (!cache.sm.isSenderDefaultAddr(remoteAddress))
            {
                cache.sm.addUnicastAddr(remoteAddress);
            }
        }
        switch (rtcppacket.type)
        {
        default:
            break;

        case RTCPPacket.COMPOUND:
            RTCPCompoundPacket rtcpcompoundpacket
                = (RTCPCompoundPacket) rtcppacket;
            cache.updateavgrtcpsize(rtcpcompoundpacket.length);
            for (int j = 0; j < rtcpcompoundpacket.packets.length; j++)
                sendTo(rtcpcompoundpacket.packets[j]);

            if (cache.sm.cleaner != null)
                cache.sm.cleaner.setClean();
            break;

        case RTCPPacket.SR:
            RTCPSRPacket rtcpsrpacket = (RTCPSRPacket) rtcppacket;
            type = 1;
            if (rtcppacket.base instanceof UDPPacket)
                ssrcinfo = cache.get(rtcpsrpacket.ssrc,
                        ((UDPPacket) rtcppacket.base).remoteAddress,
                        ((UDPPacket) rtcppacket.base).remotePort, type);
            else
                ssrcinfo = cache.get(rtcpsrpacket.ssrc, null, 0, type);
            if (ssrcinfo == null)
                break;
            ssrcinfo.setAlive(true);
            ssrcinfo.lastSRntptimestamp = (rtcpsrpacket.ntptimestampmsw << 32)
                    + rtcpsrpacket.ntptimestamplsw;
            ssrcinfo.lastSRrtptimestamp = rtcpsrpacket.rtptimestamp;
            ssrcinfo.lastSRreceiptTime = rtcpsrpacket.receiptTime;
            ssrcinfo.lastRTCPreceiptTime = rtcpsrpacket.receiptTime;
            ssrcinfo.lastHeardFrom = rtcpsrpacket.receiptTime;
            if (ssrcinfo.quiet)
            {
                ssrcinfo.quiet = false;
                ActiveReceiveStreamEvent activereceivestreamevent
                    = new ActiveReceiveStreamEvent(
                            cache.sm,
                            ssrcinfo.sourceInfo,
                            (ssrcinfo instanceof ReceiveStream)
                                ? (ReceiveStream) ssrcinfo
                                : null);
                cache.eventhandler.postEvent(activereceivestreamevent);
            }
            ssrcinfo.lastSRpacketcount = rtcpsrpacket.packetcount;
            ssrcinfo.lastSRoctetcount = rtcpsrpacket.octetcount;
            for (int k = 0; k < rtcpsrpacket.reports.length; k++)
            {
                rtcpsrpacket.reports[k].receiptTime = rtcpsrpacket.receiptTime;
                int l = rtcpsrpacket.reports[k].ssrc;
                RTCPReportBlock artcpreportblock[] = ssrcinfo.reports.get(l);
                if (artcpreportblock == null)
                {
                    artcpreportblock = new RTCPReportBlock[2];
                    artcpreportblock[0] = rtcpsrpacket.reports[k];
                    ssrcinfo.reports.put(l, artcpreportblock);
                } else
                {
                    artcpreportblock[1] = artcpreportblock[0];
                    artcpreportblock[0] = rtcpsrpacket.reports[k];
                }
            }

            if (ssrcinfo.probation > 0)
                break;
            if (!ssrcinfo.newpartsent && ssrcinfo.sourceInfo != null)
            {
                NewParticipantEvent newparticipantevent
                    = new NewParticipantEvent(cache.sm, ssrcinfo.sourceInfo);
                cache.eventhandler.postEvent(newparticipantevent);
                ssrcinfo.newpartsent = true;
            }
            if (!ssrcinfo.recvstrmap && ssrcinfo.sourceInfo != null)
            {
                ssrcinfo.recvstrmap = true;
                StreamMappedEvent streammappedevent
                    = new StreamMappedEvent(
                            cache.sm,
                            (ReceiveStream) ssrcinfo,
                            ssrcinfo.sourceInfo);
                cache.eventhandler.postEvent(streammappedevent);
            }
            SenderReportEvent senderreportevent
                = new SenderReportEvent(cache.sm, (SenderReport) ssrcinfo);
            cache.eventhandler.postEvent(senderreportevent);
            break;

        case RTCPPacket.RR:
            RTCPRRPacket rtcprrpacket = (RTCPRRPacket) rtcppacket;
            type = 2;
            if (rtcppacket.base instanceof UDPPacket)
                ssrcinfo = cache.get(rtcprrpacket.ssrc,
                        ((UDPPacket) rtcppacket.base).remoteAddress,
                        ((UDPPacket) rtcppacket.base).remotePort, type);
            else
                ssrcinfo = cache.get(rtcprrpacket.ssrc, null, 0, type);
            if (ssrcinfo == null)
                break;
            ssrcinfo.setAlive(true);
            ssrcinfo.lastRTCPreceiptTime = rtcprrpacket.receiptTime;
            ssrcinfo.lastHeardFrom = rtcprrpacket.receiptTime;
            if (ssrcinfo.quiet)
            {
                ssrcinfo.quiet = false;
                ActiveReceiveStreamEvent activereceivestreamevent1 = null;
                if (ssrcinfo instanceof ReceiveStream)
                    activereceivestreamevent1 = new ActiveReceiveStreamEvent(
                            cache.sm, ssrcinfo.sourceInfo,
                            (ReceiveStream) ssrcinfo);
                else
                    activereceivestreamevent1 = new ActiveReceiveStreamEvent(
                            cache.sm, ssrcinfo.sourceInfo, null);
                cache.eventhandler.postEvent(activereceivestreamevent1);
            }
            for (int i1 = 0; i1 < rtcprrpacket.reports.length; i1++)
            {
                rtcprrpacket.reports[i1].receiptTime = rtcprrpacket.receiptTime;
                int j1 = rtcprrpacket.reports[i1].ssrc;
                RTCPReportBlock artcpreportblock1[] = ssrcinfo.reports.get(j1);
                if (artcpreportblock1 == null)
                {
                    artcpreportblock1 = new RTCPReportBlock[2];
                    artcpreportblock1[0] = rtcprrpacket.reports[i1];
                    ssrcinfo.reports.put(j1, artcpreportblock1);
                } else
                {
                    artcpreportblock1[1] = artcpreportblock1[0];
                    artcpreportblock1[0] = rtcprrpacket.reports[i1];
                }
            }

            if (!ssrcinfo.newpartsent && ssrcinfo.sourceInfo != null)
            {
                NewParticipantEvent newparticipantevent1
                    = new NewParticipantEvent(cache.sm, ssrcinfo.sourceInfo);
                cache.eventhandler.postEvent(newparticipantevent1);
                ssrcinfo.newpartsent = true;
            }
            ReceiverReportEvent receiverreportevent
                = new ReceiverReportEvent(cache.sm, (ReceiverReport) ssrcinfo);
            cache.eventhandler.postEvent(receiverreportevent);
            break;

        case RTCPPacket.SDES:
            RTCPSDESPacket rtcpsdespacket = (RTCPSDESPacket) rtcppacket;
            for (int k1 = 0; k1 < rtcpsdespacket.sdes.length; k1++)
            {
                RTCPSDES rtcpsdes = rtcpsdespacket.sdes[k1];
                if (type == 1 || type == 2)
                {
                    if (rtcppacket.base instanceof UDPPacket)
                        ssrcinfo = cache.get(
                                rtcpsdes.ssrc,
                                ((UDPPacket) rtcppacket.base).remoteAddress,
                                ((UDPPacket) rtcppacket.base).remotePort,
                                type);
                    else
                        ssrcinfo = cache.get(rtcpsdes.ssrc, null, 0, type);
                }
                if (ssrcinfo == null)
                    break;
                ssrcinfo.setAlive(true);
                ssrcinfo.lastHeardFrom = rtcpsdespacket.receiptTime;
                ssrcinfo.addSDESInfo(rtcpsdes);
            }

            if (ssrcinfo != null && !ssrcinfo.newpartsent
                    && ssrcinfo.sourceInfo != null)
            {
                NewParticipantEvent newparticipantevent2
                    = new NewParticipantEvent(cache.sm, ssrcinfo.sourceInfo);
                cache.eventhandler.postEvent(newparticipantevent2);
                ssrcinfo.newpartsent = true;
            }
            if (ssrcinfo != null && !ssrcinfo.recvstrmap
                    && ssrcinfo.sourceInfo != null
                    && (ssrcinfo instanceof RecvSSRCInfo))
            {
                ssrcinfo.recvstrmap = true;
                StreamMappedEvent streammappedevent1
                    = new StreamMappedEvent(
                            cache.sm,
                            (ReceiveStream) ssrcinfo,
                            ssrcinfo.sourceInfo);
                cache.eventhandler.postEvent(streammappedevent1);
            }
            type = 0;
            break;

        case RTCPPacket.BYE:
            RTCPBYEPacket rtcpbyepacket = (RTCPBYEPacket) rtcppacket;
            SSRCInfo ssrcinfo1;
            if (rtcppacket.base instanceof UDPPacket)
                ssrcinfo1 = cache.get(rtcpbyepacket.ssrc[0],
                        ((UDPPacket) rtcppacket.base).remoteAddress,
                        ((UDPPacket) rtcppacket.base).remotePort);
            else
                ssrcinfo1 = cache.get(rtcpbyepacket.ssrc[0], null, 0);
            for (int l1 = 0; l1 < rtcpbyepacket.ssrc.length; l1++)
            {
                if (rtcppacket.base instanceof UDPPacket)
                    ssrcinfo1 = cache.get(rtcpbyepacket.ssrc[l1],
                            ((UDPPacket) rtcppacket.base).remoteAddress,
                            ((UDPPacket) rtcppacket.base).remotePort);
                else
                    ssrcinfo1 = cache.get(rtcpbyepacket.ssrc[l1], null, 0);
                if (ssrcinfo1 == null)
                    break;
                if (!cache.byestate)
                {
                    ssrcinfo1.setAlive(false);
                    ssrcinfo1.byeReceived = true;
                    ssrcinfo1.byeTime = rtcppacket.receiptTime;
                    ssrcinfo1.lastHeardFrom = rtcpbyepacket.receiptTime;
                }
            }

            if (ssrcinfo1 == null)
                break;
            if (ssrcinfo1.quiet)
            {
                ssrcinfo1.quiet = false;
                ActiveReceiveStreamEvent activereceivestreamevent2
                    = new ActiveReceiveStreamEvent(
                            cache.sm,
                            ssrcinfo1.sourceInfo,
                            (ssrcinfo1 instanceof ReceiveStream)
                                ? (ReceiveStream) ssrcinfo1
                                : null);
                cache.eventhandler.postEvent(activereceivestreamevent2);
            }
            ssrcinfo1.byereason = new String(rtcpbyepacket.reason);
            if (ssrcinfo1.byeReceived)
                break;
            boolean flag2 = false;
            RTPSourceInfo rtpsourceinfo = ssrcinfo1.sourceInfo;
            if (rtpsourceinfo != null && rtpsourceinfo.getStreamCount() == 0)
                flag2 = true;
            ByeEvent byeevent = null;
            if (ssrcinfo1 instanceof RecvSSRCInfo)
                byeevent = new ByeEvent(cache.sm, ssrcinfo1.sourceInfo,
                        (ReceiveStream) ssrcinfo1, new String(
                                rtcpbyepacket.reason), flag2);
            if (ssrcinfo1 instanceof PassiveSSRCInfo)
                byeevent = new ByeEvent(cache.sm, ssrcinfo1.sourceInfo, null,
                        new String(rtcpbyepacket.reason), flag2);
            cache.eventhandler.postEvent(byeevent);
            /*
             * damencho: Remove the ssrc from the cache table because we have
             * received a bye for the stream. If we continue to receive the
             * stream, it will be reported as new one. It is related to the
             * problem with reINVITEs and the changing of the codecs.
             */
            cache.remove(ssrcinfo1.ssrc);
            break;

        case RTCPPacket.APP:
            RTCPAPPPacket rtcpapppacket = (RTCPAPPPacket) rtcppacket;
            SSRCInfo ssrcinfo2;
            if (rtcppacket.base instanceof UDPPacket)
                ssrcinfo2 = cache.get(rtcpapppacket.ssrc,
                        ((UDPPacket) rtcppacket.base).remoteAddress,
                        ((UDPPacket) rtcppacket.base).remotePort);
            else
                ssrcinfo2 = cache.get(rtcpapppacket.ssrc, null, 0);
            if (ssrcinfo2 == null)
                break;
            ssrcinfo2.lastHeardFrom = rtcpapppacket.receiptTime;
            if (ssrcinfo2.quiet)
            {
                ssrcinfo2.quiet = false;
                ActiveReceiveStreamEvent activereceivestreamevent3
                    = new ActiveReceiveStreamEvent(
                            cache.sm,
                            ssrcinfo2.sourceInfo,
                            (ssrcinfo2 instanceof ReceiveStream)
                                ? (ReceiveStream) ssrcinfo2
                                : null);
                cache.eventhandler.postEvent(activereceivestreamevent3);
            }
            ApplicationEvent applicationevent
                = new ApplicationEvent(
                        cache.sm,
                        ssrcinfo2.sourceInfo,
                        (ssrcinfo2 instanceof ReceiveStream)
                            ? (ReceiveStream) ssrcinfo2
                            : null,
                        rtcpapppacket.subtype,
                        null,
                        rtcpapppacket.data);
            cache.eventhandler.postEvent(applicationevent);
            break;
        }
    }
}
