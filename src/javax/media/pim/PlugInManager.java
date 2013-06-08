package javax.media.pim;

import java.util.*;
import java.util.logging.*;

import javax.media.*;

import net.sf.fmj.registry.*;
import net.sf.fmj.utility.*;

/**
 * Internal implementation of javax.media.PlugInManager. In progress.
 *
 * @author Ken Larson
 */
public class PlugInManager extends javax.media.PlugInManager
{
    private static final Logger logger = LoggerSingleton.logger;

    private static boolean TRACE = false;

    /**
     * The registry that persists the data.
     */
    private static Registry registry = Registry.getInstance();

    /**
     * Maps of classnames to PluginInfo
     */
    @SuppressWarnings("unchecked")
    private static final HashMap<String, PlugInInfo>[] pluginMaps
        = new HashMap[]
                {
                    new HashMap<String, PlugInInfo>(),
                    new HashMap<String, PlugInInfo>(),
                    new HashMap<String, PlugInInfo>(),
                    new HashMap<String, PlugInInfo>(),
                    new HashMap<String, PlugInInfo>(),
                };

    static
    {
        // populate hash maps with info from the persisted registry
        for (int i = 0; i < Registry.NUM_PLUGIN_TYPES; i++)
        {
            final List<String> classList = registry.getPluginList(i + 1);
            HashMap<String, PlugInInfo> pluginMap = pluginMaps[i];

            for (String className : classList)
            {
                // registry only contains classnames, not in and out formats
                final PlugInInfo info = getPluginInfo(className);
                if (info != null)
                {
                    pluginMap.put(info.className, info);
                }
            }
        }
    }

    public static synchronized boolean addPlugIn(String classname, Format[] in,
            Format[] out, int type)
    {
        try
        {
            Class.forName(classname);
        } catch (ClassNotFoundException e)
        {
            logger.finer("addPlugIn failed for nonexistant class: " + classname);
            return false; // class does not exist.
        } catch (Throwable t)
        {
            logger.log(Level.WARNING, "Unable to addPlugIn for " + classname
                    + " due to inability to get its class: " + t, t);
            return false;
        }

        if (find(classname, type) != null)
        {
            return false; // already there.
        }

        final PlugInInfo plugInInfo = new PlugInInfo(classname, in, out);

        final List<String> classList = registry.getPluginList(type);
        final HashMap<String, PlugInInfo> pluginMap = pluginMaps[type - 1];

        // add to end of ordered list
        classList.add(classname);

        // add to PluginInfo map
        pluginMap.put(classname, plugInInfo);

        registry.setPluginList(type, classList);

        return true;
    }

    public static synchronized void commit() throws java.io.IOException
    {
        registry.commit();
    }

    private static synchronized PlugInInfo find(String classname, int type)
    {
        PlugInInfo info = pluginMaps[type - 1].get(classname);

        return info;
    }

    private static final PlugInInfo getPluginInfo(String pluginName)
    {
        final Object pluginObject;

        try
        {
            pluginObject = Class.forName(pluginName).newInstance();
        } catch (Throwable t)
        {
            if (t instanceof ThreadDeath)
                throw (ThreadDeath) t;
            else
            {
                logger.fine("Problem loading plugin " + pluginName + ": " + t);
                return null;
            }
        }

        final Format[] in;
        final Format[] out;

        if (pluginObject instanceof Demultiplexer)
        {
            Demultiplexer demux = (Demultiplexer) pluginObject;
            in = demux.getSupportedInputContentDescriptors();
            out = null;
        } else if (pluginObject instanceof Codec)
        {
            Codec codec = (Codec) pluginObject;
            in = codec.getSupportedInputFormats();
            out = codec.getSupportedOutputFormats(null);
        } else if (pluginObject instanceof Multiplexer)
        {
            Multiplexer mux = (Multiplexer) pluginObject;
            in = mux.getSupportedInputFormats();
            out = mux.getSupportedOutputContentDescriptors(null);
        } else if (pluginObject instanceof Renderer)
        {
            Renderer renderer = (Renderer) pluginObject;
            in = renderer.getSupportedInputFormats();
            out = null;
        } else if (pluginObject instanceof Effect)
        {
            Effect effect = (Effect) pluginObject;
            in = effect.getSupportedInputFormats();
            out = effect.getSupportedOutputFormats(null);
        } else
        {
            logger.warning("Unknown plugin type: " + pluginObject
                    + " for plugin " + pluginName);
            return null;
        }

        return new PlugInInfo(pluginName, in, out);
    }

    /**
     * Get a list of plugins that match the given input and output formats.
     *
     * @param input
     * @param output
     * @param type
     * @return A Vector of classnames
     */
    public static synchronized Vector<String> getPlugInList(Format input,
            Format output, int type)
    {
        if (TRACE)
            logger.info("getting plugin list...");
        if (!isValid(type))
        {
            return new Vector<String>();
        }

        final Vector<String> result = new Vector<String>();
        final Vector<String> classList = getVector(type);
        final HashMap<String,PlugInInfo> pluginMap = pluginMaps[type - 1];

        for (int i = 0; i < classList.size(); ++i)
        {
            final String classname = classList.get(i);
            final PlugInInfo plugInInfo = pluginMap.get(classname);
            if (plugInInfo == null)
                continue;

            if (input != null)
            {
                if (plugInInfo.inputFormats == null)
                {
                    continue;
                }
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
                {
                    continue;
                }
            }

            if (output != null)
            {
                if (plugInInfo.outputFormats == null)
                {
                    continue;
                }
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
                {
                    continue;
                }
            }

            // matched both input and output formats
            result.add(plugInInfo.className);
        }

        return result;
    }

    public static synchronized Format[] getSupportedInputFormats(
            String className, int type)
    {
        final PlugInInfo pi = find(className, type);
        if (pi == null)
        {
            return null;
        }
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

    private static Vector<String> getVector(int type)
    {
        if (!isValid(type))
        {
            return null;
        }
        final List<String> classList = registry.getPluginList(type);
        final Vector<String> result = new Vector<String>();
        result.addAll(classList);
        return result;
    }

    private static boolean isValid(int type)
    {
        return type >= 1 && type <= Registry.NUM_PLUGIN_TYPES;
    }

    public static synchronized boolean removePlugIn(String classname, int type)
    {
        final List<String> classList = registry.getPluginList(type);
        final HashMap<String, PlugInInfo> pluginMap = pluginMaps[type - 1];

        final boolean result = classList.remove(classname)
                | (pluginMap.remove(classname) != null); // don't uses shortcut
                                                         // || because we want
                                                         // to remove from both

        registry.setPluginList(type, classList);

        return result;
    }

    /**
     * according to the docs, sets the search order. does not appear to add new
     * plugins.
     *
     * @param plugins
     * @param type
     */
    public static synchronized void setPlugInList(Vector plugins, int type)
    {
        registry.setPluginList(type, plugins);
    }

    /**
     * Private constructor so that is can not be constructed. In JMF it is
     * public, but this is an implementation detail that is not important for
     * FMJ compatibility.
     */
    private PlugInManager()
    {
        super();
    }
}
