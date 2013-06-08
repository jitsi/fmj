package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/NewReceiveStreamEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class NewReceiveStreamEvent extends ReceiveStreamEvent
{
    public NewReceiveStreamEvent(SessionManager from, ReceiveStream recvStream)
    {
        super(from, recvStream, null);
    }
}
