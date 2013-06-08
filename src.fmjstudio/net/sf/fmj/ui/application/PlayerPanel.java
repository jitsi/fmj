package net.sf.fmj.ui.application;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.swing.*;

import net.sf.fmj.gui.controlpanel.*;
import net.sf.fmj.ui.dialogs.*;
import net.sf.fmj.ui.wizards.*;
import net.sf.fmj.utility.*;

import com.lti.utils.*;

/**
 *
 * @author Warren Bloomer
 *
 */
public class PlayerPanel extends JPanel
{
    private static final Logger logger = LoggerSingleton.logger;

    private PlayerPanelPrefs prefs;

    private JToolBar playerToolBar = null;
    private JButton openButton = null;
    private JButton openCaptureDeviceButton = null;
    private SwingLookControlPanel transportControlPanel = null;
    private JLabel statusBar = null;
    private JPanel videoPanel = null;
    private JComboBox addressComboBox = null;
    private JButton loadButton = null;
    private JPanel addressPanel = null;
    private JLabel locationLabel = null;
    private ContainerPlayer containerPlayer = null; // @jve:decl-index=0:visual-constraint="557,162"

    /**
     * This method initializes
     *
     */
    public PlayerPanel()
    {
        super();
        initialize();
    }

    public void addMediaLocatorAndLoad(String url)
    {
        boolean alreadyThere = false;
        for (int i = 0; i < getAddressComboBox().getItemCount(); ++i)
        {
            if (getAddressComboBox().getItemAt(i).equals(url))
            {
                alreadyThere = true;
                break;
            }
        }
        if (!alreadyThere)
        {
            getAddressComboBox().addItem(url);
        }

        if (getAddressComboBox().getSelectedItem() == null
                || !getAddressComboBox().getSelectedItem().equals(url))
            getAddressComboBox().setSelectedItem(url); // will auto-load
        else
            onLoadButtonClick(); // already selected
    }

    /**
     * This method initializes addressComboBox
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getAddressComboBox()
    {
        if (addressComboBox == null)
        {
            addressComboBox = new JComboBox();
            addressComboBox.setEditable(true);
            addressComboBox.setPreferredSize(new Dimension(400, 27));
            for (String recentUrl : prefs.recentUrls)
            {
                addressComboBox.addItem(recentUrl);
            }
            addressComboBox.setSelectedIndex(-1); // nothing selected by
                                                  // default.

            // load an item when selected from the list
            addressComboBox.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    // TODO: typing in the combo and clicking load... causes
                    // this event to fire, plus loads from clicking load.
                    // TODO: typing in the combo and clicking tab, causes this
                    // event to fire.
                    logger.fine("addressComboBox state change: " + e);
                    if (e.getStateChange() == ItemEvent.SELECTED)
                        onLoadButtonClick();
                }
            });
        }
        return addressComboBox;
    }

    /**
     * This method initializes addressPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getAddressPanel()
    {
        if (addressPanel == null)
        {
            locationLabel = new JLabel();
            locationLabel.setText("Location:");
            addressPanel = new JPanel();
            addressPanel.setLayout(new GridBagLayout());
            // addressPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0,
            // 0));
            GridBagConstraints c1 = new GridBagConstraints();
            c1.insets = new Insets(0, 2, 0, 2);
            addressPanel.add(locationLabel, c1);
            GridBagConstraints c2 = new GridBagConstraints();
            c2.fill = GridBagConstraints.HORIZONTAL;
            c2.weightx = 1.0;
            c2.insets = new Insets(0, 2, 0, 2);
            addressPanel.add(getAddressComboBox(), c2);
            // GridBagConstraints c3 = new GridBagConstraints();
            // addressPanel.add(getLoadButton(), c1);

        }
        return addressPanel;
    }

    /**
     * This method initializes containerPlayer
     *
     * @return net.sf.fmj.ui.application.ContainerPlayer
     */
    private ContainerPlayer getContainerPlayer()
    {
        if (containerPlayer == null)
        {
            containerPlayer = new ContainerPlayer(getVideoPanel());
            containerPlayer.setAutoLoop(prefs.autoLoop);
            containerPlayer
                    .setContainerPlayerStatusListener(new ContainerPlayerStatusListener()
                    {
                        public void onStatusChange(final String newStatus)
                        {
                            logger.fine("Status change: " + newStatus);
                            SwingUtilities.invokeLater(new Runnable()
                            {
                                public void run()
                                {
                                    statusBar.setText(newStatus);
                                }
                            });

                        }

                    });
        }
        return containerPlayer;
    }

    /**
     * This method initializes loadButton
     *
     * @return javax.swing.JButton
     */
    private JButton getLoadButton()
    {
        if (loadButton == null)
        {
            loadButton = new JButton();
            loadButton.setToolTipText("Load selected location");
            loadButton.setIcon(new ImageIcon(getClass().getResource(
                    "/net/sf/fmj/ui/images/import_wiz.png")));
            loadButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent event)
                {
                    onLoadButtonClick();
                }
            });
        }
        return loadButton;
    }

    /**
     * This method initializes openButton
     *
     * @return javax.swing.JButton
     */
    private JButton getOpenButton()
    {
        if (openButton == null)
        {
            openButton = new JButton();
            openButton.setToolTipText("Browse for media file...");
            openButton.setIcon(new ImageIcon(getClass().getResource(
                    "/net/sf/fmj/ui/images/cvs_folder_rep.png")));
            openButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    onOpenFile();
                }

            });
        }
        return openButton;
    }

    private JButton getOpenCaptureDeviceButton()
    {
        if (openCaptureDeviceButton == null)
        {
            openCaptureDeviceButton = new JButton();
            openCaptureDeviceButton.setToolTipText("Select capture device...");
            openCaptureDeviceButton.setIcon(new ImageIcon(getClass()
                    .getResource("/net/sf/fmj/ui/images/webcam.png"))); // TODO:
                                                                        // different
                                                                        // icon
            openCaptureDeviceButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    onOpenCaptureDevice();

                }

            });
        }
        return openCaptureDeviceButton;
    }

    private Frame getParentFrame()
    {
        // TODO: there must be a better way in swing to do this...
        Container c = getParent();
        while (c != null)
        {
            if (c instanceof Frame)
                return (Frame) c;

            c = c.getParent();
        }
        throw new RuntimeException("No parent frame");
    }

    /**
     * This method initializes playerToolBar
     *
     * @return javax.swing.JToolBar
     */
    private JToolBar getPlayerToolBar()
    {
        if (playerToolBar == null)
        {
            playerToolBar = new JToolBar();
            playerToolBar.setFloatable(false);
            playerToolBar.add(getOpenButton());
            playerToolBar.add(getOpenCaptureDeviceButton());
            playerToolBar.add(getAddressPanel());
            playerToolBar.add(getLoadButton());
        }
        return playerToolBar;
    }

    public PlayerPanelPrefs getPrefs()
    {
        return prefs;
    }

    private JLabel getStatusBar()
    {
        if (statusBar == null)
        {
            statusBar = new JLabel();
            statusBar.setText(" "); // so it will lay out properly
        }
        return statusBar;
    }

    /**
     * This method initializes transportControlPanel
     *
     * @return net.sf.fmj.ui.control.TransportControlPanel
     */
    private SwingLookControlPanel getTransportControlPanel()
    {
        if (transportControlPanel == null)
        {
            transportControlPanel = getContainerPlayer()
                    .getTransportControlPanel();
        }
        return transportControlPanel;
    }

    /**
     * This method initializes videoPanel
     *
     * @return javax.swing.JPanel
     */
    public JPanel getVideoPanel()
    {
        if (videoPanel == null)
        {
            videoPanel = new JPanel();

            videoPanel.setLayout(new BorderLayout());
            // videoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            // TitledBorder
            // mediaBorder = new TitledBorder(
            // BorderConstants.etchedBorder, "Media" );
            // videoPanel.setBorder(mediaBorder);

            videoPanel.setBackground(SystemColor.controlShadow);
        }
        return videoPanel;
    }

    /**
     * This method initializes this
     *
     */
    private void initialize()
    {
        loadPrefs();

        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(363, 218));
        this.add(getPlayerToolBar(), BorderLayout.NORTH);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        this.add(southPanel, BorderLayout.SOUTH);

        southPanel.add(getTransportControlPanel(), BorderLayout.NORTH);
        southPanel.add(getStatusBar(), BorderLayout.SOUTH);

        this.add(getVideoPanel(), BorderLayout.CENTER);

    }

    /** load preferences */
    private void loadPrefs()
    {
        try
        {
            final File f = PlayerPanelPrefs.getFile();
            if (!f.exists())
            {
                logger.fine("FMJStudio prefs file does not exist.  Using defaults.");
                setDefaultPrefs();
                return;
            }
            FileReader reader = new FileReader(f);
            prefs = new PlayerPanelPrefs();
            prefs.load(reader);
        } catch (Exception e)
        {
            logger.warning("Problem loading FMJStudio prefs: " + e
                    + ".  Using defaults.");
            setDefaultPrefs();
        }
    }

    public void onAutoLoop(boolean value)
    {
        prefs.autoLoop = value;
        if (containerPlayer != null)
            containerPlayer.setAutoLoop(value);

        savePrefs();
    }

    public void onAutoPlay(boolean value)
    {
        logger.fine("onAutoPlay: " + value);
        prefs.autoPlay = value;
        savePrefs();
    }

    private void onLoadButtonClick()
    {
        String location = (String) getAddressComboBox().getSelectedItem();

        if (location.trim().equals(""))
        {
            showError("No URL specified");
            return;
        }

        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        try
        {
            getContainerPlayer().setMediaLocation(location, prefs.autoPlay);
        } catch (Throwable e)
        {
            logger.log(Level.WARNING, "" + e, e);
            showError("" + e); // normally we would just want e.getMessage(),
                               // but in many cases this can be blank, like a
                               // NoPlayerException.
                               // for now we'll just include the whole thing
                               // which includes the class. A better way would
                               // be to
                               // translate an empty NoPlayerException into
                               // "Player not found" - which BTW isn't that
                               // sensible to a user not familiar with JMF...

            return;
        } finally
        {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        // update prefs with new URL
        if (!prefs.recentUrls.contains(location))
        {
            prefs.recentUrls.add(0, location);
            savePrefs();
        } else
        {
            // in list, make sure it is first.
            if (!prefs.recentUrls.get(0).equals(location))
            {
                // not in first position. remove and re-add at head.
                prefs.recentUrls.remove(location);
                prefs.recentUrls.add(0, location);
                savePrefs();
            }
        }
    }

    public void onOpenCaptureDevice()
    {
        MediaLocator locator = CaptureDeviceBrowser.run(getParentFrame());
        if (locator != null)
        {
            addMediaLocatorAndLoad(locator.toExternalForm());
        }
    }

    public void onOpenFile()
    {
        final JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(PlayerPanel.this) == JFileChooser.APPROVE_OPTION)
        {
            final String urlStr = URLUtils.createUrlStr(chooser
                    .getSelectedFile());
            addMediaLocatorAndLoad(urlStr);
        }
    }

    public void onOpenURL()
    {
        String url = URLPanel.run(getParentFrame());
        if (url == null)
            return; // cancel
        addMediaLocatorAndLoad(url);
    }

    public void onReceiveRTP()
    {
        String url = RTPReceivePanel.run(getParentFrame());
        if (url == null)
            return; // cancel
        addMediaLocatorAndLoad(url);
    }

    public void onTranscode()
    {
        TranscodeWizard w = new TranscodeWizard(getParentFrame(),
                prefs.transcodeWizardConfig);
        boolean result = w.run();

        // store preferences if successful
        if (result)
        {
            containerPlayer
                    .setRealizedStartedProcessor(w.getResult().processor);

            prefs.transcodeWizardConfig = w.getConfig(); // TODO: this is the
                                                         // same object, we need
                                                         // to copy somewhere.
            savePrefs();
        }
    }

    public void onTransmitRTP()
    {
        RTPTransmitWizard w = new RTPTransmitWizard(getParentFrame(),
                prefs.rtpTransmitWizardConfig);
        boolean result = w.run();

        // store preferences if successful
        if (result)
        {
            containerPlayer
                    .setRealizedStartedProcessor(w.getResult().processor);

            prefs.rtpTransmitWizardConfig = w.getConfig(); // TODO: this is the
                                                           // same object, we
                                                           // need to copy
                                                           // somewhere.
            savePrefs();
        }

    }

    /** save preferences. */
    private void savePrefs()
    {
        try
        {
            FileWriter fileWriter = new FileWriter(PlayerPanelPrefs.getFile());
            prefs.write(fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e)
        {
            logger.log(Level.WARNING, "savePrefs failed: " + e, e);
        }
    }

    /** create default prefs and save them. */
    private void setDefaultPrefs()
    {
        prefs = new PlayerPanelPrefs();

        prefs.recentUrls.add("http://stream.lrz-muenchen.de:31337/m945-hq.ogg"); // internet
                                                                                 // ogg
                                                                                 // radio,
                                                                                 // munich
                                                                                 // student
                                                                                 // station....
        prefs.recentUrls.add("http://stream.lrz-muenchen.de:31337/m945-hq.mp3"); // internet
                                                                                 // mp3
                                                                                 // radio,
                                                                                 // munich
                                                                                 // student
                                                                                 // station....
        prefs.recentUrls.add("file://samplemedia/hen.mp3");
        prefs.recentUrls.add("file://samplemedia/lion_roar.mp3");
        prefs.recentUrls.add("file://samplemedia/betterway.wav"); // from EJMF
        prefs.recentUrls.add("file://samplemedia/issues.au"); // from EJMF
        prefs.recentUrls.add("file://samplemedia/gulp.wav"); // from EJMF
        prefs.recentUrls.add("file://samplemedia/gulp2.wav"); // from EJMF
        prefs.recentUrls.add("file://samplemedia/Gloria_Patri.ogg");
        prefs.recentUrls.add("file://samplemedia/safexmas.mov"); // from EJMF
        prefs.recentUrls
                .add("http://fmj.larsontechnologies.com/samplemedia/Apollo_15_liftoff_from_inside_LM.ogg"); // Apollo
                                                                                                            // 15
                                                                                                            // movie
                                                                                                            // from
                                                                                                            // wikimedia
        // prefs.recentUrls.add("http://upload.wikimedia.org/wikipedia/commons/d/d0/Apollo_15_liftoff_from_inside_LM.ogg");
        // // Apollo 15 movie from wikimedia
        prefs.recentUrls
                .add("http://www.surfshooterhawaii.com//cgi-bin/axispush555.cgi?dummy=garb"); // sample
                                                                                              // IP
                                                                                              // surf
                                                                                              // cam,
                                                                                              // streaming
                                                                                              // MJPG
        prefs.recentUrls.add("http://towercam.uu.edu/axis-cgi/mjpg/video.cgi"); // sample
                                                                                // IP
                                                                                // camera,
                                                                                // streaming
                                                                                // MJPG
        prefs.recentUrls.add("http://www.easylife.org/386dx/smells.mp3"); // just
                                                                          // being
                                                                          // silly
                                                                          // now

        prefs.rtpTransmitWizardConfig.url = "file://samplemedia/gulp2.wav";
        prefs.rtpTransmitWizardConfig.trackConfigs = new TrackConfig[] { new TrackConfig(
                true, new AudioFormat(AudioFormat.ULAW_RTP, 8000.0, 8, 1,
                        AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED)) };
        prefs.rtpTransmitWizardConfig.destUrl = "rtp://192.168.1.4:8000/audio/16"; // RTPUrlParser.parse("rtp://192.168.1.4:8000/audio/16");

        prefs.transcodeWizardConfig.url = "file://samplemedia/gulp2.wav";
        prefs.transcodeWizardConfig.contentDescriptor = new FileTypeDescriptor(
                FileTypeDescriptor.WAVE);
        prefs.transcodeWizardConfig.trackConfigs = new TrackConfig[] { new TrackConfig(
                true, new AudioFormat(AudioFormat.LINEAR, 8000.0, 8, 1, -1,
                        AudioFormat.UNSIGNED)) };
        prefs.transcodeWizardConfig.destUrl = URLUtils.createUrlStr(new File(
                PathUtils.getTempPath(), "gulp2-transcoded.wav"));
        // TODO: format

        savePrefs();

    }

    private void showError(String e)
    {
        JOptionPane.showConfirmDialog(this, e, "Error",
                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

    }

} // @jve:decl-index=0:visual-constraint="10,10"
