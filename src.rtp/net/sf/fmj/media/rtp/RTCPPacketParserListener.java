package net.sf.fmj.media.rtp;

/**
 * Created by gp on 6/23/14.
 */
public interface RTCPPacketParserListener
{
    void enterSenderReport();

    void malformedSenderReport();

    void malformedReceiverReport();

    void malformedSourceDescription();

    void malformedEndOfParticipation();

    void uknownPayloadType();

    void visitSendeReport(RTCPSRPacket rtcpSRPacket);
}
