package net.sf.fmj.media.rtp;

import java.net.*;
import java.util.*;

import javax.media.*;
import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;

import net.sf.fmj.media.protocol.rtp.*;
import net.sf.fmj.media.rtp.util.*;

public abstract class SSRCInfo implements Report
{
    private SSRCCache cache;
    boolean alive;
    boolean payloadchange;
    boolean byeReceived;
    long byeTime;
    String byereason;
    public RTPSourceInfo sourceInfo;
    public SourceDescription name;
    public SourceDescription email;
    public SourceDescription phone;
    public SourceDescription loc;
    public SourceDescription tool;
    public SourceDescription note;
    SourceDescription priv;
    public long lastSRntptimestamp;
    long lastSRrtptimestamp;
    long lastSRoctetcount;
    long lastSRpacketcount;
    long lastRTCPreceiptTime;
    public long lastSRreceiptTime;
    long lastHeardFrom;
    boolean quiet;
    boolean inactivesent;
    boolean aging;
    public boolean sender;
    public boolean ours;
    public int ssrc;
    boolean streamconnect;
    SSRCTable<RTCPReportBlock[]> reports;
    boolean active;
    boolean newrecvstream;
    boolean recvstrmap;
    boolean newpartsent;
    boolean lastsr;
    boolean wrapped;
    static final int INITIALPROBATION = 2;
    int probation;
    static final int PAYLOAD_UNASSIGNED = -1;
    boolean wassender;
    public int prevmaxseq;
    public int prevlost;
    long starttime;
    public long rtptime;
    public long systime;
    InetAddress address;
    int port;
    RTCPReporter reporter;
    Format currentformat;
    int payloadType;
    DataSource dsource;
    javax.media.protocol.DataSource pds;
    RTPSourceStream dstream;
    RTPSinkStream sinkstream;
    long lastRTPReceiptTime;
    public int maxseq;
    public int cycles;
    public int baseseq;
    int lastbadseq;
    public int received;
    long lasttimestamp;
    int lastPayloadType = -1;
    public double jitter;
    public int bytesreceived;
    RTPStats stats;
    int clockrate;

    SSRCInfo(SSRCCache cache, int ssrc)
    {
        alive = false;
        payloadchange = false;
        byeReceived = false;
        byeTime = 0L;
        byereason = null;
        sourceInfo = null;
        name = null;
        email = null;
        phone = null;
        loc = null;
        tool = null;
        note = null;
        priv = null;
        lastSRntptimestamp = 0L;
        lastSRrtptimestamp = 0L;
        lastSRoctetcount = 0L;
        lastSRpacketcount = 0L;
        lastRTCPreceiptTime = 0L;
        lastSRreceiptTime = 0L;
        lastHeardFrom = 0L;
        quiet = false;
        inactivesent = false;
        sender = false;
        ours = false;
        streamconnect = false;
        reports = new SSRCTable<RTCPReportBlock[]>();
        active = false;
        newrecvstream = false;
        recvstrmap = false;
        newpartsent = false;
        lastsr = false;
        wrapped = false;
        probation = INITIALPROBATION;
        wassender = false;
        currentformat = null;
        payloadType = -1;
        dsource = null;
        pds = null;
        dstream = null;
        sinkstream = null;
        maxseq = 0;
        cycles = 0;
        lasttimestamp = 0L;
        jitter = 0.0D;
        clockrate = 0;
        this.cache = cache;
        this.ssrc = ssrc;
        stats = new RTPStats();
    }

    SSRCInfo(SSRCInfo info)
    {
        alive = false;
        payloadchange = false;
        byeReceived = false;
        byeTime = 0L;
        byereason = null;
        sourceInfo = null;
        name = null;
        email = null;
        phone = null;
        loc = null;
        tool = null;
        note = null;
        priv = null;
        lastSRoctetcount = 0L;
        lastSRpacketcount = 0L;
        lastSRreceiptTime = 0L;
        lastHeardFrom = 0L;
        quiet = false;
        inactivesent = false;
        sender = false;
        ours = false;
        streamconnect = false;
        reports = new SSRCTable<RTCPReportBlock[]>();
        active = false;
        newrecvstream = false;
        recvstrmap = false;
        newpartsent = false;
        lastsr = false;
        wrapped = false;
        probation = INITIALPROBATION;
        wassender = false;
        currentformat = null;
        payloadType = -1;
        pds = null;
        sinkstream = null;
        maxseq = 0;
        cycles = 0;
        lasttimestamp = 0L;
        jitter = 0.0D;
        clockrate = 0;
        cache = info.cache;
        alive = info.alive;
        sourceInfo = info.sourceInfo;
        if (sourceInfo != null)
            sourceInfo.addSSRC(this);
        cache.remove(info.ssrc);
        name = info.name;
        email = info.email;
        phone = info.phone;
        loc = info.loc;
        tool = info.tool;
        note = info.note;
        priv = info.priv;
        lastSRntptimestamp = info.lastSRntptimestamp;
        lastSRrtptimestamp = info.lastSRrtptimestamp;
        lastSRoctetcount = info.lastSRoctetcount;
        lastSRpacketcount = info.lastSRpacketcount;
        lastRTCPreceiptTime = info.lastRTCPreceiptTime;
        lastSRreceiptTime = info.lastSRreceiptTime;
        lastHeardFrom = info.lastHeardFrom;
        quiet = info.quiet;
        inactivesent = info.inactivesent;
        aging = info.aging;
        reports = info.reports;
        ours = info.ours;
        ssrc = info.ssrc;
        streamconnect = info.streamconnect;
        newrecvstream = info.newrecvstream;
        recvstrmap = info.recvstrmap;
        newpartsent = info.newpartsent;
        lastsr = info.lastsr;
        probation = info.probation;
        wassender = info.wassender;
        prevmaxseq = info.prevmaxseq;
        prevlost = info.prevlost;
        starttime = info.starttime;
        reporter = info.reporter;
        if (info.reporter != null)
            reporter.transmit.setSSRCInfo(this);
        payloadType = info.payloadType;
        dsource = info.dsource;
        pds = info.pds;
        dstream = info.dstream;
        lastRTPReceiptTime = info.lastRTPReceiptTime;
        maxseq = info.maxseq;
        cycles = info.cycles;
        baseseq = info.baseseq;
        lastbadseq = info.lastbadseq;
        received = info.received;
        lasttimestamp = info.lasttimestamp;
        lastPayloadType = info.lastPayloadType;
        jitter = info.jitter;
        bytesreceived = info.bytesreceived;
        address = info.address;
        port = info.port;
        stats = info.stats;
        clockrate = info.clockrate;
        byeTime = info.byeTime;
        byeReceived = info.byeReceived;
    }

    void addSDESInfo(RTCPSDES chunk)
    {
        int ci;
        for (ci = 0; ci < chunk.items.length; ci++)
            if (chunk.items[ci].type == 1)
                break;

        String s = new String(chunk.items[ci].data);
        String sourceinfocname = null;
        if (sourceInfo != null)
            sourceinfocname = sourceInfo.getCNAME();
        if (sourceInfo != null && !s.equals(sourceinfocname))
        {
            sourceInfo.removeSSRC(this);
            sourceInfo = null;
        }
        if (sourceInfo == null)
        {
            sourceInfo = cache.sourceInfoCache.get(s, ours);
            sourceInfo.addSSRC(this);
        }
        if (chunk.items.length > 1)
        {
            for (int i = 0; i < chunk.items.length; i++)
            {
                s = new String(chunk.items[i].data);
                switch (chunk.items[i].type)
                {
                default:
                    break;

                case 2: // '\002'
                    if (name == null)
                        name = new SourceDescription(2, s, 0, false);
                    else
                        name.setDescription(s);
                    break;

                case 3: // '\003'
                    if (email == null)
                        email = new SourceDescription(3, s, 0, false);
                    else
                        email.setDescription(s);
                    break;

                case 4: // '\004'
                    if (phone == null)
                        phone = new SourceDescription(4, s, 0, false);
                    else
                        phone.setDescription(s);
                    break;

                case 5: // '\005'
                    if (loc == null)
                        loc = new SourceDescription(5, s, 0, false);
                    else
                        loc.setDescription(s);
                    break;

                case 6: // '\006'
                    if (tool == null)
                        tool = new SourceDescription(6, s, 0, false);
                    else
                        tool.setDescription(s);
                    break;

                case 7: // '\007'
                    if (note == null)
                        note = new SourceDescription(7, s, 0, false);
                    else
                        note.setDescription(s);
                    break;

                case 8: // '\b'
                    if (priv == null)
                        priv = new SourceDescription(8, s, 0, false);
                    else
                        priv.setDescription(s);
                    break;
                }
            }

        }
    }

    void delete()
    {
        if (sourceInfo != null)
            sourceInfo.removeSSRC(this);
    }

    public String getCNAME()
    {
        return sourceInfo != null ? sourceInfo.getCNAME() : null;
    }

    public Vector<RTCPReportBlock> getFeedbackReports()
    {
        Vector<RTCPReportBlock> reportlist
            = new Vector<RTCPReportBlock>(reports.size());
        if (reports.size() == 0)
            return reportlist;
        Enumeration<RTCPReportBlock[]> reportblks = reports.elements();
        try
        {
            while (reportblks.hasMoreElements())
            {
                RTCPReportBlock[] reportblklist = reportblks.nextElement();
                RTCPReportBlock report = reportblklist[0];
                reportlist.addElement(report);
            }
        } catch (NoSuchElementException e)
        {
            System.err.println("No more elements");
        }
        reportlist.trimToSize();
        return reportlist;
    }

    public Participant getParticipant()
    {
        if ((sourceInfo instanceof LocalParticipant)
                && cache.sm.IsNonParticipating())
            return null;
        else
            return sourceInfo;
    }

    int getPayloadType()
    {
        return payloadType;
    }

    RTPSourceInfo getRTPSourceInfo()
    {
        return sourceInfo;
    }

    public Vector<SourceDescription> getSourceDescription()
    {
        Vector<SourceDescription> sdeslist = new Vector<SourceDescription>();
        sdeslist.addElement(sourceInfo.getCNAMESDES());
        if (name != null)
            sdeslist.addElement(name);
        if (email != null)
            sdeslist.addElement(email);
        if (phone != null)
            sdeslist.addElement(phone);
        if (loc != null)
            sdeslist.addElement(loc);
        if (tool != null)
            sdeslist.addElement(tool);
        if (note != null)
            sdeslist.addElement(note);
        if (priv != null)
            sdeslist.addElement(priv);
        sdeslist.trimToSize();
        return sdeslist;
    }

    public long getSSRC()
    {
        return ssrc;
    }

    public SSRCCache getSSRCCache()
    {
        return cache;
    }

    private void InitSDES()
    {
        name = new SourceDescription(2, null, 0, false);
        email = new SourceDescription(3, null, 0, false);
        phone = new SourceDescription(4, null, 0, false);
        loc = new SourceDescription(5, null, 0, false);
        tool = new SourceDescription(6, null, 0, false);
        note = new SourceDescription(7, null, 0, false);
        priv = new SourceDescription(8, null, 0, false);
    }

    void initsource(int seqnum)
    {
        if (probation <= 0)
        {
            active = true;
            setSender(true);
        }
        baseseq = seqnum;
        maxseq = seqnum - 1;
        lastbadseq = -2;
        cycles = 0;
        received = 0;
        bytesreceived = 0;
        lastRTPReceiptTime = 0L;
        lasttimestamp = 0L;
        jitter = 0.0D;
        prevmaxseq = maxseq;
        prevlost = 0;
    }

    boolean isActive()
    {
        return active;
    }

    void setAging(boolean beaging)
    {
        if (aging != beaging)
            aging = beaging;
    }

    void setAlive(boolean bealive)
    {
        setAging(false);
        if (alive == bealive)
            return;
        if (bealive)
            reports.removeAll();
        else
            setSender(false);
        alive = bealive;
    }

    public void setOurs(boolean beours)
    {
        if (ours == beours)
            return;
        if (beours)
            setAlive(true);
        else
            setAlive(false);
        ours = beours;
    }

    void setSender(boolean besender)
    {
        if (sender == besender)
            return;
        if (besender)
        {
            cache.sendercount++;
            setAlive(true);
        } else
        {
            cache.sendercount--;
        }
        sender = besender;
    }

    void setSourceDescription(SourceDescription userdesclist[])
    {
        if (userdesclist == null)
            return;
        String cname = null;
        for (SourceDescription currdesc : userdesclist)
        {
            if (currdesc == null || currdesc.getType() != 1)
                continue;
            cname = currdesc.getDescription();
            break;
        }

        String sourceinfocname = null;
        if (sourceInfo != null)
            sourceinfocname = sourceInfo.getCNAME();
        if (sourceInfo != null && cname != null
                && !cname.equals(sourceinfocname))
        {
            sourceInfo.removeSSRC(this);
            sourceInfo = null;
        }
        if (sourceInfo == null)
        {
            sourceInfo = cache.sourceInfoCache.get(cname, true);
            sourceInfo.addSSRC(this);
        }
        for (SourceDescription currdesc : userdesclist)
        {
            if (currdesc != null)
                switch (currdesc.getType())
                {
                default:
                    break;

                case 2: // '\002'
                    if (name == null)
                        name = new SourceDescription(2,
                                currdesc.getDescription(), 0, false);
                    else
                        name.setDescription(currdesc.getDescription());
                    break;

                case 3: // '\003'
                    if (email == null)
                        email = new SourceDescription(3,
                                currdesc.getDescription(), 0, false);
                    else
                        email.setDescription(currdesc.getDescription());
                    break;

                case 4: // '\004'
                    if (phone == null)
                        phone = new SourceDescription(4,
                                currdesc.getDescription(), 0, false);
                    else
                        phone.setDescription(currdesc.getDescription());
                    break;

                case 5: // '\005'
                    if (loc == null)
                        loc = new SourceDescription(5,
                                currdesc.getDescription(), 0, false);
                    else
                        loc.setDescription(currdesc.getDescription());
                    break;

                case 6: // '\006'
                    if (tool == null)
                        tool = new SourceDescription(6,
                                currdesc.getDescription(), 0, false);
                    else
                        tool.setDescription(currdesc.getDescription());
                    break;

                case 7: // '\007'
                    if (note == null)
                        note = new SourceDescription(7,
                                currdesc.getDescription(), 0, false);
                    else
                        note.setDescription(currdesc.getDescription());
                    break;

                case 8: // '\b'
                    if (priv == null)
                        priv = new SourceDescription(8,
                                currdesc.getDescription(), 0, false);
                    else
                        priv.setDescription(currdesc.getDescription());
                    break;
                }
        }
    }

    /**
     * Gets the number of expected packets since the beginning of
     * reception/transmission as defined by RFC 3550 i.e. the extended last
     * sequence number received less the initial sequence number received.
     *
     * @return the number of expected packets since the beginning of
     * reception/transmission
     */
    public long getExpectedPacketCount()
    {
        long maxseq = this.maxseq & 0xFFFFL;
        long cycles = this.cycles;
        long baseseq = this.baseseq & 0xFFFFL;

        return maxseq + cycles - baseseq + 1;
    }

    /**
     * Makes a reception report for this synchronization source from which this
     * receiver has received RTP data packets since the last report as per
     * RFC3550. It provides statistics about the data received from this
     * particular source.
     *
     * @param time
     * @return
     */
    public RTCPReportBlock makeReceiverReport(long time)
    {
        // TODO(gp) we probably need a mutexes here.
        RTCPReportBlock receiverReport = new RTCPReportBlock();

        receiverReport.ssrc = this.ssrc;
        receiverReport.lastseq = this.maxseq + this.cycles;
        receiverReport.jitter = (int) this.jitter;
        receiverReport.lsr = (int) ((this.lastSRntptimestamp & 0x0000ffffffff0000L) >> 16);
        receiverReport.dlsr = (int) ((time - this.lastSRreceiptTime) * 65.536000000000001D);
        receiverReport.packetslost = (int) (((receiverReport.lastseq - this.baseseq) + 1L) - this.received);
        if (receiverReport.packetslost < 0)
            receiverReport.packetslost = 0;
        double frac = (double) (receiverReport.packetslost - this.prevlost)
                / (double) (receiverReport.lastseq - this.prevmaxseq);
        if (frac < 0.0D)
            frac = 0.0D;
        receiverReport.fractionlost = (int) (frac * 256D);
        this.prevmaxseq = (int) receiverReport.lastseq;
        this.prevlost = receiverReport.packetslost;

        return receiverReport;
    }

    /**
     * Extends a specific 16-bit unsigned sequence number i.e. unaware of
     * cycles/wrapping into a 32-bit signed sequence number i.e. aware of
     * cycles/wrapping.
     *
     * @param seqnum the 16-bit unsigned sequence number to extend
     * @return the 32-bit signed sequence number extended from {@code seqnum}
     */
    public int extendSequenceNumber(int seqnum)
    {
        int cycles = this.cycles;
        int maxseq = this.maxseq;

        int delta = seqnum - maxseq;

        if (delta >= 0)
        {
            // Unless we look at the timestamps associated with seqnum and
            // maxseq, we could presume that seqnum is in the same cycle as
            // maxseq. However, packets i.e. sequence numbers may be
            // retransmitted. Consequently, the positive delta may signify
            // either the same cycle as maxseq or the cycle right before the
            // cycle of maxseq. In the reset of the source code, we
            // differentiate the tow cases by splitting the sequence number
            // space in half.
            if (delta > 0xFFFF / 2)
                cycles -= 0x10000;
        }
        else
        {
            // Unless we look at the timestamps associated with seqnum and
            // maxseq, we could presume that seqnum is in the cycle right
            // after the cycle of maxseq. However, disorder of packets i.e.
            // sequence numbers may be introduced by the (network) transport.
            // Consequently, the negative delta may signify either the same
            // cycle as maxseq or the cycle right after the cycle of maxseq. In
            // the rest of the source code, we differentiate the two cases by
            // splitting the sequence number space in half.
            if (delta < -0xFFFF / 2)
                cycles += 0x10000;
        }

        return seqnum + cycles;
    }
}
