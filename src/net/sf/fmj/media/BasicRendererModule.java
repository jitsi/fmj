package net.sf.fmj.media;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.filtergraph.*;
import net.sf.fmj.media.renderer.audio.*;
import net.sf.fmj.media.rtp.util.*;
import net.sf.fmj.media.util.*;

/**
 * BasicRenderer is a module which have InputConnectors and no OutputConnectors.
 * It receives data from its input connector and put its output in output device
 * such as file, URL, screen, audio device, output DataSource or null.<br>
 * MediaRenderers can be either Pull driven (as AudioPlayer) or Push driven (as
 * File renderer). VideoRenderer might be implemented as either Push or Pull.<br>
 * MediaRenderers are stopAtTime aware (so that the audio renderer would stop at
 * the correct time) and are responsible to stop the player at the required time
 * (no separate thread for poling TimeBase). <br>
 * There is no need to define buffers allocation and connectors behavior here,
 * as it is done in module. <br>
 * <br>
 * <i>Common functionality of renderers would be put here as we start the
 * implementation</i><br>
 * <i>We need the level 3 design to continue working on this class</i>
 *
 */
public class BasicRendererModule extends BasicSinkModule implements
        RTPTimeReporter
{
    protected PlaybackEngine engine;
    protected Renderer renderer;
    protected InputConnector ic;
    protected int framesPlayed = 0;
    protected float frameRate = 30f;
    protected boolean framesWereBehind = false;
    protected boolean prefetching = false;
    protected boolean started = false;
    private boolean opened = false; // to avoid opening the plugin more
    // than once.
    private int chunkSize = Integer.MAX_VALUE;
    private long lastDuration = 0;

    private RTPTimeBase rtpTimeBase = null;

    private String rtpCNAME = null;

    RenderThread renderThread;

    private Object prefetchSync = new Object();

    // This is the media time as computed by the data that's
    // gone through the sink. This is not the same as the
    // time reported by the Player's clock. The difference is
    // the player's time also take into consideration the
    // renderer's buffered data. Hence it reflects the actual
    // time played through the renderer.
    // elapseTime is used for prerolling and setStopTime.
    private ElapseTime elapseTime = new ElapseTime();

    private long LEEWAY = 10; // 10 msec.

    private long lastRendered = 0;

    private boolean failed = false;

    private boolean notToDropNext = false;

    private Buffer storedBuffer = null; // the previous buffer when data is

    // not fully consumed.
    private boolean checkRTP = false;

    private boolean noSync = false;

    final float MAX_RATE = 1.05f;

    final float RATE_INCR = .01f;

    final int FLOW_LIMIT = 20;

    boolean overMsg = false;

    int overflown = FLOW_LIMIT / 2;

    float rate = 1.0f;

    // Used in waitForPT. It computes dynamically the error inherent
    // in System.sleep().
    long systemErr = 0L;

    // Set a limit here. Cannot sync beyond a 2 sec difference.
    static final long RTP_TIME_MARGIN = 2000000000L;

    boolean rtpErrMsg = false;

    long lastTimeStamp;

    static final int MAX_CHUNK_SIZE = 16; // 1/16 of a sec.
    AudioFormat ulawFormat = new AudioFormat(AudioFormat.ULAW);
    AudioFormat linearFormat = new AudioFormat(AudioFormat.LINEAR);

    protected BasicRendererModule(Renderer r)
    {
        setRenderer(r);
        ic = new BasicInputConnector();
        if (r instanceof javax.media.renderer.VideoRenderer)
            ic.setSize(4);
        else
            ic.setSize(1);
        ic.setModule(this);
        registerInputConnector("input", ic);
        setProtocol(Connector.ProtocolSafe);
    }

    @Override
    public void abortPrefetch()
    {
        renderThread.pause();
        renderer.close();
        prefetching = false;
        opened = false;
    }

    private int computeChunkSize(Format format)
    {
        // Break up the data if it's linear or ulaw audio.
        if (format instanceof AudioFormat
                && (ulawFormat.matches(format) || linearFormat.matches(format)))
        {
            AudioFormat af = (AudioFormat) format;
            int units = af.getSampleSizeInBits() * af.getChannels() / 8;
            if (units == 0) // sample size < 1 byte.
                units = 1;
            int chunks = (int) af.getSampleRate() * units / MAX_CHUNK_SIZE;

            // Chunks should be in multiples of the independent units.
            return (chunks / units * units);
        }

        return Integer.MAX_VALUE;
    }

    @Override
    public void doClose()
    {
        renderThread.kill();
        if (renderer != null)
            renderer.close();
        if (rtpTimeBase != null)
        {
            RTPTimeBase.remove(this, rtpCNAME);
            rtpTimeBase = null;
        }
    }

    @Override
    public void doDealloc()
    {
        renderer.close();
    }

    @Override
    public void doFailedPrefetch()
    {
        renderThread.pause();
        renderer.close();
        opened = false;
        prefetching = false;
    }

    /**
     * Handles the aftermath of prefetching.
     */
    private void donePrefetch()
    {
        synchronized (prefetchSync)
        {
            if (!started && prefetching)
                renderThread.pause();
            prefetching = false;
        }

        if (moduleListener != null)
            moduleListener.bufferPrefetched(this);
    }

    @Override
    public void doneReset()
    {
        renderThread.pause();
    }

    @Override
    public boolean doPrefetch()
    {
        super.doPrefetch();
        if (!opened)
        {
            try
            {
                renderer.open();
            } catch (ResourceUnavailableException e)
            {
                prefetchFailed = true;
                return false;
            }
            prefetchFailed = false;
            opened = true;

        }

        if (!((PlaybackEngine) controller).prefetchEnabled)
            return true;

        prefetching = true;

        // We also need to start the render thread. Otherwise, it won't
        // get the first prefetch frame.
        renderThread.start();

        return true;
    }

    /**
     * The loop to process the data. It handles the getting and putting back of
     * the data buffers. It in turn calls scheduleBuffer(Buffer) to do the bulk
     * of processing.
     */
    protected boolean doProcess()
    {
        // Notify the engine if stop time has been reached.
        if ((started || prefetching) && stopTime > -1
                && elapseTime.value >= stopTime)
        {
            if (renderer instanceof Drainable)
                ((Drainable) renderer).drain();
            doStop();
            if (moduleListener != null)
                moduleListener.stopAtTime(this);
        }

        Buffer buffer;

        if (storedBuffer != null)
            buffer = storedBuffer;
        else
        {
            buffer = ic.getValidBuffer();
            /*
             * System.err.println("TS: " + buffer.getTimeStamp() + " dur: " +
             * buffer.getDuration() + " len: " + buffer.getLength() + " seq: " +
             * buffer.getSequenceNumber() + " eom: " + buffer.isEOM() +
             * " discard: " + buffer.isDiscard());
             */
        }

        if (!checkRTP)
        {
            // If this is playing back from RTP, we'll get an RTPTimeBase.
            // This test cannot be performed at realize time since the
            // actual rtp DataSource may not be available at that time
            // for RTSP playback.

            if ((buffer.getFlags() & Buffer.FLAG_RTP_TIME) != 0)
            {
                String key = engine.getCNAME();
                if (key != null)
                {
                    rtpTimeBase = RTPTimeBase.find(this, key);
                    rtpCNAME = key;
                    // Set this as the master if it is playing audio.
                    if (ic.getFormat() instanceof AudioFormat)
                    {
                        Log.comment("RTP master time set: " + renderer + "\n");
                        // System.err.println("RTP sync kicks in");
                        rtpTimeBase.setMaster(this);
                    }
                    checkRTP = true;
                    noSync = false;
                } else
                {
                    // There's no cname association yet. We can't do any
                    // synchronization at this point.
                    // We'll have to keep checking back again.
                    noSync = true;
                }
            } else
                checkRTP = true;
        }

        lastTimeStamp = buffer.getTimeStamp();

        // Check if we are in the resetted state.
        if (failed || resetted)
        {
            // Check if the input buffer contains the zero-length
            // flush flag. If so, we are almost done.
            if ((buffer.getFlags() & Buffer.FLAG_FLUSH) != 0)
            {
                resetted = false;
                renderThread.pause();

                // Notify the engine if the module has done processing.
                if (moduleListener != null)
                    moduleListener.resetted(this);
            }

            // In the resetted state, we won't process any of the
            // data. We'll just return the buffers unprocessed.
            storedBuffer = null;
            ic.readReport();
            return true;
        }

        // Schedule the buffer to be rendered.
        boolean rtn = scheduleBuffer(buffer);

        // Handle EOM
        // Also need to make sure this buffer is fully consumed, i.e.,
        // the storedBuffer is not set.
        if (storedBuffer == null && buffer.isEOM())
        {
            if (prefetching)
                donePrefetch();

            // Eventhough we've received EOM, we haven't finished
            // processing yet. We'll need to sleep till its PT.
            // Otherwise, for low FR movies, the last frame will
            // not be presented to its full duration.
            if ((buffer.getFlags() & Buffer.FLAG_NO_WAIT) == 0
                    && buffer.getTimeStamp() > 0 && buffer.getDuration() > 0
                    && buffer.getFormat() != null
                    && !(buffer.getFormat() instanceof AudioFormat) && !noSync)
            {
                waitForPT(buffer.getTimeStamp() + lastDuration);
            }

            storedBuffer = null;
            ic.readReport();

            doStop();
            if (moduleListener != null)
                moduleListener.mediaEnded(this);

            return true;
        }

        // If we are fully done with this buffer, return it.
        if (storedBuffer == null)
            ic.readReport();

        return rtn;
    }

    @Override
    public boolean doRealize()
    {
        chunkSize = computeChunkSize(ic.getFormat());

        renderThread = new RenderThread(this);

        engine = (PlaybackEngine) getController();

        return true;
    }

    @Override
    public void doStart()
    {
        super.doStart();
        // Clocked renderer is handled by the super.doStart().
        if (!(renderer instanceof Clock))
            renderer.start();
        prerolling = false;
        started = true;

        synchronized (prefetchSync)
        {
            prefetching = false;
            renderThread.start();
        }
    }

    @Override
    public void doStop()
    {
        started = false;
        prefetching = true;
        super.doStop();
        // Clocked renderer is handled by the super.doStop().
        if (renderer != null && !(renderer instanceof Clock))
            renderer.stop();
    }

    @Override
    public Object getControl(String s)
    {
        return renderer.getControl(s);
    }

    @Override
    public Object[] getControls()
    {
        return renderer.getControls();
    }

    public int getFramesPlayed()
    {
        return framesPlayed;
    }

    public Renderer getRenderer()
    {
        return renderer;
    }

    public long getRTPTime()
    {
        if (ic.getFormat() instanceof AudioFormat)
        {
            if (renderer instanceof AudioRenderer)
            {
                /*
                 * System.err.println("rtpTime[audio]: " + lastTimeStamp +
                 * " latency: " + ((AudioRenderer)renderer).getLatency() +
                 * " TS: " + (lastTimeStamp -
                 * ((AudioRenderer)renderer).getLatency()));
                 */
                return lastTimeStamp - ((AudioRenderer) renderer).getLatency();
            } else
            {
                // System.err.println("rtpTime[audio]: " + lastTimeStamp);
                return lastTimeStamp;
            }
        } else
        {
            // System.err.println("rtpTime[video]: " + lastTimeStamp);
            return lastTimeStamp;
        }
    }

    private long getSyncTime(long pts)
    {
        if (rtpTimeBase != null)
        {
            // If we are the master, we don't need to request time
            // from the rtpTimeBase.
            if (rtpTimeBase.getMaster() == getController())
                return pts;
            long ts = rtpTimeBase.getNanoseconds();
            // Cannot sync beyond a limit.
            if (ts > pts + RTP_TIME_MARGIN || ts < pts - RTP_TIME_MARGIN)
            {
                /*
                 * System.err.println("pts and mts too different: ts = " +
                 * ts/1000000L + " pts = " + pts/1000000L + " diff = " + (ts -
                 * pts)/1000000L);
                 */
                if (!rtpErrMsg)
                {
                    Log.comment("Cannot perform RTP sync beyond a difference of: "
                            + (ts - pts) / 1000000L + " msecs.\n");
                    rtpErrMsg = true;
                }
                // System.err.println("No RTP Sync: " + (ts - pts)/1000000L +
                // " msecs.\n");
                return pts;
            } else
                return ts;
        } else
            return getMediaNanoseconds();
    }

    /**
     * Handles mid-stream format change.
     */
    private boolean handleFormatChange(Format format)
    {
        // The format is changed mid-stream!
        if (!reinitRenderer(format))
        {
            // Failed.
            storedBuffer = null;
            failed = true;
            if (moduleListener != null)
                moduleListener.formatChangedFailure(this, ic.getFormat(),
                        format);
            return false;
        }

        Format oldFormat = ic.getFormat();
        ic.setFormat(format);
        if (moduleListener != null)
            moduleListener.formatChanged(this, oldFormat, format);

        if (format instanceof VideoFormat)
        {
            float fr = ((VideoFormat) format).getFrameRate();
            if (fr != Format.NOT_SPECIFIED)
                frameRate = fr;
        }

        return true;
    }

    /**
     * Handle the prerolling a buffer. It will preroll until the media has reach
     * the current media time before displaying.
     */
    protected boolean handlePreroll(Buffer buf)
    {
        if (buf.getFormat() instanceof AudioFormat)
        {
            if (!hasReachAudioPrerollTarget(buf))
                return false;

        } else if ((buf.getFlags() & Buffer.FLAG_NO_SYNC) != 0
                || buf.getTimeStamp() < 0)
        {
            // The data is non-time specific.
            // Deliberately empty at this point.

        } else if (buf.getTimeStamp() < getSyncTime(buf.getTimeStamp()))
        {
            // The data is time-specific and it hasn't yet reached the
            // target media time. So we are skipping it.

            // System.err.println("preroll video: " + buf.getTimeStamp());
            return false;
        }

        /*
         * if (buf.getFormat() instanceof AudioFormat)
         * System.err.println("done prerolling audio: " + buf.getLength()); else
         * System.err.println("done prerolling video: " + buf.getLength());
         */

        // The preroll target has been reached.
        prerolling = false;

        return true;
    }

    /**
     * Return true if given the input buffer, the audio will reach the target
     * preroll time -- the current media time.
     */
    private boolean hasReachAudioPrerollTarget(Buffer buf)
    {
        // System.err.println("preroll audio: " + buf.getLength());
        long target = getSyncTime(buf.getTimeStamp());

        elapseTime.update(buf.getLength(), buf.getTimeStamp(), buf.getFormat());

        if (elapseTime.value >= target)
        {
            long remain = ElapseTime.audioTimeToLen(elapseTime.value - target,
                    (AudioFormat) buf.getFormat());
            int offset = buf.getOffset() + buf.getLength() - (int) remain;
            if (offset >= 0)
            {
                buf.setOffset(offset);
                buf.setLength((int) remain);
            }

            elapseTime.setValue(target);

            return true;
        }

        return false;
    }

    @Override
    public boolean isThreaded()
    {
        return true;
    }

    // This is triggered from a connectorPushed.
    // Since this module is running the the safe protocol, so it's not
    // applicable.
    @Override
    protected void process()
    {
    }

    /**
     * Break down one larger buffer into smaller pieces so the processing won't
     * take that long to block.
     */
    public int processBuffer(Buffer buffer)
    {
        int remain = buffer.getLength();
        int offset = buffer.getOffset();
        int len, rc = PlugIn.BUFFER_PROCESSED_OK;
        boolean isEOM = false;

        // Data flow management. If the FLAG_BUF_OVERFLOWN flag
        // is set, we'll try to speed up the renderer to catch up.
        // This is beneficial for streaming media when the server
        // clock is faster than the client clock.
        if (renderer instanceof Clock)
        {
            if ((buffer.getFlags() & Buffer.FLAG_BUF_OVERFLOWN) != 0)
                overflown++;
            else
                overflown--;

            if (overflown > FLOW_LIMIT)
            {
                if (rate < MAX_RATE)
                {
                    rate += RATE_INCR;
                    renderer.stop();
                    ((Clock) renderer).setRate(rate);
                    renderer.start();
                    if (!overMsg)
                    {
                        Log.comment("Data buffers overflown.  Adjust rendering speed up to 5 % to compensate");
                        overMsg = true;
                    }
                }

                overflown = FLOW_LIMIT / 2;

            } else if (overflown <= 0)
            {
                if (rate > 1.0f)
                {
                    rate -= RATE_INCR;
                    renderer.stop();
                    ((Clock) renderer).setRate(rate);
                    renderer.start();
                }

                overflown = FLOW_LIMIT / 2;
            }
        }

        // Each buffer is broken down into smaller chunks for processing
        // as defined by chunkSize.
        //
        // EOM is trickier. We don't want to send multiple EOM's to
        // the renderer for each of the smaller chunks. So we catch
        // it and send it only on the last chunk.
        do
        {
            // Check for the preset stop time. Return if we are done.
            if (stopTime > -1 && elapseTime.value >= stopTime)
            {
                if (prefetching)
                    donePrefetch();
                return PlugIn.INPUT_BUFFER_NOT_CONSUMED;
            }

            // If we are prerolling, there's no need to break the data
            // into smaller chunks for processing.
            if (remain <= chunkSize || prerolling)
            {
                if (isEOM)
                {
                    isEOM = false;
                    buffer.setEOM(true);
                }
                len = remain;
            } else
            {
                if (buffer.isEOM())
                {
                    isEOM = true;
                    buffer.setEOM(false);
                }
                len = chunkSize;
            }

            buffer.setLength(len);
            buffer.setOffset(offset);

            if (prerolling && !handlePreroll(buffer))
            {
                offset += len;
                remain -= len;
                continue;
            }

            try
            {
                rc = renderer.process(buffer);

            } catch (Throwable e)
            {
                Log.dumpStack(e);
                if (moduleListener != null)
                    moduleListener.internalErrorOccurred(this);
            }

            if ((rc & PlugIn.PLUGIN_TERMINATED) != 0)
            {
                failed = true;
                if (moduleListener != null)
                    moduleListener.pluginTerminated(this);
                return rc;
            }

            if ((rc & PlugIn.BUFFER_PROCESSED_FAILED) != 0)
            {
                buffer.setDiscard(true);
                if (prefetching)
                    donePrefetch();
                return rc;

            }

            if ((rc & PlugIn.INPUT_BUFFER_NOT_CONSUMED) != 0)
            {
                // Check what's been processed so far.
                len -= buffer.getLength();
            }

            offset += len;
            remain -= len;

            // If the module is prefetching, we'll need to check to see
            // if the device has been prefetched.
            if (prefetching
                    && (!(renderer instanceof Prefetchable) || ((Prefetchable) renderer)
                            .isPrefetched()))
            {
                // If EOM happens prefetch, disable the EOM.
                // We'll get another EOM from the source module again.
                isEOM = false;
                buffer.setEOM(false);
                donePrefetch();
                break;
            }

            elapseTime.update(len, buffer.getTimeStamp(), buffer.getFormat());

        } while (remain > 0 && !resetted);

        // Re-enable the EOM flag if it were disabled previously.
        if (isEOM)
            buffer.setEOM(true);

        buffer.setLength(remain);
        buffer.setOffset(offset);

        if (rc == PlugIn.BUFFER_PROCESSED_OK)
            framesPlayed++;

        return rc;
    }

    /**
     * Attempt to re-initialize the renderer given a new input format.
     */
    protected boolean reinitRenderer(Format input)
    {
        if (renderer != null)
        {
            if (renderer.setInputFormat(input) != null)
            {
                // Fine, the existing renderer still works.
                return true;
            }
        }

        if (started)
        {
            renderer.stop();
            renderer.reset();
        }

        renderer.close();
        renderer = null;

        Renderer r;
        if ((r = SimpleGraphBuilder.findRenderer(input)) == null)
            return false;

        setRenderer(r);
        if (started)
            renderer.start();

        chunkSize = computeChunkSize(input);

        return true;
    }

    @Override
    public void reset()
    {
        super.reset();
        prefetching = false;
    }

    public void resetFramesPlayed()
    {
        framesPlayed = 0;
    }

    /**
     * Handed a buffer, this function does the scheduling of the buffer
     * processing. It in turn calls processBuffer to do the real processing.
     */
    protected boolean scheduleBuffer(Buffer buf)
    {
        int rc = PlugIn.BUFFER_PROCESSED_OK;

        Format format = buf.getFormat();

        if (format == null)
        {
            // Something's weird, we'll just assume it's the previous
            // format.
            format = ic.getFormat();
            buf.setFormat(format);
        }

        // Handle mid-stream format change.
        if (format != ic.getFormat() && !format.equals(ic.getFormat())
                && !buf.isDiscard())
        {
            // Return if failed.
            if (!handleFormatChange(format))
                return false;
        }

        // Signal the engine if the marker bit is set.
        if ((buf.getFlags() & Buffer.FLAG_SYSTEM_MARKER) != 0
                && moduleListener != null)
        {
            moduleListener.markedDataArrived(this, buf);
            buf.setFlags(buf.getFlags() & ~Buffer.FLAG_SYSTEM_MARKER);
        }

        // Now on to scheduling!
        // Whether to do synchronization or not depends on the following
        // predicate.

        if (prefetching
                || (format instanceof AudioFormat)
                || buf.getTimeStamp() <= 0
                || (buf.getFlags() & Buffer.FLAG_NO_SYNC) == Buffer.FLAG_NO_SYNC
                || noSync)
        {
            // Handle non-scheduled data.
            // Audio is handled here too since the data itself dictates
            // the timing, not the time stamps.
            // It also handles the prefetching cycle since there's no
            // need to wait for presentation time.
            /*
             * if (format instanceof javax.media.format.VideoFormat) {
             * System.err.println("BRM: display on prefetch: " +
             * buf.getSequenceNumber()); }
             */

            if (!buf.isDiscard())
                rc = processBuffer(buf);

        } else
        {
            // Handle scheduled data.
            // Video with a preset presentation timestamp is handled here.

            long mt = getSyncTime(buf.getTimeStamp());

            // Let's bring the #'s back to milli seconds range.
            long lateBy = mt / 1000000L - buf.getTimeStamp() / 1000000L
                    - getLatency() / 1000000L;

            /*
             * System.err.println("VR: PT = " + buf.getTimeStamp()/1000000L +
             * " MT = " + mt/1000000L + " lateBy = " + lateBy);
             */

            // Check the presentation schedule.

            if (storedBuffer == null && lateBy > 0)
            {
                // It's behind schedule.
                // System.err.println("frame behind by: " + lateBy);

                if (buf.isDiscard())
                {
                    // The upstream is telling me to discard this frame.
                    // This means that the upstream has drop a frame.
                    // So when the next frame comes, I'll remember not
                    // to drop a frame again. Otherwise, we'll end up
                    // double-dropping frames.
                    notToDropNext = true;
                    // System.err.println("discard frame");

                } else
                {
                    if (buf.isEOM())
                    {
                        // Don't drop the next (first frame).
                        notToDropNext = true;

                    } else
                    {
                        // Report the frame behind time to the engine.
                        if (moduleListener != null
                                && format instanceof VideoFormat)
                        {
                            float fb = lateBy * frameRate / 1000f;
                            if (fb < 1f)
                                fb = 1f;
                            moduleListener.framesBehind(this, fb, ic);
                            framesWereBehind = true;
                            // System.err.println("frames behind = " + fb);
                        }
                    }

                    if ((buf.getFlags() & Buffer.FLAG_NO_DROP) != 0)
                    {
                        // Process the frame if we are not allowed to drop
                        // the frame.

                        rc = processBuffer(buf);

                    } else
                    {
                        // Do not give up too easily. Allow for a few more
                        // provisions before giving up on rendering the frame.

                        if (lateBy < LEEWAY
                                || notToDropNext
                                || (buf.getTimeStamp() - lastRendered) > 1000000000L)
                        {
                            rc = processBuffer(buf);
                            lastRendered = buf.getTimeStamp();
                            notToDropNext = false;
                        } else
                        {
                            // System.err.println("frame dropped");
                        }
                    }
                }

            } else
            {
                // It's either on time or ahead of schedule.

                // System.err.println("VR: PT = " +
                // buf.getTimeStamp() + " MT = " + mt);

                // System.err.println("frame ahead by: " + lateBy);

                // Report "on time" to the engine.
                if (moduleListener != null && framesWereBehind
                        && format instanceof VideoFormat)
                {
                    moduleListener.framesBehind(this, 0f, ic);
                    framesWereBehind = false;
                    // System.err.println("frames behind = 0");
                }

                if (!buf.isDiscard())
                {
                    // Wait if we are ahead of the presentation time
                    // or the NO_WAIT flag is off.
                    if ((buf.getFlags() & Buffer.FLAG_NO_WAIT) == 0)
                        waitForPT(buf.getTimeStamp());

                    if (!resetted)
                    {
                        rc = processBuffer(buf);
                        lastRendered = buf.getTimeStamp();
                    }
                }
            }
        }

        // Check for processing return code.

        if ((rc & PlugIn.BUFFER_PROCESSED_FAILED) != 0)
        {
            storedBuffer = null;
        } else if ((rc & PlugIn.INPUT_BUFFER_NOT_CONSUMED) != 0)
        {
            // Save what's left for the next processing round.
            // Do not return the buf back.
            storedBuffer = buf;
        } else
        {
            // Success.
            storedBuffer = null;
            if (buf.getDuration() >= 0)
                lastDuration = buf.getDuration();
        }

        return true;
    }

    @Override
    public void setFormat(Connector connector, Format format)
    {
        renderer.setInputFormat(format);
        if (format instanceof VideoFormat)
        {
            float fr = ((VideoFormat) format).getFrameRate();
            if (fr != Format.NOT_SPECIFIED)
                frameRate = fr;
        }
    }

    /**
     * Enable prerolling.
     */
    @Override
    public void setPreroll(long wanted, long actual)
    {
        super.setPreroll(wanted, actual);
        elapseTime.setValue(actual);
    }

    protected void setRenderer(Renderer r)
    {
        renderer = r;
        if (renderer instanceof Clock)
            setClock((Clock) renderer);
    }

    @Override
    public void triggerReset()
    {
        if (renderer != null)
            renderer.reset();

        synchronized (prefetchSync)
        {
            prefetching = false;
            // If we are already done with the reset, there's no need
            // to re-start the renderThread. It's needed only if the
            // data is blocked at the renderer when reset was called.
            if (resetted)
                renderThread.start();
        }
    }

    /**
     * If the presentation time has not been reached, this function will wait
     * until that happens.
     */
    private boolean waitForPT(long pt)
    {
        long mt = getSyncTime(pt);
        long aheadBy, lastAheadBy = -1;
        long interval;
        long before, slept;
        int beenHere = 0;

        aheadBy = (pt - mt) / 1000000L;
        if (rate != 1.0f)
            aheadBy = (long) (aheadBy / rate);

        while (aheadBy > systemErr && !resetted)
        {
            if (aheadBy == lastAheadBy)
            {
                // Somehow, time hasn't changed at all since the last
                // time we slept (perhaps no audio samples updated),
                // we'll use a different scheme to compute the interval.

                // We'll compute the regular interval, plus an additional
                // 3 msecs every time we are here until we reach 33 msecs.
                interval = aheadBy + (5 * beenHere);
                if (interval > 33L)
                    interval = 33L;
                else
                    beenHere++;
                // System.err.println("been here: " + beenHere);
            } else
            {
                interval = aheadBy;
                beenHere = 0;
            }

            // Don't sleep more than 1/8 sec.
            // We'll wake up and check time again.
            interval = (interval > 125L ? 125L : interval);

            // System.err.println("mt = " + mt + " pt = " + pt);
            // System.err.println("interval = " + interval);

            before = System.currentTimeMillis();

            // The interval is scheduled at ahead of time by the
            // expected system error.
            interval -= systemErr;

            try
            {
                if (interval > 0)
                    Thread.sleep(interval);
            } catch (InterruptedException e)
            {
            }

            slept = System.currentTimeMillis() - before;

            // Compute the system err: the actual time slept minus the
            // the desired sleep time. Then take the average.
            systemErr = (slept - interval + systemErr) / 2;

            // Rule out some illegal numbers.
            if (systemErr < 0)
                systemErr = 0;
            else if (systemErr > interval)
                systemErr = interval;

            // System.err.println("slept = " + slept + " err = " + systemErr);

            // Check the time again to see if we need to sleep more.
            mt = getSyncTime(pt);

            lastAheadBy = aheadBy;
            aheadBy = (pt - mt) / 1000000L;
            if (rate != 1.0f)
                aheadBy = (long) (aheadBy / rate);

            if (getState() != Controller.Started)
                break;
        }
        return true;
    }
}

// //////////////////////////////
//
// Inner classes not! $$
// //////////////////////////////

class RenderThread extends LoopThread
{
    BasicRendererModule module;

    // public RenderThread() {
    public RenderThread(BasicRendererModule module)
    {
        this.module = module;
        setName(getName() + ": " + module.renderer);
        useVideoPriority();
    }

    @Override
    protected boolean process()
    {
        return module.doProcess();
    }
}
