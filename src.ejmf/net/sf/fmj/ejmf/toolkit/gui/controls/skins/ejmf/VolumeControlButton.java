package net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf;

import java.awt.*;

/**
 * VolumeControlButton allows creation of a small upward or downward pointing
 * arrow for use as a volume control.
 *
 * @see net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.BasicArrowButton
 */

public class VolumeControlButton extends BasicArrowButton
{
    /**
     * Create a upward pointing arrow.
     */
    public final static int INCREASE = BasicArrowButton.NORTH;
    /**
     * Create a downward pointing arrow.
     */
    public final static int DECREASE = BasicArrowButton.SOUTH;

    /**
     * Create a VolumeControlButton.
     *
     * @param orientation
     *            Determines which way arrow points, NORTH or SOUTH.
     */
    public VolumeControlButton(int orientation)
    {
        super(orientation);
    }

    /**
     * Make it the right size.
     *
     * @return Always return (10, 10)
     */
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(10, 10);
    }
}
