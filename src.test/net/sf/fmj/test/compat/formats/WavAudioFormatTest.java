package net.sf.fmj.test.compat.formats;

import java.util.*;

import javax.media.format.*;

import junit.framework.*;

import com.sun.media.format.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class WavAudioFormatTest extends TestCase
{
    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    private void gen()
    {
        {
            ArrayList keys = new ArrayList();
            keys.addAll(WavAudioFormat.formatMapper.keySet());
            Collections.sort(keys);

            for (int i = 0; i < keys.size(); ++i)
            {
                final Object k = keys.get(i);

                System.out
                        .println("assertEquals(WavAudioFormat.formatMapper.get(new Integer("
                                + k
                                + ")), \""
                                + WavAudioFormat.formatMapper.get(k) + "\");");
            }
        }

        {
            ArrayList keys = new ArrayList();
            keys.addAll(WavAudioFormat.reverseFormatMapper.keySet());
            Collections.sort(keys);

            for (int i = 0; i < keys.size(); ++i)
            {
                final Object k = keys.get(i);

                System.out
                        .println("assertEquals(WavAudioFormat.reverseFormatMapper.get(\""
                                + k
                                + "\"), new Integer("
                                + WavAudioFormat.reverseFormatMapper.get(k)
                                + "));");
            }
        }
    }

    public void testConstructors()
    {
        {
            final WavAudioFormat f = new WavAudioFormat("abc");
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

        // other constructors tested in FormatTest.

    }

    public void testMatches()
    {
        // strangely, WavAudioFormat and AudioFormat match.
        {
            final WavAudioFormat f = new WavAudioFormat("abc");
            final AudioFormat f2 = new AudioFormat("abc");
            assertEquals(f.matches(f2), true);
            assertEquals(f2.matches(f), true);
        }

        {
            final WavAudioFormat f = new WavAudioFormat("abc", 1.0, 2, 3, 7, 4,
                    5, 6, 7, byte[].class, new byte[0]);
            final AudioFormat f2 = new AudioFormat("abc", 1.0, 2, 3, 4, 5, 6,
                    7, byte[].class);
            assertEquals(f.matches(f2), false);
            assertEquals(f2.matches(f), false);
        }

    }

    public void testWavAudioFormat()
    {
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(1)), "LINEAR");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(2)), "msadpcm");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(6)), "alaw");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(7)), "ULAW");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(17)),
                "ima4/ms");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(34)),
                "truespeech");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(49)), "gsm/ms");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(50)),
                "msnaudio");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(85)),
                "mpeglayer3");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(112)),
                "voxwareac8");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(113)),
                "voxwareac10");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(114)),
                "voxwareac16");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(115)),
                "voxwareac20");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(116)),
                "voxwaremetavoice");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(117)),
                "voxwaremetasound");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(118)),
                "voxwarert29h");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(119)),
                "voxwarevr12");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(120)),
                "voxwarevr18");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(121)),
                "voxwaretq40");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(129)),
                "voxwaretq60");
        assertEquals(WavAudioFormat.formatMapper.get(new Integer(130)),
                "msrt24");
        assertEquals(WavAudioFormat.reverseFormatMapper.get("alaw"),
                new Integer(6));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("gsm/ms"),
                new Integer(49));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("ima4/ms"),
                new Integer(17));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("linear"),
                new Integer(1));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("mpeglayer3"),
                new Integer(85));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("msadpcm"),
                new Integer(2));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("msnaudio"),
                new Integer(50));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("msrt24"),
                new Integer(130));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("truespeech"),
                new Integer(34));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("ulaw"),
                new Integer(7));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("voxwareac10"),
                new Integer(113));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("voxwareac16"),
                new Integer(114));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("voxwareac20"),
                new Integer(115));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("voxwareac8"),
                new Integer(112));
        assertEquals(
                WavAudioFormat.reverseFormatMapper.get("voxwaremetasound"),
                new Integer(117));
        assertEquals(
                WavAudioFormat.reverseFormatMapper.get("voxwaremetavoice"),
                new Integer(116));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("voxwarert29h"),
                new Integer(118));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("voxwaretq40"),
                new Integer(121));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("voxwaretq60"),
                new Integer(129));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("voxwarevr12"),
                new Integer(119));
        assertEquals(WavAudioFormat.reverseFormatMapper.get("voxwarevr18"),
                new Integer(120));

    }
}
