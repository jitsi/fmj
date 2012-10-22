package net.sf.fmj.test.misc;

import junit.framework.*;
import net.sf.fmj.media.codec.audio.*;
import net.sf.fmj.media.codec.audio.alaw.*;
import net.sf.fmj.media.codec.audio.ulaw.*;
import net.sf.fmj.media.codec.video.jpeg.*;
import net.sf.fmj.media.datasink.rtp.*;
import net.sf.fmj.media.protocol.javasound.*;
import net.sf.fmj.utility.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class AllMiscTests
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for net.sf.fmj.test.misc");
        // $JUnit-BEGIN$

        suite.addTestSuite(RTPUrlParserTest.class);
        suite.addTestSuite(MuLawEncoderDecoderTest.class);
        suite.addTestSuite(ALawEncoderDecoderTest.class);
        suite.addTestSuite(RateConverterTest.class);
        suite.addTestSuite(JavaSoundUrlParserTest.class);
        suite.addTestSuite(JpegRTPHeaderTest.class);
        suite.addTestSuite(FormatArgUtilsTest.class);

        // $JUnit-END$
        return suite;
    }

}
