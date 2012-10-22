package ejmf.toolkit.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

// Mix File Filter. 
// Checks for .mix extension

public class MixFileFilter extends FileFilter implements ExtensionFilter {
    public boolean accept(File f) {
	if (f.isDirectory())	
	    return true;
	return getExtension().equals(Utility.getExtension(f));
    }
    public String getDescription() {
	return "Mix Files";
    }

    public String getExtension() {
	return ".mix";
    }
}
