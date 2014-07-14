package net.sf.fmj.media.rtp;

import java.util.*;

import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;

public class RecvSSRCInfo extends SSRCInfo implements ReceiveStream,
        SenderReport
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

    /**
     * Gets the round trip (delay) time between RTP interfaces, expressed in
     * milliseconds. The reported delay value is the time of receipt of the most
     * recent RTCP packet from the (remote) source, minus the LSR (last SR) time
     * reported in its SR (Sender Report), minus the DLSR (delay since last SR)
     * reported in its SR. A non-zero LSR value is required in order to
     * calculate round trip delay.
     *
     * @param sourceSSRC
     * @return the round trip (delay) time between RTP interfaces, expressed in
     * milliseconds. The returned value represents a 16-bit unsigned integer.
     */
    public int getRoundTripDelay(int sourceSSRC)
    {
        Feedback feedback = null;

        for (Feedback report : getFeedbackReports())
        {
            if (sourceSSRC == (int) report.getSSRC())
            {
                feedback = report;
                break;
            }
        }

        int roundTripDelay = 0;

        if (feedback != null)
        {
            long lastRTCPreceiptTime = this.lastRTCPreceiptTime;

            if (lastRTCPreceiptTime != 0)
            {
                long lsr = feedback.getLSR();
                long dlsr = feedback.getDLSR();

                roundTripDelay
                    = getRoundTripDelay(lastRTCPreceiptTime, lsr, dlsr);
            }
        }
        return roundTripDelay;
    }

    /**
     * Gets the round trip (delay) time between RTP interfaces, expressed in
     * milliseconds.
     *
     * @param systime the <tt>System</tt> time in milliseconds
     * @param lsr the LSR (last SR) time reported in SR (Sender Report)
     * @param dlsr the DLSR (delay since last SR) reported in SR
     * @return the round trip (delay) time between RTP interfaces, expressed in
     * milliseconds. The returned value represents a 16-bit unsigned integer.
     */
    public static int getRoundTripDelay(long systime, long lsr, long dlsr)
    {
        int roundTripDelay = 0;

        if (lsr > 0)
        {
            long secs = systime / 1000L;
            double msecs = (systime - secs * 1000L) / 1000D;
            long lsw = (int) (msecs * 4294967296D);
            long msw = secs;
            long ntptime = (msw << 32) + lsw;

            ntptime = (ntptime & 0x0000FFFFFFFF0000L) >> 16;

            long ntprtd = ntptime - lsr - dlsr;

            if (ntprtd > 0)
            {
                if (ntprtd > 4294967L)
                    roundTripDelay = 65536;
                else
                    roundTripDelay = (int) ((ntprtd * 1000L) >> 16);
            }
        }
        return roundTripDelay;
    }
}
