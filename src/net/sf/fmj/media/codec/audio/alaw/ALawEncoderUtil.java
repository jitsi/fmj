package net.sf.fmj.media.codec.audio.alaw;

import com.lti.utils.*;

/**
 * Turns 16-bit linear PCM values into 8-bit A-law bytes. Adapted from code by
 * Marc Sweetgall at http://www.codeproject.com/csharp/g711audio.asp
 */
public class ALawEncoderUtil
{
    public static final int MAX = 0x7fff; // maximum that can be held in 15 bits

    /**
     * An array where the index is the 16-bit PCM input, and the value is the
     * a-law result.
     */
    private static byte[] pcmToALawMap;

    static
    {
        pcmToALawMap = new byte[65536];
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; i++)
            pcmToALawMap[UnsignedUtils.uShortToInt((short) i)] = encode(i);
    }

    /**
     * Encode an array of pcm values into a pre-allocated target array
     *
     * @param data
     *            An array of bytes in Little-Endian format
     * @param target
     *            A pre-allocated array to receive the A-law bytes. This array
     *            must be at least half the size of the source.
     */
    public static void aLawEncode(boolean bigEndian, byte[] data, int offset,
            int length, byte[] target)
    {
        if (bigEndian)
            aLawEncodeBigEndian(data, offset, length, target);
        else
            aLawEncodeLittleEndian(data, offset, length, target);
    }

    /**
     * Encode a pcm value into a a-law byte
     *
     * @param pcm
     *            A 16-bit pcm value
     * @return A a-law encoded byte
     */
    public static byte aLawEncode(int pcm)
    {
        return pcmToALawMap[UnsignedUtils.uShortToInt((short) (pcm & 0xffff))];
    }

    /**
     * Encode a pcm value into a a-law byte
     *
     * @param pcm
     *            A 16-bit pcm value
     * @return A a-law encoded byte
     */
    public static byte aLawEncode(short pcm)
    {
        return pcmToALawMap[UnsignedUtils.uShortToInt(pcm)];
    }

    // /**
    // * Encode an array of pcm values
    // *
    // * @param data An array of 16-bit pcm values
    // * @parem encoded An array to be filled with a-law bytes containing the
    // results. must be same size as data.
    // */
    // public static void aLawEncode(int[] data, byte[] encoded)
    // {
    // int size = data.length;
    // //byte[] encoded = new byte[size];
    // for (int i = 0; i < size; i++)
    // encoded[i] = aLawEncode(data[i]);
    // //return encoded;
    // }
    //
    // /**
    // * Encode an array of pcm values
    // *
    // * @param data An array of 16-bit pcm values
    // * @parem encoded An array to be filled with a-law bytes containing the
    // results. must be same size as data.
    // */
    // public static void aLawEncode(short[] data, byte[] encoded)
    // {
    // int size = data.length;
    // //byte[] encoded = new byte[size];
    // for (int i = 0; i < size; i++)
    // encoded[i] = aLawEncode(data[i]);
    // //return encoded;
    // }
    //
    // /**
    // * Encode an array of pcm values
    // *
    // * @param data An array of bytes in Little-Endian format
    // * @return An array of a-law bytes containing the results
    // */
    // public static byte[] aLawEncode(byte[] data)
    // {
    // int size = data.length / 2;
    // byte[] encoded = new byte[size];
    // for (int i = 0; i < size; i++)
    // encoded[i] = aLawEncode((data[2 * i + 1] << 8) | data[2 * i]);
    // return encoded;
    // }

    public static void aLawEncodeBigEndian(byte[] data, int offset, int length,
            byte[] target)
    {
        int size = length / 2;
        for (int i = 0; i < size; i++)
            target[i] = aLawEncode(((data[offset + 2 * i + 1]) & 0xff)
                    | ((data[offset + 2 * i] & 0xff) << 8));
    }

    public static void aLawEncodeLittleEndian(byte[] data, int offset,
            int length, byte[] target)
    {
        int size = length / 2;
        for (int i = 0; i < size; i++)
            target[i] = aLawEncode(((data[offset + 2 * i + 1] & 0xff) << 8)
                    | (data[offset + 2 * i]) & 0xff);
    }

    /**
     * Encode one a-law byte from a 16-bit signed integer. Internal use only.
     *
     * @param pcm
     *            A 16-bit signed pcm value
     * @return A a-law encoded byte
     */
    private static byte encode(int pcm)
    {
        // Get the sign bit. Shift it for later use without further modification
        int sign = (pcm & 0x8000) >> 8;
        // If the number is negative, make it positive (now it's a magnitude)
        if (sign != 0)
            pcm = -pcm;
        // The magnitude must fit in 15 bits to avoid overflow
        if (pcm > MAX)
            pcm = MAX;

        /*
         * Finding the "exponent" Bits: 1 2 3 4 5 6 7 8 9 A B C D E F G S 7 6 5
         * 4 3 2 1 0 0 0 0 0 0 0 0 We want to find where the first 1 after the
         * sign bit is. We take the corresponding value from the second row as
         * the exponent value. (i.e. if first 1 at position 7 -> exponent = 2)
         * The exponent is 0 if the 1 is not found in bits 2 through 8. This
         * means the exponent is 0 even if the "first 1" doesn't exist.
         */
        int exponent = 7;
        // Move to the right and decrement exponent until we hit the 1 or the
        // exponent hits 0
        for (int expMask = 0x4000; (pcm & expMask) == 0 && exponent > 0; exponent--, expMask >>= 1)
        {
        }

        /*
         * The last part - the "mantissa" We need to take the four bits after
         * the 1 we just found. To get it, we shift 0x0f : 1 2 3 4 5 6 7 8 9 A B
         * C D E F G S 0 0 0 0 0 1 . . . . . . . . . (say that exponent is 2) .
         * . . . . . . . . . . . 1 1 1 1 We shift it 5 times for an exponent of
         * two, meaning we will shift our four bits (exponent + 3) bits. For
         * convenience, we will actually just shift the number, then AND with
         * 0x0f.
         *
         * NOTE: If the exponent is 0: 1 2 3 4 5 6 7 8 9 A B C D E F G S 0 0 0 0
         * 0 0 0 Z Y X W V U T S (we know nothing about bit 9) . . . . . . . . .
         * . . . 1 1 1 1 We want to get ZYXW, which means a shift of 4 instead
         * of 3
         */
        int mantissa = (pcm >> ((exponent == 0) ? 4 : (exponent + 3))) & 0x0f;

        // The a-law byte bit arrangement is SEEEMMMM (Sign, Exponent, and
        // Mantissa.)
        byte alaw = (byte) (sign | exponent << 4 | mantissa);

        // Last is to flip every other bit, and the sign bit (0xD5 = 1101 0101)
        return (byte) (alaw ^ 0xD5);
    }
}
