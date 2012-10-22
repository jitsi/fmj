package net.sf.fmj.test.compat.formats;

import java.awt.*;

import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class H263FormatTest extends TestCase
{
    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    public void testConstructors()
    {
        {
            final H263Format f1 = new H263Format();
            assertEquals(f1.getDataType(), byte[].class);
            assertEquals(f1.getFrameRate(), -1.f);
            assertEquals(f1.getMaxDataLength(), -1);
            assertEquals(f1.getSize(), null);
            assertEquals(f1.getAdvancedPrediction(), -1);
            assertEquals(f1.getArithmeticCoding(), -1);
            assertEquals(f1.getErrorCompensation(), -1);
            assertEquals(f1.getHrDB(), -1);
            assertEquals(f1.getPBFrames(), -1);
            assertEquals(f1.getUnrestrictedVector(), -1);

        }

        {
            final Dimension d = new Dimension(1, 2);
            final H263Format f1 = new H263Format(d, 1, int[].class, 2.f, 4, 5,
                    6, 7, 8, 9);
            assertEquals(f1.getDataType(), int[].class);
            assertEquals(f1.getFrameRate(), 2.f);
            assertEquals(f1.getMaxDataLength(), 1);
            assertEquals(f1.getSize(), new Dimension(1, 2));
            assertTrue(f1.getSize() != d);
            assertEquals(f1.getAdvancedPrediction(), 4);
            assertEquals(f1.getArithmeticCoding(), 5);
            assertEquals(f1.getErrorCompensation(), 6);
            assertEquals(f1.getHrDB(), 7);
            assertEquals(f1.getPBFrames(), 8);
            assertEquals(f1.getUnrestrictedVector(), 9);
        }
    }
}
