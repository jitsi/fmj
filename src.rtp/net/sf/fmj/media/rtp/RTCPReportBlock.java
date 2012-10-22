package net.sf.fmj.media.rtp;

import javax.media.rtp.rtcp.*;

import net.sf.fmj.media.rtp.util.*;

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

    long receiptTime;

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
        long printssrc = ssrc;
        if (ssrc < 0)
            printssrc = Signed.UnsignedInt(ssrc);
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
