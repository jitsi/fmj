package net.sf.fmj.media.multiplexer.audio;

import javax.media.protocol.*;
import javax.sound.sampled.*;

/**
 * AIFF audio multiplexer. TODO: doesn't work?
 *
 * @author Ken Larson
 *
 */
public class AIFFMux extends JavaSoundMux
{
    public AIFFMux()
    {
        super(new FileTypeDescriptor(FileTypeDescriptor.AIFF),
                AudioFileFormat.Type.AIFF);
    }

}
