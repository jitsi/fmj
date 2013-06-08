package net.sf.fmj.media;

import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.utility.*;

/**
 * Abstract base class to implement Multiplexer.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractMultiplexer extends AbstractPlugIn implements
        Multiplexer
{
    private static final Logger logger = LoggerSingleton.logger;

    protected ContentDescriptor outputContentDescriptor;
    protected Format[] inputFormats;

    protected int numTracks;

    public ContentDescriptor setContentDescriptor(
            ContentDescriptor outputContentDescriptor)
    {
        this.outputContentDescriptor = outputContentDescriptor;
        return outputContentDescriptor;
    }

    public Format setInputFormat(Format format, int trackID)
    {
        if (trackID >= numTracks)
        {
            logger.warning("Rejecting input format for track number out of range: "
                    + trackID + ": " + format);
            return null;
        }
        boolean match = false;
        for (Format supported : getSupportedInputFormats())
        {
            if (supported.matches(format))
                match = true;
        }
        if (!match)
        {
            logger.fine("Rejecting unsupported input format for track "
                    + trackID + ": " + format);
            return null;
        }

        // TODO: we want to take the most specific matching input format, and
        // make the most specific format we can.

        logger.finer("setInputFormat " + format + " " + trackID);
        if (inputFormats != null) // TODO: should we save this somewhere and
                                  // apply once inputFormats is not null?
            inputFormats[trackID] = format;

        return format;
    }

    public int setNumTracks(int numTracks)
    {
        logger.finer("setNumTracks " + numTracks);
        inputFormats = new Format[numTracks];

        this.numTracks = numTracks;
        return numTracks;
    }
}
