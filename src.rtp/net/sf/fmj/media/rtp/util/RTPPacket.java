package net.sf.fmj.media.rtp.util;

import java.io.*;

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
        super.length = len;
        super.offset = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(len);
        DataOutputStream out = new DataOutputStream(baos);
        try
        {
            out.writeByte(128);
            int mp = payloadType;
            if (marker == 1)
                mp = payloadType | 0x80;
            out.writeByte((byte) mp);
            out.writeShort(seqnum);
            out.writeInt((int) timestamp);
            out.writeInt(ssrc);
            out.write(base.data, payloadoffset, payloadlength);
            super.data = baos.toByteArray();
        } catch (IOException e)
        {
            System.out.println("caught IOException in DOS");
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
