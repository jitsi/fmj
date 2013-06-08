package net.sf.fmj.ui.wizards;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;

import net.sf.fmj.ejmf.toolkit.util.*;
import net.sf.fmj.utility.*;

/**
 *
 * @author Ken Larson
 *
 */
public class ProcessorWizardResult
{
    private static final Logger logger = LoggerSingleton.logger;

    public Processor processor;
    private StateWaiter stateWaiter;
    private DataSource destDataSource;
    private DataSink destDataSink;

    /** config.url must be set */
    public void step1_createProcessorAndSetUrl(ProcessorWizardConfig config)
            throws WizardStepException
    {
        String url = config.url;

        if (url == null || url.equals(""))
        {
            throw new WizardStepException("Source URL may not be blank");

        }

        logger.fine("Creating processor");
        try
        {
            processor = Manager.createProcessor(new MediaLocator(url));
        } catch (IOException e)
        {
            throw new WizardStepException(e);

        } catch (NoProcessorException e)
        {
            throw new WizardStepException(e);
        }

        logger.fine("Created processor " + processor);

        logger.fine("Configuring processor");

        // configure the processor
        stateWaiter = new StateWaiter(processor);
        if (!stateWaiter.blockingConfigure())
        {
            throw new WizardStepException("Failed to configure processor");

        }

        logger.fine("Configured processor");
    }

    /** config.contentDescriptor must be set */
    public void step2_setContentDescriptor(ProcessorWizardConfig config)
            throws WizardStepException
    {
        if (processor.setContentDescriptor(config.contentDescriptor) == null)
        {
            throw new WizardStepException(
                    "Unable to set content descriptor to "
                            + config.contentDescriptor);
        }

    }

    /** config.trackConfigs must be set */
    public void step3_setTrackConfigs(ProcessorWizardConfig config)
            throws WizardStepException
    {
        TrackConfig[] trackConfigs = config.trackConfigs;

        // forward transition
        final TrackControl trackControls[] = processor.getTrackControls();

        for (int i = 0; i < trackControls.length; i++)
        {
            final boolean enabled = trackConfigs[i].enabled;
            trackControls[i].setEnabled(enabled);

            if (enabled)
            { // TODO: do the conversion to Format here, so we can validate.
                Format f = trackConfigs[i].format;
                Format result = trackControls[i].setFormat(f);
                if (result == null)
                {
                    throw new WizardStepException(
                            "Unable to set format of track " + i + " to " + f);

                }
            }
        }
    }

    /** config.desturl must be set */
    public void step4_setDestUrlAndStart(ProcessorWizardConfig config)
            throws WizardStepException
    {
        // ParsedRTPUrl destUrl = config.destUrl;

        if (!stateWaiter.blockingRealize())
        {
            throw new WizardStepException("Failed to realize processor");
        }

        try
        {
            destDataSource = processor.getDataOutput();
        } catch (NotRealizedError e)
        {
            throw new WizardStepException(e);

        }

        // hand this datasource to manager for creating an RTP
        // datasink our RTP datasink will multicast the audio
        try
        {
            String url = config.destUrl;

            logger.fine("Dest url: " + url);

            MediaLocator m = new MediaLocator(url);

            destDataSink = Manager.createDataSink(destDataSource, m);
            destDataSink.open();
            destDataSink.start();

            // doesn't appear to do anything:
            // processor.addControllerListener(new ControllerListener() {
            //
            // public void controllerUpdate(ControllerEvent event)
            // {
            // if (event instanceof ControllerClosedEvent)
            // {
            // System.out.println("Controller closed, closing data sink");
            // destDataSink.close();
            //
            // }
            // }
            //
            // });

            logger.fine("Starting processor");
            stateWaiter.blockingStart();

        } catch (IOException e)
        {
            throw new WizardStepException(e);
        } catch (NoDataSinkException e)
        {
            throw new WizardStepException(e);
        }
    }
}
