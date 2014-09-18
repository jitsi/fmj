package com.sun.media.format;

import java.util.*;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.utility.*;

/**
 * Coding complete.
 *
 * @author Ken Larson
 *
 */
public class WavAudioFormat extends AudioFormat
{
    public static final int WAVE_FORMAT_PCM = 1;
    public static final int WAVE_FORMAT_ADPCM = 2;
    public static final int WAVE_FORMAT_ALAW = 6;
    public static final int WAVE_FORMAT_MULAW = 7;
    public static final int WAVE_FORMAT_OKI_ADPCM = 16;
    public static final int WAVE_FORMAT_DIGISTD = 21;
    public static final int WAVE_FORMAT_DIGIFIX = 22;
    public static final int WAVE_FORMAT_GSM610 = 49;
    public static final int WAVE_IBM_FORMAT_MULAW = 257;
    public static final int WAVE_IBM_FORMAT_ALAW = 258;
    public static final int WAVE_IBM_FORMAT_ADPCM = 259;
    public static final int WAVE_FORMAT_DVI_ADPCM = 17;
    public static final int WAVE_FORMAT_SX7383 = 7175;
    public static final int WAVE_FORMAT_DSPGROUP_TRUESPEECH = 34;
    public static final int WAVE_FORMAT_MSNAUDIO = 50;
    public static final int WAVE_FORMAT_MSG723 = 66;
    public static final int WAVE_FORMAT_MPEG_LAYER3 = 85;
    public static final int WAVE_FORMAT_VOXWARE_AC8 = 112;
    public static final int WAVE_FORMAT_VOXWARE_AC10 = 113;
    public static final int WAVE_FORMAT_VOXWARE_AC16 = 114;
    public static final int WAVE_FORMAT_VOXWARE_AC20 = 115;
    public static final int WAVE_FORMAT_VOXWARE_METAVOICE = 116;
    public static final int WAVE_FORMAT_VOXWARE_METASOUND = 117;
    public static final int WAVE_FORMAT_VOXWARE_RT29H = 118;
    public static final int WAVE_FORMAT_VOXWARE_VR12 = 119;
    public static final int WAVE_FORMAT_VOXWARE_VR18 = 120;
    public static final int WAVE_FORMAT_VOXWARE_TQ40 = 121;
    public static final int WAVE_FORMAT_VOXWARE_TQ60 = 129;
    public static final int WAVE_FORMAT_MSRT24 = 130;

    protected byte[] codecSpecificHeader;
    private int averageBytesPerSecond = NOT_SPECIFIED;

    public static final Hashtable<Integer,String> formatMapper
        = new Hashtable<Integer,String>();
    public static final Hashtable<String,Integer> reverseFormatMapper
        = new Hashtable<String,Integer>();

    static
    {
        // TODO: what are these used for?
        formatMapper.put(Integer.valueOf(1), "LINEAR");
        formatMapper.put(Integer.valueOf(2), "msadpcm");
        formatMapper.put(Integer.valueOf(6), "alaw");
        formatMapper.put(Integer.valueOf(7), "ULAW");
        formatMapper.put(Integer.valueOf(17), "ima4/ms");
        formatMapper.put(Integer.valueOf(34), "truespeech");
        formatMapper.put(Integer.valueOf(49), "gsm/ms");
        formatMapper.put(Integer.valueOf(50), "msnaudio");
        formatMapper.put(Integer.valueOf(85), "mpeglayer3");
        formatMapper.put(Integer.valueOf(112), "voxwareac8");
        formatMapper.put(Integer.valueOf(113), "voxwareac10");
        formatMapper.put(Integer.valueOf(114), "voxwareac16");
        formatMapper.put(Integer.valueOf(115), "voxwareac20");
        formatMapper.put(Integer.valueOf(116), "voxwaremetavoice");
        formatMapper.put(Integer.valueOf(117), "voxwaremetasound");
        formatMapper.put(Integer.valueOf(118), "voxwarert29h");
        formatMapper.put(Integer.valueOf(119), "voxwarevr12");
        formatMapper.put(Integer.valueOf(120), "voxwarevr18");
        formatMapper.put(Integer.valueOf(121), "voxwaretq40");
        formatMapper.put(Integer.valueOf(129), "voxwaretq60");
        formatMapper.put(Integer.valueOf(130), "msrt24");

        reverseFormatMapper.put("alaw", Integer.valueOf(6));
        reverseFormatMapper.put("gsm/ms", Integer.valueOf(49));
        reverseFormatMapper.put("ima4/ms", Integer.valueOf(17));
        reverseFormatMapper.put("linear", Integer.valueOf(1));
        reverseFormatMapper.put("mpeglayer3", Integer.valueOf(85));
        reverseFormatMapper.put("msadpcm", Integer.valueOf(2));
        reverseFormatMapper.put("msnaudio", Integer.valueOf(50));
        reverseFormatMapper.put("msrt24", Integer.valueOf(130));
        reverseFormatMapper.put("truespeech", Integer.valueOf(34));
        reverseFormatMapper.put("ulaw", Integer.valueOf(7));
        reverseFormatMapper.put("voxwareac10", Integer.valueOf(113));
        reverseFormatMapper.put("voxwareac16", Integer.valueOf(114));
        reverseFormatMapper.put("voxwareac20", Integer.valueOf(115));
        reverseFormatMapper.put("voxwareac8", Integer.valueOf(112));
        reverseFormatMapper.put("voxwaremetasound", Integer.valueOf(117));
        reverseFormatMapper.put("voxwaremetavoice", Integer.valueOf(116));
        reverseFormatMapper.put("voxwarert29h", Integer.valueOf(118));
        reverseFormatMapper.put("voxwaretq40", Integer.valueOf(121));
        reverseFormatMapper.put("voxwaretq60", Integer.valueOf(129));
        reverseFormatMapper.put("voxwarevr12", Integer.valueOf(119));
        reverseFormatMapper.put("voxwarevr18", Integer.valueOf(120));

    }

    public WavAudioFormat(String encoding)
    {
        super(encoding);
    }

    public WavAudioFormat(String encoding, double sampleRate,
            int sampleSizeInBits, int channels, int frameSizeInBits,
            int averageBytesPerSecond, byte[] codecSpecificHeader)
    {
        // averageBytesPerSecond seems to be substituted for frameRate.
        super(encoding, sampleRate, sampleSizeInBits, channels, NOT_SPECIFIED,
                NOT_SPECIFIED, frameSizeInBits,
                averageBytesPerSecond /* frameRate */, byteArray);
        this.averageBytesPerSecond = averageBytesPerSecond;
        this.codecSpecificHeader = codecSpecificHeader;

    }

    public WavAudioFormat(String encoding, double sampleRate,
            int sampleSizeInBits, int channels, int frameSizeInBits,
            int averageBytesPerSecond, int endian, int signed, float frameRate,
            Class<?> dataType, byte[] codecSpecificHeader)
    {
        // averageBytesPerSecond seems to be substituted for frameRate.
        super(encoding, sampleRate, sampleSizeInBits, channels, endian, signed,
                frameSizeInBits, averageBytesPerSecond /* frameRate */,
                dataType);
        this.averageBytesPerSecond = averageBytesPerSecond;
        this.codecSpecificHeader = codecSpecificHeader;

    }

    @Override
    public Object clone()
    {
        return new WavAudioFormat(encoding, sampleRate, sampleSizeInBits,
                channels, frameSizeInBits, averageBytesPerSecond, endian,
                signed, (float) frameRate, dataType, codecSpecificHeader);

    }

    @Override
    protected void copy(Format f)
    {
        super.copy(f);
        final WavAudioFormat oCast = (WavAudioFormat) f; // it has to be a
                                                         // WavAudioFormat, or
                                                         // ClassCastException
                                                         // will be thrown.
        this.averageBytesPerSecond = oCast.averageBytesPerSecond;
        this.codecSpecificHeader = oCast.codecSpecificHeader;

    }

    @Override
    public boolean equals(Object format)
    {
        if (!super.equals(format))
            return false;

        if (!(format instanceof WavAudioFormat))
        {
            return false;
        }

        final WavAudioFormat oCast = (WavAudioFormat) format;
        return this.averageBytesPerSecond == oCast.averageBytesPerSecond
                && this.codecSpecificHeader == oCast.codecSpecificHeader; // TODO:
                                                                          // equals
                                                                          // or
                                                                          // ==
    }

    public int getAverageBytesPerSecond()
    {
        return averageBytesPerSecond;
    }

    public byte[] getCodecSpecificHeader()
    {
        return codecSpecificHeader;
    }

    @Override
    public Format intersects(Format other)
    {
        final Format result = super.intersects(other);

        if (other instanceof WavAudioFormat)
        {
            final WavAudioFormat resultCast = (WavAudioFormat) result;

            final WavAudioFormat oCast = (WavAudioFormat) other;
            if (getClass().isAssignableFrom(other.getClass()))
            {
                // "other" was cloned.

                if (FormatUtils.specified(this.averageBytesPerSecond))
                    resultCast.averageBytesPerSecond = this.averageBytesPerSecond;
                if (FormatUtils.specified(this.codecSpecificHeader))
                    resultCast.codecSpecificHeader = this.codecSpecificHeader;

            } else if (other.getClass().isAssignableFrom(getClass()))
            { // this was cloned

                if (!FormatUtils.specified(resultCast.averageBytesPerSecond))
                    resultCast.averageBytesPerSecond = oCast.averageBytesPerSecond;
                if (!FormatUtils.specified(resultCast.codecSpecificHeader))
                    resultCast.codecSpecificHeader = oCast.codecSpecificHeader;

            }
        }
        return result;
    }

    @Override
    public boolean matches(Format format)
    {
        if (!super.matches(format))
            return false;

        if (!(format instanceof WavAudioFormat))
            return true;

        final WavAudioFormat oCast = (WavAudioFormat) format;

        return FormatUtils.matches(this.averageBytesPerSecond,
                oCast.averageBytesPerSecond)
                && FormatUtils.matches(this.codecSpecificHeader,
                        oCast.codecSpecificHeader);

    }

}
