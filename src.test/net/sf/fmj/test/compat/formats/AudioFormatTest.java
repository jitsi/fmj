package net.sf.fmj.test.compat.formats;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class AudioFormatTest extends TestCase
{
    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    public void testConstructors()
    {
        {
            final AudioFormat f = new AudioFormat("abc");
            assertEquals(f.getChannels(), -1);
            assertEquals(f.getDataType(), byte[].class);
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getEndian(), -1);
            assertEquals(f.getFrameRate(), -1.0);
            assertEquals(f.getFrameSizeInBits(), -1);
            assertEquals(f.getSampleRate(), -1.0);
            assertEquals(f.getSampleSizeInBits(), -1);
            assertEquals(f.getSigned(), -1);

        }

        {
            final AudioFormat f = new AudioFormat("abc", 1.0, 2, 3);
            assertEquals(f.getChannels(), 3);
            assertEquals(f.getDataType(), byte[].class);
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getEndian(), -1);
            assertEquals(f.getFrameRate(), -1.0);
            assertEquals(f.getFrameSizeInBits(), -1);
            assertEquals(f.getSampleRate(), 1.0);
            assertEquals(f.getSampleSizeInBits(), 2);
            assertEquals(f.getSigned(), -1);

        }

        {
            final AudioFormat f = new AudioFormat("abc", 1.0, 2, 3, 4, 5);
            assertEquals(f.getChannels(), 3);
            assertEquals(f.getDataType(), byte[].class);
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getEndian(), 4);
            assertEquals(f.getFrameRate(), -1.0);
            assertEquals(f.getFrameSizeInBits(), -1);
            assertEquals(f.getSampleRate(), 1.0);
            assertEquals(f.getSampleSizeInBits(), 2);
            assertEquals(f.getSigned(), 5);

        }

        {
            final AudioFormat f = new AudioFormat("abc", 1.0, 2, 3, 4, 5, 6, 7,
                    int[].class);
            assertEquals(f.getChannels(), 3);
            assertEquals(f.getDataType(), int[].class);
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getEndian(), 4);
            assertEquals(f.getFrameRate(), 7.0);
            assertEquals(f.getFrameSizeInBits(), 6);
            assertEquals(f.getSampleRate(), 1.0);
            assertEquals(f.getSampleSizeInBits(), 2);
            assertEquals(f.getSigned(), 5);

        }

        // verify that the frame rate/frame size is not set by default:
        {
            final AudioFormat f = new AudioFormat(AudioFormat.LINEAR, 22050.0,
                    16, 2, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
            assertEquals(f.getChannels(), 2);
            assertEquals(f.getDataType(), byte[].class);
            assertEquals(f.getEncoding(), "LINEAR");
            assertEquals(f.getEndian(), AudioFormat.LITTLE_ENDIAN);
            assertEquals(f.getFrameRate(), -1);
            assertEquals(f.getFrameSizeInBits(), -1);
            assertEquals(f.getSampleRate(), 22050.0);
            assertEquals(f.getSampleSizeInBits(), 16);
            assertEquals(f.getSigned(), AudioFormat.SIGNED);

        }

        // javasound test, just to see if it is similar
        {
            final javax.sound.sampled.AudioFormat f = new javax.sound.sampled.AudioFormat(
                    (float) 22050.0, 16, 2, false, false);

            assertEquals(f.getSampleRate(), (float) 22050.0);
            assertEquals(f.getFrameRate(), (float) 22050.0);
            assertEquals(f.getChannels(), 2);
            assertEquals(f.getSampleSizeInBits(), 16);
            assertEquals(f.getFrameSize(), 4); // javasound does set this, where
                                               // as JMF does not set frame size
                                               // in bits

        }

    }

    public void testMatchesNull()
    {
        assertFalse(new AudioFormat("speex/rtp", 8000.0, 16, 1, 0, 1, -1, -1.0,
                Format.byteArray).matches(null));
        assertFalse(new Format("speex/rtp").matches(null));

    }
}
