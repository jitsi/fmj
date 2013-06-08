package net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf;

import java.awt.*;

import javax.swing.*;

/**
 * Reverse button for StandardContolPanel. ReverseButton takes full advantage of
 * BasicArrowButton to draw a west-facing arrow icon.
 */
public class ReverseButton extends BasicArrowButton
{
    /**
     * Create a ReverseButton
     */
    public ReverseButton()
    {
        super(SwingConstants.WEST);
    }

    /**
     * Make it the right size.
     *
     * @return Always return (20,20)
     */
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(20, 20);
    }
}
