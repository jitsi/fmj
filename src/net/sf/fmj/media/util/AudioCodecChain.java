package net.sf.fmj.media.util;

import java.awt.*;

import javax.media.format.*;

public class AudioCodecChain extends CodecChain
{
    Component gainComp = null;

    public AudioCodecChain(AudioFormat input) throws UnsupportedFormatException
    {
        AudioFormat af = input;

        if (!buildChain(input))
            throw new UnsupportedFormatException(input);

        // Do not open the renderer as yet.
        // We'll only do it when the data is being prefetched.
        renderer.close();

        firstBuffer = false;
    }

    @Override
    public Component getControlComponent()
    {
        if (gainComp != null)
            return gainComp;

        // Control c = (Control)renderer.getControl("javax.media.GainControl");
        // if (c != null)
        // gainComp = new GainControlComponent((GainControl)c);
        return gainComp;
    }

    @Override
    public void reset()
    {
    }
}
