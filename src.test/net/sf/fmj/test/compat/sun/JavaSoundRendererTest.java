package net.sf.fmj.test.compat.sun;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class JavaSoundRendererTest extends TestCase
{
    public void testJavaSoundRenderer() throws ResourceUnavailableException
    {
        com.sun.media.renderer.audio.JavaSoundRenderer r = new com.sun.media.renderer.audio.JavaSoundRenderer();
        // r.open(); // TODO: causes NPE in FMJ, not JMF.
        Format[] f = r.getSupportedInputFormats();

        Format[] expected = new Format[] {
                new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray),
                new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                        Format.byteArray), };

        assertEquals(f.length, expected.length);

        for (int i = 0; i < f.length; ++i)
        {
            assertEquals(f[i].getEncoding(), expected[i].getEncoding());
            assertTrue(f[i].matches(expected[i]));
            assertTrue(f[i].equals(expected[i]));

        }

    }
}
