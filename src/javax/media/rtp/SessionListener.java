package javax.media.rtp;

import javax.media.rtp.event.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/SessionListener.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface SessionListener extends java.util.EventListener
{
    public void update(SessionEvent event);
}
