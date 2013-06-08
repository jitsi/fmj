package net.sf.fmj.ui.wizards;

import javax.media.*;

/**
 *
 * @author Ken Larson
 *
 */
public class TrackConfig
{
    public boolean enabled;
    public Format format;

    public TrackConfig()
    {
        this(false, null);
    }

    public TrackConfig(boolean enabled, Format format)
    {
        super();
        this.enabled = enabled;
        this.format = format;
    }
}
