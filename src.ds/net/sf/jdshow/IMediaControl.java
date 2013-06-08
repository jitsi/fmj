package net.sf.jdshow;

/**
 *
 * @author Ken Larson
 *
 */
public class IMediaControl extends IDispatch
{
    public IMediaControl(long ptr)
    {
        super(ptr);

    }

    public native int Run();

    public native int Stop();

}
