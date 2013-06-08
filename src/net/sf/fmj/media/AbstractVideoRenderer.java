package net.sf.fmj.media;

import java.awt.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.renderer.*;

/**
 * Abstract implementation of VideoRenderer, useful for subclassing.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractVideoRenderer extends AbstractRenderer implements
        VideoRenderer, FrameGrabbingControl
{
    private Rectangle bounds = null;

    private Buffer lastBuffer;

    protected abstract int doProcess(Buffer buffer);

    public Rectangle getBounds()
    {
        return bounds;
    }

    public abstract Component getComponent();

    public Component getControlComponent()
    {
        return null;
    }

    public Buffer grabFrame()
    {
        return lastBuffer;
    }

    @Override
    public final int process(Buffer buffer)
    {
        lastBuffer = buffer; // TODO: clone?
        return doProcess(buffer);
    }

    public void setBounds(Rectangle rect)
    {
        this.bounds = rect;
    }

    public boolean setComponent(Component comp)
    {
        // default implementation does not allow changing of component.
        return false;
    }

}
