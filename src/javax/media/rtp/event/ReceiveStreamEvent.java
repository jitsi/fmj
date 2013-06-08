package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/ReceiveStreamEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class ReceiveStreamEvent extends RTPEvent
{
    private ReceiveStream recvStream;
    private Participant participant;

    public ReceiveStreamEvent(SessionManager from, ReceiveStream stream,
            Participant participant)
    {
        super(from);
        this.recvStream = stream;
        this.participant = participant;

    }

    public Participant getParticipant()
    {
        return participant;
    }

    public ReceiveStream getReceiveStream()
    {
        return recvStream;
    }
}
