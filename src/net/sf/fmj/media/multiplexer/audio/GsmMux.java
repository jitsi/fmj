package net.sf.fmj.media.multiplexer.audio;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.multiplexer.*;

/**
 * Gsm Multiplexer class, actually just provides unmodified input to the output.
 *
 * @author Martin Harvan
 */
public class GsmMux extends AbstractStreamCopyMux
{
    public GsmMux()
    {
        super(new ContentDescriptor(FileTypeDescriptor.GSM));
    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        return new Format[] { new AudioFormat(AudioFormat.GSM, 8000, 8, 1, -1,
                -1) };
    }
}
