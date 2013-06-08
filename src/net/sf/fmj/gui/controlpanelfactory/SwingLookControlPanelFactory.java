package net.sf.fmj.gui.controlpanelfactory;

import java.awt.*;

import javax.media.*;

import net.sf.fmj.gui.controlpanel.*;

/**
 *
 * @author Ken Larson
 *
 */
public class SwingLookControlPanelFactory implements ControlPanelFactory
{
    public Component getControlPanelComponent(Player p)
    {
        return new SwingLookControlPanel(p);
    }

}
