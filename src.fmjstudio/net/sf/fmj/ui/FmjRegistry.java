package net.sf.fmj.ui;

import java.awt.*;
import java.util.logging.*;

import javax.swing.*;

import net.sf.fmj.ui.registry.*;
import net.sf.fmj.utility.*;

/**
 * FMJ Registry editor program.
 *
 * @author Warren Bloomer
 * @author Ken Larson
 *
 */
public class FmjRegistry
{
    private static final Logger logger = LoggerSingleton.logger;

    public static void main(String[] args)
    {
        FmjStartup.init(); // initialize default FMJ/JMF/logging

        if (false)
        {
            try
            {
                UIManager.setLookAndFeel(UIManager
                        .getSystemLookAndFeelClassName());
            } catch (Exception e)
            {
                logger.log(Level.WARNING, "Unable to set Swing look and feel: "
                        + e, e);

            }
        }

        try
        {
            RegistryEditorPanel panel = new RegistryEditorPanel();

            JFrame frame = new JFrame("Registry Editor");
            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(panel, BorderLayout.CENTER);

            // frame.setMinimumSize(new Dimension(480, 320)); // doesn't seem to
            // have any effect (at least in linux), and is not 1.4-compatible
            // anyway.
            frame.setSize(640, 480);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }
    }
}
