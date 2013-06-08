package net.sf.fmj.ui.wizards;

import java.awt.*;

import javax.media.protocol.*;

import net.sf.fmj.ui.wizard.*;

/**
 *
 * @author Ken Larson
 *
 */
public class RTPTransmitWizard extends Wizard
{
    private static class RTPContentDescriptorFilter implements
            ContentDescriptorFilter
    {
        public boolean isCompatible(ContentDescriptor d)
        {
            // if (d.equals(new ContentDescriptor(ContentDescriptor.RAW))) //
            // TODO: we want RAW_RTP only
            // return true;
            if (d.equals(new ContentDescriptor(ContentDescriptor.RAW_RTP)))
                return true;

            return false;
        }

    }

    private final RTPTransmitWizardResult result = new RTPTransmitWizardResult();

    private final RTPTransmitWizardConfig config;

    public RTPTransmitWizard(Frame owner, RTPTransmitWizardConfig config)
    {
        super(owner);

        if (config != null)
            this.config = config;
        else
            this.config = new RTPTransmitWizardConfig();

        getDialog().setTitle("RTP Transmit Wizard");

        final ChooseSourcePanelDescriptor descriptor1 = new ChooseSourcePanelDescriptor(
                config, result);
        registerWizardPanel(ChooseSourcePanelDescriptor.IDENTIFIER, descriptor1);

        final ContentAndTrackFormatPanelDescriptor descriptor2 = new ContentAndTrackFormatPanelDescriptor(
                RTPDestPanelDescriptor.IDENTIFIER,
                new RTPContentDescriptorFilter(), config, result);
        registerWizardPanel(ContentAndTrackFormatPanelDescriptor.IDENTIFIER,
                descriptor2);

        final RTPDestPanelDescriptor descriptor3 = new RTPDestPanelDescriptor(
                config, result);
        registerWizardPanel(RTPDestPanelDescriptor.IDENTIFIER, descriptor3);

        setCurrentPanel(ChooseSourcePanelDescriptor.IDENTIFIER);

    }

    public RTPTransmitWizardConfig getConfig()
    {
        return config;
    }

    public RTPTransmitWizardResult getResult()
    {
        return result;
    }

    public boolean run()
    {
        final int ret = showModalDialog();
        // System.out.println("Dialog return code is (0=Finish,1=Cancel,2=Error): "
        // + ret);
        return ret == 0;
    }
}
