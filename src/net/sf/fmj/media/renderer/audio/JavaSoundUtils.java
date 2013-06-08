package net.sf.fmj.media.renderer.audio;

import java.lang.reflect.*;
import java.util.*;

import javax.media.*;
import javax.media.format.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

/**
 * Utilities for dealing with JavaSound formats.
 *
 * @author Ken Larson
 *
 */
public class JavaSoundUtils
{
    public static boolean onlyStandardFormats = false;

    public static javax.sound.sampled.AudioFormat convertFormat(
            AudioFormat format)
    {
        String encodingString = format.getEncoding();
        int channels = format.getChannels();
        double frameRate = format.getFrameRate();
        int frameSize = format.getFrameSizeInBits() / 8;
        double sampleRate = format.getSampleRate();
        int sampleSize = format.getSampleSizeInBits();
        // if (sampleSize > 8 && format.getEndian() ==
        // AudioFormat.NOT_SPECIFIED)
        // throw new IllegalArgumentException("Endianness must be specified");
        boolean endian = (format.getEndian() == AudioFormat.BIG_ENDIAN);
        int signed = format.getSigned();

        Encoding encoding;
        if (AudioFormat.LINEAR.equals(encodingString))
        {
            switch (signed)
            {
            case AudioFormat.SIGNED:
                encoding = Encoding.PCM_SIGNED;
                break;
            case AudioFormat.UNSIGNED:
                encoding = Encoding.PCM_UNSIGNED;
                break;
            default:
                throw new IllegalArgumentException(
                        "Signed/Unsigned must be specified");
                // encoding = Encoding.PCM_SIGNED; // TODO: return null
            }
        } else if (AudioFormat.ALAW.equals(encodingString))
        {
            encoding = Encoding.ALAW;
        } else if (AudioFormat.ULAW.equals(encodingString))
        {
            encoding = Encoding.ULAW;
        } else if (toMpegEncoding(encodingString) != null)
        {
            encoding = toMpegEncoding(encodingString);

        } else if (toVorbisEncoding(encodingString) != null)
        {
            encoding = toVorbisEncoding(encodingString);

        } else
        {
            encoding = new CustomEncoding(encodingString);
        }

        javax.sound.sampled.AudioFormat sampledFormat;

        Class<?> classMpegEncoding = null;
        Class<?> classVorbisEncoding = null;

        if (!JavaSoundUtils.onlyStandardFormats)
        {
            try
            {
                classMpegEncoding = Class
                        .forName("javazoom.spi.mpeg.sampled.file.MpegEncoding");
                classVorbisEncoding = Class
                        .forName("javazoom.spi.vorbis.sampled.file.VorbisEncoding");
            } catch (Exception dontcare)
            {
            }
        }

        if (encoding == Encoding.PCM_SIGNED)
        {
            sampledFormat = new javax.sound.sampled.AudioFormat(
                    (float) sampleRate, sampleSize, channels, true, endian);

        } else if (encoding == Encoding.PCM_UNSIGNED)
        {
            sampledFormat = new javax.sound.sampled.AudioFormat(
                    (float) sampleRate, sampleSize, channels, false, endian);
        } else if ((null != classMpegEncoding)
                && classMpegEncoding.isInstance(encoding))
        {
            try
            {
                Class<?> classMpegAudioFormat = Class
                        .forName("javazoom.spi.mpeg.sampled.file.MpegAudioFormat");

                Class<?> partypes[] = new Class[8];
                partypes[0] = javax.sound.sampled.AudioFormat.Encoding.class;
                partypes[1] = Float.TYPE;
                partypes[2] = Integer.TYPE;
                partypes[3] = Integer.TYPE;
                partypes[4] = Integer.TYPE;
                partypes[5] = Float.TYPE;
                partypes[6] = Boolean.TYPE;
                partypes[7] = java.util.Map.class;

                Constructor<?> ct = classMpegAudioFormat.getConstructor(partypes);

                Object arglist[] = new Object[8];
                arglist[0] = encoding;
                arglist[1] = (float) sampleRate;
                arglist[2] = sampleSize;
                arglist[3] = channels;
                arglist[4] = frameSize;
                arglist[5] = (float) frameRate;
                arglist[6] = endian;
                arglist[7] = new HashMap();

                sampledFormat = (javax.sound.sampled.AudioFormat) ct
                        .newInstance(arglist);
            } catch (Exception dontcare)
            {
                sampledFormat = null;
            }
        } else if ((null != classVorbisEncoding)
                && classVorbisEncoding.isInstance(encoding))
        {
            try
            {
                Class<?> classVorbisAudioFormat = Class
                        .forName("javazoom.spi.vorbis.sampled.file.VorbisAudioFormat");

                Class<?> partypes[] = new Class[8];
                partypes[0] = javax.sound.sampled.AudioFormat.Encoding.class;
                partypes[1] = Float.TYPE;
                partypes[2] = Integer.TYPE;
                partypes[3] = Integer.TYPE;
                partypes[4] = Integer.TYPE;
                partypes[5] = Float.TYPE;
                partypes[6] = Boolean.TYPE;
                partypes[7] = java.util.Map.class;

                Constructor<?> ct = classVorbisAudioFormat
                        .getConstructor(partypes);

                Object arglist[] = new Object[8];
                arglist[0] = encoding;
                arglist[1] = (float) sampleRate;
                arglist[2] = sampleSize;
                arglist[3] = channels;
                arglist[4] = frameSize;
                arglist[5] = (float) frameRate;
                arglist[6] = endian;
                arglist[7] = new HashMap();

                sampledFormat = (javax.sound.sampled.AudioFormat) ct
                        .newInstance(arglist);
            } catch (Exception dontcare)
            {
                sampledFormat = null;
            }
        } else
        {
            sampledFormat = new javax.sound.sampled.AudioFormat(encoding,
                    (float) sampleRate, sampleSize, channels, frameSize,
                    (float) frameRate, endian);
        }

        return sampledFormat;
    }

    // TODO: move remaining mpeg/vorbis code from JavaSoundCodec to here, make
    // it work without those SPIs.
    /**
     * Convert javax.sound.sampled.AudioFormat to
     * javax.media.format.AudioFormat.
     */
    public static AudioFormat convertFormat(
            javax.sound.sampled.AudioFormat format)
    {
        Encoding encoding = format.getEncoding();
        int channels = format.getChannels();
        float frameRate = format.getFrameRate();
        int frameSize = format.getFrameSize() < 0 ? format.getFrameSize()
                : (format.getFrameSize() * 8);
        float sampleRate = format.getSampleRate();
        int sampleSize = format.getSampleSizeInBits();

        int endian = format.isBigEndian() ? AudioFormat.BIG_ENDIAN
                : AudioFormat.LITTLE_ENDIAN;

        int signed = Format.NOT_SPECIFIED;
        String encodingString = AudioFormat.LINEAR;

        if (encoding == Encoding.PCM_SIGNED)
        {
            signed = AudioFormat.SIGNED;
            encodingString = AudioFormat.LINEAR;
        } else if (encoding == Encoding.PCM_UNSIGNED)
        {
            signed = AudioFormat.UNSIGNED;
            encodingString = AudioFormat.LINEAR;
        } else if (encoding == Encoding.ALAW)
        {
            encodingString = AudioFormat.ALAW;
        } else if (encoding == Encoding.ULAW)
        {
            encodingString = AudioFormat.ULAW;
        } else
        {
            encodingString = encoding.toString();

        }

        AudioFormat jmfFormat = new AudioFormat(encodingString, sampleRate,
                sampleSize, channels, endian, signed, frameSize, frameRate,
                Format.byteArray);

        return jmfFormat;
    }

    /**
     *
     * @return null if doesn't match any mpeg encoding
     */
    private static Encoding toMpegEncoding(String encodingStr)
    {
        try
        {
            if (!JavaSoundUtils.onlyStandardFormats)
            {
                // mgodehardt: now using reflection
                Class<?> classMpegEncoding = Class
                        .forName("javazoom.spi.mpeg.sampled.file.MpegEncoding");

                final String[] mpegEncodingStrings = { "MPEG1L1", "MPEG1L2",
                        "MPEG1L3", "MPEG2DOT5L1", "MPEG2DOT5L2", "MPEG2DOT5L3",
                        "MPEG2L1", "MPEG2L2", "MPEG2L3" };

                final Encoding[] mpegEncodings = new Encoding[mpegEncodingStrings.length];

                for (int i = 0; i < mpegEncodings.length; i++)
                {
                    Field aField = classMpegEncoding
                            .getDeclaredField(mpegEncodingStrings[i]);
                    mpegEncodings[i] = (Encoding) aField.get(null);
                }

                for (int i = 0; i < mpegEncodings.length; ++i)
                {
                    if (encodingStr.equals(mpegEncodings[i].toString()))
                    {
                        return mpegEncodings[i];
                    }
                }
            }
        } catch (Exception dontcare)
        {
        }

        return null;
    }

    /**
     *
     * @return null if doesn't match any vorbis encoding
     */
    private static Encoding toVorbisEncoding(String encodingStr)
    {
        try
        {
            if (!JavaSoundUtils.onlyStandardFormats)
            {
                // mgodehardt: now using reflection
                Class<?> classVorbisEncoding = Class
                        .forName("javazoom.spi.vorbis.sampled.file.VorbisEncoding");

                final String[] vorbisEncodingStrings = { "VORBISENC" };

                final Encoding[] vorbisEncodings = new Encoding[vorbisEncodingStrings.length];

                for (int i = 0; i < vorbisEncodings.length; i++)
                {
                    Field aField = classVorbisEncoding
                            .getDeclaredField(vorbisEncodingStrings[i]);
                    vorbisEncodings[i] = (Encoding) aField.get(null);
                }

                for (int i = 0; i < vorbisEncodings.length; ++i)
                {
                    if (encodingStr.equals(vorbisEncodings[i].toString()))
                    {
                        return vorbisEncodings[i];
                    }
                }
            }
        } catch (Exception dontcare)
        {
        }

        return null;
    }

}
