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
    boolean inited;
    private static final int PACKET_SIZE = 4000;
    protected int packetsize;
    protected Format myformat;
    protected long totalSamples;
    protected long lastSeq;
    protected long lastBufSeq;
    protected RTPTransStats stats;
    protected RTCPReporter rtcprep;
    static AudioFormat dviAudio = new AudioFormat("dvi/rtp");
    static AudioFormat gsmAudio = new AudioFormat("gsm/rtp");
    static AudioFormat g723Audio = new AudioFormat("g723/rtp");
    static AudioFormat ulawAudio = new AudioFormat("ULAW/rtp");
    static AudioFormat mpegAudio = new AudioFormat("mpegaudio/rtp");
    static VideoFormat mpegVideo = new VideoFormat("mpeg/rtp");

    public SendSSRCInfo(SSRCCache cache, int ssrc)
    {
        super(cache, ssrc);
        inited = false;
        packetsize = 0;
        myformat = null;
        totalSamples = 0L;
        lastSeq = -1L;
        lastBufSeq = -1L;
        stats = null;
        rtcprep = null;
        super.baseseq = TrueRandom.nextInt();
        super.maxseq = super.baseseq;
        super.lasttimestamp = TrueRandom.nextLong();
        super.sender = true;
        super.wassender = true;
        super.sinkstream = new RTPSinkStream();
        stats = new RTPTransStats();
    }

    public SendSSRCInfo(SSRCInfo info)
    {
        super(info);
        inited = false;
        packetsize = 0;
        myformat = null;
        totalSamples = 0L;
        lastSeq = -1L;
        lastBufSeq = -1L;
        stats = null;
        rtcprep = null;
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

    protected void createDS()
    {
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
        if (b.getFormat() instanceof AudioFormat)
            if (mpegAudio.matches(b.getFormat()))
            {
                if (b.getTimeStamp() >= 0L)
                    return (b.getTimeStamp() * 90L) / 0xf4240L;
                else
                    return System.currentTimeMillis() * 90L;
            } else
            {
                totalSamples += calculateSampleCount(b);
                return totalSamples;
            }
        if (b.getFormat() instanceof VideoFormat)
        {
            if (b.getTimeStamp() >= 0L)
                return (b.getTimeStamp() * 90L) / 0xf4240L;
            else
                return System.currentTimeMillis() * 90L;
        } else
        {
            return b.getTimeStamp();
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
