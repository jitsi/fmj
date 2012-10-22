package net.sf.fmj.test.compat.misc;

import java.io.*;

import javax.media.protocol.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class InputSourceStreamTest extends TestCase
{
    public void testInputSourceStream() throws IOException
    {
        String test = new File("samplemedia/safexmas.mov").getAbsolutePath();

        InputSourceStream s = new InputSourceStream(new FileInputStream(test),
                new ContentDescriptor("video.quicktime"));
        assertEquals(s.getContentLength(), -1);
        assertEquals(s.endOfStream(), false);
        assertEquals(s.getControls().length, 0);
        assertEquals(s.getContentDescriptor().getContentType(),
                "video.quicktime");
        assertEquals(s.willReadBlock(), false);
        s.close();

    }
}
