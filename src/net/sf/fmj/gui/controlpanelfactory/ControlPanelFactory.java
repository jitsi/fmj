package net.sf.fmj.gui.controlpanelfactory;

import java.awt.*;

import javax.media.*;

/**
 * A factory for a control panel component.
 *
 * @author Ken Larson
 *
 */
public interface ControlPanelFactory
{
    public Component getControlPanelComponent(Player p);
}
