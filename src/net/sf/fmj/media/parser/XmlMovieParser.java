package net.sf.fmj.media.parser;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.lti.utils.*;
import com.lti.utils.synchronization.*;

/**
 * Parser for FMJ's XML movie format.
 *
 * @author Ken Larson
 *
 */
public class XmlMovieParser extends AbstractDemultiplexer
{
    private abstract class PullSourceStreamTrack extends AbstractTrack
    {
        public abstract void deallocate();

    }

    private class VideoTrack extends PullSourceStreamTrack
    {
        // TODO: track listener

        private final int track;
        private final Format format;

        public VideoTrack(int track, Format format)
                throws ResourceUnavailableException
        {
            super();

            this.track = track;
            this.format = format;
        }

        @Override
        public void deallocate()
        {
        }

        @Override
        public Time getDuration()
        {
            return Duration.DURATION_UNKNOWN; // TODO
        }

        @Override
        public Format getFormat()
        {
            return format;
        }

        @Override
        public Time mapFrameToTime(int frameNumber)
        {
            return TIME_UNKNOWN;
        }

        @Override
        public int mapTimeToFrame(Time t)
        {
            return FRAME_UNKNOWN;
        }

        @Override
        public void readFrame(Buffer buffer)
        {
            Buffer b;
            try
            {
                b = xmlMovieSAXHandler.readBuffer(track);
            } catch (SAXException e)
            {
                throw new RuntimeException(e);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            buffer.copy(b);

        }
    }

    private static final Logger logger = LoggerSingleton.logger;

    private ContentDescriptor[] supportedInputContentDescriptors = new ContentDescriptor[] { new ContentDescriptor(
            "video.xml") };

    private PullDataSource source;

    private PullSourceStreamTrack[] tracks;

    private XmlMovieSAXHandler xmlMovieSAXHandler;

    private XmlMovieSAXParserThread xmlMovieSAXParserThread;

    public XmlMovieParser()
    {
        super();
    }

    @Override
    public void close()
    {
        if (tracks != null)
        {
            for (int i = 0; i < tracks.length; ++i)
            {
                if (tracks[i] != null)
                {
                    tracks[i].deallocate();
                    tracks[i] = null;
                }
            }
            tracks = null;
        }

        super.close();
    }

    @Override
    public ContentDescriptor[] getSupportedInputContentDescriptors()
    {
        return supportedInputContentDescriptors;
    }

    @Override
    public Track[] getTracks() throws IOException, BadHeaderException
    {
        return tracks;
    }

    @Override
    public boolean isPositionable()
    {
        return false; // TODO
    }

    // TODO: should we stop data source in stop?
    // // @Override
    // public void stop()
    // {
    // try
    // {
    // source.stop();
    // } catch (IOException e)
    // {
    // logger.log(Level.WARNING, "" + e, e);
    // }
    // }

    @Override
    public boolean isRandomAccess()
    {
        return super.isRandomAccess(); // TODO: can we determine this from the
                                       // data source?
    }

    // @Override
    // public Time setPosition(Time where, int rounding)
    // {
    // }

    @Override
    public void open() throws ResourceUnavailableException
    {
        try
        {
            // source.connect(); // TODO: assume source is already connected
            source.start(); // TODO: stop/disconnect on stop/close.

            final PullSourceStream[] streams = source.getStreams();

            // only first stream supported.
            if (streams.length > 1)
                logger.warning("only 1 stream supported, " + streams.length
                        + " found");

            final InputStream is = new PullSourceStreamInputStream(streams[0]);
            xmlMovieSAXHandler = new XmlMovieSAXHandler();
            xmlMovieSAXParserThread = new XmlMovieSAXParserThread(
                    xmlMovieSAXHandler, is);
            xmlMovieSAXParserThread.start(); // TODO: stop when done

            Format[] formats = xmlMovieSAXHandler.readTracksInfo();

            tracks = new PullSourceStreamTrack[formats.length];

            for (int i = 0; i < formats.length; ++i)
            {
                tracks[i] = new VideoTrack(i, formats[i]);

            }

        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new ResourceUnavailableException("" + e);
        } catch (SAXException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new ResourceUnavailableException("" + e);
        } catch (InterruptedException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new ResourceUnavailableException("" + e);
        }

        super.open();

    }

    @Override
    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        source.getLocator().getProtocol();

        if (!(source instanceof PullDataSource))
            throw new IncompatibleSourceException();

        this.source = (PullDataSource) source;

    }

    // @Override
    @Override
    public void start() throws IOException
    {
    }

}

/**
 * SAX callback for FMJ's XML movie format.
 *
 * @author Ken Larson
 *
 */
class XmlMovieSAXHandler extends DefaultHandler
{
    private static int getIntAttr(Attributes atts, String qName)
            throws SAXException
    {
        final int index = atts.getIndex(qName);
        if (index < 0)
            throw new SAXException("Missing attribute: " + qName);
        return getIntAttr(atts, qName, 0);
    }

    private static int getIntAttr(Attributes atts, String qName,
            int defaultResult) throws SAXException
    {
        final int index = atts.getIndex(qName);
        if (index < 0)
            return defaultResult;
        final String s = atts.getValue(index);
        try
        {
            return Integer.parseInt(s);
        } catch (NumberFormatException e)
        {
            throw new SAXException("Expected integer: " + s, e);
        }
    }

    private static long getLongAttr(Attributes atts, String qName)
            throws SAXException
    {
        final int index = atts.getIndex(qName);
        if (index < 0)
            throw new SAXException("Missing attribute: " + qName);
        return getLongAttr(atts, qName, 0);
    }

    private static long getLongAttr(Attributes atts, String qName,
            long defaultResult) throws SAXException
    {
        final int index = atts.getIndex(qName);
        if (index < 0)
            return defaultResult;
        final String s = atts.getValue(index);
        try
        {
            return Long.parseLong(s);
        } catch (NumberFormatException e)
        {
            throw new SAXException("Expected long: " + s, e);
        }
    }

    private static String getStringAttr(Attributes atts, String qName)
            throws SAXException
    {
        final int index = atts.getIndex(qName);
        if (index < 0)
            throw new SAXException("Missing attribute: " + qName);
        return getStringAttr(atts, qName, null);
    }

    private static String getStringAttr(Attributes atts, String qName,
            String defaultResult) throws SAXException
    {
        final int index = atts.getIndex(qName);
        if (index < 0)
            return defaultResult;
        final String s = atts.getValue(index);
        return s;
    }

    private final ProducerConsumerQueue qMeta = new ProducerConsumerQueue();

    private final Map<Integer, ProducerConsumerQueue> qBuffers = new HashMap<Integer, ProducerConsumerQueue>();
    private final Map<Integer, Format> formatsMap = new HashMap<Integer, Format>();

    private int currentTrack = -1;
    private Buffer currentBuffer;
    private StringBuilder currentDataChars;

    private int state = INIT;

    private static final int INIT = 0;
    private static final int AWAIT_BUFFER = 10;
    private static final int AWAIT_DATA = 11;
    private static final int READ_DATA = 12;

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        if (state == READ_DATA)
        {
            String s = new String(ch, start, length);
            currentDataChars.append(s);
        } else
        {
            String s = new String(ch, start, length);
            s = s.trim();
            if (s.length() > 0)
                throw new SAXException("characters unexpected, state=" + state
                        + " chars=" + s);
        }
    }

    @Override
    public void endDocument() throws SAXException
    {
        if (qBuffers != null)
        {
            for (ProducerConsumerQueue q : qBuffers.values())
            {
                if (q != null)
                {
                    final Buffer eomBuffer = new Buffer();
                    eomBuffer.setEOM(true);
                    // TODO: set format/
                    try
                    {
                        q.put(eomBuffer);
                    } catch (InterruptedException e)
                    {
                        throw new SAXException(e);
                    }
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException
    {
        if (localName.equals("Tracks"))
        { // done with track info
            if (formatsMap.size() == 0)
                throw new SAXException("No tracks");
            try
            {
                final Format[] formatsArray = new Format[formatsMap.size()];
                for (int i = 0; i < formatsArray.length; ++i)
                {
                    final Format format = formatsMap.get(i);
                    if (format == null)
                        throw new SAXException("Expected format for track " + i);
                    formatsArray[i] = format;

                }
                qMeta.put(formatsArray);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        } else if (localName.equals("Data"))
        {
            byte[] data = StringUtils.hexStringToByteArray(currentDataChars
                    .toString());
            currentBuffer.setData(data);
            currentBuffer.setOffset(0);
            currentBuffer.setLength(data.length);

            try
            {
                qBuffers.get(currentTrack).put(currentBuffer);
            } catch (InterruptedException e)
            {
                throw new SAXException(e); // TODO: there should be an
                                           // InterruptedSAXException...
            }
            currentBuffer = null;
            currentTrack = -1;
            currentDataChars = null;

            state = AWAIT_BUFFER;
        }
    }

    public void postError(Exception e) throws InterruptedException
    {
        if (qMeta != null)
            qMeta.put(e);
        if (qBuffers != null)
        {
            for (ProducerConsumerQueue q : qBuffers.values())
            {
                if (q != null)
                    q.put(e);
            }
        }

    }

    public Buffer readBuffer(int track) throws SAXException, IOException,
            InterruptedException
    {
        final Object o = qBuffers.get(track).get();
        if (o instanceof Buffer)
            return (Buffer) o;
        else if (o instanceof SAXException)
            throw (SAXException) o;
        else if (o instanceof IOException)
            throw (IOException) o;
        else
            throw new RuntimeException("Unknown object in queue: " + o);
    }

    public Format[] readTracksInfo() throws SAXException, IOException,
            InterruptedException
    {
        final Object o = qMeta.get();
        if (o instanceof Format[])
            return (Format[]) o;
        else if (o instanceof SAXException)
            throw (SAXException) o;
        else if (o instanceof IOException)
            throw (IOException) o;
        else
            throw new RuntimeException("Unknown object in queue: " + o);

    }

    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException
    {
        // TODO: verify element nesting
        try
        {
            if (localName.equals("XmlMovie"))
            {
                final String version = atts.getValue(atts.getIndex("version"));
                if (!version.equals("1.0"))
                    throw new SAXException("Expection XmlMovie version 1.0");
            } else if (localName.equals("Track"))
            {
                // TODO: catch exceptions
                final int index = getIntAttr(atts, "index");
                final String formatStr = getStringAttr(atts, "format");
                Format format = FormatArgUtils.parse(formatStr);
                formatsMap.put(index, format);
                qBuffers.put(index, new ProducerConsumerQueue());
            }

            else if (localName.equals("Buffer"))
            {
                currentTrack = getIntAttr(atts, "track");
                final long sequenceNumber = getLongAttr(atts, "sequenceNumber",
                        Buffer.SEQUENCE_UNKNOWN);
                final long timeStamp = getLongAttr(atts, "timeStamp");
                final long duration = getLongAttr(atts, "duration", -1L);
                final int flags = getIntAttr(atts, "flags", 0);

                final String formatStr = getStringAttr(atts, "format", null);

                final Format format = formatStr == null ? formatsMap
                        .get(currentTrack) : FormatArgUtils.parse(formatStr);

                Buffer buffer = new Buffer();
                buffer.setSequenceNumber(sequenceNumber);
                buffer.setTimeStamp(timeStamp);
                buffer.setDuration(duration);
                buffer.setFlags(flags);
                buffer.setFormat(format);

                currentBuffer = buffer; // data will be set when we get the data
                                        // element
                currentDataChars = new StringBuilder();
                state = AWAIT_DATA;
                //
                // b.append("<Data>");
                // b.append(StringUtils.byteArrayToHexString((byte[])
                // buffer.getData(), buffer.getLength(), buffer.getOffset()));
                // b.append("</Data>");
                // b.append("</Buffer>\n");
            } else if (localName.equals("Data"))
            {
                if (state != AWAIT_DATA)
                    throw new SAXException("Not expecting Data element");
                state = READ_DATA;
            }
            // if (namespaceURI.equals("http://recipes.org") &&
            // localName.equals("ingredient")) {
            // String n = atts.getValue("","name");
            // if (n.equals("flour")) {
            // String a = atts.getValue("","amount"); // assume 'amount'
            // exists
            // amount = amount + Float.valueOf(a).floatValue();
            // }
            // }
        } catch (SAXException e)
        {
            throw e;
        } catch (Exception e)
        {
            throw new SAXException(e);
        }
    }

}

/**
 * Thread to parse FMJ's XML movie format, results handled by
 * XmlMovieSAXHandler. XmlMovieSAXHandler can be used to read parsed results.
 *
 * @author Ken Larson
 *
 */
class XmlMovieSAXParserThread extends CloseableThread
{
    private final XmlMovieSAXHandler handler;
    private final InputStream is;

    public XmlMovieSAXParserThread(XmlMovieSAXHandler handler, InputStream is)
    {
        super();
        this.handler = handler;
        this.is = is;
    }

    @Override
    public void run()
    {
        try
        {
            try
            {
                final XMLReader parser = XMLReaderFactory.createXMLReader();
                parser.setContentHandler(handler);

                parser.parse(new InputSource(is));
            } catch (SAXException e)
            {
                handler.postError(e);
            } catch (IOException e)
            {
                handler.postError(e);
            }

        } catch (InterruptedException e)
        { // exit thread
        } finally
        {
            setClosed();
        }

    }
}
