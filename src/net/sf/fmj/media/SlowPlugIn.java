package net.sf.fmj.media;

/**
 * An interface to denote a particularly slow plugin.
 */
public interface SlowPlugIn
{
    /**
     * Force to use this plugin even though there may be another better
     * alternative.
     */
    public void forceToUse();

}
