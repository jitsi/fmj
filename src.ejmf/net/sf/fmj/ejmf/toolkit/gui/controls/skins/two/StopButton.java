package net.sf.fmj.ejmf.toolkit.gui.controls.skins.two;

import javax.swing.*;

import net.sf.fmj.ejmf.toolkit.gui.controls.*;

/**
 *
 * @author Ken Larson
 *
 */
public class StopButton extends BasicIconButton
{
    public StopButton()
    {
        super(new ImageIcon(
                StartButton.class
                        .getResource("resources/control_stop_blue.png")),
                new ImageIcon(
                        StartButton.class
                                .getResource("resources/control_stop.png")));
    }

}
