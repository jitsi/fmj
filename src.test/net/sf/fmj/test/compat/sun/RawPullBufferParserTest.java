package net.sf.fmj.test.compat.sun;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

import junit.framework.*;
import net.sf.fmj.test.tracing.*;

import com.sun.media.parser.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class RawPullBufferParserTest extends TestCase
{
    public void testRawPullBufferParser()
    {
        RawPullBufferParser p = new RawPullBufferParser();

        assertEquals(p.getName(), "Raw pull stream parser");

        try
        {
            p.start();
            assertTrue(false);
        } catch (NullPointerException e1)
        {
        } catch (IOException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }

        try
        {
            p.setSource(new TracingDataSource());
            assertTrue(false);
        } catch (IncompatibleSourceException e)
        {
        } catch (IOException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }

        {
            TracingPullBufferDataSource ds = new TracingPullBufferDataSource();
            try
            {
                p.setSource(ds);
                assertTrue(false);
            } catch (IncompatibleSourceException e)
            {
                assertTrue(false);
            } catch (IOException e)
            {
                // e.printStackTrace();

            }
            assertEquals(ds.getStringBuffer().toString(), "getStreams\n");
        }

        {
            TracingPullBufferDataSource ds = new TracingPullBufferDataSource();
            ds.streams = new PullBufferStream[0];
            try
            {
                p.setSource(ds);
                assertTrue(false);
            } catch (IncompatibleSourceException e)
            {
                assertTrue(false);
            } catch (IOException e)
            {
                // e.printStackTrace();

            }
            assertEquals(ds.getStringBuffer().toString(), "getStreams\n");
            assertEquals(p.getTracks(), null);
        }

        {
            TracingPullBufferDataSource ds = new TracingPullBufferDataSource();
            ds.streams = new PullBufferStream[1];
            try
            {
                p.setSource(ds);
                assertTrue(false);
            } catch (IncompatibleSourceException e)
            {
            } catch (IOException e)
            {
                e.printStackTrace();
                assertTrue(false);

            }
            assertEquals(ds.getStringBuffer().toString(), "getStreams\n");
            assertEquals(p.getTracks(), null);
        }

        {
            TracingPullBufferDataSource ds = new TracingPullBufferDataSource();
            TracingPullBufferStream s = new TracingPullBufferStream();
            ds.streams = new PullBufferStream[1];
            ds.streams[0] = s;
            try
            {
                p.setSource(ds);
            } catch (IncompatibleSourceException e)
            {
                assertTrue(false);

            } catch (IOException e)
            {
                e.printStackTrace();
                assertTrue(false);

            }
            assertEquals(ds.getStringBuffer().toString(), "getStreams\n");
            assertEquals(s.getStringBuffer().toString(), "");
            assertEquals(p.getTracks(), null);
            assertEquals(ds.getStringBuffer().toString(), "getStreams\n");
            assertEquals(s.getStringBuffer().toString(), "");

            try
            {
                p.open();
            } catch (Exception e)
            {
                e.printStackTrace();
                assertTrue(false);
            }
            assertEquals(ds.getStringBuffer().toString(), "getStreams\n");
            assertEquals(s.getStringBuffer().toString(), "getFormat\n");
            assertTrue(p.getTracks() != null);

            try
            {
                p.start();
            } catch (Exception e)
            {
                e.printStackTrace();
                assertTrue(false);
            }

            assertEquals(ds.getStringBuffer().toString(), "getStreams\nstart\n");
            assertEquals(s.getStringBuffer().toString(), "getFormat\n");

        }
    }
}
