package net.sf.fmj.media.rtp;

/**
 * Created by gp on 6/12/14.
 */
public interface RTCPReportBuilder
{
    /**
     * Makes the RTCP reports to be sent by an <tt>RTCPTransmitter</tt> 
     * (typically the <tt>RTCPTransmitter</tt> associated with this 
     * <tt>RTCPReportBuilder</tt> instance).
     *
     * @return
     */
    RTCPPacket[] makeReports();

    /**
     * Resets this builder's internal state. This must be called after a BYE
     * packet.
     */
    void reset();

    /**
     * Sets the associated <tt>RTCPTransmitter</tt> that this 
     * <tt>RTCPReportBuilder</tt> takes statistics from.
     */
    void setRTCPTransmitter(RTCPTransmitter rtcpTransmitter);

    /**
     * Gets the associated <tt>RTCPTransmitter</tt> that this
     * <tt>RTCPReportBuilder</tt> takes statistics from.
     */
    RTCPTransmitter getRTCPTransmitter();
}
