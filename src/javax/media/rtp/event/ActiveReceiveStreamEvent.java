package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/ActiveReceiveStreamEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 */
public class ActiveReceiveStreamEvent extends ReceiveStreamEvent
{
    public ActiveReceiveStreamEvent(SessionManager from,
            Participant participant, ReceiveStream stream)
    {
        super(from, stream, participant);
    }
}
