package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/NewParticipantEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class NewParticipantEvent extends SessionEvent
{
    private Participant participant;

    public NewParticipantEvent(SessionManager from, Participant participant)
    {
        super(from);
        this.participant = participant;
    }

    public Participant getParticipant()
    {
        return participant;
    }
}
