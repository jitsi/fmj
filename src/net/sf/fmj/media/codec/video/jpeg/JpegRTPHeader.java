package net.sf.fmj.media.codec.video.jpeg;

import net.sf.fmj.utility.*;

import com.lti.utils.*;

/**
 * A JPEG/RTP header. A special header is added to each packet that immediately
 * follows the RTP header:
 *
 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | Type
 * specific | Fragment Offset |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | Type | Q
 * | Width | Height |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * @author Ken Larson
 * @author Martin Harvan
 */
public class JpegRTPHeader
{
    /**
     * In bytes.
     */
    public static final int HEADER_SIZE = 8;

    /**
     * Creates header containing Quantization tables (used when Q is set to
     * values greater than 127).
     *
     * @param length
     *            length for the headers (usually 128 - two 64 bytes long
     *            tables)
     * @param lqt
     *            Luma Quantization table
     * @param cqt
     *            Chroma Quantization table
     * @return Qtable header
     */
    public static byte[] createQHeader(int length, int[] lqt, int[] cqt)
    {
        int qHeaderLength = 4 + length; // length of the header depends on
                                        // wheter we also send the headers or
                                        // not (RFC2435 chapter 4.2) length of
                                        // it is 16 bytes + length for the
                                        // tables
        byte[] data = new byte[qHeaderLength];
        int i = 0;
        byte mbz = 0; // TODO: find out what this is, according to RFC2435 a
                      // qtable header contains this and is set to 0
        byte precision = 0; // using 8-bit tables only

        data[i++] = mbz;
        data[i++] = precision;
        data[i++] = (byte) ((length >> 8) & 0xFF);
        data[i++] = (byte) length;

        if (length != 0)
        {
            int[] zzLqt = RFC2035.createZigZag(lqt);
            int[] zzCqt = RFC2035.createZigZag(cqt);
            System.arraycopy(ArrayUtility.intArrayToByteArray(zzLqt), 0, data,
                    i, lqt.length);
            i += lqt.length;
            System.arraycopy(ArrayUtility.intArrayToByteArray(zzCqt), 0, data,
                    i, cqt.length);
            i += cqt.length;
        }
        return data;
    }

    /**
     * Creates RST header for JPEG/RTP packet.
     *
     * @param dri
     *            Restart interval - number of MCUs between restart markers
     * @param f
     *            first bit (should be set to 1)
     * @param l
     *            last bit (should be set to 1)
     * @param count
     *            number of restart markers (should be set to 3FFF)
     * @return RST header
     */
    public static byte[] createRstHeader(int dri, int f, int l, int count)
    {
        // 0 1 2 3
        // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        // | Restart Interval |F|L| Restart Count |
        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

        byte[] data = new byte[4];
        int i = 0;
        data[i++] = (byte) ((dri >> 8) & 0xFF);
        data[i++] = (byte) dri;
        data[i] = (byte) ((f & 1) << 7);
        data[i] |= (byte) ((l & 1) << 6);
        data[i++] |= (byte) (count >> 8 & 0xFF) & 0x3F;
        data[i] = (byte) count;
        return data;
    }

    private static void encode3ByteIntBE(int value, byte[] ba, int offset)
    {
        int length = 3;

        for (int i = 0; i < length; ++i)
        {
            int byteValue = value & MAX_BYTE;
            if (byteValue > MAX_SIGNED_BYTE)
                byteValue = byteValue - MAX_BYTE_PLUS1;

            ba[offset + (length - i - 1)] = (byte) byteValue;

            value = value >> BITS_PER_BYTE;
        }
    }

    private final byte typeSpecific;
    private final int fragmentOffset;
    private final byte type;

    private final byte q;

    private final byte width;

    private final byte height;

    private static final int BITS_PER_BYTE = 8;

    private static final int MAX_SIGNED_BYTE = 127;
    private static final int MAX_BYTE = 0xFF;
    private static final int MAX_BYTE_PLUS1 = 256;

    public static JpegRTPHeader parse(byte[] data, int offset)
    {
        int i = offset;
        final byte typeSpecific = data[i++];
        int fragmentOffset = 0;
        for (int j = 0; j < 3; ++j)
        { // big-endian.
            fragmentOffset <<= 8;
            fragmentOffset += data[i++] & 0xff;
        }
        final byte type = data[i++];
        final byte q = data[i++];
        final byte width = data[i++];
        final byte height = data[i++];
        return new JpegRTPHeader(typeSpecific, fragmentOffset, type, q, width,
                height);

    }

    public JpegRTPHeader(final byte typeSpecific, final int fragmentOffset,
            final byte type, final byte q, final byte width, final byte height)
    {
        super();
        this.typeSpecific = typeSpecific;
        this.fragmentOffset = fragmentOffset;
        this.type = type;
        this.q = q;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof JpegRTPHeader))
            return false;
        final JpegRTPHeader oCast = (JpegRTPHeader) o;
        return this.typeSpecific == oCast.typeSpecific
                && this.fragmentOffset == oCast.fragmentOffset
                && this.type == oCast.type && this.q == oCast.q
                && this.width == oCast.width && this.height == oCast.height;

    }

    public int getFragmentOffset()
    {
        return fragmentOffset;
    }

    public int getHeightInBlocks()
    {
        return UnsignedUtils.uByteToInt(height);
    }

    public int getHeightInPixels()
    {
        return UnsignedUtils.uByteToInt(height) * 8;
    }

    public int getQ()
    {
        return UnsignedUtils.uByteToInt(q);
    }

    public int getType()
    {
        return UnsignedUtils.uByteToInt(type);
    }

    public int getTypeSpecific()
    {
        return UnsignedUtils.uByteToInt(typeSpecific);
    }

    public int getWidthInBlocks()
    {
        return UnsignedUtils.uByteToInt(width);
    }

    public int getWidthInPixels()
    {
        return UnsignedUtils.uByteToInt(width) * 8;
    }

    @Override
    public int hashCode()
    {
        return typeSpecific + fragmentOffset + type + q + width + height;
    }

    public byte[] toBytes()
    {
        byte[] data = new byte[HEADER_SIZE];
        int i = 0;
        data[i++] = typeSpecific;

        encode3ByteIntBE(fragmentOffset, data, i);
        i += 3;

        data[i++] = type;
        data[i++] = q;
        data[i++] = width;
        data[i++] = height;

        return data;

    }

    @Override
    public String toString()
    {
        return "typeSpecific=" + getTypeSpecific() + " fragmentOffset="
                + getFragmentOffset() + " type=" + getType() + " q=" + getQ()
                + " w=" + getWidthInPixels() + " h=" + getHeightInPixels();
    }
}
