package net.sf.fmj.test.compat.formats;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;
import net.sf.fmj.codegen.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class FormatCrossFormatTest extends TestCase
{
    private static boolean nullSafeEquals(Object o1, Object o2)
    {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);

    }

    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    private void assertNotEquals(Object o1, Object o2)
    {
        if (o1 == null && o2 == null)
            return;
        if (o1 == null || o2 == null)
            assertFalse(true);
        assertFalse(o1.equals(o2));
    }

    private void checkEncodingCode(Format f1) throws SecurityException,
            IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException
    {
        // {
        // if (FormatPrivateTest.getEncodingCode(f1) != 0)
        // {
        // System.out.println("SHOULD BE ZERO: " +
        // MediaCGUtils.formatToStr(f1));
        // }
        // //assertEquals(FormatPrivateTest.getEncodingCode(f1), 0L);
        // }
        //
        // String s = f1.toString();

        {
            if (FormatPrivateTest.getEncodingCode(f1) != 0)
            {
                System.out
                        .println("assertEquals(FormatPrivateTest.getEncodingCode("
                                + MediaCGUtils.formatToStr(f1)
                                + "), "
                                + CGUtils.toLiteral(FormatPrivateTest
                                        .getEncodingCode(f1)) + ");");

            } else
            {
                System.out.println("ZERO(FormatPrivateTest.getEncodingCode("
                        + MediaCGUtils.formatToStr(f1)
                        + "), "
                        + CGUtils.toLiteral(FormatPrivateTest
                                .getEncodingCode(f1)) + ");");

            }
            // assertNotEquals(FormatPrivateTest.getEncodingCode(f1), 0L);
        }
    }

    public void testFormats() throws Exception
    {
        assertFalse(new Format(null).isSameEncoding(new Format(null)));
        assertFalse(new Format(null).isSameEncoding((String) null));
        assertFalse(new Format(null).isSameEncoding((Format) null));

        int count = 0;
        for (int i = 0; i < SerializableTest.formats.length; ++i)
        {
            Format f1 = SerializableTest.formats[i];
            // {
            // checkEncodingCode(f1);
            //
            // }
            assertEquals(f1, f1);
            assertTrue(f1.matches(f1));

            if (f1.getEncoding() != null)
            {
                // if (!f1.isSameEncoding(f1))
                // System.out.println(f1);
                assertTrue(f1.isSameEncoding(f1));
                assertTrue(f1.isSameEncoding(f1.getEncoding()));
            } else
            {
                if (f1.isSameEncoding(f1))
                    System.out.println(f1);
                assertFalse(f1.isSameEncoding(f1));
                assertFalse(f1.isSameEncoding(f1.getEncoding()));
            }
            assertEquals(f1, f1.clone());
            assertFalse(f1.clone() == f1);

            if (f1 instanceof VideoFormat)
            {
                final VideoFormat vf1 = (VideoFormat) f1;
                if (vf1.getSize() != null)
                    assertFalse(vf1.getSize() == ((VideoFormat) vf1.clone())
                            .getSize());

                final VideoFormat fRelax = (VideoFormat) f1.relax();
                assertEquals(fRelax.getEncoding(), vf1.getEncoding());
                assertEquals(fRelax.getDataType(), vf1.getDataType());
                assertEquals(fRelax.getFrameRate(), -1.f);
                assertEquals(fRelax.getMaxDataLength(), -1);
                assertEquals(fRelax.getSize(), null);

            }

            if (f1 instanceof RGBFormat)
            {
                final RGBFormat vf1 = (RGBFormat) f1;
                if (vf1.getSize() != null)
                    assertFalse(vf1.getSize() == ((RGBFormat) vf1.clone())
                            .getSize());

                final RGBFormat fRelax = (RGBFormat) f1.relax();
                assertEquals(fRelax.getEncoding(), vf1.getEncoding());
                assertEquals(fRelax.getDataType(), vf1.getDataType());
                assertEquals(fRelax.getFrameRate(), -1.f);
                assertEquals(fRelax.getMaxDataLength(), -1);
                assertEquals(fRelax.getSize(), null);
                assertEquals(fRelax.getLineStride(), -1);
                assertEquals(fRelax.getPixelStride(), -1);

            }

            if (f1.getClass() == Format.class)
            {
                final Format fRelax = f1.relax();
                assertEquals(f1, fRelax);

            }

            for (int j = 0; j < SerializableTest.formats.length; ++j)
            {
                final Format f2 = SerializableTest.formats[j];

                final Format f3 = f1.intersects(f2);

                assertFalse(f3 == f1);
                assertFalse(f3 == f2);

                if (f1.equals(f2))
                {
                    assertTrue(f2.equals(f1));
                    assertEquals(f2.toString(), f1.toString());

                }

                if (f1.matches(f2))
                {
                    if (!f2.matches(f1))
                    {
                        System.out.println(MediaCGUtils.formatToStr(f2));
                        System.out.println(MediaCGUtils.formatToStr(f1));
                    }

                    assertTrue(f2.matches(f1));
                }

                // if (f1 instanceof VideoFormat && f2 instanceof VideoFormat)
                // {
                // final VideoFormat vf1 = (VideoFormat) f1;
                // final VideoFormat vf2 = (VideoFormat) f2;
                // final VideoFormat vf3 = (VideoFormat) f3;
                //
                // assertFalse(vf3.getSize() == vf1.getSize());
                // assertFalse(vf3.getSize() == vf2.getSize());
                //
                // }
                ++count;
            }

        }

        // System.out.println(count);
    }
}
