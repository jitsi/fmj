package net.sf.fmj.utility;

import java.util.*;

import javax.media.*;

import com.lti.utils.*;

/**
 * Useful for converting JMF/FMJ objects to strings for logging.
 *
 * @author Ken Larson
 *
 */
public final class LoggingStringUtils
{
    public static String bufferFlagsToStr(int flags)
    {
        List<String> strings = new ArrayList<String>();
        if ((flags & Buffer.FLAG_EOM) != 0)
            strings.add("FLAG_EOM");
        if ((flags & Buffer.FLAG_DISCARD) != 0)
            strings.add("FLAG_DISCARD");
        if ((flags & Buffer.FLAG_SILENCE) != 0)
            strings.add("FLAG_SILENCE");
        if ((flags & Buffer.FLAG_SID) != 0)
            strings.add("FLAG_SID");
        if ((flags & Buffer.FLAG_KEY_FRAME) != 0)
            strings.add("FLAG_KEY_FRAME");
        if ((flags & Buffer.FLAG_NO_WAIT) != 0)
            strings.add("FLAG_NO_WAIT");
        if ((flags & Buffer.FLAG_NO_SYNC) != 0)
            strings.add("FLAG_NO_SYNC");
        if ((flags & Buffer.FLAG_SYSTEM_TIME) != 0)
            strings.add("FLAG_SYSTEM_TIME");
        if ((flags & Buffer.FLAG_RELATIVE_TIME) != 0)
            strings.add("FLAG_RELATIVE_TIME");
        if ((flags & Buffer.FLAG_FLUSH) != 0)
            strings.add("FLAG_FLUSH");
        if ((flags & Buffer.FLAG_SYSTEM_MARKER) != 0)
            strings.add("FLAG_SYSTEM_MARKER");
        if ((flags & Buffer.FLAG_RTP_MARKER) != 0)
            strings.add("FLAG_RTP_MARKER");
        if ((flags & Buffer.FLAG_RTP_TIME) != 0)
            strings.add("FLAG_RTP_TIME");
        if ((flags & Buffer.FLAG_BUF_OVERFLOWN) != 0)
            strings.add("FLAG_BUF_OVERFLOWN");
        if ((flags & Buffer.FLAG_BUF_UNDERFLOWN) != 0)
            strings.add("FLAG_BUF_UNDERFLOWN");
        if ((flags & Buffer.FLAG_LIVE_DATA) != 0)
            strings.add("FLAG_LIVE_DATA");

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < strings.size(); ++i)
        {
            if (b.length() != 0)
                b.append(" | ");
            b.append(strings.get(i));
        }
        return b.toString();

    }

    public static String bufferToStr(Buffer buffer)
    {
        if (buffer == null)
            return "null";
        StringBuffer b = new StringBuffer();
        b.append(buffer);

        b.append(" seq=" + buffer.getSequenceNumber());
        b.append(" off=" + buffer.getOffset());
        b.append(" len=" + buffer.getLength());
        b.append(" flags=[" + bufferFlagsToStr(buffer.getFlags()) + "]");
        b.append(" fmt=[" + buffer.getFormat() + "]");
        if (buffer.getData() != null && buffer.getData() instanceof byte[])
            b.append(" data=["
                    + buffer.getData()
                    + " "
                    + StringUtils.byteArrayToHexString(
                            (byte[]) buffer.getData(), buffer.getLength(),
                            buffer.getOffset()) + "]");
        else if (buffer.getData() != null)
            b.append(" data=[" + buffer.getData() + "]");
        else
            b.append(" data=[null]");

        return b.toString();
    }

    public static String formatToStr(Format f)
    {
        return "" + f; // We could also use MediaCGUtils to be more specific.
    }

    public static String plugInResultToStr(int result)
    {
        switch (result)
        {
        case PlugIn.BUFFER_PROCESSED_OK:
            return "BUFFER_PROCESSED_OK";
        case PlugIn.BUFFER_PROCESSED_FAILED:
            return "BUFFER_PROCESSED_FAILED";
        case PlugIn.INPUT_BUFFER_NOT_CONSUMED:
            return "INPUT_BUFFER_NOT_CONSUMED";
        case PlugIn.OUTPUT_BUFFER_NOT_FILLED:
            return "OUTPUT_BUFFER_NOT_FILLED";
        case PlugIn.PLUGIN_TERMINATED:
            return "PLUGIN_TERMINATED";
        default:
            return "" + result;
        }
    }

    private LoggingStringUtils()
    {
        super();
    }
}
