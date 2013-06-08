package net.sf.fmj.media.parser;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

/**
 * Parser for GSM media files.
 *
 * @author Martin Harvan
 */
public class GsmParser extends AbstractDemultiplexer
{
    private class PullSourceStreamTrack extends AbstractTrack
    {
        private javax.sound.sampled.AudioFormat javaSoundInputFormat;
        private PullSourceStream stream;
        private long frameLength;
        private long totalBytesRead;
        private static final int GSM_FRAME_SIZE = 33;

        public PullSourceStreamTrack(PullSourceStream stream)
        {
            super();
            this.stream = stream;
            frameLength = stream.getContentLength() / GSM_FRAME_SIZE;

        }

        private long bytesToNanos(long bytes)
        {
            final long frames = bytes / GSM_FRAME_SIZE;
            final double seconds = frames / GSM_FRAME_RATE;
            final double nanos = secondsToNanos(seconds);
            return (long) nanos;
        }

        @Override
        public Time getDuration()
        {
            final long lengthInFrames = frameLength;
            if (lengthInFrames < 0)
            {
                logger.fine("PullSourceStreamTrack: returning Duration.DURATION_UNKNOWN (1)");
                return Duration.DURATION_UNKNOWN;
            }
            final double lengthInSeconds = lengthInFrames / GSM_FRAME_RATE;
            if (lengthInSeconds < 0.0)
            {
                logger.fine("PullSourceStreamTrack: returning Duration.DURATION_UNKNOWN (2)");
                return Duration.DURATION_UNKNOWN;
            }
            final double lengthInNanos = secondsToNanos(lengthInSeconds);
            logger.fine("PullSourceStreamTrack: returning "
                    + ((long) lengthInNanos));

            return new Time((long) lengthInNanos);
        }

        @Override
        public Format getFormat()
        {
            return new AudioFormat(AudioFormat.GSM, 8000, 8, 1, -1,
                    AudioFormat.SIGNED, 264, -1, Format.byteArray);
        }

        public long getTotalBytesRead()
        {
            return totalBytesRead;
        }

        /**
         * @return -1L if cannot convert, because frame size and frame rate are
         *         not known.
         */
        private long nanosToBytes(long nanos)
        {
            final double seconds = nanosToSeconds(nanos);
            final double frames = seconds * GSM_FRAME_RATE;
            final double bytes = frames * GSM_FRAME_SIZE;
            return (long) bytes;
        }

        @Override
        public void readFrame(Buffer buffer)
        {
            final int BUFFER_SIZE = 16500; // it's best to read in multiples of
                                           // 33's
            if (buffer.getData() == null)
                buffer.setData(new byte[BUFFER_SIZE]);
            byte[] bytes = (byte[]) buffer.getData();

            try
            {
                int result = stream.read(bytes, 0, bytes.length);
                if (result < 0)
                {
                    buffer.setEOM(true);
                    buffer.setLength(0);
                    return;
                }
                buffer.setLength(result);
                buffer.setOffset(0);
            } catch (IOException e)
            {
                buffer.setEOM(true);
                buffer.setDiscard(true);
                buffer.setLength(0);
                logger.log(Level.WARNING, "" + e, e);
            }
        }

        public void setPssForReadFrame(PullSourceStream pullSourceStream)
        {
            this.stream = pullSourceStream;

        }

        public long skipNanos(long nanos) throws IOException
        {
            final long bytes = nanosToBytes(nanos);
            if (bytes <= 0)
            {
                logger.fine("GsmParser: skipping nanos: " + 0);
                return 0;
            }
            final long bytesSkipped = 0;
            totalBytesRead += bytesSkipped;
            if (bytesSkipped == bytes)
            {
                logger.fine("GsmParser: skipping nanos: " + nanos);
                return nanos;
            } else
            {
                final long result = bytesToNanos(bytesSkipped);
                logger.fine("GsmParser: skipping nanos: " + result);
                return result;
            }

        }
    }

    private static double GSM_FRAME_RATE = 50; // Frame rate of Gsm media
                                               // according to GSM specs is kbps
                                               // stream (260 bits every 20 ms)
                                               // that makes frame rate 50

    private static final Logger logger = LoggerSingleton.logger;

    private static InputStream markSupportedInputStream(InputStream is)
    {
        if (is.markSupported())
            return is;
        else
            return new BufferedInputStream(is);
    }

    private static final double nanosToSeconds(double nanos)
    {
        return nanos / 1000000000.0;
    }

    private static final double secondsToNanos(double secs)
    {
        return secs * 1000000000.0;
    }

    private ContentDescriptor[] supportedInputContentDescriptors = new ContentDescriptor[] { new ContentDescriptor(
            FileTypeDescriptor.GSM) };

    private PullDataSource source;

    private PullSourceStreamTrack[] tracks;

    private PullDataSource sourceForReadFrame;

    @Override
    public ContentDescriptor[] getSupportedInputContentDescriptors()
    {
        return supportedInputContentDescriptors;
    }

    @Override
    public Track[] getTracks() throws IOException, BadHeaderException
    {
        return tracks;
    }

    @Override
    public boolean isPositionable()
    {
        return true;
    }

    // @Override
    @Override
    public boolean isRandomAccess()
    {
        return super.isRandomAccess();
    }

    @Override
    public Time setPosition(Time where, int rounding)
    {
        // TODO Make work
        return null;
    }

    @Override
    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        if (!(source instanceof PullDataSource))
            throw new IncompatibleSourceException();

        this.source = (PullDataSource) source;

    }

    @Override
    public void start() throws IOException
    {
        source.start();
        final PullSourceStream[] streamsForFormat = source.getStreams();

        tracks = new PullSourceStreamTrack[streamsForFormat.length];
        for (int i = 0; i < streamsForFormat.length; ++i)
        {
            tracks[i] = new PullSourceStreamTrack(streamsForFormat[i]);
        }
    }

    @Override
    public void stop()
    {
        try
        {
            source.stop();
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }
    }

}