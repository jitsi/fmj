package javax.media;

import javax.media.protocol.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/Multiplexer.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface Multiplexer extends PlugIn
{
    public DataSource getDataOutput();

    public Format[] getSupportedInputFormats();

    public ContentDescriptor[] getSupportedOutputContentDescriptors(
            Format[] inputs);

    public int process(Buffer buffer, int trackID);

    public ContentDescriptor setContentDescriptor(
            ContentDescriptor outputContentDescriptor);

    public Format setInputFormat(Format format, int trackID);

    public int setNumTracks(int numTracks);
}
