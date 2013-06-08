package javax.media.protocol;

import javax.media.*;
import javax.media.control.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/CaptureDevice.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface CaptureDevice
{
    public void connect() throws java.io.IOException;

    public void disconnect();

    public CaptureDeviceInfo getCaptureDeviceInfo();

    public FormatControl[] getFormatControls();

    public void start() throws java.io.IOException;

    public void stop() throws java.io.IOException;
}
