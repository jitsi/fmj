package net.sf.fmj.utility;

import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;

import javax.media.Format;
import javax.media.format.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.format.*;

/**
 * A class for converting Format objects to and from strings that can be used as
 * arguments in command-line programs, or as parameters in URLs.
 *
 * The syntax is this: all elements are separated by a colon. Everything is
 * uppercase by default, but case is ignored. Only thing that is lowercase is x
 * in dimension. Generally, each item corresponds to a constructor argument. The
 * Format subclass is inferred from the encoding. ? is used to indicate
 * Format.NOT_SPECIFIED (-1). floating point values in audio formats are done as
 * integers. In audio formats, Big endian is B, little endian is L, signed is S,
 * unsigned is U Data types: B is byte[], S is short[], I is int[] Dimension:
 * [width]x[height], like "640x480" Trailing not specified values may be
 * omitted.
 *
 * new AudioFormat(AudioFormat.LINEAR, 44100.0, 16, 2) would be
 * LINEAR:44100:16:2
 *
 *
 *
 * TODO: support WavAudioFormat, video formats, and other missing audio formats.
 *
 * @author Ken Larson
 *
 */
public class FormatArgUtils
{
    private static class Tokens
    {
        private final String[] items;
        private int ix;

        public Tokens(String[] items)
        {
            super();
            this.items = items;
            ix = 0;
        }

        public Class<?> nextDataType() throws ParseException
        {
            String s = nextString();
            if (s == null)
                return null;

            if (s.equals(NOT_SPECIFIED))
                return null;

            s = s.toUpperCase();

            if (s.equals(BYTE_ARRAY))
                return Format.byteArray;
            else if (s.equals(SHORT_ARRAY))
                return Format.shortArray;
            else if (s.equals(INT_ARRAY))
                return Format.intArray;
            else
                throw new ParseException("Expected one of [" + BYTE_ARRAY + ","
                        + SHORT_ARRAY + "," + INT_ARRAY + "]: " + s, -1);

        }

        public Dimension nextDimension() throws ParseException
        {
            String s = nextString();
            if (s == null)
                return null;

            if (s.equals(NOT_SPECIFIED))
                return null;

            s = s.toUpperCase();

            String[] strings = s.split("X");
            if (strings.length != 2)
                throw new ParseException("Expected WIDTHxHEIGHT: " + s, -1);
            int width;
            int height;

            try
            {
                width = Integer.parseInt(strings[0]);
            } catch (NumberFormatException e)
            {
                throw new ParseException("Expected integer: " + strings[0], -1);
            }
            try
            {
                height = Integer.parseInt(strings[1]);
            } catch (NumberFormatException e)
            {
                throw new ParseException("Expected integer: " + strings[1], -1);
            }

            return new Dimension(width, height);
        }

        public double nextDouble() throws ParseException
        {
            final String s = nextString();
            if (s == null)
                return Format.NOT_SPECIFIED;

            if (s.equals(NOT_SPECIFIED))
                return Format.NOT_SPECIFIED;

            try
            {
                return Double.parseDouble(s);
            } catch (NumberFormatException e)
            {
                throw new ParseException("Expected double: " + s, -1);
            }
        }

        public int nextEndian() throws ParseException
        {
            String s = nextString();
            if (s == null)
                return Format.NOT_SPECIFIED;

            if (s.equals(NOT_SPECIFIED))
                return Format.NOT_SPECIFIED;

            s = s.toUpperCase();

            if (s.equals(BIG_ENDIAN))
                return AudioFormat.BIG_ENDIAN;
            else if (s.equals(LITTLE_ENDIAN))
                return AudioFormat.LITTLE_ENDIAN;
            else
                throw new ParseException("Expected one of [" + BIG_ENDIAN + ","
                        + LITTLE_ENDIAN + "]: " + s, -1);
        }

        public float nextFloat() throws ParseException
        {
            final String s = nextString();
            if (s == null)
                return Format.NOT_SPECIFIED;

            if (s.equals(NOT_SPECIFIED))
                return Format.NOT_SPECIFIED;

            try
            {
                return Float.parseFloat(s);
            } catch (NumberFormatException e)
            {
                throw new ParseException("Expected float: " + s, -1);
            }
        }

        public int nextInt() throws ParseException
        {
            final String s = nextString();
            if (s == null)
                return Format.NOT_SPECIFIED;

            if (s.equals(NOT_SPECIFIED))
                return Format.NOT_SPECIFIED;

            try
            {
                return Integer.parseInt(s);
            } catch (NumberFormatException e)
            {
                throw new ParseException("Expected integer: " + s, -1);
            }
        }

        public int nextRGBFormatEndian() throws ParseException
        {
            String s = nextString();
            if (s == null)
                return Format.NOT_SPECIFIED;

            if (s.equals(NOT_SPECIFIED))
                return Format.NOT_SPECIFIED;

            s = s.toUpperCase();

            if (s.equals(BIG_ENDIAN))
                return RGBFormat.BIG_ENDIAN;
            else if (s.equals(LITTLE_ENDIAN))
                return RGBFormat.LITTLE_ENDIAN;
            else
                throw new ParseException("Expected one of [" + BIG_ENDIAN + ","
                        + LITTLE_ENDIAN + "]: " + s, -1);
        }

        public int nextSigned() throws ParseException
        {
            String s = nextString();
            if (s == null)
                return Format.NOT_SPECIFIED;

            if (s.equals(NOT_SPECIFIED))
                return Format.NOT_SPECIFIED;

            s = s.toUpperCase();

            if (s.equals(UNSIGNED))
                return AudioFormat.UNSIGNED;
            else if (s.equals(SIGNED))
                return AudioFormat.SIGNED;
            else
                throw new ParseException("Expected one of [" + UNSIGNED + ","
                        + UNSIGNED + "]: " + s, -1);
        }

        public String nextString()
        {
            return nextString(null);
        }

        public String nextString(String defaultResult)
        {
            if (ix >= items.length)
                return defaultResult;

            final String result = items[ix];
            ++ix;
            return result;
        }

    }

    private static final char SEP = ':';
    public static final String BYTE_ARRAY = "B";
    public static final String SHORT_ARRAY = "S";
    public static final String INT_ARRAY = "I";

    public static final String NOT_SPECIFIED = "?";
    // audio format constants:
    public static final String BIG_ENDIAN = "B";
    public static final String LITTLE_ENDIAN = "L";
    public static final String SIGNED = "S";

    public static final String UNSIGNED = "U";
    private static final Map<String, String> formatEncodings = new HashMap<String, String>(); // corect
                                                                                              // case

    private static final Map<String, Class<?>> formatClasses = new HashMap<String, Class<?>>();

    static
    {
        buildFormatMap();
    }

    private static final void addAudioFormat(String s)
    {
        addFormat(s, AudioFormat.class);
    }

    private static final void addFormat(String s, Class<?> clazz)
    {
        formatClasses.put(s.toLowerCase(), clazz);
        formatEncodings.put(s.toLowerCase(), s);
    }

    private static final void addVideoFormat(String s)
    {
        addFormat(s, VideoFormat.class);
    }

    private static final void buildFormatMap()
    {
        addAudioFormat(AudioFormat.LINEAR);
        addAudioFormat(AudioFormat.ULAW); // = "ULAW";
        addAudioFormat(AudioFormat.ULAW_RTP); // = "ULAW/rtp";
        addAudioFormat(AudioFormat.ALAW); // = "alaw"; // strange that this is
                                          // lower case and ULAW is not...
        addAudioFormat(AudioFormat.IMA4); // = "ima4";
        addAudioFormat(AudioFormat.IMA4_MS); // = "ima4/ms";
        addAudioFormat(AudioFormat.MSADPCM); // = "msadpcm";
        addAudioFormat(AudioFormat.DVI); // = "dvi";
        addAudioFormat(AudioFormat.DVI_RTP); // = "dvi/rtp";
        addAudioFormat(AudioFormat.G723); // = "g723";
        addAudioFormat(AudioFormat.G723_RTP); // = "g723/rtp";
        addAudioFormat(AudioFormat.G728); // = "g728";
        addAudioFormat(AudioFormat.G728_RTP); // = "g728/rtp";
        addAudioFormat(AudioFormat.G729); // = "g729";
        addAudioFormat(AudioFormat.G729_RTP); // = "g729/rtp";
        addAudioFormat(AudioFormat.G729A); // = "g729a";
        addAudioFormat(AudioFormat.G729A_RTP); // = "g729a/rtp";
        addAudioFormat(AudioFormat.GSM); // = "gsm";
        addAudioFormat(AudioFormat.GSM_MS); // = "gsm/ms";
        addAudioFormat(AudioFormat.GSM_RTP); // = "gsm/rtp";
        addAudioFormat(AudioFormat.MAC3); // = "MAC3";
        addAudioFormat(AudioFormat.MAC6); // = "MAC6";
        addAudioFormat(AudioFormat.TRUESPEECH); // = "truespeech";
        addAudioFormat(AudioFormat.MSNAUDIO); // = "msnaudio";
        addAudioFormat(AudioFormat.MPEGLAYER3); // = "mpeglayer3";
        addAudioFormat(AudioFormat.VOXWAREAC8); // = "voxwareac8";
        addAudioFormat(AudioFormat.VOXWAREAC10); // = "voxwareac10";
        addAudioFormat(AudioFormat.VOXWAREAC16); // = "voxwareac16";
        addAudioFormat(AudioFormat.VOXWAREAC20); // = "voxwareac20";
        addAudioFormat(AudioFormat.VOXWAREMETAVOICE); // = "voxwaremetavoice";
        addAudioFormat(AudioFormat.VOXWAREMETASOUND); // = "voxwaremetasound";
        addAudioFormat(AudioFormat.VOXWARERT29H); // = "voxwarert29h";
        addAudioFormat(AudioFormat.VOXWAREVR12); // = "voxwarevr12";
        addAudioFormat(AudioFormat.VOXWAREVR18); // = "voxwarevr18";
        addAudioFormat(AudioFormat.VOXWARETQ40); // = "voxwaretq40";
        addAudioFormat(AudioFormat.VOXWARETQ60); // = "voxwaretq60";
        addAudioFormat(AudioFormat.MSRT24); // = "msrt24";
        addAudioFormat(AudioFormat.MPEG); // = "mpegaudio";
        addAudioFormat(AudioFormat.MPEG_RTP); // = "mpegaudio/rtp";
        addAudioFormat(AudioFormat.DOLBYAC3); // = "dolbyac3";

        for (String e : BonusAudioFormatEncodings.ALL)
            addAudioFormat(e);

        // TODO: MpegEncoding using
        // MpegEncoding.MPEG1L1,
        // MpegEncoding.MPEG1L2,
        // MpegEncoding.MPEG1L3,
        // MpegEncoding.MPEG2DOT5L1,
        // MpegEncoding.MPEG2DOT5L2,
        // MpegEncoding.MPEG2DOT5L3,
        // MpegEncoding.MPEG2L1,
        // MpegEncoding.MPEG2L2,
        // MpegEncoding.MPEG2L3,
        // TODO: VorbisEncoding using VorbisEncoding.VORBISENC

        // Video formats:

        addVideoFormat(VideoFormat.CINEPAK); // ="cvid";
        addFormat(VideoFormat.JPEG, JPEGFormat.class);
        addVideoFormat(VideoFormat.JPEG_RTP); // ="jpeg/rtp";
        addVideoFormat(VideoFormat.MPEG); // ="mpeg";
        addVideoFormat(VideoFormat.MPEG_RTP); // ="mpeg/rtp";
        addFormat(VideoFormat.H261, H261Format.class);
        addVideoFormat(VideoFormat.H261_RTP); // ="h261/rtp";
        addFormat(VideoFormat.H263, H263Format.class);
        addVideoFormat(VideoFormat.H263_RTP); // ="h263/rtp";
        addVideoFormat(VideoFormat.H263_1998_RTP); // ="h263-1998/rtp";
        addFormat(VideoFormat.RGB, RGBFormat.class);
        addFormat(VideoFormat.YUV, YUVFormat.class);
        addFormat(VideoFormat.IRGB, IndexedColorFormat.class);
        addVideoFormat(VideoFormat.SMC); // ="smc";
        addVideoFormat(VideoFormat.RLE); // ="rle";
        addVideoFormat(VideoFormat.RPZA); // ="rpza";
        addVideoFormat(VideoFormat.MJPG); // ="mjpg";
        addVideoFormat(VideoFormat.MJPEGA); // ="mjpa";
        addVideoFormat(VideoFormat.MJPEGB); // ="mjpb";
        addVideoFormat(VideoFormat.INDEO32); // ="iv32";
        addVideoFormat(VideoFormat.INDEO41); // ="iv41";
        addVideoFormat(VideoFormat.INDEO50); // ="iv50";

        // TODO: AviVideoFormat

        addFormat(BonusVideoFormatEncodings.GIF, GIFFormat.class);
        addFormat(BonusVideoFormatEncodings.PNG, PNGFormat.class);

    }

    private static final String dataTypeToStr(Class<?> clazz)
    {
        if (clazz == null)
        {
            return NOT_SPECIFIED;
        }
        if (clazz == Format.byteArray)
        {
            return BYTE_ARRAY;
        }
        if (clazz == Format.shortArray)
        {
            return SHORT_ARRAY;
        }
        if (clazz == Format.intArray)
        {
            return INT_ARRAY;
        }

        throw new IllegalArgumentException("" + clazz);

    }

    private static final String dimensionToStr(Dimension d)
    {
        if (d == null)
            return NOT_SPECIFIED;
        return ((int) d.getWidth()) + "x" + ((int) d.getHeight());
    }

    private static final String endianToStr(int endian)
    {
        if (endian == Format.NOT_SPECIFIED)
            return NOT_SPECIFIED;
        else if (endian == AudioFormat.BIG_ENDIAN)
            return BIG_ENDIAN;
        else if (endian == AudioFormat.LITTLE_ENDIAN)
            return LITTLE_ENDIAN;
        else
            throw new IllegalArgumentException("Unknown endianness: " + endian);

    }

    private static final String floatToStr(float v)
    {
        if (v == Format.NOT_SPECIFIED)
            return NOT_SPECIFIED;
        else
            return "" + v;
    }

    private static final String intToStr(int i)
    {
        if (i == Format.NOT_SPECIFIED)
            return NOT_SPECIFIED;
        else
            return "" + i;
    }

    public static Format parse(String s) throws ParseException
    {
        final String[] strings = s.split("" + SEP);
        final Tokens t = new Tokens(strings);

        int ix = 0;
        final String encodingIgnoreCase = t.nextString(null);

        if (encodingIgnoreCase == null)
            throw new ParseException("No encoding specified", 0);

        final Class<?> formatClass = formatClasses.get(encodingIgnoreCase
                .toLowerCase());
        if (formatClass == null)
            throw new ParseException("Unknown encoding: " + encodingIgnoreCase,
                    -1);

        final String encoding = formatEncodings.get(encodingIgnoreCase
                .toLowerCase());
        if (encoding == null)
            throw new ParseException("Unknown encoding: " + encodingIgnoreCase,
                    -1);

        if (AudioFormat.class.isAssignableFrom(formatClass))
        {
            final double sampleRate = t.nextDouble();
            final int sampleSizeInBits = t.nextInt();
            final int channels = t.nextInt();
            final int endian = t.nextEndian();
            final int signed = t.nextSigned();
            final int frameSizeInBits = t.nextInt();
            final double frameRate = t.nextDouble();
            Class<?> dataType = t.nextDataType();
            if (dataType == null)
                dataType = Format.byteArray; // default

            return new AudioFormat(encoding, sampleRate, sampleSizeInBits,
                    channels, endian, signed, frameSizeInBits, frameRate,
                    dataType);

        } else if (VideoFormat.class.isAssignableFrom(formatClass))
        {
            if (formatClass == JPEGFormat.class)
            {
                final java.awt.Dimension size = t.nextDimension();
                final int maxDataLength = t.nextInt();
                Class<?> dataType = t.nextDataType();
                if (dataType == null)
                    dataType = Format.byteArray; // default
                final float frameRate = t.nextFloat();
                final int q = Format.NOT_SPECIFIED; // TODO
                final int dec = Format.NOT_SPECIFIED; // TODO

                return new JPEGFormat(size, maxDataLength, dataType, frameRate,
                        q, dec);
            } else if (formatClass == GIFFormat.class)
            {
                final java.awt.Dimension size = t.nextDimension();
                final int maxDataLength = t.nextInt();
                Class<?> dataType = t.nextDataType();
                if (dataType == null)
                    dataType = Format.byteArray; // default
                final float frameRate = t.nextFloat();

                return new GIFFormat(size, maxDataLength, dataType, frameRate);
            } else if (formatClass == PNGFormat.class)
            {
                final java.awt.Dimension size = t.nextDimension();
                final int maxDataLength = t.nextInt();
                Class<?> dataType = t.nextDataType();
                if (dataType == null)
                    dataType = Format.byteArray; // default
                final float frameRate = t.nextFloat();

                return new PNGFormat(size, maxDataLength, dataType, frameRate);
            } else if (formatClass == VideoFormat.class)
            {
                final java.awt.Dimension size = t.nextDimension();
                final int maxDataLength = t.nextInt();
                Class<?> dataType = t.nextDataType();
                if (dataType == null)
                    dataType = Format.byteArray; // default
                final float frameRate = t.nextFloat();

                return new VideoFormat(encoding, size, maxDataLength, dataType,
                        frameRate);
            } else if (formatClass == RGBFormat.class)
            {
                final java.awt.Dimension size = t.nextDimension();
                final int maxDataLength = t.nextInt();
                Class<?> dataType = t.nextDataType();
                if (dataType == null)
                    dataType = Format.byteArray; // default
                final float frameRate = t.nextFloat();
                final int bitsPerPixel = t.nextInt();
                final int red = t.nextInt();
                final int green = t.nextInt();
                final int blue = t.nextInt();
                final int pixelStride = t.nextInt();
                final int lineStride = t.nextInt();
                final int flipped = t.nextInt();
                final int endian = t.nextRGBFormatEndian();

                if (pixelStride == -1 && lineStride == -1 && flipped == -1
                        && endian == -1)
                    return new RGBFormat(size, maxDataLength, dataType,
                            frameRate, bitsPerPixel, red, green, blue);

                return new RGBFormat(size, maxDataLength, dataType, frameRate,
                        bitsPerPixel, red, green, blue, pixelStride,
                        lineStride, flipped, endian);
            }

            // public RGBFormat(java.awt.Dimension size, int maxDataLength,
            // Class dataType, float frameRate, int bitsPerPixel,
            // int red, int green, int blue, int pixelStride, int lineStride,
            // int flipped, int endian)
            // TODO: others
            throw new RuntimeException("TODO: Unknown class: " + formatClass);
        } else
        {
            throw new RuntimeException("Unknown class: " + formatClass);
        }

    }

    private static final String rgbFormatEndianToStr(int endian)
    {
        if (endian == Format.NOT_SPECIFIED)
            return NOT_SPECIFIED;
        else if (endian == RGBFormat.BIG_ENDIAN)
            return BIG_ENDIAN;
        else if (endian == RGBFormat.LITTLE_ENDIAN)
            return LITTLE_ENDIAN;
        else
            throw new IllegalArgumentException("Unknown endianness: " + endian);

    }

    private static final String signedToStr(int signed)
    {
        if (signed == Format.NOT_SPECIFIED)
            return NOT_SPECIFIED;
        else if (signed == AudioFormat.SIGNED)
            return SIGNED;
        else if (signed == AudioFormat.UNSIGNED)
            return UNSIGNED;
        else
            throw new IllegalArgumentException("Unknown signedness: " + signed);

    }

    public static String toString(Format f)
    {
        final List<String> list = new ArrayList<String>();

        list.add(f.getEncoding().toUpperCase());

        if (f instanceof AudioFormat)
        {
            final AudioFormat af = (AudioFormat) f;
            list.add(intToStr((int) af.getSampleRate()));
            list.add(intToStr(af.getSampleSizeInBits()));

            list.add(intToStr(af.getChannels()));
            list.add(endianToStr(af.getEndian()));
            list.add(signedToStr(af.getSigned()));
            list.add(intToStr(af.getFrameSizeInBits()));
            list.add(intToStr((int) af.getFrameRate()));
            if (af.getDataType() != null
                    && af.getDataType() != Format.byteArray)
                list.add(dataTypeToStr(af.getDataType()));

        } else if (f instanceof VideoFormat)
        {
            final VideoFormat vf = (VideoFormat) f;
            if (f.getClass() == JPEGFormat.class)
            {
                final JPEGFormat jf = (JPEGFormat) vf;
                list.add(dimensionToStr(jf.getSize()));
                list.add(intToStr(jf.getMaxDataLength()));
                if (jf.getDataType() != null
                        && jf.getDataType() != Format.byteArray)
                    list.add(dataTypeToStr(jf.getDataType()));
                list.add(floatToStr(jf.getFrameRate()));
                // TODO: Q, decimation

            } else if (f.getClass() == GIFFormat.class)
            {
                final GIFFormat gf = (GIFFormat) vf;
                list.add(dimensionToStr(gf.getSize()));
                list.add(intToStr(gf.getMaxDataLength()));
                if (gf.getDataType() != null
                        && gf.getDataType() != Format.byteArray)
                    list.add(dataTypeToStr(gf.getDataType()));
                list.add(floatToStr(gf.getFrameRate()));

            } else if (f.getClass() == PNGFormat.class)
            {
                final PNGFormat pf = (PNGFormat) vf;
                list.add(dimensionToStr(pf.getSize()));
                list.add(intToStr(pf.getMaxDataLength()));
                if (pf.getDataType() != null
                        && pf.getDataType() != Format.byteArray)
                    list.add(dataTypeToStr(pf.getDataType()));
                list.add(floatToStr(pf.getFrameRate()));

            } else if (f.getClass() == VideoFormat.class)
            {
                list.add(dimensionToStr(vf.getSize()));
                list.add(intToStr(vf.getMaxDataLength()));
                if (vf.getDataType() != null
                        && vf.getDataType() != Format.byteArray)
                    list.add(dataTypeToStr(vf.getDataType()));
                list.add(floatToStr(vf.getFrameRate()));

            } else if (f.getClass() == RGBFormat.class)
            {
                final RGBFormat rf = (RGBFormat) vf;
                list.add(dimensionToStr(vf.getSize()));
                list.add(intToStr(vf.getMaxDataLength()));
                if (vf.getDataType() != null
                        && vf.getDataType() != Format.byteArray)
                    list.add(dataTypeToStr(vf.getDataType()));
                list.add(floatToStr(vf.getFrameRate()));
                list.add(intToStr(rf.getBitsPerPixel()));
                list.add(intToStr(rf.getRedMask())); // TODO: hex?
                list.add(intToStr(rf.getGreenMask()));
                list.add(intToStr(rf.getBlueMask()));
                list.add(intToStr(rf.getPixelStride()));
                list.add(intToStr(rf.getLineStride()));
                list.add(intToStr(rf.getFlipped())); // TODO: use a string code
                                                     // for this?
                list.add(rgbFormatEndianToStr(rf.getEndian()));

            } else
                throw new IllegalArgumentException(
                        "Unknown or unsupported format: " + f);
        } else
        {
            throw new IllegalArgumentException("" + f);
        }

        // remove any default values from the end.
        while (list.get(list.size() - 1) == null
                || list.get(list.size() - 1).equals(NOT_SPECIFIED))
            list.remove(list.size() - 1);

        final StringBuilder b = new StringBuilder();

        for (int i = 0; i < list.size(); ++i)
        {
            if (i > 0)
                b.append(SEP);
            b.append(list.get(i));
        }

        return b.toString();
    }
}
