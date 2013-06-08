package net.sf.fmj.ui.wizards;

import javax.media.protocol.*;

/**
 * Used to filter which content descriptions are offered for which type of
 * processing (RTP transmission, transcoding, etc).
 *
 * @author Ken Larson
 *
 */
public interface ContentDescriptorFilter
{
    public boolean isCompatible(ContentDescriptor d);

}
