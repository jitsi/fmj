package net.sf.fmj.media;

import java.util.*;

import javax.media.*;

/**
 * <tt>BasicModule</tt> implements a basic FMJ module.
 */
abstract public class BasicModule implements Module, StateTransistor
{
    /**
     * Connectors Registry.
     */
    class Registry extends Hashtable<String,Connector>
    {
        Connector def = null; // the Default connector.

        /**
         * Return the default if null (wildcard) is passed in.
         */
        Object get(String name)
        {
            if (name == null)
                return def;
            return super.get(name);
        }

        /** returns all Connectors in this Registry */
        Connector[] getConnectors()
        {
            Enumeration<Connector> connectorsEnum = elements();
            Connector[] connectorsArray = new Connector[size()];
            for (int i = 0; i < size(); i++)
                connectorsArray[i] = connectorsEnum.nextElement();
            return connectorsArray;
        }

        /** returns the names of all Connectors in this Registry */
        String[] getNames()
        {
            Enumeration<String> namesEnum = keys();
            String[] namesArray = new String[size()];
            for (int i = 0; i < size(); i++)
                namesArray[i] = namesEnum.nextElement();
            return namesArray;
        }

        /**
         * register Connector.
         *
         * @exception RuntimeException
         *                If the specified name is already registered
         */
        @Override
        public Connector put(String name, Connector connector)
        {
            if (containsKey(name))
                throw new RuntimeException("Connector '" + name
                        + "' already exists in Module '"
                        + BasicModule.this.getClass().getName() + "::" + name
                        + "'");
            if (def == null)
                def = connector;
            return super.put(name, connector);
        }

    }

    /**
     * registry of all input connectors created by this module.
     */
    protected Registry inputConnectors = new Registry();

    /**
     * registry of all output connectors created by this module.
     */
    protected Registry outputConnectors = new Registry();
    protected InputConnector[] inputConnectorsArray;

    protected OutputConnector[] outputConnectorsArray;

    protected int protocol = Connector.ProtocolPush;

    /**
     * the instance name of the module, declared in the manager
     */
    protected String name = null;
    protected ModuleListener moduleListener;

    protected BasicController controller; // the Controller that maintains this
                                          // module.
    protected boolean resetted = false;

    protected boolean prefetchFailed = false;

    /**
     * Called when the prefetch() is aborted, i.e. deallocate() was called while
     * prefetching. Release all resources claimed previously by the prefetch
     * call.
     */
    public void abortPrefetch()
    {
    }

    /**
     * Called when the realize() is aborted, i.e. deallocate() was called while
     * realizing. Release all resources claimed previously by the realize()
     * call.
     */
    public void abortRealize()
    {
    }

    /**
     * return if data is available on all inputConnectors and there is room in
     * all outputConnectors.
     */
    public boolean canRun()
    {
        for (int i = 0; i < inputConnectorsArray.length; i++)
            if (!inputConnectorsArray[i].isValidBufferAvailable())
                return false;
        for (int i = 0; i < outputConnectorsArray.length; i++)
            if (!outputConnectorsArray[i].isEmptyBufferAvailable())
                return false;

        return true;
    }

    public void connectorPushed(InputConnector inputConnector)
    {
        process();
    }

    /**
     * This function performs the steps to close a module or Player.
     */
    public void doClose()
    {
    }

    /**
     * This function performs the steps to deallocate a module or Player, and
     * return to the realized state.
     */
    public void doDealloc()
    {
    }

    /**
     * Called when prefetch fails.
     */
    public void doFailedPrefetch()
    {
    }

    /**
     * Called when realize fails.
     */
    public void doFailedRealize()
    {
    }

    /**
     * This function performs the steps to prefetch a module or Player.
     *
     * @return true if successful.
     */
    public boolean doPrefetch()
    {
        // commit connectors to array
        resetted = false;
        return true;
    }

    /**
     * This function performs the steps of realizing a module or a Player.
     *
     * @return true if successful.
     */
    public boolean doRealize()
    {
        return true;
    }

    /**
     * This function notifies the module that the media time has changed.
     */
    public void doSetMediaTime(Time t)
    {
    }

    /**
     * This function notifies the module that the playback rate has changed.
     */
    public float doSetRate(float r)
    {
        return r;
    }

    /**
     * This function performs the steps to start a module or Player.
     */
    public void doStart()
    {
        resetted = false;
    }

    /**
     * This function performs the steps to stop a module or Player, and return
     * to the prefetched state.
     */
    public void doStop()
    {
    }

    protected void error()
    {
        throw new RuntimeException(getClass().getName() + " error");
    }

    public Object getControl(String s)
    {
        return null;
    }

    /**
     * @return the Controller that maintains this module.
     */
    final public BasicController getController()
    {
        return controller;
    }

    /**
     * Return a list of module controls.
     */
    public Object[] getControls()
    {
        return null;
    }

    /**
     * Return the InputConnector given the connector name.
     */
    public InputConnector getInputConnector(String connectorName)
    {
        return (InputConnector) inputConnectors.get(connectorName);
    }

    /**
     * Return an array of strings containing this media module's input port
     * names.
     */
    public String[] getInputConnectorNames()
    {
        return inputConnectors.getNames();
    }

    public long getLatency()
    {
        return ((PlaybackEngine) controller).getLatency();
    }

    /**
     * Return the current time in nanoseconds.
     */
    public long getMediaNanoseconds()
    {
        return controller.getMediaNanoseconds();
    }

    /**
     * Return the current Media time.
     */
    public Time getMediaTime()
    {
        return controller.getMediaTime();
    }

    /**
     * returns the name of this Module in the Player
     */
    final public String getName()
    {
        return name;
    }

    /**
     * Return the OutputConnector given the connector name.
     */
    public OutputConnector getOutputConnector(String connectorName)
    {
        return (OutputConnector) outputConnectors.get(connectorName);

    }

    /**
     * Return an array of strings containing this media module's output port
     * names.
     */
    public String[] getOutputConnectorNames()
    {
        return outputConnectors.getNames();

    }

    /**
     * return the data transfer protocol
     */
    public int getProtocol()
    {
        return protocol;
    }

    /**
     * Return the state of the controller.
     */
    final public int getState()
    {
        return controller.getState();
    }

    /**
     * @return true if the module has been interrupted.
     */
    final public boolean isInterrupted()
    {
        return (controller == null ? false : controller.isInterrupted());
    }

    /**
     * return if this module create threads (so it run on Safe protocol) like
     * Rendering module or not (as a codec module).
     */
    public boolean isThreaded()
    {
        return true;
    }

    public boolean prefetchFailed()
    {
        return prefetchFailed;
    }

    /**
     * function which does the real processing.
     *
     * <pre>
     * if canRun {
     *    for (all inputConnectors)
     *      ic.getValidBuffer()
     *    for (all outputConnectors)
     *      oc.getEmptyBuffer()
     *    <process buffer>
     *    for (all inputConnectors)
     *      ic.readReport()
     *    for (all outputConnectors)
     *      oc.writeReport()
     * }
     * </pre>
     */
    abstract protected void process();

    /**
     * For each of the inputConnectables to this node, it needs to be registered
     * with this function.
     */
    public void registerInputConnector(String name,
            InputConnector inputConnector)
    {
        inputConnectors.put(name, inputConnector);
        inputConnector.setModule(this);
    }

    /**
     * For each of the outputConnectables from this node, it needs to be
     * registered with this function.
     */
    public void registerOutputConnector(String name,
            OutputConnector outputConnector)
    {
        outputConnectors.put(name, outputConnector);
        outputConnector.setModule(this);

    }

    /**
     * reset this module only.
     *
     * <pre>
     * if (state== Started)
     *    throw Exception()
     * for (all connectors)
     *    connector.reset()
     * </pre>
     *
     * The resetted flag is falsified only when the module is later restarted.
     */
    public void reset()
    {
        resetted = true;
    }

    /**
     * Set the Controller that maintains this module.
     */
    final public void setController(BasicController c)
    {
        controller = c;
    }

    /**
     * Selects a format for this Connector (the default is null). The
     * <b>setFormat()</b> method is typically called by the Manager as part of
     * the Connector connection method call. Typically the connector would
     * delegate this call to its owning Module.
     */
    public void setFormat(Connector connector, Format format)
    {
    }

    /**
     * Specify a <tt>ModuleListener</tt> to which this <tt>Module</tt> will send
     * events.
     *
     * @param listener
     *            The listener to which the <tt>Module</tt> will post events.
     */
    public void setModuleListener(ModuleListener listener)
    {
        moduleListener = listener;
    }

    /**
     * sets the name of this Module. Called by the owning Player
     * registerModule() method
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * sets the protocol for all the connectors
     */
    public void setProtocol(int protocol)
    {
        this.protocol = protocol;
        Connector[] connectors = inputConnectors.getConnectors();
        for (int i = 0; i < connectors.length; i++)
            connectors[i].setProtocol(protocol);
        connectors = outputConnectors.getConnectors();
        for (int i = 0; i < connectors.length; i++)
            connectors[i].setProtocol(protocol);

    }

    // ///////////////////////////////////
    // inner classes
    // ///////////////////////////////////

    /**
     * Verify to see if the given buffer has valid data sizes.
     */
    protected boolean verifyBuffer(Buffer buffer)
    {
        if (buffer.isDiscard())
            return true;
        Object data = buffer.getData();
        if (buffer.getLength() < 0)
        {
            System.err.println("warning: data length shouldn't be negative: "
                    + buffer.getLength());
        }
        if (data == null)
        {
            System.err.println("warning: data buffer is null");
            if (buffer.getLength() != 0)
            {
                System.err.println("buffer advertized length = "
                        + buffer.getLength() + " but data buffer is null!");
                return false;
            }
        } else if (data instanceof byte[])
        {
            if (buffer.getLength() > ((byte[]) data).length)
            {
                System.err.println("buffer advertized length = "
                        + buffer.getLength() + " but actual length = "
                        + ((byte[]) data).length);
                return false;
            }
        } else if (data instanceof int[])
        {
            if (buffer.getLength() > ((int[]) data).length)
            {
                System.err.println("buffer advertized length = "
                        + buffer.getLength() + " but actual length = "
                        + ((int[]) data).length);
                return false;
            }
        }
        return true;
    }

}
