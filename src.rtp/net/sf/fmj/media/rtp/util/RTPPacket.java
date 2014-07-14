package net.sf.fmj.media.rtp.util;

import java.io.*;

import net.sf.fmj.utility.*;

public class RTPPacket extends Packet
{
    public Packet base;
    public boolean extensionPresent;
    public int marker;
    public int payloadType;
    public int seqnum;
    public long timestamp;
    public int ssrc;
    public int csrc[];
    public int extensionType;
    public byte extension[];
    public int payloadoffset;
    public int payloadlength;

    public RTPPacket()
    {
    }

    public RTPPacket(Packet p)
    {
        super(p);
        base = p;
    }

    public void assemble(int len, boolean encrypted)
    {
        length = len;
        offset = 0;

        if ((data == null) || (data.length < len))
            data = new byte[len];

        ByteBufferOutputStream bbos = new ByteBufferOutputStream(data, 0, len);
        DataOutputStream dos = new DataOutputStream(bbos);
        try
        {
            dos.writeByte(128);
            int mp = payloadType;
            if (marker == 1)
                mp = payloadType | 0x80;
            dos.writeByte((byte) mp);
            dos.writeShort(seqnum);
            dos.writeInt((int) timestamp);
            dos.writeInt(ssrc);
            dos.write(base.data, payloadoffset, payloadlength);
        }
        catch (IOException e)
        {
            System.out.println("caught IOException in DOS");
        }
        finally
        {
            try { dos.close(); } catch (IOException e) {}
        }
    }

    public int calcLength()
    {
        return payloadlength + 12;
    }

    @Override
    public RTPPacket clone()
    {
        RTPPacket p = new RTPPacket(base.clone());
        p.extensionPresent = extensionPresent;
        p.marker = marker;
        p.payloadType = payloadType;
        p.seqnum = seqnum;
        p.timestamp = timestamp;
        p.ssrc = ssrc;
        p.csrc = csrc.clone();
        p.extensionType = extensionType;
        p.extension = extension;
        p.payloadoffset = payloadoffset;
        p.payloadlength = payloadlength;
        return p;
    }

    @Override
    public String toString()
    {
        String s = "RTP Packet:\n\tPayload Type: " + payloadType
                + "    Marker: " + marker + "\n\tSequence Number: " + seqnum
                + "\n\tTimestamp: " + timestamp + "\n\tSSRC (Sync Source): "
                + ssrc + "\n\tPayload Length: " + payloadlength
                + "    Payload Offset: " + payloadoffset + "\n";
        if (csrc.length > 0)
        {
            s = s + "Contributing sources:  " + csrc[0];
            for (int i = 1; i < csrc.length; i++)
                s = s + ", " + csrc[i];

            s = s + "\n";
        }
        if (extensionPresent)
            s = s + "\tExtension:  type " + extensionType + ", length "
                    + extension.length + "\n";
        return s;
    }
}
