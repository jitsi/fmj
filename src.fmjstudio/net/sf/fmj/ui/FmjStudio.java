package net.sf.fmj.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.sf.fmj.ui.application.*;
import net.sf.fmj.ui.dialogs.*;
import net.sf.fmj.ui.registry.*;
import net.sf.fmj.utility.*;

import com.lti.utils.*;

/**
 * Main class for the FMJ media player application.
 *
 * @author Warren Bloomer
 * @author Ken Larson
 *
 */
public class FmjStudio
{
    /**
     * Main method.
     *
     * @param args
     *            the arguments pased to this application
     */
    public static void main(String[] args)
    {
        FmjStartup.init(); // initialize default FMJ/JMF/logging

        // see http://developer.apple.com/technotes/tn/tn2031.html
        // see http://java.sun.com/developer/technicalArticles/JavaLP/JavaToMac/
        // It doesn't seem to work to set these in code, they have to be set by
        // the calling environment
        if (OSUtils.isMacOSX())
        {
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    "FMJ Studio");
            // System.setProperty("com.apple.mrj.application.growbox.intrudes",
            // "false"); // doesn't seem to work
        }

        //
        FmjStudio main = new FmjStudio();
        main.run(args);
    }

    private JFrame frame;
    private JFrame registryFrame;

    private PlayerPanel playerPanel;

    private final MenuBar getMenuBar()
    {
        MenuBar menuBar = new MenuBar();

        final Menu menuFile = new Menu();
        menuFile.setLabel("File");
        menuBar.add(menuFile);

        // File>Open File
        {
            final MenuItem menuItemOpenFile = new MenuItem("Open File...");
            menuItemOpenFile.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    playerPanel.onOpenFile();
                }
            });
            menuFile.add(menuItemOpenFile);
        }

        // File>Open URL
        {
            final MenuItem menuItemOpenURL = new MenuItem("Open URL...");
            menuItemOpenURL.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    playerPanel.onOpenURL();
                }
            });
            menuFile.add(menuItemOpenURL);
        }

        // File>Open RTP Session
        {
            final MenuItem menuItemReceiveRTP = new MenuItem(
                    "Open RTP Session...");
            menuItemReceiveRTP.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    playerPanel.onReceiveRTP();
                }
            });
            menuFile.add(menuItemReceiveRTP);
        }

        // File>Capture
        {
            final MenuItem menuItemOpenCaptureDevice = new MenuItem(
                    "Capture...");
            menuItemOpenCaptureDevice.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    playerPanel.onOpenCaptureDevice();
                }
            });
            menuFile.add(menuItemOpenCaptureDevice);
        }

        // separator
        menuFile.addSeparator();

        // TODO: export

        // File>Transmit RTP
        {
            final MenuItem menuItemTransmitRTP = new MenuItem("Transmit RTP...");
            menuItemTransmitRTP.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    playerPanel.onTransmitRTP();
                }
            });
            menuFile.add(menuItemTransmitRTP);
        }

        // File>Transcode
        {
            final MenuItem menuItemTranscode = new MenuItem("Transcode...");
            menuItemTranscode.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    playerPanel.onTranscode();
                }
            });
            menuFile.add(menuItemTranscode);
        }

        // separator
        menuFile.addSeparator();

        // File>Registry Editor
        {
            final MenuItem menuItemRegistryEditor = new MenuItem(
                    "Registry Editor...");
            menuItemRegistryEditor.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    onOpenRegistryEditor();
                }
            });
            menuFile.add(menuItemRegistryEditor);
        }

        // File>Exit
        {
            final MenuItem menuItemExit = new MenuItem("Exit");
            menuItemExit.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    onExit();
                }
            });
            menuFile.add(menuItemExit);
        }

        final Menu menuPlayer = new Menu();
        menuPlayer.setLabel("Player");
        menuBar.add(menuPlayer);

        // Player>Auto-play
        {
            final CheckboxMenuItem menuItemAutoPlay = new CheckboxMenuItem(
                    "Auto-play");
            menuItemAutoPlay.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    playerPanel.onAutoPlay(menuItemAutoPlay.getState());
                }

            });

            menuPlayer.add(menuItemAutoPlay);
            menuItemAutoPlay.setState(playerPanel.getPrefs().autoPlay);
        }

        // Player>Auto-loop
        {
            final CheckboxMenuItem menuItemAutoLoop = new CheckboxMenuItem(
                    "Auto-loop");
            menuItemAutoLoop.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    playerPanel.onAutoLoop(menuItemAutoLoop.getState());
                }

            });
            menuPlayer.add(menuItemAutoLoop);
            playerPanel.onAutoLoop(false); // TODO: not working right yet.
            menuItemAutoLoop.setState(playerPanel.getPrefs().autoLoop);
            menuItemAutoLoop.setEnabled(false); // TODO: not working right yet.

        }

        final Menu menuHelp = new Menu();
        menuHelp.setLabel("Help");
        menuBar.add(menuHelp);

        // Help>About
        {
            final MenuItem menuItemHelpAbout = new MenuItem("About...");
            menuItemHelpAbout.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    AboutPanel.run(frame);
                }
            });

            menuHelp.add(menuItemHelpAbout);

        }

        return menuBar;
    }

    private void onExit()
    {
        if (registryFrame != null)
            registryFrame.dispose();
        frame.dispose();
    }

    /**
     * Display the registry editor frame
     *
     */
    private void onOpenRegistryEditor()
    {
        if (registryFrame == null)
        {
            registryFrame = new JFrame("Registry Editor");
            RegistryEditorPanel panel = new RegistryEditorPanel();

            Container contentPane = registryFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(panel, BorderLayout.CENTER);

            // frame.setMinimumSize(new Dimension(480, 320)); // doesn't seem to
            // have any effect (at least in linux), and is not 1.4-compatible
            // anyway.
            registryFrame.setSize(640, 480);
        }
        registryFrame.setVisible(true);
    }

    /**
     *
     * @param args
     */
    private void run(String[] args)
    {
        frame = new JFrame("FMJ Studio");
        frame.setSize(new Dimension(640, 480));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        playerPanel = new PlayerPanel();
        contentPane.add(playerPanel);

        frame.setMenuBar(getMenuBar());

        // Resize frame whenever new Component is added
        playerPanel.getVideoPanel().addContainerListener(
                new ContainerListener()
                {
                    public void componentAdded(ContainerEvent e)
                    {
                        frame.pack();
                    }

                    public void componentRemoved(ContainerEvent e)
                    {
                        frame.pack();
                    }
                });

        frame.setVisible(true);

        if (args.length > 0)
        {
            // URL is first arg
            final String url = args[0];
            playerPanel.addMediaLocatorAndLoad(url);
        }
    }

}
