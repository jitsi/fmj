package net.sf.fmj.ui.registry;

import java.awt.*;

import javax.swing.*;

/**
 *
 * @author Warren Bloomer
 *
 */
public class PackagesPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private EntryPanel contentPrefixPanel = null;
    private EntryPanel protocolPrefixPanel = null;

    /**
     * This is the default constructor
     */
    public PackagesPanel()
    {
        super();
        initialize();
    }

    /**
     * This method initializes contentPrefixPanel
     *
     * @return net.sf.fmj.ui.registry.EntryPanel
     */
    private EntryPanel getContentPrefixPanel()
    {
        if (contentPrefixPanel == null)
        {
            contentPrefixPanel = new EntryPanel(EntryPanel.TYPE_CONTENT);
        }
        return contentPrefixPanel;
    }

    /**
     * This method initializes protocolPrefixPanel
     *
     * @return net.sf.fmj.ui.registry.EntryPanel
     */
    private EntryPanel getProtocolPrefixPanel()
    {
        if (protocolPrefixPanel == null)
        {
            protocolPrefixPanel = new EntryPanel(EntryPanel.TYPE_PROTOCOL);
        }
        return protocolPrefixPanel;
    }

    /**
     * This method initializes this
     *
     */
    private void initialize()
    {
        this.setSize(600, 400);
        this.setLayout(new GridLayout(2, 0));
        this.add(getContentPrefixPanel(), null);
        this.add(getProtocolPrefixPanel(), null);
    }

}
