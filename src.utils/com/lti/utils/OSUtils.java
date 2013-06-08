package com.lti.utils;

import java.util.logging.*;

/**
 * Helper functions to determine which OS we are running.
 *
 * @author Ken Larson
 *
 */
public final class OSUtils
{
    private static final Logger logger = Logger.global;

    public static final boolean isLinux()
    {
        return System.getProperty("os.name").equals("Linux");
    }

    public static final boolean isMacOSX()
    {
        return System.getProperty("os.name").equals("Mac OS X");
    }

    public static final boolean isSolaris()
    {
        return System.getProperty("os.name").equals("SunOS");
    }

    public static final boolean isWindows()
    {
        return System.getProperty("os.name").startsWith("Windows");
    }

    private OSUtils()
    {
        super();
        logger.fine("OS: " + System.getProperty("os.name"));
    }

}
