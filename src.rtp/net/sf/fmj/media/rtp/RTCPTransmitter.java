package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;
import java.util.*;

import net.sf.fmj.media.rtp.util.*;

public class RTCPTransmitter
{
    RTCPRawSender sender;
    OverallStats stats;
    public SSRCCache cache;
    RTCPReportBuilder reportBuilder;
    public SSRCInfo ssrcInfo;

    public RTCPTransmitter(SSRCCache cache)
    {
        stats = null;
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

        RTCPReportBuilder rb = getReportBuilder();
        RTCPPacket reports[] = rb.makeReports();

        RTCPPacket packets[];
        if (reports == null || reports.length == 0)
            packets = new RTCPPacket[1];
        else
        {
            packets = new RTCPPacket[reports.length + 1];
            System.arraycopy(reports, 0, packets, 0, reports.length);
        }

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
        rb.reset();
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

    public void report()
    {
        RTCPPacket packets[] = getReportBuilder().makeReports();
        if (packets == null || packets.length == 0)
            return;
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
            stats.update(OverallStats.TRANSMITFAILED, 1);
            cache.sm.transstats.transmit_failed++;
        }
    }

    public void setReportBuilder(
            RTCPReportBuilder reportBuilder)
    {
        this.reportBuilder = reportBuilder;
        this.reportBuilder.setRTCPTransmitter(this);
    }

    private RTCPReportBuilder getReportBuilder()
    {
        if (reportBuilder == null)
            setReportBuilder(new DefaultRTCPReportBuilderImpl());

        return reportBuilder;
    }
}
