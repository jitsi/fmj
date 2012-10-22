package net.sf.fmj.test.compat.misc;

import java.io.*;
import java.util.*;

import javax.media.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class ManagerTest extends TestCase
{
    private void assertStringVectorEquals(Vector v1, String[] a)
    {
        assertEquals(v1 == null, a == null);
        if (v1 == null)
            return;

        assertEquals(v1.size(), a.length);
        for (int j = 0; j < v1.size(); ++j)
        {
            final String s1 = (String) v1.get(j);
            final String s2 = a[j];
            assertEquals(s1, s2);
        }
    }

    private void assertStringVectorEquals(Vector v1, Vector v2)
    {
        assertEquals(v1 == null, v2 == null);
        if (v1 == null)
            return;

        assertEquals(v1.size(), v2.size());
        for (int j = 0; j < v1.size(); ++j)
        {
            final String s1 = (String) v1.get(j);
            final String s2 = (String) v2.get(j);
            assertEquals(s1, s2);
        }
    }

    private void dumpStringVector(Vector v1)
    {
        for (int i = 0; i < v1.size(); ++i)
        {
            String s = (String) v1.get(i);
            System.out.println(s);
        }
    }

    public void testCreatePlayer() throws Exception
    {
        if (true)
            return;
        synchronized (PackageManager.class) // to avoid conflicts with other
                                            // tests
        {
            String test = "file://"
                    + new File("samplemedia/safexmas.mov").getAbsolutePath();

            final Player p = Manager.createPlayer(new MediaLocator(test));
            // System.out.println(p);

        }
    }

    public void testManager()
    {
        TimeBase tb = Manager.getSystemTimeBase();
        assertEquals(tb.getClass(), SystemTimeBase.class);
        TimeBase tb2 = Manager.getSystemTimeBase();
        assertTrue(tb2 == tb);

        assertEquals(Manager.getCacheDirectory(),
                System.getProperty("java.io.tmpdir"));

        // for (Object key : System.getProperties().keySet())
        // System.out.println(key + "=" + System.getProperties().get(key));

        assertEquals(Manager.getHint(Manager.MAX_SECURITY), Boolean.FALSE);
        assertEquals(Manager.getHint(Manager.CACHING), Boolean.TRUE);
        assertEquals(Manager.getHint(Manager.LIGHTWEIGHT_RENDERER),
                Boolean.FALSE);
        assertEquals(Manager.getHint(Manager.PLUGIN_PLAYER), Boolean.FALSE);

        for (int i = Manager.PLUGIN_PLAYER + 1; i < 100; ++i)
        {
            assertEquals(Manager.getHint(i), null);
        }

        // assertEquals(Manager.getVersion(), "2.1.1e");

        synchronized (PackageManager.class) // to avoid conflicts with other
                                            // tests
        {
            assertStringVectorEquals(Manager.getHandlerClassList(""),
                    new String[] { "media.content..Handler",
                            "javax.media.content..Handler",
                            "com.sun.media.content..Handler",
                            "com.ibm.media.content..Handler", });

            assertStringVectorEquals(Manager.getHandlerClassList("abc"),
                    new String[] { "media.content.abc.Handler",
                            "javax.media.content.abc.Handler",
                            "com.sun.media.content.abc.Handler",
                            "com.ibm.media.content.abc.Handler", });

            assertStringVectorEquals(Manager.getHandlerClassList("abc.xyz"),
                    new String[] { "media.content.abc.xyz.Handler",
                            "javax.media.content.abc.xyz.Handler",
                            "com.sun.media.content.abc.xyz.Handler",
                            "com.ibm.media.content.abc.xyz.Handler", });

            assertStringVectorEquals(Manager.getHandlerClassList("abc/xyz"),
                    new String[] { "media.content.abc.xyz.Handler",
                            "javax.media.content.abc.xyz.Handler",
                            "com.sun.media.content.abc.xyz.Handler",
                            "com.ibm.media.content.abc.xyz.Handler", });

            {
                Vector v = new Vector();
                v.add("org.foo");
                PackageManager.setContentPrefixList(v);
            }

            assertStringVectorEquals(Manager.getHandlerClassList("abc"),
                    new String[] { "media.content.abc.Handler",
                            "org.foo.media.content.abc.Handler",
                            "javax.media.content.abc.Handler", });

            assertStringVectorEquals(Manager.getProcessorClassList("abc"),
                    new String[] { "media.processor.abc.Handler",
                            "org.foo.media.processor.abc.Handler",
                            "javax.media.processor.abc.Handler", });

            assertStringVectorEquals(Manager.getProcessorClassList("abc#xyz"),
                    new String[] { "media.processor.abc_xyz.Handler",
                            "org.foo.media.processor.abc_xyz.Handler",
                            "javax.media.processor.abc_xyz.Handler", });

            assertStringVectorEquals(Manager.getDataSourceList("abc"),
                    new String[] { "media.protocol.abc.DataSource",
                            "javax.media.protocol.abc.DataSource",
                            "com.sun.media.protocol.abc.DataSource",
                            "com.ibm.media.protocol.abc.DataSource", });

            {
                Vector v = new Vector();
                v.add("org.bar");
                PackageManager.setProtocolPrefixList(v);
            }

            assertStringVectorEquals(Manager.getDataSourceList("abc#xyz"),
                    new String[] { "media.protocol.abc#xyz.DataSource",
                            "org.bar.media.protocol.abc#xyz.DataSource",
                            "javax.media.protocol.abc#xyz.DataSource",

                    });

            // dumpStringVector(Manager.getDataSourceList("abc#xyz"));

            // restore PackageManager:

            {
                final Vector v = new Vector();
                v.add("javax");
                v.add("com.sun");
                v.add("com.ibm");
                PackageManager.setProtocolPrefixList(v);

            }

            {
                final Vector v = new Vector();
                v.add("javax");
                v.add("com.sun");
                v.add("com.ibm");
                PackageManager.setContentPrefixList(v);
            }

        }

    }
}
