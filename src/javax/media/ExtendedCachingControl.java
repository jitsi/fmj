package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/ExtendedCachingControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface ExtendedCachingControl extends CachingControl
{
    public void addDownloadProgressListener(DownloadProgressListener l,
            int numKiloBytes);

    public Time getBufferSize();

    public long getEndOffset();

    public long getStartOffset();

    public void pauseDownload();

    public void removeDownloadProgressListener(DownloadProgressListener l);

    public void resumeDownload();

    public void setBufferSize(Time t);

}
