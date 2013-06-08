package net.sf.fmj.ui.registry;

import java.awt.*;

import javax.media.*;
import javax.swing.*;

/**
 *
 * @author Warren Bloomer
 *
 */
public class PluginsPanel extends JPanel implements DetailsListener
{
    private static final long serialVersionUID = 1L;

    private int pluginType = PlugInManager.CODEC;

    private JPanel detailsPanel = null;
    private JTextArea detailsValue = null;
    private JLabel detailLabel = null;

    private JScrollPane detailsScrollPane = null;
    private EntryPanel entryPanel = null;

    /**
     * This is the default constructor
     */
    public PluginsPanel(int pluginType)
    {
        super();
        this.pluginType = pluginType;
        initialize();
    }

    /**
     * This method initializes detailsPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getDetailsPanel()
    {
        if (detailsPanel == null)
        {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.weighty = 1.0;
            gridBagConstraints2.weightx = 1.0;
            detailLabel = new JLabel();
            detailLabel.setText("Details");
            detailsPanel = new JPanel();
            detailsPanel.setLayout(new BorderLayout());
            detailsPanel.add(detailLabel, BorderLayout.NORTH);
            detailsPanel.add(getDetailsScrollPane(), BorderLayout.CENTER);
        }
        return detailsPanel;
    }

    /**
     * This method initializes detailsScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getDetailsScrollPane()
    {
        if (detailsScrollPane == null)
        {
            detailsValue = new JTextArea();

            detailsScrollPane = new JScrollPane(detailsValue);

        }
        return detailsScrollPane;
    }

    /**
     * This method initializes entryPanel
     *
     * @return net.sf.fmj.ui.registry.EntryPanel
     */
    EntryPanel getEntryPanel()
    {
        if (entryPanel == null)
        {
            entryPanel = new EntryPanel(pluginType);
            entryPanel.setDetailsListener(this);
        }
        return entryPanel;
    }

    /**
     * This method initializes this
     */
    private void initialize()
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.setSize(389, 274);
        this.setLayout(new GridLayout(1, 2));
        this.add(getEntryPanel(), null);
        this.add(getDetailsPanel());
    }

    public void onDetails(String text)
    {
        detailsValue.setText(text);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
