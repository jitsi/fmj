package net.sf.fmj.utility;

import java.lang.reflect.*;

/**
 * Helper class to do certain JMF-specific things. Reflection is used to avoid a
 * dependency on JMF.
 *
 * @author Ken Larson
 *
 */
public class JmfUtility
{
    /** Enables JMF logging. Has no effect for FMJ. */
    public static boolean enableLogging()
    {
        try
        {
            // com.sun.media.util.Registry.set("allowLogging", true);

            final Class<?> clazz = Class.forName("com.sun.media.util.Registry");
            final Method m = clazz.getMethod("set", String.class, Object.class);
            m.invoke(null, "allowLogging", true);

            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

}
