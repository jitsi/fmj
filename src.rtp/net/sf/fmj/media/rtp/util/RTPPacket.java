package net.sf.fmj.media.rtp.util;

import java.io.*;

import net.sf.fmj.utility.*;

import javax.media.*;

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
    public Buffer.RTPHeaderExtension headerExtension;

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

        /*
         * XXX We cannot reuse the data of super/this because it may be the same
         * as the data of base at this time.
         */
        byte[] d = new byte[len];
        ByteBufferOutputStream bbos = new ByteBufferOutputStream(d, 0, len);
        DataOutputStream dos = new DataOutputStream(bbos);

        try
        {
            byte b1 = (byte) 0x80;
            if (headerExtension != null)
                b1 |= 0x10;

            dos.writeByte(b1);
            int mp = payloadType;
            if (marker == 1)
                mp = payloadType | 0x80;
            dos.writeByte((byte) mp);
            dos.writeShort(seqnum);
            dos.writeInt((int) timestamp);
            dos.writeInt(ssrc);
            if (headerExtension != null)
            {
                int extensionLengthInWords
                    = (headerExtension.value.length + 3) / 4;
                if (extensionLengthInWords > 0)
                {
                    // "Defined by profile" field, see RFC5285
                    dos.writeShort(0xbede);
                    dos.writeShort(extensionLengthInWords);

                    dos.writeByte( (headerExtension.id << 4)
                                 | (headerExtension.value.length - 1));
                    dos.write(headerExtension.value,
                              0,
                              headerExtension.value.length);

                    // Pad the word with zeroes.
                    int i = (headerExtension.value.length + 1) % 4;
                    if (i != 0)
                        for ( ; i < 4; i++)
                            dos.writeByte(0);
                }
            }
            dos.write(base.data, payloadoffset, payloadlength);
            super.data = d;
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
        int headerExtensionsLengthInBytes = 0;
        if (headerExtension != null)
        {
            int headerExtensionsLengthInWords
                = (headerExtension.value.length + 3) / 4;

            // An extra word for "defined by profile" plus "length".
            headerExtensionsLengthInBytes
                = (headerExtensionsLengthInWords + 1 ) * 4;
        }
        return 12 /* RTP fixed header */ +
               headerExtensionsLengthInBytes +
               payloadlength;
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
        p.headerExtension = headerExtension;
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
