package net.sf.fmj.ui.wizards;

import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.ui.wizard.*;
import net.sf.fmj.utility.*;

/**
 *
 * @author Ken Larson
 *
 */
public class ContentAndTrackFormatPanelDescriptor extends WizardPanelDescriptor
{
    private static final Logger logger = LoggerSingleton.logger;

    public static final String IDENTIFIER = ContentAndTrackFormatPanelDescriptor.class
            .getName();

    private final ContentAndTrackFormatPanel panel;
    private final ProcessorWizardConfig config;
    private final ProcessorWizardResult result;
    private final Object nextPanelDescriptor;
    private final ContentDescriptorFilter contentDescriptorFilter;
    private TrackConfig[] trackConfigs;

    public ContentAndTrackFormatPanelDescriptor(Object nextPanelDescriptor,
            ContentDescriptorFilter contentDescriptorFilter,
            final ProcessorWizardConfig config, ProcessorWizardResult result)
    {
        panel = new ContentAndTrackFormatPanel();

        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel);
        this.config = config;
        this.result = result;
        this.nextPanelDescriptor = nextPanelDescriptor;
        this.contentDescriptorFilter = contentDescriptorFilter;

    }

    @Override
    public boolean aboutToDisplayPanel(Object prevId)
    {
        if (prevId == getBackPanelDescriptor())
        {
            // forward transition
            ContentDescriptor[] contentDescriptors = result.processor
                    .getSupportedContentDescriptors();

            if (contentDescriptors.length == 0)
            {
                showError("Processor supports no content descriptors");
                return false;
            }

            contentDescriptors = compatible(contentDescriptors); // eliminate
                                                                 // non-RTP-compatible
                                                                 // ones.

            if (contentDescriptors.length == 0)
            {
                showError("Processor supports no compatible content descriptors");
                return false;
            }

            getContentAndTrackFormatPanel().getComboFormat().setModel(
                    new javax.swing.DefaultComboBoxModel(contentDescriptors));

            int contentDescriptorIndexToSet = 0;

            if (config.contentDescriptor != null)
            {
                for (int i = 0; i < contentDescriptors.length; ++i)
                {
                    if (config.contentDescriptor.equals(contentDescriptors[i]))
                    {
                        contentDescriptorIndexToSet = i;
                        break;
                    }
                }
            }

            config.contentDescriptor = contentDescriptors[contentDescriptorIndexToSet]; // save,
                                                                                        // in
                                                                                        // case
                                                                                        // different

            getContentAndTrackFormatPanel().getComboFormat().setSelectedItem(
                    config.contentDescriptor);

            try
            {
                result.step2_setContentDescriptor(config);
            } catch (WizardStepException e)
            {
                showError(e);
                return false;
            }

            // TODO:

            // TODO: if the users changes the combo, we need to change the
            // tracks.

            final TrackControl trackControls[] = result.processor
                    .getTrackControls();
            if (trackControls == null || trackControls.length < 1)
            {
                showError("No tracks available");
                return false;
            }

            trackConfigs = new TrackConfig[trackControls.length];
            for (int i = 0; i < trackConfigs.length; ++i)
                trackConfigs[i] = new TrackConfig();

            for (int i = 0; i < trackControls.length; i++)
            {
                Format[] formats = trackControls[i].getSupportedFormats(); // TODO:
                                                                           // FMJ
                                                                           // returns
                                                                           // just
                                                                           // a
                                                                           // few
                                                                           // generic
                                                                           // formats,
                                                                           // and
                                                                           // JMF
                                                                           // returns
                                                                           // a
                                                                           // lot
                                                                           // of
                                                                           // specific
                                                                           // formats
                                                                           // (for
                                                                           // raw),
                                                                           // and
                                                                           // all
                                                                           // rtp
                                                                           // audio
                                                                           // formats
                                                                           // (for
                                                                           // raw
                                                                           // rtp)

                /*
                 * Available track format: dvi/rtp, 8000.0 Hz, 4-bit, Mono
                 * Available track format: dvi/rtp, 11025.0 Hz, 4-bit, Mono
                 * Available track format: dvi/rtp, 22050.0 Hz, 4-bit, Mono
                 * Available track format: ULAW/rtp, 8000.0 Hz, 8-bit, Mono,
                 * FrameSize=8 bits Available track format: gsm/rtp, 8000.0 Hz,
                 * Mono, FrameSize=264 bits
                 */

                if (formats == null)
                {
                    logger.warning("No supported formats (formats=null) for track "
                            + i);
                    trackConfigs[i].enabled = false;
                    trackConfigs[i].format = null;
                    continue;
                }
                if (formats.length == 0)
                {
                    logger.warning("No supported formats (formats.length=0) for track "
                            + i);
                    trackConfigs[i].enabled = false;
                    trackConfigs[i].format = null;
                    continue;
                }

                for (int j = 0; j < formats.length; ++j)
                {
                    logger.info("Available track format: " + formats[j]);
                }

                // TODO: hard-coded
                getContentAndTrackFormatPanel()
                        .getAudioFormatPanel()
                        .getComboAudioEncoding()
                        .setModel(
                                new javax.swing.DefaultComboBoxModel(
                                        new String[] { AudioFormat.ULAW_RTP }));
                // getContentAndTrackFormatPanel().getAudioFormatPanel().getComboAudioEncoding().setModel(
                // new javax.swing.DefaultComboBoxModel(new String[] {
                // AudioFormat.LINEAR }));
                getContentAndTrackFormatPanel()
                        .getAudioFormatPanel()
                        .getComboAudioSampleRate()
                        .setModel(
                                new javax.swing.DefaultComboBoxModel(
                                        new String[] { "8000" }));

                if (config.trackConfigs != null
                        && config.trackConfigs.length >= i + 1
                        && config.trackConfigs[i].format instanceof AudioFormat)
                { // copy from prefs
                    trackConfigs[i].format = config.trackConfigs[i].format;
                    trackConfigs[i].enabled = config.trackConfigs[i].enabled;
                } else
                { // default
                    trackConfigs[i].format = new AudioFormat(
                            AudioFormat.ULAW_RTP, 8000.0, 8, 1,
                            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED); // TODO:
                                                                            // hard-coded.
                    trackConfigs[i].enabled = trackControls[i].isEnabled();
                }

                boolean formatOk = false;
                for (int j = 0; j < formats.length; ++j)
                {
                    logger.fine("Track " + i + " supports format " + formats[j]);
                    if (formats[j].matches(trackConfigs[i].format))
                    {
                        // TODO: we only support 1 audio track. Enforce it.
                        getContentAndTrackFormatPanel().addTrack(i,
                                trackConfigs[i].enabled,
                                (AudioFormat) trackConfigs[i].format);
                        formatOk = true;
                        break;
                    }
                }

                if (!formatOk)
                {
                    trackConfigs[i].enabled = false;
                    trackConfigs[i].format = null;
                }

            }
        }

        return true;
    }

    @Override
    public boolean aboutToHidePanel(Object idOfNext)
    {
        if (idOfNext == getNextPanelDescriptor())
        {
            // forward transition

            for (int i = 0; i < trackConfigs.length; i++)
            {
                trackConfigs[i].enabled = getContentAndTrackFormatPanel()
                        .getTrackControlPanel(i).getCheckBoxEnableTrack()
                        .isSelected();

                if (trackConfigs[i].enabled)
                { // TODO: do the conversion to Format here, so we can validate.
                    trackConfigs[i].format = getContentAndTrackFormatPanel()
                            .getTrackControlPanel(i).getAudioFormatPanel()
                            .getAudioFormat();
                }
            }

            try
            {
                config.trackConfigs = trackConfigs;
                result.step3_setTrackConfigs(config);
            } catch (WizardStepException e)
            {
                showError(e);
                return false;
            }

            return true;
        }

        return super.aboutToHidePanel(idOfNext);
    }

    private ContentDescriptor[] compatible(
            ContentDescriptor[] contentDescriptors)
    {
        final List<ContentDescriptor> result = new ArrayList<ContentDescriptor>();
        for (int i = 0; i < contentDescriptors.length; ++i)
        {
            if (isCompatible(contentDescriptors[i]))
                result.add(contentDescriptors[i]);
        }

        final ContentDescriptor[] arrayResult = new ContentDescriptor[result
                .size()];
        for (int i = 0; i < result.size(); ++i)
            arrayResult[i] = result.get(i);
        return arrayResult;
    }

    @Override
    public Object getBackPanelDescriptor()
    {
        return ChooseSourcePanelDescriptor.IDENTIFIER;
    }

    public ContentAndTrackFormatPanel getContentAndTrackFormatPanel()
    {
        return (ContentAndTrackFormatPanel) getPanelComponent();
    }

    @Override
    public Object getNextPanelDescriptor()
    {
        return nextPanelDescriptor;
    }

    private boolean isCompatible(ContentDescriptor d)
    {
        return contentDescriptorFilter.isCompatible(d);

    }

}
