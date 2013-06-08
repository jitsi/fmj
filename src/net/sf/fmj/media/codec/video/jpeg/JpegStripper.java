package net.sf.fmj.media.codec.video.jpeg;

import net.sf.fmj.utility.*;

/**
 * JPEG header stripper. Useful for getting rid of the headers that are present
 * in the Jpeg data and are unnecessary (and unwanted) for JPEG/RTP.
 *
 * @author Martin Harvan
 */
public class JpegStripper
{
    private static boolean STRIP = false;

    static void dump(int[] s)
    {
        dump(s, 10);
    }

    static void dump(int[] s, int length)
    {
        for (int i = 0; i < s.length / length + 1; i++)
        {
            for (int j = i * length; j < (i * length + length) && j < s.length; j++)
            {
                String tmp = Integer.toHexString(s[j]);
                if (tmp.length() < 2)
                {
                    tmp = 0 + tmp;
                }
                System.out.print(tmp + " ");
            }

            System.out.println("");
        }

        System.out.println("Length: " + s.length);
    }

    private static int findNextMarker(int[] output)
    {
        for (int i = 0; i < output.length; i++)
        {
            if (output[i] == 0xFF
                    && (output[i + 1] != 0 && (output[i + 1] < 0xD0 || output[i + 1] > 0xD7)))
            {
                STRIP = true;
                return i;
            }
        }
        STRIP = false;
        return output.length;
    }

    /**
     * Removes most of the leading and trailing headers from Jpeg data (byte
     * array). JpegStripper does not check if data represent valid Jpeg file.
     *
     * @param ba
     *            Byte array representing Jpeg file
     * @return Jpeg data with stripped leading and trailing headers
     */
    public static byte[] removeHeaders(byte[] ba)
    {
        int ia[] = new int[ba.length];
        for (int i = 0; i < ba.length; i++)
        {
            ia[i] = ba[i] & 255;
        }
        ia = removeHeaders(ia);
        ba = new byte[ia.length];
        for (int i = 0; i < ia.length; i++)
        {
            ba[i] = (byte) ia[i];
        }
        return ba;

    }

    public static int[] removeHeaders(int[] input)
    {
        input = stripLeadingHeaders(input);
        input = stripTrailingHeaders(input);
        return input;
    }

    private static int[] stripHeader(int[] s)
    {
        return ArrayUtility.copyOfRange(s, 2, s.length);
    }

    private static int[] stripHeaderContent(int[] s)
    {
        s = stripHeader(s);
        int begin = s[0] * 256 + s[1];
        return ArrayUtility.copyOfRange(s, begin, s.length);
    }

    private static int[] stripLeadingHeaders(int[] input)
    {
        int[] output = input;

        if (input[0] == 0xFF)
        {
            switch (input[1])
            { // strip following markers: (to add new it is usually enough to
              // add the second byte of the marker header
              // and it will strip it - provided that it has the usual format
              // with length stored in following 2 bytes)
            case 0xE0: // JFIF
            case 0xDB: // Quantization tables
            case 0xC4: // Huffmann tables
            case 0xC0: // Start of Frame
            case 0xDA: // Start of Scan
            case 0xDD: // Reset header
                output = stripHeaderContent(output);
                output = stripLeadingHeaders(output);
                break;
            default:
                break;
            }
            if (input[1] == 0xD8)
            {
                output = stripLeadingHeaders(stripHeader(output));
            }
        }

        return output;
    }

    private static int[] stripOtherMarkers(int[] output)
    {
        int i = findNextMarker(output);
        int[] array = ArrayUtility.copyOfRange(output, 0, i);
        return array;

    }

    private static int[] stripTrailingHeaders(int[] output)
    {
        output = stripOtherMarkers(output);
        if (STRIP)
            output = stripTrailingHeaders(output);
        return output;

    }
}
