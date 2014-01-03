package net.sf.fmj.registry;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.media.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * This is a registry of Plugins, Protocol prefixes, Content prefixes, and MIME
 * types. The registry may be serialized to an XML. The XML file is nominally
 * located in ${user.home}/.fmj.registry.xml
 *
 * This object is used by the PackageManager and the PluginManager for
 * persisting data across sessions.
 *
 * Currently the Registry does not store the supported input and output formats
 * for Plugins. This may be supported by adding CDATA sections that are
 * serialized Format objects. However, it would be good to be able to clear the
 * stored formats, and refresh the supported formats by introspecting the
 * Plugins. Sometimes the installed plugins may be updated, and the list of
 * supported formats may change for the same plugin class.
 *
 * Nevertheless, the present situation is that the PluginManager will need to
 * determine supported formats upon loading informatin from this Registry.
 *
 * TODO separate the persistence mechanism from this object, so that it may be
 * updated/plugged-in. TODO perhaps remove reliance on JDOM. Although JDOM makes
 * it easy to program, it is another jar to ship.
 *
 * @author Warren Bloomer
 * @author Ken Larson
 * @author Lyubomir Marinov
 */
public class Registry
{
    // TODO: what exactly is stored in the JMF registry properties? just the
    // class names, or the formats as well?
    // the properties files appears to be binary, an appears to be created using
    // java serialization.
    // seems like it contains the formats.

    /** logger for this class */
    private static final Logger logger = LoggerSingleton.logger;

    private static final int[] REGISTRY_FORMATS
        = new int[] { RegistryIOFactory.XML, RegistryIOFactory.PROPERTIES, };
    private static final int DEFAULT_REGISTRY_WRITE_FORMAT
        = RegistryIOFactory.XML; // for now, we only support writing to XML.

    /** the singleton registry object */
    private static Registry registry = null;
    private static Object registryMutex = new Object();

    private final RegistryContents registryContents = new RegistryContents();

    /**
     * JMF always re-adds javax to the content and protocol prefix lists if it
     * is removed. However, this is useless because there are no protocol
     * handlers or content handlers under javax.media in JMF or FMJ. It is just
     * a wasted to even check, especially in an applet. For strict
     * JMF-compatibility, this should be set to true.
     *
     * See also Manager.USE_MEDIA_PREFIX.
     */
    private static final boolean READD_JAVAX = false;

    public static final int NUM_PLUGIN_TYPES = 5;

    /**
     * The indicator which determines whether the commit of this
     * <tt>Registry</tt> to a file is disabled (in which case {@link #commit()}
     * does nothing).
     */
    private final boolean disableCommit;

    /**
     * The boolean system property which indicates whether <tt>Registry</tt> is
     * to be committed to a file (via {@link #commit()}). 
     */
    private static final String SYSTEM_PROPERTY_DISABLE_COMMIT
        = "net.sf.fmj.utility.JmfRegistry.disableCommit";

    /**
     * The boolean system property which indicates whether <tt>Registry</tt> is
     * to be loaded from a file at initialization time. 
     */
    private static final String SYSTEM_PROPERTY_DISABLE_LOAD
        = "net.sf.fmj.utility.JmfRegistry.disableLoad";

    /**
     * Get the singleton.
     *
     * @return The singleton JmfRegistry object.
     */
    public static Registry getInstance()
    {
        synchronized (registryMutex)
        {
            if (null == registry)
            {
                registry = new Registry();
            }
            return registry;
        }
    }

    // for unit tests, add
    // -Dnet.sf.fmj.utility.JmfRegistry.disableLoad=true
    // -Dnet.sf.fmj.utility.JmfRegistry.JMFDefaults=true

    /**
     * Private constructor.
     */
    private Registry()
    {
        String TRUE = Boolean.TRUE.toString();
        String FALSE = Boolean.FALSE.toString();

        disableCommit
            = System
                .getProperty(SYSTEM_PROPERTY_DISABLE_COMMIT, FALSE)
                    .equals(TRUE);
        try
        {
            if (System
                    .getProperty(SYSTEM_PROPERTY_DISABLE_LOAD, FALSE)
                        .equals(TRUE))
            {
                setDefaults(); // this capability needed for unit tests or
                               // applets
                return;
            }
        } catch (SecurityException e)
        { // ignore, we must be in an applet.
        }

        if (!load())
        {
            logger.fine("Using registry defaults.");
            setDefaults();
        }
    }

    public synchronized boolean addDevice(CaptureDeviceInfo newDevice)
    {
        return registryContents.captureDeviceInfoList.add(newDevice);

    }

    public synchronized void addMimeType(String extension, String type)
    {
        registryContents.mimeTable.addMimeType(extension, type);
    }

    /**
     * Write the registry to file.
     *
     */
    public synchronized void commit() throws IOException
    {
        if (disableCommit)
            return;

        final int registryFormat = DEFAULT_REGISTRY_WRITE_FORMAT;
        // write to registry file
        final File file = getRegistryFile(registryFormat);
        final FileOutputStream fos = new FileOutputStream(file);

        try
        {
            RegistryIOFactory
                .createRegistryIO(registryFormat, registryContents)
                    .write(fos);
            fos.flush();
        }
        finally
        {
            fos.close();
        }
        logger.info("Wrote registry file: " + file.getAbsolutePath());
    }

    @SuppressWarnings("unchecked")
    public synchronized Vector<String> getContentPrefixList()
    {
        return (Vector<String>) registryContents.contentPrefixList.clone();
    }

    /* ---------------- for PluginManager ---------------------- */

    public synchronized String getDefaultExtension(String mimeType)
    {
        return registryContents.mimeTable.getDefaultExtension(mimeType);
    }

    @SuppressWarnings("unchecked")
    public synchronized Vector<CaptureDeviceInfo> getDeviceList()
    {
        return
            (Vector<CaptureDeviceInfo>)
                registryContents.captureDeviceInfoList.clone();
    }

    /* --------- for PackageManager ----------------- */

    public synchronized List<String> getExtensions(String mimeType)
    {
        return registryContents.mimeTable.getExtensions(mimeType);
    }

    public synchronized Hashtable<String,String> getMimeTable()
    {
        return registryContents.mimeTable.getMimeTable();
    }

    public synchronized String getMimeType(String extension)
    {
        return registryContents.mimeTable.getMimeType(extension);
    }

    /**
     * pluginType = [1..NUM_PLUGIN_TYPES]
     */
    @SuppressWarnings("unchecked")
    public synchronized List<String> getPluginList(int pluginType)
    {
        // get the list of plugins of the given type
        Vector<String> pluginList = registryContents.plugins[pluginType - 1];

        return (List<String>) pluginList.clone();
    }

    /* ---------------- for mime-type --------------------- */
    @SuppressWarnings("unchecked")
    public synchronized Vector<String> getProtocolPrefixList()
    {
        return (Vector<String>) registryContents.protocolPrefixList.clone();
    }

    /**
     * Return the filepath of the registry file.
     */
    private File getRegistryFile(int registryFormat)
    {
        /** the name of the file used to store the registry */
        String filename
            = System.getProperty(
                    "net.sf.fmj.utility.JmfRegistry.filename",
                    (registryFormat == RegistryIOFactory.PROPERTIES)
                        ? ".fmj.registry.properties"
                        : ".fmj.registry.xml"); // allow override
        File file = new File(filename);

        if (!file.isAbsolute())
            file = new File(System.getProperty("user.home"), filename);
        return file;
    }

    /**
     * Get the registry resource stream.
     *
     * @return
     */
    private InputStream getRegistryResourceStream(int registryFormat)
    {
        return
            Registry.class.getResourceAsStream(
                    (registryFormat == RegistryIOFactory.PROPERTIES)
                        ? "/fmj.registry.properties"
                        : "/fmj.registry.xml");
    }

    private synchronized boolean load()
    {
        // try resource first:
        for (int registryFormat : REGISTRY_FORMATS)
        {
            if (loadFromResource(registryFormat))
                return true;
        }

        // then file:
        for (int registryFormat : REGISTRY_FORMATS)
        {
            if (loadFromFile(registryFormat))
                return true;
        }

        return false;

    }

    private synchronized boolean loadFromFile(int registryFormat)
    {
        try
        {
            final File f = getRegistryFile(registryFormat);
            if (f.isFile() && (f.length() > 0))
            {
                final FileInputStream fis = new FileInputStream(f);
                RegistryIOFactory.createRegistryIO(registryFormat,
                        registryContents).load(fis);
                logger.info(
                        "Loaded registry from file: " + f.getAbsolutePath());
                return true;
            }
        } catch (Throwable t)
        {
            logger.warning(
                    "Problem loading registry from file: " + t.getMessage());
        }
        return false;
    }

    private synchronized boolean loadFromResource(int registryFormat)
    {
        try
        {
            final InputStream is = getRegistryResourceStream(registryFormat);
            if (is == null)
                return false;

            RegistryIOFactory
                .createRegistryIO(registryFormat, registryContents)
                    .load(is);
            logger.info(
                    "Loaded registry from resource, format: "
                        + ((registryFormat == RegistryIOFactory.PROPERTIES)
                            ? "Properties"
                            : "XML"));
            return true;
        } catch (Throwable t)
        {
            logger.warning(
                    "Problem loading registry from resource: "
                        + t.getMessage());
            return false;
        }
    }

    /* ---------------- for CaptureDeviceManager --------------------- */

    public synchronized boolean removeDevice(CaptureDeviceInfo device)
    {
        return registryContents.captureDeviceInfoList.remove(device);
    }

    public synchronized boolean removeMimeType(String fileExtension)
    {
        return registryContents.mimeTable.removeMimeType(fileExtension);
    }

    /**
     * Prefices for determining Handlers for content of particular MIME types.
     *
     * MIME types are converted to package names, e.g. text/html -> text.html
     *
     * These package names are added to the prefices in this list to determine
     * Handlers for them. i.e. "<i>prefix</i>.media.content.text.html.Handler"
     */
    public synchronized void setContentPrefixList(List<String> list)
    {
        if (READD_JAVAX && !list.contains("javax"))
            list.add("javax");

        registryContents.contentPrefixList.clear();
        registryContents.contentPrefixList.addAll(list);
    }

    /* ------------------------- defaults ------------------------- */

    private void setDefaults()
    {
        final int flags = RegistryDefaults.getDefaultFlags();

        registryContents.protocolPrefixList.addAll(RegistryDefaults
                .protocolPrefixList(flags));
        registryContents.contentPrefixList.addAll(RegistryDefaults
                .contentPrefixList(flags));

        final List<Object> list = RegistryDefaults.plugInList(flags);
        for (Object o : list)
        {
            if (o instanceof PlugInInfo)
            {
                final PlugInInfo i = (PlugInInfo) o;
                registryContents.plugins[i.type - 1].add(i.className);
            } else
            {
                final PlugInInfo i = PlugInUtility.getPlugInInfo((String) o);
                if (i != null)
                    registryContents.plugins[i.type - 1].add(i.className);
            }
        }
    }

    /**
     * Plugin list of PluginInfo objects = { classname, inputFormats,
     * outputFormats, pluginType};
     *
     * @param pluginType
     *            range of [1..NUM_PLUGIN_TYPES]
     * @param plugins
     */
    public synchronized void setPluginList(int pluginType, List<String> plugins)
    {
        // use the plugin vector for the given type
        Vector<String> pluginList = registryContents.plugins[pluginType - 1];
        pluginList.clear();
        pluginList.addAll(plugins);
    }

    /**
     * Prefices for determining URL Handlers for content delivered via
     * particular protocol.
     *
     * Protocols are converted to package names, e.g. "http" -> "http" These
     * package names are added to the prefices in this list to determine
     * Handlers for them. i.e. "<i>prefix</i>.media.protocol.http.Handler"
     *
     * TODO perhaps use URLStreamHandlers
     *
     */
    public synchronized void setProtocolPrefixList(List<String> list)
    {
        if (READD_JAVAX && !list.contains("javax"))
            list.add("javax");

        registryContents.protocolPrefixList.clear();
        registryContents.protocolPrefixList.addAll(list);
    }
}
