package javax.media.datasink;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/datasink/EndOfStreamEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class EndOfStreamEvent extends DataSinkEvent
{
    public EndOfStreamEvent(DataSink from)
    {
        super(from);
    }

    public EndOfStreamEvent(DataSink from, String reason)
    {
        super(from, reason);
    }

}
