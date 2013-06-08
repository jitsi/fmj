package javax.media.format;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/format/FormatChangeEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class FormatChangeEvent extends ControllerEvent
{
    protected Format oldFormat;
    protected Format newFormat;

    public FormatChangeEvent(Controller source)
    {
        super(source);
    }

    public FormatChangeEvent(Controller source, Format oldFormat,
            Format newFormat)
    {
        super(source);
        this.oldFormat = oldFormat;
        this.newFormat = newFormat;
    }

    public Format getNewFormat()
    {
        return newFormat;
    }

    public Format getOldFormat()
    {
        return oldFormat;
    }
}
