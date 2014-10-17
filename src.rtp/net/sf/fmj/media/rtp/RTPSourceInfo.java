package net.sf.fmj.media.rtp;

import java.util.*;

import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;

public abstract class RTPSourceInfo implements Participant
{
    RTPSourceInfoCache sic;
    private SSRCInfo ssrc[];
    private SourceDescription cname;

    RTPSourceInfo(String cname, RTPSourceInfoCache sic)
    {
        this.cname = new SourceDescription(1, cname, 0, false);
        this.sic = sic;
        ssrc = new SSRCInfo[0];
    }

    synchronized void addSSRC(SSRCInfo ssrcinfo)
    {
        for (int i = 0; i < ssrc.length; i++)
            if (ssrc[i] == ssrcinfo)
                return;

        System.arraycopy(ssrc, 0, ssrc = new SSRCInfo[ssrc.length + 1], 0,
                ssrc.length - 1);
        ssrc[ssrc.length - 1] = ssrcinfo;
    }

    public String getCNAME()
    {
        return cname.getDescription();
    }

    SourceDescription getCNAMESDES()
    {
        return cname;
    }

    public Vector getReports()
    {
        Vector reportlist = new Vector();
        for (int i = 0; i < ssrc.length; i++)
            reportlist.addElement(ssrc[i]);

        reportlist.trimToSize();
        return reportlist;
    }

    public Vector getSourceDescription()
    {
        Vector sdeslist = null;
        if (ssrc.length == 0)
        {
            sdeslist = new Vector(0);
            return sdeslist;
        } else
        {
            sdeslist = ssrc[0].getSourceDescription();
            return sdeslist;
        }
    }

    RTPStream getSSRCStream(long filterssrc)
    {
        for (int i = 0; i < ssrc.length; i++)
            if ((ssrc[i] instanceof RTPStream)
                    && ssrc[i].ssrc == (int) filterssrc)
                return (RTPStream) ssrc[i];

        return null;
    }

    int getStreamCount()
    {
        return ssrc.length;
    }

    public Vector getStreams()
    {
        Vector recvstreams = new Vector();
        for (int i = 0; i < ssrc.length; i++)
            if (ssrc[i].isActive())
                recvstreams.addElement(ssrc[i]);

        recvstreams.trimToSize();
        return recvstreams;
    }

    synchronized void removeSSRC(SSRCInfo ssrcinfo)
    {
        if (ssrcinfo.dsource != null)
            sic.ssrccache.sm.removeDataSource(ssrcinfo.dsource);

        for (int i = 0; i < ssrc.length; i++)
        {
            if (ssrc[i] == ssrcinfo)
            {
                ssrc[i] = ssrc[ssrc.length - 1];
                System.arraycopy(
                        ssrc, 0,
                        ssrc = new SSRCInfo[ssrc.length - 1], 0,
                        ssrc.length);
                break;
            }
        }

        if (ssrc.length == 0)
            sic.remove(cname.getDescription());
    }
}
