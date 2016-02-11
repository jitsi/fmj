package net.sf.fmj.media.rtp;

import java.awt.*;
import java.util.*;

import javax.media.*;
import javax.media.rtp.*;

import net.sf.fmj.media.util.*;

public abstract class RTPControlImpl implements RTPControl, RTPInfo
{
    String cname;
    Hashtable<Integer,Format> codeclist;
    int rtptime;
    int seqno;
    int payload;
    String codec;
    Format currentformat;
    SSRCInfo stream;

    public RTPControlImpl()
    {
        cname = null;
        rtptime = 0;
        seqno = 0;
        payload = -1;
        codec = "";
        currentformat = null;
        stream = null;
        codeclist = new Hashtable<Integer,Format>(5);
    }

    public void addFormat(Format info, int payload)
    {
        codeclist.put(payload, info);
    }

    public abstract String getCNAME();

    public Component getControlComponent()
    {
        return null;
    }

    public Format getFormat()
    {
        return currentformat;
    }

    public Format getFormat(int payload)
    {
        return codeclist.get(payload);
    }

    public Format[] getFormatList()
    {
        Format infolist[] = new Format[codeclist.size()];
        int i = 0;
        for (Enumeration<Format> e = codeclist.elements(); e.hasMoreElements();)
        {
            Format f = e.nextElement();
            infolist[i++] = (Format) f.clone();
        }

        return infolist;
    }

    public GlobalReceptionStats getGlobalStats()
    {
        return null;
    }

    public ReceptionStats getReceptionStats()
    {
        if (stream == null)
        {
            return null;
        } else
        {
            RecvSSRCInfo recvstream = (RecvSSRCInfo) stream;
            return recvstream.getSourceReceptionStats();
        }
    }

    public abstract int getSSRC();

    public void setRTPInfo(int rtptime, int seqno)
    {
        this.rtptime = rtptime;
        this.seqno = seqno;
    }

    @Override
    public String toString()
    {
        String s = "\n\tRTPTime is " + rtptime + "\n\tSeqno is " + seqno;
        if (codeclist != null)
            s = s + "\n\tCodecInfo is " + codeclist.toString();
        else
            s = s + "\n\tcodeclist is null";
        return s;
    }
}
