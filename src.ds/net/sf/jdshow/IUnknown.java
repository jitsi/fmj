package net.sf.jdshow;

/**
 *
 * @author Ken Larson
 *
 */
public class IUnknown extends Peered
{
    public IUnknown(long ptr)
    {
        super(ptr);
    }

    public native int QueryInterface(GUID guid, long[] p);

    public native long Release();

}
