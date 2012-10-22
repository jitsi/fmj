package com.sun.media.util;

import java.io.*;
import java.util.*;

public class Registry
{
    private static final Map<String, Object> hash = new HashMap<String, Object>();

    public static boolean commit() throws IOException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public static Object get(String key)
    {
        return (key == null) ? null : hash.get(key);
    }

    public static boolean set(String key, Object value)
    {
        if ((key != null) && (value != null))
        {
            hash.put(key, value);
            return true;
        } else
            return false;
    }
}
