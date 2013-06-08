package net.sf.fmj.media.multiplexer;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.format.*;
import net.sf.fmj.utility.*;

/**
 * Multiplexer for multipart/x-mixed-replace streams, which is a common format
 * used for MJPG IP cameras. Also adds a nonstandard property header to each
 * part, X-FMJ-Timestamp, with the JMF/FMJ timestamp as a long integer string.
 * Always uses the same boundary string, "--ssBoundaryFMJ".
 *
 * @author Ken Larson
 *
 */
public class MultipartMixedReplaceMux extends AbstractInputStreamMux
{
    private static final Logger logger = LoggerSingleton.logger;

    public static final String BOUNDARY = "--ssBoundaryFMJ";
    public static final String TIMESTAMP_KEY = "X-FMJ-Timestamp"; // will be
                                                                  // ignored by
                                                                  // most
                                                                  // recipients,
                                                                  // but with
                                                                  // FMJ we have
                                                                  // the option
                                                                  // of timing
                                                                  // the
                                                                  // playback
                                                                  // based on
                                                                  // this.

    private static final int MAX_TRACKS = 1;

    public MultipartMixedReplaceMux()
    {
        super(new ContentDescriptor("multipart.x_mixed_replace"));
    }

    @Override
    protected void doProcess(Buffer buffer, int trackID, OutputStream os)
            throws IOException
    {
        if (buffer.isEOM())
        {
            os.close();
            return; // TODO: what if there is data in buffer?
        }

        if (buffer.isDiscard())
            return;

        // example:
        // --ssBoundary8345
        // Content-Type: image/jpeg
        // Content-Length: 114587

        os.write((BOUNDARY + "\n").getBytes());
        os.write(("Content-Type: image/" + buffer.getFormat().getEncoding() + "\n")
                .getBytes());
        os.write(("Content-Length: " + buffer.getLength() + "\n").getBytes());
        os.write((TIMESTAMP_KEY + ": " + buffer.getTimeStamp() + "\n")
                .getBytes());
        os.write("\n".getBytes());

        // logger.fine("MultipartMixedReplaceMux: writing buffer length: " +
        // buffer.getLength());
        // TODO: with the piped input streams, if we write too much, we will
        // block.
        os.write((byte[]) buffer.getData(), buffer.getOffset(),
                buffer.getLength());
        // logger.fine("MultipartMixedReplaceMux: wrote buffer length: " +
        // buffer.getLength());

        os.write("\n\n".getBytes());
    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        return new Format[] { new JPEGFormat(), new GIFFormat(),
                new PNGFormat() };
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

    @Override
    public int setNumTracks(int numTracks)
    {
        return super.setNumTracks(numTracks > MAX_TRACKS ? MAX_TRACKS
                : numTracks);
    }
}
