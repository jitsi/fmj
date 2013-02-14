package net.sf.fmj.filtergraph;

import javax.media.*;

/**
 * Used as a node for the node graph. A node contains a pointer to the plugin,
 * the input and output formats, a pointer pointing to the previous node and a
 * level marker. With the use of the "prev" node pointer, a full "track" of
 * plugins can be represented. It also serves as a cache to store the supported
 * input and output formats. That way, the plugin doesn't have to be queried
 * every time for the supported formats.
 */
public class GraphNode
{
    Class<?> clz;
    public String cname;
    public PlugIn plugin;
    public int type = -1;
    public Format input, output = null;
    Format supportedIns[], supportedOuts[];
    public GraphNode prev;
    public int level;
    public boolean failed = false;
    public boolean custom = false;

    static int ARRAY_INC = 30;

    int attemptedIdx = 0;

    Format attempted[] = null; // An array of input formats attempted for

    public GraphNode(GraphNode gn, Format input, GraphNode prev, int level)
    {
        this.cname = gn.cname;
        this.plugin = gn.plugin;
        this.type = gn.type;
        this.custom = gn.custom;
        this.input = input;
        this.prev = prev;
        this.level = level;
        this.supportedIns = gn.supportedIns;
        if (gn.input == input)
            supportedOuts = gn.supportedOuts;
    }

    public GraphNode(PlugIn plugin, Format input, GraphNode prev, int level)
    {
        this((plugin == null ? null : plugin.getClass().getName()), plugin,
                input, prev, level);
    }

    public GraphNode(String cname, PlugIn plugin, Format input, GraphNode prev,
            int level)
    {
        this.cname = cname;
        this.plugin = plugin;
        this.input = input;
        this.prev = prev;
        this.level = level;
    }

    // this plugin. This is for pruning the
    // the visited paths.
    public boolean checkAttempted(Format input)
    {
        if (attempted == null)
        {
            attempted = new Format[ARRAY_INC];
            attempted[attemptedIdx++] = input;
            return false;
        }
        int j;
        for (j = 0; j < attemptedIdx; j++)
        {
            if (input.equals(attempted[j]))
                return true;
        }
        // The given input format has not been attempted for
        // this plugin, we'll add that in.
        if (attemptedIdx >= attempted.length)
        {
            // Expand the array.
            Format newarray[] = new Format[attempted.length + ARRAY_INC];
            System.arraycopy(attempted, 0, newarray, 0, attempted.length);
            attempted = newarray;
        }
        attempted[attemptedIdx++] = input;
        return false;
    }

    public Format[] getSupportedInputs()
    {
        if (supportedIns != null)
            return supportedIns;
        else if (plugin == null)
            return null;
        else if ((type == -1 || type == PlugInManager.CODEC)
                && plugin instanceof Codec)
            supportedIns = ((Codec) plugin).getSupportedInputFormats();
        else if ((type == -1 || type == PlugInManager.RENDERER)
                && plugin instanceof Renderer)
            supportedIns = ((Renderer) plugin).getSupportedInputFormats();
        else if (plugin instanceof Multiplexer)
            supportedIns = ((Multiplexer) plugin).getSupportedInputFormats();
        return supportedIns;
    }

    public Format[] getSupportedOutputs(Format in)
    {
        if (in == input && supportedOuts != null)
            return supportedOuts;
        else if (plugin == null)
            return null;
        else if ((type == -1 || type == PlugInManager.RENDERER)
                && plugin instanceof Renderer)
            return null;
        else if ((type == -1 || type == PlugInManager.CODEC)
                && plugin instanceof Codec)
        {
            Format outs[];
            outs = ((Codec) plugin).getSupportedOutputFormats(in);
            if (input == in /* && supportedOuts == null */)
                supportedOuts = outs;
            return outs;
        }
        return null;
    }

    public void resetAttempted()
    {
        attemptedIdx = 0;
        attempted = null;
    }

}
