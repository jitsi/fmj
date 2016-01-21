package net.sf.fmj.media.rtp;

import java.util.*;

import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;

public class RecvSSRCInfo extends SSRCInfo implements ReceiveStream,
        SenderReport, ReceiverReport
{
    RecvSSRCInfo(SSRCCache cache, int ssrc)
    {
        super(cache, ssrc);
    }

    RecvSSRCInfo(SSRCInfo info)
    {
        super(info);
    }

    public DataSource getDataSource()
    {
        return super.dsource;
    }

    public long getNTPTimeStampLSW()
    {
        return super.lastSRntptimestamp & 0xffffffffL;
    }

    public long getNTPTimeStampMSW()
    {
        return super.lastSRntptimestamp >> 32 & 0xffffffffL;
    }

    @Override
    public Participant getParticipant()
    {
        SSRCCache cache = getSSRCCache();
        if ((super.sourceInfo instanceof LocalParticipant)
                && cache.sm.IsNonParticipating())
            return null;
        else
            return super.sourceInfo;
    }

    public long getRTPTimeStamp()
    {
        return super.lastSRrtptimestamp;
    }

    public long getSenderByteCount()
    {
        return super.lastSRoctetcount;
    }

    public Feedback getSenderFeedback()
    {
        SSRCCache cache = getSSRCCache();
        Report report = null;
        Vector reports = null;
        Vector feedback = null;
        Feedback reportblk = null;
        try
        {
            LocalParticipant localpartc = cache.sm.getLocalParticipant();
            reports = localpartc.getReports();
            for (int i = 0; i < reports.size(); i++)
            {
                report = (Report) reports.elementAt(i);
                feedback = report.getFeedbackReports();
                for (int j = 0; j < feedback.size(); j++)
                {
                    reportblk = (Feedback) feedback.elementAt(j);
                    long ssrc = reportblk.getSSRC();
                    if (ssrc == getSSRC())
                        return reportblk;
                }

            }

            return null;
        } catch (NullPointerException e)
        {
            return null;
        }
    }

    public long getSenderPacketCount()
    {
        return super.lastSRpacketcount;
    }

    public SenderReport getSenderReport()
    {
        return this;
    }

    public ReceptionStats getSourceReceptionStats()
    {
        return super.stats;
    }

    @Override
    public long getSSRC()
    {
        return super.ssrc;
    }

    public RTPStream getStream()
    {
        return this;
    }
}
