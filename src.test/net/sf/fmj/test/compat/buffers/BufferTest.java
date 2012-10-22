package net.sf.fmj.test.compat.buffers;

import javax.media.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class BufferTest extends TestCase
{
    public void testBuffer()
    {
        assertEquals(new Buffer().getData(), null);
        assertEquals(new Buffer().getHeader(), null);
        assertEquals(new Buffer().getDuration(), -1L);
        assertEquals(new Buffer().getFlags(), 0);
        assertEquals(new Buffer().getFormat(), null);
        assertEquals(new Buffer().getLength(), 0);
        assertEquals(new Buffer().getOffset(), 0);
        assertEquals(new Buffer().getSequenceNumber(), Long.MAX_VALUE - 1);
        assertEquals(new Buffer().getTimeStamp(), -1L);
        assertEquals(new Buffer().isEOM(), false);
        assertEquals(new Buffer().isDiscard(), false);

        {
            Buffer b = new Buffer();
            assertEquals(b.getFlags(), 0);
            b.setEOM(true);
            assertEquals(b.isEOM(), true);
            assertEquals(b.getFlags(), 1);
            b.setEOM(false);
            assertEquals(b.isEOM(), false);
            assertEquals(b.getFlags(), 0);

        }

        {
            Buffer b = new Buffer();
            assertEquals(b.getFlags(), 0);
            b.setDiscard(true);
            assertEquals(b.isDiscard(), true);
            assertEquals(b.getFlags(), 2);
            b.setDiscard(false);
            assertEquals(b.isDiscard(), false);
            assertEquals(b.getFlags(), 0);

        }

        {
            Buffer b = new Buffer();
            byte[] data = new byte[0];
            b.setData(data);
            assertTrue(b.getData() == data);
            byte[] header = new byte[0];
            b.setHeader(header);
            assertTrue(b.getHeader() == header);
            b.setDuration(1234L);
            assertEquals(b.getDuration(), 1234L);
            b.setFlags(5);
            assertEquals(b.getFlags(), 5);
            Format f = new Format("abc");
            b.setFormat(f);
            assertTrue(b.getFormat() == f);
            b.setLength(9999);
            assertEquals(b.getLength(), 9999);
            b.setOffset(1111);
            assertEquals(b.getOffset(), 1111);
            b.setSequenceNumber(123456789L);
            assertEquals(b.getSequenceNumber(), 123456789L);
            b.setTimeStamp(9999999999L);
            assertEquals(b.getTimeStamp(), 9999999999L);

        }

    }
}
