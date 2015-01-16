package net.sf.fmj.media;

import java.awt.*;

import javax.media.*;
import javax.media.control.*;

import net.sf.fmj.filtergraph.*;

/**
 * BasicFilterModule is a module which is not threaded and have one
 * InputConnector and one OutputConnector. It receives data from its input
 * connector, pass the data to the level 3 plugIn codec and put the result in
 * the OutputConnector. BasicFilterModule can be either Push or Pull driven. The
 * plugIn codec might be media decoder, media encoder, effect etc.
 *
 */
public class BasicFilterModule extends BasicModule
{
    protected Codec codec;
    protected InputConnector ic;
    protected OutputConnector oc;
    protected FrameProcessingControl frameControl = null;
    protected float curFramesBehind = 0f;
    protected float prevFramesBehind = 0f;

    protected java.awt.Frame controlFrame;
    protected final boolean VERBOSE_CONTROL = false;

    protected Buffer storedInputBuffer, storedOutputBuffer;

    protected boolean readPendingFlag = false, writePendingFlag = false;

    private boolean failed = false;

    private boolean markerSet = false;

    private Object lastHdr = null;

    public BasicFilterModule(Codec c)
    {
        ic = new BasicInputConnector();
        registerInputConnector("input", ic);
        oc = new BasicOutputConnector();
        registerOutputConnector("output", oc);
        setCodec(c);
        protocol = Connector.ProtocolPush;
        Object control = c.getControl(FrameProcessingControl.class.getName());
        if (control instanceof FrameProcessingControl)
            frameControl = (FrameProcessingControl) control;
    }

    @Override
    public void doClose()
    {
        if (codec != null)
        {
            codec.close();
        }
        if (controlFrame != null)
        {
            controlFrame.dispose();
            controlFrame = null;
        }
        /*
         * if (monitorFrame != null) monitorFrame.dispose();
         */
    }

    @Override
    public boolean doPrefetch()
    {
        return super.doPrefetch();
    }

    @Override
    public boolean doRealize()
    {
        if (codec != null)
        {
            try
            {
                codec.open();

                if (VERBOSE_CONTROL)
                {
                    controlFrame = new java.awt.Frame(codec.getName()
                            + "  Control");
                    controlFrame
                            .setLayout(new com.sun.media.controls.VFlowLayout(1));
                    controlFrame.add(new Label(codec.getName() + "  Control",
                            Label.CENTER));
                    controlFrame.add(new Label(" "));

                    Control[] c = (Control[]) codec.getControls();
                    for (int i = 0; i < c.length; i++)
                    {
                        controlFrame.add(c[i].getControlComponent());
                    }
                    controlFrame.pack();
                    controlFrame.show();
                }

            } catch (ResourceUnavailableException rue)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * @return the plugIn codec of this filter, null if not yet set.
     */
    public Codec getCodec()
    {
        return codec;
    }

    @Override
    public Object getControl(String s)
    {
        return codec.getControl(s);
    }

    @Override
    public Object[] getControls()
    {
        return codec.getControls();
    }

    @Override
    public boolean isThreaded()
    {
        if ((getProtocol() == Connector.ProtocolSafe))
            return true;
        return false;

    }

    @Override
    public void process()
    {
        // System.out.println("Filter.process: " + codec);

        Buffer inputBuffer, outputBuffer;

        do
        {
            if (readPendingFlag)
                inputBuffer = storedInputBuffer;
            else
            {
                Format incomingFormat;
                inputBuffer = ic.getValidBuffer();
                incomingFormat = inputBuffer.getFormat();

                if (incomingFormat == null)
                {
                    // Something's weird, we'll just assume it's the previous
                    // format.
                    incomingFormat = ic.getFormat();
                    inputBuffer.setFormat(incomingFormat);
                }

                if (incomingFormat != ic.getFormat() && incomingFormat != null
                        && !incomingFormat.equals(ic.getFormat())
                        && !inputBuffer.isDiscard())
                {
                    // The format is changed mid-stream!

                    if (writePendingFlag)
                    {
                        // Discard the pending output buffer.
                        storedOutputBuffer.setDiscard(true);
                        oc.writeReport();
                        writePendingFlag = false;
                    }

                    // Attempt to re-initialize the plugin codec.
                    // Bail out if failed.
                    if (!reinitCodec(inputBuffer.getFormat()))
                    {
                        // Failed.
                        inputBuffer.setDiscard(true);
                        ic.readReport();
                        failed = true;
                        // Just signal an internal error for now.
                        if (moduleListener != null)
                            moduleListener.formatChangedFailure(this,
                                    ic.getFormat(), inputBuffer.getFormat());
                        return;
                    }

                    Format oldFormat = ic.getFormat();
                    ic.setFormat(inputBuffer.getFormat());
                    if (moduleListener != null)
                        moduleListener.formatChanged(this, oldFormat,
                                inputBuffer.getFormat());
                }

                // The marker flag needs to be handle more delicately.
                // For codec that takes multiple input buffers to generate
                // a single output buffer (e.g. RTP depackizer), if there's
                // one input buffer that has the market flag set, the
                // corresponding output buffer will need to have the
                // marker flag set.
                if ((inputBuffer.getFlags() & Buffer.FLAG_SYSTEM_MARKER) != 0)
                {
                    markerSet = true;
                }
            }

            if (writePendingFlag)
                outputBuffer = storedOutputBuffer;
            else
            {
                outputBuffer = oc.getEmptyBuffer();
                if (outputBuffer != null)
                {
                    outputBuffer.setLength(0);
                    outputBuffer.setOffset(0);
                    lastHdr = outputBuffer.getHeader();
                }
            }

            // Copy from input to output.
            outputBuffer.setTimeStamp(inputBuffer.getTimeStamp());
            outputBuffer.setRtpTimeStamp(inputBuffer.getRtpTimeStamp());
            outputBuffer.setHeaderExtension(inputBuffer.getHeaderExtension());
            outputBuffer.setDuration(inputBuffer.getDuration());
            outputBuffer.setSequenceNumber(inputBuffer.getSequenceNumber());
            outputBuffer.setFlags(inputBuffer.getFlags());
            outputBuffer.setHeader(inputBuffer.getHeader());

            // Check if we are in the resetted state.
            if (resetted)
            {
                // Check if the input buffer contains the zero-length
                // flush flag. If so, we are almost done.
                if ((inputBuffer.getFlags() & Buffer.FLAG_FLUSH) != 0)
                {
                    codec.reset();
                    resetted = false;
                }

                // In the resetted state, we won't process any of the
                // data. We'll just return the buffers unprocessed.
                readPendingFlag = writePendingFlag = false;
                ic.readReport();
                oc.writeReport();
                return;
            }

            if (failed || inputBuffer.isDiscard())
            {
                // Reset the marker flag.
                if (markerSet)
                {
                    outputBuffer.setFlags(outputBuffer.getFlags()
                            & ~Buffer.FLAG_SYSTEM_MARKER);
                    markerSet = false;
                }

                // If the discard flag from the upstream module
                // is set, it has probably dropped a frame. The
                // curFramesBehind counter should be resetted.
                // Otherwise, too many frames will be dropped.
                curFramesBehind = 0;

                ic.readReport();

                // Propagate the discard flag downstream.
                // This is needed so the renderer can keep track of
                // a correct frame count even for the discarded frames.
                if (!writePendingFlag)
                    oc.writeReport();

                return;
            }

            if (frameControl != null && curFramesBehind != prevFramesBehind
                    && (inputBuffer.getFlags() & Buffer.FLAG_NO_DROP) == 0)
            {
                frameControl.setFramesBehind(curFramesBehind);
                prevFramesBehind = curFramesBehind;
            }

            int rc = 0;

            try
            {
                rc = codec.process(inputBuffer, outputBuffer);

            } catch (Throwable e)
            {
                Log.dumpStack(e);
                if (moduleListener != null)
                    moduleListener.internalErrorOccurred(this);
            }

            if (PlaybackEngine.TRACE_ON && !verifyBuffer(outputBuffer))
            {
                System.err.println("verify buffer failed: " + codec);
                Thread.dumpStack();
                if (moduleListener != null)
                    moduleListener.internalErrorOccurred(this);
            }

            if ((rc & PlugIn.PLUGIN_TERMINATED) != 0)
            {
                failed = true;
                if (moduleListener != null)
                    moduleListener.pluginTerminated(this);
                readPendingFlag = writePendingFlag = false;
                ic.readReport();
                oc.writeReport();
                return;
            }

            if (curFramesBehind > 0f && outputBuffer.isDiscard())
            {
                // One frame has been dropped. We'll need to update
                // the framesBehind count.
                // If we don't do that, we run into trouble that the
                // renderer sometimes cannot update the frames behind
                // count immediately (thread scheduling may prefer this
                // thread to the rendering thread). As a result, the
                // decoder will keep dropping frames without a correct
                // frame count from the renderer.
                curFramesBehind -= 1.0f;
                if (curFramesBehind < 0)
                    curFramesBehind = 0f;

                // We'll also need to propagate the discard flag
                // downstream just so the the downstream module can
                // update their framesbehind info accordingly. To
                // do that, we override the plugin's return value to
                // make sure that OUTPUT_BUFFER_NOT_FILLED is not used.
                rc = rc & ~PlugIn.OUTPUT_BUFFER_NOT_FILLED;
            }

            if ((rc & PlugIn.BUFFER_PROCESSED_FAILED) != 0)
            {
                outputBuffer.setDiscard(true);
                // Reset the marker flag.
                if (markerSet)
                {
                    outputBuffer.setFlags(outputBuffer.getFlags()
                            & ~Buffer.FLAG_SYSTEM_MARKER);
                    markerSet = false;
                }
                ic.readReport();
                oc.writeReport();
                readPendingFlag = writePendingFlag = false;
                return;
            }

            // Do not propagate the EOM flag if the input buffer is
            // not fully consumed or the output buffer is not fully filled.
            if (outputBuffer.isEOM()
                    && ((rc & PlugIn.INPUT_BUFFER_NOT_CONSUMED) != 0 || (rc & PlugIn.OUTPUT_BUFFER_NOT_FILLED) != 0))
            {
                // We are not quite done yet, so the output buffer
                // is not set to EOM.
                outputBuffer.setEOM(false);
            }

            if ((rc & PlugIn.OUTPUT_BUFFER_NOT_FILLED) != 0)
            {
                writePendingFlag = true;// next call to getEmptyBuffer will
                                        // return the same Buffer
                storedOutputBuffer = outputBuffer;
            } else
            {
                if (markerSet)
                {
                    outputBuffer.setFlags(outputBuffer.getFlags()
                            | Buffer.FLAG_SYSTEM_MARKER);
                    markerSet = false;
                }
                /*
                 * if (outputBuffer.getFormat() instanceof RGBFormat)
                 * monitorCheck(outputBuffer);
                 */
                oc.writeReport();
                writePendingFlag = false;
            }

            if (((rc & PlugIn.INPUT_BUFFER_NOT_CONSUMED) != 0 || (inputBuffer
                    .isEOM() && !outputBuffer.isEOM())))
            {
                readPendingFlag = true; // next call to getValidBuffer will
                                        // return the same Buffer
                storedInputBuffer = inputBuffer;
            } else
            {
                inputBuffer.setHeader(lastHdr);
                ic.readReport();
                readPendingFlag = false;
            }

        } while (readPendingFlag);
    }

    /**
     * A new input format has been detected, we'll check if the existing codec
     * can handle it. Otherwise, we'll try to re-create a new codec to handle
     * it.
     */
    protected boolean reinitCodec(Format input)
    {
        // Query the existing plugin to see if it supports the new input.
        if (codec != null)
        {
            if (codec.setInputFormat(input) != null)
            {
                // Fine, the existing codec still works.
                return true;
            }
            // close the previous codec.
            codec.close();
            codec = null;
        }

        // Find a new codec that supports the input
        Codec c;
        if ((c = SimpleGraphBuilder.findCodec(input, null, null, null)) == null)
            return false;

        setCodec(c);
        return true;
    }

    public boolean setCodec(Codec codec)
    { // patch until codecmanager exists
        this.codec = codec;
        return true;
    }

    /**
     * sets the plugIn codec of this filter.
     *
     * @param codec
     *            the plugIn codec (should we specify Codec class or String)
     * @return true if successful
     */
    public boolean setCodec(String codec)
    {
        return true;
    }

    @Override
    public void setFormat(Connector c, Format f)
    {
        if (c == ic)
        {
            // Input Connector
            if (codec != null)
                codec.setInputFormat(f);
        } else if (c == oc)
        {
            if (codec != null)
                codec.setOutputFormat(f);
        }
    }

    protected void setFramesBehind(float framesBehind)
    {
        curFramesBehind = framesBehind;
    }

    /*
     * private Frame monitorFrame = null; private Panel monitorPanel = null;
     * private long oldTime = (long) -1E+9; private Dimension monitorSize =
     * null; private void monitorCheck(Buffer buffer) { if (buffer.getFormat()
     * instanceof RGBFormat) { RGBFormat rgb = (RGBFormat) buffer.getFormat();
     * if (monitorFrame == null) { monitorSize = rgb.getSize(); monitorPanel =
     * new Panel() { public void update(Graphics g) { }
     *
     * public void paint(Graphics g) { }
     *
     * public Dimension getPreferredSize() { return monitorSize; } };
     * monitorFrame = new Frame("Monitor"); monitorFrame.setLayout( new
     * BorderLayout() ); monitorFrame.add("Center", monitorPanel);
     * monitorFrame.pack();
     *
     * monitorFrame.setVisible(true); } long currentTime =
     * System.currentTimeMillis(); if (currentTime >= oldTime + 1000) { oldTime
     * = currentTime;
     *
     * Image image = javax.media.util.ImageConverter.convertToImage(buffer);
     * Graphics g = monitorPanel.getGraphics(); g.drawImage(image, 0, 0,
     * monitorPanel); } } }
     */
}
