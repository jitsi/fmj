package net.sf.fmj.media.rtp;

import java.util.*;

/**
 * Created by gp on 6/12/14.
 */
public class DefaultRTCPReportBuilderImpl implements RTCPReportBuilder
{
    private int sdescounter;
    private RTCPTransmitter rtcpTransmitter;

    public DefaultRTCPReportBuilderImpl()
    {
        reset();
    }

    @Override
    public RTCPPacket[] makeReports()
    {
        if (rtcpTransmitter == null)
            throw new IllegalStateException("rtcpTransmitter is not set");

        Vector<RTCPPacket> packets = new Vector<RTCPPacket>();
        SSRCInfo ourinfo = rtcpTransmitter.ssrcInfo;
        boolean senderreport = ourinfo.sender;
        long time = System.currentTimeMillis();
        RTCPReportBlock reports[] = makeReceiverReports(time);
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
        sp.sdes[0].ssrc = rtcpTransmitter.ssrcInfo.ssrc;
        Vector<RTCPSDESItem> itemvec = new Vector<RTCPSDESItem>();
        itemvec.addElement(new RTCPSDESItem(1, ourinfo.sourceInfo.getCNAME()));

        // We send detailed SDES information every 3 report interval to avoid
        // RTCP bandwidth overuse. See RFC3550, section 6.3.9 : Allocation of
        // Source Description Bandwidth.

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

        // Copy into an array and return.
        RTCPPacket[] res = new RTCPPacket[packets.size()];
        packets.copyInto(res);
        return res;
    }

    private RTCPReportBlock[] makeReceiverReports(long time)
    {
        Vector<RTCPReportBlock> reports = new Vector<RTCPReportBlock>();

        // Make receiver reports for all known SSRCs.
        for (Enumeration<SSRCInfo> elements = rtcpTransmitter.cache.cache.elements();
             elements.hasMoreElements();)
        {
            SSRCInfo info = elements.nextElement();
            if (!info.ours && info.sender)
            {
                RTCPReportBlock receiverReport = new RTCPReportBlock();
                receiverReport.ssrc = info.ssrc;
                receiverReport.lastseq = info.maxseq + info.cycles;
                receiverReport.jitter = (int) info.jitter;
                receiverReport.lsr = (int) ((info.lastSRntptimestamp & 0x0000ffffffff0000L) >> 16);
                receiverReport.dlsr = (int) ((time - info.lastSRreceiptTime) * 65.536000000000001D);
                receiverReport.packetslost = (int) (((receiverReport.lastseq - info.baseseq) + 1L) - info.received);
                if (receiverReport.packetslost < 0)
                    receiverReport.packetslost = 0;
                double frac = (double) (receiverReport.packetslost - info.prevlost)
                        / (double) (receiverReport.lastseq - info.prevmaxseq);
                if (frac < 0.0D)
                    frac = 0.0D;
                receiverReport.fractionlost = (int) (frac * 256D);
                info.prevmaxseq = (int) receiverReport.lastseq;
                info.prevlost = receiverReport.packetslost;
                reports.addElement(receiverReport);
            }
        }

        // Copy into an array and return.
        RTCPReportBlock res[] = new RTCPReportBlock[reports.size()];
        reports.copyInto(res);
        return res;
    }

    @Override
    public void reset()
    {
        sdescounter = 0;
    }

    @Override
    public void setRTCPTransmitter(RTCPTransmitter rtcpTransmitter)
    {
        this.rtcpTransmitter = rtcpTransmitter;
    }
}
