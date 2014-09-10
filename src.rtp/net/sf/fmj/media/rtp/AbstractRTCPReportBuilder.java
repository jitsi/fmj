package net.sf.fmj.media.rtp;

/**
 *
 * @author George Politis
 * @author Lyubomir Marinov
 */
public abstract class AbstractRTCPReportBuilder
    implements RTCPReportBuilder
{
    private RTCPTransmitter rtcpTransmitter;

    @Override
    public RTCPTransmitter getRTCPTransmitter()
    {
        return rtcpTransmitter;
    }

    @Override
    public RTCPPacket[] makeReports()
    {
        RTCPTransmitter rtcpTransmitter = getRTCPTransmitter();

        if (rtcpTransmitter == null)
            throw new IllegalStateException("rtcpTransmitter is not set");
        else
            return makeReports(rtcpTransmitter);
    }

    protected abstract RTCPPacket[] makeReports(
            RTCPTransmitter rtcpTransmitter);

    @Override
    public void reset()
    {
    }

    @Override
    public void setRTCPTransmitter(RTCPTransmitter rtcpTransmitter)
    {
        this.rtcpTransmitter = rtcpTransmitter;
    }
}
