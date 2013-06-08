package javax.media.rtp;

import java.util.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/Participant.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface Participant
{
    public String getCNAME();

    public Vector getReports();

    public Vector getSourceDescription();

    public Vector getStreams();
}
