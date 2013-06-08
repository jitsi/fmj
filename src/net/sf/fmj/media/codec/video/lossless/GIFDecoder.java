package net.sf.fmj.media.codec.video.lossless;

import javax.media.*;

import net.sf.fmj.media.codec.video.*;
import net.sf.fmj.media.format.*;

/**
 * GIF decoder Codec.
 *
 * @author Ken Larson
 *
 */
public class GIFDecoder extends ImageIODecoder
{
    private final Format[] supportedInputFormats = new Format[] { new GIFFormat(), };

    public GIFDecoder()
    {
        super("GIF");
    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        return supportedInputFormats;
    }
}
