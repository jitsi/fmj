package net.sf.fmj.media.parser;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.protocol.*;
import net.sf.fmj.media.rtp.*;

/**
 * Parser for a raw stream of buffers from a PushBufferDataSource.
 */
public class RawPushBufferParser extends RawStreamParser
{
    class FrameTrack implements Track, BufferTransferHandler
    {
        Demultiplexer parser;
        PushBufferStream pbs;
        boolean enabled = true;
        CircularBuffer bufferQ;
        Format format = null;
        TrackListener listener;
        boolean stopped = true;
        boolean closed = false;
        boolean keyFrameFound = false;
        boolean checkDepacketizer = false;
        Depacketizer depacketizer = null;
        Object keyFrameLock = new Object();

        public FrameTrack(Demultiplexer parser, PushBufferStream pbs,
                int numOfBufs)
        {
            this.pbs = pbs;
            format = pbs.getFormat();

            if (source instanceof DelegateDataSource || !isRTPFormat(format))
            {
                keyFrameFound = true;
            }

            bufferQ = new CircularBuffer(numOfBufs);
            pbs.setTransferHandler(this);

            // System.err.println("Track format is " + format);
        }

        public void close()
        {
            // Unblock the transfer handlers.
            setEnabled(false);
            synchronized (bufferQ)
            {
                closed = true;
                bufferQ.notifyAll();
            }
        }

        private Depacketizer findDepacketizer(String name, Format input)
        {
            Class<?> cls;
            Object obj;

            try
            {
                cls = BasicPlugIn.getClassForName(name);
                obj = cls.newInstance();

                if (!(obj instanceof Depacketizer))
                    return null;

                Depacketizer dpktizer = (Depacketizer) obj;

                if (dpktizer.setInputFormat(input) == null)
                    return null;

                dpktizer.open();

                return dpktizer;

            } catch (Exception e)
            {
            } catch (Error e)
            {
            }

            return null;
        }

        /**
         * Parse the RTP/H261 stream Code taken from
         * com.sun.media.codec.video.h261.NativeDecoder.
         */
        public boolean findH261Key(Buffer b)
        {
            int width, height, offset, skipBytes;
            byte data[];

            if ((data = (byte[]) b.getData()) == null)
                return false;

            offset = b.getOffset();

            // Get to the actual h261 compressed payload
            skipBytes = 4;

            if ((data[offset + skipBytes] != 0)
                    || (data[offset + skipBytes + 1] != 1)
                    || ((data[offset + skipBytes + 2] & 0xfc) != 0))
            {
                return false;
            }

            int s = (data[offset + skipBytes + 3] >> 3) & 0x01;
            width = h261Widths[s];
            height = h261Heights[s];
            format = new VideoFormat(VideoFormat.H261_RTP, new Dimension(width,
                    height), ((VideoFormat) format).getMaxDataLength(),
                    ((VideoFormat) format).getDataType(),
                    ((VideoFormat) format).getFrameRate());

            b.setFormat(format);
            return true;
        }

        /**
         * Parse the RTP/H263-1998 stream Code taken from
         * com.sun.media.codec.video.h263.NativeDecoder.
         */
        public boolean findH263_1998Key(Buffer b)
        {
            int width, height, payloadLen, offset;
            byte data[];
            int s = -1;
            int picOffset = -1;

            if ((data = (byte[]) b.getData()) == null)
                return false;

            offset = b.getOffset();

            // 2 bytes for H263-1998 header + pLen from header
            payloadLen = 2 + (((data[offset] & 0x01) << 5) | ((data[offset + 1] & 0xf8) >> 3));
            if ((data[offset] & 0x02) != 0)
            { // Video Redundancy present
                payloadLen++;
            }

            picOffset = -1;

            if (payloadLen > 5)
            {
                // Use PIC header in payload header
                if (((data[offset] & 0x02) == 0x02)
                        && ((data[offset + 3] & 0xfc) == 0x80))
                {
                    picOffset = offset + 3;
                } else if ((data[offset + 2] & 0xfc) == 0x80)
                {
                    picOffset = offset + 2;
                }
            } else if (((data[offset] & 0x04) == 0x04)
                    && ((data[offset + payloadLen] & 0xfc) == 0x80))
            {
                picOffset = offset + payloadLen;
            }

            if (picOffset < 0)
                return false;

            s = (data[picOffset + 2] >> 2) & 0x7;
            if (s == 7)
            {
                // Extended PTYPE, picture size is in the extension
                // if UFEP = 001
                if (((data[picOffset + 3] >> 1) & 0x07) == 1)
                {
                    s = ((data[picOffset + 3] << 2) & 0x04)
                            | ((data[picOffset + 4] >> 6) & 0x03);
                } else
                {
                    return false; // picture type not present
                }
            }
            if (s < 0)
                return false;

            width = h263Widths[s];
            height = h263Heights[s];

            format = new VideoFormat(VideoFormat.H263_1998_RTP, new Dimension(
                    width, height), ((VideoFormat) format).getMaxDataLength(),
                    ((VideoFormat) format).getDataType(),
                    ((VideoFormat) format).getFrameRate());
            b.setFormat(format);
            return true;
        }

        /**
         * Parse the RTP/H263 stream Code taken from
         * com.ibm.media.codec.video.h263.JavaDecoder.
         */
        public boolean findH263Key(Buffer b)
        {
            int width, height, payloadLen, offset;
            byte data[];

            if ((data = (byte[]) b.getData()) == null)
                return false;

            payloadLen = getH263PayloadHeaderLength(data, b.getOffset());
            offset = b.getOffset();
            if ((data[offset + payloadLen] != 0)
                    || (data[offset + payloadLen + 1] != 0)
                    || ((data[offset + payloadLen + 2] & 0xfc) != 0x80))
                return false;

            int s = (data[offset + payloadLen + 4] >> 2) & 0x7;
            width = h263Widths[s];
            height = h263Heights[s];

            format = new VideoFormat(VideoFormat.H263_RTP, new Dimension(width,
                    height), ((VideoFormat) format).getMaxDataLength(),
                    ((VideoFormat) format).getDataType(),
                    ((VideoFormat) format).getFrameRate());

            b.setFormat(format);
            return true;
        }

        /**
         * Parse the RTP/JPEG stream Code taken from
         * com.sun.media.codec.video.jpeg.RTPDePacketizer.
         */
        public boolean findJPEGKey(Buffer b)
        {
            if ((b.getFlags() & Buffer.FLAG_RTP_MARKER) == 0)
                return false;

            int width, height;
            byte data[];

            data = (byte[]) b.getData();
            width = (data[b.getOffset() + 6] & 0xff) * 8;
            height = (data[b.getOffset() + 7] & 0xff) * 8;

            format = new VideoFormat(VideoFormat.JPEG_RTP, new Dimension(width,
                    height), ((VideoFormat) format).getMaxDataLength(),
                    ((VideoFormat) format).getDataType(),
                    ((VideoFormat) format).getFrameRate());

            b.setFormat(format);
            return true;
        }

        private boolean findKeyFrame(Buffer buf)
        {
            if (!checkDepacketizer)
            {
                // Check to see if there's a depacketizer associated with the
                // format. If so, we'll use it for parsing the input.
                Vector pnames = PlugInManager.getPlugInList(buf.getFormat(),
                        null, Depacketizer.DEPACKETIZER);
                if (pnames.size() != 0)
                {
                    depacketizer = findDepacketizer(
                            (String) pnames.elementAt(0), buf.getFormat());
                }
                checkDepacketizer = true;
            }

            Format fmt = buf.getFormat();

            if (fmt == null)
                return false;

            if (fmt.getEncoding() == null)
            {
                synchronized (keyFrameLock)
                {
                    keyFrameFound = true;
                    keyFrameLock.notifyAll();
                }
                return true;
            }

            boolean rtn = true;

            if (jpegVideo.matches(fmt))
                rtn = findJPEGKey(buf);
            else if (h261Video.matches(fmt))
                rtn = findH261Key(buf);
            else if (h263Video.matches(fmt))
                rtn = findH263Key(buf);
            // else if (h263_1998Video.matches(fmt))
            // rtn = findH263_1998Key(buf);
            else if (mpegVideo.matches(fmt))
                rtn = findMPEGKey(buf);
            else if (mpegAudio.matches(fmt))
                rtn = findMPAKey(buf);
            else if (depacketizer != null)
            {
                fmt = depacketizer.parse(buf);
                if (fmt != null)
                {
                    // Found the format. We are done with
                    // the depacketizer.
                    format = fmt;
                    buf.setFormat(format);
                    depacketizer.close();
                    depacketizer = null;
                } else
                    rtn = false;
            }

            if (rtn)
            {
                synchronized (keyFrameLock)
                {
                    keyFrameFound = true;
                    keyFrameLock.notifyAll();
                }
            }

            return keyFrameFound;
        }

        /**
         * Parse the RTP/MPEG audio stream Code taken from
         * com.sun.media.codec.audio.mpa.DePacketizer.
         */
        public boolean findMPAKey(Buffer b)
        {
            int channels;
            double sampleRate;
            byte data[];

            if ((data = (byte[]) b.getData()) == null)
                return false;

            int off = b.getOffset();
            if (b.getLength() < 8)
                return false; // doesn't contain MPA header

            if (data[off + 2] != 0 || data[off + 3] != 0)
                return false; // frame continuation

            off += 4; // skip RTP header to get to MPA header
            if ((data[off] & 0xff) != 0xff || (data[off + 1] & 0xf6) <= 0xf0
                    || (data[off + 2] & 0xf0) == 0xf0
                    || (data[off + 2] & 0x0c) == 0x0c
                    || (data[off + 3] & 0x03) == 0x02)
                return false; // doesn't start with a valid MPA header

            int id = (data[off + 1] >> 3) & 1; // MPEG1 or MPEG2
            int six = (data[off + 2] >> 2) & 3;
            channels = (((data[off + 3] >> 6) & 3) == 3) ? 1 : 2;
            sampleRate = MPASampleTbl[id][six];

            format = new AudioFormat(AudioFormat.MPEG_RTP, sampleRate, 16,
                    channels, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

            b.setFormat(format);
            return true;
        }

        /**
         * Parse the RTP/MPEG video stream Code taken from
         * com.sun.media.codec.video.mpeg.DePacketizer.
         */
        public boolean findMPEGKey(Buffer b)
        {
            int width, height;
            float frameRate;
            byte data[];

            if ((data = (byte[]) b.getData()) == null)
            {
                return false;
            }

            int off = b.getOffset();
            if (b.getLength() < 12)
            {
                return false; // can't contain MPEG sequence header
            }

            if ((data[off + 2] & 0x20) != 0x20)
            {
                return false; // doesn't contain MPEG sequence header
            }

            if (data[off + 4] != 0 || data[off + 5] != 0 || data[off + 6] != 1
                    || (data[off + 7] & 0xff) != 0xb3)
            {
                return false; // doesn't start with MPEG sequence header
            }

            int frix = (data[off + 11] & 0x0f);
            if (frix == 0 || frix > 8)
            {
                return false; // not a valid frame rate
            }

            width = ((data[off + 8] & 0xff) << 4)
                    | ((data[off + 9] & 0xf0) >> 4);
            height = ((data[off + 9] & 0x0f) << 8) | (data[off + 10] & 0xff);
            frameRate = MPEGRateTbl[frix];

            format = new VideoFormat(VideoFormat.MPEG_RTP, new Dimension(width,
                    height), ((VideoFormat) format).getMaxDataLength(),
                    ((VideoFormat) format).getDataType(), frameRate);

            b.setFormat(format);
            return true;
        }

        public Time getDuration()
        {
            return parser.getDuration();
        }

        public Format getFormat()
        {
            return format;
        }

        int getH263PayloadHeaderLength(byte[] input, int offset)
        {
            int l = 0;
            byte b = input[offset];

            if ((b & 0x80) != 0)
            { // mode B or C
                if ((b & 0x40) != 0) // mode C
                    l = 12;
                else
                    // mode B
                    l = 8;
            } else
            { // mode A
                l = 4;
            }

            return l;
        }

        public Time getStartTime()
        {
            return new Time(0);
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public Time mapFrameToTime(int frameNumber)
        {
            return new Time(0);
        }

        public int mapTimeToFrame(Time t)
        {
            return -1;
        }

        /**
         * Peek into the data stream to parse the data format.
         */
        public void parse()
        {
            try
            {
                synchronized (keyFrameLock)
                {
                    while (!keyFrameFound)
                        keyFrameLock.wait();
                }
            } catch (Exception e)
            {
            }
        }

        public void readFrame(Buffer buffer)
        {
            // Retrieve a filled buffer.
            if (stopped)
            {
                buffer.setDiscard(true);
                buffer.setFormat(format);
                return;
            }

            Buffer filled;
            synchronized (bufferQ)
            {
                while (!bufferQ.canRead())
                {
                    try
                    {
                        bufferQ.wait();
                        if (stopped)
                        {
                            buffer.setDiscard(true);
                            buffer.setFormat(format);
                            return;
                        }
                    } catch (Exception e)
                    {
                    }
                }
                filled = bufferQ.read();
            }

            // Copy all the attributes from filled to buffer.
            Object hdr = buffer.getHeader();
            buffer.copy(filled, true);
            filled.setHeader(hdr);

            // if ((buffer.getFlags() & Buffer.FLAG_RTP_MARKER) != 0)
            // System.err.println("RBP: TS: " + buffer.getTimeStamp());

            // Update the saved format.
            format = filled.getFormat();

            synchronized (bufferQ)
            {
                bufferQ.readReport();
                bufferQ.notifyAll();
            }
        }

        public void reset()
        {
        }

        public void setEnabled(boolean t)
        {
            if (t)
                pbs.setTransferHandler(this);
            else
                pbs.setTransferHandler(null);
            enabled = t;
        }

        public void setTrackListener(TrackListener l)
        {
            listener = l;
        }

        public void start()
        {
            // we need to ensure that readFrame is returned to its
            // original state and does not return w/o blocking.
            synchronized (bufferQ)
            {
                stopped = false;
                if (source instanceof CaptureDevice)
                {
                    // Flush the buffer Q.
                    while (bufferQ.canRead())
                    {
                        bufferQ.read();
                        bufferQ.readReport();
                    }
                }
                bufferQ.notifyAll();
            }
        }

        public void stop()
        {
            // we basically need to ensure that readFrame will return
            // immediately.and also make sure that if it is called in
            // the stopped state, it returns w/o blocking.
            synchronized (bufferQ)
            {
                stopped = true;
                bufferQ.notifyAll();
            }
        }

        public void transferData(PushBufferStream pbs)
        {
            // Retrieve an empty buffer for the PSS to write into.
            Buffer buffer;
            synchronized (bufferQ)
            {
                // damencho (!stopped)
                while (!bufferQ.canWrite() && !closed && !stopped)
                {
                    try
                    {
                        bufferQ.wait();
                    } catch (Exception e)
                    {
                    }
                }
                // If source is null, the data source has been closed.
                if (closed || stopped)
                    return;
                buffer = bufferQ.getEmptyBuffer();
            }

            try
            {
                pbs.read(buffer);
            } catch (IOException e)
            {
                buffer.setDiscard(true);
            }

            // Until we find the first key frame, we won't
            // add it to the buffer queue.
            if (!keyFrameFound && !findKeyFrame(buffer))
            {
                synchronized (bufferQ)
                {
                    // Discard that buffer.
                    bufferQ.writeReport();
                    bufferQ.read();
                    bufferQ.readReport();
                }
                return;
            }

            // Put the filled buffer back to the queue for consumption.
            synchronized (bufferQ)
            {
                bufferQ.writeReport();
                bufferQ.notifyAll();
            }
        }
    }

    static final String NAMEBUFFER = "Raw video/audio buffer stream parser";

    private boolean started = false;
    // For comparing formats.
    static AudioFormat mpegAudio = new AudioFormat(AudioFormat.MPEG_RTP);
    static VideoFormat mpegVideo = new VideoFormat(VideoFormat.MPEG_RTP);
    static VideoFormat jpegVideo = new VideoFormat(VideoFormat.JPEG_RTP);
    static VideoFormat h261Video = new VideoFormat(VideoFormat.H261_RTP);

    static VideoFormat h263Video = new VideoFormat(VideoFormat.H263_RTP);
    // Seb
    // static VideoFormat h263_1998Video = new
    // VideoFormat(VideoFormat.H263_1998_RTP);

    /****************************************************************
     * Track class
     ****************************************************************/

    final int[] h261Widths = { 176, 352 };

    final int[] h261Heights = { 144, 288 };

    final int[] h263Widths = { 0, 128, 176, 352, 704, 1408, 0, 0 };

    final int[] h263Heights = { 0, 96, 144, 288, 576, 1152, 0, 0 };

    final float MPEGRateTbl[] = { 0.0f, 23.976f, 24.f, 25.f, 29.97f, 30.f,
            50.f, 59.94f, 60.f };

    public static int[][] MPASampleTbl = { { 22050, 24000, 16000, 0 }, // MPEG 2
            { 44100, 48000, 32000, 0 } // MPEG 1
    };

    @Override
    public void close()
    {
        if (source != null)
        {
            try
            {
                source.stop();
                // stop every tracks, so that readFrame() can be released.
                // close every tracks to unblock the transfer handlers.
                for (int i = 0; i < tracks.length; i++)
                {
                    ((FrameTrack) tracks[i]).stop();
                    ((FrameTrack) tracks[i]).close();
                }

                source.disconnect();
            } catch (Exception e)
            {
                // Internal error?
            }
            source = null;
        }
        started = false;
    }

    @Override
    public String getName()
    {
        return NAMEBUFFER;
    }

    /**
     */
    @Override
    public Track[] getTracks()
    {
        for (int i = 0; i < tracks.length; i++)
            ((FrameTrack) tracks[i]).parse();
        return tracks;
    }

    boolean isRTPFormat(Format fmt)
    {
        return fmt != null
                && fmt.getEncoding() != null
                && (fmt.getEncoding().endsWith("rtp") || fmt.getEncoding()
                        .endsWith("RTP"));
    }

    /**
     * Opens the plug-in software or hardware component and acquires necessary
     * resources. If all the needed resources could not be acquired, it throws a
     * ResourceUnavailableException. Data should not be passed into the plug-in
     * without first calling this method.
     */
    @Override
    public void open()
    {
        if (tracks != null)
            return;
        tracks = new Track[streams.length];
        for (int i = 0; i < streams.length; i++)
        {
            tracks[i] = new FrameTrack(this, (PushBufferStream) streams[i], 1);
        }
    }

    /**
     * Resets the state of the plug-in. Typically at end of media or when media
     * is repositioned.
     */
    @Override
    public void reset()
    {
        for (int i = 0; i < tracks.length; i++)
            ((FrameTrack) tracks[i]).reset();
    }

    @Override
    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        SourceStream[] streams;

        if (!(source instanceof PushBufferDataSource))
        {
            throw new IncompatibleSourceException(
                    "DataSource not supported: " + source);
        } else
        {
            streams = ((PushBufferDataSource) source).getStreams();
        }

        if (streams == null)
        {
            throw new IOException("Got a null stream from the DataSource");
        }

        if (streams.length == 0)
        {
            throw new IOException(
                    "Got a empty stream array from the DataSource");
        }

        if (!supports(streams))
        {
            throw new IncompatibleSourceException(
                    "DataSource not supported: " + source);
        }

        this.source = source;
        this.streams = streams;
    }

    /**
     * Start the parser.
     */
    @Override
    public void start() throws IOException
    {
        if (started)
            return;
        for (int i = 0; i < tracks.length; i++)
            ((FrameTrack) tracks[i]).start();
        source.start();
        started = true;
    }

    /**
     * Stop the parser.
     */
    @Override
    public void stop()
    {
        try
        {
            // stop each of the tracks, so that readFrame can be released
            for (int i = 0; i < tracks.length; i++)
                ((FrameTrack) tracks[i]).stop();

            source.stop();

        } catch (Exception e)
        {
            // Internal errors?
        }
        started = false;
    }

    /**
     * Override this if the Parser has additional requirements from the
     * PushSourceStream
     */
    @Override
    protected boolean supports(SourceStream[] streams)
    {
        return ((streams[0] != null) && (streams[0] instanceof PushBufferStream));
    }
}
