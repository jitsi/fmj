package ejmf.toolkit.util;

import java.awt.Component;
import java.awt.Container;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Debug { 
    public static boolean on = true;
    private static PrintWriter pw = null;
    public static void printComponent(int level, Component c) {
	if (on == false)	
	    return;
	for (int j = 0; j < level; j++)
	    System.out.print("  ");
	System.out.println(c.getClass().getName() + "(" +
			   (c.getParent() == null ? 
				"null" :
				c.getParent().getClass().getName()) + ")");
	if (c instanceof Container) 	{
	    Container con = (Container) c;
	    for (int i = 0; i < con.getComponentCount(); i++)
		printComponent(level+1, con.getComponent(i));
	}
    }

    public static void printComponentDetails(Component c) {
	if (on == false)
	    return;
	System.out.println("Screen Location = " + c.getLocationOnScreen());
	System.out.println("Size = " + c.getSize());
	System.out.println("Location = " + c.getLocation());
    }

    public static void printObject(Object o) {
	if (on == false)	
	    return;
	if (pw == null)
	   try {
               pw = new PrintWriter(new FileWriter("dbg.out"), true);
	   } catch (Exception e) {}

	if (pw != null)
		pw.println(o);
	else
            System.out.println(o);
    }

    public static void printStack(Exception e) {
	e.printStackTrace(pw);
    }
}


