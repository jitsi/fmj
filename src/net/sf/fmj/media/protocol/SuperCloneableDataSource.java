package net.sf.fmj.media.protocol;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.protocol.*;

/**
 * This is a utility class that creates clones of a DataSource. The
 * CloneableDataSource is itself a DataSource that reflects the functionality
 * and type of the input DataSource. The input should be of one of the following
 * types: PullDataSource, PullBufferDataSource, PushDataSource or
 * PushBufferDataSource. The resulting CloneableDataSource will be of the same
 * type. To create a clone of this data source, call the <tt>getClone()</tt>
 * method on this object. Any clone created from this DataSource will by Default
 * be a PushDataSource or PushBufferDataSource.
 * <p>
 * The cloned DataSource shares the properties (duration, content type, etc.) of
 * the original DataSource.
 * <p>
 * Calling <tt>connect</tt>, <tt>disconnect</tt>, <tt>start</tt>, <tt>stop</tt>
 * on the CloneableDataSource (master) will propagate the same calls to the
 * cloned (slave) DataSources.
 *
 * This is a class used by the CloneablePullDataSource, CloneablePushDataSource,
 * CloneablePullBufferDataSource, CloneablePushDataSource and shouldn't be used
 * explicitly by developers.
 *
 * @see javax.media.protocol.DataSource
 */
class SuperCloneableDataSource extends DataSource
{
    class PushBufferDataSourceSlave extends PushBufferDataSource
    {
        PushBufferStream[] streams = null;

        public PushBufferDataSourceSlave()
        {
            streams = new PushBufferStream[streamsAdapters.length];
            for (int i = 0; i < streams.length; i++)
                streams[i] = (PushBufferStream) streamsAdapters[i]
                        .createSlave();
        }

        @Override
        public void connect() throws IOException
        {
            for (int i = 0; i < streams.length; i++)
            {
                ((SourceStreamSlave) streams[i]).connect();
            }
        }

        @Override
        public void disconnect()
        {
            for (int i = 0; i < streams.length; i++)
            {
                ((SourceStreamSlave) streams[i]).disconnect();
            }
        }

        @Override
        public String getContentType()
        {
            return input.getContentType();
        }

        @Override
        public Object getControl(String controlType)
        {
            // should we duplicate the control?
            return input.getControl(controlType);
        }

        @Override
        public Object[] getControls()
        {
            // should we duplicate the controls?
            return input.getControls();
        }

        @Override
        public Time getDuration()
        {
            return input.getDuration();
        }

        @Override
        public PushBufferStream[] getStreams()
        {
            return streams;
        }

        @Override
        public void start() throws IOException
        {
            // DO NOTHING SINCE THIS IS A CLONE
        }

        @Override
        public void stop() throws IOException
        {
            // DO NOTHING SINCE THIS IS A CLONE
        }
    }

    class PushDataSourceSlave extends PushDataSource
    {
        PushSourceStream[] streams = null;

        public PushDataSourceSlave()
        {
            streams = new PushSourceStream[streamsAdapters.length];
            for (int i = 0; i < streams.length; i++)
                streams[i] = (PushSourceStream) streamsAdapters[i]
                        .createSlave();
        }

        @Override
        public void connect() throws IOException
        {
            for (int i = 0; i < streams.length; i++)
            {
                ((SourceStreamSlave) streams[i]).connect();
            }
        }

        @Override
        public void disconnect()
        {
            for (int i = 0; i < streams.length; i++)
            {
                ((SourceStreamSlave) streams[i]).disconnect();
            }
        }

        @Override
        public String getContentType()
        {
            return input.getContentType();
        }

        @Override
        public Object getControl(String controlType)
        {
            // should we duplicate the control?
            return input.getControl(controlType);
        }

        @Override
        public Object[] getControls()
        {
            // should we duplicate the controls?
            return input.getControls();
        }

        @Override
        public Time getDuration()
        {
            return input.getDuration();
        }

        @Override
        public PushSourceStream[] getStreams()
        {
            return streams;
        }

        @Override
        public void start() throws IOException
        {
            // DO NOTHING SINCE THIS IS A CLONE
        }

        @Override
        public void stop() throws IOException
        {
            // DO NOTHING SINCE THIS IS A CLONE
        }
    }

    /**
     * The DataSource to be cloned.
     */
    protected DataSource input;

    /**
     * An array of adapters where each adapter correspond to a stream in the
     * DataSource we are cloning.
     */
    public CloneableSourceStreamAdapter[] streamsAdapters;

    /*
     * The streams that will be returned by this DataSource and will be used by
     * the connected Handler.
     */
    public SourceStream[] streams = null;

    /**
     * The cloned DataSources.
     */
    private Vector clones = new Vector();

    /**
     * Constructor that takes a DataSource object for cloning.
     *
     * @param input
     *            the DataSource for cloning.
     */
    SuperCloneableDataSource(DataSource input)
    {
        this.input = input;
        SourceStream[] originalStreams = null;

        if (input instanceof PullDataSource)
            originalStreams = ((PullDataSource) input).getStreams();
        if (input instanceof PushDataSource)
            originalStreams = ((PushDataSource) input).getStreams();
        if (input instanceof PullBufferDataSource)
            originalStreams = ((PullBufferDataSource) input).getStreams();
        if (input instanceof PushBufferDataSource)
            originalStreams = ((PushBufferDataSource) input).getStreams();
        streamsAdapters = new CloneableSourceStreamAdapter[originalStreams.length];
        // create a cloneable adapter for each stream
        for (int i = 0; i < originalStreams.length; i++)
            streamsAdapters[i] = new CloneableSourceStreamAdapter(
                    originalStreams[i]);
    }

    /**
     * Open a connection to the source described by the <tt>MediaLocator</tt>.
     * <p>
     *
     * The <tt>connect</tt> method initiates communication with the source.
     *
     * @exception IOException
     *                Thrown if there are IO problems when <tt>connect</tt> is
     *                called.
     */
    @Override
    public void connect() throws IOException
    {
        input.connect();
    }

    /**
     * Clone the original datasource, returning an object of the type
     * <tt>PushDataSource</tt> or <tt>PushBufferDataSource</tt>. If the original
     * data source was a PullDataSource, then this will be a PushDataSource
     * which pushes at the same rate at which the CloneableDataSource is being
     * pulled.
     *
     * @return a slave DataSource for this DataSource.
     */
    javax.media.protocol.DataSource createClone()
    {
        DataSource newSlave;

        if ((input instanceof PullDataSource)
                || (input instanceof PushDataSource))
            newSlave = new PushDataSourceSlave();
        else
            // input is a Buffer type DataSource
            newSlave = new PushBufferDataSourceSlave();

        clones.addElement(newSlave);

        try
        {
            newSlave.connect();
        } catch (IOException e)
        {
            return null;
        }

        return newSlave;
    }

    /**
     * Close the connection to the source described by the locator.
     * <p>
     * The <tt>disconnect</tt> method frees resources used to maintain a
     * connection to the source. If no resources are in use, <tt>disconnect</tt>
     * is ignored. If <tt>stop</tt> hasn't already been called, calling
     * <tt>disconnect</tt> implies a stop.
     *
     */
    @Override
    public void disconnect()
    {
        input.disconnect();
    }

    /**
     * Get a string that describes the content-type of the media that the source
     * is providing.
     * <p>
     * It is an error to call <tt>getContentType</tt> if the source is not
     * connected.
     *
     * @return The name that describes the media content.
     */
    @Override
    public String getContentType()
    {
        return input.getContentType();
    }

    /**
     * Obtain the object that implements the specified <tt>Class</tt> or
     * <tt>Interface</tt> The full class or interface name must be used.
     * <p>
     *
     * If the control is not supported then <tt>null</tt> is returned.
     *
     * @return the object that implements the control, or <tt>null</tt>.
     */
    @Override
    public Object getControl(String controlType)
    {
        return input.getControl(controlType);
    }

    /**
     * Obtain the collection of objects that control the object that implements
     * this interface.
     * <p>
     *
     * If no controls are supported, a zero length array is returned.
     *
     * @return the collection of object controls
     */
    @Override
    public Object[] getControls()
    {
        return input.getControls();
    }

    /**
     * Get the duration of the media represented by this object. The value
     * returned is the media's duration when played at the default rate. If the
     * duration can't be determined (for example, the media object is presenting
     * live video) <tt>getDuration</tt> returns <tt>DURATION_UNKNOWN</tt>.
     *
     * @return A <tt>Time</tt> object representing the duration or
     *         DURATION_UNKNOWN.
     */
    @Override
    public Time getDuration()
    {
        return input.getDuration();
    }

    /**
     * Initiate data-transfer. The <tt>start</tt> method must be called before
     * data is available. (You must call <tt>connect</tt> before calling
     * <tt>start</tt>.)
     *
     * @exception IOException
     *                Thrown if there are IO problems with the source when
     *                <tt>start</tt> is called.
     */
    @Override
    public void start() throws IOException
    {
        input.start();
    }

    /**
     * Stop the data-transfer. If the source has not been connected and started,
     * <tt>stop</tt> does nothing.
     */
    @Override
    public void stop() throws IOException
    {
        input.stop();
    }
}
