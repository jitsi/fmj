package net.sf.fmj.ejmf.toolkit.media;

import java.util.*;

import com.lti.utils.synchronization.*;

/**
 * The ThreadQueue class provides a mechanism to run threads serially. When a
 * thread is added, it will be started as soon as all threads added before it
 * have completed.
 *
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 *
 * @author Steve Talley & Rob Gordon
 *
 *         mgodehardt: this is now a closeable thread
 */
public class ThreadQueue extends CloseableThread
{
    private Thread running;
    private Vector queue = new Vector();

    /**
     * Constructs a ThreadQueue.
     */
    public ThreadQueue(String threadName)
    {
        super();
        setName(threadName);
        setDaemon(true);
    }

    /**
     * Add a thread to this ThreadQueue.
     *
     * @param t
     *            The Thread to add.
     */
    public synchronized void addThread(Thread t)
    {
        queue.addElement(t);
        notify();
    }

    /**
     * Monitor the thread queue. When a thread is added, start it and block
     * until it finishes.
     * <p>
     * This method is called when the thread is started. It should not be called
     * directly.
     */
    @Override
    public void run()
    {
        try
        {
            while (!isClosing())
            {
                // wait for new entries in the queue
                synchronized (this)
                {
                    while (queue.size() == 0)
                    {
                        wait();
                    }

                    running = (Thread) queue.elementAt(0);
                    queue.removeElementAt(0);
                }

                // Start thread
                running.start();

                // Block until it finishes
                running.join();
            }
        } catch (InterruptedException dontcare)
        {
        }

        setClosed();
    }

    /**
     * Stop the currently running thread and any threads queued to run. Remove
     * all threads from the queue.
     */
    public synchronized void stopThreads()
    {
        if (running != null)
        {
            running.stop();
        }

        for (int i = 0, n = queue.size(); i < n; i++)
        {
            Thread t = (Thread) queue.elementAt(i);
            t.stop();
        }

        queue.removeAllElements();
    }
}
