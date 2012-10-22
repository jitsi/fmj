package ejmf.toolkit.media;

import java.util.Vector;

/**
 * The ThreadQueue class provides a mechanism to run threads
 * serially.  When a thread is added, it will be started as soon
 * as all threads added before it have completed.
 *
 * @author     Steve Talley & Rob Gordon
 */
public class ThreadQueue extends Thread {
    private Thread running;
    private Vector queue = new Vector();

    /**
     * Constructs a ThreadQueue.
     */
    public ThreadQueue() {
        super();
        setDaemon(true);
        start();
    }

    /**
     * Monitor the thread queue.  When a thread is added, start
     * it and block until it finishes.
     * <p>
     * This method is called when the thread is started.  It
     * should not be called directly.
     */
    public void run() {
        while(true) {
            synchronized(this) {
                while( queue.size() == 0 ) {
                    try {
                        wait();
                    } catch(InterruptedException e) {}
                }
                running = (Thread)queue.elementAt(0);
                queue.removeElementAt(0);
            }

            //  Start thread
            running.start();

            //  Block until it finishes
            while(true) {
                try {
                    running.join();
                    break;
                }
                catch(InterruptedException e) {}
            }
        }
    }
    
    /**
     * Add a thread to this ThreadQueue.
     *
     * @param      t
     *             The Thread to add.
     */
    public synchronized void addThread(Thread t) {
        queue.addElement(t);
        notify();
    }

    /**
     * Stop the currently running thread and any threads
     * queued to run.  Remove all threads from the queue.
     */
    public synchronized void stopThreads() {
        if( running != null ) {
            running.stop();
        }

        for(int i = 0, n = queue.size(); i < n; i++) {
            Thread t = (Thread)queue.elementAt(i);
            t.stop();
        }

        queue.removeAllElements();
    }
}
