package ejmf.toolkit.media;

import javax.media.Time;

/**
 * Provides a thread to asynchronously SyncStart the given
 * AbstractController.
 *
 * @author     Steve Talley & Rob Gordon
 */
public class SyncStartThread extends Thread {
    private AbstractController controller;
    private Time timeBaseStartTime;

    /**
     * Construct a SyncStartThread for the given
     * AbstractController.  The AbstractController will not be
     * syncStarted until this thread is started.
     */
    public SyncStartThread(
        AbstractController controller,
        Time timeBaseStartTime)
    {
        super();
        this.controller = controller;
        this.timeBaseStartTime = timeBaseStartTime;
    }

    /**
     * syncStarts the AbstractController specified in the
     * constructor.  This method should not be called directly.
     * Use start() to start this thread.
     */
    public void run() {
        controller.synchronousSyncStart(timeBaseStartTime);
    }
}
