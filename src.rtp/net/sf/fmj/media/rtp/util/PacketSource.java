package net.sf.fmj.media.rtp.util;

import java.io.*;

public interface PacketSource
{
    public abstract void closeSource();

    public abstract Packet receiveFrom() throws IOException;

    public abstract String sourceString();
}
