package net.sf.fmj.media.rtp;

import java.lang.ref.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.protocol.*;
import net.sf.fmj.media.protocol.rtp.DataSource;
import net.sf.fmj.media.rtp.util.*;

/**
 * Implements a <tt>PushBufferStream</tt> which represents a stream of RTP
 * packets being received by the local user/peer.
 *
 * @author Boris Grozev
 * @author Damian Minkov
 * @author Lyubomir Marinov
 */
public class RTPSourceStream
    extends BasicSourceStream
    implements PushBufferStream
{
    /**
     * The timeout in milliseconds to be used by the invocations of
     * {@link Object#wait(long)}.
     */
    private static final long WAIT_TIMEOUT = 100L;

    private BufferControlImpl bc;

    /**
     * The jitter buffer associated with this instance in terms of behaviour,
     * logic agnostic of the very storage-related details and the simplest of
     * RTP packet queuing specifics which are abstracted by {@link #q}.
     */
    private JitterBufferBehaviour behaviour;

    private boolean bufferWhenStopped = true;

    /**
     * The indicator which determines whether {@link #close()} has been invoked
     * without an intervening invocation of {@link #connect()}.
     */
    private boolean closed = false;

    /**
     * The indicator which determines whether {@link #close()} is executing.
     */
    private boolean closing = false;

    /**
     * The <tt>DataSource</tt> which has initialized and has this instance as
     * its <tt>sourceStream</tt>.
     */
    final DataSource datasource;

    private Format format;

    /**
     * The sequence number of the last <tt>Buffer</tt> added to this instance.
     */
    private long lastSeqRecv = Buffer.SEQUENCE_UNKNOWN;

    /**
     * Sequence number of the last <tt>Buffer</tt> read from this instance.
     */
    private long lastSeqSent = Buffer.SEQUENCE_UNKNOWN;

    /**
     * The RTP packet queue/jitter buffer which implements the storage of the
     * RTP packets added to and read from this <tt>RTPSourceStream</tt>.
     */
    final JitterBuffer q;

    /**
     * The <tt>Condition</tt> which is used for synchronization purposes instead
     * of synchronizing a block on {@link #q} because the latter is not flexible
     * enough for the thread complexity of <tt>JitterBuffer</tt>.
     */
    private final Condition qCondition;

    /**
     * The <tt>Lock</tt> which is used for synchronization purposes instead of
     * synchronizing a block on {@link #q} because the latter is not flexible
     * enough for the thread complexity of <tt>JitterBuffer</tt>.
     */
    private final Lock qLock;

    private boolean started = false;

    private final Object startSyncRoot = new Object();

    /**
     * The statistics related to the RTP packet queue/jitter buffer associated
     * with this <tt>RTPSourceStream</tt>. Implements {@link PacketQueueControl}
     * on behalf of this instance.
     */
    final JitterBufferStats stats;

    private Thread thread;

    /**
     * The (unique) reason for invoking
     * {@link BufferTransferHandler#transferData(PushBufferStream)} on
     * {@link #transferHandler}. Introduced in order to prevent busy waits when
     * there are enough packets to be read without blocking but no read is
     * actually performed.
     */
    private long transferDataReason;

    private BufferTransferHandler transferHandler;

    public RTPSourceStream(DataSource datasource)
    {
        datasource.setSourceStream(this);
        this.datasource = datasource;

        q = new JitterBuffer(4);

        qCondition = q.condition;
        qLock = q.lock;

        stats = new JitterBufferStats(this);

        // RTPSourceStream and its related classes assume that there is always a
        // JitterBufferBehaviour instance (in order to avoid null checks and for
        // the sake of simplicity). Make sure a default behaviour is initialized
        // until a specific Format is set on this instance.
        setBehaviour(null);
    }

    /**
     * Adds <tt>buffer</tt> to the queue.
     *
     * In case the queue is full: if <tt>buffer</tt>'s sequence number comes
     * before the sequence numbers of the <tt>Buffer</tt>s in the queue, nothing
     * is done. Otherwise, a packet is dropped using PktQue.dropPkt()
     *
     * @param buffer the buffer to add
     * @param flag unused
     * @param rtprawreceiver used to access the 'socket buffer'?
     */
    public void add(Buffer buffer, boolean flag, RTPRawReceiver rtprawreceiver)
    {
        if (!started && !bufferWhenStopped)
            return;

        long bufferSN = buffer.getSequenceNumber();

        // The access to lastSeqSent is synchronized because it is concurrently
        // modified by multiple threads. The access to started and
        // bufferWhenStopped above is usually synchronized on startReq so they
        // are left out to avoid synchronization on multiple monitors.
        qLock.lock();
        try
        {

        if (lastSeqRecv - bufferSN > 256L)
        {
            Log.info("Resetting queue, last seq added: " + lastSeqRecv +
                    ", current seq: " + bufferSN);
            reset();
            lastSeqRecv = bufferSN;
        }

        stats.updateMaxSizeReached();
        stats.updateSizePerPacket(buffer);
        if (!behaviour.preAdd(buffer, rtprawreceiver))
            return;

        stats.incrementNbAdd();
        lastSeqRecv = bufferSN;
        boolean almostFull = false;

        if (q.noMoreFree())
        {
            // The queue cannot accommodate the current packet so we have to
            // drop a packet.
            stats.incrementDiscardedFull();
            long l = q.getFirstSeq();
            if (l != Buffer.SEQUENCE_UNKNOWN && bufferSN < l)
            {
                // The current/received packet is the earliest. Drop it by
                // simply not adding it.
                return;
            }
            behaviour.dropPkt();
        }

        if (q.getFreeCount() <= 1)
            almostFull = true;
        Buffer qBuffer = q.getFree();
        boolean added = false;

        try
        {
            byte[] bufferData = (byte[]) buffer.getData();
            byte[] qBufferData = (byte[]) qBuffer.getData();
            if ((qBufferData == null)
                    || (qBufferData.length < bufferData.length))
            {
                qBufferData = new byte[bufferData.length];
            }

            System.arraycopy(
                    bufferData, buffer.getOffset(),
                    qBufferData, buffer.getOffset(),
                    buffer.getLength());
            qBuffer.copy(buffer);
            qBuffer.setData(qBufferData);
            if (almostFull) //with this packet added, the queue will be full
            {
                qBuffer.setFlags(
                        qBuffer.getFlags()
                            | Buffer.FLAG_BUF_OVERFLOWN
                            | Buffer.FLAG_NO_DROP);
            }
            else
            {
                qBuffer.setFlags(
                        qBuffer.getFlags() | Buffer.FLAG_NO_DROP);
            }

            q.addPkt(qBuffer);
            added = true;
        }
        finally
        {
            if (!added)
                q.returnFree(qBuffer);
        }

        // A packet was added to this PushBufferStream so transferData.
        ++transferDataReason;
        // Well, do not transferData as soon as possible if the read will block
        // but rather transferData as soon as the read will not block.
        if (!behaviour.willReadBlock())
            qCondition.signalAll();

        }
        finally
        {
            qLock.unlock();
        }
    }

    public void close()
    {
        synchronized (startSyncRoot)
        {
            if (closing)
            {
                return;
            }
            else
            {
                closing = true;
                thread = null;
            }
            startSyncRoot.notifyAll();
        }
        try
        {
            if (!closed)
            {
                closed = true;

                stats.printStats();
                stop();

                // A deadlock was observed in the implementation using
                // synchronized blocks on q and Object.notifyAll(). In order to
                // fix the deadlock, the implementation was changed to use Lock
                // and Condition instead. If the Lock is not free, it should not
                // matter much that no signaling on the Condition will be
                // performed because the waiting threads will time out anyway.
                if (qLock.tryLock())
                {
                    try
                    {
                        qCondition.signalAll();
                    }
                    finally
                    {
                        qLock.unlock();
                    }
                }

                if (bc != null)
                    bc.removeSourceStream(this);
            }
        }
        finally
        {
            synchronized (startSyncRoot)
            {
                closing = false;
                startSyncRoot.notifyAll();
            }
        }
    }

    public void connect()
    {
        synchronized (startSyncRoot)
        {
            waitWhileClosing();
            closed = false;
        }
    }

    /**
     * Gets the <tt>JitterBufferBehaviour</tt> which represents the behaviour
     * exhibited by/the logic of the jitter buffer/RTP packet queue associated
     * with this instance.
     *
     * @return the <tt>JitterBufferBehaviour</tt> which represents the behaviour
     * exhibited by/the logic of the jitter buffer/RTP packet queue associated
     * with this instance
     */
    JitterBufferBehaviour getBehaviour()
    {
        return behaviour;
    }

    /**
     * Gets the <tt>BufferControlImpl</tt> set on this instance.
     *
     * @return the <tt>BufferControlImpl</tt> set on this instance
     */
    BufferControlImpl getBufferControl()
    {
        return bc;
    }

    /**
     * {@inheritDoc}
     *
     * Adds support for {@link PacketQueueControl}.
     */
    @Override
    public Object getControl(String controlType)
    {
        return
            JitterBufferControl.class.getName().equals(controlType)
                ? stats
                : super.getControl(controlType);
    }

    /**
     * {@inheritDoc}
     *
     * Adds support for {@link PacketQueueControl}.
     */
    @Override
    public Object[] getControls()
    {
        Object[] superControls = super.getControls();
        Object[] thisControls = new Object[superControls.length + 1];

        System.arraycopy(
                superControls, 0,
                thisControls, 0,
                superControls.length);
        thisControls[superControls.length] = stats;
        return thisControls;
    }

    @Override
    public Format getFormat()
    {
        return format;
    }

    /**
     * Gets the (RTP) sequence number of the last <tt>Buffer</tt> read out of
     * this <tt>SourceStream</tt>.
     *
     * @return the (RTP) sequence number of the last <tt>Buffer</tt> read out of
     * this <tt>SourceStream</tt>
     */
    long getLastReadSequenceNumber()
    {
        return lastSeqSent;
    }

    public void prebuffer()
    {
    }

    /**
     * Pops an element off the queue and copies it to <tt>buffer</tt>. The data
     * and header arrays of <tt>buffer</tt> are reused.
     *
     * @param buffer The <tt>Buffer</tt> object to copy an element of the queue
     * to.
     */
    @Override
    public void read(Buffer buffer)
    {
        // The access to lastSeqSent is synchronized because it is concurrently
        // modified by multiple threads.
        qLock.lock();
        try
        {
            try
            {
                behaviour.read(buffer);

                if (!buffer.isDiscard())
                    lastSeqSent = buffer.getSequenceNumber();
            }
            finally
            {
                // If a packet was read, schedule a transferData as soon as
                // possible in case there are more packets to be read.
                if (!buffer.isDiscard())
                {
                    ++transferDataReason;
                    qCondition.signalAll();
                }
            }
        }
        finally
        {
            qLock.unlock();
        }
    }

    /**
     * Resets the queue, dropping all packets.
     */
    public void reset()
    {
        // The access to lastSeqSent is synchronized because it is concurrently
        // modified by multiple threads.
        qLock.lock();
        try
        {
            stats.incrementNbReset();
            resetQ();
            behaviour.reset();
            lastSeqSent = Buffer.SEQUENCE_UNKNOWN;
        }
        finally
        {
            qLock.unlock();
        }
    }

    /**
     * Empties the queue by dropping all packets.
     */
    public void resetQ()
    {
        Log.comment("Resetting the RTP packet queue");
        qLock.lock();
        try
        {
            while (q.fillNotEmpty())
            {
                behaviour.dropPkt();
                stats.incrementDiscardedReset();
            }
            // All packets which could be read were dropped so there is hardly
            // any reason to transferData (as soon as possible).
            qCondition.signalAll();
        }
        finally
        {
            qLock.unlock();
        }
    }

    /**
     * Runs in {@link #thread}.
     *
     * @param runnable the <tt>TransferDataRunnable</tt> which is running in
     * the current thread
     * @return <tt>true</tt> if the current thread is to continue invoking the
     * method; otherwise, <tt>false</tt>
     */
    private boolean runInThread(TransferDataRunnable runnable)
    {
        synchronized (startSyncRoot)
        {
            // Is this RTPSourceStream still utilizing the current thread?
            if (!Thread.currentThread().equals(thread) || closing || closed)
            {
                return false;
            }
            // Has this RTPSourceStream been started?
            if (!started)
            {
                try
                {
                    startSyncRoot.wait(WAIT_TIMEOUT);
                }
                catch (InterruptedException ie)
                {
                }
                return true;
            }
        }

        // This RTPSourceStream has been started and may or may not have been
        // stopped and/or closed afterwards.
        BufferTransferHandler transferHandler = null;

        qLock.lock();
        try
        {
            boolean wait;

            if (behaviour.willReadBlock())
            {
                // Obviously, do not transferData because the read will block.
                wait = true;
            }
            else if (runnable.transferDataReason == transferDataReason)
            {
                // There was an invocation of transferData for that particular
                // reason.
                wait = true;
            }
            else
            {
                transferHandler = this.transferHandler;
                if (transferHandler == null)
                {
                    // It is impossible to transferData because there is no
                    // object on which to invoke it.
                    wait = true;
                }
                else
                {
                    // There is going to be an invocation of transferData bellow
                    // and it is going to be for that particular reason.
                    wait = false;
                    runnable.transferDataReason = transferDataReason;
                }
            }
            if (wait)
            {
                try
                {
                    qCondition.await(WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException ie)
                {
                }
                return true;
            }
        }
        finally
        {
            qLock.unlock();
        }

        if (transferHandler != null)
            transferHandler.transferData(this);

        return true;
    }

    /**
     * Sets a <tt>JitterBufferBehaviour</tt> which represents the behaviour to
     * be exhibited by/the logic of the jitter buffer/RTP packet queue
     * associated with this instance.
     *
     * @param behaviour the <tt>JitterBufferBehaviour</tt> which represents the
     * behaviour to be exhibited by the jitter buffer/RTP packet queue
     * associated with this instance. If <tt>null</tt>, the implementation
     * defaults to <tt>BasicJitterBufferBehaviour</tt>.
     */
    private void setBehaviour(JitterBufferBehaviour behaviour)
    {
        // In order to avoid null checks, RTPSourceStream and its related
        // classes assume that there is always a JitterBufferBehaviour instance.
        // Default to BasicJitterBufferBehaviour.
        if (behaviour == null)
        {
            if (this.behaviour instanceof BasicJitterBufferBehaviour)
                return;
            else
                behaviour = new BasicJitterBufferBehaviour(this);
        }

        this.behaviour = behaviour;
    }

    public void setBufferControl(BufferControl buffercontrol)
    {
        bc = (BufferControlImpl) buffercontrol;
        updateBuffer(bc.getBufferLength());
        updateThreshold(bc.getMinimumThreshold());
    }

    public void setBufferListener(BufferListener bufferlistener)
    {
    }

    public void setBufferWhenStopped(boolean flag)
    {
        bufferWhenStopped = flag;
    }

    void setContentDescriptor(String s)
    {
        contentDescriptor = new ContentDescriptor(s);
    }

    protected void setFormat(Format format)
    {
        if (this.format != format)
        {
            this.format = format;

            // The jitter buffer/RTP packet queue associated with
            // RTPSourceStream behaves in accord with the Format of the media.
            JitterBufferBehaviour behaviour;

            if (this.format instanceof AudioFormat)
                behaviour = new AudioJitterBufferBehaviour(this);
            else if (this.format instanceof VideoFormat)
                behaviour = new VideoJitterBufferBehaviour(this);
            else
                behaviour = null;
            setBehaviour(behaviour);
        }
    }

    @Override
    public void setTransferHandler(BufferTransferHandler transferHandler)
    {
        this.transferHandler = transferHandler;
    }

    public void start()
    {
        Log.info("Starting RTPSourceStream.");
        synchronized (startSyncRoot)
        {
            started = true;
            startThread();
            startSyncRoot.notifyAll();
        }

        // A deadlock was observed in the implementation using synchronized
        // blocks on q and Object.notifyAll(). In order to fix the deadlock, the
        // implementation was changed to use Lock and Condition instead. If the
        // Lock is not free, it should not matter much that no signaling on the
        // Condition will be performed because the waiting threads will time out
        // anyway.
        if (qLock.tryLock())
        {
            try
            {
                qCondition.signalAll();
            }
            finally
            {
                qLock.unlock();
            }
        }
    }

    /**
     * Initializes and starts {@link #thread} if it has not been initialized and
     * started yet.
     */
    private void startThread()
    {
        synchronized (startSyncRoot)
        {
            waitWhileClosing();
            if ((this.thread == null) && !closed)
            {
                RTPMediaThread thread
                    = new RTPMediaThread(
                            new TransferDataRunnable(this),
                            RTPSourceStream.class.getName());

                thread.setDaemon(true);
                thread.useControlPriority();

                boolean started = false;

                this.thread = thread;
                try
                {
                    thread.start();
                    started = true;
                }
                finally
                {
                    if (!started && thread.equals(this.thread))
                        this.thread = null;
                }
            }

            startSyncRoot.notifyAll();
        }
    }

    public void stop()
    {
        Log.info("Stopping RTPSourceStream.");
        synchronized (startSyncRoot)
        {
            started = false;
            startSyncRoot.notifyAll();
            if (!bufferWhenStopped)
                reset();
        }

        // A deadlock was observed in the implementation using synchronized
        // blocks on q and Object.notifyAll(). In order to fix the deadlock, the
        // implementation was changed to use Lock and Condition instead. If the
        // Lock is not free, it should not matter much that no signaling on the
        // Condition will be performed because the waiting threads will time out
        // anyway.
        if (qLock.tryLock())
        {
            try
            {
                qCondition.signalAll();
            }
            finally
            {
                qLock.unlock();
            }
        }
    }

    /**
     * Notifies this <tt>RTPSourceStream</tt> that its {@link #thread} may have
     * exited.
     *
     * @param runnable the <tt>TransferDataRunnable</tt> which has exited
     */
    private void threadExited(TransferDataRunnable runnable)
    {
        // The current thread cannot be utilized by this RTPSourceStream any
        // longer.
        synchronized (startSyncRoot)
        {
            if (Thread.currentThread().equals(thread))
            {
                thread = null;
                startSyncRoot.notifyAll();
            }
        }
    }

    public long updateBuffer(long l)
    {
        return l;
    }

    public long updateThreshold(long l)
    {
        return l;
    }

    /**
     * Wait on {@link #startSyncRoot} while {@link #closing} equals
     * <tt>true</tt> i.e. wait on <tt>startSyncRoot</tt> until <tt>false</tt> is
     * set on <tt>closing</tt>.
     */
    private void waitWhileClosing()
    {
        boolean interrupted = false;

        while (closing)
        {
            try
            {
                startSyncRoot.wait();
            }
            catch (InterruptedException ie)
            {
                interrupted = true;
            }
        }
        if (interrupted)
            Thread.currentThread().interrupt();
    }

    /**
     * Implements <tt>Runnable</tt> which is to run in
     * {@link RTPSourceStream#thread} in order to transfer data out of the
     * <tt>RTPSourceStream</tt> while, optionally, keeping a
     * <tt>WeakReference</tt> to the <tt>RTPSourceStream</tt>.
     *
     * @author Lyubomir Marinov
     */
    private static class TransferDataRunnable
        implements Runnable
    {
        /**
         * The indicator which determines whether <tt>TransferDataRunnable</tt>
         * keeps a <tt>WeakReference</tt> to the associated
         * <tt>RTPSourceStream</tt>.
         */
        private static final boolean WEAK_REFERENCE = false;

        /**
         * The <tt>RTPSourceStream</tt> which has initialized and owns this
         * instance.
         */
        private final RTPSourceStream owner;

        /**
         * The (unique) reason for invoking
         * {@link BufferTransferHandler#transferData(PushBufferStream)} on
         * {@link #owner}. Introduced in order to prevent busy waits when there
         * are enough packets to be read without blocking but no read is
         * actually performed.
         */
        private long transferDataReason;

        /**
         * A <tt>WeakReference</tt> to {@link #owner}.
         */
        private final WeakReference<RTPSourceStream> weakReference;

        /**
         * Initializes a new <tt>TransferDataRunnable</tt> instance which is to
         * transfer data out of a specific <tt>RTPSourceStream</tt>.
         *
         * @param owner the <tt>RTPSourceStream</tt> which is initializing the
         * new instance
         */
        public TransferDataRunnable(RTPSourceStream owner)
        {
            if (WEAK_REFERENCE)
            {
                this.owner = null;
                this.weakReference = new WeakReference<RTPSourceStream>(owner);
            }
            else
            {
                this.owner = owner;
                this.weakReference = null;
            }
        }

        /**
         * Gets the <tt>RTPSourceStream</tt> which has initialized and owns this
         * instance.
         *
         * @return the <tt>RTPSourceStream</tt> which has initialized and owns
         * this instance
         */
        private RTPSourceStream getOwner()
        {
            return WEAK_REFERENCE ? weakReference.get() : owner;
        }

        @Override
        public void run()
        {
            try
            {
                do
                {
                    RTPSourceStream owner = getOwner();

                    if ((owner == null) || !owner.runInThread(this))
                        break;
                }
                while (true);
            }
            finally
            {
                RTPSourceStream owner = getOwner();

                if (owner != null)
                    owner.threadExited(this);
            }
        }
    }
}
