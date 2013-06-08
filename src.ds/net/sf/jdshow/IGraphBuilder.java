package net.sf.jdshow;

/**
 *
 * @author Ken Larson
 *
 */
public class IGraphBuilder extends IFilterGraph
{
    static native GUID Init_IID(GUID guid);

    public IGraphBuilder(long ptr)
    {
        super(ptr);

    }

    public native int RenderFile(String file, String playlist);
}
