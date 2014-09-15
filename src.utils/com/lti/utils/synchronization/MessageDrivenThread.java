package com.lti.utils.synchronization;

/**
 * A useful base class for a thread that wishes to simply respond to messages.
 *
 * @author Ken Larson
 */
public class MessageDrivenThread extends CloseableThread
{
    private MessageDrivenThreadListener listener;

    private final ProducerConsumerQueue<Object> q
        = new ProducerConsumerQueue<Object>();

    public MessageDrivenThread(ThreadGroup group, String threadName)
    {
        super(group, threadName);
    }

    public MessageDrivenThread(
            ThreadGroup group, String threadName,
            MessageDrivenThreadListener listener)
    {
        super(group, threadName);
        this.listener = listener;
    }

    /**
     * subclass should override to do message processing.
     */
    protected void doMessageReceived(Object o)
    {
        if (listener != null)
            listener.onMessage(this, o);
    }

    public void post(Object msg) throws InterruptedException
    {
        q.put(msg);
    }

    @Override
    public void run()
    {
        try
        {
            while (!isClosing())
            {
                Object o = q.get();

                doMessageReceived(o);
            }
        } catch (InterruptedException e)
        {
        } finally
        {
            setClosed();
        }
    }

    public void setListener(MessageDrivenThreadListener listener)
    {
        this.listener = listener;
    }
}
