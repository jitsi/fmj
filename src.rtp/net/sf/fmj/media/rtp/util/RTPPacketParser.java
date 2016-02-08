package net.sf.fmj.media.rtp.util;

import java.io.*;

public class RTPPacketParser
{
    public RTPPacket parse(Packet packet) throws BadFormatException
    {
        RTPPacket rtppacket = new RTPPacket(packet);
        DataInputStream datainputstream = new DataInputStream(
                new ByteArrayInputStream(rtppacket.data,
                        rtppacket.offset,
                        rtppacket.length));
        try
        {
            int firstByte = datainputstream.readUnsignedByte();
            if ((firstByte & 0xc0) != 128)
                throw new BadFormatException();
            if ((firstByte & 0x10) != 0)
                rtppacket.extensionPresent = true;
            int paddingLength = 0;
            if ((firstByte & 0x20) != 0)
                paddingLength = rtppacket.data[rtppacket.offset + rtppacket.length - 1] & 0xff;
            firstByte &= 0xf;
            rtppacket.payloadType = datainputstream.readUnsignedByte();
            rtppacket.marker = rtppacket.payloadType >> 7;
            rtppacket.payloadType &= 0x7f;
            rtppacket.seqnum = datainputstream.readUnsignedShort();
            rtppacket.timestamp = datainputstream.readInt() & 0xffffffffL;
            rtppacket.ssrc = datainputstream.readInt();
            int offset = 0;
            rtppacket.csrc = new int[firstByte];
            for (int i1 = 0; i1 < rtppacket.csrc.length; i1++)
                rtppacket.csrc[i1] = datainputstream.readInt();

            offset += 12 + (rtppacket.csrc.length << 2);
            if (rtppacket.extensionPresent)
            {
                rtppacket.extensionType = datainputstream.readUnsignedShort();
                int l = datainputstream.readUnsignedShort();
                l <<= 2;
                rtppacket.extension = new byte[l];
                datainputstream.readFully(rtppacket.extension);
                offset += l + 4;
            }
            rtppacket.payloadlength = rtppacket.length - (offset + paddingLength);
            if (rtppacket.payloadlength < 0)
                throw new BadFormatException();
            rtppacket.payloadoffset = offset + rtppacket.offset;
        } catch (EOFException eofexception)
        {
            throw new BadFormatException("Unexpected end of RTP packet");
        } catch (IOException ioexception)
        {
            throw new IllegalArgumentException("Impossible Exception");
        }
        return rtppacket;
    }
}
