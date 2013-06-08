package net.sf.fmj.ui.wizards;

import java.util.logging.*;

import javax.media.control.*;

import net.sf.fmj.media.datasink.rtp.*;
import net.sf.fmj.ui.objeditor.*;
import net.sf.fmj.ui.utils.*;
import net.sf.fmj.ui.wizard.*;
import net.sf.fmj.utility.*;

/**
 *
 * @author Ken Larson
 *
 */
public class RTPDestPanelDescriptor extends WizardPanelDescriptor
{
    private static final Logger logger = LoggerSingleton.logger;

    public static final String IDENTIFIER = RTPDestPanelDescriptor.class
            .getName();

    private final RTPDestPanel panel;
    private final RTPTransmitWizardConfig config;
    private final RTPTransmitWizardResult result;

    public RTPDestPanelDescriptor(final RTPTransmitWizardConfig config,
            RTPTransmitWizardResult result)
    {
        panel = new RTPDestPanel();
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel);
        this.config = config;
        this.result = result;

    }

    @Override
    public boolean aboutToDisplayPanel(Object prevId)
    {
        if (prevId == getBackPanelDescriptor())
        {
            // TODO: remove old tracks

            // forward transition
            int numEnabled = 0;
            TrackControl[] trackControls = result.processor.getTrackControls();

            if (trackControls.length == 0)
            {
                showError("No tracks available");
                return false;
            }

            for (int i = 0; i < trackControls.length; ++i)
            {
                if (!trackControls[i].isEnabled())
                    continue;
                ++numEnabled;
            }

            if (numEnabled < 1)
            {
                showError("At least 1 track must be enabled");
                return false;

            }

            for (int i = 0; i < trackControls.length; ++i)
            {
                if (!trackControls[i].isEnabled())
                    continue;

                boolean audio = true; // TODO
                getRTPDestPanel().addTrack(i, audio);

                if (config.destUrl != null)
                {
                    try
                    {
                        ParsedRTPUrl parsedRTPUrl = RTPUrlParser
                                .parse(config.destUrl);
                        // TODO: check type and index
                        getRTPDestPanel().getTextSessionAddress(i).setText(
                                parsedRTPUrl.elements[i].host);
                        getRTPDestPanel().getTextPort(i).setText(
                                "" + parsedRTPUrl.elements[i].port);
                        getRTPDestPanel().getComboTTL(i).setSelectedItem(
                                "" + parsedRTPUrl.elements[i].ttl);
                    } catch (RTPUrlParserException ex)
                    {
                        logger.warning("Unable to parse RTP URL: "
                                + config.destUrl + ": " + ex);

                    }
                }

            }

        }
        // panel3.setProgressValue(0);
        // panel3.setProgressText("Connecting to Server...");
        //
        // getWizard().setNextFinishButtonEnabled(false);
        // getWizard().setBackButtonEnabled(false);
        return true;

    }

    @Override
    public boolean aboutToHidePanel(Object idOfNext)
    {
        if (idOfNext == getNextPanelDescriptor())
        {
            // finish transition

            ParsedRTPUrl destUrl = null;

            TrackControl[] trackControls = result.processor.getTrackControls();
            for (int i = 0; i < trackControls.length; ++i)
            {
                if (!trackControls[i].isEnabled())
                    continue;

                boolean audio = true; // TODO

                ComponentValidator v = new ComponentValidator();
                try
                {
                    v.validateNotEmpty(
                            getRTPDestPanel().getTextSessionAddress(i),
                            getRTPDestPanel().getLabelSessionAddress());
                    v.validateNotEmpty(getRTPDestPanel().getTextPort(i),
                            getRTPDestPanel().getLabelPort());
                    v.validateInteger(getRTPDestPanel().getTextPort(i),
                            getRTPDestPanel().getLabelPort());
                    v.validateNotEmpty(getRTPDestPanel().getComboTTL(i),
                            getRTPDestPanel().getLabelTTL());
                    v.validateInteger(getRTPDestPanel().getComboTTL(i),
                            getRTPDestPanel().getLabelTTL());

                } catch (ComponentValidationException e)
                {
                    ErrorDialog.showError(getRTPDestPanel(), e.getMessage());
                    return false;
                }

                final String sessionAddress = getRTPDestPanel()
                        .getTextSessionAddress(i).getText();
                final int port = Integer.parseInt(getRTPDestPanel()
                        .getTextPort(i).getText());
                final int ttl = Integer.parseInt((String) getRTPDestPanel()
                        .getComboTTL(i).getSelectedItem());

                ParsedRTPUrlElement e = new ParsedRTPUrlElement();
                e.host = sessionAddress;
                e.port = port;
                e.ttl = ttl;
                e.type = audio ? ParsedRTPUrlElement.AUDIO
                        : ParsedRTPUrlElement.VIDEO;

                logger.fine("ParsedRTPUrlElement: " + e);

                destUrl = new ParsedRTPUrl(e); // TODO: other tracks

            }

            try
            {
                config.destUrl = destUrl.toString();
                result.step4_setDestUrlAndStart(config);
            } catch (WizardStepException e)
            {
                showError(e);
                return false;
            }

        }

        return true;
    }

    @Override
    public Object getBackPanelDescriptor()
    {
        return ContentAndTrackFormatPanelDescriptor.IDENTIFIER;
    }

    @Override
    public Object getNextPanelDescriptor()
    {
        return FINISH;
    }

    public RTPDestPanel getRTPDestPanel()
    {
        return (RTPDestPanel) getPanelComponent();
    }

}
