package net.sf.jdshow;

/**
 * Helper to set up windowed rendering.
 *
 * @author Ken Larson
 *
 */
public class WindowedRendering
{
    /**
     * Pure native.
     */
    public static native int InitWindowedRendering(long hwndApp, // Window to
                                                                 // hold the
                                                                 // video.
            long /* IGraphBuilder* */pGraph // Pointer to the Filter Graph
                                            // Manager.
    );

    public static int InitWindowedRendering2(long hwndApp, // Window to hold the
                                                           // video.
            IGraphBuilder /* IGraphBuilder* */pGraph // Pointer to the Filter
                                                     // Graph Manager.
    )
    {
        int hr;
        final long[] p = new long[1];

        hr = pGraph.QueryInterface(Com.IID_IVideoWindow, p);
        if (Com.FAILED(hr))
            return hr;

        final IVideoWindow videoWindow = new IVideoWindow(p[0]);

        hr = videoWindow.put_Owner(hwndApp);
        if (Com.FAILED(hr))
            return hr;

        hr = videoWindow.put_WindowStyle(Com.WS_CHILD | Com.WS_CLIPSIBLINGS);
        if (Com.FAILED(hr))
            return hr;

        hr = videoWindow.put_MessageDrain(hwndApp);
        if (Com.FAILED(hr))
            return hr;

        hr = videoWindow.put_Top(0);
        if (Com.FAILED(hr))
            return hr;

        hr = videoWindow.put_Left(0);
        if (Com.FAILED(hr))
            return hr;

        videoWindow.Release();

        return Com.S_OK;
    }
}
