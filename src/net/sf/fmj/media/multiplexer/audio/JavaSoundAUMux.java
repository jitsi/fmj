package net.sf.fmj.media.multiplexer.audio;

import javax.media.*;
import javax.media.format.AudioFormat;
import javax.media.protocol.*;
import javax.sound.sampled.*;

/**
 * AU audio multiplexer.
 *
 * @author Ken Larson
 *
 */
public class JavaSoundAUMux extends JavaSoundMux
{
    public JavaSoundAUMux()
    {
        super(new FileTypeDescriptor(FileTypeDescriptor.BASIC_AUDIO),
                AudioFileFormat.Type.AU);
    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        // TODO: query AudioSystem
        return new Format[] {
                new AudioFormat(AudioFormat.LINEAR, -1, 8, -1, -1,
                        AudioFormat.SIGNED),
                new AudioFormat(AudioFormat.LINEAR, -1, 16, -1,
                        AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED),
                new AudioFormat(AudioFormat.LINEAR, -1, 24, -1,
                        AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED),
                new AudioFormat(AudioFormat.LINEAR, -1, 32, -1,
                        AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED),
                new AudioFormat(AudioFormat.ULAW), // TODO: narrow down
                new AudioFormat(AudioFormat.ALAW) // TODO: narrow down
        };
    }

}
