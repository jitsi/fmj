package net.sf.fmj.test.compat.sun;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class RTPSyncBufferMuxTest extends TestCase
{
    public void test1()
    {
        com.sun.media.multiplexer.RTPSyncBufferMux m = new com.sun.media.multiplexer.RTPSyncBufferMux();
        assertEquals(m.getName(), "RTP Sync Buffer Multiplexer");
        Format[] f = m.getSupportedInputFormats();
        assertEquals(f.length, 2);

        assertEquals(f.length, 2);
        assertEquals(f[0], new AudioFormat(null, -1.0, -1, -1, -1, -1, -1,
                -1.0, Format.byteArray));
        assertEquals(f[1], new VideoFormat(null, null, -1, Format.byteArray,
                -1.0f));

        {
            Format f1 = new AudioFormat(AudioFormat.ULAW_RTP, -1.0, -1, -1, -1,
                    -1, -1, -1.0, Format.byteArray);
            for (int i = 0; i < 100; ++i)
            {
                // JMF doesn't mind if we set the input format before the mux or
                // tracks are initialized.
                // not sure if it remembers the formats in this case though.
                Format f2 = m.setInputFormat(f1, i);
                assertTrue(f2 == f1);
            }
        }

        {
            Format f1 = new AudioFormat(AudioFormat.ULAW, -1.0, -1, -1, -1, -1,
                    -1, -1.0, Format.byteArray);
            Format f2 = m.setInputFormat(f1, 0);
            assertTrue(f2 == null);
        }
    }
}
