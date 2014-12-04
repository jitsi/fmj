package net.sf.fmj.media.rtp;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;

public class SendSSRCInfo extends SSRCInfo implements SenderReport, SendStream
{
    private boolean inited = false;
    protected int packetsize = 0;
    protected Format myformat;
    private long totalSamples = 0;
    private long lastSeq = -1;
    private long lastBufSeq = -1;
    protected RTPTransStats stats;
    static AudioFormat dviAudio = new AudioFormat("dvi/rtp");
    static AudioFormat gsmAudio = new AudioFormat("gsm/rtp");
    static AudioFormat g723Audio = new AudioFormat("g723/rtp");
    static AudioFormat ulawAudio = new AudioFormat("ULAW/rtp");
    static AudioFormat mpegAudio = new AudioFormat("mpegaudio/rtp");
    static VideoFormat mpegVideo = new VideoFormat("mpeg/rtp");

    public SendSSRCInfo(SSRCCache cache, int ssrc)
    {
        super(cache, ssrc);
        init();
    }

    public SendSSRCInfo(SSRCInfo info)
    {
        super(info);
        init();
    }

    private void init()
    {
        super.baseseq = TrueRandom.nextInt();
        super.maxseq = super.baseseq;
        super.lasttimestamp = TrueRandom.nextLong();
        super.sender = true;
        super.wassender = true;
        super.sinkstream = new RTPSinkStream();
        stats = new RTPTransStats();
    }

    private int calculateSampleCount(Buffer b)
    {
        AudioFormat f = (AudioFormat) b.getFormat();
        if (f == null)
            return -1;
        long t = f.computeDuration(b.getLength());
        if (t == -1L)
            return -1;
        if (f.getSampleRate() != -1D)
            return (int) ((t * f.getSampleRate()) / 1000000000D);
        if (f.getFrameRate() != -1D)
            return (int) ((t * f.getFrameRate()) / 1000000000D);
        else
            return -1;
    }

    public void close()
    {
        try
        {
            stop();
        } catch (IOException e)
        {
        }
        SSRCCache cache = getSSRCCache();
        cache.sm.removeSendStream(this);
    }

    public DataSource getDataSource()
    {
        return super.pds;
    }

    public long getNTPTimeStampLSW()
    {
        return super.lastSRntptimestamp;
    }

    public long getNTPTimeStampMSW()
    {
        return super.lastSRntptimestamp >> 32;
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
            Participant localpartc = cache.sm.getLocalParticipant();
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
        SSRCCache cache = getSSRCCache();
        Report report = null;
        Vector reports = null;
        Vector feedback = null;
        Feedback reportblk = null;
        try
        {
            Participant localpartc = cache.sm.getLocalParticipant();
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
                        return (SenderReport) report;
                }

            }

            return null;
        } catch (NullPointerException e)
        {
            return null;
        }
    }

    public long getSequenceNumber(Buffer b)
    {
        long seq = b.getSequenceNumber();
        if (lastSeq == -1L)
        {
            lastSeq = (long) (System.currentTimeMillis() * Math.random());
            lastBufSeq = seq;
            return lastSeq;
        }
        if (seq - lastBufSeq > 1L)
            lastSeq += seq - lastBufSeq;
        else
            lastSeq++;
        lastBufSeq = seq;
        return lastSeq;
    }

    public TransmissionStats getSourceTransmissionStats()
    {
        return stats;
    }

    public RTPStream getStream()
    {
        return this;
    }

    public long getTimeStamp(Buffer b)
    {
        long bTimestamp = b.getTimeStamp();
        Format bFormat = b.getFormat();

        if ((b.getFlags() & Buffer.FLAG_RTP_TIME) != 0)
        {
            if (bTimestamp != -1)
                return bTimestamp;
        }

        if (bFormat instanceof AudioFormat)
            if (mpegAudio.matches(bFormat))
            {
                if (bTimestamp >= 0L)
                    return (bTimestamp * 90L) / (1000 * 1000L);
                else
                    return System.currentTimeMillis() * 90L;
            } else
            {
                totalSamples += calculateSampleCount(b);
                return totalSamples;
            }
        if (bFormat instanceof VideoFormat)
        {
            if (bTimestamp >= 0L)
                return (bTimestamp * 90L) / (1000 * 1000L);
            else
                return System.currentTimeMillis() * 90L;
        } else
        {
            return bTimestamp;
        }
    }

    public int setBitRate(int rate)
    {
        if (super.sinkstream != null)
            super.sinkstream.rate = rate;
        return rate;
    }

    protected void setFormat(Format fmt)
    {
        myformat = fmt;
        if (super.sinkstream != null)
        {
            int rate = 0;
            if (fmt instanceof AudioFormat)
            {
                if (ulawAudio.matches(fmt) || dviAudio.matches(fmt)
                        || mpegAudio.matches(fmt))
                    rate = (int) ((AudioFormat) fmt).getSampleRate()
                            * ((AudioFormat) fmt).getSampleSizeInBits();
                else if (gsmAudio.matches(fmt))
                    rate = 13200;
                else if (g723Audio.matches(fmt))
                    rate = 6300;
                super.sinkstream.rate = rate;
            }
        } else
        {
            System.err.println("RTPSinkStream is NULL");
        }
    }

    @Override
    public void setSourceDescription(SourceDescription userdesclist[])
    {
        super.setSourceDescription(userdesclist);
    }

    public void start() throws IOException
    {
        if (!inited)
        {
            inited = true;
            super.probation = 0;
            initsource(TrueRandom.nextInt());
            super.lasttimestamp = TrueRandom.nextLong();
        }
        if (super.pds != null)
            super.pds.start();
        if (super.sinkstream != null)
            super.sinkstream.start();
    }

    public void stop() throws IOException
    {
        if (super.pds != null)
            super.pds.stop();
        if (super.sinkstream != null)
            super.sinkstream.stop();
    }
}
