package net.sf.fmj.test.compat.sun;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.media.*;
import javax.media.protocol.*;

import junit.framework.*;

import com.sun.media.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class MimeManagerTest extends TestCase
{
    private static boolean compare(Hashtable t1, Hashtable t2)
    {
        if (t1.keySet().size() != t2.keySet().size())
        { // System.out.println("Sizes differ: " + t1.keySet().size() + ", " +
          // t2.keySet().size());
            return false;
        }

        final Iterator i1 = t1.keySet().iterator();
        while (i1.hasNext())
        {
            String key = (String) i1.next();
            String val1 = (String) t1.get(key);
            String val2 = (String) t2.get(key);
            if (!val1.equals(val2))
            {
                // System.out.println("Values differ for " + key + ":");
                // System.out.println(val1);
                // System.out.println(val2);
                //
                return false;
            }

        }

        final Iterator i2 = t2.keySet().iterator();
        while (i2.hasNext())
        {
            String key = (String) i2.next();
            String val1 = (String) t1.get(key);
            String val2 = (String) t2.get(key);
            if (!val1.equals(val2))
            {
                // System.out.println("Values differ for " + key + ":");
                // System.out.println(val1);
                // System.out.println(val2);
                return false;
            }

        }

        return true;
    }

    private static void dump(Hashtable t)
    {
        final Iterator i = t.keySet().iterator();
        while (i.hasNext())
        {
            String key = (String) i.next();
            String val = (String) t.get(key);
            System.out.println("(\"" + key + "\", \"" + val + "\")");

        }
    }

    private static boolean nullSafeEquals(Object o1, Object o2)
    {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);

    }

    public void testMimeManager()
    {
        synchronized (MimeManager.class) // so that tests modifying the mime
                                         // manager don't conflict
        {
            final Hashtable expected = new Hashtable();
            expected.put("mvr", "application/mvr");
            expected.put("aiff", "audio/x_aiff");
            expected.put("midi", "audio/midi");
            expected.put("jmx", "application/x_jmx");
            expected.put("mpg", "video/mpeg");
            expected.put("aif", "audio/x_aiff");
            expected.put("wav", "audio/x_wav");
            expected.put("mp3", "audio/mpeg");
            expected.put("mp2", "audio/mpeg");
            expected.put("mpa", "audio/mpeg");
            expected.put("spl", "application/futuresplash");
            expected.put("viv", "video/vivo");
            expected.put("au", "audio/basic");
            expected.put("g729", "audio/g729");
            expected.put("mov", "video/quicktime");
            expected.put("avi", "video/x_msvideo");
            expected.put("g728", "audio/g728");
            expected.put("cda", "audio/cdaudio");
            expected.put("g729a", "audio/g729a");
            expected.put("gsm", "audio/x_gsm");
            expected.put("mid", "audio/midi");
            expected.put("mpv", "video/mpeg");
            expected.put("swf", "application/x-shockwave-flash");
            expected.put("rmf", "audio/rmf");

            assertTrue(compare(net.sf.fmj.media.MimeManager.getMimeTable(),
                    expected));
            assertTrue(compare(
                    net.sf.fmj.media.MimeManager.getDefaultMimeTable(),
                    expected));

            Iterator i = net.sf.fmj.media.MimeManager.getMimeTable().keySet()
                    .iterator();
            while (i.hasNext())
            {
                String key = (String) i.next();
                String mimeType = net.sf.fmj.media.MimeManager.getMimeType(key);
                // System.out.println("key: " + key + " value: " + mimeType);
                assertEquals(net.sf.fmj.media.MimeManager.getMimeType(key),
                        net.sf.fmj.media.MimeManager.getMimeTable().get(key));

                String defaultExtension = net.sf.fmj.media.MimeManager
                        .getDefaultExtension(mimeType);
                // System.out.println("assertEquals(MimeManager.getDefaultExtension(\""
                // + mimeType + "\"), \"" + defaultExtension + "\");");

                // assertEquals(MimeManager.getDefaultExtension((String)
                // MimeManager.getMimeTable().get(k)), key);

            }

            {
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("application/mvr"),
                        "mvr");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/x_aiff"),
                        "aiff");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/midi"),
                        "mid");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("application/x_jmx"),
                        "jmx");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("video/mpeg"),
                        "mpg");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/x_aiff"),
                        "aiff");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/x_wav"),
                        "wav");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/mpeg"),
                        "mp2");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/mpeg"),
                        "mp2");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/mpeg"),
                        "mp2");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("application/futuresplash"),
                        "spl");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("video/vivo"),
                        "viv");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/basic"),
                        "au");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/g729"),
                        "g729");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("video/quicktime"),
                        "mov");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("video/x_msvideo"),
                        "avi");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/g728"),
                        "g728");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/cdaudio"),
                        "cda");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/g729a"),
                        "g729a");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/x_gsm"),
                        "gsm");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/midi"),
                        "mid");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("video/mpeg"),
                        "mpg");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("application/x-shockwave-flash"),
                        "swf");
                assertEquals(
                        net.sf.fmj.media.MimeManager
                                .getDefaultExtension("audio/rmf"),
                        "rmf");

            }

            // {
            // Hashtable t = MimeManager.getMimeTable();
            // dump(t);
            // }
            //
            // System.out.println("-------");
            //
            // {
            // Hashtable t = MimeManager.getDefaultMimeTable();
            // dump(t);
            // }

            assertTrue(compare(net.sf.fmj.media.MimeManager.getMimeTable(),
                    net.sf.fmj.media.MimeManager.getDefaultMimeTable()));

            assertEquals(
                    net.sf.fmj.media.MimeManager.addMimeType("foo", "foo/bar"),
                    true);
            assertEquals(
                    net.sf.fmj.media.MimeManager.addMimeType("foo", "foo/bar"),
                    true);

            assertEquals(
                    net.sf.fmj.media.MimeManager.getMimeTable().get("foo"),
                    "foo/bar");
            assertEquals(net.sf.fmj.media.MimeManager.getDefaultMimeTable()
                    .get("foo"), null);

            assertFalse(compare(net.sf.fmj.media.MimeManager.getMimeTable(),
                    net.sf.fmj.media.MimeManager.getDefaultMimeTable()));

            assertEquals(net.sf.fmj.media.MimeManager.removeMimeType("foo"),
                    true);
            assertEquals(net.sf.fmj.media.MimeManager.removeMimeType("foo"),
                    false);

            assertTrue(compare(net.sf.fmj.media.MimeManager.getMimeTable(),
                    net.sf.fmj.media.MimeManager.getDefaultMimeTable()));

            assertEquals(net.sf.fmj.media.MimeManager.addMimeType("rmf",
                    "audio/abcdefg"), false);
            assertEquals(net.sf.fmj.media.MimeManager.getMimeType("rmf"),
                    "audio/rmf");

            assertEquals(net.sf.fmj.media.MimeManager.removeMimeType("rmf"),
                    false);
            assertEquals(net.sf.fmj.media.MimeManager.getMimeType("rmf"),
                    "audio/rmf");

            // check non-media types
            assertEquals(net.sf.fmj.media.MimeManager.getMimeType("html"), null);
            assertEquals(net.sf.fmj.media.MimeManager.getMimeType("htm"), null);
            assertEquals(net.sf.fmj.media.MimeManager.getMimeType("txt"), null);

            try
            {
                // test to verify that URLDataSource does not use MimeManager
                final File file = File.createTempFile("test", ".html");
                final URLDataSource d = new URLDataSource(new URL("file://"
                        + file.getAbsolutePath()));
                d.connect();
                assertEquals(d.getContentType(), "text.html");

                com.sun.media.protocol.file.DataSource d2 = new com.sun.media.protocol.file.DataSource();
                d2.setLocator(new MediaLocator("file://"
                        + file.getAbsolutePath()));
                d2.connect();
                assertEquals(d2.getContentType(), "text.html");

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
                assertTrue(false);
            } catch (IOException e)
            {
                e.printStackTrace();
                assertTrue(false);
            }

            try
            {
                // test to verify that URLDataSource does not use MimeManager
                File file = new File("samplemedia/safexmas.mov");
                URLDataSource d = new URLDataSource(new URL("file://"
                        + file.getAbsolutePath()));
                d.connect();
                assertEquals(d.getContentType(), "video.quicktime");

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
                assertTrue(false);
            } catch (IOException e)
            {
                e.printStackTrace();
                assertTrue(false);
            }

            assertEquals(
                    net.sf.fmj.media.MimeManager.addMimeType("foo", "foo/bar"),
                    true);

            try
            {
                File file = File.createTempFile("test", "foo");
                // test to verify that URLDataSource does not use MimeManager
                // File file = new File("test.foo");
                URLDataSource d = new URLDataSource(new URL("file://"
                        + file.getAbsolutePath()));
                d.connect();
                assertEquals(d.getContentType(), "content.unknown");

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
                assertTrue(false);
            } catch (IOException e)
            {
                e.printStackTrace();
                assertTrue(false);
            }

        }
    }
}
