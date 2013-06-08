package javax.media.rtp.rtcp;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/rtcp/Feedback.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface Feedback
{
    public long getDLSR();

    public int getFractionLost();

    public long getJitter();

    public long getLSR();

    public long getNumLost();

    public long getSSRC();

    public long getXtndSeqNum();

}
