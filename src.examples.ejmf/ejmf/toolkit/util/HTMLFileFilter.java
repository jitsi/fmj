package ejmf.toolkit.util;
import java.io.File;

import javax.swing.filechooser.FileFilter;

// HTML Filter
// Checks for .htm or .html extension

public class HTMLFileFilter extends FileFilter implements ExtensionFilter {
    public boolean accept(File f) {
	if (f.isDirectory()) {
	    return true;
 	}
	return (getExtension().equals(Utility.getExtension(f)) ||
	       "html".equals(Utility.getExtension(f)));
    }
    public String getDescription() {
	return "HTML Files";
    }

    public String getExtension() {
	return ".htm";
    }
}
