package net.sf.fmj.media;

import java.io.*;

/**
 * A public static class to generate and write to fmj.log.
 */
public class Log
{
    public static boolean isEnabled = true;
    private static DataOutputStream log = null;
    public static String fileName = "fmj.log";
    private static int indent = 0;

    static
    {
        // This is to guard against the log file being opened twice at
        // the same time.
        synchronized (fileName)
        {
            // Check so we won't run this twice.
            if (isEnabled && log == null)
            {
                isEnabled = false; /* The default JMF value is false. */

                // Check the registry file to see if logging is turned on.
                Object llog = com.sun.media.util.Registry.get("allowLogging");

                if ((llog != null) && (llog instanceof Boolean))
                    isEnabled = ((Boolean) llog).booleanValue();

                if (isEnabled)
                {
                    isEnabled = false;
                    try
                    {
                        String dir;
                        Object ldir = com.sun.media.util.Registry
                                .get("secure.logDir");
                        if (ldir != null && ldir instanceof String
                                && !("".equals(ldir)))
                            dir = (String) ldir;
                        else
                            dir = System.getProperty("user.dir");

                        String file = null;
                        OutputStream out = null;
                        /*
                         * If the log file cannot be written, one may still make
                         * use of the log on the standard out/err.
                         */
                        if (dir != null)
                        {
                            file = dir + File.separator + fileName;
                            if (new File(file).canWrite())
                                out = new FileOutputStream(file);
                        }
                        if (out == null)
                        {
                            file = null;
                            out = System.err;
                        }
                        log = new DataOutputStream(out);
                        if (log != null)
                        {
                            System.err.println("Open log file: " + file);
                            isEnabled = true;
                            writeHeader();
                        }
                    } catch (Exception e)
                    {
                        System.err.println("Failed to open log file: " + e);
                    }
                }

            } // Don't need to run this twice.
        } // synchronized (fileName)
    }

    static boolean errorWarned = false;

    public static synchronized void comment(Object str)
    {
        if (isEnabled)
        {
            try
            {
                log.writeBytes("## " + str + "\n");
            } catch (IOException e)
            {
            }
        }
    }

    public static synchronized void decrIndent()
    {
        indent--;
    }

    public static synchronized void dumpStack(Throwable e)
    {
        if (isEnabled)
        {
            e.printStackTrace(new PrintWriter(log, true));
            write("");
        } else
            e.printStackTrace();
    }

    public static synchronized void error(Object str)
    {
        if (isEnabled)
        {
            if (!errorWarned)
            {
                System.err
                        .println("An error has occurred.  Check " + fileName + " for details.");
                errorWarned = true;
            }

            try
            {
                log.writeBytes("XX " + str + "\n");
            } catch (IOException e)
            {
            }
        } else
        {
            System.err.println(str);
        }
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
        if (isEnabled)
        {
            try
            {
                log.writeBytes("$$ " + str + "\n");
            } catch (IOException e)
            {
            }
        }
    }

    public static synchronized void setIndent(int i)
    {
        indent = i;
    }

    public static synchronized void warning(Object str)
    {
        if (isEnabled)
        {
            try
            {
                log.writeBytes("!! " + str + "\n");
            } catch (IOException e)
            {
            }
        }
    }

    public static synchronized void write(Object str)
    {
        if (isEnabled)
        {
            try
            {
                for (int i = indent; i > 0; i--)
                    log.writeBytes("    ");
                log.writeBytes(str + "\n");
            } catch (IOException e)
            {
            }
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
