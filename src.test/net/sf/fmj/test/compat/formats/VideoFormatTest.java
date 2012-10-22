package net.sf.fmj.test.compat.formats;

import java.awt.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class VideoFormatTest extends TestCase
{
    class MyVideoFormat extends VideoFormat
    {
        private StringBuffer b = new StringBuffer();

        public MyVideoFormat(String arg0)
        {
            super(arg0);
        }

        public MyVideoFormat(String arg0, Dimension arg1, int arg2, Class arg3,
                float arg4)
        {
            super(arg0, arg1, arg2, arg3, arg4);
        }

        // @Override
        @Override
        public Object clone()
        {
            b.append("clone\n");
            return super.clone();
        }

        // @Override
        @Override
        protected void copy(Format arg0)
        {
            super.copy(arg0);
        }

        public void doCopy(Format f)
        {
            this.copy(f);
        }

        public StringBuffer getStringBuffer()
        {
            return b;
        }
    }

    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    public void testCloneClass()
    {
        {
            final Dimension d = new Dimension(100, 200);
            final MyVideoFormat f1 = new MyVideoFormat("abc", d, 2000,
                    int[].class, 2.f);
            final Format f2 = (Format) f1.clone();
            assertEquals(f2.getClass(), VideoFormat.class); // does not
                                                            // construct clone
                                                            // using reflection

        }

        {
            final Dimension d = new Dimension(100, 200);
            final MyVideoFormat f1 = new MyVideoFormat("abc", d, 2000,
                    int[].class, 2.f);
            final Format f2 = (Format) f1.clone();
            assertEquals(f2.getClass(), VideoFormat.class); // does not
                                                            // construct clone
                                                            // using reflection

        }

        {
            final Dimension d = new Dimension(100, 200);
            final MyVideoFormat f1 = new MyVideoFormat("abc", d, 2000,
                    int[].class, 2.f);
            final Format f2 = f1.intersects(f1);
            assertEquals(f2.getClass(), VideoFormat.class); // does not
                                                            // construct
                                                            // intersects clone
                                                            // using reflection

        }

        {
            final Dimension d = new Dimension(100, 200);
            final MyVideoFormat f1 = new MyVideoFormat("abc", d, 2000,
                    int[].class, 2.f);
            final Format f2 = f1.relax();
            assertEquals(f1.getStringBuffer().toString(), "clone\n"); // make
                                                                      // sure
                                                                      // relax
                                                                      // calls
                                                                      // clone

            assertEquals(f2.getClass(), VideoFormat.class); // does not
                                                            // construct clone
                                                            // using reflection

        }

        {
            final Dimension d = new Dimension(100, 200);
            final MyVideoFormat f1 = new MyVideoFormat("abc", d, 2000,
                    int[].class, 2.f);
            final Format f2 = f1.intersects(f1);
            assertEquals(f1.getStringBuffer().toString(), "clone\n"); // make
                                                                      // sure
                                                                      // intersects
                                                                      // calls
                                                                      // clone

            assertEquals(f2.getClass(), VideoFormat.class); // does not
                                                            // construct clone
                                                            // using reflection

        }

        {
            final Dimension d = new Dimension(100, 200);
            final MyVideoFormat f1 = new MyVideoFormat("abc", d, 2000,
                    int[].class, 2.f);
            final Format f2 = f1.intersects(new Format("xyz"));
            assertEquals(f1.getStringBuffer().toString(), "clone\n"); // make
                                                                      // sure
                                                                      // intersects
                                                                      // calls
                                                                      // clone

            assertEquals(f2.getClass(), VideoFormat.class); // does not
                                                            // construct clone
                                                            // using reflection

        }

        {
            final Dimension d = new Dimension(100, 200);
            final MyVideoFormat f1 = new MyVideoFormat("abc", d, 2000,
                    int[].class, 2.f);
            final Format f2 = new Format("xyz").intersects(f1);
            assertEquals(f1.getStringBuffer().toString(), "clone\n"); // make
                                                                      // sure
                                                                      // intersects
                                                                      // calls
                                                                      // clone

            assertEquals(f2.getClass(), VideoFormat.class); // does not
                                                            // construct clone
                                                            // using reflection

        }

    }

    public void testConstructors()
    {
        {
            final VideoFormat f = new VideoFormat("abc");
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getDataType(), byte[].class);
            assertEquals(f.getFrameRate(), -1.f);
            assertEquals(f.getMaxDataLength(), -1L);
            assertEquals(f.getSize(), null);
        }

        {
            final VideoFormat f = new VideoFormat("abc",
                    new Dimension(100, 200), 2000, int[].class, 2.f);
            assertEquals(f.getEncoding(), "abc");
            assertEquals(f.getDataType(), int[].class);
            assertEquals(f.getFrameRate(), 2.f);
            assertEquals(f.getMaxDataLength(), 2000);
            assertEquals(f.getSize(), new Dimension(100, 200));
        }
    }

    public void testFieldDuplication()
    {
        {
            final Dimension d = new Dimension(100, 200);
            final VideoFormat f1 = new VideoFormat("abc", d, 2000, int[].class,
                    2.f);
            final VideoFormat f2 = (VideoFormat) f1.clone();

            assertTrue(f1.getSize().equals(d));
            assertTrue(f1.getSize() != d);
            assertTrue(f1.getSize() != f2.getSize());
            assertTrue(f1.getSize().equals(f2.getSize()));

        }

        {
            final VideoFormat f1 = new VideoFormat("abc", new Dimension(100,
                    200), 2000, int[].class, 2.f);
            final VideoFormat f2 = (VideoFormat) f1.relax();

            assertTrue(f1.getSize() != f2.getSize());
            assertTrue(f2.getSize() == null);

        }

        {
            final VideoFormat f1 = new VideoFormat("abc", new Dimension(100,
                    200), 2000, int[].class, 2.f);
            final VideoFormat f2 = (VideoFormat) f1.intersects(f1);

            assertTrue(f1.getSize() == f2.getSize());
            assertTrue(f1.getSize().equals(f2.getSize()));

        }

        {
            final VideoFormat f1 = new VideoFormat("abc", new Dimension(100,
                    200), 2000, int[].class, 2.f);
            final VideoFormat f2 = new VideoFormat("abc", new Dimension(100,
                    201), 2000, int[].class, 2.f);
            final VideoFormat f3 = (VideoFormat) f1.intersects(f2);

            assertTrue(f1.getSize() == f3.getSize());
            assertTrue(f1.getSize().equals(f3.getSize()));

        }

        {
            final VideoFormat f1 = new VideoFormat("abc", null, 2000,
                    int[].class, 2.f);
            final VideoFormat f2 = new VideoFormat("abc", new Dimension(100,
                    201), 2000, int[].class, 2.f);
            final VideoFormat f3 = (VideoFormat) f1.intersects(f2);

            assertTrue(f3.getSize() == f2.getSize());

        }

        {
            final Format f1 = new Format("abc");
            final VideoFormat f2 = new VideoFormat("abc", new Dimension(100,
                    201), 2000, int[].class, 2.f);
            final VideoFormat f3 = (VideoFormat) f1.intersects(f2);
            final VideoFormat f4 = (VideoFormat) f2.intersects(f1);

            assertTrue(f3.getSize() != f2.getSize());
            assertTrue(f4.getSize() != f2.getSize());

        }

        {
            final Dimension d = new Dimension(100, 200);
            final MyVideoFormat f1 = new MyVideoFormat("abc", d, 2000,
                    int[].class, 2.f);
            final MyVideoFormat f2 = new MyVideoFormat("xyz");
            f2.doCopy(f1);
            assertTrue(f2.getSize() != f1.getSize());
            assertEquals(f2.getSize(), f1.getSize());
        }
    }
}
