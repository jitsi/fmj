package net.sf.fmj.media;

import java.awt.*;

import javax.media.*;
import javax.media.format.*;

public abstract class BasicCodec extends BasicPlugIn implements Codec
{
    private static final boolean DEBUG = true;

    protected Format inputFormat;

    protected Format outputFormat;

    protected boolean opened = false;

    protected Format[] inputFormats = new Format[0];

    protected Format[] outputFormats = new Format[0];

    protected boolean pendingEOM = false;

    protected int checkEOM(Buffer inputBuffer, Buffer outputBuffer)
    {
        processAtEOM(inputBuffer, outputBuffer); // process tail of input

        // this is a little tricky since we have to output two frames now:
        // one to close former session, another to signle EOM
        if (outputBuffer.getLength() > 0)
        {
            pendingEOM = true;
            return BUFFER_PROCESSED_OK | INPUT_BUFFER_NOT_CONSUMED;
        } else
        {
            // in case we have nothing in the output, we are done
            propagateEOM(outputBuffer);
            return BUFFER_PROCESSED_OK;
        }
    }

    protected boolean checkFormat(Format format)
    {
        return true;
    }

    protected boolean checkInputBuffer(Buffer inputBuffer)
    {
        boolean fError = !isEOM(inputBuffer)
                && (inputBuffer == null || inputBuffer.getFormat() == null || !checkFormat(inputBuffer
                        .getFormat()));

        if (DEBUG)
            if (fError)
                System.out.println(getClass().getName()
                        + " : [error] checkInputBuffer");

        return !fError;

    }

    public void close()
    {
        opened = false;
    }

    protected int getArrayElementSize(Class<?> type)
    {
        if (type == Format.intArray)
            return 4;
        else if (type == Format.shortArray)
            return 2;
        else if (type == Format.byteArray)
            return 1;
        else
            return 0;
    }

    protected Format getInputFormat()
    {
        return inputFormat;
    }

    protected Format getOutputFormat()
    {
        return outputFormat;
    }

    public Format[] getSupportedInputFormats()
    {
        return inputFormats;
    }

    protected boolean isEOM(Buffer inputBuffer)
    {
        return inputBuffer.isEOM();
    }

    public void open() throws ResourceUnavailableException
    {
        opened = true;
    }

    protected int processAtEOM(Buffer inputBuffer, Buffer outputBuffer)
    {
        return 0;
    }

    protected void propagateEOM(Buffer outputBuffer)
    {
        updateOutput(outputBuffer, getOutputFormat(), 0, 0);
        outputBuffer.setEOM(true);
    }

    public void reset()
    {
    }

    public Format setInputFormat(Format input)
    {
        inputFormat = input;
        return input;
    }

    public Format setOutputFormat(Format output)
    {
        outputFormat = output;
        return output;
    }

    protected void updateOutput(Buffer outputBuffer, Format format, int length,
            int offset)
    {
        outputBuffer.setFormat(format);
        outputBuffer.setLength(length);
        outputBuffer.setOffset(offset);
    }

    protected RGBFormat updateRGBFormat(VideoFormat newFormat,
            RGBFormat outputFormat)
    {
        Dimension size = newFormat.getSize();
        RGBFormat oldFormat = outputFormat;
        int lineStride = size.width * oldFormat.getPixelStride();
        RGBFormat newRGB = new RGBFormat(size, lineStride * size.height,
                oldFormat.getDataType(), newFormat.getFrameRate(),
                oldFormat.getBitsPerPixel(), oldFormat.getRedMask(),
                oldFormat.getGreenMask(), oldFormat.getBlueMask(),
                oldFormat.getPixelStride(), lineStride, oldFormat.getFlipped(),
                oldFormat.getEndian());
        return newRGB;
    }
}
