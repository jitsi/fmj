package com.lti.utils.synchronization;

/**
 *
 * @author Ken Larson
 *
 */
public class SynchronizedObjectHolder<T>
{
    private T object;

    public SynchronizedObjectHolder()
    {
        super();
    }

    public SynchronizedObjectHolder(T value)
    {
        setObject(value);
    }

    public synchronized T getObject()
    {
        return object;
    }

    public synchronized void setObject(T value)
    {
        object = value;
        notifyAll();

    }

    public synchronized void waitUntilNotNull() throws InterruptedException
    {
        while (object == null)
        {
            wait();
        }

    }

    /**
     * @return true if value is now non-null, false if timeout occurred
     */
    public synchronized boolean waitUntilNotNull(int timeout)
            throws InterruptedException
    {
        long start = System.currentTimeMillis();
        while (object == null)
        {
            long now = System.currentTimeMillis();
            long diff = now - start;
            long wait = timeout - diff;
            if (wait <= 0)
                return false;
            wait(wait);
        }
        return true;

    }
}
