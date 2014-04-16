package net.sf.fmj.filtergraph;

import java.util.*;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.*;

/**
 *
 * This is the Graph builder to generate the data flow graph for rendering an
 * input format.
 *
 * It contains 3 parts: 1) Routines to search for all the supported output
 * formats; 2) Routines to build a default flow graph -- <tt>buildGraph</tt>;
 *
 * A default graph is such that no customised option is specified on the
 * <tt>TrackControl</tt>.
 *
 * It operates on a breath-first search algorithm until the final target is
 * reached as defined by the <tt>findTarget()</tt> method. Intermediate search
 * paths are stored as <tt>GraphNode</tt>s in the "candidates" vector.
 */
public class SimpleGraphBuilder
{
    static public PlugIn createPlugIn(String name, int type)
    {
        Class<?> cls;
        Object obj;

        try
        {
            // cls = Class.forName(name);
            cls = BasicPlugIn.getClassForName(name);
            obj = cls.newInstance();
        } catch (Exception e)
        {
            // Log.write("Cannot instantiate: " + name);
            return null;
        } catch (Error e)
        {
            return null;
        }

        if (verifyClass(obj, type))
            return (PlugIn) obj;
        // Log.write(name + " is not of type " + cls);
        return null;
    }

    /**
     * Find a codec that can handle the given input and output. The output
     * argument can be null if no specific output format is required.
     */
    static public Codec findCodec(Format in, Format out, Format selectedIn[],
            Format selectedOut[])
    {
        Vector cnames = PlugInManager.getPlugInList(in, out,
                PlugInManager.CODEC);
        if (cnames == null)
        {
            // Well no codec supports that input. :(
            return null;
        }

        Codec c = null;
        Format fmts[], matched;
        for (int i = 0; i < cnames.size(); i++)
        {
            if ((c = (Codec) createPlugIn((String) cnames.elementAt(i),
                    PlugInManager.CODEC)) == null)
                continue;
            fmts = c.getSupportedInputFormats();
            if ((matched = matches(in, fmts, null, c)) == null)
                continue;
            if (selectedIn != null && selectedIn.length > 0)
                selectedIn[0] = matched;
            fmts = c.getSupportedOutputFormats(matched);
            if (fmts == null || fmts.length == 0)
            {
                // Weird!
                continue;
            }
            boolean success = false;
            for (int j = 0; j < fmts.length; j++)
            {
                // Try out the supported output formats in turn.
                if (out != null)
                {
                    if (!out.matches(fmts[j])
                            || (matched = out.intersects(fmts[j])) == null)
                        continue;
                } else
                    matched = fmts[j];
                if (c.setOutputFormat(matched) != null)
                {
                    success = true;
                    break;
                }
            }
            if (success)
            {
                try
                {
                    c.open();
                } catch (ResourceUnavailableException e)
                {
                }
                if (selectedOut != null && selectedOut.length > 0)
                    selectedOut[0] = matched;
                // Alright, we are done!
                return c;
            }
        }

        return null;
    }

    /**
     * Find a renderer that can handle the given input and output. The output
     * argument can be null if no specific output format is required.
     */
    static public Renderer findRenderer(Format in)
    {
        Vector names = PlugInManager.getPlugInList(in, null,
                PlugInManager.RENDERER);
        if (names == null)
        {
            // Well no renderer supports that input. :(
            return null;
        }

        Renderer r = null;
        Format fmts[], matched;
        for (int i = 0; i < names.size(); i++)
        {
            if ((r = (Renderer) createPlugIn((String) names.elementAt(i),
                    PlugInManager.RENDERER)) == null)
                continue;
            fmts = r.getSupportedInputFormats();
            if ((matched = matches(in, fmts, null, r)) == null)
                continue;

            try
            {
                r.open();
            } catch (ResourceUnavailableException e)
            {
            }

            // Alright, we are done!
            return r;
        }

        return null;
    }

    /**
     * Return a chain of codecs and renderer to render to input format. Unlike
     * findCodec and findRenderer, it uses the same graph building algorithm
     * that the media engine uses to determine the best rendering path for a
     * particular input format. The return value is a vector of plugins of all
     * the codecs and the renderer. The plugin list is in reverse order starting
     * from the renderer. The list of the corresponding input formats for each
     * codec is also returned as an argument to the function.
     */
    static public Vector findRenderingChain(Format in, Vector formats)
    {
        SimpleGraphBuilder gb = new SimpleGraphBuilder();
        GraphNode n;

        if ((n = gb.buildGraph(in)) == null)
            return null;

        Vector list = new Vector(10);

        while (n != null && n.plugin != null)
        {
            list.addElement(n.plugin);
            if (formats != null)
                formats.addElement(n.input);
            n = n.prev;
        }

        return list;
    }

    /**
     * Given a codec class name, instantiate the codec and query it dynamically
     * to see if it supports the given input and output formats.
     */
    static public GraphNode getPlugInNode(String name, int type,
            Map<String, GraphNode> plugIns)
    {
        GraphNode gn = null;

        // Check the hash registry to see if we've already instantiated that
        // object. If not, we'll instantiate it.
        if (plugIns == null || (gn = plugIns.get(name)) == null)
        {
            PlugIn p = createPlugIn(name, type);

            gn = new GraphNode(name, p, null, null, 0);
            if (plugIns != null)
                plugIns.put(name, gn);

            if (p == null)
            {
                // If we failed to create it this time, we won't try it again.
                // We'll mark it as failed.
                gn.failed = true;
                return null;
            } else
                return gn;
        }

        // If it has been marked as failed before, we won't attempt
        // to use it again.
        if (gn.failed)
            return null;

        if (verifyClass(gn.plugin, type))
            return gn;

        return null;
    }

    /**
     * Choose a format among the two input arrays that matches and verify that
     * if the given upstream and downstream plugins accept the matched format as
     * output (for the upstream) or as input (for the downstream). Either of the
     * plugin arguments can be null. In which case the verification step will be
     * skipped accordingly.
     *
     * @param outs the supported output formats from the upstream node.
     * @param ins the supported input formats from the downstream node.
     * @param up the upstream node.
     * @param down the downstream node.
     * @return a matching format.
     */
    static public Format matches(Format outs[], Format ins[], PlugIn up,
            PlugIn down)
    {
        Format fmt;
        if (outs == null)
            return null;
        for (int i = 0; i < outs.length; i++)
        {
            if ((fmt = matches(outs[i], ins, up, down)) != null)
                return fmt;
        }
        return null;
    }

    /**
     * Choose a format among the two input arrays that matches and verify that
     * if the given upstream and downstream plugins accept the matched format as
     * output (for the upstream) or as input (for the downstream). Either of the
     * plugin arguments can be null. In which case the verification step will be
     * skipped accordingly.
     *
     * @return a matching format.
     */
    static public Format matches(Format out, Format ins[], PlugIn up,
            PlugIn down)
    {
        if (out == null || ins == null)
            return null;
        for (int i = 0; i < ins.length; i++)
        {
            if (ins[i] != null
                    && ins[i].getClass().isAssignableFrom(out.getClass())
                    && out.matches(ins[i]))
            {
                Format fmt = out.intersects(ins[i]);

                if (fmt == null)
                    // weird!
                    continue;

                // Check if the downstream accepts the given input.
                if (down != null && (fmt = verifyInput(down, fmt)) == null)
                    continue;

                // Check if the upstream accepts the given as output.
                Format refined = fmt;
                if (up != null && (refined = verifyOutput(up, fmt)) == null)
                    continue;

                // If the returned output format from the upstream is
                // different from the original input to the upstream,
                // we'll have to check that new format on the downstream
                // to make sure.
                if (down != null && refined != fmt
                        && verifyInput(down, refined) == null)
                    continue;

                return refined;
            }
        }
        return null;
    }

    static public Format matches(Format outs[], Format in, PlugIn up,
            PlugIn down)
    {
        Format ins[] = new Format[1];
        ins[0] = in;
        return matches(outs, ins, up, down);
    }

    static public boolean verifyClass(Object obj, int type)
    {
        Class<?> cls;

        switch (type)
        {
        case PlugInManager.CODEC:
            cls = Codec.class;
            break;
        case PlugInManager.RENDERER:
            cls = Renderer.class;
            break;
        case PlugInManager.MULTIPLEXER:
            cls = Multiplexer.class;
            break;
        default:
            cls = PlugIn.class;
        }

        if (cls.isInstance(obj))
            return true;
        else
            return false;
    }

    /**
     * Check if the given plugin supports the given input.
     */
    static public Format verifyInput(PlugIn p, Format in)
    {
        if (p instanceof Codec)
            return ((Codec) p).setInputFormat(in);
        if (p instanceof Renderer)
            return ((Renderer) p).setInputFormat(in);
        return null;
    }

    /**
     * Check if the given plugin supports the given output.
     */
    static public Format verifyOutput(PlugIn p, Format out)
    {
        if (p instanceof Codec)
            return ((Codec) p).setOutputFormat(out);
        return null;
    }

    // # of codec/converters allowed to use to complete a track.
    // damencho from 4 to 5
    protected int STAGES = 5;

    protected Hashtable<String,GraphNode> plugIns
        = new Hashtable<String,GraphNode>(40);

    protected GraphNode targetPlugins[] = null;

    protected Vector targetPluginNames = null;

    protected int targetType = -1;

    protected int indent = 0;

    // A non-published interface to trace the graph building process.
    static public GraphInspector inspector;

    static public void setGraphInspector(GraphInspector insp)
    {
        inspector = insp;
    }

    /**
     * Take a TrackControl and build the graph for it.
     */
    public boolean buildGraph(BasicTrackControl tc)
    {
        Log.comment("Input: " + tc.getOriginalFormat());

        Vector candidates = new Vector();
        GraphNode node = new GraphNode(null, (PlugIn) null,
                tc.getOriginalFormat(), null, 0);
        indent = 1;
        Log.setIndent(indent);

        // Define the final targets.
        if (!setDefaultTargets(tc.getOriginalFormat()))
            return false;

        candidates.addElement(node);

        GraphNode failed;

        while ((node = buildGraph(candidates)) != null)
        {
            // Found a potential graph. Check if we can build a
            // track from it.
            if ((failed = buildTrackFromGraph(tc, node)) == null)
            {
                // we are done.
                indent = 0;
                Log.setIndent(indent);
                return true;
            }

            // If we can't build a track from it, it's because there's
            // a node in the graph that cannot be opened. We'll have
            // to reap it from the candidates and the registry.
            removeFailure(candidates, failed, tc.getOriginalFormat());
        }

        indent = 0;
        Log.setIndent(indent);
        return false;
    }

    /**
     * Build a flow graph based on the given input format.
     */
    GraphNode buildGraph(Format input)
    {
        Log.comment("Input: " + input);

        Vector candidates = new Vector();
        GraphNode node = new GraphNode(null, (PlugIn) null, input, null, 0);
        indent = 1;
        Log.setIndent(indent);

        // Define the final targets.
        if (!setDefaultTargets(input))
            return null;

        candidates.addElement(node);

        GraphNode failed;

        while ((node = buildGraph(candidates)) != null)
        {
            // Found a potential graph. Verify it if all the
            // nodes can be used.
            if ((failed = verifyGraph(node)) == null)
            {
                // we are done.
                indent = 0;
                Log.setIndent(indent);
                return node;
            }

            // If we can't build a track from it, it's because there's
            // a node in the graph that cannot be opened. We'll have
            // to reap it from the candidates and the registry.
            removeFailure(candidates, failed, input);
        }

        indent = 0;
        Log.setIndent(indent);

        return node;
    }

    /**
     * Given the intermediate search candidates, build a graph until it reaches
     * a target.
     */
    protected GraphNode buildGraph(Vector candidates)
    {
        GraphNode node;
        while ((node = doBuildGraph(candidates)) == null)
        {
            if (candidates.isEmpty())
                break;
        }
        return node;
    }

    /**
     * When the graph build finds a viable graph to build, this callback will be
     * invoked to see if the graph can actually be built. Subclass should
     * implement this.
     */
    protected GraphNode buildTrackFromGraph(BasicTrackControl tc, GraphNode node)
    {
        return null;
    }

    /**
     * This is the "worker" method that does all the dirty work.
     */
    GraphNode doBuildGraph(Vector candidates)
    {
        if (candidates.isEmpty())
            return null;

        GraphNode node = (GraphNode) candidates.firstElement();
        candidates.removeElementAt(0);

        if (node.input == null
                && (node.plugin == null || !(node.plugin instanceof Codec)))
        {
            // shouldn't happen!
            Log.error("Internal error: doBuildGraph");
            return null;
        }

        int oldIndent = indent;

        Log.setIndent(node.level + 1);

//        Log.write("level: " + node.level);
        if (node.plugin != null)
        {
            // It may not seem necessary to do this since the previous round has
            // already verified the input. But since the same plugin could have
            // a different input called on it on previous rounds, it needs to be
            // reset to the designated input. This has caused a bug in failing
            // setOutputFormat for some codecs.
            if (verifyInput(node.plugin, node.input) == null)
                return null;
//            Log.write("Try plugin: " + node.plugin.getClass());
        } else
        {
//            Log.write("Given input: " + node.input);
        }

        // Stop when the target is reached as defined by the findTarget
        // method.
        GraphNode n;
        if ((n = findTarget(node)) != null)
        {
            // We are done!
//            Log.write(
//                    "Found target: "
//                        + ((n.plugin != null) ? n.plugin : n.cname));
            indent = oldIndent;
            Log.setIndent(indent);
            return n;
        }

        // Don't go deeper than allowed.
        if (node.level >= STAGES)
        {
            indent = oldIndent;
            Log.setIndent(indent);
            return null;
        }

        Format input, outs[];
        boolean mp3Pkt = false; // 2.1.1b hack -ivg

        if (node.plugin != null)
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
//                    Log.write("Weird!  The given plugin does not support any output.");
                    indent = oldIndent;
                    Log.setIndent(indent);
                    return null;
                }
            }
            input = node.input;

            // 2.1.1b hack -ivg
//            if (node.plugin instanceof com.sun.media.codec.audio.mpa.Packetizer)
//                mp3Pkt = true;

        } else
        {
            outs = new Format[1];
            outs[0] = node.input;
            input = null;
        }

        GraphNode gn;
        Format fmt, ins[];
        boolean foundSomething = false;
        for (int i = 0; i < outs.length; i++)
        {
            // Ignore outputs that are the same as the input.
            if (!node.custom && input != null && input.equals(outs[i]))
                continue;

            // Verify the output format.
            if (node.plugin != null)
            {
                if (verifyOutput(node.plugin, outs[i]) == null)
                {
//                    Log.write("Verify output failed: " + node.plugin);
//                    Log.write("  with: " + outs[i]);
                    if (inspector != null && inspector.detailMode())
                        inspector.verifyOutputFailed(node.plugin, outs[i]);
                    continue;
                }

                if (inspector != null
                        && !inspector.verify((Codec) node.plugin, node.input,
                                outs[i]))
                    continue;
            }

//            Log.write("find codec for input: " + outs[i]);

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

                // 2.1.1b hack -ivg
                // if (mp3Pkt && gn.plugin instanceof
                // com.sun.media.codec.audio.mpa.DePacketizer)
                // continue;

                // Check to see if the particular input/plugin combination
                // has already been attempted. If so, we don't need to
                // do it again.
                if (gn.checkAttempted(outs[i]))
                    continue;

//                Log.write("Try codec: " + cnames.elementAt(j));

                ins = gn.getSupportedInputs();
                if ((fmt = matches(outs[i], ins, null, gn.plugin)) == null)
                {
//                    Log.write("Verify input failed: " + outs[i]);
//                    Log.write("    : " + gn.plugin);
                    if (inspector != null && inspector.detailMode())
                        inspector.verifyInputFailed(gn.plugin, outs[i]);
                    continue;
                }

                if (inspector != null && inspector.detailMode())
                {
                    if (!inspector.verify((Codec) gn.plugin, fmt, null))
                        continue;
                }

                n = new GraphNode(gn, fmt, node, node.level + 1);
                candidates.addElement(n);
                foundSomething = true;
            }
        }

//        if (!foundSomething)
//        {
//            if (node.plugin == null)
//                Log.write("  no codec supports the given input.");
//            else
//                Log.write("  no codec supports the outputs from this plugin.");
//        }

        indent = oldIndent;
        Log.setIndent(indent);
        return null;
    }

    /**
     * This defines when the search ends. The "targets" array defines the nodes
     * that are to be the "end points" (leaf nodes) of the graph. With the
     * default graph builder, the targets array contains the list of sinks that
     * can potentially support the input format.
     */
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

        GraphNode n;

        // Check for the list of predefined targets.
        if (targetPlugins != null
                && (n = verifyTargetPlugins(node, outs)) != null)
            return n;

        return null;
    }

    /**
     * Given a node that has failed, this function will eliminate it from the
     * candiates list and mark it in the registry as as failed node so it won't
     * be attempted again. This method is upated by hsy on 10/10/2000. If we
     * just remove the failed node from candidates and mark it in the registy,
     * we can't guarantee we could roll back to the exact/desired node/state to
     * re-start searching, since this node might have already been removed from
     * candidates. The simple/straightforward fix is to clear up candidates and
     * plugIns and start allover again.
     */
    void removeFailure(Vector candidates, GraphNode failed, Format input)
    {
        if (failed.plugin == null)
            return;

        // This is the new implementation updated by hsy on 10/10/2000
        // Here we re-start building the graph allover again. Clear
        // candidates and put the initial node into it.
        Log.comment("Failed to open plugin " + failed.plugin
                + ". Will re-build the graph allover again");
        candidates.removeAllElements();
        GraphNode hsyn = new GraphNode(null, (PlugIn) null, input, null, 0);
        indent = 1;
        Log.setIndent(indent);

        candidates.addElement(hsyn);

        // clear up the hashtable plugIns too, only let it keep all the failed
        // nodes, so that we are not going to attempt these nodes again.
        failed.failed = true;
        plugIns.put(failed.plugin.getClass().getName(), failed);

        Enumeration<String> e = plugIns.keys();
        while (e.hasMoreElements())
        {
            String ss = e.nextElement();
            GraphNode nn = plugIns.get(ss);
            if (!nn.failed)
                plugIns.remove(ss);
        }
    }

    /**
     * Reset local cache and reuse the same instance for graph building.
     */
    public void reset()
    {
        // damencho change enum keyword to compile with 1.5
        Enumeration<GraphNode> enum1 = plugIns.elements();
        GraphNode n;
        while (enum1.hasMoreElements())
        {
            n = enum1.nextElement();
            n.resetAttempted();
        }
    }

    protected boolean setDefaultTargetRenderer(Format in)
    {
        // Define the final targets which uses renderers.
        if (in instanceof AudioFormat)
        {
            targetPluginNames = PlugInManager.getPlugInList(new AudioFormat(
                    null, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
                    Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
                    Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
                    Format.NOT_SPECIFIED, null), null, PlugInManager.RENDERER);
        } else if (in instanceof VideoFormat)
        {
            targetPluginNames = PlugInManager.getPlugInList(new VideoFormat(
                    null, null, Format.NOT_SPECIFIED, null,
                    Format.NOT_SPECIFIED // frameRate ???
                    ), null, PlugInManager.RENDERER);
        } else
        {
            targetPluginNames = PlugInManager.getPlugInList(null, null,
                    PlugInManager.RENDERER);
        }

        // No target available.
        if (targetPluginNames == null || targetPluginNames.size() == 0)
        {
            // Log.write("The graph builder does not recognize the input format at all:");
            // Log.write(in.toString());
            return false;
        }

        targetPlugins = new GraphNode[targetPluginNames.size()];
        targetType = PlugInManager.RENDERER;

        return true;
    }

    /**
     * Set the default targets, which are the renderers.
     */
    protected boolean setDefaultTargets(Format in)
    {
        return setDefaultTargetRenderer(in);
    }

    /**
     * Given a protential graph, verify it.
     */
    protected GraphNode verifyGraph(GraphNode node)
    {
        Format prevFormat = null;
        Vector used = new Vector(5);

        if (node.plugin == null)
        {
            // There's nothing to build.
            // i.e. the output from the source (demux) works just fine.
            // Probably just need to be multiplexed.
            return null;
        }

        Log.setIndent(indent++);

        // Build the graph from the last node.
        while (node != null && node.plugin != null)
        {
            if (used.contains(node.plugin))
            {
                // That plugin has already been used in the same path,
                // we'll need to instantiate another one of its kind.
                PlugIn p;
                if (node.cname == null
                        || (p = createPlugIn(node.cname, -1)) == null)
                {
                    Log.write("Failed to instantiate " + node.cname);
                    return node;
                }
                node.plugin = p;
            } else
            {
                used.addElement(node.plugin);
            }

            if ((node.type == -1 || node.type == PlugInManager.RENDERER)
                    && node.plugin instanceof Renderer)
            {
                ((Renderer) node.plugin).setInputFormat(node.input);
            } else if ((node.type == -1 || node.type == PlugInManager.CODEC)
                    && node.plugin instanceof Codec)
            {
                ((Codec) node.plugin).setInputFormat(node.input);
                if (prevFormat != null)
                    ((Codec) node.plugin).setOutputFormat(prevFormat);
                else if (node.output != null)
                    ((Codec) node.plugin).setOutputFormat(node.output);
            }

            // For renderers, we wait till prefetching to
            // open the device.

            if (!((node.type == -1 || node.type == PlugInManager.RENDERER) && node.plugin instanceof Renderer))
            {
                try
                {
                    node.plugin.open();
                } catch (Exception e)
                {
                    Log.warning("Failed to open: " + node.plugin);
                    node.failed = true;
                    return node;
                }
            }

            prevFormat = node.input;
            node = node.prev;
        }

        Log.setIndent(indent--);

        return null;
    }

    /**
     * Check for a match in the list of predefined targets.
     */
    protected GraphNode verifyTargetPlugins(GraphNode node, Format outs[])
    {
        GraphNode gn;
        Format fmt;

        for (int i = 0; i < targetPlugins.length; i++)
        {
            if ((gn = targetPlugins[i]) == null)
            {
                String name = (String) targetPluginNames.elementAt(i);
                if (name == null)
                    continue;

                // Initial screening before instantiating the objects.
                Format base[] = PlugInManager.getSupportedInputFormats(name,
                        targetType);
                if (matches(outs, base, null, null) == null)
                    continue;

                // Passing initial test, we'll want to instantiate it
                // to get more info from it.
                if ((gn = getPlugInNode(name, targetType, plugIns)) == null)
                {
                    targetPluginNames.setElementAt(null, i);
                    continue;
                }

                targetPlugins[i] = gn;
            }

            if ((fmt = matches(outs, gn.getSupportedInputs(), node.plugin,
                    gn.plugin)) != null)
            {
                // found the target.

                if (inspector != null)
                {
                    if (node.plugin != null
                            && !inspector.verify((Codec) node.plugin,
                                    node.input, fmt))
                        continue;
                    if ((gn.type == -1 || gn.type == PlugInManager.CODEC)
                            && gn.plugin instanceof Codec)
                    {
                        if (!inspector.verify((Codec) gn.plugin, fmt, null))
                            continue;
                    } else if ((gn.type == -1 || gn.type == PlugInManager.RENDERER)
                            && gn.plugin instanceof Renderer)
                    {
                        if (!inspector.verify((Renderer) gn.plugin, fmt))
                            continue;
                    }
                }

                return new GraphNode(gn, fmt, node, node.level + 1);
            }
        }

        return null;
    }
}
