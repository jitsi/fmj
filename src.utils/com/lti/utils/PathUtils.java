package com.lti.utils;

/**
 * Path utilities. TODO: move to src.utils/com.lti.utils.
 *
 * @author Ken Larson
 *
 */
public final class PathUtils
{
    /**
     * does not include the dot. s can be filename + ext or full path.
     *
     */
    public static String extractExtension(String s)
    {
        int index = s.lastIndexOf(".");
        if (index < 0)
            return "";
        return s.substring(index + 1, s.length());
    }

    /**
     * Convenience method to get java.io.tmpdir system property.
     */
    public static String getTempPath()
    {
        return System.getProperty("java.io.tmpdir");
    }

    private PathUtils()
    {
        super();
    }
}
