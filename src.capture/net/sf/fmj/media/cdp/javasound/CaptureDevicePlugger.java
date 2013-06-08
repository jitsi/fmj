package net.sf.fmj.media.cdp.javasound;

import java.util.logging.*;

import javax.media.*;
import javax.sound.sampled.*;

import net.sf.fmj.utility.*;

/**
 * Dynamically adds CaptureDeviceInfo to the CaptureDeviceManager. Does not
 * commit.
 *
 * @author Ken Larson
 *
 */
public class CaptureDevicePlugger
{
    private static final Logger logger = LoggerSingleton.logger;

    public void addCaptureDevices()
    {
        int index = 0;

        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (int i = 0; i < mixerInfo.length; i++)
        {
            Mixer mixer = AudioSystem.getMixer(mixerInfo[i]);

            // if sample rate is not specified, we leave it unknown (unkown
            // means it supports a variety of sample rates ) 0 to 96kHz,
            // if we add only a small subset, we miss some, building the graph
            // is faster with leaving it as it is
            Format[] formats = net.sf.fmj.media.protocol.javasound.DataSource
                    .querySupportedFormats(i);

            if ((null != formats) && (formats.length > 0))
            {
                CaptureDeviceInfo jmfInfo = new CaptureDeviceInfo("javasound:"
                        + mixerInfo[i].getName() + ":" + index,
                        new MediaLocator("javasound:#" + i), formats);
                index++;

                if (CaptureDeviceManager.getDevice(jmfInfo.getName()) == null)
                {
                    CaptureDeviceManager.addDevice(jmfInfo);
                    logger.fine("CaptureDevicePlugger: Added "
                            + jmfInfo.getLocator());
                } else
                {
                    logger.fine("CaptureDevicePlugger: Already present, skipping "
                            + jmfInfo.getLocator());
                }
            }
        }
    }
}
