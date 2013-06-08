package net.sf.fmj.media.codec.audio.ulaw;

import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * ULAW/RTP packetizer codec.
 *
 * @author Ken Larson
 *
 */
public class Packetizer extends AbstractPacketizer
{
    private static final Logger logger = LoggerSingleton.logger;

    // TODO: move to base class?
    protected Format[] outputFormats = new Format[] { new AudioFormat(
            AudioFormat.ULAW_RTP, -1.0, 8, 1, -1, -1, 8, -1.0, Format.byteArray) };

    public Packetizer()
    {
        super();
        this.inputFormats = new Format[] { new AudioFormat(AudioFormat.ULAW,
                -1.0, 8, 1, -1, -1, 8, -1.0, Format.byteArray) };

    }

    @Override
    public void close()
    {
    }

    @Override
    public String getName()
    {
        return "ULAW Packetizer";
    }

    @Override
    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
            return outputFormats;
        else
        {
            if (!(input instanceof AudioFormat))
            {
                logger.warning(this.getClass().getSimpleName()
                        + ".getSupportedOutputFormats: input format does not match, returning format array of {null} for "
                        + input); // this can cause an NPE in JMF if it ever
                                  // happens.
                return new Format[] { null };
            }
            final AudioFormat inputCast = (AudioFormat) input;
            if (!inputCast.getEncoding().equals(AudioFormat.ULAW)
                    || (inputCast.getSampleSizeInBits() != 8 && inputCast
                            .getSampleSizeInBits() != Format.NOT_SPECIFIED)
                    || (inputCast.getChannels() != 1 && inputCast.getChannels() != Format.NOT_SPECIFIED)
                    || (inputCast.getFrameSizeInBits() != 8 && inputCast
                            .getFrameSizeInBits() != Format.NOT_SPECIFIED))
            {
                logger.warning(this.getClass().getSimpleName()
                        + ".getSupportedOutputFormats: input format does not match, returning format array of {null} for "
                        + input); // this can cause an NPE in JMF if it ever
                                  // happens.
                return new Format[] { null };
            }
            final AudioFormat result = new AudioFormat(AudioFormat.ULAW_RTP,
                    inputCast.getSampleRate(), 8, 1, inputCast.getEndian(),
                    inputCast.getSigned(), 8, inputCast.getFrameRate(),
                    inputCast.getDataType());

            return new Format[] { result };
        }
    }

    @Override
    public void open()
    {
        // RFC 3551 4.5 Audio Encodings default ms/packet is 20
        // TODO: add some sanity checks
        int sampleRate = (int) ((AudioFormat) getInputFormat()).getSampleRate();
        setPacketSize(sampleRate / 50);
    }

    @Override
    public Format setInputFormat(Format arg0)
    {
        return super.setInputFormat(arg0);
    }

    @Override
    public Format setOutputFormat(Format arg0)
    {
        return super.setOutputFormat(arg0);
    }

}
