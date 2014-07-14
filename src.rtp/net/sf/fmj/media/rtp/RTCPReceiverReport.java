package net.sf.fmj.media.rtp;

import java.io.*;

import javax.media.rtp.rtcp.*;

/**
 * Represents an RTCP Receiver Report.
 *
 * @author Lyubomir Marinov
 */
public class RTCPReceiverReport
    extends RTCPReport
    implements ReceiverReport
{
    /**
     * Initializes a new <tt>RTCPReceiverReport</tt> instance.
     *
     * @param data the data of the report
     * @param offset the offset of the report in the data
     * @param length the length of the data
     * @throws IOException if an I/O error occurs while initializing the new
     * instance from the specified data
     */
    public RTCPReceiverReport(byte[] data, int offset, int length)
        throws IOException
    {
        super(data, offset, length);
    }
}
