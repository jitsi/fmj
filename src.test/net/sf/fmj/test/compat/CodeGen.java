package net.sf.fmj.test.compat;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import net.sf.fmj.codegen.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class CodeGen
{
    public static void gen(Class clazz) throws Exception
    {
        System.out.println("public void test_"
                + clazz.getName().replaceAll("\\.", "_")
                + "() throws Exception");
        System.out.println("{");

        System.out.println("\tassertEquals(" + clazz.getName()
                + ".class.getModifiers(), " + clazz.getModifiers() + ");");

        if (clazz.isInterface())
            System.out.println("\tassertTrue(" + clazz.getName()
                    + ".class.isInterface());");
        else
            System.out.println("\tassertTrue(!" + clazz.getName()
                    + ".class.isInterface());");

        for (int i = 0; i < clazz.getInterfaces().length; ++i)
        {
            final Class c = clazz.getInterfaces()[i];
            if (c == clazz)
                continue;
            System.out.println("\tassertTrue(" + c.getName()
                    + ".class.isAssignableFrom(" + clazz.getName()
                    + ".class));");
        }

        if (!clazz.isInterface())
        {
            System.out.println("\tassertTrue(" + clazz.getName()
                    + ".class.getSuperclass().equals("
                    + clazz.getSuperclass().getName() + ".class));");
        }

        if (clazz.getFields().length > 0)
        {
            System.out.println("\t// Static fields: ");
            for (int j = 0; j < clazz.getFields().length; ++j)
            {
                final Field f = clazz.getFields()[j];

                // TODO: other field types
                if (!Modifier.isPublic(f.getModifiers()))
                    continue;
                if (Modifier.isStatic(f.getModifiers()))
                {
                    if (f.getType().isPrimitive())
                    {
                        System.out.println("\tassertTrue(" + clazz.getName()
                                + "." + f.getName() + " == "
                                + toLiteralValueStr(f.get(null)) + ");");
                    } else if (f.getType() == String.class)
                    {
                        System.out.println("\tassertTrue(" + clazz.getName()
                                + "." + f.getName() + ".equals("
                                + toLiteralValueStr(f.get(null)) + "));");
                    } else
                    {
                        System.out.println("\t// TODO: test " + f.getName()
                                + " of type " + f.getType().getName());
                    }

                }

            }
            System.out.println();
        }

        if (clazz.getMethods().length > 0)
        {
            System.out.println("\t// Methods (reflection): ");
            System.out.println("\tif (true) {");
            for (int j = 0; j < clazz.getMethods().length; ++j)
            {
                final Method m = clazz.getMethods()[j];

                System.out.println("\t{");
                System.out.print("\t\tfinal Method m = " + clazz.getName()
                        + ".class.getMethod(\"" + m.getName()
                        + "\", new Class[]{");
                boolean first = true;
                for (int k = 0; k < m.getParameterTypes().length; ++k)
                {
                    final Class c = m.getParameterTypes()[k];
                    if (first)
                        first = false;
                    else
                        System.out.print(", ");
                    System.out.print(CGUtils.toNameDotClass(c));

                }
                System.out.println("});");
                System.out.println("\t\tassertEquals(m.getReturnType(), "
                        + CGUtils.toNameDotClass(m.getReturnType()) + ");");
                System.out.println("\t\tassertEquals(m.getModifiers(), "
                        + m.getModifiers() + ");");
                System.out.println("\t}");

            }
            System.out.println("\t}");

            System.out.println();
        }

        if (clazz.getConstructors().length > 0)
        {
            System.out.println("\t// Constructors (reflection): ");
            System.out.println("\tif (true) {");
            for (int j = 0; j < clazz.getConstructors().length; ++j)
            {
                final Constructor ctor = clazz.getConstructors()[j];

                System.out.println("\t{");
                System.out.print("\t\tfinal Constructor c = " + clazz.getName()
                        + ".class.getConstructor(new Class[]{");
                boolean first = true;
                for (int k = 0; k < ctor.getParameterTypes().length; ++k)
                {
                    final Class c = ctor.getParameterTypes()[k];
                    if (first)
                        first = false;
                    else
                        System.out.print(", ");
                    System.out.print(CGUtils.toNameDotClass(c));

                }
                System.out.println("});");
                System.out.println("\t\tassertEquals(c.getModifiers(), "
                        + ctor.getModifiers() + ");");
                System.out.println("\t}");

            }
            System.out.println("\t}");

            System.out.println();
        }

        if (clazz.getFields().length > 0)
        {
            System.out.println("\t// Fields (reflection): ");
            System.out.println("\tif (true) {");
            for (int j = 0; j < clazz.getDeclaredFields().length; ++j)
            {
                final Field f = clazz.getDeclaredFields()[j];

                if (!Modifier.isPublic(f.getModifiers())
                        && !Serializable.class.isAssignableFrom(clazz))
                    continue; // ignore non-public fields for non-serializable
                              // classes, for now.

                System.out.println("\t{");
                System.out.println("\t\tfinal Field f = " + clazz.getName()
                        + ".class.getDeclaredField(\"" + f.getName() + "\");");

                System.out.println("\t\tassertEquals(f.getType().getName(), "
                        + CGUtils.toLiteral(f.getType().getName()) + ");");
                System.out.println("\t\tassertEquals(f.getModifiers(), "
                        + f.getModifiers() + ");");
                System.out.println("\t}");

            }
            System.out.println("\t}");

            System.out.println();
        }

        if (clazz.getMethods().length > 0)
        {
            System.out.println("\t// Methods (compilation): ");

            System.out.println("\tif (false) {");
            System.out.println("\t" + clazz.getName() + " o = null;");
            for (int j = 0; j < clazz.getMethods().length; ++j)
            {
                final Method m = clazz.getMethods()[j];

                if (Modifier.isStatic(m.getModifiers()))
                    System.out.print("\t" + clazz.getName() + "." + m.getName()
                            + "(");
                else
                    System.out.print("\to." + m.getName() + "(");
                boolean first = true;
                for (int k = 0; k < m.getParameterTypes().length; ++k)
                {
                    final Class c = m.getParameterTypes()[k];
                    if (first)
                        first = false;
                    else
                        System.out.print(", ");
                    System.out.print(toDummy(c));

                }
                System.out.println(");");

            }
            System.out.println("\t}");
        }

        if (!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()
                && clazz.getConstructors().length > 0)
        {
            System.out.println("\t// Constructors (compilation): ");

            System.out.println("\tif (false) {");
            System.out.println("\t" + clazz.getName() + " o = null;");
            for (int j = 0; j < clazz.getConstructors().length; ++j)
            {
                final Constructor ctor = clazz.getConstructors()[j];

                System.out.print("\tnew " + ctor.getName() + "(");
                boolean first = true;
                for (int k = 0; k < ctor.getParameterTypes().length; ++k)
                {
                    final Class c = ctor.getParameterTypes()[k];
                    if (first)
                        first = false;
                    else
                        System.out.print(", ");
                    System.out.print(toDummy(c));

                }
                System.out.println(");");

            }
            System.out.println("\t}");
        }

        // if (Serializable.class.isAssignableFrom(clazz))
        // {
        // System.out.println("\t{");
        // System.out.println("\t\tassertEquals(ObjectStreamClass.lookup(" +
        // clazz.getName() + ".class).getSerialVersionUID(), " +
        // CGUtils.toLiteral(ObjectStreamClass.lookup(clazz).getSerialVersionUID())
        // + ");");
        // System.out.println("\t}");
        //
        // }

        System.out.println("}");

    }

    public static void gen(Class/* <?> */[] classes) throws Exception
    {
        final Set/* <Class<?>> */set = new HashSet/* <Class<?>> */();
        for (int i = 0; i < classes.length; ++i)
        {
            set.add(classes[i]);
        }
        gen(set);
    }

    public static void gen(Set/* <Class<?>> */classes) throws Exception
    {
        final Iterator i = classes.iterator();
        while (i.hasNext())
        {
            gen((Class) i.next());
        }
    }

    public static void main(String[] args) throws Exception
    {
        // gen(ImplClasses.ALL);
        gen(ConcreteClasses.ALL);
        // gen(InterfaceClasses.ALL);
    }

    static String toDummy(Class c)
    {
        if (c == int.class)
            return "0";
        else if (c == boolean.class)
            return "false";
        else if (c == short.class)
            return "(short) 0";
        else if (c == byte.class)
            return "(byte) 0";
        else if (c == char.class)
            return "(char) 0";
        else if (c == float.class)
            return "0.f";
        else if (c == double.class)
            return "0.0";
        else if (c == long.class)
            return "0L";
        else if (c == byte[].class)
            return "(byte[]) null";
        else if (c == int[].class)
            return "(int[]) null";
        else if (c == short[].class)
            return "(short[]) null";
        else if (c == double[].class)
            return "(double[]) null";
        else if (c == float[].class)
            return "(float[]) null";
        else if (c == long[].class)
            return "(long[]) null";
        else if (c == boolean[].class)
            return "(boolean[]) null";
        else if (c == char[].class)
            return "(char[]) null";
        else if (c.isArray())
            return "(" + c.getComponentType().getName() + "[]) null";
        else
            return "(" + c.getName() + ") null";
    }

    static String toLiteralValueStr(Object o)
    {
        if (o == null)
            return "null";
        else if (o.getClass() == Long.class)
            return CGUtils.toLiteral(((Long) o).longValue());
        else if (o.getClass() == Float.class)
            return CGUtils.toLiteral(((Float) o).floatValue());
        else if (o instanceof String)
            return CGUtils.toLiteral((String) o);
        else
            return "" + o;
    }

}
