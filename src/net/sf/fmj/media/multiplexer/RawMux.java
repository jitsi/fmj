package net.sf.fmj.media.multiplexer;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

/**
 *
 * A raw multiplexer which provides the input to the output, unmodified.
 * Resulting steams are PushSourceStream. Not tested yet. Not sure if useful for
 * anything.
 *
 * @author Ken Larson
 *
 */
public class RawMux extends AbstractStreamCopyMux
{
    public RawMux()
    {
        super(new ContentDescriptor(ContentDescriptor.RAW));
    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        // we'll take anything in a byte array.
        return new Format[] {
                new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray),
                new VideoFormat(null, null, -1, Format.byteArray, -1.0f) };
    }
}
