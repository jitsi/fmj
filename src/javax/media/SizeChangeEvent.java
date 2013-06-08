package javax.media;

import javax.media.format.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/SizeChangeEvent.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public class SizeChangeEvent extends FormatChangeEvent
{
    protected int height;

    protected float scale;

    protected int width;

    public SizeChangeEvent(Controller from, int width, int height, float scale)
    {
        super(from);
        this.width = width;
        this.height = height;
        this.scale = scale;

    }

    public int getHeight()
    {
        return height;
    }

    public float getScale()
    {
        return scale;
    }

    public int getWidth()
    {
        return width;
    }
}
