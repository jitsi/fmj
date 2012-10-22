package net.sf.fmj.test.compat.formats;

import java.awt.*;

import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class YUVFormatTest extends TestCase
{
    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    public void testConstructors()
    {
        {
            final YUVFormat f1 = new YUVFormat();
            assertEquals(f1.getDataType(), byte[].class);
            assertEquals(f1.getFrameRate(), -1.f);
            assertEquals(f1.getMaxDataLength(), -1);
            assertEquals(f1.getSize(), null);
            assertEquals(f1.getYuvType(), -1);
            assertEquals(f1.getStrideY(), -1);
            assertEquals(f1.getStrideUV(), -1);
            assertEquals(f1.getOffsetY(), -1);
            assertEquals(f1.getOffsetU(), -1);
            assertEquals(f1.getOffsetV(), -1);

        }

        {
            final Dimension d = new Dimension(1, 2);
            final YUVFormat f1 = new YUVFormat(d, 1, int[].class, 2.f, 4, 5, 6,
                    7, 8, 9);
            assertEquals(f1.getDataType(), int[].class);
            assertEquals(f1.getFrameRate(), 2.f);
            assertEquals(f1.getMaxDataLength(), 1);
            assertEquals(f1.getSize(), new Dimension(1, 2));
            assertTrue(f1.getSize() != d);
            assertEquals(f1.getYuvType(), 4);
            assertEquals(f1.getStrideY(), 5);
            assertEquals(f1.getStrideUV(), 6);
            assertEquals(f1.getOffsetY(), 7);
            assertEquals(f1.getOffsetU(), 8);
            assertEquals(f1.getOffsetV(), 9);
        }
    }

    public void testToString()
    {
        assertEquals(
                new YUVFormat().toString(),
                "YUV Video Format: Size = null MaxDataLength = -1 DataType = class [B yuvType = -1 StrideY = -1 StrideUV = -1 OffsetY = -1 OffsetU = -1 OffsetV = -1\n");

        assertEquals(
                new YUVFormat(new Dimension(1, 2), 1, int[].class, 2.f, 4, 5,
                        6, 7, 8, 9).toString(),
                "YUV Video Format: Size = java.awt.Dimension[width=1,height=2] MaxDataLength = 1 DataType = class [I yuvType = 4 StrideY = 5 StrideUV = 6 OffsetY = 7 OffsetU = 8 OffsetV = 9\n");

    }
}
