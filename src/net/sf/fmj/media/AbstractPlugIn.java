package net.sf.fmj.media;

import javax.media.*;

/**
 * Abstract implementation of PlugIn, useful for subclassing.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractPlugIn extends AbstractControls implements PlugIn
{
    private boolean opened = false;

    public void close()
    {
        opened = false;
    }

    public String getName()
    {
        return getClass().getSimpleName(); // override to provide a better name
    }

    public void open() throws ResourceUnavailableException
    {
        opened = true;
    }

    public void reset()
    { // TODO
    }

}
