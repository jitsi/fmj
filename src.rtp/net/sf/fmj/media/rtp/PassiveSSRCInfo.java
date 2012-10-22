package net.sf.fmj.media.rtp;

import javax.media.rtp.rtcp.*;

public class PassiveSSRCInfo extends SSRCInfo implements ReceiverReport
{
    PassiveSSRCInfo(SSRCCache cache, int ssrc)
    {
        super(cache, ssrc);
    }

    PassiveSSRCInfo(SSRCInfo info)
    {
        super(info);
    }
}
