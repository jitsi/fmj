package net.sf.fmj.media.control;

/**
 * Listener for changes in the state of a Control.
 */
public interface ControlChangeListener
{
    /**
     * Gets called whenever the state of a Control changes.
     */
    public void controlChanged(ControlChangeEvent e);

}
