package net.sf.fmj.utility;

import java.util.*;
import java.util.logging.*;

import javax.media.*;

/**
 * Used to help recognize when JMF is in the classpath ahead of FMJ.
 *
 * @author Ken Larson
 *
 */
public final class ClasspathChecker
{
    private static final Logger logger = LoggerSingleton.logger;

    public static boolean check()
    {
        boolean result = true;

        if (!checkFMJPrefixInPackageManager())
        {
            // logger.warning("net.sf.fmj not found in PackageManager.getContentPrefixList() and PackageManager.getProtocolPrefixList(); is JMF ahead of FMJ in the classpath?");
            result = false;
        }

        if (!checkManagerImplementation())
        {
            // logger.warning("javax.media.Manager is JMF's implementation, not FMJ's; is JMF ahead of FMJ in the classpath?");
            result = false;
        }

        return result;
    }

    public static boolean checkAndWarn()
    {
        boolean result = true;

        if (!checkFMJPrefixInPackageManager())
        {
            logger.warning("net.sf.fmj not found in PackageManager.getContentPrefixList() and PackageManager.getProtocolPrefixList(); is JMF ahead of FMJ in the classpath?");
            result = false;
        }

        if (checkJMFInClassPath())
        {
            logger.info("JMF detected in classpath");
        }

        if (!checkManagerImplementation())
        {
            logger.warning("javax.media.Manager is JMF's implementation, not FMJ's; is JMF ahead of FMJ in the classpath?");
            result = false;
        }

        logger.info("javax.media.Manager version: " + Manager.getVersion());

        return result;

    }

    public static boolean checkFMJPrefixInPackageManager()
    {
        final Vector contentList = PackageManager.getContentPrefixList();
        if (!contentList.contains("net.sf.fmj"))
            return false;

        final Vector protocolList = PackageManager.getProtocolPrefixList();
        if (!protocolList.contains("net.sf.fmj"))
            return false;

        return true;

    }

    /**
     * Check if JMF is in the classpath (regardless of position).
     *
     * @return true if JMF is in the classpath.
     */
    public static boolean checkJMFInClassPath()
    {
        try
        {
            // just try a few:
            Class.forName("com.sun.media.BasicClock");
            Class.forName("com.sun.media.BasicCodec");
            Class.forName("com.sun.media.BasicConnector");
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    /**
     *
     * @return true if FMJ manager, false if JMF.
     */
    public static boolean checkManagerImplementation()
    {
        try
        {
            Manager.class.getField("FMJ_TAG");
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }
}
