package net.sf.fmj.media.rtp;

import java.awt.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;
import javax.media.rtp.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.protocol.rtp.DataSource;

/**
 * Implements {@link PacketQueueControl} for {@link RTPSourceStream} and the
 * queue of RTP packets that it utilizes.
 *
 * @author Boris Grozev
 * @author Lyubomir Marinov
 * @author Tom Denham
 */
class JitterBufferStats
    implements JitterBufferControl
{
    /**
     * The number of RTP packets that the associated queue has discarded because
     * it was full.
     */
    private int discardedFull;

    /**
     * The number of RTP packets that the associated queue has discarded because
     * they arrived too late to be added to the queue. If the queue exhibits
     * adaptive behavior, it has taken into account the fact that the packets
     * in question have arrived too late.
     */
    private int discardedLate;

    /**
     * The number of RTP packets that the associated queue has discarded due to
     * resetting.
     */
    private int discardedReset;

    /**
     * The number of RTP packets that the associated queue has discarded while
     * shrinking.
     */
    private int discardedShrink;

    /**
     * The number of RTP packets that the associated queue has discarded because
     * they arrived too late to be added to the queue. If the queue exhibits
     * adaptive behavior, it has NOT (in contrast to {@link #discardedLate}
     * taken into account the fact that the packets in question have arrived too
     * late.
     */
    private int discardedVeryLate;

    /**
     * The maximum size/capacity in number of RTP packets that the associated
     * queue has ever reached.
     */
    private int maxSizeReached;

    private int nbAdd;

    private int nbGrow;

    private int nbReset;

    /**
     * An average approximation of the size in bytes of an RTP packet.
     */
    private int sizePerPacket;

    /**
     * The {@link RTPSourceStream} for which this instance implements
     * {@link PacketQueueControl}.
     */
    private final RTPSourceStream stream;

    /**
     * Initializes a new <tt>JitterBufferStats</tt> instance which is to
     * implement {@link PacketQueueControl} for a specific
     * {@link RTPSourceStream}.
     *
     * @param stream the <tt>RTPSourceStream</tt> for which the new instance is
     * to implement {@link PacketQueueControl}
     */
    JitterBufferStats(RTPSourceStream stream)
    {
        this.stream = stream;
    }

    /**
     * {@inheritDoc}
     *
     * Delegates to the <tt>JitterBufferBehaviour</tt> of the
     * <tt>RTPSourceStream</tt>.
     */
    @Override
    public int getAbsoluteMaximumDelay()
    {
        return stream.getBehaviour().getAbsoluteMaximumDelay();
    }

    /**
     * {@inheritDoc}
     *
     * The <tt>Control</tt> implementation of <tt>JitterBufferStats</tt> does
     * not provide any user interface of its own and, consequently, always
     * returns <tt>null</tt>.
     */
    public Component getControlComponent()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <b>Warning</b>: The implementation of <tt>JitterBufferStats</tt> does not
     * have a notion of packet duration and, consequently, it may be inaccurate.
     * The method {@link #getNominalDelay()} delegates to
     * <tt>JitterBufferBehaviour</tt> which is more likely to have a notion of
     * packet duration and, consequently, it likely to be more accurate.
     */
    public int getCurrentDelayMs()
    {
        // TODO Auto-generated method stub
        return getCurrentDelayPackets() * 20;
    }

    /**
     * {@inheritDoc}
     */
    public int getCurrentDelayPackets()
    {
        return getCurrentSizePackets() / 2;
    }

    /**
     * {@inheritDoc}
     */
    public int getCurrentPacketCount()
    {
        return stream.q.getFillCount();
    }

    /**
     * {@inheritDoc}
     */
    public int getCurrentSizePackets()
    {
        return stream.q.getCapacity();
    }

    /**
     * {@inheritDoc}
     *
     * Returns the sum of the values of the other <tt>discardedXXX</tt>
     * properties of this instance such as <tt>discardedFull</tt>,
     * <tt>discaredLate</tt>, <tt>discaredReset</tt>, etc.
     */
    public int getDiscarded()
    {
        return
            getDiscardedFull()
                + getDiscardedLate()
                + getDiscardedReset()
                + getDiscardedShrink()
                + getDiscardedVeryLate();
    }

    /**
     * {@inheritDoc}
     */
    public int getDiscardedFull()
    {
        return discardedFull;
    }

    /**
     * {@inheritDoc}
     */
    public int getDiscardedLate()
    {
        return discardedLate;
    }

    /**
     * {@inheritDoc}
     */
    public int getDiscardedReset()
    {
        return discardedReset;
    }

    /**
     * {@inheritDoc}
     */
    public int getDiscardedShrink()
    {
        return discardedShrink;
    }

    /**
     * {@inheritDoc}
     */
    public int getDiscardedVeryLate()
    {
        return discardedVeryLate;
    }

    /**
     * {@inheritDoc}
     *
     * Delegates to the <tt>JitterBufferBehaviour</tt> of the
     * <tt>RTPSourceStream</tt>.
     */
    @Override
    public int getMaximumDelay()
    {
        return stream.getBehaviour().getMaximumDelay();
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxSizeReached()
    {
        return maxSizeReached;
    }

    int getNbAdd()
    {
        return nbAdd;
    }

    /**
     * {@inheritDoc}
     *
     * Delegates to the <tt>JitterBufferBehaviour</tt> of the
     * <tt>RTPSourceStream</tt>.
     */
    @Override
    public int getNominalDelay()
    {
        return stream.getBehaviour().getNominalDelay();
    }

    /**
     * Gets an average approximation of the size in bytes of an RTP packet.
     *
     * @return an average approximation of the size in bytes of an RTP packet
     */
    int getSizePerPacket()
    {
        return sizePerPacket;
    }

    /**
     * Increments the number of RTP packets that the associated queue has
     * discarded because it was full.
     */
    void incrementDiscardedFull()
    {
        discardedFull++;
        incrementRTPStatsPDUDrop();
    }

    /**
     * Increments the number of RTP packets that the associated queue has
     * discarded because they arrived too late to be added to the queue. If the
     * queue exhibits adaptive behavior, it has taken into account the fact that
     * the packets in question have arrived too late.
     */
    void incrementDiscardedLate()
    {
        discardedLate++;
        incrementRTPStatsPDUDrop();
    }

    /**
     * Increments the number of RTP packets that the associated queue has
     * discarded due to resetting.
     */
    void incrementDiscardedReset()
    {
        discardedReset++;
        incrementRTPStatsPDUDrop();
    }

    /**
     * Increments the number of RTP packets that the associated queue has
     * discarded while shrinking.
     */
    void incrementDiscardedShrink()
    {
        discardedShrink++;
        incrementRTPStatsPDUDrop();
    }

    /**
     * Increments the number of RTP packets that the associated queue has
     * discarded because they arrived too late to be added to the queue. If the
     * queue exhibits adaptive behavior, it has NOT (in contrast to
     * {@link #discardedLate} taken into account the fact that the packets in
     * question have arrived too late.
     */
    void incrementDiscardedVeryLate()
    {
        discardedVeryLate++;
        incrementRTPStatsPDUDrop();
    }

    void incrementNbAdd()
    {
        nbAdd++;
    }

    void incrementNbGrow()
    {
        nbGrow++;
    }

    void incrementNbReset()
    {
        nbReset++;
    }

    /**
     * Updates the {@link RTPStats#PDUDROP} of the <tt>RTPStats</tt> associated
     * with this <tt>JitterBufferStats</tt> because the two classes maintain
     * discard-related statistics and <tt>RTPStats</tt> provides them to the
     * public through the interface {@link ReceptionStats}.
     */
    private void incrementRTPStatsPDUDrop()
    {
        /*
         * There is no direct chain of references from JitterBufferStats to
         * RTPStats. Walk through an indirect chain of references then but be
         * careful. Start by making sure that the RTPSourceStream associated
         * with this JitterBufferStats is still associated with the DataSource
         * which initialized it.
         */
        DataSource datasource = stream.datasource;

        if (datasource != null)
        {
            PushBufferStream[] datasourceStreams = datasource.getStreams();

            if (datasourceStreams != null)
            {
                for (PushBufferStream datasourceStream : datasourceStreams)
                {
                    if (datasourceStream == stream)
                    {
                        /*
                         * The DataSource which initialized the RTPSourceStream
                         * associated with this JitterBufferStats is still
                         * associated with it. Continue by finding an SSRCInfo
                         * which is associated with the
                         * DataSource/RTPSourceStream i.e. this
                         * JitterBufferStats.
                         */
                        RTPSessionMgr mgr = datasource.getMgr();

                        if (mgr != null)
                        {
                            SSRCInfo ssrcinfo
                                = mgr.getSSRCInfo(datasource.getSSRC());

                            if ((ssrcinfo != null)
                                    && (ssrcinfo.dsource == datasource)
                                    && (ssrcinfo.dstream == stream))
                            {
                                /*
                                 * We've located the RTPStats associated with
                                 * this JitterBufferStats.
                                 */
                                RTPStats rtpstats = ssrcinfo.stats;

                                if (rtpstats != null)
                                    rtpstats.update(RTPStats.PDUDROP);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAdaptiveBufferEnabled()
    {
        return stream.getBehaviour().isAdaptive();
    }

    /**
     * Prints a human-readable representation of this instance using
     * {@link Log#info(Object)}.
     */
    void printStats()
    {
        String cn = RTPSourceStream.class.getName() + " ";

        Log.info(cn + "Total packets added: " + getNbAdd());
        Log.info(cn + "Times reset() called: " + nbReset);
        Log.info(cn + "Times grow() called: " + nbGrow);
        Log.info(cn + "Packets dropped because full: " + getDiscardedFull());
        Log.info(cn + "Packets dropped while shrinking: " + getDiscardedShrink());
        Log.info(cn + "Packets dropped because they were late: " + getDiscardedLate());
        Log.info(cn + "Packets dropped because they were late by more than MAX_SIZE: " + getDiscardedVeryLate());
        Log.info(cn + "Packets dropped in reset(): " + getDiscardedReset());
        Log.info(cn + "Max size reached: " + getMaxSizeReached());
        Log.info(cn + "Adaptive jitter buffer mode was " + (isAdaptiveBufferEnabled() ? "enabled" : "disabled"));
    }

    /**
     * Notifies this instance that the size/capacity of the associated queue of
     * RTP packets may have changed and that it may be time for this instance to
     * update the value to be returned by {@link #getMaxSizeReached()}.
     */
    void updateMaxSizeReached()
    {
        int size = getCurrentSizePackets();

        if (maxSizeReached < size)
            maxSizeReached = size;
    }

    /**
     * Updates the average approximation of the size in bytes of an RTP packet.
     *
     * @param buffer the <tt>Buffer</tt> which is to be taken into account for
     * the purposes of calculating and maintaining an average approximation of
     * the size in bytes of an RTP packet
     */
    void updateSizePerPacket(Buffer buffer)
    {
        int bufferLength = buffer.getLength();

        sizePerPacket
            = (sizePerPacket == 0)
                ? bufferLength
                : ((sizePerPacket + bufferLength) / 2);
    }
}
