package net.sf.fmj.media.rtp;

import javax.media.rtp.*;

public class RTPRemoteSourceInfo extends RTPSourceInfo implements
        RemoteParticipant
{
    public RTPRemoteSourceInfo(String cname, RTPSourceInfoCache sic)
    {
        super(cname, sic);
    }
}
