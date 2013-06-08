package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/PacketSizeControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface PacketSizeControl extends Control
{
    public int getPacketSize();

    public int setPacketSize(int numBytes);
}
