package net.sf.fmj.rtp;

import java.net.*;
import java.util.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;
import javax.media.rtp.*;

import net.sf.fmj.media.*;

// mgodehardt: Explanation how data from microphone flows thru the fmj classes
//
// net.sf.fmj.media.protocol.javasound.DataSource         CAPTURE DEVICE, read gets called from next class thru notifier
// net.sf.fmj.media.parser.RawPushBufferParser            DEMULTIPLEXER
// net.sf.fmj.media.codec.audio.RateConverter             CODEC, process(), converts input to output ( some rate conversion )
// net.sf.fmj.media.codec.audio.ulaw.Encoder              CODEC, process(), converts input to output (output is ULAW 8000Hz)
// net.sf.fmj.media.codec.audio.ulaw.Packetizer           CODEC, process(), converts input to output ( make packets of 160 byte )
// net.sf.fmj.media.multiplexer.RTPSyncBufferMux          MULTIPLEXER
// net.sf.fmj.media.rtp.RTPSendStream                     is sending data over network
//

public class rtpaudio
{
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Usage: rtpaudio <targetIP> <targetPort>");
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
            new net.sf.fmj.media.cdp.javasound.CaptureDevicePlugger()
                    .addCaptureDevices();
            PlugInManager.commit();

            deviceList = (Vector) CaptureDeviceManager.getDeviceList(null)
                    .clone();
            if ((null == deviceList) || (deviceList.size() == 0))
            {
                System.out.println("### ERROR found no audio capture device");
                System.exit(0);
            }

            // enumerate all codec
            Vector codecList = PlugInManager.getPlugInList(null, null,
                    PlugInManager.CODEC);
            System.out.println("found " + codecList.size() + " codec");
            for (int i = 0; i < codecList.size(); i++)
            {
                String aCodecClass = (String) codecList.elementAt(i);
                System.out.println("# " + (i + 1) + " " + aCodecClass);
            }

            // fetch first available audio capture device
            deviceList = (Vector) CaptureDeviceManager.getDeviceList(null)
                    .clone();
            CaptureDeviceInfo captureDeviceInfo = (CaptureDeviceInfo) deviceList
                    .elementAt(0);
            System.out.println("### using " + captureDeviceInfo.getName());
            System.out.println("### locator " + captureDeviceInfo.getLocator());

            javax.media.protocol.DataSource dataSource = javax.media.Manager
                    .createDataSource(new javax.media.MediaLocator(
                            captureDeviceInfo.getLocator().toString()));
            // javax.media.protocol.DataSource dataSource =
            // javax.media.Manager.createDataSource(new
            // javax.media.MediaLocator("javasound://"));
            System.out.println("### created datasource "
                    + dataSource.getClass().getName());

            javax.media.control.FormatControl[] formatControls = ((javax.media.protocol.CaptureDevice) dataSource)
                    .getFormatControls();
            System.out.println("got format control "
                    + formatControls[0].getClass().getName());

            System.out.println("current format is "
                    + formatControls[0].getFormat());

            // set audio capture format
            javax.media.Format[] formats = formatControls[0]
                    .getSupportedFormats();
            for (int i = 0; i < formats.length; i++)
            {
                javax.media.format.AudioFormat af = (javax.media.format.AudioFormat) formats[i];
                if ((af.getChannels() == 1) && (af.getSampleSizeInBits() == 16))
                {
                    if (af.getSampleRate() == Format.NOT_SPECIFIED)
                    {
                        javax.media.format.AudioFormat newAudioFormat = new javax.media.format.AudioFormat(
                                af.getEncoding(), 8000.0f,
                                javax.media.Format.NOT_SPECIFIED,
                                javax.media.Format.NOT_SPECIFIED);
                        // javax.media.format.AudioFormat newAudioFormat = new
                        // javax.media.format.AudioFormat(af.getEncoding(),
                        // 44100.0f, javax.media.Format.NOT_SPECIFIED,
                        // javax.media.Format.NOT_SPECIFIED);
                        formatControls[0].setFormat(newAudioFormat
                                .intersects(af));
                        break;
                    }
                }
            }
            System.out.println("current format is now "
                    + formatControls[0].getFormat());

            FrameProcessingControl fpc = null;

            // adujst recording buffer ( to adjust latency )
            dataSource.stop();
            Object[] controls = dataSource.getControls();
            for (int i = 0; i < controls.length; i++)
            {
                String className = controls[i].getClass().getName();
                if (-1 != className.indexOf("JavaSoundBufferControl"))
                {
                    javax.media.control.BufferControl bc = (javax.media.control.BufferControl) controls[i];
                    System.out
                            .println("### current javasound buffer length is "
                                    + bc.getBufferLength() + " ms");
                    bc.setBufferLength(40);
                    System.out
                            .println("### current javasound buffer length is "
                                    + bc.getBufferLength() + " ms");
                } else if (-1 != className.indexOf("JitterBufferControl"))
                {
                    javax.media.control.BufferControl bc = (javax.media.control.BufferControl) controls[i];
                    System.out.println("### current jitter buffer length is "
                            + bc.getBufferLength() + " ms");
                    bc.setBufferLength(80);
                    System.out.println("### current jitter buffer length is "
                            + bc.getBufferLength() + " ms");
                } else if (-1 != className.indexOf("FPC"))
                {
                    fpc = (FrameProcessingControl) controls[i];
                    System.out.println("### found bitrate control "
                            + fpc.getClass());
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
            // /tracks[0].setFormat(new
            // javax.media.format.AudioFormat(javax.media.format.AudioFormat.ULAW_RTP,
            // 8000, 8, 1));
            tracks[0].setFormat(new javax.media.format.AudioFormat(
                    javax.media.format.AudioFormat.GSM_RTP, 8000, 8, 1));

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
                    psc.setPacketSize(66);
                    System.out.println("### current packetsize is "
                            + psc.getPacketSize() + " bytes");
                    break;
                }
            }

            // enumerate all controls of the processor
            Object[] pcontrols = processor.getControls();
            for (int i = 0; i < pcontrols.length; i++)
            {
                System.out.println("processor control " + i + " "
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

            processor.start();
            Thread.sleep(1000);

            System.out.println("\n>>>>>>  TRANSMITTING ULAW/RTP AUDIO NOW");
            while (2 > 1)
            {
                Thread.sleep(1000);

                if (null != bitrateControl)
                {
                    TransmissionStats stats = sendStream
                            .getSourceTransmissionStats();
                    System.out.println("rtp audio send: bitrate="
                            + bitrateControl.getBitRate() + " (pdu="
                            + stats.getPDUTransmitted() + " bytes="
                            + stats.getBytesTransmitted() + " overrun="
                            + fpc.getFramesDropped() + ")");
                }
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        System.exit(0);
    }
}
