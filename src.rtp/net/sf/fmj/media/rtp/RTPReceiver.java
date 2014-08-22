package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;

import javax.media.rtp.*;
import javax.media.rtp.event.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.protocol.rtp.*;
import net.sf.fmj.media.rtp.util.*;

/**
 *
 * @author Damian Minkov
 * @author Boris Grozev
 * @author Lyubomir Marinov
 */
public class RTPReceiver extends PacketFilter
{
    final SSRCCache cache;

    final RTPDemultiplexer rtpdemultiplexer;

    int lastseqnum;

    private boolean rtcpstarted;

    private final String content;

    final SSRCTable<RTPPacket> probationList;

    static final int MAX_DROPOUT = 3000;

    static final int MAX_MISORDER = 100;

    static final int SEQ_MOD = 0x10000;
    static final int MIN_SEQUENTIAL = 2;

    //BufferControl initialized
    private boolean initBC = false;
    public final String controlstr;
    private int errorPayload;

    public RTPReceiver(SSRCCache ssrccache, RTPDemultiplexer rtpdemultiplexer1)
    {
        lastseqnum = -1;
        rtcpstarted = false;
        content = "";
        probationList = new SSRCTable<RTPPacket>();
        controlstr = "javax.media.rtp.RTPControl";
        errorPayload = -1;
        cache = ssrccache;
        rtpdemultiplexer = rtpdemultiplexer1;
        setConsumer(null);
    }

    public RTPReceiver(SSRCCache ssrccache, RTPDemultiplexer rtpdemultiplexer1,
            DatagramSocket datagramsocket)
    {
        this(ssrccache, rtpdemultiplexer1, (new RTPRawReceiver(datagramsocket,
                ssrccache.sm.defaultstats)));
    }

    public RTPReceiver(SSRCCache ssrccache, RTPDemultiplexer rtpdemultiplexer1,
            int i, String s) throws UnknownHostException, IOException
    {
        this(ssrccache, rtpdemultiplexer1, (new RTPRawReceiver(i & -2, s,
                ssrccache.sm.defaultstats)));
    }

    public RTPReceiver(SSRCCache ssrccache, RTPDemultiplexer rtpdemultiplexer1,
            PacketSource packetsource)
    {
        this(ssrccache, rtpdemultiplexer1);
        setSource(packetsource);
    }

    @Override
    public String filtername()
    {
        return "RTP Packet Receiver";
    }

    @Override
    public Packet handlePacket(Packet packet)
    {
        return handlePacket((RTPPacket) packet);
    }

    @Override
    public Packet handlePacket(Packet packet, int i)
    {
        return null;
    }

    @Override
    public Packet handlePacket(Packet packet, SessionAddress sessionaddress)
    {
        return null;
    }

    public Packet handlePacket(Packet packet, SessionAddress sessionaddress,
            boolean flag)
    {
        return null;
    }

    public Packet handlePacket(RTPPacket rtppacket)
    {
        /* damencho: Do not process silence packets and buggy X-Lite. */
        if (rtppacket.payloadType == 13)
            return rtppacket;
        if (rtppacket.payloadType == 126)
            return null;

        if (rtppacket.base instanceof UDPPacket)
        {
            InetAddress inetaddress = ((UDPPacket) rtppacket.base).remoteAddress;
            if (cache.sm.bindtome
                    && !cache.sm.isBroadcast(cache.sm.dataaddress)
                    && !inetaddress.equals(cache.sm.dataaddress))
            {
                return null;
            }
        }
        //else if (rtppacket.base instanceof Packet)
        //    rtppacket.base.toString();

        SSRCInfo ssrcinfo;
        {
            InetAddress address;
            int port;
            if (rtppacket.base instanceof UDPPacket)
            {
                UDPPacket udppacket = (UDPPacket) rtppacket.base;
                address = udppacket.remoteAddress;
                port = udppacket.remotePort;
            }
            else
            {
                address = null;
                port = 0;
            }
            ssrcinfo = cache.get(rtppacket.ssrc, address, port, 1);
        }
        if (ssrcinfo == null)
            return null;

        //update lastHeardFrom fields in the cache for csrc's
        for (int i = 0; i < rtppacket.csrc.length; i++)
        {
            InetAddress address;
            int port;
            SSRCInfo ssrcinfo1;
            if (rtppacket.base instanceof UDPPacket)
            {
                UDPPacket udppacket = (UDPPacket) rtppacket.base;
                address = udppacket.remoteAddress;
                port = udppacket.remotePort;
            }
            else
            {
                address = null;
                port = 0;
            }
            ssrcinfo1 = cache.get(rtppacket.csrc[i], address, port, 1);
            if (ssrcinfo1 != null)
                ssrcinfo1.lastHeardFrom = ((Packet) (rtppacket)).receiptTime;
        }

        if (!ssrcinfo.sender)
        {
            ssrcinfo.initsource(rtppacket.seqnum);
            ssrcinfo.payloadType = rtppacket.payloadType;
        }
        int diff = rtppacket.seqnum - ssrcinfo.maxseq;
        if (diff > 0)
        {
            if (ssrcinfo.maxseq + 1 != rtppacket.seqnum)
                ssrcinfo.stats.update(RTPStats.PDULOST, diff - 1);
        }
        else if (diff < 0)
        {
            // Packets arriving out of order have already been counted as lost
            // (by the clause above), so decrease the lost count.
            if (diff > -MAX_MISORDER)
                ssrcinfo.stats.update(RTPStats.PDULOST, -1);
        }
        if (ssrcinfo.wrapped)
            ssrcinfo.wrapped = false;
        boolean flag = false;
        if (ssrcinfo.probation > 0)
        {
            if (rtppacket.seqnum == ssrcinfo.maxseq + 1)
            {
                ssrcinfo.probation--;
                ssrcinfo.maxseq = rtppacket.seqnum;
                if (ssrcinfo.probation == 0)
                    flag = true;
            } else
            {
                ssrcinfo.probation = 1;
                ssrcinfo.maxseq = rtppacket.seqnum;
                ssrcinfo.stats.update(RTPStats.PDUMISORD);
            }
        } else if ((diff < MAX_DROPOUT) && (diff != 0))
        {
            if (rtppacket.seqnum < ssrcinfo.baseseq)
            {
                /*
                 * Vincent Lucas: Without any lost, the seqnum cycles when
                 * passing from 65535 to 0. Thus, diff is equal to -65535. But
                 * if there have been losses, diff may be -65534, -65533, etc.
                 * On the other hand, if diff is too close to 0 (i.e. -1, -2,
                 * etc.), it may correspond to a packet out of sequence. This is
                 * why it is a sound choice to differentiate between a cycle and
                 * an out-of-sequence on the basis of a value in between the two
                 * cases i.e. -65535 / 2.
                 */
                if (diff < -65535 / 2)
                {
                    ssrcinfo.cycles += 0x10000;
                    ssrcinfo.wrapped = true;
                }
            }
            ssrcinfo.maxseq = rtppacket.seqnum;
        } else if ((diff <= 65536 - MAX_MISORDER) && (diff != 0))
        {
            ssrcinfo.stats.update(RTPStats.PDUINVALID);
            if (rtppacket.seqnum == ssrcinfo.lastbadseq)
                ssrcinfo.initsource(rtppacket.seqnum);
            else
                ssrcinfo.lastbadseq = rtppacket.seqnum + 1 & 0xffff;
        } else
        {
            ssrcinfo.stats.update(RTPStats.PDUDUP);
        }
        if (cache.sm.isUnicast())
        {
            InetAddress remoteAddress
                = ((UDPPacket) rtppacket.base).remoteAddress;
            if (!rtcpstarted)
            {
                cache.sm.startRTCPReports(remoteAddress);
                rtcpstarted = true;
                byte abyte0[] = cache.sm.controladdress.getAddress();
                int k = abyte0[3] & 0xff;
                if ((k & 0xff) == 255)
                {
                    cache.sm.addUnicastAddr(cache.sm.controladdress);
                } else
                {
                    InetAddress inetaddress1 = null;
                    boolean flag2 = true;
                    try
                    {
                        inetaddress1 = InetAddress.getLocalHost();
                    } catch (UnknownHostException unknownhostexception)
                    {
                        flag2 = false;
                    }
                    if (flag2)
                        cache.sm.addUnicastAddr(inetaddress1);
                }
            } else if (!cache.sm.isSenderDefaultAddr(remoteAddress))
            {
                cache.sm.addUnicastAddr(remoteAddress);
            }
        }

        ssrcinfo.received++;
        ssrcinfo.stats.update(RTPStats.PDUPROCSD);
        if (ssrcinfo.probation > 0)
        {
            probationList.put(ssrcinfo.ssrc, rtppacket.clone());
            return null;
        }
        ssrcinfo.maxseq = rtppacket.seqnum;
        if (ssrcinfo.lastPayloadType != -1
                && ssrcinfo.lastPayloadType != rtppacket.payloadType)
        {
            ssrcinfo.currentformat = null;
            if (ssrcinfo.dsource != null)
            {
                RTPControlImpl rtpcontrolimpl
                    = (RTPControlImpl) ssrcinfo.dsource.getControl(controlstr);
                if (rtpcontrolimpl != null)
                {
                    rtpcontrolimpl.currentformat = null;
                    rtpcontrolimpl.payload = -1;
                }
            }

            if (ssrcinfo.dsource != null)
            {
                try
                {
                    Log.warning("Stopping stream because of payload type "
                            + "mismatch: expecting pt="
                            + ssrcinfo.lastPayloadType + ", got pt="
                            + rtppacket.payloadType);
                    ssrcinfo.dsource.stop();
                }
                catch (IOException ioexception)
                {
                    System.err.println("Stopping DataSource after PCE "
                            + ioexception.getMessage());
                }
            }
            ssrcinfo.lastPayloadType = rtppacket.payloadType;
            RemotePayloadChangeEvent remotepayloadchangeevent
                = new RemotePayloadChangeEvent(
                        cache.sm, (ReceiveStream) ssrcinfo,
                        ssrcinfo.lastPayloadType, rtppacket.payloadType);
            cache.eventhandler.postEvent(remotepayloadchangeevent);
        }
        if (ssrcinfo.currentformat == null)
        {
            ssrcinfo.currentformat = cache.sm.formatinfo
                    .get(rtppacket.payloadType);
            if (ssrcinfo.currentformat == null)
            {
                if (errorPayload != rtppacket.payloadType)
                {
                    Log.error(
                            "No format has been registered for RTP payload type "
                                + rtppacket.payloadType);
                    errorPayload = rtppacket.payloadType;
                }
                return rtppacket;
            }
            if (ssrcinfo.dstream != null)
                ssrcinfo.dstream.setFormat(ssrcinfo.currentformat);
        }
        if (ssrcinfo.currentformat == null)
        {
            System.err.println("No Format for PT= " + rtppacket.payloadType);
            return rtppacket;
        }
        if (ssrcinfo.dsource != null)
        {
            RTPControlImpl rtpcontrolimpl1
                = (RTPControlImpl) ssrcinfo.dsource.getControl(controlstr);
            if (rtpcontrolimpl1 != null)
            {
                javax.media.Format format
                    = cache.sm.formatinfo.get(rtppacket.payloadType);
                rtpcontrolimpl1.currentformat = format;
            }
        }
        if (!initBC)
        {
            ((BufferControlImpl) cache.sm.buffercontrol)
                    .initBufferControl(ssrcinfo.currentformat);
            initBC = true;
        }
        if (!ssrcinfo.streamconnect)
        {
            DataSource datasource = cache.sm.dslist.get(ssrcinfo.ssrc);
            if (datasource == null)
            {
                DataSource datasource1 = cache.sm.getDataSource(null);
                if (datasource1 == null)
                {
                    datasource = cache.sm.createNewDS(null);
                    cache.sm.setDefaultDSassigned(ssrcinfo.ssrc);
                } else if (!cache.sm.isDefaultDSassigned())
                {
                    datasource = datasource1;
                    cache.sm.setDefaultDSassigned(ssrcinfo.ssrc);
                } else
                {
                    datasource = cache.sm.createNewDS(ssrcinfo.ssrc);
                }
            }
            javax.media.protocol.PushBufferStream apushbufferstream[]
                = datasource.getStreams();
            ssrcinfo.dsource = datasource;
            ssrcinfo.dstream = (RTPSourceStream) apushbufferstream[0];
            ssrcinfo.dstream.setContentDescriptor(content);
            ssrcinfo.dstream.setFormat(ssrcinfo.currentformat);
            RTPControlImpl rtpcontrolimpl2
                = (RTPControlImpl) ssrcinfo.dsource.getControl(controlstr);
            if (rtpcontrolimpl2 != null)
            {
                javax.media.Format format1 = cache.sm.formatinfo
                        .get(rtppacket.payloadType);
                rtpcontrolimpl2.currentformat = format1;
                rtpcontrolimpl2.stream = ssrcinfo;
            }
            ssrcinfo.streamconnect = true;
        }
        if (ssrcinfo.dsource != null)
            ssrcinfo.active = true;
        if (!ssrcinfo.newrecvstream)
        {
            NewReceiveStreamEvent newreceivestreamevent
                = new NewReceiveStreamEvent(cache.sm, (ReceiveStream) ssrcinfo);
            ssrcinfo.newrecvstream = true;
            cache.eventhandler.postEvent(newreceivestreamevent);
        }
        if (ssrcinfo.lastRTPReceiptTime != 0L
                && ssrcinfo.lastPayloadType == rtppacket.payloadType)
        {
            long l = rtppacket.receiptTime - ssrcinfo.lastRTPReceiptTime;
            l = (l * cache.clockrate[ssrcinfo.payloadType]) / 1000L;
            long l1 = rtppacket.timestamp - ssrcinfo.lasttimestamp;
            double d = l - l1;
            if (d < 0.0D)
                d = -d;
            ssrcinfo.jitter += 0.0625D * (d - ssrcinfo.jitter);
        }
        ssrcinfo.lastRTPReceiptTime = rtppacket.receiptTime;
        ssrcinfo.lasttimestamp = rtppacket.timestamp;
        ssrcinfo.payloadType = rtppacket.payloadType;
        ssrcinfo.lastPayloadType = rtppacket.payloadType;
        ssrcinfo.bytesreceived += rtppacket.payloadlength;
        ssrcinfo.lastHeardFrom = rtppacket.receiptTime;
        if (ssrcinfo.quiet)
        {
            ssrcinfo.quiet = false;
            ActiveReceiveStreamEvent activereceivestreamevent
                = new ActiveReceiveStreamEvent(
                        cache.sm,
                        ssrcinfo.sourceInfo,
                        (ssrcinfo instanceof ReceiveStream)
                            ? (ReceiveStream) ssrcinfo
                            : null);
            cache.eventhandler.postEvent(activereceivestreamevent);
        }
        SourceRTPPacket sourcertppacket
            = new SourceRTPPacket(rtppacket, ssrcinfo);
        if (ssrcinfo.dsource != null)
        {
            if (flag)
            {
                RTPPacket rtppacket1 = probationList.remove(ssrcinfo.ssrc);
                if (rtppacket1 != null)
                    rtpdemultiplexer.demuxpayload(new SourceRTPPacket(
                            rtppacket1, ssrcinfo));
            }
            rtpdemultiplexer.demuxpayload(sourcertppacket);
        }

        return rtppacket;
    }
}
