package net.sf.fmj.media;

import javax.media.*;

/**
 * A Module is a "black box" which does a part of the overall processing. <br>
 * It might have InputConnectors from which it receives data. It might have
 * OutputConnectors to which it transmits data. its properties are exposed via
 * Java Beans compliant accessing methods (setXXX()/getXXX) <br>
 * <i> getLatency(.) removed since it was not used</i> <center><h2>data transfer
 * protocols</center></h2> We define four protocols of data transfer between
 * Modules:
 * <ul>
 *
 * <li><b>Push - Output Data Driven</b> the upstream Module runs in its own
 * thread. The Module loops on its "process" method and when it finishes one
 * frame it calls the writeReport() method it triggers a call in the downstream
 * Module to its "process" method which is executed in the current thread. When
 * The downstream Module finishes, it call its downstream Module, recursively.
 * This is the natural protocol for capture.
 *
 * <br>
 * Here is a sample call graph for a push protocol (Source is pushing data to
 * the Drain).<br>
 *
 * <pre>
 * Source.process calls MediaOutputConnector.getEmptyBuffer()
 * Source.process puts  buffer in the empty buffer container
 * Source.process calls MediaOutputConnector.writeReport(true)
 *        MediaOutputConnector.writeReport calls Drain.connectorPushed.
 *               Drain.connectorPushed calls Drain.process
 *                     Drain.process calls MediaInputConnector.getValidBuffer
 *                     Drain.process proccesses the Buffer
 *                     Drain.process calls MediaInputConnector.readReport
 * </pre>
 *
 * <li><b>Safe - Buffer Driven</b> both the downstream Module and the upstream
 * Module run in separate threads. Both the Modules loops on their "process"
 * method and a call to either readReport() in the downstream Module or
 * writeReport() in the upstream Module wakes the other thread, if it is not
 * running. When one of the threads is blocked, it waits until it is wakened by
 * the other thread. This is the natural protocol for multiple threads.</li>
 * </ul>
 *
 * @see Control
 * @see Connector
 * @see InputConnector
 * @see OutputConnector
 */
public interface Module extends javax.media.Controls
{
    /**
     * A callback function denoting data was written to one of this Module input
     * Connectors. This function is needed in case of <b>Push</b> protocol
     * Typical reaction of the module is to process one frame in the calling
     * thread and return.
     *
     * @param inputConnector
     *            the inputConnector of the connection which have received data.
     */
    public void connectorPushed(InputConnector inputConnector);

    /**
     * Return the specified input connector. Connectors and their names are
     * typically constructed in the Module's constructor, but their construction
     * can be delayed until the Player <b>Realizing</b> state. Returns null if
     * the string doesn't match any of the Module's InputConnectors.
     *
     * @param connectorName
     *            the name of the connector.
     * @return InputConnector associated with this name.
     */
    public InputConnector getInputConnector(String connectorName);

    /**
     * Return an array of strings containing this Module's input connectors
     * names (both connected and unconnected). If this Module contains no inputs
     * an array of length zero is returned.
     *
     * @return list of input connectors as strings.
     */
    public String[] getInputConnectorNames();

    /**
     * returns the name of this Module in the Player
     */
    public String getName();

    /**
     * Return the specified output connector. Connectors and their names are
     * typically constructed in the Module's constructor. Returns null if the
     * String doesn't match any of the Module's OutputConnectors.
     *
     * @param connectorName
     *            the name of the connector
     * @return OutputConnector associated with this name.
     */
    public OutputConnector getOutputConnector(String connectorName);

    /**
     * Return an array of strings containing this Module's output connectors
     * names (both connected and unconnected). If this Module contains no
     * outputs an array of length zero is returned.
     *
     * @return list of output connectors as strings.
     */
    public String[] getOutputConnectorNames();

    /**
     * Query to see if the module has just been interrupted.
     *
     * @return true if the module has just been interrupted.
     */
    public boolean isInterrupted();

    /**
     * Each of the inputConnectors of this Module has to be registered with this
     * function. This method also sets the Module reference of the Connector
     */
    void registerInputConnector(String name, InputConnector in);

    /**
     * Each of the outputConnectors of this Module has to be registered with
     * this function. This method also sets the Module reference of the
     * Connector
     */
    void registerOutputConnector(String name, OutputConnector out);

    /**
     * Return the Module to its initial state. <br>
     * A Module should call its Connectors reset method, and then clear its
     * internal buffers typically by calling the StateTransistor's method
     * dealloc().
     */
    public void reset();

    /**
     * Selects a format for the Connector (the default is null). The
     * <b>setFormat()</b> method is typically called by the Manager as part of
     * the Connector connection method call. Typically the connector would
     * delegate this call to its owning Module.
     */
    public void setFormat(Connector connector, Format format);

    /**
     * Specify a <tt>ModuleListener</tt> to which this <tt>Module</tt> will send
     * events.
     *
     * @param listener
     *            The listener to which the <tt>Module</tt> will post events.
     */
    public void setModuleListener(ModuleListener listener);

    /**
     * sets the name of this Module. Called by the owning Player
     * registerModule() method
     */
    public void setName(String name);
}
