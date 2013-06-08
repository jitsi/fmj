package net.sf.fmj.ui.registry;

import java.awt.*;

import javax.swing.*;

/**
 *
 * @author Warren Bloomer
 *
 */
public class UserSettingsPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JLabel logLabel = null;
    private JLabel regFileLabel = null;
    private JTextField logFileTextField = null;
    private JTextField regFileTextField = null;

    /**
     * This is the default constructor
     */
    public UserSettingsPanel()
    {
        super();
        initialize();
    }

    /**
     * This method initializes logFileTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getLogFileTextField()
    {
        if (logFileTextField == null)
        {
            logFileTextField = new JTextField();
            logFileTextField.setPreferredSize(new Dimension(220, 22));
        }
        return logFileTextField;
    }

    /**
     * This method initializes regFileTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getRegFileTextField()
    {
        if (regFileTextField == null)
        {
            regFileTextField = new JTextField();
            regFileTextField.setPreferredSize(new Dimension(220, 22));
        }
        return regFileTextField;
    }

    /**
     * This method initializes this
     */
    private void initialize()
    {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.gridx = 2;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.gridwidth = 2;
        gridBagConstraints2.gridx = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        regFileLabel = new JLabel();
        regFileLabel.setText("Registry File Location");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        logLabel = new JLabel();
        logLabel.setText("Log File");
        this.setSize(422, 284);
        this.setLayout(new GridBagLayout());
        this.add(logLabel, gridBagConstraints);
        this.add(regFileLabel, gridBagConstraints1);
        this.add(getLogFileTextField(), gridBagConstraints2);
        this.add(getRegFileTextField(), gridBagConstraints3);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
