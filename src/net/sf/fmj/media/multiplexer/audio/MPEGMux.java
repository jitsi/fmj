package net.sf.fmj.media.multiplexer.audio;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.multiplexer.*;

public class MPEGMux extends BasicMux
{
    public MPEGMux()
    {
        supportedInputs = new Format[2];
        supportedInputs[0] = new AudioFormat(AudioFormat.MPEGLAYER3);
        supportedInputs[1] = new AudioFormat(AudioFormat.MPEG);
        supportedOutputs = new ContentDescriptor[1];
        supportedOutputs[0] = new FileTypeDescriptor(
                FileTypeDescriptor.MPEG_AUDIO);
    }

    public String getName()
    {
        return "MPEG Audio Multiplexer";
    }

    @Override
    public Format setInputFormat(Format input, int trackID)
    {
        if (!(input instanceof AudioFormat))
            return null;
        AudioFormat format = (AudioFormat) input;
        double sampleRate = format.getSampleRate();

        String reason = null;
        double epsilon = 0.25;

        // Check to see if some of these restrictions can be removed
        if (!format.getEncoding().equalsIgnoreCase(AudioFormat.MPEGLAYER3)
                && !format.getEncoding().equalsIgnoreCase(AudioFormat.MPEG))
            reason = "Encoding has to be MPEG audio";
        /*
         * else if ( Math.abs(sampleRate - 8000.0) > epsilon ) reason =
         * "Sample rate should be 8000. Cannot handle sample rate " +
         * sampleRate; else if (format.getFrameSizeInBits() != (33*8)) reason =
         * "framesize should be 33 bytes"; else if (format.getChannels() != 1)
         * reason = "Number of channels should be 1";
         */
        if (reason != null)
        {
            return null;
        } else
        {
            inputs[0] = format;
            return format;
        }
    }
}
