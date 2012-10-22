package net.sf.fmj.rtp;

import java.awt.*;
import java.net.*;
import java.util.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.*;

import net.sf.fmj.media.*;

public class rtpvideo
{
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Usage: rtpvideo <targetIP> <targetPort>");
            System.exit(0);
        }

        try
        {
            RegistryDefaults.setDefaultFlags(RegistryDefaults.FMJ);

            // create a clean registry
            RegistryDefaults.unRegisterAll(RegistryDefaults.ALL);
            RegistryDefaults.registerAll(RegistryDefaults.FMJ);

            // remove all capture devices
            Vector deviceList = (Vector) CaptureDeviceManager.getDeviceList(
                    null).clone();
            for (int i = 0; i < deviceList.size(); i++)
            {
                CaptureDeviceInfo cdi = (CaptureDeviceInfo) deviceList
                        .elementAt(i);
                CaptureDeviceManager.removeDevice(cdi);
            }

            // update capture device list
            new net.sf.fmj.media.cdp.civil.CaptureDevicePlugger()
                    .addCaptureDevices();
            PlugInManager.commit();

            deviceList = (Vector) CaptureDeviceManager.getDeviceList(null)
                    .clone();
            if ((null == deviceList) || (deviceList.size() == 0))
            {
                System.out.println("### ERROR found no video capture device");
                System.exit(0);
            }

            CaptureDeviceInfo captureDeviceInfo = (CaptureDeviceInfo) deviceList
                    .elementAt(0);
            System.out.println("### using " + captureDeviceInfo.getName());
            System.out.println("### locator " + captureDeviceInfo.getLocator());

            javax.media.protocol.DataSource dataSource = javax.media.Manager
                    .createDataSource(new javax.media.MediaLocator(
                            captureDeviceInfo.getLocator().toString()));
            System.out.println("### created datasource "
                    + dataSource.getClass().getName());

            // set the video capture format
            javax.media.control.FormatControl[] formatControls = ((javax.media.protocol.CaptureDevice) dataSource)
                    .getFormatControls();
            System.out.println("got format control "
                    + formatControls[0].getClass().getName());
            System.out.println("current format is "
                    + formatControls[0].getFormat());
            formatControls[0].setFormat(new VideoFormat(null, new Dimension(
                    176, 144), -1, null, -1.0f));
            // formatControls[0].setFormat(new VideoFormat(null, new
            // Dimension(352, 288), -1, null, -1.0f));
            // formatControls[0].setFormat(new VideoFormat(null, new
            // Dimension(320, 240), -1, null, -1.0f));
            // formatControls[0].setFormat(new VideoFormat(null, new
            // Dimension(640, 480), -1, null, -1.0f));
            System.out.println("format was set to "
                    + formatControls[0].getFormat());

            FrameRateControl frameRateControl = null;

            // adujst recording buffer ( to adjust latency )
            dataSource.stop();
            Object[] controls = dataSource.getControls();
            for (int i = 0; i < controls.length; i++)
            {
                String className = controls[i].getClass().getName();
                if (-1 != className.indexOf("JitterBufferControl"))
                {
                    javax.media.control.BufferControl bc = (javax.media.control.BufferControl) controls[i];
                    System.out.println("### current jitter buffer length is "
                            + bc.getBufferLength() + " buckets");
                    bc.setBufferLength(1);
                    System.out.println("### jitter buffer was set to "
                            + bc.getBufferLength() + " buckets");
                } else if (-1 != className.indexOf("VideoFrameRateControl"))
                {
                    frameRateControl = (javax.media.control.FrameRateControl) controls[i];
                }
            }
            dataSource.start();

            // create processor
            javax.media.Processor processor = javax.media.Manager
                    .createProcessor(dataSource);
            System.out.println("### created processor "
                    + processor.getClass().getName());

            processor.configure();
            for (int idx = 0; idx < 100; idx++)
            {
                if (processor.getState() == Processor.Configured)
                {
                    break;
                }
                Thread.sleep(100);
            }
            System.out.println("### processor state " + processor.getState());

            processor
                    .setContentDescriptor(new javax.media.protocol.ContentDescriptor(
                            ContentDescriptor.RAW_RTP));

            javax.media.control.TrackControl[] tracks = processor
                    .getTrackControls();
            tracks[0].setFormat(new javax.media.format.VideoFormat(
                    javax.media.format.VideoFormat.JPEG_RTP));
            System.out.println("### tracks[0] format is now "
                    + tracks[0].getFormat());

            processor.realize();
            for (int idx = 0; idx < 100; idx++)
            {
                if (processor.getState() == Controller.Realized)
                {
                    break;
                }
                Thread.sleep(100);
            }
            System.out.println("### processor state " + processor.getState());

            javax.media.protocol.DataSource dataOutput = processor
                    .getDataOutput();
            System.out.println("### processor data output "
                    + dataOutput.getClass().getName());

            // BitRateControl
            BitRateControl bitrateControl = null;

            Object[] controls2 = dataOutput.getControls();
            for (int i = 0; i < controls2.length; i++)
            {
                if (controls2[i] instanceof BitRateControl)
                {
                    bitrateControl = (BitRateControl) controls2[i];
                    System.out.println("### found bitrate control "
                            + bitrateControl.getClass());
                    break;
                }
            }

            // PacketSizeControl
            Object[] controls3 = processor.getControls();
            for (int i = 0; i < controls3.length; i++)
            {
                if (controls3[i] instanceof PacketSizeControl)
                {
                    PacketSizeControl psc = (PacketSizeControl) controls3[i];
                    System.out.println("### current packetsize is "
                            + psc.getPacketSize() + " bytes");
                    psc.setPacketSize(1400);
                    System.out.println("### packetsize was set to "
                            + psc.getPacketSize() + " bytes");
                    break;
                }
            }

            // QualityControl
            QualityControl qc = null;
            Object[] controls4 = processor.getControls();
            for (int i = 0; i < controls4.length; i++)
            {
                if (controls4[i] instanceof QualityControl)
                {
                    qc = (QualityControl) controls4[i];
                    System.out.println("### current quality is "
                            + qc.getQuality());
                    qc.setQuality(0.5f);
                    System.out.println("### quality was set to "
                            + qc.getQuality());
                    break;
                }
            }

            // FrameRateControl
            Object[] controls5 = processor.getControls();
            for (int i = 0; i < controls5.length; i++)
            {
                if (controls5[i] instanceof FrameRateControl)
                {
                    FrameRateControl frc = (FrameRateControl) controls5[i];
                    System.out.println("### current framerate is "
                            + frc.getFrameRate());
                    frc.setFrameRate(5.0f);
                    System.out.println("### framerate was set to "
                            + frc.getFrameRate());
                    break;
                }
            }

            // here you can reduce the size of video ( over RTP ) if format is
            // not set in the processor, capture device format will be used
            // FormatControl
            /*
             * Object[] controls6 = processor.getControls(); for (int i=0;
             * i<controls6.length; i++) { if ( controls6[i] instanceof
             * FormatControl ) { FormatControl fc = (FormatControl)controls6[i];
             * System.out.println("### current format is " + fc.getFormat());
             * fc.setFormat(new
             * javax.media.format.VideoFormat(javax.media.format
             * .VideoFormat.JPEG_RTP, new Dimension(128, 96), -1, null, -1));
             * //fc.setFormat(new
             * javax.media.format.VideoFormat(javax.media.format
             * .VideoFormat.JPEG_RTP, new Dimension(640, 480), -1, null, -1));
             * System.out.println("### format was set to " + fc.getFormat());
             * break; } }
             */

            Object[] pcontrols = processor.getControls();
            for (int i = 0; i < pcontrols.length; i++)
            {
                System.out.println(">> processor control " + i + " "
                        + pcontrols[i]);
            }

            javax.media.rtp.RTPManager rtpManager = javax.media.rtp.RTPManager
                    .newInstance();

            javax.media.rtp.SessionAddress local = new javax.media.rtp.SessionAddress(
                    InetAddress.getLocalHost(), Integer.valueOf(args[1])
                            .intValue());
            javax.media.rtp.SessionAddress target = new javax.media.rtp.SessionAddress(
                    InetAddress.getByName(args[0]), Integer.valueOf(args[1])
                            .intValue());

            rtpManager.initialize(local);
            rtpManager.addTarget(target);

            javax.media.rtp.SendStream sendStream = rtpManager
                    .createSendStream(dataOutput, 0);
            sendStream.start();
            System.out.println("### sendStream=" + sendStream.getClass());

            processor.start();

            float quality = 0.1f;

            System.out.println("\n>>>>>>  TRANSMITTING VIDEO NOW");
            while (2 > 1)
            {
                Thread.sleep(1000);

                int fps = -1;
                if (null != frameRateControl)
                {
                    fps = (int) frameRateControl.getFrameRate();
                }

                TransmissionStats stats = sendStream
                        .getSourceTransmissionStats();
                System.out.println("rtp video send: bitrate="
                        + bitrateControl.getBitRate() + ", captureFPS=" + fps
                        + " (pdu=" + stats.getPDUTransmitted() + " sent="
                        + stats.getBytesTransmitted() + " bytes)");

                /*
                 * if ( stats.getPDUTransmitted() > 100 ) { processor.close();
                 * sendStream.close(); dataSource.disconnect();
                 * 
                 * Thread.sleep(5000); System.out.println("rtp send: bitrate=" +
                 * bitrateControl.getBitRate() + ", captureFPS=" + fps +
                 * " (pdu=" + stats.getPDUTransmitted() + " sent=" +
                 * stats.getBytesTransmitted() + " bytes)"); System.exit(0); }
                 */
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
