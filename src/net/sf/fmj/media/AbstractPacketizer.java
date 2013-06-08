package net.sf.fmj.media;

import java.awt.*;

import javax.media.*;
import javax.media.control.*;

import net.sf.fmj.utility.*;

/**
 * Abstract base class to implement a packetizer codec.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractPacketizer extends AbstractCodec
{
    private class PSC implements PacketSizeControl, Owned
    {
        public Component getControlComponent()
        {
            return null;
        }

        public Object getOwner()
        {
            return AbstractPacketizer.this;
        }

        public int getPacketSize()
        {
            return packetSize;
        }

        public int setPacketSize(int numBytes)
        {
            setPacketSizeImpl(numBytes);
            return packetSize;
        }
    }

    private static final boolean TRACE = false;
    private int packetSize; // includes packet header size, if any
    private byte[] packetBuffer;
    private int bytesInPacketBuffer = 0;

    private boolean doNotSpanInputBuffers = false;

    // do not span input buffers. That is, if we have to send out a shorter than
    // packetSize packet because an inputBuffer does not divide evenly by
    // packetSize, do so. Otherwise, the data will carry over to the next
    // packet.
    // it makes sense for doNotSpanInputBuffers to be true for audio, and false
    // for video.

    public AbstractPacketizer()
    {
        addControl(new PSC());
    }

    /**
     * @return the number of bytes added to the packetBuffer.
     */
    protected int doBuildPacketHeader(Buffer inputBuffer, byte[] packetBuffer)
    {
        return 0;
    }

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

        if (bytesInPacketBuffer == 0)
        {
            final int packetHeaderSize = doBuildPacketHeader(inputBuffer,
                    packetBuffer);
            bytesInPacketBuffer += packetHeaderSize;
        }
        final int bytesNeededToCompletePacket = packetSize
                - bytesInPacketBuffer;
        final int bytesAvailable = inputBuffer.getLength();
        final int bytesToCopy = bytesNeededToCompletePacket < bytesAvailable ? bytesNeededToCompletePacket
                : bytesAvailable;

        System.arraycopy(inputBuffer.getData(), inputBuffer.getOffset(),
                packetBuffer, bytesInPacketBuffer, bytesToCopy);
        bytesInPacketBuffer += bytesToCopy;

        inputBuffer.setOffset(inputBuffer.getOffset() + bytesToCopy);
        inputBuffer.setLength(inputBuffer.getLength() - bytesToCopy);

        final boolean packetComplete = (doNotSpanInputBuffers && inputBuffer
                .getLength() == 0) || bytesInPacketBuffer == packetSize;

        final int result;

        if (packetComplete)
        {
            outputBuffer.setData(packetBuffer);
            outputBuffer.setOffset(0);
            outputBuffer.setLength(bytesInPacketBuffer);
            bytesInPacketBuffer = 0;

            if (inputBuffer.getLength() == 0)
                result = BUFFER_PROCESSED_OK;
            else
                result = INPUT_BUFFER_NOT_CONSUMED;
        } else
        {
            result = OUTPUT_BUFFER_NOT_FILLED;
        }

        if (TRACE)
        {
            dump("input ", inputBuffer);
            dump("output", outputBuffer);

            System.out.println("Result="
                    + LoggingStringUtils.plugInResultToStr(result));
        }
        return result;
    }

    protected void setDoNotSpanInputBuffers(boolean doNotSpanInputBuffers)
    {
        this.doNotSpanInputBuffers = doNotSpanInputBuffers;
    }

    protected void setPacketSize(int packetSize)
    {
        setPacketSizeImpl(packetSize);
    }

    protected void setPacketSizeImpl(int packetSize)
    {
        this.packetSize = packetSize;
        packetBuffer = new byte[packetSize];
        // TODO: copy old data if there was any.
    }
}
