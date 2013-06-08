package javax.media;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

import net.sf.fmj.utility.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/CaptureDeviceManager.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 *
 */
public class CaptureDeviceManager
{
    // It looks like Sun's implementation might delegate this (via reflection)
    // to javax.media.cdm.CaptureDeviceManager, which has synchronized methods.
    // We'll do the same, to ensure maximum compatibility, although the reasons
    // for this technique are not clear.
    // I suspect that they did this to make it easier to ship
    // "performance packs" for different operating systems, which might
    // have different implementations for these various managers.

    // TODO: make this class have the same signature as Sun's.

    private static final Logger logger = LoggerSingleton.logger;

    private static Class<?> implClass;
    private static Method getDeviceMethod;
    private static Method getDeviceListMethod;
    private static Method addDeviceMethod;
    private static Method removeDeviceMethod;
    private static Method commitMethod;

    public static boolean addDevice(CaptureDeviceInfo newDevice)
    {
        if (!init())
            return false;
        return ((Boolean) callImpl(addDeviceMethod, new Object[] { newDevice }))
                .booleanValue();

    }

    private static Object callImpl(Method method, Object[] args)
    {
        try
        {
            return method.invoke(null, args);
        } catch (IllegalArgumentException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            return null;
        } catch (IllegalAccessException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            return null;
        } catch (InvocationTargetException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            return null;
        }
    }

    public static void commit() throws IOException
    {
        if (!init())
            return;
        final Method method = commitMethod;
        final Object[] args = new Object[] {};

        // do this one explicitly so we can catch and re-throw IOException.

        try
        {
            method.invoke(null, args);
        } catch (IllegalArgumentException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            return;
        } catch (IllegalAccessException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            return;
        } catch (InvocationTargetException e)
        {
            if (e.getCause() instanceof IOException)
                throw (IOException) e.getCause();

            logger.log(Level.WARNING, "" + e, e);
            return;
        }

    }

    public static CaptureDeviceInfo getDevice(String deviceName)
    {
        if (!init())
            return null;
        return (CaptureDeviceInfo) callImpl(getDeviceMethod,
                new Object[] { deviceName });
    }

    public static Vector getDeviceList(Format format)
    {
        if (!init())
            return null;
        return (Vector) callImpl(getDeviceListMethod, new Object[] { format });
    }

    private static Method getStaticMethodOnImplClass(String name,
            Class<?>[] args, Class<?> returnType) throws Exception
    {
        final Method m = implClass.getMethod(name, args);
        if (m.getReturnType() != returnType)
            throw new Exception("Expected return type of method " + name
                    + " to be " + returnType + ", was " + m.getReturnType());
        if (!Modifier.isStatic(m.getModifiers()))
            throw new Exception("Expected method " + name + " to be static");
        return m;
    }

    private static synchronized boolean init()
    {
        if (implClass != null)
            return true; // already initialized;

        try
        {
            implClass = Class.forName("javax.media.cdm.CaptureDeviceManager");
            if (!(CaptureDeviceManager.class.isAssignableFrom(implClass)))
                throw new Exception(
                        "javax.media.cdm.CaptureDeviceManager not subclass of "
                                + CaptureDeviceManager.class.getName());
            getDeviceMethod = getStaticMethodOnImplClass("getDevice",
                    new Class[] { String.class }, CaptureDeviceInfo.class);
            getDeviceListMethod = getStaticMethodOnImplClass("getDeviceList",
                    new Class[] { Format.class }, Vector.class);
            addDeviceMethod = getStaticMethodOnImplClass("addDevice",
                    new Class[] { CaptureDeviceInfo.class }, boolean.class);
            removeDeviceMethod = getStaticMethodOnImplClass("removeDevice",
                    new Class[] { CaptureDeviceInfo.class }, boolean.class);
            commitMethod = getStaticMethodOnImplClass("commit", new Class[] {},
                    void.class);

        } catch (Exception e)
        {
            implClass = null;

            logger.log(Level.WARNING, "" + e, e);
            return false;
        }

        return true;
    }

    public static boolean removeDevice(CaptureDeviceInfo device)
    {
        if (!init())
            return false;
        return ((Boolean) callImpl(removeDeviceMethod, new Object[] { device }))
                .booleanValue();

    }

    public CaptureDeviceManager()
    {
    }
}
