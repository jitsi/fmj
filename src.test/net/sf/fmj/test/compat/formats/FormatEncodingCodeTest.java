package net.sf.fmj.test.compat.formats;

import java.lang.reflect.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;
import net.sf.fmj.codegen.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class FormatEncodingCodeTest extends TestCase
{
    private static boolean nullSafeEquals(Object o1, Object o2)
    {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);

    }

    private void assertNotEquals(long o1, long o2)
    {
        assertTrue(o1 != o2);
    }

    private void assertNotEquals(Object o1, Object o2)
    {
        if (o1 == null && o2 == null)
            return;
        if (o1 == null || o2 == null)
            assertFalse(true);
        assertFalse(o1.equals(o2));
    }

    private void checkEncodingCode(Format f1, boolean zero)
            throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException
    {
        if (zero)
        {
            if (FormatPrivateTest.getEncodingCode(f1) != 0)
            {
                System.out.println("SHOULD BE ZERO: "
                        + MediaCGUtils.formatToStr(f1));
            }
            assertEquals(FormatPrivateTest.getEncodingCode(f1), 0L);
        } else
        {
            if (FormatPrivateTest.getEncodingCode(f1) == 0)
            {
                System.out
                        .println("assertEquals(FormatPrivateTest.getEncodingCode("
                                + MediaCGUtils.formatToStr(f1)
                                + "), "
                                + CGUtils.toLiteral(FormatPrivateTest
                                        .getEncodingCode(f1)) + ");");

            }

            assertNotEquals(FormatPrivateTest.getEncodingCode(f1), 0L);
        }
    }

    public void testFormats() throws Exception
    {
        {
            // to demonstrate that it uses the encoding code:
            final Format f1 = new AudioFormat("abc", -1.0, -1, -1, -1, -1, -1,
                    -1.0, Format.byteArray);
            final Format f2 = new AudioFormat(" abc", -1.0, -1, -1, -1, -1, -1,
                    -1.0, Format.byteArray);
            assertEquals(f1, f2);
            assertTrue(f1.isSameEncoding(f2));
            assertTrue(f1.isSameEncoding(" abc"));

        }

        {
            // to demonstrate that it uses the encoding code:
            final Format f1 = new VideoFormat("abc");
            final Format f2 = new VideoFormat(" abc");
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertEquals(f1, f2);
            checkEncodingCode(f1, false);
            checkEncodingCode(f2, false);

        }

        {
            // to demonstrate that it uses the encoding code:
            final Format f1 = new VideoFormat("abc");
            final Format f2 = new VideoFormat(" abc");
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertTrue(f1.isSameEncoding(f2));
            checkEncodingCode(f1, false);
            checkEncodingCode(f2, false);

        }

        {
            // to demonstrate that it uses the encoding code:
            final Format f1 = new VideoFormat("abc");
            final Format f2 = new VideoFormat(" abc");
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertTrue(f1.isSameEncoding(" abc"));
            checkEncodingCode(f1, false);
            checkEncodingCode(f2, true);

        }

        {
            // to demonstrate that it uses the encoding code:
            final Format f1 = new Format("abc");
            final Format f2 = new Format(" abc");
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertTrue(f1.isSameEncoding(f2));
            checkEncodingCode(f1, false);
            checkEncodingCode(f2, false);

        }

        {
            // to demonstrate that it uses the encoding code: - // must be using
            // == first
            final Format f1 = new Format("abc");
            final Format f2 = new Format("abc");
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertTrue(f1.isSameEncoding(f2));
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);

        }

        {
            // to demonstrate that it uses the encoding code: - not if one is
            // null
            final Format f1 = new Format("abc");
            final Format f2 = new Format(null);
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertFalse(f1.isSameEncoding(f2));
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);

        }

        {
            // to demonstrate that it uses the encoding code: - not if both are
            // null
            final Format f1 = new Format(null);
            final Format f2 = new Format(null);
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertFalse(f1.isSameEncoding(f2));
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);

        }

        {
            // to demonstrate that it uses the encoding code:
            final Format f1 = new Format("abc", byte[].class);
            final Format f2 = new Format(" abc", int[].class);
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertFalse(f1.equals(f2));
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);

        }

        {
            // to demonstrate that it uses the encoding code:
            final Format f1 = new Format("abc", byte[].class);
            final Format f2 = new Format(" abc", byte[].class);
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertTrue(f1.matches(f2));
            checkEncodingCode(f1, false);
            checkEncodingCode(f2, false);

        }

        {
            // to demonstrate that it uses the encoding code:
            final Format f1 = new Format("abc", byte[].class);
            final Format f2 = new Format(" abc", int[].class);
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertFalse(f1.matches(f2));
            checkEncodingCode(f1, false);
            checkEncodingCode(f2, false);

        }

        {
            // to demonstrate that it uses the encoding code - but that it is
            // not copied with clone.
            final Format f1 = new Format("abc", byte[].class);
            final Format f2 = new Format(" abc", int[].class);
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertFalse(f1.matches(f2));

            checkEncodingCode(f1, false);
            checkEncodingCode(f2, false);
            final Format f3 = (Format) f1.clone();
            checkEncodingCode(f3, true);

        }

        {
            // to demonstrate that it uses the encoding code - but that it is
            // not copied with intersects.
            final Format f1 = new Format("abc", byte[].class);
            final Format f2 = new Format(" abc", int[].class);
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertFalse(f1.matches(f2));

            checkEncodingCode(f1, false);
            checkEncodingCode(f2, false);
            final Format f3 = f1.intersects(f1);
            checkEncodingCode(f3, true);

        }

        {
            // to demonstrate that it uses the encoding code - but that it is
            // not copied with copy.
            final Format f1 = new Format("abc", byte[].class);
            final Format f2 = new Format(" abc", int[].class);
            checkEncodingCode(f1, true);
            checkEncodingCode(f2, true);
            assertFalse(f1.matches(f2));

            checkEncodingCode(f1, false);
            checkEncodingCode(f2, false);
            final Format f3 = new Format(null);
            final Method m = Format.class.getDeclaredMethod("copy",
                    new Class[] { Format.class });
            m.setAccessible(true);
            m.invoke(f3, new Object[] { f1 });
            checkEncodingCode(f3, true);
            assertEquals(f3.getEncoding(), null);
            assertEquals(f3.getDataType(), byte[].class);

        }

    }

}
