package net.sf.fmj.test.compat.formats;

import java.awt.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

public class IndexedColorFormatTest extends TestCase
{
    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    public void testClone()
    {
        final byte[] arr1 = new byte[] { 0, 0 };
        final byte[] arr2 = new byte[] { 0, 0 };
        final byte[] arr3 = new byte[] { 0, 0 };

        final IndexedColorFormat f1 = new IndexedColorFormat(
                new Dimension(1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1,
                arr2, arr3);
        final IndexedColorFormat f2 = (IndexedColorFormat) f1.clone();

        assertTrue(f2.getRedValues() == f1.getRedValues());
        assertTrue(f2.getGreenValues() == f1.getGreenValues());
        assertTrue(f2.getBlueValues() == f1.getBlueValues());
        assertTrue(f2.getSize() != f1.getSize());

        assertEquals(f1.getSize(), f2.getSize());

    }

    public void testConstructors()
    {
        {
            final byte[] arr1 = new byte[] { 0, 0 };
            final byte[] arr2 = new byte[] { 0, 0 };
            final byte[] arr3 = new byte[] { 0, 0 };

            final IndexedColorFormat f1 = new IndexedColorFormat(new Dimension(
                    1, 1), 2000, Format.byteArray, 3.f, 1, 2, arr1, arr2, arr3);
            assertEquals(f1.getBlueValues(), arr3);
            assertEquals(f1.getDataType(), byte[].class);
            assertEquals(f1.getFrameRate(), 3.f);
            assertEquals(f1.getGreenValues(), arr2);
            assertEquals(f1.getLineStride(), 1);
            assertEquals(f1.getMapSize(), 2);
            assertEquals(f1.getMaxDataLength(), 2000);
            assertEquals(f1.getRedValues(), arr1);
            assertEquals(f1.getSize(), new Dimension(1, 1));
        }
    }

}
