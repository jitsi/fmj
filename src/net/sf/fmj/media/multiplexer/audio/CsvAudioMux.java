package net.sf.fmj.media.multiplexer.audio;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.multiplexer.*;
import net.sf.fmj.utility.*;

import com.lti.utils.*;

/**
 * Experimental mux for dumping out audio to a delimited text file. Useful for
 * debugging endian/sign/conversion problems.
 *
 * @author Ken Larson
 *
 */
public class CsvAudioMux extends AbstractInputStreamMux
{
    private static final Logger logger = LoggerSingleton.logger;

    public static void audioBufferToStream(AudioFormat f, Buffer buffer,
            OutputStream os) throws IOException
    {
        final byte[] data = (byte[]) buffer.getData();

        final int sampleSizeInBytes = f.getSampleSizeInBits() / 8;
        if (sampleSizeInBytes * 8 != f.getSampleSizeInBits())
            throw new RuntimeException(
                    "Sample size in bytes must be divisible by 8");

        final int frameSizeInBytes = sampleSizeInBytes * f.getChannels(); // TODO:
                                                                          // check
                                                                          // f.getFrameSizeInBits();

        final int framesInBuffer = buffer.getLength() / frameSizeInBytes;
        if (buffer.getLength() != framesInBuffer * frameSizeInBytes)
            throw new RuntimeException(
                    "Length of buffer not an integral number of samples");

        // if getSampleSizeInBits is 8, this value is 255:
        final long inputUnsignedMax = (1L << f.getSampleSizeInBits()) - 1;
        // if getSampleSizeInBits is 8, this value is 127:
        final long inputSignedMax = (1L << (f.getSampleSizeInBits() - 1)) - 1;

        for (int frame = 0; frame < framesInBuffer; ++frame)
        {
            for (int channel = 0; channel < f.getChannels(); ++channel)
            {
                final int offset = buffer.getOffset() + frame
                        * frameSizeInBytes + channel * sampleSizeInBytes;
                final int inputSampleLiteral = getSample(data, offset,
                        sampleSizeInBytes, f.getEndian());
                final long inputSampleLongWithoutSign = UnsignedUtils
                        .uIntToLong(inputSampleLiteral);
                final long inputSampleLongWithSign;
                if (f.getSigned() == AudioFormat.UNSIGNED)
                    inputSampleLongWithSign = inputSampleLongWithoutSign;
                else if (f.getSigned() == AudioFormat.SIGNED)
                {
                    if (inputSampleLongWithoutSign > inputSignedMax)
                        inputSampleLongWithSign = inputSampleLongWithoutSign
                                - inputUnsignedMax - 1;
                    else
                        inputSampleLongWithSign = inputSampleLongWithoutSign;
                } else
                    throw new RuntimeException(
                            "input format signed not specified");

                if (channel > 0)
                    os.write(",".getBytes());
                os.write(("" + inputSampleLongWithSign).getBytes());
            }
            os.write("\n".getBytes());
        }
    }

    // TODO: copied from RateConverter
    /**
     * bit-wise literal. Is in general unsigned, but may be signed if all 32
     * bits are used.
     */
    private static int getSample(byte[] inputBufferData,
            int byteOffsetOfSample, int inputSampleSizeInBytes, int inputEndian)
    {
        int sample = 0;
        for (int j = 0; j < inputSampleSizeInBytes; ++j)
        {
            // offset within sample handles endian-ness:
            final int offsetWithinSample = inputEndian == AudioFormat.BIG_ENDIAN ? j
                    : (inputSampleSizeInBytes - 1 - j);
            final byte b = inputBufferData[byteOffsetOfSample
                    + offsetWithinSample];
            sample <<= 8;
            sample |= b & 0xff;
        }
        // handle signed-ness.
        return sample;
    }

    private boolean headerWritten = false;

    private boolean trailerWritten = false;

    public CsvAudioMux()
    {
        super(new ContentDescriptor("audio.csv"));
    }

    @Override
    public void close()
    {
        if (!trailerWritten)
        {
            try
            {
                outputTrailer(getOutputStream());
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                throw new RuntimeException(e);
            }
            trailerWritten = true;
        }

        super.close();
    }

    @Override
    protected void doProcess(Buffer buffer, int trackID, OutputStream os)
            throws IOException
    {
        if (!headerWritten)
        {
            outputHeader(os);
            headerWritten = true;
        }
        if (buffer.isEOM())
        {
            if (!trailerWritten)
            {
                outputTrailer(os);
                trailerWritten = true;
            }
            os.close();
            return; // TODO: what if there is data in buffer?
        }

        if (buffer.isDiscard())
            return;

        audioBufferToStream((AudioFormat) inputFormats[0], buffer, os);

    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        // TODO: we accept anything, really, as long as it is in a byte array
        return new Format[] { new AudioFormat(AudioFormat.LINEAR, -1.0, -1, -1,
                -1, -1, -1, -1.0, Format.byteArray), };
    }

    @Override
    public void open() throws ResourceUnavailableException
    {
        super.open();

        if (!headerWritten)
        {
            try
            {
                outputHeader(getOutputStream());
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                throw new ResourceUnavailableException("" + e);
            }
            headerWritten = true;
        }
    }

    private void outputHeader(OutputStream os) throws IOException
    {
        // TODO: this violates the CSV format
        os.write(FormatArgUtils.toString(inputFormats[0]).getBytes());
        os.write("\n".getBytes());

    }

    private void outputTrailer(OutputStream os) throws IOException
    {
    }

    @Override
    public Format setInputFormat(Format format, int trackID)
    {
        logger.finer("setInputFormat " + format + " " + trackID);

        boolean match = false;
        for (Format supported : getSupportedInputFormats())
        {
            if (format.matches(supported))
            {
                match = true;
                break;
            }
        }
        if (!match)
        {
            logger.warning("Input format does not match any supported input format: "
                    + format);
            return null;
        }
        if (inputFormats != null) // TODO: should we save this somewhere and
                                  // apply once inputFormats is not null?
            inputFormats[trackID] = format;

        return format;
    }
}
