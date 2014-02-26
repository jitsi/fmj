package net.sf.fmj.ffmpeg_java;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.ffmpeg_java.*;
import net.sf.ffmpeg_java.AVCodecLibrary.AVCodec;
import net.sf.ffmpeg_java.AVCodecLibrary.AVCodecContext;
import net.sf.ffmpeg_java.AVCodecLibrary.AVFrame;
import net.sf.ffmpeg_java.AVFormatLibrary.AVFormatContext;
import net.sf.ffmpeg_java.AVFormatLibrary.AVInputFormat;
import net.sf.ffmpeg_java.AVFormatLibrary.AVOutputFormat;
import net.sf.ffmpeg_java.AVFormatLibrary.AVPacket;
import net.sf.ffmpeg_java.AVFormatLibrary.AVStream;
import net.sf.ffmpeg_java.FFMPEGLibrary.AVRational;
import net.sf.ffmpeg_java.custom_protocol.*;
import net.sf.ffmpeg_java.util.*;
import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;
import net.sf.fmj.utility.LoggerSingleton;

import com.lti.utils.collections.Queue;
import com.sun.jna.*;
import com.sun.jna.ptr.*;

/**
 *
 * Demultiplexer which uses ffmpeg.java native wrapper around ffmpeg
 * (libavformat, libavcodec, libavutil, libswscale).
 *
 * @author Ken Larson
 *
 */
public class FFMPEGParser extends AbstractDemultiplexer
{
    private class AudioTrack extends PullSourceStreamTrack
    {
        // TODO: track listener

        private final int audioStreamIndex;
        private AVCodecContext codecCtx;
        private final AVCodec codec;
        private Pointer buffer;
        private int bufferSize;
        private final AudioFormat format;

        public AudioTrack(int audioStreamIndex, AVStream stream,
                AVCodecContext codecCtx) throws ResourceUnavailableException
        {
            super();

            this.audioStreamIndex = audioStreamIndex;
            this.codecCtx = codecCtx;

            synchronized (AV_SYNC_OBJ)
            {
                // Find the decoder for the video stream
                this.codec = AVCODEC.avcodec_find_decoder(codecCtx.codec_id);
                if (codec == null)
                    throw new ResourceUnavailableException(
                            "Codec not found for codec_id " + codecCtx.codec_id
                                    + " (0x"
                                    + Integer.toHexString(codecCtx.codec_id)
                                    + ")"); // Codec not found - see
                                            // AVCodecLibrary.CODEC_ID constants

                // Open codec
                if (AVCODEC.avcodec_open(codecCtx, codec) < 0)
                    throw new ResourceUnavailableException(
                            "Could not open codec"); // Could not open codec

                // actually appears to be used as a short array.
                bufferSize = AVCodecLibrary.AVCODEC_MAX_AUDIO_FRAME_SIZE;
                buffer = AVUTIL.av_malloc(bufferSize);

                format = convertCodecAudioFormat(codecCtx);
            }

        }

        // @Override
        @Override
        public void deallocate()
        {
            synchronized (AV_SYNC_OBJ)
            {
                // Close the codec
                if (codecCtx != null)
                {
                    AVCODEC.avcodec_close(codecCtx);
                    codecCtx = null;
                }

                if (buffer != null)
                {
                    AVUTIL.av_free(buffer);
                    buffer = null;
                }
            }

        }

        @Override
        public Time getDuration()
        {
            if (formatCtx.duration <= 0)
                return Duration.DURATION_UNKNOWN; // not sure what
                                                  // formatCtx.duration is set
                                                  // to for unknown/unspecified
                                                  // lengths, but this seems
                                                  // like a reasonable check.
            // formatCtx.duration is in AV_TIME_BASE, which means it is in
            // milliseconds. Multiply by 1000 to get nanos.
            return new Time(formatCtx.duration * 1000L);
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
            // TODO: the reading of packets needs to be done centrally for all
            // tracks
            final AVPacket packet = nextPacket(audioStreamIndex);
            if (packet != null)
            {
                synchronized (AV_SYNC_OBJ)
                {
                    final IntByReference frameSize = new IntByReference();
                    // It is not very clear from the documentation, but it
                    // appears that we set the initial frame size to be the size
                    // of this.buffer in bytes, not in "shorts".
                    frameSize.setValue(this.bufferSize);
                    // Decode
                    AVCODEC.avcodec_decode_audio2(codecCtx, this.buffer,
                            frameSize, packet.data, packet.size);

                    // Did we get a audio data?
                    if (frameSize.getValue() < 0)
                    {
                        throw new RuntimeException("Failed to read audio frame"); // TODO:
                                                                                  // how
                                                                                  // to
                                                                                  // handle
                                                                                  // this
                                                                                  // error?
                    } else if (frameSize.getValue() > 0)
                    {
                        if (frameSize.getValue() > this.bufferSize)
                        { // realloc buffer to make room:
                          // we already allocate the maximum size, so this
                          // should never happen.
                            AVUTIL.av_free(this.buffer);
                            this.bufferSize = frameSize.getValue();
                            this.buffer = AVUTIL.av_malloc(this.bufferSize);
                        }

                        final byte[] data = this.buffer.getByteArray(0,
                                frameSize.getValue());
                        buffer.setData(data);
                        buffer.setLength(data.length);
                        buffer.setOffset(0);
                        buffer.setEOM(false);
                        buffer.setDiscard(false);
                        buffer.setTimeStamp(System.currentTimeMillis()); // TODO

                    } else
                    {
                        buffer.setLength(0);
                        buffer.setDiscard(true);
                    }

                    // Free the packet that was allocated by av_read_frame
                    // AVFORMAT.av_free_packet(packet.getPointer()) - cannot be
                    // called because it is an inlined function.
                    // so we'll just do the JNA equivalent of the inline:
                    if (packet.destruct != null)
                        packet.destruct.callback(packet);
                }

            } else
            { // TODO: error? EOM?
                buffer.setLength(0);
                buffer.setEOM(true);
                return;
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

        private final int videoStreamIndex;
        private AVStream stream;
        private AVCodecContext codecCtx;
        private AVCodec codec;
        private AVFrame srcFrame;
        private AVFrame dstFrame;
        private VideoFormat format;
        private Pointer buffer;
        private int dstPixFmt;
        /**
         * We have to keep track of frame number ourselves.
         * frame.display_picture_number seems to often always be zero. See:
         * http:
         * //lists.mplayerhq.hu/pipermail/ffmpeg-user/2005-September/001244.html
         */
        private long frameNo;

        public VideoTrack(int videoStreamIndex, AVStream stream,
                AVCodecContext codecCtx) throws ResourceUnavailableException
        {
            super();

            this.videoStreamIndex = videoStreamIndex;
            this.stream = stream;
            this.codecCtx = codecCtx;

            synchronized (AV_SYNC_OBJ)
            {
                // Find the decoder for the video stream
                this.codec = AVCODEC.avcodec_find_decoder(codecCtx.codec_id);
                if (codec == null)
                    throw new ResourceUnavailableException(
                            "Codec not found for codec_id " + codecCtx.codec_id
                                    + " (0x"
                                    + Integer.toHexString(codecCtx.codec_id)
                                    + ")"); // Codec not found - see
                                            // AVCodecLibrary.CODEC_ID constants

                // Open codec
                if (AVCODEC.avcodec_open(codecCtx, codec) < 0)
                    throw new ResourceUnavailableException(
                            "Could not open codec"); // Could not open codec

                // Allocate video frame
                srcFrame = AVCODEC.avcodec_alloc_frame();
                if (srcFrame == null)
                    throw new ResourceUnavailableException(
                            "Could not allocate frame");

                // Allocate an AVFrame structure
                dstFrame = AVCODEC.avcodec_alloc_frame();
                if (dstFrame == null)
                    throw new ResourceUnavailableException(
                            "Could not allocate frame");

                // set format
                float frameRate = (float) getFPS(stream, codecCtx);
                // we let ffmpeg convert our data to our preferred pixel format
                dstPixFmt = ListFormats.getPreferedPixelFormat();
                format = ListFormats.convertCodecPixelFormat(dstPixFmt,
                        codecCtx.width, codecCtx.height, frameRate);
                if (format == null)
                {
                    // format unsupported by fmj. we convert to RGB32
                    dstPixFmt = FFMPEGLibrary.PIX_FMT_RGB32;
                    format = ListFormats.convertCodecPixelFormat(dstPixFmt,
                            codecCtx.width, codecCtx.height, frameRate);
                }

                // Determine required buffer size and allocate buffer
                final int numBytes = AVCODEC.avpicture_get_size(dstPixFmt,
                        codecCtx.width, codecCtx.height);
                buffer = AVUTIL.av_malloc(numBytes);

                // Assign appropriate parts of buffer to image planes in
                // dstFrame
                AVCODEC.avpicture_fill(dstFrame, buffer, dstPixFmt,
                        codecCtx.width, codecCtx.height);
            }
        }

        @Override
        public void deallocate()
        {
            synchronized (AV_SYNC_OBJ)
            {
                // Close the codec
                if (codecCtx != null)
                {
                    AVCODEC.avcodec_close(codecCtx);
                    codecCtx = null;
                }

                // Free the destination image
                if (dstFrame != null)
                {
                    AVUTIL.av_free(dstFrame.getPointer());
                    dstFrame = null;
                }

                // Free the source frame
                if (srcFrame != null)
                {
                    AVUTIL.av_free(srcFrame.getPointer());
                    srcFrame = null;
                }

                if (buffer != null)
                {
                    AVUTIL.av_free(buffer);
                    buffer = null;
                }
            }

        }

        @Override
        public Time getDuration()
        {
            if (formatCtx.duration <= 0)
                return Duration.DURATION_UNKNOWN; // not sure what
                                                  // formatCtx.duration is set
                                                  // to for unknown/unspecified
                                                  // lengths, but this seems
                                                  // like a reasonable check.
            // formatCtx.duration is in AV_TIME_BASE, is in 1/1000000 sec.
            // Multiply by 1000 to get nanos.
            return new Time(formatCtx.duration * 1000L);
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
            // will be set to the minimum dts of all packets that make up a
            // frame.
            // TODO: this is not correct in all cases, see comments in
            // getTimestamp.
            long dts = -1;

            final AVPacket packet = nextPacket(videoStreamIndex);
            if (packet != null)
            {
                synchronized (AV_SYNC_OBJ)
                {
                    final IntByReference frameFinished = new IntByReference();
                    // Decode video frame

                    AVCODEC.avcodec_decode_video(codecCtx, srcFrame,
                            frameFinished, packet.data, packet.size);
                    if (dts == -1 || packet.dts < dts)
                        dts = packet.dts;

                    // Did we get a video frame?
                    if (frameFinished.getValue() != 0)
                    {
                        int res = imageConverter.img_convert(dstFrame,
                                dstPixFmt, srcFrame, codecCtx.pix_fmt,
                                codecCtx.width, codecCtx.height);
                        if (res < 0)
                            throw new RuntimeException("img_convert failed: "
                                    + res); // TODO: how to handle

                        int length = codecCtx.height * dstFrame.linesize[0];
                        Class<?> dataType = format.getDataType();
                        final Object data;
                        if (Format.intArray.equals(dataType))
                        {
                            length = length / 4;
                            data = dstFrame.data0.getIntArray(0, length);
                        } else if (Format.shortArray.equals(dataType))
                        {
                            length = length / 2;
                            data = dstFrame.data0.getShortArray(0, length);
                        } else if (Format.byteArray.equals(dataType))
                        {
                            data = dstFrame.data0.getByteArray(0, length);
                        } else
                        {
                            throw new RuntimeException(
                                    "Can't handle datatype of format:"
                                            + dataType);
                        }
                        buffer.setData(data);
                        buffer.setLength(length);
                        buffer.setOffset(0);
                        buffer.setEOM(false);
                        buffer.setDiscard(false);
                        buffer.setTimeStamp(getTimestamp(srcFrame, stream,
                                codecCtx, frameNo++, dts));
                        // System.out.println("frameNo=" + frameNo + " dts=" +
                        // dts + " timestamp=" + buffer.getTimeStamp());
                        dts = -1;
                    } else
                    {
                        buffer.setLength(0);
                        buffer.setDiscard(true);
                    }

                    // Free the packet that was allocated by av_read_frame
                    // AVFORMAT.av_free_packet(packet.getPointer()) - cannot be
                    // called because it is an inlined function.
                    // so we'll just do the JNA equivalent of the inline:
                    if (packet.destruct != null)
                        packet.destruct.callback(packet);
                }
            } else
            { // TODO: error? EOM?
                buffer.setLength(0);
                buffer.setEOM(true);
                return;
            }
        }
    }

    private static final Logger logger = LoggerSingleton.logger;
    private static final boolean PROCEED_IF_NO_AUDIO_CODEC = true; // if true,
                                                                   // we'll play
                                                                   // back video
                                                                   // only if we
                                                                   // are
                                                                   // missing an
                                                                   // audio
                                                                   // codec.
                                                                   // Typical
                                                                   // example:
                                                                   // Mpeg-4 AAC
    private final AVFormatLibrary AVFORMAT;
    private final AVCodecLibrary AVCODEC;

    private final AVUtilLibrary AVUTIL;

    private final ImageConverter imageConverter;

    private AVFormatContext formatCtx;

    private ContentDescriptor[] supportedInputContentDescriptors = null;

    // TODO: just here while waiting for JNA GlobalVariable implementation
    static final String FIRST_FFMPEG_DEMUX_NAME = "aac";

    private static final Object AV_SYNC_OBJ = new Boolean(true); // synchronize
                                                                 // on this
                                                                 // before using
                                                                 // the
                                                                 // libraries,
                                                                 // to prevent
                                                                 // threading
                                                                 // problems.

    private PullDataSource source;

    private PullSourceStreamTrack[] tracks;

    private Queue[] packetQueues; // Queue of AVPacket
    // these mimetypes are added to the types of the queried decoders
    static final String[] AAC_MIMETYPE = { "audio/X-HX-AAC-ADTS" };
    static final String[] DV_MIMETYPE = { "video/x-dv" };
    static final String[] H264_MIMETYPE = { "video/mp4" };
    static final String[] M4V_MIMETYPE = { "video/mp4v-es" };
    static final String[] ALAW_MIMETYPE = { "audio/x-alaw" };
    static final String[] MULAW_MIMETYPE = { "audio/x-mulaw" };
    static final String[] ASF_MIMETYPES = { "video/x-ms-wmv", "video/x-ms-wma" };
    static final String[] FLIC_MIMETYPES = { "video/fli", "video/flc",
            "video/x-fli", "video/x-flc" };
    static final String[] MATROSKA_MIMETYPES = { "video/x-matroska",
            "audio/x-matroska" };
    static final String[] MJPEG_MIMETYPE = { "video/x-motion-jpeg" };
    static final String[] MOV_MP4_M4A_3GP_3G2_MJ2_MIMETYPES = {
            "video/quicktime", "video/mp4", "video/3gpp", "video/mj2" };
    static final String[] MUSEPACK_MIMETYPE = { "audio/x-musepack" };
    static final String[] MPEGTSRAW_MIMETYPE = { "video/x-mpegts" };
    static final String[] MPEG1VIDEO_MIMETYPE = { "video/mpeg" };
    static final String[] MPEG2VIDEO_MIMETYPES = { "video/mpv", "video/mp2p" };
    static final String[] MTV_MIMETYPE = { "video/x-amv" };
    static final String[] MXF_MIMETYPE = { "application/mxf" };
    static final String[] NUV_MIMETYPE = { "video/x-nuv" };
    static final String[] NSV_MIMETYPE = { "application/x-nsv-vp3-mp3" };
    static final String[] OGG_MIMETYPE = { "audio/ogg" };
    static final String[] SHN_MIMETYPE = { "application/x-shorten" };
    static final String[] TTA_MIMETYPE = { "audio/x-tta" };

    static final String[] WAVPACK_MIMETYPE = { "audio/x-wavpack" };

    static final String[] NOT_SUPPORTED_FORMAT = {};
    // Mimetypes defined for fmj
    static final String[] FOURXM_MIMETYPE = { "video/x-4xm" };
    static final String[] APC_MIMETYPE = { "audio/x-apc" };
    static final String[] AVS_MIMETYPE = { "video/x-avs" };
    static final String[] BETHSOFTVID_MIMETYPE = { "video/x-bethsoft-vid" };
    static final String[] C93_MIMETYPE = { "video/x-c93" };
    static final String[] CPK_MIMETYPE = { "video/x-film-cpk" };
    static final String[] DXA_MIMETYPE = { "video/x-dxa" };
    static final String[] DSICIN_MIMETYPE = { "video/x-dsicin" };
    static final String[] DTS_MIMETYPE = { "audio/x-raw-dts" };
    static final String[] EA_MIMETYPE = { "video/x-ea" };
    static final String[] GXF_MIMETYPE = { "video/x-gxf" };
    static final String[] IDCIN_MIMETYPE = { "video/x-idcin" };
    static final String[] INGENIENT_MIMETYPE = { "video/x-ingenient" };
    static final String[] MM_MIMETYPE = { "video/x-mm" };
    static final String[] MVE_MIMETYPE = { "video/x-mve" };
    static final String[] PSXSTR_MIMETYPE = { "audio/x-psxstr" };
    static final String[] RAWVIDEO_MIMETYPE = { "video/x-raw-yuv" };
    static final String[] ROQ_MIMETYPE = { "video/x-roq" };
    static final String[] SMK_MIMETYPE = { "video/x-smk" };
    static final String[] SOL_MIMETYPE = { "audio/x-sol" };
    static final String[] THP_MIMETYPE = { "video/x-thp" };
    static final String[] SEQ_MIMETYPE = { "video/x-seq" };
    static final String[] TXD_MIMETYPE = { "video/x-txd" };
    static final String[] VC1_MIMETYPE = { "video/x-raw-vc1" };
    static final String[] VMD_MIMETYPE = { "video/x-vmd" };
    static final String[] WC3MOVIE_MIMETYPE = { "video/x-wc3-movie" };
    static final String[] WSAUD_MIMETYPE = { "video/x-wsaud" };

    static final String[] WSVQA_MIMETYPE = { "video/x-wsvqa" };

    public static AudioFormat convertCodecAudioFormat(AVCodecContext codecCtx)
    {
        AudioFormat result = null;
        switch (codecCtx.codec_id)
        {
        case AVCodecLibrary.CODEC_ID_AC3:
            result = new AudioFormat(AudioFormat.DOLBYAC3,
                    codecCtx.sample_rate, 16, codecCtx.channels,
                    ListFormats.isBigEndian() ? AudioFormat.BIG_ENDIAN
                            : AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
            break;
        /*
         * TODO: add DTS to audioformat case AVCodecLibrary.CODEC_ID_DTS: result
         * = new AudioFormat(AudioFormat.DTS, codecCtx.sample_rate, 16,
         * codecCtx.channels, ListFormats.isBigEndian() ? AudioFormat.BIG_ENDIAN
         * : AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED); break;
         */
        default:
            // ffmpeg appears to always decode audio into 16 bit samples,
            // regardless of the source.
            // system endianess and signed
            result = new AudioFormat(AudioFormat.LINEAR, codecCtx.sample_rate,
                    16, codecCtx.channels,
                    ListFormats.isBigEndian() ? AudioFormat.BIG_ENDIAN
                            : AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
            break;
        }
        return result;
    }

    static double getFPS(AVStream stream, AVCodecContext codecCtx)
    {
        final AVRational time_base = getTimeBase(stream, codecCtx);
        return (double) time_base.den / (double) time_base.num;
    }

    static AVRational getTimeBase(AVStream stream, AVCodecContext codecCtx)
    {
        // code adapted from ffmpeg utils.c: dump_format
        if (stream.r_frame_rate.num != 0 && stream.r_frame_rate.den != 0)
        {
            AVRational result = new AVRational();
            result.num = stream.r_frame_rate.den;
            result.den = stream.r_frame_rate.num;
            return result;
        } else if (stream.time_base.num != 0 && stream.time_base.den != 0)
            return stream.time_base;
        else
            return codecCtx.time_base;
    }

    static long getTimestamp(final AVFrame frame, final AVStream stream,
            final AVCodecContext codecCtx, long frameNo, long packetDts)
    {
        // from AVFrame, regarding int64_t pts:
        /**
         * presentation timestamp in time_base units (time when frame should be
         * shown to user) If AV_NOPTS_VALUE then frame_rate = 1/time_base will
         * be assumed.
         */

        // from AVCodecContext, regarding time_base:

        /**
         * This is the fundamental unit of time (in seconds) in terms of which
         * frame timestamps are represented. For fixed-fps content, timebase
         * should be 1/framerate and timestamp increments should be identically
         * 1.
         */

        // the time base here is used for calculating based on frame number.
        // TODO: if other calculations are used, using pts/dts, then this may
        // not be correct.
        /*
         * final AVRational time_base = getTimeBase(stream,
         * codecCtx);//codecCtx.time_base;
         *
         * // TODO: the frame rate is in frames, where half of an interlaced
         * frame counts as 1. so for interlaced video, // this has to be taken
         * into account. // for example safexmas.move is reported as : //
         * Duration: 00:00:16.4, start: 0.000000, bitrate: 1730 kb/s // Stream
         * #0.0(eng): Video: cinepak, yuv420p, 320x200, 30.00 fps(r) // and it
         * has 220 frames. But 220/16.4=13.4. // see
         * http://www.dranger.com/ffmpeg/tutorial05.html // for a good
         * discussion on pts. // TODO: for now, we'll just use the packetDts,
         * since pts seems to always be zero.
         *
         * if (packetDts == AVCodecLibrary.AV_NOPTS_VALUE) // TODO: with some
         * movies, pts is just always zero, so we'll handle it the same way. {
         * // If AV_NOPTS_VALUE then frame_rate = 1/time_base will be assumed.
         * // therefore we need to know the frame # return (1000000000L *
         * frameNo * (long) time_base.num) / (long) time_base.den;
         *
         * } else { // TODO: the code to do the calculation based on the dts is
         * wrong, so we'll just use the frame number based // calculation for
         * now. // not sure how to calculate the correct dts for a frame. // try
         * 4harmonic.mpg for an example of this. return (1000000000L * frameNo *
         * (long) time_base.num) / (long) time_base.den; //return ( 1000000000L
         * * packetDts * (long) time_base.num) / (long) time_base.den; // TODO:
         * is this correct? it appears to be based on the AVFrame comment, but
         * has not been tested yet.
         *
         *
         * }
         */
        double pts;
        if (packetDts != AVCodecLibrary.AV_NOPTS_VALUE)
        {
            pts = packetDts;
        } else if (frame.opaque != null
                && frame.opaque.getInt(0) != AVCodecLibrary.AV_NOPTS_VALUE)
        {
            pts = frame.opaque.getInt(0);
        } else
        {
            pts = 0;
        }
        pts *= stream.time_base.num / (double) stream.time_base.den;
        // TODO: for MPEG2 the frame can be repeated
        /*
         * double frame_delay = codecCtx.time_base.num /
         * (double)codecCtx.time_base.den; frame_delay += frame.repeat_pict *
         * (frame_delay * 0.5);
         */
        return (long) (pts * 1000000000d);
    }

    public FFMPEGParser()
    {
        try
        {
            AVFORMAT = AVFormatLibrary.INSTANCE;
            AVCODEC = AVCodecLibrary.INSTANCE;
            AVUTIL = AVUtilLibrary.INSTANCE;
            imageConverter = ImageConverterSingleton.instance();

            AVFORMAT.av_register_all();
        } catch (Throwable t)
        {
            logger.log(Level.WARNING, "Unable to initialize ffmpeg libraries: "
                    + t);
            throw new RuntimeException(t);
        }
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

    @Override
    public void close()
    {
        synchronized (AV_SYNC_OBJ)
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

            // Close the video file
            if (formatCtx != null)
            {
                AVFORMAT.av_close_input_file(formatCtx);
                formatCtx = null;
            }
        }
        super.close();
    }

    // @Override
    @Override
    public ContentDescriptor[] getSupportedInputContentDescriptors()
    {
        if (supportedInputContentDescriptors == null)
        {
            queryInputContentDescriptors();
        }
        return supportedInputContentDescriptors;
    }

    // @Override
    @Override
    public Track[] getTracks() throws IOException, BadHeaderException
    {
        return tracks;
    }

    // @Override
    @Override
    public boolean isPositionable()
    {
        return true;
    }

    // @Override
    @Override
    public boolean isRandomAccess()
    {
        return super.isRandomAccess(); // TODO: can we determine this from the
                                       // data source?
    }

    /**
     *
     * @param streamIndex
     *            the track/stream index
     * @return null on EOM
     */
    private AVPacket nextPacket(int streamIndex)
    {
        // because ffmpeg has a single function that gets the next packet,
        // without regard to track/stream, we have
        // to queue up packets that are for a different track/stream.

        synchronized (AV_SYNC_OBJ)
        {
            if (!packetQueues[streamIndex].isEmpty())
                return (AVPacket) packetQueues[streamIndex].dequeue(); // we
                                                                       // already
                                                                       // have
                                                                       // one in
                                                                       // the
                                                                       // queue
                                                                       // for
                                                                       // this
                                                                       // stream

            while (true)
            {
                final AVPacket packet = new AVPacket();
                if (AVFORMAT.av_read_frame(formatCtx, packet) < 0)
                {
                    break; // TODO: distinguish between EOM and error?
                }

                // Is this a packet from the desired stream?
                if (packet.stream_index == streamIndex)
                {
                    return packet;
                } else
                {
                    // TODO: This has been observed in other code that uses
                    // ffmpeg, not sure if it is needed.
                    // if (AVFORMAT.av_dup_packet(packet) < 0)
                    // throw new RuntimeException("av_dup_packet failed");

                    packetQueues[packet.stream_index].enqueue(packet);
                }
            }

            return null;
        }
    }

    // @Override
    @Override
    public void open() throws ResourceUnavailableException
    {
        synchronized (AV_SYNC_OBJ)
        {
            try
            {
                AVCODEC.avcodec_init(); // TODO: everything seems to be fine if
                                        // we don't call this...
            } catch (Throwable t)
            {
                logger.log(Level.WARNING, "" + t, t);
                throw new ResourceUnavailableException(
                        "avcodec_init or av_register_all failed");
            }

            // not sure what the consequences of such a mismatch are, but it is
            // worth logging a warning:
            if (AVCODEC.avcodec_version() != AVCodecLibrary.LIBAVCODEC_VERSION_INT)
                logger.warning("ffmpeg-java and ffmpeg versions do not match: avcodec_version="
                        + AVCODEC.avcodec_version()
                        + " LIBAVCODEC_VERSION_INT="
                        + AVCodecLibrary.LIBAVCODEC_VERSION_INT);

            String urlStr;

            final String protocol = source.getLocator().getProtocol();
            // TODO: ffmpeg appears to support multiple file protocols, for
            // example: file: pipe: udp: rtp: tcp: http:
            // we should also allow those.
            // TODO: would be best to query this dynamically from ffmpeg

            // we don't really use the DataSource, we just grab its URL. So
            // arbitrary data sources won't work.
            // otherwise, we register a custom URLHandler with ffmpeg, which
            // calls us back to get the data.

            if (protocol.equals("file") || protocol.equals("http"))
            {
                // just use the URL from the datasource

                // FMJ supports relative file URLs, but FFMPEG does not. So
                // we'll rewrite the URL here:
                // TODO: perhaps we should only do this if FFMPEG has a problem
                // (av_open_input_file returns nonzero).
                if (protocol.equals("file"))
                    // ffmpeg has problems with windows file-URLs like
                    // file:///c:/
                    urlStr = URLUtils.extractValidPathFromFileUrl(source
                            .getLocator().toExternalForm());
                else
                    urlStr = source.getLocator().toExternalForm();
            } else
            { // use the real java datasource, via callbacks.
                CallbackURLProtocolMgr.register(AVFORMAT);

                // TODO: do this in start?
                final String callbackURL = CallbackURLProtocolMgr
                        .addCallbackURLProtocolHandler(new PullDataSourceCallbackURLProtocolHandler(
                                source));

                // TODO: we need to remove the handler when we are done.

                urlStr = callbackURL;
            }

            final PointerByReference ppFormatCtx = new PointerByReference();

            // Open video file
            final int ret = AVFORMAT.av_open_input_file(ppFormatCtx, urlStr,
                    null, 0, null);
            if (ret != 0)
            {
                throw new ResourceUnavailableException(
                        "av_open_input_file failed: " + ret); // Couldn't open
                                                              // file
            }

            formatCtx = new AVFormatContext(ppFormatCtx.getValue());
            // System.out.println(new String(formatCtx.filename));

            // Retrieve stream information
            if (AVFORMAT.av_find_stream_info(formatCtx) < 0)
                throw new ResourceUnavailableException(
                        "Couldn't find stream information"); // Couldn't find
                                                             // stream
                                                             // information

            AVFORMAT.dump_format(formatCtx, 0, urlStr, 0);

            VideoTrack videoTrack = null;
            AudioTrack audioTrack = null;
            for (int i = 0; i < formatCtx.nb_streams; i++)
            {
                final AVStream stream = new AVStream(formatCtx.getStreams()[i]);
                final AVCodecContext codecCtx = new AVCodecContext(stream.codec);
                if (codecCtx.codec_id == 0)
                {
                    logger.info("Codec id is zero (no codec) - skipping stream "
                            + i);
                    continue;
                }
                if (codecCtx.codec_type == AVCodecLibrary.CODEC_TYPE_VIDEO
                        && videoTrack == null)
                {
                    videoTrack = new VideoTrack(i, stream, codecCtx);
                } else if (codecCtx.codec_type == AVCodecLibrary.CODEC_TYPE_AUDIO
                        && audioTrack == null)
                {
                    try
                    {
                        audioTrack = new AudioTrack(i, stream, codecCtx);
                    } catch (ResourceUnavailableException e)
                    {
                        if (!PROCEED_IF_NO_AUDIO_CODEC)
                            throw e;
                        logger.log(Level.WARNING, "Skipping audio track: " + e,
                                e);
                    }
                } else
                { // throw new
                  // ResourceUnavailableException("Unknown track codec type " +
                  // codecCtx.codec_type + " for track " + i);
                }

            }

            if (audioTrack == null && videoTrack == null)
                throw new ResourceUnavailableException(
                        "No audio or video track found");
            else if (audioTrack != null && videoTrack != null)
                tracks = new PullSourceStreamTrack[] { videoTrack, audioTrack };
            else if (audioTrack != null)
                tracks = new PullSourceStreamTrack[] { audioTrack };
            else
                tracks = new PullSourceStreamTrack[] { videoTrack };

            packetQueues = new Queue[formatCtx.nb_streams];
            for (int i = 0; i < packetQueues.length; ++i)
                packetQueues[i] = new Queue();

        }

        super.open();

    }

    protected void queryInputContentDescriptors()
    {
        Map<String,String[]> additionalFormatMimeTypes
            = new HashMap<String,String[]>();

        additionalFormatMimeTypes.put("4xm", FOURXM_MIMETYPE); // Format of some
                                                               // games by 4X
                                                               // Technologies
        additionalFormatMimeTypes.put("aac", AAC_MIMETYPE); // ADTS AAC
        additionalFormatMimeTypes.put("alaw", ALAW_MIMETYPE); // pcm A law
                                                              // format
        additionalFormatMimeTypes.put("apc", APC_MIMETYPE); // Audio format of
                                                            // some games by
                                                            // CRYO Interactive
                                                            // Entertainment
        additionalFormatMimeTypes.put("asf", ASF_MIMETYPES); // asf format
        additionalFormatMimeTypes.put("avs", AVS_MIMETYPE); // Format of the
                                                            // Creature Shock
                                                            // game.
        additionalFormatMimeTypes.put("bethsoftvid", BETHSOFTVID_MIMETYPE); // Game
                                                                            // file
                                                                            // format
                                                                            // of
                                                                            // Bethesda
                                                                            // Softworks.
        additionalFormatMimeTypes.put("c93", C93_MIMETYPE); // Format of the
                                                            // game Cyberia from
                                                            // Interplay.
        additionalFormatMimeTypes.put("daud", NOT_SUPPORTED_FORMAT); // TODO:
                                                                     // D-Cinema
                                                                     // audio
                                                                     // format
        additionalFormatMimeTypes.put("dsicin", DSICIN_MIMETYPE); // Delphine
                                                                  // Software
                                                                  // International
                                                                  // CIN format
        additionalFormatMimeTypes.put("dts", DTS_MIMETYPE); // raw dts
        additionalFormatMimeTypes.put("dv", DV_MIMETYPE); // DV video format
        additionalFormatMimeTypes.put("dxa", DXA_MIMETYPE); // Feeble Files /
                                                            // SCummVM game file
                                                            // format
        additionalFormatMimeTypes.put("ea", EA_MIMETYPE); // Used in various EA
                                                          // games
        additionalFormatMimeTypes.put("ffm", NOT_SUPPORTED_FORMAT); // FFServer
                                                                    // live feed
        additionalFormatMimeTypes.put("film_cpk", CPK_MIMETYPE); // Sega
                                                                 // FILM/CPK
        additionalFormatMimeTypes.put("flic", FLIC_MIMETYPES); // FLI/FLC/FLX
                                                               // animation
                                                               // format
        additionalFormatMimeTypes.put("gxf", GXF_MIMETYPE); // GXF format
        additionalFormatMimeTypes.put("h264", H264_MIMETYPE);
        additionalFormatMimeTypes.put("idcin", IDCIN_MIMETYPE); // CIN format of
                                                                // Id software
                                                                // in quake II
        additionalFormatMimeTypes.put("image2", NOT_SUPPORTED_FORMAT); // image2
                                                                       // sequence
        additionalFormatMimeTypes.put("image2pipe", NOT_SUPPORTED_FORMAT); // piped
                                                                           // image2
                                                                           // sequence
        additionalFormatMimeTypes.put("ingenient", INGENIENT_MIMETYPE); // Ingenient
                                                                        // MJPEG
        additionalFormatMimeTypes.put("ipmovie", MVE_MIMETYPE); // Interplay MVE
                                                                // format
        additionalFormatMimeTypes.put("m4v", M4V_MIMETYPE); // raw MPEG4 video
                                                            // format
        additionalFormatMimeTypes.put("matroska", MATROSKA_MIMETYPES); // Matroska
                                                                       // file
                                                                       // format
        additionalFormatMimeTypes.put("mjpeg", MJPEG_MIMETYPE); // mjpeg
        additionalFormatMimeTypes.put("mm", MM_MIMETYPE); // Format of american
                                                          // laser games
        additionalFormatMimeTypes.put("mov,mp4,m4a,3gp,3g2,mj2",
                MOV_MP4_M4A_3GP_3G2_MJ2_MIMETYPES); // QuickTime/MPEG4/Motion
                                                    // JPEG 2000 format
        additionalFormatMimeTypes.put("mpc", MUSEPACK_MIMETYPE); // musepack
        additionalFormatMimeTypes.put("mpegvideo", MPEG1VIDEO_MIMETYPE); // MPEG
                                                                         // video
        additionalFormatMimeTypes.put("mpeg", MPEG2VIDEO_MIMETYPES); // MPEG1
                                                                     // System
                                                                     // format
        additionalFormatMimeTypes.put("mpegtsraw", MPEGTSRAW_MIMETYPE); // MPEG2
                                                                        // raw
                                                                        // transport
                                                                        // stream
                                                                        // format
        additionalFormatMimeTypes.put("MTV", MTV_MIMETYPE); // Video format for
                                                            // chinese
                                                            // mp3/mp4/mtv
                                                            // players
        additionalFormatMimeTypes.put("mulaw", MULAW_MIMETYPE); // pcm mu law
                                                                // format
        additionalFormatMimeTypes.put("mxf", MXF_MIMETYPE); // Material Exchange
                                                            // Format
        additionalFormatMimeTypes.put("nsv", NSV_MIMETYPE); // NullSoft Video
                                                            // format
        additionalFormatMimeTypes.put("nuv", NUV_MIMETYPE); // Format of the
                                                            // NuppelVideo tv
                                                            // app
        additionalFormatMimeTypes.put("ogg", OGG_MIMETYPE); // Ogg
        additionalFormatMimeTypes.put("psxstr", PSXSTR_MIMETYPE); // Sony
                                                                  // Playstation
                                                                  // STR format
        additionalFormatMimeTypes.put("rawvideo", RAWVIDEO_MIMETYPE); // raw
                                                                      // video
                                                                      // format
        additionalFormatMimeTypes.put("RoQ", ROQ_MIMETYPE); // Id RoQ format
        additionalFormatMimeTypes.put("redir", NOT_SUPPORTED_FORMAT); // Dummy
                                                                      // redirector
                                                                      // format
        additionalFormatMimeTypes.put("rtsp", NOT_SUPPORTED_FORMAT); // TODO:
        additionalFormatMimeTypes.put("s16be", NOT_SUPPORTED_FORMAT); // pcm
                                                                      // signed
                                                                      // 16 bit
                                                                      // big
                                                                      // endian
                                                                      // format
        additionalFormatMimeTypes.put("s16le", NOT_SUPPORTED_FORMAT); // pcm
                                                                      // signed
                                                                      // 16 bit
                                                                      // little
                                                                      // endian
                                                                      // format
        additionalFormatMimeTypes.put("s8", NOT_SUPPORTED_FORMAT); // pcm signed
                                                                   // 8 bit
                                                                   // format
        additionalFormatMimeTypes.put("sdp", NOT_SUPPORTED_FORMAT); // TODO:
        additionalFormatMimeTypes.put("shn", SHN_MIMETYPE); // Shorten Audio
                                                            // Compression File
        additionalFormatMimeTypes.put("smk", SMK_MIMETYPE); // Smacker video
                                                            // format used by
                                                            // many games
        additionalFormatMimeTypes.put("sol", SOL_MIMETYPE); // Sierra SOL Format
        additionalFormatMimeTypes.put("thp", THP_MIMETYPE); // Video format of
                                                            // the gamecube
        additionalFormatMimeTypes.put("tiertexseq", SEQ_MIMETYPE); // Tiertex
                                                                   // Limited
                                                                   // SEQ format
        additionalFormatMimeTypes.put("tta", TTA_MIMETYPE); // True Audio
        additionalFormatMimeTypes.put("txd", TXD_MIMETYPE); // Renderware
                                                            // TeXture
                                                            // Dictionary
        additionalFormatMimeTypes.put("u16be", NOT_SUPPORTED_FORMAT); // pcm
                                                                      // unsigned
                                                                      // 16 bit
                                                                      // big
                                                                      // endian
                                                                      // format
        additionalFormatMimeTypes.put("u16le", NOT_SUPPORTED_FORMAT); // pcm
                                                                      // unsigned
                                                                      // 16 bit
                                                                      // little
                                                                      // endian
                                                                      // format
        additionalFormatMimeTypes.put("u8", NOT_SUPPORTED_FORMAT); // pcm
                                                                   // unsigned 8
                                                                   // bit format
        additionalFormatMimeTypes.put("vc1", VC1_MIMETYPE); // Raw VC1
        additionalFormatMimeTypes.put("vmd", VMD_MIMETYPE); //
        additionalFormatMimeTypes.put("wc3movie", WC3MOVIE_MIMETYPE); // Wing
                                                                      // Commander
                                                                      // III
                                                                      // movie
                                                                      // format
        additionalFormatMimeTypes.put("wsaud", WSAUD_MIMETYPE); // Westwood
                                                                // Studios audio
                                                                // format
        additionalFormatMimeTypes.put("wsvqa", WSVQA_MIMETYPE); // Westwood
                                                                // Studios VQA
                                                                // format
        additionalFormatMimeTypes.put("wv", WAVPACK_MIMETYPE); // WavPack
        additionalFormatMimeTypes.put("yuv4mpegpipe", NOT_SUPPORTED_FORMAT); // YUV4MPEG
                                                                             // pipe
                                                                             // format

        // map with mimetype as key and content descriptor as value to avoid
        // multiple descriptors
        Map<String,ContentDescriptor> mimeTypes
            = new HashMap<String,ContentDescriptor>();
        int i = 1;

        AVInputFormat avInputFormat = AVFORMAT
                .av_find_input_format(FIRST_FFMPEG_DEMUX_NAME);
        // AVInputFormat avInputFormat = new
        // AVInputFormat((Pointer)AVFormatLibrary.first_iformat.getValue());
        while (avInputFormat != null)
        {
            String mimeType = null;
            // we look for an encoder with same name and add mime_type of this
            AVOutputFormat avOutputFormat = AVFORMAT.guess_format(
                    avInputFormat.name, null, null);
            if (avOutputFormat != null && avOutputFormat.mime_type != null
                    && avOutputFormat.mime_type.length() > 0)
            {
                mimeType = avOutputFormat.mime_type;
                mimeTypes.put(
                        mimeType,
                        new ContentDescriptor(ContentDescriptor
                                .mimeTypeToPackageName(mimeType)));
                logger.log(Level.FINEST, i + ". " + avInputFormat.long_name
                        + " : " + mimeType);
            }
            String[] additionalMimeTypes = (String[]) additionalFormatMimeTypes
                    .get(avInputFormat.name);
            if (additionalMimeTypes != null)
            {
                if (additionalMimeTypes == NOT_SUPPORTED_FORMAT)
                {
                    logger.log(Level.FINE, "Ignoring input format: "
                            + avInputFormat.name + " ("
                            + avInputFormat.long_name + ")");
                } else
                {
                    for (int j = 0; j < additionalMimeTypes.length; j++)
                    {
                        mimeType = additionalMimeTypes[j];
                        mimeTypes.put(
                                mimeType,
                                new ContentDescriptor(ContentDescriptor
                                        .mimeTypeToPackageName(mimeType)));
                        logger.log(Level.FINEST, i + ". "
                                + avInputFormat.long_name + " : "
                                + additionalMimeTypes[j]);
                    }
                }
            }
            if (mimeType == null && additionalMimeTypes == null)
            {
                mimeType = "ffmpeg/" + avInputFormat.name;
                mimeTypes.put(
                        mimeType,
                        new ContentDescriptor(ContentDescriptor
                                .mimeTypeToPackageName(mimeType)));
            }
            i++;
            if (avInputFormat.next != null /* && avInputFormat.next.isValid() */)
            {
                avInputFormat = new AVInputFormat(avInputFormat.next);
            } else
            {
                avInputFormat = null;
            }
        }

        supportedInputContentDescriptors = (ContentDescriptor[]) mimeTypes
                .values().toArray(new ContentDescriptor[0]);
    }

    // @Override
    @Override
    public Time setPosition(Time where, int rounding)
    {
        // TODO: how to use rounding?
        synchronized (AV_SYNC_OBJ)
        {
            // when stream is -1, units are AV_TIME_BASE.
            // TODO: tutorial 7 on www.dranger.com suggests that the -1 can
            // sometimes cause problems...
            final int result = AVFORMAT.av_seek_frame(formatCtx, -1,
                    where.getNanoseconds() / 1000L, 0);
            if (result < 0)
            {
                logger.severe("av_seek_frame failed with code " + result);
                // TODO: what to return if error?
            }
            return where; // TODO: what to return

            // TODO: we have to reset the frame counters on the tracks....
        }
    }

    // @Override
    @Override
    public void setSource(DataSource source) throws IOException,
            IncompatibleSourceException
    {
        if (!(source instanceof PullDataSource))
            throw new IncompatibleSourceException();
        this.source = (PullDataSource) source;
    }

    // @Override
    @Override
    public void start() throws IOException
    {
    }

}
