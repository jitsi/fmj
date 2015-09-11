package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;
import java.util.*;

import net.sf.fmj.media.rtp.util.*;

import javax.media.rtp.rtcp.*;

/**
 * The default implementation of the <tt>RTCPTransmitter</tt> interface.
 */
public class DefaultRTCPTransmitterImpl
    implements RTCPTransmitter
{
    RTCPRawSender sender;
    OverallStats stats;
    SSRCCache cache;
    int sdescounter;
    SSRCInfo ssrcInfo;

    public DefaultRTCPTransmitterImpl(SSRCCache cache)
    {
        stats = null;
        sdescounter = 0;
        ssrcInfo = null;
        this.cache = cache;
        stats = cache.sm.defaultstats;
    }

    public DefaultRTCPTransmitterImpl(SSRCCache cache, int port, String address)
            throws UnknownHostException, IOException
    {
        this(cache, new RTCPRawSender(port, address));
    }

    public DefaultRTCPTransmitterImpl(SSRCCache cache, int port, String address,
            UDPPacketSender sender) throws UnknownHostException, IOException
    {
        this(cache, new RTCPRawSender(port, address, sender));
    }

    public DefaultRTCPTransmitterImpl(SSRCCache cache, RTCPRawSender sender)
    {
        this(cache);
        setSender(sender);
        stats = cache.sm.defaultstats;
    }

    public void bye(int ssrc, byte reason[])
    {
        if (!cache.rtcpsent)
            return;
        cache.byestate = true;
        List<RTCPPacket> repvec = makereports();
        RTCPPacket packets[] = repvec.toArray(new RTCPPacket[repvec.size() + 1]);
        int ssrclist[] = new int[1];
        ssrclist[0] = ssrc;
        RTCPBYEPacket byep = new RTCPBYEPacket(ssrclist, reason);
        packets[packets.length - 1] = byep;
        RTCPCompoundPacket cp = new RTCPCompoundPacket(packets);
        double delay;
        if (cache.aliveCount() > 50)
        {
            cache.reset(((Packet) (byep)).length);
            delay = cache.calcReportInterval(ssrcInfo.sender, false);
        } else
        {
            delay = 0.0D;
        }
        try
        {
            Thread.sleep((long) delay);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        transmit(cp);
        sdescounter = 0;
    }

    public void bye(String reason)
    {
        if (reason != null)
            bye(ssrcInfo.ssrc, reason.getBytes());
        else
            bye(ssrcInfo.ssrc, null);

        ssrcInfo.setOurs(false);
        ssrcInfo = null;
    }

    public void close()
    {
        if (sender != null)
            sender.closeConsumer();
    }

    protected RTCPReportBlock[] makerecreports(long time)
    {
        List<RTCPReportBlock> reports = new ArrayList<RTCPReportBlock>();

        // Make receiver reports for all known SSRCs.
        for (Enumeration<SSRCInfo> elements = cache.cache.elements();
             elements.hasMoreElements();)
        {
            SSRCInfo info = elements.nextElement();

            if (!info.ours && info.sender)
                reports.add(info.makeReceiverReport(time));
        }

        // Copy into an array and return.
        return reports.toArray(new RTCPReportBlock[reports.size()]);
    }

    protected List<RTCPPacket> makereports()
    {
        List<RTCPPacket> packets = new ArrayList<RTCPPacket>();
        SSRCInfo ourinfo = ssrcInfo;
        boolean senderreport = ourinfo.sender;
        long time = System.currentTimeMillis();
        RTCPReportBlock reports[] = makerecreports(time);
        RTCPReportBlock firstrep[] = reports;

        // If the number of sources for which reception statistics are being
        // reported exceeds 31, the number that will fit into one SR or RR
        // packet, then additional RR packets SHOULD follow the initial report
        // packet.
        if (reports.length > 31)
        {
            firstrep = new RTCPReportBlock[31];
            System.arraycopy(reports, 0, firstrep, 0, 31);
        }
        if (senderreport)
        {
            RTCPSRPacket srp = new RTCPSRPacket(ourinfo.ssrc, firstrep);
            packets.add(srp);
            long systime = ourinfo.systime == 0L ? System.currentTimeMillis()
                    : ourinfo.systime;
            long secs = systime / 1000L;
            double msecs = (systime - secs * 1000L) / 1000D;
            srp.ntptimestamplsw = (int) (msecs * 4294967296D);
            srp.ntptimestampmsw = secs;
            srp.rtptimestamp = (int) ourinfo.rtptime;
            srp.packetcount = ourinfo.maxseq - ourinfo.baseseq;
            srp.octetcount = ourinfo.bytesreceived;
        } else
        {
            RTCPRRPacket rrp = new RTCPRRPacket(ourinfo.ssrc, firstrep);
            packets.add(rrp);
        }
        if (firstrep != reports)
        {
            // Since a maximum of 31 reception report blocks will fit in an SR
            // or RR packet, additional RR packets SHOULD be stacked after the
            // initial SR or RR packet as needed to contain the reception
            // reports for all sources heard during the interval since the last
            // report.
            for (int offset = 31; offset < reports.length; offset += 31)
            {
                if (reports.length - offset < 31)
                    firstrep = new RTCPReportBlock[reports.length - offset];
                System.arraycopy(reports, offset, firstrep, 0, firstrep.length);
                RTCPRRPacket rrp = new RTCPRRPacket(ourinfo.ssrc, firstrep);
                packets.add(rrp);
            }

        }
        RTCPSDESPacket sp = new RTCPSDESPacket(new RTCPSDES[1]);
        sp.sdes[0] = new RTCPSDES();
        sp.sdes[0].ssrc = ssrcInfo.ssrc;
        List<RTCPSDESItem> sdesItems = new ArrayList<RTCPSDESItem>();
        sdesItems.add(
            new RTCPSDESItem(RTCPSDESItem.CNAME, ourinfo.sourceInfo.getCNAME()));

        // We send detailed SDES information every 3 report interval to avoid
        // RTCP bandwidth overuse. See RFC3550, section 6.3.9 : Allocation of
        // Source Description Bandwidth.
        if (sdescounter % 3 == 0)
        {
            SourceDescription sd;
            String s;

            // NAME
            if ((sd = ourinfo.name) != null
                && (s = sd.getDescription()) != null)
                sdesItems.add(new RTCPSDESItem(RTCPSDESItem.NAME, s));
            // EMAIL
            if ((sd = ourinfo.email) != null
                && (s = sd.getDescription()) != null)
                sdesItems.add(new RTCPSDESItem(RTCPSDESItem.EMAIL, s));
            // PHONE
            if ((sd = ourinfo.phone) != null
                && (s = sd.getDescription()) != null)
                sdesItems.add(new RTCPSDESItem(RTCPSDESItem.PHONE, s));
            // LOC
            if ((sd = ourinfo.loc) != null
                && (s = sd.getDescription()) != null)
                sdesItems.add(new RTCPSDESItem(RTCPSDESItem.LOC, s));
            // TOOL
            if ((sd = ourinfo.tool) != null
                && (s = sd.getDescription()) != null)
                sdesItems.add(new RTCPSDESItem(RTCPSDESItem.TOOL, s));
            // NOTE
            if ((sd = ourinfo.note) != null
                && (s = sd.getDescription()) != null)
                sdesItems.add(new RTCPSDESItem(RTCPSDESItem.NOTE, s));
        }
        sdescounter++;
        sp.sdes[0].items = sdesItems.toArray(new RTCPSDESItem[sdesItems.size()]);
        packets.add(sp);
        return packets;
    }

    public void report()
    {
        List<RTCPPacket> repvec = makereports();
        RTCPPacket packets[] = repvec.toArray(new RTCPPacket[repvec.size()]);
        RTCPCompoundPacket cp = new RTCPCompoundPacket(packets);
        transmit(cp);
    }

    public void setSender(RTCPRawSender s)
    {
        sender = s;
    }

    public void setSSRCInfo(SSRCInfo info)
    {
        ssrcInfo = info;
    }

    public SSRCInfo getSSRCInfo()
    {
        return ssrcInfo;
    }

    protected void transmit(RTCPCompoundPacket p)
    {
        try
        {
            sender.sendTo(p);
            if (ssrcInfo instanceof SendSSRCInfo)
            {
                ((SendSSRCInfo) ssrcInfo).stats.total_rtcp++;
                cache.sm.transstats.rtcp_sent++;
            }
            cache.updateavgrtcpsize(((Packet) (p)).length);
            if (cache.initial)
                cache.initial = false;
            if (!cache.rtcpsent)
                cache.rtcpsent = true;
        } catch (IOException e)
        {
            stats.update(OverallStats.TRANSMITFAILED, 1);
            cache.sm.transstats.transmit_failed++;
        }
    }

    public SSRCCache getCache()
    {
        return cache;
    }

    public RTCPRawSender getSender()
    {
        return sender;
    }
}
