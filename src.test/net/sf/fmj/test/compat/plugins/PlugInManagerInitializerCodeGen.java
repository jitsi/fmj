package net.sf.fmj.test.compat.plugins;

import java.util.*;

import javax.media.*;
import javax.media.PlugInManager;

import net.sf.fmj.codegen.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class PlugInManagerInitializerCodeGen
{
    private static void dumpFormatArray(Format[] in)
    {
        if (in != null)
        {
            System.out.print("\n\tnew Format[] {");

            if (in.length == 0)
            {
                System.out.print("}");
            } else
            {
                for (int i = 0; i < in.length; ++i)
                {
                    System.out.print("\n\t\t" + MediaCGUtils.formatToStr(in[i])
                            + ",");
                }

                System.out.print("\n\t}");
            }
        } else
            System.out.print("null");
    }

    public static void main(String[] args)
    {
        new PlugInManagerInitializerCodeGen().run();

    }

    private static String typeToStr(int type)
    {
        switch (type)
        {
        case PlugInManager.DEMULTIPLEXER:
            return "PlugInManager.DEMULTIPLEXER";

        case PlugInManager.CODEC:
            return "PlugInManager.CODEC";

        case PlugInManager.EFFECT:
            return "PlugInManager.EFFECT";

        case PlugInManager.RENDERER:
            return "PlugInManager.RENDERER";

        case PlugInManager.MULTIPLEXER:
            return "PlugInManager.MULTIPLEXER";

        default:
            throw new IllegalArgumentException();
        }
    }

    public void run()
    {
        for (int i = 1; i <= 5; ++i)
        {
            System.out.println();
            System.out.println("// " + typeToStr(i) + ":");
            Vector v = PlugInManager.getPlugInList(null, null, i);
            for (int j = 0; j < v.size(); ++j)
            {
                final String s = (String) v.get(j);
                System.out.print("PlugInManager.addPlugIn(\"");
                System.out.print(s);
                System.out.print("\", ");
                final Format[] in = PlugInManager
                        .getSupportedInputFormats(s, i);
                dumpFormatArray(in);

                System.out.print(", ");
                final Format[] out = PlugInManager.getSupportedOutputFormats(s,
                        i);
                dumpFormatArray(out);
                System.out.println(", ");
                System.out.print("\t" + typeToStr(i));
                System.out.println(");");
            }
        }
    }

}
