package javax.media;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

import net.sf.fmj.utility.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/PlugInManager.html"
 * target="_blank">this class in the JMF Javadoc</a>.
 *
 * @author Ken Larson
 *
 */
public class PlugInManager
{
    // This class is expected to route all calls (using reflection) to
    // javax.media.pim.PlugInManager, see the JavaDoc.
    // We'll do the same, to ensure maximum compatibility, although the reasons
    // for this technique are not clear.
    // I suspect that they did this to make it easier to ship
    // "performance packs" for different operating systems, which might
    // have different implementations for these various managers.

    private static final Logger logger = LoggerSingleton.logger;

    public static final int DEMULTIPLEXER = 1;

    public static final int CODEC = 2;

    public static final int EFFECT = 3;

    public static final int RENDERER = 4;

    public static final int MULTIPLEXER = 5;

    private static Class<?> implClass;
    private static Method getPlugInListMethod;
    private static Method setPlugInListMethod;
    private static Method commitMethod;
    private static Method addPlugInMethod;
    private static Method removePlugInMethod;
    private static Method getSupportedInputFormatsMethod;
    private static Method getSupportedOutputFormatsMethod;

    public static boolean addPlugIn(String classname, Format[] in,
            Format[] out, int type)
    {
        if (!init())
            return false;
        return ((Boolean) callImpl(addPlugInMethod, new Object[] { classname,
                in, out, Integer.valueOf(type) })).booleanValue();

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

    public static void commit() throws java.io.IOException
    {
        if (!init())
            return;
        callImpl(commitMethod, new Object[] {});
    }

    public static Vector getPlugInList(Format input, Format output, int type)
    {
        if (!init())
            return null;
        return (Vector) callImpl(getPlugInListMethod, new Object[] { input,
                output, Integer.valueOf(type) });
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

    public static Format[] getSupportedInputFormats(String className, int type)
    {
        if (!init())
            return null;
        return (Format[]) callImpl(getSupportedInputFormatsMethod,
                new Object[] { className, Integer.valueOf(type) });
    }

    public static Format[] getSupportedOutputFormats(String className, int type)
    {
        if (!init())
            return null;
        return (Format[]) callImpl(getSupportedOutputFormatsMethod,
                new Object[] { className, Integer.valueOf(type) });

    }

    private static synchronized boolean init()
    {
        if (implClass != null)
            return true; // already initialized;

        try
        {
            implClass = Class.forName("javax.media.pim.PlugInManager");
            if (!(PlugInManager.class.isAssignableFrom(implClass)))
                throw new Exception(
                        "javax.media.pim.PlugInManager not subclass of "
                                + PlugInManager.class.getName());
            getPlugInListMethod = getStaticMethodOnImplClass("getPlugInList",
                    new Class[] { Format.class, Format.class, int.class },
                    Vector.class);
            setPlugInListMethod = getStaticMethodOnImplClass("setPlugInList",
                    new Class[] { Vector.class, int.class }, void.class);
            commitMethod = getStaticMethodOnImplClass("commit", new Class[] {},
                    void.class);
            addPlugInMethod = getStaticMethodOnImplClass("addPlugIn",
                    new Class[] { String.class, Format[].class, Format[].class,
                            int.class }, boolean.class);
            removePlugInMethod = getStaticMethodOnImplClass("removePlugIn",
                    new Class[] { String.class, int.class }, boolean.class);
            getSupportedInputFormatsMethod = getStaticMethodOnImplClass(
                    "getSupportedInputFormats", new Class[] { String.class,
                            int.class }, Format[].class);
            getSupportedOutputFormatsMethod = getStaticMethodOnImplClass(
                    "getSupportedOutputFormats", new Class[] { String.class,
                            int.class }, Format[].class);

        } catch (Throwable e)
        {
            implClass = null;

            logger.log(Level.SEVERE,
                    "Unable to initialize javax.media.pim.PlugInManager: " + e,
                    e);
            return false;
        }

        return true;
    }

    public static boolean removePlugIn(String classname, int type)
    {
        if (!init())
            return false;
        return ((Boolean) callImpl(removePlugInMethod, new Object[] {
                classname, Integer.valueOf(type) })).booleanValue();
    }

    public static void setPlugInList(Vector plugins, int type)
    {
        if (!init())
            return;
        callImpl(setPlugInListMethod,
                new Object[] { plugins, Integer.valueOf(type) });
    }

    public PlugInManager()
    {
    }
}
