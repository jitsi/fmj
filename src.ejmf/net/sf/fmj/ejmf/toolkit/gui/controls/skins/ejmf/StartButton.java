package net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf;

import java.awt.*;

import javax.swing.*;

/**
 * Start button for StandardContolPanel. StartButton takes full advantage of
 * BasicArrowButton to draw an east-facing arrow icon.
 */
public class StartButton extends BasicArrowButton
{
    /**
     * Create a StartButton
     */
    public StartButton()
    {
        super(SwingConstants.EAST);
    }

    /**
     * Make it the right size.
     *
     * @return Always return (20, 20)
     */
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(20, 20);
    }
}
