package net.sf.fmj.ejmf.toolkit.gui.controls;

/**
 * ProgressBar provides a generalized interface for a component
 * used to display the progression of time.
 * <p>
 * NOTE: This interface will make it easy to slide JSlider into
 * StandardControlPanel if performance ever improves to a
 * point where it could run with a controller without causing
 * stutter.
 */

import javax.swing.event.*;

public interface ProgressBar
{
    /**
     * Register ChangeListener with ProgressBar
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Get maximum legal value of ProgressBar.
     *
     * @return maximum legal Slider value
     */
    public int getMaximum();

    /**
     * Get minimum legal value of ProgressBar.
     *
     * @return minimum legal Slider value
     */
    public int getMinimum();

    /**
     * Get current value of ProgressBar.
     *
     * @return value of Slider
     */
    public int getValue();

    /**
     * Remove object as ProgressBar ChangeListener
     */
    public void removeChangeListener(ChangeListener l);

    /**
     * Set maximum legal value of ProgressBar.
     *
     * @param value
     *            maximum legal value of Slider
     */
    public void setMaximum(int value);

    /**
     * Set legal minimum value of ProgressBar.
     *
     * @param value
     *            legal minimum value of Slider
     */
    public void setMinimum(int value);

    /**
     * Set current value of ProgressBar.
     *
     * @param value
     *            new value of Slider
     */
    public void setValue(int value);
}
