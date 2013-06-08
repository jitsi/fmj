package javax.media.protocol;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/DataSource.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding Complete.
 *
 * @author Ken Larson
 *
 */
public abstract class DataSource implements Controls, Duration
{
    private MediaLocator locator;

    public DataSource()
    {
        super();
    }

    public DataSource(MediaLocator source)
    {
        this.locator = source;
    }

    public abstract void connect() throws java.io.IOException;

    public abstract void disconnect();

    public abstract String getContentType();

    public abstract Object getControl(String controlType);

    public abstract Object[] getControls();

    public abstract Time getDuration();

    public MediaLocator getLocator()
    {
        return locator;
    }

    protected void initCheck()
    {
        if (locator == null)
            throw new Error("Uninitialized DataSource error."); // JavaDoc
                                                                // claims this
                                                                // should be
                                                                // UninitializedError(),
                                                                // but this is
                                                                // not the
                                                                // case.;

    }

    public void setLocator(MediaLocator source)
    {
        this.locator = source;
    }

    public abstract void start() throws java.io.IOException;

    public abstract void stop() throws java.io.IOException;

}
