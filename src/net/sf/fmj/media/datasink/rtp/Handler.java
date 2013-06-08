package net.sf.fmj.media.datasink.rtp;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * RTP DataSink.
 *
 * @author Ken Larson
 *
 */
public class Handler extends AbstractDataSink
{
    private static final Logger logger = LoggerSingleton.logger;

    private PushBufferDataSource source;

    // TODO: listener notifications?

    private RTPManager rtpManager;

    private SendStream[] streams; // array will be same size as number of source
                                  // streams, but entries will be null if not
                                  // being transmitted.

    private ParsedRTPUrl parsedRTPUrl;

    public void close()
    {
        // TODO: disconnect source?
        if (rtpManager != null)
            rtpManager.dispose();

        try
        {
            stop();
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }
    }

    public String getContentType()
    {
        // TODO: do we get this from the source, or the outputLocator?
        if (source != null)
            return source.getContentType();
        else
            return null;
    }

    public Object getControl(String controlType)
    {
        logger.warning("TODO: getControl " + controlType);
        return null;
    }

    public Object[] getControls()
    {
        logger.warning("TODO: getControls");
        return new Object[0];
    }

    public void open() throws IOException, SecurityException
    {
        if (getOutputLocator() == null)
            throw new IOException("Output locator not set");

        // parse the ouptut locator (URL)
        try
        {
            parsedRTPUrl = RTPUrlParser.parse(getOutputLocator()
                    .toExternalForm());
        } catch (RTPUrlParserException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new IOException("" + e);
        }

        try
        {
            rtpManager = RTPManager.newInstance();
            rtpManager.initialize(new SessionAddress());
            RTPBonusFormatsMgr.addBonusFormats(rtpManager);

            int numStreams = source.getStreams().length;
            streams = new SendStream[numStreams];
            int numStreamsInUse = 0;
            for (int streamIndex = 0; streamIndex < numStreams; ++streamIndex)
            {
                // find the type:
                final Format format = source.getStreams()[streamIndex]
                        .getFormat();
                final String elementType;
                if (format instanceof AudioFormat)
                    elementType = ParsedRTPUrlElement.AUDIO;
                else if (format instanceof VideoFormat)
                    elementType = ParsedRTPUrlElement.VIDEO;
                else
                {
                    logger.warning("Skipping unknown source stream format: "
                            + format);
                    continue;
                }
                final ParsedRTPUrlElement element = parsedRTPUrl
                        .find(elementType);

                if (element == null)
                {
                    logger.fine("Skipping source stream format not specified in URL: "
                            + format);
                    continue;
                }

                // example URL: "rtp://192.168.1.4:8000/audio/16";
                String host = element.host; // e.g. "192.168.1.4";
                final InetAddress dataAddress = InetAddress.getByName(host);
                final int dataPort = element.port; // e.g. 8000;
                final int ttl = element.ttl; // e.g. 16;

                SessionAddress remoteAddress = new SessionAddress(dataAddress,
                        dataPort, ttl);
                rtpManager.addTarget(remoteAddress);

                streams[streamIndex] = rtpManager.createSendStream(source,
                        streamIndex);
                ++numStreamsInUse;
            }

            if (numStreamsInUse <= 0)
                throw new IOException("No streams selected to be used");
            source.connect();
        } catch (InvalidSessionAddressException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new IOException("" + e);
        } catch (UnsupportedFormatException e)
        {
            logger.log(Level.WARNING, "" + e, e); // TODO: should this be in
                                                  // setDataSource()? should we
                                                  // really rethrow this as
                                                  // IOException?
            throw new IOException("" + e);
        }

    }

    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        logger.finer("setSource: " + source);
        if (!(source instanceof PushBufferDataSource))
            throw new IncompatibleSourceException();
        this.source = (PushBufferDataSource) source;
    }

    public void start() throws IOException
    {
        source.start(); // TODO: start this before or after streams?

        for (int streamIndex = 0; streamIndex < streams.length; ++streamIndex)
        {
            if (streams[streamIndex] == null)
                continue;
            streams[streamIndex].start();
        }

    }

    public void stop() throws IOException
    {
        if (source != null)
            source.stop();

        if (streams != null)
        {
            for (int streamIndex = 0; streamIndex < streams.length; ++streamIndex)
            {
                if (streams[streamIndex] == null)
                    continue;
                streams[streamIndex].stop();
            }
        }
    }

}
