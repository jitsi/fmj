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
    public void close()
    {
    }

    public String getName()
    {
        return getClass().getSimpleName(); // override to provide a better name
    }

    public void open() throws ResourceUnavailableException
    {
    }

    public void reset()
    { // TODO
    }
}
