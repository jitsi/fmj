package com.t4l.jmf;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.imageio.*;
import javax.imageio.stream.*;
import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.utility.*;

/**
 * This type of MOV file just stores frames as a series of JPEG images. This
 * codec uses ImageIO classes to convert from the jpeg VideoFormat to
 * RGBFormats.
 *
 * @author Jeremy Wood
 */
public class JPEGDecoder implements Codec
{
    private static final Logger logger = LoggerSingleton.logger;

    private static final JPEGFormat jpegFormat = new JPEGFormat();
    private static final RGBFormat rgbFormat = new RGBFormat(null, -1,
            Format.intArray, -1.f, -1, -1, -1, -1);

    static Hashtable<Dimension, BufferedImage> imageTable
        = new Hashtable<Dimension, BufferedImage>();

    protected static void readJPEG(byte[] data, BufferedImage dest)
            throws IOException
    {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ImageInputStream stream = ImageIO.createImageInputStream(in);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
        ImageReader reader = iter.next();
        if (reader == null)
            throw new UnsupportedOperationException(
                    "This image is unsupported.");
        reader.setInput(stream, false);

        ImageReadParam param = reader.getDefaultReadParam();
        param.setDestination(dest);
        reader.read(0, param);
    }

    public void close()
    {
        synchronized (imageTable)
        {
            imageTable.clear();
        }
    }

    public Object getControl(String controlType)
    {
        return null;
    }

    public Object[] getControls()
    {
        return new String[] {};
    }

    public String getName()
    {
        return "JPEG Decoder";
    }

    public Format[] getSupportedInputFormats()
    {
        return new VideoFormat[] { jpegFormat };
    }

    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
            return new VideoFormat[] { rgbFormat };

        if (input.relax().matches(jpegFormat))
        {
            final VideoFormat inputVideoFormat = (VideoFormat) input;
            // TODO:
            return new VideoFormat[] { new RGBFormat(
                    inputVideoFormat.getSize(), -1, Format.intArray,
                    inputVideoFormat.getFrameRate(), 32, 0xff0000, 0xff00, 0xff) };
        }
        return new Format[] {};
    }

    public void open() throws ResourceUnavailableException
    {
    }

    public int process(Buffer input, Buffer output)
    {
        Format inputFormat = input.getFormat();
        Format outputFormat = output.getFormat();

        if (inputFormat.relax().matches(jpegFormat)
                && (outputFormat == null || outputFormat.relax().matches(
                        rgbFormat)))
        {
            return processJPEGtoRGB(input, output);
        }
        return PlugIn.BUFFER_PROCESSED_FAILED;
    }

    protected int processJPEGtoRGB(Buffer input, Buffer output)
    {
        synchronized (imageTable)
        {
            try
            {
                VideoFormat inputFormat = (VideoFormat) input.getFormat();
                RGBFormat outputFormat = (RGBFormat) output.getFormat();

                if (outputFormat == null)
                {
                    int width = inputFormat.getSize().width;
                    int height = inputFormat.getSize().height;
                    outputFormat = new RGBFormat(new Dimension(width, height),
                            width * height, Format.intArray,
                            inputFormat.getFrameRate(), 32, 0xff0000, 0xff00,
                            0xff, // RGB masks
                            1, width, // pixel stride, line stride
                            Format.FALSE, RGBFormat.LITTLE_ENDIAN);
                    output.setFormat(outputFormat);
                }

                byte[] b = (byte[]) input.getData();
                Dimension d = inputFormat.getSize();
                BufferedImage dest = imageTable.get(d);
                if (dest == null)
                {
                    dest = new BufferedImage(d.width, d.height,
                            BufferedImage.TYPE_INT_RGB);
                }
                readJPEG(b, dest);

                imageTable.put(d, dest);

                Object obj = output.getData();
                int[] intArray;
                if (obj instanceof int[])
                {
                    intArray = (int[]) obj;
                } else
                {
                    intArray = new int[dest.getWidth() * dest.getHeight()];
                    output.setData(intArray);
                }

                RGBConverter.populateArray(dest, intArray,
                        (RGBFormat) output.getFormat());

                output.setDiscard(input.isDiscard());
                output.setDuration(input.getDuration());
                output.setEOM(input.isEOM());
                output.setFlags(input.getFlags()); // is this correct?
                output.setHeader(null);
                output.setTimeStamp(input.getTimeStamp());
                output.setSequenceNumber(input.getSequenceNumber());
                output.setOffset(0);
                output.setLength(dest.getWidth() * dest.getHeight());
                return PlugIn.BUFFER_PROCESSED_OK;
            } catch (Throwable t)
            {
                logger.log(Level.WARNING, "" + t, t);
                return PlugIn.BUFFER_PROCESSED_FAILED;
            }
        }
    }

    public void reset()
    {
    }

    public Format setInputFormat(Format f)
    {
        // we don't do anything here; just return positive feedback that we
        // can in fact handle whatever it gives us
        if (f.relax().matches(jpegFormat))
        {
            return f;
        }
        return null;
    }

    public Format setOutputFormat(Format f)
    {
        // we don't do anything here; just return positive feedback that we
        // can in fact handle whatever it gives us
        if (f.relax().matches(rgbFormat))
        {
            return f;
        }
        return null;
    }
}
