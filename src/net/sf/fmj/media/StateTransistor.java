package net.sf.fmj.media;

import javax.media.*;

/**
 * StateTransistor is an interface with the functionality of performing the
 * actual state transitions: DoPrefetch, DoRealize, etc.
 */

public interface StateTransistor
{
    /**
     * Called when the prefetch() is aborted, i.e. deallocate() was called while
     * prefetching. Release all resources claimed previously by the prefetch
     * call.
     */
    public void abortPrefetch();

    /**
     * Called when the realize() is aborted, i.e. deallocate() was called while
     * realizing. Release all resources claimed previously by the realize()
     * call.
     */
    public void abortRealize();

    /**
     * This function performs the steps to close a module or Player.
     */
    public void doClose();

    /**
     * This function performs the steps to deallocate a module or Player, and
     * return to the realized state.
     */
    public void doDealloc();

    /**
     * Called when prefetch fails.
     */
    public void doFailedPrefetch();

    /**
     * Called when realize fails.
     */
    public void doFailedRealize();

    /**
     * This function performs the steps to prefetch a module or Player.
     *
     * @return true if successful.
     */
    public boolean doPrefetch();

    /**
     * This function performs the steps of realizing a module or a Player.
     *
     * @return true if successful.
     */
    public boolean doRealize();

    /**
     * This function notifies the module that the media time has changed.
     */
    public void doSetMediaTime(Time t);

    /**
     * This function notifies the module that the playback rate has changed.
     */
    public float doSetRate(float r);

    /**
     * This function performs the steps to start a module or Player.
     */
    public void doStart();

    /**
     * This function performs the steps to stop a module or Player, and return
     * to the prefetched state.
     */
    public void doStop();

}
