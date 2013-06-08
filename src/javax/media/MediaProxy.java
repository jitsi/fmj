package javax.media;

import javax.media.protocol.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/MediaProxy.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface MediaProxy extends MediaHandler
{
    public DataSource getDataSource() throws java.io.IOException,
            NoDataSourceException;
}
