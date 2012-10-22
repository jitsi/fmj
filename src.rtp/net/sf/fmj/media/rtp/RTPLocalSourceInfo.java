package net.sf.fmj.media.rtp;

import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;

public class RTPLocalSourceInfo extends RTPSourceInfo implements
        LocalParticipant
{
    public RTPLocalSourceInfo(String cname, RTPSourceInfoCache sic)
    {
        super(cname, sic);
    }

    public void setSourceDescription(SourceDescription sdeslist[])
    {
        super.sic.ssrccache.ourssrc.setSourceDescription(sdeslist);
    }
}
