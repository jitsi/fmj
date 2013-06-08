package javax.media.rtp;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/GlobalReceptionStats.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface GlobalReceptionStats
{
    public int getBadRTCPPkts();

    public int getBadRTPkts();

    public int getBytesRecd();

    public int getLocalColls();

    public int getMalformedBye();

    public int getMalformedRR();

    public int getMalformedSDES();

    public int getMalformedSR();

    public int getPacketsLooped();

    public int getPacketsRecd();

    public int getRemoteColls();

    public int getRTCPRecd();

    public int getSRRecd();

    public int getTransmitFailed();

    public int getUnknownTypes();
}
