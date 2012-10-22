package net.sf.fmj.media.rtp;

class SynchSource
{
    int ssrc;
    long rtpTimestamp;
    long ntpTimestamp;
    double factor;

    public SynchSource(int ssrc, long rtpTimestamp, long ntpTimestamp)
    {
        this.ssrc = ssrc;
        this.rtpTimestamp = rtpTimestamp;
        this.ntpTimestamp = ntpTimestamp;
        factor = 0.0D;
    }
}
