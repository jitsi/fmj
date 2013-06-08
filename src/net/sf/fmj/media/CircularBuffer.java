package net.sf.fmj.media;

import javax.media.*;

/**
 * CircularQueue It implements a circular FIFO queue of references to Objects.
 */
public class CircularBuffer
{
    /**
     * buffer to hold the references. Would be changed to buffer array, when
     * Buffer stabalize
     */
    private Buffer buf[];
    /** write pointer */
    private int head;
    /** read pointer */
    private int tail;
    /** number of frames that can be read */
    private int availableFramesForReading;
    /** number of frames that can be written */
    private int availableFramesForWriting;
    /** number of frames that were checked out for reading */
    private int lockedFramesForReading;
    /** number of frames that were checked out for writing */
    private int lockedFramesForWriting;

    /** size of the FIFO queue */
    private int size;

    /**
     * create a queue with max number of elements
     *
     * @param n
     *            the max number of elements
     */
    public CircularBuffer(int n)
    {
        size = n;
        buf = new Buffer[n];
        for (int i = 0; i < n; i++)
            buf[i] = new Buffer(); // *** generalize this
        reset();
    }

    /** returns <tt>true</tt> if read() would succeed */
    public synchronized boolean canRead()
    {
        return (availableFramesForReading > 0);
    }

    /** returns <tt>true</tt> if getEmptyObject() would succeed */
    public synchronized boolean canWrite()
    {
        return (availableFramesForWriting > 0);
    }

    /** error dump */
    public void error()
    {
        throw new RuntimeException("CircularQueue failure:\n head=" + head
                + "\n tail=" + tail + "\n canRead=" + availableFramesForReading
                + "\n canWrite=" + availableFramesForWriting + "\n lockedRead="
                + lockedFramesForReading + "\n lockedWrite="
                + lockedFramesForWriting);
    }

    /** returns empty buffer object to put data in */
    public synchronized Buffer getEmptyBuffer()
    {
        if (availableFramesForWriting == 0)
            error();

        lockedFramesForWriting++;
        Buffer buffer = buf[tail];
        availableFramesForWriting--;
        tail++;
        if (tail >= size)
            tail -= size;

        return buffer;
    }

    public synchronized boolean lockedRead()
    {
        return (lockedFramesForReading > 0);
    }

    public synchronized boolean lockedWrite()
    {
        return (lockedFramesForWriting > 0);
    }

    /**
     * Gets frame with valid buffer from the queue
     */
    public synchronized Buffer peek()
    {
        if (availableFramesForReading == 0)
            error();

        return buf[head];
    }

    public void print()
    {
        System.err.println("CircularQueue : head=" + head + " tail=" + tail
                + " canRead=" + availableFramesForReading + " canWrite="
                + availableFramesForWriting + " lockedRead="
                + lockedFramesForReading + " lockedWrite="
                + lockedFramesForWriting);
    }

    /**
     * Gets frame with valid buffer from the queue
     */
    public synchronized Buffer read()
    {
        if (availableFramesForReading == 0)
            error();

        Buffer buffer = buf[head];
        lockedFramesForReading++;
        availableFramesForReading--;
        head++;
        if (head >= size)
            head -= size;

        return buffer;
    }

    /** indicates that the latest read frame is no longer in use */
    public synchronized void readReport()
    {
        if (lockedFramesForReading == 0)
            error();

        lockedFramesForReading--;
        availableFramesForWriting++;
    }

    /** reset the queue */
    public synchronized void reset()
    {
        availableFramesForReading = 0;
        availableFramesForWriting = size;
        lockedFramesForReading = 0;
        lockedFramesForWriting = 0;
        head = 0;
        tail = 0;
    }

    /**
     * indicates that latest Object returns to the queue <i><br>
     * we removed writeReport(false), since we have the mechanism in
     * javax.media.Buffer (discard).</i>
     */
    public synchronized void writeReport()
    {
        if (lockedFramesForWriting == 0)
            error();

        lockedFramesForWriting--;
        availableFramesForReading++; // wrote data OK
        /*
         * @@@ buffer invalid: removed since we have the mechanism in
         * javax.media.Buffer { availableFramesForWriting++; // buffer not
         * written tail--; if (tail<0) tail += size; }
         */
    }
}
