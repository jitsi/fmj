package net.sf.fmj.theora_java;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;
import net.sf.theora_java.jna.*;
import net.sf.theora_java.jna.OggLibrary.ogg_page;
import net.sf.theora_java.jna.OggLibrary.ogg_stream_state;
import net.sf.theora_java.jna.OggLibrary.ogg_sync_state;
import net.sf.theora_java.jna.TheoraLibrary.theora_comment;
import net.sf.theora_java.jna.TheoraLibrary.theora_info;
import net.sf.theora_java.jna.TheoraLibrary.theora_state;
import net.sf.theora_java.jna.TheoraLibrary.yuv_buffer;
import net.sf.theora_java.jna.VorbisLibrary.vorbis_block;
import net.sf.theora_java.jna.VorbisLibrary.vorbis_comment;
import net.sf.theora_java.jna.VorbisLibrary.vorbis_dsp_state;
import net.sf.theora_java.jna.VorbisLibrary.vorbis_info;
import net.sf.theora_java.jna.XiphLibrary.ogg_packet;
import net.sf.theora_java.jna.utils.*;

import com.sun.jna.*;
import com.sun.jna.ptr.*;

/**
 * Uses theora-jna to parse Ogg files, and decode vorbis and theora data within
 * them. Adapted from theora-jna's PlayerExample, which is adapted from
 * player_example.c.
 *
 * @author Ken Larson
 *
 */
public class NativeOggParser extends AbstractDemultiplexer
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
                    final yuv_buffer yuv = new yuv_buffer();

                    THEORA.theora_decode_YUVout(td, yuv);

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

    private final TheoraLibrary THEORA;
    private final OggLibrary OGG;

    private final VorbisLibrary VORBIS;
    private static final boolean ENABLE_VIDEO = true;
    private static final boolean ENABLE_AUDIO = true;
    /* never forget that globals are a one-way ticket to Hell */
    /* Ogg and codec state for demux/decode */
    private final ogg_sync_state oy = new ogg_sync_state();
    private final ogg_page og = new ogg_page();
    private ogg_stream_state vo = new ogg_stream_state();
    private ogg_stream_state to = new ogg_stream_state();
    private final theora_info ti = new theora_info();
    private final theora_comment tc = new theora_comment();
    private final theora_state td = new theora_state();
    private final vorbis_info vi = new vorbis_info();

    private final vorbis_dsp_state vd = new vorbis_dsp_state();
    private final vorbis_block vb = new vorbis_block();
    private vorbis_comment vc = new vorbis_comment();

    private int theora_p = 0;
    private int vorbis_p = 0;
    private int stateflag = 0;

    /* single frame video buffering */
    private int videobuf_ready = 0;
    private long /* ogg_int64_t */videobuf_granulepos = -1;
    private double videobuf_time = 0; // in seconds
    /* single audio fragment audio buffering */
    private int audiobuf_fill = 0;

    private int audiobuf_ready = 0;

    private short[] audiobuf;

    /** In bytes. */
    private int audiofd_fragsize; /*
                                   * read and write only complete fragments so
                                   * that SNDCTL_DSP_GETOSPACE is accurate
                                   * immediately after a bank switch
                                   */

    // also, once this parser is able to simply extract the tracks, rather than
    // just decode the data,
    // information on the container-less mime types is available at
    // http://wiki.xiph.org/index.php/MIME_Types_and_File_Extensions

    private final ogg_packet op = new ogg_packet();

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

    public static AudioFormat convertCodecAudioFormat(vorbis_info vi)
    {
        return new AudioFormat(AudioFormat.LINEAR, vi.rate.floatValue(), 16,
                vi.channels, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    }

    public static VideoFormat convertCodecPixelFormat(theora_info ti)
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
        final int bufferedImageType = BufferedImage.TYPE_INT_RGB;

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

    /**
     * Report the encoder-specified colorspace for the video, if any. We don't
     * actually make use of the information in this example; a real player
     * should attempt to perform color correction for whatever display device it
     * supports.
     */
    static void report_colorspace(theora_info ti)
    {
        switch (ti.colorspace)
        {
        case TheoraLibrary.OC_CS_UNSPECIFIED:
            /* nothing to report */
            break;
        case TheoraLibrary.OC_CS_ITU_REC_470M:
            logger.info("  encoder specified ITU Rec 470M (NTSC) color.");
            break;
        case TheoraLibrary.OC_CS_ITU_REC_470BG:
            logger.info("  encoder specified ITU Rec 470BG (PAL) color.");
            break;
        default:
            logger.warning("warning: encoder specified unknown colorspace ("
                    + ti.colorspace + ")");
            break;
        }
    }

    private static final double secondsToNanos(double secs)
    {
        return secs * 1000000000.0;
    }

    private PullDataSource source;

    private PullSourceStreamTrack[] tracks;

    private FileInputStream infile;

    private PullSourceStream instream;

    private boolean eomAudio; // set to true on end of media

    private boolean eomVideo; // set to true on end of media

    private int videoFrameNo = -1;

    public NativeOggParser()
    {
        try
        {
            THEORA = TheoraLibrary.INSTANCE;
            OGG = OggLibrary.INSTANCE;
            VORBIS = VorbisLibrary.INSTANCE;
        } catch (Throwable t)
        {
            logger.log(Level.WARNING, "Unable to initialize ffmpeg libraries: "
                    + t);
            throw new RuntimeException(t);
        }
    }

    private int buffer_data(FileInputStream in, ogg_sync_state oy)
            throws IOException
    {
        final int BUFSIZE = 4096;
        Pointer buffer = OGG.ogg_sync_buffer(oy, new NativeLong(BUFSIZE));
        byte[] buffer2 = new byte[BUFSIZE]; // TODO: this is inefficient.
        int bytes = in.read(buffer2, 0, BUFSIZE);
        if (bytes < 0)
            return bytes; // EOF
        buffer.write(0, buffer2, 0, bytes);
        OGG.ogg_sync_wrote(oy, new NativeLong(bytes));
        return (bytes);
    }

    private int buffer_data(ogg_sync_state oy) throws IOException
    {
        if (USE_DATASOURCE_URL_ONLY)
            return buffer_data(infile, oy);
        else
            return buffer_data(instream, oy);
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

    private int buffer_data(PullSourceStream in, ogg_sync_state oy)
            throws IOException
    {
        final int BUFSIZE = 4096;
        Pointer buffer = OGG.ogg_sync_buffer(oy, new NativeLong(BUFSIZE));
        byte[] buffer2 = new byte[BUFSIZE]; // TODO: this is inefficient.
        int bytes = in.read(buffer2, 0, BUFSIZE);
        if (bytes < 0)
            return bytes; // EOF
        buffer.write(0, buffer2, 0, bytes);
        OGG.ogg_sync_wrote(oy, new NativeLong(bytes));
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
                OGG.ogg_stream_clear(vo);
                VORBIS.vorbis_block_clear(vb);
                VORBIS.vorbis_dsp_clear(vd);
                VORBIS.vorbis_comment_clear(vc);
                VORBIS.vorbis_info_clear(vi);
            }
            if (theora_p != 0)
            {
                OGG.ogg_stream_clear(to);
                THEORA.theora_clear(td);
                THEORA.theora_comment_clear(tc);
                THEORA.theora_info_clear(ti);
            }
            OGG.ogg_sync_clear(oy);

            try
            {
                if (infile != null)
                    infile.close();
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
            }
        }
        super.close();
    }

    /** dump the theora (or vorbis) comment header */
    int dump_comments(theora_comment tc)
    {
        int i, len;

        logger.info("Encoded by " + tc.vendor.getString(0));
        if (tc.comments > 0)
        {
            logger.info("theora comment header:");
            for (i = 0; i < tc.comments; i++)
            {
                final Pointer[] user_comments = tc.user_comments
                        .getPointerArray(0, tc.comments);
                final int[] comment_lengths = tc.comment_lengths.getIntArray(0,
                        tc.comments);

                if (user_comments[i] != null)
                {
                    len = comment_lengths[i];
                    final String value = new String(
                            user_comments[i].getByteArray(0, len));
                    logger.info("\t" + value);
                }
            }
        }
        return (0);
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
                final PointerByReference pcm = new PointerByReference();

                /* if there's pending, decoded audio, grab it */
                if ((ret = VORBIS.vorbis_synthesis_pcmout(vd, pcm)) > 0)
                {
                    final Pointer ppChannels = pcm.getValue();
                    final Pointer[] pChannels = ppChannels.getPointerArray(0,
                            vi.channels);

                    final float[][] floatArrays = new float[pChannels.length][];
                    for (int k = 0; k < pChannels.length; ++k)
                    {
                        floatArrays[k] = pChannels[k].getFloatArray(0, ret);
                    }

                    int count = audiobuf_fill / 2;
                    final int maxsamples = (audiofd_fragsize - audiobuf_fill)
                            / 2 / vi.channels;
                    for (i = 0; i < ret && i < maxsamples; i++)
                    {
                        for (j = 0; j < vi.channels; j++)
                        {
                            int val = Math.round(floatArrays[j][i] * 32767.f);
                            if (val > 32767)
                                val = 32767;
                            if (val < -32768)
                                val = -32768;
                            audiobuf[count++] = (short) val;
                        }
                    }

                    VORBIS.vorbis_synthesis_read(vd, i);
                    audiobuf_fill += i * vi.channels * 2;
                    if (audiobuf_fill == audiofd_fragsize)
                        audiobuf_ready = 1;

                } else
                {
                    /* no pending audio; is there a pending packet to decode? */
                    if (OGG.ogg_stream_packetout(vo, op) > 0)
                    {
                        if (VORBIS.vorbis_synthesis(vb, op) == 0) /*
                                                                   * test for
                                                                   * success!
                                                                   */
                            VORBIS.vorbis_synthesis_blockin(vd, vb);
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
                while (OGG.ogg_sync_pageout(oy, og) > 0)
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
            while (theora_p != 0 && videobuf_ready == 0)
            {
                /* theora is one in, one out... */
                if (OGG.ogg_stream_packetout(to, op) > 0)
                {
                    ++videoFrameNo;

                    final int ret = THEORA.theora_decode_packetin(td, op);
                    if (ret < 0)
                        throw new IOException("theora_decode_packetin failed: "
                                + ret);
                    videobuf_granulepos = td.granulepos;

                    videobuf_time = THEORA.theora_granule_time(td,
                            videobuf_granulepos);
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
                while (OGG.ogg_sync_pageout(oy, og) > 0)
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
            try
            {
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
                OGG.ogg_sync_init(oy);

                /* init supporting Vorbis structures needed in header parsing */
                VORBIS.vorbis_info_init(vi);
                VORBIS.vorbis_comment_init(vc);

                /* init supporting Theora structures needed in header parsing */
                THEORA.theora_comment_init(tc);
                THEORA.theora_info_init(ti);

                // System.out.println("Parsing headers...");
                /* Ogg file open; parse the headers */
                /* Only interested in Vorbis/Theora streams */
                while (stateflag == 0)
                {
                    int ret = buffer_data(oy);
                    if (ret <= 0)
                        break;
                    while (OGG.ogg_sync_pageout(oy, og) > 0)
                    {
                        ogg_stream_state test = new ogg_stream_state();

                        /*
                         * is this a mandated initial header? If not, stop
                         * parsing
                         */
                        if (OGG.ogg_page_bos(og) == 0)
                        {
                            /*
                             * don't leak the page; get it into the appropriate
                             * stream
                             */
                            queue_page(og);
                            stateflag = 1;
                            break;
                        }

                        OGG.ogg_stream_init(test, OGG.ogg_page_serialno(og));
                        OGG.ogg_stream_pagein(test, og);
                        OGG.ogg_stream_packetout(test, op);

                        /* identify the codec: try theora */
                        if (ENABLE_VIDEO && theora_p == 0
                                && THEORA.theora_decode_header(ti, tc, op) >= 0)
                        {
                            /* it is theora */
                            to = test;
                            theora_p = 1;
                        } else if (ENABLE_AUDIO
                                && vorbis_p == 0
                                && VORBIS.vorbis_synthesis_headerin(vi, vc, op) >= 0)
                        {
                            /* it is vorbis */
                            vo = test;
                            vorbis_p = 1;
                        } else
                        {
                            /* whatever it is, we don't care about it */
                            OGG.ogg_stream_clear(test);
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
                            && ((ret = OGG.ogg_stream_packetout(to, op))) != 0)
                    {
                        if (ret < 0)
                        {
                            throw new ResourceUnavailableException(
                                    "Error parsing Theora stream headers; corrupt stream?");
                        }
                        if (THEORA.theora_decode_header(ti, tc, op) != 0)
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
                            && ((ret = OGG.ogg_stream_packetout(vo, op))) != 0)
                    {
                        if (ret < 0)
                        {
                            throw new ResourceUnavailableException(
                                    "Error parsing Vorbis stream headers; corrupt stream?");
                        }

                        if (VORBIS.vorbis_synthesis_headerin(vi, vc, op) != 0)
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

                    if (OGG.ogg_sync_pageout(oy, og) > 0)
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

                /* and now we have it all. initialize decoders */
                if (theora_p != 0)
                {
                    THEORA.theora_decode_init(td, ti);
                    final double fps = (double) ti.fps_numerator
                            / (double) ti.fps_denominator;
                    logger.info("Ogg logical stream "
                            + Integer.toHexString(to.serialno.intValue())
                            + " is Theora " + ti.width + "x" + ti.height + " "
                            + fps + " fps");
                    switch (ti.pixelformat)
                    {
                    case TheoraLibrary.OC_PF_420:
                        logger.info(" 4:2:0 video");
                        break;
                    case TheoraLibrary.OC_PF_422:
                        logger.info(" 4:2:2 video");
                        break;
                    case TheoraLibrary.OC_PF_444:
                        logger.info(" 4:4:4 video");
                        break;
                    case TheoraLibrary.OC_PF_RSVD:
                    default:
                        logger.info(" video\n  (UNKNOWN Chroma sampling!)");
                        break;
                    }
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
                    THEORA.theora_info_clear(ti);
                    THEORA.theora_comment_clear(tc);
                }
                if (vorbis_p != 0)
                {
                    VORBIS.vorbis_synthesis_init(vd, vi);
                    VORBIS.vorbis_block_init(vd, vb);
                    logger.info("Ogg logical stream "
                            + Integer.toHexString(vo.serialno.intValue())
                            + " is Vorbis " + vi.channels + " channel "
                            + vi.rate.intValue() + " Hz audio.");
                } else
                {
                    /* tear down the partial vorbis setup */
                    VORBIS.vorbis_info_clear(vi);
                    VORBIS.vorbis_comment_clear(vc);
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
    int queue_page(ogg_page page)
    {
        if (theora_p != 0)
            OGG.ogg_stream_pagein(to, og);
        if (vorbis_p != 0)
            OGG.ogg_stream_pagein(vo, og);
        return 0;
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
