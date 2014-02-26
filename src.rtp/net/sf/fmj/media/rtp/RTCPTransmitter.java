package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;
import java.util.*;

import net.sf.fmj.media.rtp.util.*;

public class RTCPTransmitter
{
    RTCPRawSender sender;
    OverallStats stats;
    SSRCCache cache;
    int sdescounter;
    SSRCInfo ssrcInfo;

    public RTCPTransmitter(SSRCCache cache)
    {
        stats = null;
        sdescounter = 0;
        ssrcInfo = null;
        this.cache = cache;
        stats = cache.sm.defaultstats;
    }

    public RTCPTransmitter(SSRCCache cache, int port, String address)
            throws UnknownHostException, IOException
    {
        this(cache, new RTCPRawSender(port, address));
    }

    public RTCPTransmitter(SSRCCache cache, int port, String address,
            UDPPacketSender sender) throws UnknownHostException, IOException
    {
        this(cache, new RTCPRawSender(port, address, sender));
    }

    public RTCPTransmitter(SSRCCache cache, RTCPRawSender sender)
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
        Vector repvec = makereports();
        RTCPPacket packets[] = new RTCPPacket[repvec.size() + 1];
        repvec.copyInto(packets);
        int ssrclist[] = new int[1];
        ssrclist[0] = ssrc;
        RTCPBYEPacket byep = new RTCPBYEPacket(ssrclist, reason);
        packets[packets.length - 1] = byep;
        RTCPCompoundPacket cp = new RTCPCompoundPacket(packets);
        RTCPTransmitter _tmp = this;
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
    }

    public void close()
    {
        if (sender != null)
            sender.closeConsumer();
    }

    public RTCPRawSender getSender()
    {
        return sender;
    }

    protected RTCPReportBlock[] makerecreports(long time)
    {
        Vector reports = new Vector();
        for (Enumeration<Object> elements = cache.cache.elements();
                elements.hasMoreElements();)
        {
            SSRCInfo info = (SSRCInfo) elements.nextElement();
            if (!info.ours && info.sender)
            {
                RTCPReportBlock rep = new RTCPReportBlock();
                rep.ssrc = info.ssrc;
                rep.lastseq = info.maxseq + info.cycles;
                rep.jitter = (int) info.jitter;
                rep.lsr = (int) ((info.lastSRntptimestamp & 0x0000ffffffff0000L) >> 16);
                rep.dlsr = (int) ((time - info.lastSRreceiptTime) * 65.536000000000001D);
                rep.packetslost = (int) (((rep.lastseq - info.baseseq) + 1L) - info.received);
                if (rep.packetslost < 0)
                    rep.packetslost = 0;
                double frac = (double) (rep.packetslost - info.prevlost)
                        / (double) (rep.lastseq - info.prevmaxseq);
                if (frac < 0.0D)
                    frac = 0.0D;
                rep.fractionlost = (int) (frac * 256D);
                info.prevmaxseq = (int) rep.lastseq;
                info.prevlost = rep.packetslost;
                reports.addElement(rep);
            }
        }

        RTCPReportBlock reportsarr[] = new RTCPReportBlock[reports.size()];
        reports.copyInto(reportsarr);
        return reportsarr;
    }

    protected Vector makereports()
    {
        Vector packets = new Vector();
        SSRCInfo ourinfo = ssrcInfo;
        boolean senderreport = false;
        if (ourinfo.sender)
            senderreport = true;
        long time = System.currentTimeMillis();
        RTCPReportBlock reports[] = makerecreports(time);
        RTCPReportBlock firstrep[] = reports;
        if (reports.length > 31)
        {
            firstrep = new RTCPReportBlock[31];
            System.arraycopy(reports, 0, firstrep, 0, 31);
        }
        if (senderreport)
        {
            RTCPSRPacket srp = new RTCPSRPacket(ourinfo.ssrc, firstrep);
            packets.addElement(srp);
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
            packets.addElement(rrp);
        }
        if (firstrep != reports)
        {
            for (int offset = 31; offset < reports.length; offset += 31)
            {
                if (reports.length - offset < 31)
                    firstrep = new RTCPReportBlock[reports.length - offset];
                System.arraycopy(reports, offset, firstrep, 0, firstrep.length);
                RTCPRRPacket rrp = new RTCPRRPacket(ourinfo.ssrc, firstrep);
                packets.addElement(rrp);
            }

        }
        RTCPSDESPacket sp = new RTCPSDESPacket(new RTCPSDES[1]);
        sp.sdes[0] = new RTCPSDES();
        sp.sdes[0].ssrc = ssrcInfo.ssrc;
        Vector<RTCPSDESItem> itemvec = new Vector<RTCPSDESItem>();
        itemvec.addElement(new RTCPSDESItem(1, ourinfo.sourceInfo.getCNAME()));
        if (sdescounter % 3 == 0)
        {
            if (ourinfo.name != null && ourinfo.name.getDescription() != null)
                itemvec.addElement(new RTCPSDESItem(2, ourinfo.name
                        .getDescription()));
            if (ourinfo.email != null && ourinfo.email.getDescription() != null)
                itemvec.addElement(new RTCPSDESItem(3, ourinfo.email
                        .getDescription()));
            if (ourinfo.phone != null && ourinfo.phone.getDescription() != null)
                itemvec.addElement(new RTCPSDESItem(4, ourinfo.phone
                        .getDescription()));
            if (ourinfo.loc != null && ourinfo.loc.getDescription() != null)
                itemvec.addElement(new RTCPSDESItem(5, ourinfo.loc
                        .getDescription()));
            if (ourinfo.tool != null && ourinfo.tool.getDescription() != null)
                itemvec.addElement(new RTCPSDESItem(6, ourinfo.tool
                        .getDescription()));
            if (ourinfo.note != null && ourinfo.note.getDescription() != null)
                itemvec.addElement(new RTCPSDESItem(7, ourinfo.note
                        .getDescription()));
        }
        sdescounter++;
        sp.sdes[0].items = new RTCPSDESItem[itemvec.size()];
        itemvec.copyInto(sp.sdes[0].items);
        packets.addElement(sp);
        return packets;
    }

    public void report()
    {
        Vector repvec = makereports();
        RTCPPacket packets[] = new RTCPPacket[repvec.size()];
        repvec.copyInto(packets);
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
            stats.update(6, 1);
            cache.sm.transstats.transmit_failed++;
        }
    }
}
