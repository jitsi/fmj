package javax.media;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

import net.sf.fmj.utility.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/PackageManager.html"
 * target="_blank">this class in the JMF Javadoc</a>. This class is used to
 * locate DataSource and MediaHandler classes. For example, if "org.foo" is in
 * the protocolPrefixList, when searching for a DataSource for a particular
 * protocol "fooproto", it will look for
 * org.foo.media.protocol.fooproto.DataSource. For example, if "org.foo" is in
 * the contentPrefixList, when searching for a MediaHandler for a particular
 * content "video.foovid", it will look for
 * org.foo.media.content.video.foovid.Handler. This class does not actually do
 * the searching for these classes, it only provides the list. The prefixes are
 * to be searched in order until a suitable class is found.
 *
 * Coding complete.
 *
 * @author Ken Larson
 *
 */
public class PackageManager
{
    // Sun's implementation delegates this (via reflection) to another class
    // javax.media.pm.PackageManager, which has synchronized methods.
    // We'll do the same, to ensure maximum compatibility, although the reasons
    // for this technique are not clear.
    // I suspect that they did this to make it easier to ship
    // "performance packs" for different operating systems, which might
    // have different implementations for these various managers.

    // TODO: make this class have the same signature as Sun's.

    private static final Logger logger = LoggerSingleton.logger;

    private static Class<?> implClass;
    private static Method getProtocolPrefixListMethod;
    private static Method setProtocolPrefixListMethod;
    private static Method commitProtocolPrefixListMethod;
    private static Method getContentPrefixListMethod;
    private static Method setContentPrefixListMethod;
    private static Method commitContentPrefixListMethod;

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

    public static void commitContentPrefixList()
    {
        if (!init())
            return;
        callImpl(commitContentPrefixListMethod, new Object[] {});
    }

    public static void commitProtocolPrefixList()
    {
        if (!init())
            return;
        callImpl(commitProtocolPrefixListMethod, new Object[] {});
    }

    public static Vector getContentPrefixList()
    {
        if (!init())
            return null;
        return (Vector) callImpl(getContentPrefixListMethod, new Object[] {});
    }

    public static Vector getProtocolPrefixList()
    {
        if (!init())
            return null;
        return (Vector) callImpl(getProtocolPrefixListMethod, new Object[] {});
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
            implClass = Class.forName("javax.media.pm.PackageManager");
            if (!(PackageManager.class.isAssignableFrom(implClass)))
                throw new Exception(
                        "javax.media.pm.PackageManager not subclass of "
                                + PackageManager.class.getName());
            getProtocolPrefixListMethod = getStaticMethodOnImplClass(
                    "getProtocolPrefixList", new Class[] {}, Vector.class);
            setProtocolPrefixListMethod = getStaticMethodOnImplClass(
                    "setProtocolPrefixList", new Class[] { Vector.class },
                    void.class);
            commitProtocolPrefixListMethod = getStaticMethodOnImplClass(
                    "commitProtocolPrefixList", new Class[] {}, void.class);
            getContentPrefixListMethod = getStaticMethodOnImplClass(
                    "getContentPrefixList", new Class[] {}, Vector.class);
            setContentPrefixListMethod = getStaticMethodOnImplClass(
                    "setContentPrefixList", new Class[] { Vector.class },
                    void.class);
            commitContentPrefixListMethod = getStaticMethodOnImplClass(
                    "commitContentPrefixList", new Class[] {}, void.class);

        } catch (Exception e)
        {
            implClass = null;

            logger.log(Level.WARNING, "" + e, e);
            return false;
        }

        return true;
    }

    public static void setContentPrefixList(Vector list)
    {
        if (!init())
            return;
        callImpl(setContentPrefixListMethod, new Object[] { list });
    }

    public static void setProtocolPrefixList(Vector list)
    {
        if (!init())
            return;
        callImpl(setProtocolPrefixListMethod, new Object[] { list });
    }

    public PackageManager()
    { // nothing to do
    }
}
