package net.sf.fmj.ui.wizards;

import net.sf.fmj.ui.wizard.*;

/**
 *
 * @author Ken Larson
 *
 */
public class ChooseSourcePanelDescriptor extends WizardPanelDescriptor
{
    public static final String IDENTIFIER = ChooseSourcePanelDescriptor.class
            .getName();

    private final ProcessorWizardConfig config;
    private final ProcessorWizardResult result;

    public ChooseSourcePanelDescriptor(ProcessorWizardConfig config,
            ProcessorWizardResult result)
    {
        super(IDENTIFIER, new ChooseSourcePanel());
        this.config = config;
        this.result = result;
    }

    @Override
    public boolean aboutToDisplayPanel(Object prevId)
    {
        if (prevId == getBackPanelDescriptor())
        {
            if (config.url != null)
                getChooseSourcePanel().getTextFieldURL().setText(config.url);
            // TODO: hard-coded, get from prefs
            return true;
        }
        return super.aboutToDisplayPanel(prevId);
    }

    @Override
    public boolean aboutToHidePanel(Object idOfNext)
    {
        if (idOfNext == getNextPanelDescriptor())
        { // forward transition

            config.url = getChooseSourcePanel().getTextFieldURL().getText();
            if (config.url == null || config.url.equals(""))
            {
                showError("Source URL may not be blank");
                return false;
            }

            try
            {
                result.step1_createProcessorAndSetUrl(config);
            } catch (WizardStepException e1)
            {
                showError(e1);
                return false;
            }

            return true;
        } else
        {
            return super.aboutToHidePanel(idOfNext);
        }
    }

    @Override
    public Object getBackPanelDescriptor()
    {
        return null;
    }

    public ChooseSourcePanel getChooseSourcePanel()
    {
        return (ChooseSourcePanel) getPanelComponent();
    }

    @Override
    public Object getNextPanelDescriptor()
    {
        return ContentAndTrackFormatPanelDescriptor.IDENTIFIER;
    }
}
