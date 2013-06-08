package net.sf.fmj.ui.registry;

import java.awt.*;
import java.util.logging.*;

import javax.swing.*;

import net.sf.fmj.utility.*;

/**
 *
 * @author Warren Bloomer
 *
 */
public class RegistryEditorPanel extends JPanel
{
    private static final Logger logger = LoggerSingleton.logger;

    private JTabbedPane registryTabbedPane = null;
    private PluginTypesPanel pluginsPanel = null;
    // private UserSettingsPanel userSettingsPanel = null;
    private CaptureDevicePanel captureDevicePanel = null;
    private MimeTypesPanel mimeTypesPanel = null;
    private PackagesPanel packagesPanel = null;

    /**
     * This method initializes
     *
     */
    public RegistryEditorPanel()
    {
        super();
        initialize();
    }

    /**
     * This method initializes captureDevicePanel
     *
     * @return net.sf.fmj.ui.registry.CaptureDevicePanel
     */
    private CaptureDevicePanel getCaptureDevicePanel()
    {
        if (captureDevicePanel == null)
        {
            captureDevicePanel = new CaptureDevicePanel();
        }
        return captureDevicePanel;
    }

    /**
     * This method initializes mimeTypesPanel
     *
     * @return net.sf.fmj.ui.registry.MimeTypesPanel
     */
    private MimeTypesPanel getMimeTypesPanel()
    {
        if (mimeTypesPanel == null)
        {
            mimeTypesPanel = new MimeTypesPanel();
        }
        return mimeTypesPanel;
    }

    // /**
    // * This method initializes userSettingsPanel
    // *
    // * @return net.sf.fmj.ui.registry.UserSettingsPanel
    // */
    // private UserSettingsPanel getUserSettingsPanel() {
    // if (userSettingsPanel == null) {
    // userSettingsPanel = new UserSettingsPanel();
    // }
    // return userSettingsPanel;
    // }

    /**
     * This method initializes packagesPanel
     *
     * @return net.sf.fmj.ui.registry.PackagesPanel
     */
    private PackagesPanel getPackagesPanel()
    {
        if (packagesPanel == null)
        {
            packagesPanel = new PackagesPanel();
        }
        return packagesPanel;
    }

    /**
     * This method initializes pluginsPanel
     *
     * @return net.sf.fmj.ui.registry.PluginsPanel
     */
    private PluginTypesPanel getPluginsPanel()
    {
        if (pluginsPanel == null)
        {
            pluginsPanel = new PluginTypesPanel();
        }
        return pluginsPanel;
    }

    /**
     * This method initializes registryTabbedPane
     *
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getRegistryTabbedPane()
    {
        if (registryTabbedPane == null)
        {
            registryTabbedPane = new JTabbedPane();
            registryTabbedPane.setPreferredSize(new Dimension(320, 240));
            // registryTabbedPane.addTab("User Settings", null,
            // getUserSettingsPanel(), null);
            registryTabbedPane.addTab("Capture Devices", null,
                    getCaptureDevicePanel(), null);
            registryTabbedPane.addTab("PlugIns", null, getPluginsPanel(), null);
            registryTabbedPane.addTab("MIME Types", null, getMimeTypesPanel(),
                    null);
            registryTabbedPane.addTab("Packages", null, getPackagesPanel(),
                    null);
        }
        return registryTabbedPane;
    }

    /**
     * This method initializes this
     *
     */
    private void initialize()
    {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(530, 320));
        this.add(getRegistryTabbedPane(), BorderLayout.CENTER);

    }

} // @jve:decl-index=0:visual-constraint="10,10"
