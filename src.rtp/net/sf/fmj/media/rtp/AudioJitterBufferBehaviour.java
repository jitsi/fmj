package net.sf.fmj.media.rtp;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;

import net.sf.fmj.media.*;

/**
 * Implements <tt>JitterBufferBehaviour</tt> for audio media data. It realizes
 * a jitter buffer which is either fixed or adaptive.
 *
 * @author Lyubomir Marinov
 */
class AudioJitterBufferBehaviour
    extends BasicJitterBufferBehaviour
{
    private final static int DEFAULT_AUD_PKT_SIZE = 256;

    /**
     * The default duration in milliseconds of an audio RTP packet. The
     * default value expresses an expectation only. A value of <tt>20</tt>
     * seems more reasonable than, for example, <tt>30</tt> because it is
     * more commonly used in specifications.
     */
    private static final int DEFAULT_MS_PER_PKT = 20;

    /**
     * The number of initial RTP packets <tt>AudioJitterBufferBehaviour</tt> is
     * to ignore before commencing the procedure of adapting.
     */
    private static final int INITIAL_PACKETS = 300;

    private static final AudioFormat MPEG = new AudioFormat("mpegaudio/rtp");

    /**
     * Whether resizing the queue is enabled.
     */
    private final boolean AJB_ENABLED;

    /**
     * How many packets to increment the queue size by, when growing.
     */
    private final int AJB_GROW_INCREMENT;

    /**
     * How many packets to monitor when deciding whether to grow the queue.
     */
    private final int AJB_GROW_INTERVAL;

    /**
     * Grow the queue if there are at least that many late packets (in the
     * last AJB_GROW_INTERVAL packets)
     */
    private final int AJB_GROW_THRESHOLD;

    /**
     * The maximum size/capacity to which this instance will grow the associated
     * RTP packet queue/jitter buffer.
     */
    private final int AJB_MAX_SIZE;

    /**
     * The minimum size/capacity to which this instance will shrink the
     * associated RTP packet queue/jitter buffer.
     */
    private final int AJB_MIN_SIZE;

    /**
     * The number of packets by which this instance is to shrink the associated
     * RTP packet queue/jitter buffer at a time.
     */
    private final int AJB_SHRINK_DECREMENT;

    /**
     * The number of packets during the receipt of which statistics are to be
     * gathered for the purposes of shrinking.
     */
    private final int AJB_SHRINK_INTERVAL;

    /**
     * The number of packets to be monitored whether they represent unnecessary
     * latency so that a decision to shrink by {@link #AJB_SHRINK_DECREMENT} may
     * be made.
     */
    private final int AJB_SHRINK_THRESHOLD;

    /**
     * Contains the number of 'late' packets from the last
     * <tt>AJB_GROW_INTERVAL</tt> packets. Updated on every add().
     */
    private int growCount;

    /**
     * Contains information about the recently received packets. A
     * <tt>0</tt> indicates that the respective packet was accepted
     * normally, a <tt>1</tt> indicates that it was dropped because it was
     * received too late. The storage of the <tt>history</tt> is circular
     * and {@link #historyPointer} always points to the last packet added.
     */
    private byte[] history;

    /**
     * The number of packets for which <tt>history</tt> is valid. Used in
     * order to avoid filling <tt>history</tt> with zeroes when it needs to
     * be reset.
     */
    private int historyLength;

    /**
     * Points to the place in <tt>history</tt> corresponding to the last
     * packet added
     */
    private int historyTail;

    /**
     * The average approximation of the duration in milliseconds of an RTP
     * packet. Used for audio only at the time of this writing. It sounds
     * reasonable to introduce such a value for the duration since there is
     * one for the size in bytes already (i.e. <tt>sizePerPkt</tt>).
     */
    private long msPerPkt = DEFAULT_MS_PER_PKT;

    private boolean replenish = true;

    /**
     * The number of packets which have been received since the last/previous
     * {@link #AJB_SHRINK_INTERVAL} has elapsed i.e. the time expressed in
     * number of packets which has elapsed from the current
     * <tt>AJB_SHRINK_INTERVAL</tt>. At the end of the interval in question, a
     * decision will be made whether the associated queue will be shrunk.
     */
    private int shrinkCount = 0;

    /**
     * The indicator which determines whether the <tt>Buffer.FLAG_SKIP_FEC</tt>
     * flag should be set on the next packet read from/out of the RTP packet
     * queue.
     */
    private boolean skipFec = false;

    /**
     * Initializes a new <tt>AudioJitterBufferBehaviour</tt> instance for the
     * purposes of a specific <tt>RTPSourceStream</tt>.
     *
     * @param stream the <tt>RTPSourceStream</tt> which has requested the
     * initialization of the new instance
     */
    public AudioJitterBufferBehaviour(RTPSourceStream stream)
    {
        super(stream);

        // Assign the adaptive jitter buffer-related properties of this instance
        // values from the Registry or default values.
        AJB_ENABLED
            = com.sun.media.util.Registry.getBoolean(
                    "adaptive_jitter_buffer_ENABLE",
                    true);
        AJB_GROW_INCREMENT
            = com.sun.media.util.Registry.getInt(
                    "adaptive_jitter_buffer_GROW_INCREMENT",
                    2);
        AJB_GROW_INTERVAL
            = com.sun.media.util.Registry.getInt(
                    "adaptive_jitter_buffer_GROW_INTERVAL",
                    30);
        AJB_GROW_THRESHOLD
            = com.sun.media.util.Registry.getInt(
                    "adaptive_jitter_buffer_GROW_THRESHOLD",
                    3);
        AJB_MAX_SIZE
            = com.sun.media.util.Registry.getInt(
                    "adaptive_jitter_buffer_MAX_SIZE",
                    16);
        AJB_MIN_SIZE
            = com.sun.media.util.Registry.getInt(
                    "adaptive_jitter_buffer_MIN_SIZE",
                    4);
        AJB_SHRINK_DECREMENT
            = com.sun.media.util.Registry.getInt(
                    "adaptive_jitter_buffer_SHRINK_DECREMENT",
                    1);
        AJB_SHRINK_INTERVAL
            = com.sun.media.util.Registry.getInt(
                    "adaptive_jitter_buffer_SHRINK_INTERVAL",
                    120);
        AJB_SHRINK_THRESHOLD
            = com.sun.media.util.Registry.getInt(
                    "adaptive_jitter_buffer_SHRINK_THRESHOLD",
                    1);

        initHistory();
    }

    /**
     * {@inheritDoc}
     *
     * Sets the value of {@link #skipFec} to <tt>true</tt>.
     */
    @Override
    public void dropPkt()
    {
        super.dropPkt();

        // We've deliberately dropped a packet since we're full. If FEC is
        // extracted from the next packet, we are likely to be in the same
        // situation again very soon. So avoid FEC being decoded from the next
        // packet read.
        skipFec = true;
        if (q.getFillCount() < AJB_SHRINK_THRESHOLD)
            shrinkCount = 0;
    }

    /**
     * {@inheritDoc}
     *
     * If this <tt>JitterBufferBehaviour</tt> is adaptive, computes the absolute
     * maximum delay based on {@link #AJB_MAX_SIZE}.
     */
    @Override
    public int getAbsoluteMaximumDelay()
    {
        long absoluteMaximumDelay;

        if (isAdaptive())
        {
            long msPerPkt = this.msPerPkt;

            if (msPerPkt <= 0)
                msPerPkt = DEFAULT_MS_PER_PKT;
            absoluteMaximumDelay = AJB_MAX_SIZE * msPerPkt;
        }
        else
        {
            absoluteMaximumDelay = super.getAbsoluteMaximumDelay();
        }
        return
            (absoluteMaximumDelay > 65535) ? 65535 : (int) absoluteMaximumDelay;
    }

    /**
     * {@inheritDoc}
     *
     * Computes the maximum delay based on the <tt>capacity</tt> of the
     * <tt>JitterBuffer</tt>/{@link #q}.
     */
    @Override
    public int getMaximumDelay()
    {
        long msPerPkt = this.msPerPkt;

        if (msPerPkt <= 0)
            msPerPkt = DEFAULT_MS_PER_PKT;

        long maximumDelay = q.getCapacity() * msPerPkt;

        return (maximumDelay > 65535) ? 65535 : (int) maximumDelay;
    }

    /**
     * {@inheritDoc}
     *
     * Computes the nominal delay based on the <tt>capacity</tt> of the
     * <tt>JitterBuffer</tt>/{@link #q} and knowing that a {@link #replenish}
     * requires a half of that <tt>capacity</tt>.
     */
    @Override
    public int getNominalDelay()
    {
        long msPerPkt = this.msPerPkt;

        if (msPerPkt <= 0)
            msPerPkt = DEFAULT_MS_PER_PKT;

        long nominalDelay = (q.getCapacity() / 2) * msPerPkt;

        return (nominalDelay > 65535) ? 65535 : (int) nominalDelay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void grow(int capacity)
    {
        super.grow(capacity);

        resetHistory();
    }

    /**
     * Initializes the history
     */
    private void initHistory()
    {
        history = new byte[AJB_GROW_INTERVAL];
        historyLength = 0;
        historyTail = 0;
        growCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAdaptive()
    {
        return AJB_ENABLED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int monitorQSize(Buffer buffer)
    {
        super.monitorQSize(buffer);

        if (AJB_ENABLED)
        {
            int size = q.getCapacity();

            if ((historyLength >= AJB_GROW_INTERVAL)
                    && (growCount >= AJB_GROW_THRESHOLD)
                    && (size < AJB_MAX_SIZE))
            {
                int n = Math.min(size + AJB_GROW_INCREMENT, AJB_MAX_SIZE);
                if (n > size)
                    grow(n);
            }

            shrinkCount++;
            // The queue will not be shrunk if it will be unable to accommodate
            // the specified buffer afterwards.
            if ((shrinkCount >= AJB_SHRINK_INTERVAL)
                    && (size > AJB_MIN_SIZE)
                    && q.freeNotEmpty())
            {
                int n = Math.max(size - AJB_SHRINK_DECREMENT, AJB_MIN_SIZE);
                if (n < size)
                    shrink(n);
            }
        }

        BufferControl bc = getBufferControl();

        if (bc == null)
            return 0;
        else
        {
            Format format = stream.getFormat();
            long ms;
            int sizePerPkt = stats.getSizePerPacket();
            if (sizePerPkt <= 0)
                sizePerPkt = DEFAULT_AUD_PKT_SIZE;
            if (MPEG.matches(format))
                ms = sizePerPkt / 4;
            else
            {
                ms = DEFAULT_MS_PER_PKT;
                try
                {
                    long ns = buffer.getDuration();

                    if (ns <= 0)
                    {
                        ns
                            = ((AudioFormat) format).computeDuration(
                                    buffer.getLength());
                        if (ns > 0)
                            ms = ns / 1000000L;
                    }
                    else
                        ms = ns / 1000000L;
                }
                catch (Throwable t)
                {
                    if (t instanceof ThreadDeath)
                        throw (ThreadDeath) t;
                }
            }
            msPerPkt = (msPerPkt + ms) / 2;
            ms = (msPerPkt == 0) ? DEFAULT_MS_PER_PKT : msPerPkt;
            int aprxBufferLengthInPkts
                = (int) (bc.getBufferLength() / ms);

            // If the adaptive jitter buffer mode is enabled, we let this queue
            // manage its size, ignoring bc. Otherwise, we adapt to the value of
            // bc (which was the behavior before resizing based on the history
            // of late packets was introduced here).
            if (!AJB_ENABLED && (aprxBufferLengthInPkts > q.getCapacity()))
            {
                grow(aprxBufferLengthInPkts);
                int size = q.getCapacity();
                Log.comment(
                        "Grew audio RTP packet queue to: " + size + " pkts, "
                            + size * sizePerPkt + " bytes.\n");
            }
            return aprxBufferLengthInPkts;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preAdd(Buffer buffer, RTPRawReceiver rtprawreceiver)
    {
        long lastSeqSent = stream.getLastReadSequenceNumber();
        long bufferSN;

        if ((lastSeqSent != Buffer.SEQUENCE_UNKNOWN)
                && ((bufferSN = buffer.getSequenceNumber()) < lastSeqSent))
        {
            // A packet which is subsequent to the specified buffer has already
            // been read. It should be added to the history so that the queue
            // may be resized if necessary. But if it is late by more than
            // AJB_MAX_SIZE, it is too late to take it into account and,
            // consequently, is ignored.
            if(lastSeqSent - bufferSN < AJB_MAX_SIZE)
            {
                recordInHistory(true);
                stats.incrementDiscardedLate();
            }
            else
                stats.incrementDiscardedVeryLate();
            return false;
        }

        recordInHistory(false);

        if (!super.preAdd(buffer, rtprawreceiver))
            return false;

        // If the queue is full and it hasn't reached it's maximum size, grow
        // it. This is to adapt to groups of packets arriving in a short period
        // of time.

        // During the first few seconds after a stream is started, the queue is
        // often observed to be full. But this is likely not due to bursts of
        // packets from the network, so we shouldn't try to adapt. Hence the
        // INITIAL_PACKETS check.
        if (AJB_ENABLED
                && q.noMoreFree()
                && (stats.getNbAdd() > INITIAL_PACKETS))
        {
            int size = q.getCapacity();
            if (size < AJB_MAX_SIZE)
            {
                // There is still room for the queue to grow and to not drop
                // packets.
                grow(Math.min(size * 2, AJB_MAX_SIZE));
            }
            else
            {
                // The queue cannot grow any further so at least one packet has
                // to be dropped. However, dropping a single packet will very
                // likely be insufficient. In order to maximize the chances of
                // bettering the situation, re-center.
                while (q.getFillCount() >= (size / 2))
                {
                    stats.incrementDiscardedFull();
                    dropPkt();
                }
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void read(Buffer buffer)
    {
        super.read(buffer);

        if (!buffer.isDiscard() && skipFec)
        {
            buffer.setFlags(buffer.getFlags() | Buffer.FLAG_SKIP_FEC);
            skipFec = false;
        }

        int totalPkts = q.getFillCount();

        if (totalPkts == 0)
            replenish = true;
        if (totalPkts < AJB_SHRINK_THRESHOLD)
            shrinkCount = 0;
    }

    /**
     * Records a packet in <tt>history</tt>.
     *
     * @param late whether the packet arrived too late or not
     */
    private void recordInHistory(boolean late)
    {
        int n = late ? 1 : 0;

        growCount += n - history[historyTail];

        history[historyTail] = (byte) n;
        historyTail = (historyTail + 1 ) % AJB_GROW_INTERVAL;

        if (historyLength < AJB_GROW_INTERVAL)
            historyLength++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        super.reset();

        resetHistory();
    }

    /**
     * Resets the history.
     */
    private void resetHistory()
    {
        historyLength = 0;
        shrinkCount = 0;
    }

    /**
     * Resizes the queue to <tt>capacity</tt>. Assumes <tt>capacity</tt> is
     * not more than the current capacity of the queue. Drops packets if
     * necessary.
     */
    private void shrink(int capacity)
    {
        if (capacity < 1)
            throw new IllegalArgumentException("capacity");

        int qCapacity = q.getCapacity();

        if (capacity == qCapacity)
            return;
        if (capacity > qCapacity)
            throw new IllegalArgumentException("capacity");

        Log.info("Shrinking packet queue to " + capacity);

        int dropped = 0;

        while (q.getFillCount() > capacity)
        {
            dropPkt();
            stats.incrementDiscardedShrink();
            ++dropped;
        }
        q.setCapacity(capacity);

        // The shrinking as it is implemented at the time of this writing is
        // attempted in order to drop at least AJB_SHRINK_DECREMENT packets from
        // the queue.
        while ((dropped < AJB_SHRINK_DECREMENT) && q.fillNotEmpty())
        {
            dropPkt();
            stats.incrementDiscardedShrink();
            ++dropped;
        }

        resetHistory();
    }

    /**
     * @Override
     */
    @Override
    public boolean willReadBlock()
    {
        boolean b = super.willReadBlock();

        if (!b)
        {
            if (replenish && (q.getFillCount() >= (q.getCapacity() / 2)))
                replenish = false;
            b = replenish;
        }
        return b;
    }
}
