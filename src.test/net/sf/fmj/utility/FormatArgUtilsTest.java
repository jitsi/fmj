package net.sf.fmj.utility;

import java.awt.*;
import java.text.*;

import javax.media.Format;
import javax.media.format.*;

import junit.framework.*;
import net.sf.fmj.media.format.*;

public class FormatArgUtilsTest extends TestCase
{
    void test(Format f) throws ParseException
    {
        String s = FormatArgUtils.toString(f);
        // System.out.println(s);
        Format f2 = FormatArgUtils.parse(s);
        assertEquals(f, f2);

    }

    public void testAudio() throws ParseException
    {
        test(new AudioFormat(AudioFormat.LINEAR, 44100.0, 16, 2));
        test(new AudioFormat(AudioFormat.LINEAR, 44100.0, 16, 2,
                AudioFormat.BIG_ENDIAN, AudioFormat.UNSIGNED));
        test(new AudioFormat(AudioFormat.LINEAR, 44100.0, 16, 2,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.UNSIGNED));
        test(new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1));
        test(new AudioFormat(AudioFormat.GSM_RTP, 8000, Format.NOT_SPECIFIED, 1));
        test(new AudioFormat(AudioFormat.G723_RTP, 8000, Format.NOT_SPECIFIED,
                1));
        test(new AudioFormat(AudioFormat.DVI_RTP, 8000, 4, 1));
        test(new AudioFormat(AudioFormat.MPEG_RTP));
        test(new AudioFormat(AudioFormat.G728_RTP, 8000.0,
                Format.NOT_SPECIFIED, 1));
        test(new AudioFormat(AudioFormat.DVI_RTP, 11025, 4, 1));
        test(new AudioFormat(AudioFormat.DVI_RTP, 22050, 4, 1));
        test(new AudioFormat(AudioFormat.G729_RTP, 8000.0,
                Format.NOT_SPECIFIED, 1));

    }

    public void testVideo() throws ParseException
    {
        test(new JPEGFormat());
        test(new GIFFormat());
        test(new PNGFormat());
        test(new GIFFormat(new Dimension(640, 480), -1, Format.byteArray, -1.f));

        // test(new VideoFormat(VideoFormat.JPEG_RTP));
        // test(new VideoFormat(VideoFormat.H261_RTP));
        // test(new VideoFormat(VideoFormat.MPEG_RTP));
        // test(new VideoFormat(VideoFormat.H263_RTP));
        // test(new VideoFormat("h263-1998/rtp"));
    }
}
