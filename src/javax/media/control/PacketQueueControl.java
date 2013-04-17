package javax.media.control;

import javax.media.Control;

/**
 * Control for the packet queue
 *
 * @author Boris Grozev
 */
public interface PacketQueueControl extends Control
{
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
     * Returns the maximum size that the queue reached (in number of packets).
     *
     * @return the maximum size that the queue reached (in number of packets).
     */
    public int getMaxSizeReached();

    /**
     * Whether the adaptive jitter buffer mode is enabled.
     *
     * @return whether the adaptive jitter buffer mode is enabled.
     */
    public boolean isAdaptiveBufferEnabled();
}
