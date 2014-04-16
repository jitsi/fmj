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
    RTPSourceInfo sourceInfo;
    SourceDescription name;
    SourceDescription email;
    SourceDescription phone;
    SourceDescription loc;
    SourceDescription tool;
    SourceDescription note;
    SourceDescription priv;
    long lastSRntptimestamp;
    long lastSRrtptimestamp;
    long lastSRoctetcount;
    long lastSRpacketcount;
    long lastRTCPreceiptTime;
    long lastSRreceiptTime;
    long lastHeardFrom;
    boolean quiet;
    boolean inactivesent;
    boolean aging;
    boolean sender;
    boolean ours;
    int ssrc;
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
    int prevmaxseq;
    int prevlost;
    long starttime;
    long rtptime;
    long systime;
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
    int maxseq;
    int cycles;
    int baseseq;
    int lastbadseq;
    int received;
    long lasttimestamp;
    int lastPayloadType;
    double jitter;
    int bytesreceived;
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
        lastPayloadType = -1;
        jitter = 0.0D;
        stats = null;
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
        lastPayloadType = -1;
        jitter = 0.0D;
        stats = null;
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
        if (reportlist.size() == 0)
            return reportlist;
        Enumeration<RTCPReportBlock[]> reportblks = reports.elements();
        try
        {
            while (reportblks.hasMoreElements())
            {
                RTCPReportBlock[] reportblklist = reportblks.nextElement();
                RTCPReportBlock report = new RTCPReportBlock();
                report = reportblklist[0];
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
        if (aging == beaging)
        {
            return;
        } else
        {
            aging = beaging;
            return;
        }
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

    void setOurs(boolean beours)
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
        for (int i = 0; i < userdesclist.length; i++)
        {
            SourceDescription currdesc = userdesclist[i];
            if (currdesc == null || currdesc.getType() != 1)
                continue;
            cname = userdesclist[i].getDescription();
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
        for (int j = 0; j < userdesclist.length; j++)
        {
            SourceDescription currdesc = userdesclist[j];
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
}
