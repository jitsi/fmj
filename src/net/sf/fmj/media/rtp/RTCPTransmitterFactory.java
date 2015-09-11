package net.sf.fmj.media.rtp;

/**
 * Created by gpolitis on 8/25/15.
 */
public interface RTCPTransmitterFactory
{
    /**
     *
     * @return
     */
    RTCPTransmitter newRTCPTransmitter(SSRCCache cache, RTCPRawSender rtcprawsender);
}
