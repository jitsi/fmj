package net.sf.fmj.media.codec.video.lossless;

import javax.media.*;

import net.sf.fmj.media.codec.video.*;
import net.sf.fmj.media.format.*;

/**
 * PNG decoder Codec.
 *
 * @author Ken Larson
 *
 */
public class PNGDecoder extends ImageIODecoder
{
    private final Format[] supportedInputFormats = new Format[] { new PNGFormat(), };

    public PNGDecoder()
    {
        super("PNG");
    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        return supportedInputFormats;
    }
}
