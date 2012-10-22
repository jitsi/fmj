package net.sf.fmj.capture.test;

import java.util.*;

import javax.media.*;
import javax.media.CaptureDeviceInfo;
import javax.media.format.*;

import net.sf.fmj.ejmf.toolkit.install.*;
import net.sf.fmj.utility.*;

import com.lti.civil.*;

/**
 * Test class for capture devices.
 * 
 * @author Ken Larson
 * 
 */
public class FMJCaptureTest
{
    public static void main(String[] args) throws Exception
    {
        // for (Format f : new SimpleAWTRenderer().getSupportedInputFormats())
        // {
        // final Format civilOutputFormat = new RGBFormat(null, -1,
        // byte[].class, -1, 24, 1, 2, 3);
        // System.out.println(civilOutputFormat.matches(f) + " " + f);
        //
        // }

        PackageUtility.addContentPrefix("net.sf.fmj", false);
        PackageUtility.addProtocolPrefix("net.sf.fmj", false);

        PlugInUtility
                .registerPlugIn("net.sf.fmj.media.renderer.video.SimpleAWTRenderer");

        CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton
                .instance();
        CaptureSystem system = factory.createCaptureSystem();
        system.init();
        List list = system.getCaptureDeviceInfoList();
        for (int i = 0; i < list.size(); ++i)
        {
            com.lti.civil.CaptureDeviceInfo civilInfo = (com.lti.civil.CaptureDeviceInfo) list
                    .get(i);

            {
                // String name, MediaLocator locator, Format[] formats
                CaptureDeviceInfo jmfInfo = new CaptureDeviceInfo(
                        civilInfo.getDescription(), new MediaLocator("civil:"
                                + civilInfo.getDeviceID()),
                        new Format[] { new RGBFormat() });
                CaptureDeviceManager.addDevice(jmfInfo);
            }

        }

        final java.util.Vector vectorDevices = CaptureDeviceManager
                .getDeviceList(null);
        if (vectorDevices == null)
        {
            System.out
                    .println("CaptureDeviceManager.getDeviceList returned null");
            return;
        }
        if (vectorDevices.size() == 0)
        {
            System.out
                    .println("CaptureDeviceManager.getDeviceList returned empty list");
            return;
        }

        for (int i = 0; i < vectorDevices.size(); i++)
        {
            CaptureDeviceInfo infoCaptureDevice = (CaptureDeviceInfo) vectorDevices
                    .elementAt(i);
            System.out.println("CaptureDeviceInfo: ");
            System.out.println(infoCaptureDevice.getName());
            System.out.println(infoCaptureDevice.getLocator());
            System.out.println(infoCaptureDevice.getFormats()[0]);

            Player player;
            try
            {
                player = Manager.createRealizedPlayer(infoCaptureDevice
                        .getLocator());
            } catch (Exception e)
            {
                throw new CaptureException(e);
            }
            player.start();

        }

        system.dispose();
    }

}
