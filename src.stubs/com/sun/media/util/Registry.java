package com.sun.media.util;

import java.io.*;
import java.util.*;

/**
 *
 * @author Lyubomir Marinov
 */
public class Registry
{
    private static final Map<String, Object> hash
        = new HashMap<String, Object>();

    public static boolean commit() throws IOException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public static Object get(String key)
    {
        return (key == null) ? null : hash.get(key);
    }

    /**
     * Gets a <tt>boolean</tt> representation of the value associated with a
     * specific key if such an association exists and it is possible to parse
     * the value as a <tt>boolean</tt>; otherwise, returns a specific default
     * value.
     *
     * @param key the key whose associated value is to be returned in the form
     * of a <tt>boolean</tt>
     * @param defaultValue a <tt>boolean</tt> value to be returned if no value
     * is associated with the specified <tt>key</tt>
     * @return a <tt>boolean</tt> representation of the value associated with
     * the specified <tt>key</tt> if such an association exists and it is
     * possible to parse the value as a <tt>boolean</tt>; otherwise,
     * <tt>defaultValue</tt>
     */
    public static boolean getBoolean(String key, boolean defaultValue)
    {
        Object value = get(key);

        return
            (value == null)
                ? defaultValue
                : Boolean.parseBoolean(value.toString());
    }

    /**
     * Gets an <tt>int</tt> representation of the value associated with a
     * specific key if such an association exists and it is possible to parse
     * the value as an <tt>int</tt>; otherwise, returns a specific default
     * value.
     *
     * @param key the key whose associated value is to be returned in the form
     * of an <tt>int</tt>
     * @param defaultValue an <tt>int</tt> value to be returned if no value is
     * associated with the specified <tt>key</tt>
     * @return an <tt>int</tt> representation of the value associated with the
     * specified <tt>key</tt> if such an association exists and it is possible
     * to parse the value as an <tt>int</tt>; otherwise, <tt>defaultValue</tt>
     */
    public static int getInt(String key, int defaultValue)
    {
        Object value = get(key);

        if (value != null)
        {
            try
            {
                return Integer.parseInt(value.toString());
            }
            catch (NumberFormatException nfe)
            {
                // defaultValue
            }
        }
        return defaultValue;
    }

    public static boolean set(String key, Object value)
    {
        if ((key != null) && (value != null))
        {
            hash.put(key, value);
            return true;
        }
        else
            return false;
    }
}
