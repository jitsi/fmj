package net.sf.fmj.media;

/**
 * Audio format encodings in FMJ but not JMF.
 *
 * @author Ken Larson
 *
 */
public class BonusAudioFormatEncodings
{
    // TODO: add the MP3/OGG ones used by JavaSoundCodec and JavaSoundRenderer.

    public static final String ALAW_RTP = "ALAW/rtp";
    public static final String SPEEX_RTP = "speex/rtp";
    public static final String ILBC_RTP = "ilbc/rtp";

    public static final String[] ALL = new String[] { ALAW_RTP, SPEEX_RTP,
            ILBC_RTP };
}
