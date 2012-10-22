package ejmf.toolkit.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EJMFProperties extends Properties {
    private static final String _defaultProperties = "/lib/ejmf.properties";

    public EJMFProperties(String propLoc) {
        super();
        load(propLoc);
    }
    
    public EJMFProperties() {
        this(_defaultProperties);
    }
    
    public synchronized void load(String propLoc) {
        InputStream rin = getClass().getResourceAsStream(propLoc);
        if(rin == null) {
            System.err.println(
                "Could not locate resource " + propLoc);
            return;
        }

        try {
            load(rin);
        } catch(IOException e) {
            System.err.println(
                "Could not load properties from resource " + propLoc);
            e.printStackTrace();
        }
    }
}
