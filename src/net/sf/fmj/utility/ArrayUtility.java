package net.sf.fmj.utility;

/**
 * Class used to convert array types (int[] -> bytep[], bytep[] -> int[] etc.).
 * Methods in this class does not check if there is any data loss occuring, they
 * simply cast all the elements into output type.
 *
 * @author Martin Harvan
 */
public class ArrayUtility
{
    public static int[] byteArrayToIntArray(byte[] b)
    {
        int[] result = new int[b.length];
        for (int i = 0; i < b.length; i++)
        {
            result[i] = b[i] & 0xFF;
        }
        return result;
    }

    public static byte[] copyOfRange(byte[] inputData, int from, int to)
    {
        return intArrayToByteArray(copyOfRange(byteArrayToIntArray(inputData),
                from, to));
    }

    public static int[] copyOfRange(int[] inputData, int from, int to)
    {
        if (inputData.length <= from || from < 0)
            throw new ArrayIndexOutOfBoundsException(from);
        if (to > inputData.length)
            throw new ArrayIndexOutOfBoundsException(to);
        if (to < from)
            throw new IllegalArgumentException();

        int[] output = new int[to - from];
        int j = 0;
        for (int i = from; i < to; i++)
        {
            output[j++] = inputData[i];
        }
        return output;
    }

    public static byte[] intArrayToByteArray(int[] b)
    {
        byte[] result = new byte[b.length];
        for (int i = 0; i < b.length; i++)
        {
            result[i] = (byte) b[i];
        }
        return result;
    }

    public static byte[] shortArrayToByteArray(short[] b)
    {
        byte[] result = new byte[b.length];
        for (int i = 0; i < b.length; i++)
        {
            result[i] = (byte) b[i];
        }
        return result;
    }
}
