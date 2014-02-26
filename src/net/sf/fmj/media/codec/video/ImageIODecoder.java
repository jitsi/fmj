package net.sf.fmj.media.codec.video;

import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.util.*;

/**
 * Base class for video codecs which use ImageIO to decode the images.
 *
 * @author Ken Larson
 *
 */
public abstract class ImageIODecoder extends AbstractCodec implements Codec
{
    private final Format[] supportedOutputFormats = new Format[] { new RGBFormat(
            null, -1, Format.byteArray, -1.0f, -1, -1, -1, -1), };

    public ImageIODecoder(String formatName)
    {
        if (!ImageIO.getImageReadersByFormatName(formatName).hasNext())
        {
            throw new RuntimeException("No ImageIO reader found for "
                    + formatName);
        }
    }

    @Override
    public abstract Format[] getSupportedInputFormats();

    @Override
    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
            return supportedOutputFormats;

        final VideoFormat inputCast = (VideoFormat) input;
        final Format[] result = new Format[] { new RGBFormat(
                inputCast.getSize(), -1, Format.byteArray,
                inputCast.getFrameRate(), -1, -1, -1, -1) };
        // TODO: we don't know the details of the output format (pixel masks,
        // etc) until we actually parse one!

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

        try
        {
            // TODO: this is very inefficient - it allocates a new byte array
            // (or more) every time
            final ByteArrayInputStream is = new ByteArrayInputStream(
                    (byte[]) input.getData(), input.getOffset(),
                    input.getLength());
            final BufferedImage image = ImageIO.read(is);
            is.close();
            final Buffer b = ImageToBuffer.createBuffer(image,
                    ((VideoFormat) outputFormat).getFrameRate());

            output.setData(b.getData());
            output.setOffset(b.getOffset());
            output.setLength(b.getLength());
            output.setFormat(b.getFormat()); // TODO: this is a bit hacky, this
                                             // format will be more specific
                                             // than the actual set output
                                             // format, because now we know what
                                             // ImageIO gave us for a
                                             // BufferedImage as far as pixel
                                             // masks, etc.

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
        // TODO: check VideoFormat and compatibility
        return super.setInputFormat(format);
    }
}