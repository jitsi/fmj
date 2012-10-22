package com.sun.media.renderer.audio;

import javax.media.*;
import javax.media.format.*;

/**
 * 
 * Use FMJ's sound renderer instead of Sun's.
 * 
 * @author Warren Bloomer
 * @author Ken Larson
 * 
 */
public class JavaSoundRenderer extends
        net.sf.fmj.media.renderer.audio.JavaSoundRenderer
{
    // override to return exactly the formats that JMF's does:

    private Format[] supportedInputFormats = new Format[] {
            new AudioFormat(AudioFormat.LINEAR, -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat(AudioFormat.ULAW, -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray), };

    @Override
    public Format[] getSupportedInputFormats()
    {
        return supportedInputFormats; // JMF doesn't return all the details.
    }
}
