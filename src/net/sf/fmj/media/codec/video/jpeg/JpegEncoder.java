package net.sf.fmj.media.codec.video.jpeg;

import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.*;
import javax.imageio.stream.*;
import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.util.*;

/**
 * JPEG encoder Codec. Interesting that JMF doesn't include such an encoder in
 * cross-platform JMF. JpegEncoder is not used in JPEG/RTP anymore, all the
 * encoding for JPEG/RTP is done in the packetizer.
 *
 * @author Ken Larson
 * @author Werner Dittman
 *
 */
public class JpegEncoder extends AbstractCodec implements Codec
{
    private final Format[] supportedInputFormats = new Format[] {
            new RGBFormat(null, -1, Format.byteArray, -1.0f, -1, -1, -1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, -1, -1, -1, -1), };
    private final Format[] supportedOutputFormats = new Format[] { new JPEGFormat(), };

    private BufferToImage bufferToImage;

    @Override
    public Format[] getSupportedInputFormats()
    {
        return supportedInputFormats;
    }

    @Override
    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
            return supportedOutputFormats;
        VideoFormat inputCast = (VideoFormat) input;
        final Format[] result = new Format[] { new JPEGFormat(
                inputCast.getSize(), -1, Format.byteArray,
                inputCast.getFrameRate(), -1, -1) };

        return result;
    }

    @Override
    public int process(Buffer input, Buffer output)
    {
        if (!checkInputBuffer(input))
        {
            return BUFFER_PROCESSED_FAILED;
        }

        if (isEOM(input))
        {
            propagateEOM(output); // TODO: what about data? can there be any?
            return BUFFER_PROCESSED_OK;
        }

        final BufferedImage image = (BufferedImage) bufferToImage
                .createImage(input);

        try
        {
            // TODO: this is very inefficient - it allocates a new byte array
            // (or more) every time

            // TODO: trying to get good compression of safexmas.avi frames, but
            // they end up being
            // 10k each at 50% quality. JMF sends them at about 3k each with 74%
            // quality.
            // I think the reason is that JMF is probably encoding the YUV in
            // the jpeg, rather
            // than the 24-bit RGB that FMJ would use when using the ffmpeg-java
            // demux.

            // TODO: we should also use a JPEGFormat explicitly, and honor those
            // params.

            JPEGImageWriteParam param = new JPEGImageWriteParam(null);
            // final JPEGEncodeParam param =
            // JPEGCodec.getDefaultJPEGEncodeParam(image);

            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.74f);
            // param.setQuality(0.74f, true);

            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            MemoryCacheImageOutputStream out = new MemoryCacheImageOutputStream(
                    os);
            ImageWriter encoder = ImageIO.getImageWritersByFormatName("JPEG")
                    .next();
            // final JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(os,
            // param);
            encoder.setOutput(out);
            encoder.write(null, new IIOImage(image, null, null), param);
            // jpeg.encode(image);

            out.close();
            os.close();

            final byte[] ba = os.toByteArray();
            output.setData(ba);
            output.setOffset(0);
            output.setLength(ba.length);
            // System.out.println("Encoded jpeg to len: " + ba.length);
            return BUFFER_PROCESSED_OK;

        } catch (IOException e)
        {
            output.setDiscard(true);
            output.setLength(0);
            return BUFFER_PROCESSED_FAILED;
        }

    }

    @Override
    public Format setInputFormat(Format format)
    {
        final VideoFormat videoFormat = (VideoFormat) format;
        if (videoFormat.getSize() == null)
            return null; // must set a size.
        // logger.fine("FORMAT: " + MediaCGUtils.formatToStr(format));
        // TODO: check VideoFormat and compatibility
        bufferToImage = new BufferToImage((VideoFormat) format);
        return super.setInputFormat(format);
    }
}
