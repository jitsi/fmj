package net.sf.fmj.media.rtp;

import javax.media.*;
import javax.media.control.*;

import net.sf.fmj.media.*;

/**
 * Implements a basic <tt>JitterBufferBehaviour</tt> which is not adaptive, does
 * not perform buffering beyond the one performed by the associated
 * <tt>JitterBuffer</tt> and is agnostic of the <tt>Format</tt> of the received
 * media data. The implementation may be used by extenders to facilitate the
 * implementation of the <tt>JitterBufferBehaviour</tt> interface.
 *
 * @author Lyubomir Marinov
 */
class BasicJitterBufferBehaviour
    implements JitterBufferBehaviour
{
    /**
     * The RTP packet queue/jitter buffer which implements the storage of the
     * RTP packets added to and read from {@link #stream}.
     */
    protected final JitterBuffer q;

    /**
     * The value which has been applied by this instance with an invocation of
     * {@link RTPRawReceiver#setRecvBufSize(int)}.
     */
    private int recvBufSize;

    /**
     * The statistics related to the RTP packet queue/jitter buffer associated
     * with {@link #stream}.
     */
    protected final JitterBufferStats stats;

    /**
     * The <tt>RTPSourceStream</tt> which has initialized this instance.
     */
    protected final RTPSourceStream stream;

    /**
     * Initializes a new <tt>BasicJitterBufferBehaviour</tt> instance for the
     * purposes of a specific <tt>RTPSourceStream</tt>.
     *
     * @param stream the <tt>RTPSourceStream</tt> which has requested the
     * initialization of the new instance
     */
    protected BasicJitterBufferBehaviour(RTPSourceStream stream)
    {
        this.stream = stream;

        this.q = this.stream.q;
        this.stats = this.stream.stats;
    }

    /**
     * Removes the first element (the one with the least sequence number)
     * from <tt>fill</tt> and releases it to be reused (adds it to
     * <tt>free</tt>)
     */
    protected void dropFirstPkt()
    {
        q.dropFirstFill();
    }

    /**
     * Removes an element from the queue and releases it to be reused.
     */
    public void dropPkt()
    {
        dropFirstPkt();
    }

    /**
     * {@inheritDoc}
     *
     * <tt>BasicJitterBufferBehaviour</tt> implements a fixed jitter buffer and,
     * consequently, returns {@link #getMaximumDelay()}.
     */
    @Override
    public int getAbsoluteMaximumDelay()
    {
        return getMaximumDelay();
    }

    /**
     * Gets the <tt>BufferControl</tt> implementation set on the associated
     * <tt>RTPSourceStream</tt>. Provided as a convenience which delegates to
     * {@link RTPSourceStream#getBufferControl()}.
     *
     * @return the <tt>BufferControl</tt> implementation set on the associated
     * <tt>RTPSourceStream</tt>
     */
    protected BufferControl getBufferControl()
    {
        return stream.getBufferControl();
    }

    /**
     * {@inheritDoc}
     *
     * <tt>BasicJitterBufferBehaviour</tt> does not have a notion of RTP packet
     * duration and, consequently, returns <tt>65535</tt>.
     */
    @Override
    public int getMaximumDelay()
    {
        return 65535;
    }

    /**
     * {@inheritDoc}
     *
     * <tt>BasicJitterBufferBehaviour</tt> does not have a notion of RTP packet
     * duration and, consequently, returns <tt>0</tt>.
     */
    @Override
    public int getNominalDelay()
    {
        return 0;
    }

    /**
     * Grows {@link #q} to a specific <tt>capacity</tt>.
     *
     * @param capacity the capacity to set on <tt>q</tt>
     * @throws IllegalArgumentException if the specified <tt>capacity</tt> is
     * less than the capacity of <tt>q</tt>
     */
    protected void grow(int capacity)
    {
        if (capacity < 1)
            throw new IllegalArgumentException("capacity");

        int qCapacity = q.getCapacity();

        if (capacity == qCapacity)
            return;
        if (capacity < qCapacity)
            throw new IllegalArgumentException("capacity");

        Log.info("Growing packet queue to " + capacity);
        stats.incrementNbGrow();
        q.setCapacity(capacity);
    }

    /**
     * {@inheritDoc}
     *
     * <tt>BasicJitterBufferBehaviour</tt> always returns <tt>false</tt> to
     * indicate that it implements a fixed jitter buffer/RTP packet queue.
     */
    public boolean isAdaptive()
    {
        return false;
    }

    /**
     * Allows extenders to adapt the size/capacity of the associated RTP packet
     * queue/<tt>JitterBuffer</tt> after a specific <tt>Buffer</tt> is received
     * and before it is added to the <tt>JitterBuffer</tt>.
     *
     * @param buffer the <tt>Buffer</tt> which has been received and is to be
     * added (after the method returns)
     * @return the approximate length in packets of the buffering performed by
     * this <tt>JitterBufferBehaviour</tt> and the associated
     * <tt>JitterBuffer</tt>. <tt>BasicJitterBufferBehaviour</tt> always returns
     * <tt>0</tt>.
     */
    protected int monitorQSize(Buffer buffer)
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * Maintains an average approximation of the size in bytes of an RTP packet
     * in the <tt>JitterBufferStats</tt> of the associated
     * <tt>RTPSourceStream</tt> and updates the <tt>recvBufSize</tt> of the
     * specified <tt>rtprawreceiver</tt>.
     */
    public boolean preAdd(Buffer buffer, RTPRawReceiver rtprawreceiver)
    {
        stats.updateSizePerPacket(buffer);

        int aprxBufferLengthInPkts = monitorQSize(buffer);

        if (aprxBufferLengthInPkts > 0)
            setRecvBufSize(rtprawreceiver, aprxBufferLengthInPkts);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void read(Buffer buffer)
    {
        if (q.getFillCount() == 0)
        {
            buffer.setDiscard(true);
        }
        else
        {
            Buffer bufferFromQueue = q.getFill();

            // Whatever follows, it sounds safer to return the bufferFromQueue
            // into the free pool eventually.
            try
            {
                // Copy the bufferFromQueue into the specified (output) buffer.
                Object bufferData = buffer.getData();
                Object bufferHeader = buffer.getHeader();

                buffer.copy(bufferFromQueue);
                bufferFromQueue.setData(bufferData);
                bufferFromQueue.setHeader(bufferHeader);
            }
            finally
            {
                q.returnFree(bufferFromQueue);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reset()
    {
    }

    protected void setRecvBufSize(
            RTPRawReceiver rtprawreceiver,
            int aprxBufferLengthInPkts)
    {
        int sizePerPkt = stats.getSizePerPacket();

        // There was no comment and the variables did not use meaningful names
        // at the time the following code was initially written. Consequently,
        // it is not immediately obvious why it is necessary at all and it may
        // be hard to understand. A possible explanation may be that, since the
        // threshold value will force a delay with a specific duration/byte
        // size, we should better be able to hold on to that much in the socket
        // so that it does not throw the delayed data away.
        int aprxThresholdInBytes
            = (aprxBufferLengthInPkts * sizePerPkt) / 2;

        if ((rtprawreceiver != null)
                && (aprxThresholdInBytes > this.recvBufSize))
        {
            rtprawreceiver.setRecvBufSize(aprxThresholdInBytes);

            int recvBufSize = rtprawreceiver.getRecvBufSize();

            this.recvBufSize
                = (recvBufSize < aprxThresholdInBytes)
                    ? 0x7fffffff /* BufferControlImpl.NOT_SPECIFIED? */
                    : aprxThresholdInBytes;
            Log.comment(
                    "RTP socket receive buffer size: " + recvBufSize
                        + " bytes.\n");
        }
    }

    /**
     * {@inheritDoc}
     *
     * <tt>BasicJitterBufferBehaviour</tt> returns <tt>true</tt> if the
     * associated RTP packet queue/jitter buffer is empty; otherwise,
     * <tt>false</tt>
     */
    public boolean willReadBlock()
    {
        return q.noMoreFill();
    }
}
