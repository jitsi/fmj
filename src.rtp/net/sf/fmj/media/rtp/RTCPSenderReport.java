/*
 * @(#)RTCPSenderReport.java
 * Created: 02-Dec-2005
 * Version: 1-1-alpha3
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 * Andrew G D Rowley
 * Christian Vincenot <sipcom@cyberspace7.net>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.sf.fmj.media.rtp;

import java.io.*;

import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;

/**
 * Represents an RTCP Sender Report
 *
 * @author Andrew G D Rowley
 * @author Christian Vincenot
 * @version 1-1-alpha3
 */
public class RTCPSenderReport extends RTCPReport implements SenderReport
{
    // The sender information
    RTCPSenderInfo senderInformation = null;

    // The RTPStream associated with the sender
    private RTPStream stream = null;

    /**
     * Creates a new RTCPSenderReport
     *
     * @param data
     *            The data of the report
     * @param offset
     *            The offset of the report in the data
     * @param length
     *            The length of the data
     * @throws IOException
     *             I/O Exception
     */
    public RTCPSenderReport(byte data[], int offset, int length)
            throws IOException
    {
        super(data, offset, length);

        senderInformation
            = new RTCPSenderInfo(
                    data,
                    offset + RTCPHeader.SIZE,
                    length - RTCPHeader.SIZE);
    }

    /**
     * Returns the sender's timestamp's least significant word.
     *
     * @return the sender's timestamp's least significant word
     */
    public long getNTPTimeStampLSW()
    {
        return senderInformation.getNtpTimestampLSW();
    }

    /**
     * Returns the sender's timestamp's most significant word.
     *
     * @return the sender's timestamp's most significant word
     */
    public long getNTPTimeStampMSW()
    {
        return senderInformation.getNtpTimestampMSW();
    }

    /**
     * Returns the RTP timestamp.
     *
     * @return the RTP timestamp
     */
    public long getRTPTimeStamp()
    {
        return senderInformation.getRtpTimestamp();
    }

    /**
     * Returns the number of bytes sent by this sender.
     *
     * @return the number of bytes sent by this sender
     */
    public long getSenderByteCount()
    {
        return senderInformation.getOctetCount();
    }

    /**
     * Returns the sender's feedbacks.
     *
     * @return the sender's feedbacks
     */
    public Feedback getSenderFeedback()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns the number of packets sent by this sender.
     *
     * @return the number of packets sent by this sender
     */
    public long getSenderPacketCount()
    {
        return senderInformation.getPacketCount();
    }

    /**
     * Returns the RTPStream associated with the sender.
     *
     * @return the RTPStream associated with the sender
     */
    public RTPStream getStream()
    {
        return stream;
    }

    /**
     * Sets the RTPStream associated with the sender.
     *
     * @param stream
     *            the RTPStream associated with the sender
     */
    protected void setStream(RTPStream stream)
    {
        this.stream = stream;
    }
}
