package net.sf.fmj.media;

import java.awt.*;
import java.util.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.filtergraph.*;
import net.sf.fmj.media.codec.video.colorspace.*;
import net.sf.fmj.media.control.*;
import net.sf.fmj.media.util.*;

/**
 * ProcessEngine implements the media engine for processors.
 */
public class ProcessEngine extends PlaybackEngine
{
    class ProcGraphBuilder extends SimpleGraphBuilder
    {
        protected ProcessEngine engine;

        protected Format targetFormat;
        protected int trackID = 0;
        protected int numTracks = 1;
        protected int nodesVisited = 0;

        /******************************************
         * 
         * Routines for building custom graphs
         * 
         ******************************************/

        Codec codecs[] = null;

        Renderer rend = null;

        Format format = null;

        ProcGraphBuilder(ProcessEngine engine)
        {
            this.engine = engine;
        }

        /**
         * Overrides GraphBuider's buildGraph method. The major difference is,
         * it pay attentions to the effects, codecs, renderers specified.
         */
        GraphNode buildCustomGraph(Format in)
        {
            Vector candidates = new Vector();
            GraphNode node, n = null;
            Format fmt, fmts[];

            // root.
            node = new GraphNode(null, (PlugIn) null, in, null, 0);
            candidates.addElement(node);

            Log.comment("Custom options specified.");
            indent = 1;
            Log.setIndent(indent);

            // Handle custom codec chain.
            if (codecs != null)
            {
                resetTargets();

                for (int i = 0; i < codecs.length; i++)
                {
                    if (codecs[i] == null)
                        continue;

                    Log.comment("A custom codec is specified: " + codecs[i]);

                    // Set the custom target to be the next codec specified.
                    setTargetPlugin(codecs[i], PlugInManager.CODEC);

                    if ((node = buildGraph(candidates)) == null)
                    {
                        Log.error("The input format is not compatible with the given codec plugin: "
                                + codecs[i]);
                        indent = 0;
                        Log.setIndent(indent);
                        return null;
                    }
                    node.level = 0;
                    candidates = new Vector();
                    candidates.addElement(node);
                }
            }

            if (outputContentDes != null)
            {
                resetTargets();

                // Set the target format.
                if (format != null)
                {
                    targetFormat = format;
                    Log.comment("An output format is specified: " + format);
                }

                // A mux is specified.
                if (!setDefaultTargetMux())
                    return null;

                if ((node = buildGraph(candidates)) == null)
                {
                    Log.error("Failed to build a graph for the given custom options.");
                    indent = 0;
                    Log.setIndent(indent);
                    return null;
                }

            } else
            {
                if (format != null)
                {
                    // A target format is set. First find a route to
                    // to transcode to the target format.

                    resetTargets();

                    targetFormat = format;
                    Log.comment("An output format is specified: " + format);

                    if ((node = buildGraph(candidates)) == null)
                    {
                        Log.error("The input format cannot be transcoded to the specified target format.");
                        indent = 0;
                        Log.setIndent(indent);
                        return null;
                    }
                    node.level = 0;
                    candidates = new Vector();
                    candidates.addElement(node);
                    targetFormat = null;
                }

                // Connect the rest of the graph to a renderer.

                if (rend != null)
                {
                    // Handle custom renderer.
                    Log.comment("A custom renderer is specified: " + rend);

                    // Set the custom target to be the renderer specified.
                    setTargetPlugin(rend, PlugInManager.RENDERER);

                    if ((node = buildGraph(candidates)) == null)
                    {
                        if (format != null)
                            Log.error("The customed transocoded format is not compatible with the given renderer plugin: "
                                    + rend);
                        else
                            Log.error("The input format is not compatible with the given renderer plugin: "
                                    + rend);
                        indent = 0;
                        Log.setIndent(indent);
                        return null;
                    }

                } else
                {
                    // Handle the default renderers.
                    if (!setDefaultTargetRenderer(format == null ? in : format))
                        return null;

                    if ((node = buildGraph(candidates)) == null)
                    {
                        if (format != null)
                            Log.error("Failed to find a renderer that supports the customed transcoded format.");
                        else
                            Log.error("Failed to build a graph to render the input format with the given custom options.");
                        indent = 0;
                        Log.setIndent(indent);
                        return null;
                    }
                }
            }

            indent = 0;
            Log.setIndent(indent);
            return node;
        }

        boolean buildCustomGraph(ProcTControl tc)
        {
            this.codecs = tc.codecChainWanted;
            this.rend = tc.rendererWanted;
            this.format = tc.formatWanted;

            if (format instanceof VideoFormat
                    && tc.getOriginalFormat() instanceof VideoFormat)
            {
                Dimension s1 = ((VideoFormat) tc.getOriginalFormat()).getSize();
                Dimension s2 = ((VideoFormat) format).getSize();
                if (s1 != null && s2 != null && !s1.equals(s2))
                {
                    // The video needs to be resized.
                    // We'll instantiate the video scaler then
                    // insert it into the flow graph.
                    RGBScaler scaler = new RGBScaler(s2);

                    if (codecs == null || codecs.length == 0)
                    {
                        codecs = new Codec[1];
                        codecs[0] = scaler;
                    } else
                    {
                        // There are some custom codecs specified.
                        // We'll use some simple heuristics to determine
                        // where to insert the scaler.
                        codecs = new Codec[tc.codecChainWanted.length + 1];
                        int i;
                        if (!isRawVideo(format))
                        {
                            // The destination format is not a raw format.
                            // we'll insert the scaler at the front.
                            codecs[0] = scaler;
                            i = 1;
                        } else
                        {
                            codecs[tc.codecChainWanted.length] = scaler;
                            i = 0;
                        }

                        for (int j = 0; j < tc.codecChainWanted.length; j++)
                            codecs[i++] = tc.codecChainWanted[j];
                    }
                }
            }

            GraphNode node, failed;

            return ((node = buildCustomGraph(tc.getOriginalFormat())) != null)
                    && ((failed = buildTrackFromGraph(tc, node)) == null);
        }

        boolean buildGraph(BasicTrackControl tc, int trackID, int numTracks)
        {
            this.trackID = trackID;
            this.numTracks = numTracks;

            // If the custom options are specified, we'll use the
            // different routine that's specialized for this.
            if (tc.isCustomized())
            {
                Log.comment("Input: " + tc.getOriginalFormat());
                return buildCustomGraph((ProcTControl) tc);
            }

            return super.buildGraph(tc);
        }

        @Override
        protected GraphNode buildTrackFromGraph(BasicTrackControl tc,
                GraphNode node)
        {
            return engine.buildTrackFromGraph(tc, node);
        }

        /**
         * Collect the supported output formats from the list of node
         * candidates.
         */
        void doGetSupportedOutputFormats(Vector candidates, Vector collected)
        {
            GraphNode node = (GraphNode) candidates.firstElement();
            candidates.removeElementAt(0);

            if (node.input == null
                    && (node.plugin == null || !(node.plugin instanceof Codec)))
            {
                // shouldn't happen!
                Log.error("Internal error: doGetSupportedOutputFormats");
                return;
            }

            if (node.plugin != null)
            {
                // It may not seem necessary to do this since the
                // previous round has already verified the input.
                // But since the same plugin could have a different
                // input called on it on previous rounds, it needs to
                // be resetted to the designated input. This has
                // caused a bug in failing setOutputFormat for some
                // codecs.
                if (verifyInput(node.plugin, node.input) == null)
                    return;
            }

            Format input, outs[];
            if (node.plugin != null)
            {
                outs = node.getSupportedOutputs(node.input);
                if (outs == null || outs.length == 0)
                    return;

                // Add the output formats to the collected list and
                // check for duplication.
                boolean found;
                int j, k, size;
                Format other;
                for (j = 0; j < outs.length; j++)
                {
                    size = collected.size();
                    found = false;

                    for (k = 0; k < size; k++)
                    {
                        other = (Format) collected.elementAt(k);
                        if (other == outs[j] || other.equals(outs[j]))
                        {
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                        collected.addElement(outs[j]);
                }
                input = node.input;
            } else
            {
                outs = new Format[1];
                outs[0] = node.input;
                input = null;
            }

            // Don't go deeper than allowed.
            if (node.level >= STAGES)
                return;

            GraphNode gn, n;
            Format fmt, ins[];
            for (int i = 0; i < outs.length; i++)
            {
                // Ignore outputs that are the same as the input.
                if (input != null && input.equals(outs[i]))
                    continue;

                // Verify the output format.
                if (node.plugin != null
                        && verifyOutput(node.plugin, outs[i]) == null)
                {
                    continue;
                }

                Vector cnames = PlugInManager.getPlugInList(outs[i], null,
                        PlugInManager.CODEC);
                if (cnames == null || cnames.size() == 0)
                    continue;

                for (int j = 0; j < cnames.size(); j++)
                {
                    // Instantiate and verify the codec.
                    if ((gn = getPlugInNode((String) cnames.elementAt(j),
                            PlugInManager.CODEC, plugIns)) == null)
                        continue;

                    // Check to see if the particular input/plugin combination
                    // has already been attempted. If so, we don't need to
                    // do it again.
                    if (gn.checkAttempted(outs[i]))
                        continue;

                    ins = gn.getSupportedInputs();
                    if ((fmt = matches(outs[i], ins, null, gn.plugin)) == null)
                        continue;
                    n = new GraphNode(gn, fmt, node, node.level + 1);
                    candidates.addElement(n);
                    nodesVisited++;
                }
            }
        }

        /**
         * This defines when the search ends. The "targets" array defines the
         * nodes that are to be the "end points" (leaf nodes) of the graph. With
         * the default graph builder, the targets array contains the list of
         * sinks that can potentially support the input format.
         */
        @Override
        protected GraphNode findTarget(GraphNode node)
        {
            Format outs[];

            // Expand the outputs of the next node.
            if (node.plugin == null)
            {
                outs = new Format[1];
                outs[0] = node.input;
            } else
            {
                if (node.output != null)
                {
                    outs = new Format[1];
                    outs[0] = node.output;
                } else
                {
                    outs = node.getSupportedOutputs(node.input);
                    if (outs == null || outs.length == 0)
                    {
                        // Log.write("Weird!  The given plugin does not support any output.");
                        return null;
                    }
                }
            }

            // If there's a constraint format, check for that first.
            if (targetFormat != null)
            {
                Format matched = null;

                if ((matched = matches(outs, targetFormat, node.plugin, null)) == null)
                    return null;

                if (inspector != null
                        && !inspector.verify((Codec) node.plugin, node.input,
                                matched))
                    return null;

                // If there's no more targets to match, we are done.
                if (targetPlugins == null && targetMuxes == null)
                {
                    node.output = matched;
                    return node;
                }

                // The matching target format is chosen.
                outs = new Format[1];
                outs[0] = matched;
            }

            GraphNode n;

            // Check for the list of predefined targets.
            if (targetPlugins != null)
            {
                if ((n = verifyTargetPlugins(node, outs)) != null)
                    return n;
                else
                    return null;
            }

            // Check for the list of predefined muxes.
            if (targetMuxes != null
                    && (n = verifyTargetMuxes(node, outs)) != null)
                return n;

            return null;
        }

        /**
         * Given an input format, find out all the supported output formats by
         * searching through the node graph.
         */
        public Format[] getSupportedOutputFormats(Format input)
        {
            long formatsTime = System.currentTimeMillis();
            Vector collected = new Vector();
            Vector candidates = new Vector();

            GraphNode node = new GraphNode(null, (PlugIn) null, input, null, 0);
            candidates.addElement(node);
            collected.addElement(input);
            nodesVisited++;

            while (!candidates.isEmpty())
                doGetSupportedOutputFormats(candidates, collected);

            // Convert the resulting vector into an array.
            Format all[] = new Format[collected.size()];

            // The following bit of code is a hack to put MPEG/RTP the
            // the end of the supported list since it's the least robust
            // RTP codecs.
            int front = 0, back = all.length - 1;
            Format mpegAudio = new AudioFormat(AudioFormat.MPEG_RTP);
            boolean mpegInput = (new AudioFormat(AudioFormat.MPEG))
                    .matches(input)
                    || (new AudioFormat(AudioFormat.MPEGLAYER3)).matches(input)
                    || (new VideoFormat(VideoFormat.MPEG)).matches(input);

            for (int i = 0; i < all.length; i++)
            {
                Object obj = collected.elementAt(i);
                if (!mpegInput && mpegAudio.matches((Format) obj))
                    all[back--] = (Format) obj;
                else
                    all[front++] = (Format) obj;
            }

            Log.comment("Getting the supported output formats for:");
            Log.comment("  " + input);
            Log.comment("  # of nodes visited: " + nodesVisited);
            Log.comment("  # of formats supported: " + all.length + "\n");
            PlaybackEngine.profile("getSupportedOutputFormats", formatsTime);

            return all;
        }

        @Override
        public void reset()
        {
            super.reset();
            resetTargets();
        }

        /**
         * Reset all the targets.
         */
        void resetTargets()
        {
            targetFormat = null;
            targetPlugins = null;
        }

        boolean setDefaultTargetMux()
        {
            // If the target muxes are already defined, we don't need
            // to do that again.
            if (targetMuxes != null)
                return true;

            Log.comment("An output content type is specified: "
                    + outputContentDes);

            targetMuxNames = PlugInManager.getPlugInList(null,
                    outputContentDes, PlugInManager.MULTIPLEXER);

            if (targetMuxNames == null || targetMuxNames.size() == 0)
            {
                Log.error("No multiplexer is found for that content type: "
                        + outputContentDes);
                return false;
            }

            targetMuxes = new GraphNode[targetMuxNames.size()];
            targetMux = null;
            targetMuxFormats = new Format[numTracks];

            // The regular targets will not be used.
            targetPluginNames = null;
            targetPlugins = null;
            return true;
        }

        @Override
        protected boolean setDefaultTargetRenderer(Format in)
        {
            if (!super.setDefaultTargetRenderer(in))
                return false;
            targetMuxes = null;

            return true;
        }

        /**
         * Set the default targets, which are the renderers.
         */
        @Override
        protected boolean setDefaultTargets(Format in)
        {
            // If there's an output content descriptor specified,
            // The targets will be a list of multiplexers.
            if (outputContentDes != null)
                return setDefaultTargetMux();
            else
                return setDefaultTargetRenderer(in);
        }

        /**
         * Set the target to a custom plugin specified.
         */
        void setTargetPlugin(PlugIn p, int type)
        {
            targetPlugins = new GraphNode[1];
            targetPlugins[0] = new GraphNode(p, null, null, 0);
            targetPlugins[0].custom = true;
            targetPlugins[0].type = type;
        }

        /**
         * If a multiplexer is specified, check for that.
         */
        GraphNode verifyTargetMuxes(GraphNode node, Format outs[])
        {
            Multiplexer mux;
            GraphNode gn;
            Format fmt;

            for (int i = 0; i < targetMuxes.length; i++)
            {
                if ((gn = targetMuxes[i]) == null)
                {
                    String name = (String) targetMuxNames.elementAt(i);

                    if (name == null)
                        continue;

                    // We'll want to instantiate it to get more info from it.
                    if ((gn = getPlugInNode(name, PlugInManager.MULTIPLEXER,
                            plugIns)) == null)
                    {
                        targetMuxNames.setElementAt(null, i);
                        continue;
                    }

                    mux = (Multiplexer) gn.plugin;

                    if (mux.setContentDescriptor(outputContentDes) == null)
                    {
                        targetMuxNames.setElementAt(null, i);
                        continue;
                    }

                    if (mux.setNumTracks(numTracks) != numTracks)
                    {
                        targetMuxNames.setElementAt(null, i);
                        continue;
                    }

                    targetMuxes[i] = gn;
                }

                if (targetMux != null && gn != targetMux)
                    continue;

                for (int j = 0; j < outs.length; j++)
                {
                    if ((fmt = ((Multiplexer) gn.plugin).setInputFormat(
                            outs[j], trackID)) == null)
                        continue;

                    // found the target.

                    if (inspector != null)
                    {
                        if (node.plugin != null
                                && !inspector.verify((Codec) node.plugin,
                                        node.input, fmt))
                            continue;
                    }

                    targetMux = gn;
                    targetMuxFormats[trackID] = fmt;
                    node.output = fmt;

                    return node;
                }
            }

            return null;
        }

    }

    class ProcTControl extends BasicTrackControl implements Owned
    {
        // Customized options.
        protected Format formatWanted = null;
        protected Codec codecChainWanted[] = null;
        protected Renderer rendererWanted = null;
        protected ProcGraphBuilder gb;

        protected Format supportedFormats[] = null;

        public ProcTControl(ProcessEngine engine, Track track,
                OutputConnector oc)
        {
            super(engine, track, oc);
        }

        /**
         * Top level routine to build a single track.
         */
        @Override
        public boolean buildTrack(int trackID, int numTracks)
        {
            if (gb == null)
                gb = new ProcGraphBuilder((ProcessEngine) engine);
            else
                gb.reset();
            boolean rtn = gb.buildGraph(this, trackID, numTracks);

            // dispose the old GraphBuilder after building a track.
            // The cache is not valid anymore.
            gb = null;

            return rtn;
        }

        /**
         * Check if the video size of the given format is valid. If not, return
         * a format with the correct size.
         */
        private Format checkSize(Format fmt)
        {
            if (!(fmt instanceof VideoFormat))
                return fmt;

            VideoFormat vfmt = (VideoFormat) fmt;
            Dimension size = ((VideoFormat) fmt).getSize();

            if (size == null)
            {
                Format ofmt = getOriginalFormat();
                if (ofmt == null
                        || (size = ((VideoFormat) ofmt).getSize()) == null)
                    return fmt;
            }

            int w = size.width, h = size.height;

            if (fmt.matches(new VideoFormat(VideoFormat.JPEG_RTP))
                    || fmt.matches(new VideoFormat(VideoFormat.JPEG)))
            {
                // JPEG sizes should be a multiple of 8.
                if (size.width % 8 != 0)
                    w = size.width / 8 * 8;
                if (size.height % 8 != 0)
                    h = size.height / 8 * 8;
                if (w == 0 || h == 0)
                {
                    w = size.width;
                    h = size.height;
                }

            } else if (fmt.matches(new VideoFormat(VideoFormat.H263_RTP))
                    || fmt.matches(new VideoFormat(VideoFormat.H263_1998_RTP))
                    || fmt.matches(new VideoFormat(VideoFormat.H263)))
            {
                // H.263 sizes are pretty rigid.
                if (size.width >= 352)
                {
                    w = 352;
                    h = 288;
                } else if (size.width >= 160)
                {
                    w = 176;
                    h = 144;
                } else
                {
                    w = 128;
                    h = 96;
                }
            }

            if (w != size.width || h != size.height)
            {
                Log.comment("setFormat: " + fmt.getEncoding()
                        + ": video aspect ratio mismatched.");
                Log.comment("  Scaled from " + size.width + "x" + size.height
                        + " to " + w + "x" + h + ".\n");
                fmt = (new VideoFormat(null, new Dimension(w, h),
                        Format.NOT_SPECIFIED, null, Format.NOT_SPECIFIED))
                        .intersects(fmt);
            }

            return fmt;
        }

        @Override
        protected FrameRateControl frameRateControl()
        {
            this.muxModule = getMuxModule();
            return frameRateControl;
        }

        @Override
        public Format getFormat()
        {
            return (formatWanted == null ? track.getFormat() : formatWanted);
        }

        public Object getOwner()
        {
            return player;
        }

        @Override
        public Format[] getSupportedFormats()
        {
            // First check to see if we have already computed the supported
            // formats for the track format in the past. If not, then we'll
            // compute the supported formats using the graph builder.

            if (supportedFormats == null
                    && (supportedFormats = Resource.getDB(track.getFormat())) == null)
            {
                if (gb == null)
                    gb = new ProcGraphBuilder((ProcessEngine) engine);
                else
                    gb.reset();
                supportedFormats = gb.getSupportedOutputFormats(track
                        .getFormat());
                supportedFormats = Resource.putDB(track.getFormat(),
                        supportedFormats);
                needSavingDB = true;
            }

            // If an output content descriptor is given, we'll need to
            // verify if the mux support individual inputs.
            if (outputContentDes != null)
            {
                return verifyMuxInputs(outputContentDes, supportedFormats);
            } else
                return supportedFormats;
        }

        @Override
        public boolean isCustomized()
        {
            return formatWanted != null || codecChainWanted != null
                    || rendererWanted != null;
        }

        /**
         * Returns true if this track holds the master time base.
         */
        @Override
        public boolean isTimeBase()
        {
            for (int j = 0; j < modules.size(); j++)
            {
                if (modules.elementAt(j) == masterSink)
                    return true;
            }
            return false;
        }

        @Override
        public void prError()
        {
            if (!isCustomized())
            {
                super.prError();
                return;
            }

            Log.error("  Cannot build a flow graph with the customized options:");
            if (formatWanted != null)
            {
                Log.error("    Unable to transcode format: "
                        + getOriginalFormat());
                Log.error("      to: " + getFormat());
                if (outputContentDes != null)
                    Log.error("      outputting to: " + outputContentDes);
            }
            if (codecChainWanted != null)
            {
                Log.error("    Unable to add customed codecs: ");
                for (int i = 0; i < codecChainWanted.length; i++)
                    Log.error("      " + codecChainWanted[i]);
            }
            if (rendererWanted != null)
            {
                Log.error("    Unable to add customed renderer: "
                        + rendererWanted);
            }
            Log.write("\n");
        }

        @Override
        protected ProgressControl progressControl()
        {
            return progressControl;
        }

        @Override
        public void setCodecChain(Codec codec[]) throws NotConfiguredError,
                UnsupportedPlugInException
        {
            if (engine.getState() > Configured)
                throwError(new NotConfiguredError(connectErr));
            if (codec.length < 1)
                throw new UnsupportedPlugInException(
                        "No codec specified in the array.");
            codecChainWanted = new Codec[codec.length];
            for (int i = 0; i < codec.length; i++)
                codecChainWanted[i] = codec[i];
        }

        @Override
        public Format setFormat(Format format)
        {
            if (engine.getState() > Configured)
                return getFormat();

            /*
             * Force a new size for testing. if (format instanceof VideoFormat)
             * { VideoFormat newf = new VideoFormat(null, new Dimension(100,
             * 100), Format.NOT_SPECIFIED, null, Format.NOT_SPECIFIED); format =
             * newf.intersects(format); }
             */

            if (format != null && !format.matches(track.getFormat()))
            {
                formatWanted = checkSize(format);
            } else
                return format;

            /*
             * Format fmts[] = getSupportedFormats(); boolean good = false; for
             * (int i = 0; i < fmts.length; i++) { if
             * (formatWanted.matches(fmts[i])) {
             * System.err.println("It's a good format"); good = true; } } if
             * (!good) { System.err.println("Format set: " + formatWanted);
             * System.err.println("It's a bad format"); }
             */

            return formatWanted;
        }

        @Override
        public void setRenderer(Renderer renderer) throws NotConfiguredError
        {
            if (engine.getState() > Configured)
                throwError(new NotConfiguredError(connectErr));
            this.rendererWanted = renderer;
            if (renderer instanceof SlowPlugIn)
                ((SlowPlugIn) renderer).forceToUse();
        }

        /**
         * If a multiplexer (ContentDescriptor) is specified, we'll verify if
         * the multiplexer supports the given input.
         */
        Format[] verifyMuxInputs(ContentDescriptor cd, Format inputs[])
        {
            if (cd == null || cd.getEncoding() == ContentDescriptor.RAW)
                return inputs;

            // Instantiate all the multiplexers that support the
            // given output content descriptor.

            Vector cnames = PlugInManager.getPlugInList(null, cd,
                    PlugInManager.MULTIPLEXER);
            if (cnames == null || cnames.size() == 0)
                return new Format[0];

            Multiplexer mux[] = new Multiplexer[cnames.size()];
            int total = 0;

            Multiplexer m;
            for (int i = 0; i < cnames.size(); i++)
            {
                if ((m = (Multiplexer) SimpleGraphBuilder
                        .createPlugIn((String) cnames.elementAt(i),
                                PlugInManager.MULTIPLEXER)) != null)
                {
                    try
                    {
                        m.setContentDescriptor(outputContentDes);
                    } catch (Exception e)
                    {
                        continue;
                    }

                    if (m.setNumTracks(1) < 1)
                        continue;

                    mux[total++] = m;
                }
            }

            // Query the multiplexers to see if they support the input
            // format.

            Format tmp[] = new Format[inputs.length];
            Format fmt;
            int vtotal = 0;

            for (int i = 0; i < inputs.length; i++)
            {
                if (total == 1)
                {
                    // Let's do some loop unrolling.
                    if ((fmt = mux[0].setInputFormat(inputs[i], 0)) != null)
                        tmp[vtotal++] = fmt;
                } else
                {
                    for (int j = 0; j < total; j++)
                    {
                        if ((fmt = mux[j].setInputFormat(inputs[i], 0)) != null)
                        {
                            tmp[vtotal++] = fmt;
                            break;
                        }
                    }
                }
            }

            Format verified[] = new Format[vtotal];
            System.arraycopy(tmp, 0, verified, 0, vtotal);

            return verified;
        }
    }

    protected BasicMuxModule muxModule;

    protected ContentDescriptor outputContentDes = null;

    String prefetchError = "Failed to prefetch: " + this;

    /**
     * This is the Graph builder to generate the data flow graph for the media
     * engine. It extends from the SimpleGraphBuilder to handle multiplexers,
     * customized output formats, codecs and renderers.
     * 
     * It contains 3 parts: 1) Routines to search for all the supported output
     * formats; 2) Routines to build a default flow graph -- buildGraph; 3)
     * Routines to build a custom flow graph -- buildCustomGraph.
     * 
     * A default graph is such that no customised option is specified on the
     * TrackControl.
     * 
     */

    // The list of target multiplexers.
    protected Vector targetMuxNames = null;

    protected GraphNode targetMuxes[] = null;

    protected GraphNode targetMux = null;

    protected Format targetMuxFormats[] = null;

    public ProcessEngine(BasicProcessor p)
    {
        super(p);
    }

    /**
     * Connect the multiplexer.
     */
    boolean connectMux()
    {
        /**
         * The target Mux has already been determined. We'll just hook it up.
         */
        BasicTrackControl tcs[] = new BasicTrackControl[trackControls.length];
        int total = 0;
        Multiplexer mux = (Multiplexer) targetMux.plugin;

        for (int i = 0; i < trackControls.length; i++)
        {
            if (trackControls[i].isEnabled())
            {
                tcs[total++] = trackControls[i];
            }
        }

        try
        {
            mux.setContentDescriptor(outputContentDes);
        } catch (Exception e)
        {
            Log.comment("Failed to set the output content descriptor on the multiplexer.");
            return false;
        }

        boolean failed = false;

        if (mux.setNumTracks(targetMuxFormats.length) != targetMuxFormats.length)
        {
            Log.comment("Failed  to set number of tracks on the multiplexer.");
            return false;
        }

        for (int mf = 0; mf < targetMuxFormats.length; mf++)
        {
            if (targetMuxFormats[mf] == null
                    || mux.setInputFormat(targetMuxFormats[mf], mf) == null)
            {
                Log.comment("Failed to set input format on the multiplexer.");
                failed = true;
                break;
            }
        }

        if (failed)
            return false;

        if (SimpleGraphBuilder.inspector != null
                && !SimpleGraphBuilder.inspector.verify(mux, targetMuxFormats))
            return false;

        // Log.comment("Found multiplexer: " + mux);

        InputConnector ic;
        BasicMuxModule bmm = new BasicMuxModule(mux, targetMuxFormats);

        // Make the connections.
        for (int j = 0; j < targetMuxFormats.length; j++)
        {
            ic = bmm.getInputConnector(BasicMuxModule.ConnectorNamePrefix + j);
            if (ic == null)
            {
                // Something is terribly wrong.
                Log.comment("BasicMuxModule: connector mismatched.");
                return false;
            }
            ic.setFormat(targetMuxFormats[j]);
            tcs[j].lastOC.setProtocol(ic.getProtocol());
            tcs[j].lastOC.connectTo(ic, targetMuxFormats[j]);
        }

        if (!bmm.doRealize())
        {
            // Log.comment("Failed to open the multiplexer.");
            return false;
        }

        bmm.setModuleListener(this);
        bmm.setController(this);
        modules.addElement(bmm);
        sinks.addElement(bmm);

        muxModule = bmm;

        return true;
    }

    /**
     * Configuring the engine.
     */
    @Override
    protected boolean doConfigure()
    {
        if (!doConfigure1())
            return false;

        // The indices to the connector names, tracks, and track controls
        // should all correspond to each other.
        String names[] = source.getOutputConnectorNames();
        trackControls = new BasicTrackControl[tracks.length];
        for (int i = 0; i < tracks.length; i++)
        {
            trackControls[i] = new ProcTControl(this, tracks[i],
                    source.getOutputConnector(names[i]));
        }

        if (!doConfigure2())
            return false;

        // By default a Processor generates RAW output.
        outputContentDes = new ContentDescriptor(ContentDescriptor.RAW);

        // The parser disables the hint tracks by default. We'll
        // re-enable them.
        reenableHintTracks();

        return true;
    }

    /**
     * The stub function to perform the steps to prefetch the controller.
     * 
     * @return true if successful.
     */
    @Override
    protected synchronized boolean doPrefetch()
    {
        if (prefetched)
            return true;

        if (!doPrefetch1())
            return false;

        // Fail if the mux module cannot be prefetched.
        if (muxModule != null && !muxModule.doPrefetch())
        {
            Log.error(prefetchError);
            Log.error("  Cannot prefetch the multiplexer: "
                    + muxModule.getMultiplexer() + "\n");
            return false;
        }

        return doPrefetch2();
    }

    /**
     * @return true if successful.
     */
    @Override
    protected synchronized boolean doRealize()
    {
        // Reset the target multiplexers
        targetMuxes = null;

        if (!super.doRealize1())
            return false;

        // Connect the tracks to a multiplexer, if there's one.
        if (targetMux != null && !connectMux())
        {
            Log.error(realizeError);
            Log.error("  Cannot connect the multiplexer\n");
            player.processError = genericProcessorError;
            return false;
        }

        if (!super.doRealize2())
            return false;

        return true;
    }

    /**
     * Start immediately. Invoked from start(tbt) when the scheduled start time
     * is reached. Use the public start(tbt) method for the public interface.
     * Override this to implement subclass behavior.
     */
    @Override
    protected synchronized void doStart()
    {
        if (started)
            return;

        doStart1();

        if (muxModule != null)
            muxModule.doStart();

        doStart2();
    }

    /**
     * Invoked from stop(). Override this to implement subclass behavior.
     */
    @Override
    protected synchronized void doStop()
    {
        if (!started)
            return;

        doStop1();

        if (muxModule != null)
            muxModule.doStop();

        doStop2();
    }

    /**
     * Search and update the master time base.
     */
    @Override
    protected BasicSinkModule findMasterSink()
    {
        // Obtain a master time base from one of its SinkModules.

        if (muxModule != null && muxModule.getClock() != null)
        {
            return muxModule;
        }

        return super.findMasterSink();
    }

    /**
     * Report the output bit rate if a mux is used.
     */
    @Override
    protected long getBitRate()
    {
        if (muxModule != null)
            return muxModule.getBitsWritten();
        else
            return source.getBitsRead();
    }

    /**
     * Return the output content-type.
     */
    public ContentDescriptor getContentDescriptor() throws NotConfiguredError
    {
        if (getState() < Configured)
            throwError(new NotConfiguredError("getContentDescriptor "
                    + NOT_CONFIGURED_ERROR));
        return outputContentDes;
    }

    /**
     * Return the output DataSource of the Processor.
     */
    public DataSource getDataOutput() throws NotRealizedError
    {
        if (getState() < Controller.Realized)
            throwError(new NotRealizedError("getDataOutput "
                    + NOT_REALIZED_ERROR));
        if (muxModule != null)
            return muxModule.getDataOutput();
        else
            return null;
    }

    BasicMuxModule getMuxModule()
    {
        return muxModule;
    }

    // ////////////////////////////////
    //
    // Flow graph building routines.
    // ////////////////////////////////

    /**
     * Get the plugin from a module. For debugging.
     */
    @Override
    protected PlugIn getPlugIn(BasicModule m)
    {
        if (m instanceof BasicMuxModule)
            return ((BasicMuxModule) m).getMultiplexer();

        return super.getPlugIn(m);
    }

    // ////////////////////////////////
    //
    // Inner classes
    // ////////////////////////////////

    /**
     * Return all the content-types which this Processor's output supports.
     */
    public ContentDescriptor[] getSupportedContentDescriptors()
            throws NotConfiguredError
    {
        if (getState() < Configured)
            throwError(new NotConfiguredError("getSupportedContentDescriptors "
                    + NOT_CONFIGURED_ERROR));
        Vector names = PlugInManager.getPlugInList(null, null,
                PlugInManager.MULTIPLEXER);
        Vector fmts = new Vector();
        Format fs[];
        int i, j, k;
        boolean duplicate;

        for (i = 0; i < names.size(); i++)
        {
            fs = PlugInManager.getSupportedOutputFormats(
                    (String) names.elementAt(i), PlugInManager.MULTIPLEXER);
            if (fs == null)
                continue;
            for (j = 0; j < fs.length; j++)
            {
                if (!(fs[j] instanceof ContentDescriptor))
                    continue;
                duplicate = false;
                for (k = 0; k < fmts.size(); k++)
                {
                    if (fmts.elementAt(k).equals(fs[j]))
                    {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate)
                    fmts.addElement(fs[j]);
            }
        }

        ContentDescriptor cds[] = new ContentDescriptor[fmts.size()];

        for (i = 0; i < fmts.size(); i++)
            cds[i] = (ContentDescriptor) fmts.elementAt(i);

        return cds;
    }

    /**
     * Get the track controls.
     */
    public TrackControl[] getTrackControls() throws NotConfiguredError
    {
        if (getState() < Configured)
            throwError(new NotConfiguredError("getTrackControls "
                    + NOT_CONFIGURED_ERROR));
        return trackControls;
    }

    /**
     * Return true if the given format is RTP related.
     */
    boolean isRTPFormat(Format fmt)
    {
        return fmt != null && fmt.getEncoding() != null
                && fmt.getEncoding().endsWith("rtp")
                || fmt.getEncoding().endsWith("RTP");
    }

    void reenableHintTracks()
    {
        for (int i = 0; i < trackControls.length; i++)
        {
            if (isRTPFormat(trackControls[i].getOriginalFormat()))
            {
                trackControls[i].setEnabled(true);
                break;
            }
        }
    }

    @Override
    protected void resetBitRate()
    {
        if (muxModule != null)
            muxModule.resetBitsWritten();
        else
            source.resetBitsRead();
    }

    /**
     * Set the output content-type.
     */
    public ContentDescriptor setContentDescriptor(ContentDescriptor ocd)
            throws NotConfiguredError
    {
        if (getState() < Configured)
            throwError(new NotConfiguredError("setContentDescriptor "
                    + NOT_CONFIGURED_ERROR));

        if (getState() > Configured)
            return null;

        if (ocd != null)
        {
            Vector cnames = PlugInManager.getPlugInList(null, ocd,
                    PlugInManager.MULTIPLEXER);

            if (cnames == null || cnames.size() == 0)
                return null;
        }

        outputContentDes = ocd;

        return outputContentDes;
    }
}
