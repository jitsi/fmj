package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/ByeEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class ByeEvent extends TimeoutEvent
{
    private String reason;

    public ByeEvent(SessionManager from, Participant participant,
            ReceiveStream recvStream, String reason, boolean participantBye)
    {
        super(from, participant, recvStream, participantBye);
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }
}
