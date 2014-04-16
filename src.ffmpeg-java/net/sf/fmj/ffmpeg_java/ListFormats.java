package net.sf.fmj.ffmpeg_java;

import java.awt.*;
import java.awt.image.*;
import java.nio.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.ffmpeg_java.*;
import net.sf.ffmpeg_java.AVCodecLibrary.AVCodec;
import net.sf.ffmpeg_java.AVCodecLibrary.AVCodecContext;
import net.sf.ffmpeg_java.AVFormatLibrary.AVOutputFormat;
import net.sf.fmj.utility.*;

/**
 *
 * @author Stephan Goetter
 *
 */
public class ListFormats
{
    private static final Logger logger = LoggerSingleton.logger;

    static final int CODEC_TYPE_UNKNOWN = -1;
    static final int CODEC_TYPE_VIDEO = 0;
    static final int CODEC_TYPE_AUDIO = 1;
    static final int CODEC_TYPE_DATA = 2;
    static final int CODEC_TYPE_SUBTITLE = 3;
    static final int CODEC_TYPE_NB = 4;

    static final AVFormatLibrary AVFORMAT = AVFormatLibrary.INSTANCE;
    static final AVCodecLibrary AVCODEC = AVCodecLibrary.INSTANCE;
    static final AVUtilLibrary AVUTIL = AVUtilLibrary.INSTANCE;

    // TODO: these are only used while global pointer implementation is in
    // progress.
    static final String FIRST_FFMPEG_MUX_NAME = "ac3";

    static final String FIRST_FFMPEG_DECODER_NAME = "aasc";

    static final String FIRST_FFMPEG_ENCODER_NAME = "asv1";
    final static int JPEG_QUALITY = 100;

    public static AudioFormat convertCodecAudioFormat(AVCodecContext codecCtx)
    {
        // ffmpeg appears to always decode audio into 16 bit samples, regardless
        // of the source.
        return new AudioFormat(AudioFormat.LINEAR, codecCtx.sample_rate, 16,
                codecCtx.channels); // / TODO: endian, signed?

    }

    public static VideoFormat convertCodecPixelFormat(int pixFmt, int width,
            int height, float frameRate)
    {
        VideoFormat result = null;
        final int red, green, blue, bitsPerPixel, pixelStride, lineStride;
        final int endianess = getCpuEndianess();

        switch (pixFmt)
        {
        case FFMPEGLibrary.PIX_FMT_RGB24: // /< Packed RGB 8:8:8, 24bpp,
                                          // RGBRGB...
            // TODO: test on bigendian box
            red = 1;
            green = 2;
            blue = 3;
            bitsPerPixel = 24;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.byteArray, frameRate, bitsPerPixel, red, green, blue);
            break;
        case FFMPEGLibrary.PIX_FMT_BGR24: // /< Packed RGB 8:8:8, 24bpp,
                                          // BGRBGR...
            // TODO: test on bigendian box
            red = 3;
            green = 2;
            blue = 1;
            bitsPerPixel = 24;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.byteArray, frameRate, bitsPerPixel, red, green, blue);
            break;
        case FFMPEGLibrary.PIX_FMT_RGB32: // /< Packed RGB 8:8:8, 32bpp, (msb)8A
                                          // 8R 8G 8B(lsb), in cpu endianness
            // TODO: test on bigendian box
            red = 0xFF0000;
            green = 0xFF00;
            blue = 0xFF;
            bitsPerPixel = 32;
            pixelStride = 1;
            lineStride = width * pixelStride;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.intArray, frameRate, bitsPerPixel, red, green, blue,
                    pixelStride, lineStride, Format.FALSE, endianess);
            break;
        case FFMPEGLibrary.PIX_FMT_RGB565: // /< Packed RGB 5:6:5, 16bpp, (msb)
                                           // 5R 6G 5B(lsb), in cpu endianness
            // TODO: test on bigendian box
            red = 0x1F;
            green = 0x7E0;
            blue = 0xF800;
            bitsPerPixel = 16;
            pixelStride = 1;
            lineStride = width * pixelStride;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.shortArray, frameRate, bitsPerPixel, red, green,
                    blue, pixelStride, lineStride, Format.FALSE, endianess);
            break;
        case FFMPEGLibrary.PIX_FMT_RGB555: // /< Packed RGB 5:5:5, 16bpp,
                                           // (msb)1A 5R 5G 5B(lsb), in cpu
                                           // endianness most significant bit to
                                           // 0
            // TODO: test on bigendian box
            red = 0x1F;
            green = 0x3E0;
            blue = 0x7C00;
            bitsPerPixel = 16;
            pixelStride = 1;
            lineStride = width * pixelStride;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.shortArray, frameRate, bitsPerPixel, red, green,
                    blue, pixelStride, lineStride, Format.FALSE, endianess);
            break;
        case FFMPEGLibrary.PIX_FMT_GRAY8: // /< Y , 8bpp
            // TODO: GrayFormat
            break;
        case FFMPEGLibrary.PIX_FMT_MONOWHITE: // /< Y , 1bpp, 0 is white, 1 is
                                              // black
            // TODO: BinaryFormat
            break;
        case FFMPEGLibrary.PIX_FMT_MONOBLACK: // /< Y , 1bpp, 0 is black, 1 is
                                              // white
            // TODO: BinaryFormat
            break;
        case FFMPEGLibrary.PIX_FMT_BGR32: // /< Packed RGB 8:8:8, 32bpp, (msb)8A
                                          // 8B 8G 8R(lsb), in cpu endianness
            // TODO: test on bigendian box
            red = 0xFF;
            green = 0xFF00;
            blue = 0xFF0000;
            bitsPerPixel = 32;
            pixelStride = 1;
            lineStride = width * pixelStride;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.intArray, frameRate, bitsPerPixel, red, green, blue,
                    pixelStride, lineStride, Format.FALSE, endianess);
            break;
        case FFMPEGLibrary.PIX_FMT_BGR565: // /< Packed RGB 5:6:5, 16bpp, (msb)
                                           // 5B 6G 5R(lsb), in cpu endianness
            // TODO: test on bigendian box
            red = 0xF800;
            green = 0x7E0;
            blue = 0x1F;
            bitsPerPixel = 16;
            pixelStride = 1;
            lineStride = width * pixelStride;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.shortArray, frameRate, bitsPerPixel, red, green,
                    blue, pixelStride, lineStride, Format.FALSE, endianess);
            break;
        case FFMPEGLibrary.PIX_FMT_BGR555: // /< Packed RGB 5:5:5, 16bpp,
                                           // (msb)1A 5B 5G 5R(lsb), in cpu
                                           // endianness most significant bit to
                                           // 1
            // TODO: test on bigendian box
            red = 0x7C00;
            green = 0x3E0;
            blue = 0x1F;
            bitsPerPixel = 16;
            pixelStride = 1;
            lineStride = width * pixelStride;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.shortArray, frameRate, bitsPerPixel, red, green,
                    blue, pixelStride, lineStride, Format.FALSE, endianess);
            break;
        case FFMPEGLibrary.PIX_FMT_BGR8: // /< Packed RGB 3:3:2, 8bpp, (msb)2B
                                         // 3G 3R(lsb)
            // TODO: test on bigendian box
            red = 0xE0;
            green = 0x1C;
            blue = 0x3;
            bitsPerPixel = 8;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.byteArray, frameRate, bitsPerPixel, red, green, blue);
            break;
        case FFMPEGLibrary.PIX_FMT_BGR4: // /< Packed RGB 1:2:1, 4bpp, (msb)1B
                                         // 2G 1R(lsb)
            // TODO: BinaryFormat
            break;
        case FFMPEGLibrary.PIX_FMT_BGR4_BYTE: // /< Packed RGB 1:2:1, 8bpp,
                                              // (msb)1B 2G 1R(lsb)
            // TODO: test on bigendian box
            red = 0x8;
            green = 0x6;
            blue = 0x1;
            bitsPerPixel = 8;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.byteArray, frameRate, bitsPerPixel, red, green, blue);
            break;
        case FFMPEGLibrary.PIX_FMT_RGB8: // /< Packed RGB 3:3:2, 8bpp, (msb)2R
                                         // 3G 3B(lsb)
            // TODO: test on bigendian box
            red = 0x7;
            green = 0x38;
            blue = 0xC0;
            bitsPerPixel = 8;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.byteArray, frameRate, bitsPerPixel, red, green, blue);
            break;
        case FFMPEGLibrary.PIX_FMT_RGB4: // /< Packed RGB 1:2:1, 4bpp, (msb)2R
                                         // 3G 3B(lsb)
            // TODO: BinaryFormat
            break;
        case FFMPEGLibrary.PIX_FMT_RGB4_BYTE: // /< Packed RGB 1:2:1, 8bpp,
                                              // (msb)2R 3G 3B(lsb)
            // TODO: test on bigendian box
            red = 0x1;
            green = 0x6;
            blue = 0x8;
            bitsPerPixel = 8;
            result = new RGBFormat(new Dimension(width, height), -1,
                    Format.byteArray, frameRate, bitsPerPixel, red, green, blue);
            break;
        case FFMPEGLibrary.PIX_FMT_RGB32_1: // /< Packed RGB 8:8:8, 32bpp,
                                            // (msb)8R 8G 8B 8A(lsb), in cpu
                                            // endianness
            // not supported as outputformat by ffmpeg
            break;
        case FFMPEGLibrary.PIX_FMT_BGR32_1: // /< Packed RGB 8:8:8, 32bpp,
                                            // (msb)8B 8G 8R 8A(lsb), in cpu
                                            // endianness
            // not supported as outputformat by ffmpeg
            break;
        case FFMPEGLibrary.PIX_FMT_GRAY16BE: // /< Y , 16bpp, big-endian
            // TODO: GrayFormat
            break;
        case FFMPEGLibrary.PIX_FMT_GRAY16LE: // /< Y , 16bpp, little-endian
            // TODO: GrayFormat
            break;
        // unsupported formats. they are converted to RGB32
        /*
         * case AVCodecLibrary.PIX_FMT_PAL8: ///< 8 bit with PIX_FMT_RGB32
         * palette // TODO: mask arrays result = new IndexedColorFormat(new
         * Dimension(width, height), -1, Format.byteArray, frameRate, width,
         * 256, new byte[256], new byte[256], new byte[256]); break;
         */
        /*
         * case AVCodecLibrary.PIX_FMT_YUV420P: ///< Planar YUV 4:2:0, 12bpp, (1
         * Cr & Cb sample per 2x2 Y samples) // TODO: test it result = new
         * YUVFormat(new Dimension(width*2, height*2), -1, Format.byteArray,
         * frameRate, YUVFormat.YUV_420, -1, -1, -1, -1, -1); break;
         */
        /*
         * case AVCodecLibrary.PIX_FMT_YUV422P: ///< Planar YUV 4:2:2, 16bpp, (1
         * Cr & Cb sample per 2x1 Y samples) // TODO: test it result = new
         * YUVFormat(new Dimension(width, height), -1, Format.byteArray,
         * frameRate, YUVFormat.YUV_422, -1, -1, -1, -1, -1); break; case
         * AVCodecLibrary.PIX_FMT_YUV411P: ///< Planar YUV 4:1:1, 12bpp, (1 Cr &
         * Cb sample per 4x1 Y samples) // TODO: test it result = new
         * YUVFormat(YUVFormat.YUV_411); break; case
         * AVCodecLibrary.PIX_FMT_YUVJ420P: ///< Planar YUV 4:2:0, 12bpp, full
         * scale (jpeg) // TODO: test it result = new JPEGFormat(new
         * Dimension(width, height), -1, Format.byteArray, frameRate,
         * JPEG_QUALITY, JPEGFormat.DEC_420); break; case
         * AVCodecLibrary.PIX_FMT_YUVJ422P: ///< Planar YUV 4:2:2, 16bpp, full
         * scale (jpeg) // TODO: test it result = new JPEGFormat(new
         * Dimension(width, height), -1, Format.byteArray, frameRate,
         * JPEG_QUALITY, JPEGFormat.DEC_422); break; case
         * AVCodecLibrary.PIX_FMT_YUVJ444P: ///< Planar YUV 4:4:4, 24bpp, full
         * scale (jpeg) // TODO: test it result = new JPEGFormat(new
         * Dimension(width, height), -1, Format.byteArray, frameRate,
         * JPEG_QUALITY, JPEGFormat.DEC_444); break;
         */
        case FFMPEGLibrary.PIX_FMT_NONE:
            break;
        case FFMPEGLibrary.PIX_FMT_XVMC_MPEG2_MC: // /< XVideo Motion
                                                  // Acceleration via common
                                                  // packet
                                                  // passing(xvmc_render.h)
            break;
        case FFMPEGLibrary.PIX_FMT_XVMC_MPEG2_IDCT:
            break;
        case FFMPEGLibrary.PIX_FMT_UYVY422: // /< Packed YUV 4:2:2, 16bpp, Cb Y0
                                            // Cr Y1
            break;
        case FFMPEGLibrary.PIX_FMT_UYYVYY411: // /< Packed YUV 4:1:1, 12bpp, Cb
                                              // Y0 Y1 Cr Y2 Y3
            break;
        case FFMPEGLibrary.PIX_FMT_YUV410P: // /< Planar YUV 4:1:0, 9bpp, (1 Cr
                                            // & Cb sample per 4x4 Y samples)
            break;
        case FFMPEGLibrary.PIX_FMT_YUV444P: // /< Planar YUV 4:4:4, 24bpp, (1 Cr
                                            // & Cb sample per 1x1 Y samples)
            break;
        case FFMPEGLibrary.PIX_FMT_YUYV422: // /< Packed YUV 4:2:2, 16bpp, Y0 Cb
                                            // Y1 Cr
            break;
        case FFMPEGLibrary.PIX_FMT_YUV440P: // /< Planar YUV 4:4:0 (1 Cr & Cb
                                            // sample per 1x2 Y samples)
            break;
        case FFMPEGLibrary.PIX_FMT_YUVJ440P: // /< Planar YUV 4:4:0 full scale
                                             // (jpeg)
            break;
        case FFMPEGLibrary.PIX_FMT_NB: // /< number of pixel formats, DO NOT USE
                                       // THIS if you want to link with shared
                                       // libav* because the number of formats
                                       // might differ between versions
            break;
        case FFMPEGLibrary.PIX_FMT_NV12: // /< Planar YUV 4:2:0, 12bpp, 1 plane
                                         // for Y and 1 for UV
            break;
        case FFMPEGLibrary.PIX_FMT_NV21: // /< as above, but U and V bytes are
                                         // swapped
            break;
        default:
            break;
        }

        return result;
    }

    static protected String getCodecType(int codecType)
    {
        String result = null;
        switch (codecType)
        {
        case CODEC_TYPE_UNKNOWN:
            result = "unknown";
            break;
        case CODEC_TYPE_VIDEO:
            result = "video";
            break;
        case CODEC_TYPE_AUDIO:
            result = "audio";
            break;
        case CODEC_TYPE_DATA:
            result = "data";
            break;
        case CODEC_TYPE_SUBTITLE:
            result = "subtitle";
            break;
        case CODEC_TYPE_NB:
            result = "nb";
            break;
        }
        return result;
    }

    public static final int getCpuEndianess()
    {
        return isBigEndian() ? RGBFormat.BIG_ENDIAN : RGBFormat.LITTLE_ENDIAN;
    }

    public static int getPixelFormatFromBufferedImageType(int bufferedImageType)
    {
        final int pixelFormat;
        switch (bufferedImageType)
        {
        case BufferedImage.TYPE_INT_ARGB:
            pixelFormat = FFMPEGLibrary.PIX_FMT_RGB32;
            break;
        case BufferedImage.TYPE_INT_ARGB_PRE:
            pixelFormat = FFMPEGLibrary.PIX_FMT_RGB32;
            break;
        case BufferedImage.TYPE_INT_RGB:
            pixelFormat = FFMPEGLibrary.PIX_FMT_RGB32;
            break;
        case BufferedImage.TYPE_INT_BGR:
            pixelFormat = FFMPEGLibrary.PIX_FMT_BGR32;
            break;
        case BufferedImage.TYPE_3BYTE_BGR:
            pixelFormat = FFMPEGLibrary.PIX_FMT_BGR24;
            break;
        case BufferedImage.TYPE_4BYTE_ABGR:
            pixelFormat = FFMPEGLibrary.PIX_FMT_BGR32;
            break;
        case BufferedImage.TYPE_4BYTE_ABGR_PRE:
            pixelFormat = FFMPEGLibrary.PIX_FMT_BGR32;
            break;
        case BufferedImage.TYPE_USHORT_555_RGB:
            pixelFormat = FFMPEGLibrary.PIX_FMT_RGB555;
            break;
        case BufferedImage.TYPE_USHORT_565_RGB:
            pixelFormat = FFMPEGLibrary.PIX_FMT_RGB565;
            break;
        case BufferedImage.TYPE_USHORT_GRAY:
            if (isBigEndian())
            {
                pixelFormat = FFMPEGLibrary.PIX_FMT_GRAY16BE;
            } else
            {
                pixelFormat = FFMPEGLibrary.PIX_FMT_GRAY16LE;
            }
            break;
        case BufferedImage.TYPE_BYTE_BINARY:
            // TODO:
            /*
             * pixelFormat = AVCodecLibrary.PIX_FMT_MONOBLACK; pixelFormat =
             * AVCodecLibrary.PIX_FMT_MONOWHITE; pixelFormat =
             * AVCodecLibrary.PIX_FMT_BGR4; pixelFormat =
             * AVCodecLibrary.PIX_FMT_RGB4;
             */
            pixelFormat = FFMPEGLibrary.PIX_FMT_RGB32;
            break;
        case BufferedImage.TYPE_BYTE_GRAY:
            pixelFormat = FFMPEGLibrary.PIX_FMT_GRAY8;
            break;
        case BufferedImage.TYPE_BYTE_INDEXED:
            pixelFormat = FFMPEGLibrary.PIX_FMT_PAL8;
            break;
        default:
            pixelFormat = FFMPEGLibrary.PIX_FMT_RGB32;
            break;
        }
        return pixelFormat;
    }

    public static int getPreferedBufferedImageType()
    {
        GraphicsDevice[] graphicsDevices = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices();
        GraphicsDevice graphicsDevice = graphicsDevices[0];
        GraphicsConfiguration graphicsConfiguration = graphicsDevice
                .getConfigurations()[0];
        return graphicsConfiguration.createCompatibleImage(1, 1).getType();
    }

    public static int getPreferedPixelFormat()
    {
        return
            getPixelFormatFromBufferedImageType(getPreferedBufferedImageType());
    }

    public static ContentDescriptor[] getSupportedOutputContentDescriptors(
            Format[] formats)
    {
        // get content descriptors from ffmpeg
        List<String> contentDescriptors = listMuxes();

        // FIXME listMuxes returns Strings, not ContentDescriptors.
        return
            (ContentDescriptor[])
                    contentDescriptors.toArray(new ContentDescriptor[0]);
    }

    public static final boolean isBigEndian()
    {
        return ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder());
    }

    static void listDecoder()
    {
        int i = 1;
        AVCodec avCodec = AVCODEC
                .avcodec_find_decoder_by_name(FIRST_FFMPEG_DECODER_NAME);
        // AVCodec avCodec = new
        // AVCodec((Pointer)AVCodecLibrary.first_avcodec.getValue());
        while (avCodec != null)
        {
            logger.log(Level.FINEST, i++ + ". " + avCodec.name + " ("
                    + getCodecType(avCodec.type) + ")");
            if (avCodec.next != null /* && avCodec.next.isValid() */)
            {
                avCodec = new AVCodec(avCodec.next);
            } else
            {
                avCodec = null;
            }
        }
    }

    static void listEncoder()
    {
        int i = 1;
        AVCodec avCodec = AVCODEC
                .avcodec_find_decoder_by_name(FIRST_FFMPEG_ENCODER_NAME);
        // AVCodec avCodec = new
        // AVCodec((Pointer)AVCodecLibrary.first_avcodec.getValue());
        while (avCodec != null)
        {
            logger.log(Level.FINEST, i++ + ". " + avCodec.name + " ("
                    + getCodecType(avCodec.type) + ")");
            if (avCodec.next != null /* && avCodec.next.isValid() */)
            {
                avCodec = new AVCodec(avCodec.next);
            } else
            {
                avCodec = null;
            }
        }
    }

    static List<String> listMuxes()
    {
        List<String> contentDescriptors = new ArrayList<String>();

        int i = 1;

        AVOutputFormat avOutputFormat
            = AVFORMAT.guess_format(FIRST_FFMPEG_MUX_NAME, null, null);
        // AVOutputFormat avOutputFormat = new
        // AVOutputFormat((Pointer)AVFormatLibrary.first_oformat.getValue());

        while (avOutputFormat != null)
        {
            String mimeType = avOutputFormat.mime_type;

            if (mimeType == null || (mimeType.length() <= 0))
                mimeType = "ffmpeg/" + avOutputFormat.name;

            logger.log(
                Level.FINEST,
                i++ + ". " + avOutputFormat.name + " - "
                    + avOutputFormat.long_name + " : " + mimeType);

            contentDescriptors.add(
                    ContentDescriptor.mimeTypeToPackageName(mimeType));

            avOutputFormat
                = (avOutputFormat.next != null /* && avOutputFormat.next.isValid() */)
                    ? new AVOutputFormat(avOutputFormat.next)
                    : null;
        }
        return contentDescriptors;
    }

    public static void main(String[] args) throws Exception
    {
        AVFORMAT.av_register_all();

        listMuxes();
        listDecoder();
        listEncoder();
    }
}
