package net.sf.fmj.media;

import javax.media.*;
import javax.media.format.*;

/**
 * Creates audio formats which have any unspecified values set to defaults
 *
 * @author Ken Larson
 *
 */
public class AudioFormatCompleter
{
    // TODO: could it be that the codecs that will accept arbitrary endianness
    // for the output should
    // return a specific endian-ness when setOutputFormat is called? What does
    // JMF do?
    // TODO: seems like we need to apply this completeness method to the filter
    // graph building in general.
    /** Fill in any needed, unspecified values, like endianness. */
    public static AudioFormat complete(AudioFormat f)
    {
        // if endianness is not specify, specify it. TODO: this is a general
        // problem with codecs, some things need to be
        // specified even if we don't care what they are.

        if (f.getSampleSizeInBits() > 8
                && f.getEndian() == Format.NOT_SPECIFIED)
        {
            return new AudioFormat(f.getEncoding(), f.getSampleRate(),
                    f.getSampleSizeInBits(), f.getChannels(),
                    AudioFormat.BIG_ENDIAN, f.getSigned(),
                    f.getFrameSizeInBits(), f.getFrameRate(), f.getDataType());
        }

        // TODO: signed, etc.

        return f;
    }
}
