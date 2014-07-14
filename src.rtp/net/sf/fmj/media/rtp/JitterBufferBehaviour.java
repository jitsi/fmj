package net.sf.fmj.media.rtp;

import javax.media.*;

/**
 * Implements a jitter buffer in terms of behaviour, logic agnostic of the very
 * storage-related details and the simplest of RTP packet queuing specifics
 * which are abstracted by <tt>JitterBuffer</tt>.
 *
 * @author Lyubomir Marinov
 * @author Tom Denham
 */
interface JitterBufferBehaviour
{
    /**
     * Drops a packet from the associated <tt>JitterBuffer</tt>. Usually, the
     * dropped packet is the oldest (in terms of receipt).
     */
    void dropPkt();

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
     * Determines whether the jitter buffer logic implemented by this instance
     * exhibits adaptive (as opposed to fixed) behaviour.
     *
     * @return <tt>true</tt> if this instance implements the behaviour of an
     * adaptive jitter buffer or <tt>false</tt> if this instance implements the
     * behaviour of a fixed jitter buffer
     */
    boolean isAdaptive();

    /**
     * Invoked by {@link RTPSourceStream} after a specific <tt>Buffer</tt> has
     * been received and before it is added to the associated
     * <tt>JitterBuffer</tt>. Allows implementations to adapt the
     * <tt>JitterBuffer</tt> to the receipt of the specified <tt>buffer</tt> and
     * to optionally prevent its addition.
     *
     * @param buffer the <tt>Buffer</tt> which has been received and which is to
     * be added to the associated <tt>JitterBuffer</tt> if <tt>true</tt> is
     * returned
     * @param rtprawreceiver
     * @return <tt>true</tt> if the specified <tt>Buffer</tt> is to be added to
     * the associated <tt>JitterBuffer</tt>; otherwise, <tt>false</tt>
     */
    boolean preAdd(Buffer buffer, RTPRawReceiver rtprawreceiver);

    /**
     * Reads from the associated <tt>JitterBuffer</tt> and writes into the
     * specified <tt>Buffer</tt>.
     *
     * @param buffer the <tt>Buffer</tt> into which the media read from the
     * associated <tt>JitterBuffer</tt> is to be written
     */
    void read(Buffer buffer);

    /**
     * Notifies this instance that the associated <tt>RTPSourceStream</tt> has
     * been reset.
     */
    void reset();

    /**
     * Determines whether a subsequent invocation of {@link #read(Buffer)} on
     * this instance will block the calling/current thread.
     *
     * @return <tt>true</tt> if a subsequent invocation of <tt>read(Buffer)</tt>
     * on this instance will block the calling/current thread or <tt>false</tt>
     * if a packet may be read without blocking
     */
    boolean willReadBlock();
}
