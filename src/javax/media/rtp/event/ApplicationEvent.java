package javax.media.rtp.event;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/event/ApplicationEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class ApplicationEvent extends ReceiveStreamEvent
{
    private int appSubtype;

    private String appString;

    private byte[] appData;

    public ApplicationEvent(SessionManager from, Participant participant,
            ReceiveStream recvStream, int appSubtype, String appString,
            byte[] appData)
    {
        super(from, recvStream, participant);
        this.appSubtype = appSubtype;
        this.appString = appString;
        this.appData = appData;
    }

    public byte[] getAppData()
    {
        return appData;
    }

    public String getAppString()
    {
        return appString;
    }

    public int getAppSubType()
    {
        return appSubtype;
    }

}
