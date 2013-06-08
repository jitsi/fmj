package net.sf.jdshow;

/**
 *
 * @author Ken Larson
 *
 */
public class IVideoWindow extends IUnknown
{
    public IVideoWindow(long ptr)
    {
        super(ptr);

    }

    public native int get_Height(long[] pHeight);

    public native int get_Width(long[] pWidth);

    public native int put_Left(int value);

    public native int put_MessageDrain(long hwnd);

    public native int put_Owner(long hwnd);

    public native int put_Top(int value);

    public native int put_WindowStyle(int value);

}
