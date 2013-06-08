package net.sf.fmj.ui.objeditor;

import java.awt.*;

/**
 *
 * @author Ken Larson
 *
 */
public class ComponentValidationException extends Exception
{
    private Component component;

    public ComponentValidationException(Component component, String msg)
    {
        super(msg);
        this.component = component;
    }

    public Component getComponent()
    {
        return component;
    }

}
