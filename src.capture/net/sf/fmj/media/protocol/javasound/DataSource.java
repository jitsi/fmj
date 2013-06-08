package net.sf.fmj.media.protocol.javasound;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.AudioFormat;
import javax.media.protocol.*;
import javax.sound.sampled.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.renderer.audio.*;
import net.sf.fmj.utility.*;

import com.lti.utils.synchronization.*;

/**
 * DataSource for JavaSound audio recording. TODO: we should maybe make this a
 * PullBufferDataSource, because otherwise it has to constantly poll javasound
 * to see if new data is available. JMF's is a PushBufferDataSource though.
 * TODO: do transfer handler notifications asynchronously
 *
 * @author Ken Larson
 *
 *         mgodehardt: added double buffering thru a ringbuffer, the buffer is
 *         filled by the AvailabilityThread, the TrackThread is fetching the
 *         data from the buffer, this can lead to dropped frames, increasing the
 *         size of the ringbuffer leads to more latency
 */
public class DataSource extends PushBufferDataSource implements CaptureDevice
{
    private class FPC implements FrameProcessingControl, Owned
    {
        public java.awt.Component getControlComponent()
        {
            return null;
        }

        public int getFramesDropped()
        {
            return jitterBuffer.getOverrunCounter();
        }

        public Object getOwner()
        {
            return DataSource.this;
        }

        public void setFramesBehind(float numFrames)
        {
        }

        public boolean setMinimalProcessing(boolean newMinimalProcessing)
        {
            return false;
        }
    }

    private class JavaSoundBufferControl implements BufferControl, Owned
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
            return DataSource.this;
        }

        public long setBufferLength(long time)
        {
            boolean isStarted = false;
            if (started.getValue())
            {
                isStarted = true;

                try
                {
                    stop();
                } catch (IOException e)
                {
                    logger.log(Level.WARNING, "" + e, e);
                }
            }

            // clamp the input value
            if (time < 20)
            {
                time = 20;
            } else if (time > 5000)
            {
                time = 5000;
            }
            buflenMS = time;

            // buffer size changed, change targetDataLine format ( this will
            // also recalc buflen )
            if (connected)
            {
                disconnect();

                try
                {
                    connect();
                } catch (IOException e)
                {
                    logger.log(Level.WARNING, "" + e, e);
                }
            }

            if (isStarted)
            {
                try
                {
                    start();
                } catch (IOException e)
                {
                    logger.log(Level.WARNING, "" + e, e);
                }
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

    private class JavaSoundFormatControl implements FormatControl, Owned
    {
        public Component getControlComponent()
        {
            return null;
        }

        public Format getFormat()
        {
            return jmfAudioFormat;
        }

        public Object getOwner()
        {
            return DataSource.this;
        }

        public Format[] getSupportedFormats()
        {
            return DataSource.this.getSupportedFormats();
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public void setEnabled(boolean enabled)
        {
            DataSource.this.enabled = enabled;
        }

        public Format setFormat(Format format)
        {
            setJMFAudioFormat((javax.media.format.AudioFormat) format);
            // TODO: return specific format if passed in format is partially
            // unspecified

            // format changed, change targetDataLine format ( this will also
            // recalc buflen )
            if (connected)
            {
                disconnect();

                try
                {
                    connect();
                } catch (IOException e)
                {
                    logger.log(Level.WARNING, "" + e, e);
                    return null;
                }
            }

            return jmfAudioFormat;
        }
    }

    private class JitterBufferControl implements BufferControl, Owned
    {
        public long getBufferLength()
        {
            return buflenMS * jitterBuffer.size();
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
            return DataSource.this;
        }

        public long setBufferLength(long time)
        {
            int jitterbuflen = (int) (time / buflenMS);
            if (jitterbuflen < 1)
            {
                jitterbuflen = 1;
            }

            jitterBuffer.resize(jitterbuflen);

            return jitterbuflen * buflenMS;
        }

        public void setEnabledThreshold(boolean b)
        {
        }

        public long setMinimumThreshold(long time)
        {
            return -1;
        }
    }

    private class MyPushBufferStream implements PushBufferStream
    {
        private class AvailabilityThread extends CloseableThread
        {
            // mgodehardt: close() should be not overwritten

            // mgodehardt: datasource is now buffered, this will smooth
            // streaming, underlying Service Provider may
            // use different buffers and read may return faster or slower, the
            // RingBuffer will smooth this
            @Override
            public void run()
            {
                if (TRACE)
                    logger.fine("jitterbuflen=" + jitterBuffer.size());

                try
                {
                    byte[] data = new byte[buflen];

                    while (!isClosing())
                    {
                        int actuallyRead = targetDataLine.read(data, 0,
                                data.length);
                        if (actuallyRead > 0)
                        {
                            final BufferTransferHandler handler
                                = transferHandlerHolder.getObject();

                            if (handler != null)
                            {
                                if (!jitterBuffer.put(data))
                                {
                                    // not called when the jitterBuffer dropped
                                    // a buffer
                                    handler.transferData(MyPushBufferStream.this);
                                }
                            }
                        } else
                        {
                            // TODO: error handling
                        }
                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                setClosed();
            }
        }

        private long sequenceNumber = 0;

        private AvailabilityThread availabilityThread;

        private final SynchronizedObjectHolder<BufferTransferHandler>
            transferHandlerHolder
                = new SynchronizedObjectHolder<BufferTransferHandler>();

        public boolean endOfStream()
        {
            return false;
        }

        public ContentDescriptor getContentDescriptor()
        {
            return new ContentDescriptor(ContentDescriptor.RAW); // It confuses
                                                                 // me that we
                                                                 // provide both
                                                                 // this, and
                                                                 // the correct
                                                                 // format below
                                                                 // (getFormat)
        }

        public long getContentLength()
        {
            return LENGTH_UNKNOWN;
        }

        public Object getControl(String controlType)
        {
            return null;
        }

        public Object[] getControls()
        {
            return new Object[0];
        }

        public Format getFormat()
        {
            return jmfAudioFormat;
        }

        public void read(Buffer buffer) throws IOException
        {
            // datasource is not started
            if (!started.getValue())
            {
                buffer.setOffset(0);
                buffer.setLength(0);
                buffer.setDiscard(true);
                return;
            }

            try
            {
                // will block until data is available
                byte[] data = (byte[]) jitterBuffer.get();

                // live data has no duration, timestamp is a high resolution
                // timer
                buffer.setFlags(Buffer.FLAG_LIVE_DATA
                        | Buffer.FLAG_RELATIVE_TIME);
                buffer.setOffset(0);
                buffer.setData(data);
                buffer.setLength(data.length);
                buffer.setFormat(jmfAudioFormat);
                buffer.setSequenceNumber(++sequenceNumber);
                buffer.setTimeStamp(System.nanoTime());

                levelControl.processData(buffer);
            } catch (Exception ex)
            {
            }
        }

        public void setTransferHandler(BufferTransferHandler transferHandler)
        {
            transferHandlerHolder.setObject(transferHandler);
        }

        public void startAvailabilityThread()
        {
            availabilityThread = new AvailabilityThread();
            availabilityThread.setName("AvailabilityThread for "
                    + MyPushBufferStream.this);
            availabilityThread.setDaemon(true);
            availabilityThread.start();
        }

        public void stopAvailabilityThread() throws InterruptedException
        {
            if (availabilityThread == null)
                return;
            availabilityThread.close();
            availabilityThread.waitUntilClosed();
            availabilityThread = null;
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

    private static final boolean TRACE = true;
    private static final Logger logger = LoggerSingleton.logger;

    public static Format[] querySupportedFormats(int mixerIndex)
    {
        List<AudioFormat> formats = new ArrayList<AudioFormat>();

        // get info about all mixers in the system
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

        // not a valid mixer index
        if ((mixerIndex < 0) || (mixerIndex >= mixerInfo.length))
        {
            return null;
        }

        // Fetch the mixer
        Mixer mixer = AudioSystem.getMixer(mixerInfo[mixerIndex]);

        Line.Info[] infos = mixer.getTargetLineInfo();
        for (int i = 0; i < infos.length; i++)
        {
            if (infos[i] instanceof DataLine.Info)
            {
                javax.sound.sampled.AudioFormat[] af = ((DataLine.Info) infos[i])
                        .getFormats();
                for (int j = 0; j < af.length; j++)
                {
                    javax.media.format.AudioFormat jmfAudioFormat = JavaSoundUtils
                            .convertFormat(af[j]);
                    if (!formats.contains(jmfAudioFormat))
                    {
                        formats.add(jmfAudioFormat);
                    }
                }
            }
        }

        // sort by quality:
        Collections.sort(
                formats,
                Collections.reverseOrder(new AudioFormatComparator()));

        // convert to an array:
        return formats.toArray(new Format[formats.size()]);
    }

    private MyPushBufferStream pushBufferStream;

    private TargetDataLine targetDataLine;

    private javax.sound.sampled.AudioFormat javaSoundAudioFormat;

    private javax.media.format.AudioFormat jmfAudioFormat;

    // audio capture buffer length in milliseconds (20ms leads to strange
    // problems under windows vista)
    private long buflenMS = 20;

    private int buflen;

    // mgodehardt: reading from the targetDataLine is smoothed thru this buffer,
    // two reads of a 40ms buffer may return after 30ms, because driver of
    // audio capture device is capturing 100ms frames
    private RingBuffer jitterBuffer = new RingBuffer(2);

    private PeakVolumeMeter levelControl;

    // The controls of the stream
    protected Object[] controls;

    private boolean connected;

    private static final String CONTENT_TYPE = ContentDescriptor.RAW;

    private final SynchronizedBoolean started = new SynchronizedBoolean(false);

    private Format[] formatsArray;

    private boolean enabled = true;

    public DataSource()
    {
        // is disabled by default, should be enabled (unmuted) if needed
        levelControl = new PeakVolumeMeter();
        levelControl.setMute(true);
    }

    @Override
    public void connect() throws IOException
    {
        if (TRACE)
            logger.fine("connect");

        // Normally, we allow a re-connection even if we are connected, due to
        // an oddity in the way Manager works. See comments there
        // in createPlayer(MediaLocator sourceLocator).
        // however, because capture devices won't go back to previous data even
        // if we reconnect, there is no point in reconnecting.

        if (connected)
            return;

        try
        {
            // set default format ( we fetch the first one, should be the one
            // with best quality and we set it to 44100Hz Sample Rate if rate is
            // unkown )
            // rate unknown means it supports a variety of sample rates ( like
            // javasound which supports rates up to 96kHz )
            if (null == jmfAudioFormat)
            {
                Format[] formats = getSupportedFormats();
                javax.media.format.AudioFormat audioFormat = (javax.media.format.AudioFormat) formats[0];

                if (audioFormat.getSampleRate() == javax.media.Format.NOT_SPECIFIED)
                {
                    javax.media.format.AudioFormat newAudioFormat = new javax.media.format.AudioFormat(
                            audioFormat.getEncoding(), 44100.0f,
                            javax.media.Format.NOT_SPECIFIED,
                            javax.media.Format.NOT_SPECIFIED);
                    setJMFAudioFormat((javax.media.format.AudioFormat) newAudioFormat
                            .intersects(audioFormat));
                } else
                {
                    setJMFAudioFormat(audioFormat);
                }
            }

            int mixerIndex = getMixerIndex();

            // get the requested TargetDataLine from the mixer and open it
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            Mixer mixer = AudioSystem.getMixer(mixerInfo[mixerIndex]);
            targetDataLine = (TargetDataLine) mixer.getLine(new DataLine.Info(
                    TargetDataLine.class, null));
            if (TRACE)
                logger.fine("targetDataLine=" + targetDataLine);

            // calculate buffer size
            buflen = (int) ((javaSoundAudioFormat.getFrameSize()
                    * javaSoundAudioFormat.getSampleRate() * buflenMS) / 1000);
            targetDataLine.open(javaSoundAudioFormat, buflen);
            if (TRACE)
                logger.fine("buflen=" + buflen);

            pushBufferStream = new MyPushBufferStream();

            controls = new Object[] { new JavaSoundFormatControl(),
                    new JavaSoundBufferControl(), new JitterBufferControl(),
                    new FPC(), levelControl };
        } catch (LineUnavailableException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new IOException("" + e);
        }

        connected = true;
    }

    @Override
    public void disconnect()
    {
        // TODO: what should happen in disconnect and what should happen in
        // stop?

        if (TRACE)
            logger.fine("disconnect");

        if (!connected)
            return;

        try
        {
            stop();

            // free resource
            if (targetDataLine != null)
            {
                targetDataLine.close();
            }
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        } finally
        {
            targetDataLine = null;
            pushBufferStream = null;
        }

        connected = false;
    }

    public CaptureDeviceInfo getCaptureDeviceInfo()
    {
        // jmf is a little buggy, if we use the same naming for audio as we use
        // in video capture devices
        // we can access all targetdatalines, but we still support javasound://
        // or javsound://44100, this will
        // be the default capturing device, as specified by os system settings
        int mixerIndex = getMixerIndex();
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        return new CaptureDeviceInfo(mixerInfo[mixerIndex].getName(),
                getLocator(), getSupportedFormats());
    }

    @Override
    public String getContentType()
    {
        return CONTENT_TYPE; // TODO: what should this be? mgodehardt: should be
                             // RAW, thats OK
    }

    @Override
    public Object getControl(String controlType)
    {
        // TODO: should return our controls
        return null;
    }

    @Override
    public Object[] getControls()
    {
        return controls;
    }

    @Override
    public Time getDuration()
    {
        return DURATION_UNBOUNDED;
    }

    public FormatControl[] getFormatControls()
    {
        return new FormatControl[] { new JavaSoundFormatControl() };
    }

    private int getMixerIndex()
    {
        // which mixer was requested ( javasound:#<mixer index> )
        int mixerIndex = -1;
        try
        {
            String remainder = getLocator().getRemainder();
            if (remainder.startsWith("#"))
            {
                remainder = remainder.substring(1);
                mixerIndex = Integer.parseInt(remainder);
            }
        } catch (Exception dontcare)
        {
        }

        // check for old style javasound:// locator
        if (-1 == mixerIndex)
        {
            if (getLocator().toString().startsWith("javasound://"))
            {
                // look for the first mixer which supports a targetdataline (
                // this is the default mixer )
                int index = 0;
                while (index < 50)
                {
                    Format[] formats = querySupportedFormats(index);

                    if ((null != formats) && (formats.length > 0))
                    {
                        mixerIndex = index;
                        break;
                    }
                    index++;
                }

                // TODO: parse the rest of the javasound locator, if user
                // specified any format options
            }
        }

        return mixerIndex;
    }

    @Override
    public PushBufferStream[] getStreams()
    {
        if (TRACE)
            logger.fine("getStreams");
        return new PushBufferStream[] { pushBufferStream };
    }

    private Format[] getSupportedFormats()
    {
        if (formatsArray != null)
            return formatsArray;

        int mixerIndex = getMixerIndex();

        formatsArray = querySupportedFormats(mixerIndex);

        return formatsArray;
    }

    private void setJavaSoundAudioFormat(javax.sound.sampled.AudioFormat f)
    {
        javaSoundAudioFormat = f;
        jmfAudioFormat = JavaSoundUtils.convertFormat(javaSoundAudioFormat);
    }

    private void setJMFAudioFormat(javax.media.format.AudioFormat f)
    {
        jmfAudioFormat = f;
        javaSoundAudioFormat = JavaSoundUtils.convertFormat(jmfAudioFormat);
    }

    @Override
    public void start() throws IOException
    {
        if (TRACE)
            logger.fine("start");

        if (started.getValue())
            return;

        targetDataLine.start();
        pushBufferStream.startAvailabilityThread();

        started.setValue(true);
    }

    @Override
    public void stop() throws IOException
    {
        if (TRACE)
            logger.fine("stop");
        if (!started.getValue())
            return;

        try
        {
            if (targetDataLine != null)
            {
                targetDataLine.stop();
                targetDataLine.flush();
            }
            if (pushBufferStream != null)
            {
                pushBufferStream.stopAvailabilityThread();
            }
        } catch (InterruptedException e)
        {
            throw new InterruptedIOException();
        } finally
        {
            started.setValue(false);
        }
    }
}
