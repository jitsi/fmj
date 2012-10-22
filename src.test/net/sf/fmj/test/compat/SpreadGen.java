package net.sf.fmj.test.compat;

import java.util.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class SpreadGen
{
    public static void gen(Class clazz) throws Exception
    {
        System.out.println((clazz.isInterface() ? "interface" : "class") + ","
                + clazz.getPackage().getName() + "," + clazz.getSimpleName());

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
        // if (false)
        gen(ConcreteClasses.ALL);
        // else
        gen(InterfaceClasses.ALL);
    }
}
