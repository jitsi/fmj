package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/TimeoutEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class TimeoutEvent extends ReceiveStreamEvent
{
    private boolean participantBye;

    public TimeoutEvent(SessionManager from, Participant participant,
            ReceiveStream recvStream, boolean participantBye)
    {
        super(from, recvStream, participant);
        this.participantBye = participantBye;
    }

    public boolean participantLeaving()
    {
        return participantBye;
    }
}
