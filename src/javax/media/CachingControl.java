package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/CachingControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface CachingControl extends Control
{
    public static final long LENGTH_UNKNOWN = Long.MAX_VALUE;

    public long getContentLength();

    public long getContentProgress();

    public java.awt.Component getControlComponent();

    public java.awt.Component getProgressBarComponent();

    public boolean isDownloading();

}
