package net.sf.fmj.media;

import javax.media.*;

/**
 * implementation of the OutputConnector interface
 */
public class BasicOutputConnector extends BasicConnector implements
        OutputConnector
{
    /** the connected input connector */
    protected InputConnector inputConnector = null;
    private boolean reset = false;

    /**
     * check if a connection to the specified InputConnector would succeed.
     */
    public Format canConnectTo(InputConnector inputConnector,
            Format useThisFormat)
    {
        if (getProtocol() != inputConnector.getProtocol())
            throw new RuntimeException("protocols do not match:: ");
        return null;
    }

    /**
     * Connects an InputConnector to this OutputConnector.
     */
    public Format connectTo(InputConnector inputConnector, Format useThisFormat)
    {
        Format format = canConnectTo(inputConnector, useThisFormat);
        // if (format==null)
        // return null;

        this.inputConnector = inputConnector;
        inputConnector.setOutputConnector(this);
        int bufferSize = Math.max(getSize(), inputConnector.getSize());

        circularBuffer = new CircularBuffer(bufferSize);
        inputConnector.setCircularBuffer(circularBuffer);
        return null;
    }

    /**
     * Get an empty buffer object.
     */
    public Buffer getEmptyBuffer()
    {
        // System.out.println(getClass().getName()+":: getEmptyBuffer");

        switch (protocol)
        {
        case ProtocolPush:
            if (!isEmptyBufferAvailable() && reset)
                return null;
            reset = false;
            return circularBuffer.getEmptyBuffer();
        case ProtocolSafe:
            synchronized (circularBuffer)
            {
                reset = false;
                while (!reset && !isEmptyBufferAvailable())
                {
                    try
                    {
                        circularBuffer.wait();
                    } catch (Exception e)
                    {
                    }
                }
                if (reset)
                    return null;
                Buffer buffer = circularBuffer.getEmptyBuffer();
                circularBuffer.notifyAll();
                return buffer;
            }

        default:
            throw new RuntimeException();
        }
    }

    /**
     * Return the InputConnectore this OutputConnector is connected to. If this
     * Connector is unconnected return null.
     */
    public InputConnector getInputConnector()
    {
        return inputConnector;
    }

    /**
     * checks if there are empty Buffer objects in the Connector's queue.
     */
    public boolean isEmptyBufferAvailable()
    {
        return circularBuffer.canWrite();
    }

    @Override
    public void reset()
    {
        synchronized (circularBuffer)
        {
            reset = true;
            super.reset();
            if (inputConnector != null)
                inputConnector.reset();
            circularBuffer.notifyAll();
        }
    }

    /**
     * put media chunk in the queue
     *
     */
    public void writeReport()
    {
        // System.out.println(getClass().getName()+":: writeReport ");

        switch (protocol)
        {
        case ProtocolPush:
            synchronized (circularBuffer)
            {
                if (reset /* && pendingWriteReport */)
                    return;
                circularBuffer.writeReport();
            }
            getInputConnector().getModule()
                    .connectorPushed(getInputConnector());
            return;
        case ProtocolSafe:
            synchronized (circularBuffer)
            {
                if (reset)
                    return;
                circularBuffer.writeReport();
                circularBuffer.notifyAll();
                return;
            }
        default:
            throw new RuntimeException();
        }

    }

}
