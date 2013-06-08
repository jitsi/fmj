package net.sf.fmj.ui.registry;

import java.awt.*;

import javax.media.*;
import javax.swing.*;

/**
 * A multipanel panel
 *
 * @author Warren Bloomer
 *
 */
public class PluginTypesPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JTabbedPane pluginTypesTabbedPane = null;
    private PluginsPanel codecPanel = null;
    private PluginsPanel demuxPanel = null;
    private PluginsPanel effectsPanel = null;
    private PluginsPanel rendererPanel = null;
    private PluginsPanel muxPanel = null;

    /**
     * This is the default constructor
     */
    public PluginTypesPanel()
    {
        super();
        initialize();
    }

    /**
     * This method initializes codecPanel
     *
     * @return net.sf.fmj.ui.registry.PluginsPanel
     */
    private PluginsPanel getCodecPanel()
    {
        if (codecPanel == null)
        {
            codecPanel = new PluginsPanel(PlugInManager.CODEC);
        }
        return codecPanel;
    }

    /**
     * This method initializes demuxPanel
     *
     * @return net.sf.fmj.ui.registry.PluginsPanel
     */
    private PluginsPanel getDemuxPanel()
    {
        if (demuxPanel == null)
        {
            demuxPanel = new PluginsPanel(PlugInManager.DEMULTIPLEXER);
        }
        return demuxPanel;
    }

    /**
     * This method initializes effectsPanel
     *
     * @return net.sf.fmj.ui.registry.PluginsPanel
     */
    private PluginsPanel getEffectsPanel()
    {
        if (effectsPanel == null)
        {
            effectsPanel = new PluginsPanel(PlugInManager.EFFECT);
        }
        return effectsPanel;
    }

    /**
     * This method initializes muxPanel
     *
     * @return net.sf.fmj.ui.registry.PluginsPanel
     */
    private PluginsPanel getMuxPanel()
    {
        if (muxPanel == null)
        {
            muxPanel = new PluginsPanel(PlugInManager.MULTIPLEXER);
        }
        return muxPanel;
    }

    /**
     * This method initializes pluginTypesTabbedPane
     *
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getPluginTypesTabbedPane()
    {
        if (pluginTypesTabbedPane == null)
        {
            pluginTypesTabbedPane = new JTabbedPane();
            pluginTypesTabbedPane.addTab("Demultiplexer", null,
                    getDemuxPanel(), null);
            pluginTypesTabbedPane.addTab("Codec", null, getCodecPanel(), null);
            pluginTypesTabbedPane.addTab("Effect", null, getEffectsPanel(),
                    null);
            pluginTypesTabbedPane.addTab("Renderer", null, getRendererPanel(),
                    null);
            pluginTypesTabbedPane.addTab("Multiplexer", null, getMuxPanel(),
                    null);
        }
        return pluginTypesTabbedPane;
    }

    /**
     * This method initializes rendererPanel
     *
     * @return net.sf.fmj.ui.registry.PluginsPanel
     */
    private PluginsPanel getRendererPanel()
    {
        if (rendererPanel == null)
        {
            rendererPanel = new PluginsPanel(PlugInManager.RENDERER);
        }
        return rendererPanel;
    }

    /**
     * This method initializes this
     */
    private void initialize()
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(480, 320));
        this.setPreferredSize(new Dimension(480, 320));
        this.add(getPluginTypesTabbedPane(), gridBagConstraints);
    }

}
