package javax.media.rtp;

import javax.media.protocol.*;
import javax.media.rtp.rtcp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/RTPStream.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface RTPStream
{
    public DataSource getDataSource();

    public Participant getParticipant();

    public SenderReport getSenderReport();

    public long getSSRC();

}
