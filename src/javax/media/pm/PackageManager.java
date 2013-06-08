package javax.media.pm;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import net.sf.fmj.registry.*;
import net.sf.fmj.utility.*;

/**
 * Internal implementation of javax.media.PackageManager.
 *
 * @author Ken Larson
 *
 */
public final class PackageManager extends javax.media.PackageManager
{
    private static final Logger logger = LoggerSingleton.logger;

    // TODO: should we synchronize?
    // TODO: what other kinds of classes are located using this manager?

    private static Registry registry = Registry.getInstance();

    public static synchronized void commitContentPrefixList()
    {
        try
        {
            registry.commit();
        } catch (Exception e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }
    }

    public static synchronized void commitProtocolPrefixList()
    {
        try
        {
            registry.commit();
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }
    }

    public static synchronized Vector<String> getContentPrefixList()
    {
        return registry.getContentPrefixList();
    }

    public static synchronized Vector<String> getProtocolPrefixList()
    {
        return registry.getProtocolPrefixList();
    }

    public static synchronized void setContentPrefixList(Vector list)
    {
        registry.setContentPrefixList(list);
    }

    public static synchronized void setProtocolPrefixList(Vector list)
    {
        registry.setProtocolPrefixList(list);
    }

    public PackageManager()
    { // nothing to do
    }
}
