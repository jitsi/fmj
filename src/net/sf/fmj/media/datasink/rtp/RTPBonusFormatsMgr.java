package net.sf.fmj.media.datasink.rtp;

import javax.media.format.*;
import javax.media.rtp.*;

import net.sf.fmj.media.*;

/**
 * Manager for nonstandard ("bonus") RTP formats supported by FMJ. No way to add
 * extra formats globally and for all sessions, or to commit, with JMF. So we
 * will just call this any time we initialize a new RTPManager instance.
 * Actually, under JMF, any new formats added to a specific RTPManager instance
 * are added to a global static list, so they take effect for future RTPManager
 * instances. FMJ does not implement this, they have to be added to every
 * instance.
 *
 * See http://archives.java.sun.com/cgi-bin/wa?A2=ind0107&L=jmf-interest&P=33617
 *
 *
 * @author Ken Larson
 *
 */
public class RTPBonusFormatsMgr
{
    // public static final int FIRST_BONUS_FORMAT = 100;
    public static final int ALAW_RTP_INDEX = 8; // defined as such in
                                                // http://www.networksorcery.com/enp/protocol/rtp.htm
    public static final int SPEEX_RTP_INDEX = 110; // compatible with
                                                   // SIP-Communicator
    public static final int ILBC_RTP_INDEX = 97; // compatible with
                                                 // SIP-Communicator

    public static void addBonusFormats(RTPManager mgr)
    {
        mgr.addFormat(new AudioFormat(BonusAudioFormatEncodings.ALAW_RTP, 8000,
                8, 1, -1, AudioFormat.SIGNED), ALAW_RTP_INDEX);

        // see
        // net.java.sip.communicator.impl.media.codec.audio.speex.JavaDecoder.
        // only relevant if this encoder/decoder pair is available.
        mgr.addFormat(new AudioFormat(BonusAudioFormatEncodings.SPEEX_RTP,
                8000, 8, 1, -1, AudioFormat.SIGNED // isSigned());
                ), SPEEX_RTP_INDEX);

        // ditto, for
        // net.java.sip.communicator.impl.media.codec.audio.ilbc.JavaDecoder:
        mgr.addFormat(new AudioFormat(BonusAudioFormatEncodings.ILBC_RTP,
                8000.0, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED),
                ILBC_RTP_INDEX);

    }
}
