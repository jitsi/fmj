package net.sf.fmj.media.rtp.util;

import java.io.*;

import javax.media.rtp.*;

public class RTPPacketSender implements PacketConsumer
{
    RTPPushDataSource dest;
    RTPConnector connector;
    OutputDataStream outstream;

    public RTPPacketSender(OutputDataStream os)
    {
        dest = null;
        connector = null;
        outstream = null;
        outstream = os;
    }

    public RTPPacketSender(RTPConnector connector) throws IOException
    {
        dest = null;
        this.connector = null;
        outstream = null;
        this.connector = connector;
        outstream = connector.getDataOutputStream();
    }

    public RTPPacketSender(RTPPushDataSource dest)
    {
        this.dest = null;
        connector = null;
        outstream = null;
        this.dest = dest;
        outstream = dest.getInputStream();
    }

    public void closeConsumer()
    {
    }

    public String consumerString()
    {
        String s = "RTPPacketSender for " + dest;
        return s;
    }

    public RTPConnector getConnector()
    {
        return connector;
    }

    public void sendTo(Packet p) throws IOException
    {
        if (outstream == null)
        {
            throw new IOException();
        } else
        {
            outstream.write(p.data, 0, p.length);
            return;
        }
    }
}
