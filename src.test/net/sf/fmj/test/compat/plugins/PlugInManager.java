package net.sf.fmj.test.compat.plugins;

import java.util.*;

import javax.media.*;

/**
 * Copy of javax.media.pim.PlugInManager, used to test.
 * 
 * @author Ken Larson
 * 
 */
public class PlugInManager extends javax.media.PlugInManager
{
    // TODO: what exactly is stored in the properties? just the class names, or
    // the formats as well?
    // the properties files appears to be binary, an appears to be created using
    // java serialization.
    // seems like it contains the formats.
    // TODO: implement efficiently using maps
    // Vectors of PlugInInfo.
    private static final Vector[] vectors = new Vector[] { new Vector(),
            new Vector(), new Vector(), new Vector(), new Vector() };
    private static final Vector[] filteredVectors = new Vector[] { null, null,
            null, null, null };

    static
    {
        PlugInManagerInitializer.init(); // populate initial values
    }

    public static synchronized boolean addPlugIn(String classname, Format[] in,
            Format[] out, int type)
    {
        try
        {
            Class.forName(classname);
        } catch (ClassNotFoundException e)
        {
            return false; // class does not exist.
        } catch (Throwable t)
        {
            System.err.println("Unable to addPlugIn for " + classname
                    + " due to inability to get its class: " + t);
            t.printStackTrace();
            return false;
        }
        if (find(classname, type) != null)
            return false; // already there.

        final PlugInInfo plugInInfo = new PlugInInfo(classname, in, out);

        final boolean result = vectors[type - 1].add(plugInInfo);
        if (filteredVectors[type - 1] != null)
        {
            filteredVectors[type - 1].add(plugInInfo);
        }
        return result;

    }

    public static synchronized void commit() throws java.io.IOException
    {
        System.err.println("PlugInManager.commit not implemented"); // TODO
    }

    private static synchronized PlugInInfo find(String classname, int type)
    {
        final Iterator i = vectors[type - 1].iterator();
        boolean found = false;
        while (i.hasNext())
        {
            PlugInInfo plugInInfo = (PlugInInfo) i.next();
            if (plugInInfo.className.equals(classname))
            {
                return plugInInfo;
            }
        }
        return null;
    }

    public static synchronized Vector getPlugInList(Format input,
            Format output, int type)
    {
        final Vector v = getVector(type);

        final Vector result = new Vector();
        for (int i = 0; i < v.size(); ++i)
        {
            final PlugInInfo plugInInfo = (PlugInInfo) v.get(i);

            if (input != null)
            {
                if (plugInInfo.inputFormats == null)
                    continue;
                boolean match = false;
                for (int j = 0; j < plugInInfo.inputFormats.length; ++j)
                {
                    if (input.matches(plugInInfo.inputFormats[j]))
                    {
                        match = true;
                        break;
                    }
                }
                if (!match)
                    continue;
            }

            if (output != null)
            {
                if (plugInInfo.outputFormats == null)
                    continue;
                boolean match = false;
                for (int j = 0; j < plugInInfo.outputFormats.length; ++j)
                {
                    if (output.matches(plugInInfo.outputFormats[j]))
                    {
                        match = true;
                        break;
                    }
                }
                if (!match)
                    continue;
            }

            result.add(plugInInfo.className);
        }

        return result;

    }

    public static synchronized Format[] getSupportedInputFormats(
            String className, int type)
    {
        final PlugInInfo pi = find(className, type);
        if (pi == null)
            return null;
        return pi.inputFormats;
    }

    public static synchronized Format[] getSupportedOutputFormats(
            String className, int type)
    {
        final PlugInInfo pi = find(className, type);
        if (pi == null)
            return null;
        return pi.outputFormats;

    }

    private static Vector getVector(int type)
    {
        if (filteredVectors[type - 1] != null)
            return filteredVectors[type - 1];

        return vectors[type - 1];

    }

    public static synchronized boolean removePlugIn(String classname, int type)
    {
        final Iterator i = vectors[type - 1].iterator();
        boolean found = false;
        while (i.hasNext())
        {
            PlugInInfo plugInInfo = (PlugInInfo) i.next();
            if (plugInInfo.className.equals(classname))
            {
                i.remove();
                found = true;
            }
        }
        return found;

    }

    // according to the docs, sets the search order. does not appear to add new
    // plugins.
    public static synchronized void setPlugInList(Vector plugins, int type)
    {
        final Vector vNew = new Vector();
        for (int i = 0; i < plugins.size(); ++i)
        {
            final String s = (String) plugins.get(i);
            final PlugInInfo plugInInfo = find(s, type);
            if (plugInInfo != null)
            {
                vNew.add(plugInInfo);
            }

        }
        filteredVectors[type - 1] = vNew;
    }

    public PlugInManager()
    {
    }
}
