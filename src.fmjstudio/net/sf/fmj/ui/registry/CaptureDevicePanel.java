package net.sf.fmj.ui.registry;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.sf.fmj.media.cdp.*;

/**
 * Settings for capture devices. TODO provide option to automatically detect
 * capture devices - provide itime interval for polling.
 *
 * @author Warren Bloomer
 * @author Ken Larson
 *
 */
public class CaptureDevicePanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * This is the default constructor
     */
    public CaptureDevicePanel()
    {
        super();
        initialize();
    }

    /**
     * This method initializes this
     *
     */
    private void initialize()
    {
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());

        final PluginsPanel p = new PluginsPanel(EntryPanel.TYPE_CAPTURE_DEVICE);
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.BOTH;
        c1.weightx = 1.0;
        c1.weighty = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER;
        // c1.insets = new Insets(0, 2, 0, 2);
        this.add(p, c1);
        JButton buttonDetect = new JButton("Detect Capture Devices");

        GridBagConstraints c2 = new GridBagConstraints();
        c2.anchor = GridBagConstraints.WEST;

        this.add(buttonDetect, c2);
        buttonDetect.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                // TODO: this updates the CaptureDeviceManager, when really, it
                // should just add stuff to the GUI's list.
                GlobalCaptureDevicePlugger.addCaptureDevices();

                p.getEntryPanel().load();

            }

        });
    }

}
