package net.sf.fmj.codegen;

/**
 * Generic code-generation utilities, useful for creating unit tests.
 *
 * @author Ken Larson
 *
 */
public class CGUtils
{
    private static final int MAX_BYTE_PLUS1 = 256;

    private static final int RADIX_16 = 16;

    public static String byteArrayToHexString(byte[] array)
    {
        return byteArrayToHexString(array, array.length);
    }

    public static String byteArrayToHexString(byte[] array, int len)
    {
        return byteArrayToHexString(array, len, 0);

    }

    public static String byteArrayToHexString(byte[] array, int len, int offset)
    {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < len; ++i)
        {
            String byteStr = Integer.toHexString(uByteToInt(array[offset + i]));
            if (byteStr.length() == 1)
                byteStr = "0" + byteStr;
            b.append(byteStr);
        }
        return b.toString();
    }

    /**
     * Dump to string using a debugger-like format - both hex and ascii.
     */
    public static String dump(byte[] bytes, int offset, int byteslen)
    {
        final StringBuffer b = new StringBuffer();

        final int width = 32;
        int len = width;
        while (offset < byteslen)
        {
            int remainder = 0;

            if (offset + len > byteslen)
            {
                len = byteslen - offset;
                remainder = width - len;
            }
            b.append(byteArrayToHexString(bytes, len, offset));
            for (int i = 0; i < remainder; ++i)
            {
                b.append("  ");
            }
            b.append(" | ");
            for (int i = 0; i < len; ++i)
            {
                byte c = bytes[offset + i];
                if (c >= ' ' && c <= '~')
                {
                    b.append((char) c);
                } else
                    b.append('.');
            }

            b.append('\n');

            offset += len;

        }
        return b.toString();
    }

    public static byte hexStringToByte(String s)
    {
        return (byte) Integer.parseInt(s, RADIX_16);
    }

    public static byte[] hexStringToByteArray(String s)
    {
        byte[] array = new byte[s.length() / 2];
        for (int i = 0; i < array.length; ++i)
        {
            array[i] = hexStringToByte(s.substring(i * 2, i * 2 + 2));
        }
        return array;
    }

    public static String replaceSpecialJavaStringChars(String raw)
    {
        // TODO: non-printable characters.
        if (raw == null)
        {
            return null;
        }

        final StringBuffer buf = new StringBuffer();

        for (int i = 0; i < raw.length(); ++i)
        {
            char c = raw.charAt(i);

            if (c == '\"')
                buf.append("\\\"");
            else if (c == '\'')
                buf.append("\\\'");
            else if (c == '\\')
                buf.append("\\\\");
            else if (c == '\r')
                buf.append("\\r");
            else if (c == '\n')
                buf.append("\\n");
            else if (c == '\t')
                buf.append("\\t");
            else if (c == '\f')
                buf.append("\\f");
            else if (c == '\b')
                buf.append("\\b");
            else if (c == (char) 0)
                buf.append("\\000");
            else
            {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    public static String toHexLiteral(int v)
    {
        return "0x" + Integer.toHexString(v);
    }

    public static String toLiteral(byte[] ba)
    {
        if (ba == null)
            return "null";
        else
        {
            StringBuffer buf = new StringBuffer();
            buf.append("new byte[] {");
            for (int i = 0; i < ba.length; ++i)
            {
                if (i > 0)
                    buf.append(", ");
                buf.append("(byte)" + (int) ba[i]);

            }
            buf.append("}");
            return buf.toString();
        }

    }

    public static String toLiteral(double v)
    {
        return "" + v; // TODO
    }

    public static String toLiteral(float v)
    {
        return "" + v + "f";
    }

    public static String toLiteral(int v)
    {
        return "" + v;
    }

    public static String toLiteral(int[] ba)
    {
        if (ba == null)
            return "null";
        else
        {
            StringBuffer buf = new StringBuffer();
            buf.append("new int[] {");
            boolean first = true;
            for (int i = 0; i < ba.length; ++i)
            {
                final int b = ba[i];
                if (first)
                    first = false;
                else
                    buf.append(",");
                buf.append("" + b);
            }
            buf.append("}");
            return buf.toString();
        }

    }

    public static String toLiteral(long v)
    {
        return "" + v + "L";
    }

    public static String toLiteral(String s)
    {
        if (s == null)
            return "null";
        return "\"" + replaceSpecialJavaStringChars(s) + "\"";
    }

    static String toName(Class<?> c)
    {
        if (c == int.class)
            return "int";
        else if (c == boolean.class)
            return "boolean";
        else if (c == short.class)
            return "short";
        else if (c == byte.class)
            return "byte";
        else if (c == char.class)
            return "char";
        else if (c == float.class)
            return "float";
        else if (c == double.class)
            return "double";
        else if (c == long.class)
            return "long";
        else if (c == byte[].class)
            return "byte[]";
        else if (c == int[].class)
            return "int[]";
        else if (c == short[].class)
            return "short[]";
        else if (c == double[].class)
            return "double[]";
        else if (c == float[].class)
            return "float[]";
        else if (c == long[].class)
            return "long[]";
        else if (c == boolean[].class)
            return "boolean[]";
        else if (c == char[].class)
            return "char[]";
        else if (c.isArray())
            return "" + toName(c.getComponentType()) + "[]";
        else
            return "" + c.getName() + "";
    }

    public static String toNameDotClass(Class<?> c)
    {
        if (c == null)
            return null;
        return toName(c) + ".class";
    }

    // because java does not support unsigned bytes, we can use this function to
    // treat a byte as unsigned.
    // TODO: duplicated in UnsignedUtils
    public static int uByteToInt(byte b)
    {
        if (b >= 0)
            return b;
        else
            return MAX_BYTE_PLUS1 + b;
    }
}
