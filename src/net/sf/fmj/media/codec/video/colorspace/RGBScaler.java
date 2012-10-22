package net.sf.fmj.media.codec.video.colorspace;

import java.awt.*;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.*;

/**
 * This the pure java RGB scaler. It works on 24-bit RGB data. It has two
 * quality settings 0.5 or less implies nearest neighbour. more the 0.5 implies
 * bilinear. NOTE: bilinear not yet implemented
 */
public class RGBScaler extends BasicCodec
{
    protected float quality = 0.5f;

    private int nativeData = 0;

    private static boolean nativeAvailable = false;

    public RGBScaler()
    {
        this(null);
    }

    public RGBScaler(Dimension sizeOut)
    {
        inputFormats = new Format[] { new RGBFormat(null, Format.NOT_SPECIFIED,
                Format.byteArray, Format.NOT_SPECIFIED, 24, 3, 2, 1, 3,
                Format.NOT_SPECIFIED, Format.FALSE, Format.NOT_SPECIFIED) };

        if (sizeOut != null)
            setOutputSize(sizeOut);
    }

    @Override
    public void close()
    {
        super.close();
        if (nativeAvailable && nativeData != 0)
        {
            try
            {
                nativeClose();
            } catch (Throwable t)
            {
            }
        }
    }

    public String getName()
    {
        return "RGB Scaler";
    }

    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
        {
            return outputFormats;
        }

        if (matches(input, inputFormats) != null)
        {
            // Need to use the incoming frame rate for the output
            float frameRate = ((VideoFormat) input).getFrameRate();
            VideoFormat frameRateFormat = new VideoFormat(null, null,
                    Format.NOT_SPECIFIED, null, frameRate);

            return new Format[] { outputFormats[0].intersects(frameRateFormat) };
        } else
        {
            return new Format[0];
        }
    }

    private native void nativeClose();

    private native void nativeScale(Object inData, long inBytes,
            Object outData, long outBytes, int psIn, int lsIn, int wIn,
            int hIn, int psOut, int lsOut, int wOut, int hOut);

    protected void nearestNeighbour(Buffer inBuffer, Buffer outBuffer)
    {
        RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
        Dimension sizeIn = vfIn.getSize();
        RGBFormat vfOut = (RGBFormat) outBuffer.getFormat();
        Dimension sizeOut = vfOut.getSize();
        int pixStrideIn = vfIn.getPixelStride();
        int pixStrideOut = vfOut.getPixelStride();
        int lineStrideIn = vfIn.getLineStride();
        int lineStrideOut = vfOut.getLineStride();
        float horRatio = (float) sizeIn.width / sizeOut.width;
        float verRatio = (float) sizeIn.height / sizeOut.height;
        Object inObj;
        Object outObj;
        long inBytes = 0;
        long outBytes = 0;

        if (nativeAvailable)
        {
            inObj = getInputData(inBuffer);
            outObj = validateData(outBuffer, 0, true);
            inBytes = getNativeData(inObj);
            outBytes = getNativeData(outObj);
        } else
        {
            inObj = inBuffer.getData();
            outObj = outBuffer.getData();
        }

        // Try it the first time assuming native is available
        // If not, set nativeAvailable to false and use the
        // java version
        if (nativeAvailable)
        {
            try
            {
                nativeScale(inObj, inBytes, outObj, outBytes, pixStrideIn,
                        lineStrideIn, sizeIn.width, sizeIn.height,
                        pixStrideOut, lineStrideOut, sizeOut.width,
                        sizeOut.height);
            } catch (Throwable t)
            {
                nativeAvailable = false;
            }
        }

        if (!nativeAvailable)
        {
            byte[] inData = (byte[]) inObj;
            byte[] outData = (byte[]) outObj;
            for (int y = 0; y < sizeOut.height; y++)
            {
                int ptrOut = y * lineStrideOut;
                int ptrIn = (int) (y * verRatio) * lineStrideIn;
                for (int x = 0; x < sizeOut.width; x++)
                {
                    int ptrIn2 = ptrIn + (int) (x * horRatio) * pixStrideIn;
                    outData[ptrOut] = inData[ptrIn2];
                    outData[ptrOut + 1] = inData[ptrIn2 + 1];
                    outData[ptrOut + 2] = inData[ptrIn2 + 2];
                    ptrOut += pixStrideOut;
                }
            }
        }
    }

    public int process(Buffer inBuffer, Buffer outBuffer)
    {
        // System.err.println("Input = " + inputFormat + "\nOutput = " +
        // outputFormat);
        int outputDataLength = ((VideoFormat) outputFormat).getMaxDataLength();

        outBuffer.setLength(outputDataLength);
        outBuffer.setFormat(outputFormat);

        if (quality <= 0.5f)
        {
            nearestNeighbour(inBuffer, outBuffer);
        }

        // outBuffer.setDiscard(true);
        return BUFFER_PROCESSED_OK;

    }

    @Override
    public Format setInputFormat(Format input)
    {
        if (matches(input, inputFormats) == null)
            return null;
        else
            return input;
    }

    @Override
    public Format setOutputFormat(Format output)
    {
        if (output == null || matches(output, outputFormats) == null)
            return null;
        RGBFormat incoming = (RGBFormat) output;

        Dimension size = incoming.getSize();
        int maxDataLength = incoming.getMaxDataLength();
        int lineStride = incoming.getLineStride();
        float frameRate = incoming.getFrameRate();
        int flipped = incoming.getFlipped();
        int endian = incoming.getEndian();

        if (size == null)
            return null;
        if (maxDataLength < size.width * size.height * 3)
            maxDataLength = size.width * size.height * 3;
        if (lineStride < size.width * 3)
            lineStride = size.width * 3;
        if (flipped != Format.FALSE)
            flipped = Format.FALSE;

        outputFormat = outputFormats[0].intersects(new RGBFormat(size,
                maxDataLength, null, frameRate, Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, lineStride,
                Format.NOT_SPECIFIED, Format.NOT_SPECIFIED));
        return outputFormat;
    }

    public void setOutputSize(Dimension sizeOut)
    {
        outputFormats = new Format[] { new RGBFormat(sizeOut, sizeOut.width
                * sizeOut.height * 3, Format.byteArray, Format.NOT_SPECIFIED,
                24, 3, 2, 1, 3, sizeOut.width * 3, Format.FALSE,
                Format.NOT_SPECIFIED) };
    }
}
