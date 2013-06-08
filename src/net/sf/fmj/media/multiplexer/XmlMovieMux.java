package net.sf.fmj.media.multiplexer;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.utility.*;

import com.lti.utils.*;

/**
 * Mux for FMJ's XML movie format.
 *
 * @author Ken Larson
 *
 */
public class XmlMovieMux extends AbstractInputStreamMux
{
    private static final Logger logger = LoggerSingleton.logger;

    private boolean headerWritten = false;

    private boolean trailerWritten = false;

    public XmlMovieMux()
    {
        super(new ContentDescriptor("video.xml"));
    }

    @Override
    public void close()
    {
        if (!trailerWritten)
        {
            try
            {
                outputTrailer(getOutputStream());
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                throw new RuntimeException(e);
            }
            trailerWritten = true;
        }

        super.close();
    }

    @Override
    protected void doProcess(Buffer buffer, int trackID, OutputStream os)
            throws IOException
    {
        if (!headerWritten)
        {
            outputHeader(os);
            headerWritten = true;
        }
        if (buffer.isEOM())
        {
            if (!trailerWritten)
            {
                outputTrailer(os);
                trailerWritten = true;
            }
            os.close();
            return; // TODO: what if there is data in buffer?
        }

        if (buffer.isDiscard())
            return;

        final StringBuilder b = new StringBuilder();
        b.append("<Buffer");
        b.append(" track=\"" + trackID + "\"");
        if (buffer.getSequenceNumber() != Buffer.SEQUENCE_UNKNOWN)
            b.append(" sequenceNumber=\"" + buffer.getSequenceNumber() + "\"");
        b.append(" timeStamp=\"" + buffer.getTimeStamp() + "\"");
        if (buffer.getDuration() >= 0) // TODO: is -1 supposed to be used as an
                                       // unspecified duration?
            b.append(" duration=\"" + buffer.getDuration() + "\"");
        if (buffer.getFlags() != 0)
            b.append(" flags=\"" + Integer.toHexString(buffer.getFlags())
                    + "\"");
        if (buffer.getFormat() != null
                && !buffer.getFormat().equals(inputFormats[trackID])) // format
                                                                      // of
                                                                      // buffer
                                                                      // should
                                                                      // always
                                                                      // match
                                                                      // input
                                                                      // format,
                                                                      // but
                                                                      // we'll
                                                                      // output
                                                                      // it if
                                                                      // it
                                                                      // doesn't
                                                                      // match
            b.append(" format=\""
                    + StringUtils.replaceSpecialXMLChars(FormatArgUtils
                            .toString(buffer.getFormat())) + "\"");
        b.append(">");

        b.append("<Data>");
        b.append(StringUtils.byteArrayToHexString((byte[]) buffer.getData(),
                buffer.getLength(), buffer.getOffset()));
        b.append("</Data>");
        b.append("</Buffer>\n");

        os.write(b.toString().getBytes());

    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        // TODO: we accept anything, really, as long as it is in a byte array
        return new Format[] {
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray),
                new VideoFormat(null, null, -1, Format.byteArray, -1.0f) };
    }

    @Override
    public void open() throws ResourceUnavailableException
    {
        super.open();

        if (!headerWritten)
        {
            try
            {
                outputHeader(getOutputStream());
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                throw new ResourceUnavailableException("" + e);
            }
            headerWritten = true;
        }
    }

    private void outputHeader(OutputStream os) throws IOException
    {
        os.write("<?xml version='1.0' encoding='utf-8'?>\n".getBytes());
        os.write("<XmlMovie version=\"1.0\">\n".getBytes());
        // TODO: how do we get duration of movie?
        os.write("<Tracks>\n".getBytes());
        for (int i = 0; i < numTracks; ++i)
        {
            // TODO: use XML for format?
            os.write(("\t<Track index=\""
                    + i
                    + "\" format=\""
                    + StringUtils.replaceSpecialXMLChars(FormatArgUtils
                            .toString(inputFormats[i])) + "\"/>\n").getBytes());

        }
        os.write("</Tracks>\n".getBytes());

    }

    private void outputTrailer(OutputStream os) throws IOException
    {
        os.write("</XmlMovie>\n".getBytes());
    }

    @Override
    public Format setInputFormat(Format format, int trackID)
    {
        logger.finer("setInputFormat " + format + " " + trackID);

        boolean match = false;
        for (Format supported : getSupportedInputFormats())
        {
            if (format.matches(supported))
            {
                match = true;
                break;
            }
        }
        if (!match)
        {
            logger.warning("Input format does not match any supported input format: "
                    + format);
            return null;
        }
        if (inputFormats != null) // TODO: should we save this somewhere and
                                  // apply once inputFormats is not null?
            inputFormats[trackID] = format;

        return format;
    }

}
