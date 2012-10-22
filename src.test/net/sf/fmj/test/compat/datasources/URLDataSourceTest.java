package net.sf.fmj.test.compat.datasources;

import java.io.*;
import java.net.*;

import javax.media.*;
import javax.media.protocol.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class URLDataSourceTest extends TestCase
{
    public void testURLDataSource() throws MalformedURLException, IOException
    {
        // TODO: test http

        String test = "file://"
                + new File("samplemedia/safexmas.mov").getAbsolutePath();

        URLDataSource s = new URLDataSource(new URL(test));
        assertTrue(s.getLocator() != null);
        assertTrue(s.getLocator().getURL().getProtocol().equals("file"));
        assertEquals(s.getLocator().getClass().getName(),
                "javax.media.MediaLocator");

        assertEquals(s.getControls().length, 0);
        try
        {
            s.getContentType();
        } catch (Error e)
        {
            assertTrue(e.getClass() == Error.class);
            assertEquals(e.getMessage(), "Source is unconnected.");
        }

        try
        {
            s.getStreams();
        } catch (Error e)
        {
            assertTrue(e.getClass() == Error.class);
            assertEquals(e.getMessage(), "Unconnected source.");
        }
        s.connect();
        assertEquals(s.getContentType(), "video.quicktime");
        PullSourceStream sts[] = s.getStreams();
        assertEquals(sts.length, 1);

        PullSourceStream st = sts[0];
        assertEquals(st.getControls().length, 0);
        ContentDescriptor cd = st.getContentDescriptor();
        assertEquals(cd.getContentType(), "video.quicktime");
        assertEquals(cd.getEncoding(), "video.quicktime");
        assertTrue(cd.getDataType() == byte[].class);

        assertFalse(st.endOfStream());
        assertEquals(st.getContentLength(), 3547908L);
        assertEquals(s.getDuration().getNanoseconds(), 9223372036854775806L);
        assertEquals(s.getDuration().getNanoseconds(),
                Time.TIME_UNKNOWN.getNanoseconds());
        assertFalse(st.willReadBlock());
        assertFalse(st.endOfStream());

        {
            final byte[] buf = new byte[1];
            final int res = st.read(buf, 0, buf.length);
            assertEquals(res, 1);
        }

        s.start();
        s.stop();
        s.connect(); // new connection - new streams?

        assertEquals(s.getStreams().length, 1);
        assertTrue(s.getStreams() != sts);
        assertTrue(s.getStreams()[0] != st);

        s.disconnect();

        // disconnect does not affect read.
        {
            final byte[] buf = new byte[1];
            final int res = st.read(buf, 0, buf.length);
            assertEquals(res, 1);
        }

    }
}
