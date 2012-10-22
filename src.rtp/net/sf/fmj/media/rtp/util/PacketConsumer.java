package net.sf.fmj.media.rtp.util;

import java.io.*;

public interface PacketConsumer
{
    public abstract void closeConsumer();

    public abstract String consumerString();

    public abstract void sendTo(Packet packet) throws IOException;
}
