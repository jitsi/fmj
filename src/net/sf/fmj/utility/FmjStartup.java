package net.sf.fmj.utility;

import java.util.logging.*;

import javax.swing.*;

import net.sf.fmj.media.*;

/**
 * Perform default initialization of FMJ/JMF/logging for an FMJ application.
 *
 * @author Ken Larson
 *
 */
public class FmjStartup
{
    private static final Logger logger = LoggerSingleton.logger;

    private static boolean initialized = false;

    public static boolean isApplet = false;

    public static final void init()
    {
        if (initialized)
            return;

        logger.info("OS: " + System.getProperty("os.name")); // just to help us
                                                             // with
                                                             // troubleshooting
                                                             // based on log
                                                             // files

        System.setProperty("java.util.logging.config.file",
                "logging.properties");
        try
        {
            LogManager.getLogManager().readConfiguration();
        } catch (Exception e)
        {
            logger.log(Level.SEVERE, "Unable to read logging configuration: "
                    + e, e);

            System.err.println("Unable to read logging configuration: " + e);
            e.printStackTrace(); // just to be sure, in case the log doesnt make
                                 // it
        }

        if (!ClasspathChecker.checkAndWarn())
        {
            // JMF is ahead of us in the classpath. Let's do some things to make
            // this go more smoothly.
            logger.info("Enabling JMF logging");
            if (!JmfUtility.enableLogging())
                logger.warning("Failed to enable JMF logging");

            // Let's register our own prefixes, etc, since they won't generally
            // be if JMF is in charge.
            logger.info("Registering FMJ prefixes and plugins with JMF");
            RegistryDefaults.registerAll(RegistryDefaults.FMJ
                    | RegistryDefaults.FMJ_NATIVE);
            // RegistryDefaults.unRegisterAll(RegistryDefaults.JMF); // TODO:
            // this can be used to make some things that work in FMJ but not in
            // JMF, work, like streaming mp3/ogg.
            // TODO: what about the removal of some/reordering?
        }

        // set system look and feel as standard for all FMJ apps:
        try
        {
            if (true)
                UIManager.setLookAndFeel(UIManager
                        .getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }

        initialized = true;
    }

    // TODO: reconcile with FmjApplet.init()
    public static final void initApplet()
    {
        if (initialized)
            return;
        // System.setProperty("java.util.logging.config.file",
        // "logging.properties");
        // try
        // {
        // LogManager.getLogManager().readConfiguration();
        // }
        // catch (Exception e)
        // {
        // logger.log(Level.SEVERE, "Unable to read logging configuration: " +
        // e, e);
        //
        // System.err.println("Unable to read logging configuration: " + e);
        // e.printStackTrace(); // just to be sure, in case the log doesnt make
        // it
        // }

        if (!ClasspathChecker.checkAndWarn())
        {
            // JMF is ahead of us in the classpath. Let's do some things to make
            // this go more smoothly.
            logger.info("Enabling JMF logging");
            if (!JmfUtility.enableLogging())
                logger.warning("Failed to enable JMF logging");

            // Let's register our own prefixes, etc, since they won't generally
            // be if JMF is in charge.
            logger.info("Registering FMJ prefixes and plugins with JMF");
            RegistryDefaults.registerAll(RegistryDefaults.FMJ);
            // RegistryDefaults.unRegisterAll(RegistryDefaults.JMF); // TODO:
            // this can be used to make some things that work in FMJ but not in
            // JMF, work, like streaming mp3/ogg.
            // TODO: what about the removal of some/reordering?
        }

        initialized = true;
    }
}
