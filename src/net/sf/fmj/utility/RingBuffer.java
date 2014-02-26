package net.sf.fmj.utility;

/**
 * RingBuffer
 *
 * @author mgodehardt
 * @version 1-1-alpha3
 */
public class RingBuffer
{
    private Object[] buckets;
    private int readIndex;
    private int writeIndex;
    private int overrunCounter;

    // ctor for the jitter buffer
    public RingBuffer(int maxItems)
    {
        resize(maxItems);
    }

    // may block until data is available
    synchronized public Object get() throws InterruptedException
    {
        if (isEmpty())
        {
            wait();
        }

        Object item = buckets[readIndex++];

        if (readIndex >= buckets.length)
        {
            readIndex = 0;
        }
        return item;
    }

    synchronized public int getOverrunCounter()
    {
        return overrunCounter;
    }

    synchronized public boolean isEmpty()
    {
        if (readIndex == writeIndex)
        {
            return true;
        }

        return false;
    }

    synchronized public boolean isFull()
    {
        int index = writeIndex + 1;
        if (index >= buckets.length)
        {
            index = 0;
        }

        if (index == readIndex)
        {
            return true;
        }

        return false;
    }

    synchronized public Object peek()
    {
        if (isEmpty())
        {
            return null;
        }

        return buckets[readIndex];
    }

    synchronized public boolean put(Object item)
    {
        boolean fBufferOverrun = false;

        if (isFull())
        {
            // /System.out.println("### RingBuffer BUFFER OVERRRUN " +
            // (buckets.length - 1));

            // remove a item
            try
            {
                get();
                fBufferOverrun = true;
            } catch (Exception dontcare)
            {
            }

            overrunCounter++;
        }

        buckets[writeIndex++] = item;
        if (writeIndex >= buckets.length)
        {
            writeIndex = 0;
        }

        notifyAll();

        return fBufferOverrun;
    }

    synchronized public void resize(int maxItems)
    {
        if (maxItems < 1)
        {
            maxItems = 1;
        }
        buckets = new Object[maxItems + 1];

        readIndex = writeIndex = overrunCounter = 0;
    }

    synchronized public int size()
    {
        return buckets.length - 1;
    }
}
