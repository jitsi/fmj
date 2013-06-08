package javax.media.rtp.rtcp;

import java.util.*;

import javax.media.rtp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/rtcp/Report.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface Report
{
    public Vector getFeedbackReports();

    public Participant getParticipant();

    public Vector getSourceDescription();

    public long getSSRC();
}
