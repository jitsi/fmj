package net.sf.fmj.media.util;

import java.awt.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.renderer.*;

public class VideoCodecChain extends CodecChain
{
    public VideoCodecChain(VideoFormat vf) throws UnsupportedFormatException
    {
        Dimension size = vf.getSize();

        if (size == null || vf == null)
            throw new UnsupportedFormatException(vf);

        if (!buildChain(vf))
            throw new UnsupportedFormatException(vf);
    }

    @Override
    public Component getControlComponent()
    {
        if (renderer instanceof VideoRenderer)
            return ((VideoRenderer) renderer).getComponent();
        else
            return null;
    }

    /**
     * MPEG video is not raw format. However, MonitorAdapter is only setting the
     * render flag true for I frames on MPEG video so it can be treated as raw.
     * Otherwise all frames must go to the decoder and the current decoder
     * overloads the CPU.
     */
    @Override
    boolean isRawFormat(Format format)
    {
        // If raw format, no need to decode just to keep state
        return ((format instanceof RGBFormat || format instanceof YUVFormat || (format
                .getEncoding() != null && (format.getEncoding()
                .equalsIgnoreCase(VideoFormat.JPEG) || format.getEncoding()
                .equalsIgnoreCase(VideoFormat.MPEG)))));
    }
}
