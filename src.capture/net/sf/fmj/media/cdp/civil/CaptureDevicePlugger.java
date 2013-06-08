package net.sf.fmj.media.cdp.civil;

import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.CaptureDeviceInfo;
import javax.media.format.*;

import net.sf.fmj.utility.*;

import com.lti.civil.*;

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

    private static final boolean QUERY_EXACT_FORMATS = true;

    private static Format[] getFormats(CaptureSystem system, String deviceID)
            throws CaptureException
    {
        final CaptureStream captureStream = system
                .openCaptureDeviceStream(deviceID);
        final List<com.lti.civil.VideoFormat> formatList = captureStream
                .enumVideoFormats();
        final Format[] formats = new Format[formatList.size()];

        for (int j = 0; j < formatList.size(); j++)
        {
            formats[j] = net.sf.fmj.media.protocol.civil.DataSource
                    .convertCivilFormat(formatList.get(j));
        }
        captureStream.dispose();

        return formats;
    }

    public void addCaptureDevices()
    {
        try
        {
            final CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton
                    .instance();
            final CaptureSystem system = factory.createCaptureSystem();
            system.init();
            final List list = system.getCaptureDeviceInfoList();
            for (int i = 0; i < list.size(); ++i)
            {
                final com.lti.civil.CaptureDeviceInfo civilInfo = (com.lti.civil.CaptureDeviceInfo) list
                        .get(i);

                {
                    // String name, MediaLocator locator, Format[] formats
                    // TODO: more accurate format
                    // TODO: don't add if already there.

                    // CaptureDeviceInfo.getName() will return a String which
                    // can be used in CaptureDeviceManager.getDevice(String
                    // captureDeviceName)
                    // thats how jmf does it
                    // the name is constructed <library name>:<capture device
                    // name>:<index>
                    // we will have unique names ( important for getDevice ) and
                    // we can fetch a human readable name easily
                    // simply split the String from getName, use the second
                    // substring, you can display this name on screen

                    final CaptureDeviceInfo jmfInfo;
                    if (QUERY_EXACT_FORMATS)
                    {
                        final Format[] formats = getFormats(system,
                                civilInfo.getDeviceID());
                        jmfInfo = new CaptureDeviceInfo("civil:"
                                + civilInfo.getDescription() + ":" + i,
                                new MediaLocator("civil:"
                                        + civilInfo.getDeviceID()), formats);
                    } else
                    {
                        jmfInfo = new CaptureDeviceInfo("civil:"
                                + civilInfo.getDescription() + ":" + i,
                                new MediaLocator("civil:"
                                        + civilInfo.getDeviceID()),
                                new Format[] { new RGBFormat() });
                    }

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
        } catch (CaptureException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }
    }
}
