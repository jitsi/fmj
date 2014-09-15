package com.lti.utils.synchronization;

/**
 * A base class for threads which need to be gracefully closed, since
 * Thread.stop() is deprecated. Subclass should check isClosing() in their main
 * loop in run(), and call setClosed() when run() completes.
 *
 * @author Ken Larson
 */
public abstract class CloseableThread extends Thread
{
    protected final SynchronizedBoolean closing = new SynchronizedBoolean(false);
    private final SynchronizedBoolean closed = new SynchronizedBoolean(false);

    public CloseableThread()
    {
        super();
    }

    public CloseableThread(String threadName)
    {
        super(threadName);
    }

    /** @deprecated */
    @Deprecated
    public CloseableThread(ThreadGroup group, String threadName)
    {
        super(group, threadName);
    }

    public void close()
    {
        closing.setValue(true);
        interrupt();
    }

    public boolean isClosed()
    {
        return closed.getValue();
    }

    /**
     * intended to be checked by thread in its main loop. break out of the main
     * loop if true.
     */
    protected boolean isClosing()
    {
        return closing.getValue();
    }

    /**
     * to be called by the thread upon exit.
     */
    protected void setClosed()
    {
        closed.setValue(true);
    }

    protected void setClosing()
    {
        closing.setValue(true);
    }

    public void waitUntilClosed() throws InterruptedException
    {
        closed.waitUntil(true);
    }
}
