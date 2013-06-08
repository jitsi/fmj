package com.sun.media.protocol.javasound;

import javax.media.*;

public class JavaSoundSourceStream
{
    public static CaptureDeviceInfo[] listCaptureDeviceInfo()
    {
        // mgodehardt: disabled , dunno who is using this class
        /*
         * final Format[] formats = DataSource.querySupportedFormats();
         *
         * final CaptureDeviceInfo jmfInfo = new CaptureDeviceInfo("JavaSound",
         * new MediaLocator("javasound:" + "//"), formats);
         *
         * return new CaptureDeviceInfo[] {jmfInfo};
         */

        return null;
    }
}
