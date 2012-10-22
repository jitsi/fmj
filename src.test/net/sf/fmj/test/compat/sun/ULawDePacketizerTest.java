package net.sf.fmj.test.compat.sun;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class ULawDePacketizerTest extends TestCase
{
    class MyDePacketizer extends com.sun.media.codec.audio.ulaw.DePacketizer
    {
        public Format[] getProtected1()
        {
            return this.inputFormats;
        }

        public Format[] getProtected2()
        {
            return this.outputFormats;
        }
    }

    public void testDePacketizer()
    {
        MyDePacketizer p = new MyDePacketizer();
        assertEquals(p.getName(), "ULAW DePacketizer");

        Format[] f = p.getSupportedInputFormats();
        assertEquals(f.length, 1);
        assertEquals(f[0], new AudioFormat("ULAW/rtp", -1.0, -1, -1, -1, -1,
                -1, -1.0, Format.byteArray));

        Format[] f2 = p.getSupportedOutputFormats(f[0]);
        assertEquals(f2.length, 1);
        assertEquals(f2[0], new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1,
                -1.0, Format.byteArray));

        Format[] f3 = p.getProtected1();
        assertTrue(f == f3);

        Format[] f4 = p.getProtected2();
        // assertTrue(f4.length == 0); // TODO: fails in FMJ

        Format[] f5 = p.getSupportedOutputFormats(new AudioFormat(
                AudioFormat.DOLBYAC3));
        assertEquals(f5.length, 1);
        assertEquals(f5[0], null);

    }
}
