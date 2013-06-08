package net.sf.fmj.filtergraph;

import javax.media.*;

/**
 * This interface defines the notification callbacks from the media engine
 * during the data flow graph building process. The return value from some of
 * the methods can intervene and affect the graph building process.
 */
public interface GraphInspector
{
    /**
     * If this method returns true, the media engine will notify the inspector
     * more frequently with more details.
     */
    public boolean detailMode();

    /**
     * This method is notified when the engine has selected the given codec and
     * has successfully attempted to set the given input and output formats on
     * it. If this method returns false, the engine will reject the given codec
     * for use in the final flow graph.
     *
     * @param codec
     *            the selected codec.
     * @param input
     *            the selected input format to the codec.
     * @param output
     *            the selected output format to the codec.
     * @return false will cause the engine to reject the given codec for use in
     *         the final flow graph.
     */
    public boolean verify(Codec codec, Format input, Format output);

    /**
     * This method is notified when the engine has selected the given
     * multiplexer and has successfully attempted to set the given input formats
     * on it. If this method returns false, the engine will reject the given
     * multiplexer for use in the final flow graph.
     *
     * @param mux the selected multiplexer.
     * @param inputs the selected input formats to the multiplexer.
     * @return <tt>false</tt> will cause the engine to reject the given
     * multiplexer for use in the final flow graph.
     */
    public boolean verify(Multiplexer mux, Format inputs[]);

    /**
     * This method is notified when the engine has selected the given renderer
     * and has successfully attempted to set the given input format on it. If
     * this method returns false, the engine will reject the given renderer for
     * use in the final flow graph.
     *
     * @param renderer
     *            the selected renderer.
     * @param input
     *            the selected input format to the renderer.
     * @return false will cause the engine to reject the given renderer for use
     *         in the final flow graph.
     */
    public boolean verify(Renderer renderer, Format input);

    /**
     * This method is notified if the engine has attempted and failed to set the
     * given input format on the given plugin.
     *
     * @param plugin
     *            the selected plugin.
     * @param input
     *            the selected input format.
     */
    public void verifyInputFailed(PlugIn plugin, Format input);

    /**
     * This method is notified if the engine has attempted and failed to set the
     * given output format on the given plugin.
     *
     * @param plugin
     *            the selected plugin.
     * @param output
     *            the selected output format.
     */
    public void verifyOutputFailed(PlugIn plugin, Format output);
}
