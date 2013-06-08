package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/RemotePayloadChangeEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class RemotePayloadChangeEvent extends ReceiveStreamEvent
{
    private int oldpayload; // strange, no getter.
    private int newpayload;

    public RemotePayloadChangeEvent(SessionManager from,
            ReceiveStream recvStream, int oldpayload, int newpayload)
    {
        super(from, recvStream, null);
        this.newpayload = newpayload;
        this.oldpayload = oldpayload;
    }

    public int getNewPayload()
    {
        return newpayload;
    }
}
