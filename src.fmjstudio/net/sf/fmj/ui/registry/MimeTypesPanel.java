package net.sf.fmj.ui.registry;

import java.awt.*;

import javax.swing.*;

/**
 *
 * @author Warren Bloomer
 *
 */
public class MimeTypesPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * This is the default constructor
     */
    public MimeTypesPanel()
    {
        super();
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize()
    {
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());

        final PluginsPanel p = new PluginsPanel(EntryPanel.TYPE_MIME_TYPES);
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.BOTH;
        c1.weightx = 1.0;
        c1.weighty = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER;
        // c1.insets = new Insets(0, 2, 0, 2);
        this.add(p, c1);
    }

}
