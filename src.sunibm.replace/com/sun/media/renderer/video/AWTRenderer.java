package com.sun.media.renderer.video;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.renderer.video.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class AWTRenderer extends SimpleAWTRenderer
{
    // override and only include formats supported by Sun' AWTRenderer:

    private final Format[] supportedInputFormats = new Format[] {
            // RGB, 32-bit, Masks=16711680:65280:255, LineStride=-1, class [I
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),

            // RGB, 32-bit, Masks=255:65280:16711680, LineStride=-1, class [I
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1)

    };

    // @Override
    @Override
    public String getName()
    {
        return "AWT Renderer";
    }

    // @Override
    @Override
    public Format[] getSupportedInputFormats()
    {
        return supportedInputFormats;
    }
}
