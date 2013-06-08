package net.sf.fmj.ui.registry;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;

import javax.media.*;
import javax.swing.*;
import javax.swing.event.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * Can be used for adding entries to: - plugins, demuxes, codecs, effects,
 * renderers, muxes - content package prefixes - protocol package prefixes
 *
 * TODO send details of selected item to listeners, e.g. PlugInInfo for plugins.
 *
 * @author Warren Bloomer
 * @author Ken Larson
 *
 */
public class EntryPanel extends JPanel
{
    private static final Logger logger = LoggerSingleton.logger;

    public static final int TYPE_DEMUX = PlugInManager.DEMULTIPLEXER;
    public static final int TYPE_CODEC = PlugInManager.CODEC;
    public static final int TYPE_EFFECT = PlugInManager.EFFECT;
    public static final int TYPE_RENDERER = PlugInManager.RENDERER;
    public static final int TYPE_MUX = PlugInManager.MULTIPLEXER;
    public static final int TYPE_CONTENT = 10001;
    public static final int TYPE_PROTOCOL = 10002;
    public static final int TYPE_CAPTURE_DEVICE = 10003;
    public static final int TYPE_MIME_TYPES = 10004;

    private final int type;

    private JPanel actionPanel = null;
    private JButton addButton = null;
    private JButton moveUpButton = null;
    private JButton moveDownButton = null;
    private JButton removeButton = null;
    private JButton commitButton = null;
    private JPanel entryPanel = null;
    private JPanel entryTopPanel = null;
    private JLabel entryLabel = null;
    private JTextField entryTextField = null;
    private JTextField entryTextField2 = null; // only used for MIME types

    private JList entryList = null;
    private DefaultComboBoxModel entryListModel = null; // @jve:decl-index=0:visual-constraint="583,242"

    private JScrollPane listScrollPane = null;

    private DetailsListener detailsListener;

    /**
     * This method initializes
     *
     */
    public EntryPanel(int type)
    {
        super();
        this.type = type;
        initialize();
    }

    private void addEntry(String entry, String value2)
    {
        // logger.info("Adding entry" + entry);

        switch (type)
        {
        case TYPE_DEMUX:
        case TYPE_CODEC:
        case TYPE_EFFECT:
        case TYPE_RENDERER:
        case TYPE_MUX:
            // TODO get in and out formats
            Format[] in = null;
            Format[] out = null;
            boolean added = PlugInManager.addPlugIn(entry, in, out, type);

            if (added)
            {
                getEntryListModel().addElement(entry);
            }
            break;

        case TYPE_CONTENT:
            // TODO check if package is valid
            Vector contentList = PackageManager.getContentPrefixList();
            if (!contentList.contains(entry))
            {
                contentList.add(entry);
                PackageManager.setContentPrefixList(contentList);
                getEntryListModel().addElement(entry);
            }
            break;

        case TYPE_PROTOCOL:
            // TODO check if package is valid
            Vector protocolList = PackageManager.getProtocolPrefixList();
            if (!protocolList.contains(entry))
            {
                protocolList.add(entry);
                PackageManager.setContentPrefixList(protocolList);
                getEntryListModel().addElement(entry);
            }
            break;
        case TYPE_CAPTURE_DEVICE:
            // TODO
            break;
        case TYPE_MIME_TYPES:

            final List existingExtensions = MimeManager.getExtensions(entry);
            if (!existingExtensions.contains(value2))
            {
                MimeManager.addMimeType(value2, entry);
                getEntryListModel().addElement(entry);
            }

            break;
        }
    }

    private boolean canMoveItems()
    {
        if (type == TYPE_MIME_TYPES)
            return false;
        return true; // TODO: others that cannot be moved?
    }

    private void commit()
    {
        try
        {
            switch (type)
            {
            case TYPE_DEMUX:
            case TYPE_CODEC:
            case TYPE_EFFECT:
            case TYPE_RENDERER:
            case TYPE_MUX:
                // read list of values from JList, set, and commit
                Vector plugins = new Vector();
                for (int i = 0; i < getEntryListModel().getSize(); i++)
                {
                    Object value = getEntryListModel().getElementAt(i);
                    plugins.add(value);
                }
                PlugInManager.setPlugInList(plugins, type);
                PlugInManager.commit();
                break;

            case TYPE_CONTENT:
                // read list of values from JList, set, and commit
                Vector contentPrefixes = new Vector();
                for (int i = 0; i < getEntryListModel().getSize(); i++)
                {
                    Object value = getEntryListModel().getElementAt(i);
                    contentPrefixes.add(value);
                }
                PackageManager.setContentPrefixList(contentPrefixes);
                PackageManager.commitContentPrefixList();
                break;

            case TYPE_PROTOCOL:
                // read list of values from JList, set, and commit
                Vector protocolPrefixes = new Vector();
                for (int i = 0; i < getEntryListModel().getSize(); i++)
                {
                    Object value = getEntryListModel().getElementAt(i);
                    protocolPrefixes.add(value);
                }
                PackageManager.setProtocolPrefixList(protocolPrefixes);
                PackageManager.commitProtocolPrefixList();
                break;
            case TYPE_CAPTURE_DEVICE:
                // add them as listed
                CaptureDeviceInfo[] cdi = new CaptureDeviceInfo[getEntryListModel()
                        .getSize()];
                for (int i = 0; i < getEntryListModel().getSize(); i++)
                {
                    Object value = getEntryListModel().getElementAt(i);
                    cdi[i] = CaptureDeviceManager.getDevice((String) value);
                }
                // remove all capture devices
                Vector deviceList = (Vector) CaptureDeviceManager
                        .getDeviceList(null).clone();
                for (int i = 0; i < deviceList.size(); i++)
                {
                    CaptureDeviceManager
                            .removeDevice((CaptureDeviceInfo) deviceList
                                    .elementAt(i));
                }
                for (int i = 0; i < cdi.length; i++)
                {
                    CaptureDeviceManager.addDevice(cdi[i]);
                }
                CaptureDeviceManager.commit();
                break;
            case TYPE_MIME_TYPES:
                MimeManager.commit();
                break;
            }
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }
    }

    /**
     * This method initializes actionPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getActionPanel()
    {
        if (actionPanel == null)
        {
            final Insets insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = insets;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 4;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = insets;
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 3;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = insets;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = insets;
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = insets;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            actionPanel = new JPanel();
            actionPanel.setLayout(new GridBagLayout());
            // actionPanel.setPreferredSize(new Dimension(100, 0));
            actionPanel.add(getAddButton(), gridBagConstraints);
            if (canMoveItems())
            {
                actionPanel.add(getMoveUpButton(), gridBagConstraints1);
                actionPanel.add(getMoveDownButton(), gridBagConstraints2);
            }

            actionPanel.add(getRemoveButton(), gridBagConstraints3);
            actionPanel.add(getCommitButton(), gridBagConstraints4);
        }
        return actionPanel;
    }

    /**
     * This method initializes addButton
     *
     * @return javax.swing.JButton
     */
    private JButton getAddButton()
    {
        if (addButton == null)
        {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    final String value1 = getEntryTextField().getText();
                    final String value2;
                    if (getEntryTextField2() != null)
                        value2 = getEntryTextField2().getText();
                    else
                        value2 = null;
                    addEntry(value1, value2);
                }
            });
        }
        return addButton;
    }

    /**
     * This method initializes commitButton
     *
     * @return javax.swing.JButton
     */
    private JButton getCommitButton()
    {
        if (commitButton == null)
        {
            commitButton = new JButton();
            commitButton.setText("Commit");
            commitButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    commit();
                }
            });
        }
        return commitButton;
    }

    public DetailsListener getDetailsListener()
    {
        return detailsListener;
    }

    private JLabel getEntryLabel()
    {
        if (entryLabel == null)
        {
            entryLabel = new JLabel();
            entryLabel.setText("Title");
        }
        return entryLabel;
    }

    /**
     * This method initializes entryList
     *
     * @return javax.swing.JList
     */
    private JList getEntryList()
    {
        if (entryList == null)
        {
            entryList = new JList(getEntryListModel());
            entryList.setDragEnabled(true);
            entryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            entryList.addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent e)
                {
                    if (!e.getValueIsAdjusting())
                        updateDetailsPane(entryList.getSelectedIndex(),
                                entryList.getSelectedValue());
                    // logger.fine("e.getValueIsAdjusting()=" +
                    // e.getValueIsAdjusting() + "first=" + e.getFirstIndex() +
                    // "; last=" + e.getLastIndex() + "; index=" +
                    // entryList.getSelectedIndex() + "; o=" +
                    // entryList.getSelectedValue());

                }

            });
        }
        return entryList;
    }

    /**
     * This method initializes entryListModel
     *
     * @return javax.swing.DefaultComboBoxModel
     */
    private DefaultComboBoxModel getEntryListModel()
    {
        if (entryListModel == null)
        {
            entryListModel = new DefaultComboBoxModel();
        }
        return entryListModel;
    }

    /**
     * This method initializes entryPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getEntryPanel()
    {
        if (entryPanel == null)
        {
            entryPanel = new JPanel();
            entryPanel.setLayout(new BorderLayout());
            entryPanel.add(getEntryTopPanel(), BorderLayout.NORTH);
            // entryPanel.add(getEntryList(), BorderLayout.CENTER);
            entryPanel.add(getListScrollPane(), BorderLayout.CENTER);
        }
        return entryPanel;
    }

    /**
     * This method initializes entryTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getEntryTextField()
    {
        if (entryTextField == null)
        {
            entryTextField = new JTextField();
            entryTextField.setEditable(true);
        }
        return entryTextField;
    }

    /**
     * This method initializes entryTextField2
     *
     * @return javax.swing.JTextField
     */
    private JTextField getEntryTextField2()
    {
        if (type != TYPE_MIME_TYPES)
            return null;

        if (entryTextField2 == null)
        {
            entryTextField2 = new JTextField();
            entryTextField2.setEditable(true);
        }
        return entryTextField2;
    }

    /**
     * This method initializes entryTopPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getEntryTopPanel()
    {
        if (entryTopPanel == null)
        {
            entryTopPanel = new JPanel();
            final int rows = usesSecondEntryTextField() ? 3 : 2;
            entryTopPanel.setLayout(new GridLayout(rows, 1));
            entryTopPanel.add(getEntryLabel());
            entryTopPanel.add(getEntryTextField());
            if (usesSecondEntryTextField())
                entryTopPanel.add(getEntryTextField2());
        }
        return entryTopPanel;
    }

    /**
     * This method initializes listScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getListScrollPane()
    {
        if (listScrollPane == null)
        {
            listScrollPane = new JScrollPane(getEntryList());
        }
        return listScrollPane;
    }

    /**
     * This method initializes moveDownButton
     *
     * @return javax.swing.JButton
     */
    private JButton getMoveDownButton()
    {
        if (moveDownButton == null)
        {
            moveDownButton = new JButton();
            moveDownButton.setText("Move Down");
            moveDownButton
                    .addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent e)
                        {
                            int[] selectedIndices = getEntryList()
                                    .getSelectedIndices();
                            for (int i = selectedIndices.length - 1; i >= 0; i--)
                            {
                                int index = selectedIndices[i];
                                if (index == getEntryListModel().getSize() - 1)
                                {
                                    // don't allow movement beyond end of list
                                    return;
                                }
                                Object entry = getEntryListModel()
                                        .getElementAt(index);
                                getEntryListModel().removeElementAt(index);
                                getEntryListModel().insertElementAt(entry,
                                        index + 1);

                                selectedIndices[i]++;
                            }

                            getEntryList().setSelectedIndices(selectedIndices);
                        }
                    });
        }
        return moveDownButton;
    }

    /**
     * This method initializes moveUpButton
     *
     * @return javax.swing.JButton
     */
    private JButton getMoveUpButton()
    {
        if (moveUpButton == null)
        {
            moveUpButton = new JButton();
            moveUpButton.setText("Move Up");
            moveUpButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    int[] selectedIndices = getEntryList().getSelectedIndices();
                    for (int i = 0; i < selectedIndices.length; i++)
                    {
                        int index = selectedIndices[i];
                        if (index == 0)
                        {
                            // dn't allow movement beyond start of list
                            break;
                        }
                        Object entry = getEntryListModel().getElementAt(index);
                        getEntryListModel().removeElementAt(index);
                        getEntryListModel().insertElementAt(entry, index - 1);

                        selectedIndices[i]--;
                    }
                    getEntryList().setSelectedIndices(selectedIndices);
                }
            });
        }
        return moveUpButton;
    }

    /**
     * This method initializes removeButton
     *
     * @return javax.swing.JButton
     */
    private JButton getRemoveButton()
    {
        if (removeButton == null)
        {
            removeButton = new JButton();
            removeButton.setText("Remove");
            removeButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    int[] selectedIndices = getEntryList().getSelectedIndices();
                    for (int i = selectedIndices.length - 1; i >= 0; i--)
                    {
                        int index = selectedIndices[i];
                        Object entry = getEntryListModel().getElementAt(index);
                        // remove plugin
                        String value = (String) getEntryListModel()
                                .getElementAt(index);
                        removeEntry(value);
                    }
                }
            });
        }
        return removeButton;
    }

    /**
     * This method initializes this
     *
     */
    private void initialize()
    {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(313, 280));
        this.add(getActionPanel(), BorderLayout.EAST);
        this.add(getEntryPanel(), BorderLayout.CENTER);
        load();
    }

    void load()
    {
        Vector list = null;
        getEntryListModel().removeAllElements();

        switch (type)
        {
        case TYPE_DEMUX:
            list = PlugInManager.getPlugInList(null, null,
                    PlugInManager.DEMULTIPLEXER);
            setTitle("Demultiplexer");
            break;

        case TYPE_CODEC:
            list = PlugInManager.getPlugInList(null, null, PlugInManager.CODEC);
            setTitle("Codec");
            break;

        case TYPE_EFFECT:
            list = PlugInManager
                    .getPlugInList(null, null, PlugInManager.EFFECT);
            setTitle("Effect");
            break;

        case TYPE_RENDERER:
            list = PlugInManager.getPlugInList(null, null,
                    PlugInManager.RENDERER);
            setTitle("Renderer");
            break;

        case TYPE_MUX:
            list = PlugInManager.getPlugInList(null, null,
                    PlugInManager.MULTIPLEXER);
            setTitle("Multiplexer");
            break;

        case TYPE_CONTENT:
            list = PackageManager.getContentPrefixList();
            setTitle("Content Prefix List");
            break;

        case TYPE_PROTOCOL:
            list = PackageManager.getProtocolPrefixList();
            setTitle("Protocol Prefix List");
            break;

        case TYPE_CAPTURE_DEVICE:
            list = new Vector();
            Vector captureDeviceList = CaptureDeviceManager.getDeviceList(null);
            for (int i = 0; i < captureDeviceList.size(); ++i)
            {
                final CaptureDeviceInfo info = (CaptureDeviceInfo) captureDeviceList
                        .get(i);
                list.add(info.getName());
            }
            setTitle("Capture Devices");
            break;

        case TYPE_MIME_TYPES:
            list = new Vector();
            final Set s = new HashSet(); // to remove dups
            s.addAll(MimeManager.getMimeTable().values());
            list.addAll(s);
            Collections.sort(list);
            setTitle("MIME Types");
            break;

        default:
            list = new Vector();
        }

        for (int i = 0; i < list.size(); i++)
        {
            getEntryListModel().addElement(list.get(i));
        }
    }

    private void removeEntry(String entry)
    {
        // logger.info("Removing entry" + entry);

        switch (type)
        {
        case TYPE_DEMUX:
        case TYPE_CODEC:
        case TYPE_EFFECT:
        case TYPE_RENDERER:
        case TYPE_MUX:
            // TODO get in and out formats
            Format[] in = null;
            Format[] out = null;
            boolean removed = PlugInManager.removePlugIn(entry, type);

            if (removed)
            {
                getEntryListModel().removeElement(entry);
            }
            break;

        case TYPE_CONTENT:
            Vector contentList = PackageManager.getContentPrefixList();
            if (contentList.remove(entry))
            {
                PackageManager.setContentPrefixList(contentList);
                getEntryListModel().removeElement(entry);
            }
            break;

        case TYPE_PROTOCOL:
            Vector protocolList = PackageManager.getProtocolPrefixList();
            if (protocolList.remove(entry))
            {
                PackageManager.setProtocolPrefixList(protocolList);
                getEntryListModel().removeElement(entry);
            }
            break;
        case TYPE_CAPTURE_DEVICE:
            CaptureDeviceInfo cdi = CaptureDeviceManager.getDevice(entry);
            getEntryListModel().removeElement(entry);
            CaptureDeviceManager.removeDevice(cdi);
            break;
        case TYPE_MIME_TYPES:
            // TODO: warn if attempt to remove "default" extensions.
            // this code will not actually remove default extensions, but no
            // warning is given.
            final List extensions = MimeManager.getExtensions(entry);
            int numRemoved = 0;
            if (extensions.size() > 0)
            {
                for (int i = 0; i < extensions.size(); ++i)
                {
                    if (MimeManager.removeMimeType((String) extensions.get(i)))
                        ++numRemoved;
                }

            }
            if (numRemoved > 0)
            {
                getEntryListModel().removeElement(entry);
            }

            break;
        }
    }

    public void setDetailsListener(DetailsListener detailsListener)
    {
        this.detailsListener = detailsListener;
    }

    public void setTitle(String title)
    {
        getEntryLabel().setText(title);
    }

    private void updateDetailsPane(int selectedIndex, Object selectedValue)
    {
        if (detailsListener == null)
            return;

        if (selectedValue == null)
        {
            detailsListener.onDetails("");
            return;
        }
        final StringBuffer b = new StringBuffer();

        switch (type)
        {
        // all plug ins:
        case TYPE_DEMUX:
        case TYPE_CODEC:
        case TYPE_EFFECT:
        case TYPE_RENDERER:
        case TYPE_MUX:
        {
            final String plugIn = (String) selectedValue;
            final Format[] inputFormats = PlugInManager
                    .getSupportedInputFormats(plugIn, type);
            final Format[] outputFormats = PlugInManager
                    .getSupportedOutputFormats(plugIn, type);
            b.append("Input Formats ---->\n\n");
            if (inputFormats != null)
            {
                for (int i = 0; i < inputFormats.length; ++i)
                {
                    b.append(i + ". " + inputFormats[i].getClass().getName()
                            + "\n " + inputFormats[i] + "\n");
                }
            }

            b.append("\n\n");
            b.append("Output Formats ---->\n\n");
            if (outputFormats != null)
            {
                for (int i = 0; i < outputFormats.length; ++i)
                {
                    b.append(i + ". " + outputFormats[i].getClass().getName()
                            + "\n " + outputFormats[i] + "\n");
                }
            }
        }
        // TODO: other types
        case TYPE_MIME_TYPES:

            final String mimeType = (String) selectedValue;
            final List extensions = MimeManager.getExtensions(mimeType);
            for (int i = 0; i < extensions.size(); ++i)
            {
                b.append((String) extensions.get(i) + "\n");
            }

            break;

        case TYPE_CAPTURE_DEVICE:

            try
            {
                CaptureDeviceInfo cdi = CaptureDeviceManager
                        .getDevice((String) selectedValue);

                javax.media.protocol.DataSource dataSource = javax.media.Manager
                        .createDataSource(cdi.getLocator());
                dataSource.connect();

                b.append("Name = " + cdi.getName());

                b.append("\n\n");
                b.append("Locator = " + cdi.getLocator());

                b.append("\n\n");
                b.append("Output Formats ---->\n\n");

                final Format[] outputFormats = cdi.getFormats();
                if (null != outputFormats)
                {
                    for (int i = 0; i < outputFormats.length; i++)
                    {
                        b.append(i + ". "
                                + outputFormats[i].getClass().getName() + "\n "
                                + outputFormats[i] + "\n");
                    }
                }
                dataSource.disconnect();
            } catch (Exception dontcare)
            {
            }
            break;
        default:

        }

        detailsListener.onDetails(b.toString());

    }

    private boolean usesSecondEntryTextField()
    {
        return type == TYPE_MIME_TYPES;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
