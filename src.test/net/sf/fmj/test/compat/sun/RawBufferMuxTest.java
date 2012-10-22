package net.sf.fmj.test.compat.sun;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class RawBufferMuxTest extends TestCase
{
    public void test1()
    {
        com.sun.media.multiplexer.RawBufferMux m = new com.sun.media.multiplexer.RawBufferMux();

        ContentDescriptor[] d = m.getSupportedOutputContentDescriptors(null);
        assertEquals(d.length, 1);
        assertEquals(d[0], new ContentDescriptor("raw"));

        Format[] f = m.getSupportedInputFormats();
        assertEquals(f.length, 2);
        assertEquals(f[0], new AudioFormat(null, -1.0, -1, -1, -1, -1, -1,
                -1.0, Format.byteArray));
        assertEquals(f[1], new VideoFormat(null, null, -1, Format.byteArray,
                -1.0f));

        ContentDescriptor[] d2 = m.getSupportedOutputContentDescriptors(f);
        assertEquals(d2.length, 1);
        assertEquals(d2[0], new ContentDescriptor("raw"));

    }
}
