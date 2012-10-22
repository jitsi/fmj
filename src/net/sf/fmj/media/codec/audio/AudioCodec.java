package net.sf.fmj.media.codec.audio;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.*;

public abstract class AudioCodec extends BasicCodec
{
    /**
     * Checks the header of the compressed audio packet and detects any format
     * changes. Does not modify the buffer in any way. TBD: how to select
     * specific output format
     */
    @Override
    public boolean checkFormat(Format format)
    {
        if (inputFormat == null || outputFormat == null
                || format != inputFormat || !format.equals(inputFormat))
        {
            inputFormat = format;
            Format fs[] = getSupportedOutputFormats(format);
            outputFormat = fs[0];
        }
        return outputFormat != null;
    }

    @Override
    public Format setInputFormat(Format format)
    {
        if (matches(format, inputFormats) == null)
            return null;
        inputFormat = format;
        return format;
    }

    @Override
    public Format setOutputFormat(Format format)
    {
        if (matches(format, getSupportedOutputFormats(inputFormat)) == null)
            return null;
        if (!(format instanceof AudioFormat))
            return null;
        outputFormat = format;
        return format;
    }
}
