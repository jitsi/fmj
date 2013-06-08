package net.sf.fmj.media.codec.audio.ulaw;

/**
 * Turns 8-bit mu-law bytes back into 16-bit PCM values. Adapted from code by
 * Marc Sweetgall at http://www.codeproject.com/csharp/g711audio.asp Note: in
 * .Net, a byte is unsigned, hence the need to always do byte & 0xff - this
 * converts an unsigned byte to the corresponding (nonnegative) int value.
 */
public class MuLawDecoderUtil
{
    /**
     * An array where the index is the mu-law input, and the value is the 16-bit
     * PCM result.
     *
     */
    private static short[] muLawToPcmMap;

    static
    {
        muLawToPcmMap = new short[256];
        for (short i = 0; i < muLawToPcmMap.length; i++)
            muLawToPcmMap[i] = decode((byte) i);
    }

    /**
     * Decode one mu-law byte. For internal use only.
     *
     * @param mulaw
     *            The encoded mu-law byte
     * @return A short containing the 16-bit result
     */
    private static short decode(byte mulaw)
    {
        // Flip all the bits
        mulaw = (byte) ~mulaw;

        // Pull out the value of the sign bit
        int sign = mulaw & 0x80;
        // Pull out and shift over the value of the exponent
        int exponent = (mulaw & 0x70) >> 4;
        // Pull out the four bits of data
        int data = mulaw & 0x0f;

        // Add on the implicit fifth bit (we know the four data bits followed a
        // one bit)
        data |= 0x10;
        /*
         * Add a 1 to the end of the data by shifting over and adding one. Why?
         * Mu-law is not a one-to-one function. There is a range of values that
         * all map to the same mu-law byte. Adding a one to the end essentially
         * adds a "half byte", which means that the decoding will return the
         * value in the middle of that range. Otherwise, the mu-law decoding
         * would always be less than the original data.
         */
        data <<= 1;
        data += 1;
        /*
         * Shift the five bits to where they need to be: left (exponent + 2)
         * places Why (exponent + 2) ? 1 2 3 4 5 6 7 8 9 A B C D E F G . 7 6 5 4
         * 3 2 1 0 . . . . . . . <-- starting bit (based on exponent) . . . . .
         * . . . . . 1 x x x x 1 <-- our data We need to move the one under the
         * value of the exponent, which means it must move (exponent + 2) times
         */
        data <<= exponent + 2;
        // Remember, we added to the original, so we need to subtract from the
        // final
        data -= MuLawEncoderUtil.BIAS;
        // If the sign bit is 0, the number is positive. Otherwise, negative.
        return (short) (sign == 0 ? data : -data);
    }

    public static void muLawDecode(boolean bigEndian, byte[] data, int offset,
            int len, byte[] decoded)
    {
        if (bigEndian)
            muLawDecodeBigEndian(data, offset, len, decoded);
        else
            muLawDecodeLittleEndian(data, offset, len, decoded);
    }

    // /**
    // * Decode an array of mu-law encoded bytes
    // *
    // * @param data An array of mu-law encoded bytes
    // * @return An array of shorts containing the results
    // */
    // public static void muLawDecode(byte[] data, short[] decoded)
    // {
    // int size = data.length;
    // //short[] decoded = new short[size];
    // for (int i = 0; i < size; i++)
    // decoded[i] = muLawToPcmMap[data[i]];
    // //return decoded;
    // }

    /**
     * Decode one mu-law byte
     *
     * @param mulaw
     *            The encoded mu-law byte
     * @return A short containing the 16-bit result
     */
    public static short muLawDecode(byte mulaw)
    {
        return muLawToPcmMap[mulaw & 0xff];
    }

    public static void muLawDecodeBigEndian(byte[] data, int offset, int len,
            byte[] decoded)
    {
        int size = len;
        // byte[] decoded = new byte[size * 2];
        for (int i = 0; i < size; i++)
        {
            // Second byte is the less significant byte
            decoded[2 * i + 1] = (byte) (muLawToPcmMap[data[offset + i] & 0xff] & 0xff);
            // First byte is the more significant byte
            decoded[2 * i] = (byte) ((muLawToPcmMap[data[offset + i] & 0xff] >> 8) & 0xff);
        }
        // return decoded;
    }

    /**
     * Decode an array of mu-law encoded bytes
     *
     * @param data
     *            An array of mu-law encoded bytes
     * @param decoded
     *            An array of bytes in Little-Endian format containing the
     *            results, must be twice as big as data.
     */
    public static void muLawDecodeLittleEndian(byte[] data, int offset,
            int len, byte[] decoded)
    {
        int size = len;
        // byte[] decoded = new byte[size * 2];
        for (int i = 0; i < size; i++)
        {
            // First byte is the less significant byte
            decoded[2 * i] = (byte) (muLawToPcmMap[data[offset + i] & 0xff] & 0xff);
            // Second byte is the more significant byte
            decoded[2 * i + 1] = (byte) ((muLawToPcmMap[data[offset + i] & 0xff] >> 8) & 0xff);
        }
        // return decoded;
    }
}
