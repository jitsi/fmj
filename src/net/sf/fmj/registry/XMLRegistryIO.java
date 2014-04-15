package net.sf.fmj.registry;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import net.sf.fmj.utility.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Implementation of RegistryIO using XML.
 *
 * @author Ken Larson
 * @author Warren Bloomer
 *
 */
class XMLRegistryIO implements RegistryIO
{
    private static final Logger logger = LoggerSingleton.logger;

    private final RegistryContents registryContents;

    /** version of the registry file format */
    private static final String version = "0.1";

    private static final String ELEMENT_REGISTRY = "registry";

    /* ---------------------- XML operations --------------------- */

    private static final String ATTR_VERSION = "version";
    private static final String ELEMENT_PROTO_PREFIX = "protocol-prefixes";

    private static final String ELEMENT_CONTENT_PREFIX = "content-prefixes";
    private static final String ELEMENT_PLUGINS = "plugins";
    private static final String ELEMENT_MIMETYPES = "mime-types";
    private static final String ELEMENT_MIMETYPE = "type";
    private static final String ELEMENT_CAPTURE_DEVICES = "capture-devices";
    private static final String ELEMENT_CODECS = "codecs";

    private static final String ELEMENT_DEMUXES = "demuxes";
    private static final String ELEMENT_MUXES = "muxes";
    private static final String ELEMENT_EFFECTS = "effects";
    private static final String ELEMENT_RENDERERS = "renderers";
    private static final String ELEMENT_PREFIX = "prefix";

    private static final String ELEMENT_CLASS = "class";
    private static final String ELEMENT_DEVICE = "device";

    private static final String ELEMENT_DEVICE_NAME = "name";
    private static final String ELEMENT_DEVICE_LOCATOR = "locator";
    private static final String ELEMENT_DEVICE_FORMAT = "format";
    private static final String ELEMENT_DEVICE_FORMAT_CLASS = "class";
    private static final String ELEMENT_DEVICE_FORMAT_DESCRIPTION = "description";
    private static final String ELEMENT_DEVICE_FORMAT_SERIALIZED = "serialized";

    public XMLRegistryIO(RegistryContents registryContents)
    {
        super();
        this.registryContents = registryContents;
    }

    /**
     * Builds a Document from the registry data structures.
     *
     * @return the Document.
     * @throws IOException
     */
    private Document buildDocument() throws IOException
    {
        Document document;

        try
        {
            document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException pce)
        {
            IOException ioe = new IOException();

            ioe.initCause(pce);
            throw ioe;
        }

        Element rootElement = document.createElement(ELEMENT_REGISTRY);
        rootElement.setAttribute(ATTR_VERSION, version);

        document.appendChild(rootElement);

        rootElement.appendChild(getPluginsElement(document));
        rootElement.appendChild(getContentElement(document));
        rootElement.appendChild(getProtocolElement(document));
        rootElement.appendChild(getMimeElement(document));
        rootElement.appendChild(getCaptureDeviceElement(document));

        return document;
    }

    private Element getCaptureDeviceElement(Document document)
            throws IOException
    {
        final Element captureDeviceElement = document
                .createElement(ELEMENT_CAPTURE_DEVICES);
        final Iterator<CaptureDeviceInfo> iter = registryContents.captureDeviceInfoList
                .iterator();
        while (iter.hasNext())
        {
            final CaptureDeviceInfo info = iter.next();

            if (info.getLocator() == null)
                continue; // should never be null, only seems to be null due to
                          // some unit tests.

            final Element deviceElement = document
                    .createElement(ELEMENT_DEVICE);
            {
                final Element deviceNameElement = document
                        .createElement(ELEMENT_DEVICE_NAME);
                deviceNameElement.setTextContent(info.getName());
                deviceElement.appendChild(deviceNameElement);
            }

            {
                final Element e = document
                        .createElement(ELEMENT_DEVICE_LOCATOR);
                e.setTextContent(info.getLocator().toExternalForm());
                deviceElement.appendChild(e);
            }
            {
                final javax.media.Format[] formats = info.getFormats();
                for (int i = 0; i < formats.length; ++i)
                {
                    final Element formatElement = document
                            .createElement(ELEMENT_DEVICE_FORMAT);

                    {
                        final Element e2 = document
                                .createElement(ELEMENT_DEVICE_FORMAT_CLASS); // for
                                                                             // XML
                                                                             // readability
                                                                             // only
                        e2.setTextContent(formats[i].getClass().getName());
                        formatElement.appendChild(e2);
                    }

                    {
                        final Element e2 = document
                                .createElement(ELEMENT_DEVICE_FORMAT_DESCRIPTION); // for
                                                                                   // XML
                                                                                   // readability
                                                                                   // only
                        e2.setTextContent(formats[i].toString());
                        formatElement.appendChild(e2);
                    }
                    // TODO: perhaps "known" formats like RGBFormat could be
                    // serialized much more nicely.
                    // we have to use serialization because that is the only way
                    // to support JMF-compatible subclasses
                    // that are not in JMF.
                    {
                        final Element e2 = document
                                .createElement(ELEMENT_DEVICE_FORMAT_SERIALIZED);
                        e2.setTextContent(SerializationUtils
                                .serialize(formats[i]));
                        formatElement.appendChild(e2);
                    }

                    deviceElement.appendChild(formatElement);
                }
            }

            captureDeviceElement.appendChild(deviceElement);
        }
        return captureDeviceElement;
    }

    private Element getChild(Element element, String name)
    {
        NodeList childNodes = element.getChildNodes();
        int childNodeCount = childNodes.getLength();

        for (int i = 0; i < childNodeCount; i++)
        {
            Node childNode = childNodes.item(i);

            if ((childNode.getNodeType() == Node.ELEMENT_NODE)
                    && childNode.getNodeName().equals(name))
                return (Element) childNode;
        }
        return null;
    }

    private List<Element> getChildren(Element element, String name)
    {
        NodeList childNodes = element.getChildNodes();
        int childNodeCount = childNodes.getLength();
        List<Element> children = new ArrayList<Element>(childNodeCount);

        for (int i = 0; i < childNodeCount; i++)
        {
            Node childNode = childNodes.item(i);

            if ((childNode.getNodeType() == Node.ELEMENT_NODE)
                    && childNode.getNodeName().equals(name))
                children.add((Element) childNode);
        }
        return children;
    }

    private Element getCodecElement(Document document)
    {
        return getPluginElement(PlugInManager.CODEC, ELEMENT_CODECS, document);
    }

    private Element getContentElement(Document document)
    {
        Element contentElement = document.createElement(ELEMENT_CONTENT_PREFIX);

        Iterator<String> prefixIter
            = registryContents.contentPrefixList.iterator();
        while (prefixIter.hasNext())
        {
            String prefix = prefixIter.next();
            Element prefixElement = document.createElement(ELEMENT_PREFIX);
            prefixElement.setTextContent(prefix);
            contentElement.appendChild(prefixElement);
        }

        return contentElement;
    }

    private Element getDemuxElement(Document document)
    {
        return getPluginElement(PlugInManager.DEMULTIPLEXER, ELEMENT_DEMUXES,
                document);
    }

    private Element getEffectElement(Document document)
    {
        return getPluginElement(PlugInManager.EFFECT, ELEMENT_EFFECTS, document);
    }

    private Element getMimeElement(Document document)
    {
        Element mimeElement = document.createElement(ELEMENT_MIMETYPES);

        Iterator<String> typesIterator
            = registryContents.mimeTable.getMimeTypes().iterator();
        while (typesIterator.hasNext())
        {
            String type = typesIterator.next();
            List<String> extensions
                = registryContents.mimeTable.getExtensions(type);

            Element typeElement = document.createElement(ELEMENT_MIMETYPE);
            typeElement.setAttribute("value", type);
            typeElement.setAttribute(
                    "default-ext",
                    registryContents.mimeTable.getDefaultExtension(type));
            mimeElement.appendChild(typeElement);

            for (int i = 0; i < extensions.size(); ++i)
            {
                String ext = extensions.get(i);
                Element extElement = document.createElement("ext");
                extElement.setTextContent(ext);
                typeElement.appendChild(extElement);
            }
        }

        return mimeElement;
    }

    private Element getMuxElement(Document document)
    {
        return getPluginElement(PlugInManager.MULTIPLEXER, ELEMENT_MUXES,
                document);
    }

    private Element getPluginElement(int pluginType, String typeName,
            Document document)
    {
        Element pluginsElement = document.createElement(typeName);
        Vector<String> plugins = registryContents.plugins[pluginType - 1];

        if (plugins != null)
        {
            Iterator<String> pluginIter = plugins.iterator();
            while (pluginIter.hasNext())
            {
                String classname = pluginIter.next();
                Element pluginElement = document.createElement(ELEMENT_CLASS);
                pluginElement.setTextContent(classname);

                pluginsElement.appendChild(pluginElement);
            }
        }

        return pluginsElement;
    }

    private Element getPluginsElement(Document document)
    {
        Element pluginElement = document.createElement(ELEMENT_PLUGINS);

        pluginElement.appendChild(getCodecElement(document));
        pluginElement.appendChild(getDemuxElement(document));
        pluginElement.appendChild(getEffectElement(document));
        pluginElement.appendChild(getMuxElement(document));
        pluginElement.appendChild(getRendererElement(document));

        return pluginElement;
    }

    private Element getProtocolElement(Document document)
    {
        Element protocolElement = document.createElement(ELEMENT_PROTO_PREFIX);
        Iterator<String> prefixIter
            = registryContents.protocolPrefixList.iterator();
        while (prefixIter.hasNext())
        {
            String prefix = prefixIter.next();
            Element prefixElement = document.createElement(ELEMENT_PREFIX);
            prefixElement.setTextContent(prefix);
            protocolElement.appendChild(prefixElement);
        }
        return protocolElement;
    }

    private Element getRendererElement(Document document)
    {
        return getPluginElement(PlugInManager.RENDERER, ELEMENT_RENDERERS,
                document);
    }

    private String getTextTrim(Element element)
    {
        String text = element.getTextContent();

        return (text == null) ? null : text.trim();
    }

    public void load(InputStream is) throws IOException
    {
        Throwable t = null;

        try
        {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(is);

            // read all the data from the document and place into data
            // structures.
            loadDocument(document);
        } catch (ParserConfigurationException pce)
        {
            t = pce;
        } catch (SAXException saxe)
        {
            t = saxe;
        }
        if (t != null)
        {
            IOException ioe = new IOException();

            ioe.initCause(t);
            throw ioe;
        }
    }

    private void loadCaptureDevices(Element element) throws IOException,
            ClassNotFoundException
    {
        registryContents.captureDeviceInfoList.clear();
        List<Element> list = getChildren(element, ELEMENT_DEVICE);
        for (int i = 0; i < list.size(); i++)
        {
            Element deviceElement = list.get(i);
            Element deviceNameElement
                = getChild(deviceElement, ELEMENT_DEVICE_NAME);
            Element deviceLocatorElement
                = getChild(deviceElement, ELEMENT_DEVICE_LOCATOR);
            List<Element> formatElementsList
                = getChildren(deviceElement, ELEMENT_DEVICE_FORMAT);
            javax.media.Format[] formats
                = new javax.media.Format[formatElementsList.size()];
            for (int j = 0; j < formatElementsList.size(); ++j)
            {
                Element formatElement = formatElementsList.get(j);
                Element serializedElement
                    = getChild(formatElement, ELEMENT_DEVICE_FORMAT_SERIALIZED);
                formats[j]
                    = SerializationUtils.deserialize(
                            getTextTrim(serializedElement));
            }

            CaptureDeviceInfo info = new CaptureDeviceInfo(
                    getTextTrim(deviceNameElement), new MediaLocator(
                            getTextTrim(deviceLocatorElement)), formats);
            registryContents.captureDeviceInfoList.add(info);

        }
    }

    /* -------------- MIME methods -------------- */

    private void loadContentPrefixes(Element element)
    {
        registryContents.contentPrefixList.clear();
        List<Element> list = getChildren(element, ELEMENT_PREFIX);
        for (int i = 0; i < list.size(); i++)
        {
            Element prefixElement = list.get(i);
            registryContents.contentPrefixList.add(getTextTrim(prefixElement));
        }
    }

    private void loadDocument(Document document) throws IOException
    {
        Element rootElement = (Element) document.getFirstChild();
        String versionString = rootElement.getAttribute(ATTR_VERSION);

        // TODO use version String
        logger.info("FMJ registry document version " + versionString);

        Element pluginsElement = getChild(rootElement, ELEMENT_PLUGINS);
        loadPlugins(pluginsElement);

        Element contentPrefixesElement = getChild(rootElement,
                ELEMENT_CONTENT_PREFIX);
        loadContentPrefixes(contentPrefixesElement);

        Element protocolPrefixesElement = getChild(rootElement,
                ELEMENT_PROTO_PREFIX);
        loadProtocolPrefixes(protocolPrefixesElement);

        Element mimetypesElement = getChild(rootElement, ELEMENT_MIMETYPES);
        loadMimeTypes(mimetypesElement);

        // load capture devices
        Element captureDevicesElement = getChild(rootElement,
                ELEMENT_CAPTURE_DEVICES);
        try
        {
            loadCaptureDevices(captureDevicesElement);
        } catch (ClassNotFoundException e)
        {
            throw new IOException(e.getMessage());
        }

    }

    /* -------------- Capture Device methods -------------- */

    private void loadMimeTypes(Element element)
    {
        registryContents.mimeTable.clear();

        List<Element> list = getChildren(element, ELEMENT_MIMETYPE);
        for (int i = 0; i < list.size(); i++)
        {
            Element typeElement = list.get(i);
            String type = typeElement.getAttribute("value");
            String defaultExtension = typeElement.getAttribute("default-ext");

            List<Element> list2 = getChildren(typeElement, "ext");
            for (int j = 0; j < list2.size(); j++)
            {
                final Element extElement = list2.get(j);
                String ext = extElement.getTextContent();

                registryContents.mimeTable.addMimeType(ext, type);
            }
            registryContents.mimeTable.addMimeType(defaultExtension, type);
        }
    }

    private void loadPlugins(Element element)
    {
        Element codecsElement = getChild(element, ELEMENT_CODECS);
        loadPlugins(codecsElement, PlugInManager.CODEC);

        Element effectsElement = getChild(element, ELEMENT_EFFECTS);
        loadPlugins(effectsElement, PlugInManager.EFFECT);

        Element renderersElement = getChild(element, ELEMENT_RENDERERS);
        loadPlugins(renderersElement, PlugInManager.RENDERER);

        Element muxesElement = getChild(element, ELEMENT_MUXES);
        loadPlugins(muxesElement, PlugInManager.MULTIPLEXER);

        Element demuxesElement = getChild(element, ELEMENT_DEMUXES);
        loadPlugins(demuxesElement, PlugInManager.DEMULTIPLEXER);
    }

    /**
     *
     * @param type
     */
    private void loadPlugins(Element element, int type)
    {
        if (element == null)
        {
            return;
        }

        List<String> vector = registryContents.plugins[type - 1];
        for (Element pluginElement : getChildren(element, ELEMENT_CLASS))
        {
            String classname = getTextTrim(pluginElement);
            vector.add(classname);
        }
    }

    private void loadProtocolPrefixes(Element element)
    {
        registryContents.protocolPrefixList.clear();
        for (Element prefixElement : getChildren(element, ELEMENT_PREFIX))
        {
            registryContents.protocolPrefixList.add(getTextTrim(prefixElement));
        }
    }

    public void write(OutputStream os) throws IOException
    {
        // build document from registry data structures
        Document document = buildDocument();

        DOMSource domSource = new DOMSource(document);
        TransformerFactory tf = TransformerFactory.newInstance();

        // Does not work on 1.4
        try
        {
            tf.setAttribute("indent-number", 4);
        } catch (Exception e)
        {
        }

        Transformer serializer;

        try
        {
            serializer = tf.newTransformer();
        } catch (TransformerConfigurationException tce)
        {
            IOException ioe = new IOException();

            ioe.initCause(tce);
            throw ioe;
        }

        // Does not work on 1.5
        try
        {
            serializer.setOutputProperty(
                    "{http://xml.apache.org/xalan}indent-amount", "4");
        } catch (Exception e)
        {
        }
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");

        try
        {
            serializer.transform(domSource, new StreamResult(
                    new OutputStreamWriter(os, "UTF-8")));
        } catch (TransformerException te)
        {
            IOException ioe = new IOException();

            ioe.initCause(te);
            throw ioe;
        }
    }
}
