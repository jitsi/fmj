package net.sf.fmj.media.rtp;

import javax.media.rtp.*;

public class OverallTransStats implements GlobalTransmissionStats
{
    protected int rtp_sent;
    protected int bytes_sent;

    /**
     * Updated by the <tt>RTCPTransmitter</tt> when an RTCP packet is sent.
     */
    public int rtcp_sent;
    protected int local_coll;
    protected int remote_coll;

    /**
     * Updated by the <tt>RTPTransmitter</tt> or the <tt>RTCPTransmitter</tt>
     * when an RTP or an RTCP packet fails to transmit.
     */
    public int transmit_failed;

    public OverallTransStats()
    {
        rtp_sent = 0;
        bytes_sent = 0;
        rtcp_sent = 0;
        local_coll = 0;
        remote_coll = 0;
        transmit_failed = 0;
    }

    public int getBytesSent()
    {
        return bytes_sent;
    }

    public int getLocalColls()
    {
        return local_coll;
    }

    public int getRemoteColls()
    {
        return remote_coll;
    }

    public int getRTCPSent()
    {
        return rtcp_sent;
    }

    public int getRTPSent()
    {
        return rtp_sent;
    }

    public int getTransmitFailed()
    {
        return transmit_failed;
    }
}
