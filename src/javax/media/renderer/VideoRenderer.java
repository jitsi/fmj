package javax.media.renderer;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/renderer/VideoRenderer.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface VideoRenderer extends Renderer
{
    public java.awt.Rectangle getBounds();

    public java.awt.Component getComponent();

    public void setBounds(java.awt.Rectangle rect);

    public boolean setComponent(java.awt.Component comp);
}
