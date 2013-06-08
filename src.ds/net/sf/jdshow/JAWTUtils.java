package net.sf.jdshow;

import java.awt.*;

/**
 * Get a window handle for a particular window. Adapted from
 * http://www.javaworld.com/javaworld/javatips/jw-javatip86.html.
 *
 * @author Ken Larson
 *
 */

public final class JAWTUtils
{
    // static
    // {
    // System.loadLibrary("jdshow");
    // }

    public static native long getWindowHandle(Canvas canvas);

    private JAWTUtils()
    {
        super();
    }

}
