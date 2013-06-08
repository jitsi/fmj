package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/LocalCollisionEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class LocalCollisionEvent extends SessionEvent
{
    private ReceiveStream recvStream;
    private long newSSRC;

    public LocalCollisionEvent(SessionManager from, ReceiveStream recvStream,
            long newSSRC)
    {
        super(from);
        this.recvStream = recvStream;
        this.newSSRC = newSSRC;
    }

    public long getNewSSRC()
    {
        return newSSRC;
    }

    public ReceiveStream getReceiveStream()
    {
        return recvStream;
    }
}
