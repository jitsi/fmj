package net.sf.fmj.ejmf.toolkit.gui.controls;

/**
 * Defines methods required of a GainMeter
 */

public interface GainMeter
{
    /**
     * Transform a gain control level value [0.0...1.0] to a value appropriate
     * for this GainMeter.
     *
     * @param level
     *            A value returned by getLevel method of a GainControl object.
     * @return An integer value that represents a legal level value for this
     *         GainMeter.
     */
    public int mapToMeterLevel(float level);

    /**
     * Set the gain value for this GainMeter.
     *
     * @param level
     *            A gain value in GainMeter coordinates.
     */

    public void setLevel(float level);

    /**
     * Sets the mute status of the GainMeter.
     *
     * @param flag
     *            If true, GainMeter is muted. Otherwise gain is not muted.
     */

    public void setMute(boolean flag);

    /**
     * Force redraw of the gain meter button
     */
    public void updateView();
}
