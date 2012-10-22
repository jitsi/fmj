package net.sf.fmj.media.parser;

import java.io.IOException;

import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Demultiplexer;
import javax.media.Duration;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Time;
import javax.media.Track;
import javax.media.TrackListener;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.Positionable;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;
import javax.media.protocol.Seekable;
import javax.media.protocol.SourceStream;

/**
 * Demultiplexer for GSM file format
 */

/**
 * GSM
 * 8000 samples per sec.
 * 160 samples represent 20 milliseconds and GSM represents them
 * in 33 bytes. So frameSize is 33 bytes and there are 50 frames
 * in one second. One second is 1650 bytes.
 */
/**
 * Adapted from http://java.sun.com/products/java-media/jmf/2.1.1/guide/JMFApp-Demux.html#87606
 * 
 *
 */
public class GsmParser implements Demultiplexer {
	
	private static final int LENGTH_DISCARD = -2;
    private Time duration = Duration.DURATION_UNKNOWN;
    private Format format = null;
    private Track[] tracks = new Track[1]; // Only 1 track is there for Gsm
    private int numBuffers = 4;
    private int bufferSize;
    private int dataSize;
    private int encoding;
    private String encodingString;
    private int sampleRate;
    private int samplesPerBlock;
    private int bytesPerSecond = 1650; // 33 * 50
    private int blockSize = 33;

    private int maxFrame = Integer.MAX_VALUE;
    private long minLocation;
    private long maxLocation;
    private PullSourceStream stream = null;
    private long currentLocation = 0;

    protected DataSource source;
    protected SourceStream[] streams;
    protected boolean seekable = false;
    protected boolean positionable = false;
    private Object sync = new Object(); // synchronizing variable

    private static ContentDescriptor[] supportedFormat =
        new ContentDescriptor[] {new ContentDescriptor("audio.x_gsm")};

    public ContentDescriptor [] getSupportedInputContentDescriptors() {
        return supportedFormat;
    }

    public void setSource(DataSource source)
        throws IOException, IncompatibleSourceException {

        if (!(source instanceof PullDataSource)) {
            throw new IncompatibleSourceException("DataSource not supported: " + source);
        } else {
            streams = ((PullDataSource) source).getStreams();
        }

        if ( streams == null) {
            throw new IOException("Got a null stream from the DataSource");
        }

        if (streams.length == 0) {
            throw new IOException("Got a empty stream array from the DataSource");
        }
        this.source = source;
        
        positionable =  (streams[0] instanceof Seekable);
        seekable =  positionable && ((Seekable)
                   streams[0]).isRandomAccess();

        if (!supports(streams))
            throw new IncompatibleSourceException("DataSource not supported: " + source);
    }

    /**
     * A Demultiplexer may support pull only or push only or both
     * pull and push streams.

     * Some Demultiplexer may have other requirements.
     * For e.g a quicktime Demultiplexer imposes an additional 
     * requirement that
     * isSeekable() and isRandomAccess() be true
     */
    protected boolean supports(SourceStream[] streams) {
        return ( (streams[0] != null) &&
                 (streams[0] instanceof PullSourceStream) );    
    }

    public boolean isPositionable() {
        return positionable;
    }

    public boolean isRandomAccess() {
        return seekable;
    }

    /**
     * Opens the plug-in software or hardware component and acquires
     * necessary resources. If all the needed resources could not be
     * acquired, it throws a ResourceUnavailableException. Data should not
     * be passed into the plug-in without first calling this method.
     */
    public void open() {
        // throws ResourceUnavailableException;
    }

    /**
     * Closes the plug-in component and releases resources. No more data
     * will be accepted by the plug-in after a call to this method. The
     * plug-in can be reinstated after being closed by calling
     * <tt>open</tt>.
     */
    public void close() {
        if (source != null) {
            try {
                source.stop();
                source.disconnect();
            } catch (IOException e) {
                // Internal error?
            }
            source = null;
        }
    }

    /**
     * This get called when the player/processor is started.
     */
    public void start() throws IOException {
         if (source != null)
             source.start();
    }

    /**
     * This get called when the player/processor is stopped.
     */
    public void stop() {
        if (source != null) {
            try {
                source.stop();
            } catch (IOException e) {
                // Internal errors?
            }
        }
    }

    /**
     * Resets the state of the plug-in. Typically at end of media 
     * or when media is repositioned.
     */
    public void reset() {
    }

    public Track[] getTracks() throws IOException, BadHeaderException {

        if (tracks[0] != null)
            return tracks;        
        stream = (PullSourceStream) streams[0];
        readHeader();
        bufferSize = bytesPerSecond;
        tracks[0] = new GsmTrack((AudioFormat) format,
                                /*enabled=*/ true,
                                 new Time(0),
                                 numBuffers,
                                 bufferSize,
                                 minLocation,
                                 maxLocation
                                 );
        return tracks;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String controlType) {
        return null;
    }
    private void /* for now void */ readHeader()
        throws IOException, BadHeaderException {

        minLocation = getLocation(stream); // Should be zero

        long contentLength = stream.getContentLength();
        if ( contentLength != SourceStream.LENGTH_UNKNOWN ) {
            double durationSeconds = contentLength / bytesPerSecond;


            duration = new Time(durationSeconds);
            maxLocation = contentLength;

        } else {
            maxLocation = Long.MAX_VALUE;
        }

        boolean signed = true;
        boolean bigEndian = false;
        format = new AudioFormat(AudioFormat.GSM,
                                 8000,  // sampleRate,
                                 16,    // sampleSizeInBits,
                                 1,     // channels,
                                 bigEndian ? AudioFormat.BIG_ENDIAN : 
                                AudioFormat.LITTLE_ENDIAN,
                                 signed ? AudioFormat.SIGNED : 
                                AudioFormat.UNSIGNED,
                                 (blockSize * 8), // frameSizeInBits
                                 Format.NOT_SPECIFIED,  
                                 Format.byteArray);
    }

    // Contains 1 audio track
    public String getTrackLayout() {
        return "A";
    }

    public Time setPosition(Time where, int rounding) {
        if (! seekable ) {
            return getMediaTime();
        }

        long time = where.getNanoseconds();
        long newPos;

        if (time < 0)
            time = 0;

        double newPosd = time * bytesPerSecond / 1000000000.0;
        double remainder = (newPosd % blockSize);
        
        newPos = (long) (newPosd - remainder);

        if (remainder > 0) {
            switch (rounding) {
            case Positionable.RoundUp:
                newPos += blockSize;
                break;
            case Positionable.RoundNearest:
                if (remainder > (blockSize / 2.0))
                    newPos += blockSize;

                break;
            }
        }

        if ( newPos > maxLocation )
            newPos = maxLocation;
        
        newPos += minLocation;
        ((BasicTrack) tracks[0]).setSeekLocation(newPos);
        return where;
    }

    public Time getMediaTime() {
        long location;
        long seekLocation = ((BasicTrack) tracks[0]).getSeekLocation();
        if (seekLocation != -1)
            location = seekLocation - minLocation;
        else
            location = getLocation(stream) - minLocation;

        return new Time( location / (double) bytesPerSecond );
    }

    public Time getDuration() {
        if ( duration.equals(Duration.DURATION_UNKNOWN) &&
             ( tracks[0] != null ) ) {
            long mediaSizeAtEOM = ((BasicTrack) 
                                 tracks[0]).getMediaSizeAtEOM();
            if (mediaSizeAtEOM > 0) {
                double durationSeconds = mediaSizeAtEOM / bytesPerSecond;
               duration = new Time(durationSeconds);
            }
        }
        return duration;
    }

    /**
     * Returns a descriptive name for the plug-in.
     * This is a user readable string.
     */
    public String getName() {
        return "Parser for raw GSM";
    }

    /**
     * Read numBytes from offset 0
     */
    public int readBytes(PullSourceStream pss, byte[] array,
                         int numBytes) throws IOException {

        return readBytes(pss, array, 0, numBytes);
    }

    public int readBytes(PullSourceStream pss, byte[] array,
                         int offset,
                         int numBytes) throws IOException {
        if (array == null) {
            throw new NullPointerException();
        } else if ((offset < 0) || (offset > array.length) ||  
                  (numBytes < 0) ||
                   ((offset + numBytes) > array.length) || 
                  ((offset + numBytes) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (numBytes == 0) {
            return 0;
        }

        int remainingLength = numBytes;
        int actualRead = 0;

        remainingLength = numBytes;
        while (remainingLength > 0) {

            actualRead = pss.read(array, offset, remainingLength);
            if (actualRead == -1) {// End of stream
                if (offset == 0) {
                    throw new IOException("GsmParser: readBytes(): Reached end of stream while trying to read " + 
                             numBytes + " bytes");
                } else {
                    return offset;
                }
            } else if (actualRead == 
            LENGTH_DISCARD) {
                return 
                  LENGTH_DISCARD;
            } else if (actualRead < 0) {
                throw new IOException("GsmParser: readBytes() read returned " + actualRead);
            }
            remainingLength -= actualRead;
            offset += actualRead;
            synchronized(sync) {
                currentLocation += actualRead;
            }
        }
        return numBytes;
    }

    protected final long getLocation(PullSourceStream pss) {
        synchronized(sync) {
            if ( (pss instanceof Seekable) )
                return ((Seekable)pss).tell();

            else
                return currentLocation;
        }
    }

    ////////////////////////////////////////////////////////////
    // Inner classes begin
    abstract private class BasicTrack implements Track {

        private Format format;
        private boolean enabled = true;
        protected Time duration;
        private Time startTime;
        private int numBuffers;
        private int dataSize;
        private PullSourceStream stream;
        private long minLocation;
        private long maxLocation;
        private long maxStartLocation;
        private GsmParser parser;
        private long sequenceNumber = 0;
        private TrackListener listener;
        private long seekLocation = -1L;
        private long mediaSizeAtEOM = -1L; // update when EOM 
                                          // implied by IOException occurs

        BasicTrack(GsmParser parser,
                   Format format, boolean enabled, 
                  Time duration, Time startTime,
                   int numBuffers, int dataSize, 
                  PullSourceStream stream) {
            this(parser, format,  enabled,  duration,  startTime,
                 numBuffers, dataSize, stream,
                 0L, Long.MAX_VALUE);
        }


        /**
         * Note to implementors who want to use this class.
         * If the maxLocation is not known, then
         * specify Long.MAX_VALUE for this parameter
         */
        public BasicTrack(GsmParser parser,
                          Format format, boolean enabled, 
                         Time duration, Time startTime,
                          int numBuffers, int dataSize, 
                         PullSourceStream stream,
                          long minLocation, long maxLocation) {

            this.parser = parser;

            this.format = format;
            this.enabled = enabled;
            this.duration = duration;
            this.startTime = startTime;
            this.numBuffers = numBuffers;
            this.dataSize = dataSize;
            this.stream = stream;
            this.minLocation = minLocation;
            this.maxLocation = maxLocation;
            maxStartLocation = maxLocation - dataSize;
        }

        public Format getFormat() {
            return format;
        }

        public void setEnabled(boolean t) {
            enabled = t;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public Time getDuration() {
            return duration;
        }


        public Time getStartTime() {
            return startTime;
        }


        public int getNumberOfBuffers() {
            return numBuffers;
        }


        public void setTrackListener(TrackListener l) {
            listener = l;
        }
    
        public synchronized void setSeekLocation(long location) {
            seekLocation = location;
        }
        public synchronized long getSeekLocation() {
            return seekLocation;
        }

        public void readFrame(Buffer buffer) {
            if (buffer == null)
                return;

            if (!enabled) {
                buffer.setDiscard(true);
                return;
            }

            buffer.setFormat(format); 
            Object obj = buffer.getData();
            byte[] data;
            long location;
            boolean needToSeek;
        
            synchronized(this) {
                if (seekLocation != -1) {
                    location = seekLocation;
                    seekLocation = -1;
                    needToSeek = true;
                } else {
                    location = parser.getLocation(stream);
                    needToSeek = false;
                }
            }

            int needDataSize;

            if (location < minLocation) {
                buffer.setDiscard(true);
                return;
            } else if (location >= maxLocation) {
                buffer.setLength(0);
                buffer.setEOM(true);
                return;
            } else if (location > maxStartLocation) {
                needDataSize = dataSize - (int) (location - 
                              maxStartLocation);
            } else {
                needDataSize = dataSize;
            }

            if  ( (obj == null) ||
                  (! (obj instanceof byte[]) ) ||
                  ( ((byte[])obj).length < needDataSize) ) {
                data = new byte[needDataSize];
                buffer.setData(data);
            } else {
                data = (byte[]) obj;
            }
            try {
                if (needToSeek) {
                    long pos = 
               ((javax.media.protocol.Seekable)stream).seek(location);

                    if ( pos == LENGTH_DISCARD) {
                        buffer.setDiscard(true);
                        return;
                    }
                }
                int actualBytesRead = parser.readBytes(stream, 
                                     data, needDataSize);
                buffer.setOffset(0);
                buffer.setLength(actualBytesRead);
                buffer.setSequenceNumber(++sequenceNumber);
              buffer.setTimeStamp(parser.getMediaTime().getNanoseconds());
            } catch (IOException e) {
                if (maxLocation != Long.MAX_VALUE) {
                    // Known maxLocation. So, this is a case of
                    // deliberately reading past EOM
                    System.err.println("readFrame: EOM " + e);
                    buffer.setLength(0); // Need this??
                    buffer.setEOM(true);
                } else {
                    // Unknown maxLocation, due to unknown content length
                    // EOM reached before the required bytes could be read.
                    long length = parser.streams[0].getContentLength();
                    if ( length != SourceStream.LENGTH_UNKNOWN ) {
                        // If content-length is known, discard this buffer, 
                    // updatemaxLocation, maxStartLocation and 
                    // mediaSizeAtEOM.  The next readFrame will read 
                   // the remaining data till EOM.
                        maxLocation = length;
                        maxStartLocation = maxLocation - dataSize;
                        mediaSizeAtEOM = maxLocation - minLocation;
                        buffer.setLength(0); // Need this??
                        buffer.setDiscard(true);
                    } else {
                        // Content Length is still unknown after an 
                       // IOException.
                        // We can still discard this buffer and keep discarding
                        // until content length is known. But this may go into
                        // into an infinite loop, if there are real IO errors
                        // So, return EOM
                        maxLocation = parser.getLocation(stream);
                        maxStartLocation = maxLocation - dataSize;
                        mediaSizeAtEOM = maxLocation - minLocation;
                        buffer.setLength(0); // Need this??
                        buffer.setEOM(true);
                    }
                }
            }
        }

        public void readKeyFrame(Buffer buffer) {
            readFrame(buffer);
        }

        public boolean willReadFrameBlock() {
            return false;
        }


        public long getMediaSizeAtEOM() {
            return mediaSizeAtEOM; // updated when EOM implied by                     
                             // IOException occurs
        }
    }

    private class GsmTrack extends BasicTrack {
        private double sampleRate;
        private float timePerFrame = 0.020F; // 20 milliseconds

        GsmTrack(AudioFormat format, boolean enabled, Time startTime,
                 int numBuffers, int bufferSize,
                 long minLocation, long maxLocation) {
            super(GsmParser.this, 
                  format, enabled, GsmParser.this.duration,
                  startTime, numBuffers, bufferSize,
                  GsmParser.this.stream, minLocation, maxLocation);

            double sampleRate = format.getSampleRate();
            int channels = format.getChannels();
            int sampleSizeInBits = format.getSampleSizeInBits();

            float bytesPerSecond;
            float bytesPerFrame;
            float samplesPerFrame;

            long durationNano = this.duration.getNanoseconds();
            if (!( (durationNano ==
                  Duration.DURATION_UNKNOWN.getNanoseconds()) ||
                   (durationNano == 
                   Duration.DURATION_UNBOUNDED.getNanoseconds()) )) {
                maxFrame = mapTimeToFrame(this.duration.getSeconds());
            }
        }

        GsmTrack(AudioFormat format, boolean enabled, Time startTime,
                 int numBuffers, int bufferSize) {
            this(format, enabled,
                 startTime, numBuffers, bufferSize,
                 0L, Long.MAX_VALUE);

        }

        // Frame numbers start from 0
        private int mapTimeToFrame(double time) {
            double frameNumber = time / timePerFrame;
            return (int) frameNumber;
        }

        // Frame numbers start from 0
        // 0-1 ==> 0, 1-2 ==> 1
        public int mapTimeToFrame(Time t) {
            double time = t.getSeconds();
            int frameNumber = mapTimeToFrame(time);
            
            if ( frameNumber > maxFrame)
                frameNumber = maxFrame; // Do we clamp it or return error
            System.out.println("mapTimeToFrame: " + (int) time + " ==> " +
                               frameNumber + " ( " + frameNumber + " )");
            return frameNumber;
        }
        public Time mapFrameToTime(int frameNumber) {
            if (frameNumber > maxFrame)
                frameNumber = maxFrame; // Do we clamp it or return error
            double time = timePerFrame * frameNumber;
            System.out.println("mapFrameToTime: " + frameNumber + " ==> " +
                                   time);
            return new Time(time);
        }
    }
}

