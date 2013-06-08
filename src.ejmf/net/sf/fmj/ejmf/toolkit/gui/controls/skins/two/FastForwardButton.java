package net.sf.fmj.ejmf.toolkit.gui.controls.skins.two;

import javax.swing.*;

import net.sf.fmj.ejmf.toolkit.gui.controls.*;

/**
 *
 * @author Ken Larson
 *
 */
public class FastForwardButton extends BasicIconButton
{
    public FastForwardButton()
    {
        super(
                new ImageIcon(
                        StartButton.class
                                .getResource("resources/control_fastforward_blue.png")),
                new ImageIcon(
                        StartButton.class
                                .getResource("resources/control_fastforward.png")));
    }
}
