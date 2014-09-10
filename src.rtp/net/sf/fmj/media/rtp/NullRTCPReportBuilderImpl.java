package net.sf.fmj.media.rtp;

/**
 * Created by gp on 7/1/14.
 */
public class NullRTCPReportBuilderImpl implements RTCPReportBuilder
{
    @Override
    public RTCPPacket[] makeReports()
    {
        return new RTCPPacket[0];
    }

    @Override
    public void reset()
    {
        // Nothing to do here.
    }

    @Override
    public void setRTCPTransmitter(RTCPTransmitter rtcpTransmitter)
    {
        // Nothing to do here.
    }

    @Override
    public RTCPTransmitter getRTCPTransmitter()
    {
        return null;
    }
}
