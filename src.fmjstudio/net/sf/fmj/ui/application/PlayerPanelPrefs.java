package net.sf.fmj.ui.application;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.media.protocol.*;

import net.sf.fmj.ui.wizards.*;
import net.sf.fmj.utility.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

/**
 *
 * @author Ken Larson
 *
 */
public class PlayerPanelPrefs
{
    private static final Logger logger = LoggerSingleton.logger;

    /** versio of the xml file format */
    private static final String version = "0.1";

    public static File getFile()
    {
        /** the name of the file used to store the prefs */
        final String filename = ".fmjstudio.prefs.xml";

        String home = System.getProperty("user.home");
        return new File(home + File.separator + filename);
    }

    public List<String> recentUrls = new ArrayList<String>();
    public boolean autoPlay = true;

    public boolean autoLoop = false;
    public RTPTransmitWizardConfig rtpTransmitWizardConfig = new RTPTransmitWizardConfig();

    public TranscodeWizardConfig transcodeWizardConfig = new TranscodeWizardConfig();

    /* ---------------------- XML operations --------------------- */

    private static final String ELEMENT_REGISTRY = "fmj-studio-prefs";
    private static final String ATTR_VERSION = "version";

    private static final String ELEMENT_RECENT_URLS = "recent-urls";
    private static final String ELEMENT_RECENT_URL = "url";

    private static final String ELEMENT_RTP_TRANSMIT_WIZARD_CONFIG = "rtp-transmit-wizard-config";
    private static final String ELEMENT_TRANSCODE_WIZARD_CONFIG = "transcode-wizard-config";

    private static Element buildElement_ProcessorWizardConfig(
            ProcessorWizardConfig processorWizardConfig, String elementStr)
    {
        Element element = new Element(elementStr);

        if (processorWizardConfig.url != null)
        {
            final Element e = new Element("source-url");
            e.setText(processorWizardConfig.url);
            element.addContent(e);
        }

        if (processorWizardConfig.contentDescriptor != null)
        {
            final Element e = new Element("content-descriptor-encoding");
            e.setText(processorWizardConfig.contentDescriptor.getEncoding());
            element.addContent(e);

        }

        if (processorWizardConfig.trackConfigs != null)
        {
            final Element e = new Element("track-configs");
            element.addContent(e);
            for (int i = 0; i < processorWizardConfig.trackConfigs.length; ++i)
            {
                final TrackConfig trackConfig = processorWizardConfig.trackConfigs[i];

                final Element e2 = new Element("track-config");
                e.addContent(e2);

                // enabled:
                {
                    final Element e3 = new Element("enabled");
                    e2.addContent(e3);
                    e3.setText("" + trackConfig.enabled);
                }

                // format:
                if (trackConfig.format != null)
                {
                    final Element e3 = new Element("format-serialized");
                    try
                    {
                        e3.setText(SerializationUtils
                                .serialize(trackConfig.format));
                        e2.addContent(e3);
                    } catch (IOException e1)
                    {
                        logger.warning("Unable to serialize format: "
                                + trackConfig.format + ": " + e);
                    }

                }

            }
        }

        if (processorWizardConfig.destUrl != null)
        {
            final Element e = new Element("dest-url");
            e.setText(processorWizardConfig.destUrl.toString());
            element.addContent(e);
        }

        return element;
    }

    private static void parseElement_ProcessorWizardConfig(Element element,
            ProcessorWizardConfig processorWizardConfig)
    {
        {
            final Element e = element.getChild("source-url");
            if (e != null)
                processorWizardConfig.url = e.getTextTrim();
        }

        {
            final Element e = element.getChild("content-descriptor-encoding");
            if (e != null)
                processorWizardConfig.contentDescriptor = new ContentDescriptor(
                        e.getTextTrim());
        }

        {
            final Element e = element.getChild("track-configs");
            if (e != null)
            {
                final List trackConfigElements = e.getChildren("track-config");
                final TrackConfig[] trackConfigs = new TrackConfig[trackConfigElements
                        .size()];
                for (int j = 0; j < trackConfigElements.size(); ++j)
                {
                    trackConfigs[j] = new TrackConfig();

                    final Element e2 = (Element) trackConfigElements.get(j);

                    // enabled
                    {
                        final Element e3 = e2.getChild("enabled");
                        if (e3 != null)
                        {
                            trackConfigs[j].enabled = e3.getTextTrim().equals(
                                    "" + true);
                        }
                    }

                    // format:
                    {
                        final Element e3 = e2.getChild("format");
                        if (e3 != null)
                        {
                            try
                            {
                                trackConfigs[j].format = SerializationUtils
                                        .deserialize(e3.getTextTrim());
                            } catch (IOException e1)
                            {
                                logger.warning("Unable to deserialize format: "
                                        + e3.getTextTrim() + ": " + e1);
                            } catch (ClassNotFoundException e1)
                            {
                                logger.warning("Unable to deserialize format: "
                                        + e3.getTextTrim() + ": " + e1);
                            }
                        }
                    }

                }
            }
        }

        {
            final Element e = element.getChild("dest-url");
            if (e != null)
            {
                final String destUrlStr = e.getTextTrim();
                processorWizardConfig.destUrl = destUrlStr;
                // try
                // {
                //
                // rtpTransmitWizardConfig.destUrl =
                // RTPUrlParser.parse(destUrlStr);
                // } catch (RTPUrlParserException ex)
                // {
                // logger.warning("Unable to parse RTP URL: " + destUrlStr +
                // ": " + e);
                //
                // }
            }
        }

    }

    /**
     * Builds a Document from the registry data structures.
     *
     * @return the Document.
     * @throws IOException
     */
    private Document buildDocument() throws IOException
    {
        Document document = new Document();

        Element rootElement = new Element(ELEMENT_REGISTRY);
        rootElement.setAttribute(ATTR_VERSION, version);

        document.setRootElement(rootElement);

        rootElement.addContent(buildElement_RecentUrls());

        // auto-start
        {
            Element e = new Element("auto-play");
            e.setText("" + autoPlay);
            rootElement.addContent(e);
        }
        // auto-loop
        {
            Element e = new Element("auto-loop");
            e.setText("" + autoLoop);
            rootElement.addContent(e);
        }

        rootElement.addContent(buildElement_RTPTransmitWizardConfig());
        rootElement.addContent(buildElement_TranscodeWizardConfig());

        return document;
    }

    private Element buildElement_RecentUrls()
    {
        Element element = new Element(ELEMENT_RECENT_URLS);

        Iterator<String> iterator = recentUrls.iterator();
        while (iterator.hasNext())
        {
            String s = iterator.next();
            Element element_RecentUrl = new Element(ELEMENT_RECENT_URL);
            element_RecentUrl.setText(s);
            element.addContent(element_RecentUrl);
        }

        return element;
    }

    private Element buildElement_RTPTransmitWizardConfig()
    {
        return buildElement_ProcessorWizardConfig(rtpTransmitWizardConfig,
                ELEMENT_RTP_TRANSMIT_WIZARD_CONFIG);
    }

    private Element buildElement_TranscodeWizardConfig()
    {
        return buildElement_ProcessorWizardConfig(transcodeWizardConfig,
                ELEMENT_TRANSCODE_WIZARD_CONFIG);
    }

    /**
     * Load the Registry data from a Reader/
     */
    public void load(Reader reader) throws IOException
    {
        // read the registry
        try
        {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(reader);

            // read all the data from the document and place into data
            // structures.
            loadDocument(document);
        } catch (JDOMException e)
        {
            // problem parsing XML.
            throw new IOException(e.getMessage());
        }
    }

    private void loadDocument(Document document) throws IOException
    {
        Element rootElement = document.getRootElement();
        String versionString = rootElement.getAttributeValue(ATTR_VERSION);

        // TODO use version String
        logger.info("FMJStudio preferences document version " + versionString);

        Element element_RecentUrls = rootElement.getChild(ELEMENT_RECENT_URLS);
        parseElement_RecentUrls(element_RecentUrls);

        // auto-start
        {
            Element e = rootElement.getChild("auto-play");
            if (e != null)
                autoPlay = e.getTextTrim().equals("" + true);
        }

        // auto-loop
        {
            Element e = rootElement.getChild("auto-loop");
            if (e != null)
                autoLoop = e.getTextTrim().equals("" + true);
        }

        parseElement_RTPTransmitWizardConfig(rootElement
                .getChild(ELEMENT_RTP_TRANSMIT_WIZARD_CONFIG));
        parseElement_TranscodeWizardConfig(rootElement
                .getChild(ELEMENT_TRANSCODE_WIZARD_CONFIG));
    }

    private void parseElement_RecentUrls(Element element)
    {
        recentUrls.clear();
        List list = element.getChildren(ELEMENT_RECENT_URL);
        for (int i = 0; i < list.size(); i++)
        {
            Element stringElement = (Element) list.get(i);
            recentUrls.add(stringElement.getTextTrim());
        }
    }

    private void parseElement_RTPTransmitWizardConfig(Element element)
    {
        rtpTransmitWizardConfig = new RTPTransmitWizardConfig();
        if (element != null)
            parseElement_ProcessorWizardConfig(element, rtpTransmitWizardConfig);
    }

    private void parseElement_TranscodeWizardConfig(Element element)
    {
        transcodeWizardConfig = new TranscodeWizardConfig();
        if (element != null)
            parseElement_ProcessorWizardConfig(element, transcodeWizardConfig);
    }

    /**
     * Write the registry data to the Writer.
     *
     * @param writer
     *            destination for the registry data.
     * @throws IOException
     */
    public void write(Writer writer) throws IOException
    {
        // build document from registry data structures
        Document document = buildDocument();

        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());

        xmlOutputter.output(document, writer);
    }
}
