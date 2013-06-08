package javax.media.rtp;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/EncryptionInfo.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class EncryptionInfo implements java.io.Serializable
{
    private int type;

    private byte[] key;

    public static final int NO_ENCRYPTION = 0;

    public static final int XOR = 1;

    public static final int MD5 = 2;

    public static final int DES = 3;

    public static final int TRIPLE_DES = 4;

    public EncryptionInfo(int type, byte[] key)
    {
        this.type = type;
        this.key = key;
    }

    public byte[] getKey()
    {
        return key;
    }

    public int getType()
    {
        return type;
    }
}
