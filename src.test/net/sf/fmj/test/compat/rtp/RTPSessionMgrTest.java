package net.sf.fmj.test.compat.rtp;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;
import net.sf.fmj.media.rtp.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class RTPSessionMgrTest extends TestCase
{
    public void testSupportedFormats()
    {
        com.sun.media.rtp.RTPSessionMgr instance = new com.sun.media.rtp.RTPSessionMgr();
        // normally we would call
        // com.sun.media.rtp.RTPSessionMgr.newInstance();, but in a test suite
        // it appears that
        // some things might not be initialized properly to allow this to work.
        // Thread problem?

        // for (int i = 0; i <= 100; ++i)
        // {
        // Format f = instance.getFormat(i);
        // if (f != null)
        // {
        // System.out.println("assertEquals(instance.getFormat(" + i + "), " +
        // MediaCGUtils.formatToStr(f) + ");");
        // }
        // }

        if (instance == null)
            throw new NullPointerException("instance");

        // generated using above code:
        {
            assertEquals(instance.getFormat(0), new AudioFormat("ULAW/rtp",
                    8000.0, 8, 1, -1, -1, -1, -1.0, Format.byteArray));
            assertEquals(instance.getFormat(3), new AudioFormat("gsm/rtp",
                    8000.0, -1, 1, -1, -1, -1, -1.0, Format.byteArray));
            assertEquals(instance.getFormat(4), new AudioFormat("g723/rtp",
                    8000.0, -1, 1, -1, -1, -1, -1.0, Format.byteArray));
            assertEquals(instance.getFormat(5), new AudioFormat("dvi/rtp",
                    8000.0, 4, 1, -1, -1, -1, -1.0, Format.byteArray));
            assertEquals(instance.getFormat(14), new AudioFormat(
                    "mpegaudio/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray));
            assertEquals(instance.getFormat(15), new AudioFormat("g728/rtp",
                    8000.0, -1, 1, -1, -1, -1, -1.0, Format.byteArray));
            assertEquals(instance.getFormat(16), new AudioFormat("dvi/rtp",
                    11025.0, 4, 1, -1, -1, -1, -1.0, Format.byteArray));
            assertEquals(instance.getFormat(17), new AudioFormat("dvi/rtp",
                    22050.0, 4, 1, -1, -1, -1, -1.0, Format.byteArray));
            assertEquals(instance.getFormat(18), new AudioFormat("g729/rtp",
                    8000.0, -1, 1, -1, -1, -1, -1.0, Format.byteArray));
            assertEquals(instance.getFormat(26), new VideoFormat("jpeg/rtp",
                    null, -1, Format.byteArray, -1.0f));
            assertEquals(instance.getFormat(31), new VideoFormat("h261/rtp",
                    null, -1, Format.byteArray, -1.0f));
            assertEquals(instance.getFormat(32), new VideoFormat("mpeg/rtp",
                    null, -1, Format.byteArray, -1.0f));
            assertEquals(instance.getFormat(34), new VideoFormat("h263/rtp",
                    null, -1, Format.byteArray, -1.0f));
            assertEquals(instance.getFormat(42), new VideoFormat(
                    "h263-1998/rtp", null, -1, Format.byteArray, -1.0f));

        }

        assertFalse(RTPSessionMgr
                .formatSupported(new com.sun.media.format.WavAudioFormat(
                        "LINEAR", 22050.0, 8, 1, 8, 22050, 0, 0, 22050.0f,
                        Format.byteArray, null)));

        assertTrue(RTPSessionMgr.formatSupported(new AudioFormat(null, -1.0,
                -1, -1, -1, -1, -1, -1.0, Format.byteArray)));

        assertFalse(RTPSessionMgr.formatSupported(new AudioFormat(
                AudioFormat.LINEAR, -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)));

        assertTrue(RTPSessionMgr.formatSupported(new AudioFormat(
                AudioFormat.ULAW_RTP, -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)));

        assertFalse(RTPSessionMgr.formatSupported(new AudioFormat(
                AudioFormat.ULAW_RTP, -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.intArray)));

        assertFalse(RTPSessionMgr.formatSupported(new AudioFormat(
                AudioFormat.ULAW, -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)));

        assertTrue(RTPSessionMgr.formatSupported(new AudioFormat(
                AudioFormat.DVI_RTP, -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)));

        assertTrue(RTPSessionMgr.formatSupported(new AudioFormat(
                AudioFormat.G723_RTP, -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)));

        assertTrue(RTPSessionMgr.formatSupported(new AudioFormat(
                AudioFormat.G728_RTP, -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)));

        assertTrue(RTPSessionMgr.formatSupported(new AudioFormat(
                AudioFormat.G729_RTP, -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)));

        // TODO: this is true in JMF, but false in FMJ, but G729A_RTP is not
        // available via getFormat in JMF.
        // So JMF must be doing a special test for this? Not sure what the
        // difference between G729_RTP and G729A_RTP is?
        // assertTrue(com.sun.media.rtp.RTPSessionMgr.formatSupported(
        // new AudioFormat(AudioFormat.G729A_RTP, -1.0, -1, -1, -1, -1, -1,
        // -1.0, Format.byteArray)));
        assertTrue(RTPSessionMgr.formatSupported(new AudioFormat(
                AudioFormat.GSM_RTP, -1.0, -1, -1, -1, -1, -1, -1.0,
                Format.byteArray)));

    }
}
