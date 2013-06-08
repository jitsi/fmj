package net.sf.fmj.media.codec;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.renderer.audio.*;
import net.sf.fmj.utility.*;

/**
 * Converts formats that JavaSound can convert.
 *
 * This has to do some tricks because of the delay in getting data from the
 * converted audio output stream. The streams are designed as streams, not
 * buffer-based, so all our threading and buffer queue tricks mean that we don't
 * get an output buffer right away for an input one. The missing output buffers
 * build up. And then we get EOM, and if the graph processing isn't done right,
 * most of the output buffers never make it to the renderer.
 *
 * TODO: if this is put ahead of com.ibm.media.codec.audio.PCMToPCM in the
 * registry, there are problems playing the safexmas movie. TODO: this should
 * perhaps be in the net.sf.fmj.media.codec.audio package.
 *
 * @author Ken Larson
 *
 */
public class JavaSoundCodec extends AbstractCodec
{
    private class AudioInputStreamThread extends Thread
    {
        private final BufferQueueInputStream bufferQueueInputStream;

        public AudioInputStreamThread(
                final BufferQueueInputStream bufferQueueInputStream)
        {
            super();
            this.bufferQueueInputStream = bufferQueueInputStream;
        }

        @Override
        public void run()
        {
            // TODO: this could take a while, perhaps it somehow needs to be
            // done in prefetch.
            try
            {
                audioInputStream = AudioSystem
                        .getAudioInputStream(new BufferedInputStream(
                                bufferQueueInputStream));
                // audioInputStream = AudioSystem.getAudioInputStream(new
                // BufferedInputStream(bufferQueueInputStream) {
                //
                // public synchronized int available() throws IOException
                // {
                // // TODO Auto-generated method stub
                // int value = super.available();
                // if (trace)
                // logger.fine(this + " available=" + value);
                // return value;
                // }
                //
                // public synchronized void mark(int readlimit)
                // {
                // // TODO Auto-generated method stub
                // if (trace)
                // logger.fine(this + " mark");
                // super.mark(readlimit);
                // }
                //
                // public synchronized int read() throws IOException
                // {
                // if (trace)
                // logger.fine(this + " read");
                // // TODO Auto-generated method stub
                // return super.read();
                // }
                //
                // public synchronized int read(byte[] b, int off, int len)
                // throws IOException
                // {
                // int result = super.read(b, off, len);
                //
                // if (trace)
                // logger.fine(this + " read " + len + " returning " + result);
                //
                // return result;
                // }
                //
                // public synchronized void reset() throws IOException
                // {
                // if (trace)
                // logger.fine(this + " RESET");
                //
                // // TODO Auto-generated method stub
                // super.reset();
                // }
                //
                // });
            } catch (UnsupportedAudioFileException e)
            {
                logger.log(Level.WARNING, "" + e, e); // TODO
                // throw new ResourceUnavailableException(e.getMessage());
                return;
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e); // TODO
                // throw new ResourceUnavailableException(e.getMessage());
                return;
            }

            final javax.sound.sampled.AudioFormat javaSoundAudioFormat = JavaSoundUtils
                    .convertFormat((AudioFormat) outputFormat);
            logger.fine("javaSoundAudioFormat converted (out)="
                    + javaSoundAudioFormat);
            audioInputStreamConverted = AudioSystem.getAudioInputStream(
                    javaSoundAudioFormat, audioInputStream);

        }
    }

    private static final Logger logger = LoggerSingleton.logger;

    private BufferQueueInputStream bufferQueueInputStream;

    private volatile AudioInputStream audioInputStream; // set in one thread and
                                                        // read in another
    private volatile AudioInputStream audioInputStreamConverted;
    private AudioInputStreamThread audioInputStreamThread;
    private boolean trace;

    private int totalIn;

    private int totalOut;
    private static final int SIZEOF_INT = 4;
    private static final int SIZEOF_LONG = 8;

    private static final int SIZEOF_SHORT = 2;
    private static final int BITS_PER_BYTE = 8;

    private static final int MAX_SIGNED_BYTE = 127;

    private static final int MAX_BYTE = 0xFF;

    private static final int MAX_BYTE_PLUS1 = 256;

    /**
     * Used to create a "fake" AU header for fakeHeader. See
     * http://en.wikipedia.org/wiki/Au_file_format. The Au file format is a
     * simple audio file format that consists of a header of 6 32-bit words and
     * then the data (high-order byte comes first). The format was introduced by
     * Sun Microsystems.
     */
    public static byte[] createAuHeader(javax.sound.sampled.AudioFormat f)
    {
        byte[] result = new byte[4 * 6];
        encodeIntBE(0x2e736e64, result, 0); // the value 0x2e736e64 (four ASCII
                                            // characters ".snd")
        encodeIntBE(result.length, result, 4); // the offset to the data in
                                               // bytes. The minimum valid
                                               // number is 24 (decimal).
        encodeIntBE(0xffffffff, result, 8); // data size in bytes. If unknown,
                                            // the value 0xffffffff should be
                                            // used.

        // Data encoding format:
        // 1=8-bit ISDN u-law, 2=8-bit linear PCM [REF-PCM], 3=16-bit linear
        // PCM, 4=24-bit linear PCM,
        // 5=32-bit linear PCM, 6=32-bit IEEE floating point, 7=64-bit IEEE
        // floating point,
        // 23=8-bit ISDN u-law compressed using the UIT-T G.721 ADPCM voice data
        // encoding scheme.

        final int encoding;
        if (f.getEncoding() == Encoding.ALAW)
        {
            if (f.getSampleSizeInBits() == 8)
                encoding = 27;
            else
                return null;
        } else if (f.getEncoding() == Encoding.ULAW)
        {
            if (f.getSampleSizeInBits() == 8)
                encoding = 1;
            else
                return null;
        } else if (f.getEncoding() == Encoding.PCM_SIGNED)
        {
            // AU appears to be signed when it uses PCM
            if (f.getSampleSizeInBits() == 8)
                encoding = 2;
            else if (f.getSampleSizeInBits() == 16)
                encoding = 3;
            else if (f.getSampleSizeInBits() == 24)
                encoding = 4;
            else if (f.getSampleSizeInBits() == 32)
                encoding = 5;
            else
                return null;

            if (f.getSampleSizeInBits() > 8 && !f.isBigEndian())
                return null; // must be big-endian
        } else if (f.getEncoding() == Encoding.PCM_UNSIGNED)
        {
            // AU appears to be signed when it uses PCM
            return null;
        } else
        {
            return null;
        }

        encodeIntBE(encoding, result, 12);

        // sample rate - the number of samples/second (e.g., 8000)
        if (f.getSampleRate() < 0)
            return null;
        encodeIntBE((int) f.getSampleRate(), result, 16);

        // channels the number of interleaved channels (e.g., 1 for mono, 2 for
        // stereo)
        if (f.getChannels() < 0)
            return null;
        encodeIntBE(f.getChannels(), result, 20);

        return result;

    }

    /**
     * See http://ccrma.stanford.edu/courses/422/projects/WaveFormat/.
     *
     * @param f
     * @return the header for the wav
     */
    public static byte[] createWavHeader(javax.sound.sampled.AudioFormat f)
    {
        // from the web link:
        // 8-bit samples are stored as unsigned bytes, ranging from 0 to 255.
        // 16-bit samples are stored as 2's-complement signed integers, ranging
        // from -32768 to 32767.

        if (f.getEncoding() != Encoding.PCM_SIGNED
                && f.getEncoding() != Encoding.PCM_UNSIGNED)
            return null;

        if (f.getSampleSizeInBits() == 8
                && f.getEncoding() != Encoding.PCM_UNSIGNED)
            return null;

        if (f.getSampleSizeInBits() == 16
                && f.getEncoding() != Encoding.PCM_SIGNED)
            return null;

        byte[] result = new byte[44];

        // from the web link:
        // The default byte ordering assumed for WAVE data files is
        // little-endian.
        // Files written using the big-endian byte ordering scheme have the
        // identifier RIFX instead of RIFF.
        if (f.getSampleSizeInBits() > 8 && f.isBigEndian())
            encodeIntBE(0x52494658, result, 0); // Contains the letters "RIFX"
                                                // in ASCII form (0x52494658
                                                // big-endian form).
        else
            encodeIntBE(0x52494646, result, 0); // Contains the letters "RIFF"
                                                // in ASCII form (0x52494646
                                                // big-endian form).

        int len = Integer.MAX_VALUE; // TODO: it is unknown, what to do?
                                     // Hopefully JavaSound won't care
        encodeIntLE(len + result.length - 8, result, 4); // total length minus
                                                         // the first 2 ints

        encodeIntBE(0x57415645, result, 8); // Contains the letters "WAVE"
                                            // (0x57415645 big-endian form).

        // The "WAVE" format consists of two subchunks: "fmt " and "data":
        // The "fmt " subchunk describes the sound data's format:

        encodeIntBE(0x666d7420, result, 12); // Contains the letters "fmt "
                                             // (0x666d7420 big-endian form).

        encodeIntLE(16, result, 16); // 16 for PCM. This is the size of the rest
                                     // of the Subchunk which follows this
                                     // number.

        encodeShortLE((short) 1, result, 20); // PCM = 1 (i.e. Linear
                                              // quantization) Values other than
                                              // 1 indicate some form of
                                              // compression.

        encodeShortLE((short) f.getChannels(), result, 22); // NumChannels Mono
                                                            // = 1, Stereo = 2,
                                                            // etc.

        encodeIntLE((int) f.getSampleRate(), result, 24); // SampleRate 8000,
                                                          // 44100, etc.

        encodeIntLE(
                (((int) f.getSampleRate()) * f.getChannels() * f
                        .getSampleSizeInBits()) / 8,
                result, 28); // ByteRate == SampleRate * NumChannels *
                             // BitsPerSample/8

        encodeShortLE(
                (short) ((f.getChannels() * f.getSampleSizeInBits()) / 8),
                result, 32); // BlockAlign == NumChannels * BitsPerSample/8 The
                             // number of bytes for one sample including all
                             // channels. I wonder what happens when this number
                             // isn't an integer?

        encodeShortLE((short) f.getSampleSizeInBits(), result, 34); // BitsPerSample
                                                                    // 8 bits =
                                                                    // 8, 16
                                                                    // bits =
                                                                    // 16, etc.

        // The "data" subchunk contains the size of the data and the actual
        // sound:

        encodeIntBE(0x64617461, result, 36);// Subchunk2ID Contains the letters
                                            // "data" (0x64617461 big-endian
                                            // form).

        encodeIntLE(len, result, 40); // Subchunk2Size == NumSamples *
                                      // NumChannels * BitsPerSample/8

        return result;
    }

    private static void encodeIntBE(int value, byte[] ba, int offset)
    {
        int length = SIZEOF_INT;

        for (int i = 0; i < length; ++i)
        {
            int byteValue = value & MAX_BYTE;
            if (byteValue > MAX_SIGNED_BYTE)
                byteValue = byteValue - MAX_BYTE_PLUS1;

            ba[offset + (length - i - 1)] = (byte) byteValue;

            value = value >> BITS_PER_BYTE;
        }
    }

    private static void encodeIntLE(int value, byte[] ba, int offset)
    {
        int length = SIZEOF_INT;

        for (int i = 0; i < length; ++i)
        {
            int byteValue = value & MAX_BYTE;
            if (byteValue > MAX_SIGNED_BYTE)
                byteValue = byteValue - MAX_BYTE_PLUS1;

            ba[offset + i] = (byte) byteValue;

            value = value >> BITS_PER_BYTE;
        }
    }

    public static void encodeShortBE(short value, byte[] ba, int offset)
    {
        int length = SIZEOF_SHORT;

        for (int i = 0; i < length; ++i)
        {
            int byteValue = value & MAX_BYTE;
            if (byteValue > MAX_SIGNED_BYTE)
                byteValue = byteValue - MAX_BYTE_PLUS1;

            ba[offset + (length - i - 1)] = (byte) byteValue;

            value = (short) (value >> BITS_PER_BYTE);
        }
    }

    public static void encodeShortLE(short value, byte[] ba, int offset)
    {
        int length = SIZEOF_SHORT;

        for (int i = 0; i < length; ++i)
        {
            int byteValue = value & MAX_BYTE;
            if (byteValue > MAX_SIGNED_BYTE)
                byteValue = byteValue - MAX_BYTE_PLUS1;

            ba[offset + i] = (byte) byteValue;

            value = (short) (value >> BITS_PER_BYTE);
        }
    }

    /**
     * See the comments in JavaSoundParser for more info.
     * AudioSystem.getAudioInputStream lets us specify the output format, but it
     * does not let us specify the input format. It gets the input format by
     * reading the stream. However, the parser has already stripped off the
     * headers, and the data we are getting is pure. To use JavaSound, we have
     * to create a fake header based on the input format information. It does
     * not matter whether this header matches the original header, it just has
     * to get AudioSystem.getAudioInputStream To figure out what kind of format
     * it is.
     *
     */
    private static byte[] fakeHeader(javax.sound.sampled.AudioFormat f)
    {
        Class<?> classVorbisAudioFormat = null;
        Class<?> classMpegAudioFormatt = null;

        if (!JavaSoundUtils.onlyStandardFormats)
        {
            try
            {
                classMpegAudioFormatt = Class
                        .forName("javazoom.spi.mpeg.sampled.file.MpegAudioFormat");
                classVorbisAudioFormat = Class
                        .forName("javazoom.spi.vorbis.sampled.file.VorbisAudioFormat");
            } catch (Exception dontcare)
            {
            }
        }

        // headers are not stripped off for MPEG 3
        if ((null != classMpegAudioFormatt)
                && classMpegAudioFormatt.isInstance(f))
        {
            return new byte[0]; // empty header
        }
        if ((null != classVorbisAudioFormat)
                && classVorbisAudioFormat.isInstance(f))
        {
            return new byte[0]; // empty header
        }

        byte[] result = createAuHeader(f);
        if (result != null)
            return result;

        result = createWavHeader(f);
        if (result != null)
            return result;

        return null; // not able to create a header
    }

    public JavaSoundCodec()
    {
        Vector<Format> formats = new Vector<Format>();

        formats.add(new AudioFormat(AudioFormat.ULAW));
        formats.add(new AudioFormat(AudioFormat.ALAW));
        formats.add(new AudioFormat(AudioFormat.LINEAR));

        if (!JavaSoundUtils.onlyStandardFormats)
        {
            // mgodehardt: now using reflection
            try
            {
                Class<?> classMpegEncoding = Class
                        .forName("javazoom.spi.mpeg.sampled.file.MpegEncoding");

                final String[] mpegEncodingStrings = { "MPEG1L1", "MPEG1L2",
                        "MPEG1L3", "MPEG2DOT5L1", "MPEG2DOT5L2", "MPEG2DOT5L3",
                        "MPEG2L1", "MPEG2L2", "MPEG2L3" };

                for (int i = 0; i < mpegEncodingStrings.length; i++)
                {
                    formats.add(new AudioFormat(mpegEncodingStrings[i]));
                }
            } catch (Exception dontcare)
            {
            }

            // mgodehardt: now using reflection
            try
            {
                Class<?> classVorbisEncoding = Class
                        .forName("javazoom.spi.vorbis.sampled.file.VorbisEncoding");

                final String[] vorbisEncodingStrings = { "VORBISENC" };

                for (int i = 0; i < vorbisEncodingStrings.length; i++)
                {
                    formats.add(new AudioFormat(vorbisEncodingStrings[i]));
                }
            } catch (Exception dontcare)
            {
            }
        }

        // TODO: get dynamically from java sound. Not sure if this is possible

        inputFormats = new Format[formats.size()];
        formats.toArray(inputFormats);
    }

    // @Override
    @Override
    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
        {
            return new Format[] { new AudioFormat(AudioFormat.LINEAR) }; // TODO
        }
        final javax.sound.sampled.AudioFormat javaSoundFormat = JavaSoundUtils
                .convertFormat((AudioFormat) input);

        // TODO: we could call AudioSystem.getTargetEncodings(javaSoundFormat);
        // rather than hard code the PCM ones:
        final javax.sound.sampled.AudioFormat[] targets1 = AudioSystem
                .getTargetFormats(Encoding.PCM_UNSIGNED, javaSoundFormat);
        final javax.sound.sampled.AudioFormat[] targets2 = AudioSystem
                .getTargetFormats(Encoding.PCM_SIGNED, javaSoundFormat);

        final javax.sound.sampled.AudioFormat[] targetsSpecial;

        Class<?> classVorbisAudioFormat = null;
        Class<?> classMpegAudioFormatt = null;

        if (!JavaSoundUtils.onlyStandardFormats)
        {
            try
            {
                classMpegAudioFormatt = Class
                        .forName("javazoom.spi.mpeg.sampled.file.MpegAudioFormat");
                classVorbisAudioFormat = Class
                        .forName("javazoom.spi.vorbis.sampled.file.VorbisAudioFormat");
            } catch (Exception dontcare)
            {
            }
        }

        // for some reason, AudioSystem.getTargetFormats doesn't return anything
        // for MpegAudioFormat
        if ((null != classMpegAudioFormatt)
                && classMpegAudioFormatt.isInstance(javaSoundFormat))
        {
            javax.sound.sampled.AudioFormat decodedFormat = new javax.sound.sampled.AudioFormat(
                    javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
                    javaSoundFormat.getSampleRate(), 16,
                    javaSoundFormat.getChannels(),
                    javaSoundFormat.getChannels() * 2,
                    javaSoundFormat.getSampleRate(), false);
            targetsSpecial = new javax.sound.sampled.AudioFormat[] { decodedFormat };
        } else if ((null != classVorbisAudioFormat)
                && classVorbisAudioFormat.isInstance(javaSoundFormat))
        {
            // TODO: what is the correct mapping?
            javax.sound.sampled.AudioFormat decodedFormat = new javax.sound.sampled.AudioFormat(
                    javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
                    javaSoundFormat.getSampleRate(), 16,
                    javaSoundFormat.getChannels(),
                    javaSoundFormat.getChannels() * 2,
                    javaSoundFormat.getSampleRate(), false);
            targetsSpecial = new javax.sound.sampled.AudioFormat[] { decodedFormat };
        } else
        {
            targetsSpecial = new javax.sound.sampled.AudioFormat[0];
        }

        final Format[] result = new Format[targets1.length + targets2.length
                + targetsSpecial.length];
        for (int i = 0; i < targets1.length; ++i)
        {
            result[i] = JavaSoundUtils.convertFormat(targets1[i]);
            logger.finer("getSupportedOutputFormats: " + result[i]);
        }
        for (int i = 0; i < targets2.length; ++i)
        {
            result[targets1.length + i] = JavaSoundUtils
                    .convertFormat(targets2[i]);
            logger.finer("getSupportedOutputFormats: "
                    + result[targets1.length + i]);
        }
        for (int i = 0; i < targetsSpecial.length; ++i)
        {
            result[targets1.length + targets2.length + i] = JavaSoundUtils
                    .convertFormat(targetsSpecial[i]);
            logger.finer("getSupportedOutputFormats: "
                    + result[targets1.length + targets2.length + i]);
        }
        // this all really depends on where MP3 decoding is handled in JavaSound
        // with an SPI.
        for (int i = 0; i < result.length; ++i)
        {
            AudioFormat a = ((AudioFormat) result[i]);
            AudioFormat inputAudioFormat = (AudioFormat) input;
            // converting from a sound format with a specific sample rate to one
            // without causes problems building filter graphs.
            // if the input format specifies a sample rate, we are only really
            // interested in output formats with concrete sample
            // rates.
            if (FormatUtils.specified(inputAudioFormat.getSampleRate())
                    && !FormatUtils.specified(a.getSampleRate()))
                result[i] = null; // TODO: remove from array.

        }
        return result;
    }

    // @Override
    @Override
    public void open() throws ResourceUnavailableException
    {
        super.open();

        bufferQueueInputStream = new BufferQueueInputStream(); // TODO: limit
                                                               // should be
                                                               // total bytes,
                                                               // not number of
                                                               // bufers.
        // bufferQueueInputStream.setTrace(((AudioFormat)
        // inputFormat).getEncoding().equals("LINEAR") && ((AudioFormat)
        // inputFormat).getSampleRate() == 22050.0);
        // this.setTrace(((AudioFormat)
        // inputFormat).getEncoding().equals("LINEAR") && ((AudioFormat)
        // inputFormat).getSampleRate() == 22050.0);

        // create fake header (see below)
        final javax.sound.sampled.AudioFormat javaSoundAudioFormat = JavaSoundUtils
                .convertFormat((AudioFormat) inputFormat);
        logger.fine("javaSoundAudioFormat converted (in)="
                + javaSoundAudioFormat);

        final byte[] header = fakeHeader(javaSoundAudioFormat);
        if (header == null)
            throw new ResourceUnavailableException(
                    "Unable to reconstruct header for format: " + inputFormat);
        if (header.length > 0)
        {
            Buffer headerBuffer = new Buffer();
            headerBuffer.setData(header);
            headerBuffer.setLength(header.length);
            bufferQueueInputStream.put(headerBuffer);
        }

        audioInputStreamThread = new AudioInputStreamThread(
                bufferQueueInputStream);
        audioInputStreamThread.start();

    }

    // @Override
    @Override
    public int process(Buffer input, Buffer output)
    {
        if (!checkInputBuffer(input))
        {
            return BUFFER_PROCESSED_FAILED;
        }

        // we can't do this since we need to put the EOM into the
        // bufferQueueInputStream, so it will return EOS to the audio input
        // stream.
        // if (isEOM(input))
        // {
        // propagateEOM(output); // TODO: what about data? can there be any?
        // return BUFFER_PROCESSED_OK;
        // }

        try
        {
            if (trace)
                logger.fine("process: " + LoggingStringUtils.bufferToStr(input));
            totalIn += input.getLength();

            final boolean noRoomInBufferQueue;

            // copy input buffer to central buffer
            noRoomInBufferQueue = !bufferQueueInputStream.put(input);
            // if noRoomInBufferQueue is true, we will return
            // INPUT_BUFFER_NOT_CONSUMED below.
            // if noRoomInBufferQueue is true, we will have to block and get
            // some actual data.

            if (audioInputStreamConverted == null)
            {
                if (noRoomInBufferQueue)
                {
                    logger.fine("JavaSoundCodec: audioInputStreamConverted == null, blocking until not null");

                    // block until input stream is ready
                    try
                    {
                        while (audioInputStreamConverted == null)
                            Thread.sleep(100);
                    } catch (InterruptedException e)
                    {
                        return BUFFER_PROCESSED_FAILED;
                    }
                } else
                {
                    logger.fine("JavaSoundCodec: audioInputStreamConverted == null, returning OUTPUT_BUFFER_NOT_FILLED");

                    output.setLength(0);
                    return OUTPUT_BUFFER_NOT_FILLED;
                }
            }
            // the potential problem with available() is if it returns too
            // little, or 0, when it could return more. This is allowed.
            // if our buffer queue is full, we'll need to do a potentially
            // blocking read, even if available() returns 0.

            // if available violates the spec and returns more than it should,
            // we have to use another threa.

            final int avail = audioInputStreamConverted.available();
            if (trace)
                logger.fine("audioInputStreamConverted.available() == " + avail
                        + ", bufferQueueInputStream.available() = "
                        + bufferQueueInputStream.available());
            if (output.getData() == null)
                output.setData(new byte[10000]); // TODO: size.
            output.setFormat(getOutputFormat());
            final byte[] data = (byte[]) output.getData();

            // if audioInputStreamConverted has reached EOM, available will
            // return 0.
            // we assume that audioInputStreamConverted can only reach EOM after
            // the input has
            // reached EOM, so if the input has reached EOM, we ignore avail,
            // and go ahead and
            // either do a blocking read or read to get -1.

            int lenToRead;
            if (noRoomInBufferQueue || input.isEOM())
                lenToRead = data.length; // force a potentially blocking read
            else
                lenToRead = avail > data.length ? data.length : avail;

            if (lenToRead == 0)
            {
                logger.finer("JavaSoundCodec: lenToRead == 0, returning OUTPUT_BUFFER_NOT_FILLED.  input.isEOM()="
                        + input.isEOM());

                // TODO: detect EOM
                output.setLength(0);
                return OUTPUT_BUFFER_NOT_FILLED;
            }
            // TODO: set format
            // TODO: Tritonus (ulaw conversion) has a flawed input stream
            // implementation. audioInputStreamConverted.available() is supposed
            // to
            // return the number of bytes that can be read without blocking. But
            // when we call with this number,
            // tritonus goes ahead and reads from the BufferQueueInputStream.
            // This causes a lockup.
            //
            final int lenRead = audioInputStreamConverted.read(data, 0,
                    lenToRead);

            logger.finer("JavaSoundCodec: Read from audioInputStreamConverted: "
                    + lenRead);

            if (lenRead == -1)
            {
                logger.fine("total in: " + totalIn + " total out: " + totalOut);
                output.setEOM(true);
                output.setLength(0);
                return BUFFER_PROCESSED_OK;
            }
            output.setLength(lenRead);
            totalOut += lenRead;
            return (noRoomInBufferQueue || input.isEOM()) ? INPUT_BUFFER_NOT_CONSUMED
                    : BUFFER_PROCESSED_OK; // if input is EOM, then they have to
                                           // keep calling us with it until we
                                           // finish processing

        } catch (IOException e)
        {
            output.setLength(0);
            return BUFFER_PROCESSED_FAILED;
        }

    }

    void setTrace(boolean value)
    {
        this.trace = value;
    }

}
