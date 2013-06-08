package net.sf.fmj.media;

import javax.media.*;

import net.sf.fmj.utility.*;

/**
 *
 * Generic DePacketizer base class. Doesn't have to do much, just copies input
 * to output. Uses buffer-swapping observed in debugging and seen in other
 * open-source DePacketizer implementations.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractDePacketizer extends AbstractCodec
{
    private static final boolean TRACE = false;

    @Override
    public int process(Buffer inputBuffer, Buffer outputBuffer)
    {
        if (TRACE)
            dump("input ", inputBuffer);

        if (!checkInputBuffer(inputBuffer))
        {
            return BUFFER_PROCESSED_FAILED;
        }

        if (isEOM(inputBuffer))
        {
            propagateEOM(outputBuffer); // TODO: what about data? can there be
                                        // any?
            return BUFFER_PROCESSED_OK;
        }

        final Object temp = outputBuffer.getData();
        outputBuffer.setData(inputBuffer.getData());
        inputBuffer.setData(temp);
        outputBuffer.setLength(inputBuffer.getLength());
        outputBuffer.setFormat(outputFormat);
        outputBuffer.setOffset(inputBuffer.getOffset());
        int result = BUFFER_PROCESSED_OK;

        if (TRACE)
        {
            dump("input ", inputBuffer);
            dump("output", outputBuffer);

            System.out.println("Result="
                    + LoggingStringUtils.plugInResultToStr(result));
        }
        return result;
    }
}
