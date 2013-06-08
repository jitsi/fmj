package net.sf.fmj.media.protocol;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

/**
 * This is a utility class for creating clones of PullDataSource. THe class
 * reflects the functionality of a PullDataSource and provides a getClone()
 * method for generating clones. The generated clone will be of type
 * PushDataSource and its streams will generate a trasferData() call each time
 * the PullDataSource's streams are read. This class also implements the
 * CaptureDevice interface.
 */
public class CloneableCaptureDevicePullDataSource extends PullDataSource
        implements SourceCloneable, CaptureDevice
{
    private SuperCloneableDataSource superClass;

    /**
     * Constructor
     *
     * @param source
     *            the source to be cloned
     */
    public CloneableCaptureDevicePullDataSource(PullDataSource source)
    {
        superClass = new SuperCloneableDataSource(source);
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
        superClass.connect();
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
    public DataSource createClone()
    {
        return superClass.createClone();
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
        superClass.disconnect();
    }

    /**
     * Return the <tt>CaptureDeviceInfo</tt> object that describes this device.
     *
     * @return The <tt>CaptureDeviceInfo</tt> object that describes this device.
     */
    public javax.media.CaptureDeviceInfo getCaptureDeviceInfo()
    {
        return ((CaptureDevice) superClass.input).getCaptureDeviceInfo();
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
        return superClass.getContentType();
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
        return superClass.getControl(controlType);
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
        return superClass.getControls();
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
        return superClass.getDuration();
    }

    /**
     * Returns an array of <tt>FormatControl</tt> objects. Each of them can be
     * used to set and get the format of each capture stream. This method can be
     * used before connect to set and get the capture formats.
     *
     * @return an array for FormatControls.
     */
    public javax.media.control.FormatControl[] getFormatControls()
    {
        return ((CaptureDevice) superClass.input).getFormatControls();
    }

    /**
     * Get the collection of streams that this source manages. The collection of
     * streams is entirely content dependent. The <tt>ContentDescriptor</tt> of
     * this <tt>DataSource</tt> provides the only indication of what streams can
     * be available on this connection.
     *
     * @return The collection of streams for this source.
     */
    @Override
    public PullSourceStream[] getStreams()
    {
        if (superClass.streams == null)
        {
            superClass.streams = new PullSourceStream[superClass.streamsAdapters.length];
            for (int i = 0; i < superClass.streamsAdapters.length; i++)
                superClass.streams[i] = superClass.streamsAdapters[i]
                        .getAdapter();
        }

        return (PullSourceStream[]) superClass.streams;
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
        superClass.start();
    }

    /**
     * Stop the data-transfer. If the source has not been connected and started,
     * <tt>stop</tt> does nothing.
     */
    @Override
    public void stop() throws IOException
    {
        superClass.stop();
    }
}
