package net.sf.fmj.theora_java;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.Buffer;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;
import net.sf.theora_java.jheora.utils.*;

import com.fluendo.jheora.*;
import com.jcraft.jogg.*;

/**
 * Uses jheora,jogg,jorbis to parse Ogg files, and decode vorbis and theora data
 * within them. Adapted from theora-java's jheora
 * net.sf.theora_java.jheora.example.PlayerExample, which is adapted from
 * player_example.c.
 *
 * @author Ken Larson
 *
 */
public class JavaOggParser extends AbstractDemultiplexer
{
    private class AudioTrack extends PullSourceStreamTrack
    {
        // TODO: track listener

        private final AudioFormat format;

        public AudioTrack() throws ResourceUnavailableException
        {
            super();

            audiofd_fragsize = 10000; // TODO: this is just a hack
            audiobuf = new short[audiofd_fragsize / 2]; // audiofd_fragsize is
                                                        // in bytes, so divide
                                                        // by two to get shorts

            synchronized (OGG_SYNC_OBJ)
            {
                format = convertCodecAudioFormat(vi);
            }

        }

        @Override
        public void deallocate()
        {
        }

        @Override
        public Time getDuration()
        {
            return Duration.DURATION_UNKNOWN; // TODO
        }

        @Override
        public Format getFormat()
        {
            return format;
        }

        // TODO: from JAVADOC:
        // This method might block if the data for a complete frame is not
        // available. It might also block if the stream contains intervening
        // data for a different interleaved Track. Once the other Track is read
        // by a readFrame call from a different thread, this method can read the
        // frame. If the intervening Track has been disabled, data for that
        // Track is read and discarded.
        //
        // Note: This scenario is necessary only if a PullDataSource
        // Demultiplexer implementation wants to avoid buffering data locally
        // and copying the data to the Buffer passed in as a parameter.
        // Implementations might decide to buffer data and not block (if
        // possible) and incur data copy overhead.

        @Override
        public Time mapFrameToTime(int frameNumber)
        {
            return TIME_UNKNOWN;
        }

        @Override
        public int mapTimeToFrame(Time t)
        {
            return FRAME_UNKNOWN;
        }

        @Override
        public void readFrame(Buffer buffer)
        {
            synchronized (OGG_SYNC_OBJ)
            {
                try
                {
                    nextAudioBuffer(); // TODO: this often generates discard
                                       // buffers, we could be smarter about it.
                                       // Same for video.
                } catch (IOException e)
                {
                    buffer.setLength(0);
                    buffer.setDiscard(true);
                    throw new RuntimeException(e); // TODO: how to handle?
                }

                /* If playback has begun, top audio buffer off immediately. */
                if (stateflag == 0)
                {
                    buffer.setEOM(eomAudio);
                    buffer.setLength(0);
                    if (!eomAudio)
                        buffer.setDiscard(true);

                    return;
                } else
                {
                    if (audiobuf_ready == 0)
                    {
                        buffer.setEOM(eomAudio);
                        buffer.setLength(0);
                        if (!eomAudio)
                            buffer.setDiscard(true);
                        // System.out.println("Generated discard buffer: ");
                        return;
                    } else
                    {
                        // convert from short array to byte array. TODO:
                        // inefficient, should just store in byte array to begin
                        // with.
                        final byte[] data = new byte[audiobuf.length * 2];
                        for (int i = 0; i < audiobuf.length; ++i)
                        {
                            // little-endian:
                            data[i * 2] = (byte) (audiobuf[i] & 0xff);
                            data[i * 2 + 1] = (byte) ((audiobuf[i] >> 8) & 0xff);
                        }

                        buffer.setData(data);
                        buffer.setLength(data.length);
                        buffer.setOffset(0);
                        buffer.setEOM(false);
                        buffer.setDiscard(false);
                        buffer.setTimeStamp(System.currentTimeMillis()); // TODO

                        // System.out.println("Generated audio buffer: " +
                        // data.length);

                        audiobuf_fill = 0;
                        audiobuf_ready = 0;

                    }
                }
            }
        }
    }

    private abstract class PullSourceStreamTrack extends AbstractTrack
    {
        public abstract void deallocate();

    }

    private class VideoTrack extends PullSourceStreamTrack
    {
        // TODO: track listener

        private final VideoFormat format;

        public VideoTrack() throws ResourceUnavailableException
        {
            super();

            synchronized (OGG_SYNC_OBJ)
            {
                // set format
                format = convertCodecPixelFormat(ti);
            }
        }

        @Override
        public void deallocate()
        {
        }

        @Override
        public Time getDuration()
        {
            return Duration.DURATION_UNKNOWN; // TODO
        }

        @Override
        public Format getFormat()
        {
            return format;
        }

        // TODO: from JAVADOC:
        // This method might block if the data for a complete frame is not
        // available. It might also block if the stream contains intervening
        // data for a different interleaved Track. Once the other Track is read
        // by a readFrame call from a different thread, this method can read the
        // frame. If the intervening Track has been disabled, data for that
        // Track is read and discarded.
        //
        // Note: This scenario is necessary only if a PullDataSource
        // Demultiplexer implementation wants to avoid buffering data locally
        // and copying the data to the Buffer passed in as a parameter.
        // Implementations might decide to buffer data and not block (if
        // possible) and incur data copy overhead.

        @Override
        public Time mapFrameToTime(int frameNumber)
        {
            return TIME_UNKNOWN;
        }

        @Override
        public int mapTimeToFrame(Time t)
        {
            return FRAME_UNKNOWN;
        }

        @Override
        public void readFrame(Buffer buffer)
        {
            synchronized (OGG_SYNC_OBJ)
            {
                try
                {
                    nextVideoBuffer();
                } catch (IOException e)
                {
                    buffer.setLength(0);
                    buffer.setDiscard(true);
                    throw new RuntimeException(e); // TODO: how to handle?
                }

                /* are we at or past time for this video frame? */
                if (stateflag != 0 && videobuf_ready != 0
                // && videobuf_time<=get_time()
                )
                {
                    final YUVBuffer yuv = new YUVBuffer();

                    td.decodeYUVout(yuv);

                    final BufferedImage bi = YUVConverter.toBufferedImage(yuv,
                            ti);

                    final Buffer b = net.sf.fmj.media.util.ImageToBuffer
                            .createBuffer(bi, format.getFrameRate());

                    buffer.setData(b.getData());
                    buffer.setLength(b.getLength());
                    buffer.setOffset(b.getOffset());
                    buffer.setEOM(false);
                    buffer.setDiscard(false);
                    buffer.setTimeStamp((long) secondsToNanos(videobuf_time));

                    // System.out.println("Generated video buffer");
                    videobuf_ready = 0;
                } else
                {
                    buffer.setEOM(eomVideo);
                    buffer.setLength(0);
                    if (!eomVideo)
                        buffer.setDiscard(true);

                }
            }

        }
    }

    private static final Logger logger = LoggerSingleton.logger;
    private static final boolean ENABLE_VIDEO = true;
    private static final boolean ENABLE_AUDIO = true;

    public static AudioFormat convertCodecAudioFormat(com.jcraft.jorbis.Info vi)
    {
        return new AudioFormat(AudioFormat.LINEAR, vi.rate, 16, vi.channels,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    }

    public static VideoFormat convertCodecPixelFormat(com.fluendo.jheora.Info ti)
    {
        // resulting format based on what YUVConverter will return. Depends on a
        // bit of internal
        // knowledge of how YUVConverter works.

        // TODO: we are ignoring any cropping here.

        final Dimension size = new Dimension(ti.width, ti.height);
        final int maxDataLength = ti.width * ti.height;
        final Class<?> dataType = int[].class;
        final int bitsPerPixel = 32;
        final float frameRate = (float) ti.fps_numerator
                / (float) ti.fps_denominator;

        final int red;
        final int green;
        final int blue;

        // YUVConverter returns TYPE_INT_RGB
        final int bufferedImageType = BufferedImage.TYPE_INT_ARGB;

        if (bufferedImageType == BufferedImage.TYPE_INT_BGR)
        {
            // TODO: test
            red = 0xFF;
            green = 0xFF00;
            blue = 0xFF0000;
        } else if (bufferedImageType == BufferedImage.TYPE_INT_RGB)
        {
            red = 0xFF0000;
            green = 0xFF00;
            blue = 0xFF;
        } else if (bufferedImageType == BufferedImage.TYPE_INT_ARGB)
        {
            red = 0xFF0000;
            green = 0xFF00;
            blue = 0xFF;
            // just ignore alpha
        } else
            throw new IllegalArgumentException(
                    "Unsupported buffered image type: " + bufferedImageType);

        return new RGBFormat(size, maxDataLength, dataType, frameRate,
                bitsPerPixel, red, green, blue);

    }

    /** dump the theora (or vorbis) comment header */
    private static int dump_comments(com.fluendo.jheora.Comment tc)
    {
        int i, len;

        logger.info("Encoded by " + tc.vendor);
        if (tc.user_comments != null && tc.user_comments.length > 0)
        {
            logger.info("theora comment header:");
            for (i = 0; i < tc.user_comments.length; i++)
            {
                if (tc.user_comments[i] != null)
                {
                    final String value = tc.user_comments[i];
                    logger.info("\t" + value);
                }
            }
        }
        return (0);
    }

    static int getSerialNo(StreamState ss)
    {
        return -1; // TODO: inaccessible with jorbis
        // TODO: we could use reflection
    }

    /**
     * Report the encoder-specified colorspace for the video, if any. We don't
     * actually make use of the information in this example; a real player
     * should attempt to perform color correction for whatever display device it
     * supports.
     */

    private static void report_colorspace(com.fluendo.jheora.Info ti)
    {
        if (ti.colorspace == Colorspace.UNSPECIFIED)
        {
            /* nothing to report */
        } else if (ti.colorspace == Colorspace.ITU_REC_470M)
        {
            logger.info("  encoder specified ITU Rec 470M (NTSC) color.");
        } else if (ti.colorspace == Colorspace.ITU_REC_470BG)
        {
            logger.info("  encoder specified ITU Rec 470BG (PAL) color.");
        } else
        {
            logger.warning("warning: encoder specified unknown colorspace ("
                    + ti.colorspace + ")");
        }
    }

    private static final double secondsToNanos(double secs)
    {
        return secs * 1000000000.0;
    }

    /* never forget that globals are a one-way ticket to Hell */
    /* Ogg and codec state for demux/decode */
    private final SyncState oy = new SyncState();
    private final Page og = new Page();

    private StreamState vo = new StreamState();
    private StreamState to = new StreamState();
    private final com.fluendo.jheora.Info ti = new com.fluendo.jheora.Info();

    private final com.fluendo.jheora.Comment tc = new com.fluendo.jheora.Comment();
    private final com.fluendo.jheora.State td = new com.fluendo.jheora.State();
    private final com.jcraft.jorbis.Info vi = new com.jcraft.jorbis.Info();

    private final com.jcraft.jorbis.DspState vd = new com.jcraft.jorbis.DspState();
    private final com.jcraft.jorbis.Block vb = new com.jcraft.jorbis.Block(vd);
    private com.jcraft.jorbis.Comment vc = new com.jcraft.jorbis.Comment();
    private int theora_p = 0;

    private int vorbis_p = 0;

    private int stateflag = 0;

    /* single frame video buffering */
    private int videobuf_ready = 0;

    private long /* ogg_int64_t */videobuf_granulepos = -1;

    // also, once this parser is able to simply extract the tracks, rather than
    // just decode the data,
    // information on the container-less mime types is available at
    // http://wiki.xiph.org/index.php/MIME_Types_and_File_Extensions

    private double videobuf_time = 0; // in seconds

    /* single audio fragment audio buffering */
    private int audiobuf_fill = 0;

    private int audiobuf_ready = 0;

    private short[] audiobuf;

    private long /* ogg_int64_t */audiobuf_granulepos = 0; /*
                                                            * time position of
                                                            * last sample
                                                            */

    /** In bytes. */
    private int audiofd_fragsize; /*
                                   * read and write only complete fragments so
                                   * that SNDCTL_DSP_GETOSPACE is accurate
                                   * immediately after a bank switch
                                   */

    private final Packet op = new Packet();

    // if USE_DATASOURCE_URL_ONLY is true, this is a bit of a hack - we don't
    // really use the DataSource, we just grab its URL. So arbitrary data
    // sources won't work.
    private final boolean USE_DATASOURCE_URL_ONLY = false;
    private ContentDescriptor[] supportedInputContentDescriptors = new ContentDescriptor[] {
            new ContentDescriptor("video.ogg"),
            new ContentDescriptor("audio.ogg"),
            new ContentDescriptor("application.ogg"),
            new ContentDescriptor("application.x_ogg"),

    // TODO: content descriptors are problematic, because an .ogg file will be
    // interpreted as an audio file, and
    // another handler may try it.

    // See http://wiki.xiph.org/index.php/MIME_Types_and_File_Extensions
    // for mime type info.
    };

    private static final Object OGG_SYNC_OBJ = new Boolean(true); // synchronize
                                                                  // on this
                                                                  // before
                                                                  // using the
                                                                  // libraries,
                                                                  // to prevent
                                                                  // threading
                                                                  // problems.

    private PullDataSource source;

    private PullSourceStreamTrack[] tracks;

    private FileInputStream infile;

    private PullSourceStream instream;

    private boolean eomAudio; // set to true on end of media

    private boolean eomVideo; // set to true on end of media

    private int videoFrameNo = -1;

    public JavaOggParser()
    {
        super();
    }

    private int buffer_data(FileInputStream in, SyncState oy)
            throws IOException
    {
        final int BUFSIZE = 4096;
        int fill = oy.buffer(BUFSIZE);
        byte[] buffer2 = oy.data;
        int bytes = in.read(buffer2, fill, BUFSIZE);
        if (bytes < 0)
            return bytes; // EOF
        oy.wrote(bytes);
        return (bytes);
    }

    // TODO: should we stop data source in stop?
    // // @Override
    // public void stop()
    // {
    // try
    // {
    // source.stop();
    // } catch (IOException e)
    // {
    // logger.log(Level.WARNING, "" + e, e);
    // }
    // }

    private int buffer_data(PullSourceStream in, SyncState oy)
            throws IOException
    {
        final int BUFSIZE = 4096;
        int fill = oy.buffer(BUFSIZE);
        byte[] buffer2 = oy.data;
        int bytes = in.read(buffer2, fill, BUFSIZE);
        if (bytes < 0)
            return bytes; // EOF
        oy.wrote(bytes);
        return (bytes);
    }

    // @Override
    // public Time setPosition(Time where, int rounding)
    // {
    // synchronized (OGG_SYNC_OBJ)
    // {
    //
    // }
    // }

    private int buffer_data(SyncState oy) throws IOException
    {
        if (USE_DATASOURCE_URL_ONLY)
            return buffer_data(infile, oy);
        else
            return buffer_data(instream, oy);
    }

    @Override
    public void close()
    {
        synchronized (OGG_SYNC_OBJ)
        {
            if (tracks != null)
            {
                for (int i = 0; i < tracks.length; ++i)
                {
                    if (tracks[i] != null)
                    {
                        tracks[i].deallocate();
                        tracks[i] = null;
                    }
                }
                tracks = null;
            }

            if (vorbis_p != 0)
            {
                vo.clear();
                vb.clear();
                vd.clear();
                // vc.clear();
                vi.clear();

            }
            if (theora_p != 0)
            {
                to.clear();
                td.clear();
                // tc.clear();
                ti.clear();
            }
            oy.clear();

            try
            {
                if (infile != null)
                    infile.close();
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
            }

            resetVars(); // this will prevent vi.clear from being called twice,
                         // which can cause an NPE
        }
        super.close();
    }

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
        return false; // TODO
    }

    @Override
    public boolean isRandomAccess()
    {
        return super.isRandomAccess(); // TODO: can we determine this from the
                                       // data source?
    }

    private void nextAudioBuffer() throws IOException
    {
        synchronized (OGG_SYNC_OBJ)
        {
            int i;
            int j;
            /*
             * we want a video and audio frame ready to go at all times. If we
             * have to buffer incoming, buffer the compressed data (ie, let ogg
             * do the buffering)
             */
            while (vorbis_p != 0 && audiobuf_ready == 0)
            {
                int ret;
                final float[][][] pcm = new float[1][][];
                final int[] index = new int[vi.channels];

                /* if there's pending, decoded audio, grab it */
                if ((ret = vd.synthesis_pcmout(pcm, index)) > 0)
                {
                    final float[][] floatArrays = pcm[0];

                    int count = audiobuf_fill / 2;
                    final int maxsamples = (audiofd_fragsize - audiobuf_fill)
                            / 2 / vi.channels;
                    for (i = 0; i < ret && i < maxsamples; i++)
                    {
                        for (j = 0; j < vi.channels; j++)
                        {
                            int val = Math
                                    .round(floatArrays[j][index[j] + i] * 32767.f);
                            if (val > 32767)
                                val = 32767;
                            if (val < -32768)
                                val = -32768;
                            audiobuf[count++] = (short) val;
                        }
                    }

                    vd.synthesis_read(i);
                    audiobuf_fill += i * vi.channels * 2;
                    if (audiobuf_fill == audiofd_fragsize)
                        audiobuf_ready = 1;
                    // TODO: these fields are inaccessible with jorbis:
                    // if(vd.granulepos>=0)
                    // audiobuf_granulepos=vd.granulepos-ret+i;
                    // else
                    // audiobuf_granulepos+=i;

                } else
                {
                    /* no pending audio; is there a pending packet to decode? */
                    if (vo.packetout(op) > 0)
                    {
                        if (vb.synthesis(op) == 0) /* test for success! */
                            vd.synthesis_blockin(vb);
                    } else
                        /* we need more data; break out to suck in another page */
                        break;
                }
            }
            // if(videobuf_ready == 0 && audiobuf_ready == 0 &&
            // feof(infile))break;

            if (audiobuf_ready == 0)
            {
                /* no data yet for somebody. Grab another page */
                int bytes = buffer_data(oy);
                if (bytes < 0)
                {
                    eomAudio = true;
                }
                while (oy.pageout(og) > 0)
                {
                    queue_page(og);
                }
            }

            // /* If playback has begun, top audio buffer off immediately. */
            // if(stateflag != 0) audio_write_nonblocking();
            //
            // /* are we at or past time for this video frame? */
            // if(stateflag != 0 && videobuf_ready != 0
            // // && videobuf_time<=get_time()
            // )
            // {
            // video_write();
            // videobuf_ready=0;
            // }

            /*
             * if our buffers either don't exist or are ready to go, we can
             * begin playback
             */
            if ((vorbis_p == 0 || audiobuf_ready != 0))
                stateflag = 1;

            // /* same if we've run out of input */
            // if(feof(infile))stateflag=1;
        }
    }

    private void nextVideoBuffer() throws IOException
    {
        synchronized (OGG_SYNC_OBJ)
        {
            int i;
            int j;

            while (theora_p != 0 && videobuf_ready == 0)
            {
                /* theora is one in, one out... */
                if (to.packetout(op) > 0)
                {
                    ++videoFrameNo;

                    final int ret = td.decodePacketin(op);
                    if (ret < 0)
                        throw new IOException("decodePacketin failed: " + ret);
                    videobuf_granulepos = td.granulepos;

                    videobuf_time = td.granuleTime(videobuf_granulepos);
                    if (videobuf_time == 0.0)
                    { // TODO: for some reason, some videos, like
                      // Apollo_15_liftoff_from_inside_LM.ogg (available from
                      // wikimedia)
                      // always report the videobuf_time as zero. So we'll just
                      // calculate it based on the frame rate and
                      // the frame number.
                        videobuf_time = (double) videoFrameNo
                                * (double) ti.fps_denominator
                                / ti.fps_numerator;
                    }

                    // /* is it already too old to be useful? This is only
                    // actually
                    // useful cosmetically after a SIGSTOP. Note that we have to
                    // decode the frame even if we don't show it (for now) due
                    // to
                    // keyframing. Soon enough libtheora will be able to deal
                    // with non-keyframe seeks. */
                    //
                    // if(videobuf_time>=get_time())
                    videobuf_ready = 1;

                } else
                    break;
            }

            // if(videobuf_ready == 0 && audiobuf_ready == 0 &&
            // feof(infile))break;

            if (videobuf_ready == 0)
            {
                /* no data yet for somebody. Grab another page */
                int bytes = buffer_data(oy);
                if (bytes < 0)
                {
                    eomVideo = true;
                }
                while (oy.pageout(og) > 0)
                {
                    queue_page(og);
                }
            }

            // /* If playback has begun, top audio buffer off immediately. */
            // if(stateflag != 0) audio_write_nonblocking();
            //
            // /* are we at or past time for this video frame? */
            // if(stateflag != 0 && videobuf_ready != 0
            // // && videobuf_time<=get_time()
            // )
            // {
            // video_write();
            // videobuf_ready=0;
            // }

            /*
             * if our buffers either don't exist or are ready to go, we can
             * begin playback
             */
            if ((theora_p == 0 || videobuf_ready != 0))
                stateflag = 1;

            // /* same if we've run out of input */
            // if(feof(infile))stateflag=1;
        }
    }

    // @Override
    @Override
    public void open() throws ResourceUnavailableException
    {
        synchronized (OGG_SYNC_OBJ)
        {
            resetVars();

            try
            {
                final String urlStr;

                if (USE_DATASOURCE_URL_ONLY)
                {
                    // just use the file URL from the datasource
                    final File f = new File(
                            URLUtils.extractValidPathFromFileUrl(source
                                    .getLocator().toExternalForm()));
                    infile = new FileInputStream(f);
                } else
                {
                    source.connect();
                    source.start(); // TODO: stop/disconnect on stop/close.
                    instream = source.getStreams()[0];

                }

                /* start up Ogg stream synchronization layer */
                oy.init();

                /* init supporting Vorbis structures needed in header parsing */
                vi.init();
                vc.init();

                /* init supporting Theora structures needed in header parsing */
                // tc.init();
                // ti.init();

                // System.out.println("Parsing headers...");
                /* Ogg file open; parse the headers */
                /* Only interested in Vorbis/Theora streams */
                while (stateflag == 0)
                {
                    int ret = buffer_data(oy);
                    if (ret <= 0)
                        break;
                    while (oy.pageout(og) > 0)
                    {
                        StreamState test = new StreamState();

                        /*
                         * is this a mandated initial header? If not, stop
                         * parsing
                         */
                        if (og.bos() == 0)
                        {
                            /*
                             * don't leak the page; get it into the appropriate
                             * stream
                             */
                            queue_page(og);
                            stateflag = 1;
                            break;
                        }

                        test.init(og.serialno());
                        test.pagein(og);
                        test.packetout(op);

                        /* identify the codec: try theora */
                        if (ENABLE_VIDEO && theora_p == 0
                                && ti.decodeHeader(tc, op) >= 0)
                        {
                            /* it is theora */
                            to = test;
                            theora_p = 1;
                        } else if (ENABLE_AUDIO && vorbis_p == 0
                                && vi.synthesis_headerin(vc, op) >= 0)
                        {
                            /* it is vorbis */
                            vo = test;
                            // memcpy(&vo,&test,sizeof(test));
                            vorbis_p = 1;
                        } else
                        {
                            /* whatever it is, we don't care about it */
                            test.clear();
                        }
                    }
                    /* fall through to non-bos page parsing */
                }

                /* we're expecting more header packets. */
                while ((theora_p != 0 && theora_p < 3)
                        || (vorbis_p != 0 && vorbis_p < 3))
                {
                    int ret;

                    /* look for further theora headers */
                    while (theora_p != 0 && (theora_p < 3)
                            && ((ret = to.packetout(op))) != 0)
                    {
                        if (ret < 0)
                        {
                            throw new ResourceUnavailableException(
                                    "Error parsing Theora stream headers; corrupt stream?");
                        }
                        if (ti.decodeHeader(tc, op) != 0)
                        {
                            throw new ResourceUnavailableException(
                                    "Error parsing Theora stream headers; corrupt stream?");
                        }
                        theora_p++;
                        if (theora_p == 3)
                            break;
                    }

                    /* look for more vorbis header packets */
                    while (vorbis_p != 0 && (vorbis_p < 3)
                            && ((ret = vo.packetout(op))) != 0)
                    {
                        if (ret < 0)
                        {
                            throw new ResourceUnavailableException(
                                    "Error parsing Vorbis stream headers; corrupt stream?");
                        }

                        if (vi.synthesis_headerin(vc, op) != 0)
                        {
                            throw new ResourceUnavailableException(
                                    "Error parsing Vorbis stream headers; corrupt stream?");
                        }
                        vorbis_p++;
                        if (vorbis_p == 3)
                            break;
                    }

                    /*
                     * The header pages/packets will arrive before anything else
                     * we care about, or the stream is not obeying spec
                     */

                    if (oy.pageout(og) > 0)
                    {
                        queue_page(og); /* demux into the appropriate stream */
                    } else
                    {
                        final int ret2 = buffer_data(oy); /*
                                                           * someone needs more
                                                           * data
                                                           */
                        if (ret2 <= 0)
                        {
                            throw new ResourceUnavailableException(
                                    "End of file while searching for codec headers.");
                        }
                    }
                }

                // System.out.println("Initializing decoders...");

                /* and now we have it all. initialize decoders */
                if (theora_p != 0)
                {
                    td.decodeInit(ti);
                    final double fps = (double) ti.fps_numerator
                            / (double) ti.fps_denominator;
                    logger.info("Ogg logical stream "
                            + Integer.toHexString(getSerialNo(to))
                            + " is Theora " + ti.width + "x" + ti.height + " "
                            + fps + " fps");
                    // TODO: jheora doesn't have pixelformat as a field of ti:
                    // switch(ti.pixelformat){
                    // case TheoraLibrary.OC_PF_420:
                    // System.out.printf(" 4:2:0 video\n"); break;
                    // case TheoraLibrary.OC_PF_422:
                    // System.out.printf(" 4:2:2 video\n"); break;
                    // case TheoraLibrary.OC_PF_444:
                    // System.out.printf(" 4:4:4 video\n"); break;
                    // case TheoraLibrary.OC_PF_RSVD:
                    // default:
                    // System.out.printf(" video\n  (UNKNOWN Chroma sampling!)\n");
                    // break;
                    // }
                    if (ti.width != ti.frame_width
                            || ti.height != ti.frame_height)
                    {
                        logger.warning("  Frame content is " + ti.frame_width
                                + "x" + ti.frame_height + " with offset ("
                                + ti.offset_x + "," + ti.offset_y + ").");
                        // TODO: we need to handle cropping properly.
                    }
                    report_colorspace(ti);
                    dump_comments(tc);
                } else
                {
                    /* tear down the partial theora setup */
                    ti.clear();
                    // tc.clear();
                }
                if (vorbis_p != 0)
                {
                    vd.synthesis_init(vi);
                    vb.init(vd);
                    logger.info("Ogg logical stream "
                            + Integer.toHexString(getSerialNo(vo))
                            + " is Vorbis " + vi.channels + " channel "
                            + vi.rate + " Hz audio.");

                } else
                {
                    /* tear down the partial vorbis setup */
                    vi.clear();
                    // vc.clear();
                }

                stateflag = 0; /* playback has not begun */

                VideoTrack videoTrack = null;
                AudioTrack audioTrack = null;

                if (theora_p != 0)
                {
                    videoTrack = new VideoTrack();
                }
                if (vorbis_p != 0)
                {
                    audioTrack = new AudioTrack();
                }

                if (audioTrack == null && videoTrack == null)
                    throw new ResourceUnavailableException(
                            "No audio or video track found");
                else if (audioTrack != null && videoTrack != null)
                    tracks = new PullSourceStreamTrack[] { videoTrack,
                            audioTrack };
                else if (audioTrack != null)
                    tracks = new PullSourceStreamTrack[] { audioTrack };
                else
                    tracks = new PullSourceStreamTrack[] { videoTrack };

            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                throw new ResourceUnavailableException("" + e);
            }
        }

        super.open();
    }

    /**
     * helper: push a page into the appropriate steam this can be done blindly;
     * a stream won't accept a page that doesn't belong to it
     */
    private int queue_page(Page page)
    {
        if (theora_p != 0)
            to.pagein(og);
        if (vorbis_p != 0)
            vo.pagein(og);
        return 0;
    }

    private void resetVars()
    {
        theora_p = 0;
        vorbis_p = 0;
        stateflag = 0;

        /* single frame video buffering */
        videobuf_ready = 0;
        videobuf_granulepos = -1;
        videobuf_time = 0; // in seconds

        /* single audio fragment audio buffering */
        audiobuf_fill = 0;
        audiobuf_ready = 0;
        audiobuf = null;
        audiobuf_granulepos = 0; /* time position of last sample */

        // TODO: we might want to re-init others to be sure.
    }

    @Override
    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        final String protocol = source.getLocator().getProtocol();

        if (USE_DATASOURCE_URL_ONLY)
        {
            if (!(protocol.equals("file")))
                throw new IncompatibleSourceException();

        } else
        {
            if (!(source instanceof PullDataSource))
                throw new IncompatibleSourceException();
        }

        this.source = (PullDataSource) source;

    }

    // @Override
    @Override
    public void start() throws IOException
    {
    }
}
