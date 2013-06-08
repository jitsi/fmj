package net.sf.fmj.media;

import javax.media.*;

/**
 * implementation of the inputConnector interface
 *
 */
public class BasicInputConnector extends BasicConnector implements
        InputConnector
{
    /** the connected outputConnector */
    protected OutputConnector outputConnector = null;
    private boolean reset = false;

    /**
     * Return the OutputConnector this InputConnector is connected to. If this
     * Connector is unconnected return null.
     */
    public OutputConnector getOutputConnector()
    {
        return outputConnector;
    }

    /**
     * Get buffer object containing media.
     */
    public Buffer getValidBuffer()
    {
        // System.out.println(getClass().getName()+":: getValidBuffer");

        switch (protocol)
        {
        case ProtocolPush:
            synchronized (circularBuffer)
            {
                if (!isValidBufferAvailable() && reset)
                    return null;
                reset = false;
                return circularBuffer.read();
            }
        case ProtocolSafe:
            synchronized (circularBuffer)
            {
                reset = false;
                while (!reset && !isValidBufferAvailable())
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
                Buffer buffer = circularBuffer.read();
                circularBuffer.notifyAll();
                return buffer;
            }
        default:
            throw new RuntimeException();
        }

    }

    /**
     * returns if there are valid Buffer objects in the Connector's queue.
     */
    public boolean isValidBufferAvailable()
    {
        return circularBuffer.canRead();
    }

    /**
     * Indicates the oldest Buffer object got from this Connector was used and
     * can be "recycled" by the upstream Module.<br>
     */
    public void readReport()
    {
        // System.out.println(getClass().getName()+":: readReport");

        switch (protocol)
        {
        case ProtocolPush:
        case ProtocolSafe:
            synchronized (circularBuffer)
            {
                if (reset)
                    return;
                circularBuffer.readReport();
                circularBuffer.notifyAll();
                return;
            }
        default:
            throw new RuntimeException();
        }
    }

    @Override
    public void reset()
    {
        synchronized (circularBuffer)
        {
            reset = true;
            super.reset();
            circularBuffer.notifyAll();
        }
    }

    /**
     * Sets the OutputConnector this InputConnector is connected to. This method
     * is called by the connectTo() method of the OutputConnector.
     */
    public void setOutputConnector(OutputConnector outputConnector)
    {
        this.outputConnector = outputConnector;
    }

}
