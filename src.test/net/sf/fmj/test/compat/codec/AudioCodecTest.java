package net.sf.fmj.test.compat.codec;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

import com.ibm.media.codec.audio.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class AudioCodecTest extends TestCase
{
    static class MyAudioCodec extends AudioCodec
    {
        public int process(Buffer input, Buffer output)
        {
            return 0;
        }

        public void setDefaultOutputFormats(AudioFormat[] defaultOutputFormats)
        {
            this.defaultOutputFormats = defaultOutputFormats;
        }

    }

    public void testAudioCodec() throws ResourceUnavailableException
    {
        MyAudioCodec c = new MyAudioCodec();
        {
            Format[] f = c.getSupportedOutputFormats(null);
            assertTrue(f == null);
        }
        {
            Format[] f = c.getSupportedInputFormats();
            assertTrue(f == null);
        }

        AudioFormat[] d = new AudioFormat[0];
        c.setDefaultOutputFormats(d);

        {
            Format[] f = c.getSupportedOutputFormats(null);
            assertTrue(f == d);
        }
        {
            Format[] f = c.getSupportedInputFormats();
            assertTrue(f == null);
        }

    }

    public void testConstants()
    {
        assertEquals(AudioFormat.ALAW, "alaw");
    }

    public void testDePacketizer()
    {
        // this is interesting, we return a format array with {null} in it from
        // a codec when it is
        // unhappy. However, JMF is not very happy if this happens and can
        // throw an NPE.

        com.sun.media.codec.audio.ulaw.DePacketizer c = new com.sun.media.codec.audio.ulaw.DePacketizer();
        c.open();
        Format[] f = c.getSupportedOutputFormats(new VideoFormat("xyz"));
        assertTrue(f.length == 1);
        assertTrue(f[0] == null);

    }
}
