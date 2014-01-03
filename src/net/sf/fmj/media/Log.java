package net.sf.fmj.media;

import java.util.logging.*;

/**
 * A public static class to generate and write to fmj.log.
 */
public class Log
{
    public static final boolean isEnabled; // The default JMF value is false.
    private static int indent = 0;

    /**
     * The Logger instance to be used.
     */
    private static Logger logger = Logger.getLogger(Log.class.getName());

    static
    {
        // Check the registry file to see if logging is turned on.
        Object allowLogging = com.sun.media.util.Registry.get("allowLogging");

        isEnabled
            = ((allowLogging != null) && (allowLogging instanceof Boolean))
                ? ((Boolean) allowLogging).booleanValue()
                : false;

        if (isEnabled)
            writeHeader();
    }

    public static synchronized void comment(Object str)
    {
        if (isEnabled && logger.isLoggable(Level.FINE))
            logger.fine((str!=null ? str.toString() : "null"));
    }

    public static synchronized void info(Object str)
    {
        if (isEnabled && logger.isLoggable(Level.INFO))
            logger.info((str!=null ? str.toString() : "null"));
    }

    public static synchronized void decrIndent()
    {
        indent--;
    }

    public static synchronized void dumpStack(Throwable e)
    {
        if (isEnabled && logger.isLoggable(Level.FINE))
        {
            for(StackTraceElement s : e.getStackTrace())
                logger.fine(s.toString());
        }
    }

    public static synchronized void error(Object str)
    {
        if (isEnabled && logger.isLoggable(Level.SEVERE))
            logger.severe((str!=null ? str.toString() : "null"));
        else
            System.err.println(str);
    }

    public static int getIndent()
    {
        return indent;
    }

    public static synchronized void incrIndent()
    {
        indent++;
    }

    public static synchronized void profile(Object str)
    {
        if (isEnabled && logger.isLoggable(Level.FINER))
            logger.finer((str!=null ? str.toString() : "null"));
    }

    public static synchronized void setIndent(int i)
    {
        indent = i;
    }

    public static synchronized void warning(Object str)
    {
        if (isEnabled && logger.isLoggable(Level.WARNING))
            logger.warning((str!=null ? str.toString() : "null"));
    }

    public static synchronized void write(Object str)
    {
        if (isEnabled && logger.isLoggable(Level.FINE))
        {
            StringBuilder sb = new StringBuilder();
            for (int i = indent; i > 0; i--)
                sb.append("    ");
            sb.append(str!=null ? str.toString() : "null");
            logger.fine(sb.toString());
        }
    }

    private static synchronized void writeHeader()
    {
        write("#\n# FMJ\n#\n");

        String os = null, osver = null, osarch = null;
        String java = null, jver = null;
        try
        {
            os = System.getProperty("os.name");
            osarch = System.getProperty("os.arch");
            osver = System.getProperty("os.version");
            java = System.getProperty("java.vendor");
            jver = System.getProperty("java.version");
        } catch (Throwable e)
        {
            // Can't get the info. No big deal.
            return;
        }

        if (os != null)
            comment("Platform: " + os + ", " + osarch + ", " + osver);
        if (java != null)
            comment("Java VM: " + java + ", " + jver);
        write("");
    }
}
