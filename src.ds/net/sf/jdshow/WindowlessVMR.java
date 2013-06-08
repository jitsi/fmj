package net.sf.jdshow;

/**
 * Helper for Windowless VMR
 *
 * @author Ken Larson
 * @deprecated Not working yet.
 */
@Deprecated
public class WindowlessVMR
{
    public static native int InitWindowlessVMR(long hwndApp, // Window to hold
                                                             // the video.
            long /* IGraphBuilder* */pGraph, // Pointer to the Filter Graph
                                             // Manager.
            long[]/* IVMRWindowlessControl** */ppWc); // Receives a pointer to
                                                      // the VMR.
}
