package net.sf.fmj.media.rtp;

import javax.media.rtp.rtcp.*;

public class RTCPReportBlock implements Feedback
{
    public static String toString(RTCPReportBlock reports[])
    {
        String s = "";
        for (int i = 0; i < reports.length; i++)
            s = s + reports[i];

        return s;
    }

    int ssrc;
    int fractionlost;
    int packetslost;
    long lastseq;
    int jitter;
    long lsr;
    long dlsr;

    public long receiptTime;

    public RTCPReportBlock(int ssrc,
            int fractionlost,
            int packetslost,
            long lastseq,
            int jitter,
            long lsr,
            long dlsr)
    {
        this.ssrc = ssrc;
        this.fractionlost = fractionlost;
        this.packetslost = packetslost;
        this.lastseq = lastseq;
        this.jitter = jitter;
        this.lsr = lsr;
        this.dlsr = dlsr;
    }

    public RTCPReportBlock()
    {
    }

    public long getDLSR()
    {
        return dlsr;
    }

    public int getFractionLost()
    {
        return fractionlost;
    }

    public long getJitter()
    {
        return jitter;
    }

    public long getLSR()
    {
        return lsr;
    }

    public long getNumLost()
    {
        return packetslost;
    }

    public long getSSRC()
    {
        return ssrc;
    }

    public long getXtndSeqNum()
    {
        return lastseq;
    }

    @Override
    public String toString()
    {
        long printssrc = 0xFFFFFFFFL & ssrc;

        return "\t\tFor source " + printssrc
                + "\n\t\t\tFraction of packets lost: " + fractionlost + " ("
                + fractionlost / 256D + ")" + "\n\t\t\tPackets lost: "
                + packetslost + "\n\t\t\tLast sequence number: " + lastseq
                + "\n\t\t\tJitter: " + jitter
                + "\n\t\t\tLast SR packet received at time " + lsr
                + "\n\t\t\tDelay since last SR packet received: " + dlsr + " ("
                + dlsr / 65536D + " seconds)\n";
    }
}
