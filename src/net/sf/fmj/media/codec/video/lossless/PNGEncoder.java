package net.sf.fmj.media.codec.video.lossless;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.codec.video.*;
import net.sf.fmj.media.format.*;

/**
 * PNG encoder Codec.
 *
 * @author Ken Larson
 *
 */
public class PNGEncoder extends ImageIOEncoder
{
    private final Format[] supportedOutputFormats = new Format[] { new PNGFormat(), };

    public PNGEncoder()
    {
        super("PNG");
    }

    @Override
    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
            return supportedOutputFormats;
        final VideoFormat inputCast = (VideoFormat) input;
        final Format[] result = new Format[] { new PNGFormat(
                inputCast.getSize(), -1, Format.byteArray,
                inputCast.getFrameRate()) };

        return result;
    }
}
