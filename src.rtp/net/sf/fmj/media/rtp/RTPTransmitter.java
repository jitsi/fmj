package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;

import javax.media.*;

import net.sf.fmj.media.rtp.util.*;

public class RTPTransmitter
{
    RTPRawSender sender;
    SSRCCache cache;

    public RTPTransmitter(SSRCCache cache)
    {
        this.cache = cache;
    }

    public RTPTransmitter(SSRCCache cache, int port, String address)
            throws UnknownHostException, IOException
    {
        this(cache, new RTPRawSender(port, address));
    }

    public RTPTransmitter(SSRCCache cache, int port, String address,
            UDPPacketSender sender) throws UnknownHostException, IOException
    {
        this(cache, new RTPRawSender(port, address, sender));
    }

    public RTPTransmitter(SSRCCache cache, RTPRawSender sender)
    {
        this(cache);
        setSender(sender);
    }

    public void close()
    {
        if (sender != null)
            sender.closeConsumer();
    }

    public RTPRawSender getSender()
    {
        return sender;
    }

    protected RTPPacket MakeRTPPacket(Buffer b, SendSSRCInfo info)
    {
        byte data[] = (byte[]) b.getData();
        if (data == null)
            return null;
        Packet p = new Packet();
        p.data = data;
        p.offset = 0;
        p.length = b.getLength();
        p.received = false;
        RTPPacket rtp = new RTPPacket(p);
        if ((b.getFlags() & Buffer.FLAG_RTP_MARKER) != 0)
            rtp.marker = 1;
        else
            rtp.marker = 0;
        info.packetsize += b.getLength();
        rtp.payloadType = ((SSRCInfo) (info)).payloadType;
        rtp.seqnum = (int) info.getSequenceNumber(b);
        rtp.timestamp = ((SSRCInfo) (info)).rtptime;
        rtp.ssrc = ((SSRCInfo) (info)).ssrc;
        rtp.payloadoffset = b.getOffset();
        rtp.payloadlength = b.getLength();
        info.bytesreceived += b.getLength();
        info.maxseq++;
        info.lasttimestamp = rtp.timestamp;

        Buffer.RTPHeaderExtension headerExtension = b.getHeaderExtension();
        if (headerExtension != null)
        {
            rtp.headerExtension = headerExtension;
        }
        return rtp;
    }

    public void setSender(RTPRawSender s)
    {
        sender = s;
    }

    protected void transmit(RTPPacket p)
    {
        try
        {
            sender.sendTo(p);
        } catch (IOException e)
        {
            cache.sm.transstats.transmit_failed++;
        }
    }

    public void TransmitPacket(Buffer b, SendSSRCInfo info)
    {
        info.rtptime = info.getTimeStamp(b);

        Object header = b.getHeader();
        if (header != null && header instanceof Long)
            info.systime = (Long) header;
        else
            info.systime = System.currentTimeMillis();

        RTPPacket p = MakeRTPPacket(b, info);
        if (p == null)
        {
            return;
        } else
        {
            transmit(p);
            info.stats.total_pdu++;
            info.stats.total_bytes = info.stats.total_bytes + b.getLength();
            cache.sm.transstats.rtp_sent++;
            cache.sm.transstats.bytes_sent = cache.sm.transstats.bytes_sent
                    + b.getLength();
            return;
        }
    }
}
