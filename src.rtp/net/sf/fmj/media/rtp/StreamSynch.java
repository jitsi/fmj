package net.sf.fmj.media.rtp;

import net.sf.fmj.media.rtp.util.*;

public class StreamSynch
{
    private static SSRCTable sources;

    public StreamSynch()
    {
        if (sources == null)
            sources = new SSRCTable();
    }

    public long calcTimestamp(int ssrc, int pt, long rtpTimestamp)
    {
        long timestamp = -1L;
        SynchSource source = (SynchSource) sources.get(ssrc);
        if (source != null)
        {
            long rate = 1L;
            if (pt >= 0 && pt <= 5)
                rate = 8000L;
            else if (pt == 5)
                rate = 8000L;
            else if (pt == 6)
                rate = 16000L;
            else if (pt >= 7 && pt <= 9)
                rate = 8000L;
            else if (pt >= 10 && pt <= 11)
                rate = 44100L;
            else if (pt == 14)
                rate = 0x15f90L;
            else if (pt == 15)
                rate = 8000L;
            else if (pt == 16)
                rate = 11025L;
            else if (pt == 17)
                rate = 22050L;
            else if (pt >= 25 && pt <= 26)
                rate = 0x15f90L;
            else if (pt == 28)
                rate = 0x15f90L;
            else if (pt >= 31 && pt <= 34)
                rate = 0x15f90L;
            else if (pt == 42)
                rate = 0x15f90L;
            timestamp = source.ntpTimestamp
                    + ((rtpTimestamp - source.rtpTimestamp) * 0x3b9aca00L)
                    / rate;
        }
        return timestamp;
    }

    public void remove(int ssrc)
    {
        if (sources != null)
            sources.remove(ssrc);
    }

    public void update(int ssrc, long rtpTimestamp, long ntpTimestampMSW,
            long ntpTimestampLSW)
    {
        double fraction = ntpTimestampLSW / 4294967296D;
        long ntpTimestamp = ntpTimestampMSW * 0x3b9aca00L
                + (long) (fraction * 1000000000D);
        SynchSource source = (SynchSource) sources.get(ssrc);
        if (source == null)
        {
            sources.put(ssrc, new SynchSource(ssrc, rtpTimestamp, ntpTimestamp));
        } else
        {
            source.factor = (rtpTimestamp - source.rtpTimestamp)
                    * (ntpTimestamp - source.ntpTimestamp);
            source.rtpTimestamp = rtpTimestamp;
            source.ntpTimestamp = ntpTimestamp;
        }
    }
}
