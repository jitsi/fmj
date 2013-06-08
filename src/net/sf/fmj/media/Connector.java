package net.sf.fmj.media;

import javax.media.*;

/**
 * Defines common interface to input and output Connectors. Inter Module
 * Connection is made of InputConnector and OutputConnector and is essentially a
 * cyclic queue of references to Buffer Objects.<br>
 * The queue is constructed at the Connectors connectTo() method, so the
 * suggested size should be set before that.<br>
 *
 * <i><br>
 * Note that these connectors are almost symetrical so they can support both
 * push and pull connection. <br>
 * In addition the two threads sync. mechanism is also supported by this
 * suggested API. </i>
 *
 *
 *
 *
 *
 * @see Module
 * @see InputConnector
 * @see OutputConnector
 * @see javax.media.Format
 *
 */
public interface Connector
{
    /**
     * constant to indicate that this connector runs on "Push" protocol. meaning
     * writing to this connector is propagated to the connected InputConnector.
     */
    public static int ProtocolPush = 0;

    /**
     * constant to indicate that this connector runs on "Safe" protocol. meaning
     * reading and writing from this connection is done using thread safe
     * monitor.
     */
    public static int ProtocolSafe = 1;

    /**
     * returns the circular buffer Object which is locked (by wait()/notify()
     * )during safe data transfer. <br>
     * <i>This method should not really be part of the API, but it is put here
     * in order to remove implementation dependencies.</i>
     */
    public Object getCircularBuffer();

    /**
     * The selected format. If <b>setFormat()</b> has not been called,
     * <b>getFormat()</b> will return null.
     *
     * @return the currently selected format.
     */
    public Format getFormat();

    /**
     * Returns the Module which registered this Connector.
     */
    public Module getModule();

    /** returns the name of this Connector in the owning Module */
    public String getName();

    /**
     * returns the data transfer protocol used by this connector. <br>
     * either <i>ProtocolPush, ProtocolSafe</i>
     */
    public int getProtocol();

    /**
     * gets the <b>minimum</b> number of buffer objects this Connector should
     * create.
     */
    public int getSize();

    /**
     * restores this Connector to its initial state: removes all the buffer
     * locks. this method is typically called when the owning Module is
     * requested to reset.
     */
    public void reset();

    /**
     * Sets the circular buffer object of the connection. This method is called
     * only by the OutputConnector.connectTo() method.<br>
     * <i>This method should not really be part of the API, but it is put here
     * in order to remove implementation dependencies.</i>
     *
     * @param circularBuffer
     *            the circular buffer used by this Connection
     */
    public void setCircularBuffer(Object circularBuffer);

    /**
     * Selects a format for this Connector (the default is null). The
     * <b>setFormat()</b> method is typically called by the Manager as part of
     * the Connector connection method call. The connector should delegate this
     * call to its owning Module.
     */
    public void setFormat(Format format);

    /**
     * sets the Module which registered this Connector.
     *
     */
    public void setModule(Module module);

    /**
     * sets the name of this Connector. Called by the owning Module
     * registerConnector() method
     */
    public void setName(String name);

    /**
     * determines the data transfer protocol used by this connector.<br>
     * <i>Perhaps the only way to change the protocol is in the constructor ?
     * </i>
     *
     * @param protocol
     *            either <i>ProtocolPush, ProtocolSafe</i>
     */
    public void setProtocol(int protocol);

    /**
     * sets the <b>minimum</b> number of buffer objects this Connector should
     * create. The default value should be one buffer object.
     */
    public void setSize(int numOfBufferObjects);
}
