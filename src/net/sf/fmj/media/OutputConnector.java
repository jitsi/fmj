package net.sf.fmj.media;

import javax.media.*;

/**
 * OutputConnector defines the buffer movement and format typing interface for
 * output connectors.
 *
 * @see InputConnector
 * @see Module
 *
 */
public interface OutputConnector extends Connector
{
    /**
     * check if a connection to the specified InputConnector would succeed.
     *
     * @param inputConnector
     *            input connector to check connection to.
     * @param useThisFormat
     *            states the format of the connection. If it is null the format
     *            would be negotiated.
     * @return the Format of the connection, null if the connection would fail.
     */
    public Format canConnectTo(InputConnector inputConnector,
            Format useThisFormat);

    /**
     * Connects an InputConnector to this OutputConnector. This method should
     * only be called by the Manager when it is connecting this OutputConnector.<br>
     * The protocol of the two connectors must match. <br>
     * the number of buffer objects in the created connection should be at least
     * the size requested by both the input and the output connector
     *
     * @param inputConnector
     *            input connector to connect to.
     * @param useThisFormat
     *            states the format of the connection. If it is null the format
     *            would be negotiated.
     * @return the Format of the connection.
     */
    public Format connectTo(InputConnector inputConnector, Format useThisFormat);

    /**
     * Get an empty buffer object. The exact behavior depands on the protocol:
     * <ul>
     * <li><b>ProtocolPush</b> - if empty buffer is not available throws
     * RuntimeException</li>
     * <li><b>ProtocolSafe</b> - if empty buffer is available read the buffer.<br>
     * if empty buffer is not available perform wait() on the connection.</li>
     * </ul>
     *
     * <i> Note that this method can be called several times before it blocks
     * (depending on circular buffer size). </i>
     */
    public Buffer getEmptyBuffer();

    /**
     * Return the InputConnectore this OutputConnector is connected to. If this
     * Connector is unconnected return null.
     *
     * @return the InputConnector this is connected to.
     */
    public InputConnector getInputConnector();

    /**
     * checks if there are empty Buffer objects in the Connector's queue.
     *
     * @return true if there are empty Buffer objects in the Connector's queue.
     */
    public boolean isEmptyBufferAvailable();

    /**
     * Indicates the oldest Buffer object got from this Connector (by calling
     * the getEmptyBuffer method) now contains valid buffer object, which can be
     * used by the downstream Module.<br>
     *
     * if such buffer Objects does not exists throws RuntimeException.
     *
     * The exact behavior depands on the protocol:
     * <ul>
     * <li><b>ProtocolPush</b> - if bufferValid call the connected
     * InputConnector Module connectorPushed() method in the calling thread.
     * method blocks until the downstream module finishes processing the buffer.
     * </li>
     * <li><b>ProtocolSafe</b> - perform notify() on the connection lock object.
     * <br>
     * </ul>
     *
     * <i>The bufferValid flag was removed. If the buffer Object is just
     * returned, it should be marked in the Buffer itself, by specifying this is
     * a discarded Buffer chunk. </i>
     *
     * @see javax.media.Buffer
     */
    public void writeReport();

}
