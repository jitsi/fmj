package net.sf.fmj.media.rtp;

import java.util.*;

import javax.media.rtp.rtcp.*;

/**
 * Created by gp on 6/12/14.
 */
public class DefaultRTCPReportBuilderImpl extends AbstractRTCPReportBuilder
{
    private int sdescounter;

    public DefaultRTCPReportBuilderImpl()
    {
    }

    private RTCPReportBlock[] makeReceiverReports(
            RTCPTransmitter rtcpTransmitter,
            long time)
    {
        List<RTCPReportBlock> reports = new ArrayList<RTCPReportBlock>();

        // Make receiver reports for all known SSRCs.
        for (Enumeration<SSRCInfo> elements
                    = rtcpTransmitter.cache.cache.elements();
                elements.hasMoreElements();)
        {
            SSRCInfo info = elements.nextElement();

            if (!info.ours && info.sender)
                reports.add(info.makeReceiverReport(time));
        }

        // Copy into an array and return.
        return reports.toArray(new RTCPReportBlock[reports.size()]);
    }

    @Override
    public RTCPPacket[] makeReports(RTCPTransmitter rtcpTransmitter)
    {
        long time = System.currentTimeMillis();

        List<RTCPPacket> packets = new ArrayList<RTCPPacket>();
        SSRCInfo ourinfo = rtcpTransmitter.ssrcInfo;
        boolean senderreport = ourinfo.sender;
        RTCPReportBlock reports[] = makeReceiverReports(rtcpTransmitter, time);
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
            long systime
                = ourinfo.systime == 0L
                    ? System.currentTimeMillis()
                    : ourinfo.systime;
            long secs = systime / 1000L;
            double msecs = (systime - secs * 1000L) / 1000D;

            srp.ntptimestamplsw = (int) (msecs * 4294967296D);
            srp.ntptimestampmsw = secs;
            srp.rtptimestamp = (int) ourinfo.rtptime;
            srp.packetcount = ourinfo.maxseq - ourinfo.baseseq;
            srp.octetcount = ourinfo.bytesreceived;
        }
        else
        {
            packets.add(new RTCPRRPacket(ourinfo.ssrc, firstrep));
        }
        if (firstrep != reports)
        {
            for (int offset = 31; offset < reports.length; offset += 31)
            {
                if (reports.length - offset < 31)
                    firstrep = new RTCPReportBlock[reports.length - offset];
                System.arraycopy(reports, offset, firstrep, 0, firstrep.length);
                packets.add(new RTCPRRPacket(ourinfo.ssrc, firstrep));
            }
        }

        List<RTCPSDESItem> sdes0Items
            = new ArrayList<RTCPSDESItem>(RTCPSDESItem.HIGHEST);

        sdes0Items.add(
                new RTCPSDESItem(RTCPSDESItem.CNAME, ourinfo.getCNAME()));

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
                sdes0Items.add(new RTCPSDESItem(RTCPSDESItem.NAME, s));
            // EMAIL
            if ((sd = ourinfo.email) != null
                    && (s = sd.getDescription()) != null)
                sdes0Items.add(new RTCPSDESItem(RTCPSDESItem.EMAIL, s));
            // PHONE
            if ((sd = ourinfo.phone) != null
                    && (s = sd.getDescription()) != null)
                sdes0Items.add(new RTCPSDESItem(RTCPSDESItem.PHONE, s));
            // LOC
            if ((sd = ourinfo.loc) != null
                    && (s = sd.getDescription()) != null)
                sdes0Items.add(new RTCPSDESItem(RTCPSDESItem.LOC, s));
            // TOOL
            if ((sd = ourinfo.tool) != null
                    && (s = sd.getDescription()) != null)
                sdes0Items.add(new RTCPSDESItem(RTCPSDESItem.TOOL, s));
            // NOTE
            if ((sd = ourinfo.note) != null
                    && (s = sd.getDescription()) != null)
                sdes0Items.add(new RTCPSDESItem(RTCPSDESItem.NOTE, s));
        }
        sdescounter++;

        RTCPSDES sdes0 = new RTCPSDES();

        sdes0.items = sdes0Items.toArray(new RTCPSDESItem[sdes0Items.size()]);
        sdes0.ssrc = rtcpTransmitter.ssrcInfo.ssrc;
        packets.add(new RTCPSDESPacket(new RTCPSDES[] { sdes0 }));

        return packets.toArray(new RTCPPacket[packets.size()]);
    }

    @Override
    public void reset()
    {
        sdescounter = 0;
    }
}
