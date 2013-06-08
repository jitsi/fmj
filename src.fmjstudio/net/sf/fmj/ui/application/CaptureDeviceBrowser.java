package net.sf.fmj.ui.application;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.swing.*;

import net.sf.fmj.media.cdp.*;
import net.sf.fmj.utility.*;

/**
 *
 * @author Ken Larson
 *
 */
public class CaptureDeviceBrowser extends JPanel
{
    private static final Logger logger = LoggerSingleton.logger;

    public static void main(String[] args)
    {
        try
        {
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    "CaptureDeviceBrowser");

            logger.fine("" + CaptureDeviceBrowser.run(null));
            System.exit(0); // TODO: why does it not close by itself?
        } catch (Throwable t)
        {
            logger.log(Level.WARNING, "" + t, t);
        }
    }

    public static MediaLocator run(Frame parent)
    {
        // TODO: parent of dialog?
        JDialog frame = new JDialog();
        frame.setTitle("Select Capture Device");
        frame.setModal(true);
        // frame.setSize(new Dimension(640, 480));
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        CaptureDeviceBrowser panel = new CaptureDeviceBrowser();
        contentPane.add(panel);
        frame.pack();
        if (parent != null)
            frame.setLocationRelativeTo(parent);
        frame.setVisible(true);
        MediaLocator result = panel.okClicked ? panel.getSelectedMediaLocator()
                : null;
        return result;
    }

    private final JComboBox deviceComboBox;

    private final JButton okButton;

    private final JButton cancelButton;

    private boolean okClicked = false;

    private Map<Integer, MediaLocator> deviceComboBoxMap = new HashMap<Integer, MediaLocator>();

    public CaptureDeviceBrowser()
    {
        setLayout(new GridBagLayout());

        deviceComboBox = new JComboBox();
        deviceComboBox.setEditable(false);
        // deviceComboBox.addItemListener(new ItemListener(){
        //
        // public void itemStateChanged(ItemEvent e)
        // {
        // if (e.getStateChange() == ItemEvent.SELECTED)
        // {
        // logger.fine("itemStateChanged: " + e.getItem());
        // if (e.getItem() != null)
        // okButton.setEnabled(true);
        // }
        //
        // }
        //
        // });

        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.weightx = 1.0;
        c1.insets = new Insets(0, 2, 0, 2);
        this.add(deviceComboBox, c1);

        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                okClicked = true;
                closeDialog();
            }

        });
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(0, 2, 0, 2);
        add(okButton, c2);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                closeDialog();
            }

        });
        add(cancelButton, c2);

        populate();

        okButton.setEnabled(!deviceComboBoxMap.isEmpty());

        this.setPreferredSize(new Dimension(300, 80));

    }

    private void closeDialog()
    {
        getParent().getParent().getParent().getParent().setVisible(false);
        ((JDialog) getParent().getParent().getParent().getParent()).dispose();

    }

    public MediaLocator getSelectedMediaLocator()
    {
        int index = deviceComboBox.getSelectedIndex();
        if (index < 0)
            return null;
        return deviceComboBoxMap.get(index);
    }

    private void populate()
    {
        GlobalCaptureDevicePlugger.addCaptureDevices(); // TODO: this needs to
                                                        // be done globally
                                                        // somewhere.

        final java.util.Vector vectorDevices = CaptureDeviceManager
                .getDeviceList(null);
        if (vectorDevices == null || vectorDevices.size() == 0)
        {
            return;
        }

        // deviceComboBox.addItem("[None]");
        // deviceComboBoxMap.put(0, null);

        for (int i = 0; i < vectorDevices.size(); i++)
        {
            CaptureDeviceInfo infoCaptureDevice = (CaptureDeviceInfo) vectorDevices
                    .get(i);
            logger.fine("CaptureDeviceInfo: ");
            logger.fine(infoCaptureDevice.getName());
            logger.fine("" + infoCaptureDevice.getLocator());
            logger.fine("" + infoCaptureDevice.getFormats()[0]);

            deviceComboBox.addItem(infoCaptureDevice.getName());
            deviceComboBoxMap.put(new Integer(i),
                    infoCaptureDevice.getLocator());

        }
    }
}
