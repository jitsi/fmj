package net.sf.fmj.media.control;

/**
 * A SliderRegionControl can be used to highlight a section of the slider.
 */
public interface SliderRegionControl extends AtomicControl
{
    /**
     * Returns the long value for this control.
     */
    long getMaxValue();

    /**
     * Returns the long value for this control.
     */
    long getMinValue();

    boolean isEnable();

    void setEnable(boolean f);

    /**
     * Sets the long value for this control. Returns the actual long that was
     * set.
     */
    long setMaxValue(long value);

    /**
     * Sets the long value for this control. Returns the actual long that was
     * set.
     */
    long setMinValue(long value);

}
