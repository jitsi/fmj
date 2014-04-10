package net.sf.fmj.media.rtp;

import javax.media.*;

import net.sf.fmj.media.rtp.util.*;

public class RTPDemultiplexer
{
    private SSRCCache cache;
    private RTPRawReceiver rtpr;
    private Buffer buffer;
    private StreamSynch streamSynch;

    public RTPDemultiplexer(SSRCCache c, RTPRawReceiver r,
            StreamSynch streamSynch)
    {
        cache = c;
        rtpr = r;
        this.streamSynch = streamSynch;
        buffer = new Buffer();
    }

    public String consumerString()
    {
        return "RTP DeMultiplexer";
    }

    public void demuxpayload(SourceRTPPacket sp)
    {
        SSRCInfo info = sp.ssrcinfo;
        RTPPacket rtpPacket = sp.p;
        info.payloadType = rtpPacket.payloadType;
        if (info.dstream != null)
        {
            buffer.setData(rtpPacket.base.data);
            buffer.setFlags(rtpPacket.flags);
            if (rtpPacket.marker == 1)
                buffer.setFlags(buffer.getFlags() | Buffer.FLAG_RTP_MARKER);
            buffer.setLength(rtpPacket.payloadlength);
            buffer.setOffset(rtpPacket.payloadoffset);

            long ts = streamSynch.calcTimestamp(info.ssrc, rtpPacket.payloadType,
                    rtpPacket.timestamp);
            buffer.setTimeStamp(ts);
            buffer.setRtpTimeStamp(rtpPacket.timestamp);

            buffer.setFlags(buffer.getFlags() | Buffer.FLAG_RTP_TIME);
            buffer.setSequenceNumber(rtpPacket.seqnum);
            buffer.setFormat(info.dstream.getFormat());
            info.dstream.add(buffer, info.wrapped, rtpr);
        }
    }
}
