package net.sf.fmj.test.compat.misc;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class CaptureDeviceManagerTest extends TestCase
{
    private void assertCaptureDeviceInfoVectorEquals(Vector v1, Vector v2)
    {
        assertEquals(v1 == null, v2 == null);
        if (v1 == null)
            return;

        assertEquals(v1.size(), v2.size());
        for (int j = 0; j < v1.size(); ++j)
        {
            final CaptureDeviceInfo s1 = (CaptureDeviceInfo) v1.get(j);
            final CaptureDeviceInfo s2 = (CaptureDeviceInfo) v2.get(j);
            assertTrue(s1 == s2);
            // assertEquals(s1, s2);
        }
    }

    private void assertNotEquals(Object a, Object b)
    {
        if (a == null && b == null)
            assertFalse(true);
        else if (a == null || b == null)
            return;

        assertFalse(a.equals(b));
    }

    private void dumpCaptureDeviceManager()
    {
        final Vector v = CaptureDeviceManager.getDeviceList(null);
        for (int i = 0; i < v.size(); ++i)
        {
            final Object o = v.get(i);
            final CaptureDeviceInfo captureDeviceInfo = (CaptureDeviceInfo) o;
            System.out.println("captureDeviceInfo: "
                    + captureDeviceInfo.getName());
        }
    }

    public void testCaptureDeviceManager()
    {
        final Vector vTest = new Vector();

        assertCaptureDeviceInfoVectorEquals(vTest,
                CaptureDeviceManager.getDeviceList(null));

        final Format[] f1s = new Format[] { new RGBFormat() };
        assertNotEquals(new CaptureDeviceInfo("abc", null, new Format[] {}),
                new CaptureDeviceInfo("abc", null, new Format[] {}));
        assertNotEquals(new CaptureDeviceInfo("abc", null, f1s),
                new CaptureDeviceInfo("abc", null, f1s)); // strange

        CaptureDeviceInfo i1 = new CaptureDeviceInfo("abc", null, f1s);
        assertNotEquals(i1, i1); // strange

        CaptureDeviceManager.addDevice(i1);
        vTest.add(i1);

        assertCaptureDeviceInfoVectorEquals(vTest,
                CaptureDeviceManager.getDeviceList(null));
        assertCaptureDeviceInfoVectorEquals(vTest,
                CaptureDeviceManager.getDeviceList(new RGBFormat()));

        CaptureDeviceManager.removeDevice(i1);
        vTest.remove(i1);

        assertCaptureDeviceInfoVectorEquals(vTest,
                CaptureDeviceManager.getDeviceList(null));

        try
        {
            CaptureDeviceManager.commit();
        } catch (IOException e)
        {
            e.printStackTrace(); // strange, doesn't throw this ever. Just
                                 // prints to standard output.
            assertTrue(false);
        }

    }
}
