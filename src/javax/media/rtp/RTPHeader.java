package javax.media.rtp;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/RTPHeader.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 *
 */
public class RTPHeader implements java.io.Serializable
{
    public static final int VALUE_NOT_SET = -1;

    private boolean extensionPresent;
    private int extensionType = VALUE_NOT_SET;
    private byte[] extension;

    public RTPHeader()
    {
        super();
    }

    public RTPHeader(boolean extensionPresent, int extensionType,
            byte[] extension)
    {
        this.extensionPresent = extensionPresent;
        this.extensionType = extensionType;
        this.extension = extension;
    }

    public RTPHeader(int marker)
    { // TODO: none of the properties seem to be affected by this.
    }

    public byte[] getExtension()
    {
        return extension;
    }

    public int getExtensionType()
    {
        return extensionType;
    }

    public boolean isExtensionPresent()
    {
        return extensionPresent;
    }

    public void setExtension(byte[] e)
    {
        this.extension = e;
    }

    public void setExtensionPresent(boolean p)
    {
        this.extensionPresent = p;
    }

    public void setExtensionType(int t)
    {
        this.extensionType = t;
    }
}
