package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/SendStreamEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class SendStreamEvent extends RTPEvent
{
    private SendStream sendStream;
    private Participant participant;

    public SendStreamEvent(SessionManager from, SendStream stream,
            Participant participant)
    {
        super(from);
        this.sendStream = stream;
        this.participant = participant;

    }

    public Participant getParticipant()
    {
        return participant;
    }

    public SendStream getSendStream()
    {
        return sendStream;
    }
}
