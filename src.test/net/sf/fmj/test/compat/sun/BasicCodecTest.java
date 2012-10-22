package net.sf.fmj.test.compat.sun;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;
import net.sf.fmj.test.tracing.*;

import com.sun.media.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class BasicCodecTest extends TestCase
{
    class MyBasicCodec extends BasicCodec
    {
        public boolean doCheckFormat(Format f)
        {
            return checkFormat(f);
        }

        public boolean doCheckInputBuffer(Buffer inputBuffer)
        {
            return checkInputBuffer(inputBuffer);
        }

        public int doGetArrayElementSize(Class type)
        {
            return getArrayElementSize(type);
        }

        public Format doGetInputFormat()
        {
            return getInputFormat();
        }

        public Format doGetOutputFormat()
        {
            return getOutputFormat();
        }

        public boolean doIsEOM(Buffer inputBuffer)
        {
            return isEOM(inputBuffer);
        }

        public void doPropagateEOM(Buffer b)
        {
            propagateEOM(b);
        }

        public void doUpdateOutput(Buffer outputBuffer, Format format,
                int length, int offset)
        {
            updateOutput(outputBuffer, format, length, offset);
        }

        public String dumpToString()
        {
            final StringBuffer b = new StringBuffer();
            b.append("inputFormat=" + inputFormat + "\n");
            b.append("outputFormat=" + outputFormat + "\n");
            b.append("opened=" + opened + "\n");
            b.append("inputFormats=" + inputFormats + "\n");
            if (inputFormats != null)
            {
                for (int i = 0; i < inputFormats.length; ++i)
                    b.append("\t" + inputFormats[i]);
            }

            b.append("outputFormats=" + outputFormats + "\n");
            if (outputFormats != null)
            {
                for (int i = 0; i < outputFormats.length; ++i)
                    b.append("\t" + outputFormats[i]);
            }

            b.append("pendingEOM=" + pendingEOM + "\n");

            return b.toString();
        }

        // @Override
        public String getName()
        {
            return null;
        }

        public Format[] getOutputFormats()
        {
            return outputFormats;
        }

        public boolean getPendingEOM()
        {
            return pendingEOM;
        }

        // @Override
        public Format[] getSupportedOutputFormats(Format arg0)
        {
            throw new RuntimeException();
        }

        public boolean isOpened()
        {
            return opened;
        }

        // @Override
        public int process(Buffer arg0, Buffer arg1)
        {
            throw new RuntimeException();
        }

        public void setInputFormats(Format[] value)
        {
            inputFormats = value;
        }

        public void setOutputFormats(Format[] value)
        {
            outputFormats = value;
        }

        public void setPendingEOM(boolean value)
        {
            pendingEOM = value;
        }

    }

    public void testArrayElementSize()
    {
        final MyBasicCodec c = new MyBasicCodec();
        assertTrue(c.doGetArrayElementSize(byte[].class) == 1);
        assertTrue(c.doGetArrayElementSize(int[].class) == 4);
        assertTrue(c.doGetArrayElementSize(short[].class) == 2);
        assertTrue(c.doGetArrayElementSize(long[].class) == 0);
        assertTrue(c.doGetArrayElementSize(byte.class) == 0);

        assertFalse(c.isOpened());

    }

    public void testBufferEOM()
    {
        final MyBasicCodec c = new MyBasicCodec();
        try
        {
            c.doIsEOM(null);
            assertFalse(true);
        } catch (NullPointerException e)
        {
        }

        final Buffer b = new Buffer();
        assertFalse(c.doIsEOM(b));
        assertFalse(c.getPendingEOM());
        b.setEOM(true);
        assertFalse(c.getPendingEOM());
        assertTrue(c.doIsEOM(b));

        assertFalse(c.getPendingEOM());

        final TracingBuffer b2 = new TracingBuffer();
        assertEquals(b2.getStringBuffer().toString(), "");

        assertFalse(c.doIsEOM(b2));
        assertEquals(b2.getStringBuffer().toString(), "isEOM\n");

        c.setOutputFormat(new Format("zzz"));
        c.doPropagateEOM(b2);
        assertEquals(b2.getStringBuffer().toString(),
                "isEOM\nsetFormat(zzz)\nsetLength(0)\nsetOffset(0)\nsetEOM\n");

        // System.out.println(b2.getStringBuffer().toString());
        assertTrue(c.doIsEOM(b2));
        assertEquals(b2.getStringBuffer().toString(),
                "isEOM\nsetFormat(zzz)\nsetLength(0)\nsetOffset(0)\nsetEOM\nisEOM\n");

        assertFalse(c.getPendingEOM());

        assertEquals(b2.getStringBuffer().toString(),
                "isEOM\nsetFormat(zzz)\nsetLength(0)\nsetOffset(0)\nsetEOM\nisEOM\n");
        // System.out.println(b2.getStringBuffer().toString());

    }

    public void testCheckFormat()
    {
        final MyBasicCodec c = new MyBasicCodec();
        assertEquals(c.doCheckFormat(null), true);
        assertEquals(c.doCheckFormat(new Format(null)), true);
        assertEquals(c.doCheckFormat(new Format("xyz")), true);

        c.setInputFormat(new RGBFormat());
        assertEquals(c.doCheckFormat(null), true);
        assertEquals(c.doCheckFormat(new YUVFormat()), true);

        c.setInputFormats(new Format[] { new RGBFormat() });
        assertEquals(c.doCheckFormat(null), true);
        assertEquals(c.doCheckFormat(new YUVFormat()), true);

        c.setOutputFormats(new Format[] { new RGBFormat() });
        assertEquals(c.doCheckFormat(null), true);
        assertEquals(c.doCheckFormat(new YUVFormat()), true);

        c.setOutputFormat(new RGBFormat());
        assertEquals(c.doCheckFormat(null), true);
        assertEquals(c.doCheckFormat(new YUVFormat()), true);

        {
            final TracingFormat f = new TracingFormat("zzz");
            c.doCheckFormat(f);
            assertEquals(f.getStringBuffer().toString(), "");
        }

    }

    public void testCheckInputBuffer()
    {
        {
            final MyBasicCodec c = new MyBasicCodec();
            final Buffer b = new Buffer();

            assertEquals(c.doCheckInputBuffer(b), false);
        }

        {
            final StringBuffer sb = new StringBuffer();
            final MyBasicCodec c = new MyBasicCodec()
            {
                // @Override
                @Override
                protected boolean checkFormat(Format arg0)
                {
                    sb.append("checkFormat");
                    return super.checkFormat(arg0);

                }

            };
            final Buffer b = new Buffer();
            final Format f = new Format("abc");
            b.setFormat(f);

            assertEquals(c.doCheckInputBuffer(b), true);
            assertEquals(sb.toString(), "checkFormat"); // checkInputBuffer
                                                        // calls checkFormat

            assertEquals(c.doCheckFormat(null), true);
            assertEquals(c.doCheckInputBuffer(new Buffer()), false);

        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final Buffer b = new Buffer();
            final Format f = new YUVFormat();
            b.setFormat(f);
            c.setInputFormat(new RGBFormat());

            assertEquals(c.doCheckInputBuffer(b), true);
        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final Buffer b = new Buffer();
            final Format f = new Format(null);
            b.setFormat(f);
            b.setLength(-1);
            b.setOffset(-1);
            b.setEOM(true);
            b.setDuration(-1L);

            c.setInputFormat(new RGBFormat());

            assertEquals(c.doCheckInputBuffer(b), true);
        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            final Format f = new Format(null);
            b.setFormat(f);
            b.setEOM(true);

            assertEquals(c.doCheckFormat(f), true);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(),
                    "setFormat(null)\nsetEOM\n");

        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            final Format f = new Format(null);
            b.setFormat(f);
            b.setEOM(true);

            assertEquals(c.doCheckInputBuffer(b), true);
            assertEquals(b.getStringBuffer().toString(),
                    "setFormat(null)\nsetEOM\nisEOM\n");

        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            final Format f = null;
            b.setFormat(f);
            b.setEOM(true);

            assertEquals(c.doCheckInputBuffer(b), true);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(),
                    "setFormat(null)\nsetEOM\nisEOM\n");

        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            final Format f = new Format(null);
            b.setFormat(f);

            assertEquals(b.getStringBuffer().toString(), "setFormat(null)\n");
            assertEquals(c.doCheckInputBuffer(b), true);
            assertEquals(b.getStringBuffer().toString(),
                    "setFormat(null)\nisEOM\ngetFormat\ngetFormat\n");

        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            final TracingFormat f = new TracingFormat(null);
            b.setFormat(f);

            assertEquals(c.doCheckInputBuffer(b), true);
            assertEquals(b.getStringBuffer().toString(),
                    "setFormat(null)\nisEOM\ngetFormat\ngetFormat\n");

        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            final Format f = new Format("xyz");
            b.setFormat(f);

            assertEquals(c.doCheckInputBuffer(b), true);
            assertEquals(b.getStringBuffer().toString(),
                    "setFormat(xyz)\nisEOM\ngetFormat\ngetFormat\n");
        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            b.setEOM(true);
            final Format f = new Format("xyz");
            b.setFormat(f);

            assertEquals(c.doCheckInputBuffer(b), true);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(),
                    "setEOM\nsetFormat(xyz)\nisEOM\n");
        }

    }

    public void testEOM() throws ResourceUnavailableException
    {
        // reset does not affect eom:
        {
            final MyBasicCodec c = new MyBasicCodec();
            assertEquals(c.getPendingEOM(), false);
            c.setPendingEOM(true);
            assertEquals(c.getPendingEOM(), true);
            {
                final String s1 = c.dumpToString();
                c.reset();
                final String s2 = c.dumpToString();
                assertEquals(s1, s2);
            }
            assertEquals(c.getPendingEOM(), true);
        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            assertEquals(c.getPendingEOM(), false);
            assertEquals(c.getPendingEOM(), false);
            {
                final String s1 = c.dumpToString();
                c.reset();
                final String s2 = c.dumpToString();
                assertEquals(s1, s2);
            }
            assertEquals(c.getPendingEOM(), false);
        }

        // open does not affect eom:
        {
            final MyBasicCodec c = new MyBasicCodec();
            assertEquals(c.getPendingEOM(), false);
            c.setPendingEOM(true);
            assertEquals(c.getPendingEOM(), true);
            c.open();
            assertEquals(c.getPendingEOM(), true);
        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            assertEquals(c.getPendingEOM(), false);
            assertEquals(c.getPendingEOM(), false);
            c.open();
            assertEquals(c.getPendingEOM(), false);
        }

        // close does not affect eom:
        {
            final MyBasicCodec c = new MyBasicCodec();
            assertEquals(c.getPendingEOM(), false);
            c.setPendingEOM(true);
            assertEquals(c.getPendingEOM(), true);
            c.close();
            assertEquals(c.getPendingEOM(), true);
        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            assertEquals(c.getPendingEOM(), false);
            assertEquals(c.getPendingEOM(), false);
            c.close();
            assertEquals(c.getPendingEOM(), false);
        }

    }

    public void testIOFormats1()
    {
        final MyBasicCodec c = new MyBasicCodec();
        assertEquals(c.doGetInputFormat(), null);
        assertEquals(c.doGetOutputFormat(), null);
        {
            final Format f1 = new Format("abc");
            final Format f1res = c.setInputFormat(f1);
            assertTrue(f1 == f1res);
            assertTrue(c.doGetInputFormat() == f1);
            assertEquals(c.doGetOutputFormat(), null);
            final Format f2 = new Format("xyz");
            final Format f2res = c.setOutputFormat(f2);
            assertTrue(f2 == f2res);
            assertTrue(c.doGetInputFormat() == f1);
            assertTrue(c.doGetOutputFormat() == f2);
        }

    }

    public void testIOFormats2()
    {
        final MyBasicCodec c = new MyBasicCodec();

        // TODO: any checking against supported i/o formats?
        // TODO: any checking against getSupportedOutputFormats - does not
        // appear to call.

        assertTrue(c.getSupportedInputFormats().length == 0);
        assertTrue(c.getOutputFormats().length == 0);

        final Format[] fs = new Format[] { new Format("abcd") };
        c.setOutputFormats(fs);
        assertTrue(c.getOutputFormats() == fs);

        {
            final Format f1 = new Format("abc");
            final Format f1res = c.setInputFormat(f1);
            assertTrue(f1 == f1res);
            assertTrue(c.doGetInputFormat() == f1);
            assertEquals(c.doGetOutputFormat(), null);
            final Format f2 = new Format("xyz");
            final Format f2res = c.setOutputFormat(f2);
            assertTrue(f2 == f2res);
            assertTrue(c.doGetInputFormat() == f1);
            assertTrue(c.doGetOutputFormat() == f2);
        }

    }

    public void testOpened() throws ResourceUnavailableException
    {
        final MyBasicCodec c = new MyBasicCodec();
        assertFalse(c.isOpened());
        c.open();
        assertTrue(c.isOpened());
        c.close();
        assertFalse(c.isOpened());
        c.open();
        assertTrue(c.isOpened());

        {
            final String s1 = c.dumpToString();
            c.reset();
            final String s2 = c.dumpToString();
            assertEquals(s1, s2);
        }

        assertTrue(c.isOpened());
        c.close();
        assertFalse(c.isOpened());
        {
            final String s1 = c.dumpToString();
            c.reset();
            final String s2 = c.dumpToString();
            assertEquals(s1, s2);
        }
        assertFalse(c.isOpened());

    }

    public void testReset() throws ResourceUnavailableException
    {
        final MyBasicCodec c = new MyBasicCodec();

        // what does reset do?

        final Format[] formats = new Format[] { null, new Format("abc") };
        final boolean[] booleanValues = new boolean[] { true, false };

        for (int i = 0; i < formats.length; ++i)
            for (int j = 0; j < booleanValues.length; ++j)
                for (int k = 0; k < booleanValues.length; ++k)
                {
                    final Format f = formats[i];
                    final boolean open = booleanValues[j];
                    final boolean eom = booleanValues[k];

                    {
                        final String s1 = c.dumpToString();
                        c.reset();
                        final String s2 = c.dumpToString();
                        assertEquals(s1, s2);
                    }

                    c.setInputFormat(f);

                    {
                        final String s1 = c.dumpToString();
                        c.reset();
                        final String s2 = c.dumpToString();
                        assertEquals(s1, s2);
                    }

                    if (open)
                        c.open();
                    else
                        c.close();

                    {
                        final String s1 = c.dumpToString();
                        c.reset();
                        final String s2 = c.dumpToString();
                        assertEquals(s1, s2);
                    }

                    c.setPendingEOM(eom);

                    {
                        final String s1 = c.dumpToString();
                        c.reset();
                        final String s2 = c.dumpToString();
                        assertEquals(s1, s2);
                    }
                }

    }

    public void testUpdateOutput()
    {
        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            final Format f = new Format("abc");
            c.doUpdateOutput(b, f, 10, 20);
            assertEquals(b.getLength(), 10);
            assertEquals(b.getOffset(), 20);
            assertTrue(b.getFormat() == f);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(), "setFormat(abc)\n"
                    + "setLength(10)\n" + "setOffset(20)\n" + "getLength\n"
                    + "getOffset\n" + "getFormat\n");
        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer()
            {
                // @Override
                @Override
                public int getLength()
                {
                    super.getLength();
                    return 1;
                }

            };
            final TracingFormat f = new TracingFormat("abc");
            assertEquals(f.getStringBuffer().toString(), "");
            c.doUpdateOutput(b, f, 10, 20);
            assertEquals(f.getStringBuffer().toString(), "getEncoding\n");

            assertEquals(b.getLength(), 1);
            assertEquals(b.getOffset(), 20);
            assertTrue(b.getFormat() == f);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(), "setFormat(abc)\n"
                    + "setLength(10)\n" + "setOffset(20)\n" + "getLength\n"
                    + "getOffset\n" + "getFormat\n");

        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            final TracingFormat f = new TracingFormat(null);
            c.doUpdateOutput(b, f, 10, 20);
            assertEquals(f.getStringBuffer().toString(), "getEncoding\n");
            assertEquals(b.getLength(), 10);
            assertEquals(b.getOffset(), 20);
            assertTrue(b.getFormat() == f);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(), "setFormat(null)\n"
                    + "setLength(10)\n" + "setOffset(20)\n" + "getLength\n"
                    + "getOffset\n" + "getFormat\n");
            assertEquals(f.getStringBuffer().toString(), "getEncoding\n");
            f.toString();
            assertEquals(f.getStringBuffer().toString(),
                    "getEncoding\ngetEncoding\n");
        }

        {
            final MyBasicCodec c = new MyBasicCodec();
            final TracingBuffer b = new TracingBuffer();
            final Format f = null;
            c.doUpdateOutput(b, f, 0, 0);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);
            assertTrue(b.getFormat() == f);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(), "setFormat(null)\n"
                    + "setLength(0)\n" + "setOffset(0)\n" + "getLength\n"
                    + "getOffset\n" + "getFormat\n");
        }

    }
}
