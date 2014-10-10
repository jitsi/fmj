package net.sf.fmj.media.rtp;

import java.util.concurrent.locks.*;

import javax.media.*;

/**
 * Implements an RTP packet queue and the storage-related functionality of a
 * jitter buffer for the purposes of {@link RTPSourceStream}. The effect of a
 * complete jitter buffer is achieved through the combined use of
 * <tt>JitterBuffer</tt> and <tt>JitterBufferBehaviour</tt>.
 *
 * @author Lyubomir Marinov
 */
class JitterBuffer
{
    /**
     * The capacity of this instance in terms of the maximum number of
     * <tt>Buffer</tt>s that it may contain.
     */
    private int capacity;

    /**
     * The <tt>Condition</tt> which is used for synchronization purposes instead
     * of synchronizing a block on this instance because the latter is not
     * flexible enough for the thread complexity of <tt>JitterBuffer</tt>.
     */
    final Condition condition;

    /**
     * The <tt>Buffer</tt>s of this <tt>JitterBuffer</tt> which may contain
     * valid media data to be read out of this instance (referred to as
     * &quot;fill&quot;) or may represent preallocated <tt>Buffer</tt> instances
     * for the purposes of reducing the effects of allocation and garbage
     * collection (referred to as &quot;free&quot;). The storage is of a
     * circular nature with the first &quot;fill&quot; at index {@link #offset}
     * and the number of &quot;fill&quot; equal to {@link #length}.
     */
    private Buffer[] elements;

    /**
     * The number of &quot;fill&quot; <tt>Buffer</tt>s in {@link #elements}.
     */
    private int length;

    /**
     * The <tt>Lock</tt> which is used for synchronization purposes instead of
     * synchronizing a block on this instance because the latter is not flexible
     * enough for the thread complexity of <tt>JitterBuffer</tt>.
     */
    final Lock lock;

    /**
     * The index in {@link #elements} of the <tt>Buffer</tt>, if any, which has
     * been retrieved from this queue and has not been returned yet.
     */
    private int locked;

    /**
     * The index in {@link #elements} of the first &quot;fill&quot;
     * <tt>Buffer</tt>.
     */
    private int offset;

    /**
     * Initializes a new <tt>JitterBuffer</tt> instance with a specific capacity
     * of <tt>Buffer</tt>s.
     *
     * @param capacity the capacity of the new instance in terms of number of
     * <tt>Buffer</tt>s
     */
    public JitterBuffer(int capacity)
    {
        if (capacity < 1)
            throw new IllegalArgumentException("capacity");

        elements = new Buffer[capacity];
        for (int i = 0; i < elements.length; i++)
            elements[i] = new Buffer();

        this.capacity = capacity;

        length = 0;
        locked = -1;
        offset = 0;

        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    /**
     * Inserts <tt>buffer</tt> in its proper place in this queue according
     * to its sequence number. The elements are always kept in ascending
     * order by sequence number.
     *
     * TODO: Check for duplicate packets
     *
     * @param buffer the <tt>Buffer</tt> to insert in this queue
     * @see #insert(Buffer)
     */
    public void addPkt(Buffer buffer)
    {
        assertLocked(buffer);
        if (noMoreFree())
            throw new IllegalStateException("noMoreFree");

        long firstSN = getFirstSeq();
        long lastSN = getLastSeq();
        long bufferSN = buffer.getSequenceNumber();

        if (firstSN == Buffer.SEQUENCE_UNKNOWN
                && lastSN == Buffer.SEQUENCE_UNKNOWN)
            append(buffer);
        else if (bufferSN < firstSN)
            prepend(buffer);
        else if (firstSN < bufferSN && bufferSN < lastSN)
            insert(buffer);
        else if (bufferSN > lastSN)
            append(buffer);
        else //only if (bufferSN == firstSN) || (bufferSN == lastSN)?
            returnFree(buffer);

        locked = -1;
    }

    /**
     * Adds <tt>buffer</tt> to the end of this queue.
     *
     * @param buffer the <tt>Buffer</tt> to be added to the end of this
     * queue
     */
    private void append(Buffer buffer)
    {
        int index = (offset + length) % capacity;

        if (index != locked)
        {
            elements[locked] = elements[index];
            elements[index] = buffer;
        }
        length++;
    }

    /**
     * Asserts that a <tt>Buffer</tt> has been retrieved from this
     * <tt>JitterBuffer</tt> and has not been returned yet.
     *
     * @throws IllegalStateException if no <tt>Buffer</tt> has been retrieved
     * from this <tt>JitterBuffer</tt> and has not been returned yet
     */
    private void assertLocked(Buffer buffer)
        throws IllegalStateException
    {
        if (locked == -1)
        {
            throw new IllegalStateException(
                    "No Buffer has been retrieved from this JitterBuffer"
                        + " and has not been returned yet.");
        }
        if (buffer != elements[locked])
            throw new IllegalArgumentException("buffer");
    }

    /**
     * Asserts that no <tt>Buffer</tt> has been retrieved from this
     * <tt>JitterBuffer</tt> and has not been returned yet.
     *
     * @throws IllegalStateException if a <tt>Buffer</tt> has been retrieved
     * from this <tt>JitterBuffer</tt> and has not been returned yet
     */
    private void assertNotLocked()
        throws IllegalStateException
    {
        if (locked != -1)
        {
            throw new IllegalStateException(
                    "A Buffer has been retrieved from this JitterBuffer"
                        + " and has not been returned yet.");
        }
    }

    void dropFill(int index)
    {
        assertNotLocked();
        if ((index < 0) || (index >= length))
            throw new IndexOutOfBoundsException(Integer.toString(index));

        index = (offset + index) % capacity;

        Buffer buffer = elements[index];

        if (index == offset)
            offset = (offset + 1) % capacity;
        else
        {
            int end = (offset + length - 1) % capacity;

            if (index != end)
            {
                while (index != offset)
                {
                    int i = index - 1;

                    if (i < 0)
                        i = capacity - 1;
                    elements[index] = elements[i];
                    index = i;
                }
                elements[index] = buffer;
                offset = (offset + 1) % capacity;
            }
        }

        length--;
        locked = index;
        returnFree(buffer);
    }

    /**
     * Removes the first element (the one with the least sequence number)
     * from <tt>fill</tt> and releases it to be reused (adds it to
     * <tt>free</tt>)
     */
    public void dropFirstFill()
    {
        returnFree(getFill());
    }

    /**
     * Determines whether there are &quot;fill&quot; <tt>Buffer<tt>s in this
     * queue.
     *
     * @return <tt>true</tt> if there are &quot;fill&quot; <tt>Buffer</tt>s in
     * this queue; otherwise, <tt>false</tt>
     */
    boolean fillNotEmpty()
    {
        return (getFillCount() != 0);
    }

    /**
     * Determines whether there are &quot;free&quot; <tt>Buffer<tt>s in this
     * queue.
     *
     * @return <tt>true</tt> if there are &quot;free&quot; <tt>Buffer</tt>s in
     * this queue; otherwise, <tt>false</tt>
     */
    boolean freeNotEmpty()
    {
        return (getFreeCount() != 0);
    }

    /**
     * Gets the capacity in (RTP) packets of this queue/jitter buffer.
     *
     * @return the capacity in (RTP) packets of this queue/jitter buffer
     */
    public int getCapacity()
    {
        int capacity;

        lock.lock();
        try
        {
            capacity = this.capacity;
        }
        finally
        {
            lock.unlock();
        }
        return capacity;
    }

    /**
     * Pops the element/<tt>Buffer</tt> at the head of this queue which contains
     * valid media data.
     *
     * @return the element/<tt>Buffer</tt> at the head of this queue which
     * contains valid media data
     */
    public Buffer getFill()
    {
        assertNotLocked();
        if (noMoreFill())
            throw new IllegalStateException("noMoreFill");

        int index = offset;
        Buffer buffer = elements[index];

        offset = (offset + 1) % capacity;
        length--;
        locked = index;
        return buffer;
    }

    public Buffer getFill(int index)
    {
        if ((index < 0) || (index >= length))
            throw new IndexOutOfBoundsException(Integer.toString(index));

        return elements[(offset + index) % capacity];
    }

    /**
     * Gets the number of &quot;fill&quot; <tt>Buffer</tt>s in this queue.
     *
     * @return the number of &quot;fill&quot; <tt>Buffer</tt>s in this queue
     */
    public int getFillCount()
    {
        int length;

        lock.lock();
        try
        {
            length = this.length;
        }
        finally
        {
            lock.unlock();
        }
        return length;
    }

    /**
     * Gets the sequence number of the element/<tt>Buffer</tt> at the head
     * of this queue or <tt>Buffer.SEQUENCE_UNKNOWN</tt> if this queue is empty.
     *
     * @return the sequence number of the element/<tt>Buffer</tt> at the
     * head of this queue or <tt>Buffer.SEQUENCE_UNKNOWN</tt> if this queue is
     * empty.
     */
    public long getFirstSeq()
    {
        return
            (length == 0)
                ? Buffer.SEQUENCE_UNKNOWN
                : elements[offset].getSequenceNumber();
    }

    /**
     * Retrieves a &quot;free&quot; <tt>Buffer</tt>s from this queue.
     *
     * @return a &quot;free&quot; <tt>Buffer</tt> from this queue
     */
    public Buffer getFree()
    {
        assertNotLocked();
        if (noMoreFree())
            throw new IllegalStateException("noMoreFree");

        int index = (offset + length) % capacity;
        Buffer buffer = elements[index];

        locked = index;
        return buffer;
    }

    /**
     * Gets the number of &quot;free&quot; <tt>Buffer</tt>s in this queue.
     *
     * @return the number of &quot;free&quot; <tt>Buffer</tt>s in this queue
     */
    public int getFreeCount()
    {
        return (capacity - length);
    }

    /**
     * Gets the sequence number of the element/<tt>Buffer</tt> at the tail
     * of this queue or <tt>Buffer.SEQUENCE_UNKNOWN</tt> if this queue is empty.
     *
     * @return the sequence number of the element/<tt>Buffer</tt> at the tail
     * of this queue or <tt>Buffer.SEQUENCE_UNKNOWN</tt> if this queue is empty.
     */
    public long getLastSeq()
    {
        return
            (length == 0)
                ? Buffer.SEQUENCE_UNKNOWN
                : elements[(offset + length - 1) % capacity]
                    .getSequenceNumber();
    }

    /**
     * Inserts <tt>buffer</tt> in the correct place in the queue, so that
     * the order is preserved. The order is by ascending sequence numbers.
     *
     * Note: This could potentially be slow, since all the elements 'bigger'
     * than <tt>buffer</tt> are moved.
     *
     * @param buffer the <tt>Buffer</tt> to insert
     */
    private void insert(Buffer buffer)
    {
        int i = offset;
        int end = (offset + length) % capacity;
        long bufferSN = buffer.getSequenceNumber();

        while (i != end)
        {
            if (elements[i].getSequenceNumber() > bufferSN)
                break;
            if (++i >= capacity)
                i = 0;
        }

        if (i == offset)
            prepend(buffer);
        else if (i == end)
            append(buffer);
        else
        {
            elements[locked] = elements[end];
            for (int j = end; j != i;)
            {
                int k = j - 1;

                if (k < 0)
                    k = capacity - 1;
                elements[j] = elements[k];
                j = k;
            }
            elements[i] = buffer;
            length++;
        }
    }

    /**
     * Determines whether there are no more &quot;fill&quot;
     * elements/<tt>Buffer</tt>s in this queue.
     *
     * @return <tt>true</tt> if there are no more &quot;fill&quot;
     * elements/<tt>Buffer</tt>s in this queue; otherwise, <tt>false</tt>
     */
    boolean noMoreFill()
    {
        return (getFillCount() == 0);
    }

    /**
     * Determines whether there are no more &quot;free&quot;
     * elements/<tt>Buffer</tt>s in this queue.
     *
     * @return <tt>true</tt> if there are no more &quot;free&quot;
     * elements/<tt>Buffer</tt>s in this queue; otherwise, <tt>false</tt>
     */
    boolean noMoreFree()
    {
        return (getFreeCount() == 0);
    }

    /**
     * Adds <tt>buffer</tt> to the beginning of this queue.
     *
     * @param buffer the <tt>Buffer</tt> to add to the beginning of this
     * queue
     */
    private void prepend(Buffer buffer)
    {
        int index = offset - 1;

        if (index < 0)
            index = capacity - 1;
        if (index != locked)
        {
            elements[locked] = elements[index];
            elements[index] = buffer;
        }
        offset = index;
        length++;
    }

    /**
     * Returns (releases) <tt>buffer</tt> to the <tt>free</tt> queue.
     *
     * @param buffer the <tt>Buffer</tt> to return
     */
    public void returnFree(Buffer buffer)
    {
        assertLocked(buffer);

        locked = -1;
    }

    /**
     * Sets the capacity of this instance in terms of the maximum number of
     * <tt>Buffer</tt>s that it may contain.
     *
     * @param capacity the capacity of this instance in terms of the maximum
     * number of <tt>Buffer</tt>s that it may contain
     */
    public void setCapacity(int capacity)
    {
        assertNotLocked();
        if (capacity < 1)
            throw new IllegalArgumentException("capacity");

        if (this.capacity == capacity)
            return;

        Buffer[] elements = new Buffer[capacity];

        while (getFillCount() > capacity)
            dropFirstFill();

        int length = Math.min(getFillCount(), capacity);

        for (int i = 0; i < length; i++)
            elements[i] = getFill(i);
        for (int i = length; i < capacity; i++)
            elements[i] = new Buffer();

        this.capacity = capacity;
        this.elements = elements;
        this.length = length;
        this.offset = 0;
    }
}
