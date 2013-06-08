package javax.media;

import javax.media.datasink.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/DataSink.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface DataSink extends MediaHandler, Controls
{
    public void addDataSinkListener(DataSinkListener listener);

    public void close();

    public String getContentType();

    public MediaLocator getOutputLocator();

    public void open() throws java.io.IOException, SecurityException;

    public void removeDataSinkListener(DataSinkListener listener);

    public void setOutputLocator(MediaLocator output);

    public void start() throws java.io.IOException;

    public void stop() throws java.io.IOException;
}
