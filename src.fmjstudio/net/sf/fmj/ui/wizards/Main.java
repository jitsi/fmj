package net.sf.fmj.ui.wizards;

import javax.media.format.*;

/**
 *
 * @author Ken Larson
 *
 */
public class Main
{
    public static void main(String[] args)
    {
        RTPTransmitWizardConfig config = new RTPTransmitWizardConfig();
        // set defaults:
        config.url = "file://samplemedia/gulp2.wav";
        config.trackConfigs = new TrackConfig[] { new TrackConfig(true,
                new AudioFormat(AudioFormat.ULAW_RTP, 8000.0, 8, 1,
                        AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED)) };
        // try
        // {
        config.destUrl = "rtp://192.168.1.4:8000/audio/16";// RTPUrlParser.parse("rtp://192.168.1.4:8000/audio/16");
        // } catch (RTPUrlParserException e)
        // {
        // throw new RuntimeException(e);
        // }

        new RTPTransmitWizard(null, config).run();

    }

}
