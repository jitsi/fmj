package net.sf.fmj.media.rtp;

import java.awt.*;

import javax.media.control.*;

import net.sf.fmj.media.*;

/**
 * Implements {@link PacketQueueControl} for {@link RTPSourceStream} and the
 * queue of RTP packets that it utilizes.
 *
 * @author Boris Grozev
 * @author Lyubomir Marinov
 * @author Tom Denham
 */
public class JitterBufferStats
    implements PacketQueueControl
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
     * The queue of RTP packets for which this instance implements
     * {@link PacketQueueControl}.
     */
    private final RTPSourceStream.PktQue q;

    /**
     * Initializes a new <tt>JitterBufferStats</tt> instance which is to
     * implement {@link PacketQueueControl} for a specific queue of RTP packets
     * utilized by an {@link RTPSourceStream}.
     *
     * @param q the queue of RTP packets utilized by an {@link RTPSourceStream}
     * for which the new instance is to implement {@link PacketQueueControl}
     */
    JitterBufferStats(RTPSourceStream.PktQue q)
    {
        this.q = q;
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
     */
    public int getCurrentDelayMs()
    {
        return (int) (getCurrentDelayPackets() * q.msPerPkt);
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
        return q.totalPkts();
    }

    /**
     * {@inheritDoc}
     */
    public int getCurrentSizePackets()
    {
        return q.size;
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
     * Increments the number of RTP packets that the associated queue has
     * discarded because it was full.
     */
    void incrementDiscardedFull()
    {
        discardedFull++;
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
    }

    /**
     * Increments the number of RTP packets that the associated queue has
     * discarded due to resetting.
     */
    void incrementDiscardedReset()
    {
        discardedReset++;
    }

    /**
     * Increments the number of RTP packets that the associated queue has
     * discarded while shrinking.
     */
    void incrementDiscardedShrink()
    {
        discardedShrink++;
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
     * {@inheritDoc}
     */
    public boolean isAdaptiveBufferEnabled()
    {
        return q.AJB_ENABLED;
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
}
