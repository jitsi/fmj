package net.sf.fmj.media.renderer.audio;

import javax.sound.sampled.AudioFormat.Encoding;

/**
 * Subclass of Encoding for 1.4 which makes constructor public. 1.5 does not
 * have this problem. TODO: now that we only support 1.5, this is no longer
 * needed.
 *
 * @author Ken Larson
 *
 */
class CustomEncoding extends Encoding
{
    public CustomEncoding(String name)
    {
        super(name);
    }

}
