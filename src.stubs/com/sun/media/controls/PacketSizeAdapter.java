package com.sun.media.controls;

import java.awt.*;

import javax.media.*;
import javax.media.control.*;

/**
 * TODO: Stub
 *
 * @author Ken Larson
 *
 */
public class PacketSizeAdapter implements PacketSizeControl
{
    protected Codec owner;
    protected boolean settable;
    protected int packetSize;

    public PacketSizeAdapter(Codec owner, int size, boolean settable)
    {
        super();
        this.owner = owner;
        packetSize = size;
        this.settable = settable;
    }

    public Component getControlComponent()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public int getPacketSize()
    {
        return packetSize;
    }

    public int setPacketSize(int numBytes)
    {
        throw new UnsupportedOperationException(); // TODO
    }

}
