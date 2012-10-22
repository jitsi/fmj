package net.sf.fmj.media.protocol.javasound;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class JavaSoundUrlParserTest extends TestCase
{
    public void test(String str, AudioFormat format)
            throws JavaSoundUrlParserException
    {
        AudioFormat f = JavaSoundUrlParser.parse(str);
        assertEquals(f, format);
    }

    public void test1() throws JavaSoundUrlParserException
    {
        test("javasound://", new AudioFormat(AudioFormat.LINEAR));

        test("javasound://44100", new AudioFormat(AudioFormat.LINEAR, 44100.0,
                Format.NOT_SPECIFIED, Format.NOT_SPECIFIED));
        test("javasound://44100/16", new AudioFormat(AudioFormat.LINEAR,
                44100.0, 16, Format.NOT_SPECIFIED));
        test("javasound://44100/16/2", new AudioFormat(AudioFormat.LINEAR,
                44100.0, 16, 2));
        test("javasound://44100/16/2/big", new AudioFormat(AudioFormat.LINEAR,
                44100.0, 16, 2, AudioFormat.BIG_ENDIAN, Format.NOT_SPECIFIED));
        test("javasound://44100/16/2/big/signed", new AudioFormat(
                AudioFormat.LINEAR, 44100.0, 16, 2, AudioFormat.BIG_ENDIAN,
                AudioFormat.SIGNED));
    }
}
