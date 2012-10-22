package ejmf.toolkit.util;

/**
  * This interface is used by subclasses of
  * FileFilter to announce they support 
  * getExtension.
  */
public interface ExtensionFilter {
    public String getExtension();
}
