package net.sf.fmj.ui.wizards;

import javax.media.protocol.*;

/**
 * Configuration of RTP transmit wizard. Data-only. Suitable for storage
 * externally, like in XML.
 *
 * @author Ken Larson
 *
 */
public class RTPTransmitWizardConfig extends ProcessorWizardConfig
{
    public RTPTransmitWizardConfig()
    {
        contentDescriptor = new ContentDescriptor(ContentDescriptor.RAW_RTP);
    }
}
