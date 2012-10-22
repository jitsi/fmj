package net.sf.fmj.media.codec.video.jpeg;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class JpegRTPHeaderTest extends TestCase
{
    public void test1()
    {
        final JpegRTPHeader h = new JpegRTPHeader((byte) 0x01, 1234567,
                (byte) 0x12, (byte) 0x23, (byte) 0x34, (byte) 0x56);
        assertEquals(h, h);
        byte[] b = h.toBytes();
        final JpegRTPHeader h2 = JpegRTPHeader.parse(b, 0);
        assertEquals(h2, h);
    }
}
