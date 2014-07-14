package javax.media.control;

import javax.media.*;

/**
 * Control for the packet queue
 *
 * @author Boris Grozev
 * @author Lyubomir Marinov
 */
public interface JitterBufferControl extends Control
{
    /**
     * Gets the absolute maximum delay in milliseconds that an adaptive jitter
     * buffer can reach under worst case conditions. If this value exceeds 65535
     * milliseconds, then 65535 shall be returned. Returns <tt>maximumDelay</tt>
     * for a fixed jitter buffer implementation.
     *
     * @return the absolute maximum delay in milliseconds that an adaptive
     * jitter buffer can reach under worst case conditions
     */
    int getAbsoluteMaximumDelay();

    /**
     * Returns the current approximate delay in milliseconds that the queue
     * introduces.
     *
     * @return the current approximate delay in milliseconds that the queue
     * introduces.
     */
    public int getCurrentDelayMs();

    /**
     * Returns the current approximate delay in number of packets that the queue
     * introduces.
     *
     * @return the current approximate delay in number of packets that the queue
     * introduces.
     */
    public int getCurrentDelayPackets();

    /**
     * Returns the number of elements currently in the queue
     */
    public int getCurrentPacketCount();

    /**
     *  Returns the current size of the queue in packets.
     *
     * @return the current size of the queue in packets.
     */
    public int getCurrentSizePackets();

    /**
     * Returns the total number of packets discarded by the queue.
     *
     * @return the total number of packets discarded by the queue.
     */
    public int getDiscarded();

    /**
     * Returns the number of packets discarded by the queue because it was full.
     *
     * @return the number of packets discarded by the queue because it was full.
     */
    public int getDiscardedFull();

    /**
     * Returns the number of packets discarded by the queue because they were
     * too late.
     *
     * @return the number of packets discarded by the queue because they were
     * too late.
     */
    public int getDiscardedLate();

    /**
     * Returns the number of packets discarded by the queue due to resetting.
     *
     * @return the number of packets discarded by the queue due to resetting.
     */
    public int getDiscardedReset();

    /**
     * Returns the number of packets discarded by the queue while shrinking.
     *
     * @return the number of packets discarded by the queue while shrinking.
     */
    public int getDiscardedShrink();

    /**
     * Gets the current maximum jitter buffer delay in milliseconds which
     * corresponds to the earliest arriving packet that would not be discarded.
     * In simple queue implementations it may correspond to the nominal size. In
     * adaptive jitter buffer implementations, the value may dynamically vary up
     * to <tt>absoluteMaximumDelay</tt>.
     *
     * @return the current maximum jitter buffer delay in milliseconds which
     * corresponds to the earliest arriving packet that would not be discarded
     */
    int getMaximumDelay();

    /**
     * Returns the maximum size that the queue reached (in number of packets).
     *
     * @return the maximum size that the queue reached (in number of packets).
     */
    public int getMaxSizeReached();

    /**
     * Gets the current nominal jitter buffer delay in milliseconds, which
     * corresponds to the nominal jitter buffer delay for packets that arrive
     * exactly on time.
     *
     * @return the current nominal jitter buffer delay in milliseconds, which
     * corresponds to the nominal jitter buffer delay for packets that arrive
     * exactly on time
     */
    int getNominalDelay();

    /**
     * Whether the adaptive jitter buffer mode is enabled.
     *
     * @return whether the adaptive jitter buffer mode is enabled.
     */
    public boolean isAdaptiveBufferEnabled();
}
