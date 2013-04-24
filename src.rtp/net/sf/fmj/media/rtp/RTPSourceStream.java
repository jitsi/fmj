package net.sf.fmj.media.rtp;

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
    implements PushBufferStream, Runnable
{
    private BufferControlImpl bc = null;

    /**
     * The jitter buffer associated with this instance in terms of behaviour,
     * logic agnostic of the very storage-related details and the simplest of
     * RTP packet queuing specifics which are abstracted by {@link #q}.
     */
    private JitterBufferBehaviour behaviour;

    private boolean bufferWhenStopped = true;

    private Format format;

    private boolean hasRead = false;

    private boolean killed = false;
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

    private boolean started = false;

    private final Object startSyncRoot = new Object();

    /**
     * The statistics related to the RTP packet queue/jitter buffer associated
     * with this <tt>RTPSourceStream</tt>. Implements {@link PacketQueueControl}
     * on behalf of this instance.
     */
    final JitterBufferStats stats;

    private RTPMediaThread thread;

    private BufferTransferHandler transferHandler;

    public RTPSourceStream(DataSource datasource)
    {
        datasource.setSourceStream(this);

        q = new JitterBuffer(4);
        stats = new JitterBufferStats(this);

        /*
         * RTPSourceStream and its related classes assume that there is always
         * a JitterBufferBehaviour instance (in order to avoid null checks and
         * for the sake of simplicity). Make sure a default behaviour is
         * initialized until a specific Format is set on this instance.
         */
        setBehaviour(null);

        createThread();
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

        /*
         * The access to lastSeqSent is synchronized because it is concurrently
         * modified by multiple threads. The access to started and
         * bufferWhenStopped above is usually synchronized on startReq so they
         * are left out to avoid synchronization on multiple monitors.
         */
        synchronized (q)
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
            /*
             * The queue cannot accommodate the current packet so we have to
             * drop a packet.
             */
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
            byte bufferData[] = (byte[]) buffer.getData();
            byte qBufferData[] = (byte[]) qBuffer.getData();
            if ((qBufferData == null)
                    || (qBufferData.length < bufferData.length))
                qBufferData = new byte[bufferData.length];
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

        if (!behaviour.willReadBlock())
            q.notifyAll();

        } /* synchronized (q) */
    }

    public void close()
    {
        if (killed)
            return;
        stats.printStats();
        stop();
        killed = true;
        synchronized (startSyncRoot)
        {
            startSyncRoot.notifyAll();
        }
        synchronized (q)
        {
            q.notifyAll();
        }
        thread = null;
        if (bc != null)
            bc.removeSourceStream(this);
    }

    public void connect()
    {
        killed = false;
        createThread();
    }

    private void createThread()
    {
        if (thread == null)
        {
            thread = new RTPMediaThread(this, "RTPStream");
            thread.useControlPriority();
            thread.start();
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
            PacketQueueControl.class.getName().equals(controlType)
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
        // TODO Auto-generated method stub
    }

    /**
     * Pops an element off the queue and copies it to <tt>buffer</tt>. The data
     * and header arrays of <tt>buffer</tt> are reused.
     *
     * @param buffer The <tt>Buffer</tt> object to copy an element of the queue
     * to.
     */
    public void read(Buffer buffer)
    {
        /*
         * The access to lastSeqSent is synchronized because it is concurrently
         * modified by multiple threads.
         */
        synchronized (q)
        {
            try
            {
                behaviour.read(buffer);

                if (!buffer.isDiscard())
                    lastSeqSent = buffer.getSequenceNumber();
            }
            finally
            {
                if (!buffer.isDiscard())
                {
                    hasRead = true;
                    q.notifyAll();
                }
            }
        }
    }

    /**
     * Resets the queue, dropping all packets.
     */
    public void reset()
    {
        /*
         * The access to lastSeqSent is synchronized because it is concurrently
         * modified by multiple threads.
         */
        synchronized (q)
        {
            stats.incrementNbReset();
            resetQ();
            behaviour.reset();
            lastSeqSent = Buffer.SEQUENCE_UNKNOWN;
        }
    }

    /**
     * Empties the queue by dropping all packets.
     */
    public void resetQ()
    {
        Log.comment("Resetting the RTP packet queue");
        synchronized (q)
        {
            for (; q.fillNotEmpty(); behaviour.dropPkt())
                stats.incrementDiscardedReset();
            q.notifyAll();
        }
    }

    public void run()
    {
        do
        {
            try
            {
                synchronized (startSyncRoot)
                {
                    if (!killed && !started)
                    {
                        startSyncRoot.wait();
                        continue;
                    }
                }
                synchronized (q)
                {
                    if (!killed && !hasRead && behaviour.willReadBlock())
                    {
                        q.wait();
                        continue;
                    }

                    hasRead = false;
                }

                BufferTransferHandler transferHandler = this.transferHandler;

                if (transferHandler != null)
                    transferHandler.transferData(this);
            }
            catch (InterruptedException ie)
            {
                Log.error("Thread " + ie.getMessage());
            }
        }
        while (!killed);
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
        /*
         * In order to avoid null checks, RTPSourceStream and its related
         * classes assume that there is always a JitterBufferBehaviour instance.
         * Default to BasicJitterBufferBehaviour.
         */
        if (behaviour == null)
        {
            if (this.behaviour instanceof BasicJitterBufferBehaviour)
                return;
            else
                behaviour = new BasicJitterBufferBehaviour(this);
        }

        if (this.behaviour != behaviour)
        {
            // TODO Auto-generated method stub
            this.behaviour = behaviour;
        }
    }

    public void setBufferControl(BufferControl buffercontrol)
    {
        bc = (BufferControlImpl) buffercontrol;
        updateBuffer(bc.getBufferLength());
        updateThreshold(bc.getMinimumThreshold());
    }

    public void setBufferListener(BufferListener bufferlistener)
    {
        // TODO Auto-generated method stub
    }

    public void setBufferWhenStopped(boolean flag)
    {
        bufferWhenStopped = flag;
    }

    void setContentDescriptor(String s)
    {
        super.contentDescriptor = new ContentDescriptor(s);
    }

    protected void setFormat(Format format)
    {
        if (this.format != format)
        {
            this.format = format;

            /*
             * The jitter buffer/RTP packet queue associated with
             * RTPSourceStream behaves in accord with the Format of the media.
             */
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
            startSyncRoot.notifyAll();
        }
        synchronized (q)
        {
            q.notifyAll();
        }
    }

    public void stop()
    {
        Log.info("Stopping RTPSourceStream.");
        synchronized (startSyncRoot)
        {
            started = false;
            if (!bufferWhenStopped)
                reset();
        }
        synchronized (q)
        {
            q.notifyAll();
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
}
