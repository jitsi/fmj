package net.sf.fmj.test.compat.sun;

import java.util.*;

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
public class BasicPlugInTest extends TestCase
{
    class MyBasicPlugIn extends BasicPlugIn
    {
        // @Override
        @Override
        public void close()
        {
        }

        public void doError()
        {
            error();
        }

        public Object doGetInputData(Buffer arg0)
        {
            return super.getInputData(arg0);
        }

        public long doGetNativeData(Object data)
        {
            return getNativeData(data);
        }

        public Object doGetOutputData(Buffer arg0)
        {
            return super.getOutputData(arg0);
        }

        public byte[] doValidateByteArraySize(Buffer buffer, int newSize)
        {
            return validateByteArraySize(buffer, newSize);
        }

        public Object doValidateData(Buffer buffer, int length,
                boolean allowNative)
        {
            return validateData(buffer, length, allowNative);
        }

        public int[] doValidateIntArraySize(Buffer buffer, int newSize)
        {
            return validateIntArraySize(buffer, newSize);
        }

        public short[] doValidateShortArraySize(Buffer buffer, int newSize)
        {
            return validateShortArraySize(buffer, newSize);
        }

        // @Override
        @Override
        public String getName()
        {
            return null;
        }

        // @Override
        @Override
        public void open() throws ResourceUnavailableException
        {
        }

        // @Override
        @Override
        public void reset()
        {
        }
    }

    public void testBasicPlugIn()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();

        assertEquals(p.getControls().length, 0);

    }

    public void testError()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();
        try
        {
            p.doError();
            assertTrue(false);
        } catch (RuntimeException e)
        {
        }
    }

    public void testGetClassForName() throws ClassNotFoundException
    {
        assertEquals(BasicPlugIn.getClassForName("java.lang.String"),
                String.class);

    }

    public void testgetInputData()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();
        {
            final TracingBuffer b = new TracingBuffer();
            p.doGetInputData(b);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(), "getData\n");
        }

        if (true)
        {
            final TracingBuffer b = new TracingBuffer();
            final byte[] bytes = new byte[] { 0, 1 };
            b.setData(bytes);
            p.doGetInputData(b);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(), "setData\ngetData\n");
            assertTrue(bytes == b.getData());
        }

    }

    public void testGetNativeData()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();

        // test empty buffer
        {
            final Buffer b = new Buffer();

            assertEquals(p.doGetNativeData(b.getData()), 0L);

        }

        {
            final Buffer b = new Buffer();
            final byte[] bBuf = new byte[] { 1, 2, 3, 4, 5 };
            b.setData(bBuf);
            assertEquals(p.doGetNativeData(b.getData()), 0L);

        }

        {
            final Buffer b = new Buffer();
            final byte[] bBuf = new byte[] { 1, 2, 3, 4, 5 };
            b.setData(bBuf);
            b.setLength(100);
            b.setOffset(20);
            b.setDuration(100000);
            b.setDiscard(false);
            b.setFormat(new Format("abc", Format.byteArray));
            b.setSequenceNumber(1234);
            b.setTimeStamp(9999L);
            assertEquals(p.doGetNativeData(b.getData()), 0L);

        }

        {
            final Buffer b = new Buffer();
            final int[] bBuf = new int[] { 1, 2, 3, 4, 5 };
            b.setData(bBuf);
            b.setLength(100);
            b.setOffset(20);
            b.setDuration(100000);
            b.setDiscard(false);
            b.setFormat(new Format("abc", Format.intArray));
            b.setSequenceNumber(1234);
            b.setTimeStamp(9999L);
            assertEquals(p.doGetNativeData(b.getData()), 0L);

        }

        {
            final TracingBuffer b = new TracingBuffer();
            final Long n = new Long(1000);
            b.setData(n);

            assertEquals(p.doGetNativeData(b.getData()), 0L);
            // System.out.println(b.getStringBuffer().toString());

        }

    }

    public void testGetNativeData2()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();

        {
            final TracingBuffer b = new TracingBuffer();
            final Long n = new Long(1000);
            b.setData(n);

            assertEquals(p.doGetNativeData("12345678"), 0L);
            assertEquals(p.doGetNativeData(new Short((short) 100)), 0L);
            assertEquals(p.doGetNativeData(new Integer(100)), 0L);
            assertEquals(
                    p.doGetNativeData(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 }),
                    0L);
            assertEquals(p.doGetNativeData(new byte[] {}), 0L);
            assertEquals(p.doGetNativeData(new byte[100]), 0L);
            assertEquals(p.doGetNativeData(null), 0L);

            // my investigations lead me to ExtBuffer and NBA, but not sure
            // where to go next.
            // ExtBuffer eb = new ExtBuffer();
            // NBA nba = new NBA(null, 0);
            // eb.setNativeData(nba);
            // assertEquals(p.doGetNativeData(null), 0L);

            // System.out.println(b.getStringBuffer().toString());

        }

    }

    public void testgetOutputData()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();
        {
            final TracingBuffer b = new TracingBuffer();
            p.doGetOutputData(b);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(), "getData\n");
        }

        if (true)
        {
            final TracingBuffer b = new TracingBuffer();
            final byte[] bytes = new byte[] { 0, 1 };
            b.setData(bytes);
            p.doGetOutputData(b);
            // System.out.println(b.getStringBuffer().toString());
            assertEquals(b.getStringBuffer().toString(), "setData\ngetData\n");
            assertTrue(bytes == b.getData());
        }

    }

    public void testMatches()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();
        {
            final Format in = new VideoFormat(null, null, -1, null, -1.0f);
            final Format[] outs = new Format[] {
                    new AudioFormat("ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                            Format.byteArray),
                    new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                            0xff0000, 0xff00, 0xff, 1, -1, 0, -1)

            };
            assertTrue(BasicPlugIn.matches(in, outs) == outs[1]);

            // Sun's BasicPlugIn throws an NPE in this situation. So for now, so
            // will FMJ's.
            try
            {
                BasicPlugIn.matches(null, outs);
                assertTrue(false);
            } catch (NullPointerException e)
            {
            }
        }

        {
            final Format in = new VideoFormat(null, null, -1, null, -1.0f);
            final Format[] outs = new Format[] {
                    new AudioFormat("ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                            Format.byteArray),
                    new AudioFormat("ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                            Format.intArray)

            };
            assertTrue(BasicPlugIn.matches(in, outs) == null);
        }

        {
            final Format in = new VideoFormat(null, null, -1, null, -1.0f);
            final Format[] outs = new Format[] {

            };
            assertTrue(BasicPlugIn.matches(in, outs) == null);
        }

    }

    public void testPlugInExists()
    {
        assertFalse(BasicPlugIn.plugInExists("abc", -1));
        assertFalse(BasicPlugIn.plugInExists("abc", 0));
        assertFalse(BasicPlugIn.plugInExists("abc", 1));
        assertFalse(BasicPlugIn.plugInExists("", 1));
        try
        {
            BasicPlugIn.plugInExists(null, 1);
            assertTrue(false);
        } catch (NullPointerException e)
        {
        }

        if (false) // this one only works if JMF is in the classpath:
            assertTrue(BasicPlugIn.plugInExists(
                    "com.ibm.media.parser.video.MpegParser",
                    PlugInManager.DEMULTIPLEXER));

        for (int i = 1; i <= 5; ++i)
        {
            final Vector v = PlugInManager.getPlugInList(null, null, i);
            for (int j = 0; j < v.size(); ++j)
            {
                final String s = (String) v.get(j);
                assertTrue(BasicPlugIn.plugInExists(s, i));

            }
        }

    }

    public void testValidate()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();

        final boolean[] allowNatives = new boolean[] { true, false };
        for (int n = 0; n < allowNatives.length; ++n)
        {
            final boolean allowNative = allowNatives[n];

            // test empty buffer, null format:
            {
                final Buffer b = new Buffer();

                try
                {
                    final byte[] ba = (byte[]) p.doValidateData(b, 0,
                            allowNative);
                    assertTrue(false);
                } catch (NullPointerException e)
                {
                }
            }

            // buf of len 5 with length set to 5, null format:
            {
                final Buffer b = new Buffer();
                final byte[] bBuf = new byte[5];
                b.setData(bBuf);
                b.setLength(bBuf.length);

                try
                {
                    final byte[] ba = (byte[]) p.doValidateData(b, 0,
                            allowNative);
                    assertTrue(false);
                } catch (NullPointerException e)
                {
                }

            }

            // buf of len 5 with length set to 5, null format datatype:
            {
                final Buffer b = new Buffer();
                final byte[] bBuf = new byte[5];
                b.setFormat(new Format("abc", null));
                b.setData(bBuf);
                b.setLength(bBuf.length);

                final byte[] ba = (byte[]) p.doValidateData(b, 0, allowNative);
                assertEquals(ba, null);
                assertEquals(b.getData(), bBuf);

            }

            // buf of len 5 with length set to 5, byte array format
            final String[] encodings = new String[] { null, "abc" };
            for (int k = 0; k < encodings.length; ++k)
            {
                final String encoding = encodings[k];

                final Buffer b = new Buffer();
                final byte[] bBuf = new byte[] { 1, 2, 3, 4, 5 };
                b.setFormat(new Format(encoding, Format.byteArray));
                b.setData(bBuf);
                b.setLength(bBuf.length);

                for (int i = 0; i < 10; ++i)
                {
                    final byte[] ba = (byte[]) p.doValidateData(b, i,
                            allowNative);
                    final int max = i > bBuf.length ? i : bBuf.length;
                    assertEquals(ba.length, max);
                    for (int j = 0; j < i; ++j)
                    {
                        if (j < bBuf.length)
                            assertEquals(ba[j], bBuf[j]);
                        else
                            assertEquals(ba[j], 0);
                    }
                }
            }

            // non-byte-array format: - reallocates as type in format.
            {
                final Buffer b = new Buffer();
                final byte[] bBuf = new byte[] { 1, 2, 3, 4, 5 };
                b.setFormat(new Format(null, Format.intArray));
                b.setData(bBuf);
                b.setLength(bBuf.length);

                for (int i = 0; i < 10; ++i)
                {
                    final int[] ba = (int[]) p
                            .doValidateData(b, i, allowNative);
                    final int max = i > bBuf.length ? i : bBuf.length;
                    assertEquals(ba.length, i);
                    for (int j = 0; j < i; ++j)
                    {
                        assertEquals(ba[j], 0);
                    }
                }
            }

            // don't set length
            {
                final Buffer b = new Buffer();
                final byte[] bBuf = new byte[] { 1, 2, 3, 4, 5 };
                b.setFormat(new Format(null, Format.byteArray));
                b.setData(bBuf);
                // b.setLength(bBuf.length);

                for (int i = 0; i < 10; ++i)
                {
                    final byte[] ba = (byte[]) p.doValidateData(b, i,
                            allowNative);
                    final int max = i > bBuf.length ? i : bBuf.length;
                    assertEquals(ba.length, max);
                    for (int j = 0; j < i; ++j)
                    {
                        if (j < bBuf.length)
                            assertEquals(ba[j], bBuf[j]);
                        else
                            assertEquals(ba[j], 0);
                    }
                }
            }
        }

    }

    public void testValidateByteArraySize()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();

        // test empty buffer:
        {
            final Buffer b = new Buffer();
            assertEquals(b.getData(), null);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final byte[] ba = p.doValidateByteArraySize(b, i);
                assertEquals(ba.length, i);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // buf of len 5 with length set to 5:
        {
            final Buffer b = new Buffer();
            final byte[] bBuf = new byte[5];
            b.setData(bBuf);
            b.setLength(bBuf.length);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), bBuf.length);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final byte[] ba = p.doValidateByteArraySize(b, i);
                if (i > bBuf.length)
                    assertTrue(ba != bBuf);
                else
                    assertTrue(ba == bBuf);
                final int max = i > bBuf.length ? i : bBuf.length;
                assertEquals(ba.length, max);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), bBuf.length);
                assertEquals(b.getOffset(), 0);
            }
        }

        // buf of len 5 with length set to 0:
        {
            final Buffer b = new Buffer();
            final byte[] bBuf = new byte[5];
            b.setData(bBuf);
            b.setLength(0);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final byte[] ba = p.doValidateByteArraySize(b, i);
                if (i > bBuf.length)
                    assertTrue(ba != bBuf);
                else
                    assertTrue(ba == bBuf);
                final int max = i > bBuf.length ? i : bBuf.length;
                assertEquals(ba.length, max);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // it appears that getLength/setLength has nothing to do with
        // doValidateByteArraySize.
        // doValidateByteArraySize looks like it checks the size of the buf, and
        // reallocates it
        // if too small.

        // try with a non-bytearray
        {
            final Buffer b = new Buffer();
            final int[] bBuf = new int[5];
            b.setData(bBuf);
            b.setLength(0);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final byte[] ba = p.doValidateByteArraySize(b, i);
                // if (i > bBuf.length)
                // assertTrue(ba != bBuf);
                // else
                // assertTrue(ba == bBuf);
                assertEquals(ba.length, i);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // appears to simply reallocate if not a byte array.
        // See if it copies existing data:
        {
            final Buffer b = new Buffer();
            final byte[] bBuf = new byte[] { 0, 1, 2, 3, 4 };
            b.setData(bBuf);
            b.setLength(0);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final byte[] ba = p.doValidateByteArraySize(b, i);
                if (i > bBuf.length)
                    assertTrue(ba != bBuf);
                else
                    assertTrue(ba == bBuf);
                final int max = i > bBuf.length ? i : bBuf.length;
                assertEquals(ba.length, max);
                for (int j = 0; j < i; ++j)
                {
                    if (j < bBuf.length)
                        assertEquals(ba[j], bBuf[j]);
                    else
                        assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // it does copy existing data.

    }

    public void testValidateIntArraySize()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();

        // test empty buffer:
        {
            final Buffer b = new Buffer();
            assertEquals(b.getData(), null);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final int[] ba = p.doValidateIntArraySize(b, i);
                assertEquals(ba.length, i);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // buf of len 5 with length set to 5:
        {
            final Buffer b = new Buffer();
            final int[] bBuf = new int[5];
            b.setData(bBuf);
            b.setLength(bBuf.length);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), bBuf.length);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final int[] ba = p.doValidateIntArraySize(b, i);
                if (i > bBuf.length)
                    assertTrue(ba != bBuf);
                else
                    assertTrue(ba == bBuf);
                final int max = i > bBuf.length ? i : bBuf.length;
                assertEquals(ba.length, max);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), bBuf.length);
                assertEquals(b.getOffset(), 0);
            }
        }

        // buf of len 5 with length set to 0:
        {
            final Buffer b = new Buffer();
            final int[] bBuf = new int[5];
            b.setData(bBuf);
            b.setLength(0);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final int[] ba = p.doValidateIntArraySize(b, i);
                if (i > bBuf.length)
                    assertTrue(ba != bBuf);
                else
                    assertTrue(ba == bBuf);
                final int max = i > bBuf.length ? i : bBuf.length;
                assertEquals(ba.length, max);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // it appears that getLength/setLength has nothing to do with
        // doValidateIntArraySize.
        // doValidateIntArraySize looks like it checks the size of the buf, and
        // reallocates it
        // if too small.

        // try with a non-intarray
        {
            final Buffer b = new Buffer();
            final short[] bBuf = new short[5];
            b.setData(bBuf);
            b.setLength(0);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final int[] ba = p.doValidateIntArraySize(b, i);
                // if (i > bBuf.length)
                // assertTrue(ba != bBuf);
                // else
                // assertTrue(ba == bBuf);
                assertEquals(ba.length, i);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // appears to simply reallocate if not a int array.
        // See if it copies existing data:
        {
            final Buffer b = new Buffer();
            final int[] bBuf = new int[] { 0, 1, 2, 3, 4 };
            b.setData(bBuf);
            b.setLength(0);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final int[] ba = p.doValidateIntArraySize(b, i);
                if (i > bBuf.length)
                    assertTrue(ba != bBuf);
                else
                    assertTrue(ba == bBuf);
                final int max = i > bBuf.length ? i : bBuf.length;
                assertEquals(ba.length, max);
                for (int j = 0; j < i; ++j)
                {
                    if (j < bBuf.length)
                        assertEquals(ba[j], bBuf[j]);
                    else
                        assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // it does copy existing data.

    }

    public void testValidateShortArraySize()
    {
        final MyBasicPlugIn p = new MyBasicPlugIn();

        // test empty buffer:
        {
            final Buffer b = new Buffer();
            assertEquals(b.getData(), null);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final short[] ba = p.doValidateShortArraySize(b, i);
                assertEquals(ba.length, i);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // buf of len 5 with length set to 5:
        {
            final Buffer b = new Buffer();
            final short[] bBuf = new short[5];
            b.setData(bBuf);
            b.setLength(bBuf.length);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), bBuf.length);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final short[] ba = p.doValidateShortArraySize(b, i);
                if (i > bBuf.length)
                    assertTrue(ba != bBuf);
                else
                    assertTrue(ba == bBuf);
                final int max = i > bBuf.length ? i : bBuf.length;
                assertEquals(ba.length, max);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), bBuf.length);
                assertEquals(b.getOffset(), 0);
            }
        }

        // buf of len 5 with length set to 0:
        {
            final Buffer b = new Buffer();
            final short[] bBuf = new short[5];
            b.setData(bBuf);
            b.setLength(0);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final short[] ba = p.doValidateShortArraySize(b, i);
                if (i > bBuf.length)
                    assertTrue(ba != bBuf);
                else
                    assertTrue(ba == bBuf);
                final int max = i > bBuf.length ? i : bBuf.length;
                assertEquals(ba.length, max);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // it appears that getLength/setLength has nothing to do with
        // doValidateShortArraySize.
        // doValidateShortArraySize looks like it checks the size of the buf,
        // and reallocates it
        // if too small.

        // try with a non-shortarray
        {
            final Buffer b = new Buffer();
            final int[] bBuf = new int[5];
            b.setData(bBuf);
            b.setLength(0);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final short[] ba = p.doValidateShortArraySize(b, i);
                // if (i > bBuf.length)
                // assertTrue(ba != bBuf);
                // else
                // assertTrue(ba == bBuf);
                assertEquals(ba.length, i);
                for (int j = 0; j < i; ++j)
                {
                    assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // appears to simply reallocate if not a short array.
        // See if it copies existing data:
        {
            final Buffer b = new Buffer();
            final short[] bBuf = new short[] { 0, 1, 2, 3, 4 };
            b.setData(bBuf);
            b.setLength(0);
            assertTrue(b.getData() == bBuf);
            assertEquals(b.getLength(), 0);
            assertEquals(b.getOffset(), 0);

            for (int i = 0; i < 10; ++i)
            {
                final short[] ba = p.doValidateShortArraySize(b, i);
                if (i > bBuf.length)
                    assertTrue(ba != bBuf);
                else
                    assertTrue(ba == bBuf);
                final int max = i > bBuf.length ? i : bBuf.length;
                assertEquals(ba.length, max);
                for (int j = 0; j < i; ++j)
                {
                    if (j < bBuf.length)
                        assertEquals(ba[j], bBuf[j]);
                    else
                        assertEquals(ba[j], 0);
                }
                assertEquals(b.getData(), ba);
                assertEquals(b.getLength(), 0);
                assertEquals(b.getOffset(), 0);
            }
        }

        // it does copy existing data.

    }
}
