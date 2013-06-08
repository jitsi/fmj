package net.sf.fmj.media.codec.audio.alaw;

import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * ALAW decoder Codec.
 *
 * @author Ken Larson
 *
 */
public class Decoder extends AbstractCodec
{
    private static final Logger logger = LoggerSingleton.logger;

    // TODO: move to base class?
    protected Format[] outputFormats = new Format[] { new AudioFormat(
            AudioFormat.LINEAR, -1.0, 16, 1, -1, AudioFormat.SIGNED, 16, -1.0,
            Format.byteArray) };

    private static final boolean TRACE = false;

    public Decoder()
    {
        super();
        this.inputFormats = new Format[] { new AudioFormat(AudioFormat.ALAW,
                -1.0, 8, 1, -1, AudioFormat.SIGNED, 8, -1.0, Format.byteArray) };

    }

    @Override
    public void close()
    {
    }

    @Override
    public String getName()
    {
        return "ALAW Decoder";
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
            if (!inputCast.getEncoding().equals(AudioFormat.ALAW)
                    || (inputCast.getSampleSizeInBits() != 8 && inputCast
                            .getSampleSizeInBits() != Format.NOT_SPECIFIED)
                    || (inputCast.getChannels() != 1 && inputCast.getChannels() != Format.NOT_SPECIFIED)
                    || (inputCast.getSigned() != AudioFormat.SIGNED && inputCast
                            .getSigned() != Format.NOT_SPECIFIED)
                    || (inputCast.getFrameSizeInBits() != 8 && inputCast
                            .getFrameSizeInBits() != Format.NOT_SPECIFIED)
                    || (inputCast.getDataType() != null && inputCast
                            .getDataType() != Format.byteArray))
            {
                logger.warning(this.getClass().getSimpleName()
                        + ".getSupportedOutputFormats: input format does not match, returning format array of {null} for "
                        + input); // this can cause an NPE in JMF if it ever
                                  // happens.
                return new Format[] { null };
            }
            final AudioFormat result = new AudioFormat(AudioFormat.LINEAR,
                    inputCast.getSampleRate(), 16, 1, inputCast.getEndian(),
                    AudioFormat.SIGNED, 16, inputCast.getFrameRate(),
                    Format.byteArray);

            return new Format[] { result };
        }
    }

    @Override
    public void open()
    {
    }

    @Override
    public int process(Buffer inputBuffer, Buffer outputBuffer)
    {
        if (TRACE)
            dump("input ", inputBuffer);

        if (!checkInputBuffer(inputBuffer))
        {
            return BUFFER_PROCESSED_FAILED;
        }

        if (isEOM(inputBuffer))
        {
            propagateEOM(outputBuffer); // TODO: what about data? can there be
                                        // any?
            return BUFFER_PROCESSED_OK;
        }

        byte[] outputBufferData = (byte[]) outputBuffer.getData();
        if (outputBufferData == null
                || outputBufferData.length < inputBuffer.getLength() * 2)
        {
            outputBufferData = new byte[inputBuffer.getLength() * 2];
            outputBuffer.setData(outputBufferData);
        }

        final boolean bigEndian = ((AudioFormat) outputFormat).getEndian() == AudioFormat.BIG_ENDIAN; // TODO:
                                                                                                      // check
                                                                                                      // for
                                                                                                      // undefined
        ALawDecoderUtil.aLawDecode(bigEndian, (byte[]) inputBuffer.getData(),
                inputBuffer.getOffset(), inputBuffer.getLength(),
                outputBufferData);
        outputBuffer.setLength(inputBuffer.getLength() * 2);
        outputBuffer.setOffset(0);
        outputBuffer.setFormat(outputFormat);
        final int result = BUFFER_PROCESSED_OK;

        if (TRACE)
        {
            dump("input ", inputBuffer);
            dump("output", outputBuffer);

            System.out.println("Result="
                    + LoggingStringUtils.plugInResultToStr(result));
        }
        return result;
    }

    @Override
    public Format setInputFormat(Format arg0)
    {
        // TODO: force sample size, etc
        return super.setInputFormat(arg0);
    }

    @Override
    public Format setOutputFormat(Format format)
    {
        if (!(format instanceof AudioFormat))
            return null;
        final AudioFormat audioFormat = (AudioFormat) format;
        return super
                .setOutputFormat(AudioFormatCompleter.complete(audioFormat));
    }

}
