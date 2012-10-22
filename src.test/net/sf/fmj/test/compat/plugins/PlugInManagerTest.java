package net.sf.fmj.test.compat.plugins;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class PlugInManagerTest extends TestCase
{
    private void assertEquals(Format[] f1s, Format[] f2s)
    {
        assertEquals(f1s == null, f2s == null);
        if (f1s != null)
        {
            assertEquals(f1s.length, f2s.length);
            for (int k = 0; k < f1s.length; ++k)
            {
                assertEquals(f1s[k], f2s[k]);
            }
        }
    }

    public void assertPlugInManagersEqual()
    {
        for (int i = 1; i <= 5; ++i)
        {
            final Vector v1 = javax.media.PlugInManager.getPlugInList(null,
                    null, i);
            final Vector v2 = net.sf.fmj.test.compat.plugins.PlugInManager
                    .getPlugInList(null, null, i);

            if (v1 == null)
                throw new NullPointerException("v1");
            if (v2 == null)
                throw new NullPointerException("v2");

            if (v1.size() != v2.size())
            {
                dumpStringVector(v1);
                System.out.println("---");
                dumpStringVector(v2);
            }
            assertEquals(v1.size(), v2.size());
            for (int j = 0; j < v1.size(); ++j)
            {
                final String s1 = (String) v1.get(j);
                final String s2 = (String) v2.get(j);
                assertEquals(s1, s2);

                final Format[] f1s = javax.media.PlugInManager
                        .getSupportedInputFormats(s1, i);
                final Format[] f2s = net.sf.fmj.test.compat.plugins.PlugInManager
                        .getSupportedInputFormats(s1, i);

                assertEquals(f1s, f2s);

            }
        }

        {
            final String s = "com.sun.media.parser.audio.WavParser";
            final Format[] in1 = javax.media.PlugInManager
                    .getSupportedInputFormats(s,
                            javax.media.PlugInManager.DEMULTIPLEXER);
            final Format[] in2 = net.sf.fmj.test.compat.plugins.PlugInManager
                    .getSupportedInputFormats(s,
                            javax.media.PlugInManager.DEMULTIPLEXER);
            assertEquals(in1, in2);
            final Format[] out1 = javax.media.PlugInManager
                    .getSupportedOutputFormats(s,
                            javax.media.PlugInManager.DEMULTIPLEXER);
            final Format[] out2 = net.sf.fmj.test.compat.plugins.PlugInManager
                    .getSupportedOutputFormats(s,
                            javax.media.PlugInManager.DEMULTIPLEXER);
            assertEquals(out1, out2);
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

    @Override
    protected void setUp() throws Exception
    {
        System.setProperty(
                "javax.media.pim.PlugInManagerInitializer.JMFDefaults", "true");

        super.setUp();
    }

    public void testPlugInManager()
    {
        synchronized (javax.media.PlugInManager.class) // so that tests that
                                                       // modify the plugins
                                                       // don't conflict.
        {
            assertPlugInManagersEqual();

            {
                final Format in = new RGBFormat();
                final Format out = new RGBFormat();

                final Vector v1 = javax.media.PlugInManager.getPlugInList(in,
                        out, javax.media.PlugInManager.DEMULTIPLEXER);
                final Vector v2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .getPlugInList(in, out,
                                javax.media.PlugInManager.DEMULTIPLEXER);

                assertStringVectorEquals(v1, v2);

            }

            {
                final Format in = new ContentDescriptor("audio.mpeg");
                final Format out = null;

                final Vector v1 = javax.media.PlugInManager.getPlugInList(in,
                        out, javax.media.PlugInManager.DEMULTIPLEXER);
                final Vector v2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .getPlugInList(in, out,
                                javax.media.PlugInManager.DEMULTIPLEXER);

                assertStringVectorEquals(v1, v2);

            }

            {
                final Format in = new AudioFormat("mpegaudio", 16000.0, -1, -1,
                        -1, 1, -1, -1.0, Format.byteArray);
                final Format out = new AudioFormat("LINEAR", -1.0, -1, -1, -1,
                        -1, -1, -1.0, Format.byteArray);

                final Vector v1 = javax.media.PlugInManager.getPlugInList(in,
                        out, javax.media.PlugInManager.CODEC);
                final Vector v2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .getPlugInList(in, out, javax.media.PlugInManager.CODEC);

                assertStringVectorEquals(v1, v2);

            }

            {
                final Format in = new AudioFormat("mpegaudio", 16001.0, -1, -1,
                        -1, 1, -1, -1.0, Format.byteArray);
                final Format out = new AudioFormat("LINEAR", -1.0, -1, -1, -1,
                        -1, -1, -1.0, Format.byteArray);

                final Vector v1 = javax.media.PlugInManager.getPlugInList(in,
                        out, javax.media.PlugInManager.CODEC);
                final Vector v2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .getPlugInList(in, out, javax.media.PlugInManager.CODEC);

                assertStringVectorEquals(v1, v2);

            }

            {
                final Format in = new AudioFormat("mpegaudio",
                        Format.NOT_SPECIFIED, -1, -1, -1, 1, -1, -1.0,
                        Format.byteArray);
                final Format out = new AudioFormat("LINEAR", -1.0, -1, -1, -1,
                        -1, -1, -1.0, Format.byteArray);

                final Vector v1 = javax.media.PlugInManager.getPlugInList(in,
                        out, javax.media.PlugInManager.CODEC);
                final Vector v2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .getPlugInList(in, out, javax.media.PlugInManager.CODEC);

                assertStringVectorEquals(v1, v2);

            }

            {
                final boolean r1 = javax.media.PlugInManager.removePlugIn(
                        "com.ibm.media.parser.video.MpegParser",
                        javax.media.PlugInManager.DEMULTIPLEXER);
                final boolean r2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .removePlugIn("com.ibm.media.parser.video.MpegParser",
                                javax.media.PlugInManager.DEMULTIPLEXER);

                assertEquals(r1, r2);

                assertPlugInManagersEqual();
            }

            {
                final boolean a1 = javax.media.PlugInManager.addPlugIn(
                        "com.ibm.media.parser.video.MpegParser", new Format[] {
                                new ContentDescriptor("audio.mpeg"),
                                new ContentDescriptor("video.mpeg"),
                                new ContentDescriptor("audio.mpeg"), },
                        new Format[] {},
                        javax.media.PlugInManager.DEMULTIPLEXER);
                final boolean a2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .addPlugIn("com.ibm.media.parser.video.MpegParser",
                                new Format[] {
                                        new ContentDescriptor("audio.mpeg"),
                                        new ContentDescriptor("video.mpeg"),
                                        new ContentDescriptor("audio.mpeg"), },
                                new Format[] {},
                                javax.media.PlugInManager.DEMULTIPLEXER);

                assertEquals(a1, a2);

                assertPlugInManagersEqual();
            }

            {
                final boolean a1 = javax.media.PlugInManager.addPlugIn(
                        "com.ibm.media.parser.video.MpegParser", new Format[] {
                                new ContentDescriptor("audio.mpeg"),
                                new ContentDescriptor("video.mpeg"),
                                new ContentDescriptor("audio.mpeg"), },
                        new Format[] {},
                        javax.media.PlugInManager.DEMULTIPLEXER);
                final boolean a2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .addPlugIn("com.ibm.media.parser.video.MpegParser",
                                new Format[] {
                                        new ContentDescriptor("audio.mpeg"),
                                        new ContentDescriptor("video.mpeg"),
                                        new ContentDescriptor("audio.mpeg"), },
                                new Format[] {},
                                javax.media.PlugInManager.DEMULTIPLEXER);

                assertEquals(a1, a2);
                assertFalse(a1);

                assertPlugInManagersEqual();
            }

            {
                final boolean a1 = javax.media.PlugInManager.addPlugIn(
                        "org.foo.media.parser.video.MpegParser", new Format[] {
                                new ContentDescriptor("audio.mpeg"),
                                new ContentDescriptor("video.mpeg"),
                                new ContentDescriptor("audio.mpeg"), },
                        new Format[] {},
                        javax.media.PlugInManager.DEMULTIPLEXER);
                final boolean a2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .addPlugIn("org.foo.media.parser.video.MpegParser",
                                new Format[] {
                                        new ContentDescriptor("audio.mpeg"),
                                        new ContentDescriptor("video.mpeg"),
                                        new ContentDescriptor("audio.mpeg"), },
                                new Format[] {},
                                javax.media.PlugInManager.DEMULTIPLEXER);

                assertEquals(a1, a2);

                assertPlugInManagersEqual();
            }

            {
                final boolean r1 = javax.media.PlugInManager.removePlugIn(
                        "org.foo.media.parser.video.MpegParser",
                        javax.media.PlugInManager.DEMULTIPLEXER);
                final boolean r2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .removePlugIn("org.foo.media.parser.video.MpegParser",
                                javax.media.PlugInManager.DEMULTIPLEXER);

                assertEquals(r1, r2);

                assertPlugInManagersEqual();
            }

            {
                final Vector v1 = new Vector();
                final Vector v2 = new Vector();

                v1.add("com.ibm.media.parser.video.MpegParser");
                v2.add("com.ibm.media.parser.video.MpegParser");

                javax.media.PlugInManager.setPlugInList(v1,
                        javax.media.PlugInManager.DEMULTIPLEXER);
                net.sf.fmj.test.compat.plugins.PlugInManager.setPlugInList(v2,
                        javax.media.PlugInManager.DEMULTIPLEXER);

                assertPlugInManagersEqual();
            }

            {
                javax.media.PlugInManager.setPlugInList(new Vector(),
                        javax.media.PlugInManager.DEMULTIPLEXER);
                net.sf.fmj.test.compat.plugins.PlugInManager.setPlugInList(
                        new Vector(), javax.media.PlugInManager.DEMULTIPLEXER);

                assertPlugInManagersEqual();
            }

            {
                final Vector v1 = new Vector();
                final Vector v2 = new Vector();

                v1.add("com.ibm.media.parser.video.MpegParser");
                v2.add("com.ibm.media.parser.video.MpegParser");

                javax.media.PlugInManager.setPlugInList(v1,
                        javax.media.PlugInManager.DEMULTIPLEXER);
                net.sf.fmj.test.compat.plugins.PlugInManager.setPlugInList(v2,
                        javax.media.PlugInManager.DEMULTIPLEXER);

                assertPlugInManagersEqual();
            }

            {
                final boolean a1 = javax.media.PlugInManager.addPlugIn(
                        "org.foo.media.parser.video.MpegParser", new Format[] {
                                new ContentDescriptor("audio.mpeg"),
                                new ContentDescriptor("video.mpeg"),
                                new ContentDescriptor("audio.mpeg"), },
                        new Format[] {},
                        javax.media.PlugInManager.DEMULTIPLEXER);
                final boolean a2 = net.sf.fmj.test.compat.plugins.PlugInManager
                        .addPlugIn("org.foo.media.parser.video.MpegParser",
                                new Format[] {
                                        new ContentDescriptor("audio.mpeg"),
                                        new ContentDescriptor("video.mpeg"),
                                        new ContentDescriptor("audio.mpeg"), },
                                new Format[] {},
                                javax.media.PlugInManager.DEMULTIPLEXER);

                assertEquals(a1, a2);

                assertPlugInManagersEqual();
            }

            {
                IOException e1 = null;
                IOException e2 = null;
                try
                {
                    javax.media.PlugInManager.commit(); // real one does not
                                                        // actually seem to
                                                        // throw the IOException
                                                        // - appears to just
                                                        // print out
                                                        // "java.lang.reflect.InvocationTargetException"
                                                        // instead.
                } catch (IOException e)
                {
                    e1 = e;
                    e.printStackTrace();
                }
                try
                {
                    net.sf.fmj.test.compat.plugins.PlugInManager.commit();
                } catch (IOException e)
                {
                    e2 = e;
                    e.printStackTrace();
                }

                assertEquals(e1 == null, e2 == null);

                assertPlugInManagersEqual();

            }
        }

    }
}
