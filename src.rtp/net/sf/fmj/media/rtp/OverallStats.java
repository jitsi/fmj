package net.sf.fmj.media.rtp;

import javax.media.rtp.*;

public class OverallStats implements GlobalReceptionStats
{
    public static final int PACKETRECD = 0;
    public static final int BYTESRECD = 1;
    public static final int BADRTPPACKET = 2;
    public static final int LOCALCOLL = 3;
    public static final int REMOTECOLL = 4;
    public static final int PACKETSLOOPED = 5;
    public static final int TRANSMITFAILED = 6;
    public static final int RTCPRECD = 11;
    public static final int SRRECD = 12;
    public static final int BADRTCPPACKET = 13;
    public static final int UNKNOWNTYPE = 14;
    public static final int MALFORMEDRR = 15;
    public static final int MALFORMEDSDES = 16;
    public static final int MALFORMEDBYE = 17;
    public static final int MALFORMEDSR = 18;
    private int numPackets;
    private int numBytes;
    private int numBadRTPPkts;
    private int numLocalColl;
    private int numRemoteColl;
    private int numPktsLooped;
    private int numTransmitFailed;
    private int numRTCPRecd;
    private int numSRRecd;
    private int numBadRTCPPkts;
    private int numUnknownTypes;
    private int numMalformedRR;
    private int numMalformedSDES;
    private int numMalformedBye;
    private int numMalformedSR;

    public OverallStats()
    {
        numPackets = 0;
        numBytes = 0;
        numBadRTPPkts = 0;
        numLocalColl = 0;
        numRemoteColl = 0;
        numPktsLooped = 0;
        numTransmitFailed = 0;
        numRTCPRecd = 0;
        numSRRecd = 0;
        numBadRTCPPkts = 0;
        numUnknownTypes = 0;
        numMalformedRR = 0;
        numMalformedSDES = 0;
        numMalformedBye = 0;
        numMalformedSR = 0;
    }

    public int getBadRTCPPkts()
    {
        return numBadRTCPPkts;
    }

    public int getBadRTPkts()
    {
        return numBadRTPPkts;
    }

    public int getBytesRecd()
    {
        return numBytes;
    }

    public int getLocalColls()
    {
        return numLocalColl;
    }

    public int getMalformedBye()
    {
        return numMalformedBye;
    }

    public int getMalformedRR()
    {
        return numMalformedRR;
    }

    public int getMalformedSDES()
    {
        return numMalformedSDES;
    }

    public int getMalformedSR()
    {
        return numMalformedSR;
    }

    public int getPacketsLooped()
    {
        return numPktsLooped;
    }

    public int getPacketsRecd()
    {
        return numPackets;
    }

    public int getRemoteColls()
    {
        return numRemoteColl;
    }

    public int getRTCPRecd()
    {
        return numRTCPRecd;
    }

    public int getSRRecd()
    {
        return numSRRecd;
    }

    public int getTransmitFailed()
    {
        return numTransmitFailed;
    }

    public int getUnknownTypes()
    {
        return numUnknownTypes;
    }

    @Override
    public String toString()
    {
        String s = "Packets Recd " + getPacketsRecd() + "\nBytes Recd "
                + getBytesRecd() + "\ngetBadRTP " + getBadRTPkts()
                + "\nLocalColl " + getLocalColls() + "\nRemoteColl "
                + getRemoteColls() + "\nPacketsLooped " + getPacketsLooped()
                + "\ngetTransmitFailed " + getTransmitFailed() + "\nRTCPRecd "
                + getTransmitFailed() + "\nSRRecd " + getSRRecd()
                + "\nBadRTCPPkts " + getBadRTCPPkts() + "\nUnknown "
                + getUnknownTypes() + "\nMalformedRR " + getMalformedRR()
                + "\nMalformedSDES " + getMalformedSDES() + "\nMalformedBye "
                + getMalformedBye() + "\nMalformedSR " + getMalformedSR();
        return s;
    }

    public synchronized void update(int which, int num)
    {
        switch (which)
        {
        case PACKETRECD: // '\0'
            numPackets += num;
            break;

        case BYTESRECD: // '\001'
            numBytes += num;
            break;

        case BADRTPPACKET: // '\002'
            numBadRTPPkts += num;
            break;

        case LOCALCOLL: // '\003'
            numLocalColl += num;
            break;

        case REMOTECOLL: // '\004'
            numRemoteColl += num;
            break;

        case PACKETSLOOPED: // '\005'
            numPktsLooped += num;
            break;

        case TRANSMITFAILED: // '\006'
            numTransmitFailed += num;
            break;

        case RTCPRECD: // '\013'
            numRTCPRecd += num;
            break;

        case SRRECD: // '\f'
            numSRRecd += num;
            break;

        case BADRTCPPACKET: // '\r'
            numBadRTPPkts += num;
            break;

        case UNKNOWNTYPE: // '\016'
            numUnknownTypes += num;
            break;

        case MALFORMEDRR: // '\017'
            numMalformedRR += num;
            break;

        case MALFORMEDSDES: // '\020'
            numMalformedSDES += num;
            break;

        case MALFORMEDBYE: // '\021'
            numMalformedBye += num;
            break;

        case MALFORMEDSR: // '\022'
            numMalformedSR += num;
            break;
        }
    }
}
