package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;

import javax.media.rtp.*;
import javax.media.rtp.event.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.protocol.rtp.*;
import net.sf.fmj.media.rtp.util.*;

public class RTPReceiver extends PacketFilter
{
    SSRCCache cache;

    RTPDemultiplexer rtpdemultiplexer;

    int lastseqnum;

    private boolean rtcpstarted;

    private boolean setpriority;

    private boolean mismatchprinted;

    private String content;

    SSRCTable probationList;

    static final int MAX_DROPOUT = 3000;

    static final int MAX_MISORDER = 100;

    static final int SEQ_MOD = 0x10000;
    static final int MIN_SEQUENTIAL = 2;
    private boolean initBC;
    public String controlstr;
    private int errorPayload;

    public RTPReceiver(SSRCCache ssrccache, RTPDemultiplexer rtpdemultiplexer1)
    {
        lastseqnum = -1;
        rtcpstarted = false;
        setpriority = false;
        mismatchprinted = false;
        content = "";
        probationList = new SSRCTable();
        initBC = false;
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
        {
            return null;
        }

        SSRCInfo ssrcinfo = null;
        if (rtppacket.base instanceof UDPPacket)
        {
            InetAddress inetaddress = ((UDPPacket) rtppacket.base).remoteAddress;
            if (cache.sm.bindtome
                    && !cache.sm.isBroadcast(cache.sm.dataaddress)
                    && !inetaddress.equals(cache.sm.dataaddress))
            {
                return null;
            }
        } else if (rtppacket.base instanceof Packet)
            rtppacket.base.toString();
        if (ssrcinfo == null)
            if (rtppacket.base instanceof UDPPacket)
                ssrcinfo = cache.get(rtppacket.ssrc,
                        ((UDPPacket) rtppacket.base).remoteAddress,
                        ((UDPPacket) rtppacket.base).remotePort, 1);
            else
                ssrcinfo = cache.get(rtppacket.ssrc, null, 0, 1);
        if (ssrcinfo == null)
        {
            return null;
        }
        for (int i = 0; i < rtppacket.csrc.length; i++)
        {
            SSRCInfo ssrcinfo1 = null;
            if (rtppacket.base instanceof UDPPacket)
                ssrcinfo1 = cache.get(rtppacket.csrc[i],
                        ((UDPPacket) rtppacket.base).remoteAddress,
                        ((UDPPacket) rtppacket.base).remotePort, 1);
            else
                ssrcinfo1 = cache.get(rtppacket.csrc[i], null, 0, 1);
            if (ssrcinfo1 != null)
                ssrcinfo1.lastHeardFrom = ((Packet) (rtppacket)).receiptTime;
        }

        if (ssrcinfo.lastPayloadType != -1
                && ssrcinfo.lastPayloadType == rtppacket.payloadType
                && mismatchprinted)
        {
            return null;
        }

        if (!ssrcinfo.sender)
        {
            ssrcinfo.initsource(rtppacket.seqnum);
            ssrcinfo.payloadType = rtppacket.payloadType;
        }
        int j = rtppacket.seqnum - ssrcinfo.maxseq;
        if (ssrcinfo.maxseq + 1 != rtppacket.seqnum && j > 0)
            ssrcinfo.stats.update(0, j - 1);
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
                ssrcinfo.stats.update(2);
            }
        } else if (j < 3000)
        {
            if (rtppacket.seqnum < ssrcinfo.baseseq)
            {
                /*
                 * Vincent Lucas: Without any lost, the seqnum cycles when
                 * passing from 65535 to 0. Thus, j is equal to -65535. But if
                 * there have been some occurrences of loss, j may be -65534,
                 * -65533, etc. On the other hand, if j is too close to 0 i.e.
                 * -1, -2, etc., it may correspond to a desequenced packet. This
                 * is why it is a sound choice to differentiate between a cycle
                 * and a desequence on the basis of a value in between the two
                 * cases i.e. -65535 / 2.
                 */
                if (j < -65535 / 2)
                {
                    ssrcinfo.cycles += 0x10000;
                    ssrcinfo.wrapped = true;
                }
            }
            ssrcinfo.maxseq = rtppacket.seqnum;
        } else if (j <= 65436)
        {
            ssrcinfo.stats.update(3);
            if (rtppacket.seqnum == ssrcinfo.lastbadseq)
                ssrcinfo.initsource(rtppacket.seqnum);
            else
                ssrcinfo.lastbadseq = rtppacket.seqnum + 1 & 0xffff;
        } else
        {
            ssrcinfo.stats.update(4);
        }
        boolean flag1 = cache.sm.isUnicast();
        if (flag1)
            if (!rtcpstarted)
            {
                cache.sm.startRTCPReports(((UDPPacket) rtppacket.base).remoteAddress);
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
            } else if (!cache.sm
                    .isSenderDefaultAddr(((UDPPacket) rtppacket.base).remoteAddress))
                cache.sm.addUnicastAddr(((UDPPacket) rtppacket.base).remoteAddress);
        ssrcinfo.received++;
        ssrcinfo.stats.update(1);
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
                RTPControlImpl rtpcontrolimpl = (RTPControlImpl) ssrcinfo.dsource
                        .getControl(controlstr);
                if (rtpcontrolimpl != null)
                {
                    rtpcontrolimpl.currentformat = null;
                    rtpcontrolimpl.payload = -1;
                }
            }
            ssrcinfo.lastPayloadType = rtppacket.payloadType;
            if (ssrcinfo.dsource != null)
                try
                {
                    ssrcinfo.dsource.stop();
                } catch (IOException ioexception)
                {
                    System.err.println("Stopping DataSource after PCE "
                            + ioexception.getMessage());
                }
            RemotePayloadChangeEvent remotepayloadchangeevent = new RemotePayloadChangeEvent(
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
                    Log.error("No format has been registered for RTP Payload type "
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
            RTPControlImpl rtpcontrolimpl1 = (RTPControlImpl) ssrcinfo.dsource
                    .getControl(controlstr);
            if (rtpcontrolimpl1 != null)
            {
                javax.media.Format format = cache.sm.formatinfo
                        .get(rtppacket.payloadType);
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
            DataSource datasource = (DataSource) cache.sm.dslist
                    .get(ssrcinfo.ssrc);
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
            javax.media.protocol.PushBufferStream apushbufferstream[] = datasource
                    .getStreams();
            ssrcinfo.dsource = datasource;
            ssrcinfo.dstream = (RTPSourceStream) apushbufferstream[0];
            ssrcinfo.dstream.setContentDescriptor(content);
            ssrcinfo.dstream.setFormat(ssrcinfo.currentformat);
            ssrcinfo.dstream.setStats(ssrcinfo.stats);
            RTPControlImpl rtpcontrolimpl2 = (RTPControlImpl) ssrcinfo.dsource
                    .getControl(controlstr);
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
            NewReceiveStreamEvent newreceivestreamevent = new NewReceiveStreamEvent(
                    cache.sm, (ReceiveStream) ssrcinfo);
            ssrcinfo.newrecvstream = true;
            cache.eventhandler.postEvent(newreceivestreamevent);
        }
        if (ssrcinfo.lastRTPReceiptTime != 0L
                && ssrcinfo.lastPayloadType == rtppacket.payloadType)
        {
            long l = ((Packet) (rtppacket)).receiptTime
                    - ssrcinfo.lastRTPReceiptTime;
            l = (l * cache.clockrate[ssrcinfo.payloadType]) / 1000L;
            long l1 = rtppacket.timestamp - ssrcinfo.lasttimestamp;
            double d = l - l1;
            if (d < 0.0D)
                d = -d;
            ssrcinfo.jitter += 0.0625D * (d - ssrcinfo.jitter);
        }
        ssrcinfo.lastRTPReceiptTime = ((Packet) (rtppacket)).receiptTime;
        ssrcinfo.lasttimestamp = rtppacket.timestamp;
        ssrcinfo.payloadType = rtppacket.payloadType;
        ssrcinfo.lastPayloadType = rtppacket.payloadType;
        ssrcinfo.bytesreceived += rtppacket.payloadlength;
        ssrcinfo.lastHeardFrom = ((Packet) (rtppacket)).receiptTime;
        if (ssrcinfo.quiet)
        {
            ssrcinfo.quiet = false;
            ActiveReceiveStreamEvent activereceivestreamevent = null;
            if (ssrcinfo instanceof ReceiveStream)
                activereceivestreamevent = new ActiveReceiveStreamEvent(
                        cache.sm, ssrcinfo.sourceInfo, (ReceiveStream) ssrcinfo);
            else
                activereceivestreamevent = new ActiveReceiveStreamEvent(
                        cache.sm, ssrcinfo.sourceInfo, null);
            cache.eventhandler.postEvent(activereceivestreamevent);
        }
        SourceRTPPacket sourcertppacket = new SourceRTPPacket(rtppacket,
                ssrcinfo);
        if (ssrcinfo.dsource != null)
        {
            if (mismatchprinted)
                mismatchprinted = false;
            if (flag)
            {
                RTPPacket rtppacket1 = (RTPPacket) probationList
                        .remove(ssrcinfo.ssrc);
                if (rtppacket1 != null)
                    rtpdemultiplexer.demuxpayload(new SourceRTPPacket(
                            rtppacket1, ssrcinfo));
            }
            rtpdemultiplexer.demuxpayload(sourcertppacket);
        }

        return rtppacket;
    }
}
