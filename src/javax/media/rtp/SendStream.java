package javax.media.rtp;

import java.io.*;

import javax.media.rtp.rtcp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/SendStream.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface SendStream extends RTPStream
{
    public void close();

    public TransmissionStats getSourceTransmissionStats();

    public int setBitRate(int bitRate);

    public void setSourceDescription(SourceDescription[] sourceDesc);

    public void start() throws IOException;

    public void stop() throws IOException;
}
