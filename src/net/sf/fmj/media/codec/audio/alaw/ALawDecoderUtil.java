package net.sf.fmj.media.codec.audio.alaw;

/**
 * Turns 8-bit A-law bytes back into 16-bit PCM values.
 *
 * Adapted from code by Marc Sweetgall at
 * http://www.codeproject.com/csharp/g711audio.asp
 */
public class ALawDecoderUtil
{
    /**
     * An array where the index is the a-law input, and the value is the 16-bit
     * PCM result.
     *
     */
    private static short[] aLawToPcmMap;

    static
    {
        aLawToPcmMap = new short[256];
        for (int i = 0; i < aLawToPcmMap.length; i++)
            aLawToPcmMap[i] = decode((byte) i);
    }

    public static void aLawDecode(boolean bigEndian, byte[] data, int offset,
            int length, byte[] decoded)
    {
        if (bigEndian)
            aLawDecodeBigEndian(data, offset, length, decoded);
        else
            aLawDecodeLittleEndian(data, offset, length, decoded);
    }

    /**
     * Decode one a-law byte
     *
     * @param alaw
     *            The encoded a-law byte
     * @return A short containing the 16-bit result
     */
    public static short aLawDecode(byte alaw)
    {
        return aLawToPcmMap[alaw & 0xff];
    }

    // /**
    // * Decode an array of a-law encoded bytes
    // *
    // * @param data An array of a-law encoded bytes
    // * @return An array of shorts containing the results
    // */
    // public static short[] aLawDecode(byte[] data)
    // {
    // int size = data.length;
    // short[] decoded = new short[size];
    // for (int i = 0; i < size; i++)
    // decoded[i] = aLawToPcmMap[data[i]];
    // return decoded;
    // }
    //
    // /**
    // * Decode an array of a-law encoded bytes
    // *
    // * @param data An array of a-law encoded bytes
    // * @param decoded An array of shorts containing the results
    // * Same as the other method that returns an array of shorts
    // */
    // public static void aLawDecode(byte[] data, short[] decoded)
    // {
    // int size = data.length;
    // for (int i = 0; i < size; i++)
    // decoded[i] = aLawToPcmMap[data[i] & 0xff];
    // }

    public static void aLawDecodeBigEndian(byte[] data, int offset, int length,
            byte[] decoded)
    {
        int size = length;
        for (int i = 0; i < size; i++)
        {
            decoded[2 * i + 1] = (byte) (aLawToPcmMap[data[offset + i] & 0xff] & 0xff);
            decoded[2 * i] = (byte) (aLawToPcmMap[data[offset + i] & 0xff] >> 8);
        }
    }

    /**
     * Decode an array of a-law encoded bytes
     *
     * @param data
     *            An array of a-law encoded bytes
     * @param decoded
     *            An array of bytes in Little-Endian format containing the
     *            results. Should be twice as big as data.
     */
    public static void aLawDecodeLittleEndian(byte[] data, int offset,
            int length, byte[] decoded)
    {
        int size = length;
        for (int i = 0; i < size; i++)
        {
            // First byte is the less significant byte
            decoded[2 * i] = (byte) (aLawToPcmMap[data[offset + i] & 0xff] & 0xff);
            // Second byte is the more significant byte
            decoded[2 * i + 1] = (byte) (aLawToPcmMap[data[offset + i] & 0xff] >> 8);
        }
    }

    /**
     * Decode one a-law byte. For internal use only.
     *
     * @param alaw
     *            The encoded a-law byte
     * @return A short containing the 16-bit result
     */
    private static short decode(byte alaw)
    {
        // Invert every other bit, and the sign bit (0xD5 = 1101 0101)
        alaw ^= 0xD5;

        // Pull out the value of the sign bit
        int sign = alaw & 0x80;
        // Pull out and shift over the value of the exponent
        int exponent = (alaw & 0x70) >> 4;
        // Pull out the four bits of data
        int data = alaw & 0x0f;

        // Shift the data four bits to the left
        data <<= 4;
        // Add 8 to put the result in the middle of the range (like adding a
        // half)
        data += 8;

        // If the exponent is not 0, then we know the four bits followed a 1,
        // and can thus add this implicit 1 with 0x100.
        if (exponent != 0)
            data += 0x100;
        /*
         * Shift the bits to where they need to be: left (exponent - 1) places
         * Why (exponent - 1) ? 1 2 3 4 5 6 7 8 9 A B C D E F G . 7 6 5 4 3 2 1
         * . . . . . . . . <-- starting bit (based on exponent) . . . . . . . Z
         * x x x x 1 0 0 0 <-- our data (Z is 0 only when exponent is 0) We need
         * to move the one under the value of the exponent, which means it must
         * move (exponent - 1) times It also means shifting is unnecessary if
         * exponent is 0 or 1.
         */
        if (exponent > 1)
            data <<= (exponent - 1);

        return (short) (sign == 0 ? data : -data);
    }
}