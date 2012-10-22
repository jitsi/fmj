package ejmf.toolkit.util;

import javax.media.Controller;

/**
 * The AsynchStopper class provides the ability to stop or
 * deallocate a Controller on a separate thread.  Controller.stop
 * and Controller.deallocate are both synchronous methods.
 *
 * @author     Steve Talley & Rob Gordon
 */
public class AsynchStopper {
    private Controller controller;

    /**
     * Construct a AsynchStopper object for the given Controller
     *
     * @param          controller
     *                 the Controller which to stop or deallocate
     *                 asynchronously
     */
    public AsynchStopper(Controller controller) {
        this.controller = controller;
    }
    
    /**
     * Deallocate the Controller on a separate thread and return
     * immediately.
     */
    public void deallocate() {
        new Thread() {
            public void run() {
                controller.deallocate();
            }
        }.start();
    }
    
    /**
     * Stop the Controller on a separate thread and return
     * immediately.
     */
    public void stop() {
        new Thread() {
            public void run() {
                controller.stop();
            }
        }.start();
    }
}
