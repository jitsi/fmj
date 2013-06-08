package net.sf.fmj.apps.transcode;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.utility.*;

/**
 * A command-line transcoder application. Example args:
 *
 * Transcode an audio file to another format: file://samplemedia/gulp2.wav
 * LINEAR:22050:8:1:?:S audio/basic file://out.au Record live video into a video
 * file: civil:/0 JPEG multipart/x-mixed-replace file://out.mmr Transmit an
 * audio file via RTP: file://samplemedia/gulp2.wav ULAW/RTP:8000:8:1 raw/rtp
 * rtp://192.168.1.2:8000/audio/16 Transmit live audio via RTP: javasound://0
 * ULAW/RTP:8000:8:1 raw/rtp rtp://192.168.1.2:8000/audio/16 javasound://0
 * ALAW/RTP:8000:8:1 raw/rtp rtp://192.168.1.2:8000/audio/16 Extract video track
 * from a movie and re-encode into another video file:
 * file://samplemedia/Apollo_15_liftoff_from_inside_LM.ogg JPEG
 * multipart/x-mixed-replace file://out.mmr Extract audio track from a movie and
 * re-encode into an audio file:
 * file://samplemedia/Apollo_15_liftoff_from_inside_LM.ogg LINEAR:44100:8:1
 * audio/basic file://out.au Receive RTP audio and record to a file:
 * rtp://192.168.1.7:8000 LINEAR:8000:8:1 audio/basic file://out.au Transmit
 * live video via RTP: civil:/0 JPEG/RTP:160x120 raw/rtp
 * rtp://192.168.1.2:8000/video/16 Receive RTP video and record to a file:
 * rtp://192.168.1.1:8000 JPEG multipart/x-mixed-replace file://out.mmr Render
 * live audio in a specific format: javasound://0 LINEAR:8000:16:1:B:S raw
 * render: javasound://0 ULAW:8000:8:1 raw render: Record audio to a file: TODO
 *
 * Render audio and video (merged): merge:[civil:/0][javasound://0] ? raw
 * render: Record both audio and video (merged) to an XML movie file:
 * merge:[civil:/0][javasound://0] JPEG:160x120 LINEAR:8000:8:1 video/xml
 * file://out.xmv
 *
 *
 * TODO: civil:/0 JPEG video/xml file://out.xmv
 *
 *
 * TODO: incompletely specified formats TODO: javasound://0 SPEEX/RTP:8000:8:1
 * raw/rtp rtp://192.168.1.2:8000/audio/16 - terrible filter graph TODO:
 * javasound://0 ILBC/RTP:8000:16:1 raw/rtp rtp://192.168.1.2:8000/audio/16 -
 * choppy TODO: javasound://0 LINEAR:8000:16:1:B:S audio/basic file://out.au
 * TODO: javasound://0 LINEAR:8000:16:1:B:S audio/x-wav file://out.wav TODO:
 * file://samplemedia/gulp2.wav LINEAR audio/basic file://out.au TODO:
 * file://samplemedia/Apollo_15_liftoff_from_inside_LM.ogg LINEAR:44100:16:1:B:S
 * audio/basic file://out.au TODO:
 * file://samplemedia/Apollo_15_liftoff_from_inside_LM.ogg LINEAR:44100:8:2
 * audio/basic file://out.au TODO:
 * file://samplemedia/Apollo_15_liftoff_from_inside_LM.ogg LINEAR audio/basic
 * file://out.au TODO: content type is often redundant with output, since
 * content type can usually be determined by output ext.
 *
 * @author Ken Larson
 *
 */
public class FmjTranscode
{
    static class KeyboardInputThread extends Thread
    {
        private final Processor processor;
        private boolean processorStopped;

        public KeyboardInputThread(Processor processor)
        {
            super();
            this.processor = processor;
            setName("KeyboardInputThread");
        }

        @Override
        public void run()
        {
            try
            {
                // while (true)
                // {
                System.out.println("Press ENTER to stop");
                int c = System.in.read(); // TODO: it would be nice to read
                                          // chars as they come in.
                System.out.println("ENTER pressed, stopping processor...");
                processor.stop();
                processorStopped = true;
                // break;
                // }
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
            }
        }

        public boolean wasProcessorStopped()
        {
            return processorStopped;
        }
    }

    private static final Logger logger = LoggerSingleton.logger;

    public static void main(String[] args) throws Exception
    {
        FmjStartup.init(); // initialize default FMJ/JMF/logging

        new FmjTranscode().run(args);
    }

    private static void usage()
    {
        System.out
                .println("Usage: FmjTranscode [Input URL] [Output format [Output format...]] [Output MIME type] [Output URL]");
        System.out.println();
        System.out
                .println("Output URL may be specified as \"render:\", in which case the output is rendered instead of sent to a specific output.");
        System.out
                .println("In this case, set the output MIME type to: \"raw\"");
        System.out.println();
        System.out.println("Output format syntax:");
        System.out
                .println("    Audio: [encoding]:[sampleRate]:[sampleSizeInBits]:[channels]:[endian]:[signed]:[frameSizeInBits]:[frameRate]:[dataType]");
        System.out
                .println("    where any trailing components may be omitted (to set them to be unspecified),");
        System.out
                .println("    any component may be set to ? to indicate unspecified, and");
        System.out
                .println("    the following conventions are used for non-numeric components:");
        System.out
                .println("    endian: B or L for big- or little- endian, respectively");
        System.out
                .println("    signed: S or U for signed or unsigned, respectively");
        System.out
                .println("    dataType: B, S, or I for byte array, short array, or int array, respectively");
        System.out
                .println("    Example: LINEAR:44100:16:2 corresponds to linear (PCM), 44100.0 khz, 16 bits per sample, stereo.");
        System.out
                .println("    Example: ULAW/RTP:8000:8:1 corresponds to ULAW/RTP, 8000.0 khz, 8 bits per sample, mono.");
        System.out
                .println("    Example: LINEAR:22050:8:1:?:S corresponds to linear (PCM), 22050.0 khz, 8 bits per sample, mono, unspecified endianness, signed");

        System.out.println();
        System.out
                .println("    Video: [encoding]:[size]:[maxDataLength]:[dataType]:[frameRate]");
        System.out
                .println("    where any trailing components may be omitted (to set them to be unspecified),");
        System.out
                .println("    any component may be set to ? to indicate unspecified, and");
        System.out
                .println("    the following conventions are used for non-numeric components:");
        System.out.println("    size: [width]x[height]");
        System.out
                .println("    frameRate: frames per second as a floating-point value");
        System.out
                .println("    dataType: B, S, or I for byte array, short array, or int array, respectively");
        System.out
                .println("    Example: JPEG corresponds to JPEG (MJPEG), with everything else unspecified");
        System.out
                .println("    Example: PNG:640x480 corresponds to PNG (MPNG), 640x480, with everything else unspecified");

        System.out.println();
        System.out.println("Examples:");
        System.out.println("Transcode an audio file to another format:");
        System.out
                .println("    file://samplemedia/gulp2.wav LINEAR:22050:8:1:?:S audio/basic file://out.au");
        System.out.println("Record live video into a video file:");
        System.out
                .println("    civil:/0 JPEG multipart/x-mixed-replace file://out.mmr");
        System.out.println("Transmit an audio file via RTP:");
        System.out
                .println("    file://samplemedia/gulp2.wav ULAW/RTP:8000:8:1 raw/rtp rtp://192.168.1.2:8000/audio/16");
        System.out.println("Transmit live audio via RTP:");
        System.out
                .println("    javasound://0 ULAW/RTP:8000:8:1 raw/rtp rtp://192.168.1.2:8000/audio/16");
        System.out
                .println("    javasound://0 ULAW/RTP:8000:8:1 raw/rtp rtp://192.168.1.2:8000/audio/16");
        System.out
                .println("Extract video track from a movie and re-encode into another video file:");
        System.out
                .println("    file://samplemedia/Apollo_15_liftoff_from_inside_LM.ogg JPEG multipart/x-mixed-replace file://out.mmr");
        System.out
                .println("Extract audio track from a movie and re-encode into an audio file:");
        System.out
                .println("    file://samplemedia/Apollo_15_liftoff_from_inside_LM.ogg LINEAR:44100:8:1 audio/basic file://out.au");
        System.out.println("Receive RTP audio and record to a file:");
        System.out
                .println("    rtp://192.168.1.7:8000 LINEAR:8000:8:1 audio/basic file://out.au");
        System.out.println("Transmit live video via RTP:");
        System.out
                .println("    civil:/0 JPEG/RTP:160x120 raw/rtp rtp://192.168.1.2:8000/video/16");
        System.out.println("Receive RTP video and record to a file:");
        System.out
                .println("    rtp://192.168.1.1:8000 JPEG multipart/x-mixed-replace file://out.mmr");
        System.out.println("Render live audio in a specific format:");
        System.out
                .println("     javasound://0 LINEAR:8000:16:1:B:S raw render:");
        System.out.println("Render audio and video (merged):");
        System.out.println("    merge:[civil:/0][javasound://0] ? raw render:");
        System.out
                .println("Record both audio and video (merged) to an XML movie file:");
        System.out
                .println("    merge:[civil:/0][javasound://0] JPEG:160x120 LINEAR:8000:8:1 video/xml file://out.xmv");
    }

    public void run(String[] args) throws Exception
    {
        if (args.length < 4)
        {
            usage();
            return;
        }

        // TODO: we should be able to specify a time limit for
        // streamed/unlimited media.

        final String inputUrl = args[0];
        // output formats - 1 or more.
        final String[] outputFormatStrs = new String[args.length - 3];
        for (int i = 0; i < outputFormatStrs.length; ++i)
            outputFormatStrs[i] = args[1 + i];

        final String outputMimeType = args[args.length - 2];
        final String outputUrl = args[args.length - 1];

        final ContentDescriptor outputContentDescriptor = new FileTypeDescriptor(
                ContentDescriptor.mimeTypeToPackageName(outputMimeType));
        final Format[] outputFormats = new Format[outputFormatStrs.length];
        for (int i = 0; i < outputFormatStrs.length; ++i)
        {
            if (outputFormatStrs[i].equals("?"))
                outputFormats[i] = null;
            else
                outputFormats[i] = FormatArgUtils.parse(outputFormatStrs[i]);

        }

        logger.info("Source URL: " + inputUrl);
        logger.info("Output MIME type: " + outputMimeType);
        logger.info("Output Content descriptor: " + outputContentDescriptor);
        for (int i = 0; i < outputFormats.length; ++i)
            logger.info("Output Format " + i + ": " + outputFormats[i]);
        logger.info("Output URL: " + outputUrl);

        transcode(inputUrl, outputFormats, outputContentDescriptor, outputUrl);

    }

    private void transcode(String inputUrl, Format[] outputFormats,
            ContentDescriptor outputContentDescriptor, String outputUrl)
            throws Exception
    {
        final ProcessorModel processorModel = new ProcessorModel(
                new MediaLocator(inputUrl), outputFormats,
                outputContentDescriptor);

        final Processor processor = Manager
                .createRealizedProcessor(processorModel);

        final DataSource ds = processor.getDataOutput();

        // final Player player;
        final DataSink destDataSink;

        // if (outputUrl.equals("RENDER")) // TODO: we can get rid of this now
        // that we have render: data sink
        // {
        // destDataSink = null;
        // player = Manager.createPlayer(ds);
        // }
        // else
        {
            // player = null;
            final MediaLocator m = new MediaLocator(outputUrl);

            destDataSink = Manager.createDataSink(ds, m);
            destDataSink.open();
            destDataSink.start();

        }

        logger.info("Starting processor");

        final net.sf.fmj.ejmf.toolkit.util.StateWaiter stateWaiter = new net.sf.fmj.ejmf.toolkit.util.StateWaiter(
                processor);

        if (!stateWaiter.blockingStart())
            throw new Exception("Failed to start");

        // if (player != null)
        // {
        // player.start();
        //
        // }

        final KeyboardInputThread keyboardInputThread = new KeyboardInputThread(
                processor);
        keyboardInputThread.setDaemon(true);
        keyboardInputThread.start();

        processor.addControllerListener(new ControllerListener()
        {
            public void controllerUpdate(ControllerEvent event)
            {
                if (event instanceof EndOfMediaEvent)
                {
                    logger.info("EOM, stopping processor");
                    processor.stop();

                }
            }

        });

        if (!stateWaiter.blockingWait(Controller.Realized))
            throw new Exception("Failed to reach stopped state");

        logger.fine("Closing processor");
        processor.close();
        logger.fine("Deallocating processor");
        processor.deallocate();

        // TODO: is the processor supposed to do this?
        if (destDataSink != null)
        {
            logger.fine("Stopping dest datasink");
            destDataSink.stop();
            logger.fine("Closing dest datasink");
            destDataSink.close();
        }

        // if (player != null)
        // {
        // logger.fine("Stopping player");
        // player.stop();
        // logger.fine("Closing player");
        // player.close();
        // }

        if (keyboardInputThread.wasProcessorStopped())
            logger.info("Transcode terminated.");
        else
            logger.info("Transcode complete.");

    }
}
