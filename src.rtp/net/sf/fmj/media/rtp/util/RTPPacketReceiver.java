package net.sf.fmj.media.rtp.util;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;
import javax.media.rtp.*;

import net.sf.fmj.media.*;

/**
 *
 * @author Lyubomir Marinov
 */
public class RTPPacketReceiver
    implements PacketSource,
               SourceTransferHandler
{
    /**
     * The name of the <tt>PushBufferStream</tt> class.
     */
    private static final String PUSH_BUFFER_STREAM_CLASS_NAME
        = PushBufferStream.class.getName();

    RTPPushDataSource rtpsource;
    CircularBuffer bufQue;
    boolean closed;
    boolean dataRead;

    public RTPPacketReceiver(PushSourceStream pss)
    {
        rtpsource = null;
        bufQue = new CircularBuffer(2);
        closed = false;
        dataRead = false;
        pss.setTransferHandler(this);
    }

    public RTPPacketReceiver(RTPPushDataSource rtpsource)
    {
        this.rtpsource = null;
        bufQue = new CircularBuffer(2);
        closed = false;
        dataRead = false;
        this.rtpsource = rtpsource;
        PushSourceStream output = rtpsource.getOutputStream();
        output.setTransferHandler(this);
    }

    public void closeSource()
    {
        synchronized (bufQue)
        {
            closed = true;
            bufQue.notifyAll();
        }
    }

    public Packet receiveFrom()
            throws IOException
    {
        Buffer buf;
        synchronized (bufQue)
        {
            if (dataRead)
            {
                bufQue.readReport();
                bufQue.notify();
            }
            while (!bufQue.canRead() && !closed)
            {
                try
                {
                    bufQue.wait(1000L);
                } catch (InterruptedException e)
                {
                }
            }
            if (closed)
            {
                buf = null;
                dataRead = false;
            } else
            {
                buf = bufQue.read();
                dataRead = true;
            }
        }

        byte data[];
        int flags;
        int length;

        if (buf != null)
        {
            data = (byte[]) buf.getData();
            flags = buf.getFlags();
            length = buf.getLength();
        }
        else
        {
            data = new byte[1];
            flags = 0;
            length = 0;
        }

        UDPPacket p = new UDPPacket();

        p.receiptTime = System.currentTimeMillis();

        p.data = data;
        p.flags = flags;
        p.length = length;
        p.offset = 0;

        return p;
    }

    public String sourceString()
    {
        String s = "RTPPacketReceiver for " + rtpsource;
        return s;
    }

    public void transferData(PushSourceStream sourcestream)
    {
        Buffer buf;
        synchronized (bufQue)
        {
            while (!bufQue.canWrite() && !closed)
            {
                try
                {
                    bufQue.wait(1000L);
                } catch (InterruptedException e)
                {
                }
            }
            if (closed)
                return;
            buf = bufQue.getEmptyBuffer();
        }

        buf.setFlags(0);
        buf.setOffset(0);

        /*
         * If sourcestream is capable of adapting to the PushBufferStream
         * interface, it may want to provide Buffer properties other than data,
         * length and offset such as flags.
         */
        PushBufferStream pbs
            = (PushBufferStream)
                sourcestream.getControl(PUSH_BUFFER_STREAM_CLASS_NAME);

        if (pbs == null)
        {
            int size = sourcestream.getMinimumTransferSize();
            byte data[] = (byte[]) buf.getData();
            int len = 0;
            if (data == null || data.length < size)
            {
                data = new byte[size];
                buf.setData(data);
            }
            try
            {
                len = sourcestream.read(data, 0, size);
            }
            catch (IOException e)
            {
            }
            buf.setLength(len);
        }
        else
        {
            try
            {
                pbs.read(buf);
            }
            catch (IOException e)
            {
                buf.setLength(0);
            }
        }

        synchronized (bufQue)
        {
            bufQue.writeReport();
            bufQue.notify();
        }
    }
}
