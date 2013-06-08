package net.sf.fmj.media;

import javax.media.*;

/**
 * InputConnector defines the buffer movement and format typing interface for
 * input connectors.
 *
 * @see OutputConnector
 *
 */
public interface InputConnector extends Connector
{
    /**
     * Return the OutputConnector this InputConnector is connected to. If this
     * Connector is unconnected return null.
     */
    public OutputConnector getOutputConnector();

    /**
     * Get buffer object containing media.<br>
     * The exact behavior depands on the protocol:
     * <ul>
     * <li><b>ProtocolPush</b> - if buffer is not available throws
     * RuntimeException</li>
     * <li><b>ProtocolSafe</b> - if buffer is available read the buffer and
     * perform notify() on the connection.<br>
     * if buffer is not available perform wait() on the connection.</li>
     * </ul>
     *
     */
    public Buffer getValidBuffer();

    /**
     * checks if there are valid Buffer objects in the Connector's queue.
     *
     * @return if there are vaild Buffer objects in the Connector's queue.
     */
    public boolean isValidBufferAvailable();

    /**
     * Indicates the oldest Buffer object got from this Connector was used and
     * can be "recycled" by the upstream Module.<br>
     * if such buffer Objects does not exists throws RuntimeException. The exact
     * behavior depands on the protocol:
     * <ul>
     * <li><b>ProtocolPush</b> - no operation.</li>
     * <li><b>ProtocolSafe</b> - perform notify() on the connection lock object.
     * <br>
     * </ul>
     *
     */
    public void readReport();

    /**
     * Sets the OutputConnector this InputConnector is connected to. This method
     * is called by the connectTo() method of the OutputConnector.
     */
    public void setOutputConnector(OutputConnector outputConnector);

}
