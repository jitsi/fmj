package net.sf.fmj.media.multiplexer;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.media.rtp.*;

public class RTPSyncBufferMux extends RawSyncBufferMux
{
    FormatInfo rtpFormats = new FormatInfo();

    public RTPSyncBufferMux()
    {
        super();
        supported = new ContentDescriptor[1];
        supported[0] = new ContentDescriptor(ContentDescriptor.RAW_RTP);
        monoIncrTime = true;
    }

    /**
     * Returns a descriptive name for the plug-in. This is a user readable
     * string.
     */
    @Override
    public String getName()
    {
        return "RTP Sync Buffer Multiplexer";
    }

    @Override
    public Format setInputFormat(Format input, int trackID)
    {
        // Screen for the supported formats.
        if (!RTPSessionMgr.formatSupported(input))
            return null;

        return super.setInputFormat(input, trackID);
    }
}
