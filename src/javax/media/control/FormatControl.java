package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/FormatControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface FormatControl extends javax.media.Control
{
    public Format getFormat();

    public Format[] getSupportedFormats();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public Format setFormat(Format format);
}
