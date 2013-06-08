package net.sf.fmj.media.codec.video;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.util.*;

/**
 * Codec that scales images from one size to another. Interestingly,
 * cross-platform JMF does not appear to have a corresponding codec.
 *
 * @author Ken Larson
 *
 */
public class ImageScaler extends AbstractCodec implements Codec
{
    // TODO: the filter graph builder won't build this into the filter graph,
    // if we specify, say a specific dimension for the output format that
    // requires the input to be scaled.
    private final Dimension DIMENSION = null; // new Dimension(160, 100);
    // TODO: all formats supported by BufferToImage
    private final Format[] supportedInputFormats = new Format[] {
            new RGBFormat(null, -1, Format.byteArray, -1.0f, -1, -1, -1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, -1, -1, -1, -1), };
    private final Format[] supportedOutputFormats = new Format[] { new RGBFormat(
            DIMENSION, -1, Format.intArray, -1.0f, -1, -1, -1, -1), };

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
        final Format[] result = new Format[] { new RGBFormat(DIMENSION, -1,
                Format.intArray, -1.0f, -1, -1, -1, -1) };
        // TODO: we have to specify the RGB, etc. in the output format.
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

        final Dimension inputSize = ((VideoFormat) inputFormat).getSize();
        final Dimension outputSize = ((VideoFormat) outputFormat).getSize();
        final double scaleX = ((double) outputSize.width)
                / ((double) inputSize.width);
        final double scaleY = ((double) outputSize.height)
                / ((double) inputSize.height);

        final BufferedImage scaled = scale(image, scaleX, scaleY); // TODO: is
                                                                   // the size
                                                                   // exact?
                                                                   // what about
                                                                   // rounding
                                                                   // errors?

        System.out.println("scaled: " + scaled.getWidth() + "x"
                + scaled.getHeight());
        final Buffer b = ImageToBuffer.createBuffer(scaled,
                ((VideoFormat) outputFormat).getFrameRate());
        output.setData(b.getData());
        output.setLength(b.getLength());
        output.setOffset(b.getOffset());
        output.setFormat(b.getFormat());
        // TODO: what about format?

        return BUFFER_PROCESSED_OK;

    }

    private BufferedImage scale(BufferedImage bi, double scaleX, double scaleY)
    {
        AffineTransform tx = new AffineTransform();
        tx.scale(scaleX, scaleY);
        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BICUBIC);
        return op.filter(bi, null);
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
