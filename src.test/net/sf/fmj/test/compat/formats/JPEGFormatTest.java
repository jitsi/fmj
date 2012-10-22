package net.sf.fmj.test.compat.formats;

import java.awt.*;

import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class JPEGFormatTest extends TestCase
{
    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    public void testConstructors()
    {
        {
            final JPEGFormat f1 = new JPEGFormat();
            assertEquals(f1.getDataType(), byte[].class);
            assertEquals(f1.getFrameRate(), -1.f);
            assertEquals(f1.getMaxDataLength(), -1);
            assertEquals(f1.getSize(), null);
            assertEquals(f1.getQFactor(), -1);
            assertEquals(f1.getDecimation(), -1);
        }

        {
            final Dimension d = new Dimension(1, 2);
            final JPEGFormat f1 = new JPEGFormat(d, 1, int[].class, 2.f, 3, 4);
            assertEquals(f1.getDataType(), int[].class);
            assertEquals(f1.getFrameRate(), 2.f);
            assertEquals(f1.getMaxDataLength(), 1);
            assertEquals(f1.getSize(), new Dimension(1, 2));
            assertTrue(f1.getSize() != d);
            assertEquals(f1.getQFactor(), 3);
            assertEquals(f1.getDecimation(), 4);
        }
    }
}
