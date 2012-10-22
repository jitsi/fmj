package net.sf.fmj.test.compat.rtp;

import javax.media.rtp.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class RTPHeaderTest extends TestCase
{
    // TODO: test serializability
    public void test()
    {
        assertEquals(new RTPHeader().isExtensionPresent(), false);
        assertEquals(new RTPHeader().getExtensionType(), -1);
        assertTrue(new RTPHeader().getExtension() == null);

        {
            byte[] ba = new byte[] { 0, 1 };
            RTPHeader h = new RTPHeader(true, 2, ba);
            assertTrue(h.getExtension() == ba);
            assertEquals(h.isExtensionPresent(), true);
            assertEquals(h.getExtensionType(), 2);

        }

        {
            byte[] ba = new byte[] { 0, 1 };
            RTPHeader h = new RTPHeader();
            h.setExtension(ba);

            assertTrue(h.getExtension() == ba);
            assertEquals(h.isExtensionPresent(), false);
            assertEquals(h.getExtensionType(), -1);

            h.setExtensionType(4);
            assertEquals(h.getExtensionType(), 4);
            assertEquals(h.isExtensionPresent(), false);
            h.setExtensionPresent(true);
            assertEquals(h.isExtensionPresent(), true);
            assertTrue(h.getExtension() == ba);
            assertEquals(h.getExtensionType(), 4);

        }

        for (int i = -1000; i < 1000; ++i)
        {
            RTPHeader h = new RTPHeader(i); // so what does this constructor do?
            assertTrue(h.getExtension() == null);
            assertEquals(h.isExtensionPresent(), false);
            assertEquals(h.getExtensionType(), -1);

        }

    }
}
