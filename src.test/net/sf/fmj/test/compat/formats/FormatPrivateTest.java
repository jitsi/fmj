package net.sf.fmj.test.compat.formats;

import java.awt.*;
import java.lang.reflect.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class FormatPrivateTest extends TestCase
{
    private static Class getClz(Format value) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException
    {
        final Field f = Format.class.getDeclaredField("clz");
        f.setAccessible(true);

        return (Class) f.get(value);
    }

    public static long getEncodingCode(Format value) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException
    {
        final Field f = Format.class.getDeclaredField("encodingCode");
        f.setAccessible(true);

        return ((Long) f.get(value)).longValue();
    }

    public static long getEncodingCode(Format value, String s)
            throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException
    {
        final Method m = Format.class.getDeclaredMethod("getEncodingCode",
                new Class[] { String.class });
        m.setAccessible(true);
        final Long result = (Long) m.invoke(value, new Object[] { s });
        return result.longValue();
    }

    private int charEncodingCodeVal(char c)
    {
        if (c <= (char) 95)
            return c - 32;
        if (c == 96)
            return -1;
        if (c <= 122)
            return c - 64;
        if (c <= 127)
            return -1;
        if (c <= 191)
            return -94;
        if (c <= 255)
            return -93;

        return -1;

    }

    void gen() throws Exception
    {
        final Format value = new Format("abc");

        for (int i = 0; i <= 255; ++i)
        {
            System.out.println("m.put((char) " + i + ", "
                    + getEncodingCode(value, "" + (char) i) + ");");
        }
    }

    // utilities for Format.getEncodingCode
    private long stringEncodingCodeVal(String s)
    {
        long result = 0;
        for (int i = 0; i < s.length(); ++i)
        {
            final char c = s.charAt(i);
            result *= 64;
            result += charEncodingCodeVal(c);

        }
        return result;
    }

    public void testAudioFormat() throws Exception
    {
        AudioFormat format = new AudioFormat("abc");

        {
            final Field f = AudioFormat.class.getDeclaredField("multiplier");
            f.setAccessible(true);
            assertEquals(f.get(format), new Double(-1.0));
        }

        {
            final Field f = AudioFormat.class.getDeclaredField("margin");
            f.setAccessible(true);
            assertEquals(((Integer) f.get(format)).intValue(), 0);
        }
        {
            final Field f = AudioFormat.class.getDeclaredField("init");
            f.setAccessible(true);
            assertEquals(((Boolean) f.get(format)).booleanValue(), false);
        }
    }

    public void testClz(Format f) throws Exception
    {
        assertEquals(getClz(f), f.getClass());
    }

    public void testEncodingCode(String s) throws Exception
    {
        final Format value = new Format("abc");
        if (getEncodingCode(value, s) != stringEncodingCodeVal(s))
        {
            System.out.println(s + ": " + getEncodingCode(value, s) + "!="
                    + stringEncodingCodeVal(s));
            assertTrue(false);
        }

    }

    public void testFormat() throws Exception
    {
        final Format value = new Format("abc");

        assertEquals(getEncodingCode(value), 0L);

        assertTrue(value.isSameEncoding(value));
        assertEquals(getEncodingCode(value), 0L);

        assertTrue(value.isSameEncoding(new Format("abc")));
        assertEquals(value.getEncoding(), "abc");
        assertEquals(getEncodingCode(value), 0L);
        assertEquals(value.matches(new Format("abc")), true);
        assertEquals(getEncodingCode(value), 0L);

        // System.out.println(getEncodingCode(value, "abc"));
        assertEquals(getEncodingCode(value, ""), 0L);
        assertEquals(getEncodingCode(value, "a"), 33L);
        assertEquals(getEncodingCode(value, "b"), 34L);
        for (int i = 'a'; i <= 'z'; ++i)
        {
            String test = "!z" + (char) i;
            testEncodingCode(test);
        }
        for (int i = 'A'; i <= 'Z'; ++i)
        {
            String test = "!z" + (char) i;
            testEncodingCode(test);
        }

        testEncodingCode("abc");
        testEncodingCode("foo.bar");
        testEncodingCode("foo/bar");
        testEncodingCode("foo_bar");

        // gen();

        // clz:

        testClz(new RGBFormat());
        testClz(new RGBFormat(new Dimension(1, 2), 1000, byte[].class, 1.f, 8,
                0xff, 0x00ff, 0x0000ff));

        for (int i = 0; i < SerializableTest.formats.length; ++i)
        {
            final Format f = SerializableTest.formats[i];

            testClz(f);
        }

    }

    public void testYUVFormat_ENCODING() throws Exception
    {
        assertEquals(new YUVFormat().getEncoding(), "yuv");

        final Field f = YUVFormat.class.getDeclaredField("ENCODING");
        f.setAccessible(true);

        assertEquals((String) f.get(null), "yuv");

    }

}
