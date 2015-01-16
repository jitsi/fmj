package net.sf.fmj.media.protocol.civil;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;

import javax.media.*;
import javax.media.CaptureDeviceInfo;
import javax.media.cdm.CaptureDeviceManager;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.format.VideoFormat;
import javax.media.protocol.*;

import net.sf.fmj.utility.*;

import com.lti.civil.*;
import com.lti.civil.Image;
import com.lti.utils.synchronization.*;

/**
 * DataSource for CIVIL video.
 *
 * @author Ken Larson
 *
 */
public class DataSource extends PushBufferDataSource implements CaptureDevice,
        FrameGrabbingControl
{
    private class CivilFormatControl implements FormatControl, Owned
    {
        public Component getControlComponent()
        {
            return null;
        }

        public Format getFormat()
        {
            try
            {
                com.lti.civil.VideoFormat vf = captureStream.getVideoFormat();
                return net.sf.fmj.media.protocol.civil.DataSource
                        .convertCivilFormat(vf);
            } catch (Exception ex)
            {
            }

            return outputVideoFormat;
        }

        public Object getOwner()
        {
            return DataSource.this;
        }

        public Format[] getSupportedFormats()
        {
            if ((captureStream != null) && connected)
            {
                try
                {
                    final List<com.lti.civil.VideoFormat> formatList = captureStream
                            .enumVideoFormats();
                    final Format[] formats = new Format[formatList.size()];
                    for (int i = 0; i < formatList.size(); i++)
                    {
                        formats[i] = net.sf.fmj.media.protocol.civil.DataSource
                                .convertCivilFormat(formatList.get(i));
                    }
                    return formats;
                } catch (CaptureException e)
                {
                    logger.log(Level.WARNING, "" + e, e);
                }
            }

            // must be in the state connected, is this correct ? or should we
            // return null ?
            return new Format[0];
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
            // returns null if format is not supported
            if (!(format instanceof VideoFormat))
            {
                return null;
            }

            // do we support the requested format ?
            try
            {
                if (connected && !started.getValue() && (null != captureStream))
                {
                    final List<com.lti.civil.VideoFormat> formatList = captureStream
                            .enumVideoFormats();
                    for (int i = 0; i < formatList.size(); i++)
                    {
                        VideoFormat videoFormat = net.sf.fmj.media.protocol.civil.DataSource
                                .convertCivilFormat(formatList.get(i));
                        if (format.matches(videoFormat))
                        {
                            captureStream.setVideoFormat(formatList.get(i));
                            outputVideoFormat = videoFormat;
                            break;
                        }
                    }
                }
            } catch (CaptureException ex)
            {
                logger.log(Level.WARNING, "" + ex, ex);
            }

            // returns specific format if passed in format is partially
            // unspecified
            return outputVideoFormat;
        }
    }

    private class JitterBufferControl implements BufferControl, Owned
    {
        public long getBufferLength()
        {
            return jitterbuflen;
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

            jitterbuflen = (int) time;
            jitterBuffer = new RingBuffer(jitterbuflen);
            if (TRACE)
                logger.fine("jitterbuflen=" + jitterbuflen);

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

            return jitterbuflen;
        }

        public void setEnabledThreshold(boolean b)
        {
        }

        public long setMinimumThreshold(long time)
        {
            return -1;
        }
    }

    private class MyCaptureObserver implements CaptureObserver
    {
        private long sequenceNumber = 0;

        public void onError(CaptureStream sender, CaptureException e)
        {
            logger.log(Level.WARNING, "" + e, e); // TODO: how to handle?
        }

        public void onNewImage(CaptureStream sender, Image image)
        {
            if (started.getValue())
            {
                long currentTime = System.nanoTime();
                // TODO: should always be the same as outputVideoFormat:
                final VideoFormat format = convertCivilFormat(image.getFormat());

                if (null == jitterBuffer)
                {
                    jitterBuffer = new RingBuffer(jitterbuflen);
                    if (TRACE)
                        logger.fine("jitterbuflen=" + jitterbuflen);
                }

                synchronized (currentBufferMutex)
                {
                    currentBuffer = new Buffer();

                    // live data has no duration, timestamp is a high resolution
                    // timer
                    currentBuffer.setFlags(Buffer.FLAG_LIVE_DATA
                            | Buffer.FLAG_SYSTEM_TIME);
                    currentBuffer.setOffset(0);
                    currentBuffer.setData(image.getBytes());
                    currentBuffer.setLength(image.getBytes().length);
                    currentBuffer.setFormat(format);
                    currentBuffer.setSequenceNumber(++sequenceNumber);
                    currentBuffer.setTimeStamp(currentTime);
                }

                if (null != pushBufferStream)
                {
                    final BufferTransferHandler handler = (BufferTransferHandler) transferHandlerHolder
                            .getObject();
                    if (handler != null)
                    {
                        if (!jitterBuffer.put(currentBuffer))
                        {
                            // not called when the jitterBuffer dropped a buffer
                            pushBufferStream.notifyTransferHandler();
                        }
                    }
                }

                // mgodehardt: will measure the real framerate
                long currentTimestamp = currentTime;
                if (-1 == lastTimestamp)
                {
                    lastTimestamp = currentTimestamp;
                }

                framesProcessed++;

                if ((currentTimestamp - lastTimestamp) > 1000000000L)
                {
                    float diffTime = (float) (currentTimestamp - lastTimestamp) / 1000000L;
                    frameRate = framesProcessed * (1000.0f / diffTime);

                    framesProcessed = 0;
                    lastTimestamp = currentTimestamp;
                }
            }
        }
    }

    private class MyPushBufferStream implements PushBufferStream
    {
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
            if (outputVideoFormat == null)
                logger.warning("outputVideoFormat == null, video format unknown.");
            return outputVideoFormat;
        }

        void notifyTransferHandler()
        {
            final BufferTransferHandler handler = (BufferTransferHandler) transferHandlerHolder
                    .getObject();
            if (handler != null)
                handler.transferData(this);
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
                Buffer aBuffer = (Buffer) jitterBuffer.get();

                // live data has no duration, timestamp is a high resolution
                // timer
                buffer.setFlags(aBuffer.getFlags());
                buffer.setOffset(0);
                buffer.setData(aBuffer.getData());
                buffer.setLength(aBuffer.getLength());
                buffer.setFormat(aBuffer.getFormat());
                buffer.setSequenceNumber(aBuffer.getSequenceNumber());
                buffer.setRtpTimeStamp(aBuffer.getRtpTimeStamp());
                buffer.setHeaderExtension(aBuffer.getHeaderExtension());
                buffer.setTimeStamp(aBuffer.getTimeStamp());
            } catch (InterruptedException ex)
            {
            }
        }

        public void setTransferHandler(BufferTransferHandler transferHandler)
        {
            transferHandlerHolder.setObject(transferHandler);
        }
    }

    private class VideoFrameRateControl implements FrameRateControl, Owned
    {
        public java.awt.Component getControlComponent()
        {
            return null;
        }

        public float getFrameRate()
        {
            return frameRate;
        }

        public float getMaxSupportedFrameRate()
        {
            return -1;
        }

        public Object getOwner()
        {
            return DataSource.this;
        }

        public float getPreferredFrameRate()
        {
            return -1;
        }

        public float setFrameRate(float newFrameRate)
        {
            try
            {
                // captureStream.setFrameRate((int)newFrameRate);
                return newFrameRate;
            } catch (Exception ex)
            {
            }
            return -1;
        }
    }

    private static final boolean TRACE = true;

    private static final Logger logger = LoggerSingleton.logger;

    private static final String CONTENT_TYPE = ContentDescriptor.RAW;

    public static VideoFormat convertCivilFormat(
            com.lti.civil.VideoFormat civilVideoFormat)
    {
        final int bitsPerPixel;
        if (civilVideoFormat.getFormatType() == com.lti.civil.VideoFormat.RGB24)
            bitsPerPixel = 24;
        else if (civilVideoFormat.getFormatType() == com.lti.civil.VideoFormat.RGB32)
            bitsPerPixel = 32;
        else
            throw new IllegalArgumentException();
        final int red, green, blue;
        red = 3;
        green = 2;
        blue = 1;

        final float fps = civilVideoFormat.getFPS();
        final float frameRate;
        if (fps < 0)
            frameRate = Format.NOT_SPECIFIED;
        else
            frameRate = fps;

        return new RGBFormat(new Dimension(civilVideoFormat.getWidth(),
                civilVideoFormat.getHeight()), -1, byte[].class, frameRate,
                bitsPerPixel, red, green, blue);

    }

    // handle ordinal locators, like civil:0 or civil:/0
    private static int ordinal(String remainder)
    {
        try
        {
            // allow either with slash or without
            if (remainder.startsWith("/"))
                remainder = remainder.substring(1);
            return Integer.parseInt(remainder);
        } catch (Exception e)
        {
            return -1;
        }
    }

    private CaptureSystem system;

    private String deviceId;
    private CaptureStream captureStream;
    private MyPushBufferStream pushBufferStream;

    private RingBuffer jitterBuffer;

    // jitter buffer len in buckets
    private int jitterbuflen = 1;
    protected Object[] controls = new Object[] { new CivilFormatControl(),
            new JitterBufferControl(), new VideoFrameRateControl() };

    // until connect was called this is empty
    private String deviceName = "";

    /**
     * The JMF video output format we are delivering.
     */
    private VideoFormat outputVideoFormat;

    private float frameRate = -1;

    private int framesProcessed;

    private long lastTimestamp;

    private boolean connected;

    private Buffer currentBuffer;

    private Object currentBufferMutex = new Object();

    private final SynchronizedBoolean started = new SynchronizedBoolean(false);

    private final SynchronizedObjectHolder transferHandlerHolder = new SynchronizedObjectHolder();

    private boolean enabled = true;

    @Override
    public void connect() throws IOException
    {
        if (TRACE)
            logger.fine("civil: connect");
        // Normally, we allow a re-connection even if we are connected, due to
        // an oddity in the way Manager works. See comments there
        // in createPlayer(MediaLocator sourceLocator).
        // however, because capture devices won't go back to previous data even
        // if we reconnect, there is no point in reconnecting.
        if (connected)
            return;

        if (null == jitterBuffer)
        {
            jitterBuffer = new RingBuffer(jitterbuflen);
            if (TRACE)
                logger.fine("jitterbuflen=" + jitterbuflen);
        }

        try
        {
            CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton
                    .instance();
            system = factory.createCaptureSystem();
            system.init();

            if (TRACE)
                logger.fine("Opening " + getLocator().getRemainder());

            // see if it is an ordinal URL like civil:0 or civil:/0
            final int ordinal = ordinal(getLocator().getRemainder());
            if (ordinal >= 0)
            {
                deviceId = deviceIdFromOrdinal(ordinal);
                if (deviceId == null)
                    throw new IOException("Unable to convert ordinal "
                            + ordinal + " to a capture device");
            } else
            {
                deviceId = getLocator().getRemainder();
            }

            // mgodehardt: fetch the human readable name of the video capture
            // device
            CaptureDeviceInfo cdi = null;

            Vector deviceList = (Vector) CaptureDeviceManager.getDeviceList(
                    null).clone();
            for (int i = 0; i < deviceList.size(); i++)
            {
                cdi = (CaptureDeviceInfo) deviceList.elementAt(i);
                if (cdi.getLocator().getRemainder().equalsIgnoreCase(deviceId))
                {
                    break;
                }
            }

            deviceName = "";
            if (null != cdi)
            {
                deviceName = cdi.getName();
            }

            captureStream = system.openCaptureDeviceStream(deviceId);
            outputVideoFormat = convertCivilFormat(captureStream
                    .getVideoFormat());

            captureStream.setObserver(new MyCaptureObserver());

            pushBufferStream = new MyPushBufferStream();

        } catch (CaptureException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new IOException("" + e);
        }

        connected = true;

    }

    private String deviceIdFromOrdinal(int index) throws CaptureException
    {
        final List list = system.getCaptureDeviceInfoList();
        if (index < 0 || index >= list.size())
            return null;

        com.lti.civil.CaptureDeviceInfo info = (com.lti.civil.CaptureDeviceInfo) list
                .get(index);
        return info.getDeviceID();

    }

    @Override
    public void disconnect()
    {
        if (TRACE)
            logger.fine("civil: disconnect");
        if (!connected)
            return;

        try
        {
            stop();
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }

        synchronized (currentBufferMutex)
        {
            currentBuffer = null;
        }

        if (captureStream != null)
        {
            try
            {
                captureStream.dispose();
            } catch (CaptureException e)
            {
                logger.log(Level.WARNING, "" + e, e);
            } finally
            {
                captureStream = null;
            }
        }

        try
        {
            system.dispose();
        } catch (Exception e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }
        system = null;

        connected = false;
    }

    public CaptureDeviceInfo getCaptureDeviceInfo()
    {
        return new CaptureDeviceInfo(deviceName, getLocator(),
                getFormatControls()[0].getSupportedFormats());
    }

    @Override
    public String getContentType()
    {
        return CONTENT_TYPE; // TODO: what should this be?, mgodehardt: RAW
                             // should be OK
    }

    @Override
    public Object getControl(String controlType)
    {
        Class<?> c;

        try
        {
            c = Class.forName(controlType);
        } catch (Exception e)
        {
            return null;
        }

        final Object[] controls = getControls();
        if (controls == null)
            return null;
        for (Object o : controls)
        {
            final Control control = (Control) o;
            if (c.isInstance(control))
                return control;
        }

        return null;

    }

    public Component getControlComponent()
    {
        return null;
    }

    @Override
    public Object[] getControls()
    {
        return controls;
    }

    // implementation of CaptureDevice:
    // From Andrew Rowley:
    // 1) I am using JMF to perform processing, and using the civil library for
    // the capture devices. If I set the video resolution too high (say 640x480
    // or more), sometimes it looks like the capturing is taking place, but
    // nothing happens. When I try to stop the sending, I get an error (Error:
    // Unable to prefetch com.sun.media.ProcessEngine@1ea0252). This does not
    // happen if I use a vfw device at any resolution, so I think it is
    // something to do with civil, possibly to do with an assumption about the
    // maximum video size? I will look into this myself to make sure that it is
    // not something in JMF, but it would be good to know that there is not
    // something going wrong in civil too.
    // It turns out that if you make the civil DataSource
    // (net.sf.fmj.media.protocol.civil.DataSource) implement
    // javax.media.protocol.CaptureDevice, then the problem goes away as JMF
    // does not then do prefetching
    @Override
    public Time getDuration()
    {
        return DURATION_UNBOUNDED;
    }

    public FormatControl[] getFormatControls()
    {
        return new FormatControl[] { new CivilFormatControl() };
    }

    @Override
    public PushBufferStream[] getStreams()
    {
        if (TRACE)
            logger.fine("getStreams");
        if (pushBufferStream == null)
            return new PushBufferStream[0];
        return new PushBufferStream[] { pushBufferStream };
    }

    public Buffer grabFrame()
    {
        Buffer aBuffer = null;

        synchronized (currentBufferMutex)
        {
            if (null != currentBuffer)
            {
                aBuffer = (Buffer) currentBuffer.clone();
                aBuffer.setFormat((Format) currentBuffer.getFormat().clone());
            }
        }

        return aBuffer;
    }

    @Override
    public void start() throws IOException
    {
        if (TRACE)
            logger.fine("civil: start");

        if (started.getValue())
        {
            logger.warning("Civil DataSource.start called while already started, ignoring");
            return;
        }

        try
        {
            captureStream.start();
        } catch (CaptureException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new IOException("" + e);
        }
        started.setValue(true);
    }

    @Override
    public void stop() throws IOException
    {
        if (TRACE)
            logger.fine("civil: stop");
        if (!started.getValue())
            return;

        try
        {
            if (captureStream != null)
            {
                captureStream.stop();
            }
        } catch (CaptureException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            throw new IOException("" + e);
        } finally
        {
            started.setValue(false);
        }
    }
}
