/*
 * @(#)RTPParticipant.java
 * Created: 26-Oct-2005
 * Version: 1-1-alpha3
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 * Andrew G D Rowley
 * Modified by Christian Vincenot <sipcom@cyberspace7.net>
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

import java.util.*;

import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;

/**
 * Represents an RTP participant
 *
 * @author Andrew G D Rowley
 * @author Christian Vincenot
 * @version 1-1-alpha3
 */
public class RTPParticipant implements Participant
{
    // The streams of the participant
    private Vector streams = new Vector();

    // The RTCP reports of the participant
    private HashMap rtcpReports = new HashMap();

    // The CNAME of the particpant
    private String cName = "";

    // A vector of source description objects
    protected HashMap sourceDescriptions = new HashMap();

    // True if the participant is active
    private boolean active = false;

    // The size of the sdes elements combined in SDES format
    private int sdesSize = 0;

    // Time on which the last report from this participant was received
    // We set it to the current to avoid getting directly timed out
    protected long lastReportTime = System.currentTimeMillis();

    /**
     * Creates a new RTPParticipant
     *
     * @param cName
     *            the RTP CNAME of this participant.
     */
    public RTPParticipant(String cName)
    {
        this.cName = cName;
        addSourceDescription(new SourceDescription(
                SourceDescription.SOURCE_DESC_CNAME, cName, 1, false));
        addSourceDescription(new SourceDescription(
                SourceDescription.SOURCE_DESC_NAME, cName, 1, false));
    }

    /**
     * Adds an RTCP Report for this participant
     *
     * @param report
     *            The report to add
     */
    public void addReport(Report report)
    {
        lastReportTime = System.currentTimeMillis();
        rtcpReports.put(new Long(report.getSSRC()), report);
        Vector sdes = report.getSourceDescription();
        for (int i = 0; i < sdes.size(); i++)
        {
            addSourceDescription((SourceDescription) sdes.get(i));
        }

        if ((streams.size() == 0) && (report instanceof RTCPReport))
        {
            ((RTCPReport) report).sourceDescriptions = new Vector(
                    sourceDescriptions.values());
        }
    }

    /**
     * Adds a source description item to the participant
     *
     * @param sdes
     *            The SDES item to add
     */
    protected void addSourceDescription(SourceDescription sdes)
    {
        SourceDescription oldSdes = (SourceDescription) sourceDescriptions
                .get(new Integer(sdes.getType()));
        if (oldSdes != null)
        {
            sdesSize -= oldSdes.getDescription().length();
            sdesSize -= 2;
        }
        sourceDescriptions.put(new Integer(sdes.getType()), sdes);
        sdesSize += 2;
        sdesSize += sdes.getDescription().length();
    }

    /**
     * Adds a stream to the participant
     *
     * @param stream
     *            stream to associate with this participant
     */
    protected void addStream(RTPStream stream)
    {
        streams.add(stream);
    }

    /**
     * Returns this participant's RTP CNAME.
     *
     * @return this participant's RTP CNAME
     */
    public String getCNAME()
    {
        return cName;
    }

    /**
     * Returns this participant's last report time, which is the last time he's
     * sent us a report.
     *
     * @return the participant's last report time
     */
    public long getLastReportTime()
    {
        return lastReportTime;
    }

    /**
     * Returns the reports associated with this participant.
     *
     * @return the reports associated with this participant
     */
    public Vector getReports()
    {
        return new Vector(rtcpReports.values());
    }

    /**
     * Returns the number of bytes of sdes that this participant requires.
     *
     * @return the number of bytes of sdes that this participant requires
     */
    public int getSdesSize()
    {
        return sdesSize;
    }

    /**
     * Returns the sources descriptions (SDES) associated with this participant.
     *
     * @return the sources descriptions (SDES) associated with this participant
     */
    public Vector getSourceDescription()
    {
        return new Vector(sourceDescriptions.values());
    }

    /**
     * Returns the streams associated with this participant.
     *
     * @return the streams associated with this participant
     */
    public Vector getStreams()
    {
        return streams;
    }

    /**
     * Returns true if the participant is active
     *
     * @return true if the participant is active
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Removes the specified stream from this participant's associated streams
     * list.
     *
     * @param stream
     *            the stream to erase
     */
    protected void removeStream(RTPStream stream)
    {
        streams.remove(stream);
    }

    /**
     * Sets the participant active or inactive
     *
     * @param active
     *            Activity of the participant, true if active
     */
    protected void setActive(boolean active)
    {
        this.active = active;
    }

}
