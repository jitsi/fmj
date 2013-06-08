package javax.media.util;

import javax.media.format.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/util/BufferToImage.html"
 * target="_blank">this class in the JMF Javadoc</a>. In progress. We implement
 * in FMJ and extend, because if JMF is (ahead) in the classpath, its
 * BufferToImage would be used even for FMJ classes like SimpleAWTRenderer. It
 * fails on 4harmonic.mpg.
 *
 * @author Ken Larson
 *
 */
public class BufferToImage extends net.sf.fmj.media.util.BufferToImage
{
    public BufferToImage(VideoFormat format)
    {
        super(format);
    }

}
