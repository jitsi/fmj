package javax.media;

import javax.media.control.*;
import javax.media.protocol.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/Processor.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface Processor extends Player
{
    public static final int Configuring = 140;
    public static final int Configured = 180;

    public void configure();

    public ContentDescriptor getContentDescriptor() throws NotConfiguredError;

    public DataSource getDataOutput() throws NotRealizedError;

    public ContentDescriptor[] getSupportedContentDescriptors()
            throws NotConfiguredError;

    public TrackControl[] getTrackControls() throws NotConfiguredError;

    public ContentDescriptor setContentDescriptor(
            ContentDescriptor outputContentDescriptor)
            throws NotConfiguredError;
}
