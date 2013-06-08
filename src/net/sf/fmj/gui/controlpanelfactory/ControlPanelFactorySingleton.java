package net.sf.fmj.gui.controlpanelfactory;

/**
 * The global singleton {@link ControlPanelFactory} instance. All FMJ handlers
 * should use this to create their control panel components, if possible,
 * ensuring a consistent, configurable UI.
 *
 * @author Ken Larson
 *
 */
public final class ControlPanelFactorySingleton
{
    private static ControlPanelFactory instance = /*
                                                   * new
                                                   * SwingLookControlPanelFactory
                                                   * ();
                                                   */new StandardControlPanelFactory();

    public static ControlPanelFactory getInstance()
    {
        return instance;
    }

    public static void setInstance(ControlPanelFactory instance)
    {
        ControlPanelFactorySingleton.instance = instance;
    }

    private ControlPanelFactorySingleton()
    {
        super();
    }
}
