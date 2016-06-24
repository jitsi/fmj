package net.sf.fmj.media.rtp;

import java.io.*;
import java.net.*;

import javax.media.Format;
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
    static final int MAX_DROPOUT = 3000;

    static final int MAX_MISORDER = 100;

    /**
     * Gets an {@link RTPControlImpl} control over the {@code dsource} of a
     * specific {@link SSRCInfo}.
     *
     * @param ssrcinfo the {@code SSRCInfo} whose {@code dsource} is to be
     * queried for a {@code RTPControlImpl} control
     * @return an {@code RTPControlImpl} control over the {@code dsource} of the
     * specified {@code ssrcinfo}
     */
    private RTPControlImpl getDsourceRTPControlImpl(SSRCInfo ssrcinfo)
    {
        return ssrcinfo.dsource.getControl(RTPControlImpl.class);
    }

    final SSRCCache cache;

    final RTPDemultiplexer rtpdemultiplexer;

    int lastseqnum = -1;

    private boolean rtcpstarted = false;

    private final String content = "";

    final SSRCTable<RTPPacket> probationList = new SSRCTable<RTPPacket>();

    /**
     * The indicator which signifies whether
     * {@link BufferControlImpl#initBufferControl(Format)} has been invoked.
     */
    private boolean initBC = false;

    private int errorPayload = -1;

    public RTPReceiver(SSRCCache ssrccache, RTPDemultiplexer rtpdemultiplexer1)
    {
        cache = ssrccache;
        rtpdemultiplexer = rtpdemultiplexer1;
        setConsumer(null);
    }

    public RTPReceiver(
            SSRCCache ssrccache,
            RTPDemultiplexer rtpdemultiplexer,
            DatagramSocket datagramsocket)
    {
        this(
                ssrccache,
                rtpdemultiplexer,
                new RTPRawReceiver(datagramsocket, ssrccache.sm.defaultstats));
    }

    public RTPReceiver(
            SSRCCache ssrccache,
            RTPDemultiplexer rtpdemultiplexer,
            int i,
            String s)
        throws IOException
    {
        this(
                ssrccache,
                rtpdemultiplexer,
                new RTPRawReceiver(i & -2, s, ssrccache.sm.defaultstats));
    }

    public RTPReceiver(
            SSRCCache ssrccache,
            RTPDemultiplexer rtpdemultiplexer,
            PacketSource packetsource)
    {
        this(ssrccache, rtpdemultiplexer);
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

    public Packet handlePacket(RTPPacket rtppacket)
    {
        // damencho: Do not process silence packets and buggy X-Lite.
        final int pt = rtppacket.payloadType;

        if (pt == 13)
            return rtppacket;

        final InetAddress address;
        final int port;

        if (rtppacket.base instanceof UDPPacket)
        {
            UDPPacket udppacket = (UDPPacket) rtppacket.base;
            address = udppacket.remoteAddress;
            if (cache.sm.bindtome
                    && !cache.sm.isBroadcast(cache.sm.dataaddress)
                    && !address.equals(cache.sm.dataaddress))
            {
                return null;
            }
            port = udppacket.remotePort;
        }
        else
        {
            address = null;
            port = 0;
        }

        SSRCInfo ssrcinfo = cache.get(rtppacket.ssrc, address, port, 1);
        if (ssrcinfo == null)
            return null;

        // update lastHeardFrom fields in the cache for csrc's
        for (int csrc : rtppacket.csrc)
        {
            SSRCInfo csrcinfo = cache.get(csrc, address, port, 1);
            if (csrcinfo != null)
                csrcinfo.lastHeardFrom = rtppacket.receiptTime;
        }

        if (!ssrcinfo.sender)
        {
            ssrcinfo.initsource(rtppacket.seqnum);
            ssrcinfo.payloadType = pt;
        }

        int oldprobation = ssrcinfo.probation;
        updateStats(ssrcinfo, rtppacket);
        int newprobation = ssrcinfo.probation;

        if (cache.sm.isUnicast())
        {
            if (!rtcpstarted)
            {
                cache.sm.startRTCPReports(address);
                rtcpstarted = true;
                byte[] controladdress = cache.sm.controladdress.getAddress();
                if ((controladdress[3] & 0xff) == 255)
                {
                    cache.sm.addUnicastAddr(cache.sm.controladdress);
                } else
                {
                    InetAddress localhost;
                    boolean addUnicastAddr;
                    try
                    {
                        localhost = InetAddress.getLocalHost();
                        addUnicastAddr = true;
                    } catch (UnknownHostException unknownhostexception)
                    {
                        localhost = null;
                        addUnicastAddr = false;
                    }
                    if (addUnicastAddr)
                        cache.sm.addUnicastAddr(localhost);
                }
            } else if (!cache.sm.isSenderDefaultAddr(address))
            {
                cache.sm.addUnicastAddr(address);
            }
        }

        ssrcinfo.received++;
        ssrcinfo.stats.update(RTPStats.PDUPROCSD);
        if (newprobation > 0)
        {
            probationList.put(ssrcinfo.ssrc, rtppacket.clone());
            return null;
        }
        ssrcinfo.maxseq = rtppacket.seqnum;

        int lastPayloadType = updateLastPayloadType(ssrcinfo, rtppacket);

        if (!assertCurrentformat(ssrcinfo, pt))
            return rtppacket;

        if (!initBC)
        {
            ((BufferControlImpl) cache.sm.buffercontrol).initBufferControl(
                    ssrcinfo.currentformat);
            initBC = true;
        }

        streamconnect(ssrcinfo); // if necessary

        if (ssrcinfo.dsource != null)
            ssrcinfo.active = true;
        if (!ssrcinfo.newrecvstream)
        {
            ssrcinfo.newrecvstream = true;
            cache.eventhandler.postEvent(
                    new NewReceiveStreamEvent(
                            cache.sm,
                            (ReceiveStream) ssrcinfo));
        }

        updateJitter(ssrcinfo, lastPayloadType, rtppacket);

        ssrcinfo.lastRTPReceiptTime = rtppacket.receiptTime;
        ssrcinfo.lasttimestamp = rtppacket.timestamp;

        updatePayloadType(ssrcinfo, rtppacket);

        ssrcinfo.bytesreceived += rtppacket.payloadlength;
        ssrcinfo.lastHeardFrom = rtppacket.receiptTime;
        if (ssrcinfo.quiet)
        {
            ssrcinfo.quiet = false;
            cache.eventhandler.postEvent(
                    new ActiveReceiveStreamEvent(
                            cache.sm,
                            ssrcinfo.sourceInfo,
                            (ssrcinfo instanceof ReceiveStream)
                                ? (ReceiveStream) ssrcinfo
                                : null));
        }

        demuxpayload(
                ssrcinfo,
                /* wasOnProbation */ oldprobation > 0 && newprobation == 0,
                rtppacket);

        return rtppacket;
    }

    private boolean assertCurrentformat(SSRCInfo ssrcinfo, int pt)
    {
        if (ssrcinfo.currentformat == null)
        {
            ssrcinfo.currentformat = cache.sm.formatinfo.get(pt);
            if (ssrcinfo.currentformat == null)
            {
                if (errorPayload != pt)
                {
                    errorPayload = pt;
                    Log.error(
                            "No format has been registered for RTP payload type"
                                + " (number) " + pt + "!");
                }
                return false;
            }
            if (ssrcinfo.dstream != null)
                ssrcinfo.dstream.setFormat(ssrcinfo.currentformat);
        }

        if (ssrcinfo.dsource != null)
        {
            RTPControlImpl rtpcontrolimpl = getDsourceRTPControlImpl(ssrcinfo);
            if (rtpcontrolimpl != null)
            {
                // XXX Querying cache.sm.formatinfo for pt seems kind of
                // convoluted given that the intent is to keep
                // rtpcontrolimpl.currentformat in sync with
                // ssrcinfo.currentformat (which is itself kept in sync with
                // pt). Besides, ssrcinfo.currentformat is in use elsewhere.
                // Moreover, the query makes the logic less flexible with
                // respect to pt.
                rtpcontrolimpl.currentformat = ssrcinfo.currentformat;
            }
        }

        return true;
    }

    private void demuxpayload(
            SSRCInfo ssrcinfo,
            boolean wasOnProbation,
            RTPPacket rtppacket)
    {
        if (ssrcinfo.dsource != null)
        {
            if (wasOnProbation)
            {
                RTPPacket rtppacket1 = probationList.remove(ssrcinfo.ssrc);
                if (rtppacket1 != null)
                    rtpdemultiplexer.demuxpayload(
                            new SourceRTPPacket(rtppacket1, ssrcinfo));
            }
            rtpdemultiplexer.demuxpayload(
                    new SourceRTPPacket(rtppacket, ssrcinfo));
        }
    }

    /**
     * Updates the value of the field {@code lastPayloadType} of
     * {@code ssrcinfo} based on the information carried by {@code rtppacket}.
     *
     * @param ssrcinfo the {@code SSRCInfo} to update
     * @param rtppacket the {@code RTPPacket} with which {@code ssrcinfo} is to
     * be updated
     * @return the value of the field {@code lastPayloadType} of
     * {@code ssrcinfo} before the update
     */
    private int updateLastPayloadType(
            SSRCInfo ssrcinfo,
            RTPPacket rtppacket)
    {
        int oldpayload = ssrcinfo.lastPayloadType;
        int newpayload = rtppacket.payloadType;

        if (oldpayload != -1 && oldpayload != newpayload)
        {
            // XXX Google Chrome/WebRTC uses zero-payload (more specifically,
            // the whole payload is padding) RTP packets to probe bandwidth
            // availability. Since such packets carry no payload significant to
            // ssrcinfo.dsource, ignore them (as far as ssrcinfo.dsource is
            // concerned, of course).
            if (rtppacket.payloadlength == 0)
            {
                // To repeat the javadoc for the sake of clarity, the return
                // value is the value of the field lastPayloadType of ssrcinfo
                // BEFORE the update.
                return oldpayload;
            }

            ssrcinfo.currentformat = null;
            if (ssrcinfo.dsource != null)
            {
                RTPControlImpl rtpcontrolimpl
                    = getDsourceRTPControlImpl(ssrcinfo);
                if (rtpcontrolimpl != null)
                {
                    rtpcontrolimpl.currentformat = null;
                    rtpcontrolimpl.payload = -1;
                }

                try
                {
                    Log.warning(
                            "Stopping stream because of payload type mismatch:"
                                + " expecting pt=" + oldpayload + ", got pt="
                                + newpayload);
                    ssrcinfo.dsource.stop();
                }
                catch (IOException ioe)
                {
                    System.err.println(
                            "Stopping DataSource after PCE "+ ioe.getMessage());
                }
            }

            ssrcinfo.lastPayloadType = newpayload;

            cache.eventhandler.postEvent(
                    new RemotePayloadChangeEvent(
                            cache.sm,
                            (ReceiveStream) ssrcinfo,
                            oldpayload, newpayload));
        }
        else
        {
            ssrcinfo.lastPayloadType = newpayload;
        }

        // To repeat the javadoc for the sake of clarity, the return value is
        // the value of the field lastPayloadType of ssrcinfo BEFORE the update.
        return oldpayload;
    }

    /**
     * Updates the value of the field {@code payloadType} of {@code ssrcinfo}
     * based on the information carried by {@code rtppacket}.
     *
     * @param ssrcinfo the {@code SSRCInfo} to update
     * @param rtppacket the {@code RTPPacket} with which {@code ssrcinfo} is to
     * be updated
     * @return the value of the field {@code payloadType} of {@code ssrcinfo}
     * before the update
     */
    private int updatePayloadType(SSRCInfo ssrcinfo, RTPPacket rtppacket)
    {
        int oldpayload = ssrcinfo.payloadType;
        int newpayload = rtppacket.payloadType;

        if (oldpayload == -1
                || (oldpayload != newpayload
                    // XXX Google Chrome/WebRTC uses zero-payload (more
                    // specifically, the whole payload is padding) RTP packets
                    // to probe bandwidth availability.
                    && rtppacket.payloadlength != 0))
        {
            ssrcinfo.payloadType = rtppacket.payloadType;
        }

        // To repeat the javadoc for the sake of clarity, the return value is
        // the value of the field payloadType of ssrcinfo BEFORE the update.
        return oldpayload;
    }

    private void streamconnect(SSRCInfo ssrcinfo)
    {
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
            javax.media.protocol.PushBufferStream[] pushbufferstreams
                = datasource.getStreams();
            ssrcinfo.dsource = datasource;
            ssrcinfo.dstream = (RTPSourceStream) pushbufferstreams[0];
            ssrcinfo.dstream.setContentDescriptor(content);
            ssrcinfo.dstream.setFormat(ssrcinfo.currentformat);
            RTPControlImpl rtpcontrolimpl = getDsourceRTPControlImpl(ssrcinfo);
            if (rtpcontrolimpl != null)
            {
                // XXX Querying cache.sm.formatinfo for pt seems kind of
                // convoluted given that the intent is to keep
                // rtpcontrolimpl.currentformat in sync with
                // ssrcinfo.currentformat (which is itself kept in sync with
                // pt). Besides, ssrcinfo.currentformat is in use elsewhere.
                // Moreover, the query makes the logic less flexible with
                // respect to pt.
                rtpcontrolimpl.currentformat = ssrcinfo.currentformat;
                rtpcontrolimpl.stream = ssrcinfo;
            }
            ssrcinfo.streamconnect = true;
        }
    }

    private void updateJitter(
            SSRCInfo ssrcinfo,
            int lastPayloadType,
            RTPPacket rtppacket)
    {
        if (ssrcinfo.lastRTPReceiptTime != 0L
                // XXX Google Chrome/WebRTC uses zero-payload (more specifically,
                // the whole payload is padding) RTP packets to probe bandwidth
                // availability. At present it sounds better to not do anything
                // about it here and allow jitter to be updated based on
                // subsequent packets of one and the same payload type (number).
                && lastPayloadType == rtppacket.payloadType)
        {
            long l = rtppacket.receiptTime - ssrcinfo.lastRTPReceiptTime;
            l = (l * cache.clockrate[ssrcinfo.payloadType]) / 1000L;
            long l1 = rtppacket.timestamp - ssrcinfo.lasttimestamp;
            double d = Math.abs((double) (l - l1));
            ssrcinfo.jitter += 0.0625D * (d - ssrcinfo.jitter);
        }
    }

    private void updateStats(SSRCInfo ssrcinfo, RTPPacket rtppacket)
    {
        int seqnum = rtppacket.seqnum;
        int diff = seqnum - ssrcinfo.maxseq;
        if (diff > 0)
        {
            if (ssrcinfo.maxseq + 1 != seqnum)
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
        if (ssrcinfo.probation > 0)
        {
            if (seqnum == ssrcinfo.maxseq + 1)
            {
                ssrcinfo.probation--;
                ssrcinfo.maxseq = seqnum;
            } else
            {
                ssrcinfo.probation = 1;
                ssrcinfo.maxseq = seqnum;
                ssrcinfo.stats.update(RTPStats.PDUMISORD);
            }
        } else if (diff < MAX_DROPOUT && diff != 0)
        {
            if (seqnum < ssrcinfo.baseseq)
            {
                // Vincent Lucas: Without any lost, the seqnum cycles when
                // passing from 65535 to 0. Thus, diff is equal to -65535. But
                // if there have been losses, diff may be -65534, -65533, etc.
                // On the other hand, if diff is too close to 0 (i.e. -1, -2,
                // etc.), it may correspond to a packet out of sequence. This is
                // why it is a sound choice to differentiate between a cycle and
                // an out-of-sequence on the basis of a value in between the two
                // cases i.e. -65535 / 2.
                if (diff < -65535 / 2)
                {
                    ssrcinfo.cycles += 0x10000;
                    ssrcinfo.wrapped = true;
                }
            }
            ssrcinfo.maxseq = seqnum;
        } else if (diff <= 65536 - MAX_MISORDER && diff != 0)
        {
            ssrcinfo.stats.update(RTPStats.PDUINVALID);
            if (seqnum == ssrcinfo.lastbadseq)
                ssrcinfo.initsource(seqnum);
            else
                ssrcinfo.lastbadseq = seqnum + 1 & 0xffff;
        } else
        {
            ssrcinfo.stats.update(RTPStats.PDUDUP);
        }
    }
}
