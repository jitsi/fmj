package net.sf.fmj.gui.controlpanelfactory;

import java.awt.*;

import javax.media.*;

import net.sf.fmj.ejmf.toolkit.gui.controlpanel.*;

/**
 * {@link ControlPanelFactory} for {@link StandardControlPanel}, which is based
 * on EJMF.
 *
 * @author Ken Larson
 *
 */
public class StandardControlPanelFactory implements ControlPanelFactory
{
    public Component getControlPanelComponent(Player p)
    {
        return new StandardControlPanel(p,
                AbstractControlPanel.USE_START_CONTROL
                        | AbstractControlPanel.USE_STOP_CONTROL
                        | AbstractControlPanel.USE_PROGRESS_CONTROL);

    }

}
