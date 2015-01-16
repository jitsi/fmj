package net.sf.fmj.media.renderer.audio;

import java.util.logging.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.AudioFormat;
import javax.sound.sampled.*;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.Control;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * Audio Renderer which uses JavaSound.
 *
 * @author Warren Bloomer
 * @author Ken Larson
 *
 */
public class JavaSoundRenderer implements Renderer
{
    private class FPC implements FrameProcessingControl, Owned
    {
        public java.awt.Component getControlComponent()
        {
            return null;
        }

        public int getFramesDropped()
        {
            return framesDropped;
        }

        public Object getOwner()
        {
            return JavaSoundRenderer.this;
        }

        public void setFramesBehind(float numFrames)
        {
        }

        public boolean setMinimalProcessing(boolean newMinimalProcessing)
        {
            return false;
        }
    }

    private class JavaSoundRendererBufferControl implements BufferControl,
            Owned
    {
        public long getBufferLength()
        {
            return buflenMS;
        }

        public java.awt.Component getControlComponent()
        {
            return null;
        }

        public boolean getEnabledThreshold()
        {
            return false;
        }

        public long getMinimumThreshold()
        {
            return -1;
        }

        public Object getOwner()
        {
            return JavaSoundRenderer.this;
        }

        public long setBufferLength(long time)
        {
            buflenMS = time;

            synchronized (bufferSizeChanged)
            {
                bufferSizeChanged = Boolean.TRUE;
            }
            return buflenMS;
        }

        public void setEnabledThreshold(boolean b)
        {
        }

        public long setMinimumThreshold(long time)
        {
            return -1;
        }
    }

    // dbFS using peak level
    private class PeakVolumeMeter extends AbstractGainControl
    {
        float peakLevel = 0.0f;

        public float getLevel()
        {
            return peakLevel;
        }

        public void processData(Buffer buf)
        {
            if (getMute() || buf.isDiscard() || (buf.getLength() <= 0))
            {
                return;
            }

            AudioFormat af = (AudioFormat) buf.getFormat();
            byte[] data = (byte[]) buf.getData();

            if (af.getEncoding().equalsIgnoreCase("LINEAR"))
            {
                if (af.getSampleSizeInBits() == 16)
                {
                    int msb = 0;
                    int lsb = 1;

                    if (af.getEndian() == AudioFormat.LITTLE_ENDIAN)
                    {
                        msb = 1;
                        lsb = 0;
                    }

                    if (af.getSigned() == AudioFormat.SIGNED)
                    {
                        int peak = 0;
                        int samples = data.length / 2;
                        for (int i = 0; i < samples; i++)
                        {
                            int value = (data[(i * 2) + msb] << 8)
                                    + (data[(i * 2) + lsb] & 0xff);
                            if (value < 0)
                            {
                                value = -value;
                            }

                            if (value > peak)
                            {
                                peak = value;
                            }
                        }

                        peakLevel = peak / 32768.0f;
                    }
                }
            }
        }

        public float setLevel(float level)
        {
            float result = getLevel();

            return result;
        }
    }

    private static final Logger logger = LoggerSingleton.logger;

    private String name = "FMJ Audio Renderer";

    // output buffer in bytes
    private int buflen;

    // output buffer length in milliseconds
    private long buflenMS = -1;

    private Boolean bufferSizeChanged = new Boolean(false);

    /** the DataLine to write audio data to. */
    private SourceDataLine sourceLine;

    /** javax.media version of audio format */
    private AudioFormat inputFormat;

    private javax.sound.sampled.AudioFormat audioFormat;
    /** javax.sound version of audio format */
    private javax.sound.sampled.AudioFormat sampledFormat;

    /** set of controls */
    private final ControlCollection controls = new ControlCollection();

    // To support ULAW, we use a codec which can convert from ULAW to LINEAR.
    // JMF's renderer can do this, although it may be overkill to use a codec.
    // TODO: support ULAW directly by simply converting the samples.
    // Same for ALAW.
    private Codec codec; // in case we need to do any conversions

    private final Buffer codecBuffer = new Buffer();

    private long lastSequenceNumber = -1;

    /* ----------------------- Renderer interface ------------------------- */

    private int framesDropped = 0;

    private PeakVolumeMeter levelControl;

    private Format[] supportedInputFormats = new Format[] {
            new AudioFormat(AudioFormat.LINEAR, -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat(AudioFormat.ULAW, -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray), // TODO: our codec doesn't support all
                                       // ULAW input formats.
            new AudioFormat(AudioFormat.ALAW, -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray), // TODO: our codec doesn't support all
                                       // ALAW input formats.
    };

    // the problem with not blocking is that we can get choppy audio. This would
    // be
    // solved theoretically by having the filter graph infrastructure pre-buffer
    // some
    // data. The other problem with non-blocking is that the filter graph has to
    // repeatedly call process, and it has no idea when it can call again and
    // have some
    // input consumed. This is, I think, kind of a rough spot in the JMF
    // architecture.
    // the filter graph could sleep, but how long should it sleep?
    // the problem with blocking, is that (if we allow it, which we don't) stop
    // will interrupt any write to sourceLine,
    // and basically, data will be lost. This will result in a gap in the audio
    // upon
    // start. If we don't interrupt with a stop, then the track can only fully
    // stop after process
    // has written all of the data.
    private static final boolean NON_BLOCKING = false;

    public JavaSoundRenderer()
    {
        // is disabled by default, should be enabled (unmuted) if needed
        levelControl = new PeakVolumeMeter();
        levelControl.setMute(true);
    }

    /**
     * Free the data line.
     */
    public void close()
    {
        logger.info("JavaSoundRenderer closing...");
        controls.clear();
        if (codec != null)
        {
            codec.close();
            codec = null;
        }
        sourceLine.close();
        sourceLine = null;
    }

    public Object getControl(String controlType)
    {
        return controls.getControl(controlType);
    }

    public Object[] getControls()
    {
        return controls.getControls();
    }

    /**
     * Returns the name of the pluging.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set supported input formats for the default or selected Mixer. Perhaps
     * just list generic LINEAR, ALAW and ULAW. At the moment, we are returning
     * all the formats handled by the current default mixer.
     */
    public Format[] getSupportedInputFormats()
    {
        // mgodehardt: JavaSound Renderer has multiple output devices, its ok to
        // not return all details
        return supportedInputFormats; // JMF doesn't return all the details.
    }

    @Override
    public int hashCode()
    {
        return super.hashCode(); // TODO: trying to change this hash code change
                                 // is useless.
        // TODO: for putting entries into the plugin manager,
        // PlugInManager.addPlugIn appears to
        // create a hash only based on the full class name, and only the last 22
        // chars of it.
        // that is,
        // ClassNameInfo.makeHashValue("com.sun.media.renderer.audio.JavaSoundRenderer")
        // and
        // ClassNameInfo.makeHashValue("net.sf.fmj.media.renderer.audio.JavaSoundRenderer")
        // both return the same value, as does
        // ClassNameInfo.makeHashValue("udio.JavaSoundRenderer")

        // therefore, this trick of creating a different hash code for this
        // class, does nothing to avoid the
        // warnings, when JMF is ahead in the classpath:

        // Problem adding net.sf.fmj.media.renderer.audio.JavaSoundRenderer to
        // plugin table.
        // Already hash value of 1262232571547748861 in plugin table for class
        // name of com.sun.media.renderer.audio.JavaSoundRenderer

    }

    private void logControls(Control[] controls)
    {
        for (int i = 0; i < controls.length; i++)
        {
            Control control = controls[i];
            logger.fine("control: " + control);
            Type controlType = control.getType();
            if (controlType instanceof CompoundControl.Type)
            {
                logControls(((CompoundControl) control).getMemberControls());
            }
        }
    }

    /**
     * Open the plugin. Must be called after the formats have been determined
     * and before "process" is called.
     *
     * Open the DataLine.
     */
    public void open() throws ResourceUnavailableException
    {
        audioFormat = JavaSoundUtils.convertFormat(inputFormat);
        logger.info("JavaSoundRenderer opening with javax.sound format: "
                + audioFormat);
        try
        {
            if (!inputFormat.getEncoding().equals(AudioFormat.LINEAR))
            {
                logger.info("JavaSoundRenderer: Audio format is not linear, creating conversion");

                if (inputFormat.getEncoding().equals(AudioFormat.ULAW))
                    codec = new net.sf.fmj.media.codec.audio.ulaw.Decoder(); // much
                                                                             // more
                                                                             // efficient
                                                                             // than
                                                                             // JavaSoundCodec
                else if (inputFormat.getEncoding().equals(AudioFormat.ALAW))
                    codec = new net.sf.fmj.media.codec.audio.alaw.Decoder(); // much
                                                                             // more
                                                                             // efficient
                                                                             // than
                                                                             // JavaSoundCodec
                else
                    throw new ResourceUnavailableException(
                            "Unsupported input format encoding: "
                                    + inputFormat.getEncoding());

                if (codec.setInputFormat(inputFormat) == null)
                    throw new ResourceUnavailableException(
                            "Codec rejected input format: " + inputFormat);

                final Format[] outputFormats = codec
                        .getSupportedOutputFormats(inputFormat);
                if (outputFormats.length < 1)
                    throw new ResourceUnavailableException(
                            "Unable to get an output format for input format: "
                                    + inputFormat);
                final AudioFormat codecOutputFormat = AudioFormatCompleter
                        .complete((AudioFormat) outputFormats[0]); // TODO:
                                                                   // choose the
                                                                   // best
                                                                   // quality
                                                                   // one.
                // specify any unspecified parameters:

                if (codec.setOutputFormat(codecOutputFormat) == null)
                    throw new ResourceUnavailableException(
                            "Codec rejected output format: "
                                    + codecOutputFormat);

                audioFormat = JavaSoundUtils.convertFormat(codecOutputFormat);

                codec.open();

                logger.info("JavaSoundRenderer: Audio format is not linear, created conversion from "
                        + inputFormat + " to " + codecOutputFormat);

            }

            // mgodehardt: we must use this, this will get a working mixer for
            // windows, linux and mac
            DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                    audioFormat);
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            logger.info("JavaSoundRenderer: sourceLine=" + sourceLine);
            sourceLine.open(audioFormat);
            logger.info("JavaSoundRenderer: buflen="
                    + sourceLine.getBufferSize());

            // fetch gain control
            FloatControl gainFloatControl = null;
            try
            {
                gainFloatControl = (FloatControl) sourceLine
                        .getControl(FloatControl.Type.MASTER_GAIN);
            } catch (Exception e)
            {
                logger.log(Level.WARNING, "" + e, e);
            }
            logger.fine("JavaSoundRenderer: gainFloatControl="
                    + gainFloatControl);

            // fecth mute control
            BooleanControl muteBooleanControl = null;
            try
            {
                muteBooleanControl = (BooleanControl) sourceLine
                        .getControl(BooleanControl.Type.MUTE);
            } catch (Exception e)
            {
                logger.log(Level.WARNING, "" + e, e);
            }
            logger.fine("JavaSoundRenderer: muteBooleanControl="
                    + muteBooleanControl);

            JavaSoundGainControl gainControl = new JavaSoundGainControl(
                    gainFloatControl, muteBooleanControl);
            controls.addControl(gainControl);

            controls.addControl(new JavaSoundRendererBufferControl());
            controls.addControl(new FPC());
            controls.addControl(levelControl);

            // /logControls(sourceLine.getControls());
        } catch (LineUnavailableException e)
        {
            throw new ResourceUnavailableException(e.getMessage());
        }
    }

    /**
     * Write the buffer to the SourceDataLine.
     */
    public int process(Buffer buffer)
    {
        // if we need to convert the format, do so using the codec.
        if (codec != null)
        {
            final int codecResult = codec.process(buffer, codecBuffer);
            if (codecResult == BUFFER_PROCESSED_FAILED)
                return BUFFER_PROCESSED_FAILED;
            if (codecResult == OUTPUT_BUFFER_NOT_FILLED)
                return BUFFER_PROCESSED_OK;

            codecBuffer.setRtpTimeStamp(buffer.getRtpTimeStamp());
            codecBuffer.setHeaderExtension(buffer.getHeaderExtension());
            codecBuffer.setTimeStamp(buffer.getTimeStamp());
            codecBuffer.setFlags(buffer.getFlags());
            codecBuffer.setSequenceNumber(buffer.getSequenceNumber());

            buffer = codecBuffer;
        }

        levelControl.processData(buffer);

        int length = buffer.getLength();
        int offset = buffer.getOffset();

        final Format format = buffer.getFormat();

        final Class<?> type = format.getDataType();
        if (type != Format.byteArray)
        {
            return BUFFER_PROCESSED_FAILED;
        }

        final byte[] data = (byte[]) buffer.getData();

        final boolean bufferNotConsumed;
        final int newBufferLength; // only applicable if bufferNotConsumed
        final int newBufferOffset; // only applicable if bufferNotConsumed

        // Buffer size changed
        try
        {
            synchronized (bufferSizeChanged)
            {
                if (bufferSizeChanged.booleanValue())
                {
                    bufferSizeChanged = Boolean.FALSE;

                    sourceLine.stop();
                    sourceLine.flush();
                    sourceLine.close();

                    buflen = (int) ((audioFormat.getFrameSize()
                            * audioFormat.getSampleRate() * buflenMS) / 1000);
                    sourceLine.open(audioFormat, buflen);
                    logger.info("JavaSoundRenderer: buflen="
                            + sourceLine.getBufferSize());
                    sourceLine.start();
                }
            }
        } catch (Exception ex)
        {
            logger.log(Level.WARNING, "" + ex, ex);
        }

        if (NON_BLOCKING)
        {
            // TODO: handle sourceLine.available(). This code currently causes
            // choppy audio.

            if (length > sourceLine.available())
            {
                // we should only write sourceLine.available() bytes, then
                // return INPUT_BUFFER_NOT_CONSUMED.
                length = sourceLine.available(); // don't try to write more than
                                                 // available
                bufferNotConsumed = true;
                newBufferLength = buffer.getLength() - length;
                newBufferOffset = buffer.getOffset() + length;

            } else
            {
                bufferNotConsumed = false;
                newBufferLength = length;
                newBufferOffset = offset;
            }
        } else
        {
            bufferNotConsumed = false;
            newBufferLength = 0;
            newBufferOffset = 0;
        }

        if (length == 0)
        {
            logger.finer("Buffer has zero length, flags = " + buffer.getFlags());

        }

        if (-1 == lastSequenceNumber)
        {
            lastSequenceNumber = buffer.getSequenceNumber();
        } else
        {
            if ((short) (lastSequenceNumber + 1) != (short) buffer
                    .getSequenceNumber())
            {
                int count = (((short) buffer.getSequenceNumber() - (short) lastSequenceNumber) & 0xffff) - 1;

                // /System.out.println("### PACKET LOST " + lastSequenceNumber +
                // " " + buffer.getSequenceNumber() + " lost=" + count);
                framesDropped += count;
            }

            lastSequenceNumber = buffer.getSequenceNumber();
        }

        // make sure all the bytes are written.
        while (length > 0)
        {
            // logger.fine("Available: " + sourceLine.available());
            // logger.fine("length: " + length);
            // logger.fine("sourceLine.getBufferSize(): " +
            // sourceLine.getBufferSize());

            final int n = sourceLine.write(data, offset, length);
            Thread.yield();

            if (n >= length)
                break;
            else if (n == 0)
            {
                // TODO: we could choose to handle a write failure this way,
                // assuming that it is considered legal to call stop while
                // process is being called.
                // however, that seems like a bad idea in general.
                // if (!sourceLine.isRunning())
                // {
                // buffer.setLength(offset);
                // buffer.setOffset(length);
                // return INPUT_BUFFER_NOT_CONSUMED; // our write was
                // interrupted.
                // }

                logger.warning("sourceLine.write returned 0, offset=" + offset
                        + "; length=" + length + "; available="
                        + sourceLine.available() + "; frame size in bytes"
                        + sourceLine.getFormat().getFrameSize()
                        + "; sourceLine.isActive() = " + sourceLine.isActive()
                        + "; " + sourceLine.isOpen()
                        + "; sourceLine.isRunning()=" + sourceLine.isRunning());
                return BUFFER_PROCESSED_FAILED; // sourceLine.write docs
                                                // indicate that this will only
                                                // happen if there is an error.

            } else
            {
                offset += n;
                length -= n;
            }

        }

        if (bufferNotConsumed)
        {
            // return INPUT_BUFFER_NOT_CONSUMED if not all bytes were written

            buffer.setLength(newBufferLength);
            buffer.setOffset(newBufferOffset);
            return INPUT_BUFFER_NOT_CONSUMED;
        }

        if (buffer.isEOM())
        {
            // TODO: the proper way to do this is to implement Drainable, and
            // let the processor call our drain method.
            sourceLine.drain(); // we need to ensure that the media finishes
                                // playing, otherwise the EOM event will
            // be posted before the media finishes playing.
        }

        return BUFFER_PROCESSED_OK;
    }

    /**
     * Reset the state of the plugin. The reset method is typically called if
     * the end of media is reached or the media is repositioned.
     */
    public void reset()
    {
        logger.info("JavaSoundRenderer resetting...");
    }

    public Format setInputFormat(Format format)
    {
        logger.info("JavaSoundRenderer setting input format to: " + format);
        if (!(format instanceof AudioFormat))
        {
            return null;
        }

        this.inputFormat = (AudioFormat) format;

        return inputFormat;
    }

    /* ----------------------------- */

    /**
     * Start the rendering process
     */
    public void start()
    {
        logger.info("JavaSoundRenderer starting...");
        sourceLine.start();
    }

    /* -------------------- private methods ----------------------- */

    /**
     * Stop the rendering process.
     */
    public void stop()
    {
        logger.info("JavaSoundRenderer stopping...");
        sourceLine.stop();

    }
}
