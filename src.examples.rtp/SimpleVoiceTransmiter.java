import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.utility.*;

/**
 *
 * Adapted from
 * http://javasolution.blogspot.com/2007/04/sound-over-ip-with-jmf-rtp.html
 *
 */
public class SimpleVoiceTransmiter
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        final String urlStr = URLUtils.createUrlStr(new File(
                "samplemedia/gulp2.wav"));// "file://samplemedia/gulp2.wav";
        Format format;
        // TODO: if receiver has JMF in classpath after FMJ, and JMF defaults
        // for PIM set to true, no audio is heard.
        format = new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1);
        // format = new AudioFormat(AudioFormat.ULAW_RTP, 8000.0, 8, 1,
        // AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        // format = new AudioFormat(BonusAudioFormatEncodings.ALAW_RTP, 8000, 8,
        // 1);
        // format = new AudioFormat(BonusAudioFormatEncodings.SPEEX_RTP, 8000,
        // 8, 1, -1, AudioFormat.SIGNED);
        // format = new AudioFormat(BonusAudioFormatEncodings.ILBC_RTP, 8000.0,
        // 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

        if (false)
        {
            // First find a capture device that will capture linear audio
            // data at 8bit 8Khz
            AudioFormat captureFormat = new AudioFormat(AudioFormat.LINEAR,
                    8000, 8, 1);

            Vector devices = CaptureDeviceManager.getDeviceList(captureFormat);

            CaptureDeviceInfo di = null;

            if (devices.size() > 0)
            {
                di = (CaptureDeviceInfo) devices.elementAt(0);
            } else
            {
                System.err.println("No capture devices");
                // exit if we could not find the relevant capturedevice.
                System.exit(-1);
            }
        }

        // Create a processor for this capturedevice & exit if we
        // cannot create it
        Processor processor = null;
        try
        {
            processor = Manager.createProcessor(new MediaLocator(urlStr));
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        } catch (NoProcessorException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        // configure the processor
        processor.configure();

        while (processor.getState() != Processor.Configured)
        {
            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        processor.setContentDescriptor(new ContentDescriptor(
                ContentDescriptor.RAW_RTP));

        TrackControl track[] = processor.getTrackControls();

        boolean encodingOk = false;

        // Go through the tracks and try to program one of them to
        // output gsm data.

        for (int i = 0; i < track.length; i++)
        {
            if (!encodingOk && track[i] instanceof FormatControl)
            {
                if (((FormatControl) track[i]).setFormat(format) == null)
                {
                    track[i].setEnabled(false);
                } else
                {
                    encodingOk = true;
                }
            } else
            {
                // we could not set this track to gsm, so disable it
                track[i].setEnabled(false);
            }
        }

        // At this point, we have determined where we can send out
        // gsm data or not.
        // realize the processor
        if (encodingOk)
        {
            if (!new net.sf.fmj.ejmf.toolkit.util.StateWaiter(processor)
                    .blockingRealize())
            {
                System.err.println("Failed to realize");
                return;
            }

            // while (processor.getState() != Processor.Realized)
            // {
            // try
            // {
            // Thread.sleep(100);
            // } catch (InterruptedException e)
            // {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // }
            // get the output datasource of the processor and exit
            // if we fail
            DataSource ds = null;

            try
            {
                ds = processor.getDataOutput();
            } catch (NotRealizedError e)
            {
                e.printStackTrace();
                System.exit(-1);
            }

            // hand this datasource to manager for creating an RTP
            // datasink our RTP datasink will multicast the audio
            try
            {
                String url = "rtp://192.168.1.4:8000/audio/16";

                MediaLocator m = new MediaLocator(url);

                DataSink d = Manager.createDataSink(ds, m);
                d.open();
                d.start();

                System.out.println("Starting processor");
                processor.start();
                Thread.sleep(30000);
            } catch (Exception e)
            {
                e.printStackTrace();
                System.exit(-1);
            }
        }

    }

}
