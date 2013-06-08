package javax.media.protocol;

import java.net.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/URLDataSource.html"
 * target="_blank">this class in the JMF Javadoc</a>. In progress.
 *
 * @author Ken Larson
 *
 */
public class URLDataSource extends net.sf.fmj.media.protocol.URLDataSource
{
    public URLDataSource()
    {
        super();
    }

    public URLDataSource(URL url)
    {
        super(url);
    }

}
