package javax.media.protocol;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/Positionable.html"
 * target="_blank">this class in the JMF Javadoc</a>.
 *
 * Complete.
 *
 * @author Ken Larson
 *
 */
public interface Positionable
{
    public static final int RoundUp = 1;

    public static final int RoundDown = 2;

    public static final int RoundNearest = 3;

    public boolean isRandomAccess();

    public Time setPosition(Time where, int rounding);
}
