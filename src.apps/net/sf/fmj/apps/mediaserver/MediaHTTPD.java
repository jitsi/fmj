package net.sf.fmj.apps.mediaserver;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.Format;
import javax.media.protocol.*;

import net.sf.fmj.media.multiplexer.*;
import net.sf.fmj.utility.*;
import fi.iki.elonen.nanohttpd.*;

/**
 * HTTP daemon which serves up transcoded media.
 *
 * @author Ken Larson
 *
 */
public class MediaHTTPD extends NanoHTTPD
{
    // examples:
    // http://localhost:8090/mediaserver?media=file://samplemedia/gulp2.wav&format=LINEAR:8000:8:1&mime=audio/basic

    // TODO: unspecified formats (see Handler.buildMux):

    // http://localhost:8090/mediaserver?media=file://samplemedia/santa.aiff&mime=audio/basic
    // http://localhost:8090/mediaserver?media=file://samplemedia/issues.au&mime=audio/basic
    // http://localhost:8090/mediaserver?media=file://samplemedia/betterway.wav&mime=audio/basic
    // http://localhost:8090/mediaserver?media=file://samplemedia/gulp.wav&mime=audio/basic

    // http://localhost:8090/mediaserver?media=civil:/0&mime=multipart/x-mixed-replace
    // http://localhost:8090/mediaserver?media=civil:/0&format=PNG&mime=multipart/x-mixed-replace
    // TODO:
    // http://localhost:8090/mediaserver?media=file://samplemedia/Gloria_Patri.ogg&mime=audio/basic

    private static final Logger logger = LoggerSingleton.logger;

    public MediaHTTPD(int port) throws IOException
    {
        super(port);
    }

    private InputStream getInputStream(String urlStr, Format outputFormat,
            ContentDescriptor outputContentDescriptor) throws Exception
    {
        final ProcessorModel processorModel = new ProcessorModel(
                new MediaLocator(urlStr), outputFormat == null ? null
                        : new Format[] { outputFormat },
                outputContentDescriptor);

        final Processor processor = Manager
                .createRealizedProcessor(processorModel);

        final DataSource ds = processor.getDataOutput();

        final DataSink[] streamDataSinkHolder = new DataSink[] { null };
        // connect the data output of the processor to a StreamDataSink, which
        // will make the data available to PipedInputStream, which we return.
        final PipedInputStream in = new PipedInputStream()
        {
            // override close to clean up everything when the media has been
            // served.
            @Override
            public void close() throws IOException
            {
                super.close();
                logger.fine("Closed input stream");
                logger.fine("Stopping processor");
                processor.stop();
                logger.fine("Closing processor");
                processor.close();
                logger.fine("Deallocating processor");
                processor.deallocate();
                if (streamDataSinkHolder[0] != null)
                {
                    logger.fine("Closing StreamDataSink");
                    streamDataSinkHolder[0].close();
                }
            }

        };
        final PipedOutputStream out = new PipedOutputStream(in);
        final DataSink streamDataSink = new StreamDataSink(out);
        streamDataSinkHolder[0] = streamDataSink;

        streamDataSink.setSource(ds);
        streamDataSink.open();
        streamDataSink.start();

        logger.info("Starting processor");
        processor.start();

        // TODO: if there is an error, make sure we clean up.
        // for example, if the client breaks the connection.
        // we need a controller listener to listen for errors.

        return in;

    }

    // private static boolean isCaptureUrl(String urlStr)
    // {
    // return urlStr.startsWith("civil:") || urlStr.startsWith("javasound:");
    // // TODO: this is hard-coded, there must be a better way.
    // }

    @Override
    public Response serve(String uri, String method, Properties header,
            Properties parms)
    {
        if (!uri.equals("/mediaserver"))
        {
            return super.serve(uri, method, header, parms); // this way we can
                                                            // also serve up
                                                            // normal files and
                                                            // content
        }

        logger.fine(method + " '" + uri + "' ");

        Enumeration<?> e = header.propertyNames();
        while (e.hasMoreElements())
        {
            String value = (String) e.nextElement();
            logger.fine("  HDR: '" + value + "' = '"
                    + header.getProperty(value) + "'");
        }
        e = parms.propertyNames();
        while (e.hasMoreElements())
        {
            String value = (String) e.nextElement();
            logger.fine("  PRM: '" + value + "' = '" + parms.getProperty(value)
                    + "'");
        }

        // TODO: check the actual path...

        final String mediaPath = parms.getProperty("media");
        final String outputFormatStr = parms.getProperty("format");
        final String mimeType = parms.getProperty("mime");
        logger.info("requested media: " + mediaPath);
        logger.info("requested mime type: " + mimeType);
        if (mediaPath == null)
            return new Response(HTTP_FORBIDDEN, "text/plain",
                    "mediaPath parameter not specified");
        if (mimeType == null)
            return new Response(HTTP_FORBIDDEN, "text/plain",
                    "mimeType parameter not specified");

        // TODO: if we aren't performing any transcoding, just serve the file up
        // directly.
        // TODO: capture sources need to be treated as singletons, with some
        // kind of broadcasting/cloning to ensure
        // that multiple connections can be made.

        final String serverSideUrlStr = mediaPath; // URLUtils.createUrlStr(new
                                                   // File(mediaPath)); // TODO:
                                                   // enforce that we can't just
                                                   // serve up anything anywhere
        final ContentDescriptor outputContentDescriptor = new FileTypeDescriptor(
                ContentDescriptor.mimeTypeToPackageName(mimeType));

        final Format outputFormat;
        if (outputFormatStr == null)
        {
            outputFormat = null;
        } else
        {
            try
            {
                outputFormat = FormatArgUtils.parse(outputFormatStr);
            } catch (ParseException e1)
            {
                logger.log(Level.WARNING, "" + e1, e1);
                return new Response(HTTP_FORBIDDEN, "text/plain", "" + e1);
            }
        }

        logger.info("serverSideUrlStr: " + serverSideUrlStr);
        logger.info("outputContentDescriptor: " + outputContentDescriptor);
        logger.info("outputFormat: " + outputFormat);

        final InputStream is;
        try
        {
            is = getInputStream(serverSideUrlStr, outputFormat,
                    outputContentDescriptor);
        } catch (Exception e1)
        {
            return new Response(HTTP_FORBIDDEN, "text/plain", "" + e1);
        }

        final String responseMimeType;
        // workaround for the problem that the multipart/x-mixed-replace
        // boundary is not stored anywhere.
        // this assumes that if we are serving multipart/x-mixed-replace data,
        // that MultipartMixedReplaceMux is being used.
        if (mimeType.equals("multipart/x-mixed-replace"))
            responseMimeType = mimeType + ";boundary="
                    + MultipartMixedReplaceMux.BOUNDARY;
        else
            responseMimeType = mimeType;
        logger.info("Response mime type: " + responseMimeType);
        return new Response(HTTP_OK, responseMimeType, is);

    }
}