/*
 * TranscodeWizard.java
 *
 * Created on June 20, 2007, 12:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.fmj.ui.wizards;

import java.awt.*;

import javax.media.protocol.*;

import net.sf.fmj.ui.wizard.*;

/**
 *
 * @author Ken Larson
 */
public class TranscodeWizard extends Wizard
{
    private static class TranscodeContentDescriptorFilter implements
            ContentDescriptorFilter
    {
        public boolean isCompatible(ContentDescriptor d)
        {
            if (d instanceof FileTypeDescriptor)
                return true;

            return false;
        }

    }

    private final TranscodeWizardResult result = new TranscodeWizardResult();

    private final TranscodeWizardConfig config;

    public TranscodeWizard(Frame owner, TranscodeWizardConfig config)
    {
        super(owner);

        if (config != null)
            this.config = config;
        else
            this.config = new TranscodeWizardConfig();

        getDialog().setTitle("Transcode Wizard");

        ChooseSourcePanelDescriptor descriptor1 = new ChooseSourcePanelDescriptor(
                config, result);
        registerWizardPanel(ChooseSourcePanelDescriptor.IDENTIFIER, descriptor1);

        ContentAndTrackFormatPanelDescriptor descriptor2 = new ContentAndTrackFormatPanelDescriptor(
                FileDestPanelDescriptor.IDENTIFIER,
                new TranscodeContentDescriptorFilter(), config, result);
        registerWizardPanel(ContentAndTrackFormatPanelDescriptor.IDENTIFIER,
                descriptor2);

        // AudioFormat f = new AudioFormat(AudioFormat.ULAW_RTP, 8000.0, 8, 1,
        // AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        // ((ContentAndTrackFormatPanel)
        // descriptor2.getPanelComponent()).addTrack(f);

        FileDestPanelDescriptor descriptor3 = new FileDestPanelDescriptor(
                config, result);
        registerWizardPanel(FileDestPanelDescriptor.IDENTIFIER, descriptor3);

        setCurrentPanel(ChooseSourcePanelDescriptor.IDENTIFIER);

    }

    public TranscodeWizardConfig getConfig()
    {
        return config;
    }

    public TranscodeWizardResult getResult()
    {
        return result;
    }

    public boolean run()
    {
        int ret = showModalDialog();

        // System.out.println("Dialog return code is (0=Finish,1=Cancel,2=Error): "
        // + ret);
        // System.out.println("Second panel selection is: " +
        // (((ContentAndTrackFormatPanel)descriptor2.getPanelComponent()).getRadioButtonSelected()));
        //
        return ret == 0;
    }
}
