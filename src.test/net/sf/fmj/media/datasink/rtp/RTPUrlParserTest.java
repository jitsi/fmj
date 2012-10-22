package net.sf.fmj.media.datasink.rtp;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class RTPUrlParserTest extends TestCase
{
    public void test1() throws RTPUrlParserException
    {
        assertEquals(
                RTPUrlParser
                        .parse("rtp://224.2.231.36:22224/video&224.2.231.36:22226/audio")
                        .toString(),
                "rtp://224.2.231.36:22224/video/0&224.2.231.36:22226/audio/0");

        assertEquals(RTPUrlParser.parse("rtp://192.168.1.4:8000/audio/16")
                .toString(), "rtp://192.168.1.4:8000/audio/16");
        assertEquals(RTPUrlParser.parse("rtp://192.168.1.4:8000/video/16")
                .toString(), "rtp://192.168.1.4:8000/video/16");
        assertEquals(RTPUrlParser.parse("rtp://hostname:8000/video/16")
                .toString(), "rtp://hostname:8000/video/16");
        assertEquals(
                RTPUrlParser.parse("rtp://hostname:8000/video").toString(),
                "rtp://hostname:8000/video/0");

        try
        {
            RTPUrlParser.parse("xxx");
            assertTrue(false);
        } catch (RTPUrlParserException e)
        {
        }

        try
        {
            RTPUrlParser
                    .parse("rtp://hostname:800000000000000000000000000000/video");
            assertTrue(false);
        } catch (RTPUrlParserException e)
        {
        }

    }
}
