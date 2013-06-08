package javax.media;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/Codec.html"
 * target="_blank">this class in the JMF Javadoc</a>.
 *
 * Complete.
 *
 * @author Ken Larson
 *
 */
public interface Codec extends PlugIn
{
    public Format[] getSupportedInputFormats();

    public Format[] getSupportedOutputFormats(Format input);

    public int process(Buffer input, Buffer output);

    public Format setInputFormat(Format format);

    public Format setOutputFormat(Format format);
}
