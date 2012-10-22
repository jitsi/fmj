package ejmf.toolkit.util;

import java.awt.Component;

public class Vendor {
   /**  
    *  Report true if Component argument was created by vendor's
    *  JMF.
    */
    public static boolean isVendorsJMFComponent(String vendor, Component c) {
	String s = c.getClass().getName();
	if (vendor.equals("intel")) {
	    // We'll test two ways for Intel. Beta pkgs start with
	    // "intel." Just in case they get it right someday,
	    // we'll also check "com.intel"
            if (s.startsWith("intel") || s.startsWith("com.intel"))
	        return true;
	} else if (vendor.equals("sun")) {
	    if (s.startsWith("com.sun"))
		return true;
	}
	return false;
    }
}
