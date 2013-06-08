package net.sf.fmj.media.codec.audio.ulaw;

import com.lti.utils.*;

/**
 * Turns 16-bit linear PCM values into 8-bit mu-law bytes. Adapted from code by
 * Marc Sweetgall at http://www.codeproject.com/csharp/g711audio.asp
 */
public class MuLawEncoderUtil
{
    public static final int BIAS = 0x84; // aka 132, or 1000 0100
    public static final int MAX = 32635; // 32767 (max 15-bit integer) minus
                                         // BIAS

    /**
     * An array where the index is the 16-bit PCM input, and the value is the
     * mu-law result.
     */
    private static byte[] pcmToMuLawMap;
    static
    {
        pcmToMuLawMap = new byte[65536];
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; i++)
            pcmToMuLawMap[UnsignedUtils.uShortToInt((short) i)] = encode(i);
    }

    /**
     * Encode one mu-law byte from a 16-bit signed integer. Internal use only.
     *
     * @param pcm
     *            A 16-bit signed pcm value
     * @return A mu-law encoded byte
     */
    private static byte encode(int pcm)
    {
        // Get the sign bit. Shift it for later use without further modification
        int sign = (pcm & 0x8000) >> 8;
        // If the number is negative, make it positive (now it's a magnitude)
        if (sign != 0)
            pcm = -pcm;
        // The magnitude must be less than 32635 to avoid overflow
        if (pcm > MAX)
            pcm = MAX;
        // Add 132 to guarantee a 1 in the eight bits after the sign bit
        pcm += BIAS;

        /*
         * Finding the "exponent" Bits: 1 2 3 4 5 6 7 8 9 A B C D E F G S 7 6 5
         * 4 3 2 1 0 . . . . . . . We want to find where the first 1 after the
         * sign bit is. We take the corresponding value from the second row as
         * the exponent value. (i.e. if first 1 at position 7 -> exponent = 2)
         */
        int exponent = 7;
        // Move to the right and decrement exponent until we hit the 1
        for (int expMask = 0x4000; (pcm & expMask) == 0; exponent--, expMask >>= 1)
        {
        }

        /*
         * The last part - the "mantissa" We need to take the four bits after
         * the 1 we just found. To get it, we shift 0x0f : 1 2 3 4 5 6 7 8 9 A B
         * C D E F G S 0 0 0 0 0 1 . . . . . . . . . (meaning exponent is 2) . .
         * . . . . . . . . . . 1 1 1 1 We shift it 5 times for an exponent of
         * two, meaning we will shift our four bits (exponent + 3) bits. For
         * convenience, we will actually just shift the number, then and with
         * 0x0f.
         */
        int mantissa = (pcm >> (exponent + 3)) & 0x0f;

        // The mu-law byte bit arrangement is SEEEMMMM (Sign, Exponent, and
        // Mantissa.)
        byte mulaw = (byte) (sign | exponent << 4 | mantissa);

        // Last is to flip the bits
        return (byte) ~mulaw;
    }

    public static void muLawEncode(boolean bigEndian, byte[] data, int offset,
            int len, byte[] target)
    {
        if (bigEndian)
            muLawEncodeBigEndian(data, offset, len, target);
        else
            muLawEncodeLittleEndian(data, offset, len, target);

    }

    /**
     * Encode a pcm value into a mu-law byte
     *
     * @param pcm
     *            A 16-bit pcm value
     * @return A mu-law encoded byte
     */
    public static byte muLawEncode(int pcm)
    {
        return pcmToMuLawMap[pcm & 0xffff];
    }

    /**
     * Encode a pcm value into a mu-law byte
     *
     * @param pcm
     *            A 16-bit pcm value
     * @return A mu-law encoded byte
     */
    public static byte muLawEncode(short pcm)
    {
        return pcmToMuLawMap[UnsignedUtils.uShortToInt(pcm)];
    }

    public static void muLawEncodeBigEndian(byte[] data, int offset, int len,
            byte[] target)
    {
        final int size = len / 2;
        for (int i = 0; i < size; i++)
            target[i] = muLawEncode(((data[offset + 2 * i + 1]) & 0xff)
                    | ((data[offset + 2 * i] & 0xff) << 8));
    }

    // /**
    // * Encode an array of pcm values
    // *
    // * @param data An array of 16-bit pcm values
    // * @return An array of mu-law bytes containing the results
    // */
    // public static void muLawEncode(int[] data, byte[] encoded)
    // {
    // int size = data.length;
    // // byte[] encoded = new byte[size];
    // for (int i = 0; i < size; i++)
    // encoded[i] = muLawEncode(data[i]);
    // //return encoded;
    // }
    //
    // /**
    // * Encode an array of pcm values
    // *
    // * @param data`An array of 16-bit pcm values
    // * @return An array of mu-law bytes containing the results
    // */
    // public static void muLawEncode(short[] data, byte[] encoded)
    // {
    // int size = data.length;
    // //byte[] encoded = new byte[size];
    // for (int i = 0; i < size; i++)
    // encoded[i] = muLawEncode(data[i]);
    // //return encoded;
    // }
    //
    // /**
    // * Encode an array of pcm values
    // *
    // * @param data An array of bytes in Little-Endian format
    // * @return An array of mu-law bytes containing the results
    // */
    // public static byte[] muLawEncode(byte[] data)
    // {
    // int size = data.length / 2;
    // byte[] encoded = new byte[size];
    // for (int i = 0; i < size; i++)
    // encoded[i] = muLawEncode((data[2 * i + 1] << 8) | data[2 * i]);
    // return encoded;
    // }

    /**
     * Encode an array of pcm values into a pre-allocated target array
     *
     * @param data
     *            An array of bytes in Little-Endian format
     * @param target
     *            A pre-allocated array to receive the mu-law bytes. This array
     *            must be at least half the size of the source.
     */
    public static void muLawEncodeLittleEndian(byte[] data, int offset,
            int len, byte[] target)
    {
        final int size = len / 2;
        for (int i = 0; i < size; i++)
            target[i] = muLawEncode(((data[offset + 2 * i + 1] & 0xff) << 8)
                    | (data[offset + 2 * i] & 0xff));
    }

    /**
     * Sets whether or not all-zero bytes are encoded as 2 instead. The pcm
     * values this concerns are in the range [32768,33924] (unsigned).
     */
    public boolean getZeroTrap()
    {
        return (pcmToMuLawMap[33000] != 0);
    }

    public void setZeroTrap(boolean value)
    {
        byte val = (byte) (value ? 2 : 0);
        for (int i = 32768; i <= 33924; i++)
            pcmToMuLawMap[i] = val;
    }

}
