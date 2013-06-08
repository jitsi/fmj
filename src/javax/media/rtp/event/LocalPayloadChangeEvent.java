package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/LocalPayloadChangeEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class LocalPayloadChangeEvent extends SendStreamEvent
{
    private int oldpayload; // strange, no getter
    private int newpayload;

    public LocalPayloadChangeEvent(SessionManager from, SendStream sendStream,
            int oldpayload, int newpayload)
    {
        super(from, sendStream, null);
        this.newpayload = newpayload;
        this.oldpayload = oldpayload;
    }

    public int getNewPayload()
    {
        return newpayload;
    }
}
