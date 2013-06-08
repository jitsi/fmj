package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/Renderer.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface Renderer extends PlugIn
{
    public Format[] getSupportedInputFormats();

    public int process(Buffer buffer);

    public Format setInputFormat(Format format);

    public void start();

    public void stop();
}
