/*
 * @(#)RTCPReport.java
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
import java.util.*;

import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;

/**
 * Represents an RTCP Report
 * 
 * @author Andrew G D Rowley
 * @author Christian Vincenot
 * @version 1-1-alpha3
 */
public abstract class RTCPReport implements Report
{
    // The participant that sent the report
    protected Participant participant;

    // The header of the report
    protected RTCPHeader header;

    // The vector of feedback reports in this report
    protected Vector feedbackReports = new Vector();

    // The source descriptions in the report
    protected Vector sourceDescriptions = new Vector();

    // The number of bytes of SDES packets read
    protected int sdesBytes = 0;

    // The CNAME of the source
    private String cName = null;

    // True if this is a bye event
    private boolean isBye = false;

    // The reason for leaving if this is a bye packet
    private String byeReason = "";

    // The ssrc of the report
    private long ssrc = 0;

    /**
     * Creates a new <tt>RTCPReport</tt> instance.
     * 
     * @param data the data of the report
     * @param offset the offset in the data where the report starts
     * @param length the length of the report in the data
     * @throws IOException if an I/O-related error is encountered
     */
    public RTCPReport(byte data[], int offset, int length) throws IOException
    {
        header = new RTCPHeader(data, offset, length);

        if (header.getPadding() == 1)
            throw new IOException("First packet has padding");
        else if (((header.getLength() + 1) * 4) > length)
            throw new IOException("Invalid Length");

        ssrc = header.getSsrc();
    }

    /**
     * Returns the reason for the bye
     * 
     * @return the reason announced for this BYE packet
     */
    public String getByeReason()
    {
        return byeReason;
    }

    /**
     * Gets the cName of the source of the report
     * 
     * @return the cName, or null if none sent
     */
    public String getCName()
    {
        return cName;
    }

    /**
     * Returns the feedback reports for this RTCP report.
     * 
     * @return the feedback reports for this RTCP report
     */
    public Vector getFeedbackReports()
    {
        return feedbackReports;
    }

    /**
     * Returns the participant linked with this RTCP report
     * 
     * @return the participant identified previously as being linked with this
     *         RTCP report
     */
    public Participant getParticipant()
    {
        return participant;
    }

    /**
     * Returns the sources descriptions (SDES) in this RTCP report.
     * 
     * @return the sources descriptions (SDES) in this RTCP report
     */
    public Vector getSourceDescription()
    {
        return sourceDescriptions;
    }

    /**
     * Returns the SSRC announced in this report.
     * 
     * @return SSRC in this report
     */
    public long getSSRC()
    {
        return ssrc;
    }

    /**
     * Returns true if a bye packet was added to the report
     * 
     * @return true if a BYE packet was added to the report
     */
    public boolean isByePacket()
    {
        return isBye;
    }

    /**
     * Reads and handles the BYE part of an RTCP report.
     * 
     * @param data
     *            the raw data in which the packet is contained
     * @param offset
     *            the offset where the BYE starts
     * @param length
     *            the length of the report
     * @throws java.io.IOException
     *             I/O Exception
     */
    protected void readBye(byte data[], int offset, int length)
            throws IOException
    {
        if (length > 0)
        {
            RTCPHeader sdesHeader = new RTCPHeader(data, offset, length);
            if (sdesHeader.getPacketType() == RTCPPacket.BYE)
            {
                isBye = true;
                if (((length + 1) * 4) > RTCPHeader.SIZE)
                {
                    int len = data[offset + RTCPHeader.SIZE] & 0xFF;
                    if ((len < (length - RTCPHeader.SIZE)) && (len > 0))
                    {
                        byeReason = new String(data, offset + RTCPHeader.SIZE
                                + 1, len);
                    }
                }
            }
        }
    }

    /**
     * Reads feedback reports from the data
     * 
     * @param data
     *            The data to read the feedback reports from
     * @param offset
     *            The offset into the data where the reports start
     * @param length
     *            The length of the data
     * @throws IOException
     *             I/O Exception
     */
    protected void readFeedbackReports(byte data[], int offset, int length)
            throws IOException
    {
        for (int i = 0; i < header.getReceptionCount(); i++)
        {
            RTCPFeedback feedback = new RTCPFeedback(data, offset, length);
            feedbackReports.add(feedback);
            offset += RTCPFeedback.SIZE;
        }
    }

    /**
     * Reads the source description from the data
     * 
     * @param data
     *            The data to read the source description from
     * @param offset
     *            The offset into the data where the SDES packet starts
     * @param length
     *            The length of the data
     * @throws IOException
     *             I/O Exception
     */
    protected void readSourceDescription(byte data[], int offset, int length)
            throws IOException
    {
        if (length > 0)
        {
            // Only do this if there is an SDES header
            RTCPHeader sdesHeader = new RTCPHeader(data, offset, length);

            if (sdesHeader.getPacketType() == RTCPPacket.SDES)
            {
                ssrc = sdesHeader.getSsrc();
                sdesBytes = (sdesHeader.getLength() + 1) * 4;
                DataInputStream stream = new DataInputStream(
                        new ByteArrayInputStream(data,
                                offset + RTCPHeader.SIZE, length));
                int type = SourceDescription.SOURCE_DESC_CNAME;
                while (type != 0)
                {
                    type = stream.readUnsignedByte();
                    if (type != 0)
                    {
                        int len = stream.readUnsignedByte();
                        byte[] desc = new byte[len];
                        stream.readFully(desc);
                        String descStr = new String(desc, "UTF-8");
                        SourceDescription description = new SourceDescription(
                                type, descStr, 0, false);
                        sourceDescriptions.add(description);
                        if (type == SourceDescription.SOURCE_DESC_CNAME)
                        {
                            cName = descStr;
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets the participant linked with this RTCP report.
     * 
     * @param participant
     *            the participant identified as linked with this RTCP report
     */
    protected void setParticipant(RTPParticipant participant)
    {
        this.participant = participant;
        Vector streams = participant.getStreams();
        if (streams.size() == 0)
        {
            Vector sdes = participant.getSourceDescription();
            for (int i = 0; i < sdes.size(); i++)
            {
                SourceDescription sdesItem = (SourceDescription) sdes.get(i);
                participant.addSourceDescription(sdesItem);
            }
        }
    }

}
